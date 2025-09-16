package com.leo.cathay.image.dto;

import com.leo.cathay.image.model.ImageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileListDto {
    private List<ImageInfo> files;
    private int imageSize;
}
