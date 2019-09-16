package com.opengarden.firechat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Pair;
import com.amplitude.api.AmplitudeClient;
import com.amplitude.api.DeviceInfo;
import com.amplitude.api.PinnedAmplitudeClient;
import com.google.gson.JsonObject;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.JitsiCallActivity;
import com.opengarden.firechat.activity.VectorCallViewActivity;
import com.opengarden.firechat.activity.VectorMediasPickerActivity;
import com.opengarden.firechat.activity.WidgetActivity;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.PIDsRetriever;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.offlineMessaging.BeaconUtil;
import com.opengarden.firechat.offlineMessaging.Bluetooth;
import com.opengarden.firechat.offlineMessaging.BluetoothLE;
import com.opengarden.firechat.offlineMessaging.OfflineMessage;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.PhoneNumberUtils;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorMarkdownParser;
import com.opengarden.firechat.util.VectorMarkdownParser.IVectorMarkdownParserListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.altbeacon.beacon.BeaconTransmitter;
import org.json.JSONException;
import org.json.JSONObject;

public class VectorApp extends MultiDexApplication {
    private static final String APPLICATION_FONT_SCALE_KEY = "APPLICATION_FONT_SCALE_KEY";
    private static final String APPLICATION_LOCALE_COUNTRY_KEY = "APPLICATION_LOCALE_COUNTRY_KEY";
    private static final String APPLICATION_LOCALE_LANGUAGE_KEY = "APPLICATION_LOCALE_LANGUAGE_KEY";
    private static final String APPLICATION_LOCALE_VARIANT_KEY = "APPLICATION_LOCALE_VARIANT_KEY";
    private static final String FONT_SCALE_HUGE = "FONT_SCALE_HUGE";
    private static final String FONT_SCALE_LARGE = "FONT_SCALE_LARGE";
    private static final String FONT_SCALE_LARGER = "FONT_SCALE_LARGER";
    private static final String FONT_SCALE_LARGEST = "FONT_SCALE_LARGEST";
    private static final String FONT_SCALE_NORMAL = "FONT_SCALE_NORMAL";
    private static final String FONT_SCALE_SMALL = "FONT_SCALE_SMALL";
    private static final String FONT_SCALE_TINY = "FONT_SCALE_TINY";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorApp";
    private static final long MAX_ACTIVITY_TRANSITION_TIME_MS = 4000;
    private static final String PREFS_CRASH_KEY = "PREFS_CRASH_KEY";
    private static String SDK_VERSION_STRING = "";
    private static String SHORT_VERSION = "";
    private static String VECTOR_VERSION_STRING = "";
    public static int VERSION_BUILD = -1;
    private static VectorApp instance;
    private static final Locale mApplicationDefaultLanguage = new Locale("en", "US");
    private static final Set<Locale> mApplicationLocales = new HashSet();
    /* access modifiers changed from: private */
    public static Activity mCurrentActivity;
    private static final Map<String, Integer> mFontTextScaleIdByPrefKey = new LinkedHashMap<String, Integer>() {
        {
            put(VectorApp.FONT_SCALE_TINY, Integer.valueOf(C1299R.string.tiny));
            put(VectorApp.FONT_SCALE_SMALL, Integer.valueOf(C1299R.string.small));
            put(VectorApp.FONT_SCALE_NORMAL, Integer.valueOf(C1299R.string.normal));
            put(VectorApp.FONT_SCALE_LARGE, Integer.valueOf(C1299R.string.large));
            put(VectorApp.FONT_SCALE_LARGER, Integer.valueOf(C1299R.string.larger));
            put(VectorApp.FONT_SCALE_LARGEST, Integer.valueOf(C1299R.string.largest));
            put(VectorApp.FONT_SCALE_HUGE, Integer.valueOf(C1299R.string.huge));
        }
    };
    public static File mLogsDirectoryFile;
    private static final Map<Float, String> mPrefKeyByFontScale = new LinkedHashMap<Float, String>() {
        {
            put(Float.valueOf(0.7f), VectorApp.FONT_SCALE_TINY);
            put(Float.valueOf(0.85f), VectorApp.FONT_SCALE_SMALL);
            put(Float.valueOf(1.0f), VectorApp.FONT_SCALE_NORMAL);
            put(Float.valueOf(1.15f), VectorApp.FONT_SCALE_LARGE);
            put(Float.valueOf(1.3f), VectorApp.FONT_SCALE_LARGER);
            put(Float.valueOf(1.45f), VectorApp.FONT_SCALE_LARGEST);
            put(Float.valueOf(1.6f), VectorApp.FONT_SCALE_HUGE);
        }
    };
    private static Bitmap mSavedPickerImagePreview;
    private static final HashSet<MXSession> mSyncingSessions = new HashSet<>();
    /* access modifiers changed from: private */
    public Timer mActivityTransitionTimer;
    /* access modifiers changed from: private */
    public TimerTask mActivityTransitionTimerTask;
    public BluetoothLE mBtle;
    /* access modifiers changed from: private */
    public CallsManager mCallsManager;
    /* access modifiers changed from: private */
    public final ArrayList<String> mCreatedActivities = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean mIsCallingInBackground = false;
    /* access modifiers changed from: private */
    public boolean mIsInBackground = true;
    private final BroadcastReceiver mLanguageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.equals(Locale.getDefault().toString(), VectorApp.getApplicationLocale().toString())) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onReceive() : the locale has been updated to ");
                sb.append(Locale.getDefault().toString());
                sb.append(", restore the expected value ");
                sb.append(VectorApp.getApplicationLocale().toString());
                Log.m209d(access$000, sb.toString());
                VectorApp.updateApplicationSettings(VectorApp.getApplicationLocale(), VectorApp.getFontScale(), ThemeUtils.INSTANCE.getApplicationTheme(context));
                if (VectorApp.getCurrentActivity() != null) {
                    VectorApp.this.restartActivity(VectorApp.getCurrentActivity());
                }
            }
        }
    };
    private long mLastMediasCheck = 0;
    private VectorMarkdownParser mMarkdownParser;
    private final EventEmitter<Activity> mOnActivityDestroyedListener = new EventEmitter<>();
    public boolean offLineMessagePreference;

    public static VectorApp getInstance() {
        return instance;
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public void onCreate() {
        boolean z;
        Log.m209d(LOG_TAG, "onCreate");
        super.onCreate();
        instance = this;
        this.mCallsManager = new CallsManager(this);
        JSONObject jSONObject = null;
        this.mActivityTransitionTimer = null;
        this.mActivityTransitionTimerTask = null;
        boolean z2 = false;
        try {
            VERSION_BUILD = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("fails to retrieve the package info ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        VECTOR_VERSION_STRING = Matrix.getInstance(this).getVersion(true, true);
        if (Matrix.getInstance(this).getDefaultSession() != null) {
            SDK_VERSION_STRING = Matrix.getInstance(this).getDefaultSession().getVersion(true);
        } else {
            SDK_VERSION_STRING = "";
        }
        try {
            SHORT_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception unused) {
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getCacheDir().getAbsolutePath());
        sb2.append("/logs");
        mLogsDirectoryFile = new File(sb2.toString());
        Log.setLogDirectory(mLogsDirectoryFile);
        Log.init("RiotLog");
        Log.m209d(LOG_TAG, "----------------------------------------------------------------");
        Log.m209d(LOG_TAG, "----------------------------------------------------------------");
        String str2 = LOG_TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append(" Application version: ");
        sb3.append(VECTOR_VERSION_STRING);
        Log.m209d(str2, sb3.toString());
        String str3 = LOG_TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append(" SDK version: ");
        sb4.append(SDK_VERSION_STRING);
        Log.m209d(str3, sb4.toString());
        String str4 = LOG_TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append(" Local time: ");
        sb5.append(new SimpleDateFormat("MM-dd HH:mm:ss.SSSZ", Locale.US).format(new Date()));
        Log.m209d(str4, sb5.toString());
        Log.m209d(LOG_TAG, "----------------------------------------------------------------");
        Log.m209d(LOG_TAG, "----------------------------------------------------------------\n\n\n\n");
        MXSession.initUserAgent(getApplicationContext());
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            final Map<String, String> mLocalesByActivity = new HashMap();

            public void onActivityCreated(Activity activity, Bundle bundle) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityCreated ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
                VectorApp.this.mCreatedActivities.add(activity.toString());
            }

            public void onActivityStarted(Activity activity) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityStarted ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
            }

            private String getActivityLocaleStatus(Activity activity) {
                StringBuilder sb = new StringBuilder();
                sb.append(VectorApp.getApplicationLocale().toString());
                sb.append("_");
                sb.append(VectorApp.getFontScale());
                sb.append("_");
                sb.append(ThemeUtils.INSTANCE.getApplicationTheme(activity));
                return sb.toString();
            }

            public void onActivityResumed(Activity activity) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityResumed ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
                VectorApp.this.setCurrentActivity(activity);
                String obj = activity.toString();
                if (this.mLocalesByActivity.containsKey(obj)) {
                    String str = (String) this.mLocalesByActivity.get(obj);
                    if (!TextUtils.equals(str, getActivityLocaleStatus(activity))) {
                        String access$0002 = VectorApp.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## onActivityResumed() : restart the activity ");
                        sb2.append(activity);
                        sb2.append(" because of the locale update from ");
                        sb2.append(str);
                        sb2.append(" to ");
                        sb2.append(getActivityLocaleStatus(activity));
                        Log.m209d(access$0002, sb2.toString());
                        VectorApp.this.restartActivity(activity);
                        return;
                    }
                }
                if (!TextUtils.equals(Locale.getDefault().toString(), VectorApp.getApplicationLocale().toString())) {
                    String access$0003 = VectorApp.LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## onActivityResumed() : the locale has been updated to ");
                    sb3.append(Locale.getDefault().toString());
                    sb3.append(", restore the expected value ");
                    sb3.append(VectorApp.getApplicationLocale().toString());
                    Log.m209d(access$0003, sb3.toString());
                    VectorApp.updateApplicationSettings(VectorApp.getApplicationLocale(), VectorApp.getFontScale(), ThemeUtils.INSTANCE.getApplicationTheme(activity));
                    VectorApp.this.restartActivity(activity);
                }
                VectorApp.this.listPermissionStatuses();
            }

            public void onActivityPaused(Activity activity) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityPaused ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
                this.mLocalesByActivity.put(activity.toString(), getActivityLocaleStatus(activity));
                VectorApp.this.setCurrentActivity(null);
            }

            public void onActivityStopped(Activity activity) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityStopped ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivitySaveInstanceState ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
            }

            public void onActivityDestroyed(Activity activity) {
                String access$000 = VectorApp.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityDestroyed ");
                sb.append(activity);
                Log.m209d(access$000, sb.toString());
                VectorApp.this.mCreatedActivities.remove(activity.toString());
                this.mLocalesByActivity.remove(activity.toString());
                if (VectorApp.this.mCreatedActivities.size() > 1) {
                    String access$0002 = VectorApp.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onActivityDestroyed : \n");
                    sb2.append(VectorApp.this.mCreatedActivities);
                    Log.m209d(access$0002, sb2.toString());
                }
            }
        });
        try {
            this.mMarkdownParser = new VectorMarkdownParser(this);
        } catch (Exception e2) {
            String str5 = LOG_TAG;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("cannot create the mMarkdownParser ");
            sb6.append(e2.getMessage());
            Log.m211e(str5, sb6.toString());
        }
        getInstance().registerReceiver(this.mLanguageReceiver, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        getInstance().registerReceiver(this.mLanguageReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        PreferencesManager.fixMigrationIssues(this);
        initApplicationLocale();
        this.offLineMessagePreference = PreferencesManager.getOfflinePreference(getApplicationContext());
        if (this.offLineMessagePreference) {
            Bluetooth.registerBroadcastReceiver();
            BeaconUtil.startBeaconReceiver();
            initBTLE();
        }
        if ((getApplicationInfo().flags & 2) != 0) {
            PinnedAmplitudeClient.getInstance().initialize(this, "43daeac0908f676baf2093c9be77755f");
            PinnedAmplitudeClient.getInstance().enableLogging(true);
            PinnedAmplitudeClient.getInstance().setLogLevel(6);
        } else {
            PinnedAmplitudeClient.getInstance().initialize(this, "5ca6b9abc77b2ea7b08d5fdd8b6d641e");
        }
        PinnedAmplitudeClient.getInstance().enableForegroundTracking(this);
        long lastHeartbeat = PreferencesManager.getLastHeartbeat(this);
        long currentTimeMillis = System.currentTimeMillis();
        long j = (currentTimeMillis - lastHeartbeat) / 1000;
        if (lastHeartbeat == -1 || j > 86400) {
            PreferencesManager.setLastHeartbeat(this, Long.valueOf(currentTimeMillis));
            JSONObject jSONObject2 = new JSONObject();
            try {
                z = BeaconTransmitter.checkTransmissionSupported(this) == 0;
                try {
                    if (VERSION.SDK_INT >= 21) {
                        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService("bluetooth");
                        if (!(bluetoothManager == null || bluetoothManager.getAdapter() == null)) {
                            z2 = getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
                        }
                    }
                } catch (Exception unused2) {
                }
            } catch (Exception unused3) {
                z = false;
            }
            try {
                jSONObject2.put("beaconTransmitter", z);
                jSONObject2.put("beaconReceiver", z2);
                jSONObject = jSONObject2;
            } catch (JSONException e3) {
                String str6 = AmplitudeClient.TAG;
                StringBuilder sb7 = new StringBuilder();
                sb7.append("json exception ");
                sb7.append(e3.toString());
                Log.m209d(str6, sb7.toString());
            }
            PinnedAmplitudeClient.getInstance().logEvent("heartbeat", jSONObject, true);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!TextUtils.equals(Locale.getDefault().toString(), getApplicationLocale().toString())) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onConfigurationChanged() : the locale has been updated to ");
            sb.append(Locale.getDefault().toString());
            sb.append(", restore the expected value ");
            sb.append(getApplicationLocale().toString());
            Log.m209d(str, sb.toString());
            updateApplicationSettings(getApplicationLocale(), getFontScale(), ThemeUtils.INSTANCE.getApplicationTheme(this));
        }
    }

    public static void markdownToHtml(final String str, final IVectorMarkdownParserListener iVectorMarkdownParserListener) {
        if (getInstance().mMarkdownParser != null) {
            getInstance().mMarkdownParser.markdownToHtml(str, iVectorMarkdownParserListener);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    iVectorMarkdownParserListener.onMarkdownParsed(str, null);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void suspendApp() {
        GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(this).getSharedGCMRegistrationManager();
        if (!sharedGCMRegistrationManager.isBackgroundSyncAllowed() || (sharedGCMRegistrationManager.useGCM() && sharedGCMRegistrationManager.hasRegistrationToken())) {
            Log.m209d(LOG_TAG, "suspendApp ; pause the event stream");
            CommonActivityUtils.pauseEventStream(this);
        } else {
            Log.m209d(LOG_TAG, "suspendApp ; the event stream is not paused because GCM is disabled.");
        }
        Iterator it = Matrix.getInstance(this).getSessions().iterator();
        while (it.hasNext()) {
            MXSession mXSession = (MXSession) it.next();
            if (mXSession.isAlive()) {
                int i = 0;
                mXSession.setIsOnline(false);
                if (sharedGCMRegistrationManager.isBackgroundSyncAllowed()) {
                    i = sharedGCMRegistrationManager.getBackgroundSyncDelay();
                }
                mXSession.setSyncDelay(i);
                mXSession.setSyncTimeout(sharedGCMRegistrationManager.getBackgroundSyncTimeOut());
                if (System.currentTimeMillis() - this.mLastMediasCheck < 86400000) {
                    this.mLastMediasCheck = System.currentTimeMillis();
                    mXSession.removeMediasBefore(this, PreferencesManager.getMinMediasLastAccessTime(getApplicationContext()));
                }
                if (mXSession.getDataHandler().areLeftRoomsSynced()) {
                    mXSession.getDataHandler().releaseLeftRooms();
                }
            }
        }
        clearSyncingSessions();
        PIDsRetriever.getInstance().onAppBackgrounded();
        MyPresenceManager.advertiseAllUnavailable();
    }

    private void startActivityTransitionTimer() {
        Log.m209d(LOG_TAG, "## startActivityTransitionTimer()");
        try {
            this.mActivityTransitionTimer = new Timer();
            this.mActivityTransitionTimerTask = new TimerTask() {
                public void run() {
                    try {
                        if (VectorApp.this.mActivityTransitionTimerTask != null) {
                            VectorApp.this.mActivityTransitionTimerTask.cancel();
                            VectorApp.this.mActivityTransitionTimerTask = null;
                        }
                        if (VectorApp.this.mActivityTransitionTimer != null) {
                            VectorApp.this.mActivityTransitionTimer.cancel();
                            VectorApp.this.mActivityTransitionTimer = null;
                        }
                    } catch (Exception e) {
                        String access$000 = VectorApp.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startActivityTransitionTimer() failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                    if (VectorApp.mCurrentActivity != null) {
                        Log.m211e(VectorApp.LOG_TAG, "## startActivityTransitionTimer() : the timer expires but there is an active activity.");
                        return;
                    }
                    boolean z = true;
                    VectorApp.this.mIsInBackground = true;
                    VectorApp vectorApp = VectorApp.this;
                    if (VectorApp.this.mCallsManager.getActiveCall() == null) {
                        z = false;
                    }
                    vectorApp.mIsCallingInBackground = z;
                    if (!VectorApp.this.mIsCallingInBackground) {
                        Log.m209d(VectorApp.LOG_TAG, "Suspend the application because there was no resumed activity within 4 seconds");
                        CommonActivityUtils.displayMemoryInformation(null, " app suspended");
                        VectorApp.this.suspendApp();
                        return;
                    }
                    Log.m209d(VectorApp.LOG_TAG, "App not suspended due to call in progress");
                }
            };
            this.mActivityTransitionTimer.schedule(this.mActivityTransitionTimerTask, MAX_ACTIVITY_TRANSITION_TIME_MS);
        } catch (Throwable th) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## startActivityTransitionTimer() : failed to start the timer ");
            sb.append(th.getMessage());
            Log.m211e(str, sb.toString());
            if (this.mActivityTransitionTimer != null) {
                this.mActivityTransitionTimer.cancel();
                this.mActivityTransitionTimer = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void listPermissionStatuses() {
        if (VERSION.SDK_INT >= 23) {
            List<String> asList = Arrays.asList(new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_CONTACTS"});
            Log.m209d(LOG_TAG, "## listPermissionStatuses() : list the permissions used by the app");
            for (String str : asList) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Status of [");
                sb.append(str);
                sb.append("] : ");
                sb.append(ContextCompat.checkSelfPermission(instance, str) == 0 ? "PERMISSION_GRANTED" : "PERMISSION_DENIED");
                Log.m209d(str2, sb.toString());
            }
        }
    }

    private void stopActivityTransitionTimer() {
        Log.m209d(LOG_TAG, "## stopActivityTransitionTimer()");
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
            this.mActivityTransitionTimerTask = null;
        }
        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
            this.mActivityTransitionTimer = null;
        }
        if (isAppInBackground() && !this.mIsCallingInBackground) {
            if (EventStreamService.isStopped()) {
                CommonActivityUtils.startEventStreamService(this);
            } else {
                CommonActivityUtils.resumeEventStream(this);
                GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(this).getSharedGCMRegistrationManager();
                if (sharedGCMRegistrationManager != null) {
                    sharedGCMRegistrationManager.checkRegistrations();
                }
            }
            ContactsManager.getInstance().clearSnapshot();
            ContactsManager.getInstance().refreshLocalContactsSnapshot();
            for (MXSession mXSession : Matrix.getInstance(this).getSessions()) {
                mXSession.getMyUser().refreshUserInfos(null);
                mXSession.setIsOnline(true);
                mXSession.setSyncDelay(0);
                mXSession.setSyncTimeout(0);
                addSyncingSession(mXSession);
            }
            this.mCallsManager.checkDeadCalls();
            Matrix.getInstance(this).getSharedGCMRegistrationManager().onAppResume();
        }
        MyPresenceManager.advertiseAllOnline();
        this.mIsCallingInBackground = false;
        this.mIsInBackground = false;
    }

    /* access modifiers changed from: private */
    public void setCurrentActivity(Activity activity) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setCurrentActivity() : from ");
        sb.append(mCurrentActivity);
        sb.append(" to ");
        sb.append(activity);
        Log.m209d(str, sb.toString());
        if (isAppInBackground() && activity != null) {
            Matrix instance2 = Matrix.getInstance(activity.getApplicationContext());
            if (instance2 != null) {
                instance2.refreshPushRules();
            }
            Log.m209d(LOG_TAG, "The application is resumed");
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" app resumed with ");
            sb2.append(activity);
            CommonActivityUtils.displayMemoryInformation(activity, sb2.toString());
        }
        if (getInstance() == null) {
            Log.m211e(LOG_TAG, "The application is resumed but there is no active instance");
        } else if (activity == null) {
            getInstance().startActivityTransitionTimer();
        } else {
            getInstance().stopActivityTransitionTimer();
        }
        mCurrentActivity = activity;
        if (mCurrentActivity != null) {
            KeyRequestHandler.getSharedInstance().processNextRequest();
        }
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public static boolean isAppInBackground() {
        return mCurrentActivity == null && getInstance() != null && getInstance().mIsInBackground;
    }

    /* access modifiers changed from: private */
    public void restartActivity(Activity activity) {
        if (!(activity instanceof VectorMediasPickerActivity) && !(activity instanceof VectorCallViewActivity) && !(activity instanceof JitsiCallActivity) && !(activity instanceof WidgetActivity)) {
            activity.startActivity(activity.getIntent());
            activity.finish();
        }
    }

    public EventEmitter<Activity> getOnActivityDestroyedListener() {
        return this.mOnActivityDestroyedListener;
    }

    public static Bitmap getSavedPickerImagePreview() {
        return mSavedPickerImagePreview;
    }

    public static void setSavedCameraImagePreview(Bitmap bitmap) {
        if (bitmap != mSavedPickerImagePreview) {
            mSavedPickerImagePreview = bitmap;
        }
    }

    public static void addSyncingSession(MXSession mXSession) {
        synchronized (mSyncingSessions) {
            mSyncingSessions.add(mXSession);
        }
    }

    public static void removeSyncingSession(MXSession mXSession) {
        if (mXSession != null) {
            synchronized (mSyncingSessions) {
                mSyncingSessions.remove(mXSession);
            }
        }
    }

    public static void clearSyncingSessions() {
        synchronized (mSyncingSessions) {
            mSyncingSessions.clear();
        }
    }

    public static boolean isSessionSyncing(MXSession mXSession) {
        boolean contains;
        if (mXSession == null) {
            return false;
        }
        synchronized (mSyncingSessions) {
            contains = mSyncingSessions.contains(mXSession);
        }
        return contains;
    }

    public boolean didAppCrash() {
        return PreferenceManager.getDefaultSharedPreferences(getInstance()).getBoolean(PREFS_CRASH_KEY, false);
    }

    public void clearAppCrashStatus() {
        PreferenceManager.getDefaultSharedPreferences(getInstance()).edit().remove(PREFS_CRASH_KEY).apply();
    }

    private static void initApplicationLocale() {
        VectorApp instance2 = getInstance();
        Locale applicationLocale = getApplicationLocale();
        float fontScaleValue = getFontScaleValue();
        String applicationTheme = ThemeUtils.INSTANCE.getApplicationTheme(instance2);
        Locale.setDefault(applicationLocale);
        Configuration configuration = new Configuration(instance2.getResources().getConfiguration());
        configuration.locale = applicationLocale;
        configuration.fontScale = fontScaleValue;
        instance2.getResources().updateConfiguration(configuration, instance2.getResources().getDisplayMetrics());
        ThemeUtils.INSTANCE.setApplicationTheme(instance2, applicationTheme);
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                VectorApp.getApplicationLocales(VectorApp.getInstance());
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static String getFontScale() {
        VectorApp instance2 = getInstance();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(instance2);
        if (defaultSharedPreferences.contains(APPLICATION_FONT_SCALE_KEY)) {
            return defaultSharedPreferences.getString(APPLICATION_FONT_SCALE_KEY, FONT_SCALE_NORMAL);
        }
        float f = instance2.getResources().getConfiguration().fontScale;
        String str = FONT_SCALE_NORMAL;
        if (mPrefKeyByFontScale.containsKey(Float.valueOf(f))) {
            str = (String) mPrefKeyByFontScale.get(Float.valueOf(f));
        }
        Editor edit = defaultSharedPreferences.edit();
        edit.putString(APPLICATION_FONT_SCALE_KEY, str);
        edit.commit();
        return str;
    }

    private static float getFontScaleValue() {
        String fontScale = getFontScale();
        if (mPrefKeyByFontScale.containsValue(fontScale)) {
            for (Entry entry : mPrefKeyByFontScale.entrySet()) {
                if (TextUtils.equals((CharSequence) entry.getValue(), fontScale)) {
                    return ((Float) entry.getKey()).floatValue();
                }
            }
        }
        return 1.0f;
    }

    public static String getFontScaleDescription() {
        VectorApp instance2 = getInstance();
        String fontScale = getFontScale();
        if (mFontTextScaleIdByPrefKey.containsKey(fontScale)) {
            return instance2.getString(((Integer) mFontTextScaleIdByPrefKey.get(fontScale)).intValue());
        }
        return instance2.getString(C1299R.string.normal);
    }

    public static void updateFontScale(String str) {
        VectorApp instance2 = getInstance();
        for (Entry entry : mFontTextScaleIdByPrefKey.entrySet()) {
            if (TextUtils.equals(instance2.getString(((Integer) entry.getValue()).intValue()), str)) {
                saveFontScale((String) entry.getKey());
            }
        }
        Configuration configuration = new Configuration(instance2.getResources().getConfiguration());
        configuration.fontScale = getFontScaleValue();
        instance2.getResources().updateConfiguration(configuration, instance2.getResources().getDisplayMetrics());
    }

    public static Locale getApplicationLocale() {
        VectorApp instance2 = getInstance();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(instance2);
        if (defaultSharedPreferences.contains(APPLICATION_LOCALE_LANGUAGE_KEY)) {
            return new Locale(defaultSharedPreferences.getString(APPLICATION_LOCALE_LANGUAGE_KEY, ""), defaultSharedPreferences.getString(APPLICATION_LOCALE_COUNTRY_KEY, ""), defaultSharedPreferences.getString(APPLICATION_LOCALE_VARIANT_KEY, ""));
        }
        Locale locale = Locale.getDefault();
        if (TextUtils.equals(getString(instance2, mApplicationDefaultLanguage, C1299R.string.resouces_country), getString(instance2, locale, C1299R.string.resouces_country))) {
            locale = mApplicationDefaultLanguage;
        }
        saveApplicationLocale(locale);
        return locale;
    }

    public static Locale getDeviceLocale() {
        VectorApp instance2 = getInstance();
        Locale applicationLocale = getApplicationLocale();
        try {
            return instance2.getPackageManager().getResourcesForApplication(DeviceInfo.OS_NAME).getConfiguration().locale;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getDeviceLocale() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return applicationLocale;
        }
    }

    private static void saveApplicationLocale(Locale locale) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(getInstance()).edit();
        String language = locale.getLanguage();
        if (!TextUtils.isEmpty(language)) {
            edit.putString(APPLICATION_LOCALE_LANGUAGE_KEY, language);
        } else {
            edit.remove(APPLICATION_LOCALE_LANGUAGE_KEY);
        }
        String country = locale.getCountry();
        if (!TextUtils.isEmpty(country)) {
            edit.putString(APPLICATION_LOCALE_COUNTRY_KEY, country);
        } else {
            edit.remove(APPLICATION_LOCALE_COUNTRY_KEY);
        }
        String variant = locale.getVariant();
        if (!TextUtils.isEmpty(variant)) {
            edit.putString(APPLICATION_LOCALE_VARIANT_KEY, variant);
        } else {
            edit.remove(APPLICATION_LOCALE_VARIANT_KEY);
        }
        edit.apply();
    }

    private static void saveFontScale(String str) {
        VectorApp instance2 = getInstance();
        if (!TextUtils.isEmpty(str)) {
            Editor edit = PreferenceManager.getDefaultSharedPreferences(instance2).edit();
            edit.putString(APPLICATION_FONT_SCALE_KEY, str);
            edit.commit();
        }
    }

    public static void updateApplicationLocale(Locale locale) {
        updateApplicationSettings(locale, getFontScale(), ThemeUtils.INSTANCE.getApplicationTheme(getInstance()));
    }

    public static void updateApplicationTheme(String str) {
        ThemeUtils.INSTANCE.setApplicationTheme(getInstance(), str);
        updateApplicationSettings(getApplicationLocale(), getFontScale(), ThemeUtils.INSTANCE.getApplicationTheme(getInstance()));
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public static void updateApplicationSettings(Locale locale, String str, String str2) {
        VectorApp instance2 = getInstance();
        saveApplicationLocale(locale);
        saveFontScale(str);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(instance2.getResources().getConfiguration());
        configuration.locale = locale;
        configuration.fontScale = getFontScaleValue();
        instance2.getResources().updateConfiguration(configuration, instance2.getResources().getDisplayMetrics());
        ThemeUtils.INSTANCE.setApplicationTheme(instance2, str2);
        PhoneNumberUtils.onLocaleUpdate();
    }

    @SuppressLint({"NewApi"})
    public static Context getLocalisedContext(Context context) {
        try {
            Resources resources = context.getResources();
            Locale applicationLocale = getApplicationLocale();
            Configuration configuration = resources.getConfiguration();
            configuration.fontScale = getFontScaleValue();
            if (VERSION.SDK_INT >= 24) {
                configuration.setLocale(applicationLocale);
                configuration.setLayoutDirection(applicationLocale);
                return context.createConfigurationContext(configuration);
            }
            configuration.locale = applicationLocale;
            if (VERSION.SDK_INT >= 17) {
                configuration.setLayoutDirection(applicationLocale);
            }
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getLocalisedContext() failed : ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return context;
        }
    }

    private static String getString(Context context, Locale locale, int i) {
        if (VERSION.SDK_INT >= 17) {
            Configuration configuration = new Configuration(context.getResources().getConfiguration());
            configuration.setLocale(locale);
            try {
                return context.createConfigurationContext(configuration).getText(i).toString();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getString() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
                return context.getString(i);
            }
        } else {
            Resources resources = context.getResources();
            Configuration configuration2 = resources.getConfiguration();
            Locale locale2 = configuration2.locale;
            configuration2.locale = locale;
            resources.updateConfiguration(configuration2, null);
            String string = resources.getString(i);
            configuration2.locale = locale2;
            resources.updateConfiguration(configuration2, null);
            return string;
        }
    }

    public static List<Locale> getApplicationLocales(Context context) {
        Locale[] availableLocales;
        if (mApplicationLocales.isEmpty()) {
            HashSet<Pair> hashSet = new HashSet<>();
            try {
                for (Locale locale : Locale.getAvailableLocales()) {
                    hashSet.add(new Pair(getString(context, locale, C1299R.string.resouces_language), getString(context, locale, C1299R.string.resouces_country)));
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getApplicationLocales() : failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
                hashSet.add(new Pair(context.getString(C1299R.string.resouces_language), context.getString(C1299R.string.resouces_country)));
            }
            for (Pair pair : hashSet) {
                mApplicationLocales.add(new Locale((String) pair.first, (String) pair.second));
            }
        }
        ArrayList arrayList = new ArrayList(mApplicationLocales);
        Collections.sort(arrayList, new Comparator<Locale>() {
            public int compare(Locale locale, Locale locale2) {
                return VectorApp.localeToLocalisedString(locale).compareTo(VectorApp.localeToLocalisedString(locale2));
            }
        });
        return arrayList;
    }

    public static String localeToLocalisedString(Locale locale) {
        String displayLanguage = locale.getDisplayLanguage(locale);
        if (TextUtils.isEmpty(locale.getDisplayCountry(locale))) {
            return displayLanguage;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(displayLanguage);
        sb.append(" (");
        sb.append(locale.getDisplayCountry(locale));
        sb.append(")");
        return sb.toString();
    }

    public void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public void runOnUIThreadDelayed(Runnable runnable, int i) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, (long) i);
    }

    public void handleReceivedOfflineMessage(OfflineMessage offlineMessage) {
        if (this.offLineMessagePreference) {
            MXSession defaultSession = Matrix.getInstance(getInstance().getApplicationContext()).getDefaultSession();
            if (defaultSession != null) {
                defaultSession.handleReceivedOfflineMessage(offlineMessage);
            }
        }
    }

    public void handleReceivedOfflineSync(JsonObject jsonObject) {
        if (this.offLineMessagePreference) {
            MXSession defaultSession = Matrix.getInstance(getInstance().getApplicationContext()).getDefaultSession();
            if (defaultSession != null) {
                defaultSession.handleReceivedOfflineSync(jsonObject);
            }
        }
    }

    public void updateOfflinePreference(Boolean bool) {
        this.offLineMessagePreference = bool.booleanValue();
    }

    public void handleSentOfflineMessage(String str) {
        MXSession defaultSession = Matrix.getInstance(getInstance().getApplicationContext()).getDefaultSession();
        if (defaultSession != null) {
            defaultSession.addSentMessageToList(str);
        }
    }

    private void initBTLE() {
        if (isBTLESupported()) {
            this.mBtle = new BluetoothLE();
        }
    }

    @SuppressLint({"InlinedApi"})
    public boolean isBTLESupported() {
        if (VERSION.SDK_INT < 18) {
            return false;
        }
        return getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }
}
