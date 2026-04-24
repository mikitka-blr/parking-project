# Нагрузочное тестирование с помощью Apache JMeter

Для проведения нагрузочного тестирования выполните следующие шаги в программе JMeter:

## 1. Настройка Test Plan
1. Откройте JMeter.
2. Кликните правой кнопкой мыши на `Test Plan` -> `Add` -> `Threads (Users)` -> `Thread Group`.
3. В параметрах **Thread Group**:
   - `Number of Threads (users)`: 50 (Количество потоков)
   - `Ramp-up period (seconds)`: 1 (Время "разогрева" потоков)
   - `Loop Count`: 100 (Количество повторений)
   
*Это означает, что JMeter создаст 50 одновременных потоков. Каждый из них 100 раз обратится к вашему API.*

## 2. Добавление HTTP Request
1. Правой кнопкой на `Thread Group` -> `Add` -> `Sampler` -> `HTTP Request`.
2. В параметрах:
   - `Protocol`: http
   - `Server Name or IP`: localhost
   - `Port Number`: 8080
   - `HTTP Request`: POST
   - `Path`: `/api/async/race-condition?increments=100`

## 3. Добавление View Results Tree и Summary Report (Слушатели)
1. Правой кнопкой на `Thread Group` -> `Add` -> `Listener` -> `View Results Tree` (Показывает подробности каждого запроса).
2. Правой кнопкой на `Thread Group` -> `Add` -> `Listener` -> `Summary Report` (Показывает статистику, сколько упало, сколько успело, пропускную способность).

## 4. Запуск теста
1. Нажмите зелёную кнопку Play на панели сверху.
2. Дождитесь окончания выполнения в правом верхнем углу (циферки остановятся).
3. Перейдите в `Summary Report`.

## Пояснение к лабе:
- Вы покажете как `unsafeCounterResult` отстаёт от `expected` из-за состояния гонки (**Race Condition**).
- А `safeCounterResult` (с использованием `AtomicInteger`) всегда равен `expected`, так как он является потокобезопасным!
- Асинхронная операция возвращает ID (загляните в View Results Tree для эндпоинта `/api/async/bulk`), и по этому ID можно проверить статус `SUBMITTED`, затем `IN_PROGRESS`, затем `COMPLETED` через `/api/async/bulk/{taskId}`.
