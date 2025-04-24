package com.sharedsystemshome;

import com.sharedsystemshome.dsa.config.ApplicationConfig;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

@SpringBootApplication
@RestController
public class DsaServiceApp {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    public static void main(String[] args) {
        ConfigurableApplicationContext configAppCtx =
                SpringApplication.run(DsaServiceApp.class, args);

        logger.info("DsaServiceApp started at " + new Date());

        logger.debug("DEBUG level logging in operation.");
        logger.info("INFO level logging in operation.");
        logger.warn("WARN level logging in operation.");
        logger.error("ERROR level logging in operation.");


    }

}
