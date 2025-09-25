package com.leo.cathay.image.repository;

import com.leo.cathay.image.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findAllByUserName(String userName);

    void deleteByFileName(String fileName);

    boolean existsByFileName(String fileName);

    Optional<FileInfo> findByFileName(String fileName);
}
