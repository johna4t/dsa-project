package com.sharedsystemshome.dsa.security.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Entity(name = "Token")
@Table(name = "TOKEN")
@Validated
public class Token {

    // User Account id and primary key
    @Id
    @SequenceGenerator(
            name = "token_sequence",
            sequenceName = "token_sequence",
            allocationSize = 1,
            initialValue = 106000001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_sequence"

    )
    @Column(name = "ID",
            updatable = false)
        public Long id;

    @Column(unique = true,
            nullable = false,
            name = "TOKEN")
    public String token;

    @NotNull(message = "Token type null.")
    @Column(name = "TOKEN_TYPE",
    nullable = false)
    @Enumerated(EnumType.STRING)
    public TokenType tokenType;

    @Column(name = "REVOKED")
    public Boolean revoked;

    @Column(name = "EXPIRED")
    public Boolean expired;

    @Column(name = "REVOKED_AT")
    private LocalDateTime revokedAt;

    @JsonIncludeProperties({"id"})
    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "USER_ID",
            referencedColumnName = "id",
            nullable=false
    )
    public UserAccount user;

    @JsonIncludeProperties({"id"})
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "REVOKED_BY",
            referencedColumnName = "id"
    )
    private UserAccount revokedBy;

    @Builder
    public Token(
            Long id,
            String token,
            TokenType tokenType,
            Boolean revoked,
            Boolean expired,
            UserAccount user,
            UserAccount revokedBy) {
        this.id = id;
        this.token = token;
        this.tokenType = tokenType;
        this.revoked = revoked;
        this.expired = expired;
        this.user = user;
        this.revokedBy = revokedBy;
        this.initialiseDefaultValues();
    }

    @Builder
    public Token() {
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues(){

        if(null == this.tokenType){
            this.tokenType = TokenType.ACCESS;
        }
        if(null == this.revoked){
            this.revoked = false;
        }
        if(null == this.expired){
            this.expired = false;
        }
    }
}
