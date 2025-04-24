package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.enums.CustomerPlanType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity(name = "CustomerPan")
@Table(name = "CUSTOMER_PLAN")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class CustomerPlan {
    // Customer Plan id and primary key
    @Id
    @SequenceGenerator(
            name = "customer_plan_sequence",
            sequenceName = "customer_plan_sequence",
            allocationSize = 1,
            initialValue = 111000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_plan_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @NotBlank(message = "Customer Plan name null or empty")
    @Column(name = "NAME",
            nullable = false,
            columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private CustomerPlanType name;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;

    @NotEmpty(message = "Customer Plan maximum number of agreements null or empty.")
    @Column(name = "MAX_AGREEMENTS",
            nullable = false,
            columnDefinition = "INTEGER")
    private Integer maxAgreements = 0;

    @NotEmpty(message = "Customer Plan maximum number of users null or empty.")
    @Column(name = "MAX_USERS",
            nullable = false,
            columnDefinition = "INTEGER")
    private Integer maxUsers = 0;

    @NotEmpty(message = "Customer Plan maximum number of partners null or empty.")
    @Column(name = "MAX_PARTNERS",
            nullable = false,
            columnDefinition = "INTEGER")
    private Integer maxPartners = 0;

}
