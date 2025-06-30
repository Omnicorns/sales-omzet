package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;




@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        // log error lengkap dengan stacktrace
        log.error("BusinessException [{}]: {}", ex.getErrorCode(), ex.getMessage(), ex);

        ErrorResponse body = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    // (opsional) handler untuk exception umum
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error:", ex);
        ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    // DTO untuk payload error
    public static class ErrorResponse {
        private final String code;
        private final String message;
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
   }
