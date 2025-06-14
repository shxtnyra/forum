# Administration API

## Управление темами

### Создать тему
**Endpoint:** `POST /v1/administration/topics`

**Требования:**
- Требуется роль ADMIN

**Тело запроса (`TopicCreateDTO`):**
```json
{
  "title": "Название темы",
  "description": "Описание темы",
  "slug": "topic-slug"
}
```

**Пример ответа (200 OK, `TopicDetailsDTO`):**
```json
{
  "id": 1,
  "slug": "topic-slug",
  "title": "Название темы",
  "description": "Описание темы",
  "createdAt": "2025-06-15T10:00:00Z",
  "postCount": 0
}
```

### Удалить тему
**Endpoint:** `DELETE /v1/administration/topics/{id}`

**Требования:**
- Требуется роль ADMIN
- Удаление физическое (без возможности восстановления)

**Пример ответа:** 204 No Content

---

## Управление постами

### Получить посты пользователя
**Endpoint:** `GET /v1/administration/users/{userId}/posts`

**Параметры:**
- `includeInvisible` (boolean) - включать скрытые посты
- `page` (int, optional) - номер страницы
- `size` (int, optional) - размер страницы

**Пример ответа (200 OK, `Page<PostShortDTO>`):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Post Title",
      "createAt": "2025-06-10T10:00:00Z",
      "author": {
        "id": 123,
        "username": "user1"
      }
    }
  ],
  "pageable": { ... }
}
```

### Получить черновики пользователя
**Endpoint:** `GET /v1/administration/users/{userId}/drafts`

**Пример ответа:** аналогично выше, но только черновики

### Получить удаленные посты
**Endpoint:** `GET /v1/administration/users/{userId}/posts/deleted`

### Изменить видимость поста
**Endpoint:** `PATCH /v1/administration/posts/{postId}/visibility`

**Параметры:**
- `isInvisible` (boolean) - сделать пост скрытым/видимым

**Пример ответа:** 204 No Content

### Удалить/восстановить пост
**Endpoints:**
- `DELETE /v1/administration/posts/{postId}` - мягкое удаление
- `PATCH /v1/administration/posts/{postId}/recover` - восстановление

---

## Управление комментариями

### Получить комментарии пользователя
**Endpoint:** `GET /v1/administration/users/{userId}/comments`

**Параметры:**
- `includeInvisible` (boolean) - включать скрытые комментарии

### Удалить/восстановить комментарий
**Endpoints:**
- `DELETE /v1/administration/comments/{commentId}`
- `PATCH /v1/administration/comments/{commentId}/recover`

---

## Управление жалобами

### Получить жалобы пользователя
**Endpoints:**
- `GET /v1/administration/users/{userId}/reports` - жалобы, отправленные пользователем
- `GET /v1/administration/users/reports/{userId}` - жалобы на пользователя

**Пример ответа (`ReportUserDTO`):**
```json
{
  "user": {
    "id": 123,
    "username": "user1"
  },
  "reportCount": 5,
  "reports": {
    "SPAM": 3,
    "HARASSMENT": 2
  }
}
```

### Получить жалобы на посты
**Endpoints:**
- `GET /v1/administration/posts/reports` - все жалобы
- `GET /v1/administration/posts/{postId}/reports` - жалобы на конкретный пост

**Пример ответа (`ReportPostDTO`):**
```json
{
  "post": {
    "id": 456,
    "title": "Post Title"
  },
  "reportCount": 3,
  "reports": {
    "INAPPROPRIATE_CONTENT": 2,
    "OTHER": 1
  }
}
```

### Получить жалобы на комментарии
**Endpoints:**
- `GET /v1/administration/comments/reports` - все жалобы
- `GET /v1/administration/posts/{postId}/comments/reports` - жалобы на комментарии поста

### Изменить статус жалобы
**Endpoints:**
- `PATCH /v1/administration/posts/{postId}/reports`
- `PATCH /v1/administration/comments/{commentId}/reports`

**Параметры:**
- `reportReason` (enum) - причина жалобы
- `newStatus` (boolean) - новый статус (решено/не решено)

---

## Общие ошибки
- 401 Unauthorized - Нет прав доступа
- 403 Forbidden - Недостаточно прав
- 404 Not Found - Ресурс не найден