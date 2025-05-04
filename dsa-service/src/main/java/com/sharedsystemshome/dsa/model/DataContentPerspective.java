package com.sharedsystemshome.dsa.model;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.util.conversion.HashMapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "DATA_CONTENT_PERSPECTIVE")
@Getter
@Setter
@NoArgsConstructor
public class DataContentPerspective {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dcp_seq")
    @SequenceGenerator(name = "dcp_seq", sequenceName = "dcp_seq", allocationSize = 1)
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

    public Object get(String key) {
        return metadata != null ? metadata.get(key) : null;
    }

    public void put(String key, Object value) {
        if (metadata != null) {
            metadata.put(key, value);
        }
    }
}
