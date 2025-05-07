package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.security.service.UserContextService;
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
            return ResponseEntity.status(201).body(this.dcdService.createDataContentDefinition(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping

    public ResponseEntity<List<DataContentDefinition>> getDataContentDefinitions(RequestEntity<Map<String, String>> requestEntity) {
        Map<String, String> queryParams = requestEntity.getBody();

        if(null == queryParams || queryParams.isEmpty()) {

            if(this.userContext.isSuperAdmin()){

                return getDataContentDefinitionsResponse(this.dcdService.getDataContentDefinitions());

            } else {

                Long id = this.userContext.getCurrentUser().getParentAccount().getId();

                return getDataContentDefinitionsResponse(
                        this.dcdService.getDataContentDefinitionsByProviderId(
                                this.userContext.getCurrentUser().getParentAccount().getId()));
            }

        } else {
            throw new BadRequestException("Unrecognised query parameter.");
        }

    }


    private ResponseEntity<List<DataContentDefinition>> getDataContentDefinitionsResponse(List<DataContentDefinition> dcds){

        if(null != dcds && !dcds.isEmpty()){

            return ResponseEntity.status(200).body(dcds);

        } else {

            return ResponseEntity.status(204).build();

        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "{id}")
    public ResponseEntity<DataContentDefinition> getDataContentDefinitionById(@PathVariable("id") Long id) {

        try{

            return ResponseEntity.status(200).body(this.dcdService.getDataContentDefinitionById(id));

        } catch(EntityNotFoundException e){

            return ResponseEntity.status(204).build();

        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping(path = "{id}")
    public ResponseEntity<DataContentDefinition> patchDataContentDefinition(
            @PathVariable("id") Long id,
            @RequestParam String description){

        this.dcdService.updateDataContentDefinition(
                id,
                null,
                description,
                null);

        return ResponseEntity.status(204).build();

    }


    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<DataContentDefinition> deleteDataContentDefinition(@PathVariable("id") Long id){

        this.dcdService.deleteDataContentDefinition(id);

        return ResponseEntity.status(204).build();

    }

}
