package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userMockRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleMockRepo;

    @Mock
    private CustomValidator<UserAccount> validator;

    private UserAccountService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.userService = new UserAccountService(
                this.userMockRepo,
                this.passwordEncoder,
                this.roleMockRepo,
                this.validator);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateUserAccount() {
        // Arrange
        String firstName = "Peter";
        String lastName = "Parker";
        String email = "peter@email.com";
        String rawPassword = "spider-man";
        String encodedPassword = "encoded-password";
        String contactNumber = "9999999";

        Role role = new Role();
        role.setName(RoleType.MEMBER); // or whatever valid RoleType you have
        List<Role> roles = List.of(role);

        UserAccount user = UserAccount.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .contactNumber(contactNumber)
                .password(rawPassword)
                .roles(roles)
                .build();

        UserAccount userToSave = UserAccount.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .contactNumber(contactNumber)
                .password(encodedPassword)
                .roles(roles)
                .build();

        UserAccount savedUser = UserAccount.builder()
                .id(1L)
                .build();

        // Act & Assert
        when(this.userMockRepo.existsByEmail(email)).thenReturn(false);
        when(this.roleMockRepo.findAll()).thenReturn(roles); // Required for getStoredRoleValues
        when(this.passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(this.userMockRepo.save(any(UserAccount.class))).thenReturn(savedUser);

        Long result = this.userService.createUserAccount(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(this.userMockRepo, times(1)).save(any(UserAccount.class));
    }


    @Test
    void testCreateUserAccount_WithInvalidUser() {

        UserAccount user = null;

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.userService.createUserAccount(user));

        assertEquals("User Account is null or empty.", e.getMessage());
        verify(this.userMockRepo, times(0)).save(user);
    }

    @Test
    void testCreateUserAccount_WithUserAlreadyRegistered() {

        String firstName = "Peter";
        String lastName = "Parker";
        String email = "peter@email.com";
        String password = "spider-man";
        String contactNumber = "9999999";
        Role role = new Role();
        List<Role> roles = new ArrayList<>(List.of(role));

        UserAccount user = UserAccount.builder()
                .email(email)
                .build();

        when(this.userMockRepo.existsByEmail(user.getEmail())).thenReturn(true);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.userService.createUserAccount(user));

        assertEquals("User Account " + email + " already exists.", e.getMessage());
        verify(this.userMockRepo, times(0)).save(user);
    }

    @Test
    void testCreateAdminUserAccount() {
        UserAccount user = UserAccount.builder()
                .email("admin@email.com")
                .password("securePass")
                .build();

        Role admin = new Role(1L, RoleType.ACCOUNT_ADMIN, List.of());
        Role member = new Role(2L, RoleType.MEMBER, List.of());

        when(roleMockRepo.findByName(RoleType.ACCOUNT_ADMIN)).thenReturn(Optional.of(admin));
        when(roleMockRepo.findByName(RoleType.MEMBER)).thenReturn(Optional.of(member));
        when(userMockRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleMockRepo.findAll()).thenReturn(List.of(admin, member));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userMockRepo.save(any(UserAccount.class))).thenReturn(UserAccount.builder().id(1L).build());

        Long id = userService.createAdminUserAccount(user);

        assertEquals(1L, id);
        verify(userMockRepo).save(any(UserAccount.class));
    }

    @Test
    void testGetUserAccountById() {

        Long id = 1L;

        UserAccount user = UserAccount.builder()
                .id(id)
                .build();

        when(this.userMockRepo.findById(id)).thenReturn(Optional.of(user));

        UserAccount result = this.userService.getUserAccountById(id);

        assertEquals(id, user.getId());

        verify(this.userMockRepo, times(1)).findById(id);
    }

    @Test
    void testGetUserAccountById_WithInvalidId() {

        Long id = 1L;

        UserAccount user = UserAccount.builder()
                .id(id)
                .build();

        when(this.userMockRepo.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.userService.getUserAccountById(id));

        assertEquals("User Account with id = " + id + " not found.", e.getMessage());

        verify(this.userMockRepo, times(1)).findById(id);
    }

    @Test
    void testGetUserAccountByIds() {
        Long userId = 1L;
        Long custId = 10L;
        UserAccount user = UserAccount.builder().id(userId).build();

        when(userMockRepo.findByIdAndParentAccountId(userId, custId)).thenReturn(Optional.of(user));

        UserAccount result = userService.getUserAccountByIds(userId, custId);

        assertEquals(userId, result.getId());
        verify(userMockRepo).findByIdAndParentAccountId(userId, custId);
    }

    @Test
    void testGetUserAccountsByCustomerId() {
        Long custId = 100L;
        UserAccount user1 = UserAccount.builder().id(1L).build();
        UserAccount user2 = UserAccount.builder().id(2L).build();

        when(userMockRepo.findUserAccountByParentAccountId(custId))
                .thenReturn(Optional.of(List.of(user1, user2)));

        List<UserAccount> result = userService.getUserAccountsByCustomerId(custId);

        assertEquals(2, result.size());
        verify(userMockRepo).findUserAccountByParentAccountId(custId);
    }

    @Test
    void testGetUserAccountByEmail() {

        String email = "peter@email.com";

        UserAccount user = UserAccount.builder()
                .email(email)
                .build();

        when(this.userMockRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UserAccount result = this.userService.getUserAccountByEmail(email);

        assertEquals(email, user.getEmail());

        verify(this.userMockRepo, times(1)).findByEmail(email);
    }


    @Test
    void testGetUserAccountByEmail_WithInvalidEmail() {

        String email = "peter@email.com";

        UserAccount user = UserAccount.builder()
                .email(email)
                .build();

        when(this.userMockRepo.findByEmail(email)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.userService.getUserAccountByEmail(email));

        assertEquals("User Account with email = " + email + " not found.", e.getMessage());

        verify(this.userMockRepo, times(1)).findByEmail(email);

    }

    @Test
    void testGetUserAccounts() {

        UserAccount user1 = UserAccount.builder()
                .id(1L)
                .build();

        UserAccount user2 = UserAccount.builder()
                .id(2L)
                .build();

        UserAccount user3 = UserAccount.builder()
                .id(3L)
                .build();

        when(this.userMockRepo.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserAccount> result = this.userService.getUserAccounts();

        assertEquals(3, result.size());
        assertEquals(user2.getId(), result.get(1).getId());

        verify(this.userMockRepo, times(1)).findAll();


    }

    @Test
    void testUpdateUserAccount() {

        Long id = 1L;
        String firstName = "Peter";
        String lastName = "Parker";
        String email1 = "peter@email.com";
        String password1 = "spider-man";
        String contactNumber1 = "9999999";
        Role role1 = new Role();
        List<Role> roles = new ArrayList<>(List.of(role1));

        UserAccount user = UserAccount.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email1)
                .password(password1)
                .contactNumber(contactNumber1)
                .roles(List.of(role1))
                .build();


        String email2 = "peter-parker@email.com";
        String password2 = "spider-person";
        String contactNumber2 = "";
        // No job title
        String jobTitle2 = "";
        Role role2 = new Role(20L, RoleType.MEMBER, null);
        List<Role> roles2 = new ArrayList<>(List.of(role2));

        List<Role> refRoles = new ArrayList<>(List.of(
                new Role(10L, RoleType.USER, null),
                new Role(20L, RoleType.MEMBER, null),
                new Role(30L, RoleType.ACCOUNT_ADMIN, null)
            )
        );

        when(this.userMockRepo.findById(id)).thenReturn(Optional.of(user));

        when(this.roleMockRepo.findAll()).thenReturn(refRoles);

        this.userService.updateUserAccount(
                id,
                null,
                null,
                jobTitle2,
                email2,
                contactNumber2,
                password2,
                roles2,
                null
        );


        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        // Job title empty when empty job title passed
        assertEquals(jobTitle2, user.getJobTitle());
        // Email should not be updated
        assertEquals(email1, user.getEmail());
        // Contact number unchanged when empty contact number passed
        assertEquals(contactNumber1, user.getContactNumber());
        assertEquals(this.passwordEncoder.encode(password2), user.getPassword());
        assertEquals(RoleType.MEMBER, user.getRoles().get(0).getName());


    }

    @Test
    void testUpdateUserAccount_WithSamePassword() {

        Long id = 1L;
        String firstName = "Peter";
        String lastName = "Parker";
        String email1 = "peter@email.com";
        String rawPassword = "abcd1234";
        String encPassword = this.passwordEncoder.encode(rawPassword);
        String contactNumber1 = "9999999";
        Role role1 = new Role();
        List<Role> roles = new ArrayList<>(List.of(role1));

        UserAccount user = UserAccount.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email1)
                .password(encPassword)
                .contactNumber(contactNumber1)
                .roles(List.of(role1))
                .build();


        when(this.userMockRepo.findById(id)).thenReturn(Optional.of(user));

        when(this.passwordEncoder.matches(rawPassword, encPassword)).thenReturn(true);

        Exception e = assertThrows(
                BusinessValidationException.class, () -> this.userService.updateUserAccount(
                id,
                null,
                null,
                null,
                null,
                null,
                rawPassword,
                null,
                null
        ));

        assertEquals("New password cannot be the same as old.", e.getMessage());


    }

    @Test
    void testUpdateUserAccount_AsAccountAdmin() {
        Long id = 1L;
        Long custId = 20L;
        CustomerAccount parent = CustomerAccount.builder().id(custId).build();
        UserAccount user = UserAccount.builder().id(id).parentAccount(parent).build();
        UserAccount update = UserAccount.builder().id(id).firstName("Updated").build();

        when(userMockRepo.findById(id)).thenReturn(Optional.of(user));

        userService.updateUserAccount(id, custId, update);

        assertEquals("Updated", user.getFirstName());
    }


    @Test
    void testUpdateUserAccount_AsSuperAdmin() {
        Long id = 1L;
        UserAccount existing = UserAccount.builder()
                .id(id).firstName("Old").lastName("User")
                .contactNumber("111")
                .password("oldPass")
                .roles(new ArrayList<>())
                .build();

        UserAccount update = UserAccount.builder()
                .id(id).firstName("New").lastName("User")
                .contactNumber("999")
                .password("newPass")
                .roles(new ArrayList<>())
                .build();

        when(userMockRepo.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("newPass", "oldPass")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.updateUserAccount(id, update);

        assertEquals("New", existing.getFirstName());
        assertEquals("encodedNewPass", existing.getPassword());
    }

    @Test
    void testCountByRoleAndCustomerAccount() {
        RoleType roleType = RoleType.MEMBER;
        Long custId = 101L;

        when(userMockRepo.countByRoleNameAndParentAccountId(roleType, custId)).thenReturn(5L);

        Long count = userService.countByRoleAndCustomerAccount(roleType, custId);

        assertEquals(5L, count);
        verify(userMockRepo).countByRoleNameAndParentAccountId(roleType, custId);
    }

    @Test
    public void testDeleteUserAccount() {
        Long id = 1L;

        when(this.userMockRepo.existsById(id)).thenReturn(true);

        this.userService.deleteUserAccount(id);

        verify(this.userMockRepo, times(1)).existsById(id);
        verify(this.userMockRepo, times(1)).deleteUserAccountById(id);
    }

    @Test
    void testDeleteUserAccount_NotExists() {
        Long id = 1L;

        when(this.userMockRepo.existsById(id)).thenReturn(false);

        Exception e = assertThrows(BusinessValidationException.class,
                () ->  this.userService.deleteUserAccount(id));

        assertEquals("User Account with id = " + id + " not found.", e.getMessage());

        verify(this.userMockRepo, times(1)).existsById(id);
        verify(this.userMockRepo, times(0)).deleteById(id);
    }

    @Test
    void testDeleteUserAccount_WithMatchingCustomerId() {
        Long userId = 1L;
        Long custId = 100L;

        // Simulate that user exists with this ID and belongs to the given customer
        when(userMockRepo.existsByIdAndParentAccountId(userId, custId)).thenReturn(true);

        // Act
        userService.deleteUserAccount(userId, custId);

        // Assert
        verify(userMockRepo, times(1)).existsByIdAndParentAccountId(userId, custId);
        verify(userMockRepo, times(1)).deleteUserAccountById(userId);
    }

    @Test
    void testDeleteUserAccount_WithMismatchedCustomerId() {
        Long userId = 1L;
        Long custId = 999L; // Mismatched ID

        // Simulate that no such user exists for the given customer
        when(userMockRepo.existsByIdAndParentAccountId(userId, custId)).thenReturn(false);

        // Act + Assert
        BusinessValidationException exception = assertThrows(
                BusinessValidationException.class,
                () -> userService.deleteUserAccount(userId, custId)
        );

        assertEquals("User Account with id = " + userId + " not found.", exception.getMessage());

        verify(userMockRepo, times(1)).existsByIdAndParentAccountId(userId, custId);
        verify(userMockRepo, never()).deleteUserAccountById(any());
    }


    @Disabled
    void testLoadUserByUsername() {
        //This test is fulfilled by testing getUserAccountByEmail and getUserAccountById
        assertEquals("--- TEST NOT IMPLEMENTED ---", "");
    }
}