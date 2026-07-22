from datetime import datetime
from typing import Any

from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    message: str = Field(min_length=1, max_length=4000)
    session_id: str = Field(min_length=3, max_length=64)


class ChatResponse(BaseModel):
    reply: str
    session_id: str
    intent: str | None = None
    source: str = "faq"
    lab_notice: str = "SkyBook AI training lab — not production-secure"


class ChatTurn(BaseModel):
    role: str
    content: str
    intent: str | None = None
    created_at: datetime | None = None


class ChatHistoryResponse(BaseModel):
    session_id: str
    messages: list[ChatTurn]


class SessionSummary(BaseModel):
    session_id: str
    message_count: int
    last_message_at: datetime | None = None


class HealthResponse(BaseModel):
    status: str
    service: str
    version: str
    training_mode: bool
    notice: str
    extras: dict[str, Any] = Field(default_factory=dict)
