package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.util.conversion.HashMapConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Entity
@Table(name = "DATA_CONTENT_PERSPECTIVE")
public class DataContentPerspective {

    // DCP id and primary key
    @Id
    @SequenceGenerator(
            name = "dcp_sequence",
            sequenceName = "dcp_sequence",
            allocationSize = 1,
            initialValue = 112000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dcp_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = false)
    @JoinColumn(name = "dcd_id")
    private DataContentDefinition dataContentDefinition;

    @Enumerated(EnumType.STRING)
    @Column(name = "metadata_scheme", nullable = false)
    private MetadataScheme metadataScheme; // e.g. "GDPR"

    @Column(name = "metadata", columnDefinition = "TEXT")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> metadata;

    @Builder
    public DataContentPerspective(
            DataContentDefinition dataContentDefinition,
            MetadataScheme metadataScheme,
            Map<String, Object> metadata) {

        this.metadataScheme = metadataScheme;
        this.metadata = metadata;
        this.dataContentDefinition = dataContentDefinition;

        this.initialiseDefaultValues();
    }

    @Builder
    public DataContentPerspective(){

        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.metadataScheme){
            // Set to GDPR scheme as default
            this.metadataScheme = MetadataScheme.GDPR;

            this.metadata = new HashMap<>();
            this.metadata.put("lawfulBasis", LawfulBasis.NOT_PERSONAL_DATA);
            this.metadata.put("specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA);
        }

        if(null != this.dataContentDefinition){
            this.dataContentDefinition.addPerspective(this);
        }

    }

    public Object get(String key) {
        return metadata != null ? metadata.get(key) : null;
    }

    public void put(String key, Object value) {
        if (metadata != null) {
            metadata.put(key, value);
        }
    }

    public Metadata getMetadata(MetadataScheme scheme) {
        if (scheme.equals(this.metadataScheme)) {
            return GdprMetadata.builder()
                    .meta(this.metadata)
                    .build();
        }

        return null;
    }

    @Override
    public String toString() {
        return "DataContentPerspective{" +
                "id=" + id +
                ", dataContentDefinition=" + (null != dataContentDefinition ? dataContentDefinition.getId() : "null") +
                ", metadataScheme=" + metadataScheme +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataContentPerspective other = (DataContentPerspective) o;

        if (this.id != null && other.id != null && this.id.equals(other.id)) {
            // Otherwise, fall back to deep metadata comparison
            Metadata thisMetadata = this.getMetadata(this.getMetadataScheme());
            Metadata otherMetadata = other.getMetadata(other.getMetadataScheme());

            return thisMetadata.equals(otherMetadata);
        } else {
            return false;
        }


    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
