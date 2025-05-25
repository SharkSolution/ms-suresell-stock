# Etapa 1: Build del proyecto con Gradle en modo producción
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -Dquarkus.package.type=fast-jar

# Etapa 2: Imagen final para ejecución
FROM eclipse-temurin:17-jre
WORKDIR /work

COPY --from=build /app/build/quarkus-app/lib/ /work/lib/
COPY --from=build /app/build/quarkus-app/*.jar /work/
COPY --from=build /app/build/quarkus-app/app/ /work/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]

