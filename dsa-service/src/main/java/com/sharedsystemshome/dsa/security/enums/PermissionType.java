package com.sharedsystemshome.dsa.security.enums;

public enum PermissionType {


    PERMIT_READ("Read"),
    PERMIT_UPDATE("Update"),
    PERMIT_CREATE("Create"),
    PERMIT_DELETE("Delete"),
    SUPER_ADMIN_READ("super_admin:read"),
    SUPER_ADMIN_UPDATE("super_admin:update"),
    SUPER_ADMIN_CREATE("super_admin:create"),
    SUPER_ADMIN_DELETE("super_admin:delete"),
    ACCOUNT_ADMIN_READ("account_admin:read"),
    ACCOUNT_ADMIN_UPDATE("account_admin:update"),
    ACCOUNT_ADMIN_CREATE("account_admin:create"),
    ACCOUNT_ADMIN_DELETE("account_admin:delete"),
    MEMBER_READ("member:read"),
    MEMBER_UPDATE("member:update"),
    MEMBER_CREATE("member:create"),
    MEMBER_DELETE("member:delete"),
    ASSOCIATE_READ("associate:read"),
    ASSOCIATE_UPDATE("associate:update"),
    ASSOCIATE_CREATE("associate:create"),
    ASSOCIATE_DELETE("associate:delete"),
    USER_READ("user:read");


    // constructor
    private PermissionType(String value) {
        this.value = value;
    }

    // internal state
    private String value;

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "PermissionType{" +
                "value='" + value + '\'' +
                '}';
    }
}
