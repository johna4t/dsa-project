package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
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

    @Autowired
    public DataContentDefinitionController(DataContentDefinitionService dcdService){

        this.dcdService = dcdService;

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
    public ResponseEntity<List<DataContentDefinition>> getDataContentDefinitions(@RequestParam Map<String,String> params) {

        // Supports /api/v1/customer-accounts?{param 1}={value 1}
        if(null == params || 0 == params.size()) {

            List<DataContentDefinition> dcds = this.dcdService.getDataContentDefinitions();

            if(null != dcds && !dcds.isEmpty()){

            return ResponseEntity.status(200).body(dcds);

            } else {

                return ResponseEntity.status(204).build();

            }
        } else {
            throw new BadRequestException("Unrecognised query parameter.");
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
