package com.sharedsystemshome.dsa.enums;

public enum MetadataScheme {

    GDPR("GDPR");

    // constructor
    private MetadataScheme(String metadataScheme) {
        this.metadataScheme = metadataScheme;
    }

    // internal state
    private final String metadataScheme;

    public String getMetadataScheme() {
        return this.metadataScheme;
    }
}
