# Shop API

REST API интернет-магазина на Spring Boot.

Проект представляет собой backend интернет-магазина с управлением пользователями, аутентификацией, товарами, вариантами товаров, корзиной, заказами, скидками и пользовательскими сессиями.

Проект также включает JWT-аутентификацию, управление refresh-сессиями, контейнеризацию с Docker и CI-проверки через GitHub Actions.

---

## Стек технологий

### Backend

* Java 26
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Bean Validation
* MapStruct
* Lombok
* Maven

### База данных и инфраструктура

* PostgreSQL 17
* Redis 8
* Docker
* Docker Compose

### Документация API

* OpenAPI
* Swagger UI

### Контроль качества и CI

* JUnit
* Checkstyle
* SpotBugs
* GitHub Actions
* GitHub Container Registry (GHCR)

---

## Запуск через Docker Compose

Убедитесь, что установлены Docker и Docker Compose.

Настройте файл `.env`, затем выполните:

### dev вариант
```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
```

### prod вариант
```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```
Проверить состояние контейнеров:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml ps
```

Остановить контейнеры:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml down
```

Остановить контейнеры и удалить постоянные volumes:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v
```



---

## Переменные окружения

Приложение использует переменные окружения для настройки подключения к PostgreSQL и Redis.

Пример `.env`:

```env
POSTGRES_DB=shop
POSTGRES_USER=shop_user
POSTGRES_PASSWORD=change_me
POSTGRES_PORT=5432

DB_HOST=postgres
DB_PORT=5432
DB_NAME=shop
DB_USERNAME=shop_user
DB_PASSWORD=change_me

REDIS_HOST=redis
REDIS_PORT=6379

BACKEND_PORT=8080

DB_CONTAINER_NAME=shop-postgres
REDIS_CONTAINER_NAME=shop-redis
BACKEND_CONTAINER_NAME=shop-backend

NETWORK_NAME=shop-backend-network

GHCR_OWNER=your_github_username
```

---

Приложение будет доступно по адресу:

```text
http://localhost:8080
```

## Документация API

После запуска приложения Swagger UI доступен по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI спецификация:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI позволяет просматривать и тестировать доступные REST API endpoints.

---
