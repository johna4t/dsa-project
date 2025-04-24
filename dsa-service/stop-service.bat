@echo off
setlocal

set "PROFILE=%1"
if "%PROFILE%"=="" set "PROFILE=dev"

echo [INFO] Stopping all Java processes using profile [%PROFILE%]...

powershell -NoProfile -Command ^
  "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*--spring.profiles.active=%PROFILE%*' } | ForEach-Object { Write-Host 'Killing PID:' $_.ProcessId; Stop-Process -Id $_.ProcessId -Force }"

echo [INFO] All matching processes stopped (if permissions allowed).

endlocal
