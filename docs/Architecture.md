# SkyBook AI — System Architecture

> **Environment notice:** SkyBook AI is a **security research and training** application.  
> It intentionally includes vulnerable code paths and outdated dependencies for educational SCA/SAST practice.  
> It is **not** production-secure and must never be deployed outside an isolated lab.

---

## 1. Overview

SkyBook AI is a realistic airline ticket booking SaaS composed of three application services plus MySQL:

| Service | Tech | Responsibility |
|---------|------|----------------|
| **frontend** | React + TypeScript + MUI + React Router + Axios | User-facing booking portal, admin UI, AI chat widget |
| **backend** | Java 21 + Spring Boot + Spring Security + JPA | Auth, flights, bookings, audit, admin APIs |
| **python-ai** | Python + FastAPI + LangChain (or LLM wrapper) | Conversational assistant, chat persistence proxy |
| **mysql** | MySQL 8 | Persistent data store |

**Product name:** SkyBook AI  
**Theme:** Modern airline booking portal  
**Auth:** JWT (access + refresh tokens), role-based access (`CUSTOMER`, `ADMIN`)

---

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Browser (User / Admin)                         │
└─────────────────────────────────┬───────────────────────────────────────┘
                                  │ HTTPS (dev: HTTP)
                                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     frontend :3000 (React SPA)                           │
│  Landing │ Search │ Booking │ History │ Profile │ Admin │ Audit │ Chat   │
└─────────────┬───────────────────────────────┬───────────────────────────┘
              │ REST + JWT                    │ REST
              ▼                               ▼
┌─────────────────────────────┐   ┌───────────────────────────────────────┐
│     backend :8080           │   │          python-ai :8000              │
│  Spring Boot Microservice   │   │  FastAPI + AI / LangChain             │
│  Auth | Flights | Bookings  │   │  Chat | FAQ | Policies | Suggest      │
│  Audit | Admin | Profile    │◄──┤  (may call backend for flight lookup) │
│  Training-mode vulns (off)  │   │  Training-mode vulns (off)            │
└──────────────┬──────────────┘   └──────────────────┬────────────────────┘
               │ JDBC                                │ HTTP (optional)
               ▼                                     │
┌─────────────────────────────┐                      │
│         mysql :3306         │◄─────────────────────┘
│  Users, Roles, Flights,     │   (ChatHistory may be written via backend
│  Bookings, Airlines,        │    or AI service with shared DB — see §5)
│  Airports, AuditLogs,       │
│  ChatHistory, RefreshTokens │
└─────────────────────────────┘
```

### Design principles

1. **Clear service boundaries** — UI, business API, and AI are independently deployable.
2. **Single source of truth for business data** — MySQL owned primarily by the Spring backend.
3. **JWT at the edge of backend** — Stateless API auth; refresh tokens stored server-side.
4. **Audit everything important** — Cross-cutting interceptor/aspect on the backend.
5. **Training mode isolation** — Vulnerable endpoints load under the `training` Spring profile / `TRAINING_MODE` (default: **enabled** for this lab; disable to harden the baseline).

---

## 3. Component Architecture

### 3.1 Frontend (`/frontend`)

```
frontend/
├── public/
├── src/
│   ├── api/              # Axios clients (auth, flights, bookings, audit, ai, admin)
│   ├── components/       # Navbar, Footer, ChatWidget, SeatMap, FlightCard, ...
│   ├── contexts/         # AuthContext
│   ├── hooks/
│   ├── layouts/          # MainLayout, AdminLayout
│   ├── pages/            # Landing, Search, Booking, Dashboard, Profile, ...
│   ├── routes/           # ProtectedRoute, AdminRoute
│   ├── theme/            # MUI airline theme
│   ├── types/
│   └── utils/
├── Dockerfile
└── package.json
```

**Pages (realistic SaaS UX)**

| Route | Page | Access |
|-------|------|--------|
| `/` | Landing | Public |
| `/search` | Flight Search + Filters | Public / Auth |
| `/booking/:flightId` | Passenger details + Seat selection | Customer |
| `/confirmation/:bookingId` | Booking confirmation | Customer |
| `/history` | Booking history + cancel | Customer |
| `/dashboard` | Customer dashboard | Customer |
| `/profile` | Profile / password | Customer, Admin |
| `/ai-chat` | Full-page AI assistant | Authenticated |
| `/admin` | Admin overview | Admin |
| `/admin/audit` | Audit dashboard (search/filter/export) | Admin |
| `/admin/flights` | Flight management (optional) | Admin |
| `/login`, `/register` | Auth | Public |

**Chat widget** — Floating component available on authenticated pages; also full `/ai-chat` page.

### 3.2 Backend (`/backend`)

```
backend/
├── src/main/java/com/skybook/
│   ├── SkyBookApplication.java
│   ├── config/           # Security, CORS, OpenAPI/Swagger, Audit
│   ├── security/         # JwtFilter, JwtService, UserDetails
│   ├── domain/           # Entities
│   ├── repository/       # Spring Data JPA
│   ├── dto/              # Request/Response DTOs
│   ├── service/          # Business logic
│   ├── controller/       # REST controllers
│   ├── audit/            # AuditAspect / Interceptor
│   └── training/         # Intentionally vulnerable demos (profile: training)
├── src/main/resources/
│   ├── application.yml
│   ├── application-training.yml   # Training profile (ON by default via spring.profiles.active)
│   └── db/migration/     # Optional Flyway (schema also in /database)
├── src/test/java/        # JUnit + Mockito
├── Dockerfile
└── pom.xml
```

**Layers**

```
Controller → Service → Repository → MySQL
                ↓
           AuditService (async/sync log writer)
