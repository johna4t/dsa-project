package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.DataSharingParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DataSharingPartyRepository extends JpaRepository<DataSharingParty, Long> {
}
