package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataFlow;
import com.sharedsystemshome.dsa.model.DataSharingAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSharingAgreementRepository extends JpaRepository<DataSharingAgreement, Long> {
    public Optional<List<DataSharingAgreement>> findDataSharingAgreementByAccountHolderId(Long custId);
}
