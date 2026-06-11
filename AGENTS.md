# Nix-Initializr

Monorepo: `backend/` (Spring Boot 4.0.6, Java 25, Maven) + `frontend/` (React 19, TypeScript 6, Vite 8). No CI.

## Backend (`backend/`)

- **Entrypoint**: `src/main/java/nixdocs/backend/Main.java`
- **Run**: `./mvnw spring-boot:run` (requires PostgreSQL + Redis)
- **Test**: `./mvnw test`
- **Docker services**: `docker compose up -d` in `backend/` (Postgres :5432, Redis :6379)
- **Schema**: `spring.jpa.hibernate.ddl-auto=update` — JPA manages it automatically
- **API versioning**: header `X-API-Version: 1` (not URL path)
- **Auth**: email-based (not username). `POST /api/auth/signup`, `POST /api/auth/signin` (JWT tokens in response)
- **Required `.env`** in `backend/` (loaded automatically by dev shell):

  ```
  JWT_SECRET=<256-bit key>
  JWT_ACCESS_TOKEN_EXPIRATION=90000
  JWT_REFRESH_TOKEN_EXPIRATION=2592000000
  GITHUB_CLIENT_ID=...
  GITHUB_CLIENT_SECRET=...
  ```

## Frontend (`frontend/`)

- **Entrypoint**: `src/main.tsx`
- `npm run dev` — Vite dev server
- `npm run build` — `tsc -b && vite build` (typechecks first)
- `npm run lint` — ESLint
- `npm run preview` — Vite preview

## Nix

- `nix develop .#frontend` / `nix develop .#backend` — per-package dev shells (source `.env` automatically)
- `nix run .#backend` — runs `./mvnw spring-boot:run` from `backend/`
- `nix develop .#podman` — podman environment
