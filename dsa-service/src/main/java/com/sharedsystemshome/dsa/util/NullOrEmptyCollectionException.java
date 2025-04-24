package com.sharedsystemshome.dsa.util;

public class NullOrEmptyCollectionException extends BusinessValidationException {

    public NullOrEmptyCollectionException(String entityName) {
        super(createMessage(entityName));
    }

    public NullOrEmptyCollectionException(String entityName, Throwable cause) {
        super(createMessage(entityName), cause);
    }

    private static String createMessage(String entityName){
        return entityName + " member collection is null or empty.";
    }

}