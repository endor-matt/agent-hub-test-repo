#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

compose() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    echo "[skybook] Docker Compose is required." >&2
    exit 1
  fi
}

echo "[skybook] Stopping stack and removing volumes..."
compose down -v
echo "[skybook] Reset complete. Run scripts/lab-up.sh to recreate."
