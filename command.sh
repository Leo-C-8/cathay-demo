#!/bin/bash

echo "請選擇操作模式："
echo "0) 建立快取後推送"
echo "1) Build"
echo "2) Run"
echo "3) Stop"
read -p "輸入選項 (1、2 或 3): " mode
if [ "$mode" == "0" ]; then
  echo "正在快取Maven..."
  docker buildx create --use
  docker buildx build --platform linux/amd64 -f bom-cache-dockerfile -t asia-east1-docker.pkg.dev/golden-medium-471501-p8/maven-images/maven-base:bom-cached --push .
  echo "Maven快取完成 ✅"

  echo "正在快取JDK...."
  docker pull --platform=linux/amd64 openjdk:17-jdk-slim
  docker tag openjdk:17-jdk-slim asia-east1-docker.pkg.dev/golden-medium-471501-p8/maven-images/openjdk-slim
  docker push asia-east1-docker.pkg.dev/golden-medium-471501-p8/maven-images/openjdk-slim
  echo "JDK快取完成 ✅"

elif [ "$mode" == "1" ]; then
  echo "正在建構 Docker 映像..."
  docker build -t account-service -f account/Dockerfile .
  docker build -t image-service -f image/Dockerfile .
  echo "建構完成 ✅"

elif [ "$mode" == "2" ]; then
  echo "啟動 Account Service..."
  docker run -d --name account-service \
    -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json \
    -v ~/.config/gcloud:/root/.config/gcloud \
    -p 8080:8080 account-service

  echo "啟動 Image Service..."
  docker run -d --name image-service \
    -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json \
    -v ~/.config/gcloud:/root/.config/gcloud \
    -p 8081:8080 image-service

elif [ "$mode" == "3" ]; then
  echo "停止 Account Service..."
  docker stop account-service && docker rm account-service

  echo "停止 Image Service..."
  docker stop image-service && docker rm image-service

else
  echo "無效的選項 ❌"
fi
