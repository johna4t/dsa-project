package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import com.sharedsystemshome.dsa.model.DataProcessor;
import com.sharedsystemshome.dsa.security.service.UserContextService;
import com.sharedsystemshome.dsa.service.DataProcessingActivityService;
import com.sharedsystemshome.dsa.service.DataProcessorService;
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
    @GetMapping(path = "{id}")
    public ResponseEntity<DataProcessingActivity> getDataProcessingActivityById(@PathVariable("id") Long id) {

        try {

            return ResponseEntity.status(200).body(this.userContext.validateAccess(
                    this.dpvService.getDataProcessingActivityById(id)));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(204).build();
        }
    }

}
