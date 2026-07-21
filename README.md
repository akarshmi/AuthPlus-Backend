<p align="center"> <img src="https://img.shields.io/badge/Auth-Plus-2ea44f?style=for-the-badge&labelColor=1a1a1a" alt="AuthPlus" width="200"> </p><h1 align="center">🛡️ AuthPlus</h1> <p align="center"><b>Production-grade authentication that won't let you sleep with the fishes</b><p align="center">
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"></a>
  <a href="https://spring.io/projects/spring-security"><img src="https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security"></a>
  <a href="https://www.postgresql.org/"><img src="https://img.shields.io/badge/PostgreSQL-15+-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"></a>
  <a href="https://jwt.io/"><img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"></a>
  <a href="https://springdoc.org/"><img src="https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="MIT License"></a>
</p>

---

## Why AuthPlus Exists

Authentication looks deceptively simple on the surface. You take an email, a password, maybe a token, and call it a day, right?

Wrong.

The number of perfectly good applications that have been humbled by authentication is frankly impressive. Most of them fall into the same, painfully avoidable traps:

| Common Mistake | What Usually Happens | Reality Check |
|---|---|---|
| **Storing passwords in plain-text** | "We'll hash it later" becomes "We never hashed it". | Later usually means never. And yes, that database dump will eventually leak. |
| **Rolling your own password hashing** | MD5, SHA-1 or some home-brewed concoction makes an appearance. | BCrypt, Argon2 and PBKDF2 exist for a reason. Reinventing crypto is a sport best left unplayed. |
| **Weak JWT implementations** | Signing with weak secrets, not validating expiry, or trusting tokens blindly. | A JWT without proper validation is just an oddly shaped JSON string with delusions of authority. |
| **Poor refresh token handling** | Re-issuing the same refresh token forever, storing it in localStorage, or never revoking it. | If stolen, this is the equivalent of handing over a master key that never expires. |
| **Ignoring token rotation** | One refresh token to rule them all, forever. | Token rotation exists to ensure that if a refresh token _does_ get compromised, its lifespan dies the moment it's misused. |
| **Re-inventing OAuth2 from scratch** | Writing a fragile, half-baked social login flow. | The spec is long for a reason. Save your energy for your actual business logic. |
| **Insecure session management** | Mixing stateless and stateful without knowing the boundaries. | Stateless auth done wrong is just chaos with extra headers. |
| **Leaking sensitive data in logs** | Passwords, JWTs, refresh tokens or OTPs casually printed to console/log files. | Your logs should tell you _what_ happened, not _how to impersonate a user_. |

**AuthPlus** was built to solve this exact problem.

It isn't trying to be everything. It is trying to be one thing, and do it properly: a **production-ready, secure, stateless, JWT-based authentication & authorization module**. No magic shortcuts. No security theatre. Just battle-tested patterns, clean architecture, and enough guardrails to stop you from shooting yourself in the foot.

---

## Features

AuthPlus focuses on doing authentication right. Every feature below exists, is implemented and is ready to plug into your application.

| Feature | Description | Funny Commentary |
|---|---|---|
| **User Registration** | Validated registration with strong password policy, phone number validation and unique email enforcement. | We check your email is unique so you don't accidentally create your 37th account at 2 AM. |
| **Secure Login** | Email + password authentication powered by Spring Security's `AuthenticationManager`. | No "username or password is incorrect" ambiguity games. Just clean, predictable auth. |
| **JWT Access Tokens** | Short-lived, signed access tokens (15 minutes) with user claims. | Short-lived for a reason. Like a good cup of coffee, it shouldn't stick around all day. |
| **Refresh Tokens (Rotation)** | Secure, database-backed refresh tokens with **full rotation** and old-token invalidation. | If someone swipes your refresh token, rotation ensures it self-destructs the moment it's reused. |
| **Stateless Authentication** | No HTTP sessions. Authentication is carried entirely by JWTs via a custom filter. | Your app scales horizontally without having to remember who's sitting where. |
| **Role-Based Authorization (RBAC)** | 9 predefined roles (SUPER_ADMIN down to PATIENT). Enforced via `@PreAuthorize` and Spring Security authorities. | Fine-grained access control without duct-taping 47 if-statements together. |
| **BCrypt Password Hashing** | Passwords hashed with BCrypt (strength 12). Never stored in plain text. | Your users' passwords deserve better than SHA-1 nostalgia. |
| **Change Password** | Authenticated users can securely change their password after verifying their current one. | Because "forgot password" shouldn't be your only escape hatch. |
| **Forgot Password Flow** | Generates a single-use, time-limited password reset token. | Secure by default. Token is single-use and expires before it can gather dust. |
| **Reset Password Flow** | Resets password using a validated token, revokes all refresh tokens on success. | We log everyone out for your safety. Yes, even you. It's for the greater good. |
| **Token Invalidation on Logout** | Revokes all refresh tokens for the authenticated user on logout. | Clean exit. No stragglers left behind. |
| **Profile Endpoint (`/me`)** | Fetch the currently authenticated user's profile without exposing the password. | Because asking "who am I?" is a valid API call. |
| **Centralized Validation** | Jakarta Validation with clean, structured error responses. | Your API will tell you _exactly_ what's wrong, not just that something is vaguely invalid. |
| **Global Exception Handling** | Consistent JSON error responses across the entire module. | No more leaking stack traces to clients. Your exception handler earns its keep. |
| **DTO-First Design** | Requests and responses are strictly separated from entities. | What leaves your API and what lives in your database should remain politely introduced, never married. |
| **OpenAPI / Swagger UI** | Fully configured with JWT Bearer auth. Hit Authorize, paste your token, test away. | Interactive docs that actually work. A rare and beautiful thing. |
| **Production-Ready Migrations** | Flyway-compatible SQL migrations with proper constraints, indexes and cascade rules. | Your schema shouldn't be a surprise to anyone, least of all production. |
| **Secure by Default** | CSRF disabled (stateless), CORS configurable, secrets externalized, no sensitive data logged. | Secure defaults so you don't have to remember the 37 things on the "don't forget" list. |

