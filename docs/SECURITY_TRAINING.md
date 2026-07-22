# SkyBook AI — Security Training Guide

> **CRITICAL:** SkyBook AI is a **security research and training** application.  
> Run only in an **isolated lab**. It is **NOT production-secure**.

---

## 1. Purpose

Practice SCA / SAST education with:

- Intentionally outdated libraries (public CVEs)  
- Intentionally vulnerable endpoints **reachable by default** in this lab  
- Side-by-side **insecure vs secure** counterparts  
- **No exploit PoCs** are shipped — use scanners and code review, not weaponized payloads  

---

## 2. Training mode (default ON — findings reachable)

| Layer | Default (lab) | Disable |
|-------|---------------|---------|
| Backend training controllers | **On** (`SPRING_PROFILES_ACTIVE=training`) | `SPRING_PROFILES_ACTIVE=default` |
| SCA vulnerable Java deps | **On classpath** (compile/runtime; jackson pin remains `provided`) | Remove `skybook.vuln.*` deps from `pom.xml` |
| AI training routes | **On** (`TRAINING_MODE=true`) | `TRAINING_MODE=false` |
| Python SCA vuln deps | Pinned in `requirements-training.txt` | Skip that file / use only `requirements.txt` |

Docker / local defaults:

```bash
SPRING_PROFILES_ACTIVE=training
TRAINING_MODE=true
```

Catalog endpoints (no extra flags required):

- Java: `GET /api/v1/training/catalog`  
- Python: `GET /api/v1/training/catalog`  

---

## 3. Intentionally vulnerable dependencies (SCA)

### Java (`backend/pom.xml` + `backend/training-sca-artifacts/pom.xml`)

Exact pins (see also [`VULNERABLE_DEPENDENCY_PINS.md`](./VULNERABLE_DEPENDENCY_PINS.md)):

| Category | Artifact | **Pinned** version | Example public issue |
|----------|----------|--------------------|----------------------|
| Logging | `org.apache.logging.log4j:log4j-core` | **2.14.1** | CVE-2021-44228 |
| JSON | `com.fasterxml.jackson.core:jackson-databind` | **2.9.10** | Multiple CVEs (e.g. CVE-2019-16335) |
| File upload | `commons-fileupload:commons-fileupload` | **1.3.1** | CVE-2016-1000031 |
| HTTP client | `org.apache.httpcomponents:httpclient` | **4.3.6** | Outdated / advisory history |
| Serialization | `commons-collections:commons-collections` | **3.2.1** | Deserialization gadget history |

- Default lab build places log4j-core, commons-fileupload, httpclient, and commons-collections on the **runtime** classpath.  
- `jackson-databind` **2.9.10** stays `provided` so Spring Boot 3’s managed Jackson is not overridden.  
- Properties: `skybook.vuln.*` in `backend/pom.xml`.  

### Python (`ai-service/requirements-training.txt`) — all `==` pins

| Category | Package | **Pinned** version |
|----------|---------|--------------------|
| YAML | PyYAML | **5.1** |
| HTTP | urllib3 | **1.24.2** |
| HTTP | requests | **2.19.0** |
| Imaging | Pillow | **8.3.0** |
| Crypto | pycrypto | **2.6.1** |
| JWT | pyjwt | **1.7.1** |

Install for SCA package detection: `pip install -r requirements-training.txt` (lab image / local). Code sinks under `/api/v1/training/**` work whenever `TRAINING_MODE=true`.

---

## 4. Intentionally vulnerable code (Java)

All under `com.skybook.training.*`, profile `training` (active by default).

| Demo | CWE | OWASP | Insecure | Secure |
|------|-----|-------|----------|--------|
| SQL Injection | CWE-89 | A03 | `/insecure/sql` | `/secure/sql` |
| Command Injection | CWE-78 | A03 | `/insecure/command` | `/secure/command` |
| Path Traversal | CWE-22 | A01 | `/insecure/path` | `/secure/path` |
| Log4j user-controlled log | CWE-117 | A06 | `/insecure/log4j` | `/secure/log4j` |
| Insecure Deserialization | CWE-502 | A08 | `/insecure/deserialize` | `/secure/deserialize` |
| XXE | CWE-611 | A05 | `/insecure/xxe` | `/secure/xxe` |
| SSRF | CWE-918 | A10 | `/insecure/ssrf` | `/secure/ssrf` |
| Hardcoded Secrets | CWE-798 | A07 | `/insecure/secrets` | `/secure/secrets` |
| Weak Cryptography | CWE-328 | A02 | `/insecure/crypto` | `/secure/crypto` |
| Missing Authorization | CWE-862 | A01 | `/insecure/users` | `/secure/users` |
| Unsafe File Upload | CWE-434 | A04 | `/insecure/upload` | `/secure/upload` |
| XSS | CWE-79 | A03 | `/insecure/xss` | `/secure/xss` |
| CSRF | CWE-352 | A01 | `/insecure/csrf/transfer` | `/secure/csrf/transfer` |

Prefix: `/api/v1/training`

---

## 5. Intentionally vulnerable code (Python)

File: `ai-service/app/training/demos.py` — `TRAINING_MODE=true` by default.

| Demo | CWE | OWASP | Insecure | Secure |
|------|-----|-------|----------|--------|
| SQL Injection | CWE-89 | A03 | `/insecure/sql` | `/secure/sql` |
| Command Execution | CWE-78 | A03 | `/insecure/command` | `/secure/command` |
| Unsafe YAML | CWE-502 | A08 | `/insecure/yaml` | `/secure/yaml` |
| Pickle | CWE-502 | A08 | `/insecure/pickle` | `/secure/pickle` |
| Path Traversal | CWE-22 | A01 | `/insecure/path` | `/secure/path` |
| SSRF | CWE-918 | A10 | `/insecure/ssrf` | `/secure/ssrf` |
| Weak JWT | CWE-347 | A07 | `/insecure/jwt` | `/secure/jwt` |
| Arbitrary File Read | CWE-73 | A01 | `/insecure/read` | `/secure/read` |
| Unsafe subprocess | CWE-78 | A03 | `/insecure/subprocess` | `/secure/subprocess` |
| Missing Authentication | CWE-306 | A07 | `/insecure/admin-stats` | `/secure/admin-stats` |

Prefix: `/api/v1/training`

---

## 6. Lab safety rules

1. Isolated network / disposable VM only.  
2. Disable training (`SPRING_PROFILES_ACTIVE=default`, `TRAINING_MODE=false`) if you need a less hazardous baseline.  
3. Prefer `./scripts/lab-reset.sh` between labs.  
4. Do not ship or run exploit payloads against non-lab systems.  
5. Mark SCA/SAST findings under training sinks as **expected lab findings**.  

---

## 7. Related docs

- [`INSTALL.md`](./INSTALL.md) — enable/disable flags  
- [`API.md`](./API.md) — training route overview  
- [`Architecture.md`](./Architecture.md) — isolation design  
- [`PHASE8.md`](./PHASE8.md) — delivery notes  

---

*SkyBook AI Security Training — Phase 8 (default-reachable findings).*
