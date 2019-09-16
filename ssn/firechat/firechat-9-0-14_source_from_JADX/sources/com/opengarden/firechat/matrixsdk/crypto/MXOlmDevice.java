package com.opengarden.firechat.matrixsdk.crypto;

import android.text.TextUtils;
import com.google.gson.JsonParser;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.MXDecryptionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2;
import com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matrix.olm.OlmAccount;
import org.matrix.olm.OlmInboundGroupSession.DecryptMessageResult;
import org.matrix.olm.OlmMessage;
import org.matrix.olm.OlmOutboundGroupSession;
import org.matrix.olm.OlmSession;
import org.matrix.olm.OlmUtility;

public class MXOlmDevice {
    private static final String LOG_TAG = "MXOlmDevice";
    private String mDeviceCurve25519Key;
    private String mDeviceEd25519Key;
    private final HashMap<String, HashMap<String, Boolean>> mInboundGroupSessionMessageIndexes;
    private MXCryptoError mInboundGroupSessionWithIdError = null;
    private OlmAccount mOlmAccount;
    private OlmUtility mOlmUtility;
    private final HashMap<String, OlmOutboundGroupSession> mOutboundGroupSessionStore;
    private final IMXCryptoStore mStore;

    public MXOlmDevice(IMXCryptoStore iMXCryptoStore) {
        this.mStore = iMXCryptoStore;
        this.mOlmAccount = this.mStore.getAccount();
        if (this.mOlmAccount == null) {
            Log.m209d(LOG_TAG, "MXOlmDevice : create a new olm account");
            try {
                this.mOlmAccount = new OlmAccount();
                this.mStore.storeAccount(this.mOlmAccount);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("MXOlmDevice : cannot initialize mOlmAccount ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        } else {
            Log.m209d(LOG_TAG, "MXOlmDevice : use an existing account");
        }
        try {
            this.mOlmUtility = new OlmUtility();
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## MXOlmDevice : OlmUtility failed with error ");
            sb2.append(e2.getMessage());
            Log.m211e(str2, sb2.toString());
            this.mOlmUtility = null;
        }
        this.mOutboundGroupSessionStore = new HashMap<>();
        try {
            this.mDeviceCurve25519Key = (String) this.mOlmAccount.identityKeys().get("curve25519");
        } catch (Exception e3) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## MXOlmDevice : cannot find curve25519 with error ");
            sb3.append(e3.getMessage());
            Log.m211e(str3, sb3.toString());
        }
        try {
            this.mDeviceEd25519Key = (String) this.mOlmAccount.identityKeys().get(OlmAccount.JSON_KEY_FINGER_PRINT_KEY);
        } catch (Exception e4) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## MXOlmDevice : cannot find ed25519 with error ");
            sb4.append(e4.getMessage());
            Log.m211e(str4, sb4.toString());
        }
        this.mInboundGroupSessionMessageIndexes = new HashMap<>();
    }

    public void release() {
        if (this.mOlmAccount != null) {
            this.mOlmAccount.releaseAccount();
        }
    }

    public String getDeviceCurve25519Key() {
        return this.mDeviceCurve25519Key;
    }

    public String getDeviceEd25519Key() {
        return this.mDeviceEd25519Key;
    }

    private String signMessage(String str) {
        try {
            return this.mOlmAccount.signMessage(str);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## signMessage() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return null;
        }
    }

    public String signJSON(Map<String, Object> map) {
        return signMessage(JsonUtils.getCanonicalizedJsonString(map));
    }

