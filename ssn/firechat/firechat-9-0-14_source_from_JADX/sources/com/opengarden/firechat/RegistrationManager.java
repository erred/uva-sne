package com.opengarden.firechat;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import com.opengarden.firechat.UnrecognizedCertHandler.Callback;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.LoginRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ProfileRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ThirdPidRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationParams;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.ssl.CertUtil;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegistrationManager {
    private static final String ERROR_EMPTY_USER_ID = "ERROR_EMPTY_USER_ID";
    private static final String ERROR_MISSING_STAGE = "ERROR_MISSING_STAGE";
    private static final String JSON_KEY_CAPTCHA_RESPONSE = "response";
    private static final String JSON_KEY_CLIENT_SECRET = "client_secret";
    private static final String JSON_KEY_ID_SERVER = "id_server";
    private static final String JSON_KEY_PUBLIC_KEY = "public_key";
    private static final String JSON_KEY_SESSION = "session";
    private static final String JSON_KEY_SID = "sid";
    private static final String JSON_KEY_THREEPID_CREDS = "threepid_creds";
    private static final String JSON_KEY_TYPE = "type";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RegistrationManager";
    private static final List<String> VECTOR_SUPPORTED_STAGES = Arrays.asList(new String[]{LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD, LoginRestClient.LOGIN_FLOW_TYPE_DUMMY, LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY, LoginRestClient.LOGIN_FLOW_TYPE_MSISDN, LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA});
    private static volatile RegistrationManager sInstance;
    private String mCaptchaResponse;
    private final List<String> mConditionalOptionalStages = new ArrayList();
    /* access modifiers changed from: private */
    public ThreePid mEmail;
    /* access modifiers changed from: private */
    public HomeServerConnectionConfig mHsConfig;
    private LoginRestClient mLoginRestClient;
    private final List<String> mOptionalStages = new ArrayList();
    private String mPassword;
    /* access modifiers changed from: private */
    public ThreePid mPhoneNumber;
    private ProfileRestClient mProfileRestClient;
    private RegistrationFlowResponse mRegistrationResponse;
    private final List<String> mRequiredStages = new ArrayList();
    /* access modifiers changed from: private */
    public boolean mShowThreePidWarning;
    private final Set<String> mSupportedStages = new HashSet();
    private ThirdPidRestClient mThirdPidRestClient;
    private String mUsername;

    private interface InternalRegistrationListener {
        void onRegistrationFailed(String str);

        void onRegistrationSuccess();
    }

    public interface RegistrationListener {
        void onRegistrationFailed(String str);

        void onRegistrationSuccess(String str);

        void onThreePidRequestFailed(String str);

        void onWaitingCaptcha();

        void onWaitingEmailValidation();
    }

    public interface ThreePidRequestListener {
        void onThreePidRequestFailed(@StringRes int i);

        void onThreePidRequested(ThreePid threePid);
    }

    public interface ThreePidValidationListener {
        void onThreePidValidated(boolean z);
    }

    public interface UsernameValidityListener {
        void onUsernameAvailabilityChecked(boolean z);
    }

    public boolean canSkip() {
        return false;
    }

    public static RegistrationManager getInstance() {
        if (sInstance == null) {
            sInstance = new RegistrationManager();
        }
        return sInstance;
    }

    private RegistrationManager() {
    }

    public void resetSingleton() {
        this.mHsConfig = null;
        this.mLoginRestClient = null;
        this.mThirdPidRestClient = null;
        this.mProfileRestClient = null;
        this.mRegistrationResponse = null;
        this.mSupportedStages.clear();
        this.mRequiredStages.clear();
        this.mOptionalStages.clear();
        this.mConditionalOptionalStages.clear();
        this.mUsername = null;
        this.mPassword = null;
        this.mEmail = null;
        this.mPhoneNumber = null;
        this.mCaptchaResponse = null;
        this.mShowThreePidWarning = false;
    }

    public void setHsConfig(HomeServerConnectionConfig homeServerConnectionConfig) {
        this.mHsConfig = homeServerConnectionConfig;
        this.mLoginRestClient = null;
        this.mThirdPidRestClient = null;
        this.mProfileRestClient = null;
    }

    public void setAccountData(String str, String str2) {
        this.mUsername = str;
        this.mPassword = str2;
    }

    public void setCaptchaResponse(String str) {
        this.mCaptchaResponse = str;
    }

    public void setSupportedRegistrationFlows(RegistrationFlowResponse registrationFlowResponse) {
        if (registrationFlowResponse != null) {
            this.mRegistrationResponse = registrationFlowResponse;
            analyzeRegistrationStages(registrationFlowResponse);
        }
    }

    public void checkUsernameAvailability(Context context, final UsernameValidityListener usernameValidityListener) {
        if (getLoginRestClient() != null) {
            RegistrationParams registrationParams = new RegistrationParams();
            registrationParams.username = this.mUsername;
            register(context, registrationParams, new InternalRegistrationListener() {
                public void onRegistrationSuccess() {
                    usernameValidityListener.onUsernameAvailabilityChecked(false);
                }

                public void onRegistrationFailed(String str) {
                    usernameValidityListener.onUsernameAvailabilityChecked(!TextUtils.equals(MatrixError.USER_IN_USE, str));
                }
            });
        }
    }

    private boolean isPasswordBasedFlowSupported() {
        if (!(this.mRegistrationResponse == null || this.mRegistrationResponse.flows == null)) {
            for (LoginFlow loginFlow : this.mRegistrationResponse.flows) {
                if (TextUtils.equals(loginFlow.type, LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD) || (loginFlow.stages != null && loginFlow.stages.contains(LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void attemptRegistration(final Context context, final RegistrationListener registrationListener) {
        Map<String, Object> map;
        String str;
        if (this.mRegistrationResponse != null && !TextUtils.isEmpty(this.mRegistrationResponse.session)) {
            if (this.mPhoneNumber != null && !isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN) && !TextUtils.isEmpty(this.mPhoneNumber.sid)) {
                str = LoginRestClient.LOGIN_FLOW_TYPE_MSISDN;
                map = getThreePidAuthParams(this.mPhoneNumber.clientSecret, this.mHsConfig.getIdentityServerUri().getHost(), this.mPhoneNumber.sid, LoginRestClient.LOGIN_FLOW_TYPE_MSISDN, this.mRegistrationResponse.session);
            } else if (this.mEmail == null || isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                if (!TextUtils.isEmpty(this.mCaptchaResponse) && !isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA)) {
                    str = LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA;
                    map = getCaptchaAuthParams(this.mCaptchaResponse);
                } else if (this.mSupportedStages.contains(LoginRestClient.LOGIN_FLOW_TYPE_DUMMY)) {
                    str = LoginRestClient.LOGIN_FLOW_TYPE_DUMMY;
                    map = new HashMap<>();
                    map.put("type", LoginRestClient.LOGIN_FLOW_TYPE_DUMMY);
                } else if (isPasswordBasedFlowSupported()) {
                    str = LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD;
                    map = new HashMap<>();
                    map.put("type", LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD);
                    map.put(JSON_KEY_SESSION, this.mRegistrationResponse.session);
                    if (this.mUsername != null) {
                        map.put("username", this.mUsername);
                    }
                    if (this.mPassword != null) {
                        map.put("password", this.mPassword);
                    }
                } else {
                    str = "";
                    map = new HashMap<>();
                }
            } else if (TextUtils.isEmpty(this.mEmail.sid)) {
                Log.m209d(LOG_TAG, "attemptRegistration: request email validation");
                requestValidationToken(this.mEmail, new ThreePidRequestListener() {
                    public void onThreePidRequested(ThreePid threePid) {
                        if (!TextUtils.isEmpty(threePid.sid)) {
                            RegistrationManager.this.attemptRegistration(context, registrationListener);
                            registrationListener.onWaitingEmailValidation();
                        }
                    }

                    public void onThreePidRequestFailed(@StringRes int i) {
                        registrationListener.onThreePidRequestFailed(context.getString(i));
                    }
                });
                return;
            } else {
                str = LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY;
                map = getThreePidAuthParams(this.mEmail.clientSecret, this.mHsConfig.getIdentityServerUri().getHost(), this.mEmail.sid, LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY, this.mRegistrationResponse.session);
            }
            boolean z = true;
            if (TextUtils.equals(str, LoginRestClient.LOGIN_FLOW_TYPE_MSISDN) && this.mEmail != null && !isCaptchaRequired()) {
                this.mShowThreePidWarning = true;
                this.mEmail = null;
            }
            RegistrationParams registrationParams = new RegistrationParams();
            if (!str.equals(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA)) {
                if (this.mUsername != null) {
                    registrationParams.username = this.mUsername;
                }
                if (this.mPassword != null) {
                    registrationParams.password = this.mPassword;
                }
                registrationParams.bind_email = Boolean.valueOf(this.mEmail != null);
                if (this.mPhoneNumber == null) {
                    z = false;
                }
                registrationParams.bind_msisdn = Boolean.valueOf(z);
            }
            if (map != null && !map.isEmpty()) {
                registrationParams.auth = map;
            }
            register(context, registrationParams, new InternalRegistrationListener() {
                public void onRegistrationSuccess() {
                    if (RegistrationManager.this.mShowThreePidWarning) {
                        registrationListener.onRegistrationSuccess(context.getString(C1299R.string.auth_threepid_warning_message));
                    } else {
                        registrationListener.onRegistrationSuccess(null);
                    }
                }

                public void onRegistrationFailed(String str) {
                    if (!TextUtils.equals(RegistrationManager.ERROR_MISSING_STAGE, str) || (RegistrationManager.this.mPhoneNumber != null && !RegistrationManager.this.isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN))) {
                        registrationListener.onRegistrationFailed(str);
                    } else if (RegistrationManager.this.mEmail == null || RegistrationManager.this.isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                        registrationListener.onWaitingCaptcha();
                    } else {
                        RegistrationManager.this.attemptRegistration(context, registrationListener);
                    }
                }
            });
        }
    }

    public void registerAfterEmailValidation(Context context, String str, String str2, String str3, String str4, final RegistrationListener registrationListener) {
        Log.m209d(LOG_TAG, "registerAfterEmailValidation");
        if (this.mRegistrationResponse != null) {
            this.mRegistrationResponse.session = str4;
        }
        RegistrationParams registrationParams = new RegistrationParams();
        registrationParams.auth = getThreePidAuthParams(str, CommonActivityUtils.removeUrlScheme(str3), str2, LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY, str4);
        this.mUsername = null;
        this.mPassword = null;
        clearThreePid();
        register(context, registrationParams, new InternalRegistrationListener() {
            public void onRegistrationSuccess() {
                registrationListener.onRegistrationSuccess(null);
            }

            public void onRegistrationFailed(String str) {
                if (TextUtils.equals(RegistrationManager.ERROR_MISSING_STAGE, str)) {
                    registrationListener.onWaitingCaptcha();
                } else {
                    registrationListener.onRegistrationFailed(str);
                }
            }
        });
    }

    public boolean hasNonSupportedStage() {
        return !VECTOR_SUPPORTED_STAGES.containsAll(this.mSupportedStages);
    }

    public boolean supportStage(String str) {
        return this.mSupportedStages.contains(str);
    }

    public boolean canAddThreePid() {
        return (this.mSupportedStages.contains(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY) && !isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) || (this.mSupportedStages.contains(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN) && !isCompleted(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN));
    }

    /* access modifiers changed from: private */
    public boolean isCompleted(String str) {
        return (this.mRegistrationResponse == null || this.mRegistrationResponse.completed == null || !this.mRegistrationResponse.completed.contains(str)) ? false : true;
    }

    public boolean isOptional(String str) {
        return this.mOptionalStages.contains(str);
    }

    private boolean isRequired(String str) {
        return this.mRequiredStages.contains(str);
    }

    public boolean isEmailRequired() {
        return this.mRegistrationResponse != null && isRequired(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY) && (this.mRegistrationResponse.completed == null || !this.mRegistrationResponse.completed.contains(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY));
    }

    public boolean isPhoneNumberRequired() {
        return this.mRegistrationResponse != null && isRequired(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN) && (this.mRegistrationResponse.completed == null || !this.mRegistrationResponse.completed.contains(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN));
    }

    private boolean isCaptchaRequired() {
        return this.mRegistrationResponse != null && isRequired(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA) && (this.mRegistrationResponse.completed == null || !this.mRegistrationResponse.completed.contains(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA));
    }

    public void submitValidationToken(String str, ThreePid threePid, final ThreePidValidationListener threePidValidationListener) {
        if (getThirdPidRestClient() != null) {
            threePid.submitValidationToken(getThirdPidRestClient(), str, threePid.clientSecret, threePid.sid, new ApiCallback<Boolean>() {
                public void onSuccess(Boolean bool) {
                    threePidValidationListener.onThreePidValidated(bool.booleanValue());
                }

                public void onNetworkError(Exception exc) {
                    threePidValidationListener.onThreePidValidated(false);
                }

                public void onMatrixError(MatrixError matrixError) {
                    threePidValidationListener.onThreePidValidated(false);
                }

                public void onUnexpectedError(Exception exc) {
                    threePidValidationListener.onThreePidValidated(false);
                }
            });
        }
    }

    public String getCaptchaPublicKey() {
        if (!(this.mRegistrationResponse == null || this.mRegistrationResponse.params == null)) {
            Object obj = this.mRegistrationResponse.params.get(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA);
            if (obj != null) {
                try {
                    return (String) ((Map) obj).get(JSON_KEY_PUBLIC_KEY);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("getCaptchaPublicKey: ");
                    sb.append(e.getLocalizedMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
        return null;
    }

    public void addEmailThreePid(ThreePid threePid) {
        this.mEmail = threePid;
    }

    public ThreePid getEmailThreePid() {
        return this.mEmail;
    }

    public void addPhoneNumberThreePid(String str, String str2, ThreePidRequestListener threePidRequestListener) {
        requestValidationToken(new ThreePid(str, str2, ThreePid.MEDIUM_MSISDN), threePidRequestListener);
    }

    public void clearThreePid() {
        this.mEmail = null;
        this.mPhoneNumber = null;
        this.mShowThreePidWarning = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0045  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getThreePidInstructions(android.content.Context r3) {
        /*
            r2 = this;
            com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse r0 = r2.mRegistrationResponse
            r1 = -1
            if (r0 == 0) goto L_0x003d
            java.lang.String r0 = "m.login.email.identity"
            boolean r0 = r2.isRequired(r0)
            if (r0 == 0) goto L_0x0019
            java.lang.String r0 = "m.login.msisdn"
            boolean r0 = r2.isRequired(r0)
            if (r0 == 0) goto L_0x0019
            r0 = 2131689530(0x7f0f003a, float:1.9008078E38)
            goto L_0x003e
        L_0x0019:
            java.lang.String r0 = "m.login.email.identity"
            boolean r0 = r2.supportStage(r0)
            if (r0 == 0) goto L_0x0031
            java.lang.String r0 = "m.login.msisdn"
            boolean r0 = r2.supportStage(r0)
            if (r0 == 0) goto L_0x002d
            r0 = 2131689532(0x7f0f003c, float:1.9008082E38)
            goto L_0x003e
        L_0x002d:
            r0 = 2131689531(0x7f0f003b, float:1.900808E38)
            goto L_0x003e
        L_0x0031:
            java.lang.String r0 = "m.login.msisdn"
            boolean r0 = r2.supportStage(r0)
            if (r0 == 0) goto L_0x003d
            r0 = 2131689533(0x7f0f003d, float:1.9008084E38)
            goto L_0x003e
        L_0x003d:
            r0 = -1
        L_0x003e:
            if (r0 == r1) goto L_0x0045
            java.lang.String r3 = r3.getString(r0)
            goto L_0x0047
        L_0x0045:
            java.lang.String r3 = ""
        L_0x0047:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.RegistrationManager.getThreePidInstructions(android.content.Context):java.lang.String");
    }

    private LoginRestClient getLoginRestClient() {
        if (this.mLoginRestClient == null && this.mHsConfig != null) {
            this.mLoginRestClient = new LoginRestClient(this.mHsConfig);
        }
        return this.mLoginRestClient;
    }

    private ThirdPidRestClient getThirdPidRestClient() {
        if (this.mThirdPidRestClient == null && this.mHsConfig != null) {
            this.mThirdPidRestClient = new ThirdPidRestClient(this.mHsConfig);
        }
        return this.mThirdPidRestClient;
    }

    private ProfileRestClient getProfileRestClient() {
        if (this.mProfileRestClient == null && this.mHsConfig != null) {
            this.mProfileRestClient = new ProfileRestClient(this.mHsConfig);
        }
        return this.mProfileRestClient;
    }

    /* access modifiers changed from: private */
    public void setRegistrationFlowResponse(RegistrationFlowResponse registrationFlowResponse) {
        if (registrationFlowResponse != null) {
            this.mRegistrationResponse = registrationFlowResponse;
        }
    }

    private void analyzeRegistrationStages(RegistrationFlowResponse registrationFlowResponse) {
        HashSet hashSet = new HashSet();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        for (LoginFlow loginFlow : registrationFlowResponse.flows) {
            hashSet.addAll(loginFlow.stages);
            if (!loginFlow.stages.contains(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA)) {
                z = true;
            }
            if (!loginFlow.stages.contains(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN)) {
                if (!loginFlow.stages.contains(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                    z2 = true;
                }
                z3 = true;
            }
            if (!loginFlow.stages.contains(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                z4 = true;
            }
        }
        this.mSupportedStages.clear();
        this.mSupportedStages.addAll(hashSet);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (hashSet.contains(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA)) {
            if (z) {
                arrayList3.add(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA);
            } else {
                arrayList.add(LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA);
            }
        }
        if (!hashSet.containsAll(Arrays.asList(new String[]{LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY, LoginRestClient.LOGIN_FLOW_TYPE_MSISDN})) || z2 || !z3 || !z4) {
            if (hashSet.contains(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                if (z4) {
                    arrayList3.add(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY);
                } else {
                    arrayList.add(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY);
                }
            }
            if (hashSet.contains(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN)) {
                if (z3) {
                    arrayList3.add(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN);
                } else {
                    arrayList.add(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN);
                }
            }
        } else {
            arrayList2.add(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY);
            arrayList2.add(LoginRestClient.LOGIN_FLOW_TYPE_MSISDN);
        }
        this.mRequiredStages.clear();
        this.mConditionalOptionalStages.clear();
        this.mOptionalStages.clear();
        this.mRequiredStages.addAll(arrayList);
        this.mConditionalOptionalStages.addAll(arrayList2);
        this.mOptionalStages.addAll(arrayList3);
    }

    private Map<String, Object> getThreePidAuthParams(String str, String str2, String str3, String str4, String str5) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        hashMap2.put("client_secret", str);
        hashMap2.put(JSON_KEY_ID_SERVER, str2);
        hashMap2.put("sid", str3);
        hashMap.put("type", str4);
        hashMap.put(JSON_KEY_THREEPID_CREDS, hashMap2);
        hashMap.put(JSON_KEY_SESSION, str5);
        return hashMap;
    }

    private Map<String, Object> getCaptchaAuthParams(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("type", LoginRestClient.LOGIN_FLOW_TYPE_RECAPTCHA);
        hashMap.put(JSON_KEY_CAPTCHA_RESPONSE, str);
        hashMap.put(JSON_KEY_SESSION, this.mRegistrationResponse.session);
        return hashMap;
    }

    /* access modifiers changed from: private */
    public void requestValidationToken(final ThreePid threePid, final ThreePidRequestListener threePidRequestListener) {
        if (getThirdPidRestClient() != null) {
            String str = threePid.medium;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -1064943142) {
                if (hashCode == 96619420 && str.equals("email")) {
                    c = 0;
                }
            } else if (str.equals(ThreePid.MEDIUM_MSISDN)) {
                c = 1;
            }
            switch (c) {
                case 0:
                    threePid.requestEmailValidationToken(getProfileRestClient(), null, true, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            threePidRequestListener.onThreePidRequested(threePid);
                        }

                        public void onNetworkError(Exception exc) {
                            RegistrationManager.this.warnAfterCertificateError(exc, threePid, threePidRequestListener);
                        }

                        public void onUnexpectedError(Exception exc) {
                            threePidRequestListener.onThreePidRequested(threePid);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (TextUtils.equals(MatrixError.THREEPID_IN_USE, matrixError.errcode)) {
                                threePidRequestListener.onThreePidRequestFailed(C1299R.string.account_email_already_used_error);
                            } else {
                                threePidRequestListener.onThreePidRequested(threePid);
                            }
                        }
                    });
                    return;
                case 1:
                    threePid.requestPhoneNumberValidationToken(getProfileRestClient(), true, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            RegistrationManager.this.mPhoneNumber = threePid;
                            threePidRequestListener.onThreePidRequested(threePid);
                        }

                        public void onNetworkError(Exception exc) {
                            RegistrationManager.this.warnAfterCertificateError(exc, threePid, threePidRequestListener);
                        }

                        public void onUnexpectedError(Exception exc) {
                            threePidRequestListener.onThreePidRequested(threePid);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (TextUtils.equals(MatrixError.THREEPID_IN_USE, matrixError.errcode)) {
                                threePidRequestListener.onThreePidRequestFailed(C1299R.string.account_phone_number_already_used_error);
                            } else {
                                threePidRequestListener.onThreePidRequested(threePid);
                            }
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void warnAfterCertificateError(Exception exc, final ThreePid threePid, final ThreePidRequestListener threePidRequestListener) {
        UnrecognizedCertificateException certificateException = CertUtil.getCertificateException(exc);
        if (certificateException != null) {
            UnrecognizedCertHandler.show(this.mHsConfig, certificateException.getFingerprint(), false, new Callback() {
                public void onAccept() {
                    RegistrationManager.this.requestValidationToken(threePid, threePidRequestListener);
                }

                public void onIgnore() {
                    threePidRequestListener.onThreePidRequested(threePid);
                }

                public void onReject() {
                    threePidRequestListener.onThreePidRequested(threePid);
                }
            });
            return;
        }
        threePidRequestListener.onThreePidRequested(threePid);
    }

    /* access modifiers changed from: private */
    public void register(Context context, RegistrationParams registrationParams, InternalRegistrationListener internalRegistrationListener) {
        if (getLoginRestClient() != null) {
            registrationParams.initial_device_display_name = context.getString(C1299R.string.login_mobile_device);
            LoginRestClient loginRestClient = this.mLoginRestClient;
            final InternalRegistrationListener internalRegistrationListener2 = internalRegistrationListener;
            final Context context2 = context;
            final RegistrationParams registrationParams2 = registrationParams;
            C13119 r1 = new UnrecognizedCertApiCallback<Credentials>(this.mHsConfig) {
                public void onSuccess(Credentials credentials) {
                    if (TextUtils.isEmpty(credentials.userId)) {
                        internalRegistrationListener2.onRegistrationFailed(RegistrationManager.ERROR_EMPTY_USER_ID);
                        return;
                    }
                    boolean z = false;
                    for (MXSession credentials2 : Matrix.getMXSessions(context2)) {
                        Credentials credentials3 = credentials2.getCredentials();
                        z |= TextUtils.equals(credentials.userId, credentials3.userId) && TextUtils.equals(credentials.homeServer, credentials3.homeServer);
                    }
                    if (RegistrationManager.this.mHsConfig == null) {
                        internalRegistrationListener2.onRegistrationFailed("null mHsConfig");
                        return;
                    }
                    if (!z) {
                        RegistrationManager.this.mHsConfig.setCredentials(credentials);
                        Matrix.getInstance(context2).addSession(Matrix.getInstance(context2).createSession(RegistrationManager.this.mHsConfig));
                    }
                    internalRegistrationListener2.onRegistrationSuccess();
                }

                public void onAcceptedCert() {
                    RegistrationManager.this.register(context2, registrationParams2, internalRegistrationListener2);
                }

                public void onTLSOrNetworkError(Exception exc) {
                    internalRegistrationListener2.onRegistrationFailed(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (TextUtils.equals(matrixError.errcode, MatrixError.USER_IN_USE)) {
                        Log.m209d(RegistrationManager.LOG_TAG, "User name is used");
                        internalRegistrationListener2.onRegistrationFailed(MatrixError.USER_IN_USE);
                    } else if (!TextUtils.equals(matrixError.errcode, MatrixError.UNAUTHORIZED)) {
                        if (matrixError.mStatus == null || matrixError.mStatus.intValue() != 401) {
                            internalRegistrationListener2.onRegistrationFailed("");
                            return;
                        }
                        try {
                            RegistrationManager.this.setRegistrationFlowResponse(JsonUtils.toRegistrationFlowResponse(matrixError.mErrorBodyAsString));
                        } catch (Exception e) {
                            String access$800 = RegistrationManager.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("JsonUtils.toRegistrationFlowResponse ");
                            sb.append(e.getLocalizedMessage());
                            Log.m211e(access$800, sb.toString());
                        }
                        internalRegistrationListener2.onRegistrationFailed(RegistrationManager.ERROR_MISSING_STAGE);
                    }
                }
            };
            loginRestClient.register(registrationParams, r1);
        }
    }
}
