package com.sharedsystemshome.dsa.enums;

public enum DataContentType {

    NOT_SPECIFIED("Not specified"),
    STRUCTURED_ELECTRONIC_DATA("Electronic file or message containing structured data"),
    DATABASE_RECORD("Record stored in a structured database"),
    UNSTRUCTURED_ELECTRONIC_DATA("Unstructured or free-text electronic content (e.g. email body, chat)"),
    ELECTRONIC_DOCUMENT("Electronic document (e.g. PDF, DOCX)"),
    IMAGE_OR_BIOMETRIC_FILE("Image or biometric capture (e.g. facial scan, fingerprint)"),
    AUDIO_VISUAL_RECORDING("Audio or video recording"),
    LOG_OR_TELEMETRY_DATA("Machine-generated logs or telemetry"),
    PAPER_DOCUMENT("Paper or other hardcopy document"),
    OTHER_MEDIUM("Other medium");

    // constructor
    private DataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    // internal state
    private final String dataContentType;

    public String getDataContentType() {
        return this.dataContentType;
    }
}
