# Инструкция по развертыванию приложения

В этом проекте используется раздельное развертывание:
- **Backend (Spring Boot)** размещается на **Railway**.
- **Frontend (React/Vite)** размещается на **Vercel**.

## 1. Развертывание Backend (Railway)
Проект настроен для нативной сборки Spring Boot приложения в Railway:
1. Зарегистрируйтесь на [Railway.app](https://railway.app) и авторизуйтесь через GitHub.
2. Создайте новый проект: нажмите `New Project` -> `Provision PostgreSQL`. Railway создаст для вас бесплатную базу данных.
3. Добавьте ваш бэкенд в этот же проект: нажмите `New` -> `GitHub Repo` -> выберите ваш репозиторий `parking2`.
4. Свяжите бэкенд с базой данных:
   - Перейдите в настройки созданного сервиса (parking2) -> вкладка **Variables**.
   - Нажмите `New Variable` и добавьте переменную `DB_URL`. В качестве значения введите: `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}`
   - Добавьте `DB_USERNAME` со значением `${PGUSER}`
   - Добавьте `DB_PASSWORD` со значением `${PGPASSWORD}`
   *Railway автоматически подставит правильные значения из созданной PostgreSQL.*
5. Перейдите на вкладку **Settings** -> **Generals** вашего бэкенда и в разделе `Environment` -> `Domains` нажмите **Generate Domain**.
6. Railway подхватит ваш проект, самостоятельно распознает, что это Java (через Nixpacks или ваш Dockerfile), соберет `.jar` и запустит сервер, передав нужный порт.
7. После деплоя скопируйте сгенерированный домен (например: `https://parking2-production.up.railway.app`). Не забудьте в дальнейшем прибавлять к нему `/api` при запросах.

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
   - Value: URL вашего бэкенда на Railway, который вы получили ранее (например: `https://parking2-production.up.railway.app/api`)
6. Нажмите **Deploy**.

Готово! После завершения сборки Vercel выдаст вам публичную ссылку на фронтенд вашего приложения. Фронтенд будет взаимодействовать с базой данных через бэкенд на Railway.
