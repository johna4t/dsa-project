package com.sharedsystemshome.dsa.security.dto;

//import com.sharedsystemshome.dsa.util.ValidatedEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
//import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class UserLogin {

    @NotBlank(message="User Login email null or empty.")
    private String email;

    @NotBlank(message="User Login password null or empty.")
    private String password;
}
