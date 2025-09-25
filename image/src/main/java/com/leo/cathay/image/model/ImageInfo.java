package com.leo.cathay.image.model;

import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.enums.ThumbnailStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageInfo {
    private String fileName;
    private String originalFileName;
    private Long fileSize;
    private Long originalFileSize;
    private LocalDateTime uploadDate;
    private ThumbnailStatus thumbnailStatus;

    public ImageInfo() {
        this.uploadDate = LocalDateTime.now();
        this.thumbnailStatus = ThumbnailStatus.PROCESSING;
    }

    public ImageInfo(FileInfo fileInfo) {
        this.fileName = fileInfo.getFileName();
        this.originalFileName = fileInfo.getOriginalFileName();
        this.fileSize = fileInfo.getFileSize();
        this.originalFileSize = fileInfo.getOriginalFileSize();
        this.thumbnailStatus = fileInfo.getThumbnailStatus();
        this.uploadDate = fileInfo.getUploadDate();
    }
}