```

### 3.3 AI Service (`/ai-service`)

```
ai-service/
├── app/
│   ├── main.py
│   ├── config.py
│   ├── routers/          # chat, health, training
│   ├── services/         # llm, faq, flight_assist
│   ├── models/
│   ├── db/               # Optional direct MySQL for ChatHistory
│   └── training/         # Intentionally vulnerable demos (flag-gated)
├── tests/                # Pytest
├── Dockerfile
└── requirements.txt
```

**Capabilities**

- Flight search assistance (proxies backend flight APIs when needed)
- Refund policies, baggage allowance, FAQs
- Booking help & travel suggestions
- Persist every conversation turn to `ChatHistory`

### 3.4 Database (`/database`)

- `schema.sql` — DDL for all tables
- `seed.sql` — Realistic airlines, airports, flights, users, sample bookings
- Delivered in Phase 2

---

## 4. Microservice Communication

| From → To | Protocol | Auth | Purpose |
|-----------|----------|------|---------|
| Frontend → Backend | REST JSON | Bearer JWT | All business operations |
| Frontend → AI | REST JSON | Bearer JWT (forwarded) or service API key | Chat queries |
| AI → Backend | REST JSON | Service token / user JWT | Flight lookup on behalf of user |
| Backend → MySQL | JDBC | DB credentials | Persistence |
| AI → MySQL | SQLAlchemy/async | DB credentials | ChatHistory (shared DB) |

**Network (Docker Compose)**

```
skybook-net
  ├── frontend   (expose 3000)
  ├── backend    (expose 8080)
  ├── python-ai  (expose 8000)
  └── mysql      (internal 3306; optional host expose for lab)
