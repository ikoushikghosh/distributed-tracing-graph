# Build stage
ARG FILENAME=input.txt
FROM maven:3.8.2-eclipse-temurin-17 AS maven
MAINTAINER Koushik Ghosh
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn dependency:go-offline -B
RUN mvn clean
RUN mvn package

# Package stage
FROM eclipse-temurin:17
WORKDIR /koushik/distributed-tracing
COPY ./${FILENAME} ./${FILENAME}
COPY --from=maven target/distributed-tracing-test.jar ./distributed-tracing-test.jar
CMD ["java", "-jar", "./distributed-tracing-test.jar"]