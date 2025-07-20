#!/bin/bash
set -e

# Define directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Navigate to the project directory
cd "$PROJECT_DIR"

echo "🚀 Cleaning and building the project..."
mvn clean install -DskipTests

echo "📊 Generating Allure report..."
mvn allure:report

echo "🌐 Serving Allure report locally..."
mvn allure:serve