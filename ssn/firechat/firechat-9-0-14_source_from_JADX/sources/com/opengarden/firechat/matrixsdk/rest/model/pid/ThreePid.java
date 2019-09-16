package com.opengarden.firechat.matrixsdk.rest.model.pid;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.ProfileRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ThirdPidRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RequestEmailValidationResponse;
import com.opengarden.firechat.matrixsdk.rest.model.RequestPhoneNumberValidationResponse;
import java.io.Serializable;
import java.util.UUID;

public class ThreePid implements Serializable {
    public static final int AUTH_STATE_TOKEN_AUTHENTIFICATED = 4;
    public static final int AUTH_STATE_TOKEN_RECEIVED = 2;
    public static final int AUTH_STATE_TOKEN_REQUESTED = 1;
    public static final int AUTH_STATE_TOKEN_SUBMITTED = 3;
    public static final int AUTH_STATE_TOKEN_UNKNOWN = 0;
    public static final String MEDIUM_EMAIL = "email";
    public static final String MEDIUM_MSISDN = "msisdn";
    public String clientSecret;
    public String country;
    public String emailAddress;
    /* access modifiers changed from: private */
    public int mValidationState;
    public String medium;
    public String phoneNumber;
    public int sendAttempt;
    public String sid;

    public ThreePid(String str, String str2) {
        this.medium = str2;
        this.emailAddress = str;
        if (TextUtils.equals("email", this.medium) && !TextUtils.isEmpty(str)) {
            this.emailAddress = this.emailAddress.toLowerCase();
        }
        this.clientSecret = UUID.randomUUID().toString();
    }

    public ThreePid(String str, String str2, String str3) {
        String str4;
        this.medium = str3;
        this.phoneNumber = str;
        if (str2 == null) {
            str4 = "";
        } else {
            str4 = str2.toUpperCase();
        }
        this.country = str4;
        this.clientSecret = UUID.randomUUID().toString();
    }

    private void resetValidationParameters() {
        this.mValidationState = 0;
        this.clientSecret = UUID.randomUUID().toString();
        this.sendAttempt = 1;
        this.sid = null;
    }

    public void requestEmailValidationToken(ProfileRestClient profileRestClient, String str, boolean z, final ApiCallback<Void> apiCallback) {
        if (profileRestClient != null && this.mValidationState != 1) {
            if (this.mValidationState != 0) {
                resetValidationParameters();
            }
            this.mValidationState = 1;
            profileRestClient.requestEmailValidationToken(this.emailAddress, this.clientSecret, this.sendAttempt, str, z, new ApiCallback<RequestEmailValidationResponse>() {
                public void onSuccess(RequestEmailValidationResponse requestEmailValidationResponse) {
                    if (TextUtils.equals(requestEmailValidationResponse.clientSecret, ThreePid.this.clientSecret)) {
                        ThreePid.this.mValidationState = 2;
                        ThreePid.this.sid = requestEmailValidationResponse.sid;
                        apiCallback.onSuccess(null);
                    }
                }

                private void commonError() {
                    ThreePid.this.sendAttempt++;
                    ThreePid.this.mValidationState = 0;
                }

                public void onNetworkError(Exception exc) {
                    commonError();
                    apiCallback.onNetworkError(exc);
                }

                public void onMatrixError(MatrixError matrixError) {
                    commonError();
                    apiCallback.onMatrixError(matrixError);
                }

                public void onUnexpectedError(Exception exc) {
                    commonError();
                    apiCallback.onUnexpectedError(exc);
                }
            });
        }
    }

    public void requestPhoneNumberValidationToken(ProfileRestClient profileRestClient, boolean z, final ApiCallback<Void> apiCallback) {
        if (profileRestClient != null && this.mValidationState != 1) {
            if (this.mValidationState != 0) {
                resetValidationParameters();
            }
            this.mValidationState = 1;
            profileRestClient.requestPhoneNumberValidationToken(this.phoneNumber, this.country, this.clientSecret, this.sendAttempt, z, new ApiCallback<RequestPhoneNumberValidationResponse>() {
                public void onSuccess(RequestPhoneNumberValidationResponse requestPhoneNumberValidationResponse) {
                    if (TextUtils.equals(requestPhoneNumberValidationResponse.clientSecret, ThreePid.this.clientSecret)) {
                        ThreePid.this.mValidationState = 2;
                        ThreePid.this.sid = requestPhoneNumberValidationResponse.sid;
                        apiCallback.onSuccess(null);
                    }
                }

                private void commonError() {
                    ThreePid.this.sendAttempt++;
                    ThreePid.this.mValidationState = 0;
                }

                public void onNetworkError(Exception exc) {
                    commonError();
                    apiCallback.onNetworkError(exc);
                }

                public void onMatrixError(MatrixError matrixError) {
                    commonError();
                    apiCallback.onMatrixError(matrixError);
                }

                public void onUnexpectedError(Exception exc) {
                    commonError();
                    apiCallback.onUnexpectedError(exc);
                }
            });
        }
    }

    public void submitValidationToken(ThirdPidRestClient thirdPidRestClient, String str, String str2, String str3, ApiCallback<Boolean> apiCallback) {
        if (thirdPidRestClient != null) {
            thirdPidRestClient.submitValidationToken(this.medium, str, str2, str3, apiCallback);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getMediumFriendlyName(java.lang.String r3, android.content.Context r4) {
        /*
            java.lang.String r0 = ""
            int r1 = r3.hashCode()
            r2 = -1064943142(0xffffffffc08641da, float:-4.1955385)
            if (r1 == r2) goto L_0x001b
            r2 = 96619420(0x5c24b9c, float:1.8271447E-35)
            if (r1 == r2) goto L_0x0011
            goto L_0x0025
        L_0x0011:
            java.lang.String r1 = "email"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L_0x0025
            r3 = 0
            goto L_0x0026
        L_0x001b:
            java.lang.String r1 = "msisdn"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L_0x0025
            r3 = 1
            goto L_0x0026
        L_0x0025:
            r3 = -1
        L_0x0026:
            switch(r3) {
                case 0: goto L_0x0032;
                case 1: goto L_0x002a;
                default: goto L_0x0029;
            }
        L_0x0029:
            goto L_0x0039
        L_0x002a:
            r3 = 2131689858(0x7f0f0182, float:1.9008743E38)
            java.lang.String r0 = r4.getString(r3)
            goto L_0x0039
        L_0x0032:
            r3 = 2131689857(0x7f0f0181, float:1.9008741E38)
            java.lang.String r0 = r4.getString(r3)
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid.getMediumFriendlyName(java.lang.String, android.content.Context):java.lang.String");
    }
}
