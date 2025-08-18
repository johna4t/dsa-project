package com.sharedsystemshome.dsa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.enums.*;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.repository.*;
import com.sharedsystemshome.dsa.security.model.Permission;
import com.sharedsystemshome.dsa.security.enums.PermissionType;
import com.sharedsystemshome.dsa.security.enums.RoleType;
import com.sharedsystemshome.dsa.security.model.Role;
import com.sharedsystemshome.dsa.security.repository.PermissionRepository;
import com.sharedsystemshome.dsa.security.repository.RoleRepository;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@PropertySource("classpath:config.properties")
@RequiredArgsConstructor
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private final Map<String, DataContentDefinition> dcds = new HashMap<>();
    private final Map<String, DataContentPerspective> dcps = new HashMap<>();
    private final Map<String, CustomerAccount> customers = new HashMap<>();
    private final Map<String, DataSharingParty> dsps = new HashMap<>();
    private final Map<String, DataSharingAgreement> dsas = new HashMap<>();
    private final Map<String, DataFlow> dfs = new HashMap<>();
    private final Map<String, Role> roles = new HashMap<>();
    private final Map<String, Permission> permissions = new HashMap<>();
    private final Map<String, DataProcessor> processors = new HashMap<>();
    private final Map<String, DataProcessingActivity> activities = new HashMap<>();
    private final Map<String, DataProcessingAction> actions = new HashMap<>();
    private final Map<String, UserAccount> users = new HashMap<>();


    @Bean
    CommandLineRunner commandLineRunner(DataContentDefinitionRepository dcdRepo,
                                        DataContentPerspectiveRepository dcpRepo,
                                        DataSharingPartyRepository dspRepo,
                                        DataSharingAgreementRepository dsaRepo,
                                        DataFlowRepository dfRepo,
                                        RoleRepository roleRepo,
                                        PermissionRepository permissionRepo,
                                        CustomerAccountRepository customerRepo,
                                        DataProcessorRepository processorRepo,
                                        DataProcessingActivityRepository activityRepo,
                                        DataProcessingActionRepository actionRepo,
                                        PasswordEncoder encoder
    ){
        logger.info("Entering method ApplicationConfig::commandLineRunner");
        return args -> {

            //DSP
            this.createDataSharingParties(dspRepo);

            //CustomerAccount is instantiated with DSP
            this.createCustomerAccounts(customerRepo);

            //DCD is instantiated with DSP
            this.createDataContentDefinitions(dcdRepo);

            //DCD is instantiated with owning DCD
            this.createDataContentPerspectives(dcpRepo);

            //DCD is instantiated with owning DCD
            this.createDataProcessors(processorRepo);

            // DPA is instantiated with DCD and DataProcessor
            this.createDataProcessingActivities(activityRepo);

            // DPA is instantiated with DCD and DataProcessor
            this.createDataProcessingActions(actionRepo);

            //DSA is instantiated with CustomerAccount
            this.createDataSharingAgreements(dsaRepo);

            //DataFlow is instantiated with DSA, DSP and DCD
            this.createDataFlows(dfRepo);

            this.createPermissions(permissionRepo);

            //Role is instantiated with Permission
            this.createRoles(roleRepo);

            //UserAccount is instantiated with Role and CustomerAccount
            this.createUserAccounts(userRepo, encoder);



        };

    }

    private void createDataSharingParties(DataSharingPartyRepository dspRepo){

        //Create Customer A DataSharingParty
        DataSharingParty dspA = DataSharingParty.builder()
                .description("Test DSP A description")
                .build();

        //Create Customer B DataSharingParty
        DataSharingParty dspB = DataSharingParty.builder()
                .description("Test DSP B description")
                .build();

        //Create Customer C DataSharingParty
        DataSharingParty dspC = DataSharingParty.builder()
                .description("Test DSP C description")
                .build();

        //Create additional DataSharingParty
        DataSharingParty dsp99 = DataSharingParty.builder()
                .description("Test DSP 99 description")
                .build();

        // Will be saved to db by parent CustomerAccount entity
        this.dsps.put("dspA", dspA);
        this.dsps.put("dspB", dspB);
        this.dsps.put("dspC", dspC);
        this.dsps.put("dsp99", dsp99);

    }

    private void createCustomerAccounts(CustomerAccountRepository customerRepo){

        CustomerAccount custA = CustomerAccount.builder()
                .name("The Avengers")
                .departmentName("Test DSP A Dept")
                .url("www.theavengers.com")
                .branchName("BU 1")
                .dataSharingParty(this.dsps.get("dspA"))
                .build();

        Address addressA = custA.getAddress();
        addressA.setAddressLine1("No. 10 Penny Lane");
        addressA.setAddressLine2("Anytown");
        addressA.setPostalCode("AB1 2DE");

        custA.setAddress(addressA);

        // Saves child DataSharingParty entity
        customerRepo.save(custA);

        CustomerAccount custB = CustomerAccount.builder()
                .name("Fantastic Four")
                .departmentName("Test DSP B Dept")
                .url("www.fantasticfour.com")
                .branchName("BU 2")
                .dataSharingParty(this.dsps.get("dspB"))
                .build();

        Address addressB = custB.getAddress();
        addressB.setAddressLine1("No. 56 Easy Street");
        addressB.setAddressLine2("Smalltown");
        addressB.setPostalCode("FG3 4HI");

        custB.setAddress(addressB);

        // Saves child DataSharingParty entity
        customerRepo.save(custB);

        CustomerAccount custC = CustomerAccount.builder()
                .name("X-Men")
                .departmentName("Test DSP C Dept")
                .url("www.xmen.com")
                .branchName("BU 3")
                .dataSharingParty(this.dsps.get("dspC"))
                .build();

        custC.getAddress().setAddressLine1("11 Baker Street");
        custC.getAddress().setAddressLine2("Bigcity");
        custC.getAddress().setPostalCode("JK5 6LM");

        // Saves child DataSharingParty entity
        customerRepo.save(custC);

        CustomerAccount custS = CustomerAccount.builder()
                .name("Shared Systems")
                .departmentName("System Admin")
                .url("www.sharedsystemshome.com")
                .build();

        custC.getAddress().setAddressLine1("11 Baker Street");
        custC.getAddress().setAddressLine2("Bigcity");
        custC.getAddress().setPostalCode("JK5 6LM");

        // Saves child DataSharingParty entity
        customerRepo.save(custS);

        CustomerAccount cust99 = CustomerAccount.builder()
                .name("Test DSP 99")
                .departmentName("Test DSP 99 Dept")
                .url("www.cust99.com")
                .branchName("BU 99")
                .dataSharingParty(this.dsps.get("dsp99"))
                .build();

        cust99.getAddress().setAddressLine1("99 Beasley Street");
        cust99.getAddress().setAddressLine2("Oldtown");
        cust99.getAddress().setPostalCode("WX9 9YZ");

        // Saves child DataSharingParty entity
        customerRepo.save(cust99);

        this.customers.put("custA", custA);
        this.customers.put("custB", custB);
        this.customers.put("custC", custC);
        this.customers.put("custS", custS);
        this.customers.put("cust99", cust99);

    }

    private void createDataContentPerspectives(DataContentPerspectiveRepository dcpRepo) {

        DataContentPerspective dcpA = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.CONSENT.name(),
                        "specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA.name(),
                        "article9Condition", Article9Condition.NOT_APPLICABLE.name()
                        )
                )
                .dataContentDefinition(this.dcds.get("dcdA"))
                .build();

        DataContentPerspective dcpA2 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                                "lawfulBasis", LawfulBasis.NOT_PERSONAL_DATA.name(),
                                "specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA.name(),
                                "article9Condition", Article9Condition.NOT_APPLICABLE.name()
                        )
                )
                .dataContentDefinition(this.dcds.get("dcdA2"))
                .build();

        DataContentPerspective dcpA3 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                                "lawfulBasis", LawfulBasis.LEGITIMATE_INTERESTS.name(),
                                "specialCategory", SpecialCategoryData.POLITICAL.name(),
                                "article9Condition", Article9Condition.REASONS_OF_PUBLIC_INTEREST.name()
                        )
                )
                .dataContentDefinition(this.dcds.get("dcdA3"))
                .build();

        DataContentPerspective dcpB = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.CONTRACT.name(),
                        "specialCategory", SpecialCategoryData.HEALTH.name(),
                        "article9Condition", Article9Condition.PUBLIC_HEALTH.name()
                    )
                )
                .dataContentDefinition(this.dcds.get("dcdB"))
                .build();

        DataContentPerspective dcpC = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.LEGITIMATE_INTERESTS.name(),
                        "specialCategory", SpecialCategoryData.POLITICAL.name(),
                        "article9Condition", Article9Condition.VITAL_INTERESTS.name()
                        )
                )
                .dataContentDefinition(this.dcds.get("dcdC"))
                .build();

        DataContentPerspective dcp99 = DataContentPerspective.builder()
                .metadataScheme(MetadataScheme.GDPR)
                .metadata(Map.of(
                        "lawfulBasis", LawfulBasis.NOT_PERSONAL_DATA.name(),
                        "specialCategory", SpecialCategoryData.NOT_SPECIAL_CATEGORY_DATA.name(),
                        "article9Condition", Article9Condition.NOT_APPLICABLE.name()
                        )
                )
                .dataContentDefinition(this.dcds.get("dcd99"))
                .build();

        dcpRepo.saveAll(List.of(dcpA, dcpA2, dcpA3, dcpB, dcpC, dcp99));

        this.dcps.put("dcpA", dcpA);
        this.dcps.put("dcpA2", dcpA2);
        this.dcps.put("dcpA3", dcpA3);
        this.dcps.put("dcpB", dcpB);
        this.dcps.put("dcpC", dcpC);
        this.dcps.put("dcp99", dcp99);

    }

    private void createDataProcessors(DataProcessorRepository dpRepo) {

        //Create Data Processors with DataSharingParty controller

        DataProcessor dpA = DataProcessor.builder()
                .name("Test Processor A")
                .description("Test Processor A description")
                .controller(this.dsps.get("dspA"))
                .email("a.someonea@email.com")
                .certifications(List.of(
                        ProcessingCertificationStandard.ISO_IEC_27001,
                        ProcessingCertificationStandard.NIST_SP,
                        ProcessingCertificationStandard.ISO_IEC_20000_1
                ))
                .website("pa.com")
                .build();

        DataProcessor dpB = DataProcessor.builder()
                .name("Test Processor A")
                .description("Test Processor B description")
                .controller(this.dsps.get("dspB"))
                .email("b.someonea@email.com")
                .certifications(List.of(
                        ProcessingCertificationStandard.ISO_IEC_27031,
                        ProcessingCertificationStandard.ISO_IEC_27701,
                        ProcessingCertificationStandard.NIST_SP,
                        ProcessingCertificationStandard.COBIT,
                        ProcessingCertificationStandard.ISO_IEC_27018
                ))
                .website("pb.com")
                .build();

        DataProcessor dpC = DataProcessor.builder()
                .name("Test Processor C")
                .description("Test Processor C description")
                .controller(this.dsps.get("dspC"))
                .email("c.someonea@email.com")
                .certifications(List.of(
                        ProcessingCertificationStandard.NIST_SP
                ))
                .website("pc.com")
                .build();

        DataProcessor dp99 = DataProcessor.builder()
                .name("Test Processor 99")
                .description("Test Processor 99 description")
                .controller(this.dsps.get("dsp99"))
                .email("someonea@email.com")
                .website("p99.com")
                .build();

        dpRepo.saveAll(List.of(dpA, dpB, dpC, dp99));

        this.processors.put("dpA", dpA);
        this.processors.put("dpB", dpB);
        this.processors.put("dpC", dpC);
        this.processors.put("dp99", dp99);

    }

    private void createDataContentDefinitions(DataContentDefinitionRepository dcdRepo){

        //Create DCDs against provider DataSharingParty
        DataContentDefinition dcdA = DataContentDefinition.builder()
                .name("Test DCD A")
                .description("Test DCD A description")
                .provider(this.dsps.get("dspA"))
                .ownerEmail("a.someonea@email.com")
                .sourceSystem("System A")
                .retentionPeriod(Period.ofYears(5))
                .build();

        DataContentDefinition dcdA2 = DataContentDefinition.builder()
                .name("Test DCD A2")
                .description("Test DCD A2 description")
                .provider(this.dsps.get("dspA"))
                .ownerEmail("a2.someonea@email.com")
                .sourceSystem("System A2")
                .retentionPeriod(Period.ofYears(1))
                .build();

        DataContentDefinition dcdA3 = DataContentDefinition.builder()
                .name("Test DCD A3")
                .description("Test DCD A3 description")
                .provider(this.dsps.get("dspA"))
                .ownerEmail("a3.someonea@email.com")
                .sourceSystem("System A3")
                .retentionPeriod(Period.ofMonths(18))
                .build();

        DataContentDefinition dcdB = DataContentDefinition.builder()
                .name("Test DCD B")
                .description("Test DCD B description")
                .provider(this.dsps.get("dspB"))
                .ownerEmail("b.someone@email.com")
                .sourceSystem("System B")
                .retentionPeriod(Period.ofMonths(36))
                .build();

        DataContentDefinition dcdC = DataContentDefinition.builder()
                .name("Test DCD C")
                .description("Test DCD C description")
                .provider(this.dsps.get("dspC"))
                .ownerEmail("c.someone@email.com")
                .sourceSystem("System C")
                .retentionPeriod(Period.ofDays(117))
                .build();

        DataContentDefinition dcd99 = DataContentDefinition.builder()
                .name("Test DCD 99")
                .description("Test DCD 99 description")
                .provider(this.dsps.get("dsp99"))
                .ownerEmail("someone.99@email.com")
                .sourceSystem("System 99")
                .retentionPeriod(Period.ofWeeks(27))
                .build();

        dcdRepo.saveAll(List.of(dcdA, dcdA2, dcdA3, dcdB, dcdC, dcd99));

        this.dcds.put("dcdA", dcdA);
        this.dcds.put("dcdA2", dcdA2);
        this.dcds.put("dcdA3", dcdA3);
        this.dcds.put("dcdB", dcdB);
        this.dcds.put("dcdC", dcdC);
        this.dcds.put("dcd99", dcd99);

    }

    private void createDataProcessingActivities(DataProcessingActivityRepository dpvRepo){

        DataProcessingActivity dpvA = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dpA"))
                .dataContentDefinition(this.dcds.get("dcdA"))
                .name("Test DP Activity A")
                .description("Test DP Activity A description.")
                .build();

        DataProcessingActivity dpvA2 = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dpA"))
                .dataContentDefinition(this.dcds.get("dcdA2"))
                .name("Test DP Activity A2")
                .description("Test DP Activity A2 description.")
                .build();

        DataProcessingActivity dpvA3 = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dpA"))
                .dataContentDefinition(this.dcds.get("dcdA3"))
                .name("Test DP Activity A3")
                .description("Test DP Activity A3 description.")
                .build();

        DataProcessingActivity dpvB = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dpB"))
                .dataContentDefinition(this.dcds.get("dcdB"))
                .name("Test DP Activity B")
                .description("Test DP Activity B description.")
                .build();

        DataProcessingActivity dpvC = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dpC"))
                .dataContentDefinition(this.dcds.get("dcdC"))
                .name("Test DP Activity C")
                .description("Test DP Activity C description.")
                .build();

        DataProcessingActivity dpv99 = DataProcessingActivity.builder()
                .dataProcessor(this.processors.get("dp99"))
                .dataContentDefinition(this.dcds.get("dcd99"))
                .name("Test DP Activity 99")
                .description("Test DP Activity 9 description.")
                .build();


        dpvRepo.saveAll(List.of(dpvA, dpvA2, dpvA3,dpvB, dpvC, dpv99));

        this.activities.put("dpvA", dpvA);
        this.activities.put("dpvA2", dpvA2);
        this.activities.put("dpvA3", dpvA3);
        this.activities.put("dpvB", dpvB);
        this.activities.put("dpvC", dpvC);
        this.activities.put("dpv99", dpv99);

    }

    private void createDataProcessingActions(DataProcessingActionRepository dpaRepo){

        DataProcessingAction dpaA_1 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA"))
                .actionType(DataProcessingActionType.ACCESS)
                .description("Test DP Action A.1 description.")
                .build();

        DataProcessingAction dpaA_2 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA"))
                .actionType(DataProcessingActionType.ANALYSIS)
                .description("Test DP Action A.2 description.")
                .build();

        DataProcessingAction dpaA_3 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA"))
                .actionType(DataProcessingActionType.PSEUDONYMISATION)
                .description("Test DP Action A.3 description.")
                .build();

        DataProcessingAction dpaA2_1 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA2"))
                .actionType(DataProcessingActionType.PROTECTION)
                .description("Test DP Action A2.1 description.")
                .build();

        DataProcessingAction dpaA2_2 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA2"))
                .actionType(DataProcessingActionType.USE)
                .description("Test DP Action A2.2 description.")
                .build();

        DataProcessingAction dpaA2_3 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA2"))
                .actionType(DataProcessingActionType.AGGREGATION)
                .description("Test DP Action A2.3 description.")
                .build();

        DataProcessingAction dpaA3_1 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA3"))
                .actionType(DataProcessingActionType.TRANSFORMATION)
                .description("Test DP Action A3.1 description.")
                .build();

        DataProcessingAction dpaA3_2 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA3"))
                .actionType(DataProcessingActionType.PROFILING)
                .description("Test DP Action A3.2 description.")
                .build();

        DataProcessingAction dpaA3_3 = DataProcessingAction.builder()
                .processingActivity(this.activities.get("dpvA3"))
                .actionType(DataProcessingActionType.ORGANISATION)
                .description("Test DP Action A3.3 description.")
                .build();

        dpaRepo.saveAll(List.of(dpaA_1, dpaA_2, dpaA_2, dpaA2_1, dpaA2_2, dpaA2_3, dpaA3_1, dpaA3_2, dpaA3_3));

        this.actions.put("dpaA_1", dpaA_1);
        this.actions.put("dpaA_2", dpaA_2);
        this.actions.put("dpaA_3", dpaA_3);
        this.actions.put("dpaA2_1", dpaA2_1);
        this.actions.put("dpaA2_2", dpaA2_2);
        this.actions.put("dpaA2_3", dpaA2_3);
        this.actions.put("dpaA3_1", dpaA3_1);
        this.actions.put("dpaA3_2", dpaA3_2);
        this.actions.put("dpaA3_3", dpaA3_3);

    }

    private void createDataSharingAgreements(DataSharingAgreementRepository dsaRepo){

        //Create DSA with provider and consumer Organisations
        DataSharingAgreement dsaA = DataSharingAgreement.builder()
                .name("Test DSA A")
                .accountHolder(this.customers.get("custA"))
                .build();

        DataSharingAgreement dsaB = DataSharingAgreement.builder()
                .name("Test DSA B")
                .accountHolder(this.customers.get("custB"))
                .build();

        DataSharingAgreement dsaC = DataSharingAgreement.builder()
                .name("Test DSA C")
                .accountHolder(this.customers.get("custC"))
                .build();

        DataSharingAgreement dsa99 = DataSharingAgreement.builder()
                .name("Test DSA 99")
                .accountHolder(this.customers.get("cust99"))
                .build();

        dsaRepo.saveAll(List.of(dsaA, dsaB, dsaC, dsa99));

        this.dsas.put("dsaA", dsaA);
        this.dsas.put("dsaB", dsaB);
        this.dsas.put("dsaC", dsaC);
        this.dsas.put("dsa99", dsa99);

    }

    private void createDataFlows(DataFlowRepository dfRepo){

        //Create DataFlow with provider and consumer Organisations, and DCD
        DataFlow dataFlowA = DataFlow.builder()
                .purposeOfSharing("Data Flow A purpose")
                .dataSharingAgreement(this.dsas.get("dsaA"))
                .provider(this.dsps.get("dspA"))
                .consumer(this.dsps.get("dspB"))
                .lawfulBasis(LawfulBasis.CONSENT)
                .dataContent(List.of(this.dcds.get("dcdA")))
                .build();

        DataFlow dataFlowB = DataFlow.builder()
                .purposeOfSharing("Data Flow B purpose")
                .dataSharingAgreement(this.dsas.get("dsaB"))
                .provider(this.dsps.get("dspC"))
                .consumer(this.dsps.get("dspB"))
                .dataContent(List.of(this.dcds.get("dcdC")))
                .build();

        DataFlow dataFlowC = DataFlow.builder()
                .purposeOfSharing("Data Flow B purpose")
                .dataSharingAgreement(this.dsas.get("dsaC"))
                .provider(this.dsps.get("dspC"))
                .consumer(this.dsps.get("dspA"))
                .dataContent(List.of(this.dcds.get("dcdC")))
                .build();

        dfRepo.saveAll(List.of(dataFlowA, dataFlowB, dataFlowC));

        this.dfs.put("dataFlowA", dataFlowA);
        this.dfs.put("dataFlowB", dataFlowB);
        this.dfs.put("dataFlowB", dataFlowC);

    }

    private void createPermissions(PermissionRepository permissionRepo){

        //Authorisation

        Permission read = Permission.builder()
                .name(PermissionType.PERMIT_READ)
                .build();

        Permission update = Permission.builder()
                .name(PermissionType.PERMIT_UPDATE)
                .build();

        Permission create = Permission.builder()
                .name(PermissionType.PERMIT_CREATE)
                .build();

        Permission delete = Permission.builder()
                .name(PermissionType.PERMIT_DELETE)
                .build();

        //SUPER_ADMIN role permissions
        Permission superRead = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_READ)
                .build();
        Permission superUpdate = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_UPDATE)
                .build();
        Permission superCreate = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_CREATE)
                .build();
        Permission superDelete = Permission.builder()
                .name(PermissionType.SUPER_ADMIN_DELETE)
                .build();

        //ACCOUNT_ADMIN role permissions
        Permission adminRead = Permission.builder()
                .name(PermissionType.ACCOUNT_ADMIN_READ)
                .build();
        Permission adminUpdate = Permission.builder()
                .name(PermissionType.ACCOUNT_ADMIN_UPDATE)
                .build();
        Permission adminCreate = Permission.builder()
                .name(PermissionType.ACCOUNT_ADMIN_CREATE)
                .build();
        Permission adminDelete = Permission.builder()
                .name(PermissionType.ACCOUNT_ADMIN_DELETE)
                .build();

        //MEMBER role permissions
        Permission memberRead = Permission.builder()
                .name(PermissionType.MEMBER_READ)
                .build();
        Permission memberUpdate = Permission.builder()
                .name(PermissionType.MEMBER_UPDATE)
                .build();
        Permission memberCreate = Permission.builder()
                .name(PermissionType.MEMBER_CREATE)
                .build();
        Permission memberDelete = Permission.builder()
                .name(PermissionType.MEMBER_DELETE)
                .build();

        //ASSOCIATE role permissions
        Permission assocRead = Permission.builder()
                .name(PermissionType.ASSOCIATE_READ)
                .build();
        Permission assocUpdate = Permission.builder()
                .name(PermissionType.ASSOCIATE_UPDATE)
                .build();
        Permission assocCreate = Permission.builder()
                .name(PermissionType.ASSOCIATE_CREATE)
                .build();
        Permission assocDelete = Permission.builder()
                .name(PermissionType.ASSOCIATE_DELETE)
                .build();

        //USER role permissions
        Permission userRead = Permission.builder()
                .name(PermissionType.USER_READ)
                .build();


        permissionRepo.saveAll(List.of(
                superRead,
                superUpdate,
                superCreate,
                superDelete,
                adminRead,
                adminUpdate,
                adminCreate,
                adminDelete,
                memberRead,
                memberUpdate,
                memberCreate,
                memberDelete,
                assocRead,
                assocUpdate,
                assocCreate,
                assocDelete,
                userRead
        ));

        this.permissions.put("superRead", superRead);
        this.permissions.put("superUpdate", superUpdate);
        this.permissions.put("superCreate", superCreate);
        this.permissions.put("superDelete", superDelete);
        this.permissions.put("adminRead", adminRead);
        this.permissions.put("adminUpdate", adminUpdate);
        this.permissions.put("adminCreate", adminCreate);
        this.permissions.put("adminDelete", adminDelete);
        this.permissions.put("memberRead", memberRead);
        this.permissions.put("memberUpdate", memberUpdate);
        this.permissions.put("memberCreate", memberCreate);
        this.permissions.put("memberDelete", memberDelete);
        this.permissions.put("assocRead", assocRead);
        this.permissions.put("assocUpdate", assocUpdate);
        this.permissions.put("assocCreate", assocCreate);
        this.permissions.put("assocDelete", assocDelete);
        this.permissions.put("userRead", userRead);

    }

    void createRoles(RoleRepository roleRepo){

        Role superAdmin = Role.builder()
                .name(RoleType.SUPER_ADMIN)
                .permissions(List.of(
                                this.permissions.get("superRead"),
                                this.permissions.get("superUpdate"),
                                this.permissions.get("superCreate"),
                                this.permissions.get("superDelete")
                        )
                )
                .build();

        Role accountAdmin = Role.builder()
                .name(RoleType.ACCOUNT_ADMIN)
                .permissions(List.of(
                                this.permissions.get("adminRead"),
                                this.permissions.get("adminUpdate"),
                                this.permissions.get("adminCreate"),
                                this.permissions.get("adminDelete")
                        )
                )
                .build();

        Role member = Role.builder()
                .name(RoleType.MEMBER)
                .permissions(List.of(
                                this.permissions.get("memberRead"),
                                this.permissions.get("memberUpdate"),
                                this.permissions.get("memberCreate"),
                                this.permissions.get("memberDelete")
                        )
                )
                .build();

        Role associate = Role.builder()
                .name(RoleType.ASSOCIATE)
//                    .permissions(List.of(assocRead, assocUpdate, assocCreate, assocDelete))
                .permissions(List.of(
                                this.permissions.get("assocRead"),
                                this.permissions.get("assocUpdate"),
                                this.permissions.get("assocCreate"),
                                this.permissions.get("assocDelete")
                        )
                )
                .build();

        Role user = Role.builder()
                .name(RoleType.USER)
//                    .permissions(List.of(userRead))
                .permissions(List.of(
                                this.permissions.get("userRead")
                        )
                )
                .build();

        roleRepo.saveAll(List.of(
                superAdmin,
                accountAdmin,
                member,
                associate,
                user
        ));

        this.roles.put("superAdmin", superAdmin);
        this.roles.put("accountAdmin", accountAdmin);
        this.roles.put("member", member);
        this.roles.put("associate", associate);
        this.roles.put("user", user);



    }

    private void createUserAccounts(UserAccountRepository userRepo, PasswordEncoder encoder) {

        UserAccount steve = UserAccount.builder()
                .parentAccount(this.customers.get("custA"))
                .firstName("Steve")
                .lastName("Rogers")
                .email("steve@avengers.com")
                .contactNumber("99999")
                .password(encoder.encode("captainamerica"))
                .roles(List.of(
                                this.roles.get("accountAdmin")
                        )
                )
                .build();
        userRepo.save(steve);

        UserAccount sam = UserAccount.builder()
                .parentAccount(this.customers.get("custA"))
                .firstName("Sam")
                .lastName("Wilson")
                .email("sam@avengers.com")
                .contactNumber("99999")
                .password(encoder.encode("thefalcon"))
                .roles(List.of(
                                this.roles.get("member")
                        )
                )
                .build();
        userRepo.save(sam);

        UserAccount reed = UserAccount.builder()
                .parentAccount(this.customers.get("custB"))
                .firstName("Reed")
                .lastName("Richards")
                .email("reed@fantastic4.com")
                .contactNumber("99999")
                .password(encoder.encode("mrfantastic"))
                .roles(List.of(
                                this.roles.get("accountAdmin")
                        )
                )
                .build();
        userRepo.save(reed);

        UserAccount susan = UserAccount.builder()
                .parentAccount(this.customers.get("custB"))
                .firstName("Susan")
                .lastName("Storm-Richards")
                .email("susan@fantastic4.com")
                .contactNumber("88888")
                .password(encoder.encode("invisiblewoman"))
                .roles(List.of(
                                this.roles.get("member")
                        )
                )
                .build();
        userRepo.save(susan);

        UserAccount charles = UserAccount.builder()
                .parentAccount(this.customers.get("custC"))
                .firstName("Charles")
                .lastName("Xavier")
                .email("charles@xmen.com")
                .contactNumber("99999")
                .password(encoder.encode("professorx"))
                .roles(List.of(
                                this.roles.get("accountAdmin")
                        )
                )
                .build();
        userRepo.save(charles);

        UserAccount james = UserAccount.builder()
                .parentAccount(this.customers.get("custC"))
                .firstName("James")
                .lastName("Howlett")
                .email("james@xmen.com")
                .contactNumber("99999")
                .password(encoder.encode("wolverine"))
                .roles(List.of(
                                this.roles.get("member")
                        )
                )
                .build();
        userRepo.save(james);

        UserAccount victor = UserAccount.builder()
                .parentAccount(this.customers.get("cust99"))
                .firstName("Victor")
                .lastName(" Von Doom")
                .email("victor@cust99.com")
                .contactNumber("99999")
                .password(encoder.encode("doctordoom"))
                .roles(List.of(
                                this.roles.get("accountAdmin")
                        )
                )
                .build();
        userRepo.save(victor);

        UserAccount john = UserAccount.builder()
                .parentAccount(this.customers.get("custS"))
                .firstName("John")
                .lastName("Arnett")
                .email("john.arnett@sharedsystemshome.com")
                .contactNumber("99999")
                .password(encoder.encode("12345"))
                .roles(List.of(
                        this.roles.get("superAdmin"),
                        this.roles.get("member")
                        )
                )
                .build();
        userRepo.save(john);




    }





    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    private final UserAccountRepository userRepo;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> this.userRepo.findByEmail(username)
                .orElseThrow(() -> new BusinessValidationException(
                        "User with email " + username + " not found.")
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public ObjectWriter objectWriter(){

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return mapper.writerWithDefaultPrettyPrinter();
    }

}
