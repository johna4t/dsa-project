package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.datatype.Address;
import com.sharedsystemshome.dsa.model.CustomerAccount;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.service.CustomerAccountService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/customer-accounts")
public class CustomerAccountController {

    private final CustomerAccountService customerService;

    @Autowired
    public CustomerAccountController (
            CustomerAccountService customerService){

        this.customerService = customerService;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<Long> postCustomerAccount(RequestEntity<CustomerAccount> request){

        try{
            return ResponseEntity.status(201).body(this.customerService.createCustomerAccount(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    //public ResponseEntity<List<CustomerAccount>> getDataSharingAgreements(RequestEntity<Map<String, String>> requestEntity)
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<CustomerAccount>> getCustomerAccounts(@RequestParam Map<String,String> params) {

        // Supports /api/v1/customer-accounts?{param 1}={value 1}
        if(null == params || 0 == params.size()) {

            List<CustomerAccount> customers = this.customerService.getCustomerAccounts();

            if (null != customers && !customers.isEmpty()) {

                return ResponseEntity.status(200).body(customers);

            } else {

                return ResponseEntity.status(204).build();

            }
        } else {
                throw new BadRequestException("Unrecognised query parameter.");
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<CustomerAccount> getCustomerAccountById(@PathVariable("id") Long id) {

        try{

            return ResponseEntity.status(200).body(this.customerService.getCustomerAccountById(id));

        } catch(EntityNotFoundException e){

            return ResponseEntity.status(204).build();

        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping
    public ResponseEntity<CustomerAccount> putCustomerAccount(RequestEntity<CustomerAccount> request) {

        this.customerService.updateCustomerAccount(request.getBody());

        return ResponseEntity.status(204).build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping(path = "{id}")
    public ResponseEntity<CustomerAccount> patchCustomerAccount(
            @PathVariable("id") Long id,
            @RequestParam String name,
            @RequestParam String deptName,
            @RequestParam String url,
            @RequestParam String buName,
            @RequestParam Address address,
            @RequestParam DataSharingParty dataSharingParty
    )
    {
        this.customerService.updateCustomerAccount(id, name, deptName, url, buName, address, dataSharingParty);

        return ResponseEntity.status(204).build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<CustomerAccount> deleteCustomerAccount(@PathVariable("id") Long id){
        this.customerService.deleteCustomerAccount(id);

        return ResponseEntity.status(204).build();

    }

}
