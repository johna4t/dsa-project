package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataFlowRepository;
import com.sharedsystemshome.dsa.repository.DataSharingAgreementRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.util.CustomValidator;
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
public class DataFlowServiceTest {

    @Mock
    private DataFlowRepository dataFlowMockRepo;

    @Mock
    private DataSharingPartyRepository dataSharingPartyMockRepo;

    @Mock
    private DataSharingAgreementRepository dataSharingAgreementMockRepo;

    @Mock
    private DataContentDefinitionRepository dataContentDefinitionMockRepo;

    @Mock
    private CustomValidator<DataFlow> validator;

    DataFlowService dataFlowService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dataFlowService = new DataFlowService(
                this.dataFlowMockRepo,
                this.dataSharingPartyMockRepo,
                this.dataSharingAgreementMockRepo,
                this.dataContentDefinitionMockRepo,
                this.validator
        );
    }


    @Test
    public void testCreateDataFlow() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(true);

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();
        when(this.dataSharingPartyMockRepo.findById(prov_id)).thenReturn(Optional.of(prov));

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();
        when(this.dataSharingPartyMockRepo.existsById(cons_id)).thenReturn(true);

        Long dcdId1 = 5L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .provider(prov)
                .name("Test DCD 1")
                .description("Test DCD 1 description")
                .build();
        prov.addDataContentDefinition(dcd1);

        Long dcdId2 = 6L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .provider(prov)
                .name("Test DCD 2")
                .description("Test DCD 2 description")
                .build();
        prov.addDataContentDefinition(dcd2);

        DataFlow dataFlow = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd1, dcd2))
