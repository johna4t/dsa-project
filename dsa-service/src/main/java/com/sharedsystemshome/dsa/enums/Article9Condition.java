package com.sharedsystemshome.dsa.enums;

public enum Article9Condition {

    NOT_APPLICABLE("N/A"),
    EXPLICIT_CONSENT("Explicit consent"),
    EMPLOYMENT("Employment, Social Security, or Social Protection Law"),
    VITAL_INTERESTS("Vital Interests"),
    NOT_FOR_PROFIT("Not-for-Profit Bodies"),
    DATA_MADE_PUBLIC("Data Made Public by the Data Subject"),
    LEGAL("Legal Claims or Judicial Acts"),
    REASONS_OF_PUBLIC_INTEREST("Substantial Public Interest (UK law)"),
    HEALTH_OR_OCCUP_CARE("Healthcare and Occupational Medicine"),
    PUBLIC_HEALTH("Public Health"),
    ARCHIVING("Archiving, Research, or Statistical Purposes");

    private final String article9Condition;

    Article9Condition(String article9Condition) {
        this.article9Condition = article9Condition;
    }

    public String getLabel() {
        return article9Condition;
    }

}
