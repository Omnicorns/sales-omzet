package com.sarinah.tenantsalesomzet.exception;

public class CustomException extends RuntimeException{
    private final String errorCode;


    public CustomException(String errorCode, String message) {
        super(message);            // pastikan message diteruskan ke RuntimeException
        this.errorCode = errorCode;
    }


    public CustomException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
