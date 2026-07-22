# SkyBook AI — Architecture Diagrams

> Source Mermaid diagrams for Phase 1. Render in any Mermaid-compatible viewer (GitHub, VS Code, docs site).

---

## 1. System Context (C4 L1)

```mermaid
C4Context
    title SkyBook AI — System Context

    Person(customer, "Customer", "Books flights, uses AI assistant")
    Person(admin, "Admin", "Manages audit logs and exports")

    System(skybook, "SkyBook AI", "Airline booking portal + AI assistant (training lab)")

    System_Ext(llm, "LLM Provider", "Optional external LLM API")

    Rel(customer, skybook, "Books, chats, manages profile")
    Rel(admin, skybook, "Reviews audits, exports")
    Rel(skybook, llm, "Optional inference", "HTTPS")
```

---

## 2. Container Diagram (C4 L2)

```mermaid
C4Container
    title SkyBook AI — Containers

    Person(user, "User / Admin")

    Container_Boundary(skybook, "SkyBook AI") {
        Container(fe, "Frontend", "React, TypeScript, MUI", "SPA booking portal")
        Container(be, "Backend", "Java 21, Spring Boot", "Auth, flights, bookings, audit")
        Container(ai, "AI Service", "Python, FastAPI", "Chat assistant")
        ContainerDb(db, "MySQL", "MySQL 8", "Relational store")
    }

    Rel(user, fe, "Uses", "HTTPS")
    Rel(fe, be, "REST + JWT", "JSON")
    Rel(fe, ai, "Chat REST", "JSON")
    Rel(ai, be, "Flight assist", "REST")
    Rel(be, db, "JDBC")
    Rel(ai, db, "ChatHistory SQL")
```

---

## 3. Deployment (Docker Compose)

```mermaid
flowchart TB
    subgraph Host["Developer / Lab Host"]
        Browser["Browser"]
    end

    subgraph Compose["docker-compose network: skybook-net"]
        FE["frontend :3000"]
        BE["backend :8080"]
        AI["python-ai :8000"]
        DB[("mysql :3306")]
    end

    Browser --> FE
    FE -->|/api/v1| BE
    FE -->|/api/v1/chat| AI
    AI -->|flight lookup| BE
    BE --> DB
    AI --> DB
```

---

## 4. Sequence — Login

```mermaid
sequenceDiagram
    actor U as User
    participant FE as Frontend
    participant BE as Backend
    participant DB as MySQL

    U->>FE: Submit credentials
    FE->>BE: POST /api/v1/auth/login
    BE->>DB: Verify user + role
    BE->>DB: Store refresh token
    BE->>DB: Insert AuditLog LOGIN
    BE-->>FE: accessToken + refreshToken + user
    FE->>FE: Persist tokens (memory/localStorage lab)
    FE-->>U: Navigate to dashboard
```

---

## 5. Sequence — Search & Book Flight

```mermaid
sequenceDiagram
    actor U as Customer
    participant FE as Frontend
    participant BE as Backend
    participant DB as MySQL

    U->>FE: Filters (src, dest, date, airline, price)
    FE->>BE: GET /api/v1/flights/search?...
    BE->>DB: Query flights
    BE-->>FE: Flight list
    U->>FE: Select flight + passengers + seats
    FE->>BE: POST /api/v1/bookings (JWT)
    BE->>DB: Validate seats / inventory
    BE->>DB: Insert Booking
    BE->>DB: Audit BOOKING_CREATED
    BE-->>FE: Booking confirmation
    FE-->>U: Confirmation page
```

---

## 6. Sequence — AI Chat Query

