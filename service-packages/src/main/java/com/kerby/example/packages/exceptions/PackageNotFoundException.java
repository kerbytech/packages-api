package com.kerby.example.packages.exceptions;

public class PackageNotFoundException extends Exception {

    public PackageNotFoundException(String message) {
        super(message);
    }

    public PackageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
