import pytest

from app.config import Settings
from app.knowledge import faq
from app.services.assistant import AssistantService


def test_faq_retrieve_baggage():
    hits = faq.retrieve("What is the baggage allowance on international flights?")
    assert hits
    assert hits[0]["intent"] == "baggage"


def test_faq_retrieve_refund():
    hits = faq.retrieve("Tell me about refund policy if I cancel")
    assert hits[0]["intent"] == "refund"


@pytest.mark.asyncio
async def test_assistant_faq_mode():
    service = AssistantService(Settings(openai_api_key=None))
    result = await service.reply("How do I book a ticket?")
    assert result.source == "faq"
    assert result.intent == "booking_help"
    assert "Search Flights" in result.reply or "book" in result.reply.lower()


def test_extract_route():
    service = AssistantService(Settings())
    assert service.extract_route("Find flights JFK to LAX under $300") == ("JFK", "LAX")
