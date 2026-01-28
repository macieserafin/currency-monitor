FROM eclipse-temurin:21-jre

WORKDIR /app

COPY currency-monitor-app/target/*SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]