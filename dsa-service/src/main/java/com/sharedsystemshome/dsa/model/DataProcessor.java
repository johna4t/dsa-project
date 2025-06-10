package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @NotBlank(message = "Data Processor email is null or empty.")
    @Column(name = "EMAIL",
            nullable = false,
            columnDefinition = "TEXT")
    private String email;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "WEBSITE",
            columnDefinition = "TEXT")
    private String website;

    @JsonIncludeProperties({"id", "name"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "data_processor_accreditation",
            joinColumns = @JoinColumn(
                    name = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "accreditation_id",
                    nullable = false)
    )
    private List<DataProcessorAccreditation> accreditations;

    @JsonIncludeProperties({"id"})
    @OneToMany(
            mappedBy = "dataProcessor",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DataProcessingActivity> associatedDataContent = new ArrayList<>();

    public DataProcessor(Long id,
                         DataSharingParty controller,
                         String name,
                         String email,
                         String description,
                         String website,
                         List<DataProcessorAccreditation> accreditations,
                         List<DataProcessingActivity> associatedDataContent
    ) {
        this.id = id;
        // Set owning entity
        this.controller = controller;
        this.name = name;
        this.email = email;
        this.description = description;
        this.website = website;
        this.accreditations = accreditations;
        this.associatedDataContent = associatedDataContent;

        initialiseDefaultValues();
    }

    @Builder
    public DataProcessor(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.accreditations) {
            this.accreditations = new ArrayList<>();
            this.accreditations.add(DataProcessorAccreditation.builder().build());
        }

        if(null != this.controller){
            this.controller.addDataProcessor(this);
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
                ", accreditations=" + (null != accreditations ?
                JpaLogUtils.getObjectIds(accreditations, DataProcessorAccreditation::getId) : "null") +
                ", associatedDataContent=" + (null != associatedDataContent ?
                JpaLogUtils.getObjectIds(associatedDataContent, DataProcessingActivity::getId) : "null") +
                '}';
    }

    @Transient
    @Override
    @JsonProperty("isReferenced")
    public Boolean isReferenced() {
        return this.associatedDataContent != null && !this.associatedDataContent.isEmpty();
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
