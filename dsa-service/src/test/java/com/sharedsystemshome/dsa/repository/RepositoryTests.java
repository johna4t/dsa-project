package com.sharedsystemshome.dsa.repository;

import com.sharedsystemshome.dsa.security.repository.PermissionRepositoryTest;
import com.sharedsystemshome.dsa.security.repository.RoleRepositoryTest;
import com.sharedsystemshome.dsa.security.repository.TokenRepositoryTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;


@Suite
@SuiteDisplayName("DSA Repository Test Suite")
@SelectClasses( {
        CustomerAccountRepositoryTest.class,
        DataContentDefinitionRepositoryTest.class,
        DataContentPerspectiveRepositoryTest.class,
        DataFlowRepositoryTest.class,
        DataSharingAgreementRepositoryTest.class,
        DataSharingPartyRepositoryTest.class,
        SharedDataContentRepositoryTest.class,
        PermissionRepositoryTest.class,
        RoleRepositoryTest.class,
        TokenRepositoryTest.class,
        UserAccountRepositoryTest.class
} )
public class RepositoryTests {
}
