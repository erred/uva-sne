package com.opengarden.firechat.matrixsdk;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.amplitude.api.AmplitudeClient;
import com.facebook.react.bridge.BaseJavaModule;
import com.google.gson.JsonObject;
import com.opengarden.firechat.BuildConfig;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoConfig;
import com.opengarden.firechat.matrixsdk.data.DataRetriever;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.data.comparator.RoomComparatorWithTag;
import com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore;
import com.opengarden.firechat.matrixsdk.data.cryptostore.MXFileCryptoStore;
import com.opengarden.firechat.matrixsdk.data.metrics.MetricsListener;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.data.store.MXStoreListener;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.p007db.MXLatestChatMessageCache;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.AccountDataRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.CallRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.CryptoRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.FilterRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.GroupsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.LoginRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.PresenceRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ProfileRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.PushRulesRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.PushersRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.RoomsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ThirdPidRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomParams;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterResponse;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.message.MediaMessage;
import com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DevicesListResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import com.opengarden.firechat.matrixsdk.sync.EventsThread;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.ContentManager;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import com.opengarden.firechat.offlineMessaging.LocalConnectionManager;
import com.opengarden.firechat.offlineMessaging.OfflineMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.matrix.olm.OlmManager;

public class MXSession {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXSession";
    public static final String MATRIX_GROUP_IDENTIFIER_REGEX = "\\+[A-Z0-9=_\\-./]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?";
    public static final String MATRIX_MESSAGE_IDENTIFIER_REGEX = "\\$[A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?";
    public static final String MATRIX_ROOM_ALIAS_REGEX = "#[A-Z0-9._%#@=+-]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?";
    public static final String MATRIX_ROOM_IDENTIFIER_REGEX = "![A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?";
    public static final String MATRIX_USER_IDENTIFIER_REGEX = "@[A-Z0-9\\x21-\\x39\\x3B-\\x7F]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?";
    public static final Pattern PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ALIAS = Pattern.compile("https:\\/\\/[A-Z0-9.-]+\\.[A-Z]{2,}\\/[A-Z]{3,}\\/#\\/room\\/#[A-Z0-9._%#@=+-]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?\\/\\$[A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?", 2);
    public static final Pattern PATTERN_CONTAIN_APP_LINK_PERMALINK_ROOM_ID = Pattern.compile("https:\\/\\/[A-Z0-9.-]+\\.[A-Z]{2,}\\/[A-Z]{3,}\\/#\\/room\\/![A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?\\/\\$[A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?", 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_ALIAS = Pattern.compile(MATRIX_ROOM_ALIAS_REGEX, 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_GROUP_IDENTIFIER = Pattern.compile(MATRIX_GROUP_IDENTIFIER_REGEX, 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_MESSAGE_IDENTIFIER = Pattern.compile(MATRIX_MESSAGE_IDENTIFIER_REGEX, 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_ROOM_IDENTIFIER = Pattern.compile(MATRIX_ROOM_IDENTIFIER_REGEX, 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ALIAS = Pattern.compile("https:\\/\\/matrix\\.to\\/#\\/#[A-Z0-9._%#@=+-]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?\\/\\$[A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?", 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_TO_PERMALINK_ROOM_ID = Pattern.compile("https:\\/\\/matrix\\.to\\/#\\/![A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?\\/\\$[A-Z0-9]+:[A-Z0-9.-]+(\\.[A-Z]{2,})?+(\\:[0-9]{2,})?", 2);
    public static final Pattern PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER = Pattern.compile(MATRIX_USER_IDENTIFIER_REGEX, 2);
    public static OlmManager mOlmManager = new OlmManager();
    @Nullable
    private static MXCryptoConfig sCryptoConfig;
    private final AccountDataRestClient mAccountDataRestClient;
    /* access modifiers changed from: private */
    public Context mAppContent;
    private BingRulesManager mBingRulesManager;
    private final CallRestClient mCallRestClient;
    public MXCallsManager mCallsManager;
    private ContentManager mContentManager;
    /* access modifiers changed from: private */
    public final Credentials mCredentials;
    /* access modifiers changed from: private */
    public MXCrypto mCrypto;
    private final CryptoRestClient mCryptoRestClient;
    private final CryptoRestClient mCustomCryptoRestClient;
    private RoomsRestClient mCustomRoomRestClient;
    /* access modifiers changed from: private */
    public MXDataHandler mDataHandler;
    private DataRetriever mDataRetriever;
    /* access modifiers changed from: private */
    public boolean mEnableCryptoWhenStartingMXSession;
    private EventsRestClient mEventsRestClient;
    private EventsThread mEventsThread;
    private ApiFailureCallback mFailureCallback;
    private String mFilterOrFilterId;
    private final FilterRestClient mFilterRestClient;
    private GroupsManager mGroupsManager;
    private final GroupsRestClient mGroupsRestClient;
    /* access modifiers changed from: private */
    public final HomeServerConnectionConfig mHsConfig;
    /* access modifiers changed from: private */
    public boolean mIsAliveSession;
    private boolean mIsBgCatchupPending;
    private boolean mIsOnline;
    private MXLatestChatMessageCache mLatestChatMessageCache;
    private final LoginRestClient mLoginRestClient;
    private final MediaScanRestClient mMediaScanRestClient;
    private MXMediasCache mMediasCache;
    /* access modifiers changed from: private */
    public MetricsListener mMetricsListener;
    private NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private PresenceRestClient mPresenceRestClient;
    private ProfileRestClient mProfileRestClient;
    private final PushRulesRestClient mPushRulesRestClient;
    /* access modifiers changed from: private */
    public PushersRestClient mPushersRestClient;
    private List<String> mReceivedOfflineMessages;
    private RoomsRestClient mRoomsRestClient;
    private int mSyncDelay;
    private List<String> mSyncRecipients;
    private int mSyncTimeout;
    private final ThirdPidRestClient mThirdPidRestClient;
    private UnsentEventsManager mUnsentEventsManager;
    private boolean mUseDataSaveMode;

    public static class Builder {
        private MXSession mxSession;

        public Builder(HomeServerConnectionConfig homeServerConnectionConfig, MXDataHandler mXDataHandler, Context context) {
            this.mxSession = new MXSession(homeServerConnectionConfig, mXDataHandler, context);
        }

        /* JADX WARNING: Removed duplicated region for block: B:8:0x0042  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.opengarden.firechat.matrixsdk.MXSession.Builder withPushServerUrl(@android.support.annotation.Nullable java.lang.String r4) {
            /*
                r3 = this;
                boolean r0 = android.text.TextUtils.isEmpty(r4)
                if (r0 != 0) goto L_0x003f
                com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r0 = new com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig     // Catch:{ Exception -> 0x0022 }
                android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0022 }
                r0.<init>(r4)     // Catch:{ Exception -> 0x0022 }
                com.opengarden.firechat.matrixsdk.MXSession r4 = r3.mxSession     // Catch:{ Exception -> 0x0022 }
                com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r4 = r4.mHsConfig     // Catch:{ Exception -> 0x0022 }
                com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r4 = r4.getCredentials()     // Catch:{ Exception -> 0x0022 }
                r0.setCredentials(r4)     // Catch:{ Exception -> 0x0022 }
                com.opengarden.firechat.matrixsdk.rest.client.PushersRestClient r4 = new com.opengarden.firechat.matrixsdk.rest.client.PushersRestClient     // Catch:{ Exception -> 0x0022 }
                r4.<init>(r0)     // Catch:{ Exception -> 0x0022 }
                goto L_0x0040
            L_0x0022:
                r4 = move-exception
                java.lang.String r0 = com.opengarden.firechat.matrixsdk.MXSession.LOG_TAG
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "## withPushServerUrl() failed "
                r1.append(r2)
                java.lang.String r2 = r4.getMessage()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                com.opengarden.firechat.matrixsdk.util.Log.m212e(r0, r1, r4)
            L_0x003f:
                r4 = 0
            L_0x0040:
                if (r4 == 0) goto L_0x0047
                com.opengarden.firechat.matrixsdk.MXSession r0 = r3.mxSession
                r0.mPushersRestClient = r4
            L_0x0047:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.MXSession.Builder.withPushServerUrl(java.lang.String):com.opengarden.firechat.matrixsdk.MXSession$Builder");
        }

        public Builder withMetricsListener(@Nullable MetricsListener metricsListener) {
            this.mxSession.mMetricsListener = metricsListener;
            return this;
        }

        public MXSession build() {
            return this.mxSession;
        }
    }

    public List<String> getSyncRecipients() {
        return this.mSyncRecipients;
    }

    public void addSyncRecipients(String str) {
        if (!this.mSyncRecipients.contains(str)) {
            this.mSyncRecipients.add(str);
        }
    }

    private MXSession(HomeServerConnectionConfig homeServerConnectionConfig) {
        this.mBingRulesManager = null;
        this.mIsAliveSession = true;
        this.mIsOnline = false;
        this.mSyncTimeout = 0;
        this.mSyncDelay = 0;
        this.mIsBgCatchupPending = false;
        this.mReceivedOfflineMessages = new ArrayList();
        this.mSyncRecipients = new ArrayList();
        this.mEnableCryptoWhenStartingMXSession = false;
        this.mCredentials = homeServerConnectionConfig.getCredentials();
        this.mHsConfig = homeServerConnectionConfig;
        this.mEventsRestClient = new EventsRestClient(homeServerConnectionConfig);
        this.mProfileRestClient = new ProfileRestClient(homeServerConnectionConfig);
        this.mPresenceRestClient = new PresenceRestClient(homeServerConnectionConfig);
        this.mRoomsRestClient = new RoomsRestClient(homeServerConnectionConfig);
        this.mCustomRoomRestClient = new RoomsRestClient(homeServerConnectionConfig, Boolean.valueOf(false));
        this.mPushRulesRestClient = new PushRulesRestClient(homeServerConnectionConfig);
        this.mPushersRestClient = new PushersRestClient(homeServerConnectionConfig);
        this.mThirdPidRestClient = new ThirdPidRestClient(homeServerConnectionConfig);
        this.mCallRestClient = new CallRestClient(homeServerConnectionConfig);
        this.mAccountDataRestClient = new AccountDataRestClient(homeServerConnectionConfig);
        this.mCryptoRestClient = new CryptoRestClient(homeServerConnectionConfig);
        this.mCustomCryptoRestClient = new CryptoRestClient(homeServerConnectionConfig, Boolean.valueOf(false));
        this.mLoginRestClient = new LoginRestClient(homeServerConnectionConfig);
        this.mGroupsRestClient = new GroupsRestClient(homeServerConnectionConfig);
        this.mMediaScanRestClient = new MediaScanRestClient(homeServerConnectionConfig);
        this.mFilterRestClient = new FilterRestClient(homeServerConnectionConfig);
    }

    private MXSession(HomeServerConnectionConfig homeServerConnectionConfig, MXDataHandler mXDataHandler, Context context) {
        this(homeServerConnectionConfig);
        this.mDataHandler = mXDataHandler;
        this.mDataHandler.getStore().addMXStoreListener(new MXStoreListener() {
            public void onStoreReady(String str) {
                Log.m209d(MXSession.LOG_TAG, "## onStoreReady()");
                MXSession.this.getDataHandler().onStoreReady();
            }

            public void onStoreCorrupted(String str, String str2) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onStoreCorrupted() : token ");
                sb.append(MXSession.this.getDataHandler().getStore().getEventStreamToken());
                Log.m209d(access$000, sb.toString());
                if (MXSession.this.getDataHandler().getStore().getEventStreamToken() == null) {
                    MXSession.this.getDataHandler().onStoreReady();
                }
            }

            public void postProcess(String str) {
                MXSession.this.getDataHandler().checkPermanentStorageData();
                if (MXSession.this.mCrypto == null) {
                    MXFileCryptoStore mXFileCryptoStore = new MXFileCryptoStore();
                    mXFileCryptoStore.initWithCredentials(MXSession.this.mAppContent, MXSession.this.mCredentials);
                    if (mXFileCryptoStore.hasData() || MXSession.this.mEnableCryptoWhenStartingMXSession) {
                        String access$000 = MXSession.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## postProcess() : create the crypto instance for session ");
                        sb.append(this);
                        Log.m209d(access$000, sb.toString());
                        MXSession.this.checkCrypto();
                        return;
                    }
                    Log.m211e(MXSession.LOG_TAG, "## postProcess() : no crypto data");
                    return;
                }
                Log.m211e(MXSession.LOG_TAG, "## postProcess() : mCrypto is already created");
            }

            public void onReadReceiptsLoaded(String str) {
                List<ReceiptData> eventReceipts = MXSession.this.mDataHandler.getStore().getEventReceipts(str, null, false, false);
                ArrayList arrayList = new ArrayList();
                for (ReceiptData receiptData : eventReceipts) {
                    arrayList.add(receiptData.userId);
                }
                MXSession.this.mDataHandler.onReceiptEvent(str, arrayList);
            }
        });
        this.mDataRetriever = new DataRetriever();
        this.mDataRetriever.setRoomsRestClient(this.mRoomsRestClient);
        this.mDataRetriever.setRooomCustomRestClient(this.mCustomRoomRestClient);
        this.mDataHandler.setDataRetriever(this.mDataRetriever);
        this.mDataHandler.setProfileRestClient(this.mProfileRestClient);
        this.mDataHandler.setPresenceRestClient(this.mPresenceRestClient);
        this.mDataHandler.setThirdPidRestClient(this.mThirdPidRestClient);
        this.mDataHandler.setRoomsRestClient(this.mRoomsRestClient);
        this.mDataHandler.setCustomRoomsRestClient(this.mCustomRoomRestClient);
        this.mDataHandler.setEventsRestClient(this.mEventsRestClient);
        this.mDataHandler.setAccountDataRestClient(this.mAccountDataRestClient);
        this.mAppContent = context;
        this.mNetworkConnectivityReceiver = new NetworkConnectivityReceiver();
        this.mNetworkConnectivityReceiver.checkNetworkConnection(context);
        this.mDataHandler.setNetworkConnectivityReceiver(this.mNetworkConnectivityReceiver);
        this.mAppContent.registerReceiver(this.mNetworkConnectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.mBingRulesManager = new BingRulesManager(this, this.mNetworkConnectivityReceiver);
        this.mDataHandler.setPushRulesManager(this.mBingRulesManager);
        this.mUnsentEventsManager = new UnsentEventsManager(this.mNetworkConnectivityReceiver, this.mDataHandler);
        this.mContentManager = new ContentManager(homeServerConnectionConfig, this.mUnsentEventsManager);
        this.mCallsManager = new MXCallsManager(this, this.mAppContent);
        this.mDataHandler.setCallsManager(this.mCallsManager);
        this.mEventsRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mProfileRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mPresenceRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mRoomsRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mCustomRoomRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mCustomCryptoRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mPushRulesRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mThirdPidRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mCallRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mAccountDataRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mCryptoRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mLoginRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mGroupsRestClient.setUnsentEventsManager(this.mUnsentEventsManager);
        this.mLatestChatMessageCache = new MXLatestChatMessageCache(this.mCredentials.userId);
        this.mMediasCache = new MXMediasCache(this.mContentManager, this.mNetworkConnectivityReceiver, this.mCredentials.userId, context);
        this.mDataHandler.setMediasCache(this.mMediasCache);
        this.mMediaScanRestClient.setMxStore(this.mDataHandler.getStore());
        this.mMediasCache.setMediaScanRestClient(this.mMediaScanRestClient);
        this.mGroupsManager = new GroupsManager(this.mDataHandler, this.mGroupsRestClient);
        this.mDataHandler.setGroupsManager(this.mGroupsManager);
    }

    private void checkIfAlive() {
        synchronized (this) {
            if (!this.mIsAliveSession) {
                Log.m212e(LOG_TAG, "Use of a released session", new Exception("Use of a released session"));
            }
        }
    }

    public static void initUserAgent(Context context) {
        RestClient.initUserAgent(context);
    }

    public String getVersion(boolean z) {
        checkIfAlive();
        return BuildConfig.VERSION_NAME;
    }

    public String getCryptoVersion(Context context, boolean z) {
        String str = "";
        if (mOlmManager == null) {
            return str;
        }
        return z ? mOlmManager.getDetailedVersion(context) : mOlmManager.getVersion();
    }

    public MXDataHandler getDataHandler() {
        checkIfAlive();
        return this.mDataHandler;
    }

    public Credentials getCredentials() {
        checkIfAlive();
        return this.mCredentials;
    }

    public EventsRestClient getEventsApiClient() {
        checkIfAlive();
        return this.mEventsRestClient;
    }

    public ProfileRestClient getProfileApiClient() {
        checkIfAlive();
        return this.mProfileRestClient;
    }

    public PresenceRestClient getPresenceApiClient() {
        checkIfAlive();
        return this.mPresenceRestClient;
    }

    public FilterRestClient getFilterRestClient() {
        checkIfAlive();
        return this.mFilterRestClient;
    }

    public void refreshUserPresence(final String str, final ApiCallback<Void> apiCallback) {
        this.mPresenceRestClient.getPresence(str, new SimpleApiCallback<User>(apiCallback) {
            public void onSuccess(User user) {
                User user2 = MXSession.this.mDataHandler.getStore().getUser(str);
                if (user2 != null) {
                    user2.presence = user.presence;
                    user2.currently_active = user.currently_active;
                    user2.lastActiveAgo = user.lastActiveAgo;
                    user = user2;
                }
                user.setLatestPresenceTs(System.currentTimeMillis());
                MXSession.this.mDataHandler.getStore().storeUser(user);
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }
        });
    }

    public PushRulesRestClient getBingRulesApiClient() {
        checkIfAlive();
        return this.mPushRulesRestClient;
    }

    public ThirdPidRestClient getThirdPidRestClient() {
        checkIfAlive();
        return this.mThirdPidRestClient;
    }

    public CallRestClient getCallRestClient() {
        checkIfAlive();
        return this.mCallRestClient;
    }

    public PushersRestClient getPushersRestClient() {
        checkIfAlive();
        return this.mPushersRestClient;
    }

    public CryptoRestClient getCryptoRestClient() {
        checkIfAlive();
        return this.mCryptoRestClient;
    }

    public HomeServerConnectionConfig getHomeServerConfig() {
        checkIfAlive();
        return this.mHsConfig;
    }

    public RoomsRestClient getRoomsApiClient() {
        checkIfAlive();
        return this.mRoomsRestClient;
    }

    public RoomsRestClient getCustomRoomsApiClient() {
        checkIfAlive();
        return this.mCustomRoomRestClient;
    }

    public MediaScanRestClient getMediaScanRestClient() {
        checkIfAlive();
        return this.mMediaScanRestClient;
    }

    /* access modifiers changed from: protected */
    public void setEventsApiClient(EventsRestClient eventsRestClient) {
        checkIfAlive();
        this.mEventsRestClient = eventsRestClient;
    }

    /* access modifiers changed from: protected */
    public void setProfileApiClient(ProfileRestClient profileRestClient) {
        checkIfAlive();
        this.mProfileRestClient = profileRestClient;
    }

    /* access modifiers changed from: protected */
    public void setPresenceApiClient(PresenceRestClient presenceRestClient) {
        checkIfAlive();
        this.mPresenceRestClient = presenceRestClient;
    }

    /* access modifiers changed from: protected */
    public void setRoomsApiClient(RoomsRestClient roomsRestClient) {
        checkIfAlive();
        this.mRoomsRestClient = roomsRestClient;
    }

    public MXLatestChatMessageCache getLatestChatMessageCache() {
        checkIfAlive();
        return this.mLatestChatMessageCache;
    }

    public MXMediasCache getMediasCache() {
        checkIfAlive();
        return this.mMediasCache;
    }

    public static void getApplicationSizeCaches(final Context context, final ApiCallback<Long> apiCallback) {
        C19363 r0 = new AsyncTask<Void, Void, Long>() {
            /* access modifiers changed from: protected */
            public Long doInBackground(Void... voidArr) {
                return Long.valueOf(ContentUtils.getDirectorySize(context, context.getApplicationContext().getFilesDir().getParentFile(), 5));
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Long l) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getCacheSize() : ");
                sb.append(l);
                Log.m209d(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onSuccess(l);
                }
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getApplicationSizeCaches() : failed ");
            sb.append(e.getMessage());
            Log.m212e(str, sb.toString(), e);
            r0.cancel(true);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(e);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void clearApplicationCaches(Context context) {
        this.mDataHandler.clear();
        try {
            this.mAppContent.unregisterReceiver(this.mNetworkConnectivityReceiver);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## clearApplicationCaches() : unregisterReceiver failed ");
            sb.append(e.getMessage());
            Log.m212e(str, sb.toString(), e);
        }
        this.mNetworkConnectivityReceiver.removeListeners();
        this.mUnsentEventsManager.clear();
        this.mLatestChatMessageCache.clearCache(context);
        this.mMediasCache.clear();
        if (this.mCrypto != null) {
            this.mCrypto.close();
        }
    }

    public void clear(Context context) {
        clear(context, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0015, code lost:
        if (r6 != null) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0017, code lost:
        clearApplicationCaches(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        r1 = new com.opengarden.firechat.matrixsdk.MXSession.C19385(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r1.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, new java.lang.Void[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0029, code lost:
        r0 = LOG_TAG;
        r2 = new java.lang.StringBuilder();
        r2.append("## clear() failed ");
        r2.append(r5.getMessage());
        com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r2.toString());
        r1.cancel(true);
        new android.os.Handler(android.os.Looper.getMainLooper()).post(new com.opengarden.firechat.matrixsdk.MXSession.C19396(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        stopEventStream();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void clear(final android.content.Context r5, final com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.mIsAliveSession     // Catch:{ all -> 0x0059 }
            if (r0 != 0) goto L_0x000e
            java.lang.String r5 = LOG_TAG     // Catch:{ all -> 0x0059 }
            java.lang.String r6 = "## clear() was already called"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)     // Catch:{ all -> 0x0059 }
            monitor-exit(r4)     // Catch:{ all -> 0x0059 }
            return
        L_0x000e:
            r0 = 0
            r4.mIsAliveSession = r0     // Catch:{ all -> 0x0059 }
            monitor-exit(r4)     // Catch:{ all -> 0x0059 }
            r4.stopEventStream()
            if (r6 != 0) goto L_0x001b
            r4.clearApplicationCaches(r5)
            goto L_0x0058
        L_0x001b:
            com.opengarden.firechat.matrixsdk.MXSession$5 r1 = new com.opengarden.firechat.matrixsdk.MXSession$5
            r1.<init>(r5, r6)
            java.util.concurrent.Executor r5 = android.os.AsyncTask.THREAD_POOL_EXECUTOR     // Catch:{ Exception -> 0x0028 }
            java.lang.Void[] r0 = new java.lang.Void[r0]     // Catch:{ Exception -> 0x0028 }
            r1.executeOnExecutor(r5, r0)     // Catch:{ Exception -> 0x0028 }
            goto L_0x0058
        L_0x0028:
            r5 = move-exception
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## clear() failed "
            r2.append(r3)
            java.lang.String r3 = r5.getMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r2)
            r0 = 1
            r1.cancel(r0)
            android.os.Handler r0 = new android.os.Handler
            android.os.Looper r1 = android.os.Looper.getMainLooper()
            r0.<init>(r1)
            com.opengarden.firechat.matrixsdk.MXSession$6 r1 = new com.opengarden.firechat.matrixsdk.MXSession$6
            r1.<init>(r6, r5)
            r0.post(r1)
        L_0x0058:
            return
        L_0x0059:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0059 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.MXSession.clear(android.content.Context, com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback):void");
    }

    public void removeMediasBefore(Context context, long j) {
        final HashSet hashSet = new HashSet();
        IMXStore store = getDataHandler().getStore();
        for (Room roomId : store.getRooms()) {
            Collection<Event> roomMessages = store.getRoomMessages(roomId.getRoomId());
            if (roomMessages != null) {
                for (Event event : roomMessages) {
                    Object obj = null;
                    try {
                        if (TextUtils.equals(Event.EVENT_TYPE_MESSAGE, event.getType())) {
                            obj = JsonUtils.toMessage(event.getContent());
                        } else if (TextUtils.equals(Event.EVENT_TYPE_STICKER, event.getType())) {
                            obj = JsonUtils.toStickerMessage(event.getContent());
                        }
                        if (obj != null && (obj instanceof MediaMessage)) {
                            MediaMessage mediaMessage = (MediaMessage) obj;
                            if (mediaMessage.isThumbnailLocalContent()) {
                                hashSet.add(Uri.parse(mediaMessage.getThumbnailUrl()).getPath());
                            }
                            if (mediaMessage.isLocalContent()) {
                                hashSet.add(Uri.parse(mediaMessage.getUrl()).getPath());
                            }
                        }
                    } catch (Exception e) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## removeMediasBefore() : failed ");
                        sb.append(e.getMessage());
                        Log.m212e(str, sb.toString(), e);
                    }
                }
            }
        }
        final long j2 = j;
        final Context context2 = context;
        C19407 r0 = new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                long removeMediasBefore = MXSession.this.getMediasCache().removeMediasBefore(j2, hashSet);
                File logDirectory = Log.getLogDirectory();
                if (logDirectory != null) {
                    File[] listFiles = logDirectory.listFiles();
                    if (listFiles != null) {
                        for (File file : listFiles) {
                            if (ContentUtils.getLastAccessTime(file) < j2) {
                                long length = removeMediasBefore + file.length();
                                file.delete();
                                removeMediasBefore = length;
                            }
                        }
                    }
                }
                if (0 != removeMediasBefore) {
                    String access$000 = MXSession.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## removeMediasBefore() : save ");
                    sb.append(Formatter.formatFileSize(context2, removeMediasBefore));
                    Log.m209d(access$000, sb.toString());
                } else {
                    Log.m209d(MXSession.LOG_TAG, "## removeMediasBefore() : useless");
                }
                return null;
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## removeMediasBefore() : failed ");
            sb2.append(e2.getMessage());
            Log.m211e(str2, sb2.toString());
            r0.cancel(true);
        }
    }

    public boolean isAlive() {
        boolean z;
        synchronized (this) {
            z = this.mIsAliveSession;
        }
        return z;
    }

    public ContentManager getContentManager() {
        checkIfAlive();
        return this.mContentManager;
    }

    public MyUser getMyUser() {
        checkIfAlive();
        return this.mDataHandler.getMyUser();
    }

    public String getMyUserId() {
        checkIfAlive();
        if (this.mDataHandler.getMyUser() != null) {
            return this.mDataHandler.getMyUser().user_id;
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c9, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startEventStream(com.opengarden.firechat.matrixsdk.sync.EventsThreadListener r5, com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r6, java.lang.String r7) {
        /*
            r4 = this;
            r4.checkIfAlive()
            java.lang.String r0 = LOG_TAG
            monitor-enter(r0)
            com.opengarden.firechat.matrixsdk.sync.EventsThread r1 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            if (r1 == 0) goto L_0x002b
            com.opengarden.firechat.matrixsdk.sync.EventsThread r1 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            boolean r1 = r1.isAlive()     // Catch:{ all -> 0x00ca }
            if (r1 != 0) goto L_0x001d
            r1 = 0
            r4.mEventsThread = r1     // Catch:{ all -> 0x00ca }
            java.lang.String r1 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = "startEventStream() : create a new EventsThread"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)     // Catch:{ all -> 0x00ca }
            goto L_0x002b
        L_0x001d:
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            r5.cancelKill()     // Catch:{ all -> 0x00ca }
            java.lang.String r5 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.String r6 = "Ignoring startEventStream() : Thread already created."
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)     // Catch:{ all -> 0x00ca }
            monitor-exit(r0)     // Catch:{ all -> 0x00ca }
            return
        L_0x002b:
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r4.mDataHandler     // Catch:{ all -> 0x00ca }
            if (r1 != 0) goto L_0x0038
            java.lang.String r5 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.String r6 = "Error starting the event stream: No data handler is defined"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r6)     // Catch:{ all -> 0x00ca }
            monitor-exit(r0)     // Catch:{ all -> 0x00ca }
            return
        L_0x0038:
            java.lang.String r1 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = "startEventStream : create the event stream"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)     // Catch:{ all -> 0x00ca }
            if (r5 != 0) goto L_0x0048
            com.opengarden.firechat.matrixsdk.sync.DefaultEventsThreadListener r5 = new com.opengarden.firechat.matrixsdk.sync.DefaultEventsThreadListener     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r4.mDataHandler     // Catch:{ all -> 0x00ca }
            r5.<init>(r1)     // Catch:{ all -> 0x00ca }
        L_0x0048:
            com.opengarden.firechat.matrixsdk.sync.EventsThread r1 = new com.opengarden.firechat.matrixsdk.sync.EventsThread     // Catch:{ all -> 0x00ca }
            android.content.Context r2 = r4.mAppContent     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient r3 = r4.mEventsRestClient     // Catch:{ all -> 0x00ca }
            r1.<init>(r2, r3, r5, r7)     // Catch:{ all -> 0x00ca }
            r4.mEventsThread = r1     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            java.lang.String r7 = r4.mFilterOrFilterId     // Catch:{ all -> 0x00ca }
            r5.setFilterOrFilterId(r7)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.data.metrics.MetricsListener r7 = r4.mMetricsListener     // Catch:{ all -> 0x00ca }
            r5.setMetricsListener(r7)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            r5.setNetworkConnectivityReceiver(r6)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            boolean r6 = r4.mIsOnline     // Catch:{ all -> 0x00ca }
            r5.setIsOnline(r6)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            int r6 = r4.mSyncTimeout     // Catch:{ all -> 0x00ca }
            r5.setServerLongPollTimeout(r6)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            int r6 = r4.mSyncDelay     // Catch:{ all -> 0x00ca }
            r5.setSyncDelay(r6)     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback r5 = r4.mFailureCallback     // Catch:{ all -> 0x00ca }
            if (r5 == 0) goto L_0x0086
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback r6 = r4.mFailureCallback     // Catch:{ all -> 0x00ca }
            r5.setFailureCallback(r6)     // Catch:{ all -> 0x00ca }
        L_0x0086:
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r5 = r4.mCredentials     // Catch:{ all -> 0x00ca }
            java.lang.String r5 = r5.accessToken     // Catch:{ all -> 0x00ca }
            if (r5 == 0) goto L_0x00c8
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            boolean r5 = r5.isAlive()     // Catch:{ all -> 0x00ca }
            if (r5 != 0) goto L_0x00c8
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ Exception -> 0x009a }
            r5.start()     // Catch:{ Exception -> 0x009a }
            goto L_0x00b5
        L_0x009a:
            r5 = move-exception
            java.lang.String r6 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r7.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r1 = "## startEventStream() :  mEventsThread.start failed "
            r7.append(r1)     // Catch:{ all -> 0x00ca }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x00ca }
            r7.append(r5)     // Catch:{ all -> 0x00ca }
            java.lang.String r5 = r7.toString()     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)     // Catch:{ all -> 0x00ca }
        L_0x00b5:
            boolean r5 = r4.mIsBgCatchupPending     // Catch:{ all -> 0x00ca }
            if (r5 == 0) goto L_0x00c8
            java.lang.String r5 = LOG_TAG     // Catch:{ all -> 0x00ca }
            java.lang.String r6 = "startEventStream : start a catchup"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r6)     // Catch:{ all -> 0x00ca }
            r5 = 0
            r4.mIsBgCatchupPending = r5     // Catch:{ all -> 0x00ca }
            com.opengarden.firechat.matrixsdk.sync.EventsThread r5 = r4.mEventsThread     // Catch:{ all -> 0x00ca }
            r5.catchup()     // Catch:{ all -> 0x00ca }
        L_0x00c8:
            monitor-exit(r0)     // Catch:{ all -> 0x00ca }
            return
        L_0x00ca:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ca }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.MXSession.startEventStream(com.opengarden.firechat.matrixsdk.sync.EventsThreadListener, com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver, java.lang.String):void");
    }

    public void refreshToken() {
        checkIfAlive();
        this.mProfileRestClient.refreshTokens(new ApiCallback<Credentials>() {
            public void onSuccess(Credentials credentials) {
                Log.m209d(MXSession.LOG_TAG, "refreshToken : succeeds.");
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("refreshToken : onNetworkError ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("refreshToken : onMatrixError ");
                sb.append(matrixError.getMessage());
                Log.m209d(access$000, sb.toString());
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("refreshToken : onMatrixError ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
            }
        });
    }

    public void setIsOnline(boolean z) {
        if (z != this.mIsOnline) {
            this.mIsOnline = z;
            if (this.mEventsThread != null) {
                this.mEventsThread.setIsOnline(z);
            }
        }
    }

    public boolean isOnline() {
        return this.mIsOnline;
    }

    public void setSyncTimeout(int i) {
        this.mSyncTimeout = i;
        if (this.mEventsThread != null) {
            this.mEventsThread.setServerLongPollTimeout(i);
        }
    }

    public int getSyncTimeout() {
        return this.mSyncTimeout;
    }

    public void setSyncDelay(int i) {
        this.mSyncDelay = i;
        if (this.mEventsThread != null) {
            this.mEventsThread.setSyncDelay(i);
        }
    }

    public int getSyncDelay() {
        return this.mSyncDelay;
    }

    @Deprecated
    public void setUseDataSaveMode(boolean z) {
        if (z) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Enable DataSyncMode # ");
            sb.append(FilterBody.getDataSaveModeFilterBody());
            Log.m209d(str, sb.toString());
            setSyncFilterOrFilterId(FilterBody.getDataSaveModeFilterBody().toJSONString());
            this.mFilterRestClient.uploadFilter(getMyUserId(), FilterBody.getDataSaveModeFilterBody(), new SimpleApiCallback<FilterResponse>() {
                public void onSuccess(FilterResponse filterResponse) {
                    MXSession.this.setSyncFilterOrFilterId(filterResponse.filterId);
                }
            });
            return;
        }
        Log.m209d(LOG_TAG, "Disable DataSyncMode");
        setSyncFilterOrFilterId(null);
    }

    public synchronized void setSyncFilterOrFilterId(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setSyncFilterOrFilterId ## ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        this.mFilterOrFilterId = str;
        if (this.mEventsThread != null) {
            this.mEventsThread.setFilterOrFilterId(str);
        }
    }

    public void refreshNetworkConnection() {
        if (this.mNetworkConnectivityReceiver != null) {
            this.mNetworkConnectivityReceiver.checkNetworkConnection(this.mAppContent);
        }
    }

    public void startEventStream(String str) {
        checkIfAlive();
        startEventStream(null, this.mNetworkConnectivityReceiver, str);
    }

    public void stopEventStream() {
        if (this.mCallsManager != null) {
            this.mCallsManager.stopTurnServerRefresh();
        }
        if (this.mEventsThread != null) {
            Log.m209d(LOG_TAG, "stopEventStream");
            this.mEventsThread.kill();
            this.mEventsThread = null;
            return;
        }
        Log.m211e(LOG_TAG, "stopEventStream : mEventsThread is already null");
    }

    public void pauseEventStream() {
        checkIfAlive();
        if (this.mCallsManager != null) {
            this.mCallsManager.pauseTurnServerRefresh();
        }
        if (this.mEventsThread != null) {
            Log.m209d(LOG_TAG, "pauseEventStream");
            this.mEventsThread.pause();
        } else {
            Log.m211e(LOG_TAG, "pauseEventStream : mEventsThread is null");
        }
        if (getMediasCache() != null) {
            getMediasCache().clearTmpDecryptedMediaCache();
        }
        if (this.mGroupsManager != null) {
            this.mGroupsManager.onSessionPaused();
        }
    }

    public String getCurrentSyncToken() {
        if (this.mEventsThread != null) {
            return this.mEventsThread.getCurrentSyncToken();
        }
        return null;
    }

    public void resumeEventStream() {
        checkIfAlive();
        if (this.mNetworkConnectivityReceiver != null) {
            this.mNetworkConnectivityReceiver.checkNetworkConnection(this.mAppContent);
        }
        if (this.mCallsManager != null) {
            this.mCallsManager.unpauseTurnServerRefresh();
        }
        if (this.mEventsThread != null) {
            Log.m209d(LOG_TAG, "## resumeEventStream() : unpause");
            this.mEventsThread.unpause();
        } else {
            Log.m211e(LOG_TAG, "resumeEventStream : mEventsThread is null");
        }
        if (this.mIsBgCatchupPending) {
            this.mIsBgCatchupPending = false;
            Log.m209d(LOG_TAG, "## resumeEventStream() : cancel bg sync");
        }
        if (getMediasCache() != null) {
            getMediasCache().clearShareDecryptedMediaCache();
        }
        if (this.mGroupsManager != null) {
            this.mGroupsManager.onSessionResumed();
        }
    }

    public void catchupEventStream() {
        checkIfAlive();
        if (this.mEventsThread != null) {
            Log.m209d(LOG_TAG, "catchupEventStream");
            this.mEventsThread.catchup();
            return;
        }
        Log.m211e(LOG_TAG, "catchupEventStream : mEventsThread is null so catchup when the thread will be created");
        this.mIsBgCatchupPending = true;
    }

    public void setFailureCallback(ApiFailureCallback apiFailureCallback) {
        checkIfAlive();
        this.mFailureCallback = apiFailureCallback;
        if (this.mEventsThread != null) {
            this.mEventsThread.setFailureCallback(apiFailureCallback);
        }
    }

    public void createRoom(ApiCallback<String> apiCallback) {
        createRoom(null, null, null, apiCallback);
    }

    public void createRoom(String str, String str2, String str3, ApiCallback<String> apiCallback) {
        createRoom(str, str2, RoomState.DIRECTORY_VISIBILITY_PRIVATE, str3, RoomState.GUEST_ACCESS_CAN_JOIN, null, apiCallback);
    }

    public void createRoom(String str, String str2, String str3, String str4, String str5, String str6, ApiCallback<String> apiCallback) {
        checkIfAlive();
        CreateRoomParams createRoomParams = new CreateRoomParams();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        createRoomParams.name = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        createRoomParams.topic = str2;
        if (TextUtils.isEmpty(str3)) {
            str3 = null;
        }
        createRoomParams.visibility = str3;
        if (TextUtils.isEmpty(str4)) {
            str4 = null;
        }
        createRoomParams.roomAliasName = str4;
        if (TextUtils.isEmpty(str5)) {
            str5 = null;
        }
        createRoomParams.guest_access = str5;
        createRoomParams.addCryptoAlgorithm(str6);
        createRoom(createRoomParams, apiCallback);
    }

    public void createEncryptedRoom(String str, ApiCallback<String> apiCallback) {
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.addCryptoAlgorithm(str);
        createRoom(createRoomParams, apiCallback);
    }

    public boolean createDirectMessageRoom(String str, ApiCallback<String> apiCallback) {
        return createDirectMessageRoom(str, null, apiCallback);
    }

    public boolean createDirectMessageRoom(String str, String str2, ApiCallback<String> apiCallback) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.addCryptoAlgorithm(str2);
        createRoomParams.setDirectMessage();
        createRoomParams.addParticipantIds(this.mHsConfig, Arrays.asList(new String[]{str}));
        createRoom(createRoomParams, apiCallback);
        return true;
    }

    /* access modifiers changed from: private */
    public void finalizeDMRoomCreation(final String str, String str2, final ApiCallback<String> apiCallback) {
        toggleDirectChatRoom(str, str2, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room room = MXSession.this.getDataHandler().getRoom(str);
                if (room != null) {
                    room.markAllAsRead(null);
                }
                if (apiCallback != null) {
                    apiCallback.onSuccess(str);
                }
            }
        });
    }

    public void createRoom(final CreateRoomParams createRoomParams, final ApiCallback<String> apiCallback) {
        this.mRoomsRestClient.createRoom(createRoomParams, new SimpleApiCallback<CreateRoomResponse>(apiCallback) {
            public void onSuccess(CreateRoomResponse createRoomResponse) {
                final String str = createRoomResponse.roomId;
                final Room room = MXSession.this.mDataHandler.getRoom(str);
                if (room.isWaitingInitialSync()) {
                    room.setOnInitialSyncCallback(new SimpleApiCallback<Void>(apiCallback) {
                        public void onSuccess(Void voidR) {
                            room.markAllAsRead(null);
                            if (createRoomParams.isDirect()) {
                                MXSession.this.finalizeDMRoomCreation(str, createRoomParams.getFirstInvitedUserId(), apiCallback);
                            } else {
                                apiCallback.onSuccess(str);
                            }
                        }
                    });
                    return;
                }
                room.markAllAsRead(null);
                if (createRoomParams.isDirect()) {
                    MXSession.this.finalizeDMRoomCreation(str, createRoomParams.getFirstInvitedUserId(), apiCallback);
                } else {
                    apiCallback.onSuccess(str);
                }
            }
        });
    }

    public void joinRoom(String str, final ApiCallback<String> apiCallback) {
        checkIfAlive();
        if (this.mDataHandler != null && str != null) {
            this.mDataRetriever.getRoomsRestClient().joinRoom(str, new SimpleApiCallback<RoomResponse>(apiCallback) {
                public void onSuccess(RoomResponse roomResponse) {
                    final String str = roomResponse.roomId;
                    Room room = MXSession.this.mDataHandler.getRoom(str);
                    if (room.isWaitingInitialSync()) {
                        room.setOnInitialSyncCallback(new SimpleApiCallback<Void>(apiCallback) {
                            public void onSuccess(Void voidR) {
                                apiCallback.onSuccess(str);
                            }
                        });
                        return;
                    }
                    room.markAllAsRead(null);
                    apiCallback.onSuccess(str);
                }
            });
        }
    }

    public void markRoomsAsRead(Collection<Room> collection, final ApiCallback<Void> apiCallback) {
        if (collection == null || collection.size() == 0) {
            if (apiCallback != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(null);
                    }
                });
            }
            return;
        }
        markRoomsAsRead(collection.iterator(), apiCallback);
    }

    /* access modifiers changed from: private */
    public void markRoomsAsRead(final Iterator it, final ApiCallback<Void> apiCallback) {
        if (it.hasNext()) {
            Room room = (Room) it.next();
            boolean z = false;
            if (this.mNetworkConnectivityReceiver.isConnected()) {
                z = room.markAllAsRead(new SimpleApiCallback<Void>(apiCallback) {
                    public void onSuccess(Void voidR) {
                        MXSession.this.markRoomsAsRead(it, apiCallback);
                    }
                });
            } else {
                room.sendReadReceipt();
            }
            if (!z) {
                markRoomsAsRead(it, apiCallback);
            }
        } else if (apiCallback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(null);
                }
            });
        }
    }

    public void lookup3Pid(String str, String str2, ApiCallback<String> apiCallback) {
        checkIfAlive();
        this.mThirdPidRestClient.lookup3Pid(str, str2, apiCallback);
    }

    public void lookup3Pids(List<String> list, List<String> list2, ApiCallback<List<String>> apiCallback) {
        checkIfAlive();
        this.mThirdPidRestClient.lookup3Pids(list, list2, apiCallback);
    }

    public void searchMessageText(String str, List<String> list, int i, int i2, String str2, ApiCallback<SearchResponse> apiCallback) {
        checkIfAlive();
        if (apiCallback != null) {
            this.mEventsRestClient.searchMessagesByText(str, list, i, i2, str2, apiCallback);
        }
    }

    public void searchMessagesByText(String str, List<String> list, String str2, ApiCallback<SearchResponse> apiCallback) {
        checkIfAlive();
        if (apiCallback != null) {
            this.mEventsRestClient.searchMessagesByText(str, list, 0, 0, str2, apiCallback);
        }
    }

    public void searchMessagesByText(String str, String str2, ApiCallback<SearchResponse> apiCallback) {
        checkIfAlive();
        if (apiCallback != null) {
            this.mEventsRestClient.searchMessagesByText(str, null, 0, 0, str2, apiCallback);
        }
    }

    public void cancelSearchMessagesByText() {
        checkIfAlive();
        this.mEventsRestClient.cancelSearchMessagesByText();
    }

    public void searchMediasByName(String str, List<String> list, String str2, ApiCallback<SearchResponse> apiCallback) {
        checkIfAlive();
        if (apiCallback != null) {
            this.mEventsRestClient.searchMediasByText(str, list, 0, 0, str2, apiCallback);
        }
    }

    public void cancelSearchMediasByText() {
        checkIfAlive();
        this.mEventsRestClient.cancelSearchMediasByText();
    }

    public void searchUsers(String str, Integer num, Set<String> set, ApiCallback<SearchUsersResponse> apiCallback) {
        checkIfAlive();
        if (apiCallback != null) {
            this.mEventsRestClient.searchUsers(str, num, set, apiCallback);
        }
    }

    public void cancelUsersSearch() {
        checkIfAlive();
        this.mEventsRestClient.cancelUsersSearch();
    }

    public BingRule fulfillRule(Event event) {
        checkIfAlive();
        return this.mBingRulesManager.fulfilledBingRule(event);
    }

    public boolean isVoipCallSupported() {
        if (this.mCallsManager != null) {
            return this.mCallsManager.isSupported();
        }
        return false;
    }

    public List<Room> roomsWithTag(String str) {
        ArrayList arrayList = new ArrayList();
        if (this.mDataHandler.getStore() == null) {
            return arrayList;
        }
        if (!TextUtils.equals(str, RoomTag.ROOM_TAG_NO_TAG)) {
            for (Room room : this.mDataHandler.getStore().getRooms()) {
                if (room.getAccountData().roomTag(str) != null) {
                    arrayList.add(room);
                }
            }
            if (arrayList.size() > 0) {
                Collections.sort(arrayList, new RoomComparatorWithTag(str));
            }
        } else {
            for (Room room2 : this.mDataHandler.getStore().getRooms()) {
                if (!room2.getAccountData().hasTags()) {
                    arrayList.add(room2);
                }
            }
        }
        return arrayList;
    }

    public List<String> roomIdsWithTag(String str) {
        List<Room> roomsWithTag = roomsWithTag(str);
        ArrayList arrayList = new ArrayList();
        for (Room roomId : roomsWithTag) {
            arrayList.add(roomId.getRoomId());
        }
        return arrayList;
    }

    public Double tagOrderToBeAtIndex(int i, int i2, String str) {
        Double valueOf = Double.valueOf(0.0d);
        Double valueOf2 = Double.valueOf(1.0d);
        List roomsWithTag = roomsWithTag(str);
        if (roomsWithTag.size() > 0) {
            if (i2 != Integer.MAX_VALUE && i2 < i) {
                i++;
            }
            if (i > 0) {
                RoomTag roomTag = ((Room) roomsWithTag.get((i < roomsWithTag.size() ? i : roomsWithTag.size()) - 1)).getAccountData().roomTag(str);
                if (roomTag.mOrder == null) {
                    Log.m211e(LOG_TAG, "computeTagOrderForRoom: Previous room in sublist has no ordering metadata. This should never happen.");
                } else {
                    valueOf = roomTag.mOrder;
                }
            }
            if (i <= roomsWithTag.size() - 1) {
                RoomTag roomTag2 = ((Room) roomsWithTag.get(i)).getAccountData().roomTag(str);
                if (roomTag2.mOrder == null) {
                    Log.m211e(LOG_TAG, "computeTagOrderForRoom: Next room in sublist has no ordering metadata. This should never happen.");
                } else {
                    valueOf2 = roomTag2.mOrder;
                }
            }
        }
        return Double.valueOf((valueOf.doubleValue() + valueOf2.doubleValue()) / 2.0d);
    }

    public void toggleDirectChatRoom(String str, String str2, ApiCallback<Void> apiCallback) {
        HashMap hashMap;
        IMXStore store = getDataHandler().getStore();
        Room room = store.getRoom(str);
        if (room != null) {
            if (store.getDirectChatRoomsDict() != null) {
                hashMap = new HashMap(store.getDirectChatRoomsDict());
            } else {
                hashMap = new HashMap();
            }
            if (!getDataHandler().getDirectChatRoomIdsList().contains(str)) {
                ArrayList arrayList = new ArrayList();
                RoomMember roomMember = null;
                if (str2 == null) {
                    ArrayList arrayList2 = new ArrayList(room.getActiveMembers());
                    if (!arrayList2.isEmpty()) {
                        int i = 1;
                        if (arrayList2.size() > 1) {
                            Collections.sort(arrayList2, new Comparator<RoomMember>() {
                                public int compare(RoomMember roomMember, RoomMember roomMember2) {
                                    if (!RoomMember.MEMBERSHIP_JOIN.equals(roomMember2.membership) || !"invite".equals(roomMember.membership)) {
                                        if (!roomMember2.membership.equals(roomMember.membership)) {
                                            return -1;
                                        }
                                        long originServerTs = roomMember.getOriginServerTs() - roomMember2.getOriginServerTs();
                                        if (0 == originServerTs) {
                                            return 0;
                                        }
                                        if (originServerTs <= 0) {
                                            return -1;
                                        }
                                    }
                                    return 1;
                                }
                            });
                            if (!TextUtils.equals(((RoomMember) arrayList2.get(0)).getUserId(), getMyUserId())) {
                                if (RoomMember.MEMBERSHIP_JOIN.equals(((RoomMember) arrayList2.get(0)).membership)) {
                                    roomMember = (RoomMember) arrayList2.get(0);
                                }
                                i = 0;
                            } else if (RoomMember.MEMBERSHIP_JOIN.equals(((RoomMember) arrayList2.get(1)).membership)) {
                                roomMember = (RoomMember) arrayList2.get(1);
                            }
                            if (roomMember == null && "invite".equals(((RoomMember) arrayList2.get(i)).membership)) {
                                roomMember = (RoomMember) arrayList2.get(i);
                            }
                        }
                        if (roomMember == null) {
                            roomMember = (RoomMember) arrayList2.get(0);
                        }
                        str2 = roomMember.getUserId();
                    } else {
                        return;
                    }
                }
                if (hashMap.containsKey(str2)) {
                    arrayList = new ArrayList((Collection) hashMap.get(str2));
                }
                arrayList.add(str);
                hashMap.put(str2, arrayList);
            } else if (store.getDirectChatRoomsDict() != null) {
                for (String str3 : new ArrayList(hashMap.keySet())) {
                    List list = (List) hashMap.get(str3);
                    if (list.contains(str)) {
                        list.remove(str);
                        if (list.isEmpty()) {
                            hashMap.remove(str3);
                        }
                    }
                }
            } else {
                Log.m211e(LOG_TAG, "## toggleDirectChatRoom(): failed to remove a direct chat room (not seen as direct chat room)");
                return;
            }
            getDataHandler().setDirectChatRoomsMap(hashMap, apiCallback);
        }
    }

    public void updatePassword(String str, String str2, ApiCallback<Void> apiCallback) {
        this.mProfileRestClient.updatePassword(getMyUserId(), str, str2, apiCallback);
    }

    public void resetPassword(String str, Map<String, String> map, ApiCallback<Void> apiCallback) {
        this.mProfileRestClient.resetPassword(str, map, apiCallback);
    }

    private void updateUsers(ArrayList<String> arrayList, ApiCallback<Void> apiCallback) {
        HashMap hashMap = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            hashMap.put((String) it.next(), new HashMap());
        }
        HashMap hashMap2 = new HashMap();
        hashMap2.put(AccountDataRestClient.ACCOUNT_DATA_KEY_IGNORED_USERS, hashMap);
        this.mAccountDataRestClient.setAccountData(getMyUserId(), AccountDataRestClient.ACCOUNT_DATA_TYPE_IGNORED_USER_LIST, hashMap2, apiCallback);
    }

    public boolean isUserIgnored(String str) {
        boolean z = false;
        if (str == null) {
            return false;
        }
        if (getDataHandler().getIgnoredUserIds().indexOf(str) >= 0) {
            z = true;
        }
        return z;
    }

    public void ignoreUsers(ArrayList<String> arrayList, ApiCallback<Void> apiCallback) {
        List ignoredUserIds = getDataHandler().getIgnoredUserIds();
        ArrayList arrayList2 = new ArrayList(getDataHandler().getIgnoredUserIds());
        if (arrayList != null && arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str = (String) it.next();
                if (arrayList2.indexOf(str) < 0) {
                    arrayList2.add(str);
                }
            }
            if (ignoredUserIds.size() != arrayList2.size()) {
                updateUsers(arrayList2, apiCallback);
            }
        }
    }

    public void unIgnoreUsers(ArrayList<String> arrayList, ApiCallback<Void> apiCallback) {
        List ignoredUserIds = getDataHandler().getIgnoredUserIds();
        ArrayList arrayList2 = new ArrayList(getDataHandler().getIgnoredUserIds());
        if (arrayList != null && arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList2.remove((String) it.next());
            }
            if (ignoredUserIds.size() != arrayList2.size()) {
                updateUsers(arrayList2, apiCallback);
            }
        }
    }

    public NetworkConnectivityReceiver getNetworkConnectivityReceiver() {
        return this.mNetworkConnectivityReceiver;
    }

    public void logout(final Context context, final ApiCallback<Void> apiCallback) {
        synchronized (this) {
            if (!this.mIsAliveSession) {
                Log.m211e(LOG_TAG, "## logout() was already called");
                return;
            }
            this.mIsAliveSession = false;
            enableCrypto(false, null);
            this.mLoginRestClient.logout(new ApiCallback<JsonObject>() {
                private void clearData() {
                    MXSession.this.mIsAliveSession = true;
                    MXSession.this.clear(context, new SimpleApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            if (apiCallback != null) {
                                apiCallback.onSuccess(null);
                            }
                        }
                    });
                }

                public void onSuccess(JsonObject jsonObject) {
                    Log.m211e(MXSession.LOG_TAG, "## logout() : succeed -> clearing the application data ");
                    clearData();
                }

                private void onError(String str) {
                    String access$000 = MXSession.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## logout() : failed ");
                    sb.append(str);
                    Log.m211e(access$000, sb.toString());
                    clearData();
                }

                public void onNetworkError(Exception exc) {
                    onError(exc.getMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError(matrixError.getMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onError(exc.getMessage());
                }
            });
        }
    }

    public void deactivateAccount(final Context context, String str, String str2, boolean z, final ApiCallback<Void> apiCallback) {
        this.mProfileRestClient.deactivateAccount(str, getMyUserId(), str2, z, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Log.m209d(MXSession.LOG_TAG, "## deactivateAccount() : succeed -> clearing the application data ");
                MXSession.this.enableCrypto(false, null);
                MXSession.this.clear(context, new SimpleApiCallback<Void>(apiCallback) {
                    public void onSuccess(Void voidR) {
                        if (apiCallback != null) {
                            apiCallback.onSuccess(null);
                        }
                    }
                });
            }
        });
    }

    public void setURLPreviewStatus(final boolean z, final ApiCallback<Void> apiCallback) {
        HashMap hashMap = new HashMap();
        hashMap.put(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE, Boolean.valueOf(!z));
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setURLPreviewStatus() : status ");
        sb.append(z);
        Log.m209d(str, sb.toString());
        this.mAccountDataRestClient.setAccountData(getMyUserId(), AccountDataRestClient.ACCOUNT_DATA_TYPE_PREVIEW_URLS, hashMap, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(MXSession.LOG_TAG, "## setURLPreviewStatus() : succeeds");
                MXSession.this.getDataHandler().getStore().setURLPreviewEnabled(z);
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setURLPreviewStatus() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setURLPreviewStatus() : failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onMatrixError(matrixError);
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setURLPreviewStatus() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onUnexpectedError(exc);
            }
        });
    }

    public void addUserWidget(final Map<String, Object> map, final ApiCallback<Void> apiCallback) {
        Log.m209d(LOG_TAG, "## addUserWidget()");
        this.mAccountDataRestClient.setAccountData(getMyUserId(), "m.widgets", map, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(MXSession.LOG_TAG, "## addUserWidget() : succeeds");
                MXSession.this.getDataHandler().getStore().setUserWidgets(map);
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## addUserWidget() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## addUserWidget() : failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onMatrixError(matrixError);
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXSession.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## addUserWidget() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback.onUnexpectedError(exc);
            }
        });
    }

    public boolean isURLPreviewEnabled() {
        return getDataHandler().getStore().isURLPreviewEnabled();
    }

    public Map<String, Object> getUserWidgets() {
        return getDataHandler().getStore().getUserWidgets();
    }

    public MXCrypto getCrypto() {
        return this.mCrypto;
    }

    public boolean isCryptoEnabled() {
        return this.mCrypto != null;
    }

    public void enableCryptoWhenStarting() {
        this.mEnableCryptoWhenStartingMXSession = true;
    }

    public static void setCryptoConfig(@Nullable MXCryptoConfig mXCryptoConfig) {
        sCryptoConfig = mXCryptoConfig;
    }

    /* access modifiers changed from: private */
    public void decryptRoomSummaries() {
        if (getDataHandler().getStore() != null) {
            for (RoomSummary latestReceivedEvent : getDataHandler().getStore().getSummaries()) {
                this.mDataHandler.decryptEvent(latestReceivedEvent.getLatestReceivedEvent(), null);
            }
        }
    }

    public void checkCrypto() {
        MXFileCryptoStore mXFileCryptoStore = new MXFileCryptoStore();
        mXFileCryptoStore.initWithCredentials(this.mAppContent, this.mCredentials);
        if ((mXFileCryptoStore.hasData() || this.mEnableCryptoWhenStartingMXSession) && this.mCrypto == null) {
            boolean z = false;
            try {
                mXFileCryptoStore.open();
                z = true;
            } catch (UnsatisfiedLinkError e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## checkCrypto() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            if (!z) {
                mOlmManager = new OlmManager();
                try {
                    mXFileCryptoStore.open();
                    z = true;
                } catch (UnsatisfiedLinkError e2) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## checkCrypto() failed 2 ");
                    sb2.append(e2.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            if (!z) {
                Log.m211e(LOG_TAG, "## checkCrypto() : cannot enable the crypto because of olm lib");
                return;
            }
            this.mCrypto = new MXCrypto(this, mXFileCryptoStore, sCryptoConfig);
            this.mDataHandler.setCrypto(this.mCrypto);
            decryptRoomSummaries();
            Log.m209d(LOG_TAG, "## checkCrypto() : the crypto engine is ready");
        } else if (this.mDataHandler.getCrypto() != this.mCrypto) {
            Log.m211e(LOG_TAG, "## checkCrypto() : the data handler crypto was not initialized");
            this.mDataHandler.setCrypto(this.mCrypto);
        }
    }

    public void enableCrypto(boolean z, final ApiCallback<Void> apiCallback) {
        if (z != isCryptoEnabled()) {
            if (z) {
                Log.m209d(LOG_TAG, "Crypto is enabled");
                MXFileCryptoStore mXFileCryptoStore = new MXFileCryptoStore();
                mXFileCryptoStore.initWithCredentials(this.mAppContent, this.mCredentials);
                mXFileCryptoStore.open();
                this.mCrypto = new MXCrypto(this, mXFileCryptoStore, sCryptoConfig);
                this.mCrypto.start(true, new SimpleApiCallback<Void>(apiCallback) {
                    public void onSuccess(Void voidR) {
                        MXSession.this.decryptRoomSummaries();
                        if (apiCallback != null) {
                            apiCallback.onSuccess(null);
                        }
                    }
                });
            } else if (this.mCrypto != null) {
                Log.m209d(LOG_TAG, "Crypto is disabled");
                IMXCryptoStore iMXCryptoStore = this.mCrypto.mCryptoStore;
                this.mCrypto.close();
                iMXCryptoStore.deleteStore();
                this.mCrypto = null;
                this.mDataHandler.setCrypto(null);
                decryptRoomSummaries();
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }
            this.mDataHandler.setCrypto(this.mCrypto);
        } else if (apiCallback != null) {
            apiCallback.onSuccess(null);
        }
    }

    public void getDevicesList(ApiCallback<DevicesListResponse> apiCallback) {
        this.mCryptoRestClient.getDevices(apiCallback);
    }

    public void setDeviceName(String str, String str2, ApiCallback<Void> apiCallback) {
        this.mCryptoRestClient.setDeviceName(str, str2, apiCallback);
    }

    public void deleteDevice(String str, String str2, ApiCallback<Void> apiCallback) {
        CryptoRestClient cryptoRestClient = this.mCryptoRestClient;
        DeleteDeviceParams deleteDeviceParams = new DeleteDeviceParams();
        final ApiCallback<Void> apiCallback2 = apiCallback;
        final String str3 = str2;
        final String str4 = str;
        C193322 r2 = new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(null);
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:19:0x0075  */
            /* JADX WARNING: Removed duplicated region for block: B:24:0x008b  */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x00d1  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMatrixError(com.opengarden.firechat.matrixsdk.rest.model.MatrixError r6) {
                /*
                    r5 = this;
                    java.lang.String r0 = com.opengarden.firechat.matrixsdk.MXSession.LOG_TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "## deleteDevice() : onMatrixError "
                    r1.append(r2)
                    java.lang.String r2 = r6.getMessage()
                    r1.append(r2)
                    java.lang.String r1 = r1.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
                    java.lang.Integer r0 = r6.mStatus
                    if (r0 == 0) goto L_0x003b
                    java.lang.Integer r0 = r6.mStatus
                    int r0 = r0.intValue()
                    r1 = 401(0x191, float:5.62E-43)
                    if (r0 != r1) goto L_0x003b
                    java.lang.String r0 = r6.mErrorBodyAsString     // Catch:{ Exception -> 0x0031 }
                    com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse r0 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toRegistrationFlowResponse(r0)     // Catch:{ Exception -> 0x0031 }
                    goto L_0x0056
                L_0x0031:
                    java.lang.String r0 = com.opengarden.firechat.matrixsdk.MXSession.LOG_TAG
                    java.lang.String r1 = "## deleteDevice(): Received status 401 - Exception - JsonUtils.toRegistrationFlowResponse()"
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r1)
                    goto L_0x0055
                L_0x003b:
                    java.lang.String r0 = com.opengarden.firechat.matrixsdk.MXSession.LOG_TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "## deleteDevice(): Received not expected status 401 ="
                    r1.append(r2)
                    java.lang.Integer r2 = r6.mStatus
                    r1.append(r2)
                    java.lang.String r1 = r1.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
                L_0x0055:
                    r0 = 0
                L_0x0056:
                    java.util.ArrayList r1 = new java.util.ArrayList
                    r1.<init>()
                    if (r0 == 0) goto L_0x0085
                    java.util.List<com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow> r2 = r0.flows
                    if (r2 == 0) goto L_0x0085
                    java.util.List<com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow> r2 = r0.flows
                    boolean r2 = r2.isEmpty()
                    if (r2 != 0) goto L_0x0085
                    java.util.List<com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow> r2 = r0.flows
                    java.util.Iterator r2 = r2.iterator()
                L_0x006f:
                    boolean r3 = r2.hasNext()
                    if (r3 == 0) goto L_0x0085
                    java.lang.Object r3 = r2.next()
                    com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow r3 = (com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow) r3
                    java.util.List<java.lang.String> r4 = r3.stages
                    if (r4 == 0) goto L_0x006f
                    java.util.List<java.lang.String> r3 = r3.stages
                    r1.addAll(r3)
                    goto L_0x006f
                L_0x0085:
                    boolean r2 = r1.isEmpty()
                    if (r2 != 0) goto L_0x00d1
                    com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceParams r6 = new com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceParams
                    r6.<init>()
                    com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceAuth r2 = new com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceAuth
                    r2.<init>()
                    r6.auth = r2
                    com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceAuth r2 = r6.auth
                    java.lang.String r0 = r0.session
                    r2.session = r0
                    com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceAuth r0 = r6.auth
                    com.opengarden.firechat.matrixsdk.MXSession r2 = com.opengarden.firechat.matrixsdk.MXSession.this
                    com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r2 = r2.mCredentials
                    java.lang.String r2 = r2.userId
                    r0.user = r2
                    com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceAuth r0 = r6.auth
                    java.lang.String r2 = r6
                    r0.password = r2
                    java.lang.String r0 = com.opengarden.firechat.matrixsdk.MXSession.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## deleteDevice() : supported stages "
                    r2.append(r3)
                    r2.append(r1)
                    java.lang.String r2 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
                    com.opengarden.firechat.matrixsdk.MXSession r0 = com.opengarden.firechat.matrixsdk.MXSession.this
                    java.lang.String r2 = r7
                    com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r3 = r5
                    r0.deleteDevice(r2, r6, r1, r3)
                    goto L_0x00da
                L_0x00d1:
                    com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r0 = r5
                    if (r0 == 0) goto L_0x00da
                    com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r0 = r5
                    r0.onMatrixError(r6)
                L_0x00da:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.MXSession.C193322.onMatrixError(com.opengarden.firechat.matrixsdk.rest.model.MatrixError):void");
            }
        };
        cryptoRestClient.deleteDevice(str, deleteDeviceParams, r2);
    }

    /* access modifiers changed from: private */
    public void deleteDevice(String str, DeleteDeviceParams deleteDeviceParams, List<String> list, ApiCallback<Void> apiCallback) {
        deleteDeviceParams.auth.type = (String) list.get(0);
        list.remove(0);
        CryptoRestClient cryptoRestClient = this.mCryptoRestClient;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        final List<String> list2 = list;
        final String str2 = str;
        final DeleteDeviceParams deleteDeviceParams2 = deleteDeviceParams;
        C193423 r1 = new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(null);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (((matrixError.mStatus != null && matrixError.mStatus.intValue() == 401) || TextUtils.equals(matrixError.errcode, MatrixError.FORBIDDEN) || TextUtils.equals(matrixError.errcode, MatrixError.UNKNOWN)) && !list2.isEmpty()) {
                    MXSession.this.deleteDevice(str2, deleteDeviceParams2, list2, apiCallback2);
                } else if (apiCallback2 != null) {
                    apiCallback2.onMatrixError(matrixError);
                }
            }
        };
        cryptoRestClient.deleteDevice(str, deleteDeviceParams, r1);
    }

    public static boolean isUserId(String str) {
        return str != null && PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(str).matches();
    }

    public static boolean isRoomId(String str) {
        return str != null && PATTERN_CONTAIN_MATRIX_ROOM_IDENTIFIER.matcher(str).matches();
    }

    public static boolean isRoomAlias(String str) {
        return str != null && PATTERN_CONTAIN_MATRIX_ALIAS.matcher(str).matches();
    }

    public static boolean isMessageId(String str) {
        return str != null && PATTERN_CONTAIN_MATRIX_MESSAGE_IDENTIFIER.matcher(str).matches();
    }

    public static boolean isGroupId(String str) {
        return str != null && PATTERN_CONTAIN_MATRIX_GROUP_IDENTIFIER.matcher(str).matches();
    }

    public void openIdToken(ApiCallback<Map<Object, Object>> apiCallback) {
        this.mAccountDataRestClient.openIdToken(getMyUserId(), apiCallback);
    }

    public GroupsManager getGroupsManager() {
        return this.mGroupsManager;
    }

    public void handleReceivedOfflineMessage(OfflineMessage offlineMessage) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("received message offline id ==> ");
        sb.append(offlineMessage.getTransactionId());
        Log.m209d(str, sb.toString());
        Event event = offlineMessage.getEvent();
        if (!this.mSyncRecipients.contains(event.userId)) {
            addSyncRecipients(event.userId);
        }
        if (this.mReceivedOfflineMessages.contains(offlineMessage.getTransactionId())) {
            Log.m209d(LOG_TAG, "Already received this message.");
            return;
        }
        this.mReceivedOfflineMessages.add(offlineMessage.getTransactionId());
        Room room = getDataHandler().getRoom(getDataHandler().getStore(), offlineMessage.getRoomId(), false);
        if (room != null) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("message  ==> ");
            sb2.append(offlineMessage.getTransactionId());
            sb2.append(" is mine");
            Log.m209d(str2, sb2.toString());
            if (event.getType().equals(Event.EVENT_TYPE_ROOM_KEY)) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("message  ==> ");
                sb3.append(offlineMessage.getTransactionId());
                sb3.append(" is a toDevice event");
                Log.m209d(str3, sb3.toString());
                event.setType(Event.EVENT_TYPE_MESSAGE_ENCRYPTED);
                this.mDataHandler.handleOfflineToDevice(event, getMyUserId(), getCredentials().deviceId);
                if (room.getMembers().size() > 2) {
                    String str4 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("message  ==> ");
                    sb4.append(offlineMessage.getTransactionId());
                    sb4.append(" has other recipients, forwarding ...");
                    Log.m209d(str4, sb4.toString());
                    if (Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).isConnected()) {
                        String str5 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("message  ==> ");
                        sb5.append(offlineMessage.getTransactionId());
                        sb5.append(" is being sent to the server");
                        Log.m209d(str5, sb5.toString());
                        this.mCustomCryptoRestClient.forwardToDevice(event.type, event.getContentAsJsonObject(), event.eventId, offlineMessage.getAccessToken(), null);
                    }
                    LocalConnectionManager.sendToPeers(offlineMessage.getTransactionId(), event, offlineMessage.getAccessToken(), 0);
                }
            } else {
                String str6 = LOG_TAG;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("message  ==> ");
                sb6.append(offlineMessage.getTransactionId());
                sb6.append(" is a message event");
                Log.m209d(str6, sb6.toString());
                room.getLiveTimeLine().handleReceivedOfflineEvent(offlineMessage.getEvent());
                if (room.getMembers().size() > 2) {
                    String str7 = LOG_TAG;
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("message  ==> ");
                    sb7.append(offlineMessage.getTransactionId());
                    sb7.append(" has other recipients, forwarding ...");
                    Log.m209d(str7, sb7.toString());
                    if (Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).isConnected()) {
                        String str8 = LOG_TAG;
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("message  ==> ");
                        sb8.append(offlineMessage.getTransactionId());
                        sb8.append(" is being sent to the server");
                        Log.m209d(str8, sb8.toString());
                        this.mCustomRoomRestClient.forwardEvent(event.eventId, event.roomId, Event.EVENT_TYPE_MESSAGE_ENCRYPTED, offlineMessage.getAccessToken(), event.getContentAsJsonObject(), null);
                    }
                    LocalConnectionManager.sendToPeers(offlineMessage.getTransactionId(), event, offlineMessage.getAccessToken(), 1);
                }
            }
        } else {
            String str9 = LOG_TAG;
            StringBuilder sb9 = new StringBuilder();
            sb9.append("message  ==> ");
            sb9.append(offlineMessage.getTransactionId());
            sb9.append(" is not mine, forwarding...");
            Log.m209d(str9, sb9.toString());
            if (event.getType().equals(Event.EVENT_TYPE_ROOM_KEY)) {
                LocalConnectionManager.sendToPeers(offlineMessage.getTransactionId(), event, offlineMessage.getAccessToken(), 0);
                if (Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).isConnected()) {
                    event.type = Event.EVENT_TYPE_MESSAGE_ENCRYPTED;
                    this.mCustomCryptoRestClient.forwardToDevice(event.type, event.getContentAsJsonObject(), event.eventId, offlineMessage.getAccessToken(), null);
                }
            } else {
                LocalConnectionManager.sendToPeers(offlineMessage.getTransactionId(), event, offlineMessage.getAccessToken(), 1);
                if (Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).isConnected()) {
                    event.type = Event.EVENT_TYPE_MESSAGE_ENCRYPTED;
                    this.mCustomRoomRestClient.forwardEvent(event.eventId, event.roomId, Event.EVENT_TYPE_MESSAGE_ENCRYPTED, offlineMessage.getAccessToken(), event.getContentAsJsonObject(), new ApiCallback<Event>() {
                        public void onMatrixError(MatrixError matrixError) {
                        }

                        public void onNetworkError(Exception exc) {
                        }

                        public void onUnexpectedError(Exception exc) {
                        }

                        public void onSuccess(Event event) {
                            Log.m209d(MXSession.LOG_TAG, " event successfully forwarded to the server");
                        }
                    });
                }
            }
        }
    }

    public void handleReceivedOfflineSync(JsonObject jsonObject) {
        if (!getCredentials().userId.equals(jsonObject.get(AmplitudeClient.USER_ID_KEY).getAsString())) {
            sendSyncOffline(null, jsonObject, Boolean.valueOf(false));
        } else {
            SyncResponse syncResponse = (SyncResponse) JsonUtils.getGson(false).fromJson(jsonObject.get("sync_data").getAsJsonObject().toString(), SyncResponse.class);
            if (!(syncResponse.rooms == null || syncResponse.rooms.join == null || syncResponse.rooms.join.size() <= 0)) {
                for (String str : syncResponse.rooms.join.keySet()) {
                    try {
                        for (Event event : ((RoomSync) syncResponse.rooms.join.get(str)).timeline.events) {
                            if (event.type.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTED) && event.sender.equals(getCredentials().userId)) {
                                return;
                            }
                        }
                        continue;
                    } catch (Exception e) {
                        String str2 = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## manageResponse() : handleJoinedRoomSync failed ");
                        sb.append(e.getMessage());
                        sb.append(" for room ");
                        sb.append(str);
                        Log.m211e(str2, sb.toString());
                    }
                }
            }
            this.mDataHandler.onSyncResponse(syncResponse, syncResponse.nextBatch, false);
        }
    }

    public void addSentMessageToList(String str) {
        if (this.mReceivedOfflineMessages.contains(str)) {
            Log.m209d(LOG_TAG, "Already received this message.");
        } else {
            this.mReceivedOfflineMessages.add(str);
        }
    }

    public void sendSyncOffline(String str, JsonObject jsonObject, Boolean bool) {
        JsonObject jsonObject2 = new JsonObject();
        if (bool.booleanValue()) {
            StringBuilder sb = new StringBuilder();
            sb.append("$");
            sb.append(System.currentTimeMillis());
            sb.append(this.mCredentials.userId);
            jsonObject2.addProperty("event_id", sb.toString());
            jsonObject2.addProperty("type", BaseJavaModule.METHOD_TYPE_SYNC);
            jsonObject2.addProperty(AmplitudeClient.USER_ID_KEY, str);
            jsonObject2.add("sync_data", jsonObject);
            jsonObject = jsonObject2;
        }
        if (VectorApp.getInstance().offLineMessagePreference) {
            try {
                LocalConnectionManager.sendToPeers(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
