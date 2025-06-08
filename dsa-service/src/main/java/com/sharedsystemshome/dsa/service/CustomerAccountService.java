package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.model.UserAccount;
import com.sharedsystemshome.dsa.repository.*;
import com.sharedsystemshome.dsa.util.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class CustomerAccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    private final CustomerAccountRepository customerRepo;

    private final UserAccountService userService;

    private final CustomValidator<CustomerAccount> customerValidator;

    //CREATE
    @Transactional
    public Long createCustomerAccount(CustomerAccount customer) {
        logger.debug("Entering method CustomerAccount::createUserAccount with customer: {}", customer);

        if(null == customer){
            throw new NullOrEmptyValueException(BusinessValidationException.CUSTOMER_ACCOUNT);
        }

        this.customerValidator.validate(customer);

        List<UserAccount> users = customer.getUsers();

        if(null == users || 0 == users.size()){
            throw new NullOrEmptyCollectionException(BusinessValidationException.USER_ACCOUNT);
        } else if(1 < users.size()) {
            throw new BusinessValidationException(BusinessValidationException.USER_ACCOUNT + " member collection contains too many values.");
        }

        UserAccount user = users.get(0);

        // Re-assign customer details to prevent "attempted to assign id from null one-to-one property" exception
        CustomerAccount savedCust = CustomerAccount.builder()
                .name(customer.getName())
                .departmentName(customer.getDepartmentName())
                .url(customer.getUrl())
                .branchName(customer.getBranchName())
                .departmentName(customer.getDepartmentName())
                .address(customer.getAddress())
                .dataSharingParty(customer.getDataSharingParty())
                .build();
        logger.debug("CustomerAccount details re-assigned to new CustomerAccount object: {}", savedCust);

        try {
            this.customerRepo.save(savedCust);
            logger.info("New CustomerAccount created with id: {}", savedCust.getId());
        } catch (Exception e) {
            throw new AddOrUpdateTransactionException(BusinessValidationException.CUSTOMER_ACCOUNT, e);
        }

        user.setParentAccount(savedCust);
        logger.debug("UserAccount set with parent CustomerAccount: {}", savedCust);

        this.userService.createAdminUserAccount(user);
        logger.info("New UserAccount created with id: {}", user.getId());

        return savedCust.getId();

    }

    //READ
    public List<CustomerAccount> getCustomerAccounts() {
        logger.debug("Entering method CustomerAccount::getCustomerAccounts");

        List<CustomerAccount> customerAccounts = this.customerRepo.findAll();

        logger.info("Found {} CustomerAccounts", customerAccounts.size());

        return customerAccounts;

    }

    public CustomerAccount getCustomerAccountById(Long id) {
        logger.debug("Entering method CustomerAccount::getCustomerAccountById");

        return this.findCustomerAccount(id);

    }

    //UPDATE
    @Transactional
    public void updateCustomerAccount(CustomerAccount customer) {
        logger.debug("Entering method CustomerAccount::updateCustomerAccount");

        if (null == customer) {
            throw new NullOrEmptyValueException(BusinessValidationException.CUSTOMER_ACCOUNT);
        }

        this.updateCustomerAccount(
                customer.getId(),
                customer.getName(),
                customer.getDepartmentName(),
                customer.getUrl(),
                customer.getBranchName(),
                customer.getAddress(),
                customer.getDataSharingParty()
        );
    }

    @Transactional
    public void updateCustomerAccount(
            Long id,
            String name,
            String deptName,
            String url,
            String branchName,
            Address address,
            DataSharingParty dataSharingParty) {
        logger.debug("Entering method CustomerAccount::updateCustomerAccount");

        CustomerAccount customer = this.findCustomerAccount(id);

        if(null != name && 0 < name.length()){
            String oldName = customer.getName();
            if(!Objects.equals(oldName, name)){
                customer.setName(name);
                logger.info("Updated value of property CustomerAccount::name from {} to {}, " +
                        "for CustomerAccount with id: {}", oldName, name, id);
            }
        }

        if(null != deptName && 0 < deptName.length()){
            String oldDeptName = customer.getDepartmentName();
            if(!Objects.equals(oldDeptName, deptName)){
                customer.setDepartmentName(deptName);
                logger.info("Updated value of property CustomerAccount:departmentName from {} to {}, " +
                        "for CustomerAccount with id: {}", oldDeptName, deptName, id);
            }
        }

        if(null != url && 0 < url.length()){
            String oldUrl = customer.getUrl();
            if(!Objects.equals(oldUrl, url)){
                customer.setUrl(url);
                logger.info("Updated value of property CustomerAccount::url from {} to {}, " +
                        "for CustomerAccount with id: {}", oldUrl, url, id);
            }
        }

        if(null != branchName){ // branchName not mandatory
            String oldBranchName = customer.getBranchName();
            if(!Objects.equals(oldBranchName, branchName)){
                customer.setBranchName(branchName);
                logger.info("Updated value of property CustomerAccount::branchName from {} to {}, " +
                        "for CustomerAccount with id: {}", oldBranchName, branchName, id);
            }
        }

        if(null != address){
            Address oldAddress = customer.getAddress();
            if(!Objects.equals(oldAddress, address)){
                customer.setAddress(address);
                logger.info("Updated value of property CustomerAccount::address from {} to {}, " +
                        "for CustomerAccount with id: {}", oldAddress, address, id);
            }
        }

        if(null != dataSharingParty){
            DataSharingParty oldDsp = customer.getDataSharingParty();
            if(!Objects.equals(oldDsp, dataSharingParty)){
                customer.setDataSharingParty(dataSharingParty);
                logger.info("Updated value of property CustomerAccount::dataSharingParty from {} to {}, " +
                        "for CustomerAccount with id: {}", oldDsp, dataSharingParty, id);
            }
        }
    }

    //DELETE
    public void deleteCustomerAccount(Long id) {
        logger.debug("Entering method CustomerAccount::deleteCustomerAccount for CustomerAccount with id: {}", id);

        this.customerAccountExists(id);

        this.customerRepo.deleteById(id);

        logger.info("Deleted CustomerAccount with id: {}", id);
    }

    public void deleteDataSharingAgreement(Long custId, Long dsaId) {
        logger.debug("Entering method CustomerAccount::deleteDataSharingAgreement for CustomerAccount with id: {} "
                        + "and DataSharingAgreement with id: {}", custId, dsaId);

        CustomerAccount customer = findCustomerAccount(custId);

        DataSharingAgreement dsaToRemove = customer.getAgreements().stream()
                .filter(dsa -> Objects.equals(dsa.getId(), dsaId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.DATA_SHARING_AGREEMENT, dsaId));

        customer.deleteDataSharingAgreement(dsaToRemove);

        this.customerRepo.save(customer);

        logger.info("Deleted DataSharingAgreement with id {} " +
                "from CustomerAccount::agreements of CustomerAccount with id: {}", dsaId, custId);
    }


    private CustomerAccount findCustomerAccount(Long id){
        logger.debug("Entering method CustomerAccount::findCustomerAccount for CustomerAccount with id: {}", id);

        CustomerAccount customer = this.customerRepo.findById(id).
                orElseThrow(() -> new EntityNotFoundException(BusinessValidationException.CUSTOMER_ACCOUNT, id));

        logger.info("Found CustomerAccount with id: {}", id);
        return customer;
    }

    private void customerAccountExists(Long id){
        logger.debug("Entering method CustomerAccount::customerAccountExists for CustomerAccount with id: {}", id);

        if(!this.customerRepo.existsById(id)){
            throw new EntityNotFoundException(BusinessValidationException.CUSTOMER_ACCOUNT, id);
        }

        logger.info("Found CustomerAccount with id: {}", id);
    }

}