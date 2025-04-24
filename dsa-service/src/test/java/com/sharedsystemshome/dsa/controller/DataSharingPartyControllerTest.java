package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.service.DataSharingPartyService;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataSharingPartyControllerTest {

    @InjectMocks
    private DataSharingPartyController dspController;

    @Mock
    private DataSharingPartyService dspMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(this.dspController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPostDataSharingParty() throws Exception{

        Long dspId = 5L;
        DataSharingParty dsp = DataSharingParty.builder()
                .description("My new DSP.")
                .build();

        Long custId = 2L;
        CustomerAccount cust = CustomerAccount.builder()
                .id(custId)
                .name("New DSPLtd")
                .departmentName("Dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .dataSharingParty(dsp)
                .build();

        when(this.dspMockService.createDataSharingParty(any())).thenReturn(dspId);

        String payload = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(dsp);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/data-sharing-parties")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(201))
                .andExpect(content().string(dspId.toString()))
                .andReturn();

        verify(this.dspMockService, times(1))
                .createDataSharingParty(any());
    }

    @Test
    void testGetDataSharingParties() throws Exception {

        Long dsp1_Id = 1L;
        String dsp1_name = "DSP 1";
        DataSharingParty dsp1 = DataSharingParty.builder()
                .id(dsp1_Id)
                .description(dsp1_name + " desc")
                .build();

        Long custId1 = 2L;
        CustomerAccount cust1 = CustomerAccount.builder()
                .id(custId1)
                .name("Cust1")
                .departmentName("Dept")
                .url("www.cust1.com")
                .branchName("Test BU")
                .dataSharingParty(dsp1)
                .build();

        Long dsp2_Id = 2L;
        String dsp2_name = "DSP 2";
        DataSharingParty dsp2 = DataSharingParty.builder()
                .id(dsp2_Id)
                .description(dsp2_name + " desc")
                .build();

        Long custId2 = 3L;
        CustomerAccount cust2 = CustomerAccount.builder()
                .id(custId2)
                .name("Cust2")
                .departmentName("Dept")
                .url("www.cust2.com")
                .branchName("Test BU")
                .dataSharingParty(dsp2)
                .build();

        List<DataSharingParty> dsps = new ArrayList<>();
        dsps.add(dsp1);
        dsps.add(dsp2);

        when(this.dspMockService.getDataSharingParties()).thenReturn(dsps);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-sharing-parties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(dsp1_Id))
                .andExpect(jsonPath("$[0].description").value(dsp1_name + " desc"))
                .andExpect(jsonPath("$[1].id").value(dsp2_Id))
                .andExpect(jsonPath("$[1].description").value(dsp2_name + " desc"))
                .andReturn();

        verify(this.dspMockService, times(1))
                .getDataSharingParties();
    }

    @Test
    void testGetDataSharingPartyById() throws Exception {

        String dspName = "DSP 1";
        Long dspId = 1L;
        DataSharingParty dsp = DataSharingParty.builder()
                .id(dspId)
                .description(dspName + " desc")
                .build();

        Long custId = 2L;
        CustomerAccount cust = CustomerAccount.builder()
                .id(custId)
                .name(dspName)
                .departmentName(dspName + " dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .dataSharingParty(dsp)
                .build();

        when(this.dspMockService.getDataSharingPartyById(dspId)).thenReturn(dsp);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/data-sharing-parties/" + dspId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(dspId))
                .andExpect(jsonPath("$.description").value(dspName + " desc"))
                .andReturn();

        verify(this.dspMockService, times(1))
                .getDataSharingPartyById(dspId);
    }

    @Test
    void testPutDataSharingParty() throws Exception {

        Long dspId = 1L;
        String updatedDesc = "New DSP desc";

        DataSharingParty updatedDsp = DataSharingParty.builder()
                .id(dspId)
                .description(updatedDesc)
                .build();

        Long custId = 2L;
        String updatedName = "New DSP name";
        String updatedUrl = "www.new-dsp.com";
        CustomerAccount cust = CustomerAccount.builder()
                .id(custId)
                .name(updatedName)
                .departmentName(updatedName + " desc")
                .url(updatedUrl)
                .branchName("Test BU")
                .dataSharingParty(updatedDsp)
                .build();


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/data-sharing-parties")
                        .content(updatedDsp.toJsonString())
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is(204))
                .andReturn();

        verify(this.dspMockService, times(1)).updateDataSharingParty(any());
    }

    @Test
    void testDeleteDataSharingParty() throws Exception {

        Long dspId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/data-sharing-parties/" + dspId))
                .andExpect(status().is(204));

        verify(this.dspMockService, times(1))
                .deleteDataSharingParty(dspId);
    }

}