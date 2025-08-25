package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@Entity(name = "DataProcessingActivity")
@Table(name = "DATA_PROCESSING_ACTIVITY")
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
    @JsonIncludeProperties({"id", "name", "controller"})
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

    @NotBlank(message = "Data Processing Activity name is null or empty.")
    @Column(name = "NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String name;

    @Column(name = "DESCRIPTION",
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

        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){
        if(null != this.dataProcessor){
            this.dataProcessor.addAssociatedDataProcessing(this);
        }
        if(null != this.dataContentDefinition){
            this.dataContentDefinition.addAssociatedDataProcessing(this);
        }
    }

    public void addActionPerformed(DataProcessingAction action) {
        if (action == null) return;

        if (this.actionsPerformed == null) {
            this.actionsPerformed = new ArrayList<>();
        }

        // Reject duplicates based on actionType + description
        boolean alreadyExists = this.actionsPerformed.stream()
                .anyMatch(existing -> existing.equals(action));

        if (!alreadyExists) {
            action.setProcessingActivity(this); // set back-reference
            this.actionsPerformed.add(action);
        }
    }

    public void removeActionPerformed(DataProcessingAction action) {
        if (action == null || this.actionsPerformed == null) return;

        if (this.actionsPerformed.remove(action)) {
            action.setProcessingActivity(null);
        }
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