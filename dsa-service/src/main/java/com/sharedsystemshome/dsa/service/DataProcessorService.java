package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.*;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class DataProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessorService.class);

    private final DataProcessorRepository dpRepo;

    private final DataSharingPartyRepository dspRepo;

    // CREATE
    public Long createDataProcessor(DataProcessor processor) {
        logger.debug("Entering createDataProcessor with processor: {}", processor);

        if (processor == null) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_PROCESSOR);
        }

        // Null safety for controller
        if (processor.getController() == null || processor.getController().getId() == null) {
            throw new NullOrEmptyValueException("Controller " + BusinessValidationException.DATA_SHARING_PARTY + " id");
        }

        Long controllerId = processor.getController().getId();

        // Correct repository for controller existence check
        if (!dspRepo.existsById(controllerId)) {
            throw new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, controllerId);
        }

        try {
            Long dpId = this.dpRepo.save(processor).getId();
            logger.info("New DataProcessor created with id: {}", dpId);
            return dpId;
        } catch (Exception e) {
            logger.error("Failed to create DataProcessor", e);
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_PROCESSOR, e);
        }
    }

    //READ
    @GetMapping("/{id}")
    public DataProcessor getDataProcessorById(@PathVariable Long id) {
        logger.debug("Entering DataProcessorController::getDataProcessorById with id: {}", id);

        DataProcessor dp = this.dpRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_PROCESSOR, id));

        logger.info("Successfully found DataProcessor with id: {}", id);
        return dp;
    }

    @GetMapping
    public List<DataProcessor> getDataProcessors(@PathVariable Long custId) {
        logger.debug("Entering DataProcessorController::getDataProcessors for custId: {}", custId);

        List<DataProcessor> dps = Optional.ofNullable(this.dpRepo.findByControllerId(custId))
                .orElse(Collections.emptyList());

        logger.info("Found {} DataProcessors for custId: {}", dps.size(), custId);

        return dps;
    }

    @Transactional
    public void updateDataProcessor(DataProcessor dp) {
        logger.debug("Entering method DataProcessor::updateDataProcessor");

        if (null == dp) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_PROCESSOR);
        }

        this.updateDataProcessor(
                dp.getId(),
                dp.getName(),
                dp.getDescription(),
                dp.getEmail(),
                dp.getWebsite(),
                dp.getCertifications()
        );

    }

    @Transactional
    public void updateDataProcessor(
            Long id,
            String name,
            String description,
            String email,
            String website,
            List<DataProcessorCertification> accreditations) {

        logger.debug("Entering method DataProcessor::updateDataProcessor");

        DataProcessor dp = this.dpRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_PROCESSOR, id));

        if (null != name && !name.isEmpty()) {
            String oldName = dp.getName();
            if (!Objects.equals(oldName, name)) {
                dp.setName(name);
                logger.info("Updated value of property DataProcessor::name from {} to {}, " +
                        "for DataProcessor with id: {}", oldName, name, id);
            }
        }
        if (null != description) {
            String oldDescription = dp.getDescription();
            if (!Objects.equals(oldDescription, description)) {
                dp.setDescription(description);
                logger.info("Updated value of property DataProcessor::description from {} to {}, " +
                        "for DataProcessor with id: {}", oldDescription, description, id);
            }
        }
        if (null != website) {
            String oldWebsite = dp.getWebsite();
            if (!Objects.equals(oldWebsite, website)) {
                dp.setWebsite(website);
                logger.info("Updated value of property DataProcessor::website from {} to {}, " +
                        "for DataProcessor with id: {}", oldWebsite, website, id);
            }
        }
        if (null != email && !email.isEmpty()) {
            String oldEmail = dp.getEmail();
            if (!Objects.equals(oldEmail, email)) {
                dp.setEmail(email);
                logger.info("Updated value of property DataProcessor::email from {} to {}, " +
                        "for DataProcessor with id: {}", oldEmail, email, id);
            }
        }
        if (accreditations != null) {
            List<DataProcessorCertification> existing = new ArrayList<>(dp.getCertifications());

            // Remove ones that no longer exist
            existing.stream()
                    .filter(e -> accreditations.stream().noneMatch(i -> i.getName() == e.getName()))
                    .forEach(dp::removeCertification);

            // Add new ones
            accreditations.stream()
                    .filter(i -> existing.stream().noneMatch(e -> e.getName() == i.getName()))
                    .forEach(i -> dp.addCertification(com.sharedsystemshome.dsa.model.DataProcessorCertification.builder()
                            .name(i.getName())
                            .build()));

            logger.info("Updated value of property DataProcessor::accreditations for DataProcessor with id: {}", id);
        }

    }

    //DELETE
    @Transactional
    public void deleteDataProcessor(Long id) {
        logger.debug("Entering deleteDataProcessor with id: {}", id);

        DataProcessor dp = this.dpRepo.findById(id).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_PROCESSOR, id));

        if(dp.isReferenced()){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_PROCESSOR);
        }

        Long conId = dp.getController().getId();
        DataSharingParty con = this.dspRepo.findById(conId)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, conId));

        DataProcessor dpToRemove = con.getProcessors().stream()
                .filter(dcd2 -> Objects.equals(dcd2.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_PROCESSOR, id));

        dp.getCertifications().clear();

        con.deleteDataProcessor(dpToRemove);

        // Remove from controller's list and break link
        con.deleteDataProcessor(dp);

        try {
            this.dspRepo.save(con); // cascade + orphanRemoval handles actual delete
            logger.info("Deleted DataProcessor with id: {}", id);
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_PARTY, e);
        }
    }
}


