package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.DataProcessingActionType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Entity(name = "DataProcessingAction")
@Table(name = "DATA_PROCESSING_ACTION")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
@NoArgsConstructor
public class DataProcessingAction {

    // Data Processing Action id and primary key
    @Id
    @SequenceGenerator(
            name = "data_processing_action_sequence",
            sequenceName = "data_processing_action_sequence",
            allocationSize = 1,
            initialValue = 116000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "data_processing_action_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @NotNull(message = "Data Processing Action Type null.")
    @Column(name = "DPA_TYPE",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private DataProcessingActionType actionType;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT",
            nullable = true)
    private String description;

    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = true)
    @JoinColumn(name = "dpa_id")
    private DataProcessingActivity processingActivity;

    @Builder
    public DataProcessingAction(Long id,
                                DataProcessingActionType actionType,
                                String description,
                                DataProcessingActivity processingActivity) {
        this.id = id;
        this.actionType = actionType;
        this.description = description;
        this.processingActivity = processingActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProcessingAction other)) return false;

        // Normalise descriptions: null, "", and blank are considered equal
        String desc1 = (this.description == null || this.description.trim().isEmpty()) ? "" : this.description.trim();
        String desc2 = (other.description == null || other.description.trim().isEmpty()) ? "" : other.description.trim();

        return this.actionType == other.actionType && desc1.equals(desc2);
    }

    @Override
    public int hashCode() {
        // Use normalized description for consistent hashing
        String normalizedDescription = (description == null || description.trim().isEmpty()) ? "" : description.trim();
        return Objects.hash(actionType, normalizedDescription);
    }
}
