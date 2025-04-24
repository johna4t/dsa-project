package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.DataSharingAgreementService;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


class DataSharingAgreementControllerTest {

    @InjectMocks
    private DataSharingAgreementController dsaController;

    @Mock
    private DataSharingAgreementService dsaMockService;

    @Mock
    private UserContextService contextMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.dsaController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPostDataSharingAgreement() throws Exception{

        CustomerAccount cust = new CustomerAccount();
        cust.setId(2L);


        Long dsaId = 5L;
        String name = "DSA 1";
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(0L)
                .name(name)
                .accountHolder(cust)
                .build();

        when(this.dsaMockService.createDataSharingAgreement(any())).thenReturn(dsaId);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String payload = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dsa);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-sharing-agreements")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(201))
                .andExpect(content().string(dsaId.toString()))
                .andReturn();

        verify(this.dsaMockService, times(1))
                .createDataSharingAgreement(any());
    }

    @Test
    void testGetDataSharingAgreements_SuperAdmin() throws Exception {

        Long dsa1_Id = 1L;
        String name1 = "DSA 1";
        DataSharingAgreement dsa1 = DataSharingAgreement.builder()
                .id(dsa1_Id)
                .name(name1)
                .build();

        Long dsa2_Id = 1L;
        String name2 = "DSA 2";
        DataSharingAgreement dsa2 = DataSharingAgreement.builder()
                .id(dsa2_Id)
                .name(name2)
                .build();

        List<DataSharingAgreement> dsas = new ArrayList<>();
        dsas.add(dsa1);
        dsas.add(dsa2);

        when(this.contextMockService.isSuperAdmin()).thenReturn(true);

        when(this.dsaMockService.getDataSharingAgreements()).thenReturn(dsas);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-sharing-agreements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(dsa1_Id))
                .andExpect(jsonPath("$[0].name").value(name1))
                .andExpect(jsonPath("$[1].id").value(dsa2_Id))
                .andExpect(jsonPath("$[1].name").value(name2))
                .andReturn();

        verify(this.dsaMockService, times(1))
                .getDataSharingAgreements();
    }

    @Test
    void testGetDataSharingAgreements_NotSuperAdmin() throws Exception {

        Long custId = 10L;
        CustomerAccount cust = CustomerAccount.builder()
                .id(custId)
                .build();

        Long userId = 5L;
        UserAccount user = UserAccount.builder()
                .id(userId)
                .parentAccount(cust)
                .build();

        Long dsa1_Id = 1L;
        String name1 = "DSA 1";
        DataSharingAgreement dsa1 = DataSharingAgreement.builder()
                .id(dsa1_Id)
                .name(name1)
                .build();

        Long dsa2_Id = 1L;
        String name2 = "DSA 2";
        DataSharingAgreement dsa2 = DataSharingAgreement.builder()
                .id(dsa2_Id)
                .name(name2)
                .build();

        List<DataSharingAgreement> dsas = new ArrayList<>();
        dsas.add(dsa1);
        dsas.add(dsa2);


        when(this.contextMockService.isSuperAdmin()).thenReturn(false);

        when(this.contextMockService.getCurrentUser()).thenReturn(user);

        when(this.dsaMockService.getDataSharingAgreementsByCustomerId(custId)).thenReturn(dsas);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-sharing-agreements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(dsa1_Id))
                .andExpect(jsonPath("$[0].name").value(name1))
                .andExpect(jsonPath("$[1].id").value(dsa2_Id))
                .andExpect(jsonPath("$[1].name").value(name2))
                .andReturn();

        verify(this.dsaMockService, times(1))
                .getDataSharingAgreementsByCustomerId(custId);
    }

    @Test
    void testGetDataSharingAgreementById() throws Exception {

        Long dsaId = 1L;
        String name = "DSA 1";
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name(name)
                .build();

        when(this.dsaMockService.getDataSharingAgreementById(dsaId)).thenReturn(dsa);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-sharing-agreements/" + dsaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(dsaId))
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();

        verify(this.dsaMockService, times(1))
                .getDataSharingAgreementById(dsaId);
    }

    @Test
    void testPatchDataSharingAgreement() throws Exception {

        Long dsaId = 1L;
        String updatedName = "New DSA name";
        LocalDate updatedStart = LocalDate.of(2023,10,23);
        LocalDate updatedEnd= LocalDate.of(2025,10,22);
        ControllerRelationship updatedCR = ControllerRelationship.SEPARATE;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/data-sharing-agreements/" + dsaId)
                        .param("name", updatedName)
                        .param("startDate", updatedStart.toString())
                        .param("endDate", updatedEnd.toString())
                        .param("controllerRelationship", updatedCR.toString()))
                .andExpect(status().is(204))
                .andDo(print());

        verify(this.dsaMockService, times(1))
                .updateDataSharingAgreement(
                        dsaId,
                        updatedName,
                        updatedStart,
                        updatedEnd,
                        updatedCR);
    }

    @Test
    void testDeleteDataSharingAgreement() throws Exception {

        Long dsaId = 1L;

        mockMvc.perform(delete("/api/v1/data-sharing-agreements/" + dsaId))
                .andExpect(status().is(204))
                .andDo(print());

        verify(this.dsaMockService, times(1))
                .deleteDataSharingAgreement(dsaId);
    }
}