//                .startDate(LocalDate.now())
//                .isPersonalData(true)
//                .lawfulBasis(LawfulBasis.CONSENT)
//                .isSpecialCategoryData(false)
//                .specialCategory(SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA)
//                .dataItems(new ArrayList<String>())
                .build();

        Long df_id = 4L;
        DataFlow savedDataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd1, dcd2))
                .build();

        when(this.dataFlowMockRepo.save(dataFlow)).thenReturn(savedDataFlow);

        Long result = dataFlowService.createDataFlow(dataFlow);

        assertNotNull(result);
        assertEquals(df_id, result);
        verify(this.dataSharingAgreementMockRepo, times(1)).existsById(id);
        verify(this.dataSharingPartyMockRepo, times(1)).findById(prov_id);
        verify(this.dataSharingPartyMockRepo, times(1)).existsById(cons_id);
        verify(this.dataFlowMockRepo, times(1)).save(dataFlow);
    }

    @Test
    public void testCreateDataFlow_DsaIdIsNull() {

        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .build();

        DataFlow dataFlow = DataFlow.builder()
                .id(1L)
                .dataSharingAgreement(dsa)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));
        assertEquals("Data Sharing Agreement id is null or empty.", e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(0)).save(dsa);
    }

    @Test
    public void testCreateDataFlow_DsaIdIsInvalid() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(false);

        DataFlow dataFlow = DataFlow.builder()
                .id(1L)
                .dataSharingAgreement(dsa)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));
        assertEquals("Data Sharing Agreement with id = " + id + " not found.", e.getMessage());

        verify(this.dataFlowMockRepo, times(0)).save(any());
    }

    @Test
    public void testCreateDataFlow_ProviderIdIsNull() {


        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(true);

        DataFlow dataFlow = DataFlow.builder()
                .id(1L)
                .provider(new DataSharingParty())
                .dataSharingAgreement(dsa)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));
        assertEquals("Provider Data Sharing Party id is null or empty.", e.getMessage());

        verify(this.dataFlowMockRepo, times(0)).save(any());


    }

    @Test
    public void testCreateDataFlow_DcdIsNull() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(true);

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();
        when(this.dataSharingPartyMockRepo.findById(prov_id)).thenReturn(Optional.of(prov));

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();
        when(this.dataSharingPartyMockRepo.existsById(cons_id)).thenReturn(true);

        DataFlow dataFlow = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));

        assertEquals("Data Content Definition member collection is null or empty.", e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(1)).existsById(id);
        verify(this.dataSharingPartyMockRepo, times(1)).findById(prov_id);
        verify(this.dataSharingAgreementMockRepo, times(0)).save(dsa);
    }

    @Test
    public void testCreateDataFlow_DcdIsInvalid() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(true);

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();
        when(this.dataSharingPartyMockRepo.findById(prov_id)).thenReturn(Optional.of(prov));

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();
        when(this.dataSharingPartyMockRepo.existsById(cons_id)).thenReturn(true);

        Long dcdId1 = 5L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .provider(prov)
                .name("Test DCD 1")
                .description("Test DCD 1 description")
                .build();
        prov.addDataContentDefinition(dcd1);

        Long dcdId2 = 6L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .provider(cons)
                .name("Test DCD 2")
                .description("Test DCD 2 description")
                .build();
        cons.addDataContentDefinition(dcd2);

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd1, dcd2))
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));

        assertEquals("Data Content Definition with id = " + dcdId2
                + " does not exist for provider Data Sharing Party with id = " + prov_id, e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(1)).existsById(id);
        verify(this.dataSharingPartyMockRepo, times(1)).findById(prov_id);
        verify(this.dataSharingPartyMockRepo, times(1)).existsById(cons_id);
        verify(this.dataFlowMockRepo, times(0)).save(dataFlow);
    }

    @Test
    public void testCreateDataFlow_SpecialCategoryIsInvalid() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.existsById(id)).thenReturn(true);

        Long prov_id = 2L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(prov_id)
                .build();
        when(this.dataSharingPartyMockRepo.findById(prov_id)).thenReturn(Optional.of(prov));

        Long cons_id = 3L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(cons_id)
                .build();
        when(this.dataSharingPartyMockRepo.existsById(cons_id)).thenReturn(true);

        Long dcdId1 = 5L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .provider(prov)
                .name("Test DCD 1")
                .description("Test DCD 1 description")
                .build();
        prov.addDataContentDefinition(dcd1);

        Long dcdId2 = 6L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .provider(prov)
                .name("Test DCD 2")
                .description("Test DCD 2 description")
                .build();
        prov.addDataContentDefinition(dcd2);

        DataFlow dataFlow = DataFlow.builder()
                .dataSharingAgreement(dsa)
                .provider(prov)
                .consumer(cons)
                .providedDcds(List.of(dcd1, dcd2))
                .specialCategory(SpecialCategoryData.HEALTH)
//                .startDate(LocalDate.now())
//                .isPersonalData(true)
//                .lawfulBasis(LawfulBasis.CONSENT)
//                .isSpecialCategoryData(false)
//                .specialCategory(SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA)
//                .dataItems(new ArrayList<String>())
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.createDataFlow(dataFlow));
        assertEquals("Confirmation of Personal Data required for Data Flow", e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(1)).existsById(id);
        verify(this.dataSharingPartyMockRepo, times(1)).findById(prov_id);
        verify(this.dataSharingPartyMockRepo, times(1)).existsById(cons_id);


    }

    @Test
    public void testGetDataFlowById_Exists() {
        Long dataFlowId = 1L;
        DataFlow dataFlow = new DataFlow();

        when(dataFlowMockRepo.findById(dataFlowId)).thenReturn(Optional.of(dataFlow));

        DataFlow result = dataFlowService.getDataFlowById(dataFlowId);

        assertNotNull(result);
        assertEquals(dataFlow, result);
        verify(dataFlowMockRepo, times(1)).findById(dataFlowId);
    }

    @Test
    public void testGetDataFlows() {

        List<DataFlow> dfs = new ArrayList<>();
        when(this.dataFlowMockRepo.findAll()).thenReturn(dfs);

        List<DataFlow> result = this.dataFlowService.getDataFlows();

        assertNotNull(result);
        assertEquals(dfs, result);
        verify(this.dataFlowMockRepo, times(1)).findAll();
    }

    @Test
    public void testGetDataFlowById_DataFlowIdIsInvalid() {
        Long dataFlowId = 1L;
        when(dataFlowMockRepo.findById(dataFlowId)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowById(dataFlowId));
        assertEquals("Data Flow with id = " + dataFlowId + " not found.", e.getMessage());

        verify(dataFlowMockRepo, times(1)).findById(dataFlowId);
    }

