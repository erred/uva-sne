package com.opengarden.firechat.matrixsdk.rest.model.message;

public class ThumbnailInfo {

    /* renamed from: h */
    public Integer f137h;
    public String mimetype;
    public Long size;

    /* renamed from: w */
    public Integer f138w;

    public ThumbnailInfo deepCopy() {
        ThumbnailInfo thumbnailInfo = new ThumbnailInfo();
        thumbnailInfo.f138w = this.f138w;
        thumbnailInfo.f137h = this.f137h;
        thumbnailInfo.size = this.size;
        thumbnailInfo.mimetype = this.mimetype;
        return thumbnailInfo;
    }
}
