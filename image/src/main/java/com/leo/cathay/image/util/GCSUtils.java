package com.leo.cathay.image.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GCSUtils {
    @Autowired
    private Storage storage;


    public void upload(byte[] filebytes, String userAccount, String fileName, String fileType, String buket) {
        BlobId id = BlobId.fromGsUtilUri(genGCSFilePath(userAccount, fileName, buket));
        BlobInfo info = BlobInfo.newBuilder(id).setContentType(fileType).build();
        storage.create(info, filebytes);
    }

    public byte[] getFile(String userAccount, String fileName, String buket) {
        BlobId id = BlobId.fromGsUtilUri(genGCSFilePath(userAccount, fileName, buket));

        Blob fileBlob = storage.get(id);

        if (Objects.isNull(fileBlob)) {
            throw new IllegalArgumentException();
        }

        return fileBlob.getContent();
    }


    private String genGCSFilePath(String accountID, String fileName, String buket) {

        StringBuilder builder = new StringBuilder();
        builder.append("gs://");
        builder.append(buket);
        builder.append("/");
        builder.append(accountID);
        builder.append("/");
        builder.append(fileName);

        return builder.toString();
    }
}
