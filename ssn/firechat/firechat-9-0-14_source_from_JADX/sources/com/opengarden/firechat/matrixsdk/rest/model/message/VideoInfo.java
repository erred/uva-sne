package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;

public class VideoInfo {
    public Long duration;

    /* renamed from: h */
    public Integer f139h;
    public String mimetype;
    public Long size;
    public EncryptedFileInfo thumbnail_file;
    public ThumbnailInfo thumbnail_info;
    public String thumbnail_url;

    /* renamed from: w */
    public Integer f140w;

    public VideoInfo deepCopy() {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.f139h = this.f139h;
        videoInfo.f140w = this.f140w;
        videoInfo.mimetype = this.mimetype;
        videoInfo.duration = this.duration;
        videoInfo.thumbnail_url = this.thumbnail_url;
        if (this.thumbnail_info != null) {
            videoInfo.thumbnail_info = this.thumbnail_info.deepCopy();
        }
        if (this.thumbnail_file != null) {
            videoInfo.thumbnail_file = this.thumbnail_file.deepCopy();
        }
        return videoInfo;
    }
}
