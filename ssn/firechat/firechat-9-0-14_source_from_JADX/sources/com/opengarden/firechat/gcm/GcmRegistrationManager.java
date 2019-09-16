package com.opengarden.firechat.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.amplitude.api.Constants;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Pusher;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.PushersRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PushersResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.PreferencesManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public final class GcmRegistrationManager {
    private static final String DEFAULT_PUSHER_APP_ID = "com.opengarden.firechat";
    private static final String DEFAULT_PUSHER_FILE_TAG = "mobile";
    private static final String DEFAULT_PUSHER_URL = "https://serve2.firech.at:5000/_matrix/push/v1/notify";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "GcmRegistrationManager";
    private static final String PREFS_ALLOW_BACKGROUND_SYNC = "GcmRegistrationManager.PREFS_ALLOW_BACKGROUND_SYNC";
    private static final String PREFS_ALLOW_NOTIFICATIONS = "GcmRegistrationManager.PREFS_ALLOW_NOTIFICATIONS";
    private static final String PREFS_ALLOW_SENDING_CONTENT_TO_GCM = "GcmRegistrationManager.PREFS_ALLOW_SENDING_CONTENT_TO_GCM";
    private static final String PREFS_GCM = "GcmRegistrationManager";
    private static final String PREFS_PUSHER_REGISTRATION_STATUS = "PREFS_PUSHER_REGISTRATION_STATUS";
    private static final String PREFS_PUSHER_REGISTRATION_TOKEN_KEY = "PREFS_PUSHER_REGISTRATION_TOKEN_KEY";
    private static final String PREFS_PUSHER_REGISTRATION_TOKEN_KEY_FCM = "PREFS_PUSHER_REGISTRATION_TOKEN_KEY_FCM";
    private static final String PREFS_SYNC_DELAY = "GcmRegistrationManager.PREFS_SYNC_DELAY";
    private static final String PREFS_SYNC_TIMEOUT = "GcmRegistrationManager.PREFS_SYNC_TIMEOUT";
    private static final String PREFS_TURN_SCREEN_ON = "GcmRegistrationManager.PREFS_TURN_SCREEN_ON";
    private static Boolean mUseGCM;
    private final String mBasePusherDeviceName;
    /* access modifiers changed from: private */
    public final Context mContext;
    private String mPusherAppName = null;
    private String mPusherLang = null;
    public ArrayList<Pusher> mPushersList = new ArrayList<>();
    /* access modifiers changed from: private */
    public Map<String, PushersRestClient> mPushersRestClients = new HashMap();
    /* access modifiers changed from: private */
    public RegistrationState mRegistrationState = RegistrationState.UNREGISTRATED;
    /* access modifiers changed from: private */
    public String mRegistrationToken = null;
    private final ArrayList<ThirdPartyRegistrationListener> mThirdPartyRegistrationListeners = new ArrayList<>();

    public interface GCMRegistrationListener {
        void onGCMRegistered();

        void onGCMRegistrationFailed();
    }

    public enum NotificationPrivacy {
        REDUCED,
        LOW_DETAIL,
        NORMAL
    }

    private enum RegistrationState {
        UNREGISTRATED,
        GCM_REGISTRATING,
        GCM_REGISTRED,
        SERVER_REGISTRATING,
        SERVER_REGISTERED,
        SERVER_UNREGISTRATING
    }

    public interface ThirdPartyRegistrationListener {
        void onThirdPartyRegistered();

        void onThirdPartyRegistrationFailed();

        void onThirdPartyUnregistered();

        void onThirdPartyUnregistrationFailed();
    }

    public GcmRegistrationManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mBasePusherDeviceName = Build.MODEL.trim();
        try {
            this.mPusherAppName = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).packageName;
            this.mPusherLang = this.mContext.getResources().getConfiguration().locale.getLanguage();
        } catch (Exception unused) {
            this.mPusherAppName = "VectorApp";
            this.mPusherLang = "en";
        }
        Matrix.getInstance(context).addNetworkEventListener(new IMXNetworkEventListener() {
            public void onNetworkConnectionUpdate(boolean z) {
                if (z && GcmRegistrationManager.this.useGCM()) {
                    if (GcmRegistrationManager.this.areDeviceNotificationsAllowed() && GcmRegistrationManager.this.mRegistrationState == RegistrationState.GCM_REGISTRED) {
                        GcmRegistrationManager.this.register(null);
                    } else if (!GcmRegistrationManager.this.areDeviceNotificationsAllowed() && GcmRegistrationManager.this.mRegistrationState == RegistrationState.SERVER_REGISTERED) {
                        GcmRegistrationManager.this.unregister(null);
                    }
                }
            }
        });
        this.mRegistrationState = getStoredRegistrationState();
        this.mRegistrationToken = getStoredRegistrationToken();
    }

    private PushersRestClient getPushersRestClient(MXSession mXSession) {
        PushersRestClient pushersRestClient = (PushersRestClient) this.mPushersRestClients.get(mXSession.getMyUserId());
        if (pushersRestClient == null) {
            if (!TextUtils.isEmpty(this.mContext.getString(C1299R.string.push_server_url))) {
                try {
                    HomeServerConnectionConfig homeServerConnectionConfig = new HomeServerConnectionConfig(Uri.parse(this.mContext.getString(C1299R.string.push_server_url)));
                    homeServerConnectionConfig.setCredentials(mXSession.getCredentials());
                    pushersRestClient = new PushersRestClient(homeServerConnectionConfig);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## getPushersRestClient() failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            if (pushersRestClient == null) {
                pushersRestClient = mXSession.getPushersRestClient();
            }
            this.mPushersRestClients.put(mXSession.getMyUserId(), pushersRestClient);
        }
        return pushersRestClient;
    }

    public void checkRegistrations() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("checkRegistrations with state ");
        sb.append(this.mRegistrationState);
        Log.m209d(str, sb.toString());
        if (!useGCM()) {
            Log.m209d(LOG_TAG, "checkRegistrations : GCM is disabled");
            return;
        }
        if (getOldStoredRegistrationToken() != null) {
            Log.m209d(LOG_TAG, "checkRegistrations : remove the GCM registration token after switching to the FCM one");
            this.mRegistrationToken = getOldStoredRegistrationToken();
            addSessionsRegistrationListener(new ThirdPartyRegistrationListener() {
                public void onThirdPartyRegistered() {
                }

                public void onThirdPartyRegistrationFailed() {
                }

                private void onGCMUnregistred() {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "resetGCMRegistration : remove the GCM registration token done");
                    GcmRegistrationManager.this.clearOldStoredRegistrationToken();
                    GcmRegistrationManager.this.mRegistrationToken = null;
                    GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.UNREGISTRATED);
                    GcmRegistrationManager.this.checkRegistrations();
                }

                public void onThirdPartyUnregistered() {
                    onGCMUnregistred();
                }

                public void onThirdPartyUnregistrationFailed() {
                    onGCMUnregistred();
                }
            });
            unregister(new ArrayList<>(Matrix.getInstance(this.mContext).getSessions()), 0);
        } else if (this.mRegistrationState == RegistrationState.UNREGISTRATED) {
            Log.m209d(LOG_TAG, "checkPusherRegistration : try to register to GCM server");
            registerToGCM(new GCMRegistrationListener() {
                public void onGCMRegistered() {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "checkRegistrations : reregistered");
                    CommonActivityUtils.onGcmUpdate(GcmRegistrationManager.this.mContext);
                }

                public void onGCMRegistrationFailed() {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "checkRegistrations : onPusherRegistrationFailed");
                }
            });
        } else if (this.mRegistrationState == RegistrationState.GCM_REGISTRED) {
            if (useGCM() && areDeviceNotificationsAllowed()) {
                register(null);
            }
        } else if (this.mRegistrationState == RegistrationState.SERVER_REGISTERED) {
            refreshPushersList(new ArrayList(Matrix.getInstance(this.mContext).getSessions()), null);
        }
    }

    /* access modifiers changed from: private */
    public String getGCMRegistrationToken() {
        String storedRegistrationToken = getStoredRegistrationToken();
        if (!TextUtils.isEmpty(storedRegistrationToken)) {
            return storedRegistrationToken;
        }
        Log.m209d(LOG_TAG, "## getGCMRegistrationToken() : undefined token -> getting a new one");
        return GCMHelper.getRegistrationToken();
    }

    private void registerToGCM(final GCMRegistrationListener gCMRegistrationListener) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("registerToGCM with state ");
        sb.append(this.mRegistrationState);
        Log.m209d(str, sb.toString());
        if (!useGCM()) {
            Log.m209d(LOG_TAG, "registerPusher : GCM is disabled");
            if (gCMRegistrationListener != null) {
                try {
                    gCMRegistrationListener.onGCMRegistrationFailed();
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("registerToGCM : onPusherRegistered/onPusherRegistrationFailed failed ");
                    sb2.append(e.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            return;
        }
        if (this.mRegistrationState == RegistrationState.UNREGISTRATED) {
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.GCM_REGISTRATING);
            try {
                new AsyncTask<Void, Void, String>() {
                    /* access modifiers changed from: protected */
                    public String doInBackground(Void... voidArr) {
                        String access$600 = GcmRegistrationManager.this.getGCMRegistrationToken();
                        if (access$600 != null) {
                            GcmRegistrationManager.this.mRegistrationToken = access$600;
                        }
                        return access$600;
                    }

                    /* access modifiers changed from: protected */
                    public void onPostExecute(String str) {
                        GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(str != null ? RegistrationState.GCM_REGISTRED : RegistrationState.UNREGISTRATED);
                        GcmRegistrationManager.this.setStoredRegistrationToken(str);
                        if (gCMRegistrationListener != null) {
                            if (str != null) {
                                try {
                                    gCMRegistrationListener.onGCMRegistered();
                                } catch (Exception e) {
                                    String access$100 = GcmRegistrationManager.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("registerToGCM : onPusherRegistered/onPusherRegistrationFailed failed ");
                                    sb.append(e.getMessage());
                                    Log.m211e(access$100, sb.toString());
                                }
                            } else {
                                gCMRegistrationListener.onGCMRegistrationFailed();
                            }
                        }
                        if (GcmRegistrationManager.this.mRegistrationState == RegistrationState.GCM_REGISTRED && GcmRegistrationManager.this.useGCM()) {
                            GcmRegistrationManager.this.register(null);
                        }
                    }
                }.execute(new Void[0]);
            } catch (Exception e2) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## registerToGCM() failed ");
                sb3.append(e2.getMessage());
                Log.m211e(str3, sb3.toString());
                if (gCMRegistrationListener != null) {
                    try {
                        gCMRegistrationListener.onGCMRegistrationFailed();
                    } catch (Exception e3) {
                        String str4 = LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("registerToGCM : onPusherRegistered/onPusherRegistrationFailed failed ");
                        sb4.append(e3.getMessage());
                        Log.m211e(str4, sb4.toString());
                    }
                }
                this.mRegistrationState = setStoredRegistrationState(RegistrationState.UNREGISTRATED);
            }
        } else if (this.mRegistrationState == RegistrationState.GCM_REGISTRATING) {
            gCMRegistrationListener.onGCMRegistrationFailed();
        } else {
            gCMRegistrationListener.onGCMRegistered();
        }
    }

    public void resetGCMRegistration() {
        resetGCMRegistration(null);
    }

    public void resetGCMRegistration(final String str) {
        Log.m209d(LOG_TAG, "resetGCMRegistration");
        if (RegistrationState.SERVER_REGISTERED == this.mRegistrationState) {
            Log.m209d(LOG_TAG, "resetGCMRegistration : unregister before retrieving the new GCM key");
            unregister(new ThirdPartyRegistrationListener() {
                public void onThirdPartyRegistered() {
                }

                public void onThirdPartyRegistrationFailed() {
                }

                public void onThirdPartyUnregistered() {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "resetGCMRegistration : unregistration is done --> start the registration process");
                    GcmRegistrationManager.this.resetGCMRegistration(str);
                }

                public void onThirdPartyUnregistrationFailed() {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "resetGCMRegistration : unregistration failed.");
                }
            });
            return;
        }
        final boolean isEmpty = TextUtils.isEmpty(str);
        Log.m209d(LOG_TAG, "resetGCMRegistration : Clear the GCM data");
        clearGCMData(isEmpty, new SimpleApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (!isEmpty) {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "resetGCMRegistration : make a full registration process.");
                    GcmRegistrationManager.this.register(null);
                    return;
                }
                Log.m209d(GcmRegistrationManager.LOG_TAG, "resetGCMRegistration : Ready to register.");
            }
        });
    }

    private static String computePushTag(MXSession mXSession) {
        StringBuilder sb = new StringBuilder();
        sb.append("mobile_");
        sb.append(Math.abs(mXSession.getMyUserId().hashCode()));
        String sb2 = sb.toString();
        if (sb2.length() <= 32) {
            return sb2;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(Math.abs(sb2.hashCode()));
        sb3.append("");
        return sb3.toString();
    }

    /* access modifiers changed from: private */
    public void manage500Error() {
        Log.m209d(LOG_TAG, "got a 500 error -> reset the registration and try again");
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (RegistrationState.SERVER_REGISTERED == GcmRegistrationManager.this.mRegistrationState) {
                    Log.m209d(GcmRegistrationManager.LOG_TAG, "500 error : unregister first");
                    GcmRegistrationManager.this.unregister(new ThirdPartyRegistrationListener() {
                        public void onThirdPartyRegistered() {
                        }

                        public void onThirdPartyRegistrationFailed() {
                        }

                        public void onThirdPartyUnregistered() {
                            Log.m209d(GcmRegistrationManager.LOG_TAG, "500 error : onThirdPartyUnregistered");
                            GcmRegistrationManager.this.setStoredRegistrationToken(null);
                            GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.UNREGISTRATED);
                            GcmRegistrationManager.this.register(null);
                        }

                        public void onThirdPartyUnregistrationFailed() {
                            Log.m209d(GcmRegistrationManager.LOG_TAG, "500 error : onThirdPartyUnregistrationFailed");
                            GcmRegistrationManager.this.setStoredRegistrationToken(null);
                            GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.UNREGISTRATED);
                            GcmRegistrationManager.this.register(null);
                        }
                    });
                    return;
                }
                Log.m209d(GcmRegistrationManager.LOG_TAG, "500 error : no GCM key");
                GcmRegistrationManager.this.setStoredRegistrationToken(null);
                GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.UNREGISTRATED);
                GcmRegistrationManager.this.register(null);
            }
        }, Constants.EVENT_UPLOAD_PERIOD_MILLIS);
    }

    public void onAppResume() {
        if (this.mRegistrationState == RegistrationState.SERVER_REGISTERED) {
            Log.m209d(LOG_TAG, "## onAppResume() : force the GCM registration");
            forceSessionsRegistration(new ThirdPartyRegistrationListener() {
                public void onThirdPartyRegistered() {
                }

                public void onThirdPartyRegistrationFailed() {
                }

                public void onThirdPartyUnregistered() {
                }

                public void onThirdPartyUnregistrationFailed() {
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void registerToThirdPartyServer(final MXSession mXSession, final boolean z, final ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        if (!areDeviceNotificationsAllowed() || !useGCM() || !mXSession.isAlive()) {
            if (!areDeviceNotificationsAllowed()) {
                Log.m209d(LOG_TAG, "registerPusher : the user disabled it.");
            } else if (!mXSession.isAlive()) {
                Log.m209d(LOG_TAG, "registerPusher : the session is not anymore alive");
            } else {
                Log.m209d(LOG_TAG, "registerPusher : GCM is disabled.");
            }
            if (thirdPartyRegistrationListener != null) {
                try {
                    thirdPartyRegistrationListener.onThirdPartyRegistrationFailed();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("registerToThirdPartyServer failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.GCM_REGISTRED);
            return;
        }
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("registerToThirdPartyServer of ");
        sb2.append(mXSession.getMyUserId());
        Log.m209d(str2, sb2.toString());
        getPushersRestClient(mXSession).addHttpPusher(this.mRegistrationToken, "com.opengarden.firechat", computePushTag(mXSession), this.mPusherLang, this.mPusherAppName, this.mBasePusherDeviceName, DEFAULT_PUSHER_URL, z, isBackgroundSyncAllowed() || !isContentSendingAllowed(), new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(GcmRegistrationManager.LOG_TAG, "registerToThirdPartyServer succeeded");
                if (thirdPartyRegistrationListener != null) {
                    try {
                        thirdPartyRegistrationListener.onThirdPartyRegistered();
                    } catch (Exception e) {
                        String access$100 = GcmRegistrationManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onSessionRegistered failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }

            private void onError(String str) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerToThirdPartyServer failed");
                sb.append(mXSession.getMyUserId());
                sb.append(" (");
                sb.append(str);
                sb.append(")");
                Log.m211e(access$100, sb.toString());
                GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.GCM_REGISTRED);
                if (thirdPartyRegistrationListener != null) {
                    try {
                        thirdPartyRegistrationListener.onThirdPartyRegistrationFailed();
                    } catch (Exception e) {
                        String access$1002 = GcmRegistrationManager.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("onThirdPartyRegistrationFailed failed ");
                        sb2.append(e.getMessage());
                        Log.m211e(access$1002, sb2.toString());
                    }
                }
            }

            public void onNetworkError(Exception exc) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerToThirdPartyServer onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$100, sb.toString());
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        if (GcmRegistrationManager.this.mRegistrationState == RegistrationState.SERVER_REGISTRATING) {
                            Log.m211e(GcmRegistrationManager.LOG_TAG, "registerToThirdPartyServer onNetworkError -> retry");
                            GcmRegistrationManager.this.registerToThirdPartyServer(mXSession, z, thirdPartyRegistrationListener);
                        }
                    }
                }, Constants.EVENT_UPLOAD_PERIOD_MILLIS);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerToThirdPartyServer onMatrixError ");
                sb.append(matrixError.errcode);
                Log.m211e(access$100, sb.toString());
                onError(matrixError.getMessage());
                if (MatrixError.UNKNOWN.equals(matrixError.errcode)) {
                    GcmRegistrationManager.this.manage500Error();
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerToThirdPartyServer onUnexpectedError ");
                sb.append(exc.getMessage());
                Log.m211e(access$100, sb.toString());
                onError(exc.getMessage());
            }
        });
    }

    public void refreshPushersList(List<MXSession> list, final ApiCallback<Void> apiCallback) {
        if (list != null && list.size() > 0) {
            getPushersRestClient((MXSession) list.get(0)).getPushers(new ApiCallback<PushersResponse>() {
                public void onSuccess(PushersResponse pushersResponse) {
                    Pusher pusher;
                    if (pushersResponse.pushers == null) {
                        GcmRegistrationManager.this.mPushersList = new ArrayList<>();
                    } else {
                        GcmRegistrationManager.this.mPushersList = new ArrayList<>(pushersResponse.pushers);
                        Iterator it = GcmRegistrationManager.this.mPushersList.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                pusher = null;
                                break;
                            }
                            pusher = (Pusher) it.next();
                            if (TextUtils.equals(pusher.pushkey, GcmRegistrationManager.this.getGCMRegistrationToken())) {
                                break;
                            }
                        }
                        if (pusher != null) {
                            GcmRegistrationManager.this.mPushersList.remove(pusher);
                            GcmRegistrationManager.this.mPushersList.add(0, pusher);
                        }
                    }
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                }

                public void onNetworkError(Exception exc) {
                    String access$100 = GcmRegistrationManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("refreshPushersList failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$100 = GcmRegistrationManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("refreshPushersList failed ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$100 = GcmRegistrationManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("refreshPushersList failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
            });
        }
    }

    public void forceSessionsRegistration(ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        if (this.mRegistrationState == RegistrationState.SERVER_REGISTERED || this.mRegistrationState == RegistrationState.GCM_REGISTRED) {
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.GCM_REGISTRED);
            register(thirdPartyRegistrationListener);
        } else if (thirdPartyRegistrationListener != null) {
            try {
                thirdPartyRegistrationListener.onThirdPartyRegistrationFailed();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("forceSessionsRegistration failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void register(final ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("register with state ");
        sb.append(this.mRegistrationState);
        Log.m209d(str, sb.toString());
        addSessionsRegistrationListener(thirdPartyRegistrationListener);
        if (this.mRegistrationState != RegistrationState.GCM_REGISTRATING && this.mRegistrationState != RegistrationState.SERVER_REGISTRATING) {
            if (this.mRegistrationState == RegistrationState.UNREGISTRATED) {
                Log.m209d(LOG_TAG, "register unregistrated : try to register again");
                registerToGCM(new GCMRegistrationListener() {
                    public void onGCMRegistered() {
                        Log.m209d(GcmRegistrationManager.LOG_TAG, "GCM registration failed again : register on server side");
                        GcmRegistrationManager.this.register(thirdPartyRegistrationListener);
                    }

                    public void onGCMRegistrationFailed() {
                        Log.m209d(GcmRegistrationManager.LOG_TAG, "register unregistrated : GCM registration failed again");
                        GcmRegistrationManager.this.dispatchOnThirdPartyRegistrationFailed();
                    }
                });
            } else if (this.mRegistrationState == RegistrationState.SERVER_REGISTERED) {
                Log.m211e(LOG_TAG, "register : already registred");
                dispatchOnThirdPartyRegistered();
            } else if (this.mRegistrationState != RegistrationState.GCM_REGISTRED) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("register : invalid state ");
                sb2.append(this.mRegistrationState);
                Log.m211e(str2, sb2.toString());
                dispatchOnThirdPartyRegistrationFailed();
            } else if (!useGCM() || !areDeviceNotificationsAllowed() || TextUtils.isEmpty(this.mRegistrationToken)) {
                dispatchOnThirdPartyRegistrationFailed();
            } else {
                this.mRegistrationState = setStoredRegistrationState(RegistrationState.SERVER_REGISTRATING);
                registerToThirdPartyServer(new ArrayList(Matrix.getInstance(this.mContext).getSessions()), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void registerToThirdPartyServer(final ArrayList<MXSession> arrayList, final int i) {
        if (i >= arrayList.size()) {
            Log.m209d(LOG_TAG, "registerSessions : all the sessions are registered");
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.SERVER_REGISTERED);
            dispatchOnThirdPartyRegistered();
            refreshPushersList(arrayList, null);
            if (!useGCM() || areDeviceNotificationsAllowed()) {
                CommonActivityUtils.onGcmUpdate(this.mContext);
            } else {
                unregister(null);
            }
            return;
        }
        final MXSession mXSession = (MXSession) arrayList.get(i);
        registerToThirdPartyServer(mXSession, i > 0, new ThirdPartyRegistrationListener() {
            public void onThirdPartyUnregistered() {
            }

            public void onThirdPartyUnregistrationFailed() {
            }

            public void onThirdPartyRegistered() {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerSessions : session ");
                sb.append(mXSession.getMyUserId());
                sb.append(" is registred");
                Log.m209d(access$100, sb.toString());
                GcmRegistrationManager.this.registerToThirdPartyServer(arrayList, i + 1);
            }

            public void onThirdPartyRegistrationFailed() {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("registerSessions : onSessionRegistrationFailed ");
                sb.append(mXSession.getMyUserId());
                Log.m209d(access$100, sb.toString());
                GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.GCM_REGISTRED);
                GcmRegistrationManager.this.dispatchOnThirdPartyRegistrationFailed();
            }
        });
    }

    public void unregister(ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("unregister with state ");
        sb.append(this.mRegistrationState);
        Log.m209d(str, sb.toString());
        addSessionsRegistrationListener(thirdPartyRegistrationListener);
        if (this.mRegistrationState != RegistrationState.SERVER_UNREGISTRATING) {
            if (this.mRegistrationState != RegistrationState.SERVER_REGISTERED) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unregisterSessions : invalid state ");
                sb2.append(this.mRegistrationState);
                Log.m211e(str2, sb2.toString());
                dispatchOnThirdPartyUnregistrationFailed();
                return;
            }
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.SERVER_UNREGISTRATING);
            unregister(new ArrayList<>(Matrix.getInstance(this.mContext).getSessions()), 0);
        }
    }

    /* access modifiers changed from: private */
    public void unregister(final ArrayList<MXSession> arrayList, final int i) {
        if (i >= arrayList.size()) {
            this.mRegistrationState = setStoredRegistrationState(RegistrationState.GCM_REGISTRED);
            if (!useGCM() || !areDeviceNotificationsAllowed() || !Matrix.hasValidSessions()) {
                CommonActivityUtils.onGcmUpdate(this.mContext);
            } else {
                register(null);
            }
            dispatchOnThirdPartyUnregistered();
            return;
        }
        unregister((MXSession) arrayList.get(i), (ThirdPartyRegistrationListener) new ThirdPartyRegistrationListener() {
            public void onThirdPartyRegistered() {
            }

            public void onThirdPartyRegistrationFailed() {
            }

            public void onThirdPartyUnregistered() {
                GcmRegistrationManager.this.unregister(arrayList, i + 1);
            }

            public void onThirdPartyUnregistrationFailed() {
                GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.SERVER_REGISTERED);
                GcmRegistrationManager.this.dispatchOnThirdPartyUnregistrationFailed();
            }
        });
    }

    public void unregister(final MXSession mXSession, Pusher pusher, final ApiCallback<Void> apiCallback) {
        getPushersRestClient(mXSession).removeHttpPusher(pusher.pushkey, pusher.appId, pusher.profileTag, pusher.lang, pusher.appDisplayName, pusher.deviceDisplayName, (String) pusher.data.get(ImagesContract.URL), new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                GcmRegistrationManager.this.mPushersRestClients.remove(mXSession.getMyUserId());
                GcmRegistrationManager.this.refreshPushersList(new ArrayList(Matrix.getInstance(GcmRegistrationManager.this.mContext).getSessions()), apiCallback);
            }

            public void onNetworkError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (matrixError.mStatus.intValue() == 404) {
                    GcmRegistrationManager.this.mPushersRestClients.remove(mXSession.getMyUserId());
                    onSuccess((Void) null);
                    return;
                }
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    public void unregister(final MXSession mXSession, final ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("unregister ");
        sb.append(mXSession.getMyUserId());
        Log.m209d(str, sb.toString());
        getPushersRestClient(mXSession).removeHttpPusher(this.mRegistrationToken, "com.opengarden.firechat", computePushTag(mXSession), this.mPusherLang, this.mPusherAppName, this.mBasePusherDeviceName, DEFAULT_PUSHER_URL, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(GcmRegistrationManager.LOG_TAG, "unregisterSession succeeded");
                if (thirdPartyRegistrationListener != null) {
                    try {
                        thirdPartyRegistrationListener.onThirdPartyUnregistered();
                    } catch (Exception e) {
                        String access$100 = GcmRegistrationManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("unregister : onThirdPartyUnregistered ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }

            private void onError(String str) {
                if (mXSession.isAlive()) {
                    String access$100 = GcmRegistrationManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("fail to unregister ");
                    sb.append(mXSession.getMyUserId());
                    sb.append(" (");
                    sb.append(str);
                    sb.append(")");
                    Log.m211e(access$100, sb.toString());
                    if (thirdPartyRegistrationListener != null) {
                        try {
                            thirdPartyRegistrationListener.onThirdPartyUnregistrationFailed();
                        } catch (Exception e) {
                            String access$1002 = GcmRegistrationManager.LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("unregister : onThirdPartyUnregistrationFailed ");
                            sb2.append(e.getMessage());
                            Log.m211e(access$1002, sb2.toString());
                        }
                    }
                }
            }

            public void onNetworkError(Exception exc) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("unregisterSession onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$100, sb.toString());
                onError(exc.getMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                if (matrixError.mStatus.intValue() == 404) {
                    onSuccess((Void) null);
                    return;
                }
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("unregisterSession onMatrixError ");
                sb.append(matrixError.errcode);
                Log.m211e(access$100, sb.toString());
                onError(matrixError.getMessage());
            }

            public void onUnexpectedError(Exception exc) {
                String access$100 = GcmRegistrationManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("unregisterSession onUnexpectedError ");
                sb.append(exc.getMessage());
                Log.m211e(access$100, sb.toString());
                onError(exc.getMessage());
            }
        });
    }

    public boolean hasRegistrationToken() {
        return this.mRegistrationToken != null;
    }

    public String getCurrentRegistrationToken() {
        return this.mRegistrationToken;
    }

    public boolean isGCMRegistred() {
        return this.mRegistrationState == RegistrationState.GCM_REGISTRED || this.mRegistrationState == RegistrationState.SERVER_REGISTRATING || this.mRegistrationState == RegistrationState.SERVER_REGISTERED;
    }

    public boolean isServerRegistred() {
        return this.mRegistrationState == RegistrationState.SERVER_REGISTERED;
    }

    public boolean isServerUnRegistred() {
        return this.mRegistrationState == RegistrationState.GCM_REGISTRED;
    }

    public void clearPreferences() {
        getGcmSharedPreferences().edit().clear().apply();
    }

    public boolean useGCM() {
        if (mUseGCM == null) {
            mUseGCM = Boolean.valueOf(true);
            try {
                mUseGCM = Boolean.valueOf(TextUtils.equals(this.mContext.getResources().getString(C1299R.string.allow_gcm_use), "true"));
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("useGCM ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return mUseGCM.booleanValue();
    }

    public NotificationPrivacy getNotificationPrivacy() {
        NotificationPrivacy notificationPrivacy = NotificationPrivacy.LOW_DETAIL;
        boolean isContentSendingAllowed = isContentSendingAllowed();
        boolean isBackgroundSyncAllowed = isBackgroundSyncAllowed();
        if (!isContentSendingAllowed || isBackgroundSyncAllowed) {
            return (isContentSendingAllowed || !isBackgroundSyncAllowed) ? notificationPrivacy : NotificationPrivacy.NORMAL;
        }
        return NotificationPrivacy.REDUCED;
    }

    public void setNotificationPrivacy(NotificationPrivacy notificationPrivacy) {
        switch (notificationPrivacy) {
            case REDUCED:
                setContentSendingAllowed(true);
                setBackgroundSyncAllowed(false);
                break;
            case LOW_DETAIL:
                setContentSendingAllowed(false);
                setBackgroundSyncAllowed(false);
                break;
            case NORMAL:
                setContentSendingAllowed(false);
                setBackgroundSyncAllowed(true);
                break;
        }
        forceSessionsRegistration(null);
    }

    public boolean areDeviceNotificationsAllowed() {
        return getGcmSharedPreferences().getBoolean(PREFS_ALLOW_NOTIFICATIONS, true);
    }

    public void setDeviceNotificationsAllowed(boolean z) {
        getGcmSharedPreferences().edit().putBoolean(PREFS_ALLOW_NOTIFICATIONS, z).apply();
        if (!useGCM()) {
            CommonActivityUtils.onGcmUpdate(this.mContext);
        }
    }

    public boolean isScreenTurnedOn() {
        return getGcmSharedPreferences().getBoolean(PREFS_TURN_SCREEN_ON, false);
    }

    public void setScreenTurnedOn(boolean z) {
        getGcmSharedPreferences().edit().putBoolean(PREFS_TURN_SCREEN_ON, z).apply();
    }

    public boolean isBackgroundSyncAllowed() {
        if (!PreferencesManager.useBatteryOptimisation(this.mContext)) {
            return false;
        }
        return getGcmSharedPreferences().getBoolean(PREFS_ALLOW_BACKGROUND_SYNC, true);
    }

    public void setBackgroundSyncAllowed(boolean z) {
        getGcmSharedPreferences().edit().putBoolean(PREFS_ALLOW_BACKGROUND_SYNC, z).apply();
        CommonActivityUtils.onGcmUpdate(this.mContext);
    }

    public boolean canStartAppInBackground() {
        return isBackgroundSyncAllowed() || getStoredRegistrationToken() != null;
    }

    public boolean isContentSendingAllowed() {
        return getGcmSharedPreferences().getBoolean(PREFS_ALLOW_SENDING_CONTENT_TO_GCM, true);
    }

    public void setContentSendingAllowed(boolean z) {
        getGcmSharedPreferences().edit().putBoolean(PREFS_ALLOW_SENDING_CONTENT_TO_GCM, z).apply();
    }

    public int getBackgroundSyncTimeOut() {
        return getGcmSharedPreferences().getInt(PREFS_SYNC_TIMEOUT, 6000);
    }

    public void setBackgroundSyncTimeOut(int i) {
        getGcmSharedPreferences().edit().putInt(PREFS_SYNC_TIMEOUT, i).apply();
    }

    public int getBackgroundSyncDelay() {
        if (this.mRegistrationToken == null && getStoredRegistrationToken() == null && !getGcmSharedPreferences().contains(PREFS_SYNC_DELAY)) {
            return 60000;
        }
        int i = 0;
        MXSession defaultSession = Matrix.getInstance(this.mContext).getDefaultSession();
        if (defaultSession != null) {
            i = defaultSession.getSyncDelay();
        }
        return getGcmSharedPreferences().getInt(PREFS_SYNC_DELAY, i);
    }

    public void setBackgroundSyncDelay(int i) {
        if (this.mRegistrationToken == null) {
            i = Math.max(i, 1000);
        }
        getGcmSharedPreferences().edit().putInt(PREFS_SYNC_DELAY, i).apply();
    }

    private SharedPreferences getGcmSharedPreferences() {
        return this.mContext.getSharedPreferences("GcmRegistrationManager", 0);
    }

    private String getStoredRegistrationToken() {
        return getGcmSharedPreferences().getString(PREFS_PUSHER_REGISTRATION_TOKEN_KEY_FCM, null);
    }

    private String getOldStoredRegistrationToken() {
        return getGcmSharedPreferences().getString(PREFS_PUSHER_REGISTRATION_TOKEN_KEY, null);
    }

    /* access modifiers changed from: private */
    public void clearOldStoredRegistrationToken() {
        Log.m209d(LOG_TAG, "Remove old registration token");
        getGcmSharedPreferences().edit().remove(PREFS_PUSHER_REGISTRATION_TOKEN_KEY).apply();
    }

    /* access modifiers changed from: private */
    public void setStoredRegistrationToken(String str) {
        Log.m209d(LOG_TAG, "Saving registration token");
        getGcmSharedPreferences().edit().putString(PREFS_PUSHER_REGISTRATION_TOKEN_KEY_FCM, str).apply();
    }

    private RegistrationState getStoredRegistrationState() {
        return RegistrationState.values()[getGcmSharedPreferences().getInt(PREFS_PUSHER_REGISTRATION_STATUS, RegistrationState.UNREGISTRATED.ordinal())];
    }

    /* access modifiers changed from: private */
    public RegistrationState setStoredRegistrationState(RegistrationState registrationState) {
        if (!(RegistrationState.GCM_REGISTRATING == registrationState || RegistrationState.SERVER_REGISTRATING == registrationState || RegistrationState.SERVER_UNREGISTRATING == registrationState)) {
            getGcmSharedPreferences().edit().putInt(PREFS_PUSHER_REGISTRATION_STATUS, registrationState.ordinal()).apply();
        }
        return registrationState;
    }

    public void clearGCMData(final boolean z, final ApiCallback apiCallback) {
        try {
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... voidArr) {
                    GcmRegistrationManager.this.setStoredRegistrationToken(null);
                    GcmRegistrationManager.this.mRegistrationToken = null;
                    GcmRegistrationManager.this.mRegistrationState = GcmRegistrationManager.this.setStoredRegistrationState(RegistrationState.UNREGISTRATED);
                    if (z) {
                        GCMHelper.clearRegistrationToken();
                    }
                    return null;
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Void voidR) {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                }
            }.execute(new Void[0]);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## clearGCMData failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            if (apiCallback != null) {
                apiCallback.onUnexpectedError(e);
            }
        }
    }

    private void addSessionsRegistrationListener(ThirdPartyRegistrationListener thirdPartyRegistrationListener) {
        synchronized (this) {
            if (thirdPartyRegistrationListener != null) {
                try {
                    if (this.mThirdPartyRegistrationListeners.indexOf(thirdPartyRegistrationListener) == -1) {
                        this.mThirdPartyRegistrationListeners.add(thirdPartyRegistrationListener);
                    }
                } finally {
                }
            }
        }
    }

    private void dispatchOnThirdPartyRegistered() {
        PreferencesManager.setAutoStartOnBoot(this.mContext, false);
        synchronized (this) {
            Iterator it = this.mThirdPartyRegistrationListeners.iterator();
            while (it.hasNext()) {
                try {
                    ((ThirdPartyRegistrationListener) it.next()).onThirdPartyRegistered();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onSessionsRegistered ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            this.mThirdPartyRegistrationListeners.clear();
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnThirdPartyRegistrationFailed() {
        synchronized (this) {
            Iterator it = this.mThirdPartyRegistrationListeners.iterator();
            while (it.hasNext()) {
                try {
                    ((ThirdPartyRegistrationListener) it.next()).onThirdPartyRegistrationFailed();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onSessionsRegistrationFailed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            this.mThirdPartyRegistrationListeners.clear();
        }
    }

    private void dispatchOnThirdPartyUnregistered() {
        synchronized (this) {
            Iterator it = this.mThirdPartyRegistrationListeners.iterator();
            while (it.hasNext()) {
                try {
                    ((ThirdPartyRegistrationListener) it.next()).onThirdPartyUnregistered();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onSessionUnregistered ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            this.mThirdPartyRegistrationListeners.clear();
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnThirdPartyUnregistrationFailed() {
        synchronized (this) {
            Iterator it = this.mThirdPartyRegistrationListeners.iterator();
            while (it.hasNext()) {
                try {
                    ((ThirdPartyRegistrationListener) it.next()).onThirdPartyUnregistrationFailed();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("dispatchOnThirdPartyUnregistrationFailed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            this.mThirdPartyRegistrationListeners.clear();
        }
    }
}
