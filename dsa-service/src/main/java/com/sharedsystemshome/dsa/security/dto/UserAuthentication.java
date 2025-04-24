package com.sharedsystemshome.dsa.security.dto;

import com.sharedsystemshome.dsa.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthentication {

    private String accessToken;
    private String refreshToken;
    private UserAccount user;
}
