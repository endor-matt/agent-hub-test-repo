# SkyBook AI — Phase 3 Backend Overview

> Security research & training lab. **Not production-secure.**  
> Training-mode vulnerable endpoints are deferred to **Phase 8** (denied by default).

## Location

`/backend` — Java 21 + Spring Boot 3.3 + Spring Security + JPA + Maven

## What was built

| Area | Details |
|------|---------|
| Auth | Register, login, refresh, logout; JWT access + hashed refresh tokens |
| Roles | `CUSTOMER`, `ADMIN` via Spring `@PreAuthorize` / `hasRole` |
| Flights | Search filters (source, destination, date, airline, min/max price), detail, airlines, airports |
| Bookings | Create (passengers + seats), history, get by id, cancel + seat restore |
| Profile | Get/update profile, change password |
| Audit | Writes on login/logout/register/booking/profile/password/export/AI bridge |
| Admin | Audit search/filter, CSV/Excel/monthly export, export history, user list |
| OpenAPI | Swagger UI at `/swagger-ui.html` |
| Tests | JUnit 5 + Mockito (`JwtService`, `BookingService`, `FlightService`, utils) |

## API base

`http://localhost:8080/api/v1`

Swagger: `http://localhost:8080/swagger-ui.html`

## Run (requires MySQL from Phase 2)

```bash
cd backend
mvn -s /tmp/skybook-maven-settings.xml spring-boot:run \
  -Dspring-boot.run.arguments="--DB_HOST=localhost --DB_USER=skybook --DB_PASSWORD=skybook"
```

Or set env: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

## Tests

```bash
cd backend
mvn -s <settings.xml> test
```

## Structure

```
backend/src/main/java/com/skybook/
  config/      Security, OpenAPI, properties
  security/    JWT filter + service
  domain/      JPA entities
  repository/  Spring Data
  dto/         Request/response models
  service/     Business logic + audit
  controller/  REST
  exception/   Problem Details handler
  util/
```

## Phase gate

**Phase 3 complete when you confirm the backend.**  
Next: **Phase 4 — React Frontend** (MUI booking UI + chat widget).
