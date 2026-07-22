"""Bundled FAQ / policy knowledge for offline SkyBook AI assistant."""

KNOWLEDGE = [
    {
        "id": "baggage-domestic",
        "intent": "baggage",
        "tags": ["baggage", "luggage", "bag", "carry-on", "checked", "weight", "kg"],
        "title": "Baggage allowance",
        "content": (
            "Most SkyBook AI economy bookings include ~23 kg checked baggage on domestic routes "
            "and 23–30 kg on international routes depending on the airline. Carry-on is typically "
            "7–10 kg. Premium cabins often include 32–40 kg. Always confirm on your booking confirmation "
            "and the specific airline rules shown on the flight details page."
        ),
    },
    {
        "id": "refund-48h",
        "intent": "refund",
        "tags": ["refund", "cancel", "cancellation", "money back", "policy", "fee"],
        "title": "Refund policy",
        "content": (
            "Cancellations made at least 48 hours before departure are usually eligible for a partial "
            "refund minus a processing fee. Within 48 hours of departure, fares are typically "
            "non-refundable except where local regulations require otherwise. Flexible fares may allow "
            "free changes. Use My Bookings to cancel and review the fare rules for your ticket."
        ),
    },
    {
        "id": "booking-help",
        "intent": "booking_help",
        "tags": ["book", "booking", "seat", "passenger", "how to", "reserve", "ticket"],
        "title": "How to book",
        "content": (
            "1) Go to Search Flights and set source, destination, date, airline, and price filters. "
            "2) Choose a flight and open Book. 3) Enter passenger details and select seats on the seat map. "
            "4) Confirm — you will receive a booking reference such as SBK*****. "
            "You can view or cancel trips under My Bookings."
        ),
    },
    {
        "id": "flight-search-tips",
        "intent": "flight_search",
        "tags": ["search", "flight", "find", "route", "cheap", "price", "jfk", "lax", "airline"],
        "title": "Searching for flights",
        "content": (
            "Use the Search page filters: source (IATA), destination (IATA), travel date, airline code, "
            "and min/max price. Popular demo routes include JFK→LAX, SFO→SEA, and JFK→LHR. "
            "If you tell me a route and max price, I can suggest how to filter or look up live inventory "
            "when connected to the SkyBook backend."
        ),
    },
    {
        "id": "travel-suggestions",
        "intent": "travel_suggestions",
        "tags": ["suggest", "recommendation", "trip", "weekend", "vacation", "where", "travel"],
        "title": "Travel suggestions",
        "content": (
            "For a short getaway from New York, Miami (MIA) and Toronto (YYZ) are popular short-haul "
            "options. For longer trips, London (LHR), Paris (CDG), Dubai (DXB), and Singapore (SIN) "
            "are well-served demo destinations in this lab catalog. Filter by cabin class for Business "
            "or Premium Economy when available."
        ),
    },
    {
        "id": "faq-checkin",
        "intent": "faq",
        "tags": ["check-in", "checkin", "boarding", "gate", "id", "passport"],
        "title": "Check-in FAQ",
        "content": (
            "Online check-in typically opens 24–48 hours before departure (airline dependent). "
            "Bring a valid government ID for domestic travel and a passport for international flights. "
            "Arrive at least 2 hours early for domestic and 3 hours for international departures."
        ),
    },
    {
        "id": "faq-support",
        "intent": "faq",
        "tags": ["support", "help", "contact", "agent", "human"],
        "title": "Support",
        "content": (
            "In this training lab, Ask AI covers common FAQs. For booking issues, open My Bookings "
            "or update your Profile. Admin users can review Audit logs for operational events."
        ),
    },
]


def score_entry(message: str, entry: dict) -> int:
    text = message.lower()
    score = 0
    for tag in entry["tags"]:
        if tag in text:
            score += 2 if len(tag) > 3 else 1
    if entry["intent"].replace("_", " ") in text:
        score += 3
    return score


def retrieve(message: str, limit: int = 3) -> list[dict]:
    ranked = sorted(
        ((score_entry(message, e), e) for e in KNOWLEDGE),
        key=lambda x: x[0],
        reverse=True,
    )
    hits = [e for s, e in ranked if s > 0][:limit]
    if not hits:
        return [KNOWLEDGE[2], KNOWLEDGE[0]]  # booking help + baggage as gentle defaults
    return hits
