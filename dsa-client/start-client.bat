@echo off
setlocal

:: Get profile
set "PROFILE=%1"
if "%PROFILE%"=="" set "PROFILE=development"

cd /d "%~dp0"

call stop-client.bat
call npm install

echo [INFO] Building Angular with config: %PROFILE%
call npx ng build --configuration %PROFILE%
if errorlevel 1 (
    echo [ERROR] Build failed.
    exit /b 1
)

echo [INFO] Starting Angular development server on port 4200
call npx ng serve --port 4200 --configuration %PROFILE%

endlocal
