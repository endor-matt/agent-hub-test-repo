# SkyBook AI — Phase 4 Frontend Overview

> Security research & training lab UI. **Not production-secure.**

## Stack

React 18 · TypeScript · Vite · React Router · MUI · Axios · Vitest + React Testing Library

## Pages

| Route | Page |
|-------|------|
| `/` | Landing (brand-forward hero) |
| `/search` | Flight search + filters |
| `/booking/:flightId` | Passengers + seat map |
| `/confirmation/:bookingId` | Booking confirmation |
| `/history` | Booking history + cancel |
| `/dashboard` | Customer dashboard |
| `/profile` | Profile + password |
| `/ai-chat` | Full-page AI chat |
| `/admin` | Admin overview |
| `/admin/audit` | Audit search/filter/export |
| `/login`, `/register` | Auth |

Also: sticky **Navbar**, **Footer**, floating **ChatWidget** (authenticated).

## Run

```bash
cd frontend
npm install --registry https://registry.npmjs.org/
npm run dev
```

App: http://localhost:3000  
Proxies `/api` → backend `:8080`, `/ai` → AI `:8000`.

## Tests

```bash
npm test
npm run build
```

## Theme

Navy / sky / teal aviation SaaS look · **Sora** + **Manrope** fonts · motion on landing hero.

## Phase gate

**Phase 4 complete when you confirm.**  
Next: **Phase 5 — Python AI Service**.
