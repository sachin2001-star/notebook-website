# Multi-stage build for better performance
FROM maven:3.9.6-openjdk-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Create data directory for H2 database
RUN mkdir -p data

# Copy the built JAR from build stage
COPY --from=build /app/target/notebook-business-0.0.1-SNAPSHOT.jar app.jar

# Expose port (will be overridden by Render's PORT environment variable)
EXPOSE 8080

# Run the application with production profile
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"] 