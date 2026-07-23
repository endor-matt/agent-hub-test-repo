"""
TRAINING ONLY — intentionally vulnerable FastAPI demos.
Enabled exclusively when TRAINING_MODE=true.
Each insecure handler is paired with a secure counterpart.
Do NOT deploy outside an isolated lab. No exploit payloads are provided.
"""

from __future__ import annotations

import base64
import hashlib
import os
import pickle
import sqlite3
import subprocess
from pathlib import Path
from typing import Any
from urllib.parse import urlparse
from urllib.request import urlopen

import jwt
import yaml
from fastapi import APIRouter, Depends, Header, HTTPException, UploadFile
from fastapi.responses import HTMLResponse, PlainTextResponse
from pydantic import BaseModel, Field

from app.config import Settings, get_settings

router = APIRouter(prefix="/api/v1/training", tags=["Training (INSECURE LAB)"])

NOTICE = "TRAINING ONLY — intentionally vulnerable demo. Not for production."
BASE_DIR = Path(__file__).resolve().parent / "sample_data"
BASE_DIR.mkdir(parents=True, exist_ok=True)
(BASE_DIR / "readme.txt").write_text("SkyBook AI training sample file\n", encoding="utf-8")


def require_training(settings: Settings = Depends(get_settings)) -> Settings:
    if not settings.training_mode:
        raise HTTPException(
            status_code=404,
            detail="Training mode disabled. Set TRAINING_MODE=true in an isolated lab only.",
        )
    return settings


class Chatty(BaseModel):
    message: str = Field(default="")


@router.get("/status")
def training_status(settings: Settings = Depends(require_training)):
    return {
        "enabled": True,
        "notice": NOTICE,
        "catalog": "/api/v1/training/catalog",
    }


@router.get("/catalog")
def catalog(_: Settings = Depends(require_training)):
    return {
        "notice": NOTICE,
        "demos": [
            {"id": "sql-injection", "cwe": "CWE-89", "owasp": "A03:2021", "insecure": "/insecure/sql", "secure": "/secure/sql"},
            {"id": "command-execution", "cwe": "CWE-78", "owasp": "A03:2021", "insecure": "/insecure/command", "secure": "/secure/command"},
            {"id": "unsafe-yaml", "cwe": "CWE-502", "owasp": "A08:2021", "insecure": "/insecure/yaml", "secure": "/secure/yaml"},
            {"id": "pickle", "cwe": "CWE-502", "owasp": "A08:2021", "insecure": "/insecure/pickle", "secure": "/secure/pickle"},
            {"id": "path-traversal", "cwe": "CWE-22", "owasp": "A01:2021", "insecure": "/insecure/path", "secure": "/secure/path"},
            {"id": "ssrf", "cwe": "CWE-918", "owasp": "A10:2021", "insecure": "/insecure/ssrf", "secure": "/secure/ssrf"},
            {"id": "weak-jwt", "cwe": "CWE-347", "owasp": "A07:2021", "insecure": "/insecure/jwt", "secure": "/secure/jwt"},
            {"id": "arbitrary-file-read", "cwe": "CWE-73", "owasp": "A01:2021", "insecure": "/insecure/read", "secure": "/secure/read"},
            {"id": "unsafe-subprocess", "cwe": "CWE-78", "owasp": "A03:2021", "insecure": "/insecure/subprocess", "secure": "/secure/subprocess"},
            {"id": "missing-auth", "cwe": "CWE-306", "owasp": "A07:2021", "insecure": "/insecure/admin-stats", "secure": "/secure/admin-stats"},
        ],
    }


# --- SQL Injection ---
@router.get("/insecure/sql")
def insecure_sql(username: str, _: Settings = Depends(require_training)):
    """CWE-89 — string-built SQL (uses ephemeral sqlite, not production DB)."""
    db_path = BASE_DIR / "training.db"
    conn = sqlite3.connect(db_path)
    try:
        conn.execute("CREATE TABLE IF NOT EXISTS demo_users (username TEXT, email TEXT)")
        conn.execute("DELETE FROM demo_users")
        conn.execute("INSERT INTO demo_users VALUES ('jdoe','jane@example.com'), ('admin','admin@skybook.lab')")
        conn.commit()
        # INTENTIONALLY VULNERABLE
        query = f"SELECT username, email FROM demo_users WHERE username = '{username}'"
        rows = list(conn.execute(query))
        return {"notice": NOTICE, "cwe": "CWE-89", "query": query, "rows": rows}
    finally:
        conn.close()


