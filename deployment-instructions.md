# Инструкция по развертыванию приложения

В этом проекте используется раздельное развертывание:
- **Backend (Spring Boot)** размещается на **Heroku**.
- **Frontend (React/Vite)** размещается на **Vercel**.

## 1. Развертывание Backend (Heroku)
Проект настроен для нативной сборки Spring Boot приложения в Heroku через Gradle:
1. Зарегистрируйтесь на [Heroku](https://heroku.com).
2. Создайте новое приложение `New` -> `Create new app` (например, `parking-api`).
3. Перейдите на вкладку **Resources** и в поиске *Add-ons* найдите **Heroku Postgres**. Выберите тариф *Mini* (он бесплатный) и добавьте к приложению. Heroku автоматически создаст базу данных и пропишет переменную окружения `DATABASE_URL`.
4. Перейдите на вкладку **Settings** -> **Reveal Config Vars**. Убедитесь, что там есть `DATABASE_URL` или добавьте свои:
   - `DB_URL`: скопируйте URL из `DATABASE_URL` заменив `postgres://` на `jdbc:postgresql://` (как в application.properties).
   - `DB_USERNAME`: ваш логин из деталей базы данных Heroku
   - `DB_PASSWORD`: ваш пароль из деталей базы
   *(Дополнительно можно переопределить `SPRING_DATASOURCE_URL`, Heroku поддерживает это изначально).*
5. Перейдите на вкладку **Deploy**.
6. В разделе `Deployment method` выберите **GitHub** и привяжите ваш репозиторий.
7. Прокрутите вниз до **Manual deploy** (или включите Automatic) и нажмите `Deploy Branch` (main). Heroku сам запустит Gradle, скачает зависимости, соберет `.jar` и запустит сервер, переопределив порт через переменную окружения `PORT`.
8. После успешного деплоя скопируйте базовый URL вашего бэкенда (например: `https://parking-api.herokuapp.com/api`).

## 2. Развертывание Frontend (Vercel)
Vercel отлично подходит для быстрой публикации React-приложений:
1. Откройте [Vercel](https://vercel.com/) и войдите через свой GitHub аккаунт.
2. Нажмите **Add New...** -> **Project**.
3. Выберите репозиторий `parking2` из списка и нажмите `Import`.
4. В настройках проекта:
   - В поле **Framework Preset** выберите **Vite**.
   - В поле **Root Directory** нажмите Edit и выберите папку `frontend`.
5. Откройте секцию **Environment Variables**:
   - Name: `VITE_API_URL`
   - Value: URL вашего бэкенда на Heroku, который вы получили ранее (например: `https://parking-api.herokuapp.com/api`)
6. Нажмите **Deploy**.

Готово! После завершения сборки Vercel выдаст вам публичную ссылку на фронтенд вашего приложения. Фронтенд будет взаимодействовать с базой данных через бэкенд на Heroku.
