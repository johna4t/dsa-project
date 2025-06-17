package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataProcessor;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.service.DataProcessorService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/data-processors")
public class DataProcessorController {

    private final DataProcessorService dpService;
    private final UserContextService userContext;

    @Autowired
    public DataProcessorController(
            DataProcessorService dpService,
            UserContextService userContext){

        this.dpService = dpService;
        this.userContext = userContext;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<Long> postDataProcessor(RequestEntity<DataProcessor> request){

        try{
            return ResponseEntity.status(201).body(
                    this.dpService.createDataProcessor(
                            this.userContext.validateAccess(request.getBody())));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<DataProcessor>> geDataProcessors(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();
        List<DataProcessor> dps = null;

        if(null == queryParams || queryParams.isEmpty()) {

            // TODO: SUPER_ADMIN returns empty for now — behavior to be enhanced later
            dps = this.dpService.getDataProcessors(this.userContext.getCurrentCustomerAccountId());

            if(null == dps || dps.isEmpty()) {

                return ResponseEntity.status(204).build();

            }

            return ResponseEntity.status(200).body(dps);

        } else {
            throw new BadRequestException("Unrecognised query parameter.");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<DataProcessor> getDataProcessorById(@PathVariable("id") Long id) {

        try {

            return ResponseEntity.status(200).body(this.userContext.validateAccess(
                    this.dpService.getDataProcessorById(id)));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(204).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(path = "{id}")
    public ResponseEntity<DataProcessor> putDataProcessor(
            @PathVariable("id") Long id,
            RequestEntity<DataProcessor> request){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dpService.getDataProcessorById(id));

            // Apply Update
            this.dpService.updateDataProcessor(request.getBody());

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
    public ResponseEntity<DataProcessor> deleteDataContentDefinition(@PathVariable("id") Long id){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dpService.getDataProcessorById(id));

            // Delete record
            this.dpService.deleteDataProcessor(id);

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
