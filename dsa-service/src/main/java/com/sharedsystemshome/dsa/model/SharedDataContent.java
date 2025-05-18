package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table( name = "shared_data_content",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"dataFlowId", "dcdId"}
                )
        })
@NoArgsConstructor
public class SharedDataContent {

    @Id
    @SequenceGenerator(
            name = "shareddatacontent_sequence",
            sequenceName = "shareddatacontent_sequence",
            allocationSize = 1,
            initialValue = 110000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shareddatacontent_sequence"

    )
    private Long id;

    // Many-to-One with DataFlow (cascade delete allowed)
    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "dataFlowId",
            referencedColumnName = "id",
            nullable = false)
    private DataFlow dataFlow;

    // Many-to-One with DataContentDefinition (deletion blocked if referenced)
    @JsonIncludeProperties({"id"})
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "dcdId",
            referencedColumnName = "id",
            nullable = false)
    private DataContentDefinition dataContentDefinition;

    public SharedDataContent(
            DataFlow dataFlow,
            DataContentDefinition dataContentDefinition) {
        this.dataFlow = dataFlow;
        this.dataContentDefinition = dataContentDefinition;
    }
}
