package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.EncryptionResult;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;

public class VideoMessage extends MediaMessage {
    public EncryptedFileInfo file;
    public VideoInfo info;
    public String url;

    public VideoMessage() {
        this.msgtype = Message.MSGTYPE_VIDEO;
    }

    public String getUrl() {
        if (this.url != null) {
            return this.url;
        }
        if (this.file != null) {
            return this.file.url;
        }
        return null;
    }

    public void setUrl(EncryptionResult encryptionResult, String str) {
        if (encryptionResult != null) {
            this.file = encryptionResult.mEncryptedFileInfo;
            this.file.url = str;
            this.url = null;
            return;
        }
        this.url = str;
    }

    public String getThumbnailUrl() {
        if (this.info != null && this.info.thumbnail_url != null) {
            return this.info.thumbnail_url;
        }
        if (this.info == null || this.info.thumbnail_file == null) {
            return null;
        }
        return this.info.thumbnail_file.url;
    }

    public void setThumbnailUrl(EncryptionResult encryptionResult, String str) {
        if (encryptionResult != null) {
            this.info.thumbnail_file = encryptionResult.mEncryptedFileInfo;
            this.info.thumbnail_file.url = str;
            this.info.thumbnail_url = null;
            return;
        }
        this.info.thumbnail_url = str;
    }

    public VideoMessage deepCopy() {
        VideoMessage videoMessage = new VideoMessage();
        videoMessage.url = this.url;
        videoMessage.msgtype = this.msgtype;
        videoMessage.body = this.body;
        if (this.info != null) {
            videoMessage.info = this.info.deepCopy();
        }
        if (this.file != null) {
            videoMessage.file = this.file.deepCopy();
        }
        return videoMessage;
    }

    public String getMimeType() {
        if (this.info != null) {
            return this.info.mimetype;
        }
        return null;
    }
}
