FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -q
COPY src src
RUN ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
