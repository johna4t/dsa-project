package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharedsystemshome.dsa.enums.ProcessingCertificationStandard;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity(name = "DataProcessor")
@Table(name = "DATA_PROCESSOR")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataProcessor implements Referenceable, Owned {

    // Data Processor id and primary key
    @Id
    @SequenceGenerator(
            name = "data_proc_sequence",
            sequenceName = "data_proc_sequence",
            allocationSize = 1,
            initialValue = 113000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "data_proc_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    // DataSharingParty parent Entity and foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne
    @JoinColumn(
            name = "controllerId",
            referencedColumnName = "id",
            nullable=false
    )
    private DataSharingParty controller;

    @NotBlank(message = "Data Processor name is null or empty.")
    @Column(name = "NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String name;

    @Column(name = "EMAIL",
            columnDefinition = "TEXT")
    private String email;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Data Processor website is null or empty.")
    @Column(name = "WEBSITE",
            nullable = false,
            columnDefinition = "TEXT")
    private String website;


    @JsonIncludeProperties({"id", "name"})
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "dataProcessor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DataProcessorCertification> certifications = new ArrayList<>();;


    @JsonIncludeProperties({"id"})
    @OneToMany(
            mappedBy = "dataProcessor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DataProcessingActivity> associatedDataProcessing;

    @Builder
    public DataProcessor(Long id,
                         DataSharingParty controller,
                         String name,
                         String email,
                         String description,
                         String website,
                         List<ProcessingCertificationStandard> certifications
    ) {
        this.id = id;
        // Set owning entity
        this.controller = controller;
        this.name = name;
        this.email = email;
        this.description = description;
        this.website = website;

        this.setCertifications(certifications);

        initialiseDefaultValues();
    }

    @Builder
    public DataProcessor(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if (this.certifications == null) {
            this.certifications = new ArrayList<>();
        } else {
            this.certifications.forEach(a -> a.setDataProcessor(this));
        }

        if(null == this.associatedDataProcessing) {
            this.associatedDataProcessing = new ArrayList<>();
        }

        if(null != this.controller){
            this.controller.addDataProcessor(this);
        }

    }

    public List<ProcessingCertificationStandard> getCertifications(){
        if (this.certifications == null) {
            return Collections.emptyList();
        }
        return this.certifications.stream()
                .map(DataProcessorCertification::getName)
                .distinct()
                .toList();
    }

    public void setCertifications(List<ProcessingCertificationStandard> certifications) {
        // Always reinitialize with a new mutable list
        // Always start with a new mutable list
        this.certifications = new ArrayList<>();

        if (certifications != null) {
            // Use a Set to ensure uniqueness
            Set<ProcessingCertificationStandard> uniqueCerts = new LinkedHashSet<>(certifications);

            for (ProcessingCertificationStandard cert : uniqueCerts) {
                if (cert != null) {
                    this.certifications.add(
                            DataProcessorCertification.builder()
                                    .name(cert)
                                    .dataProcessor(this)
                                    .build()
                    );
                }
            }
        }
    }

    public void addCertification(ProcessingCertificationStandard standard) {
        if (standard != null) {
            boolean exists = this.certifications.stream()
                    .anyMatch(existing -> existing.getName() == standard);

            if (!exists) {
                DataProcessorCertification certification = DataProcessorCertification.builder()
                        .name(standard)
                        .dataProcessor(this)
                        .build();
                this.certifications.add(certification);
            }
        }
    }

    public void removeCertification(ProcessingCertificationStandard standard) {
        if (standard != null) {
            this.certifications.removeIf(existing -> {
                boolean match = existing.getName() == standard;
                if (match) {
                    existing.setDataProcessor(null);
                }
                return match;
            });
        }
    }

    @Override
    public String toString() {
        return "DataProcessor{" +
                "id=" + id +
                ", controller=" + (null != controller ? controller.getId() : "null") +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", website='" + website + '\'' +
                ", certifications=" + (null != certifications ?
                JpaLogUtils.getObjectIds(certifications, DataProcessorCertification::getId) : "null") +
                ", associatedDataContent=" + (null != associatedDataProcessing ?
                JpaLogUtils.getObjectIds(associatedDataProcessing, DataProcessingActivity::getId) : "null") +
                '}';
    }

    @Transient
    @Override
    @JsonProperty("isReferenced")
    public Boolean isReferenced() {
        return this.associatedDataProcessing != null && !this.associatedDataProcessing.isEmpty();
    }

    @Override
    public Long ownerId() {
        return this.controller.getId();
    }

    @Override
    public Long objectId() {
        return this.getId();
    }

    @Override
    public String entityName() {

        return DataProcessor.class.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProcessor other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
