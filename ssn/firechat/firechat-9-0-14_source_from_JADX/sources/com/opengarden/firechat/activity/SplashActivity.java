package com.opengarden.firechat.activity;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.ErrorListener;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorUniversalLinkReceiver;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.services.EventStreamService.StreamAction;
import com.opengarden.firechat.util.PreferencesManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SplashActivity extends MXCActionBarActivity {
    public static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "SplashActivity";
    private static final String NEED_TO_CLEAR_CACHE_BEFORE_81200 = "NEED_TO_CLEAR_CACHE_BEFORE_81200";
    /* access modifiers changed from: private */
    public Map<MXSession, IMXEventListener> mDoneListeners = new HashMap();
    private final long mLaunchTime = System.currentTimeMillis();
    /* access modifiers changed from: private */
    public Map<MXSession, IMXEventListener> mListeners = new HashMap();

    public int getLayoutRes() {
        return C1299R.layout.vector_activity_splash;
    }

    private boolean hasCorruptedStore() {
        Iterator it = Matrix.getMXSessions(this).iterator();
        boolean z = false;
        while (it.hasNext()) {
            MXSession mXSession = (MXSession) it.next();
            if (mXSession.isAlive()) {
                z |= mXSession.getDataHandler().getStore().isCorrupted();
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void onFinish() {
        Log.m211e(LOG_TAG, "##onFinish() : start VectorHomeActivity");
        if (!hasCorruptedStore()) {
            Intent intent = new Intent(this, VectorHomeActivity.class);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            if (intent.hasExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) {
                intent.putExtra(VectorHomeActivity.EXTRA_WAITING_VIEW_STATUS, true);
            }
            if (getIntent().hasExtra(VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS)) {
                intent.putExtra(VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS, getIntent().getParcelableExtra(VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS));
                getIntent().removeExtra(VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS);
            }
            if (getIntent().hasExtra("EXTRA_ROOM_ID") && getIntent().hasExtra("EXTRA_MATRIX_ID")) {
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", getIntent().getStringExtra("EXTRA_MATRIX_ID"));
                hashMap.put("EXTRA_ROOM_ID", getIntent().getStringExtra("EXTRA_ROOM_ID"));
                intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, hashMap);
            }
            startActivity(intent);
            finish();
            return;
        }
        CommonActivityUtils.logout(this);
    }

    public void initUiAndData() {
        ArrayList<MXSession> sessions = Matrix.getInstance(getApplicationContext()).getSessions();
        if (sessions == null) {
            Log.m211e(LOG_TAG, "onCreate no Sessions");
            finish();
            return;
        }
        boolean z = true;
        if (VERSION.SDK_INT < 25 || PreferenceManager.getDefaultSharedPreferences(this).getInt(PreferencesManager.VERSION_BUILD, 0) >= 81200 || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(NEED_TO_CLEAR_CACHE_BEFORE_81200, true)) {
            ArrayList arrayList = new ArrayList();
            for (final MXSession mXSession : sessions) {
                C14461 r6 = new MXEventListener(mXSession) {
                    final /* synthetic */ MXSession val$fSession;

                    {
                        this.val$fSession = r2;
                    }

                    private void onReady() {
                        boolean containsKey;
                        synchronized (SplashActivity.LOG_TAG) {
                            containsKey = SplashActivity.this.mDoneListeners.containsKey(this.val$fSession);
                        }
                        if (!containsKey) {
                            synchronized (SplashActivity.LOG_TAG) {
                                String access$000 = SplashActivity.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("Session ");
                                sb.append(this.val$fSession.getCredentials().userId);
                                sb.append(" is initialized");
                                Log.m211e(access$000, sb.toString());
                                SplashActivity.this.mDoneListeners.put(this.val$fSession, SplashActivity.this.mListeners.get(this.val$fSession));
                                SplashActivity.this.mListeners.remove(this.val$fSession);
                                if (SplashActivity.this.mListeners.size() == 0) {
                                    VectorApp.addSyncingSession(mXSession);
                                    SplashActivity.this.onFinish();
                                }
                            }
                        }
                    }

                    public void onLiveEventsChunkProcessed(String str, String str2) {
                        super.onLiveEventsChunkProcessed(str, str2);
                        onReady();
                    }

                    public void onInitialSyncComplete(String str) {
                        super.onInitialSyncComplete(str);
                        onReady();
                    }
                };
                if (!mXSession.getDataHandler().isInitialSyncComplete()) {
                    mXSession.getDataHandler().getStore().open();
                    this.mListeners.put(mXSession, r6);
                    mXSession.getDataHandler().addListener(r6);
                    mXSession.setFailureCallback(new ErrorListener(mXSession, this));
                    arrayList.add(mXSession.getCredentials().userId);
                }
            }
            if (Matrix.getInstance(this).mHasBeenDisconnected) {
                arrayList = new ArrayList();
                for (MXSession credentials : sessions) {
                    arrayList.add(credentials.getCredentials().userId);
                }
                Matrix.getInstance(this).mHasBeenDisconnected = false;
            }
            if (EventStreamService.getInstance() == null) {
                Intent intent = new Intent(this, EventStreamService.class);
                intent.putExtra(EventStreamService.EXTRA_MATRIX_IDS, (String[]) arrayList.toArray(new String[arrayList.size()]));
                intent.putExtra(EventStreamService.EXTRA_STREAM_ACTION, StreamAction.START.ordinal());
                startService(intent);
            } else {
                EventStreamService.getInstance().startAccounts(arrayList);
            }
            GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(getApplicationContext()).getSharedGCMRegistrationManager();
            if (!sharedGCMRegistrationManager.isGCMRegistred()) {
                sharedGCMRegistrationManager.checkRegistrations();
            } else {
                sharedGCMRegistrationManager.forceSessionsRegistration(null);
            }
            synchronized (LOG_TAG) {
                if (this.mListeners.size() != 0) {
                    z = false;
                }
            }
            if (z) {
                Log.m211e(LOG_TAG, "nothing to do");
                onFinish();
            }
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(NEED_TO_CLEAR_CACHE_BEFORE_81200, false).apply();
        Matrix.getInstance(this).reloadSessions(this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        for (MXSession mXSession : this.mDoneListeners.keySet()) {
            if (mXSession.isAlive()) {
                mXSession.getDataHandler().removeListener((IMXEventListener) this.mDoneListeners.get(mXSession));
                mXSession.setFailureCallback(null);
            }
        }
    }
}
