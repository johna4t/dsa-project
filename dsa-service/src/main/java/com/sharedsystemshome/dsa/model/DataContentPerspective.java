package com.sharedsystemshome.dsa.model;

import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.util.conversion.HashMapConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "DATA_CONTENT_PERSPECTIVE")
@Getter
@Setter
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

    @Override
    public String toString() {
        return "DataContentPerspective{" +
                "id=" + id +
                ", dataContentDefinition=" + dataContentDefinition.getId() +
                ", metadataScheme=" + metadataScheme +
                ", metadata=" + metadata +
                '}';
    }
}
