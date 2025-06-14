# Reports API

## Создать жалобу на пост
**Endpoint:** `POST /v1/posts/{postId}/reports`

**Описание:** Создает новую жалобу на указанный пост.

**Требования:**
- Требуется аутентификация (JWT токен)
- Пользователь не может жаловаться на собственные посты

**Тело запроса (`ReportCreateDTO`):**
```json
{
  "reason": "SPAM" // Причина жалобы (SPAM, HARASSMENT, INAPPROPRIATE_CONTENT, etc.)
}
```

**Параметры:**
- `postId` (path, long) - ID поста, на который подается жалоба

**Пример ответа (200 OK, `ReportDetailsDTO`):**
```json
{
  "id": 1,
  "author": {
    "id": 123,
    "username": "reporter_user"
  },
  "reason": "SPAM",
  "post": {
    "id": 456,
    "title": "Post Title",
    "createAt": "2025-06-10T10:00:00Z",
    "author": {
      "id": 789,
      "username": "post_author"
    }
  },
  "comment": null,
  "reportedAt": "2025-06-15T14:30:00Z",
  "solved": false
}
```

**Возможные ошибки:**
- 401 Unauthorized - Пользователь не аутентифицирован
- 400 Bad Request - Неверный формат запроса
- 403 Forbidden - Попытка пожаловаться на собственный пост
- 404 Not Found - Пост не найден
- 409 Conflict - Повторная жалоба на тот же пост с той же причиной

---

## Создать жалобу на комментарий
**Endpoint:** `POST /v1/comments/{commentId}/reports`

**Описание:** Создает новую жалобу на указанный комментарий.

**Требования:**
- Требуется аутентификация (JWT токен)
- Пользователь не может жаловаться на собственные комментарии

**Тело запроса (`ReportCreateDTO`):**
```json
{
  "reason": "HARASSMENT"
}
```

**Параметры:**
- `commentId` (path, long) - ID комментария, на который подается жалоба

**Пример ответа (200 OK, `ReportDetailsDTO`):**
```json
{
  "id": 2,
  "author": {
    "id": 123,
    "username": "reporter_user"
  },
  "reason": "HARASSMENT",
  "post": null,
  "comment": {
    "id": 789,
    "content": "Offensive comment",
    "createAt": "2025-06-12T15:00:00Z",
    "author": {
      "id": 456,
      "username": "comment_author"
    }
  },
  "reportedAt": "2025-06-15T15:00:00Z",
  "solved": false
}
```

**Возможные ошибки:**
- 401 Unauthorized - Пользователь не аутентифицирован
- 400 Bad Request - Неверный формат запроса
- 403 Forbidden - Попытка пожаловаться на собственный комментарий
- 404 Not Found - Комментарий не найден
- 409 Conflict - Повторная жалоба на тот же комментарий с той же причиной

---

## Модели данных

### ReportCreateDTO
```json
{
  "reason": "string" // Причина жалобы (enum)
}
```

### ReportDetailsDTO
```json
{
  "id": "long",
  "author": "UserShortDTO",
  "reason": "string",
  "post": "PostShortDTO | null",
  "comment": "CommentShortDTO | null",
  "reportedAt": "LocalDateTime",
  "solved": "boolean"
}
```

### Доступные причины жалоб (ReportReason)
- `SPAM` - Спам или реклама
- `HARASSMENT` - Оскорбления или травля
- `INAPPROPRIATE_CONTENT` - Неподобающий контент
- `OTHER` - Другая причина

---

## Особенности работы:
1. Один пользователь может создать только одну жалобу на пост/комментарий с конкретной причиной
2. Жалобы на собственный контент запрещены
3. При создании жалобы автоматически устанавливается:
    - Текущая дата/время (`reportedAt`)
    - Статус `solved: false`
4. Администраторы и модераторы получают уведомления о новых жалобах