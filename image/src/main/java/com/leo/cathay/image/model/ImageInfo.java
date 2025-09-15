package com.leo.cathay.image.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageInfo {
    private String fileName;
    private long fileSize;
    private LocalDateTime uploadDate;
    private ThumbnailStatus thumbnailStatus;
    private String thumbnailDownloadLink;

    public ImageInfo() {
        this.uploadDate = LocalDateTime.now();
        this.thumbnailStatus = ThumbnailStatus.PROCESSING; // 初始狀態為 "處理中"
        this.thumbnailDownloadLink = null;
    }

    public ImageInfo(String fileName, long fileSize) {
        this();
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public enum ThumbnailStatus {
        PROCESSING("processing"),
        COMPLETED("completed"),
        FAILED("failed");

        private final String value;

        ThumbnailStatus(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}