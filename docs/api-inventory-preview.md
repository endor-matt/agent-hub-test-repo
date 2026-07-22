# SkyBook AI — API Inventory Preview (Phase 1)

> Contract preview only. Detailed request/response schemas land in `API.md` (Phase 7) and live OpenAPI from Spring + FastAPI.

**Base paths**

- Backend: `/api/v1`
- AI: `/api/v1`

## Authentication

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/register` | Public | Register customer |
| POST | `/auth/login` | Public | Issue JWT + refresh |
| POST | `/auth/refresh` | Refresh token | Rotate tokens |
| POST | `/auth/logout` | JWT | Revoke refresh + audit |

## Flights & Catalog

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/flights/search` | Public / JWT | Filters: source, destination, date, airline, min/max price |
| GET | `/flights/{id}` | Public / JWT | Flight detail + available seats |
| GET | `/airlines` | Public | Airline list |
| GET | `/airports` | Public | Airport list |

## Bookings

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/bookings` | CUSTOMER | Create booking (passengers + seats) |
| GET | `/bookings/me` | CUSTOMER | Booking history |
| GET | `/bookings/{id}` | CUSTOMER (owner) / ADMIN | Booking detail |
| POST | `/bookings/{id}/cancel` | CUSTOMER (owner) / ADMIN | Cancel booking |

## Profile

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/users/me` | JWT | Profile |
| PUT | `/users/me` | JWT | Update profile |
| POST | `/users/me/password` | JWT | Change password |

## Admin / Audit

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/admin/audit` | ADMIN | Search/filter audit logs |
| GET | `/admin/audit/export/csv` | ADMIN | Export CSV |
| GET | `/admin/audit/export/excel` | ADMIN | Export Excel |
| GET | `/admin/audit/export/monthly` | ADMIN | Monthly export |
| GET | `/admin/audit/exports` | ADMIN | Previous export metadata |
| GET | `/admin/users` | ADMIN | List users (admin) |

## AI Service

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/health` | Public | Liveness |
| POST | `/chat` | JWT | Send message; store conversation |
| GET | `/chat/history/{sessionId}` | JWT | Retrieve session history |
| GET | `/chat/sessions` | JWT | List user sessions |

## Training Mode (disabled by default)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| * | `/training/**` (backend) | Varies / often missing (intentional) | Vulnerable demos |
| * | `/training/**` (AI) | Varies | Vulnerable demos |

Each training route documents CWE, OWASP, insecure + secure variants (Phase 8).
