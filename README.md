Система управления парковкой
Описание

Проект представляет собой REST API для управления парковочными местами. В системе выделено три типа мест:

    Обычные (Regular) — места под крышей или на открытом воздухе

    С зарядкой для электромобилей (Electric) — с указанием мощности зарядки

    Для людей с ограниченными возможностями (Disabled) — с широким въездом

Реализованный функционал
1. Архитектура приложения

    Многослойная архитектура: Controller → Service → Repository

    Модель данных: абстрактный класс BaseParkingSlot и наследование для разных типов мест (полиморфизм)

    DTO и Mapper: преобразование сущностей в ответы API через ParkingMapper

2. REST API эндпоинты
   
Метод	URL	Описание	Параметры

GET	/api/slots	Получение всех мест	?occupied=true/false 

GET	/api/slots/{id}	Получение места по ID	id

GET	/api/slots/type/{type}	Фильтрация по типу места	type (REGULAR/ELECTRIC/DISABLED)

4. Примеры запросов
text

GET /api/slots

GET /api/slots?occupied=

GET /api/slots/1

GET /api/slots/type/REGULAR

4. Пример ответа
json

{
  "id": 1,
  "number": "A-101",
  "status": "Available",
  "slotType": "REGULAR",
  "additionalInfo": "Covered"
}

Технологии

    Java 15

    Spring Boot — фреймворк для создания REST API

    Maven — сборка проекта

    Checkstyle — проверка стиля кода (SquareStyle)

Запуск проекта

    Клонировать репозиторий:

bash

git clone https://github.com/mikitka-blr/parking-project.git

    Открыть в IntelliJ IDEA

    Запустить Main.java

    Открыть в браузере: http://localhost:8080/api/slots

Проверка стиля кода
bash

mvn checkstyle:check
