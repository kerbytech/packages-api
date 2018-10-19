package com.kerby.example.currency.service;

import com.kerby.example.currency.models.CurrencyCode;
import com.kerby.example.currency.exceptions.CurrencyConversionException;
import com.kerby.example.currency.exceptions.InvalidCurrencyCodeException;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public interface CurrencyService {

    CurrencyCode[] getCurrencyCodes();

    CurrencyCode getCurrencyCodeFromString(@NotNull String currencyCode) throws InvalidCurrencyCodeException;

    BigDecimal convertFromUSD(@NotNull CurrencyCode toCurrency, @NotNull BigDecimal usdAmount) throws CurrencyConversionException;


}
