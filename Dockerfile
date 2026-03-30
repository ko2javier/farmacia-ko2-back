# Etapa 1: Compilar el código usando Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Coger el .jar generado y ejecutarlo
FROM eclipse-temurin:21-jdk
COPY --from=build target/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]