#!/bin/bash

echo "請選擇操作模式："
echo "1) Build"
echo "2) Run"
read -p "輸入選項 (1 或 2): " mode

if [ "$mode" == "1" ]; then
  echo "正在建構 Docker 映像..."
  docker build -t account-service -f account/Dockerfile .
  docker build -t image-service -f image/Dockerfile .
  echo "建構完成 ✅"

elif [ "$mode" == "2" ]; then
  echo "請選擇要執行的服務："
  echo "1) Account Service"
  echo "2) Image Service"
  read -p "輸入選項 (1 或 2): " service

  if [ "$service" == "1" ]; then
    echo "啟動 Account Service..."
    docker run -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json \
      -v ~/.config/gcloud:/root/.config/gcloud -p 8080:8080 account-service
  elif [ "$service" == "2" ]; then
    echo "啟動 Image Service..."
    docker run -e GOOGLE_APPLICATION_CREDENTIALS=/root/.config/gcloud/application_default_credentials.json \
      -v ~/.config/gcloud:/root/.config/gcloud -p 8080:8080 image-service
  else
    echo "無效的選項 ❌"
  fi

else
  echo "無效的選項 ❌"
fi
