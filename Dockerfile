# ── Stage 1: deps (cache de dependências) ──
FROM maven:3.9.6-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# ── Stage 2: build ──
FROM deps AS build
COPY src ./src
RUN mvn -B -DskipTests package

# ── Stage 3: runtime otimizado ──
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-XX:+UseG1GC","-jar","app.jar"]
