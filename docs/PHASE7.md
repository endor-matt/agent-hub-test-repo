# SkyBook AI — Phase 7 Documentation

## Delivered

| Required doc | Path |
|--------------|------|
| README | [`../README.md`](../README.md) |
| Architecture | [`Architecture.md`](./Architecture.md) |
| API | [`API.md`](./API.md) |
| DATABASE | [`DATABASE.md`](./DATABASE.md) |
| SECURITY_TRAINING | [`SECURITY_TRAINING.md`](./SECURITY_TRAINING.md) |
| INSTALL | [`INSTALL.md`](./INSTALL.md) |
| Docs index | [`README.md`](./README.md) |

## Bonus diagrams

All Mermaid sources: [`diagrams/architecture-diagrams.md`](./diagrams/architecture-diagrams.md)

| Diagram | Content |
|---------|---------|
| Architecture | C4 context + containers + Docker deploy |
| Sequence | Login, book, AI chat, audit export |
| ER | Logical data model |
| AuthZ / Training | Decision & isolation flows |

## Live OpenAPI / Swagger

| Service | URL |
|---------|-----|
| Spring (springdoc) | http://localhost:8080/swagger-ui.html · `/v3/api-docs` |
| FastAPI | http://localhost:8000/docs · `/openapi.json` |

## Phase gate

**Phase 7 complete when you confirm.**  
Next: **Phase 8 — Training-mode vulnerable examples** (Java + Python demos, SCA vuln deps, update `SECURITY_TRAINING.md`).
