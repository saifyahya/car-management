#!/bin/bash
set -e

echo "🚀 Starting Hotel Valet App Deployment..."

# 1. Create .env if not present
if [ ! -f .env ]; then
    echo "📄 Creating .env file from .env.example..."
    cp .env.example .env
fi

# 2. Build and start containers
echo "📦 Building and running Docker containers..."
docker compose down
docker compose up --build -d

echo "✅ Deployment complete! App is running on port 80."
