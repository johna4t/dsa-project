package com.sharedsystemshome.dsa.security.service;

import com.sharedsystemshome.dsa.enums.UserAccountStatus;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.Owned;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserContextServiceTest {

    private UserContextService userContextService;

    private UserAccount user;
    private CustomerAccount customerAccount;

    @BeforeEach
    void setUp() {
        userContextService = new UserContextService();

        customerAccount = CustomerAccount.builder()
                .id(101L)
                .name("Test Org")
                .url("https://test.org")
                .departmentName("Test Dept")
                .build();

        Permission dummyPermission = Permission.builder()
                .name(PermissionType.ACCOUNT_ADMIN_CREATE)
                .build();

        Role accountAdminRole = Role.builder()
                .name(RoleType.ACCOUNT_ADMIN)
                .permissions(List.of(dummyPermission))
                .build();

        user = UserAccount.builder()
                .email("testuser@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .contactNumber("123456789")
                .status(UserAccountStatus.ACTIVE)
                .roles(List.of(accountAdminRole))
                .parentAccount(customerAccount)
                .build();

        userContextService.setAuthenticatedUser(user, customerAccount.getId());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSetAuthenticatedUserAndGetContext() {
        Authentication auth = userContextService.getUserContext();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(user, auth.getPrincipal());
        assertEquals(customerAccount.getId(), auth.getDetails());
    }

    @Test
    void testSetAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(user, auth.getPrincipal());
        assertEquals(customerAccount.getId(), auth.getDetails());
    }

    @Test
    void testGetUserContext() {
        Authentication context = userContextService.getUserContext();
        assertNotNull(context);
        assertTrue(context.isAuthenticated());
        assertEquals("testuser@example.com", context.getName());
    }

    @Test
    void testGetCurrentUser() {
        UserAccount currentUser = userContextService.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("testuser@example.com", currentUser.getEmail());
    }

    @Test
    void testGetCurrentUserName() {
        String username = userContextService.getCurrentUserName();
        assertEquals("testuser@example.com", username);
    }

    @Test
    void testGetCurrentCustomerAccountId() {
        Long actual = userContextService.getCurrentCustomerAccountId();
        assertEquals(101L, actual);
    }

    @Test
    void testIsAuthorised() {
        // The authority should match the role's SimpleGrantedAuthority naming logic
        assertTrue(userContextService.isAuthorised("ROLE_ACCOUNT_ADMIN"));
        assertFalse(userContextService.isAuthorised("ROLE_MEMBER"));
    }

    @Test
    void testIsSuperAdmin() {
        assertFalse(userContextService.isSuperAdmin());
    }

    @Test
    void testIsAccountAdmin() {
        assertTrue(userContextService.isAccountAdmin());
    }

    @Test
    void testIsMember() {
        assertFalse(userContextService.isMember());
    }

    @Test
    void testIsAssociate() {
        assertFalse(userContextService.isAssociate());
    }

    @Test
    void testIsUser() {
        assertFalse(userContextService.isUser());
    }

    @Test
    void testValidateAccess_WithMatchingAccount() {
        Owned owned = new Owned() {
            @Override
            public Long ownerId() {
                return 101L;
            }

            @Override
            public Long objectId() {
                return 500L;
            }

            @Override
            public String entityName() {
                return "TestEntity";
            }
        };

        assertDoesNotThrow(() -> userContextService.validateAccess(owned));
    }

    @Test
    void testValidateAccess_WithMismatchedAccount() {
        Owned owned = new Owned() {
            @Override
            public Long ownerId() {
                return 999L;
            }

            @Override
            public Long objectId() {
                return 500L;
            }

            @Override
            public String entityName() {
                return "TestEntity";
            }
        };

        SecurityValidationException exception = assertThrows(SecurityValidationException.class, () -> {
            userContextService.validateAccess(owned);
        });

        assertTrue(exception.getMessage().contains("does not exist for Customer with id"));
    }
}