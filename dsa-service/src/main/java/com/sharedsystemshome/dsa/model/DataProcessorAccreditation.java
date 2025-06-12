package com.sharedsystemshome.dsa.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.enums.ProcessingAccreditationStandard;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;


@Entity
@Table(name = "DATA_PROCESSOR_ACCREDITATION",
        uniqueConstraints = @UniqueConstraint(columnNames = {"data_processor_id", "name"}))
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataProcessorAccreditation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dp_accred_seq")
    @SequenceGenerator(name = "dp_accred_seq", sequenceName = "dp_accred_seq", allocationSize = 1, initialValue = 114000001)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private ProcessingAccreditationStandard name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "data_processor_id", nullable = false)
    private DataProcessor dataProcessor;

    // Optional: certification date, URL, etc.

    @Builder
    public DataProcessorAccreditation(Long id,
                                      ProcessingAccreditationStandard name,
                                      DataProcessor dataProcessor) {
        this.id = id;
        this.name = name != null ? name : ProcessingAccreditationStandard.NONE;
        this.dataProcessor = dataProcessor;
    }

    public DataProcessorAccreditation() {
        this.name = ProcessingAccreditationStandard.NONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProcessorAccreditation that)) return false;
        return Objects.equals(dataProcessor, that.dataProcessor) &&
                name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataProcessor, name);
    }
}

