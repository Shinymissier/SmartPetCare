# Stage 1: Build application WAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build WAR package
RUN mvn clean package -DskipTests

# Stage 2: Deploy to Apache Tomcat 9
FROM tomcat:9.0-jdk17-corretto

# Remove default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy generated WAR file as ROOT.war so app runs at the root path (/)
COPY --from=builder /build/target/petcare.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
