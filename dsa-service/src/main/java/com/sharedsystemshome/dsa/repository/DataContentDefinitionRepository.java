package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataContentDefinitionRepository extends JpaRepository<DataContentDefinition, Long> {
    List<DataContentDefinition> findByProviderId(Long provId);

    Optional<DataContentDefinition> findByIdAndProviderId(Long id, Long provId);

}
