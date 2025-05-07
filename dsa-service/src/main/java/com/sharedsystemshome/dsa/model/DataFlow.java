package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "DataFlow")
@Table(name = "DATA_FLOW")
@JsonInclude(Include.NON_NULL)
@Valid
public class DataFlow {

    // Data Flow id and primary key
    @Id
    @SequenceGenerator(
            name = "dataflow_sequence",
            sequenceName = "dataflow_sequence",
            allocationSize = 1,
            initialValue = 102000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dataflow_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    // DataSharingAgreement parent Entity and foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne
    @JoinColumn(
            name = "dsaId",
            referencedColumnName = "id",
            nullable = false
    )
    private DataSharingAgreement dataSharingAgreement;

    @Column(name = "PURPOSE")
    private String purposeOfSharing;

    @NotNull(message = "Data Flow start date null.")
    @Column(name = "START_DATE",
            nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;// = LocalDate.now();

    @Column(name = "END_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // Provider foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne(
            cascade = CascadeType.DETACH,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "providerId",
            referencedColumnName = "id",
            nullable = false
    )
    private DataSharingParty provider;

    // Consumer foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne(
            cascade = CascadeType.DETACH,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "consumerId",
            referencedColumnName = "id",
            nullable = false
    )
    private DataSharingParty consumer;

    /**
     * Indicates if personal data.
     */
    @Transient
    private Boolean isPersonalData;

    /**
     * Lawful basis for processing personal data.
     */
    @Column(name = "LAWFUL_BASIS")
    @Enumerated(EnumType.STRING)
    private LawfulBasis lawfulBasis;

    /**
     * Indicates if Special Category personal data.
     */
    @Transient
    private Boolean isSpecialCategoryData;

    /**
     * The Special Category of personal data if applicable.
     */
    @Column(name = "SPECIAL_CATEGORY")
    @Enumerated(EnumType.STRING)
    private SpecialCategoryData specialCategory;

    @JsonIncludeProperties({"id"})
    @ManyToMany
    @JoinTable(
            name = "sharedDataContent",
            joinColumns = @JoinColumn(name = "dfId"),
            inverseJoinColumns = @JoinColumn(name = "dcdId", nullable = false)
    )
    private List<DataContentDefinition> providedDcds;

    @JsonInclude
    public Boolean getIsPersonalData() {
        return LawfulBasis.NOT_PERSONAL_DATA != this.lawfulBasis;
    }

    @JsonInclude
    public Boolean getIsSpecialCategoryData() {
        return SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA != this.specialCategory;
    }

    @JsonIgnore
    public Period getPeriodOfAgreement() {
        return Period.between(this.startDate, this.endDate);
    }

    @Builder
    public DataFlow(
            Long id,
            DataSharingAgreement dataSharingAgreement,
            DataSharingParty provider,
            DataSharingParty consumer,
            LocalDate startDate,
            LocalDate endDate,
            LawfulBasis lawfulBasis,
            SpecialCategoryData specialCategory,
            String purposeOfSharing,
            List<DataContentDefinition> providedDcds) {
        this.id = id;
        this.dataSharingAgreement = dataSharingAgreement;
        this.provider = provider;
        this.consumer = consumer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lawfulBasis = lawfulBasis;
        this.specialCategory = specialCategory;
        this.purposeOfSharing = purposeOfSharing;
        this.providedDcds = providedDcds;
        this.initialiseDefaultValues();
    }

    @Builder
    public DataFlow() {
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues() {
        if (null == this.startDate) {
            this.startDate = LocalDate.now();
        }
        if (null == this.endDate) {
            this.endDate = this.startDate.plusDays(365);
        }
        if (null == this.lawfulBasis) {
            this.lawfulBasis = LawfulBasis.NOT_PERSONAL_DATA;
        }
        if (null == this.specialCategory) {
            this.specialCategory = SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA;
        }
        if (null == this.providedDcds) {
            this.providedDcds = new ArrayList<>();
        }
        if (null != this.provider) {
            this.provider.addProvidedDataFlow(this);
        }
        if (null != this.consumer) {
            this.consumer.addConsumedDataFlow(this);
        }
        if(null != this.dataSharingAgreement){
            this.dataSharingAgreement.addDataFlow(this);
        }

    }

    public void addDataContentDefinition(DataContentDefinition dcd) {

        int max = this.providedDcds.size();
        boolean dcdExists = false;
        Long partyId = dcd.getId();

        for (int i = 0; i < max; i++) {
            if (partyId == this.providedDcds.get(i).getId()) {
                dcdExists = true;
            }
        }

        if (!dcdExists) {
            this.providedDcds.add(dcd);
        }
    }

    public void removeDataContentDefinition(DataContentDefinition dcd) {

        List<DataContentDefinition> updatedDcds = new ArrayList<>(this.providedDcds);
        updatedDcds.remove(dcd);
        this.providedDcds = updatedDcds;
    }

    public String toJsonString() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public String toString() {
        return "DataFlow{" +
                "id=" + id +
                ", dataSharingAgreement=" + dataSharingAgreement.getId() +
                ", purposeOfSharing='" + purposeOfSharing + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", provider=" + provider.getId() +
                ", consumer=" + consumer.getId() +
                ", isPersonalData=" + isPersonalData +
                ", lawfulBasis=" + lawfulBasis +
                ", isSpecialCategoryData=" + isSpecialCategoryData +
                ", specialCategory=" + specialCategory +
                ", providedDcds=" + JpaLogUtils.getObjectIds(providedDcds, DataContentDefinition::getId) +
                '}';
    }
}