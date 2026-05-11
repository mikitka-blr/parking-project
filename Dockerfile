# Этап сборки (Builder)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Даем права на исполнение gradlew
RUN chmod +x ./gradlew

# Собираем проект без запуска тестов
RUN ./gradlew bootJar -x test

# Возвращаем готовый jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Копируем собранный jar-файл из предыдущего этапа
COPY --from=builder /app/build/libs/*.jar app.jar

# Указываем порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

