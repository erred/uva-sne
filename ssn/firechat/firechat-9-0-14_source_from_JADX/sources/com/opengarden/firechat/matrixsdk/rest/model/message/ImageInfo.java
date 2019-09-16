package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;

public class ImageInfo {

    /* renamed from: h */
    public Integer f135h;
    public String mimetype;
    public Integer orientation;
    public Integer rotation;
    public Long size;
    public ThumbnailInfo thumbnailInfo;
    public String thumbnailUrl;
    public EncryptedFileInfo thumbnail_file;

    /* renamed from: w */
    public Integer f136w;

    public ImageInfo deepCopy() {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.mimetype = this.mimetype;
        imageInfo.f136w = this.f136w;
        imageInfo.f135h = this.f135h;
        imageInfo.size = this.size;
        imageInfo.rotation = this.rotation;
        imageInfo.orientation = this.orientation;
        if (this.thumbnail_file != null) {
            imageInfo.thumbnail_file = this.thumbnail_file.deepCopy();
        }
        imageInfo.thumbnailUrl = this.thumbnailUrl;
        if (this.thumbnailInfo != null) {
            imageInfo.thumbnailInfo = this.thumbnailInfo.deepCopy();
        }
        return imageInfo;
    }
}
