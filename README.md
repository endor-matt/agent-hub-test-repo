# SkyBook AI

> **Security research & training lab only — NOT production-secure.**  
> Training vulns: [`docs/SECURITY_TRAINING.md`](docs/SECURITY_TRAINING.md) (**enabled by default** for reachable lab findings).

Modern airline ticket booking portal: JWT auth, flight search & booking, Ask AI assistant, and admin audit dashboard.

## Stack

| Service | Technology | Port |
|---------|------------|------|
| Frontend | React, TypeScript, MUI, React Router, Axios | 3000 |
| Backend | Java 21, Spring Boot, Spring Security, JPA | 8080 |
| AI | Python, FastAPI, LangChain-core / LLM wrapper | 8000 |
| DB | MySQL 8 | 3306 |

## Quick start

```bash
cp docker/.env.example .env
./scripts/lab-up.sh
```

- **App:** http://localhost:3000  
- **Swagger:** http://localhost:8080/swagger-ui.html  
- **AI docs:** http://localhost:8000/docs  

| User | Password | Role |
|------|----------|------|
| `jdoe` | `Customer@123` | CUSTOMER |
| `admin` | `Admin@123` | ADMIN |

Full install: [`docs/INSTALL.md`](docs/INSTALL.md)

## Project layout

```
/frontend      React SPA
/backend       Spring Boot API (+ training/ demos)
/ai-service    FastAPI AI assistant (+ training demos)
/database      schema.sql, seed.sql
/docs          Architecture, API, security training, install
/docker        Compose helpers & .env.example
/scripts       lab-up / lab-down / lab-reset
```

## Documentation

| Doc | Link |
|-----|------|
| Index | [`docs/README.md`](docs/README.md) |
| Install | [`docs/INSTALL.md`](docs/INSTALL.md) |
| Architecture | [`docs/Architecture.md`](docs/Architecture.md) |
| API | [`docs/API.md`](docs/API.md) |
| Database | [`docs/DATABASE.md`](docs/DATABASE.md) |
| Security training | [`docs/SECURITY_TRAINING.md`](docs/SECURITY_TRAINING.md) |
| Vulnerable dep pins | [`docs/VULNERABLE_DEPENDENCY_PINS.md`](docs/VULNERABLE_DEPENDENCY_PINS.md) |
| Diagrams | [`docs/diagrams/architecture-diagrams.md`](docs/diagrams/architecture-diagrams.md) |

## Features

- Register / login / logout with JWT + refresh tokens · roles CUSTOMER & ADMIN  
- Flight search · seat selection · booking history · cancel  
- Ask AI (FAQ + optional LLM) · chat history persisted  
- Audit logging · admin CSV / Excel / monthly export  
- **Training mode** (on by default): intentional SAST/SCA demos with secure counterparts  

## Implementation status

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | Architecture | ✅ |
| 2 | Database | ✅ |
| 3 | Backend | ✅ |
| 4 | Frontend | ✅ |
| 5 | Python AI | ✅ |
| 6 | Docker | ✅ |
| 7 | Documentation | ✅ |
| 8 | Training-mode vulnerable examples | ✅ |

**All phases complete.**
