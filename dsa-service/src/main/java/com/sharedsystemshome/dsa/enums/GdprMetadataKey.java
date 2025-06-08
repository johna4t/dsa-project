package com.sharedsystemshome.dsa.enums;

public enum GdprMetadataKey {
    LAWFUL_BASIS("lawfulBasis"),
    SPECIAL_CATEGORY("specialCategory"),
    ARTICLE_9_CONDITION("article9Condition");

    private final String key;

    GdprMetadataKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
