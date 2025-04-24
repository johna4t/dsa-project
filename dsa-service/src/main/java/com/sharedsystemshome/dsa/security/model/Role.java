package com.sharedsystemshome.dsa.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity(name = "Role")
@Table(name = "ROLE")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class Role {

    // Role id and primary key
    @Id
    @SequenceGenerator(
            name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1,
            initialValue = 107000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "role_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @NotNull(message = "Role name null.")
    @Column(name = "NAME",
            nullable = false,
            unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(
                    name = "role_id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id")
    )
    private List<Permission> permissions;

    @Builder
    public Role(Long id, RoleType name, List<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.initialiseDefaultValues();
    }

    @Builder
    public Role(){
        this.initialiseDefaultValues();
    }


    private void initialiseDefaultValues(){

        if(null == this.name){
            this.name = RoleType.USER;
        }
        if(null == this.permissions){
            this.permissions = new ArrayList(List.of(new Permission()));
        }
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                ", permissions=" + permissions +
                '}';
    }


    public List<SimpleGrantedAuthority> getAuthorities(){

        List<SimpleGrantedAuthority> authorities = this.getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName().name()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name.name()));

        return authorities;

    }

}
