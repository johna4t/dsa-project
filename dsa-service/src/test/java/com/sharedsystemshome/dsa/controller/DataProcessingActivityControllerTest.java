package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import com.sharedsystemshome.dsa.model.DataProcessor;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.DataProcessingActivityService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class DataProcessingActivityControllerTest {

    @InjectMocks
    private DataProcessingActivityController dpvController;

    @Mock
    private DataProcessingActivityService dpvMockService;

    @Mock
    private UserContextService userContextMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dpvController).build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetDataProcessingActivities() throws Exception {

        // Arrange
        Long customerId = 123L;
        when(userContextMockService.getCurrentCustomerAccountId()).thenReturn(customerId);

        Long dpvId1 = 1L;
        DataProcessingActivity dpv1 = DataProcessingActivity.builder()
                .id(dpvId1)
                .name("DPV 1")
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();

        Long dpvId2 = 2L;
        DataProcessingActivity dpv2 = DataProcessingActivity.builder()
                .id(dpvId2)
                .name("DPV 2")
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();

        List<DataProcessingActivity> activities = List.of(dpv1, dpv2);
        when(dpvMockService.getDataProcessingActivities(customerId)).thenReturn(activities);

        // Act
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processing-activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(dpvId1))
                .andExpect(jsonPath("$[1].id").value(dpvId2));

        // Assert
        verify(this.dpvMockService, times(1)).getDataProcessingActivities(customerId);
    }

    @Test
    void testGetDataProcessingActivities_WhenNoActivitiesExist() throws Exception {

        // Arrange
        Long customerId = 789L;
        when(this.userContextMockService.getCurrentCustomerAccountId()).thenReturn(customerId);
        when(this.dpvMockService.getDataProcessingActivities(customerId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processing-activities"))
                .andExpect(status().isNoContent())
                .andExpect(content().string("")); // Ensure empty body

        // Verify
        verify(this.dpvMockService, times(1)).getDataProcessingActivities(customerId);

    }

    @Test
    void testGetDataProcessingActivities_WithQueryParameters() throws Exception {

        // language=json
        String jsonBody = """
                {
                    "invalid": "true"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processing-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result ->
                        assertEquals("Unrecognised query parameter.", result.getResolvedException().getMessage()));

    }

    @Test
    void testGetDataProcessingActivityById() throws Exception {

        // Arrange
        DataProcessor processor = new DataProcessor();
        processor.setId(201L);

        DataContentDefinition dcd = new DataContentDefinition();
        dcd.setId(301L);

        Long dpvId = 101L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test Activity")
                .dataProcessor(processor)
                .dataContentDefinition(dcd)
                .build();

        when(dpvMockService.getDataProcessingActivityById(dpvId)).thenReturn(dpv);
        when(userContextMockService.validateAccess(dpv)).thenReturn(dpv);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processing-activities/{id}", dpvId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.id").value(dpvId))
                .andExpect(jsonPath("$.name").value("Test Activity"));

        verify(dpvMockService, times(1)).getDataProcessingActivityById(dpvId);
        verify(userContextMockService, times(1)).validateAccess(dpv);
    }

    @Test
    void testGetDataProcessingActivityById_WhenEntityDoesNotExist() throws Exception {
        // Arrange
        Long missingId = 999L;
        when(dpvMockService.getDataProcessingActivityById(missingId))
                .thenThrow(new EntityNotFoundException("DataProcessingActivity", missingId));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-processing-activities/{id}", missingId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(dpvMockService, times(1)).getDataProcessingActivityById(missingId);
        verify(userContextMockService, never()).validateAccess(any());
    }

    @Test
    void testPostDataProcessingActivity() throws Exception {

        // Given
        DataProcessor processor = new DataProcessor();
        processor.setId(201L);

        DataContentDefinition dcd = new DataContentDefinition();
        dcd.setId(301L);

        Long dpvId = 101L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .name("Test Activity")
                .dataProcessor(processor)
                .dataContentDefinition(dcd)
                .build();

        // Convert payload to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dpv);

        when(userContextMockService.validateAccess(any(DataProcessingActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(dpvMockService.createDataProcessingActivity(any(DataProcessingActivity.class)))
                .thenAnswer(invocation -> {
                    DataProcessingActivity arg = invocation.getArgument(0);
                    arg.setId(dpvId);
                    return dpvId;
                });

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/data-processing-activities")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(dpvId.toString()));

        // Verify call chain
        verify(userContextMockService, times(1)).validateAccess(any(DataProcessingActivity.class));
        verify(dpvMockService, times(1)).createDataProcessingActivity(any(DataProcessingActivity.class));
    }

    @Test
    void testPostDataProcessingActivity_WithInvalidDPV() throws Exception {
        // Given: invalid input (null body or missing fields)
        DataProcessingActivity invalidDpv = new DataProcessingActivity(); // no name, no processor, no dcd

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(invalidDpv);

        // Simulate validation failure in userContext or service
        when(userContextMockService.validateAccess(any(DataProcessingActivity.class)))
                .thenThrow(new NullOrEmptyValueException("DataProcessingActivity is missing required fields."));

        // Expect: BadRequestException is rethrown by controller
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-processing-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(result ->
                        assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                                .contains("DataProcessingActivity is missing required fields")));
    }

    @Test
    void testPutDataProcessingActivity() throws Exception {
        // Given
        DataProcessor processor = new DataProcessor();
        processor.setId(201L);

        DataContentDefinition dcd = new DataContentDefinition();
        dcd.setId(301L);

        Long dpvId = 101L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test Activity")
                .dataProcessor(processor)
                .dataContentDefinition(dcd)
                .build();

        when(dpvMockService.getDataProcessingActivityById(dpvId)).thenReturn(dpv);
        when(userContextMockService.validateAccess(dpv)).thenReturn(dpv);

        DataProcessingActivity dpv2 = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Updated Test Activity")
                .build();

        // Mock the update method
        doNothing().when(dpvMockService).updateDataProcessingActivity(dpv2);

        // Convert payload to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dpv2);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/data-processing-activities/" + dpvId)
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify call chain
        verify(dpvMockService, times(1)).getDataProcessingActivityById(dpvId);
        verify(userContextMockService, times(1)).validateAccess(dpv);
        verify(dpvMockService, times(1)).updateDataProcessingActivity(dpv);
    }




    @Test
    void testDeleteDataProcessingActivity() throws Exception {

        Long dpvId = 1L;

        when(userContextMockService.isSuperAdmin()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-processing-activities/" + dpvId))
                .andExpect(status().isNoContent());

        verify(userContextMockService, times(1)).validateAccess(any());
        verify(dpvMockService, times(1)).deleteDataProcessingActivity(dpvId);
    }


}