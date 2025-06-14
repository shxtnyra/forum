# Auth API

## Регистрация пользователя
**Endpoint:** `POST /v1/auth/register`

**Описание:** Регистрирует нового пользователя. На email отправляется письмо для подтверждения.

**Тело запроса:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "username": "user1",
  "email": "user1@example.com",
  "name": null,
  "registrationDate": "2025-01-15T10:30:00Z",
  "rating": 0
}
```

---

## Подтверждение email
**Endpoint:** `GET /api/auth/confirm?token={token}`

**Описание:** Подтверждает email пользователя по токену из письма.

**Параметры:**
- `token` (query, string) — токен подтверждения

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "confirmedAt": "2025-01-15T11:00:00Z"
}
```

---

## Вход (логин)
**Endpoint:** `POST /api/auth/login`

**Описание:** Аутентификация пользователя по логину/email и паролю. Возвращает access и refresh токены.

**Тело запроса:**
```json
{
  "loginOrEmail": "user1",
  "password": "string"
}
```

**Пример ответа (200 OK):**
```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

---

## Обновление токена
**Endpoint:** `POST /api/auth/refresh`

**Описание:** Обновляет access и refresh токены по refresh токену.

**Тело запроса:**
```json
{
  "refreshToken": "jwt-refresh-token"
}
```

**Пример ответа (200 OK):**
```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

---

## Выход (logout)
**Endpoint:** `POST /api/auth/logout`

**Описание:** Инвалидация refresh токена (выход пользователя).

**Тело запроса:**
```json
{
  "refreshToken": "jwt-refresh-token"
}
```

**Пример ответа (204 No Content):**

---

## Возможные ошибки
- 400 Bad Request — Некорректные данные
- 401 Unauthorized — Неверные логин/пароль или невалидный токен
- 409 Conflict — Пользователь уже существует
- 404 Not Found — Токен подтверждения не найден или истёк
