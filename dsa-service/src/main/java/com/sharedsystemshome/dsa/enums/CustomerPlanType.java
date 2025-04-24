package com.sharedsystemshome.dsa.enums;

public enum CustomerPlanType {

    GUEST("Guest"),
    SMALL_BUSINESS("Small Business"),
    STANDARD("Standard"),
    ENTERPRISE("Enterprise");

    // constructor
    private CustomerPlanType(String value) {
        this.value = value;
    }

    // internal state
    private String value;

    public String getValue() {
        return this.value;
    }
}
