FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY currency-monitor-core ./currency-monitor-core
COPY currency-monitor-persistence ./currency-monitor-persistence
COPY currency-monitor-nbp-client ./currency-monitor-nbp-client
COPY currency-monitor-app ./currency-monitor-app
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system app && useradd --system --gid app --create-home app
WORKDIR /app
COPY --from=build /app/currency-monitor-app/target/currency-monitor-app-*.jar app.jar
RUN chown app:app /app
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
