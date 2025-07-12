package com.sharedsystemshome.dsa.util;

public class BusinessValidationException extends RuntimeException {

    public static String CUSTOMER_ACCOUNT = "Customer Account";
    public static String DATA_CONTENT_DEFINITION = "Data Content Definition";
    public static String DATA_CONTENT_PERSPECTIVE = "Data Content Perspective";
    public static String DATA_FLOW = "Data Flow";
    public static String DATA_PROCESSING_ACTIVITY = "Data Processing Activity";
    public static String DATA_PROCESSOR = "Data Processor";
    public static String DATA_SHARING_AGREEMENT = "Data Sharing Agreement";
    public static String DATA_SHARING_PARTY = "Data Sharing Party";
    public static String USER_ACCOUNT = "User Account";
    public static String ROLE = "Role";
    public static String TOKEN = "Token";
    public static String USER_LOGIN = "User Login";
    public static String SHARED_DATA_CONTENT = "Shared Data Content";

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}