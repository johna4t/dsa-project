@echo off
setlocal enabledelayedexpansion

:: 1. Accept profile argument (default to 'dev')
set "PROFILE=%1"
if "%PROFILE%"=="" set "PROFILE=dev"

:: 2. Build using Maven with profile
echo [INFO] Building project with profile: %PROFILE%
call .\mvnw clean package -DBUILD_PROFILE=%PROFILE%
if errorlevel 1 (
    echo [ERROR] Maven build failed.
    exit /b 1
)

:: 3. Check if base JAR exists
set "BASE_JAR=target\\dsa-0.0.13-%PROFILE%.jar"
if not exist "!BASE_JAR!" (
    echo [ERROR] JAR not found: !BASE_JAR!
    exit /b 1
)

:: 4. Generate timestamped log filename
for /f %%t in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMddTHHmm"') do (
    set "TIMESTAMP=%%t"
)
set "LOG_FILE=target\\%PROFILE%-%TIMESTAMP%.log"

:: 5. Launch the JAR in background with timestamped log
echo [INFO] Launching !BASE_JAR! with logging to !LOG_FILE!...
start /B java -jar "!BASE_JAR!" --spring.profiles.active=%PROFILE% > "!LOG_FILE!" 2>&1

:: 6. Print success
echo [SUCCESS] Service started with profile '%PROFILE%' using !BASE_JAR!
echo [SUCCESS] Logs are being written to: !LOG_FILE!

endlocal
