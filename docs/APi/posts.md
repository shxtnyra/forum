Вот обновленная документация Posts API с учетом ваших DTO:

# Posts API

## Получить черновики текущего пользователя
**Endpoint:** `GET /v1/posts/drafts`

**Описание:** Возвращает страницу черновиков текущего пользователя в формате `PostShortDTO`.

**Требования:**
- Требуется аутентификация (JWT токен)

**Query-параметры:**
- `page` (int, optional) - номер страницы (по умолчанию 0)
- `size` (int, optional) - размер страницы (по умолчанию 10)
- `sort` (string, optional) - сортировка (например: `createAt,desc`)

**Пример ответа (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Draft Title",
      "createAt": "2025-06-13T10:00:00Z",
      "author": {
        "id": 123,
        "username": "user1"
      }
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

## Создать черновик поста
**Endpoint:** `POST /v1/posts/drafts`

**Описание:** Создает новый черновик поста. Возвращает `PostDetailsDTO`.

**Требования:**
- Требуется аутентификация

**Тело запроса (`PostCreateDTO`):**
```json
{
  "title": "Draft Title",
  "content": "Draft content",
  "topicId": 1
}
```

**Поля:**
- `title` (string) - заголовок поста
- `content` (string, required) - содержимое поста
- `topicId` (long, required) - ID темы

**Пример ответа (201 Created):**
```json
{
  "id": 1,
  "title": "Draft Title",
  "content": "Draft content",
  "createAt": "2025-06-13T10:00:00Z",
  "updateAt": null,
  "author": {
    "id": 123,
    "username": "user1",
    "avatarUrl": "https://example.com/avatars/user1.jpg"
  }
}
```

---

## Редактировать черновик поста
**Endpoint:** `PATCH /v1/posts/drafts/{id}`

**Описание:** Обновляет существующий черновик. Возвращает `PostDetailsDTO`.

**Требования:**
- Требуется аутентификация
- Пользователь должен быть автором черновика

**Тело запроса (`PostCreateDTO`):**
```json
{
  "title": "Updated Title",
  "content": "Updated content",
  "topicId": 1
}
```

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "title": "Updated Title",
  "content": "Updated content",
  "createAt": "2025-06-13T10:00:00Z",
  "updateAt": "2025-06-14T11:00:00Z",
  "author": {
    "id": 123,
    "username": "user1"
  }
}
```

---

## Опубликовать черновик поста
**Endpoint:** `PATCH /v1/posts/drafts/{id}/release`

**Описание:** Публикует черновик поста. Возвращает `PostDetailsDTO` опубликованного поста.

**Требования:**
- Требуется аутентификация
- Пользователь должен быть автором черновика

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "title": "Published Post",
  "content": "Post content",
  "createAt": "2025-06-13T10:00:00Z",
  "updateAt": "2025-06-15T12:00:00Z",
  "author": {
    "id": 123,
    "username": "user1"
  }
}
```

---

## Получить пост по ID
**Endpoint:** `GET /v1/posts/{id}`

**Описание:** Возвращает полную информацию о посте в формате `PostDetailsDTO`.

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "title": "Post Title",
  "content": "Post content with **markdown** support",
  "createAt": "2025-06-13T10:00:00Z",
  "updateAt": null,
  "author": {
    "id": 123,
    "username": "user1",
    "avatarUrl": "https://example.com/avatars/user1.jpg"
  }
}
```

---

## Получить ленту постов
**Endpoint:** `GET /v1/posts`

**Описание:** Возвращает список постов в формате `PostShortDTO` с пагинацией и фильтрацией.

**Query-параметры:**
- `lastSeenId` (long, optional) - ID последнего просмотренного поста
- `topic` (string, optional) - фильтр по теме (slug)
- `period` (string, optional) - период (`day`, `week`, `month`, `year`, `all`)
- `sort` (string, optional) - сортировка (`newest`, `top`)
- `limit` (int, optional) - лимит постов (макс. 20)

**Пример ответа (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Latest Post",
    "createAt": "2025-06-15T10:00:00Z",
    "author": {
      "id": 123,
      "username": "user1"
    }
  },
  {
    "id": 2,
    "title": "Another Post",
    "createAt": "2025-06-14T09:00:00Z",
    "author": {
      "id": 456,
      "username": "user2"
    }
  }
]
```

---

## Особенности:
1. **Временные метки:**
    - `createAt` - время создания
    - `updateAt` - время последнего редактирования (null если не редактировался)

2. **Доступ:**
    - Черновики видны только автору
    - Удаленные/скрытые посты не отображаются в ленте

3. **Ошибки:**
    - 401 Unauthorized - для защищенных endpoints без авторизации
    - 403 Forbidden - при попытке редактирования чужого поста
    - 404 Not Found - если пост не существует