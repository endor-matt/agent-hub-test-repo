from fastapi import APIRouter, HTTPException

from app.config import get_settings
from app.training import demos

router = APIRouter()
router.include_router(demos.router)


@router.get("/api/v1/training/ping")
def training_ping():
    """Always registered; returns 404 unless TRAINING_MODE=true (see demos.status)."""
    settings = get_settings()
    if not settings.training_mode:
        raise HTTPException(
            status_code=404,
            detail="Training mode is disabled. Set TRAINING_MODE=true (isolated lab only).",
        )
    return {"enabled": True, "catalog": "/api/v1/training/catalog"}
