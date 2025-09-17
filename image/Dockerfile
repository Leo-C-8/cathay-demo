# ===== Build Stage =====
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app

COPY . .
RUN pwd
RUN ls -l /app

# 預先安裝 security 模組到本地 Maven Repository
RUN mvn clean install -pl security -am

WORKDIR /app/image
RUN mvn clean package -Dmaven.test.skip=true

# ===== Run Stage =====
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/image/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]