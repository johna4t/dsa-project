package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import com.sharedsystemshome.dsa.util.conversion.PeriodStringConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Entity(name = "DataContentDefinition")
@Table(name = "DATA_CONTENT_DEFINITION")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataContentDefinition implements Referenceable, Owned {

    // DCD id and primary key
    @Id
    @SequenceGenerator(
            name = "dcd_sequence",
            sequenceName = "dcd_sequence",
            allocationSize = 1,
            initialValue = 104000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dcd_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;


    // DataSharingParty parent Entity and foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne
    @JoinColumn(
            name = "providerId",
            referencedColumnName = "id",
            nullable=false
    )
    private DataSharingParty provider;

    @NotBlank(message = "Data Content Definition name null or empty.")
    @Column(name = "NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String name;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;


    @NotNull(message = "Data Content Definition type null.")
    @Column(name = "DCD_TYPE",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private DataContentType dataContentType;

    @Column(name = "RETENTION_PERIOD", columnDefinition = "TEXT")
    @Convert(converter = PeriodStringConverter.class)
    private Period retentionPeriod;

//    @JsonIncludeProperties({"id"})
    @OneToMany(mappedBy = "dataContentDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataContentPerspective> perspectives;

    @Column(name = "OWNER_NAME",
            columnDefinition = "TEXT")
    private String ownerName;

    @NotBlank(message = "Data Content Definition owner email null or empty.")
    @Column(name = "OWNER_EMAIL",
            columnDefinition = "TEXT",
            nullable = false)
    private String ownerEmail;

    @NotBlank(message = "Data Content Definition source system null or empty.")
    @Column(name = "SOURCE_SYSTEM",
            columnDefinition = "TEXT",
            nullable = false)
    private String sourceSystem;

    @JsonIncludeProperties({"id"})
    @OneToMany(
            mappedBy = "dataContentDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SharedDataContent> associatedDataFlows = new ArrayList<>();

    @JsonIncludeProperties({"id"})
    @OneToMany(
            mappedBy = "dataContentDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DataProcessingActivity> associatedDataProcessing = new ArrayList<>();

    @Builder
    public DataContentDefinition(
            Long id,
            // Owning entity
            DataSharingParty provider,
            String name,
            String description,
            DataContentType dataContentType,
            Period retentionPeriod,
            List<DataContentPerspective> perspectives,
            String ownerName,
            String ownerEmail,
            String sourceSystem
    ) {
        this.id = id;
        // Set owning entity
        this.provider = provider;
        this.name = name;
        this.description = description;
        this.dataContentType = dataContentType;
        this.retentionPeriod = retentionPeriod;
        this.perspectives = perspectives;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.sourceSystem = sourceSystem;

        this.initialiseDefaultValues();
    }

    @Builder
    public DataContentDefinition(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.dataContentType) {
            this.dataContentType = DataContentType.NOT_SPECIFIED;
        }

        if(null != this.provider){
            this.provider.addDataContentDefinition(this);
        }

        if(null == this.perspectives){
            this.perspectives = new ArrayList<>();
        }

    }

    public Optional<DataContentPerspective> getPerspective(MetadataScheme scheme) {
        return perspectives.stream()
                .filter(p -> p.getMetadataScheme() == scheme)
                .findFirst();
    }

    public void addPerspective(DataContentPerspective perspective) {
        perspective.setDataContentDefinition(this);
        this.perspectives.add(perspective);
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "DataContentDefinition{" +
                "id=" + id +
                ", provider=" + (null != provider ? provider.getId() : "null") +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataContentType=" + dataContentType +
                ", retentionPeriod=" + retentionPeriod +
                ", perspectives=" + (null != perspectives ?
                JpaLogUtils.getObjectIds(perspectives, DataContentPerspective::getId) : "null") +
                ", ownerName='" + ownerName + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", associatedDataFlows=" + (null != associatedDataFlows ?
                JpaLogUtils.getObjectIds(associatedDataFlows, SharedDataContent::getId) : "null") +
                '}';
    }

    @Transient
    @Override
    @JsonProperty("isReferenced")
    public Boolean isReferenced() {
        return this.associatedDataFlows != null && !this.associatedDataFlows.isEmpty();
    }

    @Override
    public Long ownerId() {
        return this.provider.getId();
    }

    @Override
    public Long objectId() {
        return this.getId();
    }

    @Override
    public String entityName() {

        return DataContentDefinition.class.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataContentDefinition other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
