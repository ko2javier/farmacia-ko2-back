FROM eclipse-temurin:21-jdk
COPY app.jar app.jar
# Exponemos el 5000 (informativo)
EXPOSE 5000
# Arrancamos normal, SIN forzar puerto (usará el de tu properties)
ENTRYPOINT ["java", "-jar", "app.jar"]