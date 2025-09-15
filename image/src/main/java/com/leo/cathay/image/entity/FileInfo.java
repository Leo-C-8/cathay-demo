package com.leo.cathay.image.entity;

import com.leo.cathay.image.model.ImageInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "upload_date", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "thumbnail_status", columnDefinition = "thumbnail_status default 'processing'")
    private ImageInfo.ThumbnailStatus thumbnailStatus;

    @Column(name = "thumbnail_download_li")
    private String thumbnailDownloadLi;

    @PrePersist
    public void prePersist() {
        if (uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
        if (thumbnailStatus == null) {
            thumbnailStatus = ImageInfo.ThumbnailStatus.PROCESSING;
        }
    }
}
