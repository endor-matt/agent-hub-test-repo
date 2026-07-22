# SkyBook AI — Architecture Tech Decisions (Phase 1)

## Stack locks

| Layer | Choice | Rationale |
|-------|--------|-----------|
| Frontend | React 18 + TypeScript + Vite | Modern SPA DX; easy Docker image |
| UI | Material UI (MUI) | Polished SaaS look for booking flows |
| Routing | React Router v6 | Standard SPA routing + protected routes |
| HTTP | Axios | Interceptors for JWT attach/refresh |
| Backend | Java 21 + Spring Boot 3.x | Security, JPA, mature JWT patterns |
| Security | Spring Security + JWT | Role-based access for CUSTOMER/ADMIN |
| Persistence | Spring Data JPA + MySQL 8 | Matches required RDBMS |
| API docs | springdoc-openapi | Swagger UI out of the box |
| AI | FastAPI + LangChain (or thin OpenAI-compatible wrapper) | Lightweight chat microservice |
| LLM | Configurable; mock/fallback without API key | Lab runs offline-friendly |
| Compose | Docker Compose | One-command multi-service lab |

## Auth decisions

- **Access token:** short-lived JWT (e.g. 15–30 min)
- **Refresh token:** opaque, stored hashed in `RefreshTokens`, rotatable
- **Password hashing:** BCrypt (secure path); weak crypto only under training profile
- **Roles table:** many users → one role for simplicity (`CUSTOMER` | `ADMIN`); extensible later

## Booking decisions

- Seats stored as comma-separated or JSON column on booking for Phase 2 simplicity
- Seat availability checked in service layer; optimistic locking optional on flight remaining seats
- Status enum: `CONFIRMED`, `CANCELLED`

## Audit decisions

- Synchronous write in request path for training visibility (async optional later)
- User-Agent parsing for browser/OS fields
- IP from `X-Forwarded-For` when present (documented spoof risk in lab notes)

## AI decisions

- Every user/assistant turn persisted to `ChatHistory`
- FAQ/policy knowledge base bundled as local documents (no mandatory cloud)
- Optional live flight search via backend HTTP client

## Training isolation decisions

- Backend: Spring profile `training`
- AI: env `TRAINING_MODE=true`
- Vulnerable dependencies: optional Maven profile / `requirements-training.txt`
- Default compose profile does **not** enable training endpoints

## Out of scope for “secure product” claims

Payment PCI, real inventory, production secrets management, WAF — this remains a **training lab**.
