
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw -B verify

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/jpa-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]