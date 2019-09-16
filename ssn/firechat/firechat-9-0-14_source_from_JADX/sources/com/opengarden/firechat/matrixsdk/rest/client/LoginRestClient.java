package com.opengarden.firechat.matrixsdk.rest.client;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.LoginApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlowResponse;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.PasswordLoginParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.TokenLoginParams;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.List;
import java.util.UUID;
import retrofit2.C3224Response;
import retrofit2.Call;

public class LoginRestClient extends RestClient<LoginApi> {
    public static final String LOGIN_FLOW_TYPE_DUMMY = "m.login.dummy";
    public static final String LOGIN_FLOW_TYPE_EMAIL_CODE = "m.login.email.code";
    public static final String LOGIN_FLOW_TYPE_EMAIL_IDENTITY = "m.login.email.identity";
    public static final String LOGIN_FLOW_TYPE_EMAIL_URL = "m.login.email.url";
    public static final String LOGIN_FLOW_TYPE_MSISDN = "m.login.msisdn";
    public static final String LOGIN_FLOW_TYPE_OAUTH2 = "m.login.oauth2";
    public static final String LOGIN_FLOW_TYPE_PASSWORD = "m.login.password";
    public static final String LOGIN_FLOW_TYPE_RECAPTCHA = "m.login.recaptcha";

