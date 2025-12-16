# Etapa 1: Build
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

# Copiar TODO el proyecto
COPY . .

# Dar permisos a gradlew
RUN chmod +x gradlew

# Compilar Quarkus en modo JVM (uber-jar)
RUN ./gradlew build -Dquarkus.package.type=uber-jar --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR de Quarkus (runner.jar)
COPY --from=builder /app/build/*-runner.jar app.jar

EXPOSE 8280

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]