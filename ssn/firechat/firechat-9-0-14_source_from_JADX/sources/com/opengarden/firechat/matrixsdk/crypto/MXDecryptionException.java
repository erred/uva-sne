package com.opengarden.firechat.matrixsdk.crypto;

public class MXDecryptionException extends Exception {
    private MXCryptoError mCryptoError;

    public MXDecryptionException(MXCryptoError mXCryptoError) {
        this.mCryptoError = mXCryptoError;
    }

    public MXCryptoError getCryptoError() {
        return this.mCryptoError;
    }

    public String getMessage() {
        if (this.mCryptoError != null) {
            return this.mCryptoError.getMessage();
        }
        return super.getMessage();
    }

    public String getLocalizedMessage() {
        if (this.mCryptoError != null) {
            return this.mCryptoError.getLocalizedMessage();
        }
        return super.getLocalizedMessage();
    }
}