---

## Authentication Flows

AuthPlus is built around predictable, secure flows. Here's how the system actually behaves under the hood.

### 1. Registration Flow

```text
┌─────────────┐
│   Client    │
└─────┬───────┘
      │ POST /api/v1/auth/register (JSON)
      ▼
┌────────────────────────────────────────────┐
│  AuthController                           │
│  Validate @Valid RegisterRequest          │
└─────┬────────────────────────────────────┘
      │ Check email uniqueness
      ▼
┌────────────────────────────────────────────┐
│  UserRepository.existsByEmail()           │
│  → Reject if exists (400 Bad Request)     │
└─────┬────────────────────────────────────┘
      │ Encode password (BCrypt strength 12)
      ▼
┌────────────────────────────────────────────┐
│  User entity created (enabled = true)     │
│  Role persisted as Enum                   │
└─────┬────────────────────────────────────┘
      │ Save to PostgreSQL
      ▼
┌────────────────────────────────────────────┐
│  Return UserResponse (NO PASSWORD)        │
└─────┬────────────────────────────────────┘
      │ 201 Created
      ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

### 2. Login Flow

```text
┌─────────────┐
│   Client    │
└─────┬───────┘
      │ POST /api/v1/auth/login (email, password)
      ▼
┌────────────────────────────────────────────┐
│  AuthenticationManager.authenticate()      │
│  DaoAuthenticationProvider + BCrypt       │
└─────┬────────────────────────────────────┘
      │ Credentials valid?
      ├── No → 401/Bad Credentials
      ▼ Yes
┌────────────────────────────────────────────┐
│  Load User (CustomUserDetailsService)     │
└─────┬────────────────────────────────────┘
      │ Generate Access Token (15 min)
      │ Generate Refresh Token (7 days)
      ▼
┌────────────────────────────────────────────┐
│  Persist RefreshToken (DB, unique, expiry)│
│  Mark not revoked                         │
└─────┬────────────────────────────────────┘
      │ Return LoginResponse
      │ {accessToken, refreshToken, user, expiresAt}
      ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

### 3. JWT Access Token Flow (Stateless Auth)

```text
┌─────────────┐
│   Client    │
└─────┬───────┘
      │ GET /api/v1/auth/me
      │ Authorization: Bearer <accessToken>
      ▼
┌────────────────────────────────────────────┐
│  JwtAuthenticationFilter (OncePerRequest) │
│  Extract Bearer token                     │
└─────┬────────────────────────────────────┘
      │ Parse & validate signature (HS512)
      │ Check expiry, subject (email)
      ▼
┌────────────────────────────────────────────┐
│  Load UserDetails by email                │
│  Validate token against UserDetails       │
└─────┬────────────────────────────────────┘
      │ Valid?
      ├── No → Filter chain continues, Security denies (401)
      ▼ Yes
┌────────────────────────────────────────────┐
│  Set SecurityContextHolder (authenticated)│
│  Attach authorities (ROLE_*)              │
└─────┬────────────────────────────────────┘
      │ Proceed to Controller (@PreAuthorize etc.)
      ▼
┌────────────────────────────────────────────┐
│  Return protected resource                │
└─────┬────────────────────────────────────┘
      ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

### 4. Refresh Token Flow (Rotation in Action)

```text
┌─────────────┐
│   Client    │
└─────┬───────┘
      │ POST /api/v1/auth/refresh
      │ { "refreshToken": "<old-refresh>" }
      ▼
┌────────────────────────────────────────────┐
│  RefreshTokenService.verifyAndRotate()    │
└─────┬────────────────────────────────────┘
      │ Lookup token in DB (unique)
      ├── Not found → 400 TokenRefreshException
      ▼
┌────────────────────────────────────────────┐
│  Check revoked? | Check expiry (Instant)  │
│  If invalid → delete + 400                │
└─────┬────────────────────────────────────┘
      │ VALID → Mark OLD token as REVOKED
      │ (Rotation step 1 - Invalidate)
      ▼
┌────────────────────────────────────────────┐
│  Generate NEW refresh token (7 days)      │
│  Persist NEW token (not revoked)          │
└─────┬────────────────────────────────────┘
      │ Generate NEW access token (15 min)
      ▼
┌────────────────────────────────────────────┐
│  Return {accessToken, refreshToken(new)}  │
└─────┬────────────────────────────────────┘
      │ 200 OK
      ▼
┌─────────────┐
│   Client    │
└─────────────┘

NOTE: Old refresh token is DEAD. If replayed → not found/revoked → rejected.
```

### 5. Password Reset Flow

```text
┌─────────────┐                        ┌─────────────┐
│   Client    │                        │   Mailer*   │
└─────┬───────┘                        └─────┬───────┘
      │ 1. POST /forgot-password (email)     │
      ▼                                      │
