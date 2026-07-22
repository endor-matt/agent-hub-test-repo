from typing import Annotated

from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session

from app.config import Settings, get_settings
from app.db.session import get_db
from app.models.schemas import (
    ChatHistoryResponse,
    ChatRequest,
    ChatResponse,
    HealthResponse,
    SessionSummary,
)
from app.security import get_optional_user, require_user
from app.services.chat_service import ChatService

router = APIRouter(prefix="/api/v1", tags=["AI"])


def get_chat_service(settings: Settings = Depends(get_settings)) -> ChatService:
    return ChatService(settings)


@router.get("/health", response_model=HealthResponse)
def health(settings: Settings = Depends(get_settings)) -> HealthResponse:
    return HealthResponse(
        status="ok",
        service=settings.app_name,
        version=settings.app_version,
        training_mode=settings.training_mode,
        notice=settings.environment_notice,
        extras={"llm_configured": bool(settings.openai_api_key)},
    )


@router.post("/chat", response_model=ChatResponse)
async def chat(
    body: ChatRequest,
    db: Annotated[Session, Depends(get_db)],
    user: Annotated[dict | None, Depends(get_optional_user)],
    service: Annotated[ChatService, Depends(get_chat_service)],
    authorization: Annotated[str | None, Header()] = None,
) -> ChatResponse:
    return await service.chat(
        message=body.message,
        session_id=body.session_id,
        db=db,
        user_id=user["user_id"] if user else None,
        authorization=authorization or (user.get("authorization") if user else None),
    )


@router.get("/chat/history/{session_id}", response_model=ChatHistoryResponse)
def chat_history(
    session_id: str,
    db: Annotated[Session, Depends(get_db)],
    user: Annotated[dict | None, Depends(get_optional_user)],
    service: Annotated[ChatService, Depends(get_chat_service)],
) -> ChatHistoryResponse:
    return service.history(db, session_id, user["user_id"] if user else None)


@router.get("/chat/sessions", response_model=list[SessionSummary])
def chat_sessions(
    db: Annotated[Session, Depends(get_db)],
    user: Annotated[dict, Depends(require_user)],
    service: Annotated[ChatService, Depends(get_chat_service)],
) -> list[SessionSummary]:
    return service.list_sessions(db, user["user_id"])
