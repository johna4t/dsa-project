package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.enums.MetadataScheme;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DataContentPerspectiveRepositoryTest {

    @Autowired
    private CustomerAccountRepository customerRepo;

    @Autowired
    private DataSharingPartyRepository dspRepo;

    @Autowired
    private DataContentDefinitionRepository dcdRepo;

    @Autowired
    private DataContentPerspectiveRepository dcpRepo;

    @Test
    void testSaveAndLoadGdprPerspective() {
        // Create and persist a DataSharingParty linked to the customer
        DataSharingParty dsp = DataSharingParty.builder()
                .description("Test DSP")
                .build();

        // Create and persist a customer account
        CustomerAccount account = CustomerAccount.builder()
                .name("Test Customer")
                .dataSharingParty(dsp)
                .build();
        customerRepo.save(account);

        // Create a DataContentDefinition linked to the DSP
        DataContentDefinition dcd = DataContentDefinition.builder()
                .name("DCD with GDPR Perspective")
                .provider(dsp)
                .build();

        // Create GDPR metadata perspective
        DataContentPerspective dcp = new DataContentPerspective();
        dcp.setMetadataScheme(MetadataScheme.GDPR);
        dcp.setMetadata(Map.of(
                "lawfulBasis", "CONSENT",
                "specialCategory", "HEALTH"
        ));

        // Link perspective to DCD
        dcd.addPerspective(dcp);

        // Save DCD — cascades to perspective
        dcdRepo.save(dcd);

        // Fetch and verify
        DataContentDefinition savedDcd = dcdRepo.findById(dcd.getId()).orElseThrow();
        assertEquals(1, savedDcd.getPerspectives().size());

        DataContentPerspective savedDcp = savedDcd.getPerspectives().get(0);
        assertEquals(MetadataScheme.GDPR, savedDcp.getMetadataScheme());
        assertEquals("CONSENT", savedDcp.get("lawfulBasis"));
        assertEquals("HEALTH", savedDcp.get("specialCategory"));
    }
}
