package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import com.sharedsystemshome.dsa.repository.DataProcessingActivityRepository;
import com.sharedsystemshome.dsa.repository.DataProcessorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessingActivityServiceTest {

    @Mock
    private DataProcessingActivityRepository dpvMockRepo;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetDataProcessingActivityById() {
    }
}