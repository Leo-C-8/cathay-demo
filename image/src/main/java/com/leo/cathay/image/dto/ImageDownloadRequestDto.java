package com.leo.cathay.image.dto;

import com.leo.cathay.image.enums.CloudStorgeFolderName;
import lombok.Data;

@Data
public class ImageDownloadRequestDto {
    private String fileName;
    private CloudStorgeFolderName folderName;
}
