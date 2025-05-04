package com.sharedsystemshome.dsa.enums;

public enum MetadataScheme {

    GDPR(0);

    // constructor
    private MetadataScheme(int metadataScheme) {
        this.metadataScheme = metadataScheme;
    }

    // internal state
    private final int metadataScheme;

    public int getMetadataScheme() {
        return this.metadataScheme;
    }
}
