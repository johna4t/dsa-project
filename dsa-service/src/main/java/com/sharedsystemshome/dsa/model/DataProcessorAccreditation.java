package com.sharedsystemshome.dsa.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.enums.ProcessingAccreditationStandard;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Entity(name = "DataProcessorAccreditation")
@Table(name = "DATA_PROCESSOR_ACCREDITATION")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class DataProcessorAccreditation {

    // Role id and primary key
    @Id
    @SequenceGenerator(
            name = "data_proc_accred_sequence",
            sequenceName = "data_proc_accred_sequence",
            allocationSize = 1,
            initialValue = 114000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "data_proc_accred_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @NotNull(message = "Data processing accreditation name is null.")
    @Column(name = "NAME",
            nullable = false,
            unique = true)
    @Enumerated(EnumType.STRING)
    private ProcessingAccreditationStandard name;

    @Builder
    public DataProcessorAccreditation(Long id, ProcessingAccreditationStandard name) {
        this.id = id;
        this.name = name;
        this.initialiseDefaultValues();
    }

    @Builder
    public DataProcessorAccreditation(){
        this.initialiseDefaultValues();
    }


    private void initialiseDefaultValues(){

        if(null == this.name){
            this.name = ProcessingAccreditationStandard.NONE;
        }

    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" +
                '}';
    }


}
