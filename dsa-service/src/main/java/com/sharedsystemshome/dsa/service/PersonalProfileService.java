package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.dto.PersonalProfileUpdate;
import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Primary
@Validated
public class PersonalProfileService {


    private static final Logger logger = LoggerFactory.getLogger(PersonalProfileService.class);

    protected final UserAccountRepository userRepo;
    protected final PasswordEncoder passwordEncoder;
    protected final RoleRepository roleRepo;

    protected final CustomValidator<UserAccount> validator;


    //READ
    @GetMapping
    public UserAccount getPersonalProfileById(Long id) {
        logger.debug("Entering method PersonalProfile::getPersonalProfileById with id: {}", id);

        UserAccount userAccount = this.userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        logger.info("Found PersonalProfile with id: {}", id);
        return userAccount;
    }


    //UPDATE
    @Transactional
    public void updatePersonalProfile(Long id, PersonalProfileUpdate update) {
        logger.debug("Entering method PersonalProfile::updatePersonalProfile with ID: {}", id);

        // Verify user account with id exists
        UserAccount userAccount = this.userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        // Verify user account update exists
        UserAccount user = null;
        if(null != update){
            user = update.getUser();

            if(null == user){
                throw new NullOrEmptyValueException(BusinessValidationException.USER_ACCOUNT + " id");
            }

        } else {
            throw new NullOrEmptyValueException(BusinessValidationException.USER_ACCOUNT + " id");
        }

        String currentPassword = userAccount.getPassword();
        String newPassword = user.getPassword();
        String oldPassword = update.getOldPassword();
        String password = "";


        // Verify if current password to be updated with new password.
        if (null != newPassword && "" != newPassword && !this.passwordEncoder.matches(newPassword, currentPassword)) {
            // Validate old password.
            if (this.passwordEncoder.matches(oldPassword, currentPassword)) {

                password = newPassword;

            } else {
                throw new SecurityValidationException("Old password is incorrect.");
            }
        }

        String newFirstName = user.getFirstName();
        String newLastName = user.getLastName();
        String newJobTitle = user.getJobTitle();
        String newContactNumber = user.getContactNumber();

        if (null != newFirstName && !newFirstName.isEmpty()) {
            String oldFirstName = userAccount.getFirstName();
            if(!Objects.equals(oldFirstName, newFirstName)) {
                userAccount.setFirstName(newFirstName);
                logger.info("Updated value of property PersonalProfile::firstName from {} to {}, " +
                        "for PersonalProfile with id: {}", oldFirstName, newFirstName, id);
            }
        }
        if (null != newLastName && !newLastName.isEmpty()) {
            String oldLastName = userAccount.getLastName();
            if(!Objects.equals(oldLastName, newLastName)) {
                userAccount.setLastName(newLastName);
                logger.info("Updated value of property PersonalProfile::lastName from {} to {}, " +
                        "for PersonalProfile with id: {}", oldLastName, newLastName, id);
            }
        }
        if (null != newJobTitle) { // jobTitle not mandatory
            String oldJobTitle = userAccount.getJobTitle();
            if(!Objects.equals(oldJobTitle, newJobTitle)) {
                userAccount.setJobTitle(newJobTitle);
                logger.info("Updated value of property PersonalProfile::jobTitle from {} to {}, " +
                        "for PersonalProfile with id: {}", oldJobTitle, newJobTitle, id);
            }
        }
        if (null !=  newContactNumber && ! newContactNumber.isEmpty()) {
            String oldContactNumber = userAccount.getContactNumber();
            if(!Objects.equals(oldContactNumber, newContactNumber)) {
                userAccount.setContactNumber(newContactNumber);
                logger.info("Updated value of property PersonalProfile::contactNumber from {} to {}, " +
                        "for PersonalProfile with id: {}", oldContactNumber, newContactNumber, id);
            }
        }
        // Calling method is responsible for confirming this is a new password
        if (null !=  password && ! password.isEmpty()) {
            userAccount.setPassword(this.passwordEncoder.encode(password));
            logger.info("Updated value of property PersonalProfile::password " +
                    "for PersonalProfile with id: {}", id);
        }

    }



    //UPDATE
    @Transactional
    public void updatePersonalProfile(Long id,
                                      String firstName,
                                      String lastName,
                                      String jobTitle,
                                      String email,
                                      String contactNumber,
                                      String password,
                                      String oldPassword) {
        logger.debug("Entering method PersonalProfile::updatePersonalProfile with ID: {}", id);

        UserAccount user = UserAccount.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .jobTitle(jobTitle)
                .contactNumber(contactNumber)
                .password(password)
                .build();

        PersonalProfileUpdate update = PersonalProfileUpdate.builder()
                .user(user)
                .oldPassword(oldPassword)
                .build();

        this.updatePersonalProfile(id, update);

    }



}
