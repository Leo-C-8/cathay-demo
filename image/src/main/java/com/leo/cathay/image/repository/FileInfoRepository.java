package com.leo.cathay.image.repository;

import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.model.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findByThumbnailStatus(ImageInfo.ThumbnailStatus status);
}
