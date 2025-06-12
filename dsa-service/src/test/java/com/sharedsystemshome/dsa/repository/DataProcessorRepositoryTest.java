package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.ProcessingAccreditationStandard;
import com.sharedsystemshome.dsa.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Period;
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
        DataProcessorAccreditation iso22301 = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.ISO_IEC_22301)
                .build();
        DataProcessorAccreditation cyber = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .email(email)
                .accreditations(List.of(iso22301, cyber))
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

        var saved = this.testSubject.findById(dpId).orElseThrow();
        assertEquals("Test DP A", saved.getName());
        assertEquals("contact@dpa.com", saved.getEmail());
        assertEquals("www.dpa.com", saved.getWebsite());
        assertEquals("Test DA desc.", saved.getDescription());

        // Assert 2 accreditations linked
        assertEquals(2, saved.getAccreditations().size());

        // Assert correct standards
        assertTrue(saved.getAccreditations().stream()
                .anyMatch(a -> a.getName() == ProcessingAccreditationStandard.ISO_IEC_22301));
        assertTrue(saved.getAccreditations().stream()
                .anyMatch(a -> a.getName() == ProcessingAccreditationStandard.CYBER_ESSENTIALS));

        // Assert correct back-references
        saved.getAccreditations().forEach(a -> assertEquals(dpId, a.getDataProcessor().getId()));

    }

    @Test
    void testSave_WithDuplicateAccreditations() {
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

        // Create one accreditation standard, duplicated
        ProcessingAccreditationStandard standard = ProcessingAccreditationStandard.CYBER_ESSENTIALS;

        DataProcessorAccreditation a1 = DataProcessorAccreditation.builder()
                .name(standard)
                .build();

        DataProcessorAccreditation a2 = DataProcessorAccreditation.builder()
                .name(standard) // duplicate
                .build();

        DataProcessor dp = DataProcessor.builder()
                .name("DP with Duplicates")
                .email("email@test.com")
                .description("duplicate test")
                .controller(controller)
                .website("www.test.com")
                .accreditations(List.of(a1, a2))
                .build();

        // Expect DataIntegrityViolationException due to UNIQUE constraint
        assertThrows(DataIntegrityViolationException.class, () -> {
            this.testSubject.saveAndFlush(dp); // force immediate flush to catch DB constraint error
        });
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
        // Assert 0 accreditations
        assertEquals(0, saved.getAccreditations().size());
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
    void testAddAccreditation() {

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

        DataProcessorAccreditation acc1 = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessorAccreditation acc2 = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.COBIT)
                .build();

        dp.addAccreditation(acc1);
        dp.addAccreditation(acc2);

        dp = this.testSubject.saveAndFlush(dp);

        DataProcessor saved = this.testSubject.findById(dp.getId()).get();

        assertEquals(2, saved.getAccreditations().size());

    }

    @Test
    void testRemoveAccreditation() {

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

        DataProcessorAccreditation acc1 = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessorAccreditation acc2 = DataProcessorAccreditation.builder()
                .name(ProcessingAccreditationStandard.COBIT)
                .build();

        dp.addAccreditation(acc1);
        dp.addAccreditation(acc2);

        DataProcessor saved = this.testSubject.saveAndFlush(dp);

        assertEquals(2, saved.getAccreditations().size());

        // Remove accreditation
        dp.removeAccreditation(acc1);
        DataProcessor updated =this.testSubject.saveAndFlush(dp);

        // Reload and confirm it's removed
        assertEquals(1, updated.getAccreditations().size());

        boolean acc1Exists = updated.getAccreditations().stream()
                .anyMatch(a -> a.getName() == acc1.getName());
        boolean acc2Exists = updated.getAccreditations().stream()
                .anyMatch(a -> a.getName() == acc2.getName());

        assertFalse(acc1Exists, acc1.getName() + " should have been removed.");
        assertTrue(acc2Exists, acc2.getName() + " should still be present.");

    }

}


