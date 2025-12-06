# ------------ Stage 1: Build ------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

RUN apt-get update  \
    && apt-get install -y maven

COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY ./src ./src
RUN mvn package -DskipTests -B

# ------------ Stage 2: Run ------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]