@router.get("/secure/sql")
def secure_sql(username: str, _: Settings = Depends(require_training)):
    db_path = BASE_DIR / "training.db"
    conn = sqlite3.connect(db_path)
    try:
        conn.execute("CREATE TABLE IF NOT EXISTS demo_users (username TEXT, email TEXT)")
        rows = list(conn.execute("SELECT username, email FROM demo_users WHERE username = ?", (username,)))
        return {"rows": rows, "status": "parameterized"}
    finally:
        conn.close()


# --- Command / subprocess ---
@router.get("/insecure/command", response_class=PlainTextResponse)
def insecure_command(host: str, _: Settings = Depends(require_training)):
    """CWE-78 — shell command with user input."""
    # INTENTIONALLY VULNERABLE
    return subprocess.getoutput(f"ping -c 1 {host}")


@router.get("/secure/command", response_class=PlainTextResponse)
def secure_command(host: str, _: Settings = Depends(require_training)):
    allowed = {"127.0.0.1", "localhost"}
    if host not in allowed:
        return f"Rejected. Allowlist: {sorted(allowed)}"
    completed = subprocess.run(  # noqa: S603 — argv list, no shell
        ["ping", "-c", "1", host],
        capture_output=True,
        text=True,
        check=False,
    )
    return completed.stdout or completed.stderr


@router.get("/insecure/subprocess", response_class=PlainTextResponse)
def insecure_subprocess(cmd: str, _: Settings = Depends(require_training)):
    """CWE-78 — shell=True."""
    # INTENTIONALLY VULNERABLE
    return subprocess.check_output(cmd, shell=True, text=True)  # noqa: S602


@router.get("/secure/subprocess", response_class=PlainTextResponse)
def secure_subprocess(_: Settings = Depends(require_training)):
    completed = subprocess.run(["uname", "-s"], capture_output=True, text=True, check=False)
    return completed.stdout.strip()


# --- YAML / Pickle ---
@router.post("/insecure/yaml")
def insecure_yaml(payload: Chatty, _: Settings = Depends(require_training)):
    """FIXED: safe_load only, no arbitrary object construction (was CWE-502)."""
    # Remediated (AI SAST CWE-502): yaml.safe_load rejects Python object tags,
    # matching /secure/yaml.
    data = yaml.safe_load(payload.message)
    return {"notice": NOTICE, "cwe": "CWE-502 mitigated", "parsed": str(data)}


@router.post("/secure/yaml")
def secure_yaml(payload: Chatty, _: Settings = Depends(require_training)):
    data = yaml.safe_load(payload.message)
    return {"parsed": data, "status": "safe_load"}


@router.post("/insecure/pickle")
async def insecure_pickle(file: UploadFile, _: Settings = Depends(require_training)):
    """CWE-502 — pickle.loads on uploaded bytes."""
    raw = await file.read()
    # INTENTIONALLY VULNERABLE
    obj = pickle.loads(raw)  # noqa: S301
    return {"notice": NOTICE, "cwe": "CWE-502", "type": type(obj).__name__}


@router.post("/secure/pickle")
def secure_pickle(payload: dict[str, Any], _: Settings = Depends(require_training)):
    return {"echo": payload, "status": "json-only-no-pickle"}


# --- Path / file read ---
@router.get("/insecure/path", response_class=PlainTextResponse)
def insecure_path(name: str, _: Settings = Depends(require_training)):
    """CWE-22 — unsanitized path join."""
    # INTENTIONALLY VULNERABLE
    return (BASE_DIR / name).read_text(encoding="utf-8", errors="replace")


@router.get("/secure/path", response_class=PlainTextResponse)
def secure_path(name: str, _: Settings = Depends(require_training)):
    target = (BASE_DIR / name).resolve()
    if not str(target).startswith(str(BASE_DIR.resolve())):
        raise HTTPException(status_code=400, detail="Path escapes base directory")
    if not target.exists():
        raise HTTPException(status_code=404, detail="Not found")
    return target.read_text(encoding="utf-8")


