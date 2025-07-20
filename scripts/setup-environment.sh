#!/bin/bash
set -e

# Define directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Navigate to the project directory
cd "$PROJECT_DIR"

echo "🔧 Setting up environment variables..."
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
export API_BASE_URL="https://fakerestapi.azurewebsites.net"

echo "📦 Installing dependencies..."
if ! command -v mvn &> /dev/null; then
  echo "Maven not found. Installing Maven..."
  sudo apt-get update && sudo apt-get install -y maven
fi

if ! command -v docker &> /dev/null; then
  echo "Docker not found. Installing Docker..."
  sudo apt-get update && sudo apt-get install -y docker.io
fi

echo "🐳 Ensuring Docker is running..."
if ! systemctl is-active --quiet docker; then
  echo "Starting Docker..."
  sudo systemctl start docker
fi

echo "🧹 Cleaning project directory..."
mvn clean

echo "✅ Environment setup complete!"