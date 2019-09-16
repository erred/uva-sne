package com.opengarden.firechat.matrixsdk.crypto.data;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoAlgorithms;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matrix.olm.OlmAccount;
import org.matrix.olm.OlmInboundGroupSession;

public class MXOlmInboundGroupSession2 implements Serializable {
    private static final String LOG_TAG = "OlmInboundGroupSession";
    private static final long serialVersionUID = 201702011617L;
    public List<String> mForwardingCurve25519KeyChain = new ArrayList();
    public Map<String, String> mKeysClaimed;
    public String mRoomId;
    public String mSenderKey;
    public OlmInboundGroupSession mSession;

    public MXOlmInboundGroupSession2(MXOlmInboundGroupSession mXOlmInboundGroupSession) {
        this.mSession = mXOlmInboundGroupSession.mSession;
        this.mRoomId = mXOlmInboundGroupSession.mRoomId;
        this.mSenderKey = mXOlmInboundGroupSession.mSenderKey;
        this.mKeysClaimed = mXOlmInboundGroupSession.mKeysClaimed;
    }

    public MXOlmInboundGroupSession2(String str, boolean z) {
        if (!z) {
            try {
                this.mSession = new OlmInboundGroupSession(str);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot create : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        } else {
            this.mSession = OlmInboundGroupSession.importSession(str);
        }
    }

    public MXOlmInboundGroupSession2(Map<String, Object> map) throws Exception {
        try {
            this.mSession = OlmInboundGroupSession.importSession((String) map.get("session_key"));
            if (!TextUtils.equals(this.mSession.sessionIdentifier(), (String) map.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID))) {
                throw new Exception("Mismatched group session Id");
            }
            this.mSenderKey = (String) map.get("sender_key");
            this.mKeysClaimed = (Map) map.get("sender_claimed_keys");
            this.mRoomId = (String) map.get("room_id");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Map<String, Object> exportKeys() {
        HashMap hashMap = new HashMap();
        try {
            if (this.mForwardingCurve25519KeyChain == null) {
                this.mForwardingCurve25519KeyChain = new ArrayList();
            }
            hashMap.put("sender_claimed_ed25519_key", this.mKeysClaimed.get(OlmAccount.JSON_KEY_FINGER_PRINT_KEY));
            hashMap.put("forwardingCurve25519KeyChain", this.mForwardingCurve25519KeyChain);
            hashMap.put("sender_key", this.mSenderKey);
            hashMap.put("sender_claimed_keys", this.mKeysClaimed);
            hashMap.put("room_id", this.mRoomId);
            hashMap.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, this.mSession.sessionIdentifier());
            hashMap.put("session_key", this.mSession.export(this.mSession.getFirstKnownIndex()));
            hashMap.put("algorithm", MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_MEGOLM);
            return hashMap;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## export() : senderKey ");
            sb.append(this.mSenderKey);
            sb.append(" failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    public Long getFirstKnownIndex() {
        if (this.mSession != null) {
            try {
                return Long.valueOf(this.mSession.getFirstKnownIndex());
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getFirstKnownIndex() : getFirstKnownIndex failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return null;
    }

    public String exportSession(long j) {
        if (this.mSession != null) {
            try {
                return this.mSession.export(j);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## exportSession() : export failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return null;
    }
}
