# ===== Build Stage =====
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app

# 複製整個專案目錄 (從專案根目錄複製)
COPY . .
RUN pwd
RUN ls -l /app

# 預先安裝 security 模組到本地 Maven Repository
RUN mvn clean install -pl security -am

# 打包 image 服務
WORKDIR /app/image
RUN mvn clean package -Dmaven.test.skip=true

# ===== Run Stage =====
FROM openjdk:17-jdk-slim
WORKDIR /app

# 拷貝編譯好的 JAR 檔案
COPY --from=builder /app/image/target/*.jar app.jar

ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# docker run -e PORT=8080 -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json -v ~/.config/gcloud:/root/.config/gcloud -p 8080:8080 image-service