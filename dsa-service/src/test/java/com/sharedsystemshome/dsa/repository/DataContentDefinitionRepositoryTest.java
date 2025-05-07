package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.enums.DataContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataContentDefinitionRepositoryTest {

    @Autowired
    DataContentDefinitionRepository testSubject;

    @Autowired
    DataSharingPartyRepository dspRepo;

    @Autowired
    CustomerAccountRepository customerRepo;

    @BeforeEach
    void setUp() {
        //
    }

    @AfterEach
    void tearDown() {
        //
    }

    @Test
    void testSave(){

        // Test adding dataflow to repository with minimal dataset

        // Create a provider DataSharingParty with minimal dataset
        DataSharingParty prov = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Given
        CustomerAccount cust = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust.com")
                .dataSharingParty(prov)
                .branchName("Test BU")
                .build();
        Long custId = this.customerRepo.save(cust).getId();

        String name = "Referral";
        String desc = "Referral letter";
        DataContentDefinition dcd = DataContentDefinition.builder()
                .provider(prov)
                .name(name)
                .description(desc)
                .provider(prov)
                .dataContentType(DataContentType.NOT_SPECIFIED)
                .retentionPeriod(Period.ofYears(5))
                .ownerEmail("someone@email.com")
                .ownerName("Some One")
                .sourceSystem("Data Source")
                .build();

        DataContentPerspective dcp = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", "CONTRACT",
                                "specialCategory", "NOT_SPECIAL_CATEGORY_DATA"
                        )
                )
                .dataContentDefinition(dcd)
                .build();

        // When DCD is added to repository.
        Long dcdId = this.testSubject.save(dcd).getId();

        // Then

        // Assertion: repository should contain one DataFlow.
        assertEquals(1, this.testSubject.count());
        // Assertion: DataFlow should exist by id returned by save method.
        assertTrue(this.testSubject.existsById(dcdId));

        // Assertion: saved DCD should return supplied name.
        assertEquals(name, dcd.getName());
        // // Assertion: saved DCD should return supplied description.
        assertEquals(desc, dcd.getDescription());
        // Assertion: saved DCD should return default content type.
        assertEquals(DataContentType.NOT_SPECIFIED, dcd.getDataContentType());
        // Assertion: saved DCD should return provider DataSharingParty id.
        assertEquals(custId, dcd.getProvider().getId());

    }

    @Test
    void testSaveAndLoadDataContentDefinition_withGdprPerspective() {
        DataSharingParty dsp = DataSharingParty.builder()
                .description("Repo Test DSP")
                .build();

        // Create and persist a customer account
        CustomerAccount account = CustomerAccount.builder()
                .name("Test Customer")
                .dataSharingParty(dsp)
                .build();
        customerRepo.save(account);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Repo DCD with GDPR")
                .provider(dsp)
                .retentionPeriod(Period.ofYears(5))
                .dataContentType(DataContentType.NOT_SPECIFIED)
                .retentionPeriod(Period.ofYears(5))
                .ownerEmail("someone@email.com")
                .ownerName("Some One")
                .build();

        DataContentPerspective dcp = new DataContentPerspective();
        dcp.setMetadataScheme(MetadataScheme.GDPR);
        dcp.setMetadata(Map.of(
                "lawfulBasis", "CONTRACT",
                "specialCategory", "NOT_SPECIAL_CATEGORY_DATA"
        ));
        dcd.addPerspective(dcp);

        this.testSubject.save(dcd);

        DataContentDefinition saved = this.testSubject.findById(dcd.getId()).orElseThrow();
        assertEquals(1, saved.getPerspectives().size());

        DataContentPerspective savedDcp = saved.getPerspectives().get(0);
        assertEquals(MetadataScheme.GDPR, savedDcp.getMetadataScheme());
        assertEquals("CONTRACT", savedDcp.get("lawfulBasis"));
    }

}
