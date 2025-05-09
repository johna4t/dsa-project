package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import com.sharedsystemshome.dsa.service.DataContentDefinitionService;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/data-content-definitions")
public class DataContentDefinitionController {

    private final DataContentDefinitionService dcdService;
    private final UserContextService userContext;

    @Autowired
    public DataContentDefinitionController(
            DataContentDefinitionService dcdService,
            UserContextService userContext){

        this.dcdService = dcdService;
        this.userContext = userContext;

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<Long> postDataContentDefinition(RequestEntity<DataContentDefinition> request){

        try{
            return ResponseEntity.status(201).body(
                    this.dcdService.createDataContentDefinition(
                            this.userContext.validateAccess(request.getBody())));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<DataContentDefinition>> getDataContentDefinitions(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();
        List<DataContentDefinition> dcds = null;

        if(null == queryParams || queryParams.isEmpty()) {

            // TODO: SUPER_ADMIN returns empty for now — behavior to be enhanced later
            dcds = this.dcdService.getDataContentDefinitions(this.userContext.getCurrentCustomerAccountId());

            if(null == dcds || dcds.isEmpty()) {

                return ResponseEntity.status(204).build();

            }

            return ResponseEntity.status(200).body(dcds);

        } else {
            throw new BadRequestException("Unrecognised query parameter.");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<DataContentDefinition> getDataContentDefinitionById(@PathVariable("id") Long id) {

        try {

            return ResponseEntity.status(200).body(this.userContext.validateAccess(
                    this.dcdService.getDataContentDefinitionById(id)));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(204).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(path = "{id}")
    public ResponseEntity<DataContentDefinition> putDataContentDefinition(
            @PathVariable("id") Long id,
            RequestEntity<DataContentDefinition> request){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dcdService.getDataContentDefinitionById(id));

            // Apply Update
            this.dcdService.updateDataContentDefinition(request.getBody());

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
    public ResponseEntity<DataContentDefinition> deleteDataContentDefinition(@PathVariable("id") Long id){

        try {
            // Access Validation
            this.userContext.validateAccess(this.dcdService.getDataContentDefinitionById(id));

            // Delete record
            this.dcdService.deleteDataContentDefinition(id);

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
