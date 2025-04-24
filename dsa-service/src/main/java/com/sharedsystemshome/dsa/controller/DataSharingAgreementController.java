package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.DataSharingAgreementService;
import com.sharedsystemshome.dsa.enums.ControllerRelationship;
import com.sharedsystemshome.dsa.service.UserAccountService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping(path = "api/v1/data-sharing-agreements")
public class DataSharingAgreementController {

    private final DataSharingAgreementService dsaService;

    private final UserContextService userContext;

    @Autowired
    public DataSharingAgreementController(
            DataSharingAgreementService dsaService,
            UserContextService userContext,
           UserAccountService userService
    ){

        this.dsaService = dsaService;
        this.userContext = userContext;

    }

    @PostMapping
    public ResponseEntity<Long> postDataSharingAgreement(RequestEntity<DataSharingAgreement> request){

        try{
            return ResponseEntity.status(201).body(this.dsaService.createDataSharingAgreement(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }

    }

    @GetMapping
    public ResponseEntity<List<DataSharingAgreement>> getDataSharingAgreements(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();

        if (null == queryParams || 0 == queryParams.size()) {
            // No query params passed, so run default query

            if(this.userContext.isSuperAdmin()){

                return getDataSharingAgreementsResponse(this.dsaService.getDataSharingAgreements());

            } else {

                return getDataSharingAgreementsResponse(
                        this.dsaService.getDataSharingAgreementsByCustomerId(
                                this.userContext.getCurrentUser().getParentAccount().getId()));
            }
        } else {
            throw new BadRequestException("Unrecognized query parameter.");
        }
    }

    private ResponseEntity<List<DataSharingAgreement>> getDataSharingAgreementsResponse(List<DataSharingAgreement> dsas){

        if(null != dsas && !dsas.isEmpty()){

            return ResponseEntity.status(200).body(dsas);

        } else {

            return ResponseEntity.status(204).build();

        }

    }

    @GetMapping(path = "{id}")
    public ResponseEntity<DataSharingAgreement> getDataSharingAgreementById(@PathVariable("id") Long id) {

        try{

            return ResponseEntity.status(200).body(this.dsaService.getDataSharingAgreementById(id));

        } catch(EntityNotFoundException e){

            return ResponseEntity.status(204).build();

        }

    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<DataSharingAgreement> patchDataSharingAgreement(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) ControllerRelationship controllerRelationship){

        this.dsaService.updateDataSharingAgreement(id, name, startDate, endDate, controllerRelationship);

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<DataSharingAgreement> deleteDataSharingAgreement(@PathVariable("id") Long id){
        this.dsaService.deleteDataSharingAgreement(id);

        return ResponseEntity.status(204).build();
   }


}
