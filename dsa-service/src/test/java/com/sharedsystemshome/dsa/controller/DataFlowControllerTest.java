package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.service.DataFlowService;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

class DataFlowControllerTest {

    @InjectMocks
    private DataFlowController dataFlowController;

    @Mock
    private DataFlowService dataFlowMockService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dataFlowController).build();
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testPostDataFlow() throws Exception {

        Long custId = 7L;
        CustomerAccount customer = CustomerAccount.builder()
                .id(custId)
                .build();

        Long dsa_id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsa_id)
                .accountHolder(customer)
                .build();

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();

        Long dcd_id = 4L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcd_id)
                .provider(prov)
                .build();

        Long df_id = 5L;
        DataFlow dataFlow = DataFlow.builder()
                /**
                 * ISS-000-001: postDataFlow method returns DataFlow id = 0, when DataFlow is initialised
                 * with a DSA, causing test fail.  DSA therefore removed from initialisation as
                 * workaround.
                 */
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .startDate(LocalDate.of(2024, 1, 5))
                .endDate(LocalDate.of(2025, 1, 4))
                .lawfulBasis(LawfulBasis.CONSENT)
                .specialCategory(SpecialCategoryData.HEALTH)
                .purposeOfSharing("Text here")
                .providedDcds(List.of(dcd))
                .build();

        when(this.dataFlowMockService.createDataFlow(any())).thenReturn(df_id);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String payload = mapper
                .writerWithDefaultPrettyPrinter().writeValueAsString(dataFlow);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-flows")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(201))
                .andExpect(content().string(df_id.toString()))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .createDataFlow(any());

    }

    @Test
    void testGetDataFlows() throws Exception {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .build();

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();

        Long cons1_id = 3L;
        DataSharingParty cons1 = DataSharingParty.builder()
                .id(cons1_id)
                .build();

        Long cons2_id = 4L;
        DataSharingParty cons2 = DataSharingParty.builder()
                .id(cons2_id)
                .build();

        Long df1_Id = 5L;
        DataFlow df1 = DataFlow.builder()
                .id(df1_Id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons1)
                .build();

        Long df2_Id = 6L;
        DataFlow df2 = DataFlow.builder()
                .id(df2_Id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons2)
                .build();

        List<DataFlow> dataFlows = new ArrayList<>();
        dataFlows.add(df1);
        dataFlows.add(df2);

        when(this.dataFlowMockService.getDataFlows()).thenReturn(dataFlows);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(df1_Id))
                .andExpect(jsonPath("$[0].isPersonalData").value(false))
                .andExpect(jsonPath("$[0].isSpecialCategoryData").value(false))
                .andExpect(jsonPath("$[1].id").value(df2_Id))
                .andExpect(jsonPath("$[1].isPersonalData").value(false))
                .andExpect(jsonPath("$[1].isSpecialCategoryData").value(false))
                .andReturn();

        verify(this.dataFlowMockService, times(1)).getDataFlows();


    }

    @Test
    void testGetDataFlowsByProvider() throws Exception {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .build();

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();

        Long cons1_id = 3L;
        DataSharingParty cons1 = DataSharingParty.builder()
                .id(cons1_id)
                .build();

        Long cons2_id = 4L;
        DataSharingParty cons2 = DataSharingParty.builder()
                .id(cons2_id)
                .build();

        Long df1_Id = 5L;
        DataFlow df1 = DataFlow.builder()
                .id(df1_Id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons1)
                .build();

        Long df2_Id = 6L;
        DataFlow df2 = DataFlow.builder()
                .id(df2_Id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons2)
                .build();

        List<DataFlow> dataFlows = new ArrayList<>();
        dataFlows.add(df1);
        dataFlows.add(df2);

        when(this.dataFlowMockService.getDataFlowsByProviderId(prov_id)).thenReturn(dataFlows);


        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows?provider=" + prov_id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(df1_Id))
                .andExpect(jsonPath("$[0].isPersonalData").value(false))
                .andExpect(jsonPath("$[0].isSpecialCategoryData").value(false))
                .andExpect(jsonPath("$[1].id").value(df2_Id))
                .andExpect(jsonPath("$[1].isPersonalData").value(false))
                .andExpect(jsonPath("$[1].isSpecialCategoryData").value(false))
                .andReturn();

        verify(this.dataFlowMockService, times(1)).getDataFlowsByProviderId(prov_id);


    }

    @Test
    void testGetDataFlowsByConsumer() throws Exception {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .build();

        Long prov1_id = 2L;
        DataSharingParty prov1 = DataSharingParty.builder()
                .id(prov1_id)
                .build();

        Long prov2_id = 3L;
        DataSharingParty prov2 = DataSharingParty.builder()
                .id(prov2_id)
                .build();

        Long cons_id = 4L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();

        Long df1_Id = 5L;
        DataFlow df1 = DataFlow.builder()
                .id(df1_Id)
                .dataSharingAgreement(dsa)
                .provider(prov1)
                .consumer(cons)
                .build();

        Long df2_Id = 6L;
        DataFlow df2 = DataFlow.builder()
                .id(df2_Id)
                .dataSharingAgreement(dsa)
                .provider(prov2)
                .consumer(cons)
                .build();

        List<DataFlow> dataFlows = new ArrayList<>();
        dataFlows.add(df1);
        dataFlows.add(df2);

        when(this.dataFlowMockService.getDataFlowsByConsumerId(cons_id)).thenReturn(dataFlows);


        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows?consumer=" + cons_id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(df1_Id))
                .andExpect(jsonPath("$[0].isPersonalData").value(false))
                .andExpect(jsonPath("$[0].isSpecialCategoryData").value(false))
                .andExpect(jsonPath("$[1].id").value(df2_Id))
                .andExpect(jsonPath("$[1].isPersonalData").value(false))
                .andExpect(jsonPath("$[1].isSpecialCategoryData").value(false))
                .andReturn();

        verify(this.dataFlowMockService, times(1)).getDataFlowsByConsumerId(cons_id);


    }

    @Test
    void testGetDataFlowsByProviderWithInvalidUrl1() throws Exception {

        String invalidParam = "provider=1&consumer=2";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows?" + invalidParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Too many parameters passed.", result.getResolvedException().getMessage()));

    }

    @Test
    void testGetDataFlowsByProviderWithInvalidUrl2() throws Exception {

        String invalidParam = "provider=1.0";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows?" + invalidParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Invalid id format.", result.getResolvedException().getMessage()));

    }

    @Test
    void testGetDataFlowsByProviderWithInvalidUrl3() throws Exception {

        String invalidParam = "parameter=1";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows?" + invalidParam)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Unrecognised query parameter.", result.getResolvedException().getMessage()));

    }

    @Test
    void testGetDataFlowById() throws Exception {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .build();

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .build();

        when(this.dataFlowMockService.getDataFlowById(df_id)).thenReturn(dataFlow);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows/" + df_id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(11))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(df_id))
                .andExpect(jsonPath("$.isPersonalData").value(false))
                .andExpect(jsonPath("$.isSpecialCategoryData").value(false))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .getDataFlowById(df_id);
    }

