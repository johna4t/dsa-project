package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.enums.*;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.repository.DataProcessorRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DataProcessorServiceTest {

    @Mock
    private DataProcessorRepository dpMockRepo;

    @Mock
    private DataSharingPartyRepository dspMockRepo;

    private DataProcessorService dpService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dpService = new DataProcessorService(
                this.dpMockRepo,
                this.dspMockRepo
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateDataProcessor() {

        Long conId = 1L;
        DataSharingParty con = DataSharingParty.builder()
                .id(conId)
                .build();
        when(this.dspMockRepo.existsById(conId)).thenReturn(true);

        DataProcessorCertification iso22301 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.ISO_IEC_22301)
                .build();
        DataProcessorCertification cyber = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .name("Test DCD")
                .email("someone@email.com")
                .controller(con)
                .certifications(List.of(iso22301, cyber))
                .build();

        Long dpId = 1L;

        when(this.dpMockRepo.save(dp)).thenAnswer(invocation -> {
            DataProcessor input = invocation.getArgument(0);
            input.setId(dpId); // simulate ID assignment
            return input;
        });

        Long result = dpService.createDataProcessor(dp);

        assertNotNull(result);
        assertEquals(dpId, result);
        verify(this.dspMockRepo, times(1)).existsById(conId);
        verify(this.dpMockRepo, times(1)).save(dp);
    }

    @Test
    public void testCreateDataProcessor_ConIdIsNull() {

        DataSharingParty con = DataSharingParty.builder()
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(1L)
                .name("Test DCD")
                .email("someone@email.com")
                .controller(con)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dpService.createDataProcessor(dp));
        assertEquals("Controller Data Sharing Party id is null or empty.", e.getMessage());

        verify(this.dspMockRepo, times(0)).save(con);
    }

    @Test
    public void testCreateDataProcessor_ConIdIsInvalid() {

        Long conId = 1L;
        DataSharingParty con = DataSharingParty.builder()
                .id(conId)
                .build();
        when(this.dspMockRepo
                .existsById(conId)).thenReturn(false);

        DataProcessor dp = DataProcessor.builder()
                .id(1L)
                .name("Test DCD")
                .email("someone@email.com")
                .controller(con)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dpService.createDataProcessor(dp));
        assertEquals("Data Sharing Party with id = " + conId + " not found.", e.getMessage());

        verify(this.dspMockRepo, times(1)).existsById(conId);
        verify(this.dpMockRepo, times(0)).save(any());
    }

    @Test
    public void testGetDataProcessorById_Exists() {
        Long dpId = 1L;
        DataProcessor dp = DataProcessor.builder().build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        DataProcessor result = this.dpService.getDataProcessorById(dpId);

        assertNotNull(result);
        assertEquals(dp, result);
        verify(this.dpMockRepo, times(1)).findById(dpId);
    }

    @Test
    public void testGetDataProcessorById_WithInvalidDcdId() {
        Long dpId = anyLong();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dpService.getDataProcessorById(dpId));
        assertEquals("Data Processor with id = " + dpId + " not found.", e.getMessage());
        verify(this.dpMockRepo, times(1)).findById(dpId);
    }

    @Test
    void testGetDataProcessors() {
        Long conId = 1L;

        DataSharingParty con = DataSharingParty.builder()
                .id(conId)
                .description("Service Test DSP")
                .build();


        DataProcessorCertification acc1 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();
        DataProcessor dp1 = DataProcessor.builder()
                .name("DP1")
                .controller(con)
                .certifications(List.of(acc1))
                .build();


        DataProcessorCertification acc2 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.ISO_IEC_20000_1)
                .build();
        DataProcessor dp2 = DataProcessor.builder()
                .name("DP2")
                .controller(con)
                .certifications(List.of(acc1,acc2))
                .build();


        List<DataProcessor> dps = List.of(dp1, dp2);
        when(this.dpMockRepo.findByControllerId(conId)).thenReturn(dps);

        // When: the controller method is called
        List<DataProcessor> result = dpService.getDataProcessors(conId);

        // Then expect...
        assertEquals(2, result.size(), "Method returns two processors");

        verify(this.dpMockRepo, times(1)).findByControllerId(conId);

    }

    @Test
    public void testUpdateDataProcessor_WithValidData() {

        Long dpId = 1L;

        String oldName = "Test DP";
        String oldDesc = "Old description";
        String oldEmail = "js@email.com";
        String oldWebsite = "www.old.com";

        DataProcessorCertification iso22301 = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.ISO_IEC_22301)
                .build();
        DataProcessorCertification cyber = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name(oldName)
                .description(oldDesc)
                .website(oldWebsite)
                .email(oldEmail)
                .certifications(List.of(iso22301, cyber))
                .build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        String newName = "New Test DCD";
        String newDesc = "New description";
        String newWebsite = "new.com";
        String newEmail = "jb@email.com";

        DataProcessorCertification nist = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.NIST_Privacy_Framework)
                .build();

        this.dpService.updateDataProcessor(
                dpId,
                newName,
                newDesc,
                newEmail,
                newWebsite,
                List.of(cyber, nist)
        );

        assertEquals(newName, dp.getName());
        assertEquals(newDesc, dp.getDescription());
        assertEquals(newName, dp.getName());
        assertEquals(newEmail, dp.getEmail());
        assertEquals(newDesc, dp.getDescription());

        List<ProcessingCertificationStandard> accreditations = dp.getCertifications().stream()
                .map(com.sharedsystemshome.dsa.model.DataProcessorCertification::getName)
                .toList();

        assertEquals(2, accreditations.size());
        assertTrue(accreditations.contains(ProcessingCertificationStandard.CYBER_ESSENTIALS));
        assertTrue(accreditations.contains(ProcessingCertificationStandard.NIST_Privacy_Framework));
        assertFalse(accreditations.contains(ProcessingCertificationStandard.ISO_IEC_22301));

        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataProcessor_WithNoChange() {
        Long dpId = 1L;

        String oldName = "Test DP";
        String oldDesc = "Old description";
        String oldEmail = "js@email.com";
        String oldWebsite = "www.old.com";

        DataProcessorCertification acc = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name(oldName)
                .description(oldDesc)
                .email(oldEmail)
                .website(oldWebsite)
                .certifications(new ArrayList<>(List.of(acc)))
                .build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        // Call update method with the exact same values
        this.dpService.updateDataProcessor(
                dpId,
                oldName,
                oldDesc,
                oldEmail,
                oldWebsite,
                List.of(acc)
        );

        // Assertions – ensure nothing changed
        assertEquals(oldName, dp.getName());
        assertEquals(oldDesc, dp.getDescription());
        assertEquals(oldEmail, dp.getEmail());
        assertEquals(oldWebsite, dp.getWebsite());

        List<ProcessingCertificationStandard> accreditationTypes = dp.getCertifications().stream()
                .map(com.sharedsystemshome.dsa.model.DataProcessorCertification::getName)
                .toList();

        assertEquals(1, accreditationTypes.size());
        assertTrue(accreditationTypes.contains(ProcessingCertificationStandard.CYBER_ESSENTIALS));

        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataProcessor_WithEmptyString() {
        Long dpId = 1L;

        // Initial values
        String oldName = "Test DP";
        String oldDesc = "Old description";
        String oldEmail = "js@email.com";
        String oldWebsite = "www.old.com";

        DataProcessorCertification oldAcc = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name(oldName)
                .description(oldDesc)
                .email(oldEmail)
                .website(oldWebsite)
                .certifications(new ArrayList<>(List.of(oldAcc)))
                .build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        // New update values
        String newName = "";         // should not overwrite
        String newDesc = "";         // should overwrite
        String newEmail = "";        // should not overwrite
        String newWebsite = "";      // should overwrite

        DataProcessorCertification newAcc = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.NIST_Privacy_Framework)
                .build();

        this.dpService.updateDataProcessor(
                dpId,
                newName,
                newDesc,
                newEmail,
                newWebsite,
                List.of(newAcc)
        );

        // Assert that fields were updated or preserved as expected
        assertEquals(oldName, dp.getName()); // name unchanged due to empty string
        assertEquals(newDesc, dp.getDescription()); // updated because it's not null
        assertEquals(oldEmail, dp.getEmail()); // email unchanged due to empty string
        assertEquals(newWebsite, dp.getWebsite()); // updated even though it's an empty string

        // Accreditations replaced
        List<ProcessingCertificationStandard> accs = dp.getCertifications().stream()
                .map(com.sharedsystemshome.dsa.model.DataProcessorCertification::getName)
                .toList();

        assertEquals(1, accs.size());
        assertTrue(accs.contains(ProcessingCertificationStandard.NIST_Privacy_Framework));
        assertFalse(accs.contains(ProcessingCertificationStandard.CYBER_ESSENTIALS));

        // Verify correct repo interaction
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataProcessor_WithNullData() {
        Long dpId = 1L;

        // Initial values
        String oldName = "Test DP";
        String oldDesc = "Old description";
        String oldEmail = "js@email.com";
        String oldWebsite = "www.old.com";

        DataProcessorCertification oldAcc = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.CYBER_ESSENTIALS)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name(oldName)
                .description(oldDesc)
                .email(oldEmail)
                .website(oldWebsite)
                .certifications(new ArrayList<>(List.of(oldAcc)))
                .build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        // Call service with all null fields and empty accreditations
        this.dpService.updateDataProcessor(
                dpId,
                null,       // name
                null,       // description
                null,       // email
                null,       // website
                new ArrayList<>() // accreditations: should clear all
        );

        // ✅ Assert all original fields remain unchanged
        assertEquals(oldName, dp.getName());
        assertEquals(oldDesc, dp.getDescription());
        assertEquals(oldEmail, dp.getEmail());
        assertEquals(oldWebsite, dp.getWebsite());

        // ✅ Accreditations should be cleared
        assertNotNull(dp.getCertifications());
        assertTrue(dp.getCertifications().isEmpty());

        // ✅ Repository interactions
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testDeleteDataProcessor_WithAccreditations() {
        Long dpId = 2L;

        // Create DataProcessor with an accreditation
        DataProcessorCertification acc = com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                .name(ProcessingCertificationStandard.ISO_IEC_27001)
                .build();

        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .certifications(new ArrayList<>(List.of(acc))) // mutable and managed
                .build();

        // Simulate a parent controller relationship
        Long provId = 1L;
        DataSharingParty controller = DataSharingParty.builder()
                .id(provId)
                .build();

        dp.setController(controller);
        controller.addDataProcessor(dp); // maintains bidirectional link

        // Mocks
        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));
        when(this.dspMockRepo.findById(provId)).thenReturn(Optional.of(controller));

        // Perform the delete
        this.dpService.deleteDataProcessor(dpId);

        // Verify interactions
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(1)).findById(provId);
        verify(this.dspMockRepo, times(1)).save(controller);

        // Assert that processor was removed from controller
        assertFalse(controller.getProcessors().contains(dp));

        // Assert the processor is now unlinked
        assertNull(dp.getController());

        // Assert accreditations are cleared (orphan removal should handle DB deletion)
        assertTrue(dp.getCertifications().isEmpty());
    }

    @Test
    public void testDeleteDataProcessor_WithInvalidConId(){

        // Given
        Long dpId = 2L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .build();

        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        Long conId = 1L;
        DataSharingParty con = DataSharingParty.builder()
                .id(conId)
                .build();

        when(this.dspMockRepo.findById(conId)).thenReturn(Optional.empty());

        con.addDataProcessor(dp);

        // When - call the deleteDataContentDefinition method
        Exception e = assertThrows(BusinessValidationException.class, () -> {
            this.dpService.deleteDataProcessor(dpId);
        });

        // Then expect...
        assertEquals("Data Sharing Party with id = " + conId + " not found.", e.getMessage());

        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dspMockRepo, times(1)).findById(conId);
        verify(this.dspMockRepo, times(0)).save(con);
    }

    @Test
    void testGetDataProcessors_WithNullDps() {

        // Given: DCDs returned is null
        List<DataProcessor> dps = null;
        Long conId = 1L;

        // When: the controller method is called
        when(this.dpMockRepo.findByControllerId(conId)).thenReturn(dps);
        List<DataProcessor> result = dpService.getDataProcessors(conId);

        // Then expect...
        assertEquals(0, result.size(), "Method returns zero DCDs");

        verify(this.dpMockRepo, times(1)).findByControllerId(conId);

    }
}