/*    @Test
    public void testGetDataFlowProvider(){

        Long provId = 1L;
        DataSharingParty prov= DataSharingParty.builder()
                .id(provId)
                .build();
        Long dfId = 1L;
        DataFlow dataFlow = DataFlow.builder()
                .id(provId)
                .provider(prov)
                .build();

        when(this.dataFlowMockRepo.existsById(dfId)).thenReturn(true);
        when(this.dataFlowMockRepo.findById(dfId)).thenReturn(Optional.of(dataFlow));

        DataSharingParty result = this.dataFlowService.getDataFlowProvider(dfId);

        assertNotNull(result);
        assertEquals(provId, result.getId());
        verify(this.dataFlowMockRepo, times(1)).findById(dfId);

    }*/

/*    @Test
    public void testGetDataFlowProvider_InvalidDataFlow(){

        Long dfId = 1L;

        when(this.dataFlowMockRepo.existsById(any())).thenReturn(false);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowProvider(dfId));

        assertEquals("Data Flow with id = " + dfId + " does not exist.", e.getMessage());

        verify(this.dataFlowMockRepo, times(0)).findById(any());

    }*/

/*    @Test
    public void testGetDataFlowConsumer(){

        Long consId = 2L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(consId)
                .build();
        Long dfId = 1L;
        DataFlow dataFlow = DataFlow.builder()
                .id(consId)
                .consumer(cons)
                .build();

        when(this.dataFlowMockRepo.existsById(dfId)).thenReturn(true);
        when(this.dataFlowMockRepo.findById(dfId)).thenReturn(Optional.of(dataFlow));

        DataSharingParty result = this.dataFlowService.getDataFlowConsumer(dfId);

        assertNotNull(result);
        assertEquals(consId, result.getId());
        verify(this.dataFlowMockRepo, times(1)).findById(dfId);

    }*/

