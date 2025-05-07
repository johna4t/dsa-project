@echo off
echo Starting DSA Service Build and Test Suite...

REM Clean and build the project without running tests
call .\mvnw clean install -DskipTests

REM Run only the test suite class to avoid duplication
call .\mvnw test -Dtest=DsaTests

REM Start the Spring Boot application
call .\mvnw spring-boot:run

echo DSA Service started successfully.
