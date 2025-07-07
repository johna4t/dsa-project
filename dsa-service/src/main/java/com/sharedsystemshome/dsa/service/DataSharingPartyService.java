package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class DataSharingPartyService {

    private static final Logger logger = LoggerFactory.getLogger(DataSharingPartyService.class);

    private final DataSharingPartyRepository dspRepo;

    private final CustomValidator<DataSharingParty> validator;

    //CREATE
    public Long createDataSharingParty(DataSharingParty dsp){
        logger.debug("Entering method DataSharingParty::createDataSharingParty with dcd: {}", dsp);

        if(null == dsp){
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_SHARING_PARTY);
        }

        this.validator.validate(dsp);

        try{
            Long dspId = this.dspRepo.save(dsp).getId();
            logger.info("New DataSharingParty created with id: {}", dspId);
            return dspId;
        } catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_PARTY, e);
        }
    }

    //READ
    @GetMapping
    public DataSharingParty getDataSharingPartyById(Long id){

        boolean exists = this.dspRepo.existsById(id);
        logger.debug("Entering method DataSharingParty::getDataSharingPartyById with id: {}", id);

        DataSharingParty dsp = this.dspRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, id)
        );

        logger.info("Found DataSharingParty with id: {}", id);
        return dsp;

    }

    //READ ALL
    @GetMapping
    public List<DataSharingParty> getDataSharingParties(){
        logger.debug("Entering method DataSharingParty::getDataSharingParties");

        List<DataSharingParty> dsps = this.dspRepo.findAll();

        logger.info("Found {} DataSharingParty", dsps.size());
        return dsps;
    }

    //UPDATE
    @Transactional
    public void updateDataSharingParty(DataSharingParty dsp){
        logger.debug("Entering method DataSharingParty::updateDataSharingParty");

        if (null == dsp) {
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_SHARING_PARTY);
        }

        this.updateDataSharingParty(
                dsp.getId(),
                dsp.getDescription());
    }


    @Transactional
    public void updateDataSharingParty(Long dspId,
                                       String desc){
        logger.debug("Entering method DataSharingParty::updateDataSharingParty");

        DataSharingParty dsp = dspRepo.findById(dspId).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, dspId));

        if (null != desc && !desc.isEmpty()) {
            String oldDesc = dsp.getDescription();
            if(!Objects.equals(oldDesc, desc)) {
               dsp.setDescription(desc);
                logger.info("Updated value of property DataSharingParty::description from {} to {}, " +
                        "for DataFlow with id: {}", oldDesc, desc, dspId);
            }
        }
    }


    public void deleteDataContentDefinition(Long provId, Long dcdId) {
        logger.debug("Entering method DataSharingParty::deleteDataContentDefinition for DataSharingParty with id: {} "
                + "and DataContentDefinition with id: {}", provId, dcdId);

        DataSharingParty prov = findDataSharingParty(provId);

        DataContentDefinition dcdToRemove = prov.getProviderDcds().stream()
                .filter(dcd -> Objects.equals(dcd.getId(), dcdId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, dcdId));

        prov.deleteDataContentDefinition(dcdToRemove);
        this.dspRepo.save(prov);

        logger.info("Deleted DataContentDefinition with id {} " +
                "from property DataSharingParty::providerDcds of DataSharingParty with id: {}", dcdId, provId);
    }


    //DELETE
/*    public void deleteDataSharingParty(Long id){
        boolean exists = this.dspRepo.existsById(id);

        if(!exists){
            throw new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, id);
        }

        this.dspRepo.deleteById(id);
        logger.info("Deleted DataSharingParty with id: {}", id);

    }*/

    @Transactional
    public void deleteDataSharingParty(Long id){
        logger.debug("Entering DataSharingParty::deleteDataSharingParty with id: {}", id);

        DataSharingParty dsp = this.dspRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, id));

        // Break DataFlow links (non-cascaded)
        for (DataFlow flow : dsp.getProvidedDataFlows()) {
            flow.setProvider(null);
        }
        for (DataFlow flow : dsp.getConsumedDataFlows()) {
            flow.setConsumer(null);
        }
        dsp.getProvidedDataFlows().clear();
        dsp.getConsumedDataFlows().clear();

        // Orphan selfAsProcessor explicitly
        dsp.setSelfAsProcessor(null);

        // Optional: also remove associated CustomerAccount if needed
        // customerAccountRepo.delete(dsp.getAccount());

        this.dspRepo.delete(dsp);
        logger.info("Deleted DataSharingParty with id: {}", id);
    }

    // Private methods

    private DataSharingParty findDataSharingParty(Long dspId){

        DataSharingParty dsp = this.dspRepo.findById(dspId).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_PARTY, dspId));

        logger.info("Found DataSharingParty with id: {}", dspId);
        return dsp;
    }
}
