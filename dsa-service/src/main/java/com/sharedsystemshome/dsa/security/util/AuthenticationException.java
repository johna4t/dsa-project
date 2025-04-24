package com.sharedsystemshome.dsa.security.util;

public class AuthenticationException extends SecurityValidationException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}