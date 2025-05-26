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
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Data
@Entity(name = "DataFlow")
@Table(name = "DATA_FLOW")
@JsonInclude(Include.NON_NULL)
@Valid
public class DataFlow implements Owned, Referenceable{

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

    @OneToMany(
            mappedBy = "dataFlow",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SharedDataContent> associatedDataContent = new ArrayList<>();

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
            List<DataContentDefinition> dataContent
    ) {
        this.id = id;
        this.dataSharingAgreement = dataSharingAgreement;
        this.provider = provider;
        this.consumer = consumer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lawfulBasis = lawfulBasis;
        this.specialCategory = specialCategory;
        this.purposeOfSharing = purposeOfSharing;

        this.associatedDataContent = new ArrayList<>();
        if (dataContent != null) {
            dataContent.forEach(dcd -> {
                SharedDataContent association = new SharedDataContent(this, dcd);
                this.associatedDataContent.add(association);
                dcd.getAssociatedDataFlows().add(association); // Sync reverse side
            });
        }

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

        boolean alreadyLinked = this.associatedDataContent.stream()
                .anyMatch(assoc -> assoc.getDataContentDefinition().equals(dcd));

        if (!alreadyLinked) {
            this.associatedDataContent.add(new SharedDataContent(this, dcd));
        }
    }


    public void removeDataContentDefinition(DataContentDefinition dcd) {
        Iterator<SharedDataContent> iterator = this.associatedDataContent.iterator();
        while (iterator.hasNext()) {
            SharedDataContent assoc = iterator.next();
            if (assoc.getDataContentDefinition().equals(dcd)) {
                // Break both references
                assoc.setDataFlow(null);
                assoc.setDataContentDefinition(null);

                // ✅ Remove from both collections
                dcd.getAssociatedDataFlows().remove(assoc);  // remove from DCD side
                iterator.remove();                           // remove from DF side
            }
        }
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
                ", dataSharingAgreement=" + (null != dataSharingAgreement ? dataSharingAgreement.getId() : "null") +
                ", purposeOfSharing='" + purposeOfSharing + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", provider=" + (null != provider ? provider.getId() : "null") +
                ", consumer=" + (null != consumer ? consumer.getId() : "null") +
                ", isPersonalData=" + isPersonalData +
                ", lawfulBasis=" + lawfulBasis +
                ", isSpecialCategoryData=" + isSpecialCategoryData +
                ", specialCategory=" + specialCategory +
                ", associatedDataContent=" + (null != associatedDataContent ?
                JpaLogUtils.getObjectIds(associatedDataContent, SharedDataContent::getId) : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataFlow other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Long ownerId() {
        return this.dataSharingAgreement.getId();
    }

    @Override
    public Long objectId() {
        return this.getId();
    }

    @Override
    public String entityName() {
        return DataFlow.class.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    @Override
    public Boolean isReferenced() {
        return this.associatedDataContent != null && !this.associatedDataContent.isEmpty();
    }
}