package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataContentDefinitionRepository extends JpaRepository<DataContentDefinition, Long> {

}
