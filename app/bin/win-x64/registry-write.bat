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
:: Registry Write
:: -------------------------------
set REGISTRY_KEY=HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Run
set ENTRY_NAME=%~1
set ENTRY_VALUE=%~2

echo ========================================
echo  CleanMeter Registry Writer
echo ========================================
echo.

echo DEBUG: Entry Name = "%ENTRY_NAME%"
echo DEBUG: Entry Value = "%ENTRY_VALUE%"
echo.

:: Check if parameters are provided
if "%ENTRY_NAME%"=="" (
    echo ERROR: Missing entry name parameter.
    echo Usage: %~nx0 "entry_name" "entry_value"
    echo Example: %~nx0 "MyApp" "C:\Path\To\MyApp.exe"
    exit /b 1
)

if "%ENTRY_VALUE%"=="" (
    echo ERROR: Missing entry value parameter.
    echo Usage: %~nx0 "entry_name" "entry_value"
    echo Example: %~nx0 "MyApp" "C:\Path\To\MyApp.exe"
    exit /b 1
)

echo Writing registry entry...
echo Key: %REGISTRY_KEY%
echo Name: %ENTRY_NAME%
echo Value: %ENTRY_VALUE%
echo.

echo DEBUG: Executing command: reg add "%REGISTRY_KEY%" /v "%ENTRY_NAME%" /t REG_SZ /d "%ENTRY_VALUE%" /f
reg add "%REGISTRY_KEY%" /v "%ENTRY_NAME%" /t REG_SZ /d "%ENTRY_VALUE%" /f
set WRITE_RESULT=%errorLevel%
echo DEBUG: Command result: %WRITE_RESULT%

if %WRITE_RESULT% neq 0 (
    echo ERROR: Failed to write registry entry (Error Code: %WRITE_RESULT%)
    exit /b %WRITE_RESULT%
) else (
    echo SUCCESS: Registry entry written successfully!
)

echo Operation completed.
