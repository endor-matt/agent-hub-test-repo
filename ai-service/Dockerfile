# SkyBook AI Service — multi-stage (lab only)
FROM python:3.12-slim AS base
WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
# Training sinks reachable by default in this lab image
ENV TRAINING_MODE=true
COPY requirements.txt requirements-training.txt ./
RUN pip install --no-cache-dir -r requirements.txt \
 && pip install --no-cache-dir -r requirements-training.txt
COPY app ./app
EXPOSE 8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
