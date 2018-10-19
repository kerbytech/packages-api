package com.kerby.example.api;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.kerby.example.api.exceptions.ApiException;
import com.kerby.example.api.controller.PackagesController;
import com.kerby.example.api.models.responses.ErrorResponse;
import com.kerby.example.packages.exceptions.PackageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * Exception handling for PackagesController.
 *
 * All exceptions that could be thrown are handled to ensure no internal messaging is leaked.
 */
@ControllerAdvice(assignableTypes = PackagesController.class)
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(new Date(), ErrorResponse.ErrorCode.INTERNAL_ERROR, exception.getMessage());
        this.logger.error("An unknown exception occurred in the API", exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArguments(IllegalArgumentException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(new Date(), ErrorResponse.ErrorCode.INCORRECT_PARAMS, exception.getMessage());
        this.logger.error("An API request was made with incorrect arguments", exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ErrorResponse> handleJsonMappingException(JsonMappingException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(new Date(), ErrorResponse.ErrorCode.INVALID_JSON, exception.getMessage());
        this.logger.error("An API request was made with an incorrect JSON payload", exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePackageNotFoundException(PackageNotFoundException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(new Date(), ErrorResponse.ErrorCode.PACKAGE_NOT_FOUND, exception.getMessage());
        this.logger.error("An API request was made with an incorrect package id", exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Generic catch-all encase an exception is not caught by a specific handler
     * @param exception - Exception thrown by API
     * @return - 500 and ErrorResponse object
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        final ErrorResponse errorResponse = new ErrorResponse(new Date(), ErrorResponse.ErrorCode.INTERNAL_ERROR, "An internal server error occurred");
        this.logger.error("An unexpected exception occurred in the API", exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
