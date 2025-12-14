# Seeds to Roots – BackEnd

Plataforma API REST construida con Spring Boot para el e-commerce **Seeds to Roots**. Expone toda la lógica de autenticación, catálogos, usuarios, carrito de compras y tablero de estadísticas que consume el frontend (React) del mismo nombre.

## Stack & Arquitectura

- **Java 21** + **Spring Boot 3.5.8** (MVC, Data JPA, Validation, Security).
- Persistencia en **PostgreSQL** mediante Spring Data JPA e Hibernate.
- **JWT** para autenticación stateless y control de acceso por roles (`ADMIN`, `CLIENTE`).
- **Swagger / springdoc-openapi** para documentación interactiva (`/swagger-ui.html`).
- Test unitarios con **JUnit 5** y **Mockito** (servicios, controladores y utilidades).

```text
src/main/java/com/seedstoroots/app
├─ config/        -> SecurityConfig, CorsConfig, SwaggerConfig
├─ controller/    -> Endpoints REST (auth, productos, usuarios, carrito, estadísticas, test)
├─ dto/           -> Requests/Responses para aislar las entidades JPA
├─ entity/        -> Modelo relacional: Usuario, Producto, Carrito, CarritoItem, etc.
├─ repository/    -> Interfaces Spring Data JPA
├─ security/      -> Filtro JWT, utilidades de token
├─ service/       -> Interfaces
└─ service/impl/  -> Reglas de negocio (auth, productos, usuarios, carrito, estadísticas)
```

## Requisitos previos

1. **Java 21** y **Maven 3.9+** (se puede usar el wrapper `mvnw` incluido).
2. **PostgreSQL 14+** con una base creada, por ejemplo `seedstoroots`.
3. Variables/configuración de la BD y del JWT definidas en `src/main/resources/application.properties` o exportadas como variables de entorno:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seedstoroots
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update

jwt.secret=TuClaveSuperSeguraY_Larga
jwt.expiration=86400000 # 24h

cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

> En la rama de despliegue se almacenan valores seguros (no expuestos en código). Ajusta esta configuración antes de producción.

## Ejecución

```bash
# 1. Instalar dependencias y compilar
./mvnw clean install

# 2. Levantar el backend (perfil default)
./mvnw spring-boot:run
```

El servicio queda en `http://localhost:8080`. Puedes validar con `GET /test` o usando Swagger en `http://localhost:8080/swagger-ui.html`.

## Endpoints principales

| Módulo        | Ruta base         | Descripción                                                     | Seguridad |
|---------------|-------------------|-----------------------------------------------------------------|-----------|
| Auth          | `/auth`           | Login y registro. Genera tokens JWT con rol e ID del usuario.   | Público   |
| Productos     | `/api/productos`  | CRUD completo + últimos productos (`/recientes`).               | GET públicos, resto solo `ADMIN` |
| Usuarios      | `/api/usuarios`   | CRUD administrativo completo (incluye creación vía POST).       | `ADMIN`   |
| Carrito       | `/api/carrito`    | Gestión del carrito asociado al usuario autenticado.            | `CLIENTE`/`ADMIN` autenticados |
| Estadísticas  | `/api/estadisticas` | Totales de productos, usuarios, stock bajo, etc.              | `ADMIN`   |
| Test          | `/test`           | Ping simple para monitoreo.                                     | Público   |

### Seguridad y autorización

- El filtro `JwtAuthenticationFilter` valida el token y establece el `Authentication` con el rol (`ROLE_ADMIN`/`ROLE_CLIENTE`).
- `CarritoController` cruza el `usuarioId` de la URL con el que viene en el JWT para impedir que un usuario modifique carritos ajenos.
- Las reglas por rol están centralizadas en `SecurityConfig` usando `HttpSecurity#authorizeHttpRequests`; los administradores también pueden crear usuarios directamente desde `/api/usuarios` (POST).

## Pruebas automatizadas

```bash
./mvnw test
```

Se cubren servicios y controladores clave (productos, usuarios, carrito, auth) además de `JwtUtil`. El test `AppApplicationTests` valida que el contexto de Spring levante correctamente.

## Buenas prácticas ya incorporadas

- **DTOs** para evitar exponer entidades JPA al exterior.
- **Validaciones de negocio** en servicios (stock, estado activo, cantidades positivas).
- **Soft delete** para usuarios y productos (`activo = false`).
- **Configuración CORS** parametrizada vía `cors.allowed-origins` para mantener sincronizado back/front.
- **Swagger/OpenAPI** preconfigurado para facilitar la revisión del profesor o QA.
