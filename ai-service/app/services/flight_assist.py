import logging
from typing import Any

import httpx

from app.config import Settings

logger = logging.getLogger(__name__)


class FlightAssistClient:
    def __init__(self, settings: Settings):
        self.settings = settings

    async def search(
        self,
        source: str | None,
        destination: str | None,
        max_price: float | None = None,
        authorization: str | None = None,
    ) -> list[dict[str, Any]]:
        params: dict[str, Any] = {}
        if source:
            params["source"] = source
        if destination:
            params["destination"] = destination
        if max_price is not None:
            params["maxPrice"] = max_price

        headers = {}
        if authorization:
            headers["Authorization"] = authorization

        url = f"{self.settings.backend_url.rstrip('/')}/flights/search"
        try:
            async with httpx.AsyncClient(timeout=10.0) as client:
                response = await client.get(url, params=params, headers=headers)
                response.raise_for_status()
                data = response.json()
                return data if isinstance(data, list) else []
        except Exception as exc:  # noqa: BLE001
            logger.warning("Flight search assist failed: %s", exc)
            return []

    def format_flights(self, flights: list[dict[str, Any]], limit: int = 5) -> str:
        if not flights:
            return "No matching flights were returned from the booking API right now."
        lines = []
        for f in flights[:limit]:
            lines.append(
                f"- {f.get('airlineName')} {f.get('flightNumber')}: "
                f"{f.get('sourceIata')}→{f.get('destIata')} "
                f"for {f.get('currency', 'USD')} {f.get('basePrice')} "
                f"({f.get('availableSeats')} seats)"
            )
        return "\n".join(lines)
