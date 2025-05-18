package com.sharedsystemshome.dsa.service;
import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataContentPerspective;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.AddOrUpdateTransactionException;
import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.util.CustomValidator;
import com.sharedsystemshome.dsa.util.DeleteTransactionException;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.sharedsystemshome.dsa.util.BusinessValidationException;

@Service
@RequiredArgsConstructor
@Validated
public class DataContentDefinitionService {

    private static final Logger logger = LoggerFactory.getLogger(DataContentDefinitionService.class);

    private final DataContentDefinitionRepository dcdRepo;

    private final DataSharingPartyRepository dspRepo;

    private final CustomValidator<DataContentDefinition> validator;

    // CREATE
    public Long createDataContentDefinition (DataContentDefinition dcd) {
        logger.debug("Entering method (DataContentDefinition::createDataContentDefinition with dcd: {}", dcd);

        if (null == dcd) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_CONTENT_DEFINITION);
        }

        this.validator.validate(dcd);

        // Get id of parent provider DataSharingParty
        Long provId = dcd.getProvider().getId();

        if(null == provId){
            throw new NullOrEmptyValueException("Provider " + BusinessValidationException.DATA_SHARING_PARTY + " id");
        } else if(!this.dspRepo.existsById(provId)){
            throw new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, provId);
        }

        try {
            Long dcdId = this.dcdRepo.save(dcd).getId();
            logger.info("New DataContentDefinition created with id: {}", dcdId);

            return dcdId;
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_CONTENT_DEFINITION, e);
        }
    }

    //READ
    @GetMapping
    public DataContentDefinition getDataContentDefinitionById(Long id) {
        logger.debug("Entering method DataContentDefinition::getDataContentDefinitionById "
                + "with id: {}", id);

        DataContentDefinition dcd = this.dcdRepo.findById(id).orElseThrow(
                () -> {
                    return new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, id);
                }
        );

        logger.info("Found DataContentDefinition with id: {}", id);
        return dcd;
    }

    //READ ALL CUSTOMER DCDs
    @GetMapping
    public List<DataContentDefinition> getDataContentDefinitions(Long custId){
        logger.debug("Entering method DataContentDefinition::getDataContentDefinitions");

        List<DataContentDefinition> dcds = this.dcdRepo.findByProviderId(custId);

        if(null == dcds){
            // Return empty list
            dcds = new ArrayList<>();
        }

        logger.info("Found {} DataContentDefinitions", dcds.size());

        return dcds;
    }

    @Transactional
    public void updateDataContentDefinition(DataContentDefinition dcd){
        logger.debug("Entering method DataContentDefinition::updateDataContentDefinition");

        if (null == dcd) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_CONTENT_DEFINITION);
        }

        this.updateDataContentDefinition(
                dcd.getId(),
                dcd.getName(),
                dcd.getDescription(),
                dcd.getDataContentType(),
                dcd.getOwnerName(),
                dcd.getOwnerEmail(),
                dcd.getRetentionPeriod(),
                dcd.getSourceSystem(),
                dcd.getPerspectives()
        );

    }

    @Transactional
    public void updateDataContentDefinition(
            Long id,
            String name,
            String description,
            DataContentType dataContentType,
            String ownerName,
            String ownerEmail,
            Period retentionPeriod,
            String sourceSystem,
            List<DataContentPerspective> perspectives){

        logger.debug("Entering method DataContentDefinition::updateDataContentDefinition");

        DataContentDefinition dcd = this.dcdRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, id));

        if (null != name && !name.isEmpty()) {
            String oldName = dcd.getName();
            if(!Objects.equals(oldName, name)) {
                dcd.setName(name);
                logger.info("Updated value of property DataContentDefinition::name from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldName, name, id);
            }
        }
        if (null != description && !description.isEmpty()) {
            String oldDescription = dcd.getDescription();
            if(!Objects.equals(oldDescription, description)) {
                dcd.setDescription(description);
                logger.info("Updated value of property DataContentDefinition::description from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldDescription, description, id);
            }
        }
        if (null != dataContentType) {
            DataContentType oldDataContentType = dcd.getDataContentType();
            if(!Objects.equals(oldDataContentType, dataContentType)) {
                dcd.setDataContentType(dataContentType);
                logger.info("Updated value of property DataContentDefinition::dataContentType from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldDataContentType, dataContentType, id);
            }
        }
        if (null != ownerName && !ownerName.isEmpty()) {
            String oldOwnerName = dcd.getOwnerName();
            if(!Objects.equals(oldOwnerName, ownerName)) {
                dcd.setOwnerName(ownerName);
                logger.info("Updated value of property DataContentDefinition::ownerName from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldOwnerName, ownerName, id);
            }
        }
        if (null != ownerEmail && !ownerEmail.isEmpty()) {
            String oldOwnerEmail = dcd.getOwnerEmail();
            if(!Objects.equals(oldOwnerEmail, ownerEmail)) {
                dcd.setOwnerName(ownerEmail);
                logger.info("Updated value of property DataContentDefinition::ownerEmail from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldOwnerEmail, ownerEmail, id);
            }
        }
        if (null != retentionPeriod) {
            Period oldRetentionPeriod = dcd.getRetentionPeriod();
            if(!Objects.equals(oldRetentionPeriod, retentionPeriod)) {
                dcd.setRetentionPeriod(retentionPeriod);
                logger.info("Updated value of property DataContentDefinition::retentionPeriod from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldRetentionPeriod, retentionPeriod, id);
            }
        }
        if (null != sourceSystem && !sourceSystem.isEmpty()) {
            String oldSourceSystem = dcd.getSourceSystem();
            if(!Objects.equals(oldSourceSystem, sourceSystem)) {
                dcd.setSourceSystem(sourceSystem);
                logger.info("Updated value of property DataContentDefinition::sourceSystem from {} to {}, " +
                        "for DataContentDefinition with id: {}", oldSourceSystem, sourceSystem, id);
            }
        }
        if (null != perspectives && !perspectives.isEmpty()) {
            List<DataContentPerspective> oldPerspectives = dcd.getPerspectives();
            // Order doesn't matter
            if(!new HashSet<>(perspectives).equals(new HashSet<>(oldPerspectives))) {
                dcd.setSourceSystem(sourceSystem);
                logger.info("Updated value of property DataContentDefinition::perspectives " +
                        "for DataContentDefinition with id: {}", id);
            }
        }
    }


    //DELETE
    public void deleteDataContentDefinition(Long id){
        logger.debug("Entering method DataContentDefinition::deleteDataContentDefinition");

        DataContentDefinition dcd = findDataContentDefinition(id);

        if(dcd.isReferenced()){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_CONTENT_DEFINITION);
        }

        Long provId = dcd.getProvider().getId();
        DataSharingParty prov = this.dspRepo.findById(provId)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, provId));

        DataContentDefinition dcdToRemove = prov.getProviderDcds().stream()
                .filter(dcd2 -> Objects.equals(dcd2.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, id));

        prov.deleteDataContentDefinition(dcdToRemove);

        try{
            this.dspRepo.save(prov);
            logger.info("Deleted Data Content Definition with id: {}", id);
        }catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_PARTY, e);
        }

    }

    private DataContentDefinition findDataContentDefinition(Long id){

        DataContentDefinition dcd = this.dcdRepo.findById(id).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, id));

        logger.info("Found DataContentDefinition with id: {}", id);
        return dcd;
    }
}
