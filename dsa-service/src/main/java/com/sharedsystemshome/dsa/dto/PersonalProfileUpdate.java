package com.sharedsystemshome.dsa.dto;

import com.sharedsystemshome.dsa.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalProfileUpdate {

    private UserAccount user;

    private String oldPassword;
}

