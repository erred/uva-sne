package com.opengarden.firechat.matrixsdk.crypto.data;

import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Serializable;
import java.util.Map;
import org.matrix.olm.OlmInboundGroupSession;

public class MXOlmInboundGroupSession implements Serializable {
    private static final String LOG_TAG = "OlmInboundGroupSession";
    public Map<String, String> mKeysClaimed;
    public String mRoomId;
    public String mSenderKey;
    public OlmInboundGroupSession mSession;

    public MXOlmInboundGroupSession(String str) {
        try {
            this.mSession = new OlmInboundGroupSession(str);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot create : ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }
}
