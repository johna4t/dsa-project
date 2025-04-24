package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.repository.CustomerAccountRepository;
import com.sharedsystemshome.dsa.util.AddOrUpdateTransactionException;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import com.sharedsystemshome.dsa.repository.DataSharingAgreementRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.CustomValidator;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.sharedsystemshome.dsa.util.BusinessValidationException;

@Service
@RequiredArgsConstructor
@Validated
public class DataSharingAgreementService {

    private static final Logger logger = LoggerFactory.getLogger(DataSharingAgreement.class);
    private final DataSharingAgreementRepository dsaRepo;
    private final DataSharingPartyRepository dspRepo;
    private final CustomerAccountRepository customerRepo;

    private final CustomValidator<DataSharingAgreement> validator;

    //CREATE
    public Long createDataSharingAgreement(DataSharingAgreement dsa){
        logger.debug("Entering method DataSharingAgreement::createDataSharingAgreement");

        if(null == dsa){
            throw new NullOrEmptyValueException(BusinessValidationException.DATA_SHARING_AGREEMENT);
        }

        this.validator.validate(dsa);

        CustomerAccount cust = dsa.getAccountHolder();

        if(null == cust){
            throw new NullOrEmptyValueException(BusinessValidationException.CUSTOMER_ACCOUNT);
        }

        if(!this.customerRepo.existsById(cust.getId())){
            throw new EntityNotFoundException(BusinessValidationException.CUSTOMER_ACCOUNT, cust.getId());
        }

        try{
            Long dsaId = this.dsaRepo.save(dsa).getId();

            logger.info("New DataSharingAgreement created with id: {}", dsaId);
            return dsaId;
        } catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_AGREEMENT, e);
        }

    }

    public List<DataSharingAgreement> getDataSharingAgreements(){
        logger.debug("Entering method DataSharingAgreement::getDataSharingAgreements");

        List<DataSharingAgreement> dsas = this.dsaRepo.findAll();

        logger.info("Found {} DataSharingAgreements", dsas.size());

        return dsas;
    }

    public List<DataSharingAgreement> getDataSharingAgreementsByCustomerId(Long custId){
        logger.debug("Entering method DataSharingAgreement::getDataSharingAgreementsByCustomerId");

        if(null == custId){
            throw new NullOrEmptyValueException(BusinessValidationException.CUSTOMER_ACCOUNT + " id");
        }

        List<DataSharingAgreement> dsas = this.dsaRepo.findDataSharingAgreementByAccountHolderId(custId).orElseThrow(
                () -> new EntityNotFoundException(BusinessValidationException.CUSTOMER_ACCOUNT, custId)
        );

        logger.info("Found {} DataSharingAgreements for provider DataSharingParty with id: {}",
                dsas.size(), custId);
        return dsas;
    }

    public DataSharingAgreement getDataSharingAgreementById(Long id){
        logger.debug("Entering method DataSharingAgreement::getDataSharingAgreementById");

        DataSharingAgreement dsa = this.dsaRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, id)
        );

        logger.info("Found DataSharingAgreement with id: {}", id);
        return dsa;
    }

    public void deleteDataFlow(Long dsaId, Long dfId) {
        logger.debug("Entering method DataSharingAgreement::deleteDataFlow for DataSharingAgreement with id: {} "
                + "and DataFlow with id: {}", dsaId, dfId);

        DataSharingAgreement dsa = findDataSharingAgreement(dsaId);

        DataFlow dfToRemove = dsa.getDataFlows().stream()
                .filter(dataFlow -> Objects.equals(dataFlow.getId(), dfId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_FLOW, dfId));

        dsa.deleteDataFlow(dfToRemove);

        try{
            this.dsaRepo.save(dsa);
            logger.info("Deleted DataFlow with id {} " +
                    "from property DataSharingAgreement::dataFlows of DataSharingAgreement with id: {}", dsaId, dfId);
        } catch(Exception e){
            throw new AddOrUpdateTransactionException(BusinessValidationException.DATA_SHARING_AGREEMENT, e);
        }
    }

    //UPDATE
    @Transactional
    public void updateDataSharingAgreement(Long id,
                                           String name,
                                           LocalDate startDate,
                                           LocalDate endDate,
                                           ControllerRelationship controllerRelationship){
        logger.debug("Entering method DataSharingAgreement::updateDataSharingAgreement");

        DataSharingAgreement dsa = findDataSharingAgreement(id);

        if (null != name && !name.isEmpty()) {
            String oldName = dsa.getName();
            if(!Objects.equals(oldName, name)) {
                dsa.setName(name);
                logger.info("Updated value of property DataSharingAgreement::updateDataSharingAgreement from {} to {}, " +
                        "for DataSharingAgreement with id: {}", oldName, name, id);
            }
        }
        if (null != startDate) {
            LocalDate oldStartDate = dsa.getStartDate();
            if(!Objects.equals(oldStartDate, startDate)) {
                dsa.setStartDate(startDate);
                logger.info("Updated value of property DataSharingAgreement::startDate from {} to {}, " +
                        "for DataSharingAgreement with id: {}", oldStartDate, startDate, id);
            }
        }
        if (null != endDate && endDate.isAfter(dsa.getStartDate())) {
            LocalDate oldEndDate = dsa.getEndDate();
            if(!Objects.equals(oldEndDate, endDate)) {
                dsa.setEndDate(endDate);
                logger.info("Updated value of property DataSharingAgreement::endDate from {} to {}, " +
                        "for DataSharingAgreement with id: {}", oldEndDate, endDate, id);
            }
        }
        if (null != controllerRelationship) {
            ControllerRelationship oldControllerRelationship = dsa.getControllerRelationship();
            if(!Objects.equals(oldControllerRelationship, controllerRelationship)) {
                dsa.setControllerRelationship(controllerRelationship);
                logger.info("Updated value of property DataSharingAgreement::controllerRelationship from {} to {}, " +
                        "for DataSharingAgreement with id: {}", oldControllerRelationship, controllerRelationship, id);
            }
        }
    }

    //DELETE
    public void deleteDataSharingAgreement(Long id){
        logger.debug("Entering method DataSharingAgreement::deleteDataSharingAgreement with id: {}", id);

        if(!this.dsaRepo.existsById(id)){
            throw new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, id);
        }
        logger.info("Found DataSharingAgreement with id: {}", id);

        this.dsaRepo.deleteById(id);

        logger.info("Deleted DataSharingAgreement with id: {}", id);
    }

    // Private methods

    private DataSharingAgreement findDataSharingAgreement(Long dsaId){
        logger.debug("Entering method DataSharingAgreement::findDataSharingAgreement with id: {}", dsaId);

        DataSharingAgreement dsa = this.dsaRepo.findById(dsaId).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, dsaId));

        logger.info("Found DataSharingAgreement with id: {}", dsaId);
        return dsa;
    }


}
