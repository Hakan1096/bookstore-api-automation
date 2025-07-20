#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "🚀 Running Bookstore API Tests..."

mvn clean test

echo "📊 Generating Allure report..."
mvn allure:report

echo "✅ Tests completed! Report available at: target/allure-report/index.html"
