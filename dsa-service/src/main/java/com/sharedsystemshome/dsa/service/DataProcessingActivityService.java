package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.enums.DataContentType;
import com.sharedsystemshome.dsa.model.*;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataProcessingActivityRepository;
import com.sharedsystemshome.dsa.repository.DataProcessorRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.AddOrUpdateTransactionException;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class DataProcessingActivityService {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessingActivityService.class);

    private final DataProcessingActivityRepository dpvRepo;

    private final DataContentDefinitionRepository dcdRepo;

    private final DataProcessorRepository dpRepo;

    //CREATE
    public Long createDataProcessingActivity(DataProcessingActivity dpv){
        logger.debug("Entering DataProcessingActivity::createDataProcessingActivity with activity: {}", dpv);

        if (dpv == null) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_PROCESSING_ACTIVITY);
        }

        // Validate Data Processor
        DataProcessor dp = dpv.getDataProcessor();
        if (null != dp) {
            Long dpId = dp.getId();
            if (null != dpId && !this.dpRepo.existsById(dpId)) {
                throw new EntityNotFoundException(BusinessValidationException.DATA_PROCESSOR, dpId);
            }
        } else {
            throw new NullOrEmptyValueException(
                    BusinessValidationException.DATA_PROCESSOR);
        }

        // Validate DCD
        DataContentDefinition dcd = dpv.getDataContentDefinition();
        if (null != dcd) {
            Long dcdId = dcd.getId();
            if (null != dcdId && !this.dcdRepo.existsById(dcdId)) {
                throw new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, dcdId);
            }
        } else {
            throw new NullOrEmptyValueException(
                    BusinessValidationException.DATA_CONTENT_DEFINITION);
        }



        // Link actions to Data Processing Activity
        if (dpv.getActionsPerformed() != null) {
            for (DataProcessingAction action : dpv.getActionsPerformed()) {
                action.setProcessingActivity(dpv);
            }
        }

        try {
            Long dpvId = this.dpvRepo.save(dpv).getId();
            logger.info("New Data Processing Activity created with id: {}", dpvId);
            return dpvId;
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_CONTENT_DEFINITION, e);
        }

    }


    //READ
    public DataProcessingActivity getDataProcessingActivityById(@PathVariable Long id) {
        logger.debug("Entering method DataProcessingActivity::getDataProcessingActivityById with id: {}", id);

        DataProcessingActivity dpv = this.dpvRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        logger.info("Successfully found DataProcessingActivity with id: {}", id);

        return dpv;
    }

    //READ ALL CUSTOMER Data Processing Activities
    public List<DataProcessingActivity> getDataProcessingActivities(Long custId){
        logger.debug("Entering method DataProcessingActivity::getDataProcessingActivities");

        // Find all Data Processing Activities for the customer to which the parent data processor belongs
        List<DataProcessingActivity> dpvs = this.dpvRepo.findByDataProcessor_Controller_Id(custId);;

        if(null == dpvs){
            // Return empty list
            dpvs = new ArrayList<>();
        }

        logger.info("Found {} DataProcessorActivities for custId: {}", dpvs.size(), custId);

        return dpvs;
    }

    @Transactional
    public void updateDataProcessingActivity(DataProcessingActivity dpv){
        logger.debug("Entering method DataContentDefinition::updateDataContentDefinition");

        if (null == dpv) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_PROCESSING_ACTIVITY);
        }

        this.updateDataProcessingActivity(
                dpv.getId(),
                dpv.getName(),
                dpv.getDescription(),
                dpv.getActionsPerformed()
        );

    }

    @Transactional
    public void updateDataProcessingActivity(
            Long id,
            String name,
            String description,
            List<DataProcessingAction> actions
    ){
        logger.debug("Entering method DataProcessingActivity::updateDataProcessingActivity");

        DataProcessingActivity dpv = this.dpvRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        if (null != name && !name.isEmpty()) {
            String oldName = dpv.getName();
            if(!Objects.equals(oldName, name)) {
                dpv.setName(name);
                logger.info("Updated value of property DataProcessingActivity::name from {} to {}, " +
                        "for DataProcessingActivity with id: {}", oldName, name, id);
            }
        }
        if (null != description) {
            String oldDescription = dpv.getDescription();
            if(!Objects.equals(oldDescription, description)) {
                dpv.setDescription(description);
                logger.info("Updated value of property DataProcessingActivity::description from {} to {}, " +
                        "for DataProcessingActivity with id: {}", oldDescription, description, id);
            }
        }
        if (actions != null) {
            List<DataProcessingAction> existingActions = new ArrayList<>(dpv.getActionsPerformed());
            Set<DataProcessingAction> updatedSet = new HashSet<>(actions); // Uses equals/hashCode

            // Remove actions that are no longer present
            for (DataProcessingAction existing : existingActions) {
                if (!updatedSet.contains(existing)) {
                    dpv.removeActionPerformed(existing);
                    logger.info("Removed DataProcessingAction: type={}, desc={}", existing.getActionType(), existing.getDescription());
                }
            }

            // Add new or updated actions
            for (DataProcessingAction incoming : actions) {
                dpv.addActionPerformed(incoming); // Handles duplicates and sets back-reference
                logger.debug("Ensured presence of DataProcessingAction: type={}, desc={}", incoming.getActionType(), incoming.getDescription());
            }
        }
    }

    //DELETE
    public void deleteDataProcessingActivity(Long id) {
        logger.debug("Entering method DataProcessingActivityService::deleteDataProcessingActivity with id: {}", id);

        DataProcessingActivity dpv = this.dpvRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        // Remove from parent DataContentDefinition
        Long dcdId = dpv.getDataContentDefinition().getId();
        DataContentDefinition dcd = this.dcdRepo.findById(dcdId)
                .orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_CONTENT_DEFINITION, dcdId));

        DataProcessingActivity dpvToRemove1 = dcd.getAssociatedDataProcessing().stream()
                .filter(dpv2 -> Objects.equals(dpv2.getId(), id))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        dcd.deleteDataProcessingActivity(dpvToRemove1);

        // Remove from parent DataProcessor
        Long dpId = dpv.getDataProcessor().getId();
        DataProcessor dp = this.dpRepo.findById(dpId)
                .orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_PROCESSOR, dpId));

        DataProcessingActivity dpvToRemove2 = dp.getAssociatedDataProcessing().stream()
                .filter(dpv2 -> Objects.equals(dpv2.getId(), id))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        dp.deleteDataProcessingActivity(dpvToRemove2);

        try {
            this.dcdRepo.save(dcd); // Save parent (cascade + orphanRemoval will delete dpv)
            logger.info("Deleted DataProcessingActivity with id: {} via DataContentDefinition", id);
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_CONTENT_DEFINITION, e);
        }

        try {
            this.dpRepo.save(dp); // Optional: to persist any updated relationships on processor
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_PROCESSOR, e);
        }
    }


    private DataProcessingActivity findDataProcessingActivity(Long id){

        DataProcessingActivity dpv = this.dpvRepo.findById(id).
                orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        logger.info("Found DataProcessingActivity with id: {}", id);

        return dpv;
    }
}
