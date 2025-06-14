# Topics API

## Получить список всех тем
**Endpoint:** `GET /v1/topics`

**Описание:** Возвращает список всех тем форума в сокращённом формате.

**Пример ответа (200 OK):**
```json
[
  {
    "id": 1,
    "slug": "java",
    "title": "Java",
    "description": "Вопросы по Java"
  },
  {
    "id": 2,
    "slug": "spring",
    "title": "Spring Framework",
    "description": "Spring, Spring Boot и экосистема"
  }
]
```

---

## Получить подробную информацию о теме
**Endpoint:** `GET /v1/topics/{slug}`

**Описание:** Возвращает подробную информацию о теме по её slug.

**Пример ответа (200 OK):**
```json
{
  "id": 1,
  "slug": "java",
  "title": "Java",
  "description": "Вопросы по Java",
  "createdAt": "2025-01-01T12:00:00Z",
  "postCount": 123
}
```

---

## Возможные ошибки
- 404 Not Found — Тема не найдена
