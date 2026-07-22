# SkyBook AI Service — Phase 5

> Security research & training lab. **Not production-secure.**  
> Conversations persist to MySQL `chat_history`. Training vulns arrive in Phase 8 (`TRAINING_MODE=true`).

## Stack

Python 3.12 · FastAPI · SQLAlchemy · httpx · langchain-core (FAQ runnable) · optional OpenAI-compatible LLM

## Capabilities

- Flight search assistance (proxies Spring backend)
- Refund policies, baggage, booking help, travel suggestions, FAQs
- Stores every user/assistant turn
- Offline FAQ mode without API keys
- Optional LLM when `OPENAI_API_KEY` is set

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Health |
| POST | `/api/v1/chat` | Send message `{message, session_id}` |
| GET | `/api/v1/chat/history/{sessionId}` | Session history |
| GET | `/api/v1/chat/sessions` | List sessions (JWT required) |
| GET | `/docs` | OpenAPI / Swagger |

## Run

```bash
cd ai-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

## Tests

```bash
pytest -q
```

## Phase gate

**Phase 5 complete when you confirm.**  
Next: **Phase 6 — Docker Compose**.
