# ===== Build Stage =====
# 從GCP Artifact Registry 拉取
FROM asia-east1-docker.pkg.dev/golden-medium-471501-p8/maven-images/maven-base:bom-cached AS builder
WORKDIR /app

COPY . .

RUN pwd
RUN ls -l /app

# 預先將 security 模組安裝到本地的 Maven repository
RUN mvn install -pl security -am -Dmaven.test.skip=true

WORKDIR /app/account
RUN mvn clean package -Dmaven.test.skip=true

# ===== Run Stage =====
FROM asia-east1-docker.pkg.dev/golden-medium-471501-p8/maven-images/openjdk-slim
WORKDIR /app

COPY --from=builder /app/account/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
