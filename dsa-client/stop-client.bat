@echo off
setlocal

:: Kill any existing processes on port 4200
for /f "tokens=5 delims= " %%p in ('netstat -ano ^| find ":4200"') do taskkill /f /pid %%p >nul 2>&1

endlocal
