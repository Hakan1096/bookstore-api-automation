#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "🐳 Building Docker image..."
docker build -t bookstore-api-tests .

echo "🧪 Running tests in Docker..."
docker run --rm \
  -v "$(pwd)/target:/app/target" \
  bookstore-api-tests

echo "✅ Docker tests completed!"
