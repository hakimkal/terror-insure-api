# Use a Maven image with Java 11 for the build stage
FROM maven:3.8.4-openjdk-11 AS builder

# Set the working directory
WORKDIR /app

# Copy the parent project's POM file (pom.xml) to download dependencies
COPY pom.xml .

# Copy the child module POM files
COPY data/pom.xml data/
COPY security/pom.xml security/
COPY service/pom.xml service/
COPY web/pom.xml web/

# Build the project with dependencies
RUN mvn clean install -DskipTests

# Second stage: Create the final image with Java 11
FROM adoptopenjdk/openjdk11:alpine-jre

# Set the working directory
WORKDIR /app

# Copy the JAR files from the child modules
COPY --from=builder /app/web/target/*.jar app.jar

# Specify the command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
