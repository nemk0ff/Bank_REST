# 🏦 Bank Cards Management System

Система управления банковскими картами с REST API, аутентификацией JWT и ролевым доступом.

## 🚀 Быстрый старт

### Требования
- Java 17+
- Docker и Docker Compose
- Maven 3.8+

### Запуск через Docker Compose

```bash
# Клонируйте репозиторий
git clone <your-repo-url>
cd bankcards

# Запустите приложение с БД
docker-compose up -d

# Приложение будет доступно по http://localhost:8080
```

### Локальная разработка

```bash
# Сборка и запуск
mvn clean package
java -jar target/bankcards-*.jar

# Или с Maven
mvn spring-boot:run
```

## 📋 Функциональность

### 👥 Роли пользователей

**Администратор (ADMIN):**
- Полный CRUD для карт
- Управление пользователями
- Просмотр всех карт системы

**Пользователь (USER):**
- Просмотр своих карт
- Переводы между своими картами
- Блокировка карт
- Поиск и фильтрация карт

### 🔐 Аутентификация

Система использует JWT токены для аутентификации. Токен должен передаваться в заголовке:
```
Authorization: Bearer <your-jwt-token>
```

## 📊 API Документация

После запуска приложения доступны:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI спецификация**: http://localhost:8080/api/api-docs
- **YAML документация**: http://localhost:8080/api/api-docs.yaml

## 🗄️ База данных

**PostgreSQL** конфигурация:
- Хост: `localhost:5432`
- База: `banking_system`
- Пользователь: `postgres`
- Пароль: `postgres`

Миграции управляются через **Liquibase**.

## ⚙️ Конфигурация

Основные настройки в `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/banking_system
    username: postgres
    password: postgres

jwt:
  secret: your-secret-key
  expiration: 86400000 # 24 hours
```

## 🧪 Тестирование

```bash
# Запуск unit-тестов
mvn test

# Запуск с coverage
mvn jacoco:report
```

Тесты используют TestContainers для изолированного тестирования с реальной БД.

## 🐳 Docker

### Сборка образа
```bash
docker build -t bank-cards-app .
```

### Запуск контейнера
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/banking_system \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  bank-cards-app
```

## 🔧 Технологии

- **Java 17** + **Spring Boot 3.4.5**
- **Spring Security** + **JWT**
- **Spring Data JPA** + **PostgreSQL**
- **Liquibase** для миграций
- **MapStruct** для маппинга DTO
- **SpringDoc OpenAPI** для документации
- **Docker** для контейнеризации

## 🚀 Эндпоинты API

### Аутентификация
- `POST /auth/login` - Вход в систему
- `POST /auth/register` - Регистрация пользователя

### Карты (USER)
- `GET /user/cards` - Список карт пользователя
- `GET /user/cards/{id}` - Получить карту
- `POST /user/cards/{id}/block` - Заблокировать карту
- `POST /user/cards/transfer` - Перевод между картами

### Карты (ADMIN)
- `GET /admin/cards` - Все карты системы
- `POST /admin/cards` - Создать карту
- `DELETE /admin/cards/{id}` - Удалить карту
- `GET /admin/cards/user/{userId}` - Карты пользователя

## 📝 Логирование

Уровни логирования настраиваются через environment variables:
- `LOG_LEVEL_COM_EXAMPLE=DEBUG`
- `LOG_LEVEL_SPRING_SECURITY=DEBUG`
- `LOG_LEVEL_HIBERNATE_SQL=DEBUG`

## 🤝 Разработка

### Code Style
Проект использует Checkstyle для проверки code style:
```bash
mvn checkstyle:check
```