#!/usr/bin/env bash
# SkyBook AI — apply schema + seed to a local MySQL instance
# Usage: ./database/init.sh [MYSQL_HOST] [MYSQL_PORT] [MYSQL_USER] [MYSQL_PASSWORD]
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HOST="${1:-127.0.0.1}"
PORT="${2:-3306}"
USER="${3:-root}"
PASS="${4:-root}"

echo "[skybook] Applying schema to ${USER}@${HOST}:${PORT} ..."
mysql -h "$HOST" -P "$PORT" -u "$USER" -p"$PASS" < "$ROOT_DIR/database/schema.sql"
echo "[skybook] Applying seed ..."
mysql -h "$HOST" -P "$PORT" -u "$USER" -p"$PASS" < "$ROOT_DIR/database/seed.sql"
echo "[skybook] Database ready."
