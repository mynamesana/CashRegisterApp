# Use a base image with JRE (Java Runtime Environment) pre-installed
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the host to the container
COPY target/your-application.jar /app/

# Expose the port your application listens on
EXPOSE 8082

# Command to run your application when the container starts
CMD ["java", "-jar", "your-application.jar"]
