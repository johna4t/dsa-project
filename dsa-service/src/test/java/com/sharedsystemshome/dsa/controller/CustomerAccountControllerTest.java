package com.sharedsystemshome.dsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.service.CustomerAccountService;
import com.sharedsystemshome.dsa.service.DataSharingPartyService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CustomerAccountControllerTest {

    @InjectMocks
    private CustomerAccountController customerController;

    @Mock
    private CustomerAccountService customerMockService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(this.customerController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testPostCustomerAccount() throws Exception {

        DataSharingParty dsp = DataSharingParty.builder().build();
        Long custId = 5L;
        Address address = new Address(
                "10 Penny Lane",
                "AB1 2CD"
        );
        CustomerAccount customer = CustomerAccount.builder()
                .branchName("BU 1")
                .address(address)
                .dataSharingParty(dsp)
                .build();

        // Simulate saving customer
        dsp.setAccount(customer);

        // Simulate db generating id
        dsp.getSelfAsProcessor().setId(101L);

        when(this.customerMockService.createCustomerAccount(any())).thenReturn(custId);

        String payload = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(customer);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/customer-accounts")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().string(custId.toString()))
                .andReturn();

        verify(this.customerMockService, times(1))
                .createCustomerAccount(any());
    }

    @Test
    void testGetCustomerAccounts() throws Exception {

        DataSharingParty dsp1 = DataSharingParty.builder().build();
        Long custId1 = 1L;
        String buName1 = "BU 1";
        CustomerAccount customer1 = CustomerAccount.builder()
                .id(custId1)
                .dataSharingParty(dsp1)
                .branchName(buName1)
                .address(new Address("11 Penny Lane", "AB1 2CD"))
                .build();

        // Simulate saving customer
        dsp1.setAccount(customer1);

        // Simulate db generating id
        dsp1.getSelfAsProcessor().setId(101L);

        DataSharingParty dsp2 = DataSharingParty.builder().build();
        Long custId2 = 2L;
        String buName2 = "BU 2";
        CustomerAccount customer2 = CustomerAccount.builder()
                .id(custId2)
                .dataSharingParty(dsp2)
                .branchName(buName2)
                .address(new Address("12 Penny Lane", "AB1 2CD"))
                .build();

        // Simulate saving customer
        dsp2.setAccount(customer2);

        // Simulate db generating id
        dsp2.getSelfAsProcessor().setId(102L);

        List<CustomerAccount> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);

        when(this.customerMockService.getCustomerAccounts()).thenReturn(customers);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/customer-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$[0].id").value(custId1))
                .andExpect(jsonPath("$[0].branchName").value(buName1))
                .andExpect(jsonPath("$[1].id").value(custId2))
                .andExpect(jsonPath("$[1].branchName").value(buName2))
                .andReturn();

        verify(this.customerMockService, times(1))
                .getCustomerAccounts();
    }

    @Test
    void testGetCustomerAccountById() throws Exception {

        DataSharingParty dsp1 = DataSharingParty.builder().build();
        Long custId1 = 1L;
        String buName1 = "BU 1";
        CustomerAccount customer1 = CustomerAccount.builder()
                .id(custId1)
                .branchName(buName1)
                .address(new Address("11 Penny Lane", "AB1 2CD"))
                .dataSharingParty(dsp1)
                .build();

        // Simulate saving customer
        dsp1.setAccount(customer1);

        // Simulate db generating id
        dsp1.getSelfAsProcessor().setId(101L);

        DataSharingParty dsp2 = DataSharingParty.builder().build();
        Long custId2 = 2L;
        String buName2 = "BU 2";
        CustomerAccount customer2 = CustomerAccount.builder()
                .id(custId2)
                .branchName(buName2)
                .address(new Address("12 Penny Lane", "AB1 2CD"))
                .dataSharingParty(dsp2)
                .build();

        // Simulate saving customer
        dsp2.setAccount(customer2);

        // Simulate db generating id
        dsp2.getSelfAsProcessor().setId(102L);

        List<CustomerAccount> customers = new ArrayList<>();
        customers.add(customer1);
        customers.add(customer2);

        when(this.customerMockService.getCustomerAccountById(custId2)).thenReturn(customer2);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/customer-accounts/" + custId2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(7))
                // Verify the response body using JSONPath
                .andExpect(jsonPath("$.id").value(custId2))
                .andExpect(jsonPath("$.branchName").value(buName2))
                .andReturn();

        verify(this.customerMockService, times(1))
                .getCustomerAccountById(custId2);
    }


    @Test
    void testPutCustomerAccount() throws Exception {

        DataSharingParty dsp = DataSharingParty.builder().build();
        Long custId = 5L;
        Address address = new Address(
                "55 Beasley Street",
                "RS12 3TU"
        );
        CustomerAccount updatedCust = CustomerAccount.builder()
                .id(custId)
                .departmentName("Dept A")
                .branchName("BU 1A")
                .url("new url")
                .address(address)
                .dataSharingParty(dsp)
                .build();

        // Simulate saving customer
        dsp.setAccount(updatedCust);

        // Simulate db generating id
        dsp.getSelfAsProcessor().setId(101L);

        String payload = new ObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(updatedCust);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/customer-accounts")
                        .content(payload)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent())
                .andReturn();

        verify(this.customerMockService, times(1))
                .updateCustomerAccount(any());
    }

    @Test
    void testDeletCustomerAccount() throws Exception {

        Long custId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/customer-accounts/" + custId))
                .andExpect(status().isNoContent());

        verify(this.customerMockService, times(1))
                .deleteCustomerAccount(custId);
    }
}