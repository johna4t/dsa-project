package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.repository.CustomerAccountRepository;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataContentDefinitionServiceTest {

    @Mock
    private DataContentDefinitionRepository dcdMockRepo;

    @Mock
    private DataSharingPartyRepository dspMockRepo;

    @Mock
    private CustomerAccountRepository customerMockRepo;

    @Mock
    private CustomValidator<DataContentDefinition> validator;

    private DataContentDefinitionService dcdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dcdService = new DataContentDefinitionService(
                this.dcdMockRepo,
                this.dspMockRepo,
                this.validator
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateDataContentDefinition() {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();
        when(this.dspMockRepo.existsById(provId)).thenReturn(true);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Test DCD")
                .provider(prov)
                .build();

        Long dcdId = 1L;
        DataContentDefinition savedDcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .provider(prov)
                .build();

        when(this.dcdMockRepo.save(dcd)).thenReturn(savedDcd);

        Long result = dcdService.createDataContentDefinition(dcd);

        assertNotNull(result);
        assertEquals(dcdId, result);
        verify(this.dspMockRepo, times(1)).existsById(provId);
        verify(this.dcdMockRepo, times(1)).save(dcd);
    }

    @Test
    public void testCreateDataContentDefinition_ProvIdIsNull() {

        DataSharingParty prov = DataSharingParty.builder()
                .build();

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(1L)
                .name("Test DCD")
                .provider(prov)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dcdService.createDataContentDefinition(dcd));
        assertEquals("Provider Data Sharing Party id is null or empty.", e.getMessage());

        verify(this.dspMockRepo, times(0)).save(prov);
    }

    @Test
    public void testCreateDataContentDefinition_ProvIdIsInvalid() {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();
        when(this.dspMockRepo
                .existsById(provId)).thenReturn(false);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(1L)
                .provider(prov)
                .build();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dcdService.createDataContentDefinition(dcd));
        assertEquals("Data Sharing Party with id = " + provId + " not found.", e.getMessage());

        verify(this.dspMockRepo, times(1)).existsById(provId);
        verify(this.dcdMockRepo, times(0)).save(any());
    }

    @Test
    public void testDataContentDefinitionById_Exists() {
        Long dcdId = 1L;
        DataContentDefinition dcd = new DataContentDefinition();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        DataContentDefinition result = this.dcdService.getDataContentDefinitionById(dcdId);

        assertNotNull(result);
        assertEquals(dcd, result);
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
    }

    @Test
    public void testGetDataContentDefinitionById_WithInvalidDcdId() {
        Long dcdId = anyLong();

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dcdService.getDataContentDefinitionById(dcdId));
        assertEquals("Data Content Definition with id = " + dcdId + " not found.", e.getMessage());
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
    }

    @Test
    public void testGetDataContentDefinitions() {

        List<DataContentDefinition> dcds = new ArrayList<>();
        when(this.dcdMockRepo.findByProviderId(anyLong())).thenReturn(dcds);

        List<DataContentDefinition> result = this.dcdService.getDataContentDefinitions(anyLong());

        assertNotNull(result);
        assertEquals(dcds, result);
        verify(this.dcdMockRepo, times(1)).findByProviderId(anyLong());
    }

    @Test
    public void testUpdateDataContentDefinition_WithValidData() {

        Long dcdId = 1L;

        String oldName = "Test DCD";
        String oldDesc = "Old description";
        DataContentType oldDataContentType = DataContentType.STRUCTURED_ELECTRONIC_DATA;
        String oldOwnerName = "John Smith";
        String oldOwnerEmail = "js@email.com";
        Period oldRetention = Period.ofWeeks(6);
        String oldSource = "System X";
        DataContentPerspective dcp1 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", "CONTRACT",
                                        "specialCategory", "NOT_SPECIAL_CATEGORY_DATA"
                        )
                )
                .build();

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name(oldName)
                .description(oldDesc)
                .dataContentType(oldDataContentType)
                .ownerName(oldOwnerName)
                .ownerEmail(oldOwnerEmail)
                .retentionPeriod(oldRetention)
                .sourceSystem(oldSource)
                .perspectives(List.of(dcp1))
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        String newName = "New Test DCD";
        String newDesc = "New description";
        DataContentType newDataContentType = DataContentType.OTHER_MEDIUM;
        String newOwnerName = "Jane Brown";
        String newOwnerEmail = "jb@email.com";
        Period newRetention = Period.ofMonths(7);
        String newSource = "System Y";
        DataContentPerspective dcp2 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", LawfulBasis.LEGAL_OBLIGATION,
                                "specialCategory", SpecialCategoryData.GENETIC
                        )
                )
                .build();

        this.dcdService.updateDataContentDefinition(
                dcdId,
                newName, // name
                newDesc, // description
                newDataContentType,
                newOwnerName,
                newOwnerEmail, // ownerName
                newRetention,
                newSource, // Source system
                List.of(dcp2)
        );

        assertEquals(newName, dcd.getName());
        assertEquals(newDesc, dcd.getDescription());
        assertEquals(newDataContentType, dcd.getDataContentType());
        assertEquals(newOwnerName, dcd.getOwnerName());
        assertEquals(newOwnerEmail, dcd.getOwnerEmail());
        assertEquals(newSource, dcd.getSourceSystem());
        assertEquals(MetadataScheme.GDPR, dcd.getPerspectives().get(0).getMetadataScheme());
        assertEquals(LawfulBasis.LEGAL_OBLIGATION, dcd.getPerspectives().get(0).getMetadata().get("lawfulBasis"));
        assertEquals(SpecialCategoryData.GENETIC, dcd.getPerspectives().get(0).getMetadata().get("specialCategory"));
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataContentDefinition_WithNoChange() {

        Long dcdId = 1L;

        String oldName = "Test DCD";
        String oldDesc = "Old description";
        DataContentType oldDataContentType = DataContentType.STRUCTURED_ELECTRONIC_DATA;
        String oldOwnerName = "John Smith";
        String oldOwnerEmail = "js@email.com";
        Period oldRetention = Period.ofWeeks(6);
        String oldSource = "System X";
        DataContentPerspective dcp1 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", LawfulBasis.CONTRACT,
                                "specialCategory", SpecialCategoryData.GENETIC
                        )
                )
                .build();

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name(oldName)
                .description(oldDesc)
                .dataContentType(oldDataContentType)
                .ownerName(oldOwnerName)
                .ownerEmail(oldOwnerEmail)
                .retentionPeriod(oldRetention)
                .sourceSystem(oldSource)
                .perspectives(List.of(dcp1))
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        this.dcdService.updateDataContentDefinition(
                dcdId,
                oldName, // name
                oldDesc, // description
                oldDataContentType,
                oldOwnerName,
                oldOwnerEmail, // ownerName
                oldRetention,
                oldSource, // Source system
                List.of(dcp1)
        );

        assertEquals(oldName, dcd.getName());
        assertEquals(oldDesc, dcd.getDescription());
        assertEquals(oldDataContentType, dcd.getDataContentType());
        assertEquals(oldOwnerName, dcd.getOwnerName());
        assertEquals(oldOwnerEmail, dcd.getOwnerEmail());
        assertEquals(oldSource, dcd.getSourceSystem());
        assertEquals(MetadataScheme.GDPR, dcd.getPerspectives().get(0).getMetadataScheme());
        assertEquals(LawfulBasis.CONTRACT, dcd.getPerspectives().get(0).getMetadata().get("lawfulBasis"));
        assertEquals(SpecialCategoryData.GENETIC, dcd.getPerspectives().get(0).getMetadata().get("specialCategory"));
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataContentDefinition_WithEmptyString() {

        Long dcdId = 1L;

        String oldName = "Test DCD";
        String oldDesc = "Old description";
        DataContentType oldDataContentType = DataContentType.STRUCTURED_ELECTRONIC_DATA;
        String oldOwnerName = "John Smith";
        String oldOwnerEmail = "js@email.com";
        Period oldRetention = Period.ofWeeks(6);
        String oldSource = "System X";
        DataContentPerspective dcp1 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", "CONTRACT",
                                "specialCategory", "NOT_SPECIAL_CATEGORY_DATA"
                        )
                )
                .build();

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name(oldName)
                .description(oldDesc)
                .dataContentType(oldDataContentType)
                .ownerName(oldOwnerName)
                .ownerEmail(oldOwnerEmail)
                .retentionPeriod(oldRetention)
                .sourceSystem(oldSource)
                .perspectives(List.of(dcp1))
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        String newName = "";
        String newDesc = "";
        DataContentType newDataContentType = DataContentType.OTHER_MEDIUM;
        String newOwnerName = "";
        String newOwnerEmail = "";
        Period newRetention = Period.ofMonths(7);
        String newSource = "";
        DataContentPerspective dcp2 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", LawfulBasis.LEGAL_OBLIGATION,
                                "specialCategory", SpecialCategoryData.GENETIC
                        )
                )
                .build();

        this.dcdService.updateDataContentDefinition(
                dcdId,
                newName, // name
                newDesc, // description
                newDataContentType,
                newOwnerName,
                newOwnerEmail, // ownerName
                newRetention,
                newSource, // Source system
                List.of(dcp2)
        );

        assertEquals(oldName, dcd.getName());
        assertEquals(newDesc, dcd.getDescription());
        assertEquals(newDataContentType, dcd.getDataContentType());
        assertEquals(newOwnerName, dcd.getOwnerName());
        assertEquals(oldOwnerEmail, dcd.getOwnerEmail());
        assertEquals(newSource, dcd.getSourceSystem());
        assertEquals(MetadataScheme.GDPR, dcd.getPerspectives().get(0).getMetadataScheme());
        assertEquals(LawfulBasis.LEGAL_OBLIGATION, dcd.getPerspectives().get(0).getMetadata().get("lawfulBasis"));
        assertEquals(SpecialCategoryData.GENETIC, dcd.getPerspectives().get(0).getMetadata().get("specialCategory"));
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }
    @Test
    public void testUpdateDataContentDefinition_WithNullData() {

        Long dcdId = 1L;

        String oldName = "Test DCD";
        String oldDesc = "Old description";
        DataContentType oldDataContentType = DataContentType.STRUCTURED_ELECTRONIC_DATA;
        String oldOwnerName = "John Smith";
        String oldOwnerEmail = "js@email.com";
        Period oldRetention = Period.ofWeeks(6);
        String oldSource = "System X";
        DataContentPerspective dcp1 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(
                        Map.of(
                                "lawfulBasis", LawfulBasis.CONTRACT,
                                "specialCategory", SpecialCategoryData.GENETIC
                        )
                )
                .build();

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name(oldName)
                .description(oldDesc)
                .dataContentType(oldDataContentType)
                .ownerName(oldOwnerName)
                .ownerEmail(oldOwnerEmail)
                .retentionPeriod(oldRetention)
                .sourceSystem(oldSource)
                .perspectives(List.of(dcp1))
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        this.dcdService.updateDataContentDefinition(
                dcdId,
                null, // name
                null, // description
                null,
                null,
                null, // ownerName
                null,
                null, // Source system
                new ArrayList<>()
        );

        assertEquals(oldName, dcd.getName());
        assertEquals(oldDesc, dcd.getDescription());
        assertEquals(oldDataContentType, dcd.getDataContentType());
        assertEquals(oldOwnerName, dcd.getOwnerName());
        assertEquals(oldOwnerEmail, dcd.getOwnerEmail());
        assertEquals(oldSource, dcd.getSourceSystem());
        assertEquals(MetadataScheme.GDPR, dcd.getPerspectives().get(0).getMetadataScheme());
        assertEquals(LawfulBasis.CONTRACT, dcd.getPerspectives().get(0).getMetadata().get("lawfulBasis"));
        assertEquals(SpecialCategoryData.GENETIC, dcd.getPerspectives().get(0).getMetadata().get("specialCategory"));
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testUpdateDataContentDefinition_WithInvalidDcdId() {

        Long dcdId = anyLong();
        String newDesc = "New description";

        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .description("Old description")
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class,
                () -> this.dcdService.updateDataContentDefinition(
                        dcdId,
                        null,
                        newDesc,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new ArrayList<>()

                ));

        assertEquals("Data Content Definition with id = " + dcdId + " not found.", e.getMessage());

        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(anyLong());
    }

    @Test
    public void testDeleteDataContentDefinition(){

        Long dcdId = 2L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        when(this.dspMockRepo.findById(provId)).thenReturn(Optional.of(prov));

        prov.addDataContentDefinition(dcd);


        // Call the deleteDataContentDefinition method
        this.dcdService.deleteDataContentDefinition(dcdId);

        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(1)).findById(provId);
        verify(this.dspMockRepo, times(1)).save(prov);
    }


    @Test
    public void testDeleteDataContentDefinition_WithInvalidDcdId(){

        // Given
        Long dcdId = 2L;

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.empty());

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        // When - call the deleteDataContentDefinition method
        Exception e = assertThrows(BusinessValidationException.class, () -> {
            this.dcdService.deleteDataContentDefinition(dcdId);
        });

        // Then
        assertEquals("Data Content Definition with id = " + dcdId + " not found.", e.getMessage());

        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(0)).findById(provId);
        verify(this.dspMockRepo, times(0)).save(prov);
    }

    @Test
    public void testDeleteDataContentDefinition_WithInvalidProvId(){

        // Given
        Long dcdId = 2L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .build();

        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();

        when(this.dspMockRepo.findById(provId)).thenReturn(Optional.empty());

        prov.addDataContentDefinition(dcd);

        // When - call the deleteDataContentDefinition method
        Exception e = assertThrows(BusinessValidationException.class, () -> {
            this.dcdService.deleteDataContentDefinition(dcdId);
        });

        // Then expect...
        assertEquals("Data Sharing Party with id = " + provId + " not found.", e.getMessage());

        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dspMockRepo, times(1)).findById(provId);
        verify(this.dspMockRepo, times(0)).save(prov);
    }

    @Test
    void testCreateDataContentDefinition_withGdprPerspective() {
        // Assign ID manually since we're mocking the repository
        Long dspId = 1L;

        DataSharingParty dsp = DataSharingParty.builder()
                .id(dspId)
                .description("Service Test DSP")
                .build();

        // Required because the service checks this
        when(dspMockRepo.existsById(dspId)).thenReturn(true);

        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("Service DCD with GDPR")
                .provider(dsp)
                .build();

        DataContentPerspective dcp = new DataContentPerspective();
        dcp.setMetadataScheme(MetadataScheme.GDPR);
        dcp.setMetadata(Map.of(
                "lawfulBasis", "LEGITIMATE_INTERESTS",
                "specialCategory", "POLITICAL"
        ));
        dcd.addPerspective(dcp);

        // Assume mock dcdRepo returns the same object with an ID
        when(dcdMockRepo.save(any())).thenAnswer(invocation -> {
            DataContentDefinition saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        Long id = dcdService.createDataContentDefinition(dcd);
        assertNotNull(id);
        assertEquals(42L, id);
    }


    @Test
    void testFindDataContentDefinitions() {
        Long dspId = 1L;

        DataSharingParty dsp = DataSharingParty.builder()
                .id(dspId)
                .description("Service Test DSP")
                .build();

        DataContentDefinition dcd1 = DataContentDefinition.builder()
                .name("Service DCD with GDPR")
                .provider(dsp)
                .build();

        DataContentPerspective dcp1 = new DataContentPerspective();
        dcp1.setMetadataScheme(MetadataScheme.GDPR);
        dcp1.setMetadata(Map.of(
                "lawfulBasis", LawfulBasis.NOT_PERSONAL_DATA,
                "specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA
        ));
        dcd1.addPerspective(dcp1);


        DataContentDefinition dcd2 = DataContentDefinition.builder()
                .name("Service DCD with GDPR")
                .provider(dsp)
                .build();

        DataContentPerspective dcp2 = new DataContentPerspective();
        dcp2.setMetadataScheme(MetadataScheme.GDPR);
        dcp2.setMetadata(Map.of(
                "lawfulBasis", LawfulBasis.CONTRACT,
                "specialCategory", SpecialCategoryData.POLITICAL
        ));
        dcd2.addPerspective(dcp2);

        List<DataContentDefinition> dcds = List.of(dcd1, dcd2);
        when(this.dcdMockRepo.findByProviderId(dspId)).thenReturn(dcds);

        // When: the controller method is called
        List<DataContentDefinition> result = dcdService.getDataContentDefinitions(dspId);

        // Then expect...
        assertEquals(2, result.size(), "Method returns two dcds");

        verify(this.dcdMockRepo, times(1)).findByProviderId(dspId);

    }

    @Test
    void testFindDataContentDefinitionsWithNullDcds() {

        // Given: DCDs returned is null
        List<DataContentDefinition> dcds = null;
        Long dspId = 1L;

        // When: the controller method is called
        when(this.dcdMockRepo.findByProviderId(dspId)).thenReturn(dcds);
        List<DataContentDefinition> result = dcdService.getDataContentDefinitions(dspId);

        // Then expect...
        assertEquals(0, result.size(), "Method returns zero DCDs");

        verify(this.dcdMockRepo, times(1)).findByProviderId(dspId);

    }


}