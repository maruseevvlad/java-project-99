FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src



# Права на gradlew (в корне) и на code/gradlew (если он есть)
RUN chmod +x gradlew \
 && sed -i 's/\r$//' gradlew \
 && if [ -f /build/code/gradlew ]; then chmod +x /build/code/gradlew && sed -i 's/\r$//' /build/code/gradlew; fi

RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN mkdir -p /home/appuser/.gradle && chown -R appuser:appuser /home/appuser
RUN ./gradlew --version --no-daemon
RUN chown -R appuser:appuser /build
USER appuser

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre

RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /build

COPY --from=builder /build/build/libs/app-0.0.1-SNAPSHOT.jar .

RUN chmod 644 app-0.0.1-SNAPSHOT.jar && chown appuser:appuser app-0.0.1-SNAPSHOT.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=production"]
