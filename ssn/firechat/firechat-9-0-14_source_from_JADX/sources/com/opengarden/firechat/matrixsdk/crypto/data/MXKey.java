package com.opengarden.firechat.matrixsdk.crypto.data;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MXKey implements Serializable {
    public static final String KEY_CURVE_25519_TYPE = "curve25519";
    public static final String KEY_SIGNED_CURVE_25519_TYPE = "signed_curve25519";
    private static final String LOG_TAG = "MXKey";
    public String keyId;
    public Map<String, Map<String, String>> signatures;
    public String type;
    public String value;

    public MXKey() {
    }

    public MXKey(Map<String, Map<String, Object>> map) {
        if (map != null && map.size() > 0) {
            String str = (String) new ArrayList(map.keySet()).get(0);
            setKeyFullId(str);
            Map map2 = (Map) map.get(str);
            this.value = (String) map2.get("key");
            this.signatures = (Map) map2.get("signatures");
        }
    }

    public String getKeyFullId() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        sb.append(":");
        sb.append(this.keyId);
        return sb.toString();
    }

    private void setKeyFullId(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                String[] split = str.split(":");
                if (split.length == 2) {
                    this.type = split[0];
                    this.keyId = split[1];
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setKeyFullId() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    public Map<String, Object> signalableJSONDictionary() {
        HashMap hashMap = new HashMap();
        if (this.value != null) {
            hashMap.put("key", this.value);
        }
        return hashMap;
    }

    public String signatureForUserId(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || this.signatures == null || !this.signatures.containsKey(str)) {
            return null;
        }
        return (String) ((Map) this.signatures.get(str)).get(str2);
    }
}
