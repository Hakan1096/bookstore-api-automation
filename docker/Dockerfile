# Specify the base image
FROM openjdk:11-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy application files into the container
COPY . /app

# Install dependencies (if needed)
RUN apt-get update && apt-get install -y maven && mvn clean install -DskipTests

# Expose the application port
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "target/bookstore-api.jar"]