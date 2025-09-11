@echo off
echo 🏨 Hotel Reservation System - Startup Script
echo =============================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

echo ✅ Java detected

echo.
echo Choose an option:
echo 1) Start Spring Boot Backend only
echo 2) Start JavaFX Frontend only (backend must be running)
echo 3) Start both Backend and Frontend
echo 4) Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo 🚀 Starting Spring Boot backend...
    echo    This will start the REST API server on http://localhost:8080
    echo    Press Ctrl+C to stop the backend
    echo.
    mvn spring-boot:run
) else if "%choice%"=="2" (
    echo 🖥️  Starting JavaFX frontend...
    echo    This will start the JavaFX application
    echo    Make sure the backend is running first!
    echo.
    mvn javafx:run
) else if "%choice%"=="3" (
    echo 🔄 Starting both backend and frontend...
    echo    Backend will start first, then frontend
    echo.
    start "Backend" cmd /k "mvn spring-boot:run"
    timeout /t 10 /nobreak >nul
    echo ⏳ Backend should be starting, now launching frontend...
    mvn javafx:run
) else if "%choice%"=="4" (
    echo 👋 Goodbye!
    exit /b 0
) else (
    echo ❌ Invalid choice. Please run the script again.
    pause
    exit /b 1
)

pause
