# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: runtime (más ligera)
FROM eclipse-temurin:21-jre
WORKDIR /app

# (Toque Pro) Crear un usuario sin privilegios por seguridad
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar
EXPOSE 5000

# Limitar memoria y optimizar JVM
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]