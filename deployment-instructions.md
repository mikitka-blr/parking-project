   git add .
   git commit -m "Add Docker and CI/CD for PaaS deployment"
   git push# Инструкция по развертыванию приложения (PaaS)

Для развертывания приложения бесплатно мы можем использовать сервисы, предоставляющие бесплатный уровень для Docker контейнеров или Java приложений, например **Render.com**.

## Развертывание PostgreSQL (Render)
1. Выберите в панели управления Render `New` -> `PostgreSQL`.
2. Задайте имя базы. Должно выбраться "Free" (бесплатный план).
3. После создания сохраните `Internal Database URL` (для связи внутри сервиса Render) и `External Database URL`. Скопируйте пароль (`Password`).

## Развертывание Приложения Spring Boot (Render)
Репозиторий с проектом должен быть загружен на GitHub.
1. Выберите `New` -> `Web Service`.
2. Выберите репозиторий с проектом на GitHub и нажмите `Connect`.
3. Укажите параметры:
   - **Name**: имя сервиса (например, `parking-api`).
   - **Environment**: `Docker`.
   - **Branch**: `main` (или ветка с вашим кодом).
4. Перейдите в настройки `Environment Variables` (переменные окружения) и добавьте те, что используются в `application.properties`:
   - `DB_URL`: Вставьте URL базы данных (скопированный `Internal Database URL` от PostgreSQL) в формате `jdbc:postgresql://<host>/<dbname>`. Например: `jdbc:postgresql://dpg-ckc9p50kdf8s73a21s0g-a:5432/parking_db`
   - `DB_USERNAME`: Вставьте `User` базы.
   - `DB_PASSWORD`: Вставьте `Password` от базы данных.
5. Выберите тариф `Free` и нажмите `Create Web Service`.

Теперь Render самостоятельно соберет приложение с использованием нашего `Dockerfile` и запустит сервер, подключенный к базе PostgreSQL.

## Настройка Healthcheck в CI/CD
Сейчас пайплайн GitHub Actions настроен на:
- **Build и Тесты**: сборку проекта (Gradle) и прогон всех тестов.
- **Docker-сборку**: Сборку Docker-образа в случае успешного прохождения тестов.

Чтобы Render автоматически переразвертывал приложение после коммита в `main`, он использует Webhook.
Healthcheck на стороне Render контролируется автоматически по открытому порту веб-сервиса. Render считает деплой успешным, когда порт 8080 начинает отвечать на запросы.

