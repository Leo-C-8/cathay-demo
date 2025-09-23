package com.leo.cathay.image.controller;

import com.leo.cathay.image.dto.ImageInfoListDto;
import com.leo.cathay.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    public ResponseEntity<ImageInfoListDto> getListImages() {
        ImageInfoListDto imageInfoListDto = imageService.getImageList();
        return ResponseEntity.ok(imageInfoListDto);
    }

    /**
     * 下載圖片 API
     *
     * @param fileName 圖片的唯一檔案名稱
     * @return 檔案內容
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        try {
            byte[] fileBytes = imageService.downloadFile(fileName);
            Resource resource = new ByteArrayResource(fileBytes);

            ContentDisposition disposition = ContentDisposition.attachment()
                    .filename(URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(disposition);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
