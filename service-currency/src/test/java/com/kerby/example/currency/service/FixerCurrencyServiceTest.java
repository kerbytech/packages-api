package com.kerby.example.currency.service;

import com.kerby.example.currency.models.CurrencyCode;
import com.kerby.example.currency.exceptions.APICallFailedException;
import com.kerby.example.currency.exceptions.CurrencyConversionException;
import com.kerby.example.currency.service.FixerCurrencyService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Tests for FixerCurrencyService.
 * No API calls are made. Fixer API is mocked.
 * These are unit tests not integration tests with Fixer API.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = FixerCurrencyService.class)
@SpringBootConfiguration
public class FixerCurrencyServiceTest {

    private static final HashMap<CurrencyCode, BigDecimal> currencyRatesEUR = new HashMap<>();

    @BeforeClass
    public static void beforeTests() throws APICallFailedException {
        // setup mocked currency rates
        currencyRatesEUR.put(CurrencyCode.EUR, new BigDecimal(1));
        currencyRatesEUR.put(CurrencyCode.USD, new BigDecimal(1.145771));
        currencyRatesEUR.put(CurrencyCode.GBP, new BigDecimal(0.879981));
    }

    @Spy
    FixerCurrencyService fixerCurrencyService;

    @Test
    public void when_convertFromPositiveUSDAmount_expectValidConversion() throws CurrencyConversionException, APICallFailedException {
        Mockito.doReturn(currencyRatesEUR).when(this.fixerCurrencyService).getLatestEURExchangeRatesFromAPI();

        final BigDecimal usdAmount = new BigDecimal(10.00).setScale(2, RoundingMode.HALF_UP);

        Assert.assertThat(
                new BigDecimal(10.00),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(7.68),
                Matchers.closeTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.GBP, usdAmount),  new BigDecimal(0.1))
        );
        Assert.assertThat(
                new BigDecimal(8.73),
                Matchers.closeTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.EUR, usdAmount),  new BigDecimal(0.1))
        );
    }

    @Test
    public void when_convertFromNegativeEURAmount_expectValidException() throws CurrencyConversionException, APICallFailedException {
        Mockito.doReturn(currencyRatesEUR).when(this.fixerCurrencyService).getLatestEURExchangeRatesFromAPI();

        final BigDecimal usdAmount = new BigDecimal(-10.00);

        Assert.assertThat(
                new BigDecimal(-10.00),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(-7.68),
                Matchers.closeTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.GBP, usdAmount),  new BigDecimal(0.1))
        );
        Assert.assertThat(
                new BigDecimal(-8.73),
                Matchers.closeTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.EUR, usdAmount),  new BigDecimal(0.1))
        );

    }

    @Test
    public void when_convertFromZeroEURAmount_expectException() throws CurrencyConversionException, APICallFailedException {
        Mockito.doReturn(currencyRatesEUR).when(this.fixerCurrencyService).getLatestEURExchangeRatesFromAPI();

        final BigDecimal usdAmount = new BigDecimal(0);

        Assert.assertThat(
                new BigDecimal(0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.GBP, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.EUR, usdAmount))
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_convertFromZeroEURNullAmount_expectException() throws CurrencyConversionException, APICallFailedException {
        Mockito.doReturn(currencyRatesEUR).when(this.fixerCurrencyService).getLatestEURExchangeRatesFromAPI();

        final BigDecimal usdAmount = null;
        this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount);
    }

    @Test
    public void when_convertFromEURAmountMultipleTimes_expectSingleAPICallAndUseOfCachedResults() throws CurrencyConversionException, APICallFailedException {
        Mockito.doReturn(currencyRatesEUR).when(this.fixerCurrencyService).getLatestEURExchangeRatesFromAPI();

        final BigDecimal usdAmount = new BigDecimal(10.00);

        Assert.assertThat(
                new BigDecimal(10.0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(10.0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );
        Assert.assertThat(
                new BigDecimal(10.0),
                Matchers.comparesEqualTo(this.fixerCurrencyService.convertFromUSD(CurrencyCode.USD, usdAmount))
        );

        // assert API lookup was only performed once even though exchange request was made twice
        Mockito.verify(this.fixerCurrencyService, Mockito.atMost(1)).getLatestEURExchangeRatesFromAPI();
    }

    // TODO tests on race conditions of cache init, exception handling, missing currencies etc
}
