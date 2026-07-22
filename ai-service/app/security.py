import logging
from typing import Annotated

import jwt
from fastapi import Depends, Header, HTTPException, status

from app.config import Settings, get_settings

logger = logging.getLogger(__name__)


def get_optional_user(
    authorization: Annotated[str | None, Header()] = None,
    settings: Settings = Depends(get_settings),
) -> dict | None:
    """Decode JWT if present; chat still works anonymously in lab (intent preserved)."""
    if not authorization or not authorization.lower().startswith("bearer "):
        return None
    token = authorization.split(" ", 1)[1].strip()
    try:
        claims = jwt.decode(
            token,
            settings.jwt_secret,
            algorithms=["HS256"],
            options={"require": ["sub", "exp"]},
        )
        return {
            "user_id": claims.get("sub"),
            "username": claims.get("username"),
            "role": claims.get("role"),
            "authorization": authorization,
        }
    except jwt.PyJWTError as exc:
        logger.info("Invalid JWT on AI request: %s", exc)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
        ) from exc


def require_user(user: Annotated[dict | None, Depends(get_optional_user)]) -> dict:
    if not user:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Authentication required")
    return user
