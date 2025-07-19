package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataProcessingActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataProcessingActivityRepository extends JpaRepository<DataProcessingActivity, Long> {
    List<DataProcessingActivity> findByDataProcessor_Controller_Id(Long custId);
}
