#!/usr/bin/env bash
# Start SkyBook AI lab stack
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

compose() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    echo "[skybook] Docker Compose is required (docker compose or docker-compose)." >&2
    exit 1
  fi
}

if [[ ! -f .env ]]; then
  echo "[skybook] Creating .env from docker/.env.example"
  cp docker/.env.example .env
fi

echo "[skybook] Building and starting containers..."
compose up --build -d

echo "[skybook] Waiting for services..."
"$ROOT_DIR/scripts/wait-healthy.sh"

set -a
# shellcheck disable=SC1091
source .env
set +a

cat <<EOF

SkyBook AI lab is up (training environment — NOT production-secure)

  UI:       http://localhost:${FRONTEND_HOST_PORT:-3000}
  Backend:  http://localhost:${BACKEND_HOST_PORT:-8080}/swagger-ui.html
  AI docs:  http://localhost:${AI_HOST_PORT:-8000}/docs
  MySQL:    localhost:${MYSQL_HOST_PORT:-3306} (skybook/skybook)

  Demo login: jdoe / Customer@123
  Admin:      admin / Admin@123

EOF
