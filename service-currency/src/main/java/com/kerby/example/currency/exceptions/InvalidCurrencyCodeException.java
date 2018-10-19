package com.kerby.example.currency.exceptions;

public class InvalidCurrencyCodeException extends Exception {

    public InvalidCurrencyCodeException(String message) {
        super(message);
    }

    public InvalidCurrencyCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
