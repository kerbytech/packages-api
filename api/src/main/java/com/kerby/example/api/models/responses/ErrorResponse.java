package com.kerby.example.api.models.responses;

import java.util.Date;

public class ErrorResponse {

    // extra granularity of error on top of http status
    public enum ErrorCode {
        INTERNAL_ERROR (100),
        INCORRECT_PARAMS (200),
        INVALID_JSON (201),
        PACKAGE_NOT_FOUND (300);

        protected int code;
        ErrorCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return String.format("%s %d", this.name(), this.code);
        }
    }

    private Date date;
    private ErrorCode errorCode;
    private String message;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorResponse() {
    }

    public ErrorResponse(Date date, ErrorCode errorCode, String message) {
        this.date = date;
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "date=" + date +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }
}
