def test_health(client):
    res = client.get("/api/v1/health")
    assert res.status_code == 200
    body = res.json()
    assert body["status"] == "ok"
    assert body["training_mode"] is True
    assert "training" in body["notice"].lower() or "lab" in body["notice"].lower()


def test_training_enabled_by_default(client):
    res = client.get("/api/v1/training/status")
    assert res.status_code == 200
    assert res.json()["enabled"] is True


def test_chat_persists_with_fake_db(client, monkeypatch):
    async def _no_flights(*_args, **_kwargs):
        return None

    monkeypatch.setattr(
        "app.services.chat_service.ChatService._maybe_flight_context",
        _no_flights,
    )

    res = client.post(
        "/api/v1/chat",
        json={"message": "What is your refund policy?", "session_id": "test-session-1"},
    )
    assert res.status_code == 200
    body = res.json()
    assert body["session_id"] == "test-session-1"
    assert body["intent"] == "refund"
    assert "refund" in body["reply"].lower()
    # user + assistant turns
    assert len(client.fake_db.added) == 2
