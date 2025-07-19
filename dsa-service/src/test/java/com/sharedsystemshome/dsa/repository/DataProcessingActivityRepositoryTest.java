package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.DataProcessingActionType;
import com.sharedsystemshome.dsa.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataProcessingActivityRepositoryTest {

    @Autowired
    DataProcessingActivityRepository testSubject;

    @Autowired
    CustomerAccountRepository customerRepo;

    @Autowired
    DataProcessorRepository dpRepo;

    @Autowired
    DataContentDefinitionRepository dcdRepo;

    @BeforeEach
    void setUp() {


    }

    @AfterEach
    void tearDown() {

    }

    private CustomerAccount createCustomerAccount() {

        String randomHash =
                UUID.randomUUID().toString().replace("-", "").substring(0, 8); // 8-char hash

        // Create a Customer Account
        return CustomerAccount.builder()
                .name("Test Customer " + randomHash)
                .departmentName("Test Customer " + randomHash + " Dept")
                .url("www.testcust" + randomHash + ".com")
                .address(new Address())
                .dataSharingParty(
                        DataSharingParty.builder()
                                .description("Test Customer " + randomHash + " description")
                                .build()
                )
                .build();
    }

    private DataProcessor createDataProcessor(DataSharingParty dsp) {

        String randomHash =
                UUID.randomUUID().toString().replace("-", "").substring(0, 8); // 8-char hash

        // Create Data Processor
        return DataProcessor.builder()
                .name("Test DP " + randomHash)
                .website("www.dp" + randomHash + ".com")
                .controller(dsp)
                .build();
    }

    private DataContentDefinition createDataContentDefinition(DataSharingParty dsp) {

        String randomHash =
                UUID.randomUUID().toString().replace("-", "").substring(0, 8); // 8-char hash

        // Create DCD
        return DataContentDefinition.builder()
                .name("Test DCD " + randomHash)
                .dataContentType(DataContentType.NOT_SPECIFIED)
                .provider(dsp)
                .ownerEmail(randomHash + "@email.com")
                .sourceSystem("Test DCD "+ randomHash + " System")
                .build();
    }

    @Test
    void testSave() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given

        String name = "DPV A";
        String desc = "DPV A Description";

        DataProcessingActivity dpa = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .description(desc)
                .build();

        // When
        DataProcessingActivity saved = this.testSubject.save(dpa);

        // Then

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(name, saved.getName());
        assertEquals(desc, saved.getDescription());
        assertEquals(dp.getId(), saved.getDataProcessor().getId());
        assertEquals(dcd.getId(), saved.getDataContentDefinition().getId());

    }

    @Test
    void testSave_WithMinimalDataset() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));


        // Given

        String name = "DPV A";

        DataProcessingActivity dpa = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        // When
        DataProcessingActivity saved = this.testSubject.save(dpa);

        // Then

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(name, saved.getName());
        assertEquals(null, saved.getDescription());
        assertEquals(dp.getId(), saved.getDataProcessor().getId());
        assertEquals(dcd.getId(), saved.getDataContentDefinition().getId());

    }

    @Test
    void testSave_WithMissingRequiredData() {

        // Given
        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .build();

        // Then
        Exception ex = assertThrows(Exception.class, () -> {
            // When
            this.testSubject.saveAndFlush(dpv);
        });

        assertEquals(ConstraintViolationException.class.getName(), ex.getClass().getName());

    }

    @Test
    void testAddActionPerformed() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given
        String name = "DPV A";

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        DataProcessingActivity saved = this.testSubject.save(dpv);

        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("Action 1 Description")
                .build();

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.TRANSFORM)
                .description("Action 2 Description")
                .build();

        // When
        dpv.addActionPerformed(dpa1);

        dpv.addActionPerformed(dpa2);

        this.testSubject.saveAndFlush(saved);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(2, saved.getActionsPerformed().size());
        assertNotNull(saved.getActionsPerformed().get(0).getId());
        assertTrue(saved.getActionsPerformed().contains(saved.getActionsPerformed().get(0)));
        assertNotNull(saved.getActionsPerformed().get(1).getId());
        assertTrue(saved.getActionsPerformed().contains(saved.getActionsPerformed().get(1)));


    }
    @Test
    void testAddActionPerformed_WithDuplicateAction() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given
        String name = "DPV A";

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        DataProcessingActivity saved = this.testSubject.save(dpv);

        // When
        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("") // Not duplicate of dpa1
                .build();

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("Action 1 Description")
                .build();

        DataProcessingAction dpa3 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .description("     ") // Effectively duplicates dpa1
                .build();

        DataProcessingAction dpa4 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.ACCESS)
                .build(); // Effectively duplicates dpa1


        dpv.addActionPerformed(dpa1);
        // Not a duplicate
        dpv.addActionPerformed(dpa2);
        // Duplicate
        dpv.addActionPerformed(dpa3);
        // Duplicate
        dpv.addActionPerformed(dpa4);

        this.testSubject.saveAndFlush(saved);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(2, saved.getActionsPerformed().size());
        assertNotNull(saved.getActionsPerformed().get(0).getId());
        assertTrue(saved.getActionsPerformed().contains(saved.getActionsPerformed().get(0)));
        assertNotNull(saved.getActionsPerformed().get(1).getId());
        assertTrue(saved.getActionsPerformed().contains(saved.getActionsPerformed().get(1)));
    }

    @Test
    void testAddActionPerformed_WithInvalidAction() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given
        String name = "DPV A";

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        DataProcessingActivity saved = this.testSubject.save(dpv);

        // When
        dpv.addActionPerformed(new DataProcessingAction());

        // Then
        Exception ex = assertThrows(Exception.class, () -> {
            // When
            this.testSubject.saveAndFlush(dpv);
        });

        assertEquals(ConstraintViolationException.class.getName(), ex.getClass().getName());
    }

    @Test
    void testRemoveActionPerformed1() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given
        String name = "DPV A";

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        DataProcessingActivity saved = this.testSubject.save(dpv);

        DataProcessingAction dpa = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.PROFILE)
                .description("Action 1 Description")
                .build();

        dpv.addActionPerformed(dpa);

        this.testSubject.saveAndFlush(saved);

        assertEquals(1, saved.getActionsPerformed().size());

        // When
        DataProcessingAction dpaRemove = saved.getActionsPerformed().get(0);
        dpv.removeActionPerformed(dpaRemove);

        this.testSubject.saveAndFlush(saved);

        assertTrue(dpv.getActionsPerformed().isEmpty());

    }

    @Test
    void testRemoveActionPerformed2() {

        DataSharingParty dsp = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dp = this.dpRepo.save(this.createDataProcessor(dsp));

        DataContentDefinition dcd = this.dcdRepo.save(this.createDataContentDefinition(dsp));

        // Given
        String name = "DPV A";

        DataProcessingActivity dpv = DataProcessingActivity.builder()
                .dataProcessor(dp)
                .dataContentDefinition(dcd)
                .name(name)
                .build();

        DataProcessingActivity saved = this.testSubject.save(dpv);

        DataProcessingAction dpa1 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.PROFILE)
                .description("Action 1 Description")
                .build();

        DataProcessingAction dpa2 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.RESTRICT)
                .description("Action 2 Description")
                .build();

        DataProcessingAction dpa3 = DataProcessingAction.builder()
                .actionType(DataProcessingActionType.SHARE)
                .description("Action 3 Description")
                .build();

        dpv.addActionPerformed(dpa1);
        dpv.addActionPerformed(dpa2);
        dpv.addActionPerformed(dpa3);

        this.testSubject.saveAndFlush(saved);
        
        assertEquals(3, saved.getActionsPerformed().size());

        // When
        DataProcessingAction dpaRemove = saved.getActionsPerformed().get(1);
        dpv.removeActionPerformed(dpaRemove);

        this.testSubject.saveAndFlush(saved);

        assertEquals(2, saved.getActionsPerformed().size());
        assertFalse(saved.getActionsPerformed().contains(dpaRemove));

    }

    @Test
    void testFindByDataProcessor_Controller_Id() {

        DataSharingParty dspA = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dpA1 = this.dpRepo.save(this.createDataProcessor(dspA));
        DataProcessor dpA2 = this.dpRepo.save(this.createDataProcessor(dspA));

        DataContentDefinition dcdA1 = this.dcdRepo.save(this.createDataContentDefinition(dspA));
        DataContentDefinition dcdA2 = this.dcdRepo.save(this.createDataContentDefinition(dspA));


        DataSharingParty dspB = this.customerRepo.save(
                this.createCustomerAccount()).getDataSharingParty();

        DataProcessor dpB1 = this.dpRepo.save(this.createDataProcessor(dspB));

        DataContentDefinition dcdB1 = this.dcdRepo.save(this.createDataContentDefinition(dspB));


        // Given
        String nameA1 = "DPV A1";

        DataProcessingActivity dpvA1 = DataProcessingActivity.builder()
                .dataProcessor(dpA1)
                .dataContentDefinition(dcdA1)
                .name(nameA1)
                .build();

        String nameA2 = "DPV A2";

        DataProcessingActivity dpvA2 = DataProcessingActivity.builder()
                .dataProcessor(dpA2)
                .dataContentDefinition(dcdA2)
                .name(nameA2)
                .build();

        String nameB1 = "DPV B1";

        DataProcessingActivity dpvB1 = DataProcessingActivity.builder()
                .dataProcessor(dpB1)
                .dataContentDefinition(dcdB1)
                .name(nameB1)
                .build();

        // When
        List<DataProcessingActivity> dpvs = new ArrayList<>();
        dpvs.add(dpvA1);
        dpvs.add(dpvA2);
        dpvs.add(dpvB1);

        this.testSubject.saveAll(dpvs);

        List<DataProcessingActivity> foundAOnly =
                this.testSubject.findByDataProcessor_Controller_Id(dspA.getAccount().getId());

        // Then
        assertEquals(2, foundAOnly.size());
        assertTrue(foundAOnly.contains(dpvA1));
        assertTrue(foundAOnly.contains(dpvA2));
        assertFalse(foundAOnly.contains(dpvB1));


    }
}
