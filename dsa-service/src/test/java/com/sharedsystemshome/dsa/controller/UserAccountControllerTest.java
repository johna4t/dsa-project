package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.UserAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserAccountControllerTest {

    @InjectMocks
    private UserAccountController userController;

    @Mock
    private UserAccountService userMockService;

    @Mock
    private UserContextService userContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.userController).build();

        when(userContext.isSuperAdmin()).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPostUserAccount() throws Exception {
        Long id = 5L;
        String firstName = "Peter";
        String lastName = "Parker";
        String email = "peter@email.com";
        String password = "spider-man";
        String contactNumber = "9999999";

        Role role = new Role();
        role.setId(2L);
        role.setName(RoleType.MEMBER);  // Needed for proper JSON mapping
        List<Role> roles = List.of(role);

        UserAccount user = UserAccount.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .contactNumber(contactNumber)
                .password(password)
                .status(UserAccountStatus.ACTIVE)
                .roles(roles)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String payload = mapper.writeValueAsString(user);

        when(this.userMockService.createUserAccount(any(UserAccount.class))).thenReturn(id);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/user-accounts")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(id.toString()))
                .andReturn();

        // Ensure service was called with any properly deserialized UserAccount
        verify(this.userMockService, times(1))
                .createUserAccount(any(UserAccount.class));
    }


    @Test
    void testPostUserAccount_WithInvalidRole() throws Exception {

        String invalidPayload = "{"
                + "\"firstName\" : \"Peter\","
                + "\"lastName\" : \"Parker\","
                + "\"email\" : \"peter@email.com\","
                + "\"contactNumber\" : \"9999999\","
                + "\"password\" : \"spider-man\","
                + "\"status\" : \"ACTIVE\","
                + "\"roles\" : [ {"
                + "    \"id\" : 2,"
                + "    \"name\" : \"INVALID_ROLE\""  // 👈 Not a valid RoleType enum value
                + "} ]"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/user-accounts")
                        .content(invalidPayload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // ✅ Expect Jackson to fail to deserialize
    }


    @Test
    void testGetUserAccounts() throws Exception {

        Long user1_Id = 1L;
        String firstName1 = "Peter";
        String lastName1 = "Parker";
        String email1 = "peter@email.com";
        String password1 = "spider-man";
        String contactNumber1 = "9999999";
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleType.MEMBER);
        List<Role> roles = new ArrayList<>(List.of(role));

        UserAccount user1 = UserAccount.builder()
                .id(user1_Id)
                .firstName(firstName1)
                .lastName(lastName1)
                .email(email1)
                .contactNumber(contactNumber1)
                .password(password1)
                .roles(roles)
                .build();

        Long user2_Id = 2L;
        String firstName2 = "Clint";
        String lastName2 = "Barton";
        String email2 = "clint@email.com";
        String password2 = "hawkeye";
        String contactNumber2 = "8888888";

        UserAccount user2 = UserAccount.builder()
                .id(user2_Id)
                .firstName(firstName2)
                .lastName(lastName2)
                .email(email2)
                .contactNumber(contactNumber2)
                .password(password2)
                .roles(roles)
                .build();

        List<UserAccount> users = List.of(user1, user2);

        when(this.userMockService.getUserAccounts()).thenReturn(users);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(user1_Id))
                .andExpect(jsonPath("$[0].firstName").value(firstName1))
                .andExpect(jsonPath("$[1].email").value(email2))
                // Removed password check for security reasons
                .andReturn();

        verify(this.userMockService, times(1)).getUserAccounts();
    }


    @Test
    void testGetUserAccounts_WhenNotSuperAdmin() throws Exception {
        when(userContext.isSuperAdmin()).thenReturn(false);

        // Set up a mock CustomerAccount
        CustomerAccount customerAccount = CustomerAccount.builder()
                .id(123L)
                .name("Test Customer")
                .build();

        // Set up a mock UserAccount with the CustomerAccount
        UserAccount currentUser = UserAccount.builder()
                .id(99L)
                .parentAccount(customerAccount)
                .build();

        // Simulate one user returned for that customer
        UserAccount user1 = UserAccount.builder()
                .id(1L)
                .firstName("Peter")
                .lastName("Parker")
                .email("peter@email.com")
                .contactNumber("1234567")
                .build();

        when(userContext.getCurrentUser()).thenReturn(currentUser);
        when(userMockService.getUserAccountsByCustomerId(123L)).thenReturn(List.of(user1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts")
                        .content("{}") // simulate empty body (triggers default path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("peter@email.com"));

        verify(userMockService, times(1)).getUserAccountsByCustomerId(123L);
    }

    @Test
    void testGetUserAccountById() throws Exception {

        Long user1_Id = 1L;
        String firstName1 = "Peter";
        String lastName1 = "Parker";
        String email1 = "peter@email.com";
        String password1 = "spider-man";
        String contactNumber1 = "9999999";
        Role role = new Role();
        role.setId(2l);
        List<Role> roles = new ArrayList<>(List.of(role));

        UserAccount user1 = UserAccount.builder()
                .id(user1_Id)
                .firstName(firstName1)
                .lastName(lastName1)
                .email(email1)
                .contactNumber(contactNumber1)
                .password(password1)
                .roles(roles)
                .build();

        Long user2_Id = 2L;
        String firstName2 = "Clint";
        String lastName2 = "Barton";
        String email2 = "clint@email.com";
        String password2 = "hawkeye";
        String contactNumber2 = "8888888";

        UserAccount user2 = UserAccount.builder()
                .id(user2_Id)
                .firstName(firstName2)
                .lastName(lastName2)
                .email(email2)
                .contactNumber(contactNumber2)
                .password(password2)
                .roles(roles)
                .build();

        List<UserAccount> users = new ArrayList<>(List.of(user1, user2));

        when(this.userMockService.getUserAccountById(user1_Id)).thenReturn(user1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts/" + user1_Id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(13))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(user1_Id))
                .andExpect(jsonPath("$.firstName").value(firstName1))
                .andReturn();

        verify(this.userMockService, times(1))
                .getUserAccountById(user1_Id);
    }

    @Test
    void testGetUserAccountById_AsSuperAdmin() throws Exception {
        Long userId = 1L;
        UserAccount user = UserAccount.builder()
                .id(userId)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .build();

        when(userContext.isSuperAdmin()).thenReturn(true);
        when(userMockService.getUserAccountById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));

        verify(userMockService, times(1)).getUserAccountById(userId);
    }


    @Test
    void testPutUserAccount() throws Exception {

        Long userId = 1L;
        String firstName2 = "Clint";
        String lastName2 = "Barton";
        String email2 = "clint@email.com";
        String password2 = "hawkeye";
        String contactNumber2 = "8888888";
        Role role = new Role();
        role.setId(2L);
        List<Role> roles = new ArrayList<>(List.of(role));

        UserAccount updatedUser = UserAccount.builder()
                .id(userId)
                .firstName(firstName2)
                .lastName(lastName2)
                .email(email2)
                .contactNumber(contactNumber2)
                .password(password2)
                .roles(roles)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String payload = mapper
                .writerWithDefaultPrettyPrinter().writeValueAsString(updatedUser);

        // ✅ Simulate the user context (super admin)
        when(userContext.isSuperAdmin()).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user-accounts/{id}", userId) // ✅ Add userId to path
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent()) // ✅ 204
                .andReturn();

        verify(userMockService, times(1)).updateUserAccount(eq(userId), argThat(actual -> {
            return actual.getFirstName().equals("Clint") &&
                    actual.getLastName().equals("Barton") &&
                    actual.getEmail().equals("clint@email.com") &&
                    actual.getContactNumber().equals("8888888") &&
                    actual.getRoles() != null &&
                    actual.getRoles().size() == 1 &&
                    actual.getRoles().get(0).getId().equals(2L);
        }));
    }

    @Test
    void testPutUserAccount_AsAccountAdmin() throws Exception {
        Long userId = 1L;
        Role role = new Role();
        role.setId(2L);
        List<Role> roles = List.of(role);

        UserAccount updatedUser = UserAccount.builder()
                .id(userId)
                .firstName("Updated")
                .lastName("User")
                .email("update@test.com")
                .contactNumber("7777777")
                .password("newpass")
                .roles(roles)
                .build();

        when(userContext.isSuperAdmin()).thenReturn(false);
        when(userContext.isAccountAdmin()).thenReturn(true);
        when(userContext.getCurrentCustomerAccountId()).thenReturn(123L);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user-accounts/{id}", userId)
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userMockService, times(1)).updateUserAccount(eq(userId), eq(123L), argThat(actual ->
                actual.getFirstName().equals("Updated") &&
                        actual.getLastName().equals("User") &&
                        actual.getEmail().equals("update@test.com") &&
                        actual.getContactNumber().equals("7777777") &&
                        actual.getRoles().size() == 1 &&
                        actual.getRoles().get(0).getId().equals(2L)
        ));
    }

    @Test
    void testGetUserAccountsCountByRoleAndCustomerAccount_Valid() throws Exception {
        when(userMockService.countByRoleAndCustomerAccount(RoleType.MEMBER, 123L)).thenReturn(2L);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts/count")
                        .param("roleName", "MEMBER")
                        .param("parentAccountId", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));

        verify(userMockService, times(1)).countByRoleAndCustomerAccount(RoleType.MEMBER, 123L);
    }

    @Test
    void testGetUserAccountsCountByRoleAndCustomerAccount_InvalidRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user-accounts/count")
                        .param("roleName", "INVALID_ROLE")
                        .param("parentAccountId", "123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserAccount() throws Exception {
        Long userId = 1L;

        // 👇 Mock the current user returned by userContext
        UserAccount mockCurrentUser = UserAccount.builder()
                .id(2L) // Use different ID so it doesn't trigger the "self-delete" check
                .build();
        when(userContext.getCurrentUser()).thenReturn(mockCurrentUser);
        when(userContext.isSuperAdmin()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user-accounts/" + userId))
                .andExpect(status().isNoContent());

        verify(this.userMockService, times(1))
                .deleteUserAccount(userId);
    }

    @Test
    void testDeleteUserAccount_SelfDeletionIgnored() throws Exception {
        Long userId = 1L;

        // Simulate logged-in user
        UserAccount currentUser = UserAccount.builder()
                .id(userId)
                .email("self@user.com")
                .build();

        when(userContext.getCurrentUser()).thenReturn(currentUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user-accounts/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Operation ignored. You cannot delete your own user account."));

        // Ensure that no delete operation is called
        verify(userMockService, never()).deleteUserAccount(anyLong());
        verify(userMockService, never()).deleteUserAccount(anyLong(), anyLong());
    }
}