```

---

## 5. Data Ownership

| Entity | Owner service | Notes |
|--------|---------------|-------|
| Users, Roles, RefreshTokens | Backend | Auth source of truth |
| Airlines, Airports, Flights | Backend | Catalog |
| Bookings | Backend | Transactional booking flow |
| AuditLogs | Backend | Written by audit interceptor/aspect |
| ChatHistory | AI service (write) / Backend (read for admin) | Shared MySQL table |

---

## 6. Authentication & Authorization

### Flow

1. **Register** → create `User` + `CUSTOMER` role  
2. **Login** → validate credentials → issue **access JWT** + **refresh token**  
3. Access token in `Authorization: Bearer <jwt>`  
4. Refresh endpoint rotates refresh token (stored hashed in `RefreshTokens`)  
5. **Logout** → revoke refresh token + audit  

### Roles

| Role | Capabilities |
|------|----------------|
| `CUSTOMER` | Search, book, cancel own bookings, profile, AI chat, own history |
| `ADMIN` | All customer capabilities + audit dashboard, exports, user/flight admin APIs |

### JWT claims (proposed)

```json
{
  "sub": "user-uuid",
  "username": "jane.doe",
  "role": "CUSTOMER",
  "iat": 0,
  "exp": 0
}
```

Spring Security: method-level `@PreAuthorize` on admin and ownership checks on bookings.

---

## 7. Core Feature Architecture

### 7.1 Flight search & booking

```
Search(source, dest, date, airline, price range)
  → FlightService.query(...)
  → Book(flightId, passengers[], seats[])
  → validate seat availability (optimistic lock / status)
  → create Booking (CONFIRMED)
  → Audit: BOOKING_CREATED
  → Confirmation DTO
```

### 7.2 Ask AI Assistant

```
User message → ChatWidget
  → POST /ai/chat { message, sessionId }
  → AI RAG/FAQ + optional backend flight search
  → Persist ChatHistory (user + assistant turns)
  → Backend Audit: AI_QUERY (via AI callback or frontend/backend bridge)
```

### 7.3 Audit logging

Cross-cutting concern on backend for:

- Login / Logout  
- Booking created / cancelled  
- AI query  
- Profile update / password change  
- Export request  

**AuditLog fields**

| Field | Description |
|-------|-------------|
| auditId | UUID PK |
| timestamp | Event time (UTC) |
| username | Actor username |
| userId | Actor ID |
| role | CUSTOMER / ADMIN |
| ipAddress | Client IP |
| sessionId | Session / JWT `jti` correlation |
| action | e.g. `BOOKING_CREATED` |
| resource | e.g. `/api/bookings/123` |
| httpMethod | GET/POST/... |
| responseStatus | HTTP status |
| browser | Parsed User-Agent |
| operatingSystem | Parsed User-Agent |
| executionTimeMs | Handler duration |

### 7.4 Audit dashboard (Admin)

- Search & filter: date range, username, action  
- Export CSV / Excel  
- Monthly export  
- Retrieve previous/archived exports metadata  

---

## 8. API Surface (Contract Preview)

Base URLs (Docker):

- Backend: `http://localhost:8080/api/v1`  
- AI: `http://localhost:8000/api/v1`  

| Area | Examples |
|------|----------|
| Auth | `POST /auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout` |
| Flights | `GET /flights/search`, `GET /flights/{id}`, `GET /airlines`, `GET /airports` |
| Bookings | `POST /bookings`, `GET /bookings/me`, `GET /bookings/{id}`, `POST /bookings/{id}/cancel` |
| Profile | `GET/PUT /users/me`, `POST /users/me/password` |
| Audit | `GET /admin/audit`, `GET /admin/audit/export/csv`, `/export/excel`, `/export/monthly` |
| Admin | `GET /admin/users`, flight CRUD (as needed) |
| AI | `POST /chat`, `GET /chat/history/{sessionId}`, `GET /health` |
| Training | `/training/**` — **disabled unless `training` profile active** |

Full OpenAPI/Swagger is generated from Spring (`springdoc`) and FastAPI in later phases. Detailed API doc: `docs/API.md` (Phase 7).

---

## 9. Security Training Mode Architecture

### 9.1 Profiles & flags

| Component | Default (lab) | Disable |
|-----------|---------------|---------|
| Backend | `spring.profiles.active=training` | `SPRING_PROFILES_ACTIVE=default` |
| AI service | `TRAINING_MODE=true` | `TRAINING_MODE=false` |
| Vulnerable deps | On default classpath / lab image | Remove pins / skip `requirements-training.txt` |

Training endpoints:

- Clearly annotated `@Profile("training")` (Java) / feature flag (Python)
- Documented in `SECURITY_TRAINING.md` with CWE + OWASP mapping
- Each insecure example paired with a **secure** counterpart

### 9.2 Intentionally vulnerable dependency categories (SCA demos)

Isolated under training BOM / optional modules (easy to remove):

