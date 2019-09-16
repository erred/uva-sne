package com.opengarden.firechat.matrixsdk.rest.client;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.ProfileApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.AuthParams;
import com.opengarden.firechat.matrixsdk.rest.model.ChangePasswordParams;
import com.opengarden.firechat.matrixsdk.rest.model.DeactivateAccountParams;
import com.opengarden.firechat.matrixsdk.rest.model.ForgetPasswordParams;
import com.opengarden.firechat.matrixsdk.rest.model.ForgetPasswordResponse;
import com.opengarden.firechat.matrixsdk.rest.model.RequestEmailValidationParams;
import com.opengarden.firechat.matrixsdk.rest.model.RequestEmailValidationResponse;
import com.opengarden.firechat.matrixsdk.rest.model.RequestPhoneNumberValidationParams;
import com.opengarden.firechat.matrixsdk.rest.model.RequestPhoneNumberValidationResponse;
import com.opengarden.firechat.matrixsdk.rest.model.ThreePidCreds;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.login.TokenRefreshParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.TokenRefreshResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.AccountThreePidsResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.AddThreePidsParams;
import com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteThreePidParams;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.List;
import java.util.Map;
import retrofit2.C3224Response;
import retrofit2.Call;

public class ProfileRestClient extends RestClient<ProfileApi> {
    private static final String LOG_TAG = "ProfileRestClient";