```mermaid
sequenceDiagram
    actor U as User
    participant FE as Frontend
    participant AI as Python AI
    participant BE as Backend
    participant DB as MySQL

    U->>FE: Ask "baggage allowance for international?"
    FE->>AI: POST /api/v1/chat {message, sessionId}
    AI->>AI: FAQ / LLM reasoning
    opt Needs live flights
        AI->>BE: GET /api/v1/flights/search
        BE-->>AI: Flight results
    end
    AI->>DB: Insert ChatHistory turns
    AI->>BE: Notify AI_QUERY audit (or FE→BE)
    BE->>DB: Insert AuditLog AI_QUERY
    AI-->>FE: Assistant reply
    FE-->>U: Render message
```

---

## 7. Sequence — Audit Export (Admin)

```mermaid
sequenceDiagram
    actor A as Admin
    participant FE as Frontend
    participant BE as Backend
    participant DB as MySQL

    A->>FE: Filter + Export CSV/Excel
    FE->>BE: GET /api/v1/admin/audit/export/csv?...
    BE->>BE: Authorize ROLE_ADMIN
    BE->>DB: Query AuditLogs
    BE->>DB: Audit EXPORT_REQUEST
    BE-->>FE: File download
    FE-->>A: Save file
```

---

## 8. Domain ER (Logical) — Preview for Phase 2

```mermaid
erDiagram
    ROLES ||--o{ USERS : assigns
    USERS ||--o{ REFRESH_TOKENS : has
    USERS ||--o{ BOOKINGS : places
    USERS ||--o{ AUDIT_LOGS : generates
    USERS ||--o{ CHAT_HISTORY : converses

    AIRLINES ||--o{ FLIGHTS : operates
    AIRPORTS ||--o{ FLIGHTS : "source"
    AIRPORTS ||--o{ FLIGHTS : "destination"
    FLIGHTS ||--o{ BOOKINGS : includes

    ROLES {
        uuid id PK
        string name
    }
    USERS {
        uuid id PK
        string username
        string email
        string password_hash
        uuid role_id FK
    }
    REFRESH_TOKENS {
        uuid id PK
        uuid user_id FK
        string token_hash
        datetime expires_at
    }
    AIRLINES {
        uuid id PK
        string code
        string name
    }
    AIRPORTS {
        uuid id PK
        string iata
        string city
        string country
    }
    FLIGHTS {
        uuid id PK
        uuid airline_id FK
        uuid source_airport_id FK
        uuid dest_airport_id FK
        datetime departure
        decimal price
    }
    BOOKINGS {
        uuid id PK
        uuid user_id FK
        uuid flight_id FK
        string status
        string seats
    }
    AUDIT_LOGS {
        uuid id PK
        datetime timestamp
        string action
        string username
        int response_status
    }
    CHAT_HISTORY {
        uuid id PK
        uuid user_id FK
        string session_id
        string role
        text content
    }
```

---

## 9. AuthZ Decision Flow

```mermaid
flowchart TD
    Req[HTTP Request] --> Jwt{JWT valid?}
    Jwt -->|No| Unauth[401 Unauthorized]
    Jwt -->|Yes| Role{Required role?}
    Role -->|Admin API| AdminCheck{ROLE_ADMIN?}
    AdminCheck -->|No| Forbid[403 Forbidden]
    AdminCheck -->|Yes| Handler[Controller]
    Role -->|Customer resource| Own{Owns resource?}
    Own -->|No| Forbid
    Own -->|Yes| Handler
    Role -->|Public| Handler
    Handler --> Audit[Write AuditLog]
    Audit --> Resp[HTTP Response]
```

---

## 10. Training Mode Isolation

```mermaid
flowchart LR
    subgraph Default["Default profile (safe lab demo paths)"]
        A[Auth / Flights / Bookings / Audit / AI]
    end

    subgraph Training["training profile / TRAINING_MODE=true"]
        B[Vulnerable endpoints]
        C[Vulnerable optional deps]
        D[Secure counterparts for comparison]
    end

    Start[Container start] --> Flag{Training enabled?}
    Flag -->|No| Default
    Flag -->|Yes| Default
    Flag -->|Yes| Training
```
