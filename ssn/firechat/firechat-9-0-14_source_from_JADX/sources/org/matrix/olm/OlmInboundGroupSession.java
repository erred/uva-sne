package org.matrix.olm;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class OlmInboundGroupSession extends CommonSerializeUtils implements Serializable {
    private static final String LOG_TAG = "OlmInboundGroupSession";
    private static final long serialVersionUID = -772028491251653253L;
    private transient long mNativeId;

    public static class DecryptMessageResult {
        public String mDecryptedMessage;
        public long mIndex;
    }

    private native long createNewSessionJni(byte[] bArr, boolean z);

    private native byte[] decryptMessageJni(byte[] bArr, DecryptMessageResult decryptMessageResult);

    private native long deserializeJni(byte[] bArr, byte[] bArr2);

    private native byte[] exportJni(long j);

    private native long firstKnownIndexJni();

    private native boolean isVerifiedJni();

    private native void releaseSessionJni();

    private native byte[] serializeJni(byte[] bArr);

    private native byte[] sessionIdentifierJni();

    public OlmInboundGroupSession(String str) throws OlmException {
        this(str, false);
    }

    private OlmInboundGroupSession(String str, boolean z) throws OlmException {
        if (TextUtils.isEmpty(str)) {
            Log.e(LOG_TAG, "## initInboundGroupSession(): invalid session key");
            throw new OlmException(OlmException.EXCEPTION_CODE_INIT_INBOUND_GROUP_SESSION, "invalid session key");
        }
        try {
            this.mNativeId = createNewSessionJni(str.getBytes("UTF-8"), z);
        } catch (Exception e) {
            throw new OlmException(OlmException.EXCEPTION_CODE_INIT_INBOUND_GROUP_SESSION, e.getMessage());
        }
    }

    public static OlmInboundGroupSession importSession(String str) throws OlmException {
        return new OlmInboundGroupSession(str, true);
    }

    public void releaseSession() {
        if (0 != this.mNativeId) {
            releaseSessionJni();
        }
        this.mNativeId = 0;
    }

    public boolean isReleased() {
        return 0 == this.mNativeId;
    }

    public String sessionIdentifier() throws OlmException {
        try {
            return new String(sessionIdentifierJni(), "UTF-8");
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## sessionIdentifier() failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_IDENTIFIER, e.getMessage());
        }
    }

    public long getFirstKnownIndex() throws OlmException {
        try {
            return firstKnownIndexJni();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getFirstKnownIndex() failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_FIRST_KNOWN_INDEX, e.getMessage());
        }
    }

    public boolean isVerified() throws OlmException {
        try {
            return isVerifiedJni();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## isVerified() failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_IS_VERIFIED, e.getMessage());
        }
    }

    public String export(long j) throws OlmException {
        try {
            byte[] exportJni = exportJni(j);
            if (exportJni != null) {
                return new String(exportJni, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## export() failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_EXPORT, e.getMessage());
        }
    }

    public DecryptMessageResult decryptMessage(String str) throws OlmException {
        DecryptMessageResult decryptMessageResult = new DecryptMessageResult();
        try {
            byte[] decryptMessageJni = decryptMessageJni(str.getBytes("UTF-8"), decryptMessageResult);
            if (decryptMessageJni != null) {
                decryptMessageResult.mDecryptedMessage = new String(decryptMessageJni, "UTF-8");
            }
            return decryptMessageResult;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## decryptMessage() failed ");
            sb.append(e.getMessage());
            Log.e(str2, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_DECRYPT_SESSION, e.getMessage());
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        serialize(objectOutputStream);
    }

    private void readObject(ObjectInputStream objectInputStream) throws Exception {
        deserialize(objectInputStream);
    }

    /* access modifiers changed from: protected */
    public byte[] serialize(byte[] bArr, StringBuffer stringBuffer) {
        if (stringBuffer == null) {
            Log.e(LOG_TAG, "## serialize(): invalid parameter - aErrorMsg=null");
        } else if (bArr == null) {
            stringBuffer.append("Invalid input parameters in serialize()");
        } else {
            stringBuffer.setLength(0);
            try {
                return serializeJni(bArr);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## serialize() failed ");
                sb.append(e.getMessage());
                Log.e(str, sb.toString());
                stringBuffer.append(e.getMessage());
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void deserialize(byte[] bArr, byte[] bArr2) throws Exception {
        String str;
        if (bArr == null || bArr2 == null) {
            Log.e(LOG_TAG, "## deserialize(): invalid input parameters");
            str = "invalid input parameters";
        } else {
            try {
                this.mNativeId = deserializeJni(bArr, bArr2);
                str = null;
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## deserialize() failed ");
                sb.append(e.getMessage());
                Log.e(str2, sb.toString());
                str = e.getMessage();
            }
        }
        if (!TextUtils.isEmpty(str)) {
            releaseSession();
            throw new OlmException(101, str);
        }
    }
}
