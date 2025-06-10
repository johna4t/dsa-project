package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.DATA_CONTENT_PERSPECTIVE;

@Data
@Entity
@Table( name = "data_processing_activity",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"dataFlowId", "dcdId"}
                )
        })
@NoArgsConstructor
public class DataProcessingActivity {

    @Id
    @SequenceGenerator(
            name = "data_processing_activity_sequence",
            sequenceName = "data_processing_activity_sequence",
            allocationSize = 1,
            initialValue = 115000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "data_processing_activity_sequence"

    )
    private Long id;

    // Many-to-One with DataProcessor (cascade delete allowed)
    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "processorId",
            referencedColumnName = "id",
            nullable = false)
    private DataProcessor dataProcessor;

    // Many-to-One with DataContentDefinition (deletion blocked if referenced)
    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "dcdId",
            referencedColumnName = "id",
            nullable = false)
    private DataContentDefinition dataContentDefinition;

    @Builder
    public DataProcessingActivity(
            DataProcessor dataProcessor,
            DataContentDefinition dataContentDefinition) {
        this.dataProcessor = dataProcessor;
        this.dataContentDefinition = dataContentDefinition;
    }

    @Override
    public String toString() {
        return "SharedDataContent{" +
                "id=" + id +
                ", dataProcessor=" + (null != dataProcessor ? dataProcessor.getId() : "null") +
                ", dataContentDefinition=" + (null != dataContentDefinition ? dataContentDefinition.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProcessingActivity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}