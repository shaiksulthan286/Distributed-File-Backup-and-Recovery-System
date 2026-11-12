# Distributed File Backup and Recovery System (DFBRS)

## Overview
A full‑stack, enterprise‑grade backup and recovery platform that demonstrates
- chunked file uploads
- content‑addressable storage with deduplication
- versioned backups
- integrity verification
- a modern React dashboard

The project is organized as a mono‑repo with two sub‑modules:`

* `backend/` – Spring Boot 3.x (Java 21) REST API, PostgreSQL, JWT security
* `frontend/` – Vite + React + TypeScript UI with Tailwind CSS and Recharts

Both modules can be started locally via Docker Compose (see `docker-compose.yml`).

---

**⚡ Quick Start**
```bash
# from the repository root
docker compose up --build   # builds backend, frontend and PostgreSQL
```
The API will be available at `http://localhost:8080/api/` and the UI at
`http://localhost:3000`.

---

## Repository Structure
```
distributed-file-backup-recovery/
├─ backend/                # Spring Boot application
│  ├─ src/main/java/com/dfbrs/
│  │  ├─ config/          # Spring configuration (security, storage, etc.)
│  │  ├─ controller/      # REST controllers
│  │  ├─ dto/             # Data transfer objects
│  │  ├─ entity/          # JPA entities
│  │  ├─ repository/      # Spring Data repositories
│  │  ├─ service/         # Business logic
│  │  ├─ storage/         # Chunk storage engine
│  │  └─ util/            # Helper utilities
│  ├─ src/main/resources/
│  │  ├─ application.yml  # Application configuration
│  │  └─ db/migration/    # Flyway migrations
│  └─ pom.xml
├─ frontend/               # React + Vite UI
│  ├─ src/
│  │  ├─ components/      # Reusable UI components
│  │  ├─ pages/           # Route pages
│  │  ├─ services/        # API client wrappers (Axios)
│  │  ├─ hooks/           # Custom React hooks
│  │  ├─ utils/           # Generic utilities
│  │  └─ types/           # TypeScript type definitions
│  ├─ vite.config.ts
│  ├─ package.json
│  └─ tailwind.config.ts
├─ docker-compose.yml      # Services orchestration
├─ .gitignore
└─ README.md               # This document
```

---

## Development
* **Backend (local, no Docker)** –
  1. Install and start PostgreSQL locally.
  2. Create the database and user:
     ```sql
     CREATE DATABASE dfbrs;
     CREATE USER dfbrs_user WITH PASSWORD 'dfbrs_pass';
     GRANT ALL PRIVILEGES ON DATABASE dfbrs TO dfbrs_user;
     ```
  3. Run:
     ```bash
     cd backend
     set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
     set PATH=%JAVA_HOME%\bin;%PATH%
     mvn spring-boot:run
     ```
* **Frontend** – `cd frontend && npm install && npm run dev`
* **Database migrations** – Flyway runs automatically on startup.

---

## Next Steps (Implemented)
- Project scaffolding with Maven and Vite configs
- Basic Spring Boot entry point
- Placeholder `application.yml`
- README and `.gitignore`

Further work will introduce the storage engine, security, entities, services, and UI.
