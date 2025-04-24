package com.sharedsystemshome.dsa.enums;

public enum UserAccountStatus {

    ACTIVE("Active"),
    INACTIVE("Inactive");

    // constructor
    private UserAccountStatus(String value) {
        this.value = value;
    }

    // internal state
    private String value;

    public String getValue() {
        return this.value;
    }
}
