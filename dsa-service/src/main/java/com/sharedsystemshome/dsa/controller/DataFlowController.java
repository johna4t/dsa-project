package com.sharedsystemshome.dsa.controller;

import java.time.LocalDate;

import com.sharedsystemshome.dsa.service.DataFlowService;
import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.enums.LawfulBasis;
import com.sharedsystemshome.dsa.enums.SpecialCategoryData;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/data-flows")
public class DataFlowController {


    private final DataFlowService dataFlowService;
    @Autowired
    public DataFlowController(DataFlowService dataFlowService){

        this.dataFlowService = dataFlowService;

    }

    @PostMapping
    public ResponseEntity<Long> postDataFlow(RequestEntity<DataFlow> request){

        try{
            return ResponseEntity.status(201).body(this.dataFlowService.createDataFlow(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }

    }

    @GetMapping
    public ResponseEntity<List<DataFlow>> getDataFlows(@RequestParam Map<String,String> params) {

        // Supports /api/v1/data-flows?{param 1}={value 1}
        if(null == params || 0 == params.size()){

            return this.getDataFlowsResponse(this.dataFlowService.getDataFlows());

        } else if(1 < params.size()) {

            throw new BadRequestException("Too many parameters passed.");

        } else if(params.containsKey("provider")){

            Long provId;

            try {
                provId = Long.parseLong(params.get("provider"));
            } catch (NumberFormatException nfe) {
                throw new BadRequestException("Invalid id format.");
            }

            return this.getDataFlowsResponse(this.dataFlowService.getDataFlowsByProviderId(provId));

        } else if(params.containsKey("consumer")){

            Long consId;

            try {
                consId = Long.parseLong(params.get("consumer"));
            } catch (NumberFormatException nfe) {
                throw new BadRequestException("Invalid id format.");
            }

            return this.getDataFlowsResponse(this.dataFlowService.getDataFlowsByConsumerId(consId));

        }else{
            throw new BadRequestException("Unrecognised query parameter.");
        }
    }

    private ResponseEntity<List<DataFlow>> getDataFlowsResponse(List<DataFlow> dataFlows){

        if(null != dataFlows && !dataFlows.isEmpty()){

            return ResponseEntity.status(200).body(dataFlows);

        } else {

            return ResponseEntity.status(204).build();

        }

    }

    @GetMapping(path = "{id}")
    public  ResponseEntity<DataFlow> getDataFlowById(@PathVariable("id") Long id) {

        try{

            return ResponseEntity.status(200).body(this.dataFlowService.getDataFlowById(id));

        } catch(EntityNotFoundException e){

            return ResponseEntity.status(204).build();

        }

    }

    @PatchMapping(path = "{id}")
    /**
     * ISS-000-003: method does not support all entity attributes.
     */
    public ResponseEntity<DataFlow> patchDataFlow(
            @PathVariable("id") Long id,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) LawfulBasis lawfulBasis,
            @RequestParam(required = false) SpecialCategoryData specialCategory,
            @RequestParam(required = false) String purposeOfSharing
            ){

        this.dataFlowService.updateDataFlow(
                id,
                endDate,
                lawfulBasis,
                specialCategory,
                purposeOfSharing
        );

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<DataFlow> deleteDataFlow(@PathVariable("id") Long id){
        this.dataFlowService.deleteDataFlow(id);

        return ResponseEntity.status(204).build();

    }

}
