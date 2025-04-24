package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.dto.PersonalProfileUpdate;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalProfileServiceTest {

    @InjectMocks
    private PersonalProfileService service;

    @Mock
    private UserAccountRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private CustomValidator<UserAccount> validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPersonalProfileById_WhenFound() {
        Long id = 1L;
        UserAccount user = new UserAccount();
        user.setId(id);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        UserAccount result = service.getPersonalProfileById(id);

        assertEquals(id, result.getId());
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void testGetPersonalProfileById_WhenNotFound() {
        Long id = 1L;
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getPersonalProfileById(id));
    }

    @Test
    void testUpdatePersonalProfile_PasswordChangeValid() {
        Long id = 1L;
        String currentPw = "current";
        String newPw = "newPassword";

        UserAccount existingUser = new UserAccount();
        existingUser.setId(id);
        existingUser.setPassword(currentPw);

        UserAccount updatedUser = new UserAccount();
        updatedUser.setId(id);
        updatedUser.setPassword(newPw);

        PersonalProfileUpdate update = new PersonalProfileUpdate();
        update.setUser(updatedUser);
        update.setOldPassword("old");

        when(userRepo.findById(id)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(newPw, currentPw)).thenReturn(false);
        when(passwordEncoder.matches("old", currentPw)).thenReturn(true);
        when(passwordEncoder.encode(newPw)).thenReturn("hashed");

        service.updatePersonalProfile(id, update);

        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void testUpdatePersonalProfile_WithIncorrectOldPassword_ShouldThrow() {
        Long id = 1L;

        UserAccount existingUser = new UserAccount();
        existingUser.setId(id);
        existingUser.setPassword("current");

        UserAccount updatedUser = new UserAccount();
        updatedUser.setId(id);
        updatedUser.setPassword("newPassword");

        PersonalProfileUpdate update = new PersonalProfileUpdate();
        update.setUser(updatedUser);
        update.setOldPassword("wrongOld");

        when(userRepo.findById(id)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("newPassword", "current")).thenReturn(false);
        when(passwordEncoder.matches("wrongOld", "current")).thenReturn(false);

        assertThrows(SecurityValidationException.class, () -> service.updatePersonalProfile(id, update));
    }

    @Test
    void testUpdatePersonalProfile_WithNullUpdate_ShouldThrow() {
        Long id = 1L;
        when(userRepo.findById(id)).thenReturn(Optional.of(new UserAccount()));

        assertThrows(RuntimeException.class, () -> service.updatePersonalProfile(id, null));
    }

    @Test
    void testUpdatePersonalProfile_WithNullUserInUpdate_ShouldThrow() {
        Long id = 1L;
        when(userRepo.findById(id)).thenReturn(Optional.of(new UserAccount()));

        PersonalProfileUpdate update = new PersonalProfileUpdate();
        update.setUser(null);

        assertThrows(RuntimeException.class, () -> service.updatePersonalProfile(id, update));
    }
}

