# Etapa 1: Construir el JAR
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar el JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/CrediDalpBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
