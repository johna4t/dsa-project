package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.ProcessingCertificationStandard;
import com.sharedsystemshome.dsa.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataProcessorRepositoryTest {

    @Autowired
    DataProcessorRepository testSubject;

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
    void testSave() {


        // Create a provider DataSharingParty with minimal dataset
        DataSharingParty controller = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        // Given
        CustomerAccount cust = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust.com")
                .dataSharingParty(controller)
                .branchName("Test BU")
                .build();
        Long custId = this.customerRepo.save(cust).getId();

        String name = "Test DP A";
        String desc = "Test DA desc.";
        String email = "contact@dpa.com";
        String url = "www.dpa.com";

        DataProcessor dp = DataProcessor.builder()
                .email(email)
                .certifications(List.of(
                        ProcessingCertificationStandard.ISO_IEC_22301,
                        ProcessingCertificationStandard.CYBER_ESSENTIALS))
                .website(url)
                .controller(controller)
                .description(desc)
                .name(name)
                .build();

        // When Processor is added to repository.
        Long dpId = this.testSubject.save(dp).getId();

        // Then

        // Assert processor exists
        assertEquals(1, this.testSubject.count());
        assertTrue(this.testSubject.existsById(dpId));

        DataProcessor saved = this.testSubject.findById(dpId).orElseThrow();
        assertEquals("Test DP A", saved.getName());
        assertEquals("contact@dpa.com", saved.getEmail());
        assertEquals("www.dpa.com", saved.getWebsite());
        assertEquals("Test DA desc.", saved.getDescription());

        // Assert 2 certifications linked
        assertEquals(2, saved.getCertifications().size());

        // Assert correct standards
        assertTrue(saved.getCertifications().stream()
                .anyMatch(a -> a == ProcessingCertificationStandard.ISO_IEC_22301));
        assertTrue(saved.getCertifications().stream()
                .anyMatch(a -> a == ProcessingCertificationStandard.CYBER_ESSENTIALS));

    }

    @Test
    void testSave_WithDuplicateCertifications() {
        // Setup controller
        DataSharingParty controller = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust.com")
                .dataSharingParty(controller)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust);

        // Create one certification standard, duplicated
        ProcessingCertificationStandard standard = ProcessingCertificationStandard.CYBER_ESSENTIALS;

        DataProcessorCertification a1 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(standard)
                .build();

        DataProcessorCertification a2 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(standard) // duplicate
                .build();

        DataProcessor dp = DataProcessor.builder()
                .name("DP with Duplicates")
                .email("email@test.com")
                .description("duplicate test")
                .controller(controller)
                .website("www.test.com")
                .certifications(List.of(
                        ProcessingCertificationStandard.CYBER_ESSENTIALS,
                        ProcessingCertificationStandard.CYBER_ESSENTIALS))
                .build();

        // When Processor is added to repository.
        Long dpId = this.testSubject.save(dp).getId();

        // Expect only one in the list added
        assertEquals(1, this.testSubject.count());

        DataProcessor saved = this.testSubject.findById(dpId).orElseThrow();
        assertEquals(ProcessingCertificationStandard.CYBER_ESSENTIALS, saved.getCertifications().get(0));

    }

    @Test
    void testSave_WithMinimalFields() {

        // Setup controller
        DataSharingParty controller = DataSharingParty.builder()
                .description("Mid and South Essex NHS Foundation Trust")
                .build();

        CustomerAccount cust = CustomerAccount.builder()
                .name("Test DSP A")
                .departmentName("Test DSP A Dept")
                .url("www.cust.com")
                .dataSharingParty(controller)
                .branchName("Test BU")
                .build();
        this.customerRepo.save(cust);

        DataProcessor dp = DataProcessor.builder()
                .controller(controller)
                .name("Minimum DP")
                .email("min@example.com")
                .build();

        Long id = this.testSubject.save(dp).getId();
        assertTrue(this.testSubject.existsById(id));

        DataProcessor saved = this.testSubject.findById(id).orElseThrow();
        // Assert 0 certifications
        assertEquals(0, saved.getCertifications().size());
    }

    @Test
    void testSave_WithMissingRequiredFields() {

        DataProcessor dp = new DataProcessor();

        Exception ex = assertThrows(Exception.class, () -> {
            this.testSubject.saveAndFlush(dp);
        });

        assertEquals(ConstraintViolationException.class.getName(), ex.getClass().getName());

    }

    @Test
    void testAddCertification() {

        DataSharingParty controller = DataSharingParty.builder().description("Test Org").build();

        this.customerRepo.save(CustomerAccount.builder()
                .name("Test Org")
                .departmentName("Digital Services")
                .url("https://example.org")
                .branchName("London Office") // optional
                .dataSharingParty(controller)
                .build());

        DataProcessor dp = DataProcessor.builder()
                .name("DP Test")
                .email("test@dp.com")
                .controller(controller)
                .build();

        dp.addCertification(ProcessingCertificationStandard.CYBER_ESSENTIALS);
        dp.addCertification(ProcessingCertificationStandard.COBIT);

        dp = this.testSubject.saveAndFlush(dp);

        DataProcessor saved = this.testSubject.findById(dp.getId()).get();

        assertEquals(2, saved.getCertifications().size());

    }

    @Test
    void testRemoveCertification() {

        DataSharingParty controller = DataSharingParty.builder().description("Test Org").build();

        this.customerRepo.save(CustomerAccount.builder()
                .name("Test Org")
                .departmentName("Digital Services")
                .url("https://example.org")
                .branchName("London Office") // optional
                .dataSharingParty(controller)
                .build());

        DataProcessor dp = DataProcessor.builder()
                .name("DP Test")
                .email("test@dp.com")
                .controller(controller)
                .build();

        dp.addCertification(ProcessingCertificationStandard.CYBER_ESSENTIALS);
        dp.addCertification(ProcessingCertificationStandard.COBIT);

        DataProcessor saved = this.testSubject.saveAndFlush(dp);

        assertEquals(2, saved.getCertifications().size());

        // Remove certification
        dp.removeCertification(ProcessingCertificationStandard.CYBER_ESSENTIALS);
        DataProcessor updated =this.testSubject.saveAndFlush(dp);

        // Reload and confirm it's removed
        assertEquals(1, updated.getCertifications().size());

        boolean acc1Exists = updated.getCertifications().stream()
                .anyMatch(a -> a == ProcessingCertificationStandard.CYBER_ESSENTIALS);
        boolean acc2Exists = updated.getCertifications().stream()
                .anyMatch(a -> a == ProcessingCertificationStandard.COBIT);

        assertFalse(acc1Exists, ProcessingCertificationStandard.CYBER_ESSENTIALS + " should have been removed.");
        assertTrue(acc2Exists, ProcessingCertificationStandard.COBIT + " should still be present.");

    }

}


