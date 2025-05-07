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

//@Data
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

    @Builder
    public DataSharingParty(Long id,
                            // Owning entity
//                            CustomerAccount account,
                            String description,
                            List<DataFlow> providedDataFlows,
                            List<DataFlow> consumedDataFlows
    ) {
        this.id = id;
        // Set owning entity
//        this.account = account;
//        this.account.setDataSharingParty(this);
        this.description = description;
        this.providedDataFlows = providedDataFlows;
        this.consumedDataFlows = consumedDataFlows;
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.account.getName();
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return this.account.getUrl();
    }

    public List<DataContentDefinition> getProviderDcds() {
        return providerDcds;
    }

    public List<DataFlow> getProvidedDataFlows() {
        return providedDataFlows;
    }

    public List<DataFlow> getConsumedDataFlows() {
        return consumedDataFlows;
    }

    public CustomerAccount getAccount() {
        return account;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProviderDcds(List<DataContentDefinition> providerDcds) {
        this.providerDcds = providerDcds;
    }

    public void setProvidedDataFlows(List<DataFlow> providedDataFlows) {
        this.providedDataFlows = providedDataFlows;
    }

    public void setConsumedDataFlows(List<DataFlow> consumedDataFlows) {
        this.consumedDataFlows = consumedDataFlows;
    }

    public void setAccount(CustomerAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "DataSharingParty{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", providerDcds=" + JpaLogUtils.getObjectIds(providerDcds, DataContentDefinition::getId) +
                ", providedDataFlows=" + JpaLogUtils.getObjectIds(providedDataFlows, DataFlow::getId) +
                ", consumedDataFlows=" + JpaLogUtils.getObjectIds(consumedDataFlows, DataFlow::getId) +
                ", account=" + account.getId() +
                '}';
    }
}
