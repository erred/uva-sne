package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.EncryptionResult;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;

public class ImageMessage extends MediaMessage {
    public EncryptedFileInfo file;
    public ImageInfo info;
    public String url;

    public ImageMessage() {
        this.msgtype = Message.MSGTYPE_IMAGE;
    }

    public ImageMessage deepCopy() {
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.msgtype = this.msgtype;
        imageMessage.body = this.body;
        imageMessage.url = this.url;
        if (this.file != null) {
            imageMessage.file = this.file.deepCopy();
        }
        return imageMessage;
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
        if (this.info == null) {
            return null;
        }
        if (this.info.thumbnail_file != null) {
            return this.info.thumbnail_file.url;
        }
        return this.info.thumbnailUrl;
    }

    public void setThumbnailUrl(EncryptionResult encryptionResult, String str) {
        if (encryptionResult != null) {
            this.info.thumbnail_file = encryptionResult.mEncryptedFileInfo;
            this.info.thumbnail_file.url = str;
            this.info.thumbnailUrl = null;
            return;
        }
        this.info.thumbnailUrl = str;
    }

    public String getMimeType() {
        if (this.file != null) {
            return this.file.mimetype;
        }
        if (this.info != null) {
            return this.info.mimetype;
        }
        return null;
    }

    public int getRotation() {
        if (this.info == null || this.info.rotation == null) {
            return Integer.MAX_VALUE;
        }
        return this.info.rotation.intValue();
    }

    public int getOrientation() {
        if (this.info == null || this.info.orientation == null) {
            return 0;
        }
        return this.info.orientation.intValue();
    }
}
