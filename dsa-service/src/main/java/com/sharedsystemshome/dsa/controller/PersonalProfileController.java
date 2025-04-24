package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.dto.PersonalProfileUpdate;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.service.PersonalProfileService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/personal-profiles")
public class PersonalProfileController {
    private final PersonalProfileService profileService;
    private final UserContextService userContext;
    @Autowired
    public PersonalProfileController(
            PersonalProfileService profileService,
            UserContextService userContext){

        this.profileService = profileService;
        this.userContext = userContext;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<UserAccount> getPersonalProfileById(@PathVariable("id") Long id) {

        try {

            return ResponseEntity.status(200).body(
                    this.profileService.getPersonalProfileById(
                            this.userContext.getCurrentUser().getId()));

        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(204).build();

        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping
    public ResponseEntity<UserAccount> putPersonalProfile(
            RequestEntity<PersonalProfileUpdate> request){

        Long id = this.userContext.getCurrentUser().getId();

        this.profileService.updatePersonalProfile(id, request.getBody());

        return ResponseEntity.status(204).build();

    }

}
