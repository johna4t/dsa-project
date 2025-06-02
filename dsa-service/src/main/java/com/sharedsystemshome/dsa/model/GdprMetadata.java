package com.sharedsystemshome.dsa.model;

import com.sharedsystemshome.dsa.enums.Article9Condition;
import com.sharedsystemshome.dsa.enums.GdprMetadataKey;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GdprMetadata implements Metadata {

    private final Map <String, Object> metadata = new HashMap<>();

    @Builder
    public GdprMetadata(Map <String, Object> meta) {

        String lawfulBasisRaw = getRawValue(meta, "lawfulBasis");
        LawfulBasis lawfulBasis = parseEnum(lawfulBasisRaw, LawfulBasis.class);

        if (lawfulBasis == null || lawfulBasis == LawfulBasis.NOT_PERSONAL_DATA) {
            this.initialiseDefaultValues();
        } else {
            // Otherwise: continue with special category
            String specialRaw = getRawValue(meta, "specialCategory");
            SpecialCategoryData special = parseEnum(specialRaw, SpecialCategoryData.class);
            this.metadata.put("lawfulBasis", lawfulBasis.name());

            if (special == null || special == SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA) {
                this.metadata.put("specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA.name());
                this.metadata.put("article9Condition", Article9Condition.NOT_APPLICABLE.name());
            } else {
                this.metadata.put("specialCategory", special.name());

                String art9Raw = getRawValue(meta, "article9Condition");
                Article9Condition art9 = parseEnum(art9Raw, Article9Condition.class);
                this.metadata.put("article9Condition", art9 != null ? art9.name() : Article9Condition.NOT_APPLICABLE.name());
            }
        }
    }

    private String getRawValue(Map<String, Object> source, String key) {
        if (source == null) return null;
        Object value = source.get(key);
        return value instanceof String ? (String) value : null;
    }

    private void initialiseDefaultValues(){
        // Not personal data: wipe everything else
        metadata.put("lawfulBasis", LawfulBasis.NOT_PERSONAL_DATA.name());
        metadata.put("specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA.name());
        metadata.put("article9Condition", Article9Condition.NOT_APPLICABLE.name());
    }


    private String getRaw(GdprMetadataKey key) {
        Object val = this.metadata.get(key.getKey());
        return val instanceof String ? (String) val : null;
    }

    public LawfulBasis getLawfulBasis() {
        return parseEnum(getRaw(GdprMetadataKey.LAWFUL_BASIS), LawfulBasis.class);
    }

    public SpecialCategoryData getSpecialCategoryData() {
        return parseEnum(getRaw(GdprMetadataKey.SPECIAL_CATEGORY), SpecialCategoryData.class);
    }

    public Article9Condition getArticle9Condition() {
        return parseEnum(getRaw(GdprMetadataKey.ARTICLE_9_CONDITION), Article9Condition.class);
    }

    public boolean isPersonal() {
        LawfulBasis basis = getLawfulBasis();
        return basis != null && basis != LawfulBasis.NOT_PERSONAL_DATA;
    }

    public boolean isSpecialCategory() {
        SpecialCategoryData cat = getSpecialCategoryData();
        return cat != null && cat != SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA;
    }

    private <T extends Enum<T>> T parseEnum(String raw, Class<T> enumClass) {
        if (raw == null) return null;
        try {
            return Enum.valueOf(enumClass, raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GdprMetadata that = (GdprMetadata) o;

        return Objects.equals(this.getLawfulBasis(), that.getLawfulBasis()) &&
                Objects.equals(this.getSpecialCategoryData(), that.getSpecialCategoryData()) &&
                Objects.equals(this.getArticle9Condition(), that.getArticle9Condition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLawfulBasis(), getSpecialCategoryData(), getArticle9Condition());
    }
}

