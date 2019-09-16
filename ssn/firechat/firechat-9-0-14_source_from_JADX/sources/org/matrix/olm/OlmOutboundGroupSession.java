package org.matrix.olm;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class OlmOutboundGroupSession extends CommonSerializeUtils implements Serializable {
    private static final String LOG_TAG = "OlmOutboundGroupSession";
    private static final long serialVersionUID = -3133097431283604416L;
    private transient long mNativeId;

    private native long createNewSessionJni();

    private native long deserializeJni(byte[] bArr, byte[] bArr2);

    private native byte[] encryptMessageJni(byte[] bArr);

    private native int messageIndexJni();

    private native void releaseSessionJni();

    private native byte[] serializeJni(byte[] bArr);

    private native byte[] sessionIdentifierJni();

    private native byte[] sessionKeyJni();

    public OlmOutboundGroupSession() throws OlmException {
        try {
            this.mNativeId = createNewSessionJni();
        } catch (Exception e) {
            throw new OlmException(300, e.getMessage());
        }
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
            throw new OlmException(OlmException.EXCEPTION_CODE_OUTBOUND_GROUP_SESSION_IDENTIFIER, e.getMessage());
        }
    }

    public int messageIndex() {
        return messageIndexJni();
    }

    public String sessionKey() throws OlmException {
        try {
            return new String(sessionKeyJni(), "UTF-8");
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## sessionKey() failed ");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_OUTBOUND_GROUP_SESSION_KEY, e.getMessage());
        }
    }

    public String encryptMessage(String str) throws OlmException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            byte[] encryptMessageJni = encryptMessageJni(str.getBytes("UTF-8"));
            if (encryptMessageJni != null) {
                return new String(encryptMessageJni, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## encryptMessage() failed ");
            sb.append(e.getMessage());
            Log.e(str2, sb.toString());
            throw new OlmException(OlmException.EXCEPTION_CODE_OUTBOUND_GROUP_ENCRYPT_MESSAGE, e.getMessage());
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
            try {
                return serializeJni(bArr);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## serialize(): failed ");
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
