package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.ProcessingCertificationStandard;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataProcessor;
import com.sharedsystemshome.dsa.model.DataProcessorCertification;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.service.DataProcessorService;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DataProcessorControllerTest {

    @InjectMocks
    private DataProcessorController dpController;

    @Mock
    private DataProcessorService dpMockService;

    @Mock
    private UserContextService userContextMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dpController).build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "user", roles = "MEMBER")
    void testPostDataProcessor() throws Exception {
        Long dpId = 5L;


        // Create the original request DataProcessor
        DataProcessor dpRequest = DataProcessor.builder()
                .name("dp 1")
                .description("dp 1 desc")
                .certifications(new ArrayList<>(List.of(ProcessingCertificationStandard.ISO_IEC_22301)))
                .build();

        // Clone the validated version of the same object, assuming validateAccess returns it
        DataProcessor validatedDp = DataProcessor.builder()
                .name(dpRequest.getName())
                .description(dpRequest.getDescription())
                .certifications(new ArrayList<>(dpRequest.getCertifications()))
                .build();

        // Mocks
        when(userContextMockService.validateAccess(any(DataProcessor.class))).thenReturn(validatedDp);
        when(dpMockService.createDataProcessor(validatedDp)).thenReturn(dpId);

        // Convert payload to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dpRequest);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-processors")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(dpId.toString()));

        // Verify call chain
        verify(userContextMockService, times(1)).validateAccess(any(DataProcessor.class));
        verify(dpMockService, times(1)).createDataProcessor(validatedDp);
    }

    @Test
    @WithMockUser(username = "user", roles = "MEMBER")
    void testPostDataProcessor_WithUnauthorisedAccess() throws Exception {
        // Given
        DataProcessor dpRequest = DataProcessor.builder()
                .name("dp 2")
                .description("unauthorised attempt")
                .certifications(new ArrayList<>(List.of(ProcessingCertificationStandard.NIST_Privacy_Framework)))
                .build();

        // Simulate security failure
        String errorMessage = "Record with id null does not exist for Customer with id 123";

        when(userContextMockService.validateAccess(any(DataProcessor.class)))
                .thenThrow(new SecurityValidationException(errorMessage));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dpRequest);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-processors")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(result -> assertInstanceOf(SecurityValidationException.class, result.getResolvedException().getCause()))
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getCause().getMessage()));

        // Verify that createDataProcessor was NOT called
        verify(dpMockService, times(0)).createDataProcessor(any());
        verify(userContextMockService, times(1)).validateAccess(any(DataProcessor.class));
    }


    @Test
    void testGetDataProcessors() throws Exception {
        Long customerAccountId = 99L;

        when(userContextMockService.getCurrentCustomerAccountId()).thenReturn(customerAccountId);

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        Long dpId1 = 2L;
        DataProcessor dp1 = DataProcessor.builder()
                .id(dpId1)
                .name("dp 1")
                .controller(prov)
                .build();

        Long dpId2 = 3L;
        DataProcessor dp2 = DataProcessor.builder()
                .id(dpId2)
                .name("dp 2")
                .controller(prov)
                .build();

        List<DataProcessor> dps = List.of(dp1, dp2);
        when(this.dpMockService.getDataProcessors(customerAccountId)).thenReturn(dps);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(dpId1))
                .andExpect(jsonPath("$[1].id").value(dpId2));

        verify(this.dpMockService, times(1)).getDataProcessors(customerAccountId);
    }




    @Test
    @WithMockUser(username = "user", roles = "SUPER_ADMIN")
    void testGetDataProcessorById() throws Exception {

        Long dpId = 3L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("dp 2")
                .build();

        when(dpMockService.getDataProcessorById(dpId)).thenReturn(dp);
        when(userContextMockService.validateAccess(dp)).thenReturn(dp);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processors/" + dpId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dpId))
                .andExpect(jsonPath("$.name").value("dp 2"));

        verify(dpMockService, times(1)).getDataProcessorById(dpId);
        verify(userContextMockService, times(1)).validateAccess(dp);
        verifyNoMoreInteractions(dpMockService, userContextMockService);
    }



    @Test
    @WithMockUser(username = "admin", roles = "SUPER_ADMIN")
    void testDeleteDataProcessor() throws Exception {

        Long dpId = 1L;

        when(userContextMockService.isSuperAdmin()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-processors/" + dpId))
                .andExpect(status().isNoContent());

        verify(userContextMockService, times(1)).validateAccess(any());
        verify(dpMockService, times(1)).deleteDataProcessor(dpId);
    }


}
