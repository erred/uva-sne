package com.opengarden.firechat.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.support.p000v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.widget.Toast;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.ViewedRoomTracker;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.data.store.MXStoreListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRulesUpdateListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.notifications.NotificationUtils;
import com.opengarden.firechat.notifications.NotifiedEvent;
import com.opengarden.firechat.notifications.RoomsNotifications;
import com.opengarden.firechat.receiver.DismissNotificationReceiver;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.SystemUtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class EventStreamService extends Service {
    public static final String EXTRA_AUTO_RESTART_ACTION = "EventStreamService.EXTRA_AUTO_RESTART_ACTION";
    public static final String EXTRA_MATRIX_IDS = "EventStreamService.EXTRA_MATRIX_IDS";
    public static final String EXTRA_STREAM_ACTION = "EventStreamService.EXTRA_STREAM_ACTION";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "EventStreamService";
    /* access modifiers changed from: private */
    public static EventStreamService mActiveEventStreamService;
    private static final Set<String> mBackgroundNotificationEventIds = new HashSet();
    private static final List<CharSequence> mBackgroundNotificationStrings = new ArrayList();
    private static final BingRule mDefaultBingRule;
    /* access modifiers changed from: private */
    public static ForegroundNotificationState mForegroundNotificationState = ForegroundNotificationState.NONE;
    private static String mLastBackgroundNotificationRoomId;
    private static int mLastBackgroundNotificationUnreadCount;
    private static HandlerThread mNotificationHandlerThread;
    private static Handler mNotificationsHandler;
    private final onBingRulesUpdateListener mBingRulesUpdatesListener = new onBingRulesUpdateListener() {
        public void onBingRulesUpdate() {
            EventStreamService.this.getNotificationsHandler().post(new Runnable() {
                public void run() {
                    Log.m209d(EventStreamService.LOG_TAG, "## on bing rules update");
                    EventStreamService.this.mNotifiedEventsByRoomId = null;
                    EventStreamService.this.refreshMessagesNotification();
                }
            });
        }
    };
    private String mCallIdInProgress = null;
    private final MXEventListener mEventsListener = new MXEventListener() {
        public void onBingEvent(Event event, RoomState roomState, BingRule bingRule) {
            String access$000 = EventStreamService.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("prepareNotification : ");
            sb.append(event.eventId);
            sb.append(" in ");
            sb.append(roomState.roomId);
            Log.m209d(access$000, sb.toString());
            EventStreamService.this.prepareNotification(event, bingRule);
        }

        public void onLiveEventsChunkProcessed(String str, String str2) {
            EventStreamService.this.getNotificationsHandler().post(new Runnable() {
                public void run() {
                    EventStreamService.this.refreshMessagesNotification();
                    EventStreamService.this.mPendingNotifications.clear();
                }
            });
            if (StreamAction.CATCHUP == EventStreamService.this.mServiceState || StreamAction.PAUSE == EventStreamService.this.mServiceState) {
                Iterator it = EventStreamService.this.mSessions.iterator();
                boolean z = false;
                while (it.hasNext()) {
                    z |= ((MXSession) it.next()).mCallsManager.hasActiveCalls();
                }
                if (z) {
                    Log.m209d(EventStreamService.LOG_TAG, "onLiveEventsChunkProcessed : Catchup again because there are active calls");
                    EventStreamService.this.catchup(false);
                } else if (StreamAction.CATCHUP == EventStreamService.this.mServiceState) {
                    Log.m209d(EventStreamService.LOG_TAG, "onLiveEventsChunkProcessed : no Active call");
                    CallsManager.getSharedInstance().checkDeadCalls();
                    EventStreamService.this.setServiceState(StreamAction.PAUSE);
                }
            }
            if (EventStreamService.mForegroundNotificationState == ForegroundNotificationState.INITIAL_SYNCING) {
                Log.m209d(EventStreamService.LOG_TAG, "onLiveEventsChunkProcessed : end of init sync");
                EventStreamService.this.refreshForegroundNotification();
            }
        }
    };
    /* access modifiers changed from: private */
    public GcmRegistrationManager mGcmRegistrationManager;
    private String mIncomingCallId = null;
    private boolean mIsSelfDestroyed = false;
    private ArrayList<String> mMatrixIds;
    /* access modifiers changed from: private */
    public Map<String, List<NotifiedEvent>> mNotifiedEventsByRoomId = null;
    /* access modifiers changed from: private */
    public final LinkedHashMap<String, NotifiedEvent> mPendingNotifications = new LinkedHashMap<>();
    /* access modifiers changed from: private */
    public StreamAction mServiceState = StreamAction.IDLE;
    /* access modifiers changed from: private */
    public ArrayList<MXSession> mSessions;
    /* access modifiers changed from: private */
    public boolean mSuspendWhenStarted = false;

    public enum ForegroundNotificationState {
        NONE,
        INITIAL_SYNCING,
        LISTENING_FOR_EVENTS,
        INCOMING_CALL,
        CALL_IN_PROGRESS
    }

    public enum StreamAction {
        IDLE,
        STOP,
        START,
        PAUSE,
        RESUME,
        CATCHUP,
        GCM_STATUS_UPDATE,
        AUTO_RESTART
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        BingRule bingRule = new BingRule("ruleKind", "aPattern", Boolean.valueOf(true), Boolean.valueOf(true), false);
        mDefaultBingRule = bingRule;
    }

    public static EventStreamService getInstance() {
        return mActiveEventStreamService;
    }

    public void startAccounts(List<String> list) {
        for (String str : list) {
            if (this.mMatrixIds.indexOf(str) < 0) {
                MXSession session = Matrix.getInstance(getApplicationContext()).getSession(str);
                this.mSessions.add(session);
                this.mMatrixIds.add(str);
                monitorSession(session);
                session.startEventStream(null);
            }
        }
    }

    public void stopAccounts(List<String> list) {
        for (String str : list) {
            if (this.mMatrixIds.indexOf(str) >= 0) {
                MXSession session = Matrix.getInstance(getApplicationContext()).getSession(str);
                if (session != null) {
                    session.stopEventStream();
                    session.getDataHandler().removeListener(this.mEventsListener);
                    session.getDataHandler().getBingRulesManager().removeBingRulesUpdateListener(this.mBingRulesUpdatesListener);
                    CallsManager.getSharedInstance().removeSession(session);
                    this.mSessions.remove(session);
                    this.mMatrixIds.remove(str);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x005d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(android.content.Intent r5, int r6, int r7) {
        /*
            r4 = this;
            r6 = 0
            r7 = 2
            r0 = 1
            if (r5 == 0) goto L_0x000d
            java.lang.String r1 = "EventStreamService.EXTRA_AUTO_RESTART_ACTION"
            boolean r1 = r5.hasExtra(r1)
            if (r1 == 0) goto L_0x0104
        L_0x000d:
            com.opengarden.firechat.services.EventStreamService$StreamAction r1 = com.opengarden.firechat.services.EventStreamService.StreamAction.AUTO_RESTART
            com.opengarden.firechat.services.EventStreamService$StreamAction r2 = r4.mServiceState
            if (r1 != r2) goto L_0x001b
            java.lang.String r5 = LOG_TAG
            java.lang.String r6 = "onStartCommand : auto restart in progress ignore current command"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)
            return r0
        L_0x001b:
            if (r5 != 0) goto L_0x0026
            java.lang.String r1 = LOG_TAG
            java.lang.String r2 = "onStartCommand : null intent -> restart the service"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
        L_0x0024:
            r1 = 1
            goto L_0x005b
        L_0x0026:
            com.opengarden.firechat.services.EventStreamService$StreamAction r1 = com.opengarden.firechat.services.EventStreamService.StreamAction.IDLE
            com.opengarden.firechat.services.EventStreamService$StreamAction r2 = r4.mServiceState
            if (r1 != r2) goto L_0x0034
            java.lang.String r1 = LOG_TAG
            java.lang.String r2 = "onStartCommand : automatically restart the service"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
            goto L_0x0024
        L_0x0034:
            com.opengarden.firechat.services.EventStreamService$StreamAction r1 = com.opengarden.firechat.services.EventStreamService.StreamAction.STOP
            com.opengarden.firechat.services.EventStreamService$StreamAction r2 = r4.mServiceState
            if (r1 != r2) goto L_0x0042
            java.lang.String r1 = LOG_TAG
            java.lang.String r2 = "onStartCommand : automatically restart the service even if the service is stopped"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
            goto L_0x0024
        L_0x0042:
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "onStartCommand : EXTRA_AUTO_RESTART_ACTION has been set but mServiceState = "
            r2.append(r3)
            com.opengarden.firechat.services.EventStreamService$StreamAction r3 = r4.mServiceState
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
            r1 = 0
        L_0x005b:
            if (r1 == 0) goto L_0x0104
            android.content.Context r5 = r4.getApplicationContext()
            com.opengarden.firechat.Matrix r5 = com.opengarden.firechat.Matrix.getInstance(r5)
            java.util.ArrayList r5 = r5.getSessions()
            if (r5 == 0) goto L_0x00fc
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x0073
            goto L_0x00fc
        L_0x0073:
            com.opengarden.firechat.VectorApp r5 = com.opengarden.firechat.VectorApp.getInstance()
            if (r5 == 0) goto L_0x008b
            com.opengarden.firechat.VectorApp r5 = com.opengarden.firechat.VectorApp.getInstance()
            boolean r5 = r5.didAppCrash()
            if (r5 == 0) goto L_0x008b
            java.lang.String r5 = LOG_TAG
            java.lang.String r6 = "onStartCommand : no auto restart because the application crashed"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)
            return r7
        L_0x008b:
            android.content.Context r5 = r4.getApplicationContext()
            com.opengarden.firechat.Matrix r5 = com.opengarden.firechat.Matrix.getInstance(r5)
            com.opengarden.firechat.gcm.GcmRegistrationManager r5 = r5.getSharedGCMRegistrationManager()
            boolean r5 = r5.canStartAppInBackground()
            if (r5 != 0) goto L_0x00a5
            java.lang.String r5 = LOG_TAG
            java.lang.String r6 = "onStartCommand : no auto restart because the user disabled the background sync"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)
            return r7
        L_0x00a5:
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r4.mSessions = r5
            java.util.ArrayList<com.opengarden.firechat.matrixsdk.MXSession> r5 = r4.mSessions
            android.content.Context r6 = r4.getApplicationContext()
            com.opengarden.firechat.Matrix r6 = com.opengarden.firechat.Matrix.getInstance(r6)
            java.util.ArrayList r6 = r6.getSessions()
            r5.addAll(r6)
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r4.mMatrixIds = r5
            java.util.ArrayList<com.opengarden.firechat.matrixsdk.MXSession> r5 = r4.mSessions
            java.util.Iterator r5 = r5.iterator()
        L_0x00ca:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x00eb
            java.lang.Object r6 = r5.next()
            com.opengarden.firechat.matrixsdk.MXSession r6 = (com.opengarden.firechat.matrixsdk.MXSession) r6
            com.opengarden.firechat.matrixsdk.MXDataHandler r7 = r6.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r7 = r7.getStore()
            r7.open()
            java.util.ArrayList<java.lang.String> r7 = r4.mMatrixIds
            java.lang.String r6 = r6.getMyUserId()
            r7.add(r6)
            goto L_0x00ca
        L_0x00eb:
            r4.mSuspendWhenStarted = r0
            r4.start()
            com.opengarden.firechat.services.EventStreamService$StreamAction r5 = com.opengarden.firechat.services.EventStreamService.StreamAction.START
            com.opengarden.firechat.services.EventStreamService$StreamAction r6 = r4.mServiceState
            if (r5 != r6) goto L_0x00fb
            com.opengarden.firechat.services.EventStreamService$StreamAction r5 = com.opengarden.firechat.services.EventStreamService.StreamAction.AUTO_RESTART
            r4.setServiceState(r5)
        L_0x00fb:
            return r0
        L_0x00fc:
            java.lang.String r5 = LOG_TAG
            java.lang.String r6 = "onStartCommand : no session"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)
            return r7
        L_0x0104:
            r4.mSuspendWhenStarted = r6
            com.opengarden.firechat.services.EventStreamService$StreamAction[] r6 = com.opengarden.firechat.services.EventStreamService.StreamAction.values()
            java.lang.String r1 = "EventStreamService.EXTRA_STREAM_ACTION"
            com.opengarden.firechat.services.EventStreamService$StreamAction r2 = com.opengarden.firechat.services.EventStreamService.StreamAction.IDLE
            int r2 = r2.ordinal()
            int r1 = r5.getIntExtra(r1, r2)
            r6 = r6[r1]
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "onStartCommand with action : "
            r2.append(r3)
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
            java.lang.String r1 = "EventStreamService.EXTRA_MATRIX_IDS"
            boolean r1 = r5.hasExtra(r1)
            if (r1 == 0) goto L_0x018e
            java.util.ArrayList<java.lang.String> r1 = r4.mMatrixIds
            if (r1 != 0) goto L_0x018e
            java.util.ArrayList r1 = new java.util.ArrayList
            java.lang.String r2 = "EventStreamService.EXTRA_MATRIX_IDS"
            java.lang.String[] r5 = r5.getStringArrayExtra(r2)
            java.util.List r5 = java.util.Arrays.asList(r5)
            r1.<init>(r5)
            r4.mMatrixIds = r1
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r4.mSessions = r5
            java.util.ArrayList<java.lang.String> r5 = r4.mMatrixIds
            java.util.Iterator r5 = r5.iterator()
        L_0x0158:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L_0x0176
            java.lang.Object r1 = r5.next()
            java.lang.String r1 = (java.lang.String) r1
            java.util.ArrayList<com.opengarden.firechat.matrixsdk.MXSession> r2 = r4.mSessions
            android.content.Context r3 = r4.getApplicationContext()
            com.opengarden.firechat.Matrix r3 = com.opengarden.firechat.Matrix.getInstance(r3)
            com.opengarden.firechat.matrixsdk.MXSession r1 = r3.getSession(r1)
            r2.add(r1)
            goto L_0x0158
        L_0x0176:
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onStartCommand : update the matrix ids list to "
            r1.append(r2)
            java.util.ArrayList<java.lang.String> r2 = r4.mMatrixIds
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r1)
        L_0x018e:
            int[] r5 = com.opengarden.firechat.services.EventStreamService.C197812.f122x8c75a90d
            int r1 = r6.ordinal()
            r5 = r5[r1]
            switch(r5) {
                case 1: goto L_0x01b3;
                case 2: goto L_0x01b3;
                case 3: goto L_0x01a6;
                case 4: goto L_0x01a2;
                case 5: goto L_0x019e;
                case 6: goto L_0x019a;
                default: goto L_0x0199;
            }
        L_0x0199:
            goto L_0x01db
        L_0x019a:
            r4.gcmStatusUpdate()
            goto L_0x01db
        L_0x019e:
            r4.catchup(r0)
            goto L_0x01db
        L_0x01a2:
            r4.pause()
            goto L_0x01db
        L_0x01a6:
            java.lang.String r5 = LOG_TAG
            java.lang.String r6 = "## onStartCommand(): service stopped"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r6)
            r4.mIsSelfDestroyed = r0
            r4.stopSelf()
            goto L_0x01db
        L_0x01b3:
            java.util.ArrayList<com.opengarden.firechat.matrixsdk.MXSession> r5 = r4.mSessions
            if (r5 == 0) goto L_0x01c4
            java.util.ArrayList<com.opengarden.firechat.matrixsdk.MXSession> r5 = r4.mSessions
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x01c0
            goto L_0x01c4
        L_0x01c0:
            r4.start()
            goto L_0x01db
        L_0x01c4:
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onStartCommand : empty sessions list with action "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r6 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)
            return r7
        L_0x01db:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.services.EventStreamService.onStartCommand(android.content.Intent, int, int):int");
    }

    private void autoRestart() {
        int nextInt = new Random().nextInt(5000) + PathInterpolatorCompat.MAX_NUM_POINTS;
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## autoRestart() : restarts after ");
        sb.append(nextInt);
        sb.append(" ms");
        Log.m209d(str, sb.toString());
        mForegroundNotificationState = ForegroundNotificationState.NONE;
        Intent intent = new Intent(getApplicationContext(), getClass());
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_AUTO_RESTART_ACTION, EXTRA_AUTO_RESTART_ACTION);
        ((AlarmManager) getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).set(3, SystemClock.elapsedRealtime() + ((long) nextInt), PendingIntent.getService(getApplicationContext(), 1, intent, ErrorDialogData.SUPPRESSED));
    }

    public void onTaskRemoved(Intent intent) {
        Log.m209d(LOG_TAG, "## onTaskRemoved");
        autoRestart();
        super.onTaskRemoved(intent);
    }

    public void onDestroy() {
        if (!this.mIsSelfDestroyed) {
            setServiceState(StreamAction.STOP);
            if (!SystemUtilsKt.isIgnoringBatteryOptimizations(getApplicationContext()) && VERSION.SDK_INT >= 26 && mForegroundNotificationState == ForegroundNotificationState.INITIAL_SYNCING && Matrix.getInstance(getApplicationContext()).getSharedGCMRegistrationManager().hasRegistrationToken()) {
                setForegroundNotificationState(ForegroundNotificationState.NONE, null);
            }
            Log.m209d(LOG_TAG, "## onDestroy() : restart it");
            autoRestart();
        } else {
            Log.m209d(LOG_TAG, "## onDestroy() : do nothing");
            stop();
            super.onDestroy();
        }
        this.mIsSelfDestroyed = false;
    }

    /* access modifiers changed from: private */
    public void startEventStream(MXSession mXSession, IMXStore iMXStore) {
        if (mXSession.getCurrentSyncToken() != null) {
            mXSession.resumeEventStream();
        } else {
            mXSession.startEventStream(iMXStore.getEventStreamToken());
        }
    }

    private StreamAction getServiceState() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getState ");
        sb.append(this.mServiceState);
        Log.m209d(str, sb.toString());
        return this.mServiceState;
    }

    /* access modifiers changed from: private */
    public void setServiceState(StreamAction streamAction) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setState from ");
        sb.append(this.mServiceState);
        sb.append(" to ");
        sb.append(streamAction);
        Log.m209d(str, sb.toString());
        this.mServiceState = streamAction;
    }

    public static boolean isStopped() {
        return getInstance() == null || getInstance().mServiceState == StreamAction.STOP;
    }

    private void monitorSession(final MXSession mXSession) {
        mXSession.getDataHandler().addListener(this.mEventsListener);
        mXSession.getDataHandler().getBingRulesManager().addBingRulesUpdateListener(this.mBingRulesUpdatesListener);
        CallsManager.getSharedInstance().addSession(mXSession);
        mXSession.getDataHandler().addListener(new MXEventListener() {
            public void onInitialSyncComplete(String str) {
                mXSession.getDataHandler().getStore().post(new Runnable() {
                    public void run() {
                        new Handler(EventStreamService.this.getMainLooper()).post(new Runnable() {
                            public void run() {
                                EventStreamService.this.refreshForegroundNotification();
                            }
                        });
                    }
                });
            }
        });
        final IMXStore store = mXSession.getDataHandler().getStore();
        if (store.isReady()) {
            startEventStream(mXSession, store);
            if (this.mSuspendWhenStarted) {
                if (this.mGcmRegistrationManager != null) {
                    mXSession.setSyncDelay(this.mGcmRegistrationManager.getBackgroundSyncDelay());
                    mXSession.setSyncTimeout(this.mGcmRegistrationManager.getBackgroundSyncTimeOut());
                }
                catchup(false);
                return;
            }
            return;
        }
        store.addMXStoreListener(new MXStoreListener() {
            public void onStoreReady(String str) {
                EventStreamService.this.startEventStream(mXSession, store);
                if (EventStreamService.this.mSuspendWhenStarted) {
                    if (EventStreamService.this.mGcmRegistrationManager != null) {
                        mXSession.setSyncDelay(EventStreamService.this.mGcmRegistrationManager.getBackgroundSyncDelay());
                        mXSession.setSyncTimeout(EventStreamService.this.mGcmRegistrationManager.getBackgroundSyncTimeOut());
                    }
                    EventStreamService.this.catchup(false);
                }
            }

            public void onStoreCorrupted(String str, String str2) {
                if (store.getEventStreamToken() == null) {
                    EventStreamService.this.startEventStream(mXSession, store);
                } else {
                    Matrix.getInstance(EventStreamService.this.getApplicationContext()).reloadSessions(EventStreamService.this.getApplicationContext());
                }
            }

            public void onStoreOOM(final String str, final String str2) {
                new Handler(EventStreamService.this.getMainLooper()).post(new Runnable() {
                    public void run() {
                        Context applicationContext = EventStreamService.this.getApplicationContext();
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(" : ");
                        sb.append(str2);
                        Toast.makeText(applicationContext, sb.toString(), 1).show();
                        Matrix.getInstance(EventStreamService.this.getApplicationContext()).reloadSessions(EventStreamService.this.getApplicationContext());
                    }
                });
            }
        });
    }

    private void start() {
        this.mGcmRegistrationManager = Matrix.getInstance(getApplicationContext()).getSharedGCMRegistrationManager();
        StreamAction serviceState = getServiceState();
        if (serviceState == StreamAction.START) {
            Log.m211e(LOG_TAG, "start : Already started.");
            Iterator it = this.mSessions.iterator();
            while (it.hasNext()) {
                ((MXSession) it.next()).refreshNetworkConnection();
            }
        } else if (serviceState == StreamAction.PAUSE || serviceState == StreamAction.CATCHUP) {
            Log.m211e(LOG_TAG, "start : Resuming active stream.");
            resume();
        } else if (this.mSessions == null) {
            Log.m211e(LOG_TAG, "start : No valid MXSession.");
        } else {
            Log.m209d(LOG_TAG, "## start : start the service");
            if (!(mActiveEventStreamService == null || this == mActiveEventStreamService)) {
                mActiveEventStreamService.stop();
            }
            mActiveEventStreamService = this;
            Iterator it2 = this.mSessions.iterator();
            while (it2.hasNext()) {
                MXSession mXSession = (MXSession) it2.next();
                if (mXSession == null || mXSession.getDataHandler() == null || mXSession.getDataHandler().getStore() == null) {
                    Log.m211e(LOG_TAG, "start : the session is not anymore valid.");
                    return;
                }
                monitorSession(mXSession);
            }
            refreshForegroundNotification();
            setServiceState(StreamAction.START);
        }
    }

    public void stopNow() {
        stop();
        this.mIsSelfDestroyed = true;
        stopSelf();
    }

    private void stop() {
        Log.m209d(LOG_TAG, "## stop(): the service is stopped");
        setForegroundNotificationState(ForegroundNotificationState.NONE, null);
        if (this.mSessions != null) {
            Iterator it = this.mSessions.iterator();
            while (it.hasNext()) {
                MXSession mXSession = (MXSession) it.next();
                if (mXSession != null && mXSession.isAlive()) {
                    mXSession.stopEventStream();
                    mXSession.getDataHandler().removeListener(this.mEventsListener);
                    mXSession.getDataHandler().getBingRulesManager().removeBingRulesUpdateListener(this.mBingRulesUpdatesListener);
                    CallsManager.getSharedInstance().removeSession(mXSession);
                }
            }
        }
        this.mMatrixIds = null;
        this.mSessions = null;
        setServiceState(StreamAction.STOP);
        mActiveEventStreamService = null;
    }

    private void pause() {
        StreamAction serviceState = getServiceState();
        if (StreamAction.START == serviceState || StreamAction.RESUME == serviceState) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onStartCommand pause from state ");
            sb.append(serviceState);
            Log.m209d(str, sb.toString());
            if (this.mSessions != null) {
                Iterator it = this.mSessions.iterator();
                while (it.hasNext()) {
                    ((MXSession) it.next()).pauseEventStream();
                }
                setServiceState(StreamAction.PAUSE);
                return;
            }
            return;
        }
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("onStartCommand invalid state pause ");
        sb2.append(serviceState);
        Log.m211e(str2, sb2.toString());
    }

    /* access modifiers changed from: private */
    public void catchup(boolean z) {
        StreamAction serviceState = getServiceState();
        boolean z2 = true;
        if (!z) {
            Log.m209d(LOG_TAG, "catchup  without checking state ");
        } else {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("catchup with state ");
            sb.append(serviceState);
            sb.append(" CurrentActivity ");
            sb.append(VectorApp.getCurrentActivity());
            Log.m209d(str, sb.toString());
            if (!(serviceState == StreamAction.CATCHUP || serviceState == StreamAction.PAUSE || (StreamAction.START == serviceState && VectorApp.getCurrentActivity() == null))) {
                z2 = false;
            }
        }
        if (z2) {
            if (this.mSessions != null) {
                Iterator it = this.mSessions.iterator();
                while (it.hasNext()) {
                    ((MXSession) it.next()).catchupEventStream();
                }
            } else {
                Log.m211e(LOG_TAG, "catchup no session");
            }
            setServiceState(StreamAction.CATCHUP);
            return;
        }
        Log.m209d(LOG_TAG, "No catchup is triggered because there is already a running event thread");
    }

    private void resume() {
        Log.m209d(LOG_TAG, "## resume : resume the service");
        if (this.mSessions != null) {
            Iterator it = this.mSessions.iterator();
            while (it.hasNext()) {
                ((MXSession) it.next()).resumeEventStream();
            }
        }
        setServiceState(StreamAction.START);
    }

    private void gcmStatusUpdate() {
        Log.m209d(LOG_TAG, "## gcmStatusUpdate");
        if (ForegroundNotificationState.NONE != mForegroundNotificationState) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## gcmStatusUpdate : gcm status succeeds. So, stop foreground service (");
            sb.append(mForegroundNotificationState);
            sb.append(")");
            Log.m209d(str, sb.toString());
            if (ForegroundNotificationState.LISTENING_FOR_EVENTS == mForegroundNotificationState) {
                setForegroundNotificationState(ForegroundNotificationState.NONE, null);
            }
        }
        refreshForegroundNotification();
    }

    private boolean shouldDisplayListenForEventsNotification() {
        return (!this.mGcmRegistrationManager.useGCM() || (TextUtils.isEmpty(this.mGcmRegistrationManager.getCurrentRegistrationToken()) && !this.mGcmRegistrationManager.isServerRegistred())) && this.mGcmRegistrationManager.isBackgroundSyncAllowed() && this.mGcmRegistrationManager.areDeviceNotificationsAllowed();
    }

    public void refreshForegroundNotification() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## refreshForegroundNotification from state ");
        sb.append(mForegroundNotificationState);
        Log.m209d(str, sb.toString());
        MXSession defaultSession = Matrix.getInstance(getApplicationContext()).getDefaultSession();
        if (defaultSession == null) {
            Log.m211e(LOG_TAG, "## updateServiceForegroundState(): no session");
        } else if (mForegroundNotificationState == ForegroundNotificationState.INCOMING_CALL || mForegroundNotificationState == ForegroundNotificationState.CALL_IN_PROGRESS) {
            Log.m209d(LOG_TAG, "## refreshForegroundNotification : does nothing as there is a pending call");
        } else if (this.mGcmRegistrationManager != null) {
            if (!defaultSession.getDataHandler().isInitialSyncComplete() || isStopped() || this.mServiceState == StreamAction.CATCHUP) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## refreshForegroundNotification : put the service in foreground because of an initial sync ");
                sb2.append(mForegroundNotificationState);
                Log.m209d(str2, sb2.toString());
                setForegroundNotificationState(ForegroundNotificationState.INITIAL_SYNCING, null);
            } else if (shouldDisplayListenForEventsNotification()) {
                Log.m209d(LOG_TAG, "## refreshForegroundNotification : put the service in foreground because of GCM registration");
                setForegroundNotificationState(ForegroundNotificationState.LISTENING_FOR_EVENTS, null);
            } else {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## refreshForegroundNotification : put the service in background from state ");
                sb3.append(mForegroundNotificationState);
                Log.m209d(str3, sb3.toString());
                setForegroundNotificationState(ForegroundNotificationState.NONE, null);
            }
        }
    }

    private void setForegroundNotificationState(ForegroundNotificationState foregroundNotificationState, Notification notification) {
        if (foregroundNotificationState != mForegroundNotificationState) {
            mForegroundNotificationState = foregroundNotificationState;
            switch (mForegroundNotificationState) {
                case NONE:
                    NotificationUtils.INSTANCE.cancelNotificationForegroundService(this);
                    stopForeground(true);
                    break;
                case INITIAL_SYNCING:
                    notification = NotificationUtils.INSTANCE.buildForegroundServiceNotification(this, C1299R.string.notification_sync_in_progress);
                    break;
                case LISTENING_FOR_EVENTS:
                    notification = NotificationUtils.INSTANCE.buildForegroundServiceNotification(this, C1299R.string.notification_listen_for_events);
                    break;
                case INCOMING_CALL:
                case CALL_IN_PROGRESS:
                    if (notification == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("A notification object must be passed for state ");
                        sb.append(foregroundNotificationState);
                        throw new IllegalArgumentException(sb.toString());
                    }
                    break;
            }
            if (notification != null) {
                startForeground(61, notification);
            }
        }
    }

    private void prepareCallNotification(Event event, BingRule bingRule) {
        String str;
        if (!event.getType().equals(Event.EVENT_TYPE_CALL_INVITE)) {
            Log.m209d(LOG_TAG, "prepareCallNotification : don't bing - Call invite");
            return;
        }
        MXSession mXSession = Matrix.getMXSession(getApplicationContext(), event.getMatrixId());
        if (mXSession == null || !mXSession.isAlive()) {
            Log.m209d(LOG_TAG, "prepareCallNotification : don't bing - no session");
            return;
        }
        Room room = mXSession.getDataHandler().getRoom(event.roomId);
        if (room == null) {
            Log.m209d(LOG_TAG, "prepareCallNotification : don't bing - the room does not exist");
            return;
        }
        try {
            str = event.getContentAsJsonObject().get("call_id").getAsString();
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("prepareNotification : getContentAsJsonObject ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            str = null;
        }
        if (!TextUtils.isEmpty(str)) {
            displayIncomingCallNotification(mXSession, room, event, str, bingRule);
        }
    }

    /* access modifiers changed from: private */
    public void prepareNotification(Event event, BingRule bingRule) {
        if (this.mPendingNotifications.containsKey(event.eventId)) {
            Log.m209d(LOG_TAG, "prepareNotification : don't bing - the event was already binged");
        } else if (!this.mGcmRegistrationManager.areDeviceNotificationsAllowed()) {
            Log.m209d(LOG_TAG, "prepareNotification : the push has been disable on this device");
        } else if (event.isCallEvent()) {
            prepareCallNotification(event, bingRule);
        } else {
            String str = event.roomId;
            if (!VectorApp.isAppInBackground() && str != null && event.roomId.equals(ViewedRoomTracker.getInstance().getViewedRoomId())) {
                Log.m209d(LOG_TAG, "prepareNotification : don't bing because it is the currently opened room");
            } else if (event.getContent().getAsJsonObject().has("body") || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(event.getType()) || event.isCallEvent()) {
                MXSession mXSession = Matrix.getMXSession(getApplicationContext(), event.getMatrixId());
                if (mXSession == null || !mXSession.isAlive()) {
                    Log.m209d(LOG_TAG, "prepareNotification : don't bing - no session");
                } else if (mXSession.getDataHandler().getRoom(str) == null) {
                    Log.m209d(LOG_TAG, "prepareNotification : don't bing - the room does not exist");
                } else {
                    if (bingRule == null) {
                        bingRule = mDefaultBingRule;
                    }
                    BingRule bingRule2 = bingRule;
                    LinkedHashMap<String, NotifiedEvent> linkedHashMap = this.mPendingNotifications;
                    String str2 = event.eventId;
                    NotifiedEvent notifiedEvent = new NotifiedEvent(event.roomId, event.eventId, bingRule2, event.getOriginServerTs());
                    linkedHashMap.put(str2, notifiedEvent);
                }
            } else {
                Log.m209d(LOG_TAG, "onBingEvent : don't bing - no body and not a call event");
            }
        }
    }

    public static void onMessagesNotificationDismiss(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onMessagesNotificationDismiss ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (mActiveEventStreamService != null) {
            mActiveEventStreamService.refreshMessagesNotification();
        }
    }

    public static void cancelNotificationsForRoomId(String str, String str2) {
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("cancelNotificationsForRoomId ");
        sb.append(str);
        sb.append(" - ");
        sb.append(str2);
        Log.m209d(str3, sb.toString());
        if (mActiveEventStreamService != null) {
            mActiveEventStreamService.cancelNotifications(str2);
        }
    }

    /* access modifiers changed from: private */
    public Handler getNotificationsHandler() {
        if (mNotificationHandlerThread == null) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("NotificationsService_");
                sb.append(System.currentTimeMillis());
                mNotificationHandlerThread = new HandlerThread(sb.toString(), 1);
                mNotificationHandlerThread.start();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## getNotificationsHandler failed : ");
                sb2.append(e.getMessage());
                Log.m211e(str, sb2.toString());
            }
        }
        if (mNotificationsHandler == null) {
            try {
                mNotificationsHandler = new Handler(mNotificationHandlerThread.getLooper());
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## getNotificationsHandler failed : ");
                sb3.append(e2.getMessage());
                Log.m211e(str2, sb3.toString());
            }
        }
        if (mNotificationsHandler == null) {
            return new Handler(getMainLooper());
        }
        return mNotificationsHandler;
    }

    private void clearNotification() {
        NotificationUtils.INSTANCE.cancelAllNotifications(this);
        getNotificationsHandler().post(new Runnable() {
            public void run() {
                if (EventStreamService.this.mPendingNotifications != null) {
                    EventStreamService.this.mPendingNotifications.clear();
                }
                if (EventStreamService.this.mNotifiedEventsByRoomId != null) {
                    EventStreamService.this.mNotifiedEventsByRoomId.clear();
                }
                RoomsNotifications.deleteCachedRoomNotifications(VectorApp.getInstance());
            }
        });
    }

    public static void removeNotification() {
        if (mActiveEventStreamService != null) {
            mActiveEventStreamService.clearNotification();
        }
    }

    public static void checkDisplayedNotifications() {
        if (mActiveEventStreamService != null) {
            mActiveEventStreamService.getNotificationsHandler().post(new Runnable() {
                public void run() {
                    if (EventStreamService.mActiveEventStreamService != null) {
                        EventStreamService.mActiveEventStreamService.refreshMessagesNotification();
                    }
                }
            });
        }
    }

    private void cancelNotifications(final String str) {
        getNotificationsHandler().post(new Runnable() {
            public void run() {
                if (EventStreamService.this.mNotifiedEventsByRoomId == null) {
                    return;
                }
                if (str == null || EventStreamService.this.mNotifiedEventsByRoomId.containsKey(str)) {
                    EventStreamService.this.mNotifiedEventsByRoomId = null;
                    EventStreamService.this.refreshMessagesNotification();
                }
            }
        });
    }

    public static void onStaticNotifiedEvent(Context context, Event event, String str, String str2, int i) {
        String str3;
        String str4;
        NotificationUtils.INSTANCE.createNotificationChannels(context);
        if (event != null && !mBackgroundNotificationEventIds.contains(event.eventId)) {
            mBackgroundNotificationEventIds.add(event.eventId);
            if (TextUtils.isEmpty(str)) {
                str3 = "";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(": ");
                str3 = sb.toString();
            }
            if (event.content == null) {
                if (event.roomId == null || TextUtils.isEmpty(str3)) {
                    mBackgroundNotificationStrings.clear();
                    mLastBackgroundNotificationUnreadCount = mBackgroundNotificationEventIds.size();
                } else {
                    if (mLastBackgroundNotificationRoomId == null || !mLastBackgroundNotificationRoomId.equals(event.roomId)) {
                        mLastBackgroundNotificationUnreadCount = 0;
                        mLastBackgroundNotificationRoomId = event.roomId;
                    } else {
                        mBackgroundNotificationStrings.remove(0);
                    }
                    mLastBackgroundNotificationUnreadCount++;
                }
                str4 = context.getResources().getQuantityString(C1299R.plurals.room_new_messages_notification, mLastBackgroundNotificationUnreadCount, new Object[]{Integer.valueOf(mLastBackgroundNotificationUnreadCount)});
            } else {
                if (TextUtils.isEmpty(str2)) {
                    str2 = event.sender;
                }
                if (!TextUtils.isEmpty(str2) && !str2.equalsIgnoreCase(str)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str3);
                    sb2.append(str2);
                    sb2.append(StringUtils.SPACE);
                    str3 = sb2.toString();
                }
                if (event.isEncrypted()) {
                    str4 = context.getString(C1299R.string.encrypted_message);
                } else {
                    RiotEventDisplay riotEventDisplay = new RiotEventDisplay(context, event, null);
                    riotEventDisplay.setPrependMessagesWithAuthor(false);
                    str4 = riotEventDisplay.getTextualDisplay().toString();
                }
            }
            if (!TextUtils.isEmpty(str4)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str3);
                sb3.append(str4);
                SpannableString spannableString = new SpannableString(sb3.toString());
                spannableString.setSpan(new StyleSpan(1), 0, str3.length(), 33);
                mBackgroundNotificationStrings.add(0, spannableString);
                EventStreamService instance = getInstance();
                List<CharSequence> list = mBackgroundNotificationStrings;
                BingRule bingRule = new BingRule(null, null, Boolean.valueOf(true), Boolean.valueOf(true), true);
                instance.displayMessagesNotification(list, bingRule);
            }
        } else if (i == 0) {
            mBackgroundNotificationStrings.clear();
            mLastBackgroundNotificationUnreadCount = 0;
            mLastBackgroundNotificationRoomId = null;
            getInstance().displayMessagesNotification(null, null);
        }
    }

    /* access modifiers changed from: private */
    public void displayMessagesNotification(final List<CharSequence> list, final BingRule bingRule) {
        NotificationUtils.INSTANCE.createNotificationChannels(this);
        new Handler(getMainLooper()).post(new Runnable() {
            public void run() {
                if (!EventStreamService.this.mGcmRegistrationManager.areDeviceNotificationsAllowed() || list == null || list.size() == 0) {
                    NotificationUtils.INSTANCE.cancelNotificationMessage(EventStreamService.this);
                    RoomsNotifications.deleteCachedRoomNotifications(VectorApp.getInstance());
                    return;
                }
                Notification buildMessagesListNotification = NotificationUtils.INSTANCE.buildMessagesListNotification(EventStreamService.this.getApplicationContext(), list, bingRule);
                if (buildMessagesListNotification != null) {
                    NotificationUtils.INSTANCE.showNotificationMessage(EventStreamService.this, buildMessagesListNotification);
                } else {
                    NotificationUtils.INSTANCE.cancelNotificationMessage(EventStreamService.this);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void refreshMessagesNotification() {
        mBackgroundNotificationStrings.clear();
        final boolean z = false;
        mLastBackgroundNotificationUnreadCount = 0;
        mLastBackgroundNotificationRoomId = null;
        mBackgroundNotificationEventIds.clear();
        final NotifiedEvent eventToNotify = getEventToNotify();
        if (!this.mGcmRegistrationManager.areDeviceNotificationsAllowed()) {
            this.mNotifiedEventsByRoomId = null;
            new Handler(getMainLooper()).post(new Runnable() {
                public void run() {
                    EventStreamService.this.displayMessagesNotification(null, null);
                }
            });
        } else if (refreshNotifiedMessagesList()) {
            if (this.mNotifiedEventsByRoomId == null || this.mNotifiedEventsByRoomId.size() == 0) {
                new Handler(getMainLooper()).post(new Runnable() {
                    public void run() {
                        EventStreamService.this.displayMessagesNotification(null, null);
                    }
                });
            } else {
                if (eventToNotify == null) {
                    z = true;
                }
                if (z) {
                    IMXStore store = Matrix.getInstance(getBaseContext()).getDefaultSession().getDataHandler().getStore();
                    if (store == null) {
                        Log.m211e(LOG_TAG, "## refreshMessagesNotification() : null store");
                        return;
                    }
                    long j = 0;
                    for (String str : new ArrayList(this.mNotifiedEventsByRoomId.keySet())) {
                        List list = (List) this.mNotifiedEventsByRoomId.get(str);
                        NotifiedEvent notifiedEvent = (NotifiedEvent) list.get(list.size() - 1);
                        Event event = store.getEvent(notifiedEvent.mEventId, notifiedEvent.mRoomId);
                        if (event == null) {
                            String str2 = LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshMessagesNotification() : the event ");
                            sb.append(notifiedEvent.mEventId);
                            sb.append(" in room ");
                            sb.append(notifiedEvent.mRoomId);
                            sb.append(" does not exist anymore");
                            Log.m211e(str2, sb.toString());
                            this.mNotifiedEventsByRoomId.remove(str);
                        } else if (event.getOriginServerTs() > j) {
                            j = event.getOriginServerTs();
                            eventToNotify = notifiedEvent;
                        }
                    }
                }
                final HashMap hashMap = new HashMap(this.mNotifiedEventsByRoomId);
                if (eventToNotify != null) {
                    DismissNotificationReceiver.setLatestNotifiedMessageTs(this, eventToNotify.mOriginServerTs);
                }
                new Handler(getMainLooper()).post(new Runnable() {
                    public void run() {
                        if (hashMap.size() > 0) {
                            Notification buildMessageNotification = NotificationUtils.INSTANCE.buildMessageNotification(EventStreamService.this.getApplicationContext(), (Map<String, ? extends List<? extends NotifiedEvent>>) new HashMap<String,Object>(hashMap), eventToNotify, z);
                            if (buildMessageNotification != null) {
                                NotificationUtils.INSTANCE.showNotificationMessage(EventStreamService.this, buildMessageNotification);
                            } else {
                                EventStreamService.this.displayMessagesNotification(null, null);
                            }
                        } else {
                            Log.m211e(EventStreamService.LOG_TAG, "## refreshMessagesNotification() : mNotifiedEventsByRoomId is empty");
                            EventStreamService.this.displayMessagesNotification(null, null);
                        }
                    }
                });
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0078 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x002f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.opengarden.firechat.notifications.NotifiedEvent getEventToNotify() {
        /*
            r8 = this;
            java.util.LinkedHashMap<java.lang.String, com.opengarden.firechat.notifications.NotifiedEvent> r0 = r8.mPendingNotifications
            int r0 = r0.size()
            r1 = 0
            if (r0 <= 0) goto L_0x0085
            android.content.Context r0 = r8.getBaseContext()
            com.opengarden.firechat.Matrix r0 = com.opengarden.firechat.Matrix.getInstance(r0)
            com.opengarden.firechat.matrixsdk.MXSession r0 = r0.getDefaultSession()
            com.opengarden.firechat.matrixsdk.MXDataHandler r0 = r0.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r0 = r0.getStore()
            java.util.ArrayList r2 = new java.util.ArrayList
            java.util.LinkedHashMap<java.lang.String, com.opengarden.firechat.notifications.NotifiedEvent> r3 = r8.mPendingNotifications
            java.util.Collection r3 = r3.values()
            r2.<init>(r3)
            java.util.Collections.reverse(r2)
            java.util.Iterator r2 = r2.iterator()
        L_0x002f:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0080
            java.lang.Object r3 = r2.next()
            com.opengarden.firechat.notifications.NotifiedEvent r3 = (com.opengarden.firechat.notifications.NotifiedEvent) r3
            java.lang.String r4 = r3.mRoomId
            com.opengarden.firechat.matrixsdk.data.Room r4 = r0.getRoom(r4)
            if (r4 == 0) goto L_0x002f
            java.lang.String r5 = r3.mEventId
            boolean r5 = r4.isEventRead(r5)
            if (r5 != 0) goto L_0x002f
            java.lang.String r5 = r3.mEventId
            java.lang.String r6 = r3.mRoomId
            com.opengarden.firechat.matrixsdk.rest.model.Event r5 = r0.getEvent(r5, r6)
            if (r5 == 0) goto L_0x0071
            com.opengarden.firechat.util.RiotEventDisplay r6 = new com.opengarden.firechat.util.RiotEventDisplay
            android.content.Context r7 = r8.getApplicationContext()
            com.opengarden.firechat.matrixsdk.data.RoomState r4 = r4.getLiveState()
            r6.<init>(r7, r5, r4)
            r4 = 0
            r6.setPrependMessagesWithAuthor(r4)
            java.lang.CharSequence r4 = r6.getTextualDisplay()
            if (r4 == 0) goto L_0x0071
            java.lang.String r4 = r4.toString()
            goto L_0x0072
        L_0x0071:
            r4 = r1
        L_0x0072:
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x002f
            java.util.LinkedHashMap<java.lang.String, com.opengarden.firechat.notifications.NotifiedEvent> r0 = r8.mPendingNotifications
            r0.clear()
            r8.mNotifiedEventsByRoomId = r1
            return r3
        L_0x0080:
            java.util.LinkedHashMap<java.lang.String, com.opengarden.firechat.notifications.NotifiedEvent> r0 = r8.mPendingNotifications
            r0.clear()
        L_0x0085:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.services.EventStreamService.getEventToNotify():com.opengarden.firechat.notifications.NotifiedEvent");
    }

    private boolean refreshNotifiedMessagesList() {
        boolean z;
        Exception exc;
        MXSession defaultSession = Matrix.getInstance(getBaseContext()).getDefaultSession();
        if (defaultSession == null || !defaultSession.getDataHandler().getBingRulesManager().isReady()) {
            return false;
        }
        IMXStore store = defaultSession.getDataHandler().getStore();
        if (store == null || !store.areReceiptsReady()) {
            return false;
        }
        long notificationDismissTs = DismissNotificationReceiver.getNotificationDismissTs(this);
        if (this.mNotifiedEventsByRoomId == null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##refreshNotifiedMessagesList() : min message TS ");
            sb.append(notificationDismissTs);
            Log.m209d(str, sb.toString());
            this.mNotifiedEventsByRoomId = new HashMap();
            for (Room room : store.getRooms()) {
                if (room.isInvited()) {
                    Collection<Event> roomMessages = store.getRoomMessages(room.getRoomId());
                    if (roomMessages != null) {
                        for (Event event : roomMessages) {
                            if (event.getOriginServerTs() >= notificationDismissTs && Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(event.getType())) {
                                try {
                                    if ("invite".equals(event.getContentAsJsonObject().getAsJsonPrimitive("membership").getAsString())) {
                                        BingRule fulfillRule = defaultSession.fulfillRule(event);
                                        if (fulfillRule != null && fulfillRule.isEnabled && fulfillRule.shouldNotify()) {
                                            ArrayList arrayList = new ArrayList();
                                            NotifiedEvent notifiedEvent = r12;
                                            NotifiedEvent notifiedEvent2 = new NotifiedEvent(event.roomId, event.eventId, fulfillRule, event.getOriginServerTs());
                                            arrayList.add(notifiedEvent);
                                            this.mNotifiedEventsByRoomId.put(room.getRoomId(), arrayList);
                                        }
                                    }
                                } catch (Exception unused) {
                                    Log.m211e(LOG_TAG, "##refreshNotifiedMessagesList() : invitation parsing failed");
                                }
                            }
                        }
                    }
                } else {
                    try {
                        List<Event> unreadEvents = store.unreadEvents(room.getRoomId(), null);
                        if (unreadEvents != null && unreadEvents.size() > 0) {
                            ArrayList arrayList2 = new ArrayList();
                            for (Event event2 : unreadEvents) {
                                if (event2.getOriginServerTs() > notificationDismissTs) {
                                    BingRule fulfillRule2 = defaultSession.fulfillRule(event2);
                                    if (fulfillRule2 != null && fulfillRule2.isEnabled && fulfillRule2.shouldNotify()) {
                                        String str2 = event2.roomId;
                                        NotifiedEvent notifiedEvent3 = new NotifiedEvent(str2, event2.eventId, fulfillRule2, event2.getOriginServerTs());
                                        arrayList2.add(notifiedEvent3);
                                    }
                                } else {
                                    String str3 = LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("##refreshNotifiedMessagesList() : ignore event ");
                                    sb2.append(event2.eventId);
                                    sb2.append(" in room ");
                                    sb2.append(event2.roomId);
                                    sb2.append(" because of the TS ");
                                    sb2.append(event2.originServerTs);
                                    Log.m209d(str3, sb2.toString());
                                }
                            }
                            if (arrayList2.size() > 0) {
                                this.mNotifiedEventsByRoomId.put(room.getRoomId(), arrayList2);
                            }
                        }
                    } catch (Exception e) {
                        Exception exc2 = e;
                        String str4 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("##refreshNotifiedMessagesList(): failed checking the unread ");
                        sb3.append(exc2.getMessage());
                        Log.m211e(str4, sb3.toString());
                    }
                }
            }
            return true;
        }
        try {
            z = false;
            for (String str5 : new ArrayList(this.mNotifiedEventsByRoomId.keySet())) {
                try {
                    Room room2 = store.getRoom(str5);
                    if (room2 == null) {
                        String str6 = LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## refreshNotifiedMessagesList() : the room ");
                        sb4.append(str5);
                        sb4.append(" does not exist anymore");
                        Log.m209d(str6, sb4.toString());
                        this.mNotifiedEventsByRoomId.remove(str5);
                        z = true;
                    } else {
                        List list = (List) this.mNotifiedEventsByRoomId.get(str5);
                        NotifiedEvent notifiedEvent4 = (NotifiedEvent) list.get(0);
                        if (!room2.isEventRead(notifiedEvent4.mEventId)) {
                            if (notifiedEvent4.mOriginServerTs < notificationDismissTs) {
                            }
                        }
                        NotifiedEvent notifiedEvent5 = (NotifiedEvent) list.get(list.size() - 1);
                        if (room2.isEventRead(notifiedEvent5.mEventId) || notifiedEvent5.mOriginServerTs <= notificationDismissTs) {
                            list.clear();
                        } else {
                            boolean z2 = z;
                            int i = 0;
                            while (i < list.size()) {
                                try {
                                    NotifiedEvent notifiedEvent6 = (NotifiedEvent) list.get(i);
                                    if (!room2.isEventRead(notifiedEvent6.mEventId)) {
                                        if (notifiedEvent6.mOriginServerTs > notificationDismissTs) {
                                            i++;
                                        }
                                    }
                                    list.remove(i);
                                    z2 = true;
                                } catch (Exception e2) {
                                    exc = e2;
                                    z = z2;
                                    String str7 = LOG_TAG;
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append("##refreshNotifiedMessagesList(): failed while building mNotifiedEventsByRoomId ");
                                    sb5.append(exc.getMessage());
                                    Log.m211e(str7, sb5.toString());
                                    return z;
                                }
                            }
                            z = z2;
                        }
                        if (list.size() == 0) {
                            this.mNotifiedEventsByRoomId.remove(str5);
                            z = true;
                        }
                    }
                } catch (Exception e3) {
                    exc = e3;
                    String str72 = LOG_TAG;
                    StringBuilder sb52 = new StringBuilder();
                    sb52.append("##refreshNotifiedMessagesList(): failed while building mNotifiedEventsByRoomId ");
                    sb52.append(exc.getMessage());
                    Log.m211e(str72, sb52.toString());
                    return z;
                }
            }
        } catch (Exception e4) {
            exc = e4;
            z = false;
            String str722 = LOG_TAG;
            StringBuilder sb522 = new StringBuilder();
            sb522.append("##refreshNotifiedMessagesList(): failed while building mNotifiedEventsByRoomId ");
            sb522.append(exc.getMessage());
            Log.m211e(str722, sb522.toString());
            return z;
        }
        return z;
    }

    public void displayIncomingCallNotification(MXSession mXSession, Room room, Event event, String str, BingRule bingRule) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("displayIncomingCallNotification : ");
        sb.append(str);
        sb.append(" in ");
        sb.append(room.getRoomId());
        Log.m209d(str2, sb.toString());
        if (!TextUtils.isEmpty(this.mIncomingCallId)) {
            Log.m209d(LOG_TAG, "displayIncomingCallNotification : the incoming call in progress is already displayed");
        } else if (!TextUtils.isEmpty(this.mCallIdInProgress)) {
            Log.m209d(LOG_TAG, "displayIncomingCallNotification : a 'call in progress' notification is displayed");
        } else if (CallsManager.getSharedInstance().getActiveCall() == null) {
            Log.m209d(LOG_TAG, "displayIncomingCallNotification : display the dedicated notification");
            setForegroundNotificationState(ForegroundNotificationState.INCOMING_CALL, NotificationUtils.INSTANCE.buildIncomingCallNotification(this, RoomsNotifications.getRoomName(getApplicationContext(), mXSession, room, event), mXSession.getMyUserId(), str));
            this.mIncomingCallId = str;
            if (Matrix.getInstance(VectorApp.getInstance()).getSharedGCMRegistrationManager().isScreenTurnedOn()) {
                WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(268435466, "MXEventListener");
                newWakeLock.acquire(3000);
                newWakeLock.release();
            }
        } else {
            Log.m209d(LOG_TAG, "displayIncomingCallNotification : do not display the incoming call notification because there is a pending call");
        }
    }

    public void displayCallInProgressNotification(MXSession mXSession, Room room, String str) {
        if (str != null) {
            setForegroundNotificationState(ForegroundNotificationState.CALL_IN_PROGRESS, NotificationUtils.INSTANCE.buildPendingCallNotification(getApplicationContext(), room.getName(mXSession.getCredentials().userId), room.getRoomId(), mXSession.getCredentials().userId, str));
            this.mCallIdInProgress = str;
        }
    }

    public void hideCallNotifications() {
        if (ForegroundNotificationState.CALL_IN_PROGRESS == mForegroundNotificationState || ForegroundNotificationState.INCOMING_CALL == mForegroundNotificationState) {
            if (ForegroundNotificationState.CALL_IN_PROGRESS == mForegroundNotificationState) {
                this.mCallIdInProgress = null;
            } else {
                this.mIncomingCallId = null;
            }
            setForegroundNotificationState(ForegroundNotificationState.NONE, null);
            refreshForegroundNotification();
        }
    }
}
