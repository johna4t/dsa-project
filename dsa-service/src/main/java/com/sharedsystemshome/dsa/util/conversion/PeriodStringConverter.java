package com.sharedsystemshome.dsa.util.conversion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Period;

@Converter(autoApply = false)
public class PeriodStringConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return period == null ? null : period.toString(); // ISO-8601 (e.g., P5Y, P6M3D)
    }

    @Override
    public Period convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Period.parse(dbData);
    }
}