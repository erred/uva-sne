package com.opengarden.firechat.matrixsdk.rest.model.message;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.EncryptionResult;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.util.Log;

public class FileMessage extends MediaMessage {
    private static final String LOG_TAG = "FileMessage";
    public EncryptedFileInfo file;
    public FileInfo info;
    public String url;

    public FileMessage() {
        this.msgtype = Message.MSGTYPE_FILE;
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

    public FileMessage deepCopy() {
        FileMessage fileMessage = new FileMessage();
        fileMessage.msgtype = this.msgtype;
        fileMessage.body = this.body;
        fileMessage.url = this.url;
        if (this.info != null) {
            fileMessage.info = this.info.deepCopy();
        }
        if (this.file != null) {
            fileMessage.file = this.file.deepCopy();
        }
        return fileMessage;
    }

    public String getMimeType() {
        if (this.info == null) {
            return null;
        }
        if ((TextUtils.isEmpty(this.info.mimetype) || "text/uri-list".equals(this.info.mimetype)) && this.body.indexOf(46) > 0) {
            String substring = this.body.substring(this.body.lastIndexOf(46) + 1, this.body.length());
            try {
                this.info.mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(substring.toLowerCase());
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getMimeType() : getMimeTypeFromExtensionfailed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        if (TextUtils.isEmpty(this.info.mimetype)) {
            this.info.mimetype = "application/octet-stream";
        }
        return this.info.mimetype;
    }
}
