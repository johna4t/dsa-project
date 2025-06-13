package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataProcessor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataProcessorRepository extends JpaRepository<DataProcessor, Long> {

    List<DataProcessor> findByControllerId(Long conId);
}
