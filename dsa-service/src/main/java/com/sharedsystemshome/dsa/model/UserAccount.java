package com.sharedsystemshome.dsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.model.Token;
import com.sharedsystemshome.dsa.util.JpaLogUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Entity(name = "UserAccount")
@Table(name = "USER_ACCOUNT")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class UserAccount implements UserDetails {

    // User Account id and primary key
    @Id
    @SequenceGenerator(
            name = "user_account_sequence",
            sequenceName = "user_account_sequence",
            allocationSize = 1,
            initialValue = 105000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_account_sequence"

    )
    @Column(name = "ID",
            updatable = false)
    private Long id;

    @NotBlank(message = "User Account first name null or empty.")
    @Column(name = "FIRST_NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String firstName;

    @NotBlank(message = "User Account last name null or empty.")
    @Column(name = "LAST_NAME",
            nullable = false,
            columnDefinition = "TEXT")
    private String lastName;

    @NotBlank(message = "User Account email null or empty.")
    @Email(message = "User Account email invalid")
    @Column(name = "EMAIL",
            nullable = false,
            unique = true,
            columnDefinition = "TEXT")
    private String email;

    @NotBlank(message = "User Account contact number null or empty.")
    @Column(name = "CONTACT_NUMBER",
            nullable = false,
            columnDefinition = "TEXT")
    private String contactNumber;

    @Column(name = "JOB_TITLE",
            columnDefinition = "TEXT")
    private String jobTitle;

    @NotBlank(message = "User Account password null or empty.")
    @Column(name = "PASSWORD",
            nullable = false,
            columnDefinition = "TEXT")
    private String password;

    @NotNull(message = "User Account status null.")
    @Column(name = "STATUS",
            nullable = false,
            columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus status;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "ACCOUNT_NOT_EXPIRED")
    private Boolean isAccountNonExpired;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "ACCOUNT_NOT_LOCKED")
    private Boolean isAccountNonLocked;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "CREDS_NOT_EXPIRED")
    private Boolean isCredentialsNonExpired;

    // UserAccount parent Entity and foreign key
    @JsonIncludeProperties({"id"})
    @ManyToOne
    @JoinColumn(
            name = "accountId",
            referencedColumnName = "id",
            nullable = false
    )
    private CustomerAccount parentAccount;

    @JsonIncludeProperties({"id", "name"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(
                    name = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    nullable = false)
    )
    private List<Role> roles;

    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Token> tokens;

    @JsonIgnore
    @OneToMany(
            mappedBy = "revokedBy",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Token> revokedTokens;

    @Builder
    public UserAccount(Long id,
                       // Owning entity
                       CustomerAccount parentAccount,
                       String firstName,
                       String lastName,
                       String email,
                       String contactNumber,
                       String jobTitle,
                       String password,
                       UserAccountStatus status,
                       List<Role> roles,
                       Boolean isAccountNonExpired,
                       Boolean isAccountNonLocked,
                       Boolean isCredentialsNonExpired) {
        this.id = id;
        this.parentAccount = parentAccount;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.jobTitle = jobTitle;
        this.password = password;
        this.status = status;
        this.roles = roles;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.initialiseDefaultValues();
    }

    @Builder
    public UserAccount(){
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.status) {
            //Default should probably be INACTIVE for live use
            this.status = UserAccountStatus.ACTIVE;
        }
        if(null == this.isAccountNonExpired) {
            this.isAccountNonExpired = true;
        }
        if(null == this.isAccountNonLocked) {
            this.isAccountNonLocked = true;
        }
        if(null == this.isCredentialsNonExpired) {
            this.isCredentialsNonExpired = true;
        }
        if(null == this.roles) {
            this.roles = new ArrayList(List.of(new Role()));
        }
        if(null != this.parentAccount){
            this.parentAccount.addUserAccount(this);
        }

    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // See MyUserDetailsService::getPrivileges
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (final Role role : this.roles) {
            authorities.addAll(role.getAuthorities());
        }

        return authorities;
    }






    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return UserAccountStatus.ACTIVE == this.status;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", parentAccount=" + (null != parentAccount ? parentAccount.getId() : "null") +
                ", roles=" + (null != roles ?
                JpaLogUtils.getObjectIds(roles, Role::getId) : "null") +
                ", tokens=" + (null != tokens ?
                JpaLogUtils.getObjectIds(tokens, Token::getId) : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
