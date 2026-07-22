import logging
import re
import uuid
from collections import defaultdict

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.config import Settings
from app.db.models import ChatHistory
from app.models.schemas import ChatHistoryResponse, ChatResponse, ChatTurn, SessionSummary
from app.services.assistant import AssistantService
from app.services.flight_assist import FlightAssistClient

logger = logging.getLogger(__name__)

PRICE_PATTERN = re.compile(r"(?:under|below|max(?:imum)?|less than)\s*\$?\s*(\d+(?:\.\d+)?)", re.I)


class ChatService:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.assistant = AssistantService(settings)
        self.flights = FlightAssistClient(settings)

    async def chat(
        self,
        *,
        message: str,
        session_id: str,
        db: Session,
        user_id: str | None,
        authorization: str | None,
    ) -> ChatResponse:
        flight_context = await self._maybe_flight_context(message, authorization)

        result = await self.assistant.reply(message, flight_context=flight_context)

        self._save_turn(db, session_id, user_id, "user", message, result.intent, None)
        self._save_turn(
            db,
            session_id,
            user_id,
            "assistant",
            result.reply,
            result.intent,
            {"source": result.source},
        )
        db.commit()

        return ChatResponse(
            reply=result.reply,
            session_id=session_id,
            intent=result.intent,
            source=result.source,
        )

    async def _maybe_flight_context(self, message: str, authorization: str | None) -> str | None:
        route = self.assistant.extract_route(message)
        intent = self.assistant.detect_intent(message)
        wants_search = intent == "flight_search" or route is not None or "flight" in message.lower()
        if not wants_search:
            return None

        source = destination = None
        if route:
            source, destination = route

        max_price = None
        price_match = PRICE_PATTERN.search(message)
        if price_match:
            max_price = float(price_match.group(1))

        flights = await self.flights.search(source, destination, max_price, authorization)
        if not flights and not route:
            return None
        return self.flights.format_flights(flights)

    def history(self, db: Session, session_id: str, user_id: str | None) -> ChatHistoryResponse:
        stmt = (
            select(ChatHistory)
            .where(ChatHistory.session_id == session_id)
            .order_by(ChatHistory.created_at.asc())
        )
        rows = list(db.scalars(stmt))
        if user_id:
            rows = [r for r in rows if r.user_id is None or r.user_id == user_id]
        return ChatHistoryResponse(
            session_id=session_id,
            messages=[
                ChatTurn(role=r.role, content=r.content, intent=r.intent, created_at=r.created_at)
                for r in rows
            ],
        )

    def list_sessions(self, db: Session, user_id: str) -> list[SessionSummary]:
        stmt = (
            select(ChatHistory)
            .where(ChatHistory.user_id == user_id)
            .order_by(ChatHistory.created_at.desc())
        )
        rows = list(db.scalars(stmt))
        grouped: dict[str, list[ChatHistory]] = defaultdict(list)
        for row in rows:
            grouped[row.session_id].append(row)
        summaries: list[SessionSummary] = []
        for session_id, items in grouped.items():
            summaries.append(
                SessionSummary(
                    session_id=session_id,
                    message_count=len(items),
                    last_message_at=max(i.created_at for i in items if i.created_at),
                )
            )
        summaries.sort(key=lambda s: s.last_message_at or s.session_id, reverse=True)
        return summaries

    def _save_turn(
        self,
        db: Session,
        session_id: str,
        user_id: str | None,
        role: str,
        content: str,
        intent: str | None,
        metadata: dict | None,
    ) -> None:
        row = ChatHistory(
            id=str(uuid.uuid4()),
            session_id=session_id,
            user_id=user_id,
            role=role,
            content=content,
            intent=intent,
            metadata_json=metadata,
        )
        db.add(row)
