package com.sharedsystemshome.dsa.security.dto;


import com.sharedsystemshome.dsa.security.model.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class UserRegistration {

    @NotBlank(message = "User Registration first name null or empty.")
    private String firstName;
    @NotBlank(message = "User Registration last name null or empty.")
    private String lastName;
    @NotBlank(message = "User Registration email null or empty.")
    private String email;
    @NotBlank(message = "User Registration password null or empty.")
    private String password;
    @NotBlank(message = "User Registration contact number null or empty.")
    private String contactNumber;
    @NotEmpty(message = "User Registration roles null.")
    @Size(min = 1, message = "User Registration roles empty.")
    private List<Role> roles;

}
