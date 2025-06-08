package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SharedDataContentRepositoryTest {

    @Autowired
    SharedDataContentRepository testSubject;

    @Autowired
    DataSharingPartyRepository dspRepo;

    @Autowired
    CustomerAccountRepository customerRepo;

    @Autowired
    DataContentDefinitionRepository dcdRepo;

    @Autowired
    DataSharingAgreementRepository dsaRepo;

    @Autowired
    DataFlowRepository dataFlowRepo;

    @BeforeEach
    void setUp() {
        //
    }

    @AfterEach
    void tearDown() {
        //
    }

    @Test
    void testSaveDataFlowToCreateSharedDataContent(){

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

        String dcdName = "Test DCD A";
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name(dcdName)
                .description("Test DCD description")
                .provider(prov)
                .ownerEmail("someone@email.com")
                .sourceSystem("Producer System")
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

        String purpose = "Data Flow A purpose";
        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing(purpose)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd))
                .build();
        this.dataFlowRepo.save(dataFlow);

        SharedDataContent sdc = dataFlow.getAssociatedDataContent().get(0);

        // Assertion: repository should contain one SDC.
        assertEquals(1, this.testSubject.count());
        // Assertion: SDC should exist by id in repository.
        assertTrue(this.testSubject.existsById(sdc.getId()));

        // Assertion: SDC should return DCD name.
        assertEquals(dcdName, sdc.getDataContentDefinition().getName());
        // // Assertion: SDC should return DataFlow purpose.
        assertEquals(purpose, sdc.getDataFlow().getPurposeOfSharing());

    }

    @Test
    void testDeleteSdcUsingAssociatedDataContent(){

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

        String dcdName = "Test DCD A";
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name(dcdName)
                .description("Test DCD description")
                .provider(prov)
                .ownerEmail("someone@email.com")
                .sourceSystem("Producer System")
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

        String purpose = "Data Flow A purpose";
        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing(purpose)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd))
                .build();
        this.dataFlowRepo.save(dataFlow);

        Long sdcId = dataFlow.getAssociatedDataContent().get(0).getId();

        // Assertion: repository should contain one SDC.
        assertEquals(1, this.testSubject.count());
        // Assertion: SDC should exist by id in repository.
        assertTrue(this.testSubject.existsById(sdcId));

        // Get SDC to delete from associated data content
        SharedDataContent sdcToDelete = dataFlow.getAssociatedDataContent().stream()
                .filter(sdc -> sdc.getDataContentDefinition().equals(dcd))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching SDC"));

        // Break both sides
        sdcToDelete.setDataFlow(null);
        sdcToDelete.setDataContentDefinition(null);


        dataFlow.getAssociatedDataContent().remove(sdcToDelete);
        dcd.getAssociatedDataFlows().remove(sdcToDelete);

        // Delete
        this.testSubject.deleteById(sdcId);

        // Optionally save df and dcd
        dataFlowRepo.save(dataFlow);
        dcdRepo.save(dcd);

        // Assertion: repository should contain no SDC.
        assertEquals(0, this.testSubject.count(), "SDC repo is empty");
        // Assertion: SDC should not exist by id in repository.
        assertFalse(this.testSubject.existsById(sdcId), "SDC does not exist in SDC repo");

        // Assertion: DCD should still exist.
        assertTrue(dcdRepo.existsById(dcd.getId()), "DCD exists");
        assertEquals(dcdName, dcd.getName(), "DCD has correct name value");

        // Assertion: DataFlow should still exist.
        assertTrue(dataFlowRepo.existsById(dataFlow.getId()), "DataFlow exists");
        assertEquals(purpose, dataFlow.getPurposeOfSharing(), "DataFlow has correct purpose value");

    }

    @Test
    void testDeleteSdcUsingAssociatedDataFlows(){

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

        String dcdName = "Test DCD A";
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name(dcdName)
                .description("Test DCD description")
                .provider(prov)
                .ownerEmail("someone@email.com")
                .sourceSystem("Producer System")
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

        String purpose = "Data Flow A purpose";
        DataFlow dataFlow = DataFlow.builder()
                .purposeOfSharing(purpose)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .dataContent(List.of(dcd))
                .build();
        this.dataFlowRepo.save(dataFlow);

        Long sdcId = dataFlow.getAssociatedDataContent().get(0).getId();

        // Assertion: repository should contain one SDC.
        assertEquals(1, this.testSubject.count());
        // Assertion: SDC should exist by id in repository.
        assertTrue(this.testSubject.existsById(sdcId));

        // Get SDC to delete from associated data flows
        SharedDataContent sdcToDelete = dcd.getAssociatedDataFlows().stream()
                .filter(sdc -> sdc.getDataFlow().equals(dataFlow))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching SDC"));

        // Break both sides
        sdcToDelete.setDataFlow(null);
        sdcToDelete.setDataContentDefinition(null);


        dataFlow.getAssociatedDataContent().remove(sdcToDelete);
        dcd.getAssociatedDataFlows().remove(sdcToDelete);

        // Delete
        this.testSubject.deleteById(sdcId);

        // Optionally save df and dcd
        dataFlowRepo.save(dataFlow);
        dcdRepo.save(dcd);

        // Assertion: repository should contain no SDC.
        assertEquals(0, this.testSubject.count(), "SDC repo is empty");
        // Assertion: SDC should not exist by id in repository.
        assertFalse(this.testSubject.existsById(sdcId), "SDC does not exist in SDC repo");

        // Assertion: DCD should still exist.
        assertTrue(dcdRepo.existsById(dcd.getId()), "DCD exists");
        assertEquals(dcdName, dcd.getName(), "DCD has correct name value");

        // Assertion: DataFlow should still exist.
        assertTrue(dataFlowRepo.existsById(dataFlow.getId()), "DataFlow exists");
        assertEquals(purpose, dataFlow.getPurposeOfSharing(), "DataFlow has correct purpose value");

    }
}
