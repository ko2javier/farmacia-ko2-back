# MEMORY.md — KO2Pharma / EasyFarma Backend (fp-api)

## Perfil del alumno
- Estudiante FP DAW, nivel intermedio Angular/Bootstrap/Spring Boot/MySQL
- Proyecto académico con entrega junio 2026
- El alumno partió de un proyecto base existente al que añadió mejoras significativas
- El profesor pidió documentar el delta (qué había antes vs qué añadió él)

## Stack backend
- Spring Boot 3.4.2 + Java 21
- Spring Security + JWT (jjwt 0.11.5)
- MySQL en Aiven (cloud) — dos bases de datos:
  - `fp_dam` → producción (application.properties)
  - `easyfarma_dev` → desarrollo (application-dev.properties) ✅ ya creado
- Desplegado en Railway via Docker CLI (sin Git — `docker push` manual)
- Puerto: 5000 (variable de entorno PORT)

## Estructura del proyecto
```
fp-api-dev/FP/
├── src/main/java/com/FP_Final/FP/
│   ├── config/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtUtil.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── ArticuloController.java   → /articulos
│   │   ├── AuthController.java       → /auth
│   │   ├── CimaController.java       → proxy AEMPS
│   │   ├── UserController.java       → /users
│   │   ├── VentaCanceladaController.java
│   │   └── VentaController.java      → /ventas
│   ├── model/
│   │   ├── Articulos.java            → tabla articulos
│   │   ├── Users.java / Authorities.java
│   │   ├── Ventas.java               → tabla ventas (desnormalizada)
│   │   ├── VentaCancelada.java       → tabla ventas_canceladas
│   │   ├── VentaDTO.java / Insert_DTO.java / Insert_User_DTO.java / UpdateDTO.java
│   └── service/ repository/          → capas estándar
```

## Modelo de datos actual (producción)

### `articulos`
| campo     | tipo         | notas                  |
|-----------|--------------|------------------------|
| id        | INT PK AI    |                        |
| nombre    | VARCHAR(100) |                        |
| categoria | VARCHAR(50)  |                        |
| precio    | DOUBLE       |                        |
| cantidad  | INT          | stock                  |
| codigo    | VARCHAR(13)  | UNIQUE, código barras  |

### `ventas` (desnormalizada — diseño original del proyecto base)
| campo        | tipo         | notas                    |
|--------------|--------------|--------------------------|
| id           | INT PK AI    |                          |
| fecha        | DATE         |                          |
| hora         | TIME         |                          |
| id_username  | VARCHAR      | username del vendedor    |
| dnicliente   | VARCHAR(20)  |                          |
| nameproducto | VARCHAR(100) | nombre copiado en venta  |
| cantidad     | INT          |                          |
| importe      | DOUBLE       |                          |
| codigo       | VARCHAR(13)  | código del artículo      |

### `ventas_canceladas`
| campo           | tipo          | notas                |
|-----------------|---------------|----------------------|
| id              | BIGINT PK AI  |                      |
| nombre_producto | VARCHAR       |                      |
| cantidad        | INT           |                      |
| importe         | DOUBLE        |                      |
| responsable     | VARCHAR       | quien canceló        |
| fecha           | DATETIME      | auto al crear        |

### `users` / `authorities`
- Autenticación delegada a JdbcUserDetailsManager
- Roles: ROLE_ADMIN / ROLE_SELLER (columna `permiso` en users, prefijo ROLE_ se añade en query)

## Endpoints actuales

| Método | Ruta                        | Descripción                          |
|--------|-----------------------------|--------------------------------------|
| GET    | /articulos/All              | Todos los artículos                  |
| GET    | /articulos/search/{keyword} | Búsqueda por keyword                 |
| POST   | /articulos/insert           | Crear artículo (Insert_DTO)          |
| PUT    | /articulos/updateStock      | Actualizar stock batch (List<UpdateDTO>) |
| PUT    | /articulos/updateItem       | Actualizar precio/cantidad           |
| DELETE | /articulos/{id}             | Eliminar artículo                    |
| POST   | /auth/**                    | Login (público, sin JWT)             |
| GET    | /ventas/all                 | Todas las ventas                     |
| GET    | /ventas/{username}          | Ventas por usuario                   |
| POST   | /ventas/registrar           | Registrar 1 venta                    |
| POST   | /ventas/registrar/list      | Registrar lista de ventas            |
| DELETE | /ventas/cancelar/{id}       | Cancelar venta (mueve a ventas_canceladas + restaura stock) |

## CORS permitidos
- http://localhost:4200
- https://farmacia-ko2.up.railway.app
- https://pharma.ko2-oreilly.com
- https://pharma-b.ko2-oreilly.com
- https://farmacia-ko2-frontend.vercel.app

## Despliegue Railway
- Sin Git — se hace build local y `docker push` al registry de Railway
- Dockerfile y docker-compose.yml en la raíz de FP/
- Variables de entorno en Railway: PORT, JWT_SECRET, DB_URL, DB_USER, DB_PASSWORD

## Pendiente — Rediseño backend (sábado grande)
⚠️ ANTES de tocar prod: usar perfil `dev` → `-Dspring.profiles.active=dev`
- [x] Añadir columna `aemps_code` y `laboratorio` en tabla `articulos` → hecho en `easyfarma_dev`
- [ ] Replicar ALTER TABLE en `fp_dam` (producción) cuando se confirme estable
- [ ] Tabla `order` (id, user, total, payment_method: CASH/CARD/INSURANCE, date, status)
- [ ] Tabla `order_line` (id, order_id, product, qty, price)
- [ ] Devolución = cambiar `status` a RETURNED — NO DELETE destructivo
- [ ] No tocar tabla de usuarios
- DB relativamente vacía (~100 registros por tabla, ~10 usuarios)

## Historial de cambios recientes (rama dev)
- `SecurityConfig.java` — añadido `PATCH` a `allowedMethods` en config CORS (faltaba, causaba falso error CORS en navegador para `PATCH /articulos/{id}/aemps`)
- `Articulos.java` — añadidos campos `aempsCode` (`aemps_code`) y `laboratorio`
- `ArticuloController.java` — añadido `PATCH /{id}/aemps` → llama a `articuloService.linkAemps()`
- `ArticuloService.java` — añadido método `linkAemps(id, aempsCode, laboratorio)`
- `CimaController.java` — proxy hacia `https://cima.aemps.es/cima/rest/medicamentos`
- `VentaCancelada` — refactor: renombrado desde `ComprasCliente` (modelo, repo, servicio, controller)

## Notas técnicas importantes
- `ddl-auto=none` en ambos perfiles — Hibernate NUNCA toca el esquema
- JdbcUserDetailsManager lee usuarios de MySQL directamente (no entities de Users)
- La tabla `ventas` es desnormalizada por diseño del proyecto base — no cambiar sin consenso
- `application-dev.properties` ya existe y apunta a `easyfarma_dev` en Aiven

## Arranque
Lee este archivo y di: "Situado en KO2Pharma Backend. ¿Por dónde continuamos?"
