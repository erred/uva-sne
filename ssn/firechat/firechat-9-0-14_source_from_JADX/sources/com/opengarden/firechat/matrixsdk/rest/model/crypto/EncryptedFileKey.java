package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;
import java.util.List;

public class EncryptedFileKey implements Serializable {
    public String alg;
    public Boolean ext;

    /* renamed from: k */
    public String f134k;
    public List<String> key_ops;
    public String kty;

    public EncryptedFileKey deepCopy() {
        EncryptedFileKey encryptedFileKey = new EncryptedFileKey();
        encryptedFileKey.alg = this.alg;
        encryptedFileKey.ext = this.ext;
        encryptedFileKey.key_ops = this.key_ops;
        encryptedFileKey.kty = this.kty;
        encryptedFileKey.f134k = this.f134k;
        return encryptedFileKey;
    }
}
