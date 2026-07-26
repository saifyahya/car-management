#!/usr/bin/env bash
set -euo pipefail
(cd backend && mvn spring-boot:run) &
BACKEND_PID=$!
trap 'kill $BACKEND_PID 2>/dev/null || true' EXIT
(cd frontend && npm install --no-audit --no-fund && npm start)
