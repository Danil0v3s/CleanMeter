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
echo  CleanMeter Hardware Monitor - DELETE
echo ========================================
echo.

REM Check if service exists
echo Checking if service exists...
sc query "%SERVICE_NAME%" >nul 2>&1
if %errorLevel% neq 0 (
    echo Service "%SERVICE_NAME%" does not exist or is already uninstalled.
    exit /b 1
)
echo Service found, proceeding with removal...

REM Stop the service
echo Stopping service...
sc stop "%SERVICE_NAME%"
echo Stop command result: %errorLevel%

REM Wait a moment for the service to stop
echo Waiting for service to stop...
timeout /t 5 /nobreak >nul

REM Delete the service
echo Removing service...
sc delete "%SERVICE_NAME%"
set DELETE_RESULT=%errorLevel%
echo Delete command result: %DELETE_RESULT%

if %DELETE_RESULT% neq 0 (
    echo ERROR: Failed to remove service (Error Code: %DELETE_RESULT%)
) else (
    echo SUCCESS: Service removed successfully!
    echo.
    echo Verifying removal:
    sc query "%SERVICE_NAME%" >nul 2>&1
    if %errorLevel% neq 0 (
        echo Service successfully removed from system.
    ) else (
        echo WARNING: Service may still be present in system.
    )
)
