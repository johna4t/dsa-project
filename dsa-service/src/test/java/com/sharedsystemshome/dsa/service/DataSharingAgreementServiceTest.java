package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import com.sharedsystemshome.dsa.repository.CustomerAccountRepository;
import com.sharedsystemshome.dsa.repository.DataSharingAgreementRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import com.sharedsystemshome.dsa.util.CustomValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSharingAgreementServiceTest {


    @Mock
    private DataSharingAgreementRepository dsaMockRepo;

    @Mock
    private DataSharingPartyRepository dspMockRepo;

    @Mock
    private CustomerAccountRepository customerMockRepo;

    @Mock
    private CustomValidator<DataSharingAgreement> valdator;

    private DataSharingAgreementService dsaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dsaService = new DataSharingAgreementService(
                this.dsaMockRepo,
                this.dspMockRepo,
                this.customerMockRepo,
                this.valdator
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateDataSharingAgreement_ValidDsa() {

        Long custId = 3l;
        CustomerAccount customer = CustomerAccount.builder()
                .id(custId)
                .build();

        when(this.customerMockRepo.existsById(custId)).thenReturn(true);


        // Given - DSA created to pass to method being tested
        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .accountHolder(customer)
//                .name("Test DSA")
//                .controllerRelationship(ControllerRelationship.JOINT)
                // Optional data
//                .startDate(startDate)
//                .endDate(endDate)
//                .partiesToAgreement(new ArrayList<>())
                .build();

        DataSharingAgreement savedDsa = DataSharingAgreement.builder()
                .id(dsaId)
                .accountHolder(customer)
//                .name(dsa.getName())
//                .controllerRelationship(dsa.getControllerRelationship())
                // Optional data
//                .startDate(startDate)
//                .endDate(endDate)
//                .partiesToAgreement(new ArrayList<>())
                .build();

        // Mock dsa repo to return DSA with id on save
        when(this.dsaMockRepo.save(dsa)).thenReturn(savedDsa);

        // When method run
        Long result = this.dsaService.createDataSharingAgreement(dsa);

        // Then
        assertNotNull(result);
        assertEquals(dsaId, result);
        verify(this.dsaMockRepo, times(1)).save(dsa);

    }

    @Test
    public void testCreateDataSharingAgreement_DsaIsNull() {

        DataSharingAgreement dsa = null;

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.createDataSharingAgreement(dsa));

        assertEquals("Data Sharing Agreement is null or empty.", e.getMessage());

        verify(this.dsaMockRepo, times(0)).save(dsa);

    }

    @Test
    public void testCreateDataSharingAgreement_SaveThrowsException() {

        Long custId = 3l;
        CustomerAccount customer = CustomerAccount.builder()
                .id(custId)
                .build();

        when(this.customerMockRepo.existsById(custId)).thenReturn(true);

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .accountHolder(customer)
                .build();

        when(this.dsaMockRepo.save(dsa)).thenThrow(IllegalArgumentException.class);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.createDataSharingAgreement(dsa));

        assertEquals("Unable to add or update Data Sharing Agreement.", e.getMessage());

        verify(this.dsaMockRepo, times(1)).save(dsa);

    }

    @Test
    void testGetDataSharingAgreementById_ValidId() {
        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .build();

        when(this.dsaMockRepo.findById(dsaId)).thenReturn(Optional.of(dsa));

        DataSharingAgreement result = this.dsaService.getDataSharingAgreementById(dsaId);

        assertNotNull(result);
        assertEquals(dsa, result);
        verify(this.dsaMockRepo, times(1)).findById(dsaId);
    }

    @Test
    public void testGetDataSharingAgreementById_InvalidId() {
        Long id = 1L;
        when(this.dsaMockRepo.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.getDataSharingAgreementById(id));
        assertEquals("Data Sharing Agreement with id = " + id + " not found.", e.getMessage());

        verify(this.dsaMockRepo, times(1)).findById(id);
    }

    @Test
    void testGetDataSharingAgreements() {

        List<DataSharingAgreement> dsas = new ArrayList<>();
        when(this.dsaMockRepo.findAll()).thenReturn(dsas);

        List<DataSharingAgreement> result = this.dsaService.getDataSharingAgreements();

        assertNotNull(result);
        assertEquals(dsas, result);
        verify(this.dsaMockRepo, times(1)).findAll();
    }

    @Test
    void testUpdateDataSharingAgreement_WithValidData() {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .startDate(LocalDate.of(2023,5,9))
                .endDate(LocalDate.of(2024,5,9))
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();

        when(this.dsaMockRepo.findById(any())).thenReturn(Optional.of(dsa));

        String newName = "DSA 2";
        LocalDate newStartDate = LocalDate.of(2023, 9,9);
        LocalDate newEndDate = LocalDate.of(2024, 9,9);
        ControllerRelationship newCR = ControllerRelationship.SEPARATE;

        this.dsaService.updateDataSharingAgreement(
                dsaId,
                newName,
                newStartDate,
                newEndDate,
                newCR
        );

        assertEquals(newName, dsa.getName());
        assertEquals(newStartDate, dsa.getStartDate());
        assertEquals(newEndDate, dsa.getEndDate());
        assertEquals(newCR, dsa.getControllerRelationship());
        assertEquals(Period.between(newStartDate, newEndDate),
                dsa.getPeriodOfAgreement());

        verify(this.dsaMockRepo, times(1)).findById(dsaId);

    }

    @Test
    void testUpdateDataSharingAgreement_DsaIdIsInvalid() {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .name("DSA 1")
                .startDate(LocalDate.of(2023,5,9))
                .endDate(LocalDate.of(2024,5,9))
                .controllerRelationship(ControllerRelationship.JOINT)
                .build();

        when(this.dsaMockRepo.findById(any())).thenReturn(Optional.empty());

        String newName = "DSA 2";
        LocalDate newStartDate = LocalDate.of(2023, 9,9);
        LocalDate newEndDate = LocalDate.of(2023, 9,9);
        ControllerRelationship newCR = ControllerRelationship.SEPARATE;

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.updateDataSharingAgreement(
                        dsaId,
                        newName,
                        newStartDate,
                        newEndDate,
                        newCR
                ));

        assertEquals("Data Sharing Agreement with id = " + dsaId + " not found.", e.getMessage());

        verify(this.dsaMockRepo, times(1)).findById(dsaId);

    }

    @Test
    void testDeleteDataFlow() {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .build();
        when(this.dsaMockRepo.findById(dsaId)).thenReturn(Optional.of(dsa));

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                /**
                 * DataFlow passed to method includes an id (which it would not contain in practice),
                 * however, saved Data Flow is created by Data Sharing Agreement repo
                 */
                .id(df_id)
                .dataSharingAgreement(dsa)
                .build();

        assertEquals(1, dsa.getDataFlows().size());

        // Then
        this.dsaService.deleteDataFlow(dsaId, df_id);
        assertEquals(0, dsa.getDataFlows().size());
        verify(this.dsaMockRepo, times(1)).save(dsa);

    }

    @Test
    void testDeleteDataFlow_WithInvalidDataFlow() {

        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .build();
        when(this.dsaMockRepo.findById(dsaId)).thenReturn(Optional.of(dsa));

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                /**
                 * DataFlow passed to method includes an id (which it would not contain in practice),
                 * however, saved Data Flow is created by Data Sharing Agreement repo
                 */
                .id(df_id)
                .dataSharingAgreement(dsa)
                .build();

        assertEquals(1, dsa.getDataFlows().size());

        // Then
        Long invalidDfId = 5L;
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.deleteDataFlow(dsaId, invalidDfId));
        assertEquals("Data Flow with id = " + invalidDfId + " not found.", e.getMessage());
        assertEquals(1, dsa.getDataFlows().size());
        verify(this.dsaMockRepo, times(0)).save(dsa);

    }

    @Test
    void testDeleteDataSharingAgreement() {
        // Given - DSA created to pass to method being tested
        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .build();

        when(this.dsaMockRepo.existsById(dsaId)).thenReturn(true);

        // When method run
        this.dsaService.deleteDataSharingAgreement(dsaId);

        // Then
        verify(this.dsaMockRepo, times(1)).existsById(dsaId);
        verify(this.dsaMockRepo, times(1)).deleteById(dsaId);

    }

    @Test
    void testDeleteDataSharingAgreement_InvalidDsaId() {
        // Given - DSA created to pass to method being tested
        Long dsaId = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(dsaId)
                .build();

        when(this.dsaMockRepo.existsById(any())).thenReturn(false);

        // When method run
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dsaService.deleteDataSharingAgreement(dsaId));

        // Then
        assertEquals("Data Sharing Agreement with id = " + dsaId + " not found.",
                e.getMessage());
        verify(this.dsaMockRepo, times(1)).existsById(dsaId);
        verify(this.dsaMockRepo, times(0)).deleteById(dsaId);

    }

}