┌────────────────────────────────────────────┤
│  Generate UUID reset token (single-use)   │
│  Store with user, expiry (60 min), used=false
└─────┬────────────────────────────────────┘
      │ 2. Return generic response (security-safe)
      │    "Reset link sent if email exists"
      ▼
┌────────────────────────────────────────────┐
│  Log token (dev) / Send via email (prod) │
└─────┬────────────────────────────────────┘
      │ *(Email integration point - configurable) ▼
      │                                (token delivered)
      │ 3. User opens reset link / submits
      │    POST /reset-password {token, newPassword}
      ▼
┌────────────────────────────────────────────┐
│  Validate token exists                    │
│  Check !used && expiryDate > now          │
└─────┬────────────────────────────────────┘
      │ Valid?
      ├── No → 400 Bad Request
      ▼ Yes
┌────────────────────────────────────────────┐
│  Encode new password (BCrypt)             │
│  Update user.password                     │
│  Mark token as USED                       │
│  REVOKE ALL refresh tokens for user      │
└─────┬────────────────────────────────────┘
      │ 204 No Content
      ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

> _Note:_ In the current implementation, the password reset token is logged (for local/dev visibility). In production, replace that with your transactional email provider (SendGrid, SES, Mailgun, SMTP, etc.) and **never** log the raw token.

### 6. Change Password Flow