/*    @Test
    public void testGetDataFlowConsumer_InvalidDataFlow(){

        Long dfId = 2L;

        when(this.dataFlowMockRepo.existsById(any())).thenReturn(false);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowConsumer(dfId));

        assertEquals("Data Flow with id = " + dfId + " does not exist.", e.getMessage());

        verify(this.dataFlowMockRepo, times(0)).findById(any());

    }*/


    @Test
    public void testGetDataFlowsByProviderId_ProviderIdIsNull(){

        // Call the getDataFlowsByConsumer method with a null consumer
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowsByProviderId(null));

        assertEquals("Provider Data Sharing Party id is null or empty.", e.getMessage());

    }

    @Test
    void testGetDataFlowsByProviderId_ProviderIdIsValid() {
        // Create a list of sample data flows
        List<DataFlow> sampleDataFlows = new ArrayList<>();
        sampleDataFlows.add(new DataFlow());
        sampleDataFlows.add(new DataFlow());

        // Mock the behavior of the dataFlowRepository and organisationRepository
        Long provId = 1L;
        when(this.dataFlowMockRepo.findDataFlowByProviderId(provId)).thenReturn(Optional.of(sampleDataFlows));

        // Call the getDataFlowsByConsumer method with a valid consumer ID
        List<DataFlow> result = dataFlowService.getDataFlowsByProviderId(provId);

        // Verify that the dataFlowRepository's findDataFlowByConsumerId method was called
        verify(this.dataFlowMockRepo, times(1)).findDataFlowByProviderId(provId);

        // Verify that the result contains the sample data flows
        assertEquals(sampleDataFlows.size(), result.size());
    }

    @Test
    void testGetDataFlowsByProviderId_ProviderIdIsInValid() {

        // Mock the behavior of the dataFlowRepository and organisationRepository
        Long provId = 1L;
        when(this.dataFlowMockRepo.findDataFlowByProviderId(provId)).thenReturn(Optional.empty());

        // Call the getDataFlowsByConsumer method with an invalid consumer ID
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowsByProviderId(provId));

        assertEquals("Provider Data Sharing Party with id = " + provId + " not found.", e.getMessage());

        verify(this.dataFlowMockRepo, times(1)).findDataFlowByProviderId(provId);
    }

    @Test
    public void testGetDataFlowsByConsumerId_ConsumerIdIsNull(){

        // Call the getDataFlowsByConsumer method with a null consumer
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowsByConsumerId(null));

        assertEquals("Consumer Data Sharing Party id is null or empty.", e.getMessage());

    }

    @Test
    void testGetDataFlowsByConsumerId_ConsumerIdIsValid() {
        // Create a list of sample data flows
        List<DataFlow> sampleDataFlows = new ArrayList<>();
        sampleDataFlows.add(new DataFlow());
        sampleDataFlows.add(new DataFlow());

        // Mock the behavior of the dataFlowRepository and organisationRepository
        Long consId = 1L;
        when(this.dataFlowMockRepo.findDataFlowByConsumerId(consId)).thenReturn(Optional.of(sampleDataFlows));

        // Call the getDataFlowsByConsumer method with a valid consumer ID
        List<DataFlow> result = dataFlowService.getDataFlowsByConsumerId(consId);

        // Verify that the dataFlowRepository's findDataFlowByConsumerId method was called
        verify(this.dataFlowMockRepo, times(1)).findDataFlowByConsumerId(consId);

        // Verify that the result contains the sample data flows
        assertEquals(sampleDataFlows.size(), result.size());
    }

    @Test
    void testGetDataFlowsByConsumerId_ConsumerIdIsInValid() {

        // Mock the behavior of the dataFlowRepository and organisationRepository
        Long consId = 1L;
        when(this.dataFlowMockRepo.findDataFlowByConsumerId(consId)).thenReturn(Optional.empty());

        // Call the getDataFlowsByConsumer method with an invalid consumer ID
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.getDataFlowsByConsumerId(consId));

        assertEquals("Consumer Data Sharing Party with id = " + consId + " not found.", e.getMessage());

        verify(this.dataFlowMockRepo, times(1)).findDataFlowByConsumerId(consId);
    }

    @Test
    public void testUpdateDataFlow_WithValidData() {
        Long dataFlowId = 1L;
        Period p1 = Period.ofMonths(5).plusDays(15);
        Period p2 = Period.ofMonths(10).plusDays(15);
        LocalDate newEndDate = LocalDate.now().plus(p2);
        LawfulBasis newLawfulBasis = LawfulBasis.CONSENT;
        SpecialCategoryData newSpecial = SpecialCategoryData.HEALTH;
        String newPurpose = "New purpose of sharing";

        DataFlow dataFlow = DataFlow.builder()
                .id(dataFlowId)
                .endDate(LocalDate.now().plus(p1))
                .lawfulBasis(LawfulBasis.CONTRACT)
                .purposeOfSharing("Purpose of sharing")
                .build();

        when(this.dataFlowMockRepo.findById(dataFlowId)).thenReturn(Optional.of(dataFlow));

        this.dataFlowService.updateDataFlow(
                dataFlowId,
                newEndDate,
                LawfulBasis.CONSENT,
                newSpecial,
                newPurpose
        );

        assertEquals(newEndDate, dataFlow.getEndDate());
        assertEquals(newLawfulBasis, dataFlow.getLawfulBasis());
        assertEquals(newSpecial, dataFlow.getSpecialCategory());
        assertEquals(newPurpose, dataFlow.getPurposeOfSharing());
    }

    @Test
    public void testUpdateDataFlow_DataFlowIsInvalid() {
        Long dataFlowId = 1L;
        Long newConsId = 2L;
        DataSharingParty cons = DataSharingParty.builder()
                .id(1L)
                .build();
        DataFlow dataFlow = DataFlow.builder()
                .id(dataFlowId)
                .consumer(cons)
                .build();


        when(this.dataFlowMockRepo.findById(dataFlowId)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.updateDataFlow(
                        dataFlowId,
                        null,
                        null,
                        null,
                        null
                ));

        assertEquals("Data Flow with id = " + dataFlowId + " not found.", e.getMessage());

        verify(this.dataFlowMockRepo, times(1)).findById(dataFlowId);

    }

    @Test
    public void testDeleteDataFlow(){

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.findById(id)).thenReturn(Optional.of(dsa));

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .build();

        dsa.addDataFlow(dataFlow);

        when(this.dataFlowMockRepo.findById(anyLong())).thenReturn(Optional.of(dataFlow));

        // Call the deleteDataFlow method
        this.dataFlowService.deleteDataFlow(df_id);

        verify(this.dataFlowMockRepo, times(1)).findById(df_id);
        verify(this.dataSharingAgreementMockRepo, times(1)).findById(id);
        verify(this.dataSharingAgreementMockRepo, times(1)).save(dsa);

    }

    @Test
    void testDeleteDataFlow_DataFlowIdIsInvalid() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
