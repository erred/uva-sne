package com.opengarden.firechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.opengarden.firechat.UnrecognizedCertHandler.Callback;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.SplashActivity;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXDataHandler.RequestNetworkErrorListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.MXSession.Builder;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequestCancellation;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto.IRoomKeysRequestListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.data.store.MXFileStore;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.p007db.MXLatestChatMessageCache;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.LoginRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.ssl.Fingerprint;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.store.LoginStorage;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Matrix {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "Matrix";
    /* access modifiers changed from: private */
    public static Matrix instance;
    /* access modifiers changed from: private */
    public static final MXEventListener mLiveEventListener = new MXEventListener() {
        boolean mClearCacheRequired = false;
        private boolean mRefreshUnreadCounter = false;

        public void onIgnoredUsersListUpdate() {
            this.mClearCacheRequired = true;
        }

        public void onLiveEvent(Event event, RoomState roomState) {
            this.mRefreshUnreadCounter |= Event.EVENT_TYPE_MESSAGE.equals(event.getType()) || Event.EVENT_TYPE_RECEIPT.equals(event.getType());
            WidgetsManager.getSharedInstance().onLiveEvent(Matrix.instance.getDefaultSession(), event);
        }

        public void onLiveEventsChunkProcessed(String str, String str2) {
            if (!(Matrix.instance == null || Matrix.instance.mMXSessions == null)) {
                if (this.mClearCacheRequired && !VectorApp.isAppInBackground()) {
                    this.mClearCacheRequired = false;
                    Matrix.instance.reloadSessions(VectorApp.getInstance());
                } else if (this.mRefreshUnreadCounter) {
                    GcmRegistrationManager sharedGCMRegistrationManager = Matrix.instance.getSharedGCMRegistrationManager();
                    if (sharedGCMRegistrationManager != null && (!sharedGCMRegistrationManager.useGCM() || !sharedGCMRegistrationManager.hasRegistrationToken())) {
                        Iterator it = Matrix.instance.mMXSessions.iterator();
                        int i = 0;
                        while (it.hasNext()) {
                            MXSession mXSession = (MXSession) it.next();
                            if (mXSession.isAlive()) {
                                BingRulesManager bingRulesManager = mXSession.getDataHandler().getBingRulesManager();
                                for (Room room : mXSession.getDataHandler().getStore().getRooms()) {
                                    if (room.isInvited()) {
                                        i++;
                                    } else {
                                        int notificationCount = room.getNotificationCount();
                                        if (bingRulesManager.isRoomMentionOnly(room.getRoomId())) {
                                            notificationCount = room.getHighlightCount();
                                        }
                                        if (notificationCount > 0) {
                                            i++;
                                        }
                                    }
                                }
                            }
                        }
                        CommonActivityUtils.updateBadgeCount(Matrix.instance.mAppContext, i);
                    }
                }
                VectorApp.clearSyncingSessions();
            }
            this.mRefreshUnreadCounter = false;
            Log.m209d(Matrix.LOG_TAG, "onLiveEventsChunkProcessed ");
            EventStreamService.checkDisplayedNotifications();
        }
    };
    /* access modifiers changed from: private */
    public final Context mAppContext;
    private final GcmRegistrationManager mGCMRegistrationManager;
    public boolean mHasBeenDisconnected = false;
    /* access modifiers changed from: private */
    public final LoginStorage mLoginStorage;
    /* access modifiers changed from: private */
    public ArrayList<MXSession> mMXSessions;
    private ArrayList<IMXStore> mTmpStores;

    private Matrix(Context context) {
        instance = this;
        this.mAppContext = context.getApplicationContext();
        this.mLoginStorage = new LoginStorage(this.mAppContext);
        this.mMXSessions = new ArrayList<>();
        this.mTmpStores = new ArrayList<>();
        this.mGCMRegistrationManager = new GcmRegistrationManager(this.mAppContext);
    }

    public static synchronized Matrix getInstance(Context context) {
        Matrix matrix;
        synchronized (Matrix.class) {
            if (instance == null && context != null) {
                instance = new Matrix(context);
            }
            matrix = instance;
        }
        return matrix;
    }

    public LoginStorage getLoginStorage() {
        return this.mLoginStorage;
    }

    public static String getApplicationName() {
        return instance.mAppContext.getApplicationInfo().loadLabel(instance.mAppContext.getPackageManager()).toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00cd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getVersion(boolean r9, boolean r10) {
        /*
            r8 = this;
            java.lang.String r0 = ""
            java.lang.String r1 = ""
            r2 = 0
            android.content.Context r3 = r8.mAppContext     // Catch:{ Exception -> 0x0044 }
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ Exception -> 0x0044 }
            android.content.Context r4 = r8.mAppContext     // Catch:{ Exception -> 0x0044 }
            java.lang.String r4 = r4.getPackageName()     // Catch:{ Exception -> 0x0044 }
            android.content.pm.PackageInfo r3 = r3.getPackageInfo(r4, r2)     // Catch:{ Exception -> 0x0044 }
            java.lang.String r3 = r3.versionName     // Catch:{ Exception -> 0x0044 }
            android.content.Context r0 = r8.mAppContext     // Catch:{ Exception -> 0x003f }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x003f }
            r4 = 2131690290(0x7f0f0332, float:1.900962E38)
            java.lang.String r0 = r0.getString(r4)     // Catch:{ Exception -> 0x003f }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x003d }
            if (r1 != 0) goto L_0x0063
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x003d }
            r1.<init>()     // Catch:{ Exception -> 0x003d }
            r1.append(r0)     // Catch:{ Exception -> 0x003d }
            java.lang.String r4 = "-"
            r1.append(r4)     // Catch:{ Exception -> 0x003d }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x003d }
            r0 = r1
            goto L_0x0063
        L_0x003d:
            r1 = move-exception
            goto L_0x0049
        L_0x003f:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x0049
        L_0x0044:
            r3 = move-exception
            r7 = r3
            r3 = r0
            r0 = r1
            r1 = r7
        L_0x0049:
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "## versionName() : failed "
            r5.append(r6)
            java.lang.String r1 = r1.getMessage()
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r1)
        L_0x0063:
            android.content.Context r1 = r8.mAppContext
            android.content.res.Resources r1 = r1.getResources()
            r4 = 2131689776(0x7f0f0130, float:1.9008577E38)
            java.lang.String r1 = r1.getString(r4)
            android.content.Context r4 = r8.mAppContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131689586(0x7f0f0072, float:1.9008192E38)
            java.lang.String r4 = r4.getString(r5)
            if (r10 == 0) goto L_0x0099
            java.lang.String r10 = "0"
            boolean r10 = android.text.TextUtils.equals(r4, r10)
            if (r10 != 0) goto L_0x0099
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "b"
            r9.append(r10)
            r9.append(r4)
            java.lang.String r1 = r9.toString()
            r9 = 0
        L_0x0099:
            if (r9 == 0) goto L_0x00cd
            android.content.Context r9 = r8.mAppContext
            android.content.res.Resources r9 = r9.getResources()
            r10 = 2131689777(0x7f0f0131, float:1.9008579E38)
            java.lang.String r9 = r9.getString(r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r3)
            java.lang.String r2 = " ("
            r10.append(r2)
            r10.append(r0)
            r10.append(r1)
            java.lang.String r0 = "-"
            r10.append(r0)
            r10.append(r9)
            java.lang.String r9 = ")"
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            goto L_0x00e9
        L_0x00cd:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r3)
            java.lang.String r10 = " ("
            r9.append(r10)
            r9.append(r0)
            r9.append(r1)
            java.lang.String r10 = ")"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
        L_0x00e9:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.Matrix.getVersion(boolean, boolean):java.lang.String");
    }

    public static ArrayList<MXSession> getMXSessions(Context context) {
        if (context == null || instance == null) {
            return null;
        }
        return instance.getSessions();
    }

    public ArrayList<MXSession> getSessions() {
        ArrayList<MXSession> arrayList = new ArrayList<>();
        synchronized (LOG_TAG) {
            if (this.mMXSessions != null) {
                arrayList = new ArrayList<>(this.mMXSessions);
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:46:0x009d, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.opengarden.firechat.matrixsdk.MXSession getDefaultSession() {
        /*
            r9 = this;
            monitor-enter(r9)
            java.util.ArrayList r0 = r9.getSessions()     // Catch:{ all -> 0x009e }
            int r1 = r0.size()     // Catch:{ all -> 0x009e }
            r2 = 0
            if (r1 <= 0) goto L_0x0014
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.MXSession r0 = (com.opengarden.firechat.matrixsdk.MXSession) r0     // Catch:{ all -> 0x009e }
            monitor-exit(r9)
            return r0
        L_0x0014:
            com.opengarden.firechat.store.LoginStorage r0 = r9.mLoginStorage     // Catch:{ all -> 0x009e }
            java.util.ArrayList r0 = r0.getCredentialsList()     // Catch:{ all -> 0x009e }
            r1 = 0
            if (r0 == 0) goto L_0x009c
            int r3 = r0.size()     // Catch:{ all -> 0x009e }
            if (r3 != 0) goto L_0x0025
            goto L_0x009c
        L_0x0025:
            com.opengarden.firechat.VectorApp r3 = com.opengarden.firechat.VectorApp.getInstance()     // Catch:{ all -> 0x009e }
            boolean r3 = r3.didAppCrash()     // Catch:{ all -> 0x009e }
            java.util.HashSet r4 = new java.util.HashSet     // Catch:{ all -> 0x009e }
            r4.<init>()     // Catch:{ all -> 0x009e }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x009e }
            r5.<init>()     // Catch:{ all -> 0x009e }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x009e }
        L_0x003b:
            boolean r6 = r0.hasNext()     // Catch:{ all -> 0x009e }
            if (r6 == 0) goto L_0x0083
            java.lang.Object r6 = r0.next()     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r6 = (com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig) r6     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r7 = r6.getCredentials()     // Catch:{ all -> 0x009e }
            if (r7 == 0) goto L_0x003b
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r7 = r6.getCredentials()     // Catch:{ all -> 0x009e }
            java.lang.String r7 = r7.userId     // Catch:{ all -> 0x009e }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x009e }
            if (r7 != 0) goto L_0x003b
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r7 = r6.getCredentials()     // Catch:{ all -> 0x009e }
            java.lang.String r7 = r7.userId     // Catch:{ all -> 0x009e }
            boolean r7 = r4.contains(r7)     // Catch:{ all -> 0x009e }
            if (r7 != 0) goto L_0x003b
            com.opengarden.firechat.matrixsdk.MXSession r7 = r9.createSession(r6)     // Catch:{ all -> 0x009e }
            if (r3 == 0) goto L_0x0076
            com.opengarden.firechat.VectorApp r8 = com.opengarden.firechat.VectorApp.getInstance()     // Catch:{ all -> 0x009e }
            r7.clear(r8)     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.MXSession r7 = r9.createSession(r6)     // Catch:{ all -> 0x009e }
        L_0x0076:
            r5.add(r7)     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r6 = r6.getCredentials()     // Catch:{ all -> 0x009e }
            java.lang.String r6 = r6.userId     // Catch:{ all -> 0x009e }
            r4.add(r6)     // Catch:{ all -> 0x009e }
            goto L_0x003b
        L_0x0083:
            java.lang.String r0 = LOG_TAG     // Catch:{ all -> 0x009e }
            monitor-enter(r0)     // Catch:{ all -> 0x009e }
            r9.mMXSessions = r5     // Catch:{ all -> 0x0099 }
            monitor-exit(r0)     // Catch:{ all -> 0x0099 }
            int r0 = r5.size()     // Catch:{ all -> 0x009e }
            if (r0 != 0) goto L_0x0091
            monitor-exit(r9)
            return r1
        L_0x0091:
            java.lang.Object r0 = r5.get(r2)     // Catch:{ all -> 0x009e }
            com.opengarden.firechat.matrixsdk.MXSession r0 = (com.opengarden.firechat.matrixsdk.MXSession) r0     // Catch:{ all -> 0x009e }
            monitor-exit(r9)
            return r0
        L_0x0099:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0099 }
            throw r1     // Catch:{ all -> 0x009e }
        L_0x009c:
            monitor-exit(r9)
            return r1
        L_0x009e:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.Matrix.getDefaultSession():com.opengarden.firechat.matrixsdk.MXSession");
    }

    public static MXSession getMXSession(Context context, String str) {
        return getInstance(context.getApplicationContext()).getSession(str);
    }

    public synchronized MXSession getSession(String str) {
        if (str != null) {
            try {
                synchronized (this) {
                }
                Iterator it = getSessions().iterator();
                while (it.hasNext()) {
                    MXSession mXSession = (MXSession) it.next();
                    Credentials credentials = mXSession.getCredentials();
                    if (credentials != null && credentials.userId.equals(str)) {
                        return mXSession;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return getDefaultSession();
    }

    public static void setSessionErrorListener(Activity activity) {
        if (instance != null && activity != null) {
            for (MXSession mXSession : getMXSessions(activity)) {
                if (mXSession.isAlive()) {
                    mXSession.setFailureCallback(new ErrorListener(mXSession, activity));
                }
            }
        }
    }

    public static void removeSessionErrorListener(Activity activity) {
        if (instance != null && activity != null) {
            for (MXSession mXSession : getMXSessions(activity)) {
                if (mXSession.isAlive()) {
                    mXSession.setFailureCallback(null);
                }
            }
        }
    }

    public MXMediasCache getMediasCache() {
        if (getSessions().size() > 0) {
            return ((MXSession) getSessions().get(0)).getMediasCache();
        }
        return null;
    }

    public MXLatestChatMessageCache getDefaultLatestChatMessageCache() {
        if (getSessions().size() > 0) {
            return ((MXSession) getSessions().get(0)).getLatestChatMessageCache();
        }
        return null;
    }

    public static boolean hasValidSessions() {
        boolean z;
        if (instance == null) {
            Log.m211e(LOG_TAG, "hasValidSessions : has no instance");
            return false;
        }
        synchronized (LOG_TAG) {
            z = instance.mMXSessions != null && instance.mMXSessions.size() > 0;
            if (!z) {
                Log.m211e(LOG_TAG, "hasValidSessions : has no session");
            } else {
                Iterator it = instance.mMXSessions.iterator();
                while (it.hasNext()) {
                    MXSession mXSession = (MXSession) it.next();
                    z &= mXSession.isAlive() && mXSession.getDataHandler() != null;
                }
                if (!z) {
                    Log.m211e(LOG_TAG, "hasValidSessions : one sesssion has no valid data handler");
                }
            }
        }
        return z;
    }

    public void deactivateSession(Context context, final MXSession mXSession, String str, boolean z, @NonNull final ApiCallback<Void> apiCallback) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## deactivateSession() ");
        sb.append(mXSession.getMyUserId());
        Log.m209d(str2, sb.toString());
        mXSession.deactivateAccount(context, LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD, str, z, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Matrix.this.mLoginStorage.removeCredentials(mXSession.getHomeServerConfig());
                mXSession.getDataHandler().removeListener(Matrix.mLiveEventListener);
                VectorApp.removeSyncingSession(mXSession);
                synchronized (Matrix.LOG_TAG) {
                    Matrix.this.mMXSessions.remove(mXSession);
                }
                apiCallback.onSuccess(voidR);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x006d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clearSession(android.content.Context r4, final com.opengarden.firechat.matrixsdk.MXSession r5, boolean r6, final com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r7) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r5.isAlive()     // Catch:{ all -> 0x006e }
            if (r0 != 0) goto L_0x0028
            java.lang.String r4 = LOG_TAG     // Catch:{ all -> 0x006e }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
            r6.<init>()     // Catch:{ all -> 0x006e }
            java.lang.String r7 = "## clearSession() "
            r6.append(r7)     // Catch:{ all -> 0x006e }
            java.lang.String r5 = r5.getMyUserId()     // Catch:{ all -> 0x006e }
            r6.append(r5)     // Catch:{ all -> 0x006e }
            java.lang.String r5 = " is already released"
            r6.append(r5)     // Catch:{ all -> 0x006e }
            java.lang.String r5 = r6.toString()     // Catch:{ all -> 0x006e }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r5)     // Catch:{ all -> 0x006e }
            monitor-exit(r3)
            return
        L_0x0028:
            java.lang.String r0 = LOG_TAG     // Catch:{ all -> 0x006e }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
            r1.<init>()     // Catch:{ all -> 0x006e }
            java.lang.String r2 = "## clearSession() "
            r1.append(r2)     // Catch:{ all -> 0x006e }
            java.lang.String r2 = r5.getMyUserId()     // Catch:{ all -> 0x006e }
            r1.append(r2)     // Catch:{ all -> 0x006e }
            java.lang.String r2 = " clearCredentials "
            r1.append(r2)     // Catch:{ all -> 0x006e }
            r1.append(r6)     // Catch:{ all -> 0x006e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x006e }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)     // Catch:{ all -> 0x006e }
            if (r6 == 0) goto L_0x0055
            com.opengarden.firechat.store.LoginStorage r0 = r3.mLoginStorage     // Catch:{ all -> 0x006e }
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r1 = r5.getHomeServerConfig()     // Catch:{ all -> 0x006e }
            r0.removeCredentials(r1)     // Catch:{ all -> 0x006e }
        L_0x0055:
            com.opengarden.firechat.matrixsdk.MXDataHandler r0 = r5.getDataHandler()     // Catch:{ all -> 0x006e }
            com.opengarden.firechat.matrixsdk.listeners.MXEventListener r1 = mLiveEventListener     // Catch:{ all -> 0x006e }
            r0.removeListener(r1)     // Catch:{ all -> 0x006e }
            com.opengarden.firechat.Matrix$3 r0 = new com.opengarden.firechat.Matrix$3     // Catch:{ all -> 0x006e }
            r0.<init>(r5, r7)     // Catch:{ all -> 0x006e }
            if (r6 == 0) goto L_0x0069
            r5.logout(r4, r0)     // Catch:{ all -> 0x006e }
            goto L_0x006c
        L_0x0069:
            r5.clear(r4, r0)     // Catch:{ all -> 0x006e }
        L_0x006c:
            monitor-exit(r3)
            return
        L_0x006e:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.Matrix.clearSession(android.content.Context, com.opengarden.firechat.matrixsdk.MXSession, boolean, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback):void");
    }

    public synchronized void clearSessions(Context context, boolean z, ApiCallback<Void> apiCallback) {
        ArrayList arrayList;
        synchronized (LOG_TAG) {
            arrayList = new ArrayList(this.mMXSessions);
        }
        clearSessions(context, arrayList.iterator(), z, apiCallback);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clearSessions(android.content.Context r9, java.util.Iterator<com.opengarden.firechat.matrixsdk.MXSession> r10, boolean r11, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r12) {
        /*
            r8 = this;
            monitor-enter(r8)
            boolean r0 = r10.hasNext()     // Catch:{ all -> 0x0025 }
            if (r0 != 0) goto L_0x000f
            if (r12 == 0) goto L_0x000d
            r9 = 0
            r12.onSuccess(r9)     // Catch:{ all -> 0x0025 }
        L_0x000d:
            monitor-exit(r8)
            return
        L_0x000f:
            java.lang.Object r0 = r10.next()     // Catch:{ all -> 0x0025 }
            com.opengarden.firechat.matrixsdk.MXSession r0 = (com.opengarden.firechat.matrixsdk.MXSession) r0     // Catch:{ all -> 0x0025 }
            com.opengarden.firechat.Matrix$4 r7 = new com.opengarden.firechat.Matrix$4     // Catch:{ all -> 0x0025 }
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r1.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x0025 }
            r8.clearSession(r9, r0, r11, r7)     // Catch:{ all -> 0x0025 }
            monitor-exit(r8)
            return
        L_0x0025:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.Matrix.clearSessions(android.content.Context, java.util.Iterator, boolean, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback):void");
    }

    public synchronized void addSession(MXSession mXSession) {
        this.mLoginStorage.addCredentials(mXSession.getHomeServerConfig());
        synchronized (LOG_TAG) {
            this.mMXSessions.add(mXSession);
        }
    }

    public MXSession createSession(HomeServerConnectionConfig homeServerConnectionConfig) {
        return createSession(this.mAppContext, homeServerConnectionConfig);
    }

    private MXSession createSession(Context context, HomeServerConnectionConfig homeServerConnectionConfig) {
        Credentials credentials = homeServerConnectionConfig.getCredentials();
        MXFileStore mXFileStore = new MXFileStore(homeServerConnectionConfig, context);
        new MXDataHandler(mXFileStore, credentials);
        final MXSession build = new Builder(homeServerConnectionConfig, new MXDataHandler(mXFileStore, credentials), this.mAppContext).withPushServerUrl(context.getString(C1299R.string.push_server_url)).build();
        build.getDataHandler().setRequestNetworkErrorListener(new RequestNetworkErrorListener() {
            public void onConfigurationError(String str) {
                String access$300 = Matrix.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## createSession() : onConfigurationError ");
                sb.append(str);
                Log.m211e(access$300, sb.toString());
                if (TextUtils.equals(str, MatrixError.UNKNOWN_TOKEN) && VectorApp.getCurrentActivity() != null) {
                    Log.m211e(Matrix.LOG_TAG, "## createSession() : onTokenCorrupted");
                    CommonActivityUtils.logout(VectorApp.getCurrentActivity());
                }
            }

            public void onSSLCertificateError(UnrecognizedCertificateException unrecognizedCertificateException) {
                if (VectorApp.getCurrentActivity() != null) {
                    Fingerprint fingerprint = unrecognizedCertificateException.getFingerprint();
                    String access$300 = Matrix.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## createSession() : Found fingerprint: SHA-256: ");
                    sb.append(fingerprint.getBytesAsHexString());
                    Log.m209d(access$300, sb.toString());
                    UnrecognizedCertHandler.show(build.getHomeServerConfig(), fingerprint, true, new Callback() {
                        public void onIgnore() {
                        }

                        public void onAccept() {
                            Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).getLoginStorage().replaceCredentials(build.getHomeServerConfig());
                        }

                        public void onReject() {
                            Log.m209d(Matrix.LOG_TAG, "Found fingerprint: reject fingerprint");
                            CommonActivityUtils.logout((Context) VectorApp.getCurrentActivity(), Arrays.asList(new MXSession[]{build}), true, null);
                        }
                    });
                }
            }
        });
        if (!TextUtils.isEmpty(credentials.deviceId)) {
            build.enableCryptoWhenStarting();
        }
        build.getDataHandler().addListener(mLiveEventListener);
        build.setUseDataSaveMode(PreferencesManager.useDataSaveMode(context));
        build.getDataHandler().addListener(new MXEventListener() {
            public void onInitialSyncComplete(String str) {
                if (build.getCrypto() != null) {
                    build.getCrypto().addRoomKeysRequestListener(new IRoomKeysRequestListener() {
                        public void onRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
                            KeyRequestHandler.getSharedInstance().handleKeyRequest(incomingRoomKeyRequest);
                        }

                        public void onRoomKeyRequestCancellation(IncomingRoomKeyRequestCancellation incomingRoomKeyRequestCancellation) {
                            KeyRequestHandler.getSharedInstance().handleKeyRequestCancellation(incomingRoomKeyRequestCancellation);
                        }
                    });
                }
            }
        });
        return build;
    }

    public void reloadSessions(final Context context) {
        Log.m211e(LOG_TAG, "## reloadSessions");
        CommonActivityUtils.logout(context, (List<MXSession>) getMXSessions(context), false, (ApiCallback<Void>) new SimpleApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                synchronized (Matrix.LOG_TAG) {
                    Iterator it = Matrix.this.mLoginStorage.getCredentialsList().iterator();
                    while (it.hasNext()) {
                        Matrix.this.mMXSessions.add(Matrix.this.createSession((HomeServerConnectionConfig) it.next()));
                    }
                }
                Matrix.getInstance(context).getSharedGCMRegistrationManager().clearGCMData(false, new SimpleApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        Intent intent = new Intent(context.getApplicationContext(), SplashActivity.class);
                        intent.setFlags(268468224);
                        context.getApplicationContext().startActivity(intent);
                        if (VectorApp.getCurrentActivity() != null) {
                            VectorApp.getCurrentActivity().finish();
                        }
                    }
                });
            }
        });
    }

    public GcmRegistrationManager getSharedGCMRegistrationManager() {
        return this.mGCMRegistrationManager;
    }

    public void refreshPushRules() {
        ArrayList sessions;
        synchronized (this) {
            sessions = getSessions();
        }
        Iterator it = sessions.iterator();
        while (it.hasNext()) {
            MXSession mXSession = (MXSession) it.next();
            if (mXSession.getDataHandler() != null) {
                mXSession.getDataHandler().refreshPushRules();
            }
        }
    }

    public void addNetworkEventListener(IMXNetworkEventListener iMXNetworkEventListener) {
        if (getDefaultSession() != null && iMXNetworkEventListener != null) {
            getDefaultSession().getNetworkConnectivityReceiver().addEventListener(iMXNetworkEventListener);
        }
    }

    public void removeNetworkEventListener(IMXNetworkEventListener iMXNetworkEventListener) {
        if (getDefaultSession() != null && iMXNetworkEventListener != null) {
            getDefaultSession().getNetworkConnectivityReceiver().removeEventListener(iMXNetworkEventListener);
        }
    }

    public boolean isConnected() {
        if (getDefaultSession() != null) {
            return getDefaultSession().getNetworkConnectivityReceiver().isConnected();
        }
        return true;
    }

    public int addTmpStore(IMXStore iMXStore) {
        if (iMXStore == null) {
            return -1;
        }
        int indexOf = this.mTmpStores.indexOf(iMXStore);
        if (indexOf < 0) {
            this.mTmpStores.add(iMXStore);
            indexOf = this.mTmpStores.indexOf(iMXStore);
        }
        return indexOf;
    }

    public IMXStore getTmpStore(int i) {
        if (i < 0 || i >= this.mTmpStores.size()) {
            return null;
        }
        return (IMXStore) this.mTmpStores.get(i);
    }

    public void clearTmpStoresList() {
        this.mTmpStores = new ArrayList<>();
    }
}
