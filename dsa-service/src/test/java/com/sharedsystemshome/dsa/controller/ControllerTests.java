package com.sharedsystemshome.dsa.controller;

import com.sharedsystemshome.dsa.security.controller.AuthenticationControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;


@Suite
@SuiteDisplayName("DSA Controller Test Suite")
@SelectClasses( {
        AuthenticationControllerTest.class,
        CustomerAccountControllerTest.class,
        DataContentDefinitionControllerTest.class,
        DataFlowControllerTest.class,
        DataSharingAgreementControllerTest.class,
        DataSharingPartyControllerTest.class,
        PersonalProfileControllerTest.class,
        UserAccountControllerTest.class,
} )
public class ControllerTests {
}
