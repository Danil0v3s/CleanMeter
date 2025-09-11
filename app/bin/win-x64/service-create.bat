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
set SCRIPT_DIR=%~dp0
set EXE_PATH=%SCRIPT_DIR%HardwareMonitor.exe

echo ========================================
echo  CleanMeter Hardware Monitor - CREATE
echo ========================================
echo.

REM Check if executable exists
echo Checking for executable at: %EXE_PATH%
if not exist "%EXE_PATH%" (
    echo ERROR: HardwareMonitor.exe not found at: %EXE_PATH%
    echo Please build and publish the project first using:
    echo dotnet publish -c Release -r win-x64 --self-contained false
    pause
    exit /b 1
)
echo Found executable: %EXE_PATH%

REM Stop the service if it's running
echo Stopping service if running...
sc stop "%SERVICE_NAME%" >nul
echo Stop command result: %errorLevel% >nul

REM Delete existing service if it exists
echo Removing existing service if it exists...
sc delete "%SERVICE_NAME%" >nul
echo Delete command result: %errorLevel% >nul

REM Create the service
echo.
echo Creating service with command:
echo sc create "%SERVICE_NAME%" binPath= "\"%EXE_PATH%\"" DisplayName= "%DISPLAY_NAME%" start= auto
sc create "%SERVICE_NAME%" binPath= "\"%EXE_PATH%\"" DisplayName= "%DISPLAY_NAME%" start= auto

REM Wait a moment for service registration to complete
echo.
echo Waiting for service registration to complete...
timeout /t 5 /nobreak >nul

echo Service created successfully!

REM Set service description
echo Setting service description...
sc description "%SERVICE_NAME%" "%SERVICE_DESCRIPTION%"
echo Description command result: %errorLevel%

REM Configure service recovery options
echo Configuring service recovery options...
sc failure "%SERVICE_NAME%" reset= 86400 actions= restart/30000/restart/60000/restart/120000
echo Recovery options result: %errorLevel%

REM Start the service
echo Starting service...
sc start "%SERVICE_NAME%"
set START_RESULT=%errorLevel%
echo Start command result: %START_RESULT%

if %START_RESULT% neq 0 (
    echo WARNING: Service created but failed to start (Error Code: %START_RESULT%)
    echo Check the Event Log for details or start manually using: sc start "%SERVICE_NAME%"
) else (
    echo SUCCESS: CleanMeter Hardware Monitor Service installed and started successfully!
)

echo Operation completed.
