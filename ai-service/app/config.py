from functools import lru_cache
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    app_name: str = "SkyBook AI Service"
    app_version: str = "1.0.0-TRAINING"
    environment_notice: str = (
        "Security research & training lab only. Not production-secure."
    )

    host: str = "0.0.0.0"
    port: int = 8000

    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "skybook"
    db_user: str = "skybook"
    db_password: str = "skybook"

    backend_url: str = "http://localhost:8080/api/v1"
    jwt_secret: str = "SkyBookLabOnlySecretKeyChangeMe_MustBeAtLeast256BitsLong!!"
    cors_origins: str = "http://localhost:3000,http://localhost:5173"

    # TRAINING_MODE: intentional demos reachable by default (set false to hide)
    training_mode: bool = True

    # Optional OpenAI-compatible LLM (FAQ fallback when unset)
    openai_api_key: str | None = None
    openai_model: str = "gpt-4o-mini"
    openai_base_url: str | None = None

    @property
    def database_url(self) -> str:
        return (
            f"mysql+pymysql://{self.db_user}:{self.db_password}"
            f"@{self.db_host}:{self.db_port}/{self.db_name}?charset=utf8mb4"
        )

    @property
    def cors_origin_list(self) -> list[str]:
        return [o.strip() for o in self.cors_origins.split(",") if o.strip()]


@lru_cache
def get_settings() -> Settings:
    return Settings()
