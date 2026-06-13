# Authentication API

Base path: `/api/auth`  
API version header: `X-API-Version: 1`

---

## POST /api/auth/signup

Create a new local account.

### Request Headers

| Header | Value | Required |
|---|---|---|
| `X-API-Version` | `1` | Yes |
| `Content-Type` | `application/json` | Yes |

### Request Body

```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `username` | `string` | Yes | Display name |
| `email` | `string` | Yes | Email address (used as login identifier) |
| `password` | `string` | Yes | Password (will be encrypted before storage) |

### Success Response

**Status:** `201 Created`

No response body.

### Error Responses

**Status:** `400 Bad Request`

```json
{
  "timestamp": "2026-06-11T12:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "Missing parameters"
}
```

Returned when any required field is `null` or empty.

---

## POST /api/auth/signin

Authenticate with email and password. Returns JWT access and refresh tokens as HTTP-only cookies.

### Request Headers

| Header | Value | Required |
|---|---|---|
| `X-API-Version` | `1` | Yes |
| `Content-Type` | `application/json` | Yes |

### Request Body

```json
{
  "email": "string",
  "password": "string"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | `string` | Yes | Registered email address |
| `password` | `string` | Yes | Account password |

### Success Response

**Status:** `200 OK`

No response body. Tokens are set as HTTP-only cookies:

| Cookie | Path | Max-Age | HttpOnly | SameSite | Secure |
|---|---|---|---|---|---|
| `accessToken` | `/` | 90s | Yes | `Strict` | Yes |
| `refreshToken` | `/api/auth/refresh` | 30d | Yes | `Strict` | Yes |

- `accessToken` — short-lived JWT for authenticating subsequent requests (sent as `Authorization: Bearer <token>`)
- `refreshToken` — long-lived JWT used to obtain a new access token via `/refresh`

### Error Responses

**Status:** `400 Bad Request`

```json
{
  "timestamp": "2026-06-11T12:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "Missing parameters"
}
```

Returned when `email` or `password` is `null` or empty.

---

## POST /api/auth/refresh

Exchange a valid refresh token for a new access/refresh token pair. Uses token rotation — each request invalidates the previous refresh token and issues a new one.

### Request Headers

| Header | Value | Required |
|---|---|---|
| `X-API-Version` | `1` | Yes |

### Request Cookies

| Cookie | Required | Description |
|---|---|---|
| `refreshToken` | Yes | Valid refresh token (set by `/signin` or a previous `/refresh`) |

No request body.

### Success Response

**Status:** `200 OK`

No response body. New tokens are set as HTTP-only cookies (same as `/signin`):

| Cookie | Path | Max-Age | HttpOnly | SameSite | Secure |
|---|---|---|---|---|---|
| `accessToken` | `/` | 90s | Yes | `Strict` | Yes |
| `refreshToken` | `/api/auth/refresh` | 30d | Yes | `Strict` | Yes |

### Error Responses

**Status:** `400 Bad Request`

```json
{
  "timestamp": "2026-06-11T12:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "Missing refresh token"
}
```

Returned when the `refreshToken` cookie is missing or empty.

---

**Status:** `401 Unauthorized`

```json
{
  "timestamp": "2026-06-11T12:00:00.000",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired refresh token"
}
```

Returned when the refresh token is invalid, expired, malformed, or doesn't match the stored token.
