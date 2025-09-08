# ===== Build Stage =====
FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app

# 複製 pom.xml 和 source code
COPY . .

# 建立 Spring Boot 應用，產出 JAR（跳過測試加快速度）
RUN mvn clean package -Dmaven.test.skip=true

# ===== Run Stage =====
FROM openjdk:17-jdk-slim

WORKDIR /app

# 拷貝 fat JAR（只取編譯後的 jar）
COPY --from=builder /app/target/*.jar app.jar

# 設定 Cloud Run 需要的 PORT
ENV PORT=8080

# 啟動 Spring Boot 應用
ENTRYPOINT ["java", "-jar", "app.jar"]
