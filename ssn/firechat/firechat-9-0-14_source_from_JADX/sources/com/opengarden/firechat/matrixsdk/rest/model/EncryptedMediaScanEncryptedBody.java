package com.opengarden.firechat.matrixsdk.rest.model;

import com.google.gson.annotations.SerializedName;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedBodyFileInfo;

public class EncryptedMediaScanEncryptedBody {
    @SerializedName("encrypted_body")
    public EncryptedBodyFileInfo encryptedBodyFileInfo;
}
