package com.leo.cathay.image.service;

import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.model.ImageInfo;
import com.leo.cathay.image.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageService {

    private final Path uploadDir = Paths.get("uploads");
    private final Path thumbnailDir = Paths.get("thumbnails");
    // 使用 ConcurrentHashMap 模擬資料庫儲存檔案資訊
    private final Map<String, ImageInfo> fileInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private FileInfoRepository fileInfoRepository;

    public ImageService() throws IOException {
        // 確保上傳和縮圖資料夾存在
        Files.createDirectories(uploadDir);
        Files.createDirectories(thumbnailDir);
    }

    /**
     * 處理圖片上傳並儲存到本地
     *
     * @param file 使用者上傳的圖片檔案
     * @return 包含檔案資訊的 FileInfo 物件
     * @throws IOException 如果檔案儲存失敗
     */
    public ImageInfo uploadFile(MultipartFile file) throws IOException {
        System.out.println("[uploadFile] file = " + file);

        // 取得使用者桌面上的 test 資料夾路徑
        Path desktopTestDir = Paths.get(System.getProperty("user.home"), "Desktop", "test");

        // 確保目錄存在
        Files.createDirectories(desktopTestDir);

        // 處理檔名（移除空白與特殊字元）
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IOException("檔案名稱無效");
        }
        String safeFileName = originalFileName.replaceAll("\\s+", "_");

        // 建立儲存路徑
        Path filePath = desktopTestDir.resolve(safeFileName);

        // 儲存檔案
        file.transferTo(filePath.toFile());

        // 建立 ImageInfo（記憶體用）
        ImageInfo imageInfo = new ImageInfo(safeFileName, file.getSize());
        fileInfoMap.put(safeFileName, imageInfo);

        // 建立 FileInfo（資料庫用）
        FileInfo fileInfoEntity = FileInfo.builder()
                .fileName(safeFileName)
                .uploadDate(imageInfo.getUploadDate())
                .thumbnailStatus(ImageInfo.ThumbnailStatus.PROCESSING)
                .thumbnailDownloadLi(null)
                .build();

        fileInfoRepository.save(fileInfoEntity); // 寫入資料庫

        // 模擬異步縮圖作業
        processThumbnail(safeFileName);

        return imageInfo;
    }


    /**
     * 模擬縮圖作業，將檔案狀態從 "processing" 更新為 "completed"
     *
     * @param fileName 檔案名稱
     */
    private void processThumbnail(String fileName) {
        System.out.println("fileName = " + fileName);
        // 實際應用中，這段程式碼會啟動一個異步任務來進行縮圖
        // 這裡我們只是延遲一段時間來模擬作業
        new Thread(() -> {
            try {
                // 模擬縮圖需要花費的時間
                Thread.sleep(3000);

                ImageInfo imageInfo = fileInfoMap.get(fileName);
                if (imageInfo != null) {
                    // 模擬創建一個縮圖檔案
                    Path thumbnailPath = thumbnailDir.resolve("thumbnail_" + fileName);
                    Files.write(thumbnailPath, "這是模擬的縮圖內容".getBytes());

                    imageInfo.setThumbnailStatus(ImageInfo.ThumbnailStatus.COMPLETED);
                    imageInfo.setThumbnailDownloadLink("/images/download/" + "thumbnail_" + fileName);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                // 處理失敗情況
                ImageInfo imageInfo = fileInfoMap.get(fileName);
                if (imageInfo != null) {
                    imageInfo.setThumbnailStatus(ImageInfo.ThumbnailStatus.FAILED);
                }
            }
        }).start();
    }

    /**
     * 取得所有已上傳圖片的清單
     *
     * @return FileInfo 物件的 List
     */
    public List<ImageInfo> getImageList() {
        return new ArrayList<>(fileInfoMap.values());
    }

    /**
     * 取得特定檔案的 Path 物件
     *
     * @param fileName 檔案名稱
     * @return 檔案的 Path 物件
     */
    public Path getFile(String fileName) {
        return this.thumbnailDir.resolve(fileName);
    }
}