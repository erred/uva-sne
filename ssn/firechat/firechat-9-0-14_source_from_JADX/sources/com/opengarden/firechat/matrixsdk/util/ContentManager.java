package com.opengarden.firechat.matrixsdk.util;

import android.support.annotation.Nullable;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import java.net.URLEncoder;

public class ContentManager {
    private static final String LOG_TAG = "ContentManager";
    public static final String MATRIX_CONTENT_IDENTICON_PREFIX = "identicon/";
    public static final String MATRIX_CONTENT_URI_SCHEME = "mxc://";
    public static final String METHOD_CROP = "crop";
    public static final String METHOD_SCALE = "scale";
    public static final String URI_PREFIX_CONTENT_API = "/_matrix/media/v1/";
    private String mDownloadUrlPrefix;
    private final HomeServerConnectionConfig mHsConfig;
    private boolean mIsAvScannerEnabled;
    private final UnsentEventsManager mUnsentEventsManager;

    public ContentManager(HomeServerConnectionConfig homeServerConnectionConfig, UnsentEventsManager unsentEventsManager) {
        this.mHsConfig = homeServerConnectionConfig;
        this.mUnsentEventsManager = unsentEventsManager;
        configureAntiVirusScanner(false);
    }

    public void configureAntiVirusScanner(boolean z) {
        this.mIsAvScannerEnabled = z;
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mHsConfig.getAntiVirusServerUri().toString());
            sb.append("/");
            sb.append(RestClient.URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE);
            this.mDownloadUrlPrefix = sb.toString();
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.mHsConfig.getHomeserverUri().toString());
        sb2.append(URI_PREFIX_CONTENT_API);
        this.mDownloadUrlPrefix = sb2.toString();
    }

    public boolean isAvScannerEnabled() {
        return this.mIsAvScannerEnabled;
    }

    public HomeServerConnectionConfig getHsConfig() {
        return this.mHsConfig;
    }

    public UnsentEventsManager getUnsentEventsManager() {
        return this.mUnsentEventsManager;
    }

    public static String getIdenticonURL(String str) {
        String str2;
        if (str == null) {
            return null;
        }
        try {
            str2 = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getIdenticonURL() : java.net.URLEncoder.encode failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
            str2 = null;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("mxc://identicon/");
        sb2.append(str2);
        return sb2.toString();
    }

    public static boolean isValidMatrixContentUrl(String str) {
        return str != null && str.startsWith(MATRIX_CONTENT_URI_SCHEME);
    }

    @Nullable
    public String downloadTaskIdForMatrixMediaContent(String str) {
        if (isValidMatrixContentUrl(str)) {
            return str.substring(MATRIX_CONTENT_URI_SCHEME.length());
        }
        return null;
    }

    @Nullable
    public String getDownloadableUrl(String str) {
        return getDownloadableUrl(str, false);
    }

    @Nullable
    public String getDownloadableUrl(String str, boolean z) {
        if (!isValidMatrixContentUrl(str)) {
            return null;
        }
        if (!z || !this.mIsAvScannerEnabled) {
            String substring = str.substring(MATRIX_CONTENT_URI_SCHEME.length());
            StringBuilder sb = new StringBuilder();
            sb.append(this.mDownloadUrlPrefix);
            sb.append("download/");
            sb.append(substring);
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.mDownloadUrlPrefix);
        sb2.append("download_encrypted");
        return sb2.toString();
    }

    @Nullable
    public String getDownloadableThumbnailUrl(String str, int i, int i2, String str2) {
        String str3;
        if (!isValidMatrixContentUrl(str)) {
            return null;
        }
        String substring = str.substring(MATRIX_CONTENT_URI_SCHEME.length());
        if (substring.endsWith("#auto")) {
            substring = substring.substring(0, substring.length() - "#auto".length());
        }
        if (substring.startsWith(MATRIX_CONTENT_IDENTICON_PREFIX)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mHsConfig.getHomeserverUri().toString());
            sb.append(URI_PREFIX_CONTENT_API);
            str3 = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mDownloadUrlPrefix);
            sb2.append("thumbnail/");
            str3 = sb2.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str3);
        sb3.append(substring);
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb4);
        sb5.append("?width=");
        sb5.append(i);
        String sb6 = sb5.toString();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb6);
        sb7.append("&height=");
        sb7.append(i2);
        String sb8 = sb7.toString();
        StringBuilder sb9 = new StringBuilder();
        sb9.append(sb8);
        sb9.append("&method=");
        sb9.append(str2);
        return sb9.toString();
    }
}
