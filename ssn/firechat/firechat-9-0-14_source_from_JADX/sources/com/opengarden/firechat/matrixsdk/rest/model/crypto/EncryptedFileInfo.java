package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;
import java.util.Map;

public class EncryptedFileInfo implements Serializable {
    public Map<String, String> hashes;

    /* renamed from: iv */
    public String f132iv;
    public EncryptedFileKey key;
    public String mimetype;
    public String url;

    /* renamed from: v */
    public String f133v;

    public EncryptedFileInfo deepCopy() {
        EncryptedFileInfo encryptedFileInfo = new EncryptedFileInfo();
        encryptedFileInfo.url = this.url;
        encryptedFileInfo.mimetype = this.mimetype;
        if (this.key != null) {
            encryptedFileInfo.key = this.key.deepCopy();
        }
        encryptedFileInfo.f132iv = this.f132iv;
        encryptedFileInfo.hashes = this.hashes;
        return encryptedFileInfo;
    }
}
