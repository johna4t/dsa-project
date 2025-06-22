package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity(name = "DataSharingParty")
@Table(name = "DATA_SHARING_PARTY")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSharingParty {

    @Id
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;


    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "provider",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<DataContentDefinition> providerDcds;

    @JsonIgnore
    @OneToMany(
            mappedBy = "controller",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<DataProcessor> processors;

    @JsonIgnore
//    @JsonIncludeProperties({"id"})
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "provider"
//            cascade = CascadeType.ALL
    )
    private List<DataFlow> providedDataFlows;

    @JsonIgnore
//    @JsonIncludeProperties({"id"})
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "consumer"
//            cascade = CascadeType.ALL
    )
    private List<DataFlow> consumedDataFlows;

    // Shared primary key with Customer Account
    @JsonIncludeProperties({"id"})
    @OneToOne
    @MapsId
    @JsonIgnore
    @JoinColumn(
            name = "id",
            nullable = false
    )
    private CustomerAccount account;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(
            name = "processor_id",
            referencedColumnName = "id")
    private DataProcessor selfAsProcessor;

    @Builder
    public DataSharingParty(Long id,
                            // Owning entity
//                            CustomerAccount account,
                            String description,
                            List<DataFlow> providedDataFlows,
                            List<DataFlow> consumedDataFlows,
                            List<DataProcessor> processors
    ) {
        this.id = id;
        // Set owning entity
//        this.account = account;
//        this.account.setDataSharingParty(this);
        this.description = description;
        this.providedDataFlows = providedDataFlows;
        this.consumedDataFlows = consumedDataFlows;
        this.processors = processors;
        this.initialiseDefaultValues();
    }

    @Builder
    public DataSharingParty(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){
        this.providerDcds = new ArrayList<DataContentDefinition>();
        this.providedDataFlows = new ArrayList<DataFlow>();
        this.consumedDataFlows = new ArrayList<DataFlow>();
        this.processors = new ArrayList<DataProcessor>();
    }

    public void addProvidedDataFlow(DataFlow dataFlow) {
        this.providedDataFlows.add(dataFlow);
        dataFlow.setProvider(this);
    }

    public void addConsumedDataFlow(DataFlow dataFlow) {
        this.consumedDataFlows.add(dataFlow);
        dataFlow.setConsumer(this);
    }

    public void addDataContentDefinition(DataContentDefinition dcd){
        this.providerDcds.add(dcd);
        dcd.setProvider(this);
    }

    public void deleteDataContentDefinition(DataContentDefinition dcd) {
        this.providerDcds.remove(dcd);
        dcd.setProvider(null);
    }

    public void addDataProcessor(DataProcessor processor){
        this.processors.add(processor);
        processor.setController(this);
    }

    public void deleteDataProcessor(DataProcessor processor) {
        this.processors.remove(processor);
        processor.setController(null);
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @JsonInclude
    public Boolean getIsProvider(){
        return !this.providedDataFlows.isEmpty();
    }

    @JsonInclude
    public Boolean getIsConsumer(){
        return !this.consumedDataFlows.isEmpty();
    }

    public void setAccount(CustomerAccount account) {
        this.account = account;
        if (this.account != null) {
            this.selfAsProcessor = DataProcessor.builder()
                    .name(this.account.getName())
                    .website(this.account.getUrl())
                    .description(this.description)
                    .build();

            // Set DSP as controller (to participate in normal controller-DP logic)
            this.selfAsProcessor.setController(this);
        }
    }

    @Override
    public String toString() {
        return "DataSharingParty{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", providerDcds=" + (null != providerDcds ?
                JpaLogUtils.getObjectIds(providerDcds, DataContentDefinition::getId) : "null") +
                ", providedDataFlows=" + (null != providedDataFlows ?
                JpaLogUtils.getObjectIds(providedDataFlows, DataFlow::getId) : "null") +
                ", consumedDataFlows=" + (null != consumedDataFlows ?
                JpaLogUtils.getObjectIds(consumedDataFlows, DataFlow::getId) : "null") +
                ", account=" + (null != account ? account.getId() : "null") +
                ", processors=" + (null != processors ?
                JpaLogUtils.getObjectIds(processors, DataProcessor::getId) : "null") +
                ", selfAsProcessor=" + (null != selfAsProcessor ? selfAsProcessor.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSharingParty other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
