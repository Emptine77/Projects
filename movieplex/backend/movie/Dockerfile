FROM openjdk:17-jdk

WORKDIR /app
EXPOSE 8082

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]