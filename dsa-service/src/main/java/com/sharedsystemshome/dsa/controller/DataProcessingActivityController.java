package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.service.DataProcessingActivityService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/data-processing-activities")
public class DataProcessingActivityController {

    private final DataProcessingActivityService dpvService;
    private final UserContextService userContext;

    @Autowired
    public DataProcessingActivityController(
            DataProcessingActivityService dpvService,
            UserContextService userContext){

        this.dpvService = dpvService;
        this.userContext = userContext;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<Long> postDataProcessingActivity(RequestEntity<DataProcessingActivity> request){

        try{
            return ResponseEntity.status(201).body(
                    this.dpvService.createDataProcessingActivity(
                            this.userContext.validateAccess(request.getBody())));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<DataProcessingActivity> getDataProcessingActivityById(@PathVariable("id") Long id) {

        try {

            return ResponseEntity.status(200).body(this.userContext.validateAccess(
                    this.dpvService.getDataProcessingActivityById(id)));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(204).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<DataProcessingActivity>> getDataProcessingActivities(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();
        List<DataProcessingActivity> dpvs = null;

        if(null == queryParams || queryParams.isEmpty()) {

            // TODO: SUPER_ADMIN returns empty for now — behavior to be enhanced later
            dpvs = this.dpvService.getDataProcessingActivities(this.userContext.getCurrentCustomerAccountId());

            if(null == dpvs || dpvs.isEmpty()) {

                return ResponseEntity.status(204).build();

            }

            return ResponseEntity.status(200).body(dpvs);

        } else {
            throw new BadRequestException("Unrecognised query parameter.");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(path = "{id}")
    public ResponseEntity<DataProcessingActivity> putDataProcessingActivity(
            @PathVariable("id") Long id,
            RequestEntity<DataProcessingActivity> request){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dpvService.getDataProcessingActivityById(id));

            // Apply Update
            this.dpvService.updateDataProcessingActivity(request.getBody());

            // Return 204 No Content for successful update without response body
            return ResponseEntity.status(204).build();

        } catch (EntityNotFoundException | SecurityValidationException e) {
            // Silent fail for security/privacy
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            // Explicit client error for malformed or invalid payloads
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<DataProcessingActivity> deleteDataProcessingActivity(@PathVariable("id") Long id){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dpvService.getDataProcessingActivityById(id));

            // Delete record
            this.dpvService.deleteDataProcessingActivity(id);

            // Return 204 No Content for successful update without response body
            return ResponseEntity.status(204).build();

        } catch (EntityNotFoundException | SecurityValidationException e) {
            // Silent fail for security/privacy
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            // Explicit client error for malformed or invalid payloads
            throw new BadRequestException(e.getMessage(), e);
        }

    }

}
