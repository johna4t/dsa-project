package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Builder
    public SharedDataContent(
            DataFlow dataFlow,
            DataContentDefinition dataContentDefinition) {
        this.dataFlow = dataFlow;
        this.dataContentDefinition = dataContentDefinition;
    }

    @Override
    public String toString() {
        return "SharedDataContent{" +
                "id=" + id +
                ", dataFlow=" + dataFlow +
                ", dataContentDefinition=" + dataContentDefinition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SharedDataContent other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
