package com.sharedsystemshome.dsa.security.util;

public class SecurityValidationException extends RuntimeException {
    public SecurityValidationException(String message) {
        super(message);
    }

    public SecurityValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}