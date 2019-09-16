package com.opengarden.firechat.matrixsdk.crypto.data;

import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MXDeviceInfo implements Serializable {
    public static final int DEVICE_VERIFICATION_BLOCKED = 2;
    public static final int DEVICE_VERIFICATION_UNKNOWN = -1;
    public static final int DEVICE_VERIFICATION_UNVERIFIED = 0;
    public static final int DEVICE_VERIFICATION_VERIFIED = 1;
    private static final long serialVersionUID = 20129670646382964L;
    public List<String> algorithms;
    public String deviceId;
    public Map<String, String> keys;
    public int mVerified = -1;
    public Map<String, Map<String, String>> signatures;
    public Map<String, Object> unsigned;
    public String userId;

    public MXDeviceInfo() {
    }

    public MXDeviceInfo(String str) {
        this.deviceId = str;
    }

    public boolean isUnknown() {
        return this.mVerified == -1;
    }

    public boolean isVerified() {
        return this.mVerified == 1;
    }

    public boolean isUnverified() {
        return this.mVerified == 0;
    }

    public boolean isBlocked() {
        return this.mVerified == 2;
    }

    public String fingerprint() {
        if (this.keys == null || TextUtils.isEmpty(this.deviceId)) {
            return null;
        }
        Map<String, String> map = this.keys;
        StringBuilder sb = new StringBuilder();
        sb.append("ed25519:");
        sb.append(this.deviceId);
        return (String) map.get(sb.toString());
    }

    public String identityKey() {
        if (this.keys == null || TextUtils.isEmpty(this.deviceId)) {
            return null;
        }
        Map<String, String> map = this.keys;
        StringBuilder sb = new StringBuilder();
        sb.append("curve25519:");
        sb.append(this.deviceId);
        return (String) map.get(sb.toString());
    }

    public String displayName() {
        if (this.unsigned != null) {
            return (String) this.unsigned.get("device_display_name");
        }
        return null;
    }

    public Map<String, Object> signalableJSONDictionary() {
        HashMap hashMap = new HashMap();
        hashMap.put(AmplitudeClient.DEVICE_ID_KEY, this.deviceId);
        if (this.userId != null) {
            hashMap.put(AmplitudeClient.USER_ID_KEY, this.userId);
        }
        if (this.algorithms != null) {
            hashMap.put("algorithms", this.algorithms);
        }
        if (this.keys != null) {
            hashMap.put("keys", this.keys);
        }
        return hashMap;
    }

    public Map<String, Object> JSONDictionary() {
        HashMap hashMap = new HashMap();
        hashMap.put(AmplitudeClient.DEVICE_ID_KEY, this.deviceId);
        if (this.userId != null) {
            hashMap.put(AmplitudeClient.USER_ID_KEY, this.userId);
        }
        if (this.algorithms != null) {
            hashMap.put("algorithms", this.algorithms);
        }
        if (this.keys != null) {
            hashMap.put("keys", this.keys);
        }
        if (this.signatures != null) {
            hashMap.put("signatures", this.signatures);
        }
        if (this.unsigned != null) {
            hashMap.put("unsigned", this.unsigned);
        }
        return hashMap;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MXDeviceInfo ");
        sb.append(this.userId);
        sb.append(":");
        sb.append(this.deviceId);
        return sb.toString();
    }
}
