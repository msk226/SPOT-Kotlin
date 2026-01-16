# 멀티스테이지 빌드
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY . .

ARG MODULE
# core/boot -> core:boot 형태로 변환하여 Gradle 명령어에 사용
RUN MODULE_GRADLE=$(echo ${MODULE} | tr '/' ':') && \
    ./gradlew :${MODULE_GRADLE}:bootJar --no-daemon

# 런타임
FROM eclipse-temurin:21-jre

WORKDIR /app
ARG MODULE
COPY --from=builder /app/${MODULE}/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
