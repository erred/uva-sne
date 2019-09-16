package com.opengarden.firechat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.LoginRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ThirdPidRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationParams;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import java.util.List;

public class LoginHandler {
    private static final String LOG_TAG = "LoginHandler";

    /* access modifiers changed from: private */
    public void onRegistrationDone(Context context, HomeServerConnectionConfig homeServerConnectionConfig, Credentials credentials, ApiCallback<HomeServerConnectionConfig> apiCallback) {
        if (TextUtils.isEmpty(credentials.userId)) {
            apiCallback.onMatrixError(new MatrixError(MatrixError.FORBIDDEN, "No user id"));
            return;
        }
        boolean z = false;
        for (MXSession credentials2 : Matrix.getMXSessions(context)) {
            Credentials credentials3 = credentials2.getCredentials();
            z |= TextUtils.equals(credentials.userId, credentials3.userId) && TextUtils.equals(credentials.homeServer, credentials3.homeServer);
        }
        if (!z) {
            homeServerConnectionConfig.setCredentials(credentials);
            Matrix.getInstance(context).addSession(Matrix.getInstance(context).createSession(homeServerConnectionConfig));
        }
        apiCallback.onSuccess(homeServerConnectionConfig);
    }

    public void login(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, String str3, String str4, ApiCallback<HomeServerConnectionConfig> apiCallback) {
        final Context applicationContext = context.getApplicationContext();
        final HomeServerConnectionConfig homeServerConnectionConfig2 = homeServerConnectionConfig;
        final ApiCallback<HomeServerConnectionConfig> apiCallback2 = apiCallback;
        final String str5 = str;
        final String str6 = str2;
        final String str7 = str3;
        final String str8 = str4;
        C12811 r0 = new UnrecognizedCertApiCallback<Credentials>(homeServerConnectionConfig, apiCallback) {
            public void onSuccess(Credentials credentials) {
                LoginHandler.this.onRegistrationDone(applicationContext, homeServerConnectionConfig2, credentials, apiCallback2);
            }

            public void onAcceptedCert() {
                LoginHandler.this.login(applicationContext, homeServerConnectionConfig2, str5, str6, str7, str8, apiCallback2);
            }
        };
        callLogin(context, homeServerConnectionConfig, str, str2, str3, str4, r0);
    }

    private void callLogin(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, String str3, String str4, ApiCallback<Credentials> apiCallback) {
        LoginRestClient loginRestClient = new LoginRestClient(homeServerConnectionConfig);
        String string = context.getString(C1299R.string.login_mobile_device);
        if (!TextUtils.isEmpty(str)) {
            if (Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
                loginRestClient.loginWith3Pid("email", str.toLowerCase(VectorApp.getApplicationLocale()), str4, string, null, apiCallback);
            } else {
                loginRestClient.loginWithUser(str, str4, string, null, apiCallback);
            }
        } else if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
            loginRestClient.loginWithPhoneNumber(str2, str3, str4, string, null, apiCallback);
        }
    }

    public void getSupportedLoginFlows(Context context, HomeServerConnectionConfig homeServerConnectionConfig, ApiCallback<List<LoginFlow>> apiCallback) {
        final Context applicationContext = context.getApplicationContext();
        LoginRestClient loginRestClient = new LoginRestClient(homeServerConnectionConfig);
        final HomeServerConnectionConfig homeServerConnectionConfig2 = homeServerConnectionConfig;
        final ApiCallback<List<LoginFlow>> apiCallback2 = apiCallback;
        C12822 r0 = new UnrecognizedCertApiCallback<List<LoginFlow>>(homeServerConnectionConfig, apiCallback) {
            public void onAcceptedCert() {
                LoginHandler.this.getSupportedLoginFlows(applicationContext, homeServerConnectionConfig2, apiCallback2);
            }

            public void onSuccess(List<LoginFlow> list) {
                apiCallback2.onSuccess(list);
            }
        };
        loginRestClient.getSupportedLoginFlows(r0);
    }

    public void getSupportedRegistrationFlows(Context context, HomeServerConnectionConfig homeServerConnectionConfig, ApiCallback<HomeServerConnectionConfig> apiCallback) {
        register(context, homeServerConnectionConfig, new RegistrationParams(), apiCallback);
    }

    private void register(Context context, HomeServerConnectionConfig homeServerConnectionConfig, RegistrationParams registrationParams, ApiCallback<HomeServerConnectionConfig> apiCallback) {
        final Context applicationContext = context.getApplicationContext();
        LoginRestClient loginRestClient = new LoginRestClient(homeServerConnectionConfig);
        registrationParams.initial_device_display_name = context.getString(C1299R.string.login_mobile_device);
        final HomeServerConnectionConfig homeServerConnectionConfig2 = homeServerConnectionConfig;
        final ApiCallback<HomeServerConnectionConfig> apiCallback2 = apiCallback;
        C12833 r0 = new UnrecognizedCertApiCallback<Credentials>(homeServerConnectionConfig, apiCallback) {
            public void onSuccess(Credentials credentials) {
                LoginHandler.this.onRegistrationDone(applicationContext, homeServerConnectionConfig2, credentials, apiCallback2);
            }

            public void onAcceptedCert() {
                LoginHandler.this.getSupportedRegistrationFlows(applicationContext, homeServerConnectionConfig2, apiCallback2);
            }
        };
        loginRestClient.register(registrationParams, r0);
    }

    public void submitEmailTokenValidation(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, String str3, ApiCallback<Boolean> apiCallback) {
        ThreePid threePid = new ThreePid(null, "email");
        final HomeServerConnectionConfig homeServerConnectionConfig2 = homeServerConnectionConfig;
        ThirdPidRestClient thirdPidRestClient = new ThirdPidRestClient(homeServerConnectionConfig2);
        final Context context2 = context;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final ApiCallback<Boolean> apiCallback2 = apiCallback;
        C12844 r2 = new UnrecognizedCertApiCallback<Boolean>(homeServerConnectionConfig2, apiCallback) {
            public void onAcceptedCert() {
                LoginHandler.this.submitEmailTokenValidation(context2, homeServerConnectionConfig2, str4, str5, str6, apiCallback2);
            }
        };
        threePid.submitValidationToken(thirdPidRestClient, str, str2, str3, r2);
    }
}
