# ===== Build Stage =====
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app

# 複製整個專案目錄
# 這裡的 . 代表 Docker Build Context 的根目錄
COPY . .
RUN pwd
RUN ls -l /app
# 預先將 security 模組安裝到本地的 Maven repository
RUN mvn clean install -pl security -am

# 進入 account 模組目錄，進行打包
WORKDIR /app/account
RUN mvn clean package -Dmaven.test.skip=true

# ===== Run Stage =====
FROM openjdk:17-jdk-slim
WORKDIR /app

# 從 builder 階段複製 fat JAR
COPY --from=builder /app/account/target/*.jar app.jar

ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# docker run -e PORT=8080 -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json -v ~/.config/gcloud:/root/.config/gcloud -p 8080:8080 account-service