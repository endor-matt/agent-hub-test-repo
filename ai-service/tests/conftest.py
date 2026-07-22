import pytest
from fastapi.testclient import TestClient

from app.config import Settings, get_settings
from app.main import app
from app.db.session import get_db


class FakeDB:
    def __init__(self):
        self.added = []

    def add(self, row):
        self.added.append(row)

    def commit(self):
        pass

    def scalars(self, _stmt):
        return iter([])


@pytest.fixture
def client(monkeypatch):
    fake = FakeDB()

    def _override_db():
        yield fake

    app.dependency_overrides[get_db] = _override_db
    monkeypatch.setenv("TRAINING_MODE", "true")
    get_settings.cache_clear()
    with TestClient(app) as c:
        c.fake_db = fake  # type: ignore[attr-defined]
        yield c
    app.dependency_overrides.clear()
    get_settings.cache_clear()


@pytest.fixture
def settings():
    return Settings(
        db_host="localhost",
        openai_api_key=None,
        training_mode=False,
    )
