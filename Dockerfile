# Usa una imagen ligera con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado por Spring Boot
COPY target/CrediDalpBackend-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por donde correrá tu backend
EXPOSE 8080

# Comando que inicia tu app
ENTRYPOINT ["java", "-jar", "app.jar"]
