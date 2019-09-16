package com.opengarden.firechat.matrixsdk;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.facebook.common.util.UriUtil;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.ssl.Fingerprint;
import com.opengarden.firechat.repositories.ServerUrlsRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.CipherSuite;
import okhttp3.TlsVersion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeServerConnectionConfig {
    private boolean mAllowHttpExtension;
    private List<Fingerprint> mAllowedFingerprints;
    private Uri mAntiVirusServerUri;
    private Credentials mCredentials;
    private Uri mHsUri;
    private Uri mIdentityServerUri;
    private boolean mPin;
    private boolean mShouldAcceptTlsExtensions;
    private List<CipherSuite> mTlsCipherSuites;
    private List<TlsVersion> mTlsVersions;

    public HomeServerConnectionConfig(Uri uri) {
        this(uri, null);
    }

    public HomeServerConnectionConfig(Uri uri, @Nullable Credentials credentials) {
        this(uri, null, credentials, new ArrayList(), false);
    }

    public HomeServerConnectionConfig(Uri uri, @Nullable Uri uri2, @Nullable Credentials credentials, List<Fingerprint> list, boolean z) {
        this.mAllowedFingerprints = new ArrayList();
        if (uri == null || (!UriUtil.HTTP_SCHEME.equals(uri.getScheme()) && !UriUtil.HTTPS_SCHEME.equals(uri.getScheme()))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid home server URI: ");
            sb.append(uri);
            throw new RuntimeException(sb.toString());
        } else if (uri2 == null || UriUtil.HTTP_SCHEME.equals(uri.getScheme()) || UriUtil.HTTPS_SCHEME.equals(uri.getScheme())) {
            if (uri.toString().endsWith("/")) {
                try {
                    String uri3 = uri.toString();
                    uri = Uri.parse(uri3.substring(0, uri3.length() - 1));
                } catch (Exception unused) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Invalid home server URI: ");
                    sb2.append(uri);
                    throw new RuntimeException(sb2.toString());
                }
            }
            if (uri2 != null && uri2.toString().endsWith("/")) {
                try {
                    String uri4 = uri2.toString();
                    uri2 = Uri.parse(uri4.substring(0, uri4.length() - 1));
                } catch (Exception unused2) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Invalid identity server URI: ");
                    sb3.append(uri2);
                    throw new RuntimeException(sb3.toString());
                }
            }
            this.mHsUri = uri;
            this.mIdentityServerUri = uri2;
            this.mAntiVirusServerUri = null;
            if (list != null) {
                this.mAllowedFingerprints = list;
            }
            this.mPin = z;
            this.mCredentials = credentials;
            this.mShouldAcceptTlsExtensions = true;
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Invalid identity server URI: ");
            sb4.append(uri2);
            throw new RuntimeException(sb4.toString());
        }
    }

    public void setHomeserverUri(Uri uri) {
        this.mHsUri = uri;
    }

    public Uri getHomeserverUri() {
        return this.mHsUri;
    }

    public void setIdentityServerUri(Uri uri) {
        this.mIdentityServerUri = uri;
    }

    public Uri getIdentityServerUri() {
        if (this.mIdentityServerUri != null) {
            return this.mIdentityServerUri;
        }
        return this.mHsUri;
    }

    public void setAntiVirusServerUri(Uri uri) {
        this.mAntiVirusServerUri = uri;
    }

    public Uri getAntiVirusServerUri() {
        if (this.mAntiVirusServerUri != null) {
            return this.mAntiVirusServerUri;
        }
        return this.mHsUri;
    }

    public List<Fingerprint> getAllowedFingerprints() {
        return this.mAllowedFingerprints;
    }

    public Credentials getCredentials() {
        return this.mCredentials;
    }

    public void setCredentials(Credentials credentials) {
        this.mCredentials = credentials;
    }

    public boolean shouldPin() {
        return this.mPin;
    }

    public void setAcceptedTlsVersions(List<TlsVersion> list) {
        if (list != null) {
            this.mTlsVersions = Collections.unmodifiableList(list);
        }
    }

    public List<TlsVersion> getAcceptedTlsVersions() {
        return this.mTlsVersions;
    }

    public void setAcceptedTlsCipherSuites(List<CipherSuite> list) {
        if (list != null) {
            this.mTlsCipherSuites = Collections.unmodifiableList(list);
        }
    }

    public List<CipherSuite> getAcceptedTlsCipherSuites() {
        return this.mTlsCipherSuites;
    }

    public void setShouldAcceptTlsExtensions(boolean z) {
        this.mShouldAcceptTlsExtensions = z;
    }

    public boolean shouldAcceptTlsExtensions() {
        return this.mShouldAcceptTlsExtensions;
    }

    @VisibleForTesting
    public void allowHttpConnection() {
        this.mAllowHttpExtension = true;
    }

    public boolean isHttpConnectionAllowed() {
        return this.mAllowHttpExtension;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HomeserverConnectionConfig{mHsUri=");
        sb.append(this.mHsUri);
        sb.append(", mIdentityServerUri=");
        sb.append(this.mIdentityServerUri);
        sb.append(", mAntiVirusServerUri=");
        sb.append(this.mAntiVirusServerUri);
        sb.append(", mAllowedFingerprints size=");
        sb.append(this.mAllowedFingerprints.size());
        sb.append(", mCredentials=");
        sb.append(this.mCredentials);
        sb.append(", mPin=");
        sb.append(this.mPin);
        sb.append('}');
        return sb.toString();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(ServerUrlsRepository.HOME_SERVER_URL_PREF, this.mHsUri.toString());
        jSONObject.put(ServerUrlsRepository.IDENTITY_SERVER_URL_PREF, getIdentityServerUri().toString());
        if (this.mAntiVirusServerUri != null) {
            jSONObject.put("antivirus_server_url", this.mAntiVirusServerUri.toString());
        }
        jSONObject.put("pin", this.mPin);
        if (this.mCredentials != null) {
            jSONObject.put("credentials", this.mCredentials.toJson());
        }
        if (this.mAllowedFingerprints != null) {
            ArrayList arrayList = new ArrayList(this.mAllowedFingerprints.size());
            for (Fingerprint json : this.mAllowedFingerprints) {
                arrayList.add(json.toJson());
            }
            jSONObject.put("fingerprints", new JSONArray(arrayList));
        }
        jSONObject.put("tls_extensions", this.mShouldAcceptTlsExtensions);
        if (this.mTlsVersions != null) {
            ArrayList arrayList2 = new ArrayList(this.mTlsVersions.size());
            for (TlsVersion javaName : this.mTlsVersions) {
                arrayList2.add(javaName.javaName());
            }
            jSONObject.put("tls_versions", new JSONArray(arrayList2));
        }
        if (this.mTlsCipherSuites != null) {
            ArrayList arrayList3 = new ArrayList(this.mTlsCipherSuites.size());
            for (CipherSuite javaName2 : this.mTlsCipherSuites) {
                arrayList3.add(javaName2.javaName());
            }
            jSONObject.put("tls_cipher_suites", new JSONArray(arrayList3));
        }
        return jSONObject;
    }

    public static HomeServerConnectionConfig fromJson(JSONObject jSONObject) throws JSONException {
        JSONArray optJSONArray = jSONObject.optJSONArray("fingerprints");
        ArrayList arrayList = new ArrayList();
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                arrayList.add(Fingerprint.fromJson(optJSONArray.getJSONObject(i)));
            }
        }
        JSONObject optJSONObject = jSONObject.optJSONObject("credentials");
        HomeServerConnectionConfig homeServerConnectionConfig = new HomeServerConnectionConfig(Uri.parse(jSONObject.getString(ServerUrlsRepository.HOME_SERVER_URL_PREF)), jSONObject.has(ServerUrlsRepository.IDENTITY_SERVER_URL_PREF) ? Uri.parse(jSONObject.getString(ServerUrlsRepository.IDENTITY_SERVER_URL_PREF)) : null, optJSONObject != null ? Credentials.fromJson(optJSONObject) : null, arrayList, jSONObject.optBoolean("pin", false));
        if (jSONObject.has("antivirus_server_url")) {
            homeServerConnectionConfig.setAntiVirusServerUri(Uri.parse(jSONObject.getString("antivirus_server_url")));
        }
        homeServerConnectionConfig.setShouldAcceptTlsExtensions(jSONObject.optBoolean("tls_extensions", true));
        if (jSONObject.has("tls_versions")) {
            ArrayList arrayList2 = new ArrayList();
            JSONArray optJSONArray2 = jSONObject.optJSONArray("tls_versions");
            if (optJSONArray2 != null) {
                for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                    arrayList2.add(TlsVersion.forJavaName(optJSONArray2.getString(i2)));
                }
            }
            homeServerConnectionConfig.setAcceptedTlsVersions(arrayList2);
        } else {
            homeServerConnectionConfig.setAcceptedTlsVersions(null);
        }
        if (jSONObject.has("tls_cipher_suites")) {
            ArrayList arrayList3 = new ArrayList();
            JSONArray optJSONArray3 = jSONObject.optJSONArray("tls_cipher_suites");
            if (optJSONArray3 != null) {
                for (int i3 = 0; i3 < optJSONArray3.length(); i3++) {
                    arrayList3.add(CipherSuite.forJavaName(optJSONArray3.getString(i3)));
                }
            }
            homeServerConnectionConfig.setAcceptedTlsCipherSuites(arrayList3);
        } else {
            homeServerConnectionConfig.setAcceptedTlsCipherSuites(null);
        }
        return homeServerConnectionConfig;
    }
}