//        when(this.dataSharingAgreementMockRepo.findById(id)).thenReturn(Optional.of(dsa));

        Long df_id = 4L;
        /**
         DataFlow dataFlow = DataFlow.builder()
         .id(df_id)
         .dataSharingAgreement(dsa)
         .build();

         dsa.addDataFlow(dataFlow);
         */
        when(this.dataFlowMockRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Call the deleteDataFlow method with a non-existent data flow
        Exception e = assertThrows(BusinessValidationException.class, () -> {
            dataFlowService.deleteDataFlow(df_id);
        });

        assertEquals("Data Flow with id = " + df_id + " not found.", e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(0)).findById(id);
        verify(this.dataSharingAgreementMockRepo, times(0)).save(dsa);
    }

    @Test
    void testDeleteDataFlow_DsaIdIsInvalid() {

        Long id = 1L;
        DataSharingAgreement dsa = DataSharingAgreement.builder()
                .id(id)
                .build();
        when(this.dataSharingAgreementMockRepo.findById(anyLong())).thenReturn(Optional.empty());

        Long df_id = 4L;
        DataFlow dataFlow = DataFlow.builder()
                .id(df_id)
                .dataSharingAgreement(dsa)
                .build();

        dsa.addDataFlow(dataFlow);

        when(this.dataFlowMockRepo.findById(anyLong())).thenReturn(Optional.of(dataFlow));

        Exception e = assertThrows(BusinessValidationException.class, () -> {
            dataFlowService.deleteDataFlow(df_id);
        });

        assertEquals("Data Sharing Agreement with id = " + id + " not found.", e.getMessage());

        verify(this.dataSharingAgreementMockRepo, times(1)).findById(id);
        verify(this.dataSharingAgreementMockRepo, times(0)).save(dsa);
    }



    @Test
    void testAddDataContentDefinition() {

        Long dcdId1 = 1L;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .build();

        Long dcdId2 = 2L;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .build();

        // Create DataFlow
        Long dfId = 1L;
        DataFlow dataFlow = DataFlow.builder()
                .id(dfId)
                .build();

        when(this.dataFlowMockRepo.findById(any())).thenReturn(Optional.of(dataFlow));
        when(this.dataContentDefinitionMockRepo.findById(dcdId1)).thenReturn(Optional.of(dcd1));
        when(this.dataContentDefinitionMockRepo.findById(dcdId2)).thenReturn(Optional.of(dcd2));

        // Should add dcd 1 to list
        this.dataFlowService.addDataContentDefinition(dfId, dcdId1);
        // Should add dcd 2 to list
        this.dataFlowService.addDataContentDefinition(dfId, dcdId2);
        // Should NOT add dcd 1 again
        this.dataFlowService.addDataContentDefinition(dfId, dcdId1);

        assertEquals(2, dataFlow.getProvidedDcds().size());
    }

    @Test
    void testAddDataContentDefinition_WithInvalidDcdId() {

        Long dcdId = 1L;

        // Create DSA
        Long dfId = 1L;
        DataFlow dsa = DataFlow.builder()
                .id(dfId)
                .build();

        when(this.dataFlowMockRepo.findById(any())).thenReturn(Optional.of(dsa));
        when(this.dataContentDefinitionMockRepo.findById(dcdId)).thenReturn(Optional.empty());

        // Should NOT add DCD to list
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.addDataContentDefinition(dfId, dcdId));
        assertEquals("Data Content Definition with id = " + dcdId+ " not found.", e.getMessage());
        verify(this.dataFlowMockRepo, times(1)).findById(dfId);
        verify(this.dataContentDefinitionMockRepo, times(1)).findById(dcdId);
    }

    @Test
    void testRemoveDataContentDefinition() {

        Long dcdId1 = 1l;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .build();

        Long dcdId2 = 2l;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .build();

        List<DataContentDefinition> dcds = new ArrayList<>();
        dcds.add(dcd1);
        dcds.add(dcd2);

        // Given - DataFloe created to pass to method being tested
        Long dfId = 1L;
        DataFlow dataFlow = DataFlow.builder()
                .id(dfId)
                .providedDcds(dcds)
                .build();

        when(this.dataFlowMockRepo.findById(dfId)).thenReturn(Optional.of(dataFlow));

        // Mock DataFlow repo to return DataFlow with id on save
        when(this.dataFlowMockRepo.save(dataFlow)).thenReturn(dataFlow);

        assertEquals(2, dataFlow.getProvidedDcds().size());

        // When method run
        this.dataFlowService.removeDataContentDefinition(dfId, dcdId2);

        // Then
        assertEquals(1, dataFlow.getProvidedDcds().size());
        verify(this.dataFlowMockRepo, times(1)).save(dataFlow);
    }


    @Test
    void testRemoveDataContentDefinition_InvalidDcdId() {

        Long dcdId1 = 1l;
        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .id(dcdId1)
                .build();

        Long dcdId2 = 2l;
        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .id(dcdId2)
                .build();

        List<DataContentDefinition> dcds = new ArrayList<>();
        dcds.add(dcd1);
        dcds.add(dcd2);

        // Given - DataFloe created to pass to method being tested
        Long dfId = 1L;
        DataFlow dataFlow = DataFlow.builder()
                .id(dfId)
                .providedDcds(dcds)
                .build();

        when(this.dataFlowMockRepo.findById(dfId)).thenReturn(Optional.of(dataFlow));

        assertEquals(2, dataFlow.getProvidedDcds().size());

        // Then
        Long invalidDcdId = 3L;
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dataFlowService.removeDataContentDefinition(dfId, invalidDcdId));
        assertEquals("Data Content Definition with id = " + invalidDcdId + " not found.", e.getMessage());
        assertEquals(2, dataFlow.getProvidedDcds().size());
        verify(this.dataFlowMockRepo, times(0)).save(dataFlow);
    }

    private DataFlow savedDataFlow(DataFlow dataflow, Long id){

        dataflow.setId(id);

        return dataflow;

    }

}

