# KO2Pharma — Backend API

REST API for a pharmacy management system built as a final project for a Higher Vocational Degree in Web Application Development (DAW).

**Live demo:** [https://pharma.ko2-oreilly.com](https://pharma.ko2-oreilly.com)

---

## Part of the KO2Pharma project

| Repo | Tech | Description |
|------|------|-------------|
| [farmacia-ko2-frontend](https://github.com/ko2javier/farmacia-ko2-frontend) | Angular 19 | SPA frontend |
| **This repo** | Spring Boot + Java 21 | REST API backend |
| [farmacia-ko2-ia](https://github.com/ko2javier/farmacia-ko2-ia) | Python + FastAPI | AI semantic matching microservice |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.2 |
| Security | Spring Security + JWT (jjwt) |
| Database | MySQL (Aiven cloud) |
| AI Integration | Python/FastAPI microservice (Railway) |
| External API | AEMPS — Spanish Medicines Agency |
| Deployment | Railway (CI/CD from GitHub) |
| Docs | Swagger UI / SpringDoc OpenAPI |

---

## Features

- **JWT Authentication** — stateless, role-based (`ROLE_ADMIN` / `ROLE_SELLER`)
- **Inventory management** — CRUD with optimistic locking on stock updates
- **Sales & cancellations** — register sales, cancel with automatic stock restore
- **AEMPS integration** — real-time search against the official Spanish drug registry
- **AI-assisted drug validation** — calls a FastAPI microservice that uses semantic matching to suggest the 3 most relevant drugs from the AEMPS catalogue before adding to inventory
- **Activity log** — every write operation is traced with username, action, resource and IP
- **Swagger UI** — full interactive API docs with JWT auth support

---

## Architecture

```
Angular Frontend
      │
      ▼
Spring Boot API (Railway, port 5000)
      ├── MySQL Aiven Cloud  (persistence)
      ├── AEMPS REST API     (drug catalogue)
      └── FastAPI IA Service (Railway) ← semantic AI matching
```

Spring Boot acts as the central gateway: it receives requests from the Angular SPA, queries the database, proxies AEMPS, and orchestrates calls to the AI microservice.

---

## Project Structure

```
src/main/java/com/FP_Final/FP/
├── config/
│   ├── SecurityConfig.java          # CORS, JWT filter, authorization rules
│   ├── JwtUtil.java                 # Token generation & validation
│   └── JwtAuthenticationFilter.java
├── controller/
│   ├── AuthController.java          # POST /auth/login
│   ├── ArticuloController.java      # CRUD /articulos
│   ├── CimaController.java          # AEMPS proxy + AI validation
│   ├── VentaController.java         # Sales
│   ├── VentaCanceladaController.java
│   ├── UserController.java
│   └── ActivityLogController.java
├── service/
│   ├── IaService.java               # FastAPI IA microservice client
│   ├── ActivityLogService.java      # Audit trail
│   └── ...
└── model/                           # Entities + DTOs
```

---

## API Endpoints

| Method | Route | Auth | Description |
|--------|-------|------|-------------|
| POST | `/auth/login` | Public | Get JWT token |
| GET | `/articulos/All` | JWT | List all products |
| GET | `/articulos/search/{keyword}` | JWT | Search by name |
| POST | `/articulos/insert` | JWT | Add product |
| PUT | `/articulos/updateStock` | JWT | Batch stock update |
| DELETE | `/articulos/{id}` | JWT | Delete product |
| GET | `/api/cima/buscar` | JWT | Search AEMPS drug catalogue |
| GET | `/api/cima/validar` | JWT | Validate + AI match before adding |
| POST | `/ventas/registrar` | JWT | Register a sale |
| DELETE | `/ventas/cancelar/{id}` | JWT | Cancel sale & restore stock |
| GET | `/activity-log` | JWT (ADMIN) | Audit log |

Full interactive docs available at `/swagger-ui/index.html` on the live instance.

---

## Security Design

- Passwords hashed with BCrypt via `JdbcUserDetailsManager`
- JWT signed with HMAC-SHA256, secret injected via environment variable
- All credentials managed through environment variables — no hardcoded secrets
- CORS restricted to known frontend origins

---

## Local Setup

```bash
# 1. Clone the repo
git clone https://github.com/ko2javier/farmacia-ko2-back.git
cd farmacia-ko2-back/FP

# 2. Create .env with your own values
cp .env.example .env   # fill in DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET, IA_SERVICE_URL, IA_SERVICE_API_KEY

# 3. Run
./mvnw spring-boot:run
```

Or with Docker:
```bash
docker compose up --build
```

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL for MySQL (Aiven or local) |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HMAC-SHA256 key (min 64 chars) |
| `IA_SERVICE_URL` | Base URL of the FastAPI AI microservice |
| `IA_SERVICE_API_KEY` | Shared secret for the AI microservice |

---

## Author

**Javier** — DAW student, San Viator  
GitHub: [@ko2javier](https://github.com/ko2javier)
