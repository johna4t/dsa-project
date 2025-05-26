package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.SharedDataContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SharedDataContentRepository extends JpaRepository<SharedDataContent, Long> {

    @Modifying
    @Query("DELETE FROM SharedDataContent sdc WHERE sdc.dataFlow.id = :dataFlowId")
    void deleteByDataFlowId(@Param("dataFlowId") Long dataFlowId);
}
