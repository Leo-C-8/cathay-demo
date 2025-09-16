package com.leo.cathay.image.controller;

import com.leo.cathay.image.dto.FileListDto;
import com.leo.cathay.image.model.ImageInfo;
import com.leo.cathay.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<FileListDto> getListImages() {
        FileListDto fileListDto = imageService.getImageList();
        return ResponseEntity.ok(fileListDto);
    }
}

