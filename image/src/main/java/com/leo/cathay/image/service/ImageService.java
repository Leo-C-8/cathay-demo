package com.leo.cathay.image.service;

import com.leo.cathay.image.dto.ImageInfoListDto;
import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.enums.CloudStorgeFolderName;
import com.leo.cathay.image.enums.ThumbnailStatus;
import com.leo.cathay.image.model.ImageInfo;
import com.leo.cathay.image.repository.FileInfoRepository;
import com.leo.cathay.image.util.GCSUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ImageService {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    GCSUtils gcsUtils;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String cloudStorageBucket;

    /**
     * 上傳圖片並將其資訊儲存至資料庫。
     *
     * @param file 使用者上傳的圖片檔案
     * @return 包含檔案資訊的 FileInfo 物件
     * @throws IOException 如果檔案儲存失敗
     */
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        System.out.println("[ImageService] uploadFile");

        try {
            if (file.isEmpty()) {
                throw new IOException("上傳檔案不得為空。");
            }

            // 從 SecurityContextHolder 中獲取當前使用者的名稱
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            // 使用 UUID 產生唯一檔名，防止名稱衝突
            String originalFileName = file.getOriginalFilename();

            String fileExtension = "";

            if (Objects.nonNull(originalFileName) && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String uniqueFileName = String.valueOf(UUID.randomUUID());

            FileInfo fileInfo = FileInfo.builder()
                    .fileName(uniqueFileName)
                    .originalFileName(originalFileName)
                    .userName(currentUserName)
                    .originalFileSize(file.getSize())
                    .build();

            // 將實體儲存到資料庫
            FileInfo savedFileInfo = fileInfoRepository.save(fileInfo);
            gcsUtils.upload(file.getBytes(), currentUserName, CloudStorgeFolderName.ORIGINAL, uniqueFileName, fileExtension, cloudStorageBucket);

            return savedFileInfo;
        } catch (Exception e) {
            System.out.println("[uploadFile] Fail, e :" + e);
        }

        return null;
    }

    /**
     * 取得所有已上傳圖片的清單（從資料庫）
     *
     * @return 包含所有圖片資訊的列表
     */
    public ImageInfoListDto getImageList() {
        System.out.println("[ImageService] getImageList");

        // 從 SecurityContextHolder 中獲取當前使用者的名稱
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        List<FileInfo> fileInfoList = fileInfoRepository.findAllByUserName(currentUserName);

        List<ImageInfo> imageInfoList = fileInfoList.stream()
                .map(ImageInfo::new)
                .collect(Collectors.toList());

        return new ImageInfoListDto(imageInfoList, imageInfoList.size());
    }

    /**
     * 從 Google Cloud Storage 下載檔案。
     *
     * @param fileName 圖片的唯一檔案名稱
     * @return 檔案的位元組陣列
     * @throws IOException 如果檔案下載失敗
     */
    public byte[] downloadFile(String fileName, CloudStorgeFolderName folderName) throws IOException {
        System.out.println("[ImageService] downloadFile");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        return gcsUtils.getFile(currentUserName, folderName, fileName, cloudStorageBucket);
    }

    @Transactional
    public void deleteFile(String fileName) throws IOException {
        System.out.println("[ImageService] deleteFile");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // 刪除 FileInfo
        if (!fileInfoRepository.existsByFileName(fileName)) {
            throw new IllegalArgumentException("FileInfo not found for fileName: " + fileName);
        }
        fileInfoRepository.deleteByFileName(fileName);

        // 呼叫 gcsUtils.deleteFile 來刪除原始圖
        gcsUtils.deleteFile(currentUserName, CloudStorgeFolderName.ORIGINAL, fileName, cloudStorageBucket);

        // 呼叫 gcsUtils.deleteFile 來刪除縮圖
        gcsUtils.deleteFile(currentUserName, CloudStorgeFolderName.THUMBNAIL, fileName, cloudStorageBucket);
    }

    /**
     * 將指定檔案名稱的 FileInfo 縮圖狀態更新為 COMPLETED。
     *
     * @param fileName 要更新的檔案名稱（UUID 格式）
     * @return 更新後的 FileInfo 物件，若找不到則回傳 null
     */
    @Transactional
    public FileInfo updateThumbnailStatusToCompleted(String fileName, long fileSize) {
        System.out.println("[ImageService] updateThumbnailStatusToCompleted, fileName=" + fileName);

        return fileInfoRepository.findByFileName(fileName)
                .map(fileInfo -> {
                    fileInfo.setThumbnailStatus(ThumbnailStatus.COMPLETED);
                    fileInfo.setFileSize(fileSize);
                    return fileInfoRepository.save(fileInfo);
                })
                .orElse(null);
    }
}