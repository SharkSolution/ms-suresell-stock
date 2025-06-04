# Etapa final: solo empaquetar lo generado en /build
FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-jre
WORKDIR /work
COPY build/quarkus-app/lib/ /work/lib/
COPY build/quarkus-app/*.jar /work/
COPY build/quarkus-app/app/ /work/app/
COPY build/quarkus-app/quarkus/ /work/quarkus/
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]