1. Vulnerable logging library  
2. Vulnerable JSON parsing library  
3. Vulnerable file upload library  
4. Vulnerable HTTP client  
5. Vulnerable serialization library  

All labeled **TRAINING ONLY** in `pom.xml` / `requirements-training.txt`.

### 9.3 Intentionally vulnerable code classes

**Java (training profile):** SQL Injection, Command Injection, Path Traversal, Insecure Deserialization, XXE, SSRF, Hardcoded Secrets, Weak Cryptography, Missing Authorization, Unsafe File Upload, XSS, CSRF  

**Python (training flag):** SQL Injection, Command Execution, Unsafe YAML, Pickle, Path Traversal, SSRF, Weak JWT, Arbitrary File Read, Unsafe subprocess, Missing Auth  

Details: Phase 8 + `SECURITY_TRAINING.md`.

---

## 10. Cross-Cutting Concerns

| Concern | Approach |
|---------|----------|
| CORS | Backend + AI allow frontend origin |
| Validation | Bean Validation / Pydantic |
| Errors | Problem Details / consistent JSON error body |
| Logging | Structured app logs ≠ audit logs |
| Time | UTC storage; display in local TZ on UI |
| IDs | UUID primary keys where practical |
| OpenAPI | springdoc-openapi + FastAPI auto schema |
| Tests | JUnit/Mockito, Pytest, React Testing Library |

---

## 11. Deployment Architecture (Docker Compose)

```
docker-compose.yml
  mysql
    └── healthcheck → backend depends_on healthy
  backend
    └── env: DB_*, JWT_*, AI_SERVICE_URL, PROFILE
  python-ai
    └── env: BACKEND_URL, DB_*, TRAINING_MODE, LLM_*
  frontend
    └── env: VITE_API_URL, VITE_AI_URL
```

Scripts under `/scripts` for bootstrap, seed wait, and lab reset.

---

## 12. Quality & Testing Strategy

| Layer | Framework | Focus |
|-------|-----------|-------|
| Backend | JUnit 5 + Mockito | Services, security filters, booking rules |
| AI | Pytest | Chat routes, FAQ matching, training flag on by default |
| Frontend | Vitest + React Testing Library | Auth flows, search filters, protected routes |

CI-friendly: unit tests run without external LLM (mocked).

---

## 13. Documentation Map

| Document | Phase | Purpose |
|----------|-------|---------|
| `docs/Architecture.md` | 1 / 7 | System design |
| `docs/DATABASE.md` | 2 / 7 | Schema & ER |
| `docs/API.md` | 7 | REST contracts |
| `docs/SECURITY_TRAINING.md` | 7 / 8 | Vuln catalog & lab rules |
| `docs/INSTALL.md` | 6 / 7 | Setup |
| `docs/diagrams/*` | 1 / 7 | Mermaid diagrams |
| `docs/README.md` | 7 | Docs index |
| `README.md` | 7 | Entry point |

---

## 14. Implementation Phases

| Phase | Scope | Status |
|-------|-------|--------|
| **1** | Architecture | **Confirmed** |
| **2** | Database (`schema.sql`, `seed.sql`) | **Confirmed** |
| **3** | Spring Boot backend | **Confirmed** |
| **4** | React frontend | **Confirmed** |
| **5** | Python AI service | **Confirmed** |
| **6** | Docker Compose | **Confirmed** |
| **7** | Documentation suite | **Confirmed** |
| **8** | Training-mode vulnerable examples | **Complete** |
| **3** | Spring Boot backend | Pending |
| **4** | React frontend | Pending |
| **5** | Python AI service | Pending |
| **6** | Docker Compose | Pending |
| **7** | Documentation suite | Pending |
| **8** | Training-mode vulnerable examples | Pending |

---

## 15. Non-Goals (this lab)

- Real payment gateway integration  
- Real GDS / airline inventory feeds  
- Production hardening / WAF / rate-limit SLAs as “secure by default” claims  
- Public internet exposure of training endpoints  

---

*SkyBook AI — Security Training Lab Architecture*  
*Phase 1 deliverable*
