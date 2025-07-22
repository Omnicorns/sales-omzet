package com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.configuration;

import com.sarinah.tenantsalesomzet.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Tangani BusinessException normal (misal dilempar di service/controller).
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
            log.error("BusinessException [{}]: {}", ex.getErrorCode(), ex.getMessage(), ex);
            ErrorResponse body = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
            // gunakan HTTP status yang ada di exception
            return ResponseEntity
                    .status(ex.getHttpStatus())
                    .body(body);
        }

        /**
         * Tangani error saat Jackson baca JSON (misal error di setter/deserializer)
         * dan unwrap BusinessException jika itu penyebabnya.
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleJsonParse(HttpMessageNotReadableException ex) {
            // ambil root cause
            Throwable cause = ex.getMostSpecificCause();
            if (cause instanceof BusinessException) {
                BusinessException be = (BusinessException) cause;
                log.error("JSON parse BusinessException [{}]: {}", be.getErrorCode(), be.getMessage(), ex);
                ErrorResponse body = new ErrorResponse(be.getErrorCode(), be.getMessage());
                return ResponseEntity
                        .status(be.getHttpStatus())
                        .body(body);
            }

            // fallback untuk error JSON lain
            log.error("Invalid JSON request:", ex);
            ErrorResponse body = new ErrorResponse("MALFORMED_JSON", "Malformed JSON request");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(body);
        }

        /**
         * Catch‑all untuk unexpected exception lain.
         */
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
            public String getCode()    { return code; }
            public String getMessage() { return message; }
        }
    }


