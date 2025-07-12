package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import com.sharedsystemshome.dsa.model.DataProcessor;
import com.sharedsystemshome.dsa.repository.DataProcessingActivityRepository;
import com.sharedsystemshome.dsa.repository.DataProcessorRepository;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.AddOrUpdateTransactionException;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.EntityNotFoundException;
import com.sharedsystemshome.dsa.util.NullOrEmptyValueException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
@Validated
public class DataProcessingActivityService {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessingActivityService.class);

    private final DataProcessingActivityRepository dpvRepo;


    //READ
    @GetMapping("/{id}")
    public DataProcessingActivity getDataProcessingActivityById(@PathVariable Long id) {
        logger.debug("Entering method DataProcessingActivity::getDataProcessingActivityById with id: {}", id);

        DataProcessingActivity dpv = this.dpvRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        BusinessValidationException.DATA_PROCESSING_ACTIVITY, id));

        logger.info("Successfully found DataProcessingActivity with id: {}", id);

        return dpv;
    }
}
