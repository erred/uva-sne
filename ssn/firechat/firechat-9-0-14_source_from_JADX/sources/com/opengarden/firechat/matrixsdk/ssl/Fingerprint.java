package com.opengarden.firechat.matrixsdk.ssl;

import android.util.Base64;
import com.google.android.gms.common.util.AndroidUtilsLight;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

public class Fingerprint {
    private final byte[] mBytes;
    private String mDisplayableHexRepr = null;
    private final HashType mHashType;

    public enum HashType {
        SHA1,
        SHA256
    }

    public Fingerprint(byte[] bArr, HashType hashType) {
        this.mBytes = bArr;
        this.mHashType = hashType;
    }

    public static Fingerprint newSha256Fingerprint(X509Certificate x509Certificate) throws CertificateException {
        return new Fingerprint(CertUtil.generateSha256Fingerprint(x509Certificate), HashType.SHA256);
    }

    public static Fingerprint newSha1Fingerprint(X509Certificate x509Certificate) throws CertificateException {
        return new Fingerprint(CertUtil.generateSha1Fingerprint(x509Certificate), HashType.SHA1);
    }

    public HashType getType() {
        return this.mHashType;
    }

    public byte[] getBytes() {
        return this.mBytes;
    }

    public String getBytesAsHexString() {
        if (this.mDisplayableHexRepr == null) {
            this.mDisplayableHexRepr = CertUtil.fingerprintToHexString(this.mBytes);
        }
        return this.mDisplayableHexRepr;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("bytes", Base64.encodeToString(getBytes(), 0));
        jSONObject.put("hash_type", this.mHashType.toString());
        return jSONObject;
    }

    public static Fingerprint fromJson(JSONObject jSONObject) throws JSONException {
        HashType hashType;
        String string = jSONObject.getString("hash_type");
        byte[] decode = Base64.decode(jSONObject.getString("bytes"), 0);
        if ("SHA256".equalsIgnoreCase(string)) {
            hashType = HashType.SHA256;
        } else if (AndroidUtilsLight.DIGEST_ALGORITHM_SHA1.equalsIgnoreCase(string)) {
            hashType = HashType.SHA1;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unrecognized hash type: ");
            sb.append(string);
            throw new JSONException(sb.toString());
        }
        return new Fingerprint(decode, hashType);
    }

    public boolean matchesCert(X509Certificate x509Certificate) throws CertificateException {
        Fingerprint fingerprint;
        switch (this.mHashType) {
            case SHA256:
                fingerprint = newSha256Fingerprint(x509Certificate);
                break;
            case SHA1:
                fingerprint = newSha1Fingerprint(x509Certificate);
                break;
            default:
                fingerprint = null;
                break;
        }
        return equals(fingerprint);
    }

    public String toString() {
        return String.format("Fingerprint{type: '%s', fingeprint: '%s'}", new Object[]{this.mHashType.toString(), getBytesAsHexString()});
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Fingerprint fingerprint = (Fingerprint) obj;
        if (!Arrays.equals(this.mBytes, fingerprint.mBytes)) {
            return false;
        }
        if (this.mHashType != fingerprint.mHashType) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.mBytes != null ? Arrays.hashCode(this.mBytes) : 0) * 31;
        if (this.mHashType != null) {
            i = this.mHashType.hashCode();
        }
        return hashCode + i;
    }
}
