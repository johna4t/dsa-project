package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.DataContentDefinitionService;
import com.sharedsystemshome.dsa.enums.DataContentType;
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
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DataContentDefinitionControllerTest {

    @InjectMocks
    private DataContentDefinitionController dcdController;

    @Mock
    private DataContentDefinitionService dcdMockService;

    @Mock
    private UserContextService userContextMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dcdController).build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "user", roles = "SUPER_ADMIN")
    void testPostDataContentDefinition() throws Exception {

        Long dcdId = 5L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("DCD 1")
                .description("DCD 1 desc")
                .build();

        when(userContextMockService.isSuperAdmin()).thenReturn(true);
        when(dcdMockService.createDataContentDefinition(any())).thenReturn(dcdId);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dcd);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-content-definitions")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(dcdId.toString()));

        verify(dcdMockService, times(1)).createDataContentDefinition(any());
    }


    @Test
    void testGetDataContentDefinitions() throws Exception {
        Long customerAccountId = 99L;

        when(userContextMockService.getCurrentCustomerAccountId()).thenReturn(customerAccountId);

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        Long dcdId1 = 2L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .name("DCD 1")
                .provider(prov)
                .build();

        Long dcdId2 = 3L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .name("DCD 2")
                .provider(prov)
                .build();

        List<DataContentDefinition> dcds = List.of(dcd1, dcd2);
        when(this.dcdMockService.getDataContentDefinitions(customerAccountId)).thenReturn(dcds);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-content-definitions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(dcdId1))
                .andExpect(jsonPath("$[0].dataContentType").value(DataContentType.NOT_SPECIFIED.toString()))
                .andExpect(jsonPath("$[1].id").value(dcdId2))
                .andExpect(jsonPath("$[1].dataContentType").value(DataContentType.NOT_SPECIFIED.toString()));

        verify(this.dcdMockService, times(1)).getDataContentDefinitions(customerAccountId);
    }



    @Test
    @WithMockUser(username = "user", roles = "SUPER_ADMIN")
    void testGetDataContentDefinitionById() throws Exception {

        Long dcdId = 3L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("DCD 2")
                .dataContentType(DataContentType.PAPER_DOCUMENT)
                .build();

        when(dcdMockService.getDataContentDefinitionById(dcdId)).thenReturn(dcd);
        when(userContextMockService.validateAccess(dcd)).thenReturn(dcd);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-content-definitions/" + dcdId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dcdId))
                .andExpect(jsonPath("$.dataContentType").value(DataContentType.PAPER_DOCUMENT.toString()));

        verify(dcdMockService, times(1)).getDataContentDefinitionById(dcdId);
        verify(userContextMockService, times(1)).validateAccess(dcd);
    }

    @Test
    void testPutDataContentDefinition() throws Exception {

        Long dcdId = 3L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("DCD Name")
                .dataContentType(DataContentType.PAPER_DOCUMENT)
                .build();

        when(dcdMockService.getDataContentDefinitionById(dcdId)).thenReturn(dcd);
        when(userContextMockService.validateAccess(dcd)).thenReturn(dcd);

        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId)
                .name("Updated DCD Name")
                .dataContentType(DataContentType.UNSTRUCTURED_ELECTRONIC_DATA)
                .build();

        // Mock the update method
        doNothing().when(dcdMockService).updateDataContentDefinition(dcd2);

        // Convert payload to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(dcd2);

       this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/data-content-definitions/" + dcdId)
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify call chain
        verify(dcdMockService, times(1)).getDataContentDefinitionById(dcdId);
        verify(userContextMockService, times(1)).validateAccess(dcd);
        verify(dcdMockService, times(1)).updateDataContentDefinition(dcd2);
    }



    @Test
    @WithMockUser(username = "admin", roles = "SUPER_ADMIN")
    void testDeleteDataContentDefinition() throws Exception {

        Long dcdId = 1L;

        when(userContextMockService.isSuperAdmin()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-content-definitions/" + dcdId))
                .andExpect(status().isNoContent());

        verify(dcdMockService, times(1))
                .deleteDataContentDefinition(dcdId);
    }


}
