# CRM Spring Boot API

A REST API for a CRM application built with Spring Boot, Spring Security (JWT), and MySQL.

- **Base URL:** `http://localhost:9090`
- **Auth:** Bearer token (JWT) — include `Authorization: Bearer <token>` on all protected routes

---

## Auth Endpoints

### Register

```
POST /api/auth/register
```

**Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response `200`:**
```json
{
  "message": "User registered successfully",
  "email": "john@example.com"
}
```

---

### Login

```
POST /api/auth/login
```

**Body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response `200`:**
```json
{
  "status": "success",
  "message": "User authenticated successfully",
  "token": "<jwt_token>"
}
```

---

## User Endpoints

> All endpoints below require a valid JWT token.
> Header required: `Authorization: Bearer <token>`

### Get Current User

```
GET /api/user/me
```

**Response `200`:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Response `403`** — token missing or invalid.

---

### Update Name

```
PATCH /api/user/me/name
```

**Body:**
```json
{
  "name": "Jane Doe"
}
```

**Fields:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `name` | `String` | Yes | Not blank |

**Response `200`:** updated user object.
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Response `400`** — name is blank.
**Response `403`** — token missing or invalid.

---

### Update Password

```
PATCH /api/user/me/password
```

**Body:**
```json
{
  "oldPassword": "current123",
  "newPassword": "newpass456"
}
```

**Fields:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `oldPassword` | `String` | Yes | Not blank |
| `newPassword` | `String` | Yes | Not blank, min 6 characters |

**Response `200`:**
```json
{
  "message": "Password updated successfully"
}
```

**Response `400`** — old password is wrong or new password fails validation.
```json
{
  "error": "Old password is incorrect"
}
```

**Response `403`** — token missing or invalid.

---

### Logout

Stateless JWT — the server confirms logout but the client **must delete the token** from storage.

```
POST /api/user/logout
```

**Response `200`:**
```json
{
  "status": "success",
  "message": "Logged out successfully. Please delete your token on the client side."
}
```

**Response `403`** — token missing or invalid.

---

## Customer Endpoints

> All endpoints require a valid JWT token.

### Get All Customers

```
GET /api/customers
```

### Get Customer by ID

```
GET /api/customers/{id}
```

### Create Customer

```
POST /api/customers
```

### Update Customer

```
PUT /api/customers/{id}
```

### Delete Customer

```
DELETE /api/customers/{id}
```

---

## Task Endpoints

> All endpoints require a valid JWT token.

### Get All Tasks

```
GET /api/tasks
```

### Get Task by ID

```
GET /api/tasks/{id}
```

### Create Task

```
POST /api/tasks
```

### Update Task

```
PUT /api/tasks/{id}
```

### Delete Task

```
DELETE /api/tasks/{id}
```

---

## React Integration Example

```js
const API = "http://localhost:9090";
const authHeader = () => ({ Authorization: `Bearer ${localStorage.getItem("token")}` });

// Login and store token
const res = await fetch(`${API}/api/auth/login`, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ email, password }),
});
const { token } = await res.json();
localStorage.setItem("token", token);

// Get current user
const me = await fetch(`${API}/api/user/me`, {
  headers: authHeader(),
});

// Update name
await fetch(`${API}/api/user/me/name`, {
  method: "PATCH",
  headers: { ...authHeader(), "Content-Type": "application/json" },
  body: JSON.stringify({ name: "Jane Doe" }),
});

// Update password
await fetch(`${API}/api/user/me/password`, {
  method: "PATCH",
  headers: { ...authHeader(), "Content-Type": "application/json" },
  body: JSON.stringify({ oldPassword: "current123", newPassword: "newpass456" }),
});

// Logout
await fetch(`${API}/api/user/logout`, {
  method: "POST",
  headers: authHeader(),
});
localStorage.removeItem("token");
```