    public ProfileRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, ProfileApi.class, "", false);
    }

    public void displayname(final String str, final ApiCallback<String> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("display name userId : ");
        sb.append(str);
        String sb2 = sb.toString();
        Call displayname = ((ProfileApi) this.mApi).displayname(str);
        final ApiCallback<String> apiCallback2 = apiCallback;
        C27882 r2 = new RestAdapterCallback<User>(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.displayname(str, apiCallback);
            }
        }) {
            public void success(User user, C3224Response<User> response) {
                onEventSent();
                apiCallback2.onSuccess(user.displayname);
            }
        };
        displayname.enqueue(r2);
    }

    public void updateDisplayname(final String str, final ApiCallback<Void> apiCallback) {
        User user = new User();
        user.displayname = str;
        ((ProfileApi) this.mApi).displayname(this.mCredentials.userId, user).enqueue(new RestAdapterCallback("update display name", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.updateDisplayname(str, apiCallback);
            }
        }));
    }

    public void avatarUrl(final String str, final ApiCallback<String> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("avatarUrl userId : ");
        sb.append(str);
        String sb2 = sb.toString();
        Call avatarUrl = ((ProfileApi) this.mApi).avatarUrl(str);
        final ApiCallback<String> apiCallback2 = apiCallback;
        C27915 r2 = new RestAdapterCallback<User>(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.avatarUrl(str, apiCallback);
            }
        }) {
            public void success(User user, C3224Response response) {
                onEventSent();
                apiCallback2.onSuccess(user.getAvatarUrl());
            }
        };
        avatarUrl.enqueue(r2);
    }

    public void updateAvatarUrl(final String str, final ApiCallback<Void> apiCallback) {
        User user = new User();
        user.setAvatarUrl(str);
        ((ProfileApi) this.mApi).avatarUrl(this.mCredentials.userId, user).enqueue(new RestAdapterCallback("updateAvatarUrl", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.updateAvatarUrl(str, apiCallback);
            }
        }));
    }

    public void updatePassword(String str, String str2, String str3, ApiCallback<Void> apiCallback) {
        ChangePasswordParams changePasswordParams = new ChangePasswordParams();
        changePasswordParams.auth = new AuthParams();
        changePasswordParams.auth.type = LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD;
        changePasswordParams.auth.user = str;
        changePasswordParams.auth.password = str2;
        changePasswordParams.new_password = str3;
        Call updatePassword = ((ProfileApi) this.mApi).updatePassword(changePasswordParams);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C27937 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.updatePassword(str4, str5, str6, apiCallback2);
            }
        };
        updatePassword.enqueue(new RestAdapterCallback("update password", unsentEventsManager, apiCallback, r4));
    }

    public void resetPassword(final String str, final Map<String, String> map, final ApiCallback<Void> apiCallback) {
        ChangePasswordParams changePasswordParams = new ChangePasswordParams();
        changePasswordParams.auth = new AuthParams();
        changePasswordParams.auth.type = LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY;
        changePasswordParams.auth.threepid_creds = map;
        changePasswordParams.new_password = str;
        ((ProfileApi) this.mApi).updatePassword(changePasswordParams).enqueue(new RestAdapterCallback("Reset password", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.resetPassword(str, map, apiCallback);
            }
        }));
    }

    public void forgetPassword(final String str, final ApiCallback<ThreePid> apiCallback) {
        if (!TextUtils.isEmpty(str)) {
            final ThreePid threePid = new ThreePid(str, "email");
            ForgetPasswordParams forgetPasswordParams = new ForgetPasswordParams();
            forgetPasswordParams.email = str;
            forgetPasswordParams.client_secret = threePid.clientSecret;
            forgetPasswordParams.send_attempt = Integer.valueOf(1);
            forgetPasswordParams.id_server = this.mHsConfig.getIdentityServerUri().getHost();
            Call forgetPassword = ((ProfileApi) this.mApi).forgetPassword(forgetPasswordParams);
            final ApiCallback<ThreePid> apiCallback2 = apiCallback;
            C277810 r1 = new RestAdapterCallback<ForgetPasswordResponse>("forget password", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
                public void onRetry() {
                    ProfileRestClient.this.forgetPassword(str, apiCallback);
                }
            }) {
                public void success(ForgetPasswordResponse forgetPasswordResponse, C3224Response response) {
                    onEventSent();
                    threePid.sid = forgetPasswordResponse.sid;
                    apiCallback2.onSuccess(threePid);
                }
            };
            forgetPassword.enqueue(r1);
        }
    }

    public void deactivateAccount(String str, String str2, String str3, boolean z, ApiCallback<Void> apiCallback) {
        DeactivateAccountParams deactivateAccountParams = new DeactivateAccountParams();
        deactivateAccountParams.auth = new AuthParams();
        final String str4 = str;
        deactivateAccountParams.auth.type = str4;
        final String str5 = str2;
        deactivateAccountParams.auth.user = str5;
        final String str6 = str3;
        deactivateAccountParams.auth.password = str6;
        final boolean z2 = z;
        deactivateAccountParams.erase = z2;
        Call deactivate = ((ProfileApi) this.mApi).deactivate(deactivateAccountParams);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C277911 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.deactivateAccount(str4, str5, str6, z2, apiCallback2);
            }
        };
        deactivate.enqueue(new RestAdapterCallback("deactivate account", unsentEventsManager, apiCallback, r0));
    }

    public void refreshTokens(ApiCallback<Credentials> apiCallback) {
        TokenRefreshParams tokenRefreshParams = new TokenRefreshParams();
        tokenRefreshParams.refresh_token = this.mCredentials.refreshToken;
        Call call = ((ProfileApi) this.mApi).tokenrefresh(tokenRefreshParams);
        final ApiCallback<Credentials> apiCallback2 = apiCallback;
        C278012 r1 = new RestAdapterCallback<TokenRefreshResponse>("refreshTokens", this.mUnsentEventsManager, apiCallback, null) {
            public void success(TokenRefreshResponse tokenRefreshResponse, C3224Response response) {
                onEventSent();
                ProfileRestClient.this.mCredentials.refreshToken = tokenRefreshResponse.refresh_token;
                ProfileRestClient.this.mCredentials.accessToken = tokenRefreshResponse.access_token;
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(ProfileRestClient.this.mCredentials);
                }
            }
        };
        call.enqueue(r1);
    }

    public void threePIDs(ApiCallback<List<ThirdPartyIdentifier>> apiCallback) {
        Call threePIDs = ((ProfileApi) this.mApi).threePIDs();
        final ApiCallback<List<ThirdPartyIdentifier>> apiCallback2 = apiCallback;
        C278113 r1 = new RestAdapterCallback<AccountThreePidsResponse>("threePIDs", this.mUnsentEventsManager, apiCallback, null) {
            public void success(AccountThreePidsResponse accountThreePidsResponse, C3224Response<AccountThreePidsResponse> response) {
                onEventSent();
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(accountThreePidsResponse.threepids);
                }
            }
        };
        threePIDs.enqueue(r1);
    }

    public void requestEmailValidationToken(String str, String str2, int i, String str3, boolean z, ApiCallback<RequestEmailValidationResponse> apiCallback) {
        final String str4;
        RequestEmailValidationParams requestEmailValidationParams = new RequestEmailValidationParams();
        String str5 = str;
        requestEmailValidationParams.email = str5;
        String str6 = str2;
        requestEmailValidationParams.clientSecret = str6;
        requestEmailValidationParams.sendAttempt = Integer.valueOf(i);
        requestEmailValidationParams.id_server = this.mHsConfig.getIdentityServerUri().getHost();
        if (!TextUtils.isEmpty(str3)) {
            str4 = str3;
            requestEmailValidationParams.next_link = str4;
        } else {
            str4 = str3;
        }
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str7 = str5;
        final String str8 = str6;
        final int i2 = i;
        final boolean z2 = z;
        final ApiCallback<RequestEmailValidationResponse> apiCallback2 = apiCallback;
        C278214 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.requestEmailValidationToken(str7, str8, i2, str4, z2, apiCallback2);
            }
        };
        final String str9 = str5;
        final String str10 = str6;
        final int i3 = i;
        final ApiCallback<RequestEmailValidationResponse> apiCallback3 = apiCallback;
        C278315 r02 = new RestAdapterCallback<RequestEmailValidationResponse>("requestEmailValidationToken", unsentEventsManager, apiCallback, r0) {
            public void success(RequestEmailValidationResponse requestEmailValidationResponse, C3224Response response) {
                onEventSent();
                requestEmailValidationResponse.email = str9;
                requestEmailValidationResponse.clientSecret = str10;
                requestEmailValidationResponse.sendAttempt = Integer.valueOf(i3);
                apiCallback3.onSuccess(requestEmailValidationResponse);
            }
        };
        if (z) {
            ((ProfileApi) this.mApi).requestEmailValidationForRegistration(requestEmailValidationParams).enqueue(r02);
        } else {
            ((ProfileApi) this.mApi).requestEmailValidation(requestEmailValidationParams).enqueue(r02);
        }
    }

    public void requestPhoneNumberValidationToken(String str, String str2, String str3, int i, boolean z, ApiCallback<RequestPhoneNumberValidationResponse> apiCallback) {
        RequestPhoneNumberValidationParams requestPhoneNumberValidationParams = new RequestPhoneNumberValidationParams();
        final String str4 = str;
        requestPhoneNumberValidationParams.phone_number = str4;
        final String str5 = str2;
        requestPhoneNumberValidationParams.country = str5;
        String str6 = str3;
        requestPhoneNumberValidationParams.clientSecret = str6;
        requestPhoneNumberValidationParams.sendAttempt = Integer.valueOf(i);
        requestPhoneNumberValidationParams.id_server = this.mHsConfig.getIdentityServerUri().getHost();
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str7 = str6;
        final int i2 = i;
        final boolean z2 = z;
        final ApiCallback<RequestPhoneNumberValidationResponse> apiCallback2 = apiCallback;
        C278416 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.requestPhoneNumberValidationToken(str4, str5, str7, i2, z2, apiCallback2);
            }
        };
        final String str8 = str6;
        final int i3 = i;
        final ApiCallback<RequestPhoneNumberValidationResponse> apiCallback3 = apiCallback;
        C278517 r02 = new RestAdapterCallback<RequestPhoneNumberValidationResponse>("requestPhoneNumberValidationToken", unsentEventsManager, apiCallback, r0) {
            public void success(RequestPhoneNumberValidationResponse requestPhoneNumberValidationResponse, C3224Response response) {
                onEventSent();
                requestPhoneNumberValidationResponse.clientSecret = str8;
                requestPhoneNumberValidationResponse.sendAttempt = Integer.valueOf(i3);
                apiCallback3.onSuccess(requestPhoneNumberValidationResponse);
            }
        };
        if (z) {
            ((ProfileApi) this.mApi).requestPhoneNumberValidationForRegistration(requestPhoneNumberValidationParams).enqueue(r02);
        } else {
            ((ProfileApi) this.mApi).requestPhoneNumberValidation(requestPhoneNumberValidationParams).enqueue(r02);
        }
    }

    public void add3PID(final ThreePid threePid, final boolean z, final ApiCallback<Void> apiCallback) {
        AddThreePidsParams addThreePidsParams = new AddThreePidsParams();
        addThreePidsParams.three_pid_creds = new ThreePidCreds();
        String uri = this.mHsConfig.getIdentityServerUri().toString();
        if (uri.startsWith("http://")) {
            uri = uri.substring("http://".length());
        } else if (uri.startsWith("https://")) {
            uri = uri.substring("https://".length());
        }
        addThreePidsParams.three_pid_creds.id_server = uri;
        addThreePidsParams.three_pid_creds.sid = threePid.sid;
        addThreePidsParams.three_pid_creds.client_secret = threePid.clientSecret;
        addThreePidsParams.bind = Boolean.valueOf(z);
        ((ProfileApi) this.mApi).add3PID(addThreePidsParams).enqueue(new RestAdapterCallback("add3PID", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.add3PID(threePid, z, apiCallback);
            }
        }));
    }

    public void delete3PID(final ThirdPartyIdentifier thirdPartyIdentifier, final ApiCallback<Void> apiCallback) {
        DeleteThreePidParams deleteThreePidParams = new DeleteThreePidParams();
        deleteThreePidParams.medium = thirdPartyIdentifier.medium;
        deleteThreePidParams.address = thirdPartyIdentifier.address;
        ((ProfileApi) this.mApi).delete3PID(deleteThreePidParams).enqueue(new RestAdapterCallback("delete3PID", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                ProfileRestClient.this.delete3PID(thirdPartyIdentifier, apiCallback);
            }
        }));
    }
}
