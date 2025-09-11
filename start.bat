@echo off
REM Hotel Reservation System Startup Script for Windows
REM This script starts both the Spring Boot backend and JavaFX frontend

echo 🏨 Hotel Reservation System - Starting Application...
echo ==================================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher and try again
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher and try again
    pause
    exit /b 1
)

echo ✅ Java and Maven are installed
echo.

REM Compile the project first
echo 🔨 Compiling project...
mvn compile -q
if %errorlevel% neq 0 (
    echo ❌ Error: Project compilation failed
    pause
    exit /b 1
)
echo ✅ Project compiled successfully
echo.

REM Start Spring Boot backend in background
echo 🚀 Starting Spring Boot backend on port 8081...
start "Hotel Backend" cmd /c "mvn spring-boot:run"

REM Wait for backend to start
echo ⏳ Waiting for backend to start...
timeout /t 15 /nobreak >nul

REM Check if backend is running
curl -s http://127.0.0.1:8081/api/rooms >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Backend failed to start properly
    echo Please check the backend window for details
    pause
    exit /b 1
)

echo ✅ Backend started successfully on http://127.0.0.1:8081
echo.

REM Start JavaFX frontend
echo 🖥️  Starting JavaFX frontend...
echo ==================================================
echo 📋 Sample Login Credentials:
echo    Admin:  admin1 / password123
echo    Visitor: visitor1 / password123
echo ==================================================
echo.

REM Start JavaFX
mvn javafx:run

echo.
echo 🛑 JavaFX application closed
echo Press any key to exit...
pause >nul
