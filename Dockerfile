FROM maven:3.9.3-eclipse-temurin-11-alpine as builder
LABEL authors="Developer Relations"
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean -e -B package

#FROM openjdk:11-jre-slim-bullseye
#FROM openjdk:17.0.2-slim-bullseye
FROM --platform=linux/amd64 eclipse-temurin:11-jre-alpine
#FROM --platform=linux/amd64 amazoncorretto:11-alpine3.19
WORKDIR /app
COPY --from=builder /app/target/RTO_Kotlin-1.0-jar-with-dependencies.jar .
COPY EmaConfig.xml .

# run RTO_Kotlin-1.0-jar-with-dependencies.jar with ENTRYPOINT
ENTRYPOINT ["java", "-jar", "./RTO_Kotlin-1.0-jar-with-dependencies.jar"]