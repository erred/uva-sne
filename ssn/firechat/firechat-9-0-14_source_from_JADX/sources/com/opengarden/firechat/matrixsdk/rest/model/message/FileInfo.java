package com.opengarden.firechat.matrixsdk.rest.model.message;

public class FileInfo {
    public String mimetype;
    public Long size;

    public FileInfo deepCopy() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.mimetype = this.mimetype;
        fileInfo.size = this.size;
        return fileInfo;
    }
}
