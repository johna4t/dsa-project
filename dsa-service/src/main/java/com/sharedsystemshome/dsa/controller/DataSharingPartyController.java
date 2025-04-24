package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.service.DataSharingPartyService;

import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/data-sharing-parties")
public class DataSharingPartyController {


    private final DataSharingPartyService dspService;
    @Autowired
    public DataSharingPartyController(DataSharingPartyService dspService){

        this.dspService = dspService;

    }

    @PostMapping
    public ResponseEntity<Long> postDataSharingParty(RequestEntity<DataSharingParty> request){

        try{
            return ResponseEntity.status(201).body(this.dspService.createDataSharingParty(request.getBody()));
        } catch(Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<DataSharingParty>> getDataSharingParties(@RequestParam Map<String,String> params) {

        // Supports /api/v1/data-sharing-parties?{param 1}={value 1}
        if(null == params || 0 == params.size()) {

            List<DataSharingParty> dsps = this.dspService.getDataSharingParties();

            if(null != dsps && !dsps.isEmpty()){

                return ResponseEntity.status(200).body(dsps);

            } else {

                return ResponseEntity.status(204).build();

            }
        } else {
            throw new BadRequestException("Unrecognised query parameter.");
        }
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<DataSharingParty> getDataSharingPartyById(@PathVariable("id") Long id) {

        try{

            return ResponseEntity.status(200).body(this.dspService.getDataSharingPartyById(id));

        } catch(EntityNotFoundException e){

            return ResponseEntity.status(204).build();

        }

    }

    @PutMapping
    public ResponseEntity<DataSharingParty> putDataSharingParty(RequestEntity<DataSharingParty> request){

        this.dspService.updateDataSharingParty(request.getBody());

        return ResponseEntity.status(204).build();
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<DataSharingParty> deleteDataSharingParty(@PathVariable("id") Long id){
        this.dspService.deleteDataSharingParty(id);

        return ResponseEntity.status(204).build();
    }
}
