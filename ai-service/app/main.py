import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import get_settings
from app.routers import chat, training

logger = logging.getLogger(__name__)
settings = get_settings()


@asynccontextmanager
async def lifespan(_app: FastAPI):
    logger.info(
        "Starting %s v%s | training_mode=%s | notice=%s",
        settings.app_name,
        settings.app_version,
        settings.training_mode,
        settings.environment_notice,
    )
    yield


app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description=(
        "SkyBook AI conversational assistant for the security research & training lab. "
        "NOT production-secure. Conversations are stored in MySQL chat_history."
    ),
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origin_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router)
app.include_router(training.router)


@app.get("/")
def root():
    return {
        "service": settings.app_name,
        "docs": "/docs",
        "health": "/api/v1/health",
        "notice": settings.environment_notice,
    }