/*
    @Test
    void testGetDataFlowProvider() throws Exception {

        Long prov_id = 2L;
        String name = "Provider Org";
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .description(name)
                .build();

        CustomerAccount cust1 = CustomerAccount.builder()
                .id(5L)
                .name("Cust1")
                .departmentName("Cust1 Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov)
                .build();

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .accountHolder(cust1)
                .build();


        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .id(6L)
                .name("Cust2")
                .departmentName("Cust2 Dept")
                .url("www.cust2.com")
                .dataSharingParty(cons)
                .build();

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .build();

        when(this.dataFlowMockService.getDataFlowProvider(df_id)).thenReturn(prov);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows/" + df_id + "/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(6))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(prov_id))
                .andExpect(jsonPath("$.description").value(name))
                .andExpect(jsonPath("$.isProvider").value(true))
                .andExpect(jsonPath("$.isConsumer").value(false))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .getDataFlowProvider(df_id);
    }
*/

/*
    @Test
    void testGetDataFlowConsumer() throws Exception {

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();

        CustomerAccount cust1 = CustomerAccount.builder()
                .id(5L)
                .name("Cust1")
                .departmentName("Cust1 Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov)
                .build();

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .accountHolder(cust1)
                .build();

        Long cons_id = 3L;
        String name = "Consumer Org";
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .description(name)
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .id(6L)
                .name("Cust2")
                .departmentName("Cust2 Dept")
                .url("www.cust2.com")
                .dataSharingParty(cons)
                .build();

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .build();

        when(this.dataFlowMockService.getDataFlowConsumer(df_id)).thenReturn(cons);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-flows/" + df_id + "/consumers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(6))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(cons_id))
                .andExpect(jsonPath("$.description").value(name))
                .andExpect(jsonPath("$.isProvider").value(false))
                .andExpect(jsonPath("$.isConsumer").value(true))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .getDataFlowConsumer(df_id);
    }
*/

    @Test
    void testPatchDataFlow() throws Exception {

        LocalDate newEndDate = LocalDate.of(2023, 9, 1);
        LawfulBasis newLawfulBasis = LawfulBasis.CONSENT;
        SpecialCategoryData newSpecial = SpecialCategoryData.HEALTH;
        String newPurpose = "New purpose of sharing";

        Long dfId = 1L;
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/data-flows/" + dfId)
                        .param("endDate", newEndDate.toString())
                        .param("lawfulBasis", newLawfulBasis.toString())
                        .param("specialCategory", newSpecial.toString())
                        .param("purposeOfSharing", newPurpose)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .updateDataFlow(
                        dfId,
                        newEndDate,
                        newLawfulBasis,
                        newSpecial,
                        newPurpose);

    }

    @Test
    void testDeleteDataFlow() throws Exception {

        Long dfId = 1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-flows/" + dfId))
                .andExpect(status().is(204))
                .andReturn();

        verify(this.dataFlowMockService, times(1))
                .deleteDataFlow(dfId);
    }

}