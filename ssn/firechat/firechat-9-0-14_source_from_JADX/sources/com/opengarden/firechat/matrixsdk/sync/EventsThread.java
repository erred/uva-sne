package com.opengarden.firechat.matrixsdk.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.p000v4.app.NotificationCompat;
import com.google.gson.Gson;
import com.opengarden.firechat.matrixsdk.data.metrics.MetricsListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class EventsThread extends Thread {
    private static final int DEFAULT_CLIENT_TIMEOUT_MS = 120000;
    private static final int DEFAULT_SERVER_TIMEOUT_MS = 30000;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "EventsThread";
    private static final int RETRY_WAIT_TIME_MS = 10000;
    /* access modifiers changed from: private */
    public static final Map<String, EventsThread> mSyncObjectByInstance = new HashMap();
    private final AlarmManager mAlarmManager;
    private final Context mContext;
    /* access modifiers changed from: private */
    public String mCurrentToken;
    /* access modifiers changed from: private */
    public int mDefaultServerTimeoutms = DEFAULT_SERVER_TIMEOUT_MS;
    private EventsRestClient mEventsRestClient;
    private ApiFailureCallback mFailureCallback;
    private String mFilterOrFilterId;
    /* access modifiers changed from: private */
    public boolean mInitialSyncDone = false;
    /* access modifiers changed from: private */
    public boolean mIsCatchingUp = false;
    private boolean mIsInDataSaveMode = false;
    /* access modifiers changed from: private */
    public boolean mIsNetworkSuspended = false;
    private boolean mIsOnline = false;
    /* access modifiers changed from: private */
    public boolean mKilling = false;
    /* access modifiers changed from: private */
    public EventsThreadListener mListener;
    private MetricsListener mMetricsListener;
    private NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private final IMXNetworkEventListener mNetworkListener = new IMXNetworkEventListener() {
        public void onNetworkConnectionUpdate(boolean z) {
            String access$000 = EventsThread.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onNetworkConnectionUpdate : before ");
            sb.append(EventsThread.this.mbIsConnected);
            sb.append(" now ");
            sb.append(z);
            Log.m209d(access$000, sb.toString());
            synchronized (EventsThread.this.mSyncObject) {
                EventsThread.this.mbIsConnected = z;
            }
            if (z && !EventsThread.this.mKilling) {
                Log.m209d(EventsThread.LOG_TAG, "onNetworkConnectionUpdate : call onNetworkAvailable");
                EventsThread.this.onNetworkAvailable();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mNextServerTimeoutms = DEFAULT_SERVER_TIMEOUT_MS;
    /* access modifiers changed from: private */
    public boolean mPaused = true;
    /* access modifiers changed from: private */
    public PendingIntent mPendingDelayedIntent;
    private PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public int mRequestDelayMs = 0;
    /* access modifiers changed from: private */
    public final Object mSyncObject = new Object();
    /* access modifiers changed from: private */
    public boolean mbIsConnected = true;

    public static class SyncDelayReceiver extends BroadcastReceiver {
        public static final String EXTRA_INSTANCE_ID = "EXTRA_INSTANCE_ID";

        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra(EXTRA_INSTANCE_ID);
            if (stringExtra != null && EventsThread.mSyncObjectByInstance.containsKey(stringExtra)) {
                EventsThread eventsThread = (EventsThread) EventsThread.mSyncObjectByInstance.get(stringExtra);
                eventsThread.mPendingDelayedIntent = null;
                String access$000 = EventsThread.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("start a sync after ");
                sb.append(eventsThread.mRequestDelayMs);
                sb.append(" ms");
                Log.m209d(access$000, sb.toString());
                synchronized (eventsThread.mSyncObject) {
                    eventsThread.mSyncObject.notify();
                }
            }
        }
    }

    public EventsThread(Context context, EventsRestClient eventsRestClient, EventsThreadListener eventsThreadListener, String str) {
        super("Events thread");
        this.mContext = context;
        this.mEventsRestClient = eventsRestClient;
        this.mListener = eventsThreadListener;
        this.mCurrentToken = str;
        mSyncObjectByInstance.put(toString(), this);
        this.mAlarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
    }

    public void setMetricsListener(MetricsListener metricsListener) {
        this.mMetricsListener = metricsListener;
    }

    public String getCurrentSyncToken() {
        return this.mCurrentToken;
    }

    public void setFilterOrFilterId(String str) {
        this.mFilterOrFilterId = str;
    }

    public void setServerLongPollTimeout(int i) {
        this.mDefaultServerTimeoutms = Math.max(i, DEFAULT_SERVER_TIMEOUT_MS);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setServerLongPollTimeout : ");
        sb.append(this.mDefaultServerTimeoutms);
        Log.m209d(str, sb.toString());
    }

    public int getServerLongPollTimeout() {
        return this.mDefaultServerTimeoutms;
    }

    public void setSyncDelay(int i) {
        this.mRequestDelayMs = Math.max(0, i);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setSyncDelay() : ");
        sb.append(this.mRequestDelayMs);
        sb.append(" with state ");
        sb.append(getState());
        Log.m209d(str, sb.toString());
        if (State.WAITING != getState()) {
            return;
        }
        if (!this.mPaused || (this.mRequestDelayMs == 0 && this.mIsCatchingUp)) {
            if (!this.mPaused) {
                Log.m209d(LOG_TAG, "## setSyncDelay() : resume the application");
            }
            if (this.mRequestDelayMs == 0 && this.mIsCatchingUp) {
                Log.m209d(LOG_TAG, "## setSyncDelay() : cancel catchup");
                this.mIsCatchingUp = false;
            }
            synchronized (this.mSyncObject) {
                this.mSyncObject.notify();
            }
        }
    }

    public int getSyncDelay() {
        return this.mRequestDelayMs;
    }

    public void setNetworkConnectivityReceiver(NetworkConnectivityReceiver networkConnectivityReceiver) {
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
    }

    public void setFailureCallback(ApiFailureCallback apiFailureCallback) {
        this.mFailureCallback = apiFailureCallback;
    }

    public void pause() {
        Log.m209d(LOG_TAG, "pause()");
        this.mPaused = true;
        this.mIsCatchingUp = false;
    }

    /* access modifiers changed from: private */
    public void onNetworkAvailable() {
        Log.m209d(LOG_TAG, "onNetWorkAvailable()");
        if (this.mIsNetworkSuspended) {
            this.mIsNetworkSuspended = false;
            if (this.mPaused) {
                Log.m209d(LOG_TAG, "the event thread is still suspended");
                return;
            }
            Log.m209d(LOG_TAG, "Resume the thread");
            this.mIsCatchingUp = false;
            synchronized (this.mSyncObject) {
                this.mSyncObject.notify();
            }
            return;
        }
        Log.m209d(LOG_TAG, "onNetWorkAvailable() : nothing to do");
    }

    public void unpause() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## unpause() : thread state ");
        sb.append(getState());
        Log.m209d(str, sb.toString());
        if (State.WAITING == getState()) {
            Log.m209d(LOG_TAG, "## unpause() : the thread was paused so resume it.");
            this.mPaused = false;
            synchronized (this.mSyncObject) {
                this.mSyncObject.notify();
            }
        }
        this.mIsCatchingUp = false;
    }

    public void catchup() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## catchup() : thread state ");
        sb.append(getState());
        Log.m209d(str, sb.toString());
        if (State.WAITING == getState()) {
            Log.m209d(LOG_TAG, "## catchup() : the thread was paused so wake it up");
            this.mPaused = false;
            synchronized (this.mSyncObject) {
                this.mSyncObject.notify();
            }
        }
        this.mIsCatchingUp = true;
    }

    public void kill() {
        Log.m209d(LOG_TAG, "killing ...");
        this.mKilling = true;
        if (this.mPaused) {
            Log.m209d(LOG_TAG, "killing : the thread was pause so wake it up");
            this.mPaused = false;
            synchronized (this.mSyncObject) {
                this.mSyncObject.notify();
            }
            Log.m209d(LOG_TAG, "Resume the thread to kill it.");
        }
    }

    public void cancelKill() {
        if (this.mKilling) {
            Log.m209d(LOG_TAG, "## cancelKill() : Cancel the pending kill");
            this.mKilling = false;
            return;
        }
        Log.m209d(LOG_TAG, "## cancelKill() : Nothing to d");
    }

    public void setIsOnline(boolean z) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setIsOnline to ");
        sb.append(z);
        Log.m209d(str, sb.toString());
        this.mIsOnline = z;
    }

    public boolean isOnline() {
        return this.mIsOnline;
    }

    public void run() {
        try {
            Looper.prepare();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## run() : prepare failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        startSync();
    }

    /* access modifiers changed from: private */
    public static boolean hasDevicesChanged(SyncResponse syncResponse) {
        return (syncResponse.deviceLists == null || syncResponse.deviceLists.changed == null || syncResponse.deviceLists.changed.size() <= 0) ? false : true;
    }

    private void resumeInitialSync() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Resuming initial sync from ");
        sb.append(this.mCurrentToken);
        Log.m209d(str, sb.toString());
        SyncResponse syncResponse = new SyncResponse();
        syncResponse.nextBatch = this.mCurrentToken;
        this.mListener.onSyncResponse(syncResponse, null, true);
    }

    private void executeInitialSync() {
        Log.m209d(LOG_TAG, "Requesting initial sync...");
        long currentTimeMillis = System.currentTimeMillis();
        String json = new Gson().toJson((Object) FilterBody.getDataSaveModeFilterBody());
        while (!isInitialSyncDone()) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            this.mEventsRestClient.syncFromToken(null, 0, 120000, this.mIsOnline ? null : User.PRESENCE_OFFLINE, json, new SimpleApiCallback<SyncResponse>(this.mFailureCallback) {
                public void onSuccess(SyncResponse syncResponse) {
                    Log.m209d(EventsThread.LOG_TAG, "Received initial sync response.");
                    boolean z = false;
                    EventsThread.this.mNextServerTimeoutms = EventsThread.hasDevicesChanged(syncResponse) ? 0 : EventsThread.this.mDefaultServerTimeoutms;
                    EventsThreadListener access$1100 = EventsThread.this.mListener;
                    if (EventsThread.this.mNextServerTimeoutms == 0) {
                        z = true;
                    }
                    access$1100.onSyncResponse(syncResponse, null, z);
                    EventsThread.this.mCurrentToken = syncResponse.nextBatch;
                    countDownLatch.countDown();
                }

                private void sleepAndUnblock() {
                    Log.m213i(EventsThread.LOG_TAG, "Waiting a bit before retrying");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            countDownLatch.countDown();
                        }
                    }, 10000);
                }

                public void onNetworkError(Exception exc) {
                    if (EventsThread.this.isInitialSyncDone()) {
                        onSuccess((SyncResponse) null);
                        return;
                    }
                    String access$000 = EventsThread.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Sync V2 onNetworkError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    super.onNetworkError(exc);
                    sleepAndUnblock();
                }

                public void onMatrixError(MatrixError matrixError) {
                    super.onMatrixError(matrixError);
                    if (MatrixError.isConfigurationErrorCode(matrixError.errcode)) {
                        EventsThread.this.mListener.onConfigurationError(matrixError.errcode);
                        return;
                    }
                    EventsThread.this.mListener.onSyncError(matrixError);
                    sleepAndUnblock();
                }

                public void onUnexpectedError(Exception exc) {
                    super.onUnexpectedError(exc);
                    String access$000 = EventsThread.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Sync V2 onUnexpectedError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    sleepAndUnblock();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException unused) {
                Log.m211e(LOG_TAG, "Interrupted whilst performing initial sync.");
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## startSync() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
        if (this.mMetricsListener != null) {
            this.mMetricsListener.onInitialSyncFinished(currentTimeMillis2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0197  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0229 A[SYNTHETIC] */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startSync() {
        /*
            r20 = this;
            r7 = r20
            r8 = 0
            r7.mPaused = r8
            java.lang.String r1 = r7.mCurrentToken
            r9 = 1
            if (r1 == 0) goto L_0x000c
            r1 = 1
            goto L_0x000d
        L_0x000c:
            r1 = 0
        L_0x000d:
            r7.mInitialSyncDone = r1
            boolean r1 = r7.mInitialSyncDone
            r10 = 0
            if (r1 == 0) goto L_0x0027
            r20.resumeInitialSync()
            com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse r1 = new com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse
            r1.<init>()
            java.lang.String r2 = r7.mCurrentToken
            r1.nextBatch = r2
            com.opengarden.firechat.matrixsdk.sync.EventsThreadListener r2 = r7.mListener
            r2.onSyncResponse(r1, r10, r9)
            r1 = 0
            goto L_0x0085
        L_0x0027:
            com.google.gson.Gson r1 = new com.google.gson.Gson
            r1.<init>()
            com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody r2 = com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody.getDataSaveModeFilterBody()
            java.lang.String r1 = r1.toJson(r2)
        L_0x0034:
            boolean r2 = r7.mInitialSyncDone
            if (r2 != 0) goto L_0x0083
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r2.<init>(r9)
            com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient r11 = r7.mEventsRestClient
            r12 = 0
            r13 = 0
            r14 = 120000(0x1d4c0, float:1.68156E-40)
            boolean r3 = r7.mIsOnline
            if (r3 == 0) goto L_0x004a
            r15 = r10
            goto L_0x004d
        L_0x004a:
            java.lang.String r3 = "offline"
            r15 = r3
        L_0x004d:
            com.opengarden.firechat.matrixsdk.sync.EventsThread$3 r3 = new com.opengarden.firechat.matrixsdk.sync.EventsThread$3
            com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback r4 = r7.mFailureCallback
            r3.<init>(r4, r2)
            r16 = r1
            r17 = r3
            r11.syncFromToken(r12, r13, r14, r15, r16, r17)
            r2.await()     // Catch:{ InterruptedException -> 0x007b, Exception -> 0x005f }
            goto L_0x0034
        L_0x005f:
            r0 = move-exception
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "## startSync() failed "
            r3.append(r4)
            java.lang.String r4 = r0.getMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)
            goto L_0x0034
        L_0x007b:
            java.lang.String r2 = LOG_TAG
            java.lang.String r3 = "Interrupted whilst performing initial sync."
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)
            goto L_0x0034
        L_0x0083:
            int r1 = r7.mNextServerTimeoutms
        L_0x0085:
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Starting event stream from token "
            r3.append(r4)
            java.lang.String r4 = r7.mCurrentToken
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r2 = r7.mNetworkConnectivityReceiver
            if (r2 == 0) goto L_0x00b5
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r2 = r7.mNetworkConnectivityReceiver
            com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener r3 = r7.mNetworkListener
            r2.addEventListener(r3)
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r2 = r7.mNetworkConnectivityReceiver
            boolean r2 = r2.isConnected()
            r7.mbIsConnected = r2
            boolean r2 = r7.mbIsConnected
            r2 = r2 ^ r9
            r7.mIsNetworkSuspended = r2
        L_0x00b5:
            boolean r2 = r7.mKilling
            if (r2 != 0) goto L_0x022d
            boolean r2 = r7.mPaused
            if (r2 != 0) goto L_0x0115
            boolean r2 = r7.mIsNetworkSuspended
            if (r2 != 0) goto L_0x0115
            int r2 = r7.mRequestDelayMs
            if (r2 == 0) goto L_0x0115
            java.lang.String r2 = LOG_TAG
            java.lang.String r3 = "startSync : start a delay timer "
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
            android.content.Intent r2 = new android.content.Intent
            android.content.Context r3 = r7.mContext
            java.lang.Class<com.opengarden.firechat.matrixsdk.sync.EventsThread$SyncDelayReceiver> r4 = com.opengarden.firechat.matrixsdk.sync.EventsThread.SyncDelayReceiver.class
            r2.<init>(r3, r4)
            java.lang.String r3 = "EXTRA_INSTANCE_ID"
            java.lang.String r4 = r20.toString()
            r2.putExtra(r3, r4)
            android.content.Context r3 = r7.mContext
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r8, r2, r4)
            r7.mPendingDelayedIntent = r2
            long r2 = android.os.SystemClock.elapsedRealtime()
            int r4 = r7.mRequestDelayMs
            long r4 = (long) r4
            long r11 = r2 + r4
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 23
            r4 = 2
            if (r2 < r3) goto L_0x010e
            android.os.PowerManager r2 = r7.mPowerManager
            android.content.Context r3 = r7.mContext
            java.lang.String r3 = r3.getPackageName()
            boolean r2 = r2.isIgnoringBatteryOptimizations(r3)
            if (r2 == 0) goto L_0x010e
            android.app.AlarmManager r2 = r7.mAlarmManager
            android.app.PendingIntent r3 = r7.mPendingDelayedIntent
            r2.setAndAllowWhileIdle(r4, r11, r3)
            goto L_0x0115
        L_0x010e:
            android.app.AlarmManager r2 = r7.mAlarmManager
            android.app.PendingIntent r3 = r7.mPendingDelayedIntent
            r2.set(r4, r11, r3)
        L_0x0115:
            boolean r2 = r7.mPaused
            if (r2 != 0) goto L_0x0121
            boolean r2 = r7.mIsNetworkSuspended
            if (r2 != 0) goto L_0x0121
            android.app.PendingIntent r2 = r7.mPendingDelayedIntent
            if (r2 == 0) goto L_0x0192
        L_0x0121:
            android.app.PendingIntent r2 = r7.mPendingDelayedIntent
            if (r2 == 0) goto L_0x012d
            java.lang.String r2 = LOG_TAG
            java.lang.String r3 = "Event stream is paused because there is a timer delay."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
            goto L_0x0140
        L_0x012d:
            boolean r2 = r7.mIsNetworkSuspended
            if (r2 == 0) goto L_0x0139
            java.lang.String r2 = LOG_TAG
            java.lang.String r3 = "Event stream is paused because there is no available network."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
            goto L_0x0140
        L_0x0139:
            java.lang.String r2 = LOG_TAG
            java.lang.String r3 = "Event stream is paused. Waiting."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)
        L_0x0140:
            java.lang.String r2 = LOG_TAG     // Catch:{ InterruptedException -> 0x0176 }
            java.lang.String r3 = "startSync : wait ..."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ InterruptedException -> 0x0176 }
            java.lang.Object r2 = r7.mSyncObject     // Catch:{ InterruptedException -> 0x0176 }
            monitor-enter(r2)     // Catch:{ InterruptedException -> 0x0176 }
            java.lang.Object r3 = r7.mSyncObject     // Catch:{ all -> 0x0172 }
            r3.wait()     // Catch:{ all -> 0x0172 }
            monitor-exit(r2)     // Catch:{ all -> 0x0172 }
            android.app.PendingIntent r2 = r7.mPendingDelayedIntent     // Catch:{ InterruptedException -> 0x0176 }
            if (r2 == 0) goto L_0x0169
            java.lang.String r2 = LOG_TAG     // Catch:{ InterruptedException -> 0x0176 }
            java.lang.String r3 = "startSync : cancel mSyncDelayTimer"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ InterruptedException -> 0x0176 }
            android.app.AlarmManager r2 = r7.mAlarmManager     // Catch:{ InterruptedException -> 0x0176 }
            android.app.PendingIntent r3 = r7.mPendingDelayedIntent     // Catch:{ InterruptedException -> 0x0176 }
            r2.cancel(r3)     // Catch:{ InterruptedException -> 0x0176 }
            android.app.PendingIntent r2 = r7.mPendingDelayedIntent     // Catch:{ InterruptedException -> 0x0176 }
            r2.cancel()     // Catch:{ InterruptedException -> 0x0176 }
            r7.mPendingDelayedIntent = r10     // Catch:{ InterruptedException -> 0x0176 }
        L_0x0169:
            java.lang.String r2 = LOG_TAG     // Catch:{ InterruptedException -> 0x0176 }
            java.lang.String r3 = "Event stream woken from pause."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ InterruptedException -> 0x0176 }
            r14 = 0
            goto L_0x0193
        L_0x0172:
            r0 = move-exception
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x0172 }
            throw r3     // Catch:{ InterruptedException -> 0x0176 }
        L_0x0176:
            r0 = move-exception
            r2 = r0
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unexpected interruption while paused: "
            r4.append(r5)
            java.lang.String r2 = r2.getMessage()
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)
        L_0x0192:
            r14 = r1
        L_0x0193:
            boolean r1 = r7.mKilling
            if (r1 != 0) goto L_0x0229
            java.util.concurrent.CountDownLatch r15 = new java.util.concurrent.CountDownLatch
            r15.<init>(r9)
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Get events from token "
            r2.append(r3)
            java.lang.String r3 = r7.mCurrentToken
            r2.append(r3)
            java.lang.String r3 = " with filterOrFilterId "
            r2.append(r3)
            java.lang.String r3 = r7.mFilterOrFilterId
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
            int r1 = r7.mDefaultServerTimeoutms
            r7.mNextServerTimeoutms = r1
            com.opengarden.firechat.VectorApp r1 = com.opengarden.firechat.VectorApp.getInstance()
            android.content.Context r1 = r1.getApplicationContext()
            com.opengarden.firechat.Matrix r1 = com.opengarden.firechat.Matrix.getInstance(r1)
            com.opengarden.firechat.matrixsdk.MXSession r4 = r1.getDefaultSession()
            com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient r11 = r7.mEventsRestClient
            java.util.List r12 = r4.getSyncRecipients()
            java.lang.String r13 = r7.mCurrentToken
            r16 = 120000(0x1d4c0, float:1.68156E-40)
            boolean r1 = r7.mIsOnline
            if (r1 == 0) goto L_0x01e4
            r17 = r10
            goto L_0x01e8
        L_0x01e4:
            java.lang.String r1 = "offline"
            r17 = r1
        L_0x01e8:
            java.lang.String r6 = r7.mFilterOrFilterId
            com.opengarden.firechat.matrixsdk.sync.EventsThread$4 r18 = new com.opengarden.firechat.matrixsdk.sync.EventsThread$4
            com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback r3 = r7.mFailureCallback
            r1 = r18
            r2 = r7
            r5 = r14
            r19 = r6
            r6 = r15
            r1.<init>(r3, r4, r5, r6)
            r1 = r15
            r15 = r16
            r16 = r17
            r17 = r19
            r11.syncFromToken(r12, r13, r14, r15, r16, r17, r18)
            r1.await()     // Catch:{ InterruptedException -> 0x0222, Exception -> 0x0206 }
            goto L_0x0229
        L_0x0206:
            r0 = move-exception
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "latch.await() failed "
            r2.append(r3)
            java.lang.String r3 = r0.getMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
            goto L_0x0229
        L_0x0222:
            java.lang.String r1 = LOG_TAG
            java.lang.String r2 = "Interrupted whilst polling message"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
        L_0x0229:
            int r1 = r7.mNextServerTimeoutms
            goto L_0x00b5
        L_0x022d:
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r1 = r7.mNetworkConnectivityReceiver
            if (r1 == 0) goto L_0x0238
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r1 = r7.mNetworkConnectivityReceiver
            com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener r2 = r7.mNetworkListener
            r1.removeEventListener(r2)
        L_0x0238:
            java.lang.String r1 = LOG_TAG
            java.lang.String r2 = "Event stream terminating."
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.sync.EventsThread.startSync():void");
    }

    /* access modifiers changed from: private */
    public boolean isInitialSyncDone() {
        return this.mCurrentToken != null;
    }
}
