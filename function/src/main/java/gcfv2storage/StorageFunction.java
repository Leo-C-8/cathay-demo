package gcfv2storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.cloud.storage.*;
import io.cloudevents.CloudEvent;
import okhttp3.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Cloud Function 處理 Cloud Storage Object Finalize 事件。
 * 負責下載原始圖片，壓縮，上傳縮圖，並通知完成 API。
 */
public class StorageFunction implements CloudEventsFunction {

    private static final Logger logger = Logger.getLogger(StorageFunction.class.getName());

    private static final String COMPLETED_API_ENDPOINT =
            "https://cathay-demo-image-191169836402.asia-east1.run.app/images/completed";

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    // 圖片壓縮比率
    private static final double COMPRESSION_PERCENTAGE = 0.5;

    @Override
    public void accept(CloudEvent event) {
        String pathName = null;
        String bucketName = null;

        // 1. 錯誤處理：捕捉整個處理過程中的所有異常
        try {
            // 解析 Cloud Event 數據
            String eventString = new String(event.getData().toBytes());
            logger.info("Cloud Event data: " + eventString);

            // 使用 Jackson 解析事件數據，獲取檔案名和桶名
            Map<String, String> map = jsonMapper.readValue(eventString, Map.class);
            pathName = map.get("name");
            bucketName = map.get("bucket");

            if (pathName == null || bucketName == null) {
                logger.severe("Missing 'pathName' or 'bucket' in CloudEvent data.");
                return;
            }

            if (pathName.contains("thumbnail")) {
                logger.info("Is Thumbnail File, Skip");
                return;
            }

            // 2. 處理路徑和 URI
            String sourceUri = "gs://" + bucketName + "/" + pathName;
            String targetUri = sourceUri.replace("original", "thumbnail");

            logger.info("sourceUri = " + sourceUri);
            logger.info("targetUri = " + targetUri);

            // 3. 下載、壓縮、上傳
            byte[] originalFile = download(sourceUri);
            byte[] compressFile = compress(originalFile, COMPRESSION_PERCENTAGE);
            upload(compressFile, targetUri);

            // 4. 通知完成 API
            callCompletedApi(pathName.substring(pathName.lastIndexOf("/") + 1), compressFile.length);

            logger.info(String.format("Successfully completed process for file: %s", pathName));

        } catch (Exception e) {
            // 如果處理失敗，記錄詳細錯誤訊息
            logger.severe(String.format("Processing failed for file %s in bucket %s. Error: %s",
                    pathName, bucketName, e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 壓縮圖片到指定百分比。
     */
    private byte[] compress(byte[] source, double compressionPercentage) throws IOException {
        BufferedImage inputImage;

        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(source)) {
            inputImage = ImageIO.read(byteInputStream);
        }

        if (inputImage == null) {
            throw new IOException("Unable to read image data for compression.");
        }

        // 計算壓縮後的寬度和高度
        int targetWidth = (int) (inputImage.getWidth() * compressionPercentage);
        int targetHeight = (int) (inputImage.getHeight() * compressionPercentage);

        // 壓縮圖片（調整大小）
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = outputImage.createGraphics();
        // 使用更高質量的渲染提示 (Rendering Hints)
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(inputImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        // 將壓縮後的圖片轉換為 byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(outputImage, "jpg", baos);
            baos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * 從 Cloud Storage 下載檔案。
     */
    private byte[] download(String storageUri) {
        logger.info("Downloading from: " + storageUri);
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.fromGsUtilUri(storageUri);

        // 檢查檔案是否存在
        Blob blob = storage.get(blobId);
        if (blob == null) {
            throw new RuntimeException("File not found at: " + storageUri);
        }

        return blob.getContent();
    }


    /**
     * 上傳檔案到 Cloud Storage。
     */
    private void upload(byte[] file, String storageUri) {
        logger.info("Uploading to: " + storageUri);
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId id = BlobId.fromGsUtilUri(storageUri);
        BlobInfo info = BlobInfo.newBuilder(id).setContentType("image/jpeg").build();
        storage.create(info, file);
    }

    /**
     * 呼叫遠端 API，通知圖片處理完成。
     */
    private void callCompletedApi(String fileName, long fileSize) throws IOException {
        EventarcPayloadDto payload = new EventarcPayloadDto(fileName, fileSize);

        // 將 Payload 轉換為 JSON
        RequestBody body = RequestBody.create(
                jsonMapper.writeValueAsString(payload),
                MediaType.get("application/json; charset=utf-8")
        );

        logger.info("ENDPOINT: " + COMPLETED_API_ENDPOINT);

        Request request = new Request.Builder()
                .url(COMPLETED_API_ENDPOINT)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                logger.severe(String.format("Failed to notify API for %s. Code: %d, Response: %s",
                        fileName, response.code(), responseBody));
                logger.severe("Failed to notify completion API. HTTP status: " + response.code());
            }
            logger.info("Completion API notified successfully.");
        }
    }

    /**
     * Cloud Run/GKE API 服務所需的 DTO
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventarcPayloadDto {

        @JsonProperty("fileName")
        private String fileName;

        @JsonProperty("fileSize")
        private long fileSize;

        public EventarcPayloadDto() {
        }

        public EventarcPayloadDto(String fileName, long fileSize) {
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        public String getFileName() {
            return fileName;
        }

        public long getFileSize() {
            return fileSize;
        }
    }
}