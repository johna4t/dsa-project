package com.sharedsystemshome.dsa.enums;

public enum DataContentType {

    NOT_SPECIFIED(0),
    STRUCTURED_ELECTR0NIC_DATA(10),
    ELECTRONIC_DOCUMENT(20),
    PAPER_DOCUMENT(30),
    OTHER_MEDIUM(40);

    // constructor
    private DataContentType(int dataContentType) {
        this.dataContentType = dataContentType;
    }

    // internal state
    private int dataContentType;

    public int getDataContentType() {
        return this.dataContentType;
    }
}
