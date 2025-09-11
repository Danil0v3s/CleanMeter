@echo off
:: -------------------------------
:: Auto-elevate to Administrator
:: -------------------------------
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Requesting administrative privileges...
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

:: -------------------------------
:: Service config
:: -------------------------------
set SERVICE_NAME=CleanMeterHardwareMonitor
set SERVICE_DESCRIPTION=Hardware monitoring service for CleanMeter application
set DISPLAY_NAME=CleanMeter Hardware Monitor

echo ========================================
echo  CleanMeter Hardware Monitor - STOP
echo ========================================
echo.

REM Check if service exists
echo Checking if service exists...
sc query "%SERVICE_NAME%" >nul 2>&1
if %errorLevel% neq 0 (
    echo Service "%SERVICE_NAME%" does not exist.
    echo Operation completed.
    exit /b 1
)

REM Check if service is running
echo Checking service status...
sc query "%SERVICE_NAME%" | find "RUNNING" >nul
if %errorLevel% neq 0 (
    echo Service "%SERVICE_NAME%" is not running.
    echo Operation completed.
    exit /b 0
)

REM Stop the service
echo Stopping service...
sc stop "%SERVICE_NAME%"
set STOP_RESULT=%errorLevel%
echo Stop command result: %STOP_RESULT%

if %STOP_RESULT% neq 0 (
    echo WARNING: Failed to stop service (Error Code: %STOP_RESULT%)
    echo Check the Event Log for details or try stopping manually using: sc stop "%SERVICE_NAME%"
) else (
    echo Waiting for service to stop...
    timeout /t 3 /nobreak >nul
    
    REM Verify service has stopped
    sc query "%SERVICE_NAME%" | find "STOPPED" >nul
    if %errorLevel% equ 0 (
        echo SUCCESS: CleanMeter Hardware Monitor Service stopped successfully!
    ) else (
        echo WARNING: Service may still be stopping...
    )
)

echo Operation completed.

