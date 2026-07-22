# SkyBook AI — Python AI Service

FastAPI assistant for the SkyBook security training lab.

## Quick start

```bash
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

- Health: http://localhost:8000/api/v1/health  
- Docs: http://localhost:8000/docs  

## Env

See `.env.example`. Shared JWT secret must match the Spring backend for authenticated chat history scoping.
