# ===== Стадия 1: сборка =====
FROM eclipse-temurin:21-jdk-alpine AS build

# Устанавливаем bash и unzip для Gradle
RUN apk add --no-cache bash unzip

WORKDIR /app

# Копируем Gradle Wrapper и файлы сборки
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Делаем gradlew исполняемым и скачиваем зависимости
RUN chmod +x gradlew && ./gradlew build --no-daemon --dry-run

# Копируем весь проект и собираем jar (без тестов для ускорения)
COPY . .
RUN ./gradlew build --no-daemon -x test

# ===== Стадия 2: минимальный финальный образ =====
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копируем только готовый jar из стадии сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Порт, который слушает Spring Boot
EXPOSE 8080

# Команда запуска
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
