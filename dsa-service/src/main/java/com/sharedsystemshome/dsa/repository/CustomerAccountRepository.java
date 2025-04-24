package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

}
