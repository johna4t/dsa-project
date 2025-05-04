package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.util.conversion.DurationStringConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity(name = "DataContentDefinition")
@Table(name = "DATA_CONTENT_DEFINITION")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataContentDefinition {

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
    @Convert(converter = DurationStringConverter.class)
    private Duration retentionPeriod;

    @OneToMany(mappedBy = "dataContentDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataContentPerspective> perspectives;


    @Builder
    public DataContentDefinition(
            Long id,
            // Owning entity
            DataSharingParty provider,
            String name,
            String description,
            DataContentType dataContentType,
            Duration retentionPeriod,
            List<DataContentPerspective> perspectives
    ) {
        this.id = id;
        // Set owning entity
        this.provider = provider;
        this.name = name;
        this.description = description;
        this.dataContentType = dataContentType;
        this.retentionPeriod = retentionPeriod;
        this.perspectives = perspectives;
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
                ", provider=" + provider +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
