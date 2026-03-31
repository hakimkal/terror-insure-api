# Multi-stage build for cloud deployment
# Stage 1: Build the application
FROM maven:3.8-eclipse-temurin-11-alpine AS builder

WORKDIR /build

# Copy pom files first for better caching
COPY pom.xml .
COPY data/pom.xml data/
COPY security/pom.xml security/
COPY service/pom.xml service/
COPY web/pom.xml web/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Force cache invalidation for source code
ARG CACHEBUST=1
# Copy source code
COPY data/src data/src
COPY security/src security/src
COPY service/src service/src
COPY web/src web/src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:11-jre

# Add a non-root user for security
RUN groupadd -r terron && useradd -r -g terron terron

WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /build/web/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R terron:terron /app

# Switch to non-root user
USER terron

# Expose the application port
EXPOSE 8000

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
