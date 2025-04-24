package com.sharedsystemshome.dsa.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.security.enums.PermissionType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Data
@Entity(name = "Permission")
@Table(name = "PERMISSION",
uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Permission {

    @Id
    @SequenceGenerator(
            name = "permission_sequence",
            sequenceName = "permission_sequence",
            allocationSize = 1,
            initialValue = 108000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "permission_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @Column(name = "NAME",
            unique = true)
    @Enumerated(EnumType.STRING)
    private PermissionType name;

    @Builder
    public Permission(Long id, PermissionType name) {
        this.id = id;
        this.name = name;
        this.initialiseDefaultValues();
    }
    @Builder
    public Permission(){
        this.initialiseDefaultValues();
    }


    private void initialiseDefaultValues(){

        if(null == this.name){
            this.name = PermissionType.PERMIT_READ;
        }
    }

}
