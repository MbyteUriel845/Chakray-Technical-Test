# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the Maven files
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR built from the previous step
COPY --from=build /app/target/technical-test-0.0.1-SNAPSHOT.jar app.jar

# Exposes the port on which your application runs
EXPOSE 8080

# Command to run the JAR
ENTRYPOINT ["java","-jar","app.jar"]
