# SkyBook AI — API Reference

> Base URLs (Docker lab):  
> **Backend** `http://localhost:8080/api/v1`  
> **AI** `http://localhost:8000/api/v1`  
> Live OpenAPI: Backend [Swagger UI](http://localhost:8080/swagger-ui.html) · AI [FastAPI docs](http://localhost:8000/docs)

Auth header: `Authorization: Bearer <accessToken>`

---

## 1. Authentication

### POST `/auth/register`

Register a new **CUSTOMER**.

```json
{
  "username": "newuser",
  "email": "new@example.com",
  "password": "Password123!",
  "firstName": "New",
  "lastName": "User",
  "phone": "+1-555-0199"
}
```

**201** → `AuthResponse`

### POST `/auth/login`

```json
{ "username": "jdoe", "password": "Customer@123" }
```

**200** →

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque>",
  "tokenType": "Bearer",
  "expiresInMinutes": 30,
  "user": {
    "id": "...",
    "username": "jdoe",
    "email": "jane.doe@example.com",
    "firstName": "Jane",
    "lastName": "Doe",
    "role": "CUSTOMER",
    "status": "ACTIVE"
  }
}
```

### POST `/auth/refresh`

```json
{ "refreshToken": "<opaque>" }
```

Rotates refresh token; returns new `AuthResponse`.

### POST `/auth/logout`

```json
{ "refreshToken": "<opaque>" }
```

Revokes refresh token; writes `USER_LOGOUT` audit.

---

## 2. Catalog & Flights

### GET `/airlines` · GET `/airports`

Public lists.

### GET `/flights/search`

| Query | Description |
|-------|-------------|
| `source` | IATA (e.g. `JFK`) |
| `destination` | IATA |
| `date` | `YYYY-MM-DD` (UTC day window) |
| `airline` | Airline code |
| `minPrice` / `maxPrice` | Decimal |

**200** → `FlightResponse[]`

### GET `/flights/{id}`

**200** → `FlightResponse`

---

## 3. Bookings

Requires authenticated user (CUSTOMER or ADMIN).

### POST `/bookings`

```json
{
  "flightId": "55555555-5555-5555-5555-555555555001",
  "passengers": [
    { "firstName": "Jane", "lastName": "Doe", "passport": "US998877" }
  ],
  "seats": ["12A"],
  "contactEmail": "jane.doe@example.com",
  "contactPhone": "+1-555-0101"
}
```

**201** → `BookingResponse` · Audit `BOOKING_CREATED`

### GET `/bookings/me`

Current user’s bookings.

### GET `/bookings/{id}`

Owner or ADMIN.

### POST `/bookings/{id}/cancel`

```json
{ "reason": "Change of plans" }
```

Restores seats · Audit `BOOKING_CANCELLED`

---

## 4. Profile

### GET `/users/me`

### PUT `/users/me`

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "phone": "+1-555-0101"
}
```

Audit `PROFILE_UPDATE`

### POST `/users/me/password`

```json
{
  "currentPassword": "Customer@123",
  "newPassword": "Customer@456"
}
```

Audit `PASSWORD_CHANGE`

---

## 5. Admin & Audit

Requires `ROLE_ADMIN`.

### GET `/admin/users`

### GET `/admin/audit`

| Query | Description |
|-------|-------------|
| `username` | Exact filter |
| `action` | e.g. `USER_LOGIN` |
| `dateFrom` / `dateTo` | `YYYY-MM-DD` |
| `page` / `size` | Pagination |

**200** → Spring Data page of `AuditLogResponse`

### GET `/admin/audit/export/csv` · `/export/excel`

Same filters; file download. Audit `EXPORT_REQUEST`.

### GET `/admin/audit/export/monthly?month=2026-06`

Previous calendar month by default.

### GET `/admin/audit/exports`

Prior export metadata rows.

### POST `/admin/audit/ai-query`

Bridge to record `AI_QUERY` (optional; used when AI notifies backend).

---

## 6. AI Service

### GET `/health`

```json
{
  "status": "ok",
  "training_mode": false,
  "notice": "Security research & training lab only..."
}
```

### POST `/chat`

JWT optional (recommended). Body:

```json
{
  "message": "What is the baggage allowance?",
  "session_id": "chat-uuid"
}
```

**200**

```json
{
  "reply": "...",
  "session_id": "chat-uuid",
  "intent": "baggage",
  "source": "faq"
}
```

Persists user + assistant turns to `chat_history`.

### GET `/chat/history/{sessionId}`

### GET `/chat/sessions`

Requires JWT — lists sessions for the current user.

---

## 7. Training mode (Phase 8)

| Service | Enable | Catalog |
|---------|--------|---------|
| Backend | Default `training` profile | `GET /api/v1/training/catalog` |
| AI | Default `TRAINING_MODE=true` | `GET /api/v1/training/catalog` |

Each demo exposes `/insecure/...` and `/secure/...` pairs with CWE + OWASP tags.  
See [`SECURITY_TRAINING.md`](./SECURITY_TRAINING.md). **No exploit PoCs are provided.**

---

## Audit action codes

`USER_LOGIN` · `USER_LOGOUT` · `USER_REGISTER` · `BOOKING_CREATED` · `BOOKING_CANCELLED` · `AI_QUERY` · `PROFILE_UPDATE` · `PASSWORD_CHANGE` · `EXPORT_REQUEST`

## Error shape

Spring Problem Details / JSON with `detail`, `timestamp`, and lab notice property.
