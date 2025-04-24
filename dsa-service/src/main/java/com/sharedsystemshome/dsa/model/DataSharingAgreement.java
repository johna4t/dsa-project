package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "DataSharingAgreement")
@Table(name = "DATA_SHARING_AGREEMENT")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSharingAgreement {

    // Data Sharing Agreement id and primary key
    @Id
    @SequenceGenerator(
            name = "dsa_sequence",
            sequenceName = "dsa_sequence",
            allocationSize = 1,
            initialValue = 103000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dsa_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;


    @NotBlank(message = "Data Sharing Agreement name null or empty.")
    @Column(name = "NAME",
            nullable = false)
    private String name;

    @NotNull(message = "Data Sharing Agreement controller relationship null.")
    @Column(name = "DATA_CONTROLLER_RLN",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private ControllerRelationship controllerRelationship;

    @NotNull(message = "Data Sharing Agreement start date null.")
    @Column(name = "START_DATE",
            nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @Column(name = "END_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.of(9999,9,9);

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "dataSharingAgreement",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<DataFlow> dataFlows;

    // Customer Account foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne(
            cascade = CascadeType.DETACH,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "customerAccountId",
            referencedColumnName = "id",
            nullable = false
    )
    private CustomerAccount accountHolder;

    @JsonIgnore
    public Period getPeriodOfAgreement() {
        return Period.between(this.startDate, this.endDate);
    }

    public void addDataFlow(DataFlow dataFlow){
        this.dataFlows.add(dataFlow);
        dataFlow.setDataSharingAgreement(this);
    }
    public void deleteDataFlow(DataFlow dataFlow) {
        this.dataFlows.remove(dataFlow);
        dataFlow.setDataSharingAgreement(null);
    }

    @Builder
    public DataSharingAgreement(
            Long id,
            // Owning entity
            CustomerAccount accountHolder,
            String name,
            ControllerRelationship controllerRelationship,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.id = id;
        // Set owning entity
        this.accountHolder = accountHolder;
        this.name = name;
        this.controllerRelationship = controllerRelationship;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialiseDefaultValues();
    }

    @Builder
    public DataSharingAgreement(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.controllerRelationship){
            this.controllerRelationship = ControllerRelationship.JOINT;
        }
        if(null == this.startDate){
            this.startDate = LocalDate.now();
        }
        if(null == this.endDate){
            this.endDate = this.startDate.plusDays(365);
        }
        this.dataFlows = new ArrayList<DataFlow>();
        if(null != this.accountHolder){
            this.accountHolder.addDataSharingAgreement(this);
        }
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "DataSharingAgreement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", controllerRelationship=" + controllerRelationship +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", dataFlows=" + dataFlows +
                '}';
    }


}

