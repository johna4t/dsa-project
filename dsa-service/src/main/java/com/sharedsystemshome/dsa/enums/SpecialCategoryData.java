package com.sharedsystemshome.dsa.enums;

public enum SpecialCategoryData {

    NOT_SPECIAL_CATEGORY_DATA("N/A"),
    RACIAL("Personal data revealing racial or ethnic origin"),
    POLITICAL("Personal data revealing political opinions"),
    RELIGIOUS("Personal data revealing religious or philosophical beliefs"),
    TRADE_UNION("Personal data revealing trade union membership"),
    GENETIC("Genetic data"),
    BIOMETRIC_ID("Biometric data (where used for identification purposes)"),
    HEALTH("Data concerning health"),
    SEX_LIFE("Data concerning a person’s sex life"),
    SEX_ORIENT("Data concerning a person’s sexual orientation");

    private final String specialCategoryData;

    SpecialCategoryData(String specialCategoryData) {
        this.specialCategoryData = specialCategoryData;
    }

    public String getSpecialCategoryData() {
        return specialCategoryData;
    }
}
