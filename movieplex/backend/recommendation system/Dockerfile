FROM openjdk:17-jdk

WORKDIR /app
EXPOSE 8090

COPY target/*.jar recommendation-system.jar

CMD ["java", "-jar", "recommendation-system.jar"]