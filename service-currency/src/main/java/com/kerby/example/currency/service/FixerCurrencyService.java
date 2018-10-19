package com.kerby.example.currency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerby.example.common.annotations.VisibleForTestMock;
import com.kerby.example.currency.exceptions.CurrencyConversionException;
import com.kerby.example.currency.models.CurrencyCode;
import com.kerby.example.currency.exceptions.APICallFailedException;
import com.kerby.example.currency.exceptions.InvalidCurrencyCodeException;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * A service using the Fixer API for converting a USD amount to any other currency.
 *
 * Frustratingly the Fixer 'Convert From/To' and 'Live Exchange Rates For USD' are both behind a paid subscription plan.
 * The only service we have access to on a free plan is a 'Exchange Rates for EUR'.
 *
 * This means we must:
 *  - Do all conversions locally with EUR exchange rates
 *  - Calculate how much a USD amount equals in EUR and use the Exchange Rates for EUR to get other currency conversions
 *
 * To reduce the number of API calls the exchange rate is called once and cached locally.
 */
@Configuration
@Service
public class FixerCurrencyService implements CurrencyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String apiKey;

    // singleton cache shared across all instances
    private static HashMap<CurrencyCode, BigDecimal> currencyRatesEURCache = null;

    @VisibleForTestMock
    FixerCurrencyService() { this.apiKey = null; }

    @Autowired
    public FixerCurrencyService(@Value("${fixer.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @VisibleForTestMock
    HashMap<CurrencyCode, BigDecimal> getLatestEURExchangeRatesFromAPI() throws APICallFailedException {
        Assert.isTrue(StringUtils.isNotBlank(this.apiKey), "Unable to lookup currency rates from Fixer as API key is not set");

        logger.debug(String.format("Attempting to lookup currency rates from Fixer using api key: [%s]", apiKey));

        final RestTemplate restTemplate = new RestTemplate();

        final StringBuilder url = new StringBuilder("http://data.fixer.io/api/latest");
        url.append(String.format("?access_key=%s", apiKey));

        logger.info(url.toString());
        ResponseEntity<String> response = null;

        try {
            response = restTemplate.getForEntity(url.toString(), String.class);
            if (response != null && response.getStatusCode() == HttpStatus.OK) {

                final ObjectMapper objectMapper = new ObjectMapper();
                final JsonNode root = objectMapper.readTree(response.getBody());
                final JsonNode rates = root.path("rates");

                final HashMap<CurrencyCode, BigDecimal> currencyRates = new HashMap<>(rates.size());
                rates.fieldNames().forEachRemaining(rate -> {
                    final BigDecimal value = new BigDecimal(rates.get(rate).asDouble());
                    if (EnumUtils.isValidEnum(CurrencyCode.class, rate)) {
                        logger.trace(String.format("Storing rate of: [%s] for currency: [%s]", value.toPlainString(), rate));
                        currencyRates.put(CurrencyCode.valueOf(rate), value);
                    } else {
                        logger.warn(String.format("Skipping currency: [%s] as it's not a supported currency", rate));
                    }
                });

                logger.info(String.format("Received currency rates: [%d] from Fixer API", currencyRates.size()));

                return currencyRates;
            } else {
                throw new APICallFailedException(
                        String.format("Fixer API returned status code: [%s] while looking up latest exchange rates. [%s]",
                                response.getStatusCode(), response.getBody()));
            }
        } catch (RestClientException e) {
            throw new APICallFailedException("Something went wrong querying Fixer API for EUR currency rates", e);
        } catch (IOException e) {
            throw new APICallFailedException(String.format("Something went wrong converting rest response to JSON. Response: [%s]", response), e);
        }
    }

    private HashMap<CurrencyCode, BigDecimal> getCachedEURExchangeRates() {

        // only lookup currency rates if not already cached
        if (currencyRatesEURCache == null) {
            synchronized (FixerCurrencyService.class) {
                // double checked locking to ensure a previous thread hasn't already initialised currency rates cache
                if (currencyRatesEURCache == null) {
                    try {
                        // store in cache
                        currencyRatesEURCache = this.getLatestEURExchangeRatesFromAPI();
                        logger.info("Updated currency rate cache");
                    } catch (APICallFailedException e) {
                        logger.error("Something went wrong calling the Fixer API. It failed to return exchange rates", e);
                    }
                }
            }
        }

        return currencyRatesEURCache;
    }

    @Override
    public CurrencyCode[] getCurrencyCodes() {
        return CurrencyCode.values();
    }

    public CurrencyCode getCurrencyCodeFromString(@NotNull String currencyCode) throws InvalidCurrencyCodeException {
        Assert.notNull(currencyCode, "Currency code string is required to lookup enum");
        final CurrencyCode result;

        try {
            result = CurrencyCode.valueOf(currencyCode);
        } catch (Exception e) {
            throw new InvalidCurrencyCodeException(String.format("No valid currency code exists with label: [%s]", currencyCode), e);
        }

        return result;
    }

    @Override
    public BigDecimal convertFromUSD(@NotNull CurrencyCode toCurrency, @NotNull BigDecimal usdAmount) throws CurrencyConversionException {
        Assert.notNull(toCurrency, "Currency code to convert to is required");
        Assert.notNull(usdAmount, "Amount in USD to convert is required");
        BigDecimal result = null;

        final HashMap<CurrencyCode, BigDecimal> currencyRatesEUR = getCachedEURExchangeRates();
        if (currencyRatesEUR != null && !currencyRatesEUR.isEmpty() && currencyRatesEUR.containsKey(toCurrency)) {
            /**
             * Because of limitations in the API (only having EUR rates) we must
             *  - Convert the original USD amount to EUR using the latest EUR exchange rate
             *  - Convert the EUR amount using the desired exchange rate to the target currency
             */

            final BigDecimal usdCurrencyExchangeRate = currencyRatesEUR.get(CurrencyCode.USD);
            final BigDecimal targetCurrencyExchangeRate = currencyRatesEUR.get(toCurrency);

            if (usdCurrencyExchangeRate == null || targetCurrencyExchangeRate == null) {
                throw new CurrencyConversionException(String.format("Unable to convert: [%s] from USD as no currency rate was found for: USD=[%s] or [%s]=[%s]", usdAmount, usdCurrencyExchangeRate, toCurrency, targetCurrencyExchangeRate));
            }
            // convert USD amount to EUR
            final BigDecimal eurAmount = usdAmount.divide(usdCurrencyExchangeRate, 2, RoundingMode.HALF_UP);
            // convert EUR to any currency that is requested
            result = eurAmount.multiply(targetCurrencyExchangeRate).setScale(2, RoundingMode.HALF_UP);
            logger.debug(String.format("Converted amount of: [%s] from USD to: [%s]. Result: [%s]", usdAmount, toCurrency, result));
        } else {
            throw new CurrencyConversionException(String.format("Unable to convert from USD to: [%s] as no currency rate exists", toCurrency));
        }

        return result;
    }

}
