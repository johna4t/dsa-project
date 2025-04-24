package com.sharedsystemshome.dsa.util;

public class NullOrEmptyValueException extends BusinessValidationException {

    public NullOrEmptyValueException(String entityName) {
        super(createMessage(entityName));
    }

    public NullOrEmptyValueException(String entityName, Throwable cause) {
        super(createMessage(entityName), cause);
    }

    private static String createMessage(String entityName){
        return entityName + " is null or empty.";
    }

}