# ===== STAGE 1: BUILD =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy file Maven wrapper / pom trước để cache dependency
COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN ./mvnw -q dependency:go-offline

# Copy source code
COPY src src

# Build jar
RUN ./mvnw -q package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy file jar đã build
COPY --from=build /app/target/*.jar app.jar

# Render sẽ set ENV PORT, trong Spring đã dùng server.port=${PORT:8080}
ENV JAVA_OPTS=""

# Expose cho rõ (Render vẫn dùng PORT riêng, nhưng để 8080 cho local)
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
