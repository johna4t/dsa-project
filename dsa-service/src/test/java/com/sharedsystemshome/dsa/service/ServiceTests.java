package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.security.service.AuthenticationServiceTest;
import com.sharedsystemshome.dsa.security.service.TokenServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;


@Suite
@SuiteDisplayName("DSA Service Test Suite")
// Alternatively can use @SelectPackages({"com.sharedsystemshome.dsa.service.DataSharingPartyServiceTest", "..."})
@SelectClasses( {
        AuthenticationServiceTest.class,
        CustomerAccountServiceTest.class,
        DataContentDefinitionServiceTest.class,
        DataFlowServiceTest.class,
        DataProcessorServiceTest.class,
        DataSharingAgreementServiceTest.class,
        DataSharingPartyServiceTest.class,
        PersonalProfileServiceTest.class,
        TokenServiceTest.class,
        UserAccountServiceTest.class
} )
public class ServiceTests {
}
