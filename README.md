# TI ETL pipeline

## Overview
Provide two level support document presentation.

## Architecture
```text
PDF
│
│ PagePdfDocumentReader
▼
page Documents
│
│ LogicalSectionSplitter
├───────────────────────────────┐
▼                               ▼
question_generation_document   question_generation_section
│                               │
└──────────────┬────────────────┘
│
▼
TokenTextSplitter
│
▼
PGVector

```

### The ingestion flow
```text

pages
│
├─────────────────────────────┐
│                             │
▼                             ▼
LogicalSectionSplitter       TokenTextSplitter
│                             │
▼                             ▼
section table                PGVector

```
## Tech Stack

Built with 
- **Java 21**, 
- **Spring Boot 4**, 
- Spring Security OAuth2 Resource Server (JWT)
- **Spring AI 2.0.1**, 
- **RabbitMQ**
- Micrometer + Prometheus
- OpenTelemetry + Zipkin
- Springdoc OpenAPI

## Key Configuration

From `application.yml`:

- Server port: `8086` (default)
- OAuth issuer: `https://${OKTA_DOMAIN}/`

## Environment Variables (minimum)

- UPLOAD_STORAGE_PATH
- OKTA_DOMAIN
- RABBITMQ_HOST
- RABBITMQ_USER
- RABBITMQ_PASS
- MISTRAL_AI_API_KEY
- OPEN_AI_API_KEY
- DOCUMENT_DB_URL
- DOCUMENT_USER
- DOCUMENT_PASSWORD

Recommended:
- OPEN_AI_ENDPOINT
- CHAT_MODEL
- OPEN_AI_COMPLETIONS_PATH
- EMBEDDING_MODEL
- OPEN_AI_EMBEDDINGS_PATH

---
## Build & Run

### Prerequisites

- JDK 21
- Gradle (or use wrapper)
- PostgreSQL
- Redis
- Reachable AI endpoint (OpenAI-compatible)
- Document DB should be run (see https://github.com/MarinaPimenova/ti-document-db)


### Run locally

```bash
./gradlew clean build
./gradlew bootRun
```

---

## API Docs

After startup:

- Swagger UI: `http://localhost:8086/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8086/v3/api-docs`

---
