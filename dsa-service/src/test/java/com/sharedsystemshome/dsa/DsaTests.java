package com.sharedsystemshome.dsa;

import com.sharedsystemshome.dsa.controller.ControllerTests;
import com.sharedsystemshome.dsa.repository.RepositoryTests;
import com.sharedsystemshome.dsa.service.ServiceTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("DSA Test Runner")
@SelectClasses({
        RepositoryTests.class,
        ServiceTests.class,
        ControllerTests.class
})
public class DsaTests {
    // This class doesn't need any methods or code.
}
