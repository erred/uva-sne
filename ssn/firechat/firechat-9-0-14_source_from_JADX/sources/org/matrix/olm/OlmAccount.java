package org.matrix.olm;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.json.JSONObject;

public class OlmAccount extends CommonSerializeUtils implements Serializable {
    public static final String JSON_KEY_FINGER_PRINT_KEY = "ed25519";
    public static final String JSON_KEY_IDENTITY_KEY = "curve25519";
    public static final String JSON_KEY_ONE_TIME_KEY = "curve25519";
    private static final String LOG_TAG = "OlmAccount";
    private static final long serialVersionUID = 3497486121598434824L;
    private transient long mNativeId;

    private native long createNewAccountJni();

    private native long deserializeJni(byte[] bArr, byte[] bArr2);

    private native void generateOneTimeKeysJni(int i);

    private native byte[] identityKeysJni();

    private native void markOneTimeKeysAsPublishedJni();

    private native long maxOneTimeKeysJni();

    private native byte[] oneTimeKeysJni();

    private native void releaseAccountJni();

    private native void removeOneTimeKeysJni(long j);

    private native byte[] serializeJni(byte[] bArr);

    private native byte[] signMessageJni(byte[] bArr);

    public OlmAccount() throws OlmException {
        try {
            this.mNativeId = createNewAccountJni();
        } catch (Exception e) {
            throw new OlmException(10, e.getMessage());
        }
    }

    /* access modifiers changed from: 0000 */
    public long getOlmAccountId() {
        return this.mNativeId;
    }

    public void releaseAccount() {
        if (0 != this.mNativeId) {
            releaseAccountJni();
        }
        this.mNativeId = 0;
    }

    public boolean isReleased() {
        return 0 == this.mNativeId;
    }

    public Map<String, String> identityKeys() throws OlmException {
        JSONObject jSONObject;
        try {
            byte[] identityKeysJni = identityKeysJni();
            if (identityKeysJni != null) {
                try {
                    jSONObject = new JSONObject(new String(identityKeysJni, "UTF-8"));
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## identityKeys(): Exception - Msg=");
                    sb.append(e.getMessage());
                    Log.e(str, sb.toString());
                }
            } else {
                Log.e(LOG_TAG, "## identityKeys(): Failure - identityKeysJni()=null");
                jSONObject = null;
            }
            return OlmUtility.toStringMap(jSONObject);
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## identityKeys(): Failure - ");
            sb2.append(e2.getMessage());
            Log.e(str2, sb2.toString());
            throw new OlmException(102, e2.getMessage());
        }
    }

    public long maxOneTimeKeys() {
        return maxOneTimeKeysJni();
    }

    public void generateOneTimeKeys(int i) throws OlmException {
        try {
            generateOneTimeKeysJni(i);
        } catch (Exception e) {
            throw new OlmException(103, e.getMessage());
        }
    }

    public Map<String, Map<String, String>> oneTimeKeys() throws OlmException {
        JSONObject jSONObject;
        try {
            byte[] oneTimeKeysJni = oneTimeKeysJni();
            if (oneTimeKeysJni != null) {
                try {
                    jSONObject = new JSONObject(new String(oneTimeKeysJni, "UTF-8"));
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## oneTimeKeys(): Exception - Msg=");
                    sb.append(e.getMessage());
                    Log.e(str, sb.toString());
                }
            } else {
                Log.e(LOG_TAG, "## oneTimeKeys(): Failure - identityKeysJni()=null");
                jSONObject = null;
            }
            return OlmUtility.toStringMapMap(jSONObject);
        } catch (Exception e2) {
            throw new OlmException(104, e2.getMessage());
        }
    }

    public void removeOneTimeKeys(OlmSession olmSession) throws OlmException {
        if (olmSession != null) {
            try {
                removeOneTimeKeysJni(olmSession.getOlmSessionId());
            } catch (Exception e) {
                throw new OlmException(105, e.getMessage());
            }
        }
    }

    public void markOneTimeKeysAsPublished() throws OlmException {
        try {
            markOneTimeKeysAsPublishedJni();
        } catch (Exception e) {
            throw new OlmException(106, e.getMessage());
        }
    }

    public String signMessage(String str) throws OlmException {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes == null) {
                return null;
            }
            byte[] signMessageJni = signMessageJni(bytes);
            if (signMessageJni != null) {
                return new String(signMessageJni, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            throw new OlmException(107, e.getMessage());
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
            stringBuffer.append("Invalid input parameters in serializeDataWithKey()");
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
            releaseAccount();
            throw new OlmException(101, str);
        }
    }
}
