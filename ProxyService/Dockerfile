FROM maven:3.9.9-eclipse-temurin-23 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

ENTRYPOINT ["top", "-b"]

FROM eclipse-temurin:23-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/ProxyService-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
EXPOSE 50050

ENTRYPOINT ["java", "-jar", "app.jar"]