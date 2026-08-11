# KO2Pharma — Backend API

REST API for the **KO2Pharma** pharmacy management system, built as the final project of the Higher Diploma in **Web Application Development (DAW)** at IES San Viator.

**Live demo:** [https://pharma.ko2-oreilly.com](https://pharma.ko2-oreilly.com)

---

## Full project

| Repo | Tech | Description |
|------|------|-------------|
| [farmacia-ko2-frontend](https://github.com/ko2javier/farmacia-ko2-frontend) | Angular 19 | Frontend SPA |
| **This repo** | Spring Boot + Java 21 | Backend REST API |
| [farmacia-ko2-ia](https://github.com/ko2javier/farmacia-ko2-ia) | Python + FastAPI | AI microservice for semantic validation |

---

## Tech stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Main language |
| Spring Boot | 3.4.2 | Base framework |
| Spring Security | 3.4.2 | Authentication and authorization |
| JWT (jjwt) | 0.11.5 | Stateless tokens |
| Spring Data JPA | 3.4.2 | ORM / data access |
| MySQL (Aiven Cloud) | — | Production database |
| SpringDoc OpenAPI | 2.8.6 | Swagger UI documentation |
| Docker Compose + GitHub Actions | — | Containerized deployment with CI/CD |

---

## Key features

- **JWT authentication** — stateless, role-based (`SUPERADMIN / ADMIN / SELLER`)
- **Inventory management** — full CRUD with stock control
- **Sales and cancellations** — sale registration, cancellation with automatic stock restoration (`@Transactional`)
- **AEMPS integration** — real-time lookups against Spain's official medicine registry
- **AI-assisted validation** — calls a FastAPI microservice that uses semantic matching to suggest the 3 most relevant AEMPS catalog matches before adding an item to inventory
- **Activity log** — every write operation is traced with user, action, resource and IP
- **Swagger UI** — full interactive documentation with JWT support

---

## Architecture

```
Angular 19 (Frontend)
        │
        │ HTTP + JWT
        ▼
Spring Boot API (port 5000)              ← central gateway
        │
        ├──► MySQL on Aiven Cloud          (persistence)
        ├──► AEMPS API                     (medicine catalog)
        └──► FastAPI AI microservice       (semantic matching with GPT-4o-mini)
```

Spring Boot acts as the central gateway: it receives requests from the Angular SPA, queries the database, proxies calls to AEMPS, and orchestrates calls to the AI microservice.

---

## Project structure

```
src/main/java/com/FP_Final/FP/
├── config/
│   ├── SecurityConfig.java              # CORS, JWT filter, access rules, BCrypt
│   ├── JwtUtil.java                     # Token generation and validation
│   ├── JwtAuthenticationFilter.java     # Per-request JWT filter
│   └── SwaggerConfig.java              # OpenAPI configuration
├── controller/
│   ├── AuthController.java              # POST /auth/login
│   ├── UserController.java              # User CRUD
│   ├── ArticuloController.java          # Inventory CRUD
│   ├── VentaController.java             # Sales registration and lookup
│   ├── VentaCanceladaController.java    # Cancellation history
│   ├── ActivityLogController.java       # Audit log
│   └── CimaController.java             # AEMPS proxy + AI validation
├── service/
│   ├── IaService.java                   # Client for the FastAPI AI microservice
│   ├── ActivityLogService.java          # Operation traceability
│   └── ...
├── repository/
├── model/                               # JPA entities + DTOs
└── exception/
    └── GlobalExceptionHandler.java      # Centralized @RestControllerAdvice
```

---

## Endpoints

### Authentication — `/auth`
| Method | Route | Description | Auth |
|---|---|---|---|
| POST | `/auth/login` | Login with username/password → returns JWT | No |

### Users — `/users`
| Method | Route | Description | Min. role |
|---|---|---|---|
| GET | `/users/all` | List all users | ADMIN |
| POST | `/users/insert` | Create user | ADMIN |
| PUT | `/users/update` | Edit user | ADMIN |
| DELETE | `/users/{id}` | Delete user | ADMIN |

### Inventory — `/articulos`
| Method | Route | Description | Min. role |
|---|---|---|---|
| GET | `/articulos/All` | List full inventory | SELLER |
| GET | `/articulos/search/{keyword}` | Search by name | SELLER |
| POST | `/articulos/insert` | Insert item | ADMIN |
| PUT | `/articulos/updateItem` | Edit price/quantity | ADMIN |
| PUT | `/articulos/updateStock` | Bulk stock update | SELLER |
| DELETE | `/articulos/{id}` | Delete item | ADMIN |
| PATCH | `/articulos/{id}/aemps` | Link to AEMPS record | ADMIN |

### Sales — `/ventas`
| Method | Route | Description | Min. role |
|---|---|---|---|
| GET | `/ventas/all` | Full sales history | ADMIN |
| GET | `/ventas/{username}` | Sales for a given user | SELLER |
| POST | `/ventas/registrar` | Register a sale | SELLER |
| POST | `/ventas/registrar/list` | Register a ticket (multiple lines) | SELLER |
| DELETE | `/ventas/cancelar/{id}` | Cancel a sale and restore stock | ADMIN |

### Cancellations — `/cancelaciones`
| Method | Route | Description | Min. role |
|---|---|---|---|
| GET | `/cancelaciones` | Cancellation history | ADMIN |

### Activity — `/activity-log`
| Method | Route | Description | Min. role |
|---|---|---|---|
| GET | `/activity-log/all` | Full audit log | SUPERADMIN |

### AEMPS / AI — `/api/cima`
| Method | Route | Description |
|---|---|---|
| GET | `/api/cima/buscar` | Search medicines in AEMPS |
| GET | `/api/cima/medicamento` | Medicine detail by registration number |
| GET | `/api/cima/validar` | Validate a name against AEMPS + AI microservice |

Full interactive documentation at `/swagger-ui/index.html`.

---

## Security

- **Stateless JWT** — tokens signed with HMAC-SHA256, 10-hour validity, carry username and role.
- **BCrypt** — passwords hashed with 10 rounds.
- **CORS** — allowed origins explicitly configured in `SecurityConfig`.
- **Roles:** `SUPERADMIN > ADMIN > SELLER`
- `/auth/**` and `/swagger-ui/**` are public; everything else requires a valid token.
- All credentials are managed through environment variables — no secrets in code.

---

## Running locally

### Requirements
- Java 21
- Maven 3.x
- Local MySQL or access to Aiven Cloud

```bash
# 1. Clone the repo
git clone https://github.com/ko2javier/farmacia-ko2-back.git
cd farmacia-ko2-back/FP

# 2. Configure environment variables
cp .env.example .env   # fill in your own values

# 3. Run
./mvnw spring-boot:run
```

With Docker:
```bash
docker compose up --build
```

The API will be available at `http://localhost:5000`.
Swagger UI: `http://localhost:5000/swagger-ui/index.html`

---

## Environment variables

| Variable | Description |
|---|---|
| `PORT` | Server port (defaults to 5000) |
| `DB_URL` | MySQL JDBC URL (Aiven or local) |
| `DB_USER` | Database user |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | HMAC-SHA256 key (minimum 64 characters) |
| `IA_SERVICE_URL` | Base URL of the FastAPI AI microservice |
| `IA_SERVICE_API_KEY` | Shared key with the AI microservice |

---

## Production deployment

Deployed on an AWS EC2 instance (Ubuntu + Docker Compose), alongside the AI microservice on the same host, communicating over an internal Docker network. A GitHub Actions CI/CD pipeline builds and redeploys the service on every push to `master`.

---

## Author

**K. Jabier O'Reilly**
GitHub: [@ko2javier](https://github.com/ko2javier)
