package com.sharedsystemshome.dsa.enums;

public enum LawfulBasis {

    NOT_PERSONAL_DATA("N/A"),
    CONSENT("Consent"),
    CONTRACT("Performance of contract"),
    LEGAL_OBLIGATION("Legal obligation"),
    VITAL_INTERESTS("Public tasks"),
    LEGITIMATE_INTERESTS("Legitimate interest");

    // constructor
    private LawfulBasis(String lawfulBasis) {
        this.lawfulBasis = lawfulBasis;
    }

    // internal state
    private final String lawfulBasis;

    public String getLawfulBasis() {
        return this.lawfulBasis;
    }
}
