### Hexlet tests and linter status:
[![Actions Status](https://github.com/ArturStimbiris/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/ArturStimbiris/java-project-99/actions)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=ArturStimbiris_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ArturStimbiris_java-project-99)

### Demo  
Рабочая демо-версия приложения доступна по адресу:  
https://java-project-99-8dn6.onrender.com

## Task Manager — Система управления задачами

### Описание проекта

Task Manager — это полнофункциональная система управления задачами, разработанная на Spring Boot, которая предоставляет современный API для управления задачами, пользователями и статусами задач. Проект реализует все ключевые аспекты разработки веб-приложений, включая аутентификацию, авторизацию, ORM-моделирование и мониторинг ошибок.

### Ключевые особенности

- **RESTful API**: Полностью соответствует принципам REST  
- **Аутентификация и авторизация**: JWT-based аутентификация с ролевой моделью доступа  
- **Безопасность**: Spring Security с защитой от распространённых уязвимостей  
- **База данных**: Поддержка PostgreSQL и H2 для разработки и тестирования  
- **Документация API**: Автоматическая генерация документации через SpringDoc OpenAPI  
- **Мониторинг ошибок**: Интеграция с Sentry для отслеживания ошибок в реальном времени  
- **Тестирование**: Полное покрытие unit и integration тестами  
- **CI/CD**: Автоматизированная сборка и деплой через GitHub Actions  

### Функциональность

- **Управление пользователями**: Регистрация, аутентификация, CRUD операции  
- **Управление статусами задач**: Создание, редактирование и удаление статусов задач  
- **JWT аутентификация**: Безопасный механизм аутентификации  
- **Валидация данных**: Комплексная валидация входных данных  
- **Обработка ошибок**: Единый центр обработки исключений  
- **Логирование ошибок**: Интеграция с Sentry для мониторинга  

### Технологический стек

- **Backend**: Spring Boot 3.5.5, Spring Security, Spring Data JPA  
- **База данных**: PostgreSQL, H2 (для тестирования)  
- **Аутентификация**: JWT (JSON Web Tokens)  
- **Документация**: SpringDoc OpenAPI 3.0  
- **Мониторинг**: Sentry для отслеживания ошибок  
- **Тестирование**: JUnit 5, MockMvc, Spring Security Test  
- **Сборка**: Gradle с поддержкой Java 21  
- **Деплой**: Render.com с автоматическим CI/CD  

### Быстрый старт

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/ArturStimbiris/java-project-99.git
   ```

2. Установите зависимости и запустите приложение:
   ```bash
   ./gradlew bootRun
   ```

3. Приложение будет доступно по адресу:  
   `http://localhost:8080`

### API Endpoints

- `POST /api/login` — Аутентификация пользователя  
- `GET /api/users` — Получение списка пользователей  
- `POST /api/users` — Создание нового пользователя  
- `GET /api/task_statuses` — Получение списка статусов задач  
- `POST /api/task_statuses` — Создание нового статуса задачи  

Этот проект является выпускной работой курса **"Java-разработчик"** на платформе Hexlet и демонстрирует все полученные в ходе обучения навыки и знания.