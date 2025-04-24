package com.sharedsystemshome.dsa.enums;

public enum LawfulBasis {

    NOT_PERSONAL_DATA(0),
    //Article 6 Conditions
    CONSENT(10),
    CONTRACT(20),
    LEGAL_OBLIGATION(30),
    VITAL_INTERESTS(40),
    PUBLIC_TASK(50),
    LEGITIMATE_INTERESTS(60),
    SPECIAL_CATEGORY(70),
    CRIMINAL_OFFENCE_DATA(80);

    // constructor
    private LawfulBasis(int lawfulBasis) {
        this.lawfulBasis = lawfulBasis;
    }

    // internal state
    private int lawfulBasis;

    public int getLawfulBasis() {
        return this.lawfulBasis;
    }
}
