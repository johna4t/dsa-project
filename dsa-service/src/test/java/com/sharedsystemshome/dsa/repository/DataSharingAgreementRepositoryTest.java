package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import jakarta.transaction.TransactionScoped;
import org.junit.Ignore;
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
import java.util.Iterator;
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
    SharedDataContentRepository sdcRepo;

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

    @Ignore
    void testDeleteDataFlowWithOrphanCleanup() {

        // TODo - consider removing DataSharingAgreement::deleteDataFlow(DataFlow dataFlow)

        // Given

        // Create DataSharingParty and Customer
        DataSharingParty dsp1 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();
        CustomerAccount cust = CustomerAccount.builder()
                .name("Cust")
                .departmentName("Cust dept")
                .url("www.cust.com")
                .dataSharingParty(dsp1)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust);

        // Create DataContentDefinitions
        DataContentDefinition dcd1 = dcdRepo.save(DataContentDefinition.builder()
                .provider(dsp1)
                .name("DCD1")
                .ownerEmail("someone@email.com")
                .sourceSystem("System 1")
                .retentionPeriod(Period.ofYears(5))
                .build());

        DataContentDefinition dcd2 = dcdRepo.save(DataContentDefinition.builder()
                .provider(dsp1)
                .name("DCD2")
                .ownerEmail("someoneelse@email.com")
                .sourceSystem("System 2")
                .retentionPeriod(Period.ofYears(2))
                .build());

        // Create DataSharingAgreement
        DataSharingAgreement dsa = this.testSubject.save(DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust)
                .controllerRelationship(ControllerRelationship.JOINT)
                .build());

        // Create Consumer
        DataSharingParty dsp2 = DataSharingParty.builder()
                .description("Consumer NHS Trust")
                .build();
        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Cust2")
                .departmentName("Cust2 dept")
                .url("www.cust2.com")
                .dataSharingParty(dsp2)
                .branchName("Test BU2")
                .build();
        this.customerRepo.save(cust2);

        // Create DataFlow A
        DataFlow dfA = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .dataContent(List.of(dcd1))
                .provider(dsp1)
                .consumer(dsp2)
                .build();
        dfA = dataFlowRepo.save(dfA);
        Long dfAId = dfA.getId();

        // Create DataFlow B
        DataFlow dfB = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .dataContent(List.of(dcd2))
                .provider(dsp1)
                .consumer(dsp2)
                .build();
        dataFlowRepo.save(dfB);

        // Validate both DataFlows exist
        assertEquals(2, dataFlowRepo.count());
        assertTrue(dataFlowRepo.existsById(dfAId));

        Boolean exists = true;
        // --- Delete associated SharedDataContent explicitly
        List<SharedDataContent> orphans = new ArrayList<>(dfA.getAssociatedDataContent());
        for (SharedDataContent sdc : orphans) {
            Long id = sdc.getId();
            sdcRepo.deleteById(id);
            sdcRepo.flush();
            exists = sdcRepo.existsById(id);
        }

        dataFlowRepo.save(dfA);

        // --- Now delete dfA
        dataFlowRepo.deleteById(dfAId);
        // this.testSubject.save(dsa);

        // --- Then
        assertFalse(dataFlowRepo.existsById(dfAId));
        assertEquals(1, dataFlowRepo.count());
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
