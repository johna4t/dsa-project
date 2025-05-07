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
    void testPostDataContentDefinition() throws Exception {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        Long dcdId = 5L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("DCD 1")
                .description("DCD 1 desc")
                .provider(prov)
                .build();

        when(this.dcdMockService.createDataContentDefinition(any())).thenReturn(dcdId);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String payload = mapper
                .writerWithDefaultPrettyPrinter().writeValueAsString(dcd);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-content-definitions")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(201))
                .andExpect(content().string(dcdId.toString()))
                .andReturn();

        verify(this.dcdMockService, times(1))
                .createDataContentDefinition(any());

    }

    @Test
    void testGetDataContentDefinitions() throws Exception {
        when(userContextMockService.isSuperAdmin()).thenReturn(true);

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
        when(this.dcdMockService.getDataContentDefinitions()).thenReturn(dcds);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-content-definitions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(dcdId1))
                .andExpect(jsonPath("$[0].dataContentType").value(DataContentType.NOT_SPECIFIED.toString()))
                .andExpect(jsonPath("$[1].id").value(dcdId2))
                .andExpect(jsonPath("$[1].dataContentType").value(DataContentType.NOT_SPECIFIED.toString()));

        verify(this.dcdMockService, times(1)).getDataContentDefinitions();
    }


    @Test
    @WithMockUser(username = "user", roles = "NUTS")
    void testGetDataContentDefinitionById() throws Exception {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        DataContentPerspective dcp1 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.CONSENT,
                        "specialCategory", SpecialCategoryData.POLITICAL
                ))
                .build();

        Long dcdId1 = 2L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .name("DCD 1")
                .provider(prov)
                .perspectives(List.of(dcp1))
                .build();

        DataContentPerspective dcp2 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.CONTRACT,
                        "specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA
                ))
                .build();

        Long dcdId2 = 3L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .name("DCD 2")
                .dataContentType(DataContentType.PAPER_DOCUMENT)
                .provider(prov)
                .perspectives(List.of(dcp2))
                .build();

        List<DataContentDefinition> dcds = new ArrayList<>();
        dcds.add(dcd1);
        dcds.add(dcd2);

        when(this.dcdMockService.getDataContentDefinitionById(dcdId2)).thenReturn(dcd2);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-content-definitions/" + dcdId2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(5))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(dcdId2))
                .andExpect(jsonPath("$.dataContentType").value(DataContentType.PAPER_DOCUMENT.toString()))
                .andReturn();

        verify(this.dcdMockService, times(1))
                .getDataContentDefinitionById(dcdId2);
    }

    @Test
    void testPatchDataContentDefinition() throws Exception {

        String updatedDescription = "Updated description";

        Long dcdId = 1L;

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/data-content-definitions/" + dcdId)
                        .param("description", updatedDescription)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204))
                .andReturn();

        verify(this.dcdMockService, times(1))
                .updateDataContentDefinition(
                        dcdId,
                        null,
                        updatedDescription,
                        null
                );
    }



    @Test
    void testDeleteDataContentDefinition() throws Exception {

        Long dcdId = 1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-content-definitions/" + dcdId))
                .andExpect(status().is(204))
                .andReturn();

        verify(this.dcdMockService, times(1))
                .deleteDataContentDefinition(dcdId);
    }

}
