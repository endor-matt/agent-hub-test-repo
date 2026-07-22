# SkyBook AI — Phase 8 Training Mode

## Delivered

| Item | Location |
|------|----------|
| Java insecure/secure demos | `backend/src/main/java/com/skybook/training/` |
| Java profile gate | `SPRING_PROFILES_ACTIVE=training` (**default**) |
| Java SCA deps | `backend/pom.xml` `skybook.vuln.*` on default classpath |
| Java SCA-only POM | `backend/training-sca-artifacts/pom.xml` |
| Python insecure/secure demos | `ai-service/app/training/demos.py` |
| Python flag | `TRAINING_MODE=true` (**default**) |
| Python SCA deps | `ai-service/requirements-training.txt` |
| Full guide | [`SECURITY_TRAINING.md`](./SECURITY_TRAINING.md) |

## Defaults (findings reachable)

```bash
# Already the lab defaults — no export required for a standard start
SPRING_PROFILES_ACTIVE=training
TRAINING_MODE=true
```

### Disable (safer baseline)

```bash
export SPRING_PROFILES_ACTIVE=default
export TRAINING_MODE=false
```

Catalog:

- http://localhost:8080/api/v1/training/catalog  
- http://localhost:8000/api/v1/training/catalog  

## Status

**Phase 8 complete — intentional findings reachable by default.**

All 8 phases delivered. Use docs index: [`README.md`](./README.md).
