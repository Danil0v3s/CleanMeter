@echo off
:: -------------------------------
:: Auto-elevate to Administrator
:: -------------------------------
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Requesting administrative privileges...
    powershell -Command "Start-Process '%~f0' -ArgumentList '%*' -Verb RunAs"
    exit /b
)

:: -------------------------------
:: Registry Delete
:: -------------------------------
set REGISTRY_KEY=HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Run
set ENTRY_NAME=%~1

echo ========================================
echo  CleanMeter Registry Deleter
echo ========================================
echo.

echo DEBUG: Entry Name = "%ENTRY_NAME%"
echo.

:: Check if parameter is provided
if "%ENTRY_NAME%"=="" (
    echo ERROR: Missing entry name parameter.
    echo Usage: %~nx0 "entry_name"
    echo Example: %~nx0 "MyApp"
    exit /b 1
)

echo Deleting registry entry...
echo Key: %REGISTRY_KEY%
echo Name: %ENTRY_NAME%
echo.

:: Check if entry exists
reg query "%REGISTRY_KEY%" /v "%ENTRY_NAME%" >nul 2>&1
if %errorLevel% neq 0 (
    echo WARNING: Registry entry "%ENTRY_NAME%" does not exist.
    echo Operation completed.
    exit /b 0
)

echo DEBUG: Executing command: reg delete "%REGISTRY_KEY%" /v "%ENTRY_NAME%" /f
reg delete "%REGISTRY_KEY%" /v "%ENTRY_NAME%" /f
set DELETE_RESULT=%errorLevel%
echo DEBUG: Command result: %DELETE_RESULT%

if %DELETE_RESULT% neq 0 (
    echo ERROR: Failed to delete registry entry (Error Code: %DELETE_RESULT%)
    exit /b %DELETE_RESULT%
) else (
    echo SUCCESS: Registry entry deleted successfully!
)

echo Operation completed.
