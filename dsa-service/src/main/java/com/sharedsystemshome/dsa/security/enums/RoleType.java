package com.sharedsystemshome.dsa.security.enums;

public enum RoleType {

    //Minimal readonly privileges
    USER("User"),
    //Associate member of account
    ASSOCIATE("Partner Member"),
    //Full member of account
    MEMBER("Customer Member"),
    //Full member of account with ability to manage global account settings and other users
    ACCOUNT_ADMIN("Account Administrator"),
    //Full read and write access across all accounts
    SUPER_ADMIN("Global Administrator");

    // constructor
    private RoleType(String value) {
        this.value = value;
    }

    // internal state
    private String value;

    public String value() {
        return this.value;
    }}
