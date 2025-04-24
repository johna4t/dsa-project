package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataSharingAgreementRepositoryTest {

    @Autowired
    DataSharingAgreementRepository testSubject;

    @Autowired
    DataSharingPartyRepository dspRepo;

    @Autowired
    DataContentDefinitionRepository dcdRepo;

    @Autowired
    DataFlowRepository dataFlowRepo;

    @Autowired
    CustomerAccountRepository customerRepo;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        //
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSave_MinimalDataset() {

        // Test adding DSA to repository with minimal dataset

        // Given

        // Create provider DSP
        DataSharingParty prov = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create a DataSharingAgreement with minimal dataset
        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust)
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();

        // When - DataSharingAgreement is added to repository.
        Long dsaId = this.testSubject.save(dsa).getId();


        // Then

        // Assertion: repository should contain one DSA.
        assertEquals(1, this.testSubject.count());
        // Assertion: DSA should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dsaId));

        DataSharingAgreement savedDsa = this.testSubject.findById(dsaId).get();

        //  Assertion: saved DataSharingAgreement should return default start date.
        assertEquals(LocalDate.now(), savedDsa.getStartDate());
    }

    @Test
    void testSave_MaximalDataset() {

        // Test adding DSA to repository with minimal dataset

        // Given

        // Create provider DSP
        DataSharingParty prov = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create a DataSharingAgreement with minimal dataset
        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        LocalDate startDate = LocalDate.now().plus(Period.ofDays(5));
        LocalDate endDate = startDate.plus(Period.ofDays(365));

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust)
                .controllerRelationship(ControllerRelationship.JOINT)
                // Optional data
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // When - DataSharingAgreement is added to repository.
        Long dsaId = this.testSubject.save(dsa).getId();

        // Then

        // Assertion: repository should contain one DSA.
        assertEquals(1, this.testSubject.count());
        // Assertion: DSA should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dsaId));

        DataSharingAgreement savedDsa = this.testSubject.findById(dsaId).get();

        //  Assertion: saved DataSharingAgreement should return start date.
        assertEquals(startDate, savedDsa.getStartDate());
        //  Assertion: saved DataSharingAgreement should return end date.
        assertEquals(endDate, savedDsa.getEndDate());
    }

    @Test
    void unitTestDeleteDataFlow() {
        // Given

        // Create DataSharingParty to add to DataFlow
        DataSharingParty dsp1 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create a DataSharingAgreement with minimal dataset to add to DataFlow
        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .dataSharingParty(dsp1)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust);

        // Create provider DCD 1
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .provider(cust.getDataSharingParty())
                .name("Prov DCD 1")
                .description("Prov DCD 1 desc.")
                .build();
        this.dcdRepo.save(dcd1);

        // Create provider DCD 2
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .provider(cust.getDataSharingParty())
                .name("Prov DCD 12")
                .description("Prov DCD 2 desc.")
                .build();
        this.dcdRepo.save(dcd2);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust)
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();
        this.testSubject.save(dsa);

        // Create consumer DSP
        DataSharingParty dsp2 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust2")
                .build();

        // Create provider Customer
        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Cust2")
                .departmentName("Cust2 dept")
                .url("www.cust2.com")
                .dataSharingParty(dsp2)
                .branchName("Test BU2")
                .build();
        this.customerRepo.save(cust2);

        // Create DataFlow
        DataFlow dfA = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .providedDcds(List.of(dcd1))
                .provider(cust.getDataSharingParty())
                .consumer(cust2.getDataSharingParty())
                .build();
        this.dataFlowRepo.save(dfA);
        Long dfAId = dfA.getId();

        // Create DataFlow
        DataFlow dfB = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(cust.getDataSharingParty())
                .consumer(cust2.getDataSharingParty())
                .providedDcds(List.of(dcd2))
                .build();
        this.dataFlowRepo.save(dfB);

        assertEquals(2, dsa.getDataFlows().size());
        assertTrue(this.dataFlowRepo.existsById(dfAId));

        // When

        // Remove DataFlow
        dsa.deleteDataFlow(dfA);

        // Then

        //  Assertion: saved DataSharingAgreement should have 1 x DataFlow.
        assertEquals(1, dsa.getDataFlows().size());
        assertFalse(this.dataFlowRepo.existsById(dfAId));

    }

    @Test
    void unitTestDeleteById_CustomerAccountDependency(){
        // Test adding DSA to repository with minimal dataset

        // Given

        // Create provider DSP
        DataSharingParty prov = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create a DataSharingAgreement with minimal dataset
        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();

        this.customerRepo.save(cust);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust)
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();

        // When - DataSharingAgreement is added to repository.
        Long dsaId = this.testSubject.save(dsa).getId();


        // Then

        // Assertion: repository should contain one DSA.
        assertEquals(1, this.testSubject.count());
        // Assertion: DSA should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dsaId));

        this.customerRepo.deleteById(cust.getId());

        assertEquals(0, this.testSubject.count());
    }

}
