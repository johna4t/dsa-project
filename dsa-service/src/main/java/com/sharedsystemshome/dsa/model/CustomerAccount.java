package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.util.conversion.HashMapConverter;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;
import java.util.function.Function;

@Data
@Entity(name = "CustomerAccount")
@Table(name = "CUSTOMER_ACCOUNT")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class CustomerAccount {

    @Id
    @SequenceGenerator(
            name = "customer_account_sequence",
            sequenceName = "customer_account_sequence",
            allocationSize = 1,
            initialValue = 109000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_account_sequence"
    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    //Name of organisation
    @NotBlank(message = "Customer Account name null or empty.")
    @Column(name = "NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String name;

    @NotBlank(message = "Customer Account url null or empty.")
    @Column(name = "URL",
            columnDefinition = "TEXT")
    private String url;

    //Name of team or department
    @NotBlank(message = "Customer Account department name null or empty.")
    @Column(name = "DEPT_NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String departmentName;

    //Name of office or branch
    @Column(name = "BRANCH_NAME",
            nullable = true,
            columnDefinition = "TEXT")
    private String branchName;

    @Column(name = "ADDRESS",
            nullable = false,
            columnDefinition = "TEXT")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> address;

    @Setter(AccessLevel.NONE)
    @JsonIncludeProperties({"id"})
    @OneToMany(
            mappedBy = "accountHolder",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<DataSharingAgreement> agreements;

    @JsonIncludeProperties({"id"})
    @ManyToMany
    @JoinTable(
            name = "dataSharingPartnership",
            joinColumns = @JoinColumn(name = "custId"),
            inverseJoinColumns = @JoinColumn(name = "dspId")
    )
    private List<DataSharingParty> dataSharingPartners;

    @OneToOne(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @PrimaryKeyJoinColumn
    private DataSharingParty dataSharingParty;

    @OneToMany(
            mappedBy = "parentAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<UserAccount> users;


    @Builder
    public CustomerAccount(Long id,
                           String name,
                           String departmentName,
                           String url,
                           String branchName,
                           Address address,
                           DataSharingParty dataSharingParty,
                           List<UserAccount> users,
                           List<DataSharingParty> dataSharingPartners
    ) {
        this.id = id;

        this.name = name;

        this.departmentName = departmentName;

        this.url = url;

        this.branchName = branchName;

        this.dataSharingParty = dataSharingParty;

        this.dataSharingPartners = dataSharingPartners;

        if(null != address){
            this.address = new TreeMap<>();
            this.address.putAll(address.getAddress());
        }

        this.users = users;

        this.initialiseDefaultValues();
    }

    @Builder
    public CustomerAccount(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.address){
            this.address = new TreeMap<>();
            this.address.putAll(new Address().getAddress());
        }

        this.agreements = new ArrayList<>();

        if(null == this.dataSharingParty){
            this.dataSharingParty = new DataSharingParty();
        }
        this.dataSharingParty.setAccount(this);

        if(null == this.dataSharingPartners){
            this.dataSharingPartners = new ArrayList<>();
        }
        if(null == this.users){
            this.users = new ArrayList<>();
        }

    }

    public Address getAddress(){

        return new Address(this.address);

    }

    public void setAddress(Address address){

        if(null != address){
            this.address = address.getAddress();
        }
    }

    public void addDataSharingAgreement(DataSharingAgreement dsa){
        this.agreements.add(dsa);
    }

    public void deleteDataSharingAgreement(DataSharingAgreement dsa) {
        this.agreements.remove(dsa);
        dsa.setAccountHolder(null);
    }

    public void addUserAccount(UserAccount user){
        this.users.add(user);
        user.setParentAccount(this);
    }

    public void deleteUserAccount(UserAccount user) {
        this.users.remove(user);
        user.setParentAccount(null);
    }

    public void addDataSharingPartner(DataSharingParty dsp) {

        int max = this.dataSharingPartners.size();
        boolean dspExists = false;
        Long partyId = dsp.getId();

        for (int i = 0; i < max; i++) {
            if (partyId == this.dataSharingPartners.get(i).getId()) {
                dspExists = true;
            }
        }

        if (!dspExists) {
            this.dataSharingPartners.add(dsp);
        }
    }

    public void removeDataSharingPartner(DataSharingParty dsp) {

        List<DataSharingParty> updatedDsps = new ArrayList<>(this.dataSharingPartners);
        updatedDsps.remove(dsp);
        this.dataSharingPartners = updatedDsps;
    }

@Override
    public String toString() {
        return "CustomerAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", address=" + address +
                ", agreements=" + (null != agreements ?
                JpaLogUtils.getObjectIds(agreements, DataSharingAgreement::getId) : "null") +
                ", dataSharingPartners=" + (null != dataSharingPartners ?
                JpaLogUtils.getObjectIds(dataSharingPartners, DataSharingParty::getId) : "null") +
                ", dataSharingParty=" + (null != dataSharingParty ? dataSharingParty.getId() : "null") +
                ", users=" + (null != users ?
                JpaLogUtils.getObjectIds(users, UserAccount::getId) : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerAccount other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
