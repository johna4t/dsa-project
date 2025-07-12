package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@Data
@Entity(name = "DataProcessingActivity")
@Table( name = "DATA_PROCESSING_ACTIVITY",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"processorId", "dcdId"}
                )
        })
@NoArgsConstructor
public class DataProcessingActivity implements Owned  {

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
    @JsonIncludeProperties({"id", "name"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "processorId",
            referencedColumnName = "id",
            nullable = false)
    private DataProcessor dataProcessor;

    // Many-to-One with DataContentDefinition (deletion blocked if referenced)
    @JsonIncludeProperties({"id", "name"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "dcdId",
            referencedColumnName = "id",
            nullable = false)
    private DataContentDefinition dataContentDefinition;

    @NotNull(message = "Data Processing Activity name is null.")
    @Column(name = "DPA_NAME",
            nullable = false)
    private String name;

    @Column(name = "DPA_DESC",
            nullable = true)
    private String description;


    @OneToMany(mappedBy = "processingActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataProcessingAction> actionsPerformed;

    @Builder
    public DataProcessingActivity(Long id,
                                  DataProcessor dataProcessor,
                                  DataContentDefinition dataContentDefinition,
                                  String name,
                                  String description) {
        this.id = id;
        this.dataProcessor = dataProcessor;
        this.dataContentDefinition = dataContentDefinition;
        this.name = name;
        this.description = description;
    }


    @Override
    public String toString() {
        return "DataProcessingActivity{" +
                "id=" + id +
                ", dataProcessor=" + (null != dataProcessor ? dataProcessor.getId() : "null") +
                ", dataContentDefinition=" + (null != dataContentDefinition ? dataContentDefinition.getId() : "null") +
                '}';
    }

    @Override
    public Long ownerId() {
        return this.dataProcessor.getController().getId();
    }

    @Override
    public Long objectId() {
        return this.getId();
    }

    @Override
    public String entityName() {

        return DataProcessingActivity.class.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
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