package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.PermissionRepository;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CustomerAccountRepositoryTest {

    private final String buName = "Information Management";
    private final String addressLine1 = "10 Main Street";
    private final String addressLine2 = "Anytown";
    private final String addressLine3 = "";
    private final String addressLine4 = "North East";
    private final String addressLine5 = "UK";
    private final String postalCode = "XY1 Z23";


    @Autowired
    CustomerAccountRepository testSubject;

    @Autowired
    DataSharingAgreementRepository dsaRepo;

    @Autowired
    DataSharingPartyRepository dspRepo;

    @Autowired
    DataFlowRepository dfRepo;

    @Autowired
    DataContentDefinitionRepository dcdRepo;

    @Autowired
    DataFlowRepository dataFlowRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Autowired
    UserAccountRepository userRepo;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSave_WithMinimalDataset() {

        CustomerAccount customer = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .branchName(this.buName)
                .build();
        this.testSubject.save(customer);

        // Assertion: repository should contain one CustomerAccount.
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        assertEquals(this.buName, customer.getBranchName());
        assertEquals("", customer.getAddress().getAddressLine1());
        assertEquals("", customer.getAddress().getAddressLine2());
        assertEquals("", customer.getAddress().getAddressLine3());
        assertEquals("", customer.getAddress().getAddressLine4());
        assertEquals("", customer.getAddress().getAddressLine5());
        assertEquals("", customer.getAddress().getPostalCode());

    }

    @Test
    void testSave1() {

        Address address = new Address();
        address.setAddressLine1(this.addressLine1);
        address.setAddressLine2(this.addressLine2);
        address.setAddressLine3(this.addressLine3);
        address.setAddressLine4(this.addressLine4);
        address.setAddressLine5(this.addressLine5);
        address.setPostalCode(this.postalCode);

        String dspName = "Test DSP";
        String dspDesc = "Test DSP desc";
        String dspUrl = "www.dsp.com";
        String deptName = "Test DSP A Dept";
        DataSharingParty dsp = DataSharingParty.builder()
                .description(dspDesc)
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name(dspName)
                .departmentName(deptName)
                .url(dspUrl)
                .branchName(this.buName)
                .address(address)
                .dataSharingParty(dsp)
                .build();
        this.testSubject.save(customer);

        // Assertion: repository should contain one CustomerAccount.
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        assertEquals(dspName, customer.getName());
        assertEquals(deptName, customer.getDepartmentName());
        assertEquals(dspUrl, customer.getUrl());
        assertEquals(this.buName, customer.getBranchName());
        assertEquals(this.addressLine1, customer.getAddress().getAddressLine1());
        assertEquals(this.addressLine2, customer.getAddress().getAddressLine2());
        assertEquals(this.addressLine3, customer.getAddress().getAddressLine3());
        assertEquals(this.addressLine4, customer.getAddress().getAddressLine4());
        assertEquals(this.addressLine5, customer.getAddress().getAddressLine5());
        assertEquals(this.postalCode, customer.getAddress().getPostalCode());
        assertEquals(customer.getId(), customer.getDataSharingParty().getId());
        assertEquals(dspName, customer.getDataSharingParty().getName());
        assertEquals(dspDesc, customer.getDataSharingParty().getDescription());
        assertEquals(dspUrl, customer.getDataSharingParty().getUrl());

    }

    @Test
    void testSave2() {

        String dspName = "Test DSP";
        String dspDesc = "Test DSP desc";
        String dspUrl = "www.dsp.com";
        DataSharingParty dsp = DataSharingParty.builder()
                .description(dspDesc)
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name(dspName)
                .departmentName(dspName + " Dept")
                .url(dspUrl)
                .branchName(this.buName)
                .dataSharingParty(dsp)
                .build();

        Address address = customer.getAddress();
        address.setAddressLine1(this.addressLine1);
        address.setAddressLine2(this.addressLine2);
        address.setAddressLine3(this.addressLine3);
        address.setAddressLine4(this.addressLine4);
        address.setAddressLine5(this.addressLine5);
        address.setPostalCode(this.postalCode);

        this.testSubject.save(customer);

        // Assertion: repository should contain one DataFlow.
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        assertEquals(dspName, customer.getName());
        assertEquals(dspName + " Dept", customer.getDepartmentName());
        assertEquals(dspUrl, customer.getUrl());
        assertEquals(customer.getBranchName(), this.buName);
        assertEquals(customer.getAddress().getAddressLine1(), this.addressLine1);
        assertEquals(customer.getAddress().getAddressLine2(), this.addressLine2);
        assertEquals(customer.getAddress().getAddressLine3(), this.addressLine3);
        assertEquals(customer.getAddress().getAddressLine4(), this.addressLine4);
        assertEquals(customer.getAddress().getAddressLine5(), this.addressLine5);
        assertEquals(customer.getAddress().getPostalCode(), this.postalCode);

    }


    @Test
    void testSave3() {

        String dspName = "Test DSP";
        String dspDesc = "Test DSP desc";
        String dspUrl = "www.dsp.com";
        DataSharingParty dsp = DataSharingParty.builder()
                .description(dspDesc)
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name(dspName)
                .departmentName(dspName + " dept")
                .url(dspUrl)
                .branchName(this.buName)
                .dataSharingParty(dsp)
                .build();

        Address address = new Address();
        address.setAddressLine1(this.addressLine1);
        address.setAddressLine2(this.addressLine2);
        address.setAddressLine3(this.addressLine3);
        address.setAddressLine4(this.addressLine4);
        address.setAddressLine5(this.addressLine5);
        address.setPostalCode(this.postalCode);

        customer.setAddress(address);

        this.testSubject.save(customer);

        // Assertion: repository should contain one CustomerAccount.
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        assertEquals(dspName, customer.getName());
        assertEquals(dspName + " dept", customer.getDepartmentName());
        assertEquals(dspUrl, customer.getUrl());
        assertEquals(customer.getBranchName(), this.buName);
        assertEquals(customer.getAddress().getAddressLine1(), this.addressLine1);
        assertEquals(customer.getAddress().getAddressLine2(), this.addressLine2);
        assertEquals(customer.getAddress().getAddressLine3(), this.addressLine3);
        assertEquals(customer.getAddress().getAddressLine4(), this.addressLine4);
        assertEquals(customer.getAddress().getAddressLine5(), this.addressLine5);
        assertEquals(customer.getAddress().getPostalCode(), this.postalCode);

    }

    @Test
    void testSave_withoutDsp() {

        Address address = new Address();
        address.setAddressLine1(this.addressLine1);
        address.setAddressLine2(this.addressLine2);
        address.setAddressLine3(this.addressLine3);
        address.setAddressLine4(this.addressLine4);
        address.setAddressLine5(this.addressLine5);
        address.setPostalCode(this.postalCode);

        String dspName = "Test DSP";
        String dspUrl = "www.dsp.com";
        String deptName = "Test DSP A Dept";

        CustomerAccount customer = CustomerAccount.builder()
                .name(dspName)
                .departmentName(deptName)
                .url(dspUrl)
                .branchName(this.buName)
                .address(address)
                .build();
        this.testSubject.save(customer);

        // Assertion: repository should contain one CustomerAccount.
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        assertEquals(dspName, customer.getName());
        assertEquals(deptName, customer.getDepartmentName());
        assertEquals(dspUrl, customer.getUrl());

        assertEquals(this.buName, customer.getBranchName());
        assertEquals(this.addressLine1, customer.getAddress().getAddressLine1());
        assertEquals(this.addressLine2, customer.getAddress().getAddressLine2());
        assertEquals(this.addressLine3, customer.getAddress().getAddressLine3());
        assertEquals(this.addressLine4, customer.getAddress().getAddressLine4());
        assertEquals(this.addressLine5, customer.getAddress().getAddressLine5());
        assertEquals(this.postalCode, customer.getAddress().getPostalCode());
        assertEquals(customer.getId(), customer.getDataSharingParty().getId());
        assertEquals(dspName, customer.getDataSharingParty().getName());
        assertNull(customer.getDataSharingParty().getDescription());
        assertEquals(dspUrl, customer.getDataSharingParty().getUrl());

    }


    @Test
    void testDelete() {

        String dspName = "Test DSP";
        String dspDesc = "Test DSP desc";
        String dspUrl = "www.dsp.com";
        DataSharingParty dsp = DataSharingParty.builder()
                .description(dspDesc)
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name(dspName)
                .departmentName(dspName + " dept")
                .url(dspUrl)
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(dsp)
                .build();
        this.testSubject.save(customer);

        // Assertion: repository should contain one CustomerAccount
        assertEquals(1, this.testSubject.count());
        // Assertion: CustomerAccount should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(customer.getId()));

        this.testSubject.deleteById(customer.getId());

        // Assertion: repository should contain one CustomerAccount
        assertEquals(0, this.testSubject.count());
    }

    @Test
    void testDeleteById1() {

        DataSharingParty prov = DataSharingParty.builder()
                .description("Test Prov desc")
                .build();

        CustomerAccount customer1 = CustomerAccount.builder()
                .name("Test Prov")
                .departmentName("Test Prov dept")
                .url("www.prov.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(prov)
                .build();
        this.testSubject.save(customer1);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Test DCD")
                .description("Test DCD desc")
                .provider(prov)
                .ownerEmail("someone@email.com")
                .sourceSystem("Some System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(customer1)
                .build();
        this.dsaRepo.save(dsa);

        DataSharingParty cons = DataSharingParty.builder()
                .description("Test Cons desc")
                .build();

        CustomerAccount customer2 = CustomerAccount.builder()
                .name("Test Cons")
                .departmentName("Test Cons dept")
                .url("www.cons.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(cons)
                .build();
        this.testSubject.save(customer2);

        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing("Purpose of sharing")
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd))
                .build();
        this.dataFlowRepo.save(dataFlow);

        this.testSubject.deleteById(customer1.getId());

        assertEquals(1, this.testSubject.count());

    }

    @Test
    void testDeleteById2() {

        DataSharingParty prov = DataSharingParty.builder()
                .description("Test Prov desc")
                .build();

        CustomerAccount customer1 = CustomerAccount.builder()
                .name("Test Prov")
                .departmentName("Test Prov dept")
                .url("www.prov.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(prov)
                .build();
        this.testSubject.save(customer1);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Test DCD")
                .description("Test DCD desc")
                .provider(prov)
                .build();
        this.dcdRepo.save(dcd);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(customer1)
                .build();
        this.dsaRepo.save(dsa);

        DataSharingParty cons = DataSharingParty.builder()
                .description("Test Cons desc")
                .build();

        CustomerAccount customer2 = CustomerAccount.builder()
                .name("Test Cons")
                .departmentName("Test Cons dept")
                .url("www.cons.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(cons)
                .build();
        this.testSubject.save(customer2);

        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing("Purpose of sharing")
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd))
                .build();
        this.dataFlowRepo.save(dataFlow);

        this.testSubject.deleteById(customer2.getId());

        //Then
        Exception e1 = assertThrows(ConstraintViolationException.class, () -> {
            //When
            this.testSubject.count();
        });

//        this.testSubject.deleteById(customer1.getId());
//
//        //Then
//        Exception e2= assertThrows(DataIntegrityViolationException.class, () -> {
//            //When
//            this.testSubject.count();
//        });


    }

    @Test
    void unitTestDeleteDataSharingAgreement(){

        DataSharingParty dsp = DataSharingParty.builder()
                .description("Test Prov desc")
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name("Test Prov")
                .departmentName("Test Prov dept")
                .url("www.prov.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(dsp)
                .build();
        this.testSubject.save(customer);

        DataSharingAgreement dsa1 = DataSharingAgreement.builder()
                .name("Test DSA 1")
                .accountHolder(customer)
                .build();
        this.dsaRepo.save(dsa1);

        DataSharingAgreement dsa2 = DataSharingAgreement.builder()
                .name("Test DSA 2")
                .accountHolder(customer)
                .build();
        this.dsaRepo.save(dsa2);

        Long dsaId1 = dsa1.getId();

        assertEquals(2, this.dsaRepo.count());

        customer.deleteDataSharingAgreement(dsa1);

        assertEquals(1, this.dsaRepo.count());
        assertFalse(this.dcdRepo.existsById(dsaId1));

    }

    @Test
    void unitTestDeleteUserAccount(){

        DataSharingParty dsp = DataSharingParty.builder()
                .description("Test Prov desc")
                .build();

        CustomerAccount customer = CustomerAccount.builder()
                .name("Test Prov")
                .departmentName("Test Prov dept")
                .url("www.prov.com")
                .branchName(this.buName)
                .address(new Address())
                .dataSharingParty(dsp)
                .build();
        this.testSubject.save(customer);

        Permission permission = Permission.builder()
                .name(PermissionType.USER_READ)
                .build();
        this.permissionRepo.save(permission);

        Role role = Role.builder()
                .name(RoleType.USER)
                .permissions(List.of(permission))
                .build();
        this.roleRepo.save(role);

        UserAccount user1 = UserAccount.builder()
                .parentAccount(customer)
                .firstName("Test first name 1")
                .lastName("Test last name 1")
                .contactNumber("99999999")
                .email("test1@email.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(List.of(role))
                .build();
        this.userRepo.save(user1);

        UserAccount user2 = UserAccount.builder()
                .parentAccount(customer)
                .firstName("Test first name 2")
                .lastName("Test last name 2")
                .contactNumber("99999999")
                .email("test2@email.com")
                .password(new BCryptPasswordEncoder().encode("67891"))
                .roles(List.of(role))
                .build();
        this.userRepo.save(user2);


        Long userId1 = user1.getId();

        assertEquals(2, this.userRepo.count());

        customer.deleteUserAccount(user1);

        assertEquals(1, this.userRepo.count());
        assertFalse(this.userRepo.existsById(userId1));

    }

    @Test
    void unitTestAddDataSharingPartner() {

        // Given

        // Create provider DSP and Customer 0
        DataSharingParty dsp0
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust0 = CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Test Prov dept")
                .url("www.cust0.com")
                .dataSharingParty(dsp0)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust0);

        // Create provider DSP and Customer 1
        DataSharingParty dsp1 = DataSharingParty.builder()
                .build();

        CustomerAccount cust1= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Test Prov dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp1)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust1);

        // Create provider DSP and Customer 2
        DataSharingParty dsp2
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust2= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp2)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust2);

        // Create provider DSP and Customer 3
        DataSharingParty dsp3
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust3= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp0)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust3);

        assertEquals(0, cust0.getDataSharingPartners().size());

        cust0.addDataSharingPartner(cust1.getDataSharingParty());
        cust0.addDataSharingPartner(cust2.getDataSharingParty());
        cust0.addDataSharingPartner(cust3.getDataSharingParty());

        assertEquals(3, cust0.getDataSharingPartners().size());


    }

    @Test
    void unitTestRemoveDataSharingPartner() {

        // Given

        // Create provider DSP and Customer 0
        DataSharingParty dsp0
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust0= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp0)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust0);

        // Create provider DSP and Customer 1
        DataSharingParty dsp1
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust1= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp1)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust1);

        // Create provider DSP and Customer 2
        DataSharingParty dsp2
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust2= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp2)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust2);

        // Create provider DSP and Customer 3
        DataSharingParty dsp3
                = DataSharingParty.builder()
                .build();

        CustomerAccount cust3= CustomerAccount.builder()
                .name("Mid and South Essex NHS Foundation Trust")
                .departmentName("Dept")
                .url("www.nhs.uk")
                .dataSharingParty(dsp0)
                .branchName("Test BU")
                .build();
        this.testSubject.save(cust3);

        assertEquals(0, cust0.getDataSharingPartners().size());

        cust0.addDataSharingPartner(cust1.getDataSharingParty());
        cust0.addDataSharingPartner(cust2.getDataSharingParty());
        cust0.addDataSharingPartner(cust3.getDataSharingParty());

        assertEquals(3, cust0.getDataSharingPartners().size());
        assertTrue(cust0.getDataSharingPartners().contains(
                cust2.getDataSharingParty()));

        Long custId2 = cust2.getId();

        cust0.removeDataSharingPartner(cust2.getDataSharingParty());

        // Then
        //Assert partner DSP is removed from Customer
        assertEquals(2, cust0.getDataSharingPartners().size());
        assertFalse(cust0.getDataSharingPartners().contains(
                cust2.getDataSharingParty()));
        //Assert DSP is NOT removed from repo
        assertTrue(this.dspRepo.existsById(custId2));
        assertEquals(3, this.dspRepo.count());

    }


}