    public Map<String, Map<String, String>> getOneTimeKeys() {
        try {
            return this.mOlmAccount.oneTimeKeys();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getOneTimeKeys() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    public long getMaxNumberOfOneTimeKeys() {
        if (this.mOlmAccount != null) {
            return this.mOlmAccount.maxOneTimeKeys();
        }
        return -1;
    }

    public void markKeysAsPublished() {
        try {
            this.mOlmAccount.markOneTimeKeysAsPublished();
            this.mStore.storeAccount(this.mOlmAccount);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## markKeysAsPublished() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void generateOneTimeKeys(int i) {
        try {
            this.mOlmAccount.generateOneTimeKeys(i);
            this.mStore.storeAccount(this.mOlmAccount);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## generateOneTimeKeys() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0069  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String createOutboundSession(java.lang.String r5, java.lang.String r6) {
        /*
            r4 = this;
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## createOutboundSession() ; theirIdentityKey "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = " theirOneTimeKey "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            r0 = 0
            org.matrix.olm.OlmSession r1 = new org.matrix.olm.OlmSession     // Catch:{ Exception -> 0x004b }
            r1.<init>()     // Catch:{ Exception -> 0x004b }
            org.matrix.olm.OlmAccount r2 = r4.mOlmAccount     // Catch:{ Exception -> 0x0049 }
            r1.initOutboundSession(r2, r5, r6)     // Catch:{ Exception -> 0x0049 }
            com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore r6 = r4.mStore     // Catch:{ Exception -> 0x0049 }
            r6.storeSession(r1, r5)     // Catch:{ Exception -> 0x0049 }
            java.lang.String r5 = r1.sessionIdentifier()     // Catch:{ Exception -> 0x0049 }
            java.lang.String r6 = LOG_TAG     // Catch:{ Exception -> 0x0049 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0049 }
            r2.<init>()     // Catch:{ Exception -> 0x0049 }
            java.lang.String r3 = "## createOutboundSession() ;  olmSession.sessionIdentifier: "
            r2.append(r3)     // Catch:{ Exception -> 0x0049 }
            r2.append(r5)     // Catch:{ Exception -> 0x0049 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0049 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r6, r2)     // Catch:{ Exception -> 0x0049 }
            return r5
        L_0x0049:
            r5 = move-exception
            goto L_0x004d
        L_0x004b:
            r5 = move-exception
            r1 = r0
        L_0x004d:
            java.lang.String r6 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## createOutboundSession() failed ; "
            r2.append(r3)
            java.lang.String r5 = r5.getMessage()
            r2.append(r5)
            java.lang.String r5 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)
            if (r1 == 0) goto L_0x006c
            r1.releaseSession()
        L_0x006c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice.createOutboundSession(java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x009c */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e0 A[Catch:{ Exception -> 0x0114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ef A[Catch:{ Exception -> 0x0114 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.String, java.lang.String> createInboundSession(java.lang.String r7, int r8, java.lang.String r9) {
        /*
            r6 = this;
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## createInboundSession() : theirIdentityKey: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            r0 = 0
            org.matrix.olm.OlmSession r1 = new org.matrix.olm.OlmSession     // Catch:{ Exception -> 0x00f7 }
            r1.<init>()     // Catch:{ Exception -> 0x00f7 }
            org.matrix.olm.OlmAccount r2 = r6.mOlmAccount     // Catch:{ Exception -> 0x00f5 }
            r1.initInboundSessionFrom(r2, r7, r9)     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0114 }
            r3.<init>()     // Catch:{ Exception -> 0x0114 }
            java.lang.String r4 = "## createInboundSession() : sessionId: "
            r3.append(r4)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r4 = r1.sessionIdentifier()     // Catch:{ Exception -> 0x0114 }
            r3.append(r4)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0114 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ Exception -> 0x0114 }
            org.matrix.olm.OlmAccount r2 = r6.mOlmAccount     // Catch:{ Exception -> 0x0048 }
            r2.removeOneTimeKeys(r1)     // Catch:{ Exception -> 0x0048 }
            com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore r2 = r6.mStore     // Catch:{ Exception -> 0x0048 }
            org.matrix.olm.OlmAccount r3 = r6.mOlmAccount     // Catch:{ Exception -> 0x0048 }
            r2.storeAccount(r3)     // Catch:{ Exception -> 0x0048 }
            goto L_0x0063
        L_0x0048:
            r2 = move-exception
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0114 }
            r4.<init>()     // Catch:{ Exception -> 0x0114 }
            java.lang.String r5 = "## createInboundSession() : removeOneTimeKeys failed "
            r4.append(r5)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r2 = r2.getMessage()     // Catch:{ Exception -> 0x0114 }
            r4.append(r2)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x0114 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)     // Catch:{ Exception -> 0x0114 }
        L_0x0063:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0114 }
            r3.<init>()     // Catch:{ Exception -> 0x0114 }
            java.lang.String r4 = "## createInboundSession() : ciphertext: "
            r3.append(r4)     // Catch:{ Exception -> 0x0114 }
            r3.append(r9)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0114 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x009c }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x009c }
            r3.<init>()     // Catch:{ Exception -> 0x009c }
            java.lang.String r4 = "## createInboundSession() :ciphertext: SHA256:"
            r3.append(r4)     // Catch:{ Exception -> 0x009c }
            org.matrix.olm.OlmUtility r4 = r6.mOlmUtility     // Catch:{ Exception -> 0x009c }
            java.lang.String r5 = "utf-8"
            java.lang.String r5 = java.net.URLEncoder.encode(r9, r5)     // Catch:{ Exception -> 0x009c }
            java.lang.String r4 = r4.sha256(r5)     // Catch:{ Exception -> 0x009c }
            r3.append(r4)     // Catch:{ Exception -> 0x009c }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x009c }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ Exception -> 0x009c }
            goto L_0x00a3
        L_0x009c:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.String r3 = "## createInboundSession() :ciphertext: cannot encode ciphertext"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)     // Catch:{ Exception -> 0x0114 }
        L_0x00a3:
            org.matrix.olm.OlmMessage r2 = new org.matrix.olm.OlmMessage     // Catch:{ Exception -> 0x0114 }
            r2.<init>()     // Catch:{ Exception -> 0x0114 }
            r2.mCipherText = r9     // Catch:{ Exception -> 0x0114 }
            long r8 = (long) r8     // Catch:{ Exception -> 0x0114 }
            r2.mType = r8     // Catch:{ Exception -> 0x0114 }
            java.lang.String r8 = r1.decryptMessage(r2)     // Catch:{ Exception -> 0x00b9 }
            com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore r9 = r6.mStore     // Catch:{ Exception -> 0x00b7 }
            r9.storeSession(r1, r7)     // Catch:{ Exception -> 0x00b7 }
            goto L_0x00d5
        L_0x00b7:
            r7 = move-exception
            goto L_0x00bb
        L_0x00b9:
            r7 = move-exception
            r8 = r0
        L_0x00bb:
            java.lang.String r9 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0114 }
            r2.<init>()     // Catch:{ Exception -> 0x0114 }
            java.lang.String r3 = "## createInboundSession() : decryptMessage failed "
            r2.append(r3)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r7 = r7.getMessage()     // Catch:{ Exception -> 0x0114 }
            r2.append(r7)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x0114 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r9, r7)     // Catch:{ Exception -> 0x0114 }
        L_0x00d5:
            java.util.HashMap r7 = new java.util.HashMap     // Catch:{ Exception -> 0x0114 }
            r7.<init>()     // Catch:{ Exception -> 0x0114 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0114 }
            if (r9 != 0) goto L_0x00e5
            java.lang.String r9 = "payload"
            r7.put(r9, r8)     // Catch:{ Exception -> 0x0114 }
        L_0x00e5:
            java.lang.String r8 = r1.sessionIdentifier()     // Catch:{ Exception -> 0x0114 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0114 }
            if (r9 != 0) goto L_0x00f4
            java.lang.String r9 = "session_id"
            r7.put(r9, r8)     // Catch:{ Exception -> 0x0114 }
        L_0x00f4:
            return r7
        L_0x00f5:
            r7 = move-exception
            goto L_0x00f9
        L_0x00f7:
            r7 = move-exception
            r1 = r0
        L_0x00f9:
            java.lang.String r8 = LOG_TAG     // Catch:{ Exception -> 0x0114 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0114 }
            r9.<init>()     // Catch:{ Exception -> 0x0114 }
            java.lang.String r2 = "## createInboundSession() : the session creation failed "
            r9.append(r2)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r7 = r7.getMessage()     // Catch:{ Exception -> 0x0114 }
            r9.append(r7)     // Catch:{ Exception -> 0x0114 }
            java.lang.String r7 = r9.toString()     // Catch:{ Exception -> 0x0114 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r8, r7)     // Catch:{ Exception -> 0x0114 }
            return r0
        L_0x0114:
            r7 = move-exception
            java.lang.String r8 = LOG_TAG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r2 = "## createInboundSession() : OlmSession creation failed "
            r9.append(r2)
            java.lang.String r7 = r7.getMessage()
            r9.append(r7)
            java.lang.String r7 = r9.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r7)
            if (r1 == 0) goto L_0x0134
            r1.releaseSession()
        L_0x0134:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice.createInboundSession(java.lang.String, int, java.lang.String):java.util.Map");
    }

    public Set<String> getSessionIds(String str) {
        Map deviceSessions = this.mStore.getDeviceSessions(str);
        if (deviceSessions != null) {
            return deviceSessions.keySet();
        }
        return null;
    }

    public String getSessionId(String str) {
        Set sessionIds = getSessionIds(str);
        if (sessionIds == null || sessionIds.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList(sessionIds);
        Collections.sort(arrayList);
        return (String) arrayList.get(0);
    }

    public Map<String, Object> encryptMessage(String str, String str2, String str3) {
        OlmSession sessionForDevice = getSessionForDevice(str, str2);
        HashMap hashMap = null;
        if (sessionForDevice == null) {
            return null;
        }
        try {
            String str4 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## encryptMessage() : olmSession.sessionIdentifier: ");
            sb.append(sessionForDevice.sessionIdentifier());
            Log.m209d(str4, sb.toString());
            OlmMessage encryptMessage = sessionForDevice.encryptMessage(str3);
            this.mStore.storeSession(sessionForDevice, str);
            HashMap hashMap2 = new HashMap();
            try {
                hashMap2.put("body", encryptMessage.mCipherText);
                hashMap2.put("type", Long.valueOf(encryptMessage.mType));
                return hashMap2;
            } catch (Exception e) {
                e = e;
                hashMap = hashMap2;
                String str5 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## encryptMessage() : failed ");
                sb2.append(e.getMessage());
                Log.m211e(str5, sb2.toString());
                return hashMap;
            }
        } catch (Exception e2) {
            e = e2;
            String str52 = LOG_TAG;
            StringBuilder sb22 = new StringBuilder();
            sb22.append("## encryptMessage() : failed ");
            sb22.append(e.getMessage());
            Log.m211e(str52, sb22.toString());
            return hashMap;
        }
    }

    public String decryptMessage(String str, int i, String str2, String str3) {
        OlmSession sessionForDevice = getSessionForDevice(str3, str2);
        String str4 = null;
        if (sessionForDevice == null) {
            return null;
        }
        OlmMessage olmMessage = new OlmMessage();
        olmMessage.mCipherText = str;
        olmMessage.mType = (long) i;
        try {
            String decryptMessage = sessionForDevice.decryptMessage(olmMessage);
            try {
                this.mStore.storeSession(sessionForDevice, str3);
                return decryptMessage;
            } catch (Exception e) {
                e = e;
                str4 = decryptMessage;
                String str5 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## decryptMessage() : decryptMessage failed ");
                sb.append(e.getMessage());
                Log.m211e(str5, sb.toString());
                return str4;
            }
        } catch (Exception e2) {
            e = e2;
            String str52 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## decryptMessage() : decryptMessage failed ");
            sb2.append(e.getMessage());
            Log.m211e(str52, sb2.toString());
            return str4;
        }
    }

    public boolean matchesSession(String str, String str2, int i, String str3) {
        boolean z = false;
        if (i != 0) {
            return false;
        }
        OlmSession sessionForDevice = getSessionForDevice(str, str2);
        if (sessionForDevice != null && sessionForDevice.matchesInboundSession(str3)) {
            z = true;
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0034  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String createOutboundGroupSession() {
        /*
            r6 = this;
            r0 = 0
            org.matrix.olm.OlmOutboundGroupSession r1 = new org.matrix.olm.OlmOutboundGroupSession     // Catch:{ Exception -> 0x0016 }
            r1.<init>()     // Catch:{ Exception -> 0x0016 }
            java.util.HashMap<java.lang.String, org.matrix.olm.OlmOutboundGroupSession> r2 = r6.mOutboundGroupSessionStore     // Catch:{ Exception -> 0x0014 }
            java.lang.String r3 = r1.sessionIdentifier()     // Catch:{ Exception -> 0x0014 }
            r2.put(r3, r1)     // Catch:{ Exception -> 0x0014 }
            java.lang.String r2 = r1.sessionIdentifier()     // Catch:{ Exception -> 0x0014 }
            return r2
        L_0x0014:
            r2 = move-exception
            goto L_0x0018
        L_0x0016:
            r2 = move-exception
            r1 = r0
        L_0x0018:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "createOutboundGroupSession "
            r4.append(r5)
            java.lang.String r2 = r2.getMessage()
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)
            if (r1 == 0) goto L_0x0037
            r1.releaseSession()
        L_0x0037:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice.createOutboundGroupSession():java.lang.String");
    }

    public String getSessionKey(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                return ((OlmOutboundGroupSession) this.mOutboundGroupSessionStore.get(str)).sessionKey();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getSessionKey() : failed ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        return null;
    }

    public int getMessageIndex(String str) {
        if (!TextUtils.isEmpty(str)) {
            return ((OlmOutboundGroupSession) this.mOutboundGroupSessionStore.get(str)).messageIndex();
        }
        return 0;
    }

    public String encryptGroupMessage(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                return ((OlmOutboundGroupSession) this.mOutboundGroupSessionStore.get(str)).encryptMessage(str2);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptGroupMessage() : failed ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
            }
        }
        return null;
    }

    public boolean addInboundGroupSession(String str, String str2, String str3, String str4, List<String> list, Map<String, String> map, boolean z) {
        if (getInboundGroupSession(str, str4, str3) != null) {
            String str5 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## addInboundGroupSession() : Update for megolm session ");
            sb.append(str4);
            sb.append("/");
            sb.append(str);
            Log.m211e(str5, sb.toString());
            return false;
        }
        MXOlmInboundGroupSession2 mXOlmInboundGroupSession2 = new MXOlmInboundGroupSession2(str2, z);
        if (mXOlmInboundGroupSession2.mSession == null) {
            Log.m211e(LOG_TAG, "## addInboundGroupSession : invalid session");
            return false;
        }
        try {
            if (!TextUtils.equals(mXOlmInboundGroupSession2.mSession.sessionIdentifier(), str)) {
                String str6 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## addInboundGroupSession : ERROR: Mismatched group session ID from senderKey: ");
                sb2.append(str4);
                Log.m211e(str6, sb2.toString());
                return false;
            }
            mXOlmInboundGroupSession2.mSenderKey = str4;
            mXOlmInboundGroupSession2.mRoomId = str3;
            mXOlmInboundGroupSession2.mKeysClaimed = map;
            mXOlmInboundGroupSession2.mForwardingCurve25519KeyChain = list;
            this.mStore.storeInboundGroupSession(mXOlmInboundGroupSession2);
            return true;
        } catch (Exception e) {
            String str7 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## addInboundGroupSession : sessionIdentifier') failed ");
            sb3.append(e.getMessage());
            Log.m211e(str7, sb3.toString());
            return false;
        }
    }

    public MXOlmInboundGroupSession2 importInboundGroupSession(Map<String, Object> map) {
        MXOlmInboundGroupSession2 mXOlmInboundGroupSession2;
        String str = (String) map.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID);
        String str2 = (String) map.get("sender_key");
        if (getInboundGroupSession(str, str2, (String) map.get("room_id")) != null) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## importInboundGroupSession() : Update for megolm session ");
            sb.append(str2);
            sb.append("/");
            sb.append(str);
            Log.m211e(str3, sb.toString());
            return null;
        }
        try {
            mXOlmInboundGroupSession2 = new MXOlmInboundGroupSession2(map);
        } catch (Exception unused) {
            String str4 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## importInboundGroupSession() : Update for megolm session ");
            sb2.append(str2);
            sb2.append("/");
            sb2.append(str);
            Log.m211e(str4, sb2.toString());
            mXOlmInboundGroupSession2 = null;
        }
        if (mXOlmInboundGroupSession2 == null || mXOlmInboundGroupSession2.mSession == null) {
            Log.m211e(LOG_TAG, "## importInboundGroupSession : invalid session");
            return null;
        }
        try {
            if (!TextUtils.equals(mXOlmInboundGroupSession2.mSession.sessionIdentifier(), str)) {
                String str5 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## importInboundGroupSession : ERROR: Mismatched group session ID from senderKey: ");
                sb3.append(str2);
                Log.m211e(str5, sb3.toString());
                return null;
            }
            this.mStore.storeInboundGroupSession(mXOlmInboundGroupSession2);
            return mXOlmInboundGroupSession2;
        } catch (Exception e) {
            String str6 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## importInboundGroupSession : sessionIdentifier') failed ");
            sb4.append(e.getMessage());
            Log.m211e(str6, sb4.toString());
            return null;
        }
    }

    public void removeInboundGroupSession(String str, String str2) {
        if (str != null && str2 != null) {
            this.mStore.removeInboundGroupSession(str, str2);
        }
    }

    public MXDecryptionResult decryptGroupMessage(String str, String str2, String str3, String str4, String str5) throws MXDecryptionException {
        DecryptMessageResult decryptMessageResult;
        MXDecryptionResult mXDecryptionResult = new MXDecryptionResult();
        MXOlmInboundGroupSession2 inboundGroupSession = getInboundGroupSession(str4, str5, str2);
        if (inboundGroupSession == null) {
            String str6 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## decryptGroupMessage() : Cannot retrieve inbound group session ");
            sb.append(str4);
            Log.m211e(str6, sb.toString());
            throw new MXDecryptionException(this.mInboundGroupSessionWithIdError);
        } else if (TextUtils.equals(str2, inboundGroupSession.mRoomId)) {
            String str7 = "";
            try {
                decryptMessageResult = inboundGroupSession.mSession.decryptMessage(str);
            } catch (Exception e) {
                String str8 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## decryptGroupMessage () : decryptMessage failed ");
                sb2.append(e.getMessage());
                Log.m211e(str8, sb2.toString());
                str7 = e.getMessage();
                decryptMessageResult = null;
            }
            if (decryptMessageResult != null) {
                if (str3 != null) {
                    if (!this.mInboundGroupSessionMessageIndexes.containsKey(str3)) {
                        this.mInboundGroupSessionMessageIndexes.put(str3, new HashMap());
                    }
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str5);
                    sb3.append("|");
                    sb3.append(str4);
                    sb3.append("|");
                    sb3.append(decryptMessageResult.mIndex);
                    String sb4 = sb3.toString();
                    if (((HashMap) this.mInboundGroupSessionMessageIndexes.get(str3)).get(sb4) != null) {
                        String format = String.format(MXCryptoError.DUPLICATE_MESSAGE_INDEX_REASON, new Object[]{Long.valueOf(decryptMessageResult.mIndex)});
                        String str9 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## decryptGroupMessage() : ");
                        sb5.append(format);
                        Log.m211e(str9, sb5.toString());
                        throw new MXDecryptionException(new MXCryptoError(MXCryptoError.DUPLICATED_MESSAGE_INDEX_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, format));
                    }
                    ((HashMap) this.mInboundGroupSessionMessageIndexes.get(str3)).put(sb4, Boolean.valueOf(true));
                }
                this.mStore.storeInboundGroupSession(inboundGroupSession);
                try {
                    mXDecryptionResult.mPayload = new JsonParser().parse(JsonUtils.convertFromUTF8(decryptMessageResult.mDecryptedMessage));
                    if (mXDecryptionResult.mPayload == null) {
                        Log.m211e(LOG_TAG, "## decryptGroupMessage() : fails to parse the payload");
                        return null;
                    }
                    mXDecryptionResult.mKeysClaimed = inboundGroupSession.mKeysClaimed;
                    mXDecryptionResult.mSenderKey = str5;
                    mXDecryptionResult.mForwardingCurve25519KeyChain = inboundGroupSession.mForwardingCurve25519KeyChain;
                    return mXDecryptionResult;
                } catch (Exception e2) {
                    String str10 = LOG_TAG;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("## decryptGroupMessage() : RLEncoder.encode failed ");
                    sb6.append(e2.getMessage());
                    Log.m211e(str10, sb6.toString());
                    return null;
                }
            } else {
                Log.m211e(LOG_TAG, "## decryptGroupMessage() : failed to decode the message");
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.OLM_ERROR_CODE, str7, null));
            }
        } else {
            String format2 = String.format(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_REASON, new Object[]{str2, inboundGroupSession.mRoomId});
            String str11 = LOG_TAG;
            StringBuilder sb7 = new StringBuilder();
            sb7.append("## decryptGroupMessage() : ");
            sb7.append(format2);
            Log.m211e(str11, sb7.toString());
            throw new MXDecryptionException(new MXCryptoError(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, format2));
        }
    }

    public void resetReplayAttackCheckInTimeline(String str) {
        if (str != null) {
            this.mInboundGroupSessionMessageIndexes.remove(str);
        }
    }

    public void verifySignature(String str, Map<String, Object> map, String str2) throws Exception {
        this.mOlmUtility.verifyEd25519Signature(str2, str, JsonUtils.getCanonicalizedJsonString(map));
    }

    public String sha256(String str) {
        return this.mOlmUtility.sha256(JsonUtils.convertToUTF8(str));
    }

    private OlmSession getSessionForDevice(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            Map deviceSessions = this.mStore.getDeviceSessions(str);
            if (deviceSessions != null) {
                return (OlmSession) deviceSessions.get(str2);
            }
        }
        return null;
    }

    public MXOlmInboundGroupSession2 getInboundGroupSession(String str, String str2, String str3) {
        this.mInboundGroupSessionWithIdError = null;
        MXOlmInboundGroupSession2 inboundGroupSession = this.mStore.getInboundGroupSession(str, str2);
        if (inboundGroupSession == null) {
            String str4 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getInboundGroupSession() : Cannot retrieve inbound group session ");
            sb.append(str);
            Log.m211e(str4, sb.toString());
            this.mInboundGroupSessionWithIdError = new MXCryptoError(MXCryptoError.UNKNOWN_INBOUND_SESSION_ID_ERROR_CODE, MXCryptoError.UNKNOWN_INBOUND_SESSION_ID_REASON, null);
        } else if (!TextUtils.equals(str3, inboundGroupSession.mRoomId)) {
            String format = String.format(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_REASON, new Object[]{str3, inboundGroupSession.mRoomId});
            String str5 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getInboundGroupSession() : ");
            sb2.append(format);
            Log.m211e(str5, sb2.toString());
            this.mInboundGroupSessionWithIdError = new MXCryptoError(MXCryptoError.INBOUND_SESSION_MISMATCH_ROOM_ID_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, format);
        }
        return inboundGroupSession;
    }

    public boolean hasInboundSessionKeys(String str, String str2, String str3) {
        return getInboundGroupSession(str3, str2, str) != null;
    }
}
