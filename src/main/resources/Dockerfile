# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom & source vào image
COPY pom.xml .
COPY src ./src

# Build jar, bỏ test cho nhanh
RUN mvn -q -DskipTests package

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy file jar đã build từ stage 1
COPY --from=build /app/target/*.jar app.jar

# Render sẽ set PORT, còn trong app bạn đã có server.port=${PORT:8080}
ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

