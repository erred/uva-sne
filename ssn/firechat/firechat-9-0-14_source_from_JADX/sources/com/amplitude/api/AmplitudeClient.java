package com.amplitude.api;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.support.p000v4.p002os.EnvironmentCompat;
import android.util.Pair;
import com.google.android.gms.dynamite.ProviderConstants;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.opengarden.firechat.matrixsdk.rest.model.login.PasswordLoginParams;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.OkHttpClient;
import org.altbeacon.bluetooth.Pdu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmplitudeClient {
    public static final String DEVICE_ID_KEY = "device_id";
    public static final String END_SESSION_EVENT = "session_end";
    public static final String LAST_EVENT_ID_KEY = "last_event_id";
    public static final String LAST_EVENT_TIME_KEY = "last_event_time";
    public static final String LAST_IDENTIFY_ID_KEY = "last_identify_id";
    public static final String OPT_OUT_KEY = "opt_out";
    public static final String PREVIOUS_SESSION_ID_KEY = "previous_session_id";
    public static final String SEQUENCE_NUMBER_KEY = "sequence_number";
    public static final String START_SESSION_EVENT = "session_start";
    public static final String TAG = "com.amplitude.api.AmplitudeClient";
    public static final String USER_ID_KEY = "user_id";
    /* access modifiers changed from: private */
    public static final AmplitudeLog logger = AmplitudeLog.getLogger();
    protected String apiKey;
    /* access modifiers changed from: private */
    public boolean backoffUpload;
    /* access modifiers changed from: private */
    public int backoffUploadBatchSize;
    protected Context context;
    protected DatabaseHelper dbHelper;
    protected String deviceId;
    /* access modifiers changed from: private */
    public DeviceInfo deviceInfo;
    private int eventMaxCount;
    /* access modifiers changed from: private */
    public int eventUploadMaxBatchSize;
    private long eventUploadPeriodMillis;
    /* access modifiers changed from: private */
    public int eventUploadThreshold;
    /* access modifiers changed from: private */
    public boolean flushEventsOnClose;
    protected OkHttpClient httpClient;
    WorkerThread httpThread;
    /* access modifiers changed from: private */
    public boolean inForeground;
    protected boolean initialized;
    protected String instanceName;
    Throwable lastError;
    long lastEventId;
    long lastEventTime;
    long lastIdentifyId;
    WorkerThread logThread;
    private long minTimeBetweenSessionsMillis;
    private boolean newDeviceIdPerInstall;
    private boolean offline;
    /* access modifiers changed from: private */
    public boolean optOut;
    long previousSessionId;
    long sequenceNumber;
    long sessionId;
    private long sessionTimeoutMillis;
    private boolean trackingSessionEvents;
    /* access modifiers changed from: private */
    public AtomicBoolean updateScheduled;
    AtomicBoolean uploadingCurrently;
    String url;
    private boolean useAdvertisingIdForDeviceId;
    protected String userId;
    private boolean usingForegroundTracking;

    public AmplitudeClient() {
        this(null);
    }

    public AmplitudeClient(String str) {
        this.newDeviceIdPerInstall = false;
        this.useAdvertisingIdForDeviceId = false;
        this.initialized = false;
        this.optOut = false;
        this.offline = false;
        this.sessionId = -1;
        this.sequenceNumber = 0;
        this.lastEventId = -1;
        this.lastIdentifyId = -1;
        this.lastEventTime = -1;
        this.previousSessionId = -1;
        this.eventUploadThreshold = 30;
        this.eventUploadMaxBatchSize = 100;
        this.eventMaxCount = 1000;
        this.eventUploadPeriodMillis = Constants.EVENT_UPLOAD_PERIOD_MILLIS;
        this.minTimeBetweenSessionsMillis = 300000;
        this.sessionTimeoutMillis = 1800000;
        this.backoffUpload = false;
        this.backoffUploadBatchSize = this.eventUploadMaxBatchSize;
        this.usingForegroundTracking = false;
        this.trackingSessionEvents = false;
        this.inForeground = false;
        this.flushEventsOnClose = true;
        this.updateScheduled = new AtomicBoolean(false);
        this.uploadingCurrently = new AtomicBoolean(false);
        this.url = Constants.EVENT_LOG_URL;
        this.logThread = new WorkerThread("logThread");
        this.httpThread = new WorkerThread("httpThread");
        this.instanceName = C0532Utils.normalizeInstanceName(str);
        this.logThread.start();
        this.httpThread.start();
    }

    public AmplitudeClient initialize(Context context2, String str) {
        return initialize(context2, str, null);
    }

    public synchronized AmplitudeClient initialize(final Context context2, String str, final String str2) {
        if (context2 == null) {
            logger.mo9078e(TAG, "Argument context cannot be null in initialize()");
            return this;
        } else if (C0532Utils.isEmptyString(str)) {
            logger.mo9078e(TAG, "Argument apiKey cannot be null or blank in initialize()");
            return this;
        } else {
            this.context = context2.getApplicationContext();
            this.apiKey = str;
            this.dbHelper = DatabaseHelper.getDatabaseHelper(this.context, this.instanceName);
            runOnLogThread(new Runnable() {
                public void run() {
                    if (!AmplitudeClient.this.initialized) {
                        try {
                            if (AmplitudeClient.this.instanceName.equals(Constants.DEFAULT_INSTANCE)) {
                                AmplitudeClient.upgradePrefs(context2);
                                AmplitudeClient.upgradeSharedPrefsToDB(context2);
                            }
                            AmplitudeClient.this.httpClient = new OkHttpClient();
                            AmplitudeClient.this.initializeDeviceInfo();
                            if (str2 != null) {
                                this.userId = str2;
                                AmplitudeClient.this.dbHelper.insertOrReplaceKeyValue(AmplitudeClient.USER_ID_KEY, str2);
                            } else {
                                this.userId = AmplitudeClient.this.dbHelper.getValue(AmplitudeClient.USER_ID_KEY);
                            }
                            Long longValue = AmplitudeClient.this.dbHelper.getLongValue(AmplitudeClient.OPT_OUT_KEY);
                            AmplitudeClient.this.optOut = longValue != null && longValue.longValue() == 1;
                            AmplitudeClient.this.previousSessionId = AmplitudeClient.this.getLongvalue(AmplitudeClient.PREVIOUS_SESSION_ID_KEY, -1);
                            if (AmplitudeClient.this.previousSessionId >= 0) {
                                AmplitudeClient.this.sessionId = AmplitudeClient.this.previousSessionId;
                            }
                            AmplitudeClient.this.sequenceNumber = AmplitudeClient.this.getLongvalue(AmplitudeClient.SEQUENCE_NUMBER_KEY, 0);
                            AmplitudeClient.this.lastEventId = AmplitudeClient.this.getLongvalue(AmplitudeClient.LAST_EVENT_ID_KEY, -1);
                            AmplitudeClient.this.lastIdentifyId = AmplitudeClient.this.getLongvalue(AmplitudeClient.LAST_IDENTIFY_ID_KEY, -1);
                            AmplitudeClient.this.lastEventTime = AmplitudeClient.this.getLongvalue(AmplitudeClient.LAST_EVENT_TIME_KEY, -1);
                            AmplitudeClient.this.initialized = true;
                        } catch (CursorWindowAllocationException e) {
                            AmplitudeClient.logger.mo9078e(AmplitudeClient.TAG, String.format("Failed to initialize Amplitude SDK due to: %s", new Object[]{e.getMessage()}));
                            this.apiKey = null;
                        }
                    }
                }
            });
            return this;
        }
    }

    public AmplitudeClient enableForegroundTracking(Application application) {
        if (!this.usingForegroundTracking && contextAndApiKeySet("enableForegroundTracking()") && VERSION.SDK_INT >= 14) {
            application.registerActivityLifecycleCallbacks(new AmplitudeCallbacks(this));
        }
        return this;
    }

    /* access modifiers changed from: private */
    public void initializeDeviceInfo() {
        this.deviceInfo = new DeviceInfo(this.context);
        this.deviceId = initializeDeviceId();
        this.deviceInfo.prefetch();
    }

    public AmplitudeClient enableNewDeviceIdPerInstall(boolean z) {
        this.newDeviceIdPerInstall = z;
        return this;
    }

    public AmplitudeClient useAdvertisingIdForDeviceId() {
        this.useAdvertisingIdForDeviceId = true;
        return this;
    }

    public AmplitudeClient enableLocationListening() {
        runOnLogThread(new Runnable() {
            public void run() {
                if (AmplitudeClient.this.deviceInfo == null) {
                    throw new IllegalStateException("Must initialize before acting on location listening.");
                }
                AmplitudeClient.this.deviceInfo.setLocationListening(true);
            }
        });
        return this;
    }

    public AmplitudeClient disableLocationListening() {
        runOnLogThread(new Runnable() {
            public void run() {
                if (AmplitudeClient.this.deviceInfo == null) {
                    throw new IllegalStateException("Must initialize before acting on location listening.");
                }
                AmplitudeClient.this.deviceInfo.setLocationListening(false);
            }
        });
        return this;
    }

    public AmplitudeClient setEventUploadThreshold(int i) {
        this.eventUploadThreshold = i;
        return this;
    }

    public AmplitudeClient setEventUploadMaxBatchSize(int i) {
        this.eventUploadMaxBatchSize = i;
        this.backoffUploadBatchSize = i;
        return this;
    }

    public AmplitudeClient setEventMaxCount(int i) {
        this.eventMaxCount = i;
        return this;
    }

    public AmplitudeClient setEventUploadPeriodMillis(int i) {
        this.eventUploadPeriodMillis = (long) i;
        return this;
    }

    public AmplitudeClient setMinTimeBetweenSessionsMillis(long j) {
        this.minTimeBetweenSessionsMillis = j;
        return this;
    }

    public AmplitudeClient setSessionTimeoutMillis(long j) {
        this.sessionTimeoutMillis = j;
        return this;
    }

    public AmplitudeClient setOptOut(final boolean z) {
        if (!contextAndApiKeySet("setOptOut()")) {
            return this;
        }
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(AmplitudeClient.this.apiKey)) {
                    this.optOut = z;
                    AmplitudeClient.this.dbHelper.insertOrReplaceKeyLongValue(AmplitudeClient.OPT_OUT_KEY, Long.valueOf(z ? 1 : 0));
                }
            }
        });
        return this;
    }

    public boolean isOptedOut() {
        return this.optOut;
    }

    public AmplitudeClient enableLogging(boolean z) {
        logger.setEnableLogging(z);
        return this;
    }

    public AmplitudeClient setLogLevel(int i) {
        logger.setLogLevel(i);
        return this;
    }

    public AmplitudeClient setOffline(boolean z) {
        this.offline = z;
        if (!z) {
            uploadEvents();
        }
        return this;
    }

    public AmplitudeClient setFlushEventsOnClose(boolean z) {
        this.flushEventsOnClose = z;
        return this;
    }

    public AmplitudeClient trackSessionEvents(boolean z) {
        this.trackingSessionEvents = z;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public void useForegroundTracking() {
        this.usingForegroundTracking = true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isUsingForegroundTracking() {
        return this.usingForegroundTracking;
    }

    /* access modifiers changed from: 0000 */
    public boolean isInForeground() {
        return this.inForeground;
    }

    public void logEvent(String str) {
        logEvent(str, null);
    }

    public void logEvent(String str, JSONObject jSONObject) {
        logEvent(str, jSONObject, false);
    }

    public void logEvent(String str, JSONObject jSONObject, boolean z) {
        logEvent(str, jSONObject, null, z);
    }

    public void logEvent(String str, JSONObject jSONObject, JSONObject jSONObject2) {
        logEvent(str, jSONObject, jSONObject2, false);
    }

    public void logEvent(String str, JSONObject jSONObject, JSONObject jSONObject2, boolean z) {
        logEvent(str, jSONObject, jSONObject2, getCurrentTimeMillis(), z);
    }

    public void logEvent(String str, JSONObject jSONObject, JSONObject jSONObject2, long j, boolean z) {
        if (validateLogEvent(str)) {
            logEventAsync(str, jSONObject, null, null, jSONObject2, j, z);
        }
    }

    public void logEventSync(String str) {
        logEventSync(str, null);
    }

    public void logEventSync(String str, JSONObject jSONObject) {
        logEventSync(str, jSONObject, false);
    }

    public void logEventSync(String str, JSONObject jSONObject, boolean z) {
        logEventSync(str, jSONObject, null, z);
    }

    public void logEventSync(String str, JSONObject jSONObject, JSONObject jSONObject2) {
        logEventSync(str, jSONObject, jSONObject2, false);
    }

    public void logEventSync(String str, JSONObject jSONObject, JSONObject jSONObject2, boolean z) {
        logEventSync(str, jSONObject, jSONObject2, getCurrentTimeMillis(), z);
    }

    public void logEventSync(String str, JSONObject jSONObject, JSONObject jSONObject2, long j, boolean z) {
        if (validateLogEvent(str)) {
            logEvent(str, jSONObject, null, null, jSONObject2, j, z);
        }
    }

    /* access modifiers changed from: protected */
    public boolean validateLogEvent(String str) {
        if (!C0532Utils.isEmptyString(str)) {
            return contextAndApiKeySet("logEvent()");
        }
        logger.mo9078e(TAG, "Argument eventType cannot be null or blank in logEvent()");
        return false;
    }

    /* access modifiers changed from: protected */
    public void logEventAsync(String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3, JSONObject jSONObject4, long j, boolean z) {
        final JSONObject cloneJSONObject = jSONObject != null ? C0532Utils.cloneJSONObject(jSONObject) : jSONObject;
        final JSONObject cloneJSONObject2 = jSONObject3 != null ? C0532Utils.cloneJSONObject(jSONObject3) : jSONObject3;
        final JSONObject cloneJSONObject3 = jSONObject4 != null ? C0532Utils.cloneJSONObject(jSONObject4) : jSONObject4;
        final String str2 = str;
        final JSONObject jSONObject5 = jSONObject2;
        final long j2 = j;
        final boolean z2 = z;
        C05255 r0 = new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(AmplitudeClient.this.apiKey)) {
                    AmplitudeClient.this.logEvent(str2, cloneJSONObject, jSONObject5, cloneJSONObject2, cloneJSONObject3, j2, z2);
                }
            }
        };
        runOnLogThread(r0);
    }

    /* access modifiers changed from: protected */
    public long logEvent(String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3, JSONObject jSONObject4, long j, boolean z) {
        long j2;
        long j3;
        JSONObject jSONObject5;
        JSONObject jSONObject6;
        AmplitudeLog amplitudeLog = logger;
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Logged event to Amplitude: ");
        sb.append(str);
        amplitudeLog.mo9076d(str2, sb.toString());
        if (this.optOut) {
            return -1;
        }
        if (!(this.trackingSessionEvents && (str.equals(START_SESSION_EVENT) || str.equals(END_SESSION_EVENT))) && !z) {
            if (!this.inForeground) {
                startNewSessionIfNeeded(j);
            } else {
                refreshSessionTime(j);
            }
        }
        JSONObject jSONObject7 = new JSONObject();
        try {
            jSONObject7.put("event_type", replaceWithJSONNull(str));
            jSONObject7.put(Param.TIMESTAMP, j);
            jSONObject7.put(USER_ID_KEY, replaceWithJSONNull(this.userId));
            jSONObject7.put(DEVICE_ID_KEY, replaceWithJSONNull(this.deviceId));
            String str3 = VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID;
            if (z) {
                j3 = -1;
            } else {
                j3 = this.sessionId;
            }
            jSONObject7.put(str3, j3);
            jSONObject7.put("version_name", replaceWithJSONNull(this.deviceInfo.getVersionName()));
            jSONObject7.put("os_name", replaceWithJSONNull(this.deviceInfo.getOsName()));
            jSONObject7.put("os_version", replaceWithJSONNull(this.deviceInfo.getOsVersion()));
            jSONObject7.put("device_brand", replaceWithJSONNull(this.deviceInfo.getBrand()));
            jSONObject7.put("device_manufacturer", replaceWithJSONNull(this.deviceInfo.getManufacturer()));
            jSONObject7.put("device_model", replaceWithJSONNull(this.deviceInfo.getModel()));
            jSONObject7.put("carrier", replaceWithJSONNull(this.deviceInfo.getCarrier()));
            jSONObject7.put(PasswordLoginParams.IDENTIFIER_KEY_COUNTRY, replaceWithJSONNull(this.deviceInfo.getCountry()));
            jSONObject7.put("language", replaceWithJSONNull(this.deviceInfo.getLanguage()));
            jSONObject7.put("platform", Constants.PLATFORM);
            jSONObject7.put("uuid", UUID.randomUUID().toString());
            jSONObject7.put(SEQUENCE_NUMBER_KEY, getNextSequenceNumber());
            JSONObject jSONObject8 = new JSONObject();
            jSONObject8.put("name", Constants.LIBRARY);
            jSONObject8.put(ProviderConstants.API_COLNAME_FEATURE_VERSION, Constants.VERSION);
            jSONObject7.put("library", jSONObject8);
            if (jSONObject2 == null) {
                jSONObject2 = new JSONObject();
            }
            Location mostRecentLocation = this.deviceInfo.getMostRecentLocation();
            if (mostRecentLocation != null) {
                JSONObject jSONObject9 = new JSONObject();
                jSONObject9.put("lat", mostRecentLocation.getLatitude());
                jSONObject9.put("lng", mostRecentLocation.getLongitude());
                jSONObject2.put(FirebaseAnalytics.Param.LOCATION, jSONObject9);
            }
            if (this.deviceInfo.getAdvertisingId() != null) {
                jSONObject2.put("androidADID", this.deviceInfo.getAdvertisingId());
            }
            jSONObject2.put("limit_ad_tracking", this.deviceInfo.isLimitAdTrackingEnabled());
            jSONObject2.put("gps_enabled", this.deviceInfo.isGooglePlayServicesEnabled());
            jSONObject7.put("api_properties", jSONObject2);
            String str4 = "event_properties";
            if (jSONObject == null) {
                jSONObject5 = new JSONObject();
            } else {
                jSONObject5 = truncate(jSONObject);
            }
            jSONObject7.put(str4, jSONObject5);
            String str5 = "user_properties";
            if (jSONObject3 == null) {
                jSONObject6 = new JSONObject();
            } else {
                jSONObject6 = truncate(jSONObject3);
            }
            jSONObject7.put(str5, jSONObject6);
            jSONObject7.put("groups", jSONObject4 == null ? new JSONObject() : truncate(jSONObject4));
            j2 = saveEvent(str, jSONObject7);
        } catch (JSONException e) {
            logger.mo9078e(TAG, String.format("JSON Serialization of event type %s failed, skipping: %s", new Object[]{str, e.toString()}));
            j2 = -1;
        }
        return j2;
    }

    /* access modifiers changed from: protected */
    public long saveEvent(String str, JSONObject jSONObject) {
        String jSONObject2 = jSONObject.toString();
        if (C0532Utils.isEmptyString(jSONObject2)) {
            logger.mo9078e(TAG, String.format("Detected empty event string for event type %s, skipping", new Object[]{str}));
            return -1;
        }
        if (str.equals(Constants.IDENTIFY_EVENT)) {
            this.lastIdentifyId = this.dbHelper.addIdentify(jSONObject2);
            setLastIdentifyId(this.lastIdentifyId);
        } else {
            this.lastEventId = this.dbHelper.addEvent(jSONObject2);
            setLastEventId(this.lastEventId);
        }
        int min = Math.min(Math.max(1, this.eventMaxCount / 10), 20);
        if (this.dbHelper.getEventCount() > ((long) this.eventMaxCount)) {
            this.dbHelper.removeEvents(this.dbHelper.getNthEventId((long) min));
        }
        if (this.dbHelper.getIdentifyCount() > ((long) this.eventMaxCount)) {
            this.dbHelper.removeIdentifys(this.dbHelper.getNthIdentifyId((long) min));
        }
        long totalEventCount = this.dbHelper.getTotalEventCount();
        if (totalEventCount % ((long) this.eventUploadThreshold) != 0 || totalEventCount < ((long) this.eventUploadThreshold)) {
            updateServerLater(this.eventUploadPeriodMillis);
        } else {
            updateServer();
        }
        return str.equals(Constants.IDENTIFY_EVENT) ? this.lastIdentifyId : this.lastEventId;
    }

    /* access modifiers changed from: private */
    public long getLongvalue(String str, long j) {
        Long longValue = this.dbHelper.getLongValue(str);
        return longValue == null ? j : longValue.longValue();
    }

    /* access modifiers changed from: 0000 */
    public long getNextSequenceNumber() {
        this.sequenceNumber++;
        this.dbHelper.insertOrReplaceKeyLongValue(SEQUENCE_NUMBER_KEY, Long.valueOf(this.sequenceNumber));
        return this.sequenceNumber;
    }

    /* access modifiers changed from: 0000 */
    public void setLastEventTime(long j) {
        this.lastEventTime = j;
        this.dbHelper.insertOrReplaceKeyLongValue(LAST_EVENT_TIME_KEY, Long.valueOf(j));
    }

    /* access modifiers changed from: 0000 */
    public void setLastEventId(long j) {
        this.lastEventId = j;
        this.dbHelper.insertOrReplaceKeyLongValue(LAST_EVENT_ID_KEY, Long.valueOf(j));
    }

    /* access modifiers changed from: 0000 */
    public void setLastIdentifyId(long j) {
        this.lastIdentifyId = j;
        this.dbHelper.insertOrReplaceKeyLongValue(LAST_IDENTIFY_ID_KEY, Long.valueOf(j));
    }

    public long getSessionId() {
        return this.sessionId;
    }

    /* access modifiers changed from: 0000 */
    public void setPreviousSessionId(long j) {
        this.previousSessionId = j;
        this.dbHelper.insertOrReplaceKeyLongValue(PREVIOUS_SESSION_ID_KEY, Long.valueOf(j));
    }

    /* access modifiers changed from: 0000 */
    public boolean startNewSessionIfNeeded(long j) {
        if (inSession()) {
            if (isWithinMinTimeBetweenSessions(j)) {
                refreshSessionTime(j);
                return false;
            }
            startNewSession(j);
            return true;
        } else if (!isWithinMinTimeBetweenSessions(j)) {
            startNewSession(j);
            return true;
        } else if (this.previousSessionId == -1) {
            startNewSession(j);
            return true;
        } else {
            setSessionId(this.previousSessionId);
            refreshSessionTime(j);
            return false;
        }
    }

    private void startNewSession(long j) {
        if (this.trackingSessionEvents) {
            sendSessionEvent(END_SESSION_EVENT);
        }
        setSessionId(j);
        refreshSessionTime(j);
        if (this.trackingSessionEvents) {
            sendSessionEvent(START_SESSION_EVENT);
        }
    }

    private boolean inSession() {
        return this.sessionId >= 0;
    }

    private boolean isWithinMinTimeBetweenSessions(long j) {
        return j - this.lastEventTime < (this.usingForegroundTracking ? this.minTimeBetweenSessionsMillis : this.sessionTimeoutMillis);
    }

    private void setSessionId(long j) {
        this.sessionId = j;
        setPreviousSessionId(j);
    }

    /* access modifiers changed from: 0000 */
    public void refreshSessionTime(long j) {
        if (inSession()) {
            setLastEventTime(j);
        }
    }

    private void sendSessionEvent(String str) {
        if (contextAndApiKeySet(String.format("sendSessionEvent('%s')", new Object[]{str})) && inSession()) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("special", str);
                logEvent(str, null, jSONObject, null, null, this.lastEventTime, false);
            } catch (JSONException unused) {
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onExitForeground(final long j) {
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(AmplitudeClient.this.apiKey)) {
                    AmplitudeClient.this.refreshSessionTime(j);
                    AmplitudeClient.this.inForeground = false;
                    if (AmplitudeClient.this.flushEventsOnClose) {
                        AmplitudeClient.this.updateServer();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void onEnterForeground(final long j) {
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(AmplitudeClient.this.apiKey)) {
                    AmplitudeClient.this.startNewSessionIfNeeded(j);
                    AmplitudeClient.this.inForeground = true;
                }
            }
        });
    }

    public void logRevenue(double d) {
        logRevenue(null, 1, d);
    }

    public void logRevenue(String str, int i, double d) {
        logRevenue(str, i, d, null, null);
    }

    public void logRevenue(String str, int i, double d, String str2, String str3) {
        if (contextAndApiKeySet("logRevenue()")) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("special", Constants.AMP_REVENUE_EVENT);
                jSONObject.put("productId", str);
                jSONObject.put(FirebaseAnalytics.Param.QUANTITY, i);
                jSONObject.put(FirebaseAnalytics.Param.PRICE, d);
                jSONObject.put("receipt", str2);
                jSONObject.put("receiptSig", str3);
            } catch (JSONException unused) {
            }
            logEventAsync(Constants.AMP_REVENUE_EVENT, null, jSONObject, null, null, getCurrentTimeMillis(), false);
        }
    }

    public void logRevenueV2(Revenue revenue) {
        if (contextAndApiKeySet("logRevenueV2()") && revenue != null && revenue.isValidRevenue()) {
            logEvent(Constants.AMP_REVENUE_EVENT, revenue.toJSONObject());
        }
    }

    public void setUserProperties(JSONObject jSONObject, boolean z) {
        setUserProperties(jSONObject);
    }

    public void setUserProperties(JSONObject jSONObject) {
        if (jSONObject != null && jSONObject.length() != 0 && contextAndApiKeySet("setUserProperties")) {
            JSONObject truncate = truncate(jSONObject);
            if (truncate.length() != 0) {
                Identify identify = new Identify();
                Iterator keys = truncate.keys();
                while (keys.hasNext()) {
                    String str = (String) keys.next();
                    try {
                        identify.setUserProperty(str, truncate.get(str));
                    } catch (JSONException e) {
                        logger.mo9078e(TAG, e.toString());
                    }
                }
                identify(identify);
            }
        }
    }

    public void clearUserProperties() {
        identify(new Identify().clearAll());
    }

    public void identify(Identify identify) {
        identify(identify, false);
    }

    public void identify(Identify identify, boolean z) {
        if (identify != null && identify.userPropertiesOperations.length() != 0 && contextAndApiKeySet("identify()")) {
            logEventAsync(Constants.IDENTIFY_EVENT, null, null, identify.userPropertiesOperations, null, getCurrentTimeMillis(), z);
        }
    }

    public void setGroup(String str, Object obj) {
        JSONObject jSONObject;
        if (contextAndApiKeySet("setGroup()") && !C0532Utils.isEmptyString(str)) {
            try {
                jSONObject = new JSONObject().put(str, obj);
            } catch (JSONException e) {
                logger.mo9078e(TAG, e.toString());
                jSONObject = null;
            }
            logEventAsync(Constants.IDENTIFY_EVENT, null, null, new Identify().setUserProperty(str, obj).userPropertiesOperations, jSONObject, getCurrentTimeMillis(), false);
        }
    }

    public JSONObject truncate(JSONObject jSONObject) {
        if (jSONObject == null) {
            return new JSONObject();
        }
        if (jSONObject.length() > 1000) {
            logger.mo9089w(TAG, "Warning: too many properties (more than 1000), ignoring");
            return new JSONObject();
        }
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            String str = (String) keys.next();
            try {
                Object obj = jSONObject.get(str);
                if (!str.equals(Constants.AMP_REVENUE_RECEIPT)) {
                    if (!str.equals(Constants.AMP_REVENUE_RECEIPT_SIG)) {
                        if (obj.getClass().equals(String.class)) {
                            jSONObject.put(str, truncate((String) obj));
                        } else if (obj.getClass().equals(JSONObject.class)) {
                            jSONObject.put(str, truncate((JSONObject) obj));
                        } else if (obj.getClass().equals(JSONArray.class)) {
                            jSONObject.put(str, truncate((JSONArray) obj));
                        }
                    }
                }
                jSONObject.put(str, obj);
            } catch (JSONException e) {
                logger.mo9078e(TAG, e.toString());
            }
        }
        return jSONObject;
    }

    public JSONArray truncate(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return new JSONArray();
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if (obj.getClass().equals(String.class)) {
                jSONArray.put(i, truncate((String) obj));
            } else if (obj.getClass().equals(JSONObject.class)) {
                jSONArray.put(i, truncate((JSONObject) obj));
            } else if (obj.getClass().equals(JSONArray.class)) {
                jSONArray.put(i, truncate((JSONArray) obj));
            }
        }
        return jSONArray;
    }

    public String truncate(String str) {
        return str.length() <= 1024 ? str : str.substring(0, 1024);
    }

    public String getUserId() {
        return this.userId;
    }

    public AmplitudeClient setUserId(final String str) {
        if (!contextAndApiKeySet("setUserId()")) {
            return this;
        }
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(this.apiKey)) {
                    this.userId = str;
                    AmplitudeClient.this.dbHelper.insertOrReplaceKeyValue(AmplitudeClient.USER_ID_KEY, str);
                }
            }
        });
        return this;
    }

    public AmplitudeClient setDeviceId(final String str) {
        Set invalidDeviceIds = getInvalidDeviceIds();
        if (!contextAndApiKeySet("setDeviceId()") || C0532Utils.isEmptyString(str) || invalidDeviceIds.contains(str)) {
            return this;
        }
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(this.apiKey)) {
                    this.deviceId = str;
                    AmplitudeClient.this.dbHelper.insertOrReplaceKeyValue(AmplitudeClient.DEVICE_ID_KEY, str);
                }
            }
        });
        return this;
    }

    public AmplitudeClient regenerateDeviceId() {
        if (!contextAndApiKeySet("regenerateDeviceId()")) {
            return this;
        }
        runOnLogThread(new Runnable() {
            public void run() {
                if (!C0532Utils.isEmptyString(this.apiKey)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(DeviceInfo.generateUUID());
                    sb.append("R");
                    AmplitudeClient.this.setDeviceId(sb.toString());
                }
            }
        });
        return this;
    }

    public void uploadEvents() {
        if (contextAndApiKeySet("uploadEvents()")) {
            this.logThread.post(new Runnable() {
                public void run() {
                    if (!C0532Utils.isEmptyString(AmplitudeClient.this.apiKey)) {
                        AmplitudeClient.this.updateServer();
                    }
                }
            });
        }
    }

    private void updateServerLater(long j) {
        if (!this.updateScheduled.getAndSet(true)) {
            this.logThread.postDelayed(new Runnable() {
                public void run() {
                    AmplitudeClient.this.updateScheduled.set(false);
                    AmplitudeClient.this.updateServer();
                }
            }, j);
        }
    }

    /* access modifiers changed from: protected */
    public void updateServer() {
        updateServer(false);
    }

    /* access modifiers changed from: protected */
    public void updateServer(boolean z) {
        if (!this.optOut && !this.offline && !this.uploadingCurrently.getAndSet(true)) {
            long min = Math.min((long) (z ? this.backoffUploadBatchSize : this.eventUploadMaxBatchSize), this.dbHelper.getTotalEventCount());
            if (min <= 0) {
                this.uploadingCurrently.set(false);
                return;
            }
            try {
                Pair mergeEventsAndIdentifys = mergeEventsAndIdentifys(this.dbHelper.getEvents(this.lastEventId, min), this.dbHelper.getIdentifys(this.lastIdentifyId, min), min);
                if (((JSONArray) mergeEventsAndIdentifys.second).length() == 0) {
                    this.uploadingCurrently.set(false);
                    return;
                }
                final long longValue = ((Long) ((Pair) mergeEventsAndIdentifys.first).first).longValue();
                final long longValue2 = ((Long) ((Pair) mergeEventsAndIdentifys.first).second).longValue();
                final String jSONArray = ((JSONArray) mergeEventsAndIdentifys.second).toString();
                WorkerThread workerThread = this.httpThread;
                C051813 r3 = new Runnable() {
                    public void run() {
                        AmplitudeClient.this.makeEventUploadPostRequest(AmplitudeClient.this.httpClient, jSONArray, longValue, longValue2);
                    }
                };
                workerThread.post(r3);
            } catch (JSONException e) {
                this.uploadingCurrently.set(false);
                logger.mo9078e(TAG, e.toString());
            } catch (CursorWindowAllocationException e2) {
                this.uploadingCurrently.set(false);
                logger.mo9078e(TAG, String.format("Caught Cursor window exception during event upload, deferring upload: %s", new Object[]{e2.getMessage()}));
            }
        }
    }

    /* access modifiers changed from: protected */
    public Pair<Pair<Long, Long>, JSONArray> mergeEventsAndIdentifys(List<JSONObject> list, List<JSONObject> list2, long j) throws JSONException {
        long j2;
        long j3;
        List<JSONObject> list3 = list;
        List<JSONObject> list4 = list2;
        JSONArray jSONArray = new JSONArray();
        long j4 = -1;
        long j5 = -1;
        while (true) {
            if (((long) jSONArray.length()) >= j) {
                break;
            }
            boolean isEmpty = list.isEmpty();
            boolean isEmpty2 = list2.isEmpty();
            if (isEmpty && isEmpty2) {
                logger.mo9089w(TAG, String.format("mergeEventsAndIdentifys: number of events and identifys less than expected by %d", new Object[]{Long.valueOf(j - ((long) jSONArray.length()))}));
                break;
            }
            if (isEmpty2) {
                JSONObject jSONObject = (JSONObject) list3.remove(0);
                j2 = jSONObject.getLong("event_id");
                jSONArray.put(jSONObject);
            } else {
                if (isEmpty) {
                    JSONObject jSONObject2 = (JSONObject) list4.remove(0);
                    j3 = jSONObject2.getLong("event_id");
                    jSONArray.put(jSONObject2);
                } else if (!((JSONObject) list3.get(0)).has(SEQUENCE_NUMBER_KEY) || ((JSONObject) list3.get(0)).getLong(SEQUENCE_NUMBER_KEY) < ((JSONObject) list4.get(0)).getLong(SEQUENCE_NUMBER_KEY)) {
                    JSONObject jSONObject3 = (JSONObject) list3.remove(0);
                    j2 = jSONObject3.getLong("event_id");
                    jSONArray.put(jSONObject3);
                } else {
                    JSONObject jSONObject4 = (JSONObject) list4.remove(0);
                    j3 = jSONObject4.getLong("event_id");
                    jSONArray.put(jSONObject4);
                }
                j5 = j3;
            }
            j4 = j2;
        }
        return new Pair<>(new Pair(Long.valueOf(j4), Long.valueOf(j5)), jSONArray);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01a6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void makeEventUploadPostRequest(okhttp3.OkHttpClient r9, java.lang.String r10, long r11, long r13) {
        /*
            r8 = this;
            java.lang.String r0 = "2"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = ""
            r1.append(r2)
            long r2 = r8.getCurrentTimeMillis()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = ""
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r3.<init>()     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r3.append(r0)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            java.lang.String r4 = r8.apiKey     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r3.append(r4)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r3.append(r10)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r3.append(r1)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            java.lang.String r3 = r3.toString()     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            com.amplitude.security.MD5 r4 = new com.amplitude.security.MD5     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r4.<init>()     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            java.lang.String r5 = "UTF-8"
            byte[] r3 = r3.getBytes(r5)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            byte[] r3 = r4.digest(r3)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            java.lang.String r3 = r8.bytesToHexString(r3)     // Catch:{ UnsupportedEncodingException -> 0x0045 }
            r2 = r3
            goto L_0x0051
        L_0x0045:
            r3 = move-exception
            com.amplitude.api.AmplitudeLog r4 = logger
            java.lang.String r5 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r3 = r3.toString()
            r4.mo9078e(r5, r3)
        L_0x0051:
            okhttp3.FormBody$Builder r3 = new okhttp3.FormBody$Builder
            r3.<init>()
            java.lang.String r4 = "v"
            okhttp3.FormBody$Builder r0 = r3.add(r4, r0)
            java.lang.String r3 = "client"
            java.lang.String r4 = r8.apiKey
            okhttp3.FormBody$Builder r0 = r0.add(r3, r4)
            java.lang.String r3 = "e"
            okhttp3.FormBody$Builder r10 = r0.add(r3, r10)
            java.lang.String r0 = "upload_time"
            okhttp3.FormBody$Builder r10 = r10.add(r0, r1)
            java.lang.String r0 = "checksum"
            okhttp3.FormBody$Builder r10 = r10.add(r0, r2)
            okhttp3.FormBody r10 = r10.build()
            r0 = 0
            okhttp3.Request$Builder r1 = new okhttp3.Request$Builder     // Catch:{ IllegalArgumentException -> 0x01ac }
            r1.<init>()     // Catch:{ IllegalArgumentException -> 0x01ac }
            java.lang.String r2 = r8.url     // Catch:{ IllegalArgumentException -> 0x01ac }
            okhttp3.Request$Builder r1 = r1.url(r2)     // Catch:{ IllegalArgumentException -> 0x01ac }
            okhttp3.Request$Builder r10 = r1.post(r10)     // Catch:{ IllegalArgumentException -> 0x01ac }
            okhttp3.Request r10 = r10.build()     // Catch:{ IllegalArgumentException -> 0x01ac }
            r1 = 1
            okhttp3.Call r9 = r9.newCall(r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            okhttp3.Response r9 = r9.execute()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            okhttp3.ResponseBody r10 = r9.body()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = r10.string()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r2 = "success"
            boolean r2 = r10.equals(r2)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r2 == 0) goto L_0x00c6
            com.amplitude.api.WorkerThread r9 = r8.logThread     // Catch:{ ConnectException -> 0x00c3, UnknownHostException -> 0x00c0, IOException -> 0x00bd, AssertionError -> 0x00ba, Exception -> 0x00b7 }
            com.amplitude.api.AmplitudeClient$14 r10 = new com.amplitude.api.AmplitudeClient$14     // Catch:{ ConnectException -> 0x00c3, UnknownHostException -> 0x00c0, IOException -> 0x00bd, AssertionError -> 0x00ba, Exception -> 0x00b7 }
            r2 = r10
            r3 = r8
            r4 = r11
            r6 = r13
            r2.<init>(r4, r6)     // Catch:{ ConnectException -> 0x00c3, UnknownHostException -> 0x00c0, IOException -> 0x00bd, AssertionError -> 0x00ba, Exception -> 0x00b7 }
            r9.post(r10)     // Catch:{ ConnectException -> 0x00c3, UnknownHostException -> 0x00c0, IOException -> 0x00bd, AssertionError -> 0x00ba, Exception -> 0x00b7 }
            goto L_0x01a4
        L_0x00b7:
            r9 = move-exception
            goto L_0x0171
        L_0x00ba:
            r9 = move-exception
            goto L_0x017f
        L_0x00bd:
            r9 = move-exception
            goto L_0x018d
        L_0x00c0:
            r9 = move-exception
            goto L_0x019d
        L_0x00c3:
            r9 = move-exception
            goto L_0x01a2
        L_0x00c6:
            java.lang.String r2 = "invalid_api_key"
            boolean r2 = r10.equals(r2)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r2 == 0) goto L_0x00d9
            com.amplitude.api.AmplitudeLog r9 = logger     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r11 = "Invalid API key, make sure your API key is correct in initialize()"
            r9.mo9078e(r10, r11)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            goto L_0x016d
        L_0x00d9:
            java.lang.String r2 = "bad_checksum"
            boolean r2 = r10.equals(r2)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r2 == 0) goto L_0x00ec
            com.amplitude.api.AmplitudeLog r9 = logger     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r11 = "Bad checksum, post request was mangled in transit, will attempt to reupload later"
            r9.mo9089w(r10, r11)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            goto L_0x016d
        L_0x00ec:
            java.lang.String r2 = "request_db_write_failed"
            boolean r2 = r10.equals(r2)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r2 == 0) goto L_0x00fe
            com.amplitude.api.AmplitudeLog r9 = logger     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r11 = "Couldn't write to request database on server, will attempt to reupload later"
            r9.mo9089w(r10, r11)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            goto L_0x016d
        L_0x00fe:
            int r9 = r9.code()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r2 = 413(0x19d, float:5.79E-43)
            if (r9 != r2) goto L_0x0150
            boolean r9 = r8.backoffUpload     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r9 == 0) goto L_0x0122
            int r9 = r8.backoffUploadBatchSize     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            if (r9 != r1) goto L_0x0122
            r9 = 0
            int r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r2 < 0) goto L_0x0119
            com.amplitude.api.DatabaseHelper r2 = r8.dbHelper     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r2.removeEvent(r11)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
        L_0x0119:
            int r11 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r11 < 0) goto L_0x0122
            com.amplitude.api.DatabaseHelper r9 = r8.dbHelper     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r9.removeIdentify(r13)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
        L_0x0122:
            r8.backoffUpload = r1     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            com.amplitude.api.DatabaseHelper r9 = r8.dbHelper     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            long r9 = r9.getEventCount()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            int r9 = (int) r9     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            int r10 = r8.backoffUploadBatchSize     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            int r9 = java.lang.Math.min(r9, r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            double r9 = (double) r9     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r11 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r9 = r9 / r11
            double r9 = java.lang.Math.ceil(r9)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            int r9 = (int) r9     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r8.backoffUploadBatchSize = r9     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            com.amplitude.api.AmplitudeLog r9 = logger     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r11 = "Request too large, will decrease size and attempt to reupload"
            r9.mo9089w(r10, r11)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            com.amplitude.api.WorkerThread r9 = r8.logThread     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            com.amplitude.api.AmplitudeClient$15 r10 = new com.amplitude.api.AmplitudeClient$15     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r10.<init>()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r9.post(r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            goto L_0x016d
        L_0x0150:
            com.amplitude.api.AmplitudeLog r9 = logger     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r11 = "com.amplitude.api.AmplitudeClient"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r12.<init>()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r13 = "Upload failed, "
            r12.append(r13)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r12.append(r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = ", will attempt to reupload later"
            r12.append(r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            java.lang.String r10 = r12.toString()     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
            r9.mo9089w(r11, r10)     // Catch:{ ConnectException -> 0x01a0, UnknownHostException -> 0x019b, IOException -> 0x018b, AssertionError -> 0x017d, Exception -> 0x016f }
        L_0x016d:
            r1 = 0
            goto L_0x01a4
        L_0x016f:
            r9 = move-exception
            r1 = 0
        L_0x0171:
            com.amplitude.api.AmplitudeLog r10 = logger
            java.lang.String r11 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r12 = "Exception:"
            r10.mo9079e(r11, r12, r9)
            r8.lastError = r9
            goto L_0x01a4
        L_0x017d:
            r9 = move-exception
            r1 = 0
        L_0x017f:
            com.amplitude.api.AmplitudeLog r10 = logger
            java.lang.String r11 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r12 = "Exception:"
            r10.mo9079e(r11, r12, r9)
            r8.lastError = r9
            goto L_0x01a4
        L_0x018b:
            r9 = move-exception
            r1 = 0
        L_0x018d:
            com.amplitude.api.AmplitudeLog r10 = logger
            java.lang.String r11 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r12 = r9.toString()
            r10.mo9078e(r11, r12)
            r8.lastError = r9
            goto L_0x01a4
        L_0x019b:
            r9 = move-exception
            r1 = 0
        L_0x019d:
            r8.lastError = r9
            goto L_0x01a4
        L_0x01a0:
            r9 = move-exception
            r1 = 0
        L_0x01a2:
            r8.lastError = r9
        L_0x01a4:
            if (r1 != 0) goto L_0x01ab
            java.util.concurrent.atomic.AtomicBoolean r9 = r8.uploadingCurrently
            r9.set(r0)
        L_0x01ab:
            return
        L_0x01ac:
            r9 = move-exception
            com.amplitude.api.AmplitudeLog r10 = logger
            java.lang.String r11 = "com.amplitude.api.AmplitudeClient"
            java.lang.String r9 = r9.toString()
            r10.mo9078e(r11, r9)
            java.util.concurrent.atomic.AtomicBoolean r9 = r8.uploadingCurrently
            r9.set(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.AmplitudeClient.makeEventUploadPostRequest(okhttp3.OkHttpClient, java.lang.String, long, long):void");
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    private Set<String> getInvalidDeviceIds() {
        HashSet hashSet = new HashSet();
        hashSet.add("");
        hashSet.add("9774d56d682e549c");
        hashSet.add(EnvironmentCompat.MEDIA_UNKNOWN);
        hashSet.add("000000000000000");
        hashSet.add(Constants.PLATFORM);
        hashSet.add("DEFACE");
        hashSet.add("00000000-0000-0000-0000-000000000000");
        return hashSet;
    }

    private String initializeDeviceId() {
        Set invalidDeviceIds = getInvalidDeviceIds();
        String value = this.dbHelper.getValue(DEVICE_ID_KEY);
        if (!C0532Utils.isEmptyString(value) && !invalidDeviceIds.contains(value)) {
            return value;
        }
        if (!this.newDeviceIdPerInstall && this.useAdvertisingIdForDeviceId) {
            String advertisingId = this.deviceInfo.getAdvertisingId();
            if (!C0532Utils.isEmptyString(advertisingId) && !invalidDeviceIds.contains(advertisingId)) {
                this.dbHelper.insertOrReplaceKeyValue(DEVICE_ID_KEY, advertisingId);
                return advertisingId;
            }
        }
        StringBuilder sb = new StringBuilder();
        DeviceInfo deviceInfo2 = this.deviceInfo;
        sb.append(DeviceInfo.generateUUID());
        sb.append("R");
        String sb2 = sb.toString();
        this.dbHelper.insertOrReplaceKeyValue(DEVICE_ID_KEY, sb2);
        return sb2;
    }

    /* access modifiers changed from: protected */
    public void runOnLogThread(Runnable runnable) {
        if (Thread.currentThread() != this.logThread) {
            this.logThread.post(runnable);
        } else {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public Object replaceWithJSONNull(Object obj) {
        return obj == null ? JSONObject.NULL : obj;
    }

    /* access modifiers changed from: protected */
    public synchronized boolean contextAndApiKeySet(String str) {
        if (this.context == null) {
            AmplitudeLog amplitudeLog = logger;
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("context cannot be null, set context with initialize() before calling ");
            sb.append(str);
            amplitudeLog.mo9078e(str2, sb.toString());
            return false;
        } else if (!C0532Utils.isEmptyString(this.apiKey)) {
            return true;
        } else {
            AmplitudeLog amplitudeLog2 = logger;
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("apiKey cannot be null or empty, set apiKey with initialize() before calling ");
            sb2.append(str);
            amplitudeLog2.mo9078e(str3, sb2.toString());
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public String bytesToHexString(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] cArr2 = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & Pdu.MANUFACTURER_DATA_PDU_TYPE;
            int i2 = i * 2;
            cArr2[i2] = cArr[b >>> 4];
            cArr2[i2 + 1] = cArr[b & 15];
        }
        return new String(cArr2);
    }

    static boolean upgradePrefs(Context context2) {
        return upgradePrefs(context2, null, null);
    }

    static boolean upgradePrefs(Context context2, String str, String str2) {
        if (str == null) {
            str = "com.amplitude.api";
            try {
                str = Constants.class.getPackage().getName();
            } catch (Exception unused) {
            }
        }
        if (str2 == null) {
            str2 = "com.amplitude.api";
        }
        try {
            if (str2.equals(str)) {
                return false;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(".");
            sb.append(context2.getPackageName());
            String sb2 = sb.toString();
            SharedPreferences sharedPreferences = context2.getSharedPreferences(sb2, 0);
            if (sharedPreferences.getAll().size() == 0) {
                return false;
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2);
            sb3.append(".");
            sb3.append(context2.getPackageName());
            String sb4 = sb3.toString();
            Editor edit = context2.getSharedPreferences(sb4, 0).edit();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append(".previousSessionId");
            if (sharedPreferences.contains(sb5.toString())) {
                String str3 = Constants.PREFKEY_PREVIOUS_SESSION_ID;
                StringBuilder sb6 = new StringBuilder();
                sb6.append(str);
                sb6.append(".previousSessionId");
                edit.putLong(str3, sharedPreferences.getLong(sb6.toString(), -1));
            }
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str);
            sb7.append(".deviceId");
            if (sharedPreferences.contains(sb7.toString())) {
                String str4 = Constants.PREFKEY_DEVICE_ID;
                StringBuilder sb8 = new StringBuilder();
                sb8.append(str);
                sb8.append(".deviceId");
                edit.putString(str4, sharedPreferences.getString(sb8.toString(), null));
            }
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str);
            sb9.append(".userId");
            if (sharedPreferences.contains(sb9.toString())) {
                String str5 = Constants.PREFKEY_USER_ID;
                StringBuilder sb10 = new StringBuilder();
                sb10.append(str);
                sb10.append(".userId");
                edit.putString(str5, sharedPreferences.getString(sb10.toString(), null));
            }
            StringBuilder sb11 = new StringBuilder();
            sb11.append(str);
            sb11.append(".optOut");
            if (sharedPreferences.contains(sb11.toString())) {
                String str6 = Constants.PREFKEY_OPT_OUT;
                StringBuilder sb12 = new StringBuilder();
                sb12.append(str);
                sb12.append(".optOut");
                edit.putBoolean(str6, sharedPreferences.getBoolean(sb12.toString(), false));
            }
            edit.apply();
            sharedPreferences.edit().clear().apply();
            AmplitudeLog amplitudeLog = logger;
            String str7 = TAG;
            StringBuilder sb13 = new StringBuilder();
            sb13.append("Upgraded shared preferences from ");
            sb13.append(sb2);
            sb13.append(" to ");
            sb13.append(sb4);
            amplitudeLog.mo9081i(str7, sb13.toString());
            return true;
        } catch (Exception e) {
            logger.mo9079e(TAG, "Error upgrading shared preferences", e);
            return false;
        }
    }

    static boolean upgradeSharedPrefsToDB(Context context2) {
        return upgradeSharedPrefsToDB(context2, null);
    }

    static boolean upgradeSharedPrefsToDB(Context context2, String str) {
        if (str == null) {
            str = "com.amplitude.api";
        }
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper(context2);
        String value = databaseHelper.getValue(DEVICE_ID_KEY);
        Long longValue = databaseHelper.getLongValue(PREVIOUS_SESSION_ID_KEY);
        Long longValue2 = databaseHelper.getLongValue(LAST_EVENT_TIME_KEY);
        if (!C0532Utils.isEmptyString(value) && longValue != null && longValue2 != null) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(".");
        sb.append(context2.getPackageName());
        SharedPreferences sharedPreferences = context2.getSharedPreferences(sb.toString(), 0);
        migrateStringValue(sharedPreferences, Constants.PREFKEY_DEVICE_ID, null, databaseHelper, DEVICE_ID_KEY);
        SharedPreferences sharedPreferences2 = sharedPreferences;
        DatabaseHelper databaseHelper2 = databaseHelper;
        migrateLongValue(sharedPreferences2, Constants.PREFKEY_LAST_EVENT_TIME, -1, databaseHelper2, LAST_EVENT_TIME_KEY);
        migrateLongValue(sharedPreferences2, Constants.PREFKEY_LAST_EVENT_ID, -1, databaseHelper2, LAST_EVENT_ID_KEY);
        migrateLongValue(sharedPreferences2, Constants.PREFKEY_LAST_IDENTIFY_ID, -1, databaseHelper2, LAST_IDENTIFY_ID_KEY);
        migrateLongValue(sharedPreferences2, Constants.PREFKEY_PREVIOUS_SESSION_ID, -1, databaseHelper2, PREVIOUS_SESSION_ID_KEY);
        migrateStringValue(sharedPreferences, Constants.PREFKEY_USER_ID, null, databaseHelper, USER_ID_KEY);
        migrateBooleanValue(sharedPreferences, Constants.PREFKEY_OPT_OUT, false, databaseHelper, OPT_OUT_KEY);
        return true;
    }

    private static void migrateLongValue(SharedPreferences sharedPreferences, String str, long j, DatabaseHelper databaseHelper, String str2) {
        if (databaseHelper.getLongValue(str2) == null) {
            databaseHelper.insertOrReplaceKeyLongValue(str2, Long.valueOf(sharedPreferences.getLong(str, j)));
            sharedPreferences.edit().remove(str).apply();
        }
    }

    private static void migrateStringValue(SharedPreferences sharedPreferences, String str, String str2, DatabaseHelper databaseHelper, String str3) {
        if (C0532Utils.isEmptyString(databaseHelper.getValue(str3))) {
            String string = sharedPreferences.getString(str, str2);
            if (!C0532Utils.isEmptyString(string)) {
                databaseHelper.insertOrReplaceKeyValue(str3, string);
                sharedPreferences.edit().remove(str).apply();
            }
        }
    }

    private static void migrateBooleanValue(SharedPreferences sharedPreferences, String str, boolean z, DatabaseHelper databaseHelper, String str2) {
        if (databaseHelper.getLongValue(str2) == null) {
            databaseHelper.insertOrReplaceKeyLongValue(str2, Long.valueOf(sharedPreferences.getBoolean(str, z) ? 1 : 0));
            sharedPreferences.edit().remove(str).apply();
        }
    }

    /* access modifiers changed from: protected */
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
