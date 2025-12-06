# ------------ Stage 1: Build ------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY ./src ./src
COPY ./pom.xml .

COPY ./mvnw .
COPY ./.mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw -DskipTests package

# ------------ Stage 2: Run ------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]

