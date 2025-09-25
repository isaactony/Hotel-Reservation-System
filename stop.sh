#!/bin/bash

# Hotel Reservation System Stop Script
# This script stops all running Hotel Reservation System processes

echo "🛑 Stopping Hotel Reservation System..."

# Stop Spring Boot backend
echo "Stopping Spring Boot backend..."
pkill -f "spring-boot:run" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Spring Boot backend stopped"
else
    echo "ℹ️  Spring Boot backend was not running"
fi

# Stop JavaFX frontend
echo "Stopping JavaFX frontend..."
pkill -f "javafx:run" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ JavaFX frontend stopped"
else
    echo "ℹ️  JavaFX frontend was not running"
fi

echo "✅ All Hotel Reservation System processes stopped"
