package com.sarinah.tenantsalesomzet.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    /**
     * Constructor default → HTTP 400 Bad Request
     */
    public BusinessException(String errorCode, String message) {
        this(HttpStatus.BAD_REQUEST, errorCode, message, null);
    }

    /**
     * Constructor dengan cause → HTTP 400 Bad Request
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        this(HttpStatus.BAD_REQUEST, errorCode, message, cause);
    }

    /**
     * Constructor dengan custom HTTP status
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message) {
        this(httpStatus, errorCode, message, null);
    }

    /**
     * Constructor penuh
     */
    public BusinessException(HttpStatus httpStatus, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
