package com.sharedsystemshome.dsa.util;

public class AddOrUpdateTransactionException extends BusinessValidationException {

    public AddOrUpdateTransactionException(String entityName) {
        super(createMessage(entityName));
    }

    public AddOrUpdateTransactionException(String entityName, Throwable cause) {
        super(createMessage(entityName), cause);
    }

    private static String createMessage(String entityName){
        return "Unable to add or update " + entityName + ".";
    }

}