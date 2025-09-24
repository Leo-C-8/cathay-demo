package com.leo.cathay.image.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.leo.cathay.image.enums.CloudStorgeFolderName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class GCSUtils {
    @Autowired
    private Storage storage;


    public void upload(byte[] filebytes, String userAccount, CloudStorgeFolderName folderName, String fileName, String fileType, String buket) {
        System.out.println("[GCSUtils] upload");

        BlobId id = BlobId.fromGsUtilUri(genGCSFilePath(userAccount, folderName, fileName, buket));
        BlobInfo info = BlobInfo.newBuilder(id).setContentType(fileType).build();
        storage.create(info, filebytes);
    }

    public byte[] getFile(String userAccount, CloudStorgeFolderName folderName, String fileName, String buket) {
        System.out.println("[GCSUtils] getFile");

        BlobId id = BlobId.fromGsUtilUri(genGCSFilePath(userAccount, folderName, fileName, buket));

        Blob fileBlob = storage.get(id);

        if (Objects.isNull(fileBlob)) {
            throw new IllegalArgumentException();
        }

        return fileBlob.getContent();
    }

    public void deleteFile(String userAccount, CloudStorgeFolderName folderName, String fileName, String buket) throws IOException {
        System.out.println("[GCSUtils] deleteFile");

        BlobId id = BlobId.fromGsUtilUri(genGCSFilePath(userAccount, folderName, fileName, buket));


        boolean deleted = storage.delete(id);
        if (!deleted) {
            throw new IOException("Failed to delete file from Google Cloud Storage.");
        }
    }

    private String genGCSFilePath(String accountID, CloudStorgeFolderName folderName, String fileName, String buket) {
        System.out.println("[GCSUtils] genGCSFilePath");


        StringBuilder builder = new StringBuilder();
        builder.append("gs://");
        builder.append(buket);
        builder.append("/");
        builder.append(folderName.getValue());
        builder.append("/");
        builder.append(accountID);
        builder.append("/");
        builder.append(fileName);

        return builder.toString();
    }
}
