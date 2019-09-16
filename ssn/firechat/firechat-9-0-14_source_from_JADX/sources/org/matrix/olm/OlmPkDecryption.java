package org.matrix.olm;

import android.util.Log;

public class OlmPkDecryption {
    private static final String LOG_TAG = "OlmPkDecryption";
    private transient long mNativeId;

    private native long createNewPkDecryptionJni();

    private native byte[] decryptJni(OlmPkMessage olmPkMessage);

    private native byte[] generateKeyJni();

    private native void releasePkDecryptionJni();

    public OlmPkDecryption() throws OlmException {
        try {
            this.mNativeId = createNewPkDecryptionJni();
        } catch (Exception e) {
            throw new OlmException(OlmException.EXCEPTION_CODE_PK_DECRYPTION_CREATION, e.getMessage());
        }
    }

    public void releaseDecryption() {
        if (0 != this.mNativeId) {
            releasePkDecryptionJni();
        }
        this.mNativeId = 0;
    }

    public boolean isReleased() {
        return 0 == this.mNativeId;
    }

    public String generateKey() throws OlmException {
        try {
            return new String(generateKeyJni(), "UTF-8");
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## setRecipientKey(): failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_PK_DECRYPTION_GENERATE_KEY, e.getMessage());
        }
    }

    public String decrypt(OlmPkMessage olmPkMessage) throws OlmException {
        if (olmPkMessage == null) {
            return null;
        }
        try {
            return new String(decryptJni(olmPkMessage), "UTF-8");
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## pkDecrypt(): failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_PK_DECRYPTION_DECRYPT, e.getMessage());
        }
    }
}
