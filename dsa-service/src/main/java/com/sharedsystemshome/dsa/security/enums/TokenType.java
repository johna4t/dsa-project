package com.sharedsystemshome.dsa.security.enums;

public enum TokenType {

    ACCESS("Access"),
    REFRESH("Refresh");

    // constructor
    private TokenType(String value) {
        this.value = value;
    }

    // internal state
    private String value;

    public String value() {
        return this.value;
    }
}
