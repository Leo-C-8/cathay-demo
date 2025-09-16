package com.leo.cathay.image.entity;

import com.leo.cathay.image.enums.ThumbnailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "thumbnail_status")
    private ThumbnailStatus thumbnailStatus; // 這裡直接引用獨立的類別

    @Column(name = "thumbnail_download_li")
    private String thumbnailDownloadLi;

    @PrePersist
    public void prePersist() {
        if (uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
        if (thumbnailStatus == null) {
            thumbnailStatus = ThumbnailStatus.PROCESSING;
        }
    }
}