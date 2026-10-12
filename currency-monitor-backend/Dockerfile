FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY currency-monitor-core ./currency-monitor-core
COPY currency-monitor-persistence ./currency-monitor-persistence
COPY currency-monitor-nbp-client ./currency-monitor-nbp-client
COPY currency-monitor-app ./currency-monitor-app
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/currency-monitor-app/target/currency-monitor-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
