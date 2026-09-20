# KO2Pharma — Backend API

REST API for the **KO2Pharma** pharmacy management system, built as the final project of the Higher Diploma in **Web Application Development (DAW)** at IES San Viator.

**Live demo:** [https://pharma.ko2-oreilly.com](https://pharma.ko2-oreilly.com)

---

## Full project

| Repo | Tech | Description |
|------|------|-------------|
| [farmacia-ko2-frontend](https://github.com/ko2javier/farmacia-ko2-frontend) | Angular 21 | Frontend SPA |
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
| Docker Compose | — | Runtime on a self-managed Hetzner VPS |
| GitHub Actions | — | CI/CD — a push to `master` deploys over SSH |

---

## Key features

- **JWT authentication** — stateless tokens carrying username and role (`SUPERADMIN / ADMIN / SELLER`)
- **Inventory management** — full CRUD with batch stock updates
- **Sales and cancellations** — sale registration (single or full ticket); cancelling moves the sale into a `CANCELLED` history table as one `@Transactional` unit
- **AEMPS integration** — real-time lookups against Spain's official medicine registry
- **AI-assisted validation** — calls a FastAPI microservice that uses semantic matching to suggest the 3 most relevant AEMPS catalog matches before adding an item to inventory
- **Activity log** — every write operation is traced with user, action, resource and IP
- **Swagger UI** — full interactive documentation with JWT support

---

## Architecture

```
Angular SPA  ·  pharma.ko2-oreilly.com
        │
        │ HTTPS + JWT
        ▼
┌─ Hetzner VPS · Docker network `farmacia-ko2-network` ──────────┐
│                                                                 │
│  fp_api  ·  Spring Boot :5000  ·  pharma-api.ko2-oreilly.com   │
│     │                                                           │
│     └──► fp_gpt  ·  FastAPI :8000  (GPT-4o-mini matching)      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
        │                        │
        ▼                        ▼
  MySQL (Aiven Cloud)      AEMPS / CIMA API
```

Spring Boot acts as the central gateway: it receives requests from the Angular SPA, queries the database, proxies calls to AEMPS, and orchestrates calls to the AI microservice.

The API and the AI microservice run as two containers on the same host, joined by an external Docker network. The API reaches the microservice by container name (`http://fp_gpt:8000`), so the AI service is never exposed to the internet — it has no published port.

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
| Method | Route | Description | Access |
|---|---|---|---|
| GET | `/users/all` | List all users | JWT |
| POST | `/users/insert` | Create user | JWT — an ADMIN may only create SELLER accounts |
| PUT | `/users/update` | Edit user | JWT — an ADMIN may not edit ADMIN or SUPERADMIN |
| DELETE | `/users/{id}` | Delete user | JWT — an ADMIN may not delete ADMIN or SUPERADMIN |

### Inventory — `/articulos`
| Method | Route | Description | Access |
|---|---|---|---|
| GET | `/articulos/All` | List full inventory | JWT |
| GET | `/articulos/search/{keyword}` | Search by name | JWT |
| POST | `/articulos/insert` | Insert item | JWT |
| PUT | `/articulos/updateItem` | Edit price/quantity | JWT |
| PUT | `/articulos/updateStock` | Bulk stock update | JWT |
| DELETE | `/articulos/{id}` | Delete item | JWT |
| PATCH | `/articulos/{id}/aemps` | Link to AEMPS record | JWT |

### Sales — `/ventas`
| Method | Route | Description | Access |
|---|---|---|---|
| GET | `/ventas/all` | Full sales history | JWT |
| GET | `/ventas/{username}` | Sales for a given user | JWT |
| POST | `/ventas/registrar` | Register a sale | JWT |
| POST | `/ventas/registrar/list` | Register a ticket (multiple lines) | JWT |
| DELETE | `/ventas/cancelar/{id}` | Cancel a sale (moves it to the cancellation history) | JWT |

### Cancellations — `/cancelaciones`
| Method | Route | Description | Access |
|---|---|---|---|
| GET | `/cancelaciones` | Cancellation history | JWT |

### Activity — `/activity-log`
| Method | Route | Description | Access |
|---|---|---|---|
| GET | `/activity-log/all` | Full audit log | `ROLE_SUPERADMIN` |

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
- **Roles:** `SUPERADMIN > ADMIN > SELLER`, stored in `users.permiso` and exposed to Spring Security as `ROLE_<permiso>`.
- `/auth/**` and `/swagger-ui/**` are public; everything else requires a valid token.
- All credentials are managed through environment variables — no secrets in code.

**Where authorization is actually enforced.** At the HTTP layer `SecurityConfig` restricts exactly one path by role — `/activity-log/**` requires `ROLE_SUPERADMIN`; every other route resolves to `.anyRequest().authenticated()`, so any valid token reaches it. The `/users` rules (an ADMIN cannot create anything above SELLER, nor edit or delete an ADMIN or SUPERADMIN) are enforced in `UserController` as explicit checks against the caller's authority, not by annotations.

This is a known limitation rather than a design choice: the project has no `@PreAuthorize` and no method security, so role separation on inventory and sales endpoints is currently enforced by the UI, not by the API. Closing it means enabling `@EnableMethodSecurity` and annotating the write endpoints.

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

Running on a **self-managed Hetzner VPS** under Docker Compose, at **`https://pharma-api.ko2-oreilly.com`**. The migration off Railway (via an AWS EC2 intermediate step) completed in August 2026.

**Pipeline** — `.github/workflows/deploy.yml`, on every push to `master`:

1. GitHub Actions opens an SSH session to the VPS (`appleboy/ssh-action`).
2. `git pull` in `~/apps/back`, cloning the repo there on the first run.
3. The `.env` is written from repository secrets — `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `IA_SERVICE_API_KEY`, `HOST_PORT`.
4. `docker compose up --build -d app` rebuilds and restarts the container.

Nothing is deployed by hand and no secret lives in the repository: the `.env` only ever exists on the server, regenerated on each deploy.

**Runtime layout**

| | |
|---|---|
| Container | `fp_api` (`restart: unless-stopped`) |
| Network | `farmacia-ko2-network` — external, shared with the `fp_gpt` AI container |
| Port | `${HOST_PORT:-5000}` → `5000`, configurable because the host runs other projects |
| Schema | `SPRING_JPA_HIBERNATE_DDL_AUTO=none` — Hibernate never alters the database |

The AI microservice is deployed the same way from its own repository into `~/apps/gpt-micro`, and joins the same Docker network. Neither container publishes the AI port: `fp_api` reaches it over the internal network as `http://fp_gpt:8000`.

---

## Author

**K. Jabier O'Reilly**
GitHub: [@ko2javier](https://github.com/ko2javier)
