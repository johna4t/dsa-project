package com.sharedsystemshome.dsa.util;

public class EntityNotFoundException extends BusinessValidationException {

    public EntityNotFoundException(String entityName, Long id) {
        super(createMessage(entityName, id));
    }

    public EntityNotFoundException(String entityName, Long id, Throwable cause) {
        super(createMessage(entityName, id), cause);
    }

    private static String createMessage(String entityName, Long id){

        if(null == id){
            return entityName + " with null id does not exist.";
        } else {
            return entityName + " with id = " + id + " not found.";
        }
    }

}
