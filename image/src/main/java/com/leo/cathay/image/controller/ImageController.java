package com.leo.cathay.image.controller;

import com.leo.cathay.image.model.ImageInfo;
import com.leo.cathay.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 圖片上傳 API
     *
     * @param file 圖片檔案
     * @return 上傳成功的訊息
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("[uploadImage] File Empty");
            return ResponseEntity.badRequest().body("請選擇一個檔案。");
        }
        try {
            System.out.println("[uploadImage] file = " + file);
            imageService.uploadFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("圖片上傳成功！");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("檔案上傳失敗：" + e.getMessage());
        }
    }

    /**
     * 取得圖片清單 API
     *
     * @return 包含所有圖片資訊的列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<ImageInfo>> getListImages() {
        List<ImageInfo> fileList = imageService.getImageList();
        return ResponseEntity.ok(fileList);
    }

    /**
     * 圖片下載 API (下載縮圖)
     *
     * @param fileName 檔案名稱
     * @return 圖片檔案
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable String fileName) {
        try {
            Path filePath = imageService.getFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/octet-stream";
                // 這裡可以根據檔案類型動態設定
                // 但為了簡單，這裡統一使用 octet-stream

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "檔案不存在或無法讀取！");
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "檔案路徑錯誤：" + e.getMessage());
        }
    }
}

