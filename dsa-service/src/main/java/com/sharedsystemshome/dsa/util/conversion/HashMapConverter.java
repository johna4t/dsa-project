package com.sharedsystemshome.dsa.util.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> objectMapValue) {

        String jsonStringValue = null;
        try {
            jsonStringValue = objectMapper.writeValueAsString(objectMapValue);
        } catch (final JsonProcessingException e) {
            throw new BusinessValidationException("Error persisting data as JSON.", e);
        }

        return jsonStringValue;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String jsonStringValue) {

        Map<String, Object> objectMapValue = null;
        try {
            objectMapValue = objectMapper.readValue(jsonStringValue, new TypeReference<TreeMap<String, Object>>() {});
        } catch (final IOException e) {
            throw new BusinessValidationException("Error retrieving data as JSON object.", e);        }

        return objectMapValue;
    }



}
