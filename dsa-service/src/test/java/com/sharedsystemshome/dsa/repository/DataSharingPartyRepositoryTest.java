package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataSharingPartyRepositoryTest {

    @Autowired
    private DataSharingPartyRepository testSubject;

    @Autowired
    private CustomerAccountRepository customerRepo;

    @Autowired
    private DataContentDefinitionRepository dcdRepo;

    @Autowired
    private DataSharingAgreementRepository dsaRepo;

    @Autowired
    private DataFlowRepository dfRepo;

    @BeforeEach
    void setUpFixture() {

    }

    @Test
    void testSave(){

        // Test adding DSP to repository with minimal dataset

        // Given - create an DataSharingParty
        String name = "Mid and South Essex NHS Foundation Trust";

        DataSharingParty dsp = DataSharingParty.builder()
                .build();

        CustomerAccount cust = CustomerAccount.builder()
                .name(name)
                .departmentName(name + " dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .dataSharingParty(dsp)
                .build();

        this.customerRepo.save(cust);

        // Then

        // Assertion: repository should contain one DSP.
        assertEquals(1, this.testSubject.count());
        // Assertion: DSP should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(cust.getDataSharingParty().getId()));
        // Assertion: saved DSP should return name.
        assertEquals(name, this.testSubject.findById(cust.getDataSharingParty().getId()).get().getName());
    }

    @Test
    void unitTestDeleteById_CustomerAccountDependency(){

        // Test adding DSP to repository with minimal dataset

        // Given - create an DataSharingParty
        String name = "Mid and South Essex NHS Foundation Trust";

        DataSharingParty dsp = DataSharingParty.builder()
                .build();

        CustomerAccount cust = CustomerAccount.builder()
                .name(name)
                .departmentName(name + " dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .dataSharingParty(dsp)
                .build();

        this.customerRepo.save(cust);

        // Then

        // Assertion: repository should contain one DSP.
        assertEquals(1, this.testSubject.count());

        this.customerRepo.deleteById(cust.getId());

        // Assertion: repository should contain no DSP.
        assertEquals(0, this.testSubject.count());

    }

    @Test
    void unitTestDeleteDataContentDefinition(){

        // Test adding DSP to repository with minimal dataset

        // Given - create an DataSharingParty
        String name = "Mid and South Essex NHS Foundation Trust";

        DataSharingParty dsp = DataSharingParty.builder()
                .build();

        CustomerAccount cust = CustomerAccount.builder()
                .name(name)
                .departmentName(name + " dept")
                .url("www.cust.com")
                .branchName("Test BU")
                .dataSharingParty(dsp)
                .build();

        this.customerRepo.save(cust);

        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .provider(dsp)
                .name("Test DCD1")
                .dataContentType(DataContentType.NOT_SPECIFIED)
                .description("Test DCD1 desc")
                .build();
        this.dcdRepo.save(dcd1);
        Long dcdId1 = dcd1.getId();

        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .provider(dsp)
                .name("Test DCD2")
                .dataContentType(DataContentType.ELECTRONIC_DOCUMENT)
                .description("Test DCD2 desc")
                .build();
        this.dcdRepo.save(dcd2);

        // Then

        // Assertion: repository should contain one DSP.
        assertEquals(2, this.dcdRepo.count());
        assertTrue(this.dcdRepo.existsById(dcdId1));

        dsp.deleteDataContentDefinition(dcd1);

        // Assertion: repository should contain no DSP.
        assertEquals(1, this.dcdRepo.count());
        assertFalse(this.dcdRepo.existsById(dcdId1));

    }

}