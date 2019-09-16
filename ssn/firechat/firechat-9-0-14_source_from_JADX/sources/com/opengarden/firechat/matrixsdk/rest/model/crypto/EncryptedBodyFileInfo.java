package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import org.matrix.olm.OlmPkMessage;

public class EncryptedBodyFileInfo {
    public String ciphertext;
    public String ephemeral;
    public String mac;

    public EncryptedBodyFileInfo(OlmPkMessage olmPkMessage) {
        this.ciphertext = olmPkMessage.mCipherText;
        this.mac = olmPkMessage.mMac;
        this.ephemeral = olmPkMessage.mEphemeralKey;
    }
}
