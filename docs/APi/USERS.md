# Users API

# Публичные
## Получить список пользователей

**Endpoint**: `GET /api/users/list`

**Описание**: Возвращает полный список пользователей в сокращенном формате (без пагинации).

**Пример запроса**:
```bash
curl -X GET 'https://api.yourdomain.com/api/users/list' \
  -H 'Authorization: Bearer {access_token}'
```

**Пример ответа (200 OK)**:
```json
[
  {
    "id": 1,
    "username": "user1",
    "avatarUrl": "/images/avatars/1.jpg"
  },
  {
    "id": 2,
    "username": "user2",
    "avatarUrl": "/images/avatars/2.jpg"
  }
]
```

---

## Получить профиль пользователя

**Endpoint**: `GET /api/users/{id}`

**Параметры**:
- `id` (path, long) - ID пользователя

**Пример запроса**:
```bash
curl -X GET 'https://api.yourdomain.com/api/users/123' \
  -H 'Authorization: Bearer {access_token}'
```

**Пример ответа (200 OK)**:
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "name": "John Doe",
  "registrationDate": "2025-01-15T10:30:00Z",
  "rating": 42
}
```

**Возможные ошибки**:
- `404 Not Found` - Пользователь не найден

---

## Поиск пользователей по никнейму

**Endpoint**: `GET /api/users/find?nickname={query}`

**Описание**: Ищет

**Параметры**:
- `nickname` (query, string) - Часть никнейма (мин. 3 символа)

**Пример запроса**:
```bash
curl -X GET 'https://api.yourdomain.com/api/users/find?nickname=john' \
  -H 'Authorization: Bearer {access_token}'
```

**Пример ответа (200 OK)**:
```json
[
  {
    "id": 123,
    "username": "johndoe",
    "avatarUrl": "/images/avatars/123.jpg"
  }
]
```

---

# Управление своим профилем(с авторизацией)

### Получить свой профиль
**Endpoint**: `GET /api/users/me`

Требуется: Аутентификация

### Обновить профиль
**Endpoint**: `PUT /api/users/me`

**Тело запроса**:
```json
{
  "name": "New Name",
  "avatarUrl": "https://new.avatar.url"
}
```

### Удалить аккаунт
**Endpoint**: `DELETE /api/users/me`

**Важно**: Операция необратима. Все данные будут удалены.

---

## Схемы данных

### UserPreviewDTO
```json
{
  "id": "long",
  "username": "string",
  "avatarUrl": "string"
}
```

### UserProfileDTO
```json
{
  "id": "long",
  "username": "string",
  "email": "string",
  "name": "string",
  "registrationDate": "ISO8601",
  "rating": "int"
}