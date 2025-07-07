package com.sharedsystemshome.dsa.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.enums.ProcessingCertificationStandard;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;


@Entity
@Table(name = "DATA_PROCESSOR_CERTIFICATION",
        uniqueConstraints = @UniqueConstraint(columnNames = {"data_processor_id", "name"}))
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataProcessorCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dp_cert_seq")
    @SequenceGenerator(name = "dp_cert_seq", sequenceName = "dp_cert_seq", allocationSize = 1, initialValue = 114000001)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private ProcessingCertificationStandard name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "data_processor_id", nullable = false)
    private DataProcessor dataProcessor;

    // Optional: certification date, URL, etc.

    @Builder
    public DataProcessorCertification(Long id,
                                      ProcessingCertificationStandard name,
                                      DataProcessor dataProcessor) {
        this.id = id;
        this.name = name != null ? name : ProcessingCertificationStandard.NONE;
        this.dataProcessor = dataProcessor;
    }

    public DataProcessorCertification() {
        this.name = ProcessingCertificationStandard.NONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProcessorCertification that)) return false;
        return Objects.equals(dataProcessor, that.dataProcessor) &&
                name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataProcessor, name);
    }
}

