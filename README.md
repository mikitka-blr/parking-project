# Система управления парковкой

## Описание
REST API для управления парковочными местами. Поддерживаются три типа мест:
- **Обычные** — под крышей или на открытом воздухе
- **Электромобили** — с указанием мощности зарядки
- **Для инвалидов** — с широким въездом

## Выполненные требования

### 1. Spring Boot приложение
Создано Spring Boot приложение с точкой входа `Main.java`, аннотированной `@SpringBootApplication`.

### 2. REST API для ключевой сущности
Ключевая сущность — парковочное место (`BaseParkingSlot` и его наследники). Для неё реализованы все необходимые эндпоинты.

### 3. GET endpoints
- **С @RequestParam:** `/api/slots?occupied=true` — фильтрация мест по статусу занятости
- **С @PathVariable:** `/api/slots/{id}` — получение места по ID и `/api/slots/type/{type}` — получение по типу

### 4. Многослойная архитектура
Реализованы слои:
- **Controller** — обработка HTTP-запросов
- **Service** — бизнес-логика
- **Repository** — хранение данных (in-memory)

### 5. DTO и Mapper
- **DTO:** `ParkingSlotDTO` — объект для передачи данных клиенту
- **Mapper:** `ParkingMapper` — преобразование Entity в DTO

### 6. Checkstyle
Настроен Checkstyle с правилами SquareStyle, код приведён к единому стилю.

## Технологии
- Java 25
- Spring Boot
- Maven
- Checkstyle (SquareStyle)
- SonarCloud

## Запуск кода

Клонировать репозиторий
git clone https://github.com/mikitka-blr/parking-project.git

Перейти в папку проекта
cd parking-project

Запустить приложение
./mvnw spring-boot:run

Или открыть проект в IntelliJ IDEA и запустить Main.java.

После запуска сервер будет доступен по адресу:
http://localhost:8080

Доступные запросы

- GET	/api/slots	Получение всех мест	/api/slots
- GET	/api/slots	Фильтрация по статусу	/api/slots?occupied=true
- GET	/api/slots/{id}	Получение места по ID	/api/slots/1
- GET	/api/slots/type/{type}	Получение по типу	/api/slots/type/REGULAR

# Проверка работы

Получить все места
http://localhost:8080/api/slots

Получить только занятые
http://localhost:8080/api/slots?occupied=true

Получить место с ID=1
http://localhost:8080/api/slots/1

Получить обычные места
http://localhost:8080/api/slots/type/REGULAR

# Проверка стиля кода (Checkstyle)

mvn checkstyle:check

# Анализ кода с SonarCloud

[https://sonarcloud.io/images/project_badges/sonarcloud-black.svg](https://sonarcloud.io/project/overview?id=mikitka-blr_parking-project)

## Обновления: Реляционная БД и JPA

## CRUD операции
# CREATE (POST)

- POST http://localhost:8080/api/users

{

    "fullName": "Иван Петров",
    
    "email": "ivan@example.com",
    
    "phone": "+375-29-111-22-33"
    
}

# READ ALL (GET)

- GET http://localhost:8080/api/users

# PUT 
-  put http://localhost:8080/api/users/1

{

    "fullName": "Обновленный Иван",

    "email": "ivan.updated@example.com",
    
    "phone": "+375-29-999-88-77"
    
}

DELETE (DELETE)

- DELETE http://localhost:8080/api/users/1

## Демонстрация транзакций

# Проблема (без @Transactional)

- POST http://localhost:8080/api/demo/error

{

    "fullName": "Проблемный Тест",
    
    "email": "problem@example.com",
    
    "phone": "+375-29-777-77-77"
    
}

Ответ: ОШИБКА: пользователь сохранился, а парковка нет!

# Решение (с @Transactional)

- POST http://localhost:8080/api/demo/success

{

    "fullName": "Успешный Тест",
    
    "email": "success@example.com",
    
    "phone": "+375-29-888-88-88"
    
}

Ответ: УСПЕХ: всё сохранилось!

## Демонстрация N+1

# Проблема

- GET http://localhost:8080/api/demo/nplus1

В консоли: 1 + N SQL запросов

# Решение

- GET http://localhost:8080/api/demo/solution

В консоли: 1 SQL запрос с JOIN

## Проверка в pgAdmin

# Все таблицы
sql

SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

# Все пользователи
sql

SELECT id, full_name, email, phone, created_at FROM users ORDER BY id;

# Все парковки
sql

SELECT * FROM parking_lots;

# Все места с типами
sql

SELECT 
    ps.id,
    ps.number,
    ps.occupied,
    pl.name as parking_lot,
    CASE 
        WHEN rs.id IS NOT NULL THEN 'Regular' 
        WHEN es.id IS NOT NULL THEN 'Electric'
        WHEN ds.id IS NOT NULL THEN 'Disabled' 
    END as type
FROM parking_slots ps
LEFT JOIN parking_lots pl ON ps.parking_lot_id = pl.id
LEFT JOIN regular_slots rs ON ps.id = rs.id
LEFT JOIN electric_slots es ON ps.id = es.id
LEFT JOIN disabled_slots ds ON ps.id = ds.id
ORDER BY ps.id;

# Проверка ManyToMany
sql

SELECT 
    r.id as reservation_id,
    u.full_name as user_name,
    ps.number as slot_number,
    STRING_AGG(es.name, ', ') as services
FROM reservations r
JOIN users u ON r.user_id = u.id
JOIN parking_slots ps ON r.slot_id = ps.id
LEFT JOIN reservation_services rs ON r.id = rs.reservation_id
LEFT JOIN extra_services es ON rs.service_id = es.id
GROUP BY r.id, u.full_name, ps.number;

# Количество записей
sql

SELECT 'users' as table_name, COUNT(*) FROM users
UNION ALL SELECT 'parking_lots', COUNT(*) FROM parking_lots
UNION ALL SELECT 'parking_slots', COUNT(*) FROM parking_slots
UNION ALL SELECT 'reservations', COUNT(*) FROM reservations
UNION ALL SELECT 'extra_services', COUNT(*) FROM extra_services;

# Проверка после транзакций
- После проблемного запроса (без транзакции)

- SELECT * FROM users WHERE email = 'problem@example.com'; -- пользователь ЕСТЬ
- SELECT * FROM parking_lots WHERE name IS NULL; -- парковки НЕТ

# После успешного запроса (с транзакцией)

- SELECT * FROM users WHERE email = 'success@example.com'; -- пользователь ЕСТЬ
- SELECT * FROM parking_lots WHERE name = 'Центральная парковка'; -- парковка ЕСТЬ
