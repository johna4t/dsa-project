package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.enums.DataProcessingActionType;
import com.sharedsystemshome.dsa.model.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DataProcessingActivityRepositoryTest {

    final static String CUST_NAME = "Test Customer A";
    final static String CUST_DEPT = "Test Customer A - Dept A-1";
    final static String CUST_URL = "www.custA.com";
    final static String DP_WEBSITE = "www.dpA.com";
    final static String DP_NAME = "Test DP A";
    final static String DCD_NAME = "Test DCD A";
    final static String DCD_OWNER_EMAIL = "ownerDcdA@email.com";
    final static String DCD_SOURCE_SYST = "Test DCD A System";

    private CustomerAccount customer;
    private DataProcessor dataProcessor;
    private DataContentDefinition dcd;

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
        // Create a Customer Account
        return CustomerAccount.builder()
                .name(CUST_NAME)
                .departmentName(CUST_DEPT)
                .url(CUST_URL)
                .address(new Address())
                .dataSharingParty(
                        DataSharingParty.builder()
                                .description("Mid and South Essex NHS Foundation Trust")
                                .build()
                )
                .build();
    }

    private DataProcessor createDataProcessor(DataSharingParty dsp) {

        // Create Data Processor
        return DataProcessor.builder()
                .name(DP_NAME)
                .website(DP_WEBSITE)
                .controller(dsp)
                .build();
    }

    private DataContentDefinition createDataContentDefinition(DataSharingParty dsp) {
        // Create DCD
        return DataContentDefinition.builder()
                .name(DCD_NAME)
                .dataContentType(DataContentType.NOT_SPECIFIED)
                .provider(dsp)
                .ownerEmail(DCD_OWNER_EMAIL)
                .sourceSystem(DCD_SOURCE_SYST)
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
}
