package com.kerby.example.currency.exceptions;

public class APICallFailedException extends Exception {

    public APICallFailedException(String message) {
        super(message);
    }

    public APICallFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
