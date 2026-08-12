# Multi-stage build for Spring Boot + Docker Swarm
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget \
    && addgroup -S spring \
    && adduser -S spring -G spring
COPY --from=build /workspace/target/spring-mvc-0.0.1-SNAPSHOT.jar app.jar
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN sed -i 's/\r$//' /app/docker-entrypoint.sh \
    && chmod +x /app/docker-entrypoint.sh \
    && chown -R spring:spring /app
USER spring
EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
