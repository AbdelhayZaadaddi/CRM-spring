# Auth API Documentation

Base URL: `/api/auth`

These endpoints are **public** — no JWT token required.

---

## Endpoints

### POST /api/auth/register
Registers a new user account. The role is automatically set to `USER`.

**Request Body**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | Yes | Full name of the user |
| `email` | String | Yes | Unique email address |
| `password` | String | Yes | Plain-text password (hashed with BCrypt before storing) |

**Response** `200 OK`
```json
{
  "message": "User registered successfully",
  "email": "john@example.com"
}
```

**Error** `400 Bad Request` — validation failure (missing or invalid fields).

```json
{
  "name": "Name is required",
  "email": "Email is required"
}
```

**Error** `500 Internal Server Error` — when the email is already registered.

---

### POST /api/auth/login
Authenticates a user and returns a JWT token valid for **24 hours**.

**Request Body**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | String | Yes | Registered email address |
| `password` | String | Yes | Plain-text password |

**Response** `200 OK`
```json
{
  "status": "success",
  "message": "User authenticated successfully",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Error** `403 Forbidden` — when credentials are invalid.

---

## Using the Token

Include the token from `/login` in all subsequent requests via the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Tokens expire after **24 hours**. After expiry, log in again to obtain a new token.

