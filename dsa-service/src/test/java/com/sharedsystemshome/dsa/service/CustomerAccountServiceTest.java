package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.CustomerAccountRepository;
import com.sharedsystemshome.dsa.repository.DataSharingAgreementRepository;
import com.sharedsystemshome.dsa.repository.UserAccountRepository;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerAccountServiceTest {

    @Mock
    private CustomerAccountRepository customerMockRepo;

    @Mock
    private RoleRepository roleMockRepo;

    @Mock
    private UserAccountRepository userMockRepo;

    @Mock
    private DataSharingAgreementRepository dsaMockRepo;

    private CustomerAccountService customerService;

    private CustomValidator customerValidator;

    private UserAccountService userService;

    private CustomValidator userValidator;

    private static Address address;


    @BeforeAll
    public static void setUpAll(){
        final String buName = "Information Management";
        final String addressLine1 = "10 Main Street";
        final String addressLine2 = "Anytown";
        final String addressLine3 = "";
        final String addressLine4 = "North East";
        final String addressLine5 = "UK";
        final String postalCode = "XY1 Z23";

        address = new Address();
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setAddressLine3(addressLine3);
        address.setAddressLine4(addressLine4);
        address.setAddressLine5(addressLine5);
        address.setPostalCode(postalCode);
    }



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.customerService = new CustomerAccountService(
                this.customerMockRepo,
                this.userService,
                this.userValidator);
    }

    @AfterEach
    void tearDown() {
    }

    @Disabled
    //Unable to mock Customer Account.  Method verified by black-box testing.
    void testCreateCustomerAccount_ValidCustomer() {

        Long custId = 1L;
        CustomerAccount customer = CustomerAccount.builder()
                .users(List.of(new UserAccount()))
                .build();

        CustomerAccount savedCustomer = CustomerAccount.builder()
                .id(custId)
                .users(List.of(new UserAccount()))
                .build();

        when(this.customerMockRepo.save(any())).thenReturn(savedCustomer);

        Long result = this.customerService.createCustomerAccount(customer);

        assertNotNull(result);
        assertEquals(custId, result);
        verify(this.customerMockRepo, times(1)).save(customer);
    }

    @Test
    public void testCreateCustomer_CustomerIsNull() {

        CustomerAccount customer = null;

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.customerService.createCustomerAccount(customer));

        assertEquals("Customer Account is null or empty.", e.getMessage());

        verify(this.customerMockRepo, times(0)).save(customer);

    }

    @Disabled
    //Unable to mock Customer Account.  Method verified by black-box testing.
    public void testCreateCustomer_SaveThrowsException() {

        CustomerAccount customer = CustomerAccount.builder()
                .dataSharingParty(new DataSharingParty())
                .address(new Address())
                .build();

        when(this.customerMockRepo.save(customer)).thenThrow(IllegalArgumentException.class);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.customerService.createCustomerAccount(customer));

        assertTrue(e.getMessage().contains("Unable to save Customer Account because "));

        verify(this.customerMockRepo, times(1)).save(customer);

    }

    @Test
    void testGetCustomerAccounts() {

        List<CustomerAccount> customers = new ArrayList<>();
        when(this.customerMockRepo.findAll()).thenReturn(customers);

        List<CustomerAccount> result = this.customerService.getCustomerAccounts();

        assertNotNull(result);
        assertEquals(customers, result);
        verify(this.customerMockRepo, times(1)).findAll();
    }

    @Test
    void testGetCustomerAccountById() {

        Long id = 1L;
        CustomerAccount customer = CustomerAccount.builder()
                .id(id)
                .address(new Address())
                .build();

        when(this.customerMockRepo.findById(id)).thenReturn(Optional.of(customer));

        CustomerAccount result = this.customerService.getCustomerAccountById(id);

        assertNotNull(result);
        assertEquals(customer, result);
        verify(this.customerMockRepo, times(1)).findById(id);
    }

    @Test
    void testGetCustomerAccountById_InvalidId() {

        Long id = 1L;
        when(this.customerMockRepo.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.customerService.getCustomerAccountById(id));
        assertEquals(e.getMessage(), "Customer Account with id = " + id + " not found.");
        verify(this.customerMockRepo, times(1)).findById(id);
    }


    @Test
    void testUpdateCustomerAccount() {

        Long id = 1L;
        String oldBuName = "Info Mgt";
        CustomerAccount customer = CustomerAccount.builder()
                .id(id)
                .branchName(oldBuName)
                .address(new Address())
                .build();

        String newBuName = "Data Mgt";
        CustomerAccount updatedCust = CustomerAccount.builder()
                .id(id)
                .branchName(newBuName)
                .address(address)
                .build();

        when(this.customerMockRepo.findById(id)).thenReturn(Optional.of(customer));

        this.customerService.updateCustomerAccount(updatedCust);

        verify(this.customerMockRepo, times(1)).findById(id);
        verify(this.customerMockRepo, times(0)).save(customer);

        assertEquals(updatedCust.getBranchName(), customer.getBranchName());
        assertEquals(updatedCust.getAddress().getAddressLine2(), customer.getAddress().getAddressLine2());
        assertEquals(updatedCust.getAddress().getPostalCode(), customer.getAddress().getPostalCode());

    }

    @Test
    public void testUpdateCustomerAccount_InvalidId(){

        Long id = 1L;
        CustomerAccount customer = CustomerAccount.builder()
                .id(id)
                .address(new Address())
                .build();

        when(this.customerMockRepo.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.customerService.updateCustomerAccount(customer));

        assertEquals("Customer Account with id = " + id + " not found.", e.getMessage());

        verify(this.customerMockRepo, times(1)).findById(id);

    }


    @Test
    void testDeleteCustomerAccount() {

        Long id = 1L;

        when(this.customerMockRepo.existsById(id)).thenReturn(true);

        this.customerService.deleteCustomerAccount(id);

        verify(this.customerMockRepo, times(1)).existsById(id);
        verify(this.customerMockRepo, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCustomerAccount_InvalidId() {

        Long id = 1L;

        when(this.customerMockRepo.existsById(id)).thenReturn(false);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.customerService.deleteCustomerAccount(id));

        assertEquals("Customer Account with id = " + id + " not found.", e.getMessage());

        verify(this.customerMockRepo, times(1)).existsById(id);
        verify(this.customerMockRepo, times(0)).deleteById(id);
    }


}