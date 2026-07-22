# Docker assets for SkyBook AI

| File | Purpose |
|------|---------|
| `../docker-compose.yml` | Primary Compose file (run from repo root) |
| `docker-compose.yml` | Same stack for `cd docker && docker compose up --build` |
| `.env.example` | Lab environment template |

Prefer:

```bash
cp docker/.env.example .env
./scripts/lab-up.sh
```
