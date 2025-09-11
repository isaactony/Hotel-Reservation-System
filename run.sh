#!/bin/bash

# Hotel Reservation System - Run Script
echo "🏨 Hotel Reservation System - Startup Script"
echo "============================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION detected. Java 17 or higher is required."
    exit 1
fi

echo "✅ Java version $JAVA_VERSION detected"

# Function to start Spring Boot backend
start_backend() {
    echo "🚀 Starting Spring Boot backend..."
    echo "   This will start the REST API server on http://localhost:8080"
    echo "   Press Ctrl+C to stop the backend"
    echo ""
    mvn spring-boot:run
}

# Function to start JavaFX frontend
start_frontend() {
    echo "🖥️  Starting JavaFX frontend..."
    echo "   This will start the JavaFX application"
    echo "   Make sure the backend is running first!"
    echo ""
    
    # Try Maven JavaFX plugin first
    if mvn javafx:run 2>/dev/null; then
        echo "✅ JavaFX started successfully with Maven plugin"
    else
        echo "⚠️  Maven JavaFX plugin failed, trying alternative method..."
        
        # Compile the project
        echo "📦 Compiling project..."
        mvn clean compile -q
        
        if [ $? -eq 0 ]; then
            echo "✅ Compilation successful"
            
            # Try to run with java command directly
            echo "🚀 Starting JavaFX application..."
            java --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED \
                 --add-exports javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED \
                 --add-exports javafx.base/com.sun.javafx.binding=ALL-UNNAMED \
                 --add-exports javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED \
                 --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
                 -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) \
                 com.hotel.javafx.SimpleLauncher
        else
            echo "❌ Compilation failed"
            exit 1
        fi
    fi
}

# Function to start both
start_both() {
    echo "🔄 Starting both backend and frontend..."
    echo "   Backend will start first, then frontend"
    echo ""
    
    # Start backend in background
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    # Wait a bit for backend to start
    echo "⏳ Waiting for backend to start..."
    sleep 15
    
    # Start frontend
    start_frontend
    
    # Clean up background process when frontend exits
    kill $BACKEND_PID 2>/dev/null
}

# Main menu
echo ""
echo "Choose an option:"
echo "1) Start Spring Boot Backend only"
echo "2) Start JavaFX Frontend only (backend must be running)"
echo "3) Start both Backend and Frontend"
echo "4) Exit"
echo ""

read -p "Enter your choice (1-4): " choice

case $choice in
    1)
        start_backend
        ;;
    2)
        start_frontend
        ;;
    3)
        start_both
        ;;
    4)
        echo "👋 Goodbye!"
        exit 0
        ;;
    *)
        echo "❌ Invalid choice. Please run the script again."
        exit 1
        ;;
esac
