# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create data directory for H2 database
RUN mkdir -p data

# Expose port (will be overridden by Render's PORT environment variable)
EXPOSE 8080

# Run the application with production profile
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "target/notebook-business-0.0.1-SNAPSHOT.jar"] 