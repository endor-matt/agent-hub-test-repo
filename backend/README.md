# SkyBook AI Backend

Java 21 / Spring Boot API for the SkyBook AI **security training lab**.

> Not production-secure. See root README and Phase 8 training docs.

## Quick start

1. Apply `database/schema.sql` + `database/seed.sql`
2. Configure DB env vars (defaults: `skybook`/`skybook` @ localhost:3306)
3. `mvn spring-boot:run`
4. Open Swagger: http://localhost:8080/swagger-ui.html

## Lab users

| User | Password | Role |
|------|----------|------|
| admin | Admin@123 | ADMIN |
| jdoe | Customer@123 | CUSTOMER |

## Modules

- Auth JWT + refresh
- Flights / bookings
- Profile
- Admin audit dashboard APIs
- AI query audit bridge (`POST /api/v1/admin/audit/ai-query` — prefer customer-scoped bridge in Phase 5)

## Training profile

`SPRING_PROFILES_ACTIVE=training` reserved for Phase 8 vulnerable demos (currently no training controllers; `/api/v1/training/**` denied).
