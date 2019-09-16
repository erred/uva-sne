package org.matrix.olm;

import android.util.Log;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import kotlin.jvm.internal.ByteCompanionObject;
import org.json.JSONObject;

public class OlmUtility {
    private static final String LOG_TAG = "OlmUtility";
    public static final int RANDOM_KEY_SIZE = 32;
    private long mNativeId;

    private native long createUtilityJni();

    private native void releaseUtilityJni();

    private native byte[] sha256Jni(byte[] bArr);

    private native String verifyEd25519SignatureJni(byte[] bArr, byte[] bArr2, byte[] bArr3);

    public OlmUtility() throws OlmException {
        initUtility();
    }

    private void initUtility() throws OlmException {
        try {
            this.mNativeId = createUtilityJni();
        } catch (Exception e) {
            throw new OlmException(500, e.getMessage());
        }
    }

    public void releaseUtility() {
        if (0 != this.mNativeId) {
            releaseUtilityJni();
        }
        this.mNativeId = 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0061 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void verifyEd25519Signature(java.lang.String r2, java.lang.String r3, java.lang.String r4) throws org.matrix.olm.OlmException {
        /*
            r1 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0034 }
            if (r0 != 0) goto L_0x002a
            boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0034 }
            if (r0 != 0) goto L_0x002a
            boolean r0 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0034 }
            if (r0 == 0) goto L_0x0013
            goto L_0x002a
        L_0x0013:
            java.lang.String r0 = "UTF-8"
            byte[] r2 = r2.getBytes(r0)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r0 = "UTF-8"
            byte[] r3 = r3.getBytes(r0)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r0 = "UTF-8"
            byte[] r4 = r4.getBytes(r0)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r2 = r1.verifyEd25519SignatureJni(r2, r3, r4)     // Catch:{ Exception -> 0x0034 }
            goto L_0x0053
        L_0x002a:
            java.lang.String r2 = "OlmUtility"
            java.lang.String r3 = "## verifyEd25519Signature(): invalid input parameters"
            android.util.Log.e(r2, r3)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r2 = "JAVA sanity check failure - invalid input parameters"
            goto L_0x0053
        L_0x0034:
            r2 = move-exception
            java.lang.String r3 = "OlmUtility"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r0 = "## verifyEd25519Signature(): failed "
            r4.append(r0)
            java.lang.String r0 = r2.getMessage()
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.e(r3, r4)
            java.lang.String r2 = r2.getMessage()
        L_0x0053:
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0061
            org.matrix.olm.OlmException r3 = new org.matrix.olm.OlmException
            r4 = 501(0x1f5, float:7.02E-43)
            r3.<init>(r4, r2)
            throw r3
        L_0x0061:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.matrix.olm.OlmUtility.verifyEd25519Signature(java.lang.String, java.lang.String, java.lang.String):void");
    }

    public String sha256(String str) {
        if (str != null) {
            try {
                return new String(sha256Jni(str.getBytes("UTF-8")), "UTF-8");
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sha256(): failed ");
                sb.append(e.getMessage());
                Log.e(str2, sb.toString());
            }
        }
        return null;
    }

    public static byte[] getRandomKey() {
        byte[] bArr = new byte[32];
        new SecureRandom().nextBytes(bArr);
        for (int i = 0; i < 32; i++) {
            bArr[i] = (byte) (bArr[i] & ByteCompanionObject.MAX_VALUE);
        }
        return bArr;
    }

    public boolean isReleased() {
        return 0 == this.mNativeId;
    }

    public static Map<String, String> toStringMap(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            String str = (String) keys.next();
            try {
                Object obj = jSONObject.get(str);
                if (obj instanceof String) {
                    hashMap.put(str, (String) obj);
                } else {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## toStringMap(): unexpected type ");
                    sb.append(obj.getClass());
                    Log.e(str2, sb.toString());
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## toStringMap(): failed ");
                sb2.append(e.getMessage());
                Log.e(str3, sb2.toString());
            }
        }
        return hashMap;
    }

    public static Map<String, Map<String, String>> toStringMapMap(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            String str = (String) keys.next();
            try {
                Object obj = jSONObject.get(str);
                if (obj instanceof JSONObject) {
                    hashMap.put(str, toStringMap((JSONObject) obj));
                } else {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## toStringMapMap(): unexpected type ");
                    sb.append(obj.getClass());
                    Log.e(str2, sb.toString());
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## toStringMapMap(): failed ");
                sb2.append(e.getMessage());
                Log.e(str3, sb2.toString());
            }
        }
        return hashMap;
    }
}
