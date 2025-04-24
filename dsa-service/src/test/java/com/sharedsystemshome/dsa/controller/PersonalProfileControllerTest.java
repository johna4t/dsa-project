package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.dto.PersonalProfileUpdate;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.PersonalProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PersonalProfileControllerTest {

    @InjectMocks
    private PersonalProfileController controller;

    @Mock
    private PersonalProfileService profileService;

    @Mock
    private UserContextService userContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetPersonalProfileById_ReturnsProfile() throws Exception {
        UserAccount currentUser = UserAccount.builder()
                .id(1L)
                .firstName("Tony")
                .lastName("Stark")
                .email("tony@starkindustries.com")
                .build();

        when(userContext.getCurrentUser()).thenReturn(currentUser);
        when(profileService.getPersonalProfileById(1L)).thenReturn(currentUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/personal-profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tony@starkindustries.com"));
    }

    @Test
    void testPutPersonalProfile_UpdatesSuccessfully() throws Exception {
        UserAccount currentUser = UserAccount.builder()
                .id(1L)
                .firstName("Bruce")
                .lastName("Banner")
                .email("bruce@avengers.com")
                .build();

        when(userContext.getCurrentUser()).thenReturn(currentUser);

        PersonalProfileUpdate update = PersonalProfileUpdate.builder()
                .user(currentUser)
                .oldPassword("hulkSmash123")
                .build();

        String payload = objectMapper.writeValueAsString(update);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/personal-profiles")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(profileService).updatePersonalProfile(eq(1L), any(PersonalProfileUpdate.class));
    }
}
