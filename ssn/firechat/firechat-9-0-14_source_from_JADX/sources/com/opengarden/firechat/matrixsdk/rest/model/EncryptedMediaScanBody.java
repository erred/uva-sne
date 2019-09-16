package com.opengarden.firechat.matrixsdk.rest.model;

import com.google.gson.annotations.SerializedName;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;

public class EncryptedMediaScanBody {
    @SerializedName("file")
    public EncryptedFileInfo encryptedFileInfo;
}
