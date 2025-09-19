package com.leo.cathay.image.service;

import com.leo.cathay.image.dto.FileListDto;
import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.enums.ThumbnailStatus;
import com.leo.cathay.image.model.ImageInfo;
import com.leo.cathay.image.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ImageService {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    /**
     * 上傳圖片並將其資訊儲存至資料庫。
     *
     * @param file 使用者上傳的圖片檔案
     * @return 包含檔案資訊的 FileInfo 物件
     * @throws IOException 如果檔案儲存失敗
     */
    public FileInfo uploadFile(MultipartFile file) throws IOException {
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
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            FileInfo fileInfo = FileInfo.builder()
                    .fileName(uniqueFileName)
                    .originalFileName(originalFileName)
                    .userName(currentUserName)
                    .fileSize(file.getSize())
                    .build();

            // 將實體儲存到資料庫
            FileInfo savedFileInfo = fileInfoRepository.save(fileInfo);

            // 啟動異步縮圖作業
            processThumbnail(savedFileInfo);

            return savedFileInfo;
        } catch (Exception e) {
            System.out.println("[uploadFile] Fail, e :" + e);
        }

        return null;
    }

    /**
     * 模擬異步縮圖作業，並更新資料庫中的檔案狀態。
     *
     * @param fileInfo 待處理的檔案資訊
     */
    private void processThumbnail(FileInfo fileInfo) {
        new Thread(() -> {
            try {
                // 模擬縮圖作業
                Thread.sleep(3000000);

                // 更新資料庫中的檔案狀態和下載連結
                fileInfo.setThumbnailStatus(ThumbnailStatus.COMPLETED);
                fileInfo.setThumbnailDownloadLi("/images/download/" + "thumbnail_" + fileInfo.getFileName());

            } catch (InterruptedException e) {
                // 處理失敗情況
                fileInfo.setThumbnailStatus(ThumbnailStatus.FAILED);
            } finally {
                // 無論成功或失敗，都將最終狀態儲存到資料庫
                fileInfoRepository.save(fileInfo);
            }
        }).start();
    }

    /**
     * 取得所有已上傳圖片的清單（從資料庫）
     *
     * @return 包含所有圖片資訊的列表
     */
    public FileListDto getImageList() {
        // 從 SecurityContextHolder 中獲取當前使用者的名稱
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        List<FileInfo> fileInfoList = fileInfoRepository.findAllByUserName(currentUserName);

        List<ImageInfo> imageInfoList = fileInfoList.stream()
                .map(ImageInfo::new)
                .collect(Collectors.toList());

        return new FileListDto(imageInfoList, imageInfoList.size());
    }
}