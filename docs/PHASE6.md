# SkyBook AI — Phase 6 Docker

> Isolated security research & training stack. **Not production-secure.**

## Services

| Service | Container | Host port | Image/build |
|---------|-----------|-----------|-------------|
| MySQL 8.4 | `skybook-mysql` | 3306 | Official + schema/seed init |
| Backend | `skybook-backend` | 8080 | `backend/Dockerfile` |
| AI | `skybook-python-ai` | 8000 | `ai-service/Dockerfile` |
| Frontend | `skybook-frontend` | 3000 | nginx + SPA |

Network: `skybook-net`

## Quick start

```bash
cp docker/.env.example .env
./scripts/lab-up.sh
```

Or:

```bash
docker compose up --build -d
./scripts/wait-healthy.sh
```

## URLs

- App: http://localhost:3000  
- Swagger: http://localhost:8080/swagger-ui.html  
- AI docs: http://localhost:8000/docs  

## Scripts

| Script | Action |
|--------|--------|
| `scripts/lab-up.sh` | Build + start + wait |
| `scripts/lab-down.sh` | Stop |
| `scripts/lab-reset.sh` | Stop + delete volumes (re-seed) |
| `scripts/lab-logs.sh` | Follow logs |
| `scripts/wait-healthy.sh` | Poll health endpoints |

## Reset database

```bash
./scripts/lab-reset.sh
./scripts/lab-up.sh
```

## Training mode

`TRAINING_MODE=true` by default. Phase 8 intentional demos are reachable out of the box.

## Phase gate

**Phase 6 complete when you confirm.**  
Next: **Phase 7 — Documentation suite**.
