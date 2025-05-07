package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataContentDefinitionRepository extends JpaRepository<DataContentDefinition, Long> {
    Optional<List<DataContentDefinition>> findDataContentDefinitionByProviderId(Long custId);

}
