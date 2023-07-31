FROM maven:3.9.3-eclipse-temurin-11-alpine as builder
LABEL authors="Developer Relations"
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn clean -e -B package

#FROM openjdk:11-jre-slim-bullseye
FROM openjdk:17.0.2-slim-bullseye
WORKDIR /app
COPY --from=builder /app/target/RTO_Kotlin-1.0-jar-with-dependencies.jar .
COPY EmaConfig.xml .

# run Consumer-1.0-jar-with-dependencies.jar with CMD
CMD ["java", "-jar", "./RTO_Kotlin-1.0-jar-with-dependencies.jar"]