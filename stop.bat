@echo off
REM Hotel Reservation System Stop Script for Windows
REM This script stops all running Hotel Reservation System processes

echo 🛑 Stopping Hotel Reservation System...

REM Stop Spring Boot backend
echo Stopping Spring Boot backend...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Hotel Backend*" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Spring Boot backend stopped
) else (
    echo ℹ️  Spring Boot backend was not running
)

REM Stop JavaFX frontend
echo Stopping JavaFX frontend...
taskkill /f /im java.exe /fi "WINDOWTITLE eq javafx:run*" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ JavaFX frontend stopped
) else (
    echo ℹ️  JavaFX frontend was not running
)

echo ✅ All Hotel Reservation System processes stopped
pause