```text
┌─────────────┐
│   Client    │
└─────┬───────┘
      │ POST /api/v1/auth/change-password
      │ Bearer <accessToken>
      │ {currentPassword, newPassword}
      ▼
┌────────────────────────────────────────────┐
│  Authenticated (JWT filter)               │
└─────┬────────────────────────────────────┘
      │ Verify currentPassword matches BCrypt
      ├── No → 400 "Current password incorrect"
      ▼ Yes
┌────────────────────────────────────────────┐
│  Encode newPassword (BCrypt 12)           │
│  Save user                                │
└─────┬────────────────────────────────────┘
      │ 204 No Content
      ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

---

## Tech Stack

AuthPlus is intentionally lean. Every dependency pulls its weight and serves a clear security or maintainability purpose.

### Backend

| Technology | Version | Why We Chose It |
|---|---|---|
| **[Java](https://www.java.com/)** | 21 | Modern LTS. Records, pattern matching, improved performance and long-term support. |
| **[Spring Boot](https://spring.io/projects/spring-boot)** | 3.x | Convention over configuration, mature ecosystem, battle-tested for production. |
| **[Spring Security](https://spring.io/projects/spring-security)** | 6.x | Industry standard for authN/authZ. Clean filter chain, method security (`@PreAuthorize`) and solid extensibility. |
| **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** | Latest | Clean repository abstraction, reduces boilerplate without hiding SQL when you need it. |
| **[Hibernate ORM](https://hibernate.org/orm/)** | 6.x | Reliable ORM, great with PostgreSQL, entity lifecycle management done right. |
| **[Spring Web](https://spring.io/projects/spring-framework)** | 6.x | REST API development with predictable, annotation-driven controllers. |
| **[Spring Validation](https://beanvalidation.org/)** | Jakarta Validation | Enforce contracts at the boundary. Fail fast, fail clearly. |
| **[PostgreSQL](https://www.postgresql.org/)** | 15+ | Robust, ACID-compliant, great indexing, JSON support and production-proven. |
| **[Flyway](https://flywaydb.org/)** | Latest | Version-controlled schema migrations. No more "it worked on my local DB" mysteries. |
| **[JWT (JJWT)](https://github.com/jwtk/jjwt)** | 0.12.5 | Modern, maintained JWT library. Clean API, secure defaults and no unnecessary transitive baggage. |
| **[Lombok](https://projectlombok.org/)** | Latest | Cuts boilerplate (builders, getters, constructors) without compromising readability. |
| **[SpringDoc OpenAPI (Swagger)](https://springdoc.org/)** | 3.0.x | Generates OpenAPI 3 specs automatically. Clean Swagger UI with JWT auth baked in. |
| **[Maven](https://maven.apache.org/)** | 3.9+ | Predictable builds, dependency management and easy CI integration. |

### Testing

| Technology | Why We Chose It |
|---|---|
| **[JUnit 5](https://junit.org/junit5/)** | Modern testing framework. Parameterized tests, nested classes, solid extension model. |
| **[Spring Boot Test](https://docs.spring.io/spring-boot/reference/testing/index.html)** | Integration testing with real context where it matters, sliced tests where it doesn't. |
| **[MockMvc](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)** | End-to-end HTTP testing without spinning up a full servlet container overhead unnecessarily. |
| **[AssertJ](https://assertj.github.io/doc/)** | Fluent, readable assertions. Your tests should read like specifications. |

---

## Project Architecture

AuthPlus follows **Clean, Layered Architecture** with strict separation of concerns. Each layer has one job, does it well, and stays out of the others' business.

### Folder Structure

```text
src/main/java/com/unitrix/auth
├── config/                # Application configuration (Security, CORS, Swagger, Jackson)
├── controller/            # REST controllers (HTTP layer)
├── dto/                   # Data Transfer Objects (request/response contracts)
├── entity/                # JPA entities (domain persistence model)
├── exception/             # Custom exceptions + GlobalExceptionHandler
├── repository/            # Spring Data JPA repositories
├── security/              # Security primitives (JWT filter, UserDetails, UserDetailsService)
├── service/               # Business logic interfaces
│   └── impl/              # Business logic implementations
└── util/                  # Cross-cutting utilities
```

### Module Breakdown

| Package | Responsibility | Notes |
|---|---|---|
| **`config`** | Centralized Spring configuration. | Houses `SecurityConfig` (filter chain, stateless, RBAC), `CorsConfig` (configurable origins), `SwaggerConfig` (JWT bearer auth), `JacksonConfig` (serialization). |
| **`controller`** | Thin HTTP boundary. | Maps HTTP requests to service calls, validates DTOs (`@Valid`), returns proper status codes. No business logic. |
| **`dto`** | API contracts. | Strictly separates external contracts from internal entities. Never exposes passwords or JPA internals. |
| **`entity`** | Persistence model. | `User` implements `UserDetails` (clean mapping to Spring Security), `RefreshToken` (DB-backed with rotation), `PasswordResetToken` (single-use, time-limited). |
| **`exception`** | Error modeling & handling. | Custom exceptions (`BadRequestException`, `ResourceNotFoundException`, `TokenRefreshException`) + `GlobalExceptionHandler` for consistent JSON errors. |
| **`repository`** | Data access. | Spring Data JPA. Simple, testable, query methods where clear. |
| **`security`** | Security plumbing. | `JwtAuthenticationFilter` (extract/validate JWT, populate SecurityContext), `CustomUserDetailsService` (loads user by email), `UserDetailsImpl` not used (User implements UserDetails directly - clean choice). |
| **`service` / `service.impl`** | Business logic. | Orchestrates validation, hashing, token generation/rotation, password reset. Transactional boundaries defined here. |
| **`util`** | Helpers. | Shared, stateless utilities (e.g. `SecurityUtils` if needed). Kept minimal by design. |

### Database Schema (Migrations)

AuthPlus uses **Flyway** migrations. Schema is explicit, versioned and safe.

| Table | Purpose | Key Points |
|---|---|---|
| `users` | Core user identity | Unique email index, role enum stored as VARCHAR, boolean flags for account state, timestamps. |
| `refresh_tokens` | DB-backed refresh tokens | Unique token, FK to users (ON DELETE CASCADE), expiry (TIMESTAMPTZ), `revoked` flag for rotation/invalidation, indexed by user_id. |
| `password_reset_tokens` | Single-use reset tokens | Unique token, FK to users, expiry, `used` flag (prevents replay). |

See [`src/main/resources/db/migration/V1__init_auth_tables.sql`](src/main/resources/db/migration/V1__init_auth_tables.sql) for full DDL.

---

## Security Design

Security isn't an afterthought here. It's the default posture.

### Password Security

- **BCrypt Hashing** (strength 12) via `BCryptPasswordEncoder`.
- **Password Policy enforced at DTO boundary**: minimum 8 characters, must contain uppercase, lowercase, digit and special character (`@$!%*?&`).
- **Never logged**. Passwords are never written to logs, never serialized back, never exposed in responses.
- **Reset invalidates sessions**: On successful password reset, **all refresh tokens** for that user are revoked.

### JWT Design

| Aspect | Value | Rationale |
|---|---|---|
| **Access Token TTL** | 15 minutes | Minimizes blast radius if stolen. Short-lived by design. |
| **Refresh Token TTL** | 7 days | Balances usability (no constant re-login) with security. |
| **Algorithm** | HS256/HS512 (HMAC-SHA) | Symmetric, simple, fast. Secret managed via env vars. |
| **Claims** | `sub` (email), `role`, `userId` | Minimal, useful claims for authorization/context without overloading. |
| **Storage (Refresh)** | **Database-backed** | Critical difference. We can revoke, rotate, detect reuse. Not just stateless JWTs floating around. |
| **Rotation** | **Enforced** | Old refresh token is marked `revoked` on use. New one issued. Replay of old token is rejected. |
| **Stateless Sessions** | Yes | No `JSESSIONID`. Scales horizontally without sticky sessions or distributed session stores (by default). |

### Spring Security Configuration

```text
SecurityFilterChain
├── CSRF → disabled (stateless REST APIs, Bearer tokens)
├── CORS → configurable via properties (origins, methods, headers, credentials)
├── Session → STATELESS (SessionCreationPolicy.STATELESS)
├── Public endpoints → /register, /login, /refresh, /forgot-password, /reset-password, /v3/api-docs/**, /swagger-ui/**
├── Everything else → authenticated
└── Filters → JwtAuthenticationFilter BEFORE UsernamePasswordAuthenticationFilter
```

- **Method Security Enabled** (`@EnableMethodSecurity`) to support `@PreAuthorize`, `@Secured` if needed.
- **DaoAuthenticationProvider** backed by `CustomUserDetailsService` + BCrypt.
- **AuthenticationManager** exposed as Bean for programmatic authentication (login flow).

### Authorization Model

Roles are enforced at multiple levels:

| Role | Typical Use | Example Guard |
|---|---|---|
| `SUPER_ADMIN` | Platform-level superuser | `@PreAuthorize("hasRole('SUPER_ADMIN')")` |
| `ADMIN` | Hospital administration | `@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")` |
| `DOCTOR` | Clinical staff | `@PreAuthorize("hasRole('DOCTOR')")` |
| `NURSE` | Nursing staff | `@PreAuthorize("hasRole('NURSE')")` |
| `RECEPTIONIST` | Front desk | `@PreAuthorize("hasRole('RECEPTIONIST')")` |
| `LAB_TECHNICIAN` | Lab operations | `@PreAuthorize("hasRole('LAB_TECHNICIAN')")` |
| `PHARMACIST` | Pharmacy | `@PreAuthorize("hasRole('PHARMACIST')")` |
| `ACCOUNTANT` | Billing/Finance | `@PreAuthorize("hasRole('ACCOUNTANT')")` |
| `PATIENT` | End user/patient | `@PreAuthorize("hasRole('PATIENT')")` |

Authorities are prefixed with `ROLE_` (Spring Security convention). The enum is **extensible** by design - add a new constant, no structural changes required.

### Security Best Practices Applied

- **Secrets externalized**: All secrets (JWT, DB, CORS) via environment variables / `application.yml` (no hardcoded secrets).
- **No sensitive data in logs**: Passwords, JWTs, refresh tokens, OTPs/tokens are **never** logged.
- **Input validation everywhere**: DTO-level with Jakarta Validation. Fail early.
- **Least privilege**: Stateless, short-lived access tokens, revocable refresh tokens.
- **Defence in depth**: DB constraints (UNIQUE, FK, NOT NULL), validation, auth filters, method security.
- **Token reuse detection**: Via rotation + revoked flag. Replay attempts are rejected.
- **CORS configurable**: Tighten to your actual frontend origins in production.
- **Generic error messages on auth**: Forgot-password returns a generic response to avoid user enumeration.

---

## REST API Reference

All endpoints are prefixed with `/api/v1/auth`. Request/response bodies are JSON. Validation errors return a structured `ApiErrorResponse`.

### 1. Register

Create a new user account.

**Endpoint:** `POST /api/v1/auth/register`

**Auth:** Public

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@unitrix.com",
  "phoneNumber": "+12345678901",
  "password": "StrongP@ss1!",
  "role": "PATIENT"
}
```

**Validation Rules:**

| Field | Rule |
|---|---|
| `firstName` / `lastName` | Not blank, max 50 |
| `email` | Valid email, unique, max 100 |
| `phoneNumber` | E.164 format (optional but validated if present) e.g. `+12345678901` |
| `password` | Min 8, max 64, must contain uppercase, lowercase, number, special char (`@$!%*?&`) |
| `role` | Not null, must be one of defined `Role` enum values |

**Success Response (201 Created):**

```json
{
  "id": "e7a43e9f-0e8a-4f77-9f7d-8d3f7c8f3e4f",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@unitrix.com",
  "phoneNumber": "+12345678901",
  "role": "PATIENT",
  "enabled": true,
  "createdAt": "2024-11-20T14:32:18.421234"
}
```

**Error (400 Bad Request - Email exists):**

```json
{
  "timestamp": "2024-11-20T14:33:02.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already in use",
  "path": "/api/v1/auth/register"
}
```

---

### 2. Login

Authenticate and receive access + refresh tokens.

**Endpoint:** `POST /api/v1/auth/login`

**Auth:** Public

**Request Body:**

```json
{
  "email": "john.doe@unitrix.com",
  "password": "StrongP@ss1!"
}
```

**Success Response (200 OK):**

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": "e7a43e9f-0e8a-4f77-9f7d-8d3f7c8f3e4f",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@unitrix.com",
    "phoneNumber": "+12345678901",
    "role": "PATIENT",
    "enabled": true,
    "createdAt": "2024-11-20T14:32:18.421234"
  },
  "expiresAt": 1732116723456
}
```

**Notes:**

- `expiresAt` is epoch millis (approximate expiry of the **access token**).
- Store `refreshToken` securely (HttpOnly Secure cookie is recommended for browser apps; for mobile/API clients store in secure keystore/secure storage).

---

### 3. Refresh Token

Rotate refresh token and issue new access token.

**Endpoint:** `POST /api/v1/auth/refresh`

**Auth:** Public

**Request Body:**

```json
{
  "refreshToken": "your-old-refresh-token-here"
}
```

**Success Response (200 OK):**

```json
{
  "accessToken": "new-access-token...",
  "refreshToken": "new-rotated-refresh-token...",
  "user": {
    "id": "e7a43e9f-0e8a-4f77-9f7d-8d3f7c8f3e4f",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@unitrix.com",
    "phoneNumber": "+12345678901",
    "role": "PATIENT",
    "enabled": true,
    "createdAt": "2024-11-20T14:32:18.421234"
  },
  "expiresAt": 1732117623456
}
```

**Error (400 Bad Request - Expired/Revoked/Not Found):**

```json
{
  "timestamp": "2024-11-20T14:40:12.456Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Refresh token expired or revoked. Please login again.",
  "path": "/api/v1/auth/refresh"
}
```

---

### 4. Get Current User (`/me`)

Fetch authenticated user's profile.

**Endpoint:** `GET /api/v1/auth/me`

**Auth:** Required (Bearer JWT)

**Headers:**

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Success Response (200 OK):**

```json
{
  "id": "e7a43e9f-0e8a-4f77-9f7d-8d3f7c8f3e4f",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@unitrix.com",
  "phoneNumber": "+12345678901",
  "role": "PATIENT",
  "enabled": true,
  "createdAt": "2024-11-20T14:32:18.421234"
}
```

**Error (401/403)** - Returned by security filter if token missing/invalid.

---

### 5. Change Password

Change password for authenticated user.

**Endpoint:** `POST /api/v1/auth/change-password`

**Auth:** Required (Bearer JWT)

**Request Body:**

```json
{
  "currentPassword": "StrongP@ss1!",
  "newPassword": "EvenStrongerP@ss2!"
}
```

**Password rules** same as registration (min 8, uppercase, lowercase, number, special char).

**Success Response (204 No Content):** Empty body.

**Error (400 Bad Request - Wrong current password):**

```json
{
  "timestamp": "2024-11-20T14:45:33.789Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Current password incorrect",
  "path": "/api/v1/auth/change-password"
}
```

---

### 6. Forgot Password

Request a password reset token.

**Endpoint:** `POST /api/v1/auth/forgot-password`

**Auth:** Public

**Request Body:**

```json
{
  "email": "john.doe@unitrix.com"
}
```

**Success Response (200 OK):**

```text
Reset link sent if email exists
```

> **Security note:** Response is **generic** regardless of whether the email exists. This prevents user enumeration attacks.

---

### 7. Reset Password

Reset password using token.

**Endpoint:** `POST /api/v1/auth/reset-password`

**Auth:** Public

**Request Body:**

```json
{
  "token": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "newPassword": "NewStrongP@ss1!"
}
```

**Success Response (204 No Content):** Empty body.

**Side effects on success:**

- Password updated (BCrypt)
- Reset token marked as `used`
- **All refresh tokens** for the user are revoked (forced re-login on all devices)

**Error (400 Bad Request - Invalid/Expired/Used):**

```json
{
  "timestamp": "2024-11-20T14:52:10.112Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Token expired or used",
  "path": "/api/v1/auth/reset-password"
}
```

---

### 8. Logout

Revoke all refresh tokens for authenticated user.

**Endpoint:** `POST /api/v1/auth/logout`

**Auth:** Required (Bearer JWT)

**Headers:**

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Success Response (204 No Content):** Empty body.

---

## Error Handling

AuthPlus uses a centralized `@RestControllerAdvice` (`GlobalExceptionHandler`) to ensure **every error** returns a predictable, structured JSON response.

### Error Response Schema

```json
{
  "timestamp": "2024-11-20T14:55:22.334Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/auth/register"
}
```

### Exception Mapping

| Exception | HTTP Status | When It Occurs |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | Jakarta Validation fails (DTO constraints). Aggregates all field errors. |
| `BadRequestException` | 400 | Business rule violations (email in use, wrong password, invalid/used reset token). |
| `TokenRefreshException` | 400 | Refresh token not found, expired, or revoked (rotation/reuse). |
| `ResourceNotFoundException` | 404 | Resource lookup fails (e.g. user not found in specific flows). |
| `AuthenticationException` (Spring Security) | 401 | Invalid credentials (handled by Spring Security filter chain). |
| `AccessDeniedException` | 403 | Authenticated but not authorized (role insufficient) - surfaced by security. |

### HTTP Status Cheat Sheet (with commentary)

| Status Code | Meaning | Funny Commentary |
|---|---|---|
| **200 OK** | All good, here's your data. | The API is having a good day. So are you. |
| **201 Created** | Resource created successfully. | We built it. It exists now. No take-backs. |
| **204 No Content** | Success, nothing to return. | Efficient. Polite. No JSON clutter. |
| **400 Bad Request** | You sent something invalid. Read the message. | The API is not mad, just disappointed. |
| **401 Unauthorized** | Missing or invalid credentials/token. | Show your wristband (JWT) or try the door again. |
| **403 Forbidden** | Authenticated, but not allowed. | You have a key, just not _this_ key. |
| **404 Not Found** | Resource doesn't exist. | It was never here. We checked twice. |
| **422 Unprocessable Entity** | (Not used currently) Validation semantics differ. | We stick to 400 for validation - clean and consistent. |

---

## Environment Variables

AuthPlus is **12-factor-app friendly**. No secrets in source code. Configure everything via environment variables.

| Variable | Required | Default | Description |
|---|---|---|---|
| `DATABASE_URL` | Yes | `jdbc:postgresql://localhost:5432/unitrix_hospital` | JDBC URL for PostgreSQL. |
| `DATABASE_USERNAME` | Yes | `postgres` | Database username. |
| `DATABASE_PASSWORD` | Yes | `postgres` | Database password. |
| `JWT_SECRET` | **Yes (Production)** | `mustBeAtLeast64BytesLong...` | Base64/UTF-8 secret for signing JWTs. **Must be >= 64 characters** for HS512. Rotate with care. |
| `JWT_ACCESS_TOKEN_EXPIRATION` | No | `900000` (15 min) | Access token expiry in milliseconds. |
| `JWT_REFRESH_TOKEN_EXPIRATION` | No | `604800000` (7 days) | Refresh token expiry in milliseconds. |
| `CORS_ORIGINS` | No | `http://localhost:3000,http://localhost:5173` | Comma-separated allowed origins (for browser clients). |
| `SPRING_PROFILES_ACTIVE` | No | `default` | Active Spring profile (dev/test/prod). |

> **Security Warning:** The default `JWT_SECRET` is **only** for local development. In production, generate a cryptographically strong random secret (>= 64 bytes). Never commit `.env` files.

See [`.env.example`](.env.example) for a ready-to-copy template.

---

## Running the Project Locally

Get AuthPlus up and running in under 5 minutes.

### Prerequisites

- **Java 21+** ([Adoptium/Temurin](https://adoptium.net/))
- **Maven 3.9+**
- **PostgreSQL 15+** (running locally or via Docker)
- **Git**

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/authplus.git
cd authplus
```

### 2. Set Up the Database

Create a database for AuthPlus:

```sql
CREATE DATABASE unitrix_hospital;
CREATE USER unitrix_user WITH PASSWORD 'strong_password';
GRANT ALL PRIVILEGES ON DATABASE unitrix_hospital TO unitrix_user;
```

Or use Docker (quick option):

```bash
docker run --name unitrix-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=unitrix_hospital -p 5432:5432 -d postgres:15
```

### 3. Configure Environment Variables

Copy the example env file:

```bash
cp .env.example .env
```

Edit `.env` with your values (at minimum DB creds and a strong JWT secret for anything beyond local testing).

### 4. Run Database Migrations

Flyway runs automatically on startup (`spring.flyway.enabled=true`). So just starting the app will create all tables.

### 5. Build & Run (Backend)

Using Maven Wrapper (recommended):

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

Or with your system Maven:

```bash
mvn clean install && mvn spring-boot:run
```

The application will start on: [http://localhost:8080](http://localhost:8080)

### 6. Explore API Documentation (Swagger UI)

Open Swagger UI in your browser:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**Authorize with JWT:**

1. Login via `POST /api/v1/auth/login` to get `accessToken`
2. Click the **Authorize** button (top-right)
3. Paste `Bearer <your-access-token>` (include `Bearer` prefix)
4. Click Authorize → Test protected endpoints instantly

### 7. Run Tests

```bash
# Unit + Integration tests
./mvnw test

# With coverage (if configured in CI)
./mvnw verify
```

Target coverage: **>= 80%** (unit + integration tests cover JWT, auth service, controllers, password encoding, refresh token flows).

---

## Design Decisions

AuthPlus makes deliberate, opinionated choices. Here's the _why_ behind the important ones.

| Decision | Choice | Alternatives | Why This Choice |
|---|---|---|---|
| **Authentication Strategy** | **Stateless JWT** (access) + **DB-backed refresh tokens** | Pure JWT (no DB), Session-based (JSESSIONID), OAuth-only | Pure stateless JWTs can't be revoked securely. Sessions don't scale horizontally without extra infra. This hybrid gives revocability, rotation, and horizontal scalability. |
| **Token Rotation** | **Enforced on every refresh** | No rotation / reuse detection | If a refresh token leaks, rotation kills it immediately on first reuse. It's one of the cheapest, highest-value security wins. |
| **Refresh Token Storage** | **PostgreSQL (DB)** | Redis, in-memory, JWT-only | DB gives strong consistency, ACID, simple revocation (`revoked` flag), and no extra infra dependency. Redis is great but optional here. |
| **Password Hashing** | **BCrypt (strength 12)** | Argon2, PBKDF2, SCrypt | BCrypt is mature, well-supported in Spring Security, battle-tested, and safe with sensible cost. Argon2 is excellent too - BCrypt was chosen for ecosystem stability. |
| **UserDetails Mapping** | **User entity implements UserDetails** | Separate UserDetailsImpl class | Less indirection. Clean, avoids mapping layers. Entity stays aligned with security concerns without leaking to API (DTOs prevent that). |
| **DTO-First API** | **Strict separation** | Return entities directly | Decouples persistence from API contracts. Prevents accidental password leaks, lazy-loading issues, and makes API evolution painless. |
| **Stateless Sessions** | **SessionCreationPolicy.STATELESS** | IF_REQUIRED / ALWAYS | Correct for REST APIs with Bearer tokens. No server-side session state, scales out cleanly. |
| **Global Exception Handling** | **@RestControllerAdvice** | Per-controller try-catch | Consistent error shape across all endpoints. DRY. Easier to evolve and document. |
| **Schema Migrations** | **Flyway** | Liquibase, Hibernate auto-ddl | Version-controlled, repeatable, safe for CI/CD and production. `ddl-auto=validate` (not `create/update`) prevents drift. |
| **Validation Strategy** | **DTO boundary (@Valid)** | Service-layer validation only | Fail fast at HTTP boundary. Returns 400 with structured field errors before hitting business logic. |
| **CORS** | **Configurable (env-driven)** | Hardcoded | Environment-specific. Easy to tighten in prod, permissive enough for local dev. |
| **Logging** | **SLF4J, no sensitive data** | System.out.println | Structured, configurable logging. Explicit rule: never log passwords, JWTs, refresh tokens, reset tokens. |

---

## Future Improvements

AuthPlus is production-ready as-is, but there's always room to harden and extend. Here's a pragmatic roadmap (checkboxes so you can track progress).

### Security Hardening

- [ ] **MFA (TOTP)** - Time-based one-time passwords (Google Authenticator, Authy)
- [ ] **WebAuthn / Passkeys** - Passwordless authentication (FIDO2)
- [ ] **Device Management** - Track logged-in devices, revoke sessions per-device
- [ ] **Session Dashboard** - List active sessions (refresh tokens) for users
- [ ] **Rate Limiting** - Per-IP / per-user throttling on login, register, forgot-password (e.g. Bucket4j or Redis-backed)
- [ ] **Account Lockout** - Temporary lock after N failed login attempts
- [ ] **JWT Key Rotation** - Support for multiple signing keys (kid header) for zero-downtime rotation

### Infrastructure & Observability

- [ ] **Redis Integration** - Store refresh tokens in Redis for ultra-fast validation / revocation in high-scale clusters
- [ ] **Docker & Docker Compose** - One-command local setup (App + PostgreSQL + Redis)
- [ ] **Kubernetes Manifests** - Production-ready K8s deployment (ConfigMaps, Secrets, HPA)
- [ ] **Actuator Endpoints** - Health, info, metrics (expose carefully, secure with roles)
- [ ] **Prometheus + Grafana** - Auth metrics (login success/failure, token refresh rate, etc.)
- [ ] **Audit Logging** - Immutable audit trail for sensitive actions (password change, role change, logout-all)
- [ ] **Structured JSON Logging** - Logback with JSON encoder for log aggregation (ELK/EFK/Loki)

### Features & DX

- [ ] **Email Integration** - Replace token logging with real SMTP (Spring Mail) + beautiful HTML email templates
- [ ] **Email Verification** - Optional email verification on registration
- [ ] **Resend Verification Email** - Self-serve resend flow
- [ ] **Social Login** - Google OAuth2 (Spring Security OAuth2 Client) - extendable to GitHub, Microsoft etc.
- [ ] **Admin User Management API** - CRUD for users, role assignment, enable/disable accounts
- [ ] **API Key Authentication** - Alternative auth for service-to-service calls
- [ ] **Brute-force Protection** - Distributed rate limiting (Redis) for multi-instance deployments
- [ ] **Internationalization (i18n)** - Localized validation/error messages

### Testing & Quality

- [ ] **Contract Tests** - OpenAPI contract validation
- [ ] **Load Testing** - Gatling/k6 scripts for auth endpoints (login/refresh under load)
- [ ] **Security Scanning** - Dependency checks (OWASP Dependency-Check), SAST in CI
- [ ] **Testcontainers** - Spin up real PostgreSQL in integration tests (more realistic)

---

## What You'll Learn

Reading (and extending) AuthPlus is a great way to level up your backend security fundamentals. Here's what you'll walk away with:

| Concept | What You'll Learn | Where In Code |
|---|---|---|
| **Stateless Authentication** | How JWT-based stateless auth actually works with Spring Security filters. | `JwtAuthenticationFilter`, `SecurityConfig` |
| **Refresh Token Rotation** | Why rotation matters and how to implement secure reuse detection. | `RefreshTokenServiceImpl.verifyAndRotate()` |
| **Spring Security Internals** | AuthenticationManager, AuthenticationProvider, UserDetails, SecurityContext. | `SecurityConfig`, `CustomUserDetailsService` |
| **BCrypt & Password Security** | Proper password hashing, validation policy, and safe reset flows. | `AuthServiceImpl` (register, change/reset password) |
| **Clean Architecture** | Layered design, DTOs, separation of concerns, transactional boundaries. | Entire module structure |
| **Global Exception Handling** | Consistent API error modeling with `@RestControllerAdvice`. | `GlobalExceptionHandler`, `ApiErrorResponse` |
| **Token Revocation Strategy** | DB-backed revocation vs pure stateless JWTs - trade-offs and implementation. | `RefreshToken` entity + service |
| **Secure Reset Flows** | Single-use, time-limited tokens without leaking user existence. | `PasswordResetToken`, forgot/reset flows |
| **Validation Done Right** | Jakarta Validation at boundaries + clean aggregation of errors. | DTOs + `GlobalExceptionHandler` |
| **Production Schema Management** | Versioned migrations with Flyway, constraints, indexes, cascades. | `V1__init_auth_tables.sql` |
| **Testing Auth Flows** | Unit tests for JWT + integration tests for full auth flows (MockMvc). | `JwtServiceTest`, `AuthIntegrationTest` |

---

## Contributing

Contributions are welcome! AuthPlus aims to stay secure, minimal and production-ready. Here's how to contribute without breaking that.

### Contribution Guidelines

1. **Fork** the repository
2. **Create a feature branch**: `git checkout -b feat/your-feature-name`
3. **Follow code style**: Constructor injection only (no field injection), meaningful names, SOLID principles
4. **Write tests**: Add/update unit/integration tests for new behavior. Aim to keep >= 80% coverage
5. **Keep security in mind**: Never log sensitive data. If adding auth flows, think about rotation/revocation
6. **Update docs**: If you change endpoints, DTOs, env vars or flows - update this README
7. **Use Conventional Commits**: e.g. `feat: add TOTP MFA support`, `fix: prevent refresh token reuse edge case`
8. **Run tests locally**: `./mvnw clean test` must pass
9. **Push & PR**: Open a PR with a clear description of _what_ and _why_. Screenshots not needed, test cases are better

### Code Quality Checklist

- [ ] Constructor injection (no `@Autowired` on fields)
- [ ] No `TODO`s / placeholders left behind
- [ ] No sensitive data logged
- [ ] DTOs used for all API I/O (no entities exposed)
- [ ] Validation on request DTOs
- [ ] Transactional boundaries where appropriate
- [ ] JavaDoc on public classes/methods (especially services, security, config)
- [ ] Passes `mvn clean verify`

---

## License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for full details.

---

## Final Words

Authentication is one of those things you _really_ don't want to get wrong twice. AuthPlus exists so you don't have to roll it the third time.

It's secure by default, boring in all the right places (predictable, testable, maintainable), and opinionated enough to save you from the common foot-guns.

If it saves you an hour of debugging refresh token rotation at 2 AM, or prevents one token-reuse incident, it's done its job.

**If you find AuthPlus useful, drop a ⭐ on the repo.** It always makes the maintainers weirdly happy.

<p align="center">
  <a href="#authplus">⬆️ Back to Top</a>
</p>

<p align="center">
  <i>Built securely. Logged responsibly. Rotated properly.</i><br/>
  <i>(Your future security audit will thank you.)</i>
</p>
