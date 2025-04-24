package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.repository.DataContentDefinitionRepository;
import com.sharedsystemshome.dsa.repository.DataFlowRepository;
import com.sharedsystemshome.dsa.repository.DataSharingAgreementRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.util.*;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sharedsystemshome.dsa.util.BusinessValidationException.*;

@Service
@RequiredArgsConstructor
@Validated
public class DataFlowService {

    private static final Logger logger = LoggerFactory.getLogger(DataFlowService.class);

    private final DataFlowRepository dataFlowRepo;
    private final DataSharingPartyRepository dspRepo;
    private final DataSharingAgreementRepository dsaRepo;
    private final DataContentDefinitionRepository dcdRepo;

    private final CustomValidator<DataFlow> validator;

    //CREATE
    public Long createDataFlow (DataFlow dataFlow) {
        logger.debug("Entering method DataFlow::createDataFlow with dcd: {}", dataFlow);

        if(null == dataFlow){
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_FLOW);
        }

        this.validator.validate(dataFlow);

        // Get id of parent DSA
        Long dsaId = dataFlow.getDataSharingAgreement().getId();
        if(null == dsaId){
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_SHARING_AGREEMENT + " id");
        } else if(!this.dsaRepo.existsById(dsaId)) {
            throw new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, dsaId);
        }

        //Verify provider id exists
        Long provId = dataFlow.getProvider().getId();
        if(null == provId){
            throw new NullOrEmptyValueException("Provider " + BusinessValidationException.DATA_SHARING_PARTY + " id");
        }

        //Test provider is valid
        DataSharingParty prov = this.dspRepo.findById(provId)
                .orElseThrow(() -> new EntityNotFoundException("Provider " + BusinessValidationException.DATA_SHARING_PARTY, provId));

        //Verify consumer id exists
        Long consId = dataFlow.getConsumer().getId();
        if(null == consId){
            throw new NullOrEmptyValueException("Consumer " +  BusinessValidationException.DATA_SHARING_PARTY + " id");
        }

        //Test consumer is valid
        if(!this.dspRepo.existsById(consId)){
            throw new EntityNotFoundException("Consumer " + BusinessValidationException.DATA_SHARING_PARTY, consId);
        }

        //Test Data Flow has one or more DCDs
        List<DataContentDefinition> dfDcds = dataFlow.getProvidedDcds();
        if(null == dfDcds || 0 == dfDcds.size()){
            throw new NullOrEmptyCollectionException(BusinessValidationException.DATA_CONTENT_DEFINITION);
        }

        //Create array of dfDcdIds as subset <= max set
        final int maxD = dfDcds.size();
        List<Long> dfDcdIds = new ArrayList<>(maxD);

        for(int i = 0; i < maxD; i++){
            dfDcdIds.add(dfDcds.get(i).getId());
        }

        //Create array of provDcdIds as max set
        List<DataContentDefinition> provDcds = prov.getProviderDcds();
        final int maxP = provDcds.size();
        List<Long> provDcdIds = new ArrayList<>(maxP);

        for(int i = 0; i < maxP; i++){
            provDcdIds.add(provDcds.get(i).getId());
        }

        //Test each DCD is owned by provider DataSharingParty
        for (Long dfDcdId : dfDcdIds) {
            if (provDcdIds.contains(dfDcdId)) {
                //continue
            } else {
                throw new BusinessValidationException(
                        BusinessValidationException.DATA_CONTENT_DEFINITION
                                + " with id = "
                                + dfDcdId
                                + " does not exist for provider "
                                + BusinessValidationException.DATA_SHARING_PARTY
                                + " with id = "
                                + provId);
            }
        }
        logger.info("Property DataFlow::providedDcds is valid");

        //Validate Personal and Special Category data
        if(!dataFlow.getIsPersonalData() && dataFlow.getIsSpecialCategoryData()){
            throw new BusinessValidationException("Confirmation of Personal Data required for " + BusinessValidationException.DATA_FLOW);
        }
        logger.info("Property DataFlow::isSpecialCategoryData is valid with respect to "
                + "property DataFlow::isPersonalData");

        try{
            Long dfId = this.dataFlowRepo.save(dataFlow).getId();
            logger.info("New DataFlow created with id: {}", dfId);
            return dfId;
        } catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_FLOW, e);
        }
    }

    //READ
    @GetMapping
    public DataFlow getDataFlowById(Long id){
        logger.debug("Entering method DataFlow::getDataFlowById with id: {}", id);

        DataFlow dataFlow = this.dataFlowRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(BusinessValidationException.DATA_FLOW, id)
        );

        logger.info("Found DataFlow with id: {}", id);
        return dataFlow;

    }

    //READ - FILTER BY PROVIDER
    @GetMapping
    public List<DataFlow> getDataFlowsByProviderId(Long id){
        logger.debug("Entering method DataFlow::getDataFlowsByProviderId with id: {}", id);

        if(null == id){
            throw new NullOrEmptyValueException("Provider " + BusinessValidationException.DATA_SHARING_PARTY + " id");
        }

        List<DataFlow> dfs = this.dataFlowRepo.findDataFlowByProviderId(id).orElseThrow(
                () -> new EntityNotFoundException("Provider " + BusinessValidationException.DATA_SHARING_PARTY, id)
        );

        logger.info("Found {} DataFlows for provider DataSharingParty with id: {}",
                dfs.size(), id);
        return  dfs;

    }


    //READ - FILTER BY CONSUMER
    @GetMapping
    public List<DataFlow> getDataFlowsByConsumerId(Long id){
        logger.debug("Entering method DataFlow::getDataFlowsByConsumerId with id: {}", id);

        if(null == id){
            throw new NullOrEmptyValueException("Consumer " + BusinessValidationException.DATA_SHARING_PARTY + " id");
        }

        List<DataFlow> dfs = this.dataFlowRepo.findDataFlowByConsumerId(id).orElseThrow(
                () -> new EntityNotFoundException("Consumer " + BusinessValidationException.DATA_SHARING_PARTY, id)
        );

        logger.info("Found {} DataFlows for consumer DataSharingParty with id: {}",
                dfs.size(), id);
        return  dfs;

    }

    //READ ALL
    // Remove this as only getting DataFlows for given DSA
    @GetMapping
    public List<DataFlow> getDataFlows(){
        logger.debug("Entering method DataFlow::getDataFlows");

        List<DataFlow> dfs = this.dataFlowRepo.findAll();

        logger.info("Found {} DataFlows", dfs.size());
        return dfs;
    }


    //UPDATE
    @Transactional
    /**
     * ISS-000-002: method does not support all entity attributes.
     */
    public void updateDataFlow(Long dataFlowId,
                               LocalDate endDate,
                               LawfulBasis lawfulBasis,
                               SpecialCategoryData specialCategory,
                               String purposeOfSharing){
        logger.debug("Entering method DataFlow::updateDataFlow");

        DataFlow dataFlow = this.dataFlowRepo.findById(dataFlowId).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_FLOW, dataFlowId));

        if (null != endDate && endDate.isAfter(dataFlow.getStartDate())) {
            LocalDate oldEndDate = dataFlow.getEndDate();
            if(!Objects.equals(oldEndDate, endDate)) {
                dataFlow.setEndDate(endDate);
                logger.info("Updated value of property DataFlow::endDate from {} to {}, " +
                        "for DataFlow with id: {}", oldEndDate, endDate, dataFlowId);
            }
        }
        if (null != lawfulBasis) {
            //Personal data must have lawful basis
            if(dataFlow.getIsPersonalData() && LawfulBasis.NOT_PERSONAL_DATA != lawfulBasis) {
                LawfulBasis oldLawfulBasis = dataFlow.getLawfulBasis();
                if(!Objects.equals(oldLawfulBasis, lawfulBasis)) {
                    dataFlow.setLawfulBasis(lawfulBasis);
                    logger.info("Updated value of property DataFlow::lawfulBasis from {} to {}, " +
                            "for DataFlow with id: {}", oldLawfulBasis, lawfulBasis, dataFlowId);
                }
            }else{
                throw new BusinessValidationException(BusinessValidationException.DATA_FLOW + " Lawful Basis only applicable to Personal Data.");
            }
        }
        //Personal data may or may not have a special category
        if (null != specialCategory) {
            if(dataFlow.getIsPersonalData()){
                SpecialCategoryData oldSpecialCategory = dataFlow.getSpecialCategory();
                if (!Objects.equals(oldSpecialCategory, specialCategory)) {
                    dataFlow.setSpecialCategory(specialCategory);
                    logger.info("Updated value of property DataFlow::specialCategory from {} to {}, " +
                            "for DataFlow with id: {}", oldSpecialCategory, specialCategory, dataFlowId);
                }
            } else {
                throw new BusinessValidationException(BusinessValidationException.DATA_FLOW + " Special Category only applicable to Personal Data.");
            }

        }
        if (null != purposeOfSharing && !purposeOfSharing.isEmpty()) {
            String oldPurposeOfSharing = dataFlow.getPurposeOfSharing();
            if(!Objects.equals(oldPurposeOfSharing, purposeOfSharing)) {
                dataFlow.setPurposeOfSharing(purposeOfSharing);
                logger.info("Updated value of property DataFlow::purposeOfSharing from {} to {}, " +
                        "for DataFlow with id: {}", oldPurposeOfSharing, purposeOfSharing, dataFlowId);
            }
        }
    }

    public void removeDataContentDefinition(Long dfId, Long dcdId) {
        logger.debug("Entering method DataFlow::removeDataContentDefinition for DataFlow with id: {} "
                + "and DataSharingAgreement with id: {}", dfId, dcdId);

        DataFlow df = findDataFlow(dfId);

        DataContentDefinition dcdToRemove = df.getProvidedDcds().stream()
                .filter(dcd -> Objects.equals(dcd.getId(), dcdId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_CONTENT_DEFINITION, dcdId));

        df.removeDataContentDefinition(dcdToRemove);
        this.dataFlowRepo.save(df);

        logger.info("Removed DataContentDefinition with id {} " +
                "from property DataFlow::providedDcds of DataFlow with id: {}", dfId, dcdId);
    }

    @Transactional
    public void addDataContentDefinition(Long dfId, Long dcdId){
        logger.debug("Entering method DataFlow::addDataContentDefinition for DataFlow with id: {} "
                + "and DataSharingAgreement with id: {}", dfId, dcdId);

        DataFlow df = findDataFlow(dfId);

        DataContentDefinition dcd = this.dcdRepo.findById(dcdId).
                orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_CONTENT_DEFINITION, dcdId));

        df.addDataContentDefinition(dcd);
        logger.info("Added DataContentDefinition with id {} " +
                "to property DataFlow::providedDcds of DataFlow with id: {}", dfId, dcdId);
    }


    //DELETE
    public void deleteDataFlow(Long id){
        logger.debug("Entering method DataFlow::deleteDataFlow with id: {}", id);

        DataFlow dataFlow = findDataFlow(id);

        Long dsaId = dataFlow.getDataSharingAgreement().getId();
        DataSharingAgreement dsa = this.dsaRepo.findById(dsaId)
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, dsaId));

        DataFlow dfToRemove = dsa.getDataFlows().stream()
                .filter(df -> Objects.equals(df.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_FLOW, id));

        dsa.deleteDataFlow(dfToRemove);

        try{
            this.dsaRepo.save(dsa);
            logger.info("Deleted DataFlow with id: {}", id);
        }catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_AGREEMENT, e);
        }

    }

    private DataFlow findDataFlow(Long dfId){
        logger.debug("Entering method DataFlow::findDataFlow with id: {}", dfId);

        DataFlow df = this.dataFlowRepo.findById(dfId).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_FLOW, dfId));

        logger.info("Found DataFlow with id: {}", dfId);
        return df;
    }
}

