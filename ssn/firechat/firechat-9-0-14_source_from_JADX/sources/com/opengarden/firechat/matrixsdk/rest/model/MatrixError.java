package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class MatrixError implements Serializable {
    public static final String BAD_JSON = "M_BAD_JSON";
    public static final String BAD_PAGINATION = "M_BAD_PAGINATION";
    public static final String FORBIDDEN = "M_FORBIDDEN";
    public static final String LIMIT_EXCEEDED = "M_LIMIT_EXCEEDED";
    public static final String LIMIT_TYPE_MAU = "monthly_active_user";
    public static final String LOGIN_EMAIL_URL_NOT_YET = "M_LOGIN_EMAIL_URL_NOT_YET";
    public static final String M_CONSENT_NOT_GIVEN = "M_CONSENT_NOT_GIVEN";
    public static final String NOT_FOUND = "M_NOT_FOUND";
    public static final String NOT_JSON = "M_NOT_JSON";
    public static final String NOT_SUPPORTED = "M_NOT_SUPPORTED";
    public static final String OLD_VERSION = "M_OLD_VERSION";
    public static final String RESOURCE_LIMIT_EXCEEDED = "M_RESOURCE_LIMIT_EXCEEDED";
    public static final String ROOM_IN_USE = "M_ROOM_IN_USE";
    public static final String SERVER_NOT_TRUSTED = "M_SERVER_NOT_TRUSTED";
    public static final String THREEPID_AUTH_FAILED = "M_THREEPID_AUTH_FAILED";
    public static final String THREEPID_IN_USE = "M_THREEPID_IN_USE";
    public static final String THREEPID_NOT_FOUND = "M_THREEPID_NOT_FOUND";
    public static final String TOO_LARGE = "M_TOO_LARGE";
    public static final String UNAUTHORIZED = "M_UNAUTHORIZED";
    public static final String UNKNOWN = "M_UNKNOWN";
    public static final String UNKNOWN_TOKEN = "M_UNKNOWN_TOKEN";
    public static final String UNRECOGNIZED = "M_UNRECOGNIZED";
    public static final String USER_IN_USE = "M_USER_IN_USE";
    public static final Set<String> mConfigurationErrorCodes = new HashSet(Arrays.asList(new String[]{UNKNOWN_TOKEN, OLD_VERSION}));
    @SerializedName("admin_contact")
    public String adminUri;
    @SerializedName("consent_uri")
    public String consentUri;
    public String errcode;
    public String error;
    @SerializedName("limit_type")
    public String limitType;
    public ResponseBody mErrorBody;
    public String mErrorBodyAsString;
    public MediaType mErrorBodyMimeType;
    public String mReason;
    public Integer mStatus;
    public Integer retry_after_ms;

    public MatrixError() {
    }

    public MatrixError(String str, String str2) {
        this.errcode = str;
        this.error = str2;
    }

    public String getLocalizedMessage() {
        String str = "";
        if (!TextUtils.isEmpty(this.error)) {
            return this.error;
        }
        return !TextUtils.isEmpty(this.errcode) ? this.errcode : str;
    }

    public String getMessage() {
        return getLocalizedMessage();
    }

    public boolean isSupportedErrorCode() {
        return FORBIDDEN.equals(this.errcode) || UNKNOWN_TOKEN.equals(this.errcode) || BAD_JSON.equals(this.errcode) || NOT_JSON.equals(this.errcode) || NOT_FOUND.equals(this.errcode) || LIMIT_EXCEEDED.equals(this.errcode) || USER_IN_USE.equals(this.errcode) || ROOM_IN_USE.equals(this.errcode) || TOO_LARGE.equals(this.errcode) || BAD_PAGINATION.equals(this.errcode) || OLD_VERSION.equals(this.errcode) || UNRECOGNIZED.equals(this.errcode) || RESOURCE_LIMIT_EXCEEDED.equals(this.errcode);
    }

    public static boolean isConfigurationErrorCode(String str) {
        return str != null && mConfigurationErrorCodes.contains(str);
    }
}
