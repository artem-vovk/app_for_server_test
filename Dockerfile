FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/jsf-test-0.0.1-SNAPSHOT.jar app.jar

#it is point for data, it needs to use during deployment - docker run -d -v /path/on/host:/app/data your_image_name 
#or in jenkins during deployment
VOLUME ["/app/data"] 

ENTRYPOINT ["java", "-jar", "app.jar"]
