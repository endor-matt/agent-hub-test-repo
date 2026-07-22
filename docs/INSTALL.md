# SkyBook AI — Installation Guide

> **Security research & training lab only.** Do not deploy to the public internet or treat as production-secure.

## Prerequisites

| Tool | Version |
|------|---------|
| Docker + Compose plugin | Recent (Compose V2) |
| **or** local stack | Node 18+, Java 21, Maven 3.9+, Python 3.10+, MySQL 8 |
| curl | For health checks |

## Option A — Docker (recommended)

```bash
git clone <repo-url> skybook-ai
cd skybook-ai
cp docker/.env.example .env
./scripts/lab-up.sh
```

| Service | URL |
|---------|-----|
| Web UI | http://localhost:3000 |
| Backend Swagger | http://localhost:8080/swagger-ui.html |
| AI OpenAPI | http://localhost:8000/docs |
| MySQL | `localhost:3306` user/pass `skybook`/`skybook` |

### Lab logins

| Username | Password | Role |
|----------|----------|------|
| `admin` | `Admin@123` | ADMIN |
| `jdoe` | `Customer@123` | CUSTOMER |
| `asmith` / `mchen` / `lwong` | `Customer@123` | CUSTOMER |

### Useful commands

```bash
./scripts/lab-logs.sh          # follow logs
./scripts/lab-down.sh          # stop
./scripts/lab-reset.sh         # wipe DB volume + reseed on next up
```

### Optional LLM

Set in `.env`:

```bash
OPENAI_API_KEY=sk-...
# OPENAI_BASE_URL=https://api.openai.com/v1   # or compatible gateway
OPENAI_MODEL=gpt-4o-mini
```

Without a key, the AI service uses offline FAQ mode.

---

## Option B — Local development (no Docker)

### 1. Database

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
# or: ./database/init.sh 127.0.0.1 3306 root <password>
```

Create app user if needed:

```sql
CREATE USER IF NOT EXISTS 'skybook'@'%' IDENTIFIED BY 'skybook';
GRANT ALL ON skybook.* TO 'skybook'@'%';
FLUSH PRIVILEGES;
```

### 2. Backend

```bash
cd backend
# If ~/.m2/settings.xml is broken, use a clean settings file:
mvn -s /path/to/clean-settings.xml spring-boot:run
```

Env / defaults: `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (see `application.yml`).

Swagger: http://localhost:8080/swagger-ui.html

### 3. AI service

```bash
cd ai-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

Docs: http://localhost:8000/docs

### 4. Frontend

```bash
cd frontend
npm install --registry https://registry.npmjs.org/
npm run dev
```

UI: http://localhost:3000  
Vite proxies `/api` → `:8080` and `/ai` → `:8000`.

---

## Verification checklist

- [ ] Login as `jdoe` / `Customer@123`
- [ ] Search JFK → LAX (date ≈ today+3 from seed)
- [ ] Book a flight with seat selection
- [ ] Ask AI about baggage / refunds
- [ ] Login as `admin` / `Admin@123` → Audit dashboard → export CSV

---

## Training mode

Intentionally vulnerable demos are **enabled by default** so lab findings are reachable. Full catalog: [`SECURITY_TRAINING.md`](./SECURITY_TRAINING.md).

| Component | Default | Disable |
|-----------|---------|---------|
| Backend controllers | `SPRING_PROFILES_ACTIVE=training` | `SPRING_PROFILES_ACTIVE=default` |
| Backend SCA vuln jars | on default classpath (jackson pin stays `provided`) | remove `skybook.vuln.*` deps |
| AI controllers | `TRAINING_MODE=true` | `TRAINING_MODE=false` |
| AI SCA vuln pkgs | `pip install -r ai-service/requirements-training.txt` |

Catalog URLs (when enabled): `/api/v1/training/catalog` on backend (:8080) and AI (:8000).

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Backend cannot validate schema | Run `lab-reset.sh` or re-apply `schema.sql`/`seed.sql` |
| Frontend 502 on `/api` | Backend not healthy — check `docker compose logs backend` |
| AI chat FAQ-only forever | Expected without `OPENAI_API_KEY`; FAQ still works |
| `docker compose` not found | Install Docker Compose V2 plugin |
| npm 403 via Endor firewall | Use `npm install --registry https://registry.npmjs.org/` |

## Next

- API reference: [`API.md`](./API.md)  
- Architecture: [`Architecture.md`](./Architecture.md)  
- Database: [`DATABASE.md`](./DATABASE.md)
