# Etapa 1: Construcción del JAR
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

COPY . .

# Asegúrate que gradlew tenga permisos de ejecución
RUN chmod +x ./gradlew \
    && ./gradlew build -Dquarkus.package.type=fast-jar -x test

# Etapa 2: Imagen final optimizada
FROM eclipse-temurin:17-jre
WORKDIR /work

COPY --from=build /app/build/quarkus-app/lib/ /work/lib/
COPY --from=build /app/build/quarkus-app/*.jar /work/
COPY --from=build /app/build/quarkus-app/app/ /work/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /work/quarkus/

ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]
