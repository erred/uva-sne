package com.opengarden.firechat.matrixsdk;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.MXDecryptionException;
import com.opengarden.firechat.matrixsdk.crypto.MXEventDecryptionResult;
import com.opengarden.firechat.matrixsdk.data.DataRetriever;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.metrics.MetricsListener;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.data.store.MXMemoryStore;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.AccountDataRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.PresenceRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ProfileRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.RoomsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ThirdPidRestClient;
import com.opengarden.firechat.matrixsdk.rest.json.ConditionDeserializer;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomAliasDescription;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.Condition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRuleSet;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRulesResponse;
import com.opengarden.firechat.matrixsdk.rest.model.group.InvitedGroupSync;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.sync.InvitedRoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils.MatrixFieldNamingStrategy;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.MXOsHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class MXDataHandler implements IMXEventListener {
    private static final String LEFT_ROOMS_FILTER = "{\"room\":{\"timeline\":{\"limit\":1},\"include_leave\":true}}";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXDataHandler";
    private AccountDataRestClient mAccountDataRestClient;
    /* access modifiers changed from: private */
    public boolean mAreLeftRoomsSynced;
    private BingRulesManager mBingRulesManager;
    private MXCallsManager mCallsManager;
    private final Credentials mCredentials;
    private MXCrypto mCrypto;
    private IMXEventListener mCryptoEventsListener = null;
    private RoomsRestClient mCustomRoomRestClient;
    private DataRetriever mDataRetriever;
    private final Set<IMXEventListener> mEventListeners = new HashSet();
    private EventsRestClient mEventsRestClient;
    private GroupsManager mGroupsManager;
    private List<String> mIgnoredUserIdsList;
    /* access modifiers changed from: private */
    public volatile String mInitialSyncToToken = null;
    private boolean mIsAlive = true;
    /* access modifiers changed from: private */
    public boolean mIsRetrievingLeftRooms;
    private boolean mIsStartingCryptoWithInitialSync = false;
    /* access modifiers changed from: private */
    public final ArrayList<ApiCallback<Void>> mLeftRoomsRefreshCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public final MXMemoryStore mLeftRoomsStore;
    private List<String> mLocalDirectChatRoomIdsList = null;
    private MXMediasCache mMediasCache;
    private MetricsListener mMetricsListener;
    private MyUser mMyUser;
    private NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private PresenceRestClient mPresenceRestClient;
    private ProfileRestClient mProfileRestClient;
    private RequestNetworkErrorListener mRequestNetworkErrorListener;
    @Nullable
    private MatrixError mResourceLimitExceededError;
    private RoomsRestClient mRoomsRestClient;
    private final IMXStore mStore;
    private final MXOsHandler mSyncHandler;
    private HandlerThread mSyncHandlerThread;
    private ThirdPidRestClient mThirdPidRestClient;
    private final MXOsHandler mUiHandler;
    private final Set<String> mUpdatedRoomIdList = new HashSet();

    public interface RequestNetworkErrorListener {
        void onConfigurationError(String str);

        void onSSLCertificateError(UnrecognizedCertificateException unrecognizedCertificateException);
    }

    private class RoomIdsListRetroCompat {
        final String mParticipantUserId;
        final String mRoomId;

        public RoomIdsListRetroCompat(String str, String str2) {
            this.mParticipantUserId = str;
            this.mRoomId = str2;
        }
    }

    public void onCryptoSyncComplete() {
    }

    public MXDataHandler(IMXStore iMXStore, Credentials credentials) {
        this.mStore = iMXStore;
        this.mCredentials = credentials;
        this.mUiHandler = new MXOsHandler(Looper.getMainLooper());
        StringBuilder sb = new StringBuilder();
        sb.append(LOG_TAG);
        sb.append(this.mCredentials.userId);
        this.mSyncHandlerThread = new HandlerThread(sb.toString(), 1);
        this.mSyncHandlerThread.start();
        this.mSyncHandler = new MXOsHandler(this.mSyncHandlerThread.getLooper());
        this.mLeftRoomsStore = new MXMemoryStore(credentials, iMXStore.getContext());
    }

    public void setRequestNetworkErrorListener(RequestNetworkErrorListener requestNetworkErrorListener) {
        this.mRequestNetworkErrorListener = requestNetworkErrorListener;
    }

    public void setMetricsListener(MetricsListener metricsListener) {
        this.mMetricsListener = metricsListener;
    }

    public Credentials getCredentials() {
        return this.mCredentials;
    }

    public void setProfileRestClient(ProfileRestClient profileRestClient) {
        this.mProfileRestClient = profileRestClient;
    }

    public ProfileRestClient getProfileRestClient() {
        return this.mProfileRestClient;
    }

    public void setPresenceRestClient(PresenceRestClient presenceRestClient) {
        this.mPresenceRestClient = presenceRestClient;
    }

    public PresenceRestClient getPresenceRestClient() {
        return this.mPresenceRestClient;
    }

    public void setThirdPidRestClient(ThirdPidRestClient thirdPidRestClient) {
        this.mThirdPidRestClient = thirdPidRestClient;
    }

    public ThirdPidRestClient getThirdPidRestClient() {
        return this.mThirdPidRestClient;
    }

    public void setRoomsRestClient(RoomsRestClient roomsRestClient) {
        this.mRoomsRestClient = roomsRestClient;
    }

    public void setCustomRoomsRestClient(RoomsRestClient roomsRestClient) {
        this.mCustomRoomRestClient = roomsRestClient;
    }

    public void setEventsRestClient(EventsRestClient eventsRestClient) {
        this.mEventsRestClient = eventsRestClient;
    }

    public void setAccountDataRestClient(AccountDataRestClient accountDataRestClient) {
        this.mAccountDataRestClient = accountDataRestClient;
    }

    public void setNetworkConnectivityReceiver(NetworkConnectivityReceiver networkConnectivityReceiver) {
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
        if (getCrypto() != null) {
            getCrypto().setNetworkConnectivityReceiver(this.mNetworkConnectivityReceiver);
        }
    }

    public void setGroupsManager(GroupsManager groupsManager) {
        this.mGroupsManager = groupsManager;
    }

    public MXCrypto getCrypto() {
        return this.mCrypto;
    }

    public void setCrypto(MXCrypto mXCrypto) {
        this.mCrypto = mXCrypto;
    }

    public boolean isCryptoEnabled() {
        return this.mCrypto != null;
    }

    public List<String> getIgnoredUserIds() {
        if (this.mIgnoredUserIdsList == null) {
            this.mIgnoredUserIdsList = this.mStore.getIgnoredUserIdsList();
        }
        if (this.mIgnoredUserIdsList == null) {
            this.mIgnoredUserIdsList = new ArrayList();
        }
        return this.mIgnoredUserIdsList;
    }

    private void checkIfAlive() {
        synchronized (this) {
            if (!this.mIsAlive) {
                Log.m211e(LOG_TAG, "use of a released dataHandler");
            }
        }
    }

    public boolean isAlive() {
        boolean z;
        synchronized (this) {
            z = this.mIsAlive;
        }
        return z;
    }

    public void onConfigurationError(String str) {
        if (this.mRequestNetworkErrorListener != null) {
            this.mRequestNetworkErrorListener.onConfigurationError(str);
        }
    }

    public void onSSLCertificateError(UnrecognizedCertificateException unrecognizedCertificateException) {
        if (this.mRequestNetworkErrorListener != null) {
            this.mRequestNetworkErrorListener.onSSLCertificateError(unrecognizedCertificateException);
        }
    }

    @Nullable
    public MatrixError getResourceLimitExceededError() {
        return this.mResourceLimitExceededError;
    }

    public MyUser getMyUser() {
        checkIfAlive();
        IMXStore store = getStore();
        if (this.mMyUser == null) {
            this.mMyUser = new MyUser(store.getUser(this.mCredentials.userId));
            this.mMyUser.setDataHandler(this);
            if (store.displayName() == null) {
                store.setAvatarURL(this.mMyUser.getAvatarUrl(), System.currentTimeMillis());
                store.setDisplayName(this.mMyUser.displayname, System.currentTimeMillis());
            } else {
                this.mMyUser.displayname = store.displayName();
                this.mMyUser.setAvatarUrl(store.avatarURL());
            }
            this.mMyUser.user_id = this.mCredentials.userId;
        } else if (store != null) {
            if (store.displayName() == null && this.mMyUser.displayname != null) {
                store.setAvatarURL(this.mMyUser.getAvatarUrl(), System.currentTimeMillis());
                store.setDisplayName(this.mMyUser.displayname, System.currentTimeMillis());
            } else if (!TextUtils.equals(this.mMyUser.displayname, store.displayName())) {
                this.mMyUser.displayname = store.displayName();
                this.mMyUser.setAvatarUrl(store.avatarURL());
            }
        }
        this.mMyUser.refreshUserInfos(null);
        return this.mMyUser;
    }

    public boolean isInitialSyncComplete() {
        checkIfAlive();
        return this.mInitialSyncToToken != null;
    }

    public DataRetriever getDataRetriever() {
        checkIfAlive();
        return this.mDataRetriever;
    }

    public void setDataRetriever(DataRetriever dataRetriever) {
        checkIfAlive();
        this.mDataRetriever = dataRetriever;
    }

    public void setPushRulesManager(BingRulesManager bingRulesManager) {
        if (isAlive()) {
            this.mBingRulesManager = bingRulesManager;
            this.mBingRulesManager.loadRules(new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    MXDataHandler.this.onBingRulesUpdate();
                }
            });
        }
    }

    public void setCallsManager(MXCallsManager mXCallsManager) {
        checkIfAlive();
        this.mCallsManager = mXCallsManager;
    }

    public MXCallsManager getCallsManager() {
        checkIfAlive();
        return this.mCallsManager;
    }

    public void setMediasCache(MXMediasCache mXMediasCache) {
        checkIfAlive();
        this.mMediasCache = mXMediasCache;
    }

    public MXMediasCache getMediasCache() {
        checkIfAlive();
        return this.mMediasCache;
    }

    public PushRuleSet pushRules() {
        if (!isAlive() || this.mBingRulesManager == null) {
            return null;
        }
        return this.mBingRulesManager.pushRules();
    }

    public void refreshPushRules() {
        if (isAlive() && this.mBingRulesManager != null) {
            this.mBingRulesManager.loadRules(new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    MXDataHandler.this.onBingRulesUpdate();
                }
            });
        }
    }

    public BingRulesManager getBingRulesManager() {
        checkIfAlive();
        return this.mBingRulesManager;
    }

    public void setCryptoEventsListener(IMXEventListener iMXEventListener) {
        this.mCryptoEventsListener = iMXEventListener;
    }

    public void addListener(IMXEventListener iMXEventListener) {
        if (isAlive() && iMXEventListener != null) {
            synchronized (this) {
                this.mEventListeners.add(iMXEventListener);
            }
            if (this.mInitialSyncToToken != null) {
                iMXEventListener.onInitialSyncComplete(this.mInitialSyncToToken);
            }
        }
    }

    public void removeListener(IMXEventListener iMXEventListener) {
        if (isAlive() && iMXEventListener != null) {
            synchronized (this) {
                this.mEventListeners.remove(iMXEventListener);
            }
        }
    }

    public void clear() {
        synchronized (this) {
            this.mIsAlive = false;
            this.mEventListeners.clear();
        }
        this.mStore.close();
        this.mStore.clear();
        if (this.mSyncHandlerThread != null) {
            this.mSyncHandlerThread.quit();
            this.mSyncHandlerThread = null;
        }
    }

    public String getUserId() {
        return isAlive() ? this.mCredentials.userId : "dummy";
    }

    /* access modifiers changed from: 0000 */
    public void checkPermanentStorageData() {
        if (!isAlive()) {
            Log.m211e(LOG_TAG, "checkPermanentStorageData : the session is not anymore active");
            return;
        }
        for (Room room : this.mStore.getRooms()) {
            room.init(this.mStore, room.getRoomId(), this);
        }
        for (RoomSummary roomSummary : this.mStore.getSummaries()) {
            if (roomSummary.getLatestRoomState() != null) {
                roomSummary.getLatestRoomState().setDataHandler(this);
            }
        }
    }

    public IMXStore getStore() {
        if (isAlive()) {
            return this.mStore;
        }
        Log.m211e(LOG_TAG, "getStore : the session is not anymore active");
        return null;
    }

    public IMXStore getStore(String str) {
        if (!isAlive()) {
            Log.m211e(LOG_TAG, "getStore : the session is not anymore active");
            return null;
        } else if (str == null) {
            return this.mStore;
        } else {
            if (this.mLeftRoomsStore.getRoom(str) != null) {
                return this.mLeftRoomsStore;
            }
            return this.mStore;
        }
    }

    public RoomMember getMember(Collection<RoomMember> collection, String str) {
        if (isAlive()) {
            for (RoomMember roomMember : collection) {
                if (TextUtils.equals(str, roomMember.getUserId())) {
                    return roomMember;
                }
            }
        } else {
            Log.m211e(LOG_TAG, "getMember : the session is not anymore active");
        }
        return null;
    }

    public boolean doesRoomExist(String str) {
        return (str == null || this.mStore.getRoom(str) == null) ? false : true;
    }

    public Collection<Room> getLeftRooms() {
        return new ArrayList(this.mLeftRoomsStore.getRooms());
    }

    public Room getRoom(String str) {
        return getRoom(str, true);
    }

    public Room getRoom(String str, boolean z) {
        return getRoom(this.mStore, str, z);
    }

    public Room getRoom(String str, boolean z, boolean z2) {
        if (str == null) {
            return null;
        }
        Room room = this.mStore.getRoom(str);
        if (room == null && z) {
            room = this.mLeftRoomsStore.getRoom(str);
        }
        return (room != null || !z2) ? room : getRoom(this.mStore, str, z2);
    }

    public Room getRoom(IMXStore iMXStore, String str, boolean z) {
        Room room;
        if (!isAlive()) {
            Log.m211e(LOG_TAG, "getRoom : the session is not anymore active");
            return null;
        } else if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            synchronized (this) {
                room = iMXStore.getRoom(str);
                if (room == null && z) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## getRoom() : create the room ");
                    sb.append(str);
                    Log.m209d(str2, sb.toString());
                    room = new Room();
                    room.init(iMXStore, str, this);
                    iMXStore.storeRoom(room);
                } else if (room != null && room.getDataHandler() == null) {
                    String str3 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("getRoom ");
                    sb2.append(str);
                    sb2.append(" was not initialized");
                    Log.m211e(str3, sb2.toString());
                    room.init(iMXStore, str, this);
                    iMXStore.storeRoom(room);
                }
            }
            return room;
        }
    }

    public void checkRoom(Room room) {
        if (room == null) {
            return;
        }
        if (room.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "checkRoom : the room was not initialized");
            room.init(this.mStore, room.getRoomId(), this);
        } else if (room.getLiveTimeLine() != null && room.getLiveTimeLine().mDataHandler == null) {
            Log.m211e(LOG_TAG, "checkRoom : the timeline was not initialized");
            room.init(this.mStore, room.getRoomId(), this);
        }
    }

    public Collection<RoomSummary> getSummaries(boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(getStore().getSummaries());
        if (z) {
            arrayList.addAll(this.mLeftRoomsStore.getSummaries());
        }
        return arrayList;
    }

    public void roomIdByAlias(String str, final ApiCallback<String> apiCallback) {
        Iterator it = getStore().getRooms().iterator();
        final String str2 = null;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Room room = (Room) it.next();
            if (!TextUtils.equals(room.getState().alias, str)) {
                Iterator it2 = room.getState().getAliases().iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (TextUtils.equals((String) it2.next(), str)) {
                            str2 = room.getRoomId();
                            continue;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (str2 != null) {
                    break;
                }
            } else {
                str2 = room.getRoomId();
                break;
            }
        }
        if (str2 != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(str2);
                }
            });
        } else {
            this.mRoomsRestClient.getRoomIdByAlias(str, new SimpleApiCallback<RoomAliasDescription>(apiCallback) {
                public void onSuccess(RoomAliasDescription roomAliasDescription) {
                    apiCallback.onSuccess(roomAliasDescription.room_id);
                }
            });
        }
    }

    public void deleteRoomEvent(Event event) {
        if (isAlive()) {
            Room room = getRoom(event.roomId);
            if (room != null) {
                this.mStore.deleteEvent(event);
                Event latestEvent = this.mStore.getLatestEvent(event.roomId);
                RoomState deepCopy = room.getState().deepCopy();
                RoomSummary summary = this.mStore.getSummary(event.roomId);
                if (summary == null) {
                    summary = new RoomSummary(null, latestEvent, deepCopy, this.mCredentials.userId);
                } else {
                    summary.setLatestReceivedEvent(latestEvent, deepCopy);
                }
                if (TextUtils.equals(summary.getReadReceiptEventId(), event.eventId)) {
                    summary.setReadReceiptEventId(latestEvent.eventId);
                }
                if (TextUtils.equals(summary.getReadMarkerEventId(), event.eventId)) {
                    summary.setReadMarkerEventId(latestEvent.eventId);
                }
                this.mStore.storeSummary(summary);
                return;
            }
            return;
        }
        Log.m211e(LOG_TAG, "deleteRoomEvent : the session is not anymore active");
    }

    public User getUser(String str) {
        if (!isAlive()) {
            Log.m211e(LOG_TAG, "getUser : the session is not anymore active");
            return null;
        }
        User user = this.mStore.getUser(str);
        if (user == null) {
            user = this.mLeftRoomsStore.getUser(str);
        }
        return user;
    }

    private void manageAccountData(Map<String, Object> map, boolean z) {
        try {
            if (map.containsKey("events")) {
                List list = (List) map.get("events");
                if (!list.isEmpty()) {
                    manageIgnoredUsers(list, z);
                    managePushRulesUpdate(list);
                    manageDirectChatRooms(list, z);
                    manageUrlPreview(list);
                    manageUserWidgets(list);
                }
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("manageAccountData failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    private void manageUserWidgets(List<Map<String, Object>> list) {
        if (list.size() != 0) {
            for (Map map : list) {
                if (TextUtils.equals((String) map.get("type"), "m.widgets") && map.containsKey("content")) {
                    Map map2 = (Map) map.get("content");
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## manageUserWidgets() : ");
                    sb.append(map2);
                    Log.m209d(str, sb.toString());
                    this.mStore.setUserWidgets(map2);
                }
            }
        }
    }

    private void managePushRulesUpdate(List<Map<String, Object>> list) {
        for (Map map : list) {
            if (TextUtils.equals((String) map.get("type"), "m.push_rules")) {
                if (map.containsKey("content")) {
                    Gson create = new GsonBuilder().setFieldNamingStrategy(new MatrixFieldNamingStrategy()).excludeFieldsWithModifiers(2, 8).registerTypeAdapter(Condition.class, new ConditionDeserializer()).create();
                    getBingRulesManager().buildRules((PushRulesResponse) create.fromJson(create.toJsonTree(map.get("content")), PushRulesResponse.class));
                    onBingRulesUpdate();
                }
                return;
            }
        }
    }

    private void manageIgnoredUsers(List<Map<String, Object>> list, boolean z) {
        List<String> ignoredUsers = ignoredUsers(list);
        if (ignoredUsers != null) {
            List ignoredUserIds = getIgnoredUserIds();
            if (ignoredUsers.size() != 0 || ignoredUserIds.size() != 0) {
                if (ignoredUsers.size() != ignoredUserIds.size() || !ignoredUsers.containsAll(ignoredUserIds)) {
                    this.mStore.setIgnoredUserIdsList(ignoredUsers);
                    this.mIgnoredUserIdsList = ignoredUsers;
                    if (!z) {
                        onIgnoredUsersListUpdate();
                    }
                }
            }
        }
    }

    private List<String> ignoredUsers(List<Map<String, Object>> list) {
        ArrayList arrayList = null;
        if (list.size() != 0) {
            for (Map map : list) {
                if (TextUtils.equals((String) map.get("type"), AccountDataRestClient.ACCOUNT_DATA_TYPE_IGNORED_USER_LIST) && map.containsKey("content")) {
                    Map map2 = (Map) map.get("content");
                    if (map2.containsKey(AccountDataRestClient.ACCOUNT_DATA_KEY_IGNORED_USERS)) {
                        Map map3 = (Map) map2.get(AccountDataRestClient.ACCOUNT_DATA_KEY_IGNORED_USERS);
                        if (map3 != null) {
                            arrayList = new ArrayList(map3.keySet());
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    private void manageDirectChatRooms(List<Map<String, Object>> list, boolean z) {
        if (list.size() != 0) {
            for (Map map : list) {
                if (TextUtils.equals((String) map.get("type"), AccountDataRestClient.ACCOUNT_DATA_TYPE_DIRECT_MESSAGES) && map.containsKey("content")) {
                    Map map2 = (Map) map.get("content");
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## manageDirectChatRooms() : update direct chats map");
                    sb.append(map2);
                    Log.m209d(str, sb.toString());
                    this.mStore.setDirectChatRoomsDict(map2);
                    this.mLocalDirectChatRoomIdsList = null;
                    if (!z) {
                        onDirectMessageChatRoomsListUpdate();
                    }
                }
            }
        }
    }

    private void manageUrlPreview(List<Map<String, Object>> list) {
        if (list.size() != 0) {
            for (Map map : list) {
                if (TextUtils.equals((String) map.get("type"), AccountDataRestClient.ACCOUNT_DATA_TYPE_PREVIEW_URLS) && map.containsKey("content")) {
                    Map map2 = (Map) map.get("content");
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## manageUrlPreview() : ");
                    sb.append(map2);
                    Log.m209d(str, sb.toString());
                    boolean z = true;
                    if (map2.containsKey(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE)) {
                        z = true ^ ((Boolean) map2.get(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE)).booleanValue();
                    }
                    this.mStore.setURLPreviewEnabled(z);
                }
            }
        }
    }

    private void handlePresenceEvent(Event event) {
        if (Event.EVENT_TYPE_PRESENCE.equals(event.getType())) {
            User user = JsonUtils.toUser(event.getContent());
            if (!TextUtils.isEmpty(event.getSender())) {
                user.user_id = event.getSender();
            }
            User user2 = this.mStore.getUser(user.user_id);
            if (user2 == null) {
                user.setDataHandler(this);
            } else {
                user2.currently_active = user.currently_active;
                user2.presence = user.presence;
                user2.lastActiveAgo = user.lastActiveAgo;
                user = user2;
            }
            user.setLatestPresenceTs(System.currentTimeMillis());
            if (this.mCredentials.userId.equals(user.user_id)) {
                getMyUser().displayname = user.displayname;
                getMyUser().avatar_url = user.getAvatarUrl();
                this.mStore.setAvatarURL(user.getAvatarUrl(), event.getOriginServerTs());
                this.mStore.setDisplayName(user.displayname, event.getOriginServerTs());
            }
            this.mStore.storeUser(user);
            onPresenceUpdate(event, user);
        }
    }

    public void onSyncResponse(final SyncResponse syncResponse, final String str, final boolean z) {
        this.mSyncHandler.post(new Runnable() {
            public void run() {
                MXDataHandler.this.manageResponse(syncResponse, str, z);
            }
        });
    }

    public void deleteRoom(String str) {
        Room room = getStore().getRoom(str);
        if (room != null) {
            if (this.mAreLeftRoomsSynced) {
                Room room2 = getRoom((IMXStore) this.mLeftRoomsStore, str, true);
                room2.setIsLeft(true);
                RoomSummary summary = getStore().getSummary(str);
                if (summary != null) {
                    this.mLeftRoomsStore.storeSummary(new RoomSummary(summary, summary.getLatestReceivedEvent(), summary.getLatestRoomState(), getUserId()));
                }
                ArrayList arrayList = new ArrayList();
                Collection<Event> roomMessages = getStore().getRoomMessages(str);
                if (roomMessages != null) {
                    for (Event event : roomMessages) {
                        arrayList.addAll(getStore().getEventReceipts(str, event.eventId, false, false));
                        this.mLeftRoomsStore.storeLiveRoomEvent(event);
                    }
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        this.mLeftRoomsStore.storeReceipt((ReceiptData) it.next(), str);
                    }
                }
                room2.getLiveTimeLine().setState(room.getLiveTimeLine().getState());
            }
            getStore().deleteRoom(str);
        }
    }

    /* access modifiers changed from: private */
    public void manageResponse(SyncResponse syncResponse, String str, boolean z) {
        String str2;
        boolean z2;
        String str3;
        ArrayList arrayList;
        HashMap hashMap;
        if (!isAlive()) {
            Log.m211e(LOG_TAG, "manageResponse : ignored because the session has been closed");
            return;
        }
        boolean z3 = false;
        boolean z4 = str == null;
        String str4 = null;
        if (syncResponse != null) {
            Log.m209d(LOG_TAG, "onSyncComplete");
            if (!(syncResponse.toDevice == null || syncResponse.toDevice.events == null || syncResponse.toDevice.events.size() <= 0)) {
                String str5 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("manageResponse : receives ");
                sb.append(syncResponse.toDevice.events.size());
                sb.append(" toDevice events");
                Log.m209d(str5, sb.toString());
                for (Event handleToDeviceEvent : syncResponse.toDevice.events) {
                    handleToDeviceEvent(handleToDeviceEvent);
                }
            }
            if (syncResponse.accountData != null) {
                String str6 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Received ");
                sb2.append(syncResponse.accountData.size());
                sb2.append(" accountData events");
                Log.m209d(str6, sb2.toString());
                manageAccountData(syncResponse.accountData, z4);
            }
            if (syncResponse.rooms != null) {
                if (syncResponse.rooms.join == null || syncResponse.rooms.join.size() <= 0) {
                    z2 = true;
                } else {
                    String str7 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Received ");
                    sb3.append(syncResponse.rooms.join.size());
                    sb3.append(" joined rooms");
                    Log.m209d(str7, sb3.toString());
                    if (this.mMetricsListener != null) {
                        this.mMetricsListener.onRoomsLoaded(syncResponse.rooms.join.size());
                    }
                    for (String str8 : syncResponse.rooms.join.keySet()) {
                        try {
                            if (this.mLeftRoomsStore.getRoom(str8) != null) {
                                String str9 = LOG_TAG;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("the room ");
                                sb4.append(str8);
                                sb4.append(" moves from left to the joined ones");
                                Log.m209d(str9, sb4.toString());
                                this.mLeftRoomsStore.deleteRoom(str8);
                            }
                            getRoom(str8).handleJoinedRoomSync((RoomSync) syncResponse.rooms.join.get(str8), z4);
                        } catch (Exception e) {
                            String str10 = LOG_TAG;
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("## manageResponse() : handleJoinedRoomSync failed ");
                            sb5.append(e.getMessage());
                            sb5.append(" for room ");
                            sb5.append(str8);
                            Log.m211e(str10, sb5.toString());
                        }
                    }
                    z2 = false;
                }
                if (syncResponse.rooms.invite != null && syncResponse.rooms.invite.size() > 0) {
                    String str11 = LOG_TAG;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("Received ");
                    sb6.append(syncResponse.rooms.invite.size());
                    sb6.append(" invited rooms");
                    Log.m209d(str11, sb6.toString());
                    HashMap hashMap2 = null;
                    boolean z5 = false;
                    for (String str12 : syncResponse.rooms.invite.keySet()) {
                        try {
                            String str13 = LOG_TAG;
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("## manageResponse() : the user has been invited to ");
                            sb7.append(str12);
                            Log.m209d(str13, sb7.toString());
                            if (this.mLeftRoomsStore.getRoom(str12) != null) {
                                String str14 = LOG_TAG;
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append("the room ");
                                sb8.append(str12);
                                sb8.append(" moves from left to the invited ones");
                                Log.m209d(str14, sb8.toString());
                                this.mLeftRoomsStore.deleteRoom(str12);
                            }
                            Room room = getRoom(str12);
                            InvitedRoomSync invitedRoomSync = (InvitedRoomSync) syncResponse.rooms.invite.get(str12);
                            room.handleInvitedRoomSync(invitedRoomSync);
                            if (room.isDirectChatInvitation()) {
                                Iterator it = invitedRoomSync.inviteState.events.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        str3 = null;
                                        break;
                                    }
                                    Event event = (Event) it.next();
                                    if (event.sender != null) {
                                        str3 = event.sender;
                                        break;
                                    }
                                }
                                if (str3 != null) {
                                    if (hashMap2 == null) {
                                        if (getStore().getDirectChatRoomsDict() != null) {
                                            hashMap = new HashMap(getStore().getDirectChatRoomsDict());
                                        } else {
                                            hashMap = new HashMap();
                                        }
                                        hashMap2 = hashMap;
                                    }
                                    if (hashMap2.containsKey(str3)) {
                                        arrayList = new ArrayList((Collection) hashMap2.get(str3));
                                    } else {
                                        arrayList = new ArrayList();
                                    }
                                    if (arrayList.indexOf(str12) < 0) {
                                        Log.m209d(LOG_TAG, "## manageResponse() : add this new invite in direct chats");
                                        arrayList.add(str12);
                                        hashMap2.put(str3, arrayList);
                                        z5 = true;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            String str15 = LOG_TAG;
                            StringBuilder sb9 = new StringBuilder();
                            sb9.append("## manageResponse() : handleInvitedRoomSync failed ");
                            sb9.append(e2.getMessage());
                            sb9.append(" for room ");
                            sb9.append(str12);
                            Log.m211e(str15, sb9.toString());
                        }
                    }
                    if (z5) {
                        this.mAccountDataRestClient.setAccountData(this.mCredentials.userId, AccountDataRestClient.ACCOUNT_DATA_TYPE_DIRECT_MESSAGES, hashMap2, new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                Log.m209d(MXDataHandler.LOG_TAG, "## manageResponse() : succeeds");
                            }

                            public void onNetworkError(Exception exc) {
                                String access$100 = MXDataHandler.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## manageResponse() : update account data failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$100 = MXDataHandler.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## manageResponse() : update account data failed ");
                                sb.append(matrixError.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$100 = MXDataHandler.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## manageResponse() : update account data failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }
                        });
                    }
                    z2 = false;
                }
                if (syncResponse.rooms.leave == null || syncResponse.rooms.leave.size() <= 0) {
                    z3 = z2;
                } else {
                    String str16 = LOG_TAG;
                    StringBuilder sb10 = new StringBuilder();
                    sb10.append("Received ");
                    sb10.append(syncResponse.rooms.leave.size());
                    sb10.append(" left rooms");
                    Log.m209d(str16, sb10.toString());
                    for (String str17 : syncResponse.rooms.leave.keySet()) {
                        String str18 = RoomMember.MEMBERSHIP_LEAVE;
                        Room room2 = getRoom(str17);
                        if (room2 != null) {
                            room2.handleJoinedRoomSync((RoomSync) syncResponse.rooms.leave.get(str17), z4);
                            RoomMember member = room2.getMember(getUserId());
                            if (member != null) {
                                str18 = member.membership;
                            }
                            String str19 = LOG_TAG;
                            StringBuilder sb11 = new StringBuilder();
                            sb11.append("## manageResponse() : leave the room ");
                            sb11.append(str17);
                            Log.m209d(str19, sb11.toString());
                        }
                        if (TextUtils.equals(str18, RoomMember.MEMBERSHIP_KICK) || TextUtils.equals(str18, RoomMember.MEMBERSHIP_BAN)) {
                            onRoomKick(str17);
                        } else {
                            getStore().deleteRoom(str17);
                            onLeaveRoom(str17);
                        }
                        if (this.mAreLeftRoomsSynced && TextUtils.equals(str18, RoomMember.MEMBERSHIP_LEAVE)) {
                            getRoom((IMXStore) this.mLeftRoomsStore, str17, true).handleJoinedRoomSync((RoomSync) syncResponse.rooms.leave.get(str17), z4);
                        }
                    }
                }
            } else {
                z3 = true;
            }
            if (syncResponse.groups != null) {
                if (syncResponse.groups.invite != null && !syncResponse.groups.invite.isEmpty()) {
                    for (String str20 : syncResponse.groups.invite.keySet()) {
                        InvitedGroupSync invitedGroupSync = (InvitedGroupSync) syncResponse.groups.invite.get(str20);
                        this.mGroupsManager.onNewGroupInvitation(str20, invitedGroupSync.profile, invitedGroupSync.inviter, !z4);
                    }
                }
                if (syncResponse.groups.join != null && !syncResponse.groups.join.isEmpty()) {
                    for (String onJoinGroup : syncResponse.groups.join.keySet()) {
                        this.mGroupsManager.onJoinGroup(onJoinGroup, !z4);
                    }
                }
                if (syncResponse.groups.leave != null && !syncResponse.groups.leave.isEmpty()) {
                    for (String onLeaveGroup : syncResponse.groups.leave.keySet()) {
                        this.mGroupsManager.onLeaveGroup(onLeaveGroup, !z4);
                    }
                }
            }
            if (!(syncResponse.presence == null || syncResponse.presence.events == null)) {
                String str21 = LOG_TAG;
                StringBuilder sb12 = new StringBuilder();
                sb12.append("Received ");
                sb12.append(syncResponse.presence.events.size());
                sb12.append(" presence events");
                Log.m209d(str21, sb12.toString());
                for (Event handlePresenceEvent : syncResponse.presence.events) {
                    handlePresenceEvent(handlePresenceEvent);
                }
            }
            if (this.mCrypto != null) {
                this.mCrypto.onSyncCompleted(syncResponse, str, z);
            }
            IMXStore store = getStore();
            if (!z3 && store != null) {
                store.setEventStreamToken(syncResponse.nextBatch);
                store.commit();
            }
        } else {
            z3 = true;
        }
        if (z4) {
            if (!z) {
                startCrypto(true);
            } else {
                this.mIsStartingCryptoWithInitialSync = !z3;
            }
            if (syncResponse != null) {
                str4 = syncResponse.nextBatch;
            }
            onInitialSyncComplete(str4);
        } else {
            if (!z) {
                startCrypto(this.mIsStartingCryptoWithInitialSync);
            }
            if (syncResponse != null) {
                try {
                    str2 = syncResponse.nextBatch;
                } catch (Exception e3) {
                    String str22 = LOG_TAG;
                    StringBuilder sb13 = new StringBuilder();
                    sb13.append("onLiveEventsChunkProcessed failed ");
                    sb13.append(e3.getMessage());
                    Log.m211e(str22, sb13.toString());
                }
            } else {
                str2 = str;
            }
            onLiveEventsChunkProcessed(str, str2);
            try {
                this.mCallsManager.checkPendingIncomingCalls();
            } catch (Exception e4) {
                String str23 = LOG_TAG;
                StringBuilder sb14 = new StringBuilder();
                sb14.append("checkPendingIncomingCalls failed ");
                sb14.append(e4);
                sb14.append(StringUtils.SPACE);
                sb14.append(e4.getMessage());
                Log.m211e(str23, sb14.toString());
            }
        }
    }

    private void refreshUnreadCounters() {
        HashSet<String> hashSet;
        synchronized (this.mUpdatedRoomIdList) {
            hashSet = new HashSet<>(this.mUpdatedRoomIdList);
            this.mUpdatedRoomIdList.clear();
        }
        for (String room : hashSet) {
            Room room2 = this.mStore.getRoom(room);
            if (room2 != null) {
                room2.refreshUnreadCounter();
            }
        }
    }

    public boolean areLeftRoomsSynced() {
        return this.mAreLeftRoomsSynced;
    }

    public boolean isRetrievingLeftRooms() {
        return this.mIsRetrievingLeftRooms;
    }

    public void releaseLeftRooms() {
        if (this.mAreLeftRoomsSynced) {
            this.mLeftRoomsStore.clear();
            this.mAreLeftRoomsSynced = false;
        }
    }

    public void retrieveLeftRooms(ApiCallback<Void> apiCallback) {
        int size;
        if (!this.mAreLeftRoomsSynced) {
            synchronized (this.mLeftRoomsRefreshCallbacks) {
                if (apiCallback != null) {
                    try {
                        this.mLeftRoomsRefreshCallbacks.add(apiCallback);
                    } catch (Throwable th) {
                        while (true) {
                            throw th;
                        }
                    }
                }
                size = this.mLeftRoomsRefreshCallbacks.size();
            }
            if (1 == size) {
                this.mIsRetrievingLeftRooms = true;
                Log.m209d(LOG_TAG, "## refreshHistoricalRoomsList() : requesting");
                this.mEventsRestClient.syncFromToken(null, 0, 30000, null, LEFT_ROOMS_FILTER, new ApiCallback<SyncResponse>() {
                    public void onSuccess(final SyncResponse syncResponse) {
                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                if (syncResponse.rooms.leave != null) {
                                    for (String str : syncResponse.rooms.leave.keySet()) {
                                        Room room = MXDataHandler.this.getRoom((IMXStore) MXDataHandler.this.mLeftRoomsStore, str, true);
                                        if (room != null) {
                                            room.setIsLeft(true);
                                            room.handleJoinedRoomSync((RoomSync) syncResponse.rooms.leave.get(str), true);
                                            RoomMember member = room.getState().getMember(MXDataHandler.this.getUserId());
                                            if (member == null || !TextUtils.equals(member.membership, RoomMember.MEMBERSHIP_LEAVE)) {
                                                MXDataHandler.this.mLeftRoomsStore.deleteRoom(str);
                                            }
                                        }
                                    }
                                    String access$100 = MXDataHandler.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("## refreshHistoricalRoomsList() : ");
                                    sb.append(MXDataHandler.this.mLeftRoomsStore.getRooms().size());
                                    sb.append(" left rooms");
                                    Log.m209d(access$100, sb.toString());
                                }
                                MXDataHandler.this.mIsRetrievingLeftRooms = false;
                                MXDataHandler.this.mAreLeftRoomsSynced = true;
                                synchronized (MXDataHandler.this.mLeftRoomsRefreshCallbacks) {
                                    Iterator it = MXDataHandler.this.mLeftRoomsRefreshCallbacks.iterator();
                                    while (it.hasNext()) {
                                        ((ApiCallback) it.next()).onSuccess(null);
                                    }
                                    MXDataHandler.this.mLeftRoomsRefreshCallbacks.clear();
                                }
                            }
                        });
                        thread.setPriority(1);
                        thread.start();
                    }

                    public void onNetworkError(Exception exc) {
                        synchronized (MXDataHandler.this.mLeftRoomsRefreshCallbacks) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshHistoricalRoomsList() : failed ");
                            sb.append(exc.getMessage());
                            Log.m209d(access$100, sb.toString());
                            Iterator it = MXDataHandler.this.mLeftRoomsRefreshCallbacks.iterator();
                            while (it.hasNext()) {
                                ((ApiCallback) it.next()).onNetworkError(exc);
                            }
                            MXDataHandler.this.mLeftRoomsRefreshCallbacks.clear();
                        }
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        synchronized (MXDataHandler.this.mLeftRoomsRefreshCallbacks) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshHistoricalRoomsList() : failed ");
                            sb.append(matrixError.getMessage());
                            Log.m209d(access$100, sb.toString());
                            Iterator it = MXDataHandler.this.mLeftRoomsRefreshCallbacks.iterator();
                            while (it.hasNext()) {
                                ((ApiCallback) it.next()).onMatrixError(matrixError);
                            }
                            MXDataHandler.this.mLeftRoomsRefreshCallbacks.clear();
                        }
                    }

                    public void onUnexpectedError(Exception exc) {
                        synchronized (MXDataHandler.this.mLeftRoomsRefreshCallbacks) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshHistoricalRoomsList() : failed ");
                            sb.append(exc.getMessage());
                            Log.m209d(access$100, sb.toString());
                            Iterator it = MXDataHandler.this.mLeftRoomsRefreshCallbacks.iterator();
                            while (it.hasNext()) {
                                ((ApiCallback) it.next()).onUnexpectedError(exc);
                            }
                            MXDataHandler.this.mLeftRoomsRefreshCallbacks.clear();
                        }
                    }
                });
            }
        } else if (apiCallback != null) {
            apiCallback.onSuccess(null);
        }
    }

    private void handleToDeviceEvent(Event event) {
        decryptEvent(event, null);
        if (!TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE) || event.getContent() == null || !TextUtils.equals(JsonUtils.getMessageMsgType(event.getContent()), "m.bad.encrypted")) {
            onToDeviceEvent(event);
            return;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## handleToDeviceEvent() : Warning: Unable to decrypt to-device event : ");
        sb.append(event.getContent());
        Log.m211e(str, sb.toString());
    }

    public boolean decryptEvent(Event event, String str) {
        MXEventDecryptionResult mXEventDecryptionResult;
        if (event != null && TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE_ENCRYPTED)) {
            if (getCrypto() != null) {
                try {
                    mXEventDecryptionResult = getCrypto().decryptEvent(event, str);
                } catch (MXDecryptionException e) {
                    event.setCryptoError(e.getCryptoError());
                    mXEventDecryptionResult = null;
                }
                if (mXEventDecryptionResult != null) {
                    event.setClearData(mXEventDecryptionResult);
                    return true;
                }
            } else {
                event.setCryptoError(new MXCryptoError(MXCryptoError.ENCRYPTING_NOT_ENABLED_ERROR_CODE, MXCryptoError.ENCRYPTING_NOT_ENABLED_REASON, null));
            }
        }
        return false;
    }

    public void resetReplayAttackCheckInTimeline(String str) {
        if (str != null && this.mCrypto != null && this.mCrypto.getOlmDevice() != null) {
            this.mCrypto.resetReplayAttackCheckInTimeline(str);
        }
    }

    private List<IMXEventListener> getListenersSnapshot() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mEventListeners);
        }
        return arrayList;
    }

    public void onStoreReady() {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onStoreReady();
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onStoreReady : listenersSnapshot) {
                    try {
                        onStoreReady.onStoreReady();
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onStoreReady ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onAccountInfoUpdate(final MyUser myUser) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onAccountInfoUpdate(myUser);
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onAccountInfoUpdate : listenersSnapshot) {
                    try {
                        onAccountInfoUpdate.onAccountInfoUpdate(myUser);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onAccountInfoUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onPresenceUpdate(final Event event, final User user) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onPresenceUpdate(event, user);
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onPresenceUpdate : listenersSnapshot) {
                    try {
                        onPresenceUpdate.onPresenceUpdate(event, user);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onPresenceUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    private boolean ignoreEvent(String str) {
        boolean z = false;
        if (!this.mIsRetrievingLeftRooms || TextUtils.isEmpty(str)) {
            return false;
        }
        if (this.mLeftRoomsStore.getRoom(str) != null) {
            z = true;
        }
        return z;
    }

    public void onLiveEvent(final Event event, final RoomState roomState) {
        if (!ignoreEvent(event.roomId)) {
            String type = event.getType();
            if (!TextUtils.equals(Event.EVENT_TYPE_TYPING, type) && !TextUtils.equals(Event.EVENT_TYPE_RECEIPT, type) && !TextUtils.equals(Event.EVENT_TYPE_TYPING, type)) {
                synchronized (this.mUpdatedRoomIdList) {
                    this.mUpdatedRoomIdList.add(roomState.roomId);
                }
            }
            if (this.mCryptoEventsListener != null) {
                this.mCryptoEventsListener.onLiveEvent(event, roomState);
            }
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onLiveEvent : listenersSnapshot) {
                        try {
                            onLiveEvent.onLiveEvent(event, roomState);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onLiveEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onLiveEventsChunkProcessed(final String str, final String str2) {
        this.mResourceLimitExceededError = null;
        refreshUnreadCounters();
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onLiveEventsChunkProcessed(str, str2);
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onLiveEventsChunkProcessed : listenersSnapshot) {
                    try {
                        onLiveEventsChunkProcessed.onLiveEventsChunkProcessed(str, str2);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onLiveEventsChunkProcessed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onBingEvent(Event event, RoomState roomState, BingRule bingRule) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onBingEvent(event, roomState, bingRule);
        }
        if (!ignoreEvent(event.roomId)) {
            final List listenersSnapshot = getListenersSnapshot();
            MXOsHandler mXOsHandler = this.mUiHandler;
            final Event event2 = event;
            final RoomState roomState2 = roomState;
            final BingRule bingRule2 = bingRule;
            C187613 r1 = new Runnable() {
                public void run() {
                    for (IMXEventListener onBingEvent : listenersSnapshot) {
                        try {
                            onBingEvent.onBingEvent(event2, roomState2, bingRule2);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onBingEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            };
            mXOsHandler.post(r1);
        }
    }

    public void updateEventState(Event event, SentState sentState) {
        if (event != null && event.mSentState != sentState) {
            event.mSentState = sentState;
            getStore().flushRoomEvents(event.roomId);
            onEventSentStateUpdated(event);
        }
    }

    public void onEventSentStateUpdated(final Event event) {
        if (!ignoreEvent(event.roomId)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onEventSentStateUpdated : listenersSnapshot) {
                        try {
                            onEventSentStateUpdated.onEventSentStateUpdated(event);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onEventSentStateUpdated ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onEventSent(final Event event, final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onEventSent(event, str);
        }
        if (!ignoreEvent(event.roomId)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onEventSent : listenersSnapshot) {
                        try {
                            onEventSent.onEventSent(event, str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onEventSent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onBingRulesUpdate() {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onBingRulesUpdate();
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onBingRulesUpdate : listenersSnapshot) {
                    try {
                        onBingRulesUpdate.onBingRulesUpdate();
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onBingRulesUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    private void dispatchOnInitialSyncComplete(String str) {
        this.mInitialSyncToToken = str;
        refreshUnreadCounters();
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onInitialSyncComplete(str);
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onInitialSyncComplete : listenersSnapshot) {
                    try {
                        onInitialSyncComplete.onInitialSyncComplete(MXDataHandler.this.mInitialSyncToToken);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onInitialSyncComplete ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void dispatchOnCryptoSyncComplete() {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onCryptoSyncComplete : listenersSnapshot) {
                    try {
                        onCryptoSyncComplete.onCryptoSyncComplete();
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("OnCryptoSyncComplete ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    private void startCrypto(boolean z) {
        if (getCrypto() != null && !getCrypto().isStarted() && !getCrypto().isStarting()) {
            getCrypto().setNetworkConnectivityReceiver(this.mNetworkConnectivityReceiver);
            getCrypto().start(z, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    MXDataHandler.this.dispatchOnCryptoSyncComplete();
                }

                private void onError(String str) {
                    String access$100 = MXDataHandler.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onInitialSyncComplete() : getCrypto().start fails ");
                    sb.append(str);
                    Log.m211e(access$100, sb.toString());
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

    public void onInitialSyncComplete(String str) {
        dispatchOnInitialSyncComplete(str);
    }

    public void onSyncError(final MatrixError matrixError) {
        if (MatrixError.RESOURCE_LIMIT_EXCEEDED.equals(matrixError.errcode)) {
            this.mResourceLimitExceededError = matrixError;
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onSyncError : listenersSnapshot) {
                    try {
                        onSyncError.onSyncError(matrixError);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onSyncError ");
                        sb.append(e.getMessage());
                        Log.m212e(access$100, sb.toString(), e);
                    }
                }
            }
        });
    }

    public void onNewRoom(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onNewRoom(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onNewRoom : listenersSnapshot) {
                        try {
                            onNewRoom.onNewRoom(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onNewRoom ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onJoinRoom(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onJoinRoom(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onJoinRoom : listenersSnapshot) {
                        try {
                            onJoinRoom.onJoinRoom(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onJoinRoom ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onRoomInitialSyncComplete(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onRoomInitialSyncComplete(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onRoomInitialSyncComplete : listenersSnapshot) {
                        try {
                            onRoomInitialSyncComplete.onRoomInitialSyncComplete(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomInitialSyncComplete ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onRoomInternalUpdate(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onRoomInternalUpdate(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onRoomInternalUpdate : listenersSnapshot) {
                        try {
                            onRoomInternalUpdate.onRoomInternalUpdate(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomInternalUpdate ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onNotificationCountUpdate(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onNotificationCountUpdate(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onNotificationCountUpdate : listenersSnapshot) {
                        try {
                            onNotificationCountUpdate.onNotificationCountUpdate(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onNotificationCountUpdate ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onLeaveRoom(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onLeaveRoom(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onLeaveRoom : listenersSnapshot) {
                        try {
                            onLeaveRoom.onLeaveRoom(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onLeaveRoom ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onRoomKick(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onRoomKick(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onRoomKick : listenersSnapshot) {
                        try {
                            onRoomKick.onRoomKick(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomKick ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onReceiptEvent(final String str, final List<String> list) {
        synchronized (this.mUpdatedRoomIdList) {
            this.mUpdatedRoomIdList.add(str);
        }
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onReceiptEvent(str, list);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onReceiptEvent : listenersSnapshot) {
                        try {
                            onReceiptEvent.onReceiptEvent(str, list);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onReceiptEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onRoomTagEvent(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onRoomTagEvent(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onRoomTagEvent : listenersSnapshot) {
                        try {
                            onRoomTagEvent.onRoomTagEvent(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomTagEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onReadMarkerEvent(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onReadMarkerEvent(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onReadMarkerEvent : listenersSnapshot) {
                        try {
                            onReadMarkerEvent.onReadMarkerEvent(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onReadMarkerEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onRoomFlush(final String str) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onRoomFlush(str);
        }
        if (!ignoreEvent(str)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onRoomFlush : listenersSnapshot) {
                        try {
                            onRoomFlush.onRoomFlush(str);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomFlush ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onIgnoredUsersListUpdate() {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onIgnoredUsersListUpdate();
        }
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onIgnoredUsersListUpdate : listenersSnapshot) {
                    try {
                        onIgnoredUsersListUpdate.onIgnoredUsersListUpdate();
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onIgnoredUsersListUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onToDeviceEvent(final Event event) {
        if (this.mCryptoEventsListener != null) {
            this.mCryptoEventsListener.onToDeviceEvent(event);
        }
        if (!ignoreEvent(event.roomId)) {
            final List listenersSnapshot = getListenersSnapshot();
            this.mUiHandler.post(new Runnable() {
                public void run() {
                    for (IMXEventListener onToDeviceEvent : listenersSnapshot) {
                        try {
                            onToDeviceEvent.onToDeviceEvent(event);
                        } catch (Exception e) {
                            String access$100 = MXDataHandler.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("OnToDeviceEvent ");
                            sb.append(e.getMessage());
                            Log.m211e(access$100, sb.toString());
                        }
                    }
                }
            });
        }
    }

    public void onDirectMessageChatRoomsListUpdate() {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onDirectMessageChatRoomsListUpdate : listenersSnapshot) {
                    try {
                        onDirectMessageChatRoomsListUpdate.onDirectMessageChatRoomsListUpdate();
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onDirectMessageChatRoomsListUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onEventDecrypted(final Event event) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onEventDecrypted : listenersSnapshot) {
                    try {
                        onEventDecrypted.onEventDecrypted(event);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onDecryptedEvent ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onNewGroupInvitation(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onNewGroupInvitation : listenersSnapshot) {
                    try {
                        onNewGroupInvitation.onNewGroupInvitation(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onNewGroupInvitation ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onJoinGroup(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onJoinGroup : listenersSnapshot) {
                    try {
                        onJoinGroup.onJoinGroup(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onJoinGroup ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onLeaveGroup(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onLeaveGroup : listenersSnapshot) {
                    try {
                        onLeaveGroup.onLeaveGroup(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onLeaveGroup ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onGroupProfileUpdate(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onGroupProfileUpdate : listenersSnapshot) {
                    try {
                        onGroupProfileUpdate.onGroupProfileUpdate(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onGroupProfileUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onGroupRoomsListUpdate(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onGroupRoomsListUpdate : listenersSnapshot) {
                    try {
                        onGroupRoomsListUpdate.onGroupRoomsListUpdate(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onGroupRoomsListUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onGroupUsersListUpdate(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onGroupUsersListUpdate : listenersSnapshot) {
                    try {
                        onGroupUsersListUpdate.onGroupUsersListUpdate(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onGroupUsersListUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void onGroupInvitedUsersListUpdate(final String str) {
        final List listenersSnapshot = getListenersSnapshot();
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (IMXEventListener onGroupInvitedUsersListUpdate : listenersSnapshot) {
                    try {
                        onGroupInvitedUsersListUpdate.onGroupInvitedUsersListUpdate(str);
                    } catch (Exception e) {
                        String access$100 = MXDataHandler.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onGroupInvitedUsersListUpdate ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public List<String> getDirectChatRoomIdsList() {
        if (this.mLocalDirectChatRoomIdsList != null) {
            return this.mLocalDirectChatRoomIdsList;
        }
        IMXStore store = getStore();
        ArrayList arrayList = new ArrayList();
        if (store == null) {
            Log.m211e(LOG_TAG, "## getDirectChatRoomIdsList() : null store");
            return arrayList;
        }
        Collection<List> collection = null;
        if (store.getDirectChatRoomsDict() != null) {
            collection = store.getDirectChatRoomsDict().values();
        }
        if (collection != null) {
            for (List<String> it : collection) {
                for (String str : it) {
                    if (arrayList.indexOf(str) < 0) {
                        arrayList.add(str);
                    }
                }
            }
        }
        this.mLocalDirectChatRoomIdsList = arrayList;
        return arrayList;
    }

    public void setDirectChatRoomsMap(Map<String, List<String>> map, ApiCallback<Void> apiCallback) {
        Log.m209d(LOG_TAG, "## setDirectChatRoomsMap()");
        IMXStore store = getStore();
        if (store != null) {
            store.setDirectChatRoomsDict(map);
        } else {
            Log.m211e(LOG_TAG, "## setDirectChatRoomsMap() : null store");
        }
        this.mLocalDirectChatRoomIdsList = null;
        this.mAccountDataRestClient.setAccountData(getMyUser().user_id, AccountDataRestClient.ACCOUNT_DATA_TYPE_DIRECT_MESSAGES, map, apiCallback);
    }

    private void forceDirectChatRoomValue(List<RoomIdsListRetroCompat> list, ApiCallback<Void> apiCallback) {
        ArrayList arrayList;
        HashMap hashMap = new HashMap();
        if (list != null) {
            for (RoomIdsListRetroCompat roomIdsListRetroCompat : list) {
                if (hashMap.containsKey(roomIdsListRetroCompat.mParticipantUserId)) {
                    arrayList = new ArrayList((Collection) hashMap.get(roomIdsListRetroCompat.mParticipantUserId));
                    arrayList.add(roomIdsListRetroCompat.mRoomId);
                } else {
                    arrayList = new ArrayList();
                    arrayList.add(roomIdsListRetroCompat.mRoomId);
                }
                hashMap.put(roomIdsListRetroCompat.mParticipantUserId, arrayList);
            }
            this.mAccountDataRestClient.setAccountData(getMyUser().user_id, AccountDataRestClient.ACCOUNT_DATA_TYPE_DIRECT_MESSAGES, hashMap, apiCallback);
        }
    }

    private void getDirectChatRoomIdsListRetroCompat(IMXStore iMXStore, ArrayList<RoomIdsListRetroCompat> arrayList) {
        if (iMXStore != null && arrayList != null) {
            Iterator it = new ArrayList(iMXStore.getRooms()).iterator();
            while (it.hasNext()) {
                Room room = (Room) it.next();
                if (room.getActiveMembers().size() == 2 && room.getAccountData() != null && !room.getAccountData().hasTags()) {
                    RoomMember member = room.getMember(getMyUser().user_id);
                    ArrayList arrayList2 = new ArrayList(room.getActiveMembers());
                    if (member != null) {
                        String str = member.membership;
                        if (TextUtils.equals(str, RoomMember.MEMBERSHIP_JOIN) || TextUtils.equals(str, RoomMember.MEMBERSHIP_BAN) || TextUtils.equals(str, RoomMember.MEMBERSHIP_LEAVE)) {
                            arrayList.add(new RoomIdsListRetroCompat(((RoomMember) arrayList2.get(TextUtils.equals(((RoomMember) arrayList2.get(0)).getUserId(), getMyUser().user_id) ? 1 : 0)).getUserId(), room.getRoomId()));
                        }
                    }
                }
            }
        }
    }

    public List<String> getDirectChatRoomIdsList(String str) {
        ArrayList arrayList = new ArrayList();
        IMXStore store = getStore();
        if (store.getDirectChatRoomsDict() != null) {
            HashMap hashMap = new HashMap(store.getDirectChatRoomsDict());
            if (hashMap.containsKey(str)) {
                arrayList = new ArrayList();
                for (String str2 : (List) hashMap.get(str)) {
                    if (store.getRoom(str2) != null) {
                        arrayList.add(str2);
                    }
                }
            } else {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getDirectChatRoomIdsList(): UserId ");
                sb.append(str);
                sb.append(" has no entry in account_data");
                Log.m217w(str3, sb.toString());
            }
        } else {
            Log.m217w(LOG_TAG, "## getDirectChatRoomIdsList(): failure - getDirectChatRoomsDict()=null");
        }
        return arrayList;
    }

    public void handleOfflineToDevice(Event event, String str, String str2) {
        JsonObject asJsonObject = event.getContentAsJsonObject().getAsJsonObject(str);
        JsonObject asJsonObject2 = asJsonObject != null ? asJsonObject.getAsJsonObject(str2) : null;
        if (asJsonObject2 != null) {
            event.content = asJsonObject2.getAsJsonObject();
            handleToDeviceEvent(event);
        }
    }
}