    public LoginRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, LoginApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public void getSupportedLoginFlows(final ApiCallback<List<LoginFlow>> apiCallback) {
        Call login = ((LoginApi) this.mApi).login();
        final ApiCallback<List<LoginFlow>> apiCallback2 = apiCallback;
        C27662 r1 = new RestAdapterCallback<LoginFlowResponse>("geLoginSupportedFlows", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                LoginRestClient.this.getSupportedLoginFlows(apiCallback);
            }
        }) {
            public void success(LoginFlowResponse loginFlowResponse, C3224Response response) {
                onEventSent();
                apiCallback2.onSuccess(loginFlowResponse.flows);
            }
        };
        login.enqueue(r1);
    }

    public void register(final RegistrationParams registrationParams, final ApiCallback<Credentials> apiCallback) {
        if (!TextUtils.isEmpty(registrationParams.password) && TextUtils.isEmpty(registrationParams.initial_device_display_name)) {
            registrationParams.initial_device_display_name = Build.MODEL.trim();
            registrationParams.x_show_msisdn = Boolean.valueOf(true);
        } else if (registrationParams.password == null && registrationParams.username == null && registrationParams.auth == null) {
            registrationParams.x_show_msisdn = Boolean.valueOf(true);
        }
        Call register = ((LoginApi) this.mApi).register(registrationParams);
        final ApiCallback<Credentials> apiCallback2 = apiCallback;
        C27684 r1 = new RestAdapterCallback<JsonObject>("register", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                LoginRestClient.this.register(registrationParams, apiCallback);
            }
        }) {
            public void success(JsonObject jsonObject, C3224Response response) {
                onEventSent();
                LoginRestClient.this.mCredentials = (Credentials) LoginRestClient.this.gson.fromJson((JsonElement) jsonObject, Credentials.class);
                apiCallback2.onSuccess(LoginRestClient.this.mCredentials);
            }
        };
        register.enqueue(r1);
    }

    public void loginWithUser(String str, String str2, ApiCallback<Credentials> apiCallback) {
        loginWithUser(str, str2, null, null, apiCallback);
    }

    public void loginWithUser(String str, String str2, String str3, @Nullable String str4, ApiCallback<Credentials> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("loginWithUser : ");
        sb.append(str);
        String sb2 = sb.toString();
        PasswordLoginParams passwordLoginParams = new PasswordLoginParams();
        passwordLoginParams.setUserIdentifier(str, str2);
        passwordLoginParams.setDeviceName(str3);
        passwordLoginParams.setDeviceId(str4);
        login(passwordLoginParams, apiCallback, sb2);
    }

    public void loginWith3Pid(String str, String str2, String str3, ApiCallback<Credentials> apiCallback) {
        loginWith3Pid(str, str2, str3, null, null, apiCallback);
    }

    public void loginWith3Pid(String str, String str2, String str3, String str4, @Nullable String str5, ApiCallback<Credentials> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("loginWith3pid : ");
        sb.append(str2);
        String sb2 = sb.toString();
        PasswordLoginParams passwordLoginParams = new PasswordLoginParams();
        passwordLoginParams.setThirdPartyIdentifier(str, str2, str3);
        passwordLoginParams.setDeviceName(str4);
        passwordLoginParams.setDeviceId(str5);
        login(passwordLoginParams, apiCallback, sb2);
    }

    public void loginWithPhoneNumber(String str, String str2, String str3, ApiCallback<Credentials> apiCallback) {
        loginWithPhoneNumber(str, str2, str3, null, null, apiCallback);
    }

    public void loginWithPhoneNumber(String str, String str2, String str3, String str4, @Nullable String str5, ApiCallback<Credentials> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("loginWithPhoneNumber : ");
        sb.append(str);
        String sb2 = sb.toString();
        PasswordLoginParams passwordLoginParams = new PasswordLoginParams();
        passwordLoginParams.setPhoneIdentifier(str, str2, str3);
        passwordLoginParams.setDeviceName(str4);
        passwordLoginParams.setDeviceId(str5);
        login(passwordLoginParams, apiCallback, sb2);
    }

    public void login(LoginParams loginParams, ApiCallback<Credentials> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("login with a ");
        sb.append(loginParams.getClass().getSimpleName());
        sb.append(" object");
        login(loginParams, apiCallback, sb.toString());
    }

    /* access modifiers changed from: private */
    public void login(final LoginParams loginParams, final ApiCallback<Credentials> apiCallback, final String str) {
        Call login = ((LoginApi) this.mApi).login(loginParams);
        final ApiCallback<Credentials> apiCallback2 = apiCallback;
        C27706 r1 = new RestAdapterCallback<JsonObject>(str, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                LoginRestClient.this.login(loginParams, apiCallback, str);
            }
        }) {
            public void success(JsonObject jsonObject, C3224Response<JsonObject> response) {
                onEventSent();
                LoginRestClient.this.mCredentials = (Credentials) LoginRestClient.this.gson.fromJson((JsonElement) jsonObject, Credentials.class);
                apiCallback2.onSuccess(LoginRestClient.this.mCredentials);
            }
        };
        login.enqueue(r1);
    }

    public void loginWithToken(String str, String str2, String str3, ApiCallback<Credentials> apiCallback) {
        loginWithToken(str, str2, UUID.randomUUID().toString(), str3, apiCallback);
    }

    public void loginWithToken(String str, String str2, String str3, String str4, ApiCallback<Credentials> apiCallback) {
        TokenLoginParams tokenLoginParams = new TokenLoginParams();
        final String str5 = str;
        tokenLoginParams.user = str5;
        final String str6 = str2;
        tokenLoginParams.token = str6;
        final String str7 = str3;
        tokenLoginParams.txn_id = str7;
        if (str4 == null || TextUtils.isEmpty(str4.trim())) {
            tokenLoginParams.initial_device_display_name = Build.MODEL.trim();
        } else {
            tokenLoginParams.initial_device_display_name = str4.trim();
        }
        Call login = ((LoginApi) this.mApi).login(tokenLoginParams);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<Credentials> apiCallback2 = apiCallback;
        C27717 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                LoginRestClient.this.loginWithToken(str5, str6, str7, apiCallback2);
            }
        };
        final ApiCallback<Credentials> apiCallback3 = apiCallback;
        C27728 r02 = new RestAdapterCallback<JsonObject>("loginWithPassword user", unsentEventsManager, apiCallback, r0) {
            public void success(JsonObject jsonObject, C3224Response response) {
                onEventSent();
                LoginRestClient.this.mCredentials = (Credentials) LoginRestClient.this.gson.fromJson((JsonElement) jsonObject, Credentials.class);
                apiCallback3.onSuccess(LoginRestClient.this.mCredentials);
            }
        };
        login.enqueue(r02);
    }

    public void logout(final ApiCallback<JsonObject> apiCallback) {
        ((LoginApi) this.mApi).logout().enqueue(new RestAdapterCallback("logout user", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                LoginRestClient.this.logout(apiCallback);
            }
        }));
    }
}
