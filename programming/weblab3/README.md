# WebLab4 — Микросервисы + Web Components

Веб-приложение: пользователь вводит координаты точки и получает ответ, попала ли точка в заданную область.

- Backend теперь разделён на **2 независимых микросервиса**:
  - **Auth**: регистрация/аутентификация, выдача JWT-токена
  - **Geometry**: REST API для проверки точки, сохраняет результаты в своей схеме БД
- Frontend — **2 Web Components**:
  - `<point-form>`: форма (логин/пароль + x/y/r) и отправка запросов
  - `<check-result>`: отображение результата и графика области

Технологии: **JAX-RS (REST)**, **JPA/Hibernate**, **MySQL**, **WildFly**, **Vite**, **Web Components**.

---

## Запуск

```bash
docker compose down -v
docker compose up --build
```

**Важно:** после старта подождите 1–2 минуты, пока MySQL и WildFly поднимутся.

- **Фронтенд:** http://localhost:3000
- **Auth (прямой порт):** http://localhost:8081/auth
- **Geometry (прямой порт):** http://localhost:8082/geometry

Фронт обращается к сервисам через Nginx-прокси (один origin):
- `/auth/...` → `auth-service`
- `/geometry/...` → `geometry-service`

---

## Проверка работы

1. Откройте http://localhost:3000
2. Зарегистрируйтесь (логин + пароль)
3. Войдите (получите токен)
4. Введите `x`, `y`, `r` и нажмите «Проверить»

---

## REST API

Через фронтенд (рекомендуется, один origin):
- `POST /auth/api/auth/register` `{ "login": "...", "password": "..." }`
- `POST /auth/api/auth/login` `{ "login": "...", "password": "..." }` → `{ "token": "..." }`
- `POST /geometry/api/geometry/check` `{ "x": 1.0, "y": 2.0, "r": 2.0 }` + `Authorization: Bearer <token>` → `{ "hit": true|false }`

---

## Структура проекта

```
weblab3/
├── docker-compose.yml
├── backend/
│   ├── mysql/init.sql
│   ├── auth-service/ (REST, JWT, users)
│   └── geometry-service/ (REST, JWT validation, area check + results)
└── frontend/
    ├── Dockerfile, nginx.conf
    └── src/wc/ (Web Components)
```

---

## Технологии

| Компонент | Технология |
|-----------|------------|
| Backend | JAX-RS (REST), JPA/Hibernate, MySQL, JWT |
| Frontend | Web Components, Vite |
| Сервер | WildFly 27 |
| БД | MySQL 8.0 |
