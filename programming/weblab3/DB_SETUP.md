# Подготовка БД PostgreSQL с нуля

## Вариант 1: Docker (автоматически)

При запуске `docker-compose up` PostgreSQL создаётся автоматически:

- **База:** `coordinates_db`
- **Пользователь:** `postgres`
- **Пароль:** `postgres`
- **Порт:** `5433` (на хосте) → `5432` (внутри контейнера)

Скрипт `backend/postgres/init.sql` выполняется при первом запуске и создаёт таблицы `users` и `results`.

---

## Вариант 2: Ручная настройка в pgAdmin

### 1. Установка pgAdmin

Скачайте pgAdmin с [pgadmin.org](https://www.pgadmin.org/download/) или установите PostgreSQL с pgAdmin.

### 2. Подключение к серверу PostgreSQL

1. Запустите pgAdmin.
2. Правый клик по **Servers** → **Register** → **Server**.
3. **General** → имя: `Local` (любое).
4. **Connection:**
   - Host: `localhost` (или `127.0.0.1`)
   - Port: `5432` (или `5433`, если используете Docker)
   - Username: `postgres`
   - Password: `postgres` (или ваш пароль)
   - Save password: по желанию
5. **Save**.

### 3. Создание базы данных

1. Правый клик по **Databases** → **Create** → **Database**.
2. **Database:** `coordinates_db`
3. **Owner:** `postgres`
4. **Save**.

### 4. Создание таблиц

1. Выберите базу `coordinates_db`.
2. **Tools** → **Query Tool** (или F5).
3. Вставьте и выполните SQL:

```sql
-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    login VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(100) NOT NULL,
    created TIMESTAMP,
    modified TIMESTAMP
);

-- Таблица результатов проверки точек
CREATE TABLE IF NOT EXISTS results (
    id BIGSERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    r DOUBLE PRECISION NOT NULL,
    hit BOOLEAN NOT NULL,
    user_login VARCHAR(50) REFERENCES users(login) ON DELETE CASCADE,
    created TIMESTAMP
);
```

4. Нажмите **Execute** (F5 или иконка ▶).

### 5. Проверка

В Query Tool выполните:

```sql
SELECT * FROM users;
SELECT * FROM results;
```

Таблицы должны быть пустыми.

---

## Вариант 3: Полный сброс через Docker

Если база была повреждена или нужно начать с нуля:

```bash
docker-compose down -v
docker-compose up -d
```

Флаг `-v` удаляет volumes, включая данные PostgreSQL. При следующем `up` база и таблицы создаются заново.

---

## Параметры подключения для приложения

| Параметр | Значение |
|----------|----------|
| Host     | `localhost` (или `postgres` внутри Docker) |
| Port     | `5432` (в Docker снаружи: `5433`) |
| Database | `coordinates_db` |
| User     | `postgres` |
| Password | `postgres` |

---

## Схема таблиц

**users**
| Колонка       | Тип         | Описание            |
|---------------|--------------|---------------------|
| login         | VARCHAR(50)  | PK, логин           |
| password_hash | VARCHAR(100) | хэш пароля (BCrypt) |
| created       | TIMESTAMP    | дата создания       |
| modified      | TIMESTAMP    | дата изменения      |

**results**
| Колонка   | Тип            | Описание                          |
|-----------|-----------------|-----------------------------------|
| id        | BIGSERIAL       | PK, автоинкремент                 |
| x, y, r   | DOUBLE PRECISION| координаты и радиус               |
| hit       | BOOLEAN         | попала ли точка в область         |
| user_login| VARCHAR(50)     | FK → users(login), ON DELETE CASCADE |
| created   | TIMESTAMP       | дата создания                     |