@router.get("/insecure/read", response_class=PlainTextResponse)
def insecure_read(path: str, _: Settings = Depends(require_training)):
    """CWE-73 — arbitrary file read."""
    # INTENTIONALLY VULNERABLE
    return Path(path).read_text(encoding="utf-8", errors="replace")


@router.get("/secure/read", response_class=PlainTextResponse)
def secure_read(name: str, _: Settings = Depends(require_training)):
    return secure_path(name)


# --- SSRF ---
@router.get("/insecure/ssrf", response_class=PlainTextResponse)
def insecure_ssrf(url: str, _: Settings = Depends(require_training)):
    """CWE-918 — unrestricted URL fetch."""
    # INTENTIONALLY VULNERABLE
    with urlopen(url, timeout=3) as resp:  # noqa: S310
        return resp.read(2048).decode("utf-8", errors="replace")


@router.get("/secure/ssrf")
def secure_ssrf(url: str, _: Settings = Depends(require_training)):
    allowed_hosts = {"example.com", "httpbin.org"}
    parsed = urlparse(url)
    if parsed.scheme not in {"http", "https"} or parsed.hostname not in allowed_hosts:
        raise HTTPException(status_code=400, detail=f"Host not allowlisted: {allowed_hosts}")
    with urlopen(url, timeout=3) as resp:  # noqa: S310 — after allowlist
        preview = resp.read(2048).decode("utf-8", errors="replace")
    return {"status": "ok", "preview": preview}


# --- JWT ---
@router.get("/insecure/jwt")
def insecure_jwt(token: str, _: Settings = Depends(require_training)):
    """CWE-347 — accepts alg=none / skips signature verification."""
    # INTENTIONALLY VULNERABLE
    claims = jwt.decode(token, options={"verify_signature": False})
    return {"notice": NOTICE, "cwe": "CWE-347", "claims": claims}


@router.get("/secure/jwt")
def secure_jwt(token: str, settings: Settings = Depends(require_training)):
    claims = jwt.decode(token, settings.jwt_secret, algorithms=["HS256"])
    return {"claims": claims, "status": "verified-hs256"}


# --- Missing auth ---
@router.get("/insecure/admin-stats")
def insecure_admin_stats(_: Settings = Depends(require_training)):
    """CWE-306 — sensitive stats with no authentication."""
    return {"notice": NOTICE, "cwe": "CWE-306", "totalBookings": 42, "revenueUsd": 99999}


@router.get("/secure/admin-stats")
def secure_admin_stats(
    settings: Settings = Depends(require_training),
    authorization: str | None = Header(default=None),
):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Authentication required")
    token = authorization.split(" ", 1)[1]
    claims = jwt.decode(token, settings.jwt_secret, algorithms=["HS256"])
    if claims.get("role") != "ADMIN":
        raise HTTPException(status_code=403, detail="Admin role required")
    return {"totalBookings": 42, "revenueUsd": 99999, "status": "authorized"}


@router.get("/insecure/xss", response_class=HTMLResponse)
def insecure_xss(q: str, _: Settings = Depends(require_training)):
    """Bonus HTML reflection for XSS education (CWE-79)."""
    return f"<html><body>Query: {q}</body></html>"


@router.get("/secure/xss", response_class=HTMLResponse)
def secure_xss(q: str, _: Settings = Depends(require_training)):
    safe = (
        q.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
    )
    return f"<html><body>Query: {safe}</body></html>"


@router.get("/insecure/weak-hash")
def insecure_weak_hash(password: str, _: Settings = Depends(require_training)):
    return {"md5": hashlib.md5(password.encode()).hexdigest(), "cwe": "CWE-328"}  # noqa: S324


@router.get("/helper/b64-pickle-info")
def helper_info(_: Settings = Depends(require_training)):
    """Educational note only — does not generate attack payloads."""
    return {
        "notice": NOTICE,
        "info": "Use a benign pickled object you create yourself in a sandbox; do not download untrusted pickles.",
        "secure_alternative": "Prefer JSON DTOs (see /secure/pickle).",
        "example_benign_b64_length": len(base64.b64encode(pickle.dumps({"lab": True}))),
    }
