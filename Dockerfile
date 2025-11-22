FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/sentiment-service.jar /app/sentiment-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/sentiment-service.jar"]
