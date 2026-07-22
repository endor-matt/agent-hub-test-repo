#!/usr/bin/env bash
# Wait until core lab endpoints respond
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

compose() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    return 0
  fi
}

BACKEND_URL="${BACKEND_URL:-http://localhost:8080/v3/api-docs}"
AI_URL="${AI_URL:-http://localhost:8000/api/v1/health}"
UI_URL="${UI_URL:-http://localhost:3000/}"
RETRIES="${RETRIES:-60}"

wait_url() {
  local url="$1"
  local name="$2"
  local i=0
  until curl -sf "$url" >/dev/null 2>&1; do
    i=$((i + 1))
    if [[ $i -ge $RETRIES ]]; then
      echo "[skybook] Timeout waiting for $name ($url)"
      compose ps || true
      exit 1
    fi
    sleep 3
  done
  echo "[skybook] Ready: $name"
}

wait_url "$BACKEND_URL" "backend"
wait_url "$AI_URL" "python-ai"
wait_url "$UI_URL" "frontend"
