package com.leo.cathay.image.model;

import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.enums.ThumbnailStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageInfo {
    private String fileName;
    private String originalFileName;
    private long fileSize;
    private LocalDateTime uploadDate;
    private ThumbnailStatus thumbnailStatus;
    private String thumbnailDownloadLink;

    public ImageInfo() {
        this.uploadDate = LocalDateTime.now();
        this.thumbnailStatus = ThumbnailStatus.PROCESSING;
        this.thumbnailDownloadLink = null;
    }

    public ImageInfo(FileInfo fileInfo) {
        this.fileName = fileInfo.getFileName();
        this.originalFileName = fileInfo.getOriginalFileName();
        this.fileSize = fileInfo.getFileSize();
        this.thumbnailStatus = fileInfo.getThumbnailStatus();
        this.thumbnailDownloadLink = fileInfo.getThumbnailDownloadLi();
        this.uploadDate = fileInfo.getUploadDate();
    }
}