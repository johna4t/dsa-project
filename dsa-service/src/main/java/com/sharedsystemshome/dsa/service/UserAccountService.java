package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
@Validated
public class UserAccountService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    protected final UserAccountRepository userRepo;
    protected final PasswordEncoder passwordEncoder;
    protected final RoleRepository roleRepo;

    protected final CustomValidator<UserAccount> validator;



    //CREATE
    @PostMapping
    public Long createUserAccount(UserAccount user) {
        logger.debug("Entering method UserAccount::createUserAccount with user: {}", user);

        if (null == user) {
            throw new NullOrEmptyValueException(BusinessValidationException.USER_ACCOUNT);
        }

        this.validator.validate(user);

        if (this.userRepo.existsByEmail(user.getEmail())) {
            throw new BusinessValidationException(BusinessValidationException.USER_ACCOUNT + " " + user.getEmail() + " already exists.");
        }

        List<Role> roles = this.getStoredRoleValues(user.getRoles());

        if(null != roles && !roles.isEmpty()){
            user.setRoles(roles);
        } else {
            throw new BusinessValidationException(BusinessValidationException.USER_ACCOUNT + " " + user.getEmail() + " invalid role.");
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        try {
            Long userId = this.userRepo.save(user).getId();
            logger.info("New UserAccount created with id: {}", userId);
            return userId;
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.USER_ACCOUNT, e);
        }
    }

    public Long createAdminUserAccount(UserAccount user) {
        logger.debug("Entering method UserAccount::createAdminUserAccount with user: {}", user);

        if (null == user) {
            throw new BusinessValidationException("Invalid admin User cannot be registered.");
        }

        this.validator.validate(user);

        Role admin = this.roleRepo.findByName(RoleType.ACCOUNT_ADMIN).orElseThrow(
                () -> {
                    return new BusinessValidationException(BusinessValidationException.ROLE + " with name = " + RoleType.ACCOUNT_ADMIN.name() + " not found.");
                }
        );
        Role member = this.roleRepo.findByName(RoleType.MEMBER).orElseThrow(
                () -> {
                    return new BusinessValidationException(BusinessValidationException.ROLE + " with name = " + RoleType.MEMBER.name() + " not found.");
                }
        );

        user.setRoles(List.of(admin, member));
        Long userId = this.createUserAccount(user);
        logger.info("New admin UserAccount created with id: {}", userId);

        return userId;

    }

    //READ
    @GetMapping
    public UserAccount getUserAccountById(Long id) {
        logger.debug("Entering method UserAccount::getUserAccountById with id: {}", id);

        UserAccount userAccount = this.userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        logger.info("Found UserAccount with id: {}", id);
        return userAccount;
    }

    @GetMapping
    public UserAccount getUserAccountByIds(Long id, Long custId) {
        logger.debug("Entering method UserAccount::getUserAccountById with id: {}", id);

        UserAccount userAccount = this.userRepo.findByIdAndParentAccountId(id, custId).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        logger.info("Found UserAccount with id: {}", id);
        return userAccount;
    }


    @GetMapping
    public UserAccount getUserAccountByEmail(String email) {
        logger.debug("Entering method UserAccount::getUserAccountByEmail with email: {}", email);

        UserAccount userAccount = this.userRepo.findByEmail(email).orElseThrow(
                () -> {
                    return new BusinessValidationException(BusinessValidationException.USER_ACCOUNT + " with email = " + email + " not found.");
                }
        );

        logger.info("Found UserAccount with email: {}", email);
        return userAccount;
    }

    //READ ALL
    @GetMapping
    public List<UserAccount> getUserAccounts() {
        logger.debug("Entering method UserAccount::getUserAccounts");

        List<UserAccount> userAccounts = this.userRepo.findAll();

        logger.info("Found {} UserAccounts", userAccounts.size());
        return userAccounts;
    }


    public List<UserAccount>  getUserAccountsByCustomerId(Long custId){
        logger.debug("Entering method UserAccount::getUserAccountsByCustomerId");

        if(null == custId){
            throw new NullOrEmptyValueException(BusinessValidationException.CUSTOMER_ACCOUNT + " id");
        }

        List<UserAccount> users = this.userRepo.findUserAccountByParentAccountId(custId).orElseThrow(
                () -> new EntityNotFoundException(BusinessValidationException.CUSTOMER_ACCOUNT, custId)
        );

        logger.info("Found {} UserAccounts for CustomerAccount with id: {}",
                users.size(), custId);
        return users;
    }


    //UPDATE - used by Super Admins
    @Transactional
    public void updateUserAccount(Long id, UserAccount update) {
        logger.debug("Entering method UserAccount::updateUserAccount with ID: {}", id);

        // Verify user account with id exists
        UserAccount userAccount = this.userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        // Verify user account update exists
        UserAccount user = null;
        if(null == update){
            throw new NullOrEmptyValueException(BusinessValidationException.USER_ACCOUNT + " id");
        }

        this.updateUserAccount(
                update.getId(),
                update.getFirstName(),
                update.getLastName(),
                update.getJobTitle(),
                null, // Cannot change email address with this method
                update.getContactNumber(),
                update.getPassword(),
                update.getRoles(),
                update.getStatus());
    }

    //UPDATE - used by Account Admins
    @Transactional
    public void updateUserAccount(Long id, Long custId, UserAccount update) {
        logger.debug("Entering method UserAccount::updateUserAccount with user: {}", update);

        // Verify user account with id exists
        UserAccount user = this.userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                }
        );

        // Verify user account update exists
        if(null == update){
            throw new NullOrEmptyValueException(BusinessValidationException.USER_ACCOUNT + " id");
        }

        // Verify user context has the same customer id as the user account
        if(!custId.equals(user.getParentAccount().getId())){
            throw new SecurityValidationException(BusinessValidationException.USER_ACCOUNT + " with id = " + id + " not found.");
        }

        this.updateUserAccount(
                update.getId(),
                update.getFirstName(),
                update.getLastName(),
                update.getJobTitle(),
                null, // Cannot change email address with this method
                update.getContactNumber(),
                update.getPassword(),
                update.getRoles(),
                update.getStatus());
    }

    @Transactional
    public void updateUserAccount(Long id,
                                  String firstName,
                                  String lastName,
                                  String jobTitle,
                                  String email,
                                  String contactNumber,
                                  String password,
                                  List<Role> roles,
                                  UserAccountStatus status) {
        logger.debug("Entering method UserAccount::updateUserAccount with ID: {}", id);

        UserAccount user = userRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
                });

        if (null != firstName && !firstName.isEmpty()) {
            String oldFirstName = user.getFirstName();
            if(!Objects.equals(oldFirstName, firstName)) {
                user.setFirstName(firstName);
                logger.info("Updated value of property UserAccount::firstName from {} to {}, " +
                        "for UserAccount with id: {}", oldFirstName, firstName, id);
            }
        }
        if (null != lastName && !lastName.isEmpty()) {
            String oldLastName = user.getLastName();
            if(!Objects.equals(oldLastName, lastName)) {
                user.setLastName(lastName);
                logger.info("Updated value of property UserAccount::lastName from {} to {}, " +
                        "for UserAccount with id: {}", oldLastName, lastName, id);
            }
        }
        if (null != jobTitle) { // jobTitle not mandatory
            String oldJobTitle = user.getJobTitle();
            if(!Objects.equals(oldJobTitle, jobTitle)) {
                user.setJobTitle(jobTitle);
                logger.info("Updated value of property UserAccount::jobTitle from {} to {}, " +
                        "for UserAccount with id: {}", oldJobTitle, jobTitle, id);
            }
        }
        if (null != contactNumber && !contactNumber.isEmpty()) {
            String oldContactNumber = user.getContactNumber();
            if(!Objects.equals(oldContactNumber, contactNumber)) {
                user.setContactNumber(contactNumber);
                logger.info("Updated value of property UserAccount::contactNumber from {} to {}, " +
                        "for UserAccount with id: {}", oldContactNumber, contactNumber, id);
            }
        }
        if (null != password && !password.isEmpty()) {
            if (!this.passwordEncoder.matches(password, user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(password));
                logger.info("Updated value of property UserAccount::password " +
                        "for UserAccount with id: {}", id);
            } else {
                throw new BusinessValidationException("New password cannot be the same as old.");
            }
        }
        if (null != roles && !roles.isEmpty()) {
            List<Role> oldRoles = user.getRoles();

            if(!this.rolesEqual(oldRoles, roles)) {

                List<Role> updatedRoles = this.getStoredRoleValues(roles);

                // Only update if set of valid roles returned
                if(null != updatedRoles && !updatedRoles.isEmpty()){
                    user.setRoles(updatedRoles);
                    logger.info("Updated value of property UserAccount::roles from {} to {}, " +
                            "for UserAccount with id: {}", oldRoles, roles, id);
                }

            }
        }
        if (null != status) {
            UserAccountStatus oldStatus = user.getStatus();
            if(!Objects.equals(oldStatus, status)) {
                user.setStatus(status);
                logger.info("Updated value of property UserAccount::status from {} to {}, " +
                        "for UserAccount with id: {}", oldStatus, status, id);
            }
        }
    }

    public static boolean rolesEqual(List<Role> newRoles, List<Role> oldRoles) {

        Set<RoleType> newRoleNames = newRoles.stream().map(Role::getName).collect(Collectors.toSet());
        Set<RoleType> oldRoleNames = oldRoles.stream().map(Role::getName).collect(Collectors.toSet());

        return newRoleNames.equals(oldRoleNames);
    }

    protected List<Role> getStoredRoleValues(List<Role> newRoles) {

        List<Role> allRoles = this.roleRepo.findAll();

        // Map name -> Role for quick lookup
        Map<RoleType, Role> refRoleMap = allRoles.stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        // Return a subset of refRoles matching role names in rolesToCorrect
        return newRoles.stream()
                .map(role -> refRoleMap.get(role.getName())) // Get the exact refRole with all info
                .filter(Objects::nonNull) // Exclude names not found in refRoles
                .collect(Collectors.toList());
    }

    //DELETE
    @Transactional
    public void deleteUserAccount(Long id) {
        logger.debug("Entering UserAccount::deleteUserAccount with id: {}", id);

        boolean exists = this.userRepo.existsById(id);

        if (!exists) {
            throw new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
        }

        userRepo.deleteUserAccountById(id);
        logger.info("Deleted UserAccount with id: {}", id);
    }

    //DELETE
    @Transactional
    public void deleteUserAccount(Long id, Long custId) {
        logger.debug("Entering UserAccount::deleteUserAccount with id: {}", id);

        boolean exists = this.userRepo.existsByIdAndParentAccountId(id, custId);

        if (!exists) {
            throw new EntityNotFoundException(BusinessValidationException.USER_ACCOUNT, id);
        }

        userRepo.deleteUserAccountById(id);
        logger.info("Deleted UserAccount with id: {}", id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Entering UserAccount::loadUserByUsername with username: {}", username);

        UserAccount userAccount = getUserAccountByEmail(username);

        UserDetails user = this.getUserAccountById(userAccount.getId());
        logger.info("Loaded UserAccount with username: {}", username);

        return user;
    }

    public Long countByRoleAndCustomerAccount(RoleType roleType, Long parentAccountId) {
        return this.userRepo.countByRoleNameAndParentAccountId(roleType, parentAccountId);
    }



}
