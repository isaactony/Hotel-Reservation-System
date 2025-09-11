#!/bin/bash

# Hotel Reservation System Startup Script
# This script starts both the Spring Boot backend and JavaFX frontend

echo "🏨 Hotel Reservation System - Starting Application..."
echo "=================================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher and try again"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher and try again"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Error: Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo "✅ Maven version: $(mvn -version | head -n 1)"
echo ""

# Function to cleanup background processes
cleanup() {
    echo ""
    echo "🛑 Shutting down Hotel Reservation System..."
    echo "Stopping Spring Boot backend..."
    pkill -f "spring-boot:run" 2>/dev/null || true
    echo "Stopping JavaFX frontend..."
    pkill -f "javafx:run" 2>/dev/null || true
    echo "✅ All processes stopped"
    exit 0
}

# Set up signal handlers for cleanup
trap cleanup SIGINT SIGTERM

# Compile the project first
echo "🔨 Compiling project..."
if ! mvn compile -q; then
    echo "❌ Error: Project compilation failed"
    exit 1
fi
echo "✅ Project compiled successfully"
echo ""

# Start Spring Boot backend in background
echo "🚀 Starting Spring Boot backend on port 8081..."
mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 10

# Check if backend is running
if ! curl -s http://127.0.0.1:8081/api/rooms > /dev/null 2>&1; then
    echo "❌ Error: Backend failed to start properly"
    echo "Check backend.log for details"
    kill $BACKEND_PID 2>/dev/null || true
    exit 1
fi

echo "✅ Backend started successfully on http://127.0.0.1:8081"
echo ""

# Start JavaFX frontend
echo "🖥️  Starting JavaFX frontend..."
echo "=================================================="
echo "📋 Sample Login Credentials:"
echo "   Admin:  admin1 / password123"
echo "   Visitor: visitor1 / password123"
echo "=================================================="
echo ""

# Start JavaFX in foreground
mvn javafx:run

# If we reach here, JavaFX has exited
echo ""
echo "🛑 JavaFX application closed"
cleanup
