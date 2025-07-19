package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.DataProcessingActionType;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataProcessingActivityRepository;
import com.sharedsystemshome.dsa.repository.DataProcessorRepository;
import com.sharedsystemshome.dsa.util.AddOrUpdateTransactionException;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataProcessingActivityServiceTest {

    @Mock
    private DataProcessingActivityRepository dpvMockRepo;

    @Mock
    private DataContentDefinitionRepository dcdMockRepo;

    @Mock
    private DataProcessorRepository dpMockRepo;

    private DataProcessingActivityService dpvService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.dpvService = new DataProcessingActivityService(
                this.dpvMockRepo,
                this.dcdMockRepo,
                this.dpMockRepo
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateDataProcessingActivity() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.existsById(dpId)).thenReturn(true);

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.existsById(dcdId)).thenReturn(true);

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();

        Long dpvId = 99L;
        when(this.dpvMockRepo.save(dpv))
                .thenAnswer(invocation -> {
                    dpv.setId(dpvId); // Simulate DB assigning an ID
                    return dpv;
                });

        Long result = this.dpvService.createDataProcessingActivity(dpv);

        assertNotNull(result);
        assertEquals(dpvId, result);
        verify(this.dpMockRepo, times(1)).existsById(dpId);
        verify(this.dcdMockRepo, times(1)).existsById(dcdId);
        verify(this.dpvMockRepo, times(1)).save(dpv);
    }

    @Test
    void testCreateDataProcessingActivity_WithMissingDcd() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.existsById(dpId)).thenReturn(true);

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .name("Test DPV")
                .dataProcessor(dp)
                .build();

        Exception ex = assertThrows(NullOrEmptyValueException.class, () -> {
            this.dpvService.createDataProcessingActivity(dpv);
        });

        assertNotNull(ex);
        assertEquals("Data Content Definition is null or empty.", ex.getMessage());
        verify(this.dpMockRepo, times(1)).existsById(dpId);
        verify(this.dcdMockRepo, times(0)).existsById(anyLong());
        verify(this.dpvMockRepo, times(0)).save(dpv);
    }

    @Test
    void testCreateDataProcessingActivity_WithInvalidDataProcessor() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.existsById(dpId)).thenReturn(false);

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.existsById(dcdId)).thenReturn(true);

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();

        Long dpvId = 99L;
        when(this.dpvMockRepo.save(dpv))
                .thenAnswer(invocation -> {
                    dpv.setId(dpvId); // Simulate DB assigning an ID
                    return dpv;
                });

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            this.dpvService.createDataProcessingActivity(dpv);
        });

        assertNotNull(ex);
        assertEquals("Data Processor with id = " + dpId + " not found.", ex.getMessage());
        verify(this.dpMockRepo, times(1)).existsById(dpId);
        verify(this.dcdMockRepo, times(0)).existsById(anyLong());
        verify(this.dpvMockRepo, times(0)).save(dpv);
    }

    @Test
    void testGetDataProcessingActivities() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.existsById(dpId)).thenReturn(true);

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.existsById(dcdId)).thenReturn(true);

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();

        List<DataProcessingActivity> dpvs = new ArrayList<>();
        dpvs.add(dpv);

        when(this.dpvMockRepo.findByDataProcessor_Controller_Id(anyLong())).thenReturn(dpvs);

        List<DataProcessingActivity> result = this.dpvService.getDataProcessingActivities(anyLong());

        assertNotNull(result);
        assertEquals(dpvs, result);
        verify(this.dpvMockRepo, times(1)).findByDataProcessor_Controller_Id(anyLong());
        assertEquals(1, result.size());
    }

    @Test
    void testGetDataProcessingActivityById() {

        Long dpvId = 99L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test DPV")
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();
        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        DataProcessingActivity result = this.dpvService.getDataProcessingActivityById(dpvId);

        assertNotNull(result);
        assertEquals(dpvId, result.getId());
        assertEquals(dpv.getName(), result.getName());
        assertEquals(dpv.getDataProcessor(), result.getDataProcessor());
        assertEquals(dpv.getDataContentDefinition(), result.getDataContentDefinition());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);

    }

    @Test
    void testGetDataProcessingActivityById_WithInvalidActivity() {

        // Given
        Long dpvId = 99L;

        // When
        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.empty());

        // Then
        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            // When
            this.dpvService.getDataProcessingActivityById(dpvId);
        });

        assertNotNull(ex);
        assertEquals("Data Processing Activity with id = " + dpvId + " not found.", ex.getMessage());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);
    }

    @Test
    void testGetDataProcessingActivities_IsEmpty() {

        List<DataProcessingActivity> dpvs = new ArrayList<>();
        when(this.dpvMockRepo.findByDataProcessor_Controller_Id(anyLong())).thenReturn(null);

        List<DataProcessingActivity> result = this.dpvService.getDataProcessingActivities(anyLong());

        assertNotNull(result);
        assertEquals(dpvs, result);
        verify(this.dpvMockRepo, times(1)).findByDataProcessor_Controller_Id(anyLong());
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateDataProcessingActivity() {

        Long dpvId = 99L;
        String oldName = "Test DPV";
        String oldDescription = "Test Description";
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name(oldName)
                .description(oldDescription)
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();

        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("Action 1 Description")
                .build();
        dpv.addActionPerformed(dpa1);

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.TRANSFORM)
                .description("Action 2 Description")
                .build();
        dpv.addActionPerformed(dpa2);

        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        String newName = "New DPV";
        String newDescription = "New Description";
        DataProcessingAction dpa3 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.AGGREGATE)
                .description("Action 3 Description")
                .build();

        this.dpvService.updateDataProcessingActivity(
                dpvId,
                newName,
                newDescription,
                List.of(dpa2, dpa3) // Remove dpa1 and add dpa3
        );

        List<DataProcessingAction> actions = dpv.getActionsPerformed();

        Boolean dpa1Exists = false;
        for (DataProcessingAction dpa : dpv.getActionsPerformed()) {
            if (dpa.getActionType() == DataProcessingActionType.ACCESS) {
                dpa1Exists = true;
                break;
            }
        }

        Boolean dpa2Exists = false;
        for (DataProcessingAction dpa : dpv.getActionsPerformed()) {
            if (dpa.getActionType() == DataProcessingActionType.TRANSFORM) {
                dpa2Exists = true;
                break;
            }
        }

        Boolean dpa3Exists = false;
        for (DataProcessingAction dpa : dpv.getActionsPerformed()) {
            if (dpa.getActionType() == DataProcessingActionType.AGGREGATE) {
                dpa3Exists = true;
                break;
            }
        }

        assertEquals(newName, dpv.getName());
        assertEquals(newDescription, dpv.getDescription());
        assertEquals(2, actions.size());
        assertFalse(dpa1Exists, "DPA 1 should have been removed.");
        assertTrue(dpa2Exists, "DPA 2 should have been retained.");
        assertTrue(dpa3Exists, "DPA 3 should have been added.");
        verify(this.dpvMockRepo, times(1)).findById(dpvId);

    }

    @Test
    void testUpdateDataProcessingActivity_WithEmptyData() {

        Long dpvId = 99L;
        String oldName = "Test DPV";
        String oldDescription = "Test Description";
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name(oldName)
                .description(oldDescription)
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();

        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("Action 1 Description")
                .build();
        dpv.addActionPerformed(dpa1);

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.TRANSFORM)
                .description("Action 2 Description")
                .build();
        dpv.addActionPerformed(dpa2);

        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        String newName = "";
        String newDescription = "";
        this.dpvService.updateDataProcessingActivity(
                dpvId,
                newName,
                newDescription,
                new ArrayList<>() // Remove all actions
        );

        List<DataProcessingAction> actions = dpv.getActionsPerformed();

        assertEquals(oldName, dpv.getName());
        assertEquals(newDescription, dpv.getDescription());
        assertTrue(actions.isEmpty());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);

    }

    @Test
    void testUpdateDataProcessingActivity_WithNullData() {

        Long dpvId = 99L;
        String oldName = "Test DPV";
        String oldDescription = "Test Description";
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name(oldName)
                .description(oldDescription)
                .dataProcessor(new DataProcessor())
                .dataContentDefinition(new DataContentDefinition())
                .build();

        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("Action 1 Description")
                .build();
        dpv.addActionPerformed(dpa1);

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.TRANSFORM)
                .description("Action 2 Description")
                .build();
        dpv.addActionPerformed(dpa2);

        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        this.dpvService.updateDataProcessingActivity(
                dpvId,
                null,
                null,
               null
        );

        List<DataProcessingAction> actions = dpv.getActionsPerformed();

        assertEquals(oldName, dpv.getName());
        assertEquals(oldDescription, dpv.getDescription());
        assertEquals(2, actions.size());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);

    }

    @Test
    void testDeleteDataProcessingActivity() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        Long dpvId = 20L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();
        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        this.dpvService.deleteDataProcessingActivity(dpvId);

        verify(this.dpvMockRepo, times(1)).findById(dpvId);
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dcdMockRepo, times(1)).save(dcd);
        verify(this.dpMockRepo, times(1)).save(dp);
    }

    @Test
    void testDeleteDataProcessingActivity_WithInvalidId() {

        Long dpvId = 20L;

        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
                this.dpvService.deleteDataProcessingActivity(dpvId);
            });

        assertNotNull(ex);
        assertEquals("Data Processing Activity with id = " + dpvId + " not found.", ex.getMessage());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);
        verify(this.dcdMockRepo, times(0)).findById(anyLong());
        verify(this.dpMockRepo, times(0)).findById(anyLong());
        verify(this.dcdMockRepo, times(0)).save(any());
        verify(this.dpMockRepo, times(0)).save(any());
    }

    @Test
    void testDeleteDataProcessingActivity_WithInvalidDataProcessorId() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.empty());

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        Long dpvId = 20L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();
        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));


        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            this.dpvService.deleteDataProcessingActivity(dpvId);
        });

        assertNotNull(ex);
        assertEquals("Data Processor with id = " + dpId + " not found.", ex.getMessage());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dcdMockRepo, times(0)).save(any());
        verify(this.dpMockRepo, times(0)).save(any());
    }

    @Test
    void testDeleteDataProcessingActivity_DcdSaveFails() {

        Long dpId = 5L;
        DataProcessor dp = DataProcessor.builder()
                .id(dpId)
                .name("Test Data Processor")
                .build();
        when(this.dpMockRepo.findById(dpId)).thenReturn(Optional.of(dp));

        Long dcdId = 10L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                .id(dcdId)
                .name("Test DCD")
                .build();
        when(this.dcdMockRepo.findById(dcdId)).thenReturn(Optional.of(dcd));

        Long dpvId = 20L;
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .id(dpvId)
                .name("Test DPV")
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .build();
        when(this.dpvMockRepo.findById(dpvId)).thenReturn(Optional.of(dpv));

        when(this.dcdMockRepo.save(dcd)).thenThrow(new RuntimeException("Simulated failure"));

        Exception ex = assertThrows(AddOrUpdateTransactionException.class, () -> {
            this.dpvService.deleteDataProcessingActivity(dpvId);
        });

        assertNotNull(ex);
        assertEquals("Unable to add or update Data Content Definition.", ex.getMessage());
        verify(this.dpvMockRepo, times(1)).findById(dpvId);
        verify(this.dcdMockRepo, times(1)).findById(dcdId);
        verify(this.dpMockRepo, times(1)).findById(dpId);
        verify(this.dcdMockRepo, times(1)).save(dcd);
        verify(this.dpMockRepo, times(0)).save(dp);
    }
}