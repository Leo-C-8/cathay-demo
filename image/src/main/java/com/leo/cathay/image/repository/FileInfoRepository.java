package com.leo.cathay.image.repository;

import com.leo.cathay.image.entity.FileInfo;
import com.leo.cathay.image.enums.ThumbnailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findByThumbnailStatus(ThumbnailStatus status);

    List<FileInfo> findAllByUserName(String userName);

    void deleteByFileName(String fileName);

    boolean existsByFileName(String fileName);
}
