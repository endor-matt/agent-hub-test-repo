"""Simple LLM wrapper with FAQ retrieval + optional LangChain runnable + OpenAI HTTP."""

from __future__ import annotations

import logging
import re
from dataclasses import dataclass

import httpx
from langchain_core.runnables import RunnableLambda

from app.config import Settings
from app.knowledge import faq

logger = logging.getLogger(__name__)


@dataclass
class AssistantResult:
    reply: str
    intent: str | None
    source: str


ROUTE_PATTERN = re.compile(
    r"\b([A-Z]{3})\s*(?:to|->|→)\s*([A-Z]{3})\b",
    re.IGNORECASE,
)


class AssistantService:
    def __init__(self, settings: Settings):
        self.settings = settings
        # LangChain runnable wrapper around FAQ composition (offline-friendly)
        self.faq_chain = RunnableLambda(self._compose_faq_reply)

    def detect_intent(self, message: str) -> str:
        hits = faq.retrieve(message, limit=1)
        if hits:
            return hits[0]["intent"]
        return "general"

    def extract_route(self, message: str) -> tuple[str, str] | None:
        match = ROUTE_PATTERN.search(message)
        if not match:
            return None
        return match.group(1).upper(), match.group(2).upper()

    def _compose_faq_reply(self, payload: dict) -> str:
        message = payload["message"]
        flight_context = payload.get("flight_context")
        hits = faq.retrieve(message)
        faq_block = "\n\n".join(f"**{h['title']}**: {h['content']}" for h in hits)
        intro = "Here is what I found for you:\n\n"
        extra = f"\n\nLive inventory tip:\n{flight_context}" if flight_context else ""
        return f"{intro}{faq_block}{extra}\n\n_(SkyBook AI lab assistant — FAQ mode)_".strip()

    async def reply(
        self,
        message: str,
        flight_context: str | None = None,
    ) -> AssistantResult:
        intent = self.detect_intent(message)
        if flight_context:
            intent = "flight_search"

        hits = faq.retrieve(message)
        faq_block = "\n\n".join(f"**{h['title']}**: {h['content']}" for h in hits)
        context_bits = [faq_block]
        if flight_context:
            context_bits.append(flight_context)

        if self.settings.openai_api_key:
            try:
                llm_reply = await self._llm_generate(message, "\n\n".join(context_bits))
                return AssistantResult(reply=llm_reply, intent=intent, source="llm")
            except Exception as exc:  # noqa: BLE001
                logger.warning("LLM call failed, using FAQ fallback: %s", exc)

        reply = await self.faq_chain.ainvoke(
            {"message": message, "flight_context": flight_context}
        )
        return AssistantResult(reply=reply, intent=intent, source="faq")

    async def _llm_generate(self, message: str, context: str) -> str:
        base = (self.settings.openai_base_url or "https://api.openai.com/v1").rstrip("/")
        url = f"{base}/chat/completions"
        headers = {
            "Authorization": f"Bearer {self.settings.openai_api_key}",
            "Content-Type": "application/json",
        }
        body = {
            "model": self.settings.openai_model,
            "temperature": 0.2,
            "messages": [
                {
                    "role": "system",
                    "content": (
                        "You are SkyBook AI, a helpful airline booking assistant in a security "
                        "training lab. Answer briefly using the provided context. Do not claim "
                        "to be production-secure. If unsure, suggest Search Flights or My Bookings.\n\n"
                        f"Context:\n{context}"
                    ),
                },
                {"role": "user", "content": message},
            ],
        }
        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.post(url, headers=headers, json=body)
            response.raise_for_status()
            data = response.json()
            return data["choices"][0]["message"]["content"]
