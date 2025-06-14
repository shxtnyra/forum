# Users API

## Получить список пользователей

## Получить страницу пользователей (пагинация)
**Endpoint:** `GET /v1/users`

**Описание:** Возвращает страницу пользователей с метаданными пагинации.

**Query-параметры:**
- `page` (int, optional) — номер страницы (начиная с 0)
- `size` (int, optional) — размер страницы
- `sort` (string, optional) — сортировка, например: `createdAt,desc`

**Пример ответа (200 OK):**
```json
{
  "content": [
    { "id": 1, "username": "user1", "avatarUrl": "/images/avatars/1.jpg" }
  ],
  "pageable": { ... },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "size": 10,
  "number": 0
}
```

---

## Получить топ пользователей по рейтингу
**Endpoint:** `GET /v1/users/top`

**Описание:** Возвращает топ-50 пользователей по рейтингу.

**Пример ответа (200 OK):**
```json
[
    {
        "id": 38,
        "nickname": "pro37",
        "avatarUrl": "https://i.pravatar.cc/150?img=2",
        "totalRating": 9999.0
    },
    {
        "id": 7,
        "nickname": "newbie6",
        "avatarUrl": "https://i.pravatar.cc/150?img=7",
        "totalRating": 232.0
    },
    {
        "id": 24,
        "nickname": null,
        "avatarUrl": "https://i.pravatar.cc/150?img=6",
        "totalRating": 70.0
    }
]
```

---

## Получить профиль пользователя по ID
**Endpoint:** `GET /v1/users/{id}`

**Описание:** Возвращает подробный профиль пользователя по его ID.

**Пример ответа (200 OK):**
```json
{
    "id": 4,
    "name": null,
    "nickname": "ninja3",
    "profileDescription": "Преподаватель C#",
    "avatarURL": "https://i.pravatar.cc/150?img=4",
    "createAt": "2024-09-22T11:11:57.26925",
    "lastActivity": null,
    "totalRating": 0.0,
    "role": "ROLE_USER",
    "confirmed": true
}
```

---

## Получить посты пользователя
**Endpoint:** `GET /v1/users/{id}/posts`

**Описание:** Возвращает страницу постов, созданных пользователем с указанным id.

**Query-параметры:**
- `page`, `size`, `sort` — параметры пагинации

---

## Получить комментарии пользователя
**Endpoint:** `GET /v1/users/{id}/comments`

**Описание:** Возвращает страницу комментариев, созданных пользователем с указанным id.

---

## Поиск пользователей по никнейму
**Endpoint:** `GET /v1/users/find?nickname={query}`

**Описание:** Ищет пользователей по части никнейма (регистронезависимо, минимум 3 символа).

**Пример ответа (200 OK):**
```json
{
  "id": 123,
  "username": "johndoe",
  "avatarUrl": "/images/avatars/123.jpg"
}
```

---

# Управление своим профилем (требуется авторизация)

## Получить свой профиль
**Endpoint:** `GET /v1/users/me`

**Описание:** Возвращает профиль текущего аутентифицированного пользователя.

**Пример ответа (200 OK):**
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "name": "John Doe",
  "avatarUrl": "https://example.com/avatar.jpg",
  "rating": 42
}
```

---

## Обновить свой профиль
**Endpoint:** `PUT /v1/users/me`

**Описание:** Обновляет профиль текущего пользователя.

**Тело запроса:**
```json
{
  "name": "New Name",
  "avatarUrl": "https://new.avatar.url"
}
```

**Пример ответа (200 OK):**
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "name": "New Name",
  "avatarUrl": "https://new.avatar.url",
  "rating": 42
}
```

---

## Удалить свой аккаунт
**Endpoint:** `DELETE /v1/users/me`

**Описание:** Удаляет аккаунт текущего пользователя. Операция необратима.

**Важно:** Все данные будут удалены.

**Пример ответа (204 No Content):**
```
```

---

## Возможные ошибки
- 404 Not Found — Пользователь не найден
- 400 Bad Request — Некорректные данные
- 409 Conflict — Имя пользователя занято