package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.DataContentType;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataFlowRepositoryTest {

    @Autowired
    DataFlowRepository testSubject;

    @Autowired
    DataSharingPartyRepository organisationRepo;

    @Autowired
    DataSharingAgreementRepository dsaRepo;

    @Autowired
    CustomerAccountRepository customerRepo;

    @Autowired
    DataContentDefinitionRepository dcdRepo;

    @BeforeEach
    void setUp() {
        //
    }

    @AfterEach
    void tearDown() {
        //
    }

    @Test
    void testSave_MinimalDataset(){

        // Given

        //Create Customer A DataSharingParty
        DataSharingParty prov = DataSharingParty.builder()
                .description("Test DSP A description")
                .build();

        // Create a DataSharingAgreement with minimal dataset to add to DataFlow
        CustomerAccount cust1 = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust1.com")
                .branchName("BU 1")
                .dataSharingParty(prov)
                .build();
        this.customerRepo.save(cust1);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Test DCD A")
                .description("Test DCD A description")
                .provider(prov)
                .ownerEmail("someone@email.com")
                .sourceSystem("System A")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA A")
                .accountHolder(cust1)
                .build();
        Long dsaId = this.dsaRepo.save(dsa).getId();

        // Create a consumer DataSharingParty with minimal dataset to add to DataFlow
        DataSharingParty cons = DataSharingParty.builder()
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Test DSP B")
                .departmentName("Test DSP B Dept")
                .url("www.cust2.com")
                .branchName("BU 2")
                .dataSharingParty(cons)
                .build();
        this.customerRepo.save(cust2);

        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing("Data Flow A purpose")
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd))
                .build();

        // When - Data Flow is added to repository.
        Long dfId = this.testSubject.save(dataFlow).getId();

        // Then

        // Assertion: repository should contain one DataFlow.
        assertEquals(1, this.testSubject.count());
        // Assertion: DataFlow should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dfId));

        // Assertion: saved DataFlow should return default is personal data.
        assertEquals(false, dataFlow.getIsPersonalData());
        // Assertion: saved DataFlow should return default is special category.
        assertEquals(false, dataFlow.getIsSpecialCategoryData());
        // // Assertion: saved DataFlow should return default start date.
        assertEquals(LocalDate.now(), dataFlow.getStartDate());
        // Assertion: saved DataFlow should return DataSharingAgreement id.
        assertEquals(dsaId, dataFlow.getDataSharingAgreement().getId());
        // Assertion: saved DataFlow should return provider DataSharingParty id.
        assertEquals(cust1.getDataSharingParty().getId(), dataFlow.getProvider().getId());


    }

    @Test
    void testSave_MaximalDataset(){

        // Test adding dataflow to repository with maximal dataset

        // Given

        // Create a provider DataSharingParty with minimal dataset to add to DataFlow
        DataSharingParty prov = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create a DataSharingAgreement with minimal dataset to add to DataFlow
        CustomerAccount cust1 = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust1);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .name("Test DSA")
                .accountHolder(cust1)
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();
        this.dsaRepo.save(dsa).getId();


        // Create a consumer DataSharingParty with minimal dataset to add to DataFlow
        DataSharingParty cons = DataSharingParty.builder()
                .description("The Royal Orthopaedic Hospital NHS Foundation Trust")
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Test DSP B")
                .departmentName("Test DSP B Dept")
                .url("www.cust2.com")
                .dataSharingParty(cons)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust2);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Referral")
                .description("Referral letter")
                .provider(prov)
                .dataContentType(DataContentType.ELECTRONIC_DOCUMENT)
                .retentionPeriod(Period.ofYears(7))
                .ownerEmail("someone@email.com")
                .sourceSystem("Some System")
                .build();

        Boolean isSpecialCategoryData = true;
        LocalDate startDate = LocalDate.now().plus(Period.ofDays(5));
        LocalDate endDate = startDate.plus(Period.ofDays(365));
        LawfulBasis lawfulBasis = LawfulBasis.CONSENT;
        String purpose = "To provide healthcare";
        SpecialCategoryData specialCategory = SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA;

        DataFlow dataFlow = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd))
                // Optional data
                .lawfulBasis(lawfulBasis)
                .specialCategory(specialCategory)
                .startDate(startDate)
                .endDate(endDate)
                .purposeOfSharing(purpose)
                .build();
        Long dfId = this.testSubject.save(dataFlow).getId();


        // Then

        // Assertion: repository should contain one DataFlow.
        assertEquals(1, this.testSubject.count());
        // Assertion: DataFlow should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dfId));

        // Assertion: saved DataFlow should return is personal data.
        assertEquals(true, dataFlow.getIsPersonalData());
        // Assertion: saved DataFlow should return lawful basis.
        assertEquals(lawfulBasis, dataFlow.getLawfulBasis());
        // Assertion: saved DataFlow should return is special category.
        assertEquals(false, dataFlow.getIsSpecialCategoryData());
        // Assertion: saved DataFlow should return special category.
        assertEquals(specialCategory, dataFlow.getSpecialCategory());
        // Assertion: saved DataFlow should return start date.
        assertEquals(startDate, dataFlow.getStartDate());
        // Assertion: saved DataFlow should return end date.
        assertEquals(endDate, dataFlow.getEndDate());
        // Assertion: saved DataFlow should return purpose.
        assertEquals(purpose, dataFlow.getPurposeOfSharing());


    }

    @Test
    void testFindDataFlowByProviderId() {

        // Given

        // Create provider DSP, Customer and DCD 1
        DataSharingParty prov1 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust1 = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov1)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust1);

        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .name("DCD1")
                .provider(prov1)
                .description("DCD1 desc")
                .ownerEmail("someone@email.com")
                .sourceSystem("Some System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd1);

        // Create provider DSP, Customer and DCD 2
        DataSharingParty prov2 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Create DataSharingParty and add to repository
        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Test DSP B")
                .departmentName("Test DSP B Dept")
                .url("www.cust2.com")
                .dataSharingParty(prov2)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust2);

        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .name("DCD2")
                .provider(prov2)
                .description("DCD2 desc")
                .ownerEmail("someother@email.com")
                .sourceSystem("Some Other System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd2);

        DataContentDefinition dcd3 = DataContentDefinition.builder()
                .name("DCD3")
                .provider(prov1)
                .description("DCD3 desc")
                .ownerEmail("someone.else@email.com")
                .sourceSystem("Another System")
                .retentionPeriod(Period.ofMonths(12))
                .build();
        this.dcdRepo.save(dcd3);


        // Create consumer DSP and Customer account holder
        DataSharingParty cons = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount acc = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.acc.com")
                .dataSharingParty(cons)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(acc);

        // Create consumer DSA
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .accountHolder(acc)
                .name("DSA")
                .build();
        this.dsaRepo.save(dsa);


        // Create DataFlow 1 with prov1
        DataFlow df1 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov1)
                .consumer(cons)
                .dataContent(List.of(dcd1))
                .build();
        this.testSubject.save(df1);

        // Create DataFlow 2 with prov2
        DataFlow df2 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov2)
                .consumer(cons)
                .dataContent(List.of(dcd2))
                .build();
        this.testSubject.save(df2);

        // Create DataFlow 3 with prov1
        DataFlow df3 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov1)
                .consumer(cons)
                .dataContent(List.of(dcd3))
                .build();
        this.testSubject.save(df3);


        // When - DataFlows are queried by provider
        List<DataFlow> dfs = testSubject.findDataFlowByProviderId(prov1.getId()).orElseThrow();

        // Then
        assertEquals(2, dfs.size());
        assertEquals(prov1.getId(), (dfs.get(0).getProvider().getId()));
        assertTrue(this.dcdRepo.existsById(dfs.get(0).getAssociatedDataContent().get(0).getId()));
        assertEquals(prov1.getId(), (dfs.get(1).getProvider().getId()));
        assertTrue(this.dcdRepo.existsById(dfs.get(1).getAssociatedDataContent().get(0).getId()));

    }

    @Test
    void testFindDataFlowByConsumerId() {

        // Given

        // Create provider DSP, Customer and DCD 1
        DataSharingParty prov1 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust1 = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov1)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust1);

        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .name("DCD1")
                .provider(prov1)
                .description("DCD1 desc")
                .ownerEmail("someone@email.com")
                .sourceSystem("Some System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd1);

        // Create provider DSP, Customer and DCD 2
        DataSharingParty prov2 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Test DSP B")
                .departmentName("Test DSP B Dept")
                .url("www.cust2.com")
                .dataSharingParty(prov2)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust2);

        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .name("DCD2")
                .provider(prov2)
                .description("DCD2 desc")
                .ownerEmail("someone.else@email.com")
                .sourceSystem("Some Other System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd2);

        // Create provider DSP, Customer and DCD 3
        DataSharingParty prov3 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust3 = CustomerAccount.builder()
                .name("Test DSP C")
                .departmentName("Test DSP C Dept")
                .url("www.cust3.com")
                .dataSharingParty(prov3)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust3);

        DataContentDefinition dcd3 = DataContentDefinition.builder()
                .name("DCD3")
                .provider(prov3)
                .description("DCD3 desc")
                .ownerEmail("someother@email.com")
                .sourceSystem("System 3")
                .retentionPeriod(Period.ofYears(25))
                .build();
        this.dcdRepo.save(dcd3);


        // Create consumer DSP and Customer 4
        DataSharingParty cons1 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust4 = CustomerAccount.builder()
                .name("Test DSP D")
                .departmentName("Test DSP D Dept")
                .url("www.cust4.com")
                .dataSharingParty(cons1)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust4);

        // Create consumer DSP and Customer 5
        DataSharingParty cons2 = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust5 = CustomerAccount.builder()
                .name("Test DSP E")
                .departmentName("Test DSP E Dept")
                .url("www.cust5.com")
                .dataSharingParty(cons2)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust5);

        // Create consumer DSA
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .accountHolder(cust4)
                .name("DSA")
                .build();
        this.dsaRepo.save(dsa);


        // Create DataFlow 1 with prov1
        DataFlow df1 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov1)
                .consumer(cons1)
                .dataContent(List.of(dcd1))
                .build();
        this.testSubject.save(df1);

        // Create DataFlow 2 with prov2
        DataFlow df2 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov2)
                .consumer(cons1)
                .dataContent(List.of(dcd2))
                .build();
        this.testSubject.save(df2);

        // Create DataFlow 3 with prov1
        DataFlow df3 = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov3)
                .consumer(cons2)
                .dataContent(List.of(dcd3))
                .build();
        this.testSubject.save(df3);


        // When - DataFlows are queried by provider
        List<DataFlow> dfs = testSubject.findDataFlowByConsumerId(cons1.getId()).orElseThrow();

        // Then
        assertEquals(2, dfs.size());
        assertEquals(cons1.getId(), (dfs.get(0).getConsumer().getId()));
        assertTrue(this.dcdRepo.existsById(dfs.get(0).getAssociatedDataContent().get(0).getId()));
        assertEquals(cons1.getId(), (dfs.get(1).getConsumer().getId()));
        assertTrue(this.dcdRepo.existsById(dfs.get(1).getAssociatedDataContent().get(0).getId()));

    }


    @Test
    void unitTestRemoveDataContentDefinition() {

        // Given

        // Create provider DSP, Customer and DCD 1
        DataSharingParty prov
                = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust1 = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust1.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust1);

        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .name("DCD1")
                .provider(prov)
                .description("DCD1 desc")
                .ownerEmail("someone@email.com")
                .sourceSystem("Some System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd1);

        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .name("DCD2")
                .provider(prov)
                .description("DCD2 desc")
                .ownerEmail("someother@email.com")
                .sourceSystem("Some Other System")
                .retentionPeriod(Period.ofYears(5))
                .build();
        this.dcdRepo.save(dcd2);

        DataContentDefinition dcd3 = DataContentDefinition.builder()
                .name("DCD3")
                .provider(prov)
                .description("DCD3 desc")
                .ownerEmail("someone.else@email.com")
                .sourceSystem("Another System")
                .retentionPeriod(Period.ofDays(100))
                .build();
        this.dcdRepo.save(dcd3);

        // Create consumer DSP and Customer 2
        DataSharingParty cons = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust2 = CustomerAccount.builder()
                .name("Test DSP B")
                .departmentName("Test DSP B Dept")
                .url("www.cust2.com")
                .dataSharingParty(cons)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust2);

        // Create consumer DSA
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .accountHolder(cust1)
                .name("DSA")
                .build();
        this.dsaRepo.save(dsa);


        // Create DataFlow 1 with prov1
        DataFlow dataFlow = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd1, dcd2, dcd3))
                .build();
        this.testSubject.save(dataFlow);

        Long dcdId2 = dcd2.getId();

        assertEquals(3, dataFlow.getAssociatedDataContent().size());
        assertTrue(this.dcdRepo.existsById(dcdId2));

        //When
        dataFlow.removeDataContentDefinition(dcd2);

        // Then
        //Assert DCD is removed from DataFlow
        assertEquals(2, dataFlow.getAssociatedDataContent().size());
        assertFalse(dataFlow.getAssociatedDataContent().contains(dcd2));
        //Assert DCD is NOT removed from repo
        assertTrue(this.dcdRepo.existsById(dcdId2));
        assertEquals(3, this.dcdRepo.count());

    }
}