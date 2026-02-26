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
REST API
Доступные запросы
Метод	URL	Описание	Пример
GET	/api/slots	Получение всех мест	/api/slots
GET	/api/slots	Фильтрация по статусу	/api/slots?occupied=true
GET	/api/slots/{id}	Получение места по ID	/api/slots/1
GET	/api/slots/type/{type}	Получение по типу	/api/slots/type/REGULAR
Пример ответа
json

{
  "id": 1,
  "number": "A-101",
  "status": "Available",
  "slotType": "REGULAR",
  "additionalInfo": "Covered"
}

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

https://sonarcloud.io/images/project_badges/sonarcloud-black.svg
