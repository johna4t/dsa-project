package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataFlowRepository extends JpaRepository<DataFlow, Long> {

   public Optional<List<DataFlow>> findDataFlowByConsumerId(Long consId);
   public Optional<List<DataFlow>> findDataFlowByProviderId(Long provId);
}