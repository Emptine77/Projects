FROM openjdk:17-jdk

WORKDIR /app
EXPOSE 8090

COPY api-gateway/target/*.jar movieplex.jar

CMD ["java", "-jar", "movieplex.jar"]