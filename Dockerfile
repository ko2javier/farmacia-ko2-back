FROM openjdk:21-jdk-slim
COPY app.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]