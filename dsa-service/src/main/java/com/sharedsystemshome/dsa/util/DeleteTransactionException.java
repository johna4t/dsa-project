package com.sharedsystemshome.dsa.util;

public class DeleteTransactionException extends BusinessValidationException {

    public DeleteTransactionException(String entityName) {
        super(createMessage(entityName));
    }

    public DeleteTransactionException(String entityName, Throwable cause) {
        super(createMessage(entityName), cause);
    }

    private static String createMessage(String entityName){
        return "Unable to delete " + entityName + ".";
    }

}