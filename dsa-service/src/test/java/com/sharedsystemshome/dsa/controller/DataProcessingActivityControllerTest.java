package com.sharedsystemshome.dsa.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

class DataProcessingActivityControllerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "user", roles = "SUPER_ADMIN")
    void testGetDataProcessingActivityById() throws Exception {

    }
}