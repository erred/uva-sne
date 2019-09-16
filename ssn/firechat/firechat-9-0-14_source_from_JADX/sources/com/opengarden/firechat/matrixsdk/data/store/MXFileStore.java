package com.opengarden.firechat.matrixsdk.data.store;

import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import com.opengarden.firechat.matrixsdk.util.CompatUtil;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.MXOsHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class MXFileStore extends MXMemoryStore {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXFileStore";
    private static final int MAX_STORED_MESSAGES_COUNT = 50;
    private static final String MXFILE_STORE_FOLDER = "MXFileStore";
    private static final String MXFILE_STORE_GROUPS_FOLDER = "groups";
    private static final String MXFILE_STORE_GZ_ROOMS_MESSAGES_FOLDER = "messages_gz";
    private static final String MXFILE_STORE_GZ_ROOMS_STATE_EVENTS_FOLDER = "state_rooms_events";
    private static final String MXFILE_STORE_GZ_ROOMS_STATE_FOLDER = "state_gz";
    private static final String MXFILE_STORE_METADATA_FILE_NAME = "MXFileStore";
    private static final String MXFILE_STORE_ROOMS_ACCOUNT_DATA_FOLDER = "accountData";
    private static final String MXFILE_STORE_ROOMS_RECEIPT_FOLDER = "receipts";
    private static final String MXFILE_STORE_ROOMS_SUMMARY_FOLDER = "summary";
    private static final String MXFILE_STORE_ROOMS_TOKENS_FOLDER = "tokens";
    private static final String MXFILE_STORE_USER_FOLDER = "users";
    private static final int MXFILE_VERSION = 22;
    /* access modifiers changed from: private */
    public boolean mAreReceiptsReady = false;
    private boolean mAreUsersLoaded = false;
    /* access modifiers changed from: private */
    public MXOsHandler mFileStoreHandler = null;
    private HashSet<String> mGroupsToCommit;
    private File mGzStoreRoomsMessagesFolderFile = null;
    private File mGzStoreRoomsStateEventsFolderFile = null;
    private File mGzStoreRoomsStateFolderFile = null;
    private HandlerThread mHandlerThread = null;
    /* access modifiers changed from: private */
    public boolean mIsKilled = false;
    /* access modifiers changed from: private */
    public boolean mIsNewStorage = false;
    /* access modifiers changed from: private */
    public boolean mIsOpening = false;
    /* access modifiers changed from: private */
    public boolean mIsPostProcessingDone = false;
    /* access modifiers changed from: private */
    public boolean mIsReady = false;
    /* access modifiers changed from: private */
    public boolean mMetaDataHasChanged = false;
    private HashMap<String, List<Event>> mPendingRoomStateEvents = new HashMap<>();
    /* access modifiers changed from: private */
    public long mPreloadTime = 0;
    /* access modifiers changed from: private */
    public final List<String> mRoomReceiptsToLoad = new ArrayList();
    private HashSet<String> mRoomsToCommitForAccountData;
    /* access modifiers changed from: private */
    public HashSet<String> mRoomsToCommitForMessages;
    /* access modifiers changed from: private */
    public HashSet<String> mRoomsToCommitForReceipts;
    /* access modifiers changed from: private */
    public HashSet<String> mRoomsToCommitForStates;
    /* access modifiers changed from: private */
    public HashSet<String> mRoomsToCommitForSummaries;
    /* access modifiers changed from: private */
    public File mStoreFolderFile = null;
    /* access modifiers changed from: private */
    public File mStoreGroupsFolderFile = null;
    /* access modifiers changed from: private */
    public File mStoreRoomsAccountDataFolderFile = null;
    /* access modifiers changed from: private */
    public File mStoreRoomsMessagesReceiptsFolderFile = null;
    /* access modifiers changed from: private */
    public File mStoreRoomsSummaryFolderFile = null;
    private File mStoreRoomsTokensFolderFile = null;
    /* access modifiers changed from: private */
    public final HashMap<String, Long> mStoreStats = new HashMap<>();
    /* access modifiers changed from: private */
    public File mStoreUserFolderFile = null;
    private HashSet<String> mUserIdsToCommit;

    private void saveRoomStateEvents(String str) {
    }

    private void saveRoomStatesEvents() {
    }

    public boolean isCorrupted() {
        return false;
    }

    public boolean isPermanent() {
        return true;
    }

    public void storeRoomStateEvent(String str, Event event) {
    }

    private void createDirTree(String str) {
        this.mStoreFolderFile = new File(new File(this.mContext.getApplicationContext().getFilesDir(), "MXFileStore"), str);
        if (!this.mStoreFolderFile.exists()) {
            this.mStoreFolderFile.mkdirs();
        }
        this.mGzStoreRoomsMessagesFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_GZ_ROOMS_MESSAGES_FOLDER);
        if (!this.mGzStoreRoomsMessagesFolderFile.exists()) {
            this.mGzStoreRoomsMessagesFolderFile.mkdirs();
        }
        this.mStoreRoomsTokensFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_ROOMS_TOKENS_FOLDER);
        if (!this.mStoreRoomsTokensFolderFile.exists()) {
            this.mStoreRoomsTokensFolderFile.mkdirs();
        }
        this.mGzStoreRoomsStateFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_GZ_ROOMS_STATE_FOLDER);
        if (!this.mGzStoreRoomsStateFolderFile.exists()) {
            this.mGzStoreRoomsStateFolderFile.mkdirs();
        }
        this.mGzStoreRoomsStateEventsFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_GZ_ROOMS_STATE_EVENTS_FOLDER);
        if (!this.mGzStoreRoomsStateEventsFolderFile.exists()) {
            this.mGzStoreRoomsStateEventsFolderFile.mkdirs();
        }
        this.mStoreRoomsSummaryFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_ROOMS_SUMMARY_FOLDER);
        if (!this.mStoreRoomsSummaryFolderFile.exists()) {
            this.mStoreRoomsSummaryFolderFile.mkdirs();
        }
        this.mStoreRoomsMessagesReceiptsFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_ROOMS_RECEIPT_FOLDER);
        if (!this.mStoreRoomsMessagesReceiptsFolderFile.exists()) {
            this.mStoreRoomsMessagesReceiptsFolderFile.mkdirs();
        }
        this.mStoreRoomsAccountDataFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_ROOMS_ACCOUNT_DATA_FOLDER);
        if (!this.mStoreRoomsAccountDataFolderFile.exists()) {
            this.mStoreRoomsAccountDataFolderFile.mkdirs();
        }
        this.mStoreUserFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_USER_FOLDER);
        if (!this.mStoreUserFolderFile.exists()) {
            this.mStoreUserFolderFile.mkdirs();
        }
        this.mStoreGroupsFolderFile = new File(this.mStoreFolderFile, MXFILE_STORE_GROUPS_FOLDER);
        if (!this.mStoreGroupsFolderFile.exists()) {
            this.mStoreGroupsFolderFile.mkdirs();
        }
    }

    public MXFileStore(HomeServerConnectionConfig homeServerConnectionConfig, Context context) {
        initCommon();
        setContext(context);
        this.mIsReady = false;
        this.mCredentials = homeServerConnectionConfig.getCredentials();
        StringBuilder sb = new StringBuilder();
        sb.append("MXFileStoreBackgroundThread_");
        sb.append(this.mCredentials.userId);
        this.mHandlerThread = new HandlerThread(sb.toString(), 1);
        createDirTree(this.mCredentials.userId);
        this.mRoomsToCommitForMessages = new HashSet<>();
        this.mRoomsToCommitForStates = new HashSet<>();
        this.mRoomsToCommitForSummaries = new HashSet<>();
        this.mRoomsToCommitForAccountData = new HashSet<>();
        this.mRoomsToCommitForReceipts = new HashSet<>();
        this.mUserIdsToCommit = new HashSet<>();
        this.mGroupsToCommit = new HashSet<>();
        loadMetaData();
        if (this.mMetadata == null) {
            deleteAllData(true);
        }
        if (this.mMetadata == null || this.mMetadata.mAccessToken == null) {
            this.mIsNewStorage = true;
            this.mIsOpening = true;
            this.mHandlerThread.start();
            this.mFileStoreHandler = new MXOsHandler(this.mHandlerThread.getLooper());
            this.mMetadata = new MXFileStoreMetaData();
            this.mMetadata.mUserId = this.mCredentials.userId;
            this.mMetadata.mAccessToken = this.mCredentials.accessToken;
            this.mMetadata.mVersion = 22;
            this.mMetaDataHasChanged = true;
            saveMetaData();
            this.mEventStreamToken = null;
            this.mIsOpening = false;
            this.mIsReady = true;
            this.mAreReceiptsReady = true;
        }
    }

    private void setIsKilled(boolean z) {
        synchronized (this) {
            this.mIsKilled = z;
        }
    }

    /* access modifiers changed from: private */
    public boolean isKilled() {
        boolean z;
        synchronized (this) {
            z = this.mIsKilled;
        }
        return z;
    }

    public void commit() {
        if (this.mMetadata != null && this.mMetadata.mAccessToken != null && !isKilled()) {
            Log.m209d(LOG_TAG, "++ Commit");
            saveUsers();
            saveGroups();
            saveRoomsMessages();
            saveRoomStates();
            saveRoomStatesEvents();
            saveSummaries();
            saveRoomsAccountData();
            saveReceipts();
            saveMetaData();
            Log.m209d(LOG_TAG, "-- Commit");
        }
    }

    public void open() {
        super.open();
        final long currentTimeMillis = System.currentTimeMillis();
        synchronized (this) {
            if (!this.mIsReady && !this.mIsOpening && this.mMetadata != null && this.mHandlerThread != null) {
                this.mIsOpening = true;
                Log.m211e(LOG_TAG, "Open the store.");
                if (this.mFileStoreHandler == null) {
                    try {
                        this.mHandlerThread.start();
                        this.mFileStoreHandler = new MXOsHandler(this.mHandlerThread.getLooper());
                    } catch (IllegalThreadStateException unused) {
                        Log.m211e(LOG_TAG, "mHandlerThread is already started.");
                        return;
                    }
                }
                new Thread(new Runnable() {
                    public void run() {
                        MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                            public void run() {
                                String str;
                                Log.m211e(MXFileStore.LOG_TAG, "Open the store in the background thread.");
                                boolean z = MXFileStore.this.mMetadata.mVersion == 22 && TextUtils.equals(MXFileStore.this.mMetadata.mUserId, MXFileStore.this.mCredentials.userId) && TextUtils.equals(MXFileStore.this.mMetadata.mAccessToken, MXFileStore.this.mCredentials.accessToken);
                                if (!z) {
                                    str = "Invalid store content";
                                    Log.m211e(MXFileStore.LOG_TAG, str);
                                } else {
                                    str = null;
                                }
                                if (z) {
                                    z &= MXFileStore.this.loadRoomsMessages();
                                    if (!z) {
                                        str = "loadRoomsMessages fails";
                                        Log.m211e(MXFileStore.LOG_TAG, str);
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "loadRoomsMessages succeeds");
                                    }
                                }
                                if (z) {
                                    z &= MXFileStore.this.loadGroups();
                                    if (!z) {
                                        str = "loadGroups fails";
                                        Log.m211e(MXFileStore.LOG_TAG, str);
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "loadGroups succeeds");
                                    }
                                }
                                if (z) {
                                    z &= MXFileStore.this.loadRoomsState();
                                    if (!z) {
                                        str = "loadRoomsState fails";
                                        Log.m211e(MXFileStore.LOG_TAG, str);
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "loadRoomsState succeeds");
                                        long currentTimeMillis = System.currentTimeMillis();
                                        Log.m211e(MXFileStore.LOG_TAG, "Retrieve the users from the roomstate");
                                        for (Room liveState : MXFileStore.this.getRooms()) {
                                            for (RoomMember updateUserWithRoomMemberEvent : liveState.getLiveState().getMembers()) {
                                                MXFileStore.this.updateUserWithRoomMemberEvent(updateUserWithRoomMemberEvent);
                                            }
                                        }
                                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                                        String access$000 = MXFileStore.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Retrieve ");
                                        sb.append(MXFileStore.this.mUsers.size());
                                        sb.append(" users with the room states in ");
                                        sb.append(currentTimeMillis2);
                                        sb.append("  ms");
                                        Log.m211e(access$000, sb.toString());
                                        MXFileStore.this.mStoreStats.put("Retrieve users", Long.valueOf(currentTimeMillis2));
                                    }
                                }
                                if (z) {
                                    z &= MXFileStore.this.loadSummaries();
                                    if (!z) {
                                        str = "loadSummaries fails";
                                        Log.m211e(MXFileStore.LOG_TAG, str);
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "loadSummaries succeeds");
                                        for (String str2 : MXFileStore.this.mRoomSummaries.keySet()) {
                                            Room room = MXFileStore.this.getRoom(str2);
                                            if (room == null) {
                                                String access$0002 = MXFileStore.LOG_TAG;
                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append("loadSummaries : the room ");
                                                sb2.append(str2);
                                                sb2.append(" does not exist");
                                                Log.m211e(access$0002, sb2.toString());
                                                z = false;
                                            } else if (room.getMember(MXFileStore.this.mCredentials.userId) == null) {
                                                String access$0003 = MXFileStore.LOG_TAG;
                                                StringBuilder sb3 = new StringBuilder();
                                                sb3.append("loadSummaries) : a summary exists for the roomId ");
                                                sb3.append(str2);
                                                sb3.append(" but the user is not anymore a member");
                                                Log.m211e(access$0003, sb3.toString());
                                            }
                                        }
                                    }
                                }
                                if (z) {
                                    z &= MXFileStore.this.loadRoomsAccountData();
                                    if (!z) {
                                        str = "loadRoomsAccountData fails";
                                        Log.m211e(MXFileStore.LOG_TAG, str);
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "loadRoomsAccountData succeeds");
                                    }
                                }
                                if (!z) {
                                    Log.m211e(MXFileStore.LOG_TAG, "Fail to open the store in background");
                                    MXFileStoreMetaData mXFileStoreMetaData = MXFileStore.this.mMetadata;
                                    MXFileStore.this.deleteAllData(true);
                                    MXFileStore.this.mRoomsToCommitForMessages = new HashSet();
                                    MXFileStore.this.mRoomsToCommitForStates = new HashSet();
                                    MXFileStore.this.mRoomsToCommitForSummaries = new HashSet();
                                    MXFileStore.this.mRoomsToCommitForReceipts = new HashSet();
                                    MXFileStore.this.mMetadata = mXFileStoreMetaData;
                                    if (MXFileStore.this.mMetadata == null) {
                                        MXFileStore.this.mMetadata = new MXFileStoreMetaData();
                                        MXFileStore.this.mMetadata.mUserId = MXFileStore.this.mCredentials.userId;
                                        MXFileStore.this.mMetadata.mAccessToken = MXFileStore.this.mCredentials.accessToken;
                                        MXFileStore.this.mMetaDataHasChanged = true;
                                    } else {
                                        MXFileStore.this.mMetadata.mEventStreamToken = null;
                                    }
                                    MXFileStore.this.mMetadata.mVersion = 22;
                                    MXFileStore.this.mEventStreamToken = null;
                                    MXFileStore.this.mAreReceiptsReady = true;
                                } else {
                                    Log.m209d(MXFileStore.LOG_TAG, "++ store stats");
                                    for (String str3 : MXFileStore.this.mRoomEvents.keySet()) {
                                        Room room2 = MXFileStore.this.getRoom(str3);
                                        if (!(room2 == null || room2.getLiveState() == null)) {
                                            int size = room2.getLiveState().getMembers().size();
                                            int size2 = ((LinkedHashMap) MXFileStore.this.mRoomEvents.get(str3)).size();
                                            String access$0004 = MXFileStore.LOG_TAG;
                                            StringBuilder sb4 = new StringBuilder();
                                            sb4.append(" room ");
                                            sb4.append(str3);
                                            sb4.append(" : membersCount ");
                                            sb4.append(size);
                                            sb4.append(" - eventsCount ");
                                            sb4.append(size2);
                                            Log.m209d(access$0004, sb4.toString());
                                        }
                                    }
                                    Log.m209d(MXFileStore.LOG_TAG, "-- store stats");
                                }
                                Log.m209d(MXFileStore.LOG_TAG, "## open() : post processing.");
                                MXFileStore.this.dispatchPostProcess(MXFileStore.this.mCredentials.userId);
                                MXFileStore.this.mIsPostProcessingDone = true;
                                synchronized (this) {
                                    MXFileStore.this.mIsReady = true;
                                }
                                MXFileStore.this.mIsOpening = false;
                                if (z || MXFileStore.this.mIsNewStorage) {
                                    MXFileStore.this.mRoomReceiptsToLoad.addAll(MXFileStore.listFiles(MXFileStore.this.mStoreRoomsMessagesReceiptsFolderFile.list()));
                                    MXFileStore.this.mPreloadTime = System.currentTimeMillis() - currentTimeMillis;
                                    if (MXFileStore.this.mMetricsListener != null) {
                                        MXFileStore.this.mMetricsListener.onStorePreloaded(MXFileStore.this.mPreloadTime);
                                    }
                                    Log.m211e(MXFileStore.LOG_TAG, "The store is opened.");
                                    MXFileStore.this.dispatchOnStoreReady(MXFileStore.this.mCredentials.userId);
                                    MXFileStore.this.loadReceipts();
                                    MXFileStore.this.loadUsers();
                                    return;
                                }
                                Log.m211e(MXFileStore.LOG_TAG, "The store is corrupted.");
                                MXFileStore.this.dispatchOnStoreCorrupted(MXFileStore.this.mCredentials.userId, str);
                            }
                        });
                    }
                }).start();
            } else if (this.mIsReady) {
                new Thread(new Runnable() {
                    public void run() {
                        MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                            public void run() {
                                if (MXFileStore.this.mIsPostProcessingDone || MXFileStore.this.mIsNewStorage) {
                                    if (!MXFileStore.this.mIsPostProcessingDone) {
                                        Log.m211e(MXFileStore.LOG_TAG, "## open() : is ready but the post processing was not yet done.");
                                        MXFileStore.this.dispatchPostProcess(MXFileStore.this.mCredentials.userId);
                                        MXFileStore.this.mIsPostProcessingDone = true;
                                    } else {
                                        Log.m211e(MXFileStore.LOG_TAG, "## open() when ready : the post processing is already done.");
                                    }
                                    MXFileStore.this.dispatchOnStoreReady(MXFileStore.this.mCredentials.userId);
                                    MXFileStore.this.mPreloadTime = System.currentTimeMillis() - currentTimeMillis;
                                    if (MXFileStore.this.mMetricsListener != null) {
                                        MXFileStore.this.mMetricsListener.onStorePreloaded(MXFileStore.this.mPreloadTime);
                                    }
                                    return;
                                }
                                Log.m211e(MXFileStore.LOG_TAG, "## open() : is ready but the post processing was not yet done : please wait....");
                            }
                        });
                    }
                }).start();
            }
        }
    }

    public boolean areReceiptsReady() {
        boolean z;
        synchronized (this) {
            z = this.mAreReceiptsReady;
        }
        return z;
    }

    public long getPreloadTime() {
        return this.mPreloadTime;
    }

    public Map<String, Long> getStats() {
        return this.mStoreStats;
    }

    public void close() {
        Log.m209d(LOG_TAG, "Close the store");
        super.close();
        setIsKilled(true);
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
        }
        this.mHandlerThread = null;
    }

    public void clear() {
        Log.m209d(LOG_TAG, "Clear the store");
        super.clear();
        deleteAllData(false);
    }

    /* access modifiers changed from: private */
    public void deleteAllData(boolean z) {
        try {
            ContentUtils.deleteDirectory(this.mStoreFolderFile);
            if (z) {
                createDirTree(this.mCredentials.userId);
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("deleteAllData failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        if (z) {
            initCommon();
        }
        this.mMetadata = null;
        this.mEventStreamToken = null;
        this.mAreUsersLoaded = true;
    }

    public boolean isReady() {
        boolean z;
        synchronized (this) {
            z = this.mIsReady;
        }
        return z;
    }

    private long directorySize(File file) {
        long length;
        long j = 0;
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; i++) {
                    if (listFiles[i].isDirectory()) {
                        length = j + directorySize(listFiles[i]);
                    } else {
                        length = j + listFiles[i].length();
                    }
                    j = length;
                }
            }
        }
        return j;
    }

    public long diskUsage() {
        return directorySize(this.mStoreFolderFile);
    }

    public void setEventStreamToken(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Set token to ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        super.setEventStreamToken(str);
        this.mMetaDataHasChanged = true;
    }

    public boolean setDisplayName(String str, long j) {
        boolean displayName = super.setDisplayName(str, j);
        this.mMetaDataHasChanged = displayName;
        return displayName;
    }

    public boolean setAvatarURL(String str, long j) {
        boolean avatarURL = super.setAvatarURL(str, j);
        this.mMetaDataHasChanged = avatarURL;
        return avatarURL;
    }

    public void setThirdPartyIdentifiers(List<ThirdPartyIdentifier> list) {
        Log.m209d(LOG_TAG, "Set setThirdPartyIdentifiers");
        this.mMetaDataHasChanged = true;
        super.setThirdPartyIdentifiers(list);
    }

    public void setIgnoredUserIdsList(List<String> list) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setIgnoredUsers() : ");
        sb.append(list);
        Log.m209d(str, sb.toString());
        this.mMetaDataHasChanged = true;
        super.setIgnoredUserIdsList(list);
    }

    public void setDirectChatRoomsDict(Map<String, List<String>> map) {
        Log.m209d(LOG_TAG, "## setDirectChatRoomsDict()");
        this.mMetaDataHasChanged = true;
        super.setDirectChatRoomsDict(map);
    }

    public void storeUser(User user) {
        if (!TextUtils.equals(this.mCredentials.userId, user.user_id)) {
            this.mUserIdsToCommit.add(user.user_id);
        }
        super.storeUser(user);
    }

    public void flushRoomEvents(String str) {
        super.flushRoomEvents(str);
        this.mRoomsToCommitForMessages.add(str);
        if (this.mMetadata != null && this.mMetadata.mAccessToken != null && !isKilled()) {
            saveRoomsMessages();
        }
    }

    public void storeRoomEvents(String str, TokensChunkResponse<Event> tokensChunkResponse, Direction direction) {
        boolean z = true;
        if (direction == Direction.BACKWARDS) {
            LinkedHashMap linkedHashMap = (LinkedHashMap) this.mRoomEvents.get(str);
            if (linkedHashMap != null) {
                if (linkedHashMap.size() >= 50) {
                    z = false;
                }
                if (!z) {
                    Log.m209d(LOG_TAG, "storeRoomEvents : do not flush because reaching the max size");
                }
            }
        }
        super.storeRoomEvents(str, tokensChunkResponse, direction);
        if (z) {
            this.mRoomsToCommitForMessages.add(str);
        }
    }

    public void storeLiveRoomEvent(Event event) {
        super.storeLiveRoomEvent(event);
        this.mRoomsToCommitForMessages.add(event.roomId);
    }

    public void deleteEvent(Event event) {
        super.deleteEvent(event);
        this.mRoomsToCommitForMessages.add(event.roomId);
    }

    private void deleteRoomMessagesFiles(String str) {
        File file = new File(this.mGzStoreRoomsMessagesFolderFile, str);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deleteRoomMessagesFiles - messagesListFile failed ");
                sb.append(e.getMessage());
                Log.m209d(str2, sb.toString());
            }
        }
        File file2 = new File(this.mStoreRoomsTokensFolderFile, str);
        if (file2.exists()) {
            try {
                file2.delete();
            } catch (Exception e2) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("deleteRoomMessagesFiles - tokenFile failed ");
                sb2.append(e2.getMessage());
                Log.m209d(str3, sb2.toString());
            }
        }
    }

    public void deleteRoom(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("deleteRoom ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        super.deleteRoom(str);
        deleteRoomMessagesFiles(str);
        deleteRoomStateFile(str);
        deleteRoomSummaryFile(str);
        deleteRoomReceiptsFile(str);
        deleteRoomAccountDataFile(str);
    }

    public void deleteAllRoomMessages(String str, boolean z) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("deleteAllRoomMessages ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        super.deleteAllRoomMessages(str, z);
        if (!z) {
            deleteRoomMessagesFiles(str);
        }
        deleteRoomSummaryFile(str);
        this.mRoomsToCommitForMessages.add(str);
        this.mRoomsToCommitForSummaries.add(str);
    }

    public void storeLiveStateForRoom(String str) {
        super.storeLiveStateForRoom(str);
        this.mRoomsToCommitForStates.add(str);
    }

    public void flushSummary(RoomSummary roomSummary) {
        super.flushSummary(roomSummary);
        this.mRoomsToCommitForSummaries.add(roomSummary.getRoomId());
        if (this.mMetadata != null && this.mMetadata.mAccessToken != null && !isKilled()) {
            saveSummaries();
        }
    }

    public void flushSummaries() {
        super.flushSummaries();
        this.mRoomsToCommitForSummaries.addAll(this.mRoomSummaries.keySet());
        if (this.mMetadata != null && this.mMetadata.mAccessToken != null && !isKilled()) {
            saveSummaries();
        }
    }

    public void storeSummary(RoomSummary roomSummary) {
        super.storeSummary(roomSummary);
        if (roomSummary != null && roomSummary.getRoomId() != null && !this.mRoomsToCommitForSummaries.contains(roomSummary.getRoomId())) {
            this.mRoomsToCommitForSummaries.add(roomSummary.getRoomId());
        }
    }

    private void saveUsers() {
        final HashSet hashSet;
        if (this.mAreUsersLoaded && this.mUserIdsToCommit.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet2 = this.mUserIdsToCommit;
            this.mUserIdsToCommit = new HashSet<>();
            try {
                synchronized (this.mUsers) {
                    hashSet = new HashSet(this.mUsers.values());
                }
                new Thread(new Runnable() {
                    public void run() {
                        MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                            public void run() {
                                User user;
                                if (!MXFileStore.this.isKilled()) {
                                    String access$000 = MXFileStore.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("saveUsers ");
                                    sb.append(hashSet2.size());
                                    sb.append(" users (");
                                    sb.append(hashSet.size());
                                    sb.append(" known ones)");
                                    Log.m209d(access$000, sb.toString());
                                    long currentTimeMillis = System.currentTimeMillis();
                                    HashMap hashMap = new HashMap();
                                    Iterator it = hashSet2.iterator();
                                    while (it.hasNext()) {
                                        String str = (String) it.next();
                                        synchronized (MXFileStore.this.mUsers) {
                                            user = (User) MXFileStore.this.mUsers.get(str);
                                        }
                                        if (user != null) {
                                            int storageHashKey = user.getStorageHashKey();
                                            if (!hashMap.containsKey(Integer.valueOf(storageHashKey))) {
                                                hashMap.put(Integer.valueOf(storageHashKey), new ArrayList());
                                            }
                                        }
                                    }
                                    Iterator it2 = hashSet.iterator();
                                    while (it2.hasNext()) {
                                        User user2 = (User) it2.next();
                                        if (hashMap.containsKey(Integer.valueOf(user2.getStorageHashKey()))) {
                                            ((ArrayList) hashMap.get(Integer.valueOf(user2.getStorageHashKey()))).add(user2);
                                        }
                                    }
                                    for (Integer intValue : hashMap.keySet()) {
                                        int intValue2 = intValue.intValue();
                                        MXFileStore mXFileStore = MXFileStore.this;
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("saveUser ");
                                        sb2.append(intValue2);
                                        String sb3 = sb2.toString();
                                        File access$2600 = MXFileStore.this.mStoreUserFolderFile;
                                        StringBuilder sb4 = new StringBuilder();
                                        sb4.append(intValue2);
                                        sb4.append("");
                                        mXFileStore.writeObject(sb3, new File(access$2600, sb4.toString()), hashMap.get(Integer.valueOf(intValue2)));
                                    }
                                    String access$0002 = MXFileStore.LOG_TAG;
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append("saveUsers done in ");
                                    sb5.append(System.currentTimeMillis() - currentTimeMillis);
                                    sb5.append(" ms");
                                    Log.m209d(access$0002, sb5.toString());
                                }
                            }
                        });
                    }
                }).start();
            } catch (OutOfMemoryError e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("saveUser : cannot clone the users list");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadUsers() {
        List<String> listFiles = listFiles(this.mStoreUserFolderFile.list());
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        for (String str : listFiles) {
            File file = new File(this.mStoreUserFolderFile, str);
            StringBuilder sb = new StringBuilder();
            sb.append("loadUsers ");
            sb.append(str);
            Object readObject = readObject(sb.toString(), file);
            if (readObject != null) {
                try {
                    arrayList.addAll((List) readObject);
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("loadUsers failed : ");
                    sb2.append(e.toString());
                    Log.m211e(str2, sb2.toString());
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            synchronized (this.mUsers) {
                User user2 = (User) this.mUsers.get(user.user_id);
                if (user2 == null || user2.isRetrievedFromRoomMember() || user2.getLatestPresenceTs() < user.getLatestPresenceTs()) {
                    this.mUsers.put(user.user_id, user);
                }
            }
        }
        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
        String str3 = LOG_TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("loadUsers (");
        sb3.append(listFiles.size());
        sb3.append(" files) : retrieve ");
        sb3.append(this.mUsers.size());
        sb3.append(" users in ");
        sb3.append(currentTimeMillis2);
        sb3.append("ms");
        Log.m211e(str3, sb3.toString());
        this.mStoreStats.put("loadUsers", Long.valueOf(currentTimeMillis2));
        this.mAreUsersLoaded = true;
        saveUsers();
    }

    private LinkedHashMap<String, Event> getSavedEventsMap(String str) {
        LinkedHashMap linkedHashMap;
        ArrayList arrayList;
        synchronized (mRoomEventsLock) {
            linkedHashMap = (LinkedHashMap) this.mRoomEvents.get(str);
        }
        synchronized (mRoomEventsLock) {
            arrayList = new ArrayList(linkedHashMap.values());
        }
        int i = 0;
        if (arrayList.size() > 50) {
            i = arrayList.size() - 50;
            while (!((Event) arrayList.get(i)).hasToken() && i > 0) {
                i--;
            }
            if (i > 0) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getSavedEveventsMap() : ");
                sb.append(str);
                sb.append(" reduce the number of messages ");
                sb.append(arrayList.size());
                sb.append(" -> ");
                sb.append(arrayList.size() - i);
                Log.m209d(str2, sb.toString());
            }
        }
        LinkedHashMap<String, Event> linkedHashMap2 = new LinkedHashMap<>();
        while (i < arrayList.size()) {
            Event event = (Event) arrayList.get(i);
            linkedHashMap2.put(event.eventId, event);
            i++;
        }
        return linkedHashMap2;
    }

    /* access modifiers changed from: private */
    public void saveRoomMessages(String str) {
        LinkedHashMap linkedHashMap;
        synchronized (mRoomEventsLock) {
            linkedHashMap = (LinkedHashMap) this.mRoomEvents.get(str);
        }
        String str2 = (String) this.mRoomTokens.get(str);
        if (linkedHashMap == null || str2 == null) {
            deleteRoomMessagesFiles(str);
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            LinkedHashMap savedEventsMap = getSavedEventsMap(str);
            StringBuilder sb = new StringBuilder();
            sb.append("saveRoomsMessage ");
            sb.append(str);
            if (writeObject(sb.toString(), new File(this.mGzStoreRoomsMessagesFolderFile, str), savedEventsMap)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("saveRoomsMessage ");
                sb2.append(str);
                if (writeObject(sb2.toString(), new File(this.mStoreRoomsTokensFolderFile, str), str2)) {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("saveRoomsMessage (");
                    sb3.append(str);
                    sb3.append(") : ");
                    sb3.append(savedEventsMap.size());
                    sb3.append(" messages saved in ");
                    sb3.append(System.currentTimeMillis() - currentTimeMillis);
                    sb3.append(" ms");
                    Log.m209d(str3, sb3.toString());
                }
            }
        }
    }

    private void saveRoomsMessages() {
        if (this.mRoomsToCommitForMessages.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet = this.mRoomsToCommitForMessages;
            this.mRoomsToCommitForMessages = new HashSet<>();
            new Thread(new Runnable() {
                public void run() {
                    MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                        public void run() {
                            if (!MXFileStore.this.isKilled()) {
                                long currentTimeMillis = System.currentTimeMillis();
                                Iterator it = hashSet.iterator();
                                while (it.hasNext()) {
                                    MXFileStore.this.saveRoomMessages((String) it.next());
                                }
                                String access$000 = MXFileStore.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("saveRoomsMessages : ");
                                sb.append(hashSet.size());
                                sb.append(" rooms in ");
                                sb.append(System.currentTimeMillis() - currentTimeMillis);
                                sb.append(" ms");
                                Log.m209d(access$000, sb.toString());
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private boolean loadRoomMessages(String str) {
        LinkedHashMap linkedHashMap;
        File file = new File(this.mGzStoreRoomsMessagesFolderFile, str);
        boolean z = false;
        if (file.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append("events ");
            sb.append(str);
            Object readObject = readObject(sb.toString(), file);
            if (readObject == null) {
                return false;
            }
            try {
                linkedHashMap = (LinkedHashMap) readObject;
                if (linkedHashMap.size() > 100) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## loadRoomMessages() : the room ");
                    sb2.append(str);
                    sb2.append(" has ");
                    sb2.append(linkedHashMap.size());
                    sb2.append(" stored events : we need to find a way to reduce it.");
                    Log.m209d(str2, sb2.toString());
                }
                for (Event event : linkedHashMap.values()) {
                    if (event.mSentState == SentState.UNDELIVERABLE || event.mSentState == SentState.UNSENT || event.mSentState == SentState.SENDING || event.mSentState == SentState.WAITING_RETRY || event.mSentState == SentState.ENCRYPTING) {
                        event.mSentState = SentState.UNDELIVERABLE;
                        z = true;
                    }
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("loadRoomMessages ");
                sb3.append(str);
                sb3.append("failed : ");
                sb3.append(e.getMessage());
                Log.m211e(str3, sb3.toString());
                return false;
            }
        } else {
            linkedHashMap = null;
        }
        if (linkedHashMap != null) {
            Room room = new Room();
            room.init(this, str, null);
            room.setReadyState(true);
            storeRoom(room);
            this.mRoomEvents.put(str, linkedHashMap);
        }
        if (z) {
            saveRoomMessages(str);
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0079  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean loadRoomToken(java.lang.String r7) {
        /*
            r6 = this;
            com.opengarden.firechat.matrixsdk.data.Room r0 = r6.getRoom(r7)
            r1 = 1
            if (r0 == 0) goto L_0x007d
            r0 = 0
            r2 = 0
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0055 }
            java.io.File r4 = r6.mStoreRoomsTokensFolderFile     // Catch:{ Exception -> 0x0055 }
            r3.<init>(r4, r7)     // Catch:{ Exception -> 0x0055 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0055 }
            r4.<init>()     // Catch:{ Exception -> 0x0055 }
            java.lang.String r5 = "loadRoomToken "
            r4.append(r5)     // Catch:{ Exception -> 0x0055 }
            r4.append(r7)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0055 }
            java.lang.Object r3 = r6.readObject(r4, r3)     // Catch:{ Exception -> 0x0055 }
            if (r3 != 0) goto L_0x0028
            goto L_0x0070
        L_0x0028:
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0055 }
            java.util.Map r0 = r6.mRoomEvents     // Catch:{ Exception -> 0x0051 }
            java.lang.Object r0 = r0.get(r7)     // Catch:{ Exception -> 0x0051 }
            java.util.LinkedHashMap r0 = (java.util.LinkedHashMap) r0     // Catch:{ Exception -> 0x0051 }
            if (r0 == 0) goto L_0x004f
            int r4 = r0.size()     // Catch:{ Exception -> 0x0051 }
            if (r4 <= 0) goto L_0x004f
            java.util.Collection r0 = r0.values()     // Catch:{ Exception -> 0x0051 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x0051 }
            java.lang.Object r0 = r0.next()     // Catch:{ Exception -> 0x0051 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = (com.opengarden.firechat.matrixsdk.rest.model.Event) r0     // Catch:{ Exception -> 0x0051 }
            java.lang.String r4 = r0.mToken     // Catch:{ Exception -> 0x0051 }
            if (r4 == 0) goto L_0x004f
            java.lang.String r0 = r0.mToken     // Catch:{ Exception -> 0x0051 }
            goto L_0x0071
        L_0x004f:
            r0 = r3
            goto L_0x0071
        L_0x0051:
            r0 = move-exception
            r1 = r0
            r0 = r3
            goto L_0x0056
        L_0x0055:
            r1 = move-exception
        L_0x0056:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "loadRoomToken failed : "
            r4.append(r5)
            java.lang.String r1 = r1.toString()
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r1)
        L_0x0070:
            r1 = 0
        L_0x0071:
            if (r0 == 0) goto L_0x0079
            java.util.Map r2 = r6.mRoomTokens
            r2.put(r7, r0)
            goto L_0x00a3
        L_0x0079:
            r6.deleteRoom(r7)
            goto L_0x00a3
        L_0x007d:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0088 }
            java.io.File r2 = r6.mStoreRoomsTokensFolderFile     // Catch:{ Exception -> 0x0088 }
            r0.<init>(r2, r7)     // Catch:{ Exception -> 0x0088 }
            r0.delete()     // Catch:{ Exception -> 0x0088 }
            goto L_0x00a3
        L_0x0088:
            r7 = move-exception
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "loadRoomToken failed with error "
            r2.append(r3)
            java.lang.String r7 = r7.getMessage()
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r7)
        L_0x00a3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.store.MXFileStore.loadRoomToken(java.lang.String):boolean");
    }

    /* access modifiers changed from: private */
    public boolean loadRoomsMessages() {
        try {
            List<String> listFiles = listFiles(this.mGzStoreRoomsMessagesFolderFile.list());
            long currentTimeMillis = System.currentTimeMillis();
            boolean z = true;
            for (String str : listFiles) {
                if (z) {
                    z &= loadRoomMessages(str);
                }
            }
            if (z) {
                long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("loadRoomMessages : ");
                sb.append(listFiles.size());
                sb.append(" rooms in ");
                sb.append(currentTimeMillis2);
                sb.append(" ms");
                Log.m209d(str2, sb.toString());
                this.mStoreStats.put("loadRoomMessages", Long.valueOf(currentTimeMillis2));
            }
            List<String> listFiles2 = listFiles(this.mStoreRoomsTokensFolderFile.list());
            long currentTimeMillis3 = System.currentTimeMillis();
            for (String str3 : listFiles2) {
                if (z) {
                    z &= loadRoomToken(str3);
                }
            }
            if (!z) {
                return z;
            }
            String str4 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadRoomToken : ");
            sb2.append(listFiles2.size());
            sb2.append(" rooms in ");
            sb2.append(System.currentTimeMillis() - currentTimeMillis3);
            sb2.append(" ms");
            Log.m209d(str4, sb2.toString());
            return z;
        } catch (Exception e) {
            String str5 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("loadRoomToken failed : ");
            sb3.append(e.getMessage());
            Log.m211e(str5, sb3.toString());
            return false;
        }
    }

    public void getRoomStateEvents(String str, ApiCallback<List<Event>> apiCallback) {
        super.getRoomStateEvents(str, apiCallback);
    }

    private void deleteRoomStateFile(String str) {
        File file = new File(this.mGzStoreRoomsStateFolderFile, str);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deleteRoomStateFile failed with error ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        File file2 = new File(this.mGzStoreRoomsStateEventsFolderFile, str);
        if (file2.exists()) {
            try {
                file2.delete();
            } catch (Exception e2) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("deleteRoomStateFile failed with error ");
                sb2.append(e2.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveRoomState(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("++ saveRoomsState ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        File file = new File(this.mGzStoreRoomsStateFolderFile, str);
        Room room = (Room) this.mRooms.get(str);
        if (room != null) {
            long currentTimeMillis = System.currentTimeMillis();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("saveRoomsState ");
            sb2.append(str);
            writeObject(sb2.toString(), file, room.getState());
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("saveRoomsState ");
            sb3.append(room.getState().getMembers().size());
            sb3.append(" members : ");
            sb3.append(System.currentTimeMillis() - currentTimeMillis);
            sb3.append(" ms");
            Log.m209d(str3, sb3.toString());
        } else {
            Log.m209d(LOG_TAG, "saveRoomsState : delete the room state");
            deleteRoomStateFile(str);
        }
        String str4 = LOG_TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("-- saveRoomsState ");
        sb4.append(str);
        Log.m209d(str4, sb4.toString());
    }

    private void saveRoomStates() {
        if (this.mRoomsToCommitForStates.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet = this.mRoomsToCommitForStates;
            this.mRoomsToCommitForStates = new HashSet<>();
            new Thread(new Runnable() {
                public void run() {
                    MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                        public void run() {
                            if (!MXFileStore.this.isKilled()) {
                                long currentTimeMillis = System.currentTimeMillis();
                                Iterator it = hashSet.iterator();
                                while (it.hasNext()) {
                                    MXFileStore.this.saveRoomState((String) it.next());
                                }
                                String access$000 = MXFileStore.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("saveRoomsState : ");
                                sb.append(hashSet.size());
                                sb.append(" rooms in ");
                                sb.append(System.currentTimeMillis() - currentTimeMillis);
                                sb.append(" ms");
                                Log.m209d(access$000, sb.toString());
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private boolean loadRoomState(String str) {
        Room room = getRoom(str);
        boolean z = true;
        if (room != null) {
            RoomState roomState = null;
            try {
                File file = new File(this.mGzStoreRoomsStateFolderFile, str);
                if (file.exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("loadRoomState ");
                    sb.append(str);
                    Object readObject = readObject(sb.toString(), file);
                    if (readObject == null) {
                        z = false;
                    } else {
                        roomState = (RoomState) readObject;
                    }
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("loadRoomState failed : ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
            if (roomState != null) {
                room.getLiveTimeLine().setState(roomState);
            } else {
                deleteRoom(str);
            }
        } else {
            try {
                new File(this.mGzStoreRoomsStateFolderFile, str).delete();
            } catch (Exception e2) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("loadRoomState failed to delete a file : ");
                sb3.append(e2.getMessage());
                Log.m211e(str3, sb3.toString());
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean loadRoomsState() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            List<String> listFiles = listFiles(this.mGzStoreRoomsStateFolderFile.list());
            boolean z = true;
            for (String str : listFiles) {
                if (z) {
                    z &= loadRoomState(str);
                }
            }
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("loadRoomsState ");
            sb.append(listFiles.size());
            sb.append(" rooms in ");
            sb.append(currentTimeMillis2);
            sb.append(" ms");
            Log.m209d(str2, sb.toString());
            this.mStoreStats.put("loadRoomsState", Long.valueOf(currentTimeMillis2));
            return z;
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadRoomsState failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void deleteRoomAccountDataFile(String str) {
        File file = new File(this.mStoreRoomsAccountDataFolderFile, str);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deleteRoomAccountDataFile failed : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    private void saveRoomsAccountData() {
        if (this.mRoomsToCommitForAccountData.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet = this.mRoomsToCommitForAccountData;
            this.mRoomsToCommitForAccountData = new HashSet<>();
            new Thread(new Runnable() {
                public void run() {
                    MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                        public void run() {
                            if (!MXFileStore.this.isKilled()) {
                                long currentTimeMillis = System.currentTimeMillis();
                                Iterator it = hashSet.iterator();
                                while (it.hasNext()) {
                                    String str = (String) it.next();
                                    RoomAccountData roomAccountData = (RoomAccountData) MXFileStore.this.mRoomAccountData.get(str);
                                    if (roomAccountData != null) {
                                        MXFileStore mXFileStore = MXFileStore.this;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("saveRoomsAccountData ");
                                        sb.append(str);
                                        mXFileStore.writeObject(sb.toString(), new File(MXFileStore.this.mStoreRoomsAccountDataFolderFile, str), roomAccountData);
                                    } else {
                                        MXFileStore.this.deleteRoomAccountDataFile(str);
                                    }
                                }
                                String access$000 = MXFileStore.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("saveSummaries : ");
                                sb2.append(hashSet.size());
                                sb2.append(" account data in ");
                                sb2.append(System.currentTimeMillis() - currentTimeMillis);
                                sb2.append(" ms");
                                Log.m209d(access$000, sb2.toString());
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private boolean loadRoomAccountData(String str) {
        boolean z = false;
        RoomAccountData roomAccountData = null;
        try {
            File file = new File(this.mStoreRoomsAccountDataFolderFile, str);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                sb.append("loadRoomAccountData ");
                sb.append(str);
                Object readObject = readObject(sb.toString(), file);
                if (readObject == null) {
                    Log.m211e(LOG_TAG, "loadRoomAccountData failed");
                    return false;
                }
                roomAccountData = (RoomAccountData) readObject;
            }
            z = true;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadRoomAccountData failed : ");
            sb2.append(e.toString());
            Log.m211e(str2, sb2.toString());
        }
        if (roomAccountData != null) {
            Room room = getRoom(str);
            if (room != null) {
                room.setAccountData(roomAccountData);
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean loadRoomsAccountData() {
        try {
            List<String> listFiles = listFiles(this.mStoreRoomsAccountDataFolderFile.list());
            long currentTimeMillis = System.currentTimeMillis();
            boolean z = true;
            for (String loadRoomAccountData : listFiles) {
                z &= loadRoomAccountData(loadRoomAccountData);
            }
            if (!z) {
                return z;
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("loadRoomsAccountData : ");
            sb.append(listFiles.size());
            sb.append(" rooms in ");
            sb.append(System.currentTimeMillis() - currentTimeMillis);
            sb.append(" ms");
            Log.m209d(str, sb.toString());
            return z;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadRoomsAccountData failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
            return false;
        }
    }

    public void storeAccountData(String str, RoomAccountData roomAccountData) {
        super.storeAccountData(str, roomAccountData);
        if (str != null && ((Room) this.mRooms.get(str)) != null && roomAccountData != null) {
            this.mRoomsToCommitForAccountData.add(str);
        }
    }

    /* access modifiers changed from: private */
    public void deleteRoomSummaryFile(String str) {
        File file = new File(this.mStoreRoomsSummaryFolderFile, str);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deleteRoomSummaryFile failed : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    private void saveSummaries() {
        if (this.mRoomsToCommitForSummaries.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet = this.mRoomsToCommitForSummaries;
            this.mRoomsToCommitForSummaries = new HashSet<>();
            new Thread(new Runnable() {
                public void run() {
                    MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                        public void run() {
                            if (!MXFileStore.this.isKilled()) {
                                long currentTimeMillis = System.currentTimeMillis();
                                Iterator it = hashSet.iterator();
                                while (it.hasNext()) {
                                    String str = (String) it.next();
                                    try {
                                        File file = new File(MXFileStore.this.mStoreRoomsSummaryFolderFile, str);
                                        RoomSummary roomSummary = (RoomSummary) MXFileStore.this.mRoomSummaries.get(str);
                                        if (roomSummary != null) {
                                            MXFileStore mXFileStore = MXFileStore.this;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("saveSummaries ");
                                            sb.append(str);
                                            mXFileStore.writeObject(sb.toString(), file, roomSummary);
                                        } else {
                                            MXFileStore.this.deleteRoomSummaryFile(str);
                                        }
                                    } catch (OutOfMemoryError e) {
                                        MXFileStore.this.dispatchOOM(e);
                                    } catch (Exception e2) {
                                        String access$000 = MXFileStore.LOG_TAG;
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("saveSummaries failed : ");
                                        sb2.append(e2.getMessage());
                                        Log.m211e(access$000, sb2.toString());
                                    }
                                }
                                String access$0002 = MXFileStore.LOG_TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("saveSummaries : ");
                                sb3.append(hashSet.size());
                                sb3.append(" summaries in ");
                                sb3.append(System.currentTimeMillis() - currentTimeMillis);
                                sb3.append(" ms");
                                Log.m209d(access$0002, sb3.toString());
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private boolean loadSummary(String str) {
        RoomSummary roomSummary;
        boolean z = false;
        try {
            File file = new File(this.mStoreRoomsSummaryFolderFile, str);
            StringBuilder sb = new StringBuilder();
            sb.append("loadSummary ");
            sb.append(str);
            Object readObject = readObject(sb.toString(), file);
            if (readObject == null) {
                Log.m211e(LOG_TAG, "loadSummary failed");
                return false;
            }
            roomSummary = (RoomSummary) readObject;
            z = true;
            if (roomSummary != null) {
                Room room = getRoom(roomSummary.getRoomId());
                if (room != null) {
                    roomSummary.setLatestRoomState(room.getState());
                }
                this.mRoomSummaries.put(str, roomSummary);
            }
            return z;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadSummary failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
            roomSummary = null;
        }
    }

    /* access modifiers changed from: private */
    public boolean loadSummaries() {
        try {
            List<String> listFiles = listFiles(this.mStoreRoomsSummaryFolderFile.list());
            long currentTimeMillis = System.currentTimeMillis();
            boolean z = true;
            for (String loadSummary : listFiles) {
                z &= loadSummary(loadSummary);
            }
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("loadSummaries ");
            sb.append(listFiles.size());
            sb.append(" rooms in ");
            sb.append(currentTimeMillis2);
            sb.append(" ms");
            Log.m209d(str, sb.toString());
            this.mStoreStats.put("loadSummaries", Long.valueOf(currentTimeMillis2));
            return z;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadSummaries failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
            return false;
        }
    }

    private void loadMetaData() {
        long currentTimeMillis = System.currentTimeMillis();
        this.mEventStreamToken = null;
        this.mMetadata = null;
        File file = new File(this.mStoreFolderFile, "MXFileStore");
        if (file.exists()) {
            Object readObject = readObject("loadMetaData", file);
            if (readObject != null) {
                try {
                    this.mMetadata = (MXFileStoreMetaData) readObject;
                    if (this.mMetadata.mUserDisplayName != null) {
                        this.mMetadata.mUserDisplayName.trim();
                    }
                    this.mEventStreamToken = this.mMetadata.mEventStreamToken;
                } catch (Exception unused) {
                    Log.m211e(LOG_TAG, "## loadMetaData() : is corrupted");
                    return;
                }
            }
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("loadMetaData : ");
        sb.append(System.currentTimeMillis() - currentTimeMillis);
        sb.append(" ms");
        Log.m209d(str, sb.toString());
    }

    private void saveMetaData() {
        if (this.mMetaDataHasChanged && this.mFileStoreHandler != null && this.mMetadata != null) {
            this.mMetaDataHasChanged = false;
            final MXFileStoreMetaData deepCopy = this.mMetadata.deepCopy();
            new Thread(new Runnable() {
                public void run() {
                    MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                        public void run() {
                            if (MXFileStore.this.mIsKilled) {
                                return;
                            }
                            if (MXFileStore.this.mMetadata.mEventStreamToken != null) {
                                long currentTimeMillis = System.currentTimeMillis();
                                MXFileStore.this.writeObject("saveMetaData", new File(MXFileStore.this.mStoreFolderFile, "MXFileStore"), deepCopy);
                                String access$000 = MXFileStore.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("saveMetaData : ");
                                sb.append(System.currentTimeMillis() - currentTimeMillis);
                                sb.append(" ms");
                                Log.m209d(access$000, sb.toString());
                                return;
                            }
                            Log.m211e(MXFileStore.LOG_TAG, "## saveMetaData() : cancelled because mEventStreamToken is null");
                        }
                    });
                }
            }).start();
        }
    }

    public List<ReceiptData> getEventReceipts(String str, String str2, boolean z, boolean z2) {
        synchronized (this.mRoomReceiptsToLoad) {
            if (this.mRoomReceiptsToLoad.indexOf(str) >= 2) {
                this.mRoomReceiptsToLoad.remove(str);
                this.mRoomReceiptsToLoad.add(1, str);
            }
        }
        return super.getEventReceipts(str, str2, z, z2);
    }

    public boolean storeReceipt(ReceiptData receiptData, String str) {
        boolean storeReceipt = super.storeReceipt(receiptData, str);
        if (storeReceipt) {
            synchronized (this) {
                this.mRoomsToCommitForReceipts.add(str);
            }
        }
        return storeReceipt;
    }

    private boolean loadReceipts(String str) {
        HashMap hashMap;
        Map map;
        File file = new File(this.mStoreRoomsMessagesReceiptsFolderFile, str);
        if (file.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append("loadReceipts ");
            sb.append(str);
            Object readObject = readObject(sb.toString(), file);
            if (readObject == null) {
                return false;
            }
            try {
                List<ReceiptData> list = (List) readObject;
                hashMap = new HashMap();
                for (ReceiptData receiptData : list) {
                    hashMap.put(receiptData.userId, receiptData);
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("loadReceipts failed : ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
                return false;
            }
        } else {
            hashMap = null;
        }
        if (hashMap != null) {
            synchronized (this.mReceiptsByRoomIdLock) {
                map = (Map) this.mReceiptsByRoomId.get(str);
                this.mReceiptsByRoomId.put(str, hashMap);
            }
            if (map != null) {
                for (ReceiptData storeReceipt : map.values()) {
                    storeReceipt(storeReceipt, str);
                }
            }
            dispatchOnReadReceiptsLoaded(str);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean loadReceipts() {
        String str;
        boolean z = false;
        try {
            int size = this.mRoomReceiptsToLoad.size();
            long currentTimeMillis = System.currentTimeMillis();
            while (this.mRoomReceiptsToLoad.size() > 0) {
                synchronized (this.mRoomReceiptsToLoad) {
                    str = (String) this.mRoomReceiptsToLoad.get(0);
                }
                loadReceipts(str);
                synchronized (this.mRoomReceiptsToLoad) {
                    this.mRoomReceiptsToLoad.remove(0);
                }
            }
            saveReceipts();
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("loadReceipts ");
            sb.append(size);
            sb.append(" rooms in ");
            sb.append(currentTimeMillis2);
            sb.append(" ms");
            Log.m209d(str2, sb.toString());
            this.mStoreStats.put("loadReceipts", Long.valueOf(currentTimeMillis2));
            z = true;
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("loadReceipts failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
        }
        synchronized (this) {
            this.mAreReceiptsReady = true;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
        if (r3.mReceiptsByRoomId.containsKey(r4) == false) goto L_0x002b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0019, code lost:
        r0 = new java.util.ArrayList(((java.util.Map) r3.mReceiptsByRoomId.get(r4)).values());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002c, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002d, code lost:
        if (r0 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        new java.lang.Thread(new com.opengarden.firechat.matrixsdk.data.store.MXFileStore.C26299(r3)).start();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000e, code lost:
        r1 = r3.mReceiptsByRoomIdLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        monitor-enter(r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveReceipts(final java.lang.String r4) {
        /*
            r3 = this;
            java.util.List<java.lang.String> r0 = r3.mRoomReceiptsToLoad
            monitor-enter(r0)
            java.util.List<java.lang.String> r1 = r3.mRoomReceiptsToLoad     // Catch:{ all -> 0x0041 }
            boolean r1 = r1.contains(r4)     // Catch:{ all -> 0x0041 }
            if (r1 == 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return
        L_0x000d:
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            java.lang.Object r1 = r3.mReceiptsByRoomIdLock
            monitor-enter(r1)
            java.util.Map r0 = r3.mReceiptsByRoomId     // Catch:{ all -> 0x003e }
            boolean r0 = r0.containsKey(r4)     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x002b
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x003e }
            java.util.Map r2 = r3.mReceiptsByRoomId     // Catch:{ all -> 0x003e }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ all -> 0x003e }
            java.util.Map r2 = (java.util.Map) r2     // Catch:{ all -> 0x003e }
            java.util.Collection r2 = r2.values()     // Catch:{ all -> 0x003e }
            r0.<init>(r2)     // Catch:{ all -> 0x003e }
            goto L_0x002c
        L_0x002b:
            r0 = 0
        L_0x002c:
            monitor-exit(r1)     // Catch:{ all -> 0x003e }
            if (r0 != 0) goto L_0x0030
            return
        L_0x0030:
            com.opengarden.firechat.matrixsdk.data.store.MXFileStore$9 r1 = new com.opengarden.firechat.matrixsdk.data.store.MXFileStore$9
            r1.<init>(r4, r0)
            java.lang.Thread r4 = new java.lang.Thread
            r4.<init>(r1)
            r4.start()
            return
        L_0x003e:
            r4 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x003e }
            throw r4
        L_0x0041:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.store.MXFileStore.saveReceipts(java.lang.String):void");
    }

    private void saveReceipts() {
        synchronized (this) {
            Iterator it = this.mRoomsToCommitForReceipts.iterator();
            while (it.hasNext()) {
                saveReceipts((String) it.next());
            }
            this.mRoomsToCommitForReceipts.clear();
        }
    }

    private void deleteRoomReceiptsFile(String str) {
        File file = new File(this.mStoreRoomsMessagesReceiptsFolderFile, str);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deleteReceiptsFile - failed ");
                sb.append(e.getMessage());
                Log.m209d(str2, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean writeObject(String str, File file, Object obj) {
        String parent = file.getParent();
        String name = file.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(".tmp");
        File file2 = new File(parent, sb.toString());
        if (file2.exists()) {
            file2.delete();
        }
        if (file.exists()) {
            file.renameTo(file2);
        }
        boolean z = false;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(CompatUtil.createGzipOutputStream(new FileOutputStream(file)));
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            z = true;
        } catch (OutOfMemoryError e) {
            dispatchOOM(e);
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## writeObject()  ");
            sb2.append(str);
            sb2.append(" : failed ");
            sb2.append(e2.getMessage());
            Log.m211e(str2, sb2.toString());
        }
        if (z) {
            file2.delete();
        } else {
            file2.renameTo(file);
        }
        return z;
    }

    private Object readObject(String str, File file) {
        Object obj;
        String parent = file.getParent();
        String name = file.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(".tmp");
        File file2 = new File(parent, sb.toString());
        if (file2.exists()) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## readObject : rescue from a tmp file ");
            sb2.append(file2.getName());
            Log.m211e(str2, sb2.toString());
            file = file2;
        }
        Object obj2 = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            obj = objectInputStream.readObject();
            try {
                objectInputStream.close();
                return obj;
            } catch (OutOfMemoryError e) {
                e = e;
            } catch (Exception e2) {
                Exception exc = e2;
                obj2 = obj;
                e = exc;
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## readObject()  ");
                sb3.append(str);
                sb3.append(" : failed ");
                sb3.append(e.getMessage());
                Log.m211e(str3, sb3.toString());
                return obj2;
            }
        } catch (OutOfMemoryError e3) {
            e = e3;
            obj = null;
            dispatchOOM(e);
            return obj;
        } catch (Exception e4) {
            e = e4;
            String str32 = LOG_TAG;
            StringBuilder sb32 = new StringBuilder();
            sb32.append("## readObject()  ");
            sb32.append(str);
            sb32.append(" : failed ");
            sb32.append(e.getMessage());
            Log.m211e(str32, sb32.toString());
            return obj2;
        }
    }

    /* access modifiers changed from: private */
    public static List<String> listFiles(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (strArr != null) {
            for (String str : strArr) {
                if (!str.endsWith(".tmp")) {
                    arrayList.add(str);
                } else {
                    arrayList2.add(str.substring(0, str.length() - ".tmp".length()));
                }
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (!arrayList.contains(str2)) {
                    String str3 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## listFiles() : ");
                    sb.append(str2);
                    sb.append(" does not exist but a tmp file has been retrieved");
                    Log.m211e(str3, sb.toString());
                    arrayList.add(str2);
                }
            }
        }
        return arrayList;
    }

    public void post(Runnable runnable) {
        if (this.mFileStoreHandler != null) {
            this.mFileStoreHandler.post(runnable);
        } else {
            super.post(runnable);
        }
    }

    public void storeGroup(Group group) {
        super.storeGroup(group);
        if (group != null && !TextUtils.isEmpty(group.getGroupId())) {
            this.mGroupsToCommit.add(group.getGroupId());
        }
    }

    public void flushGroup(Group group) {
        super.flushGroup(group);
        if (group != null && !TextUtils.isEmpty(group.getGroupId())) {
            this.mGroupsToCommit.add(group.getGroupId());
            saveGroups();
        }
    }

    public void deleteGroup(String str) {
        super.deleteGroup(str);
        if (!TextUtils.isEmpty(str)) {
            this.mGroupsToCommit.add(str);
        }
    }

    private void saveGroups() {
        if (this.mGroupsToCommit.size() > 0 && this.mFileStoreHandler != null) {
            final HashSet<String> hashSet = this.mGroupsToCommit;
            this.mGroupsToCommit = new HashSet<>();
            try {
                new Thread(new Runnable() {
                    public void run() {
                        MXFileStore.this.mFileStoreHandler.post(new Runnable() {
                            public void run() {
                                Group group;
                                if (!MXFileStore.this.isKilled()) {
                                    String access$000 = MXFileStore.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("saveGroups ");
                                    sb.append(hashSet.size());
                                    sb.append(" groups");
                                    Log.m209d(access$000, sb.toString());
                                    long currentTimeMillis = System.currentTimeMillis();
                                    Iterator it = hashSet.iterator();
                                    while (it.hasNext()) {
                                        String str = (String) it.next();
                                        synchronized (MXFileStore.this.mGroups) {
                                            group = (Group) MXFileStore.this.mGroups.get(str);
                                        }
                                        if (group != null) {
                                            MXFileStore mXFileStore = MXFileStore.this;
                                            StringBuilder sb2 = new StringBuilder();
                                            sb2.append("saveGroup ");
                                            sb2.append(str);
                                            mXFileStore.writeObject(sb2.toString(), new File(MXFileStore.this.mStoreGroupsFolderFile, str), group);
                                        } else {
                                            File file = new File(MXFileStore.this.mStoreGroupsFolderFile, str);
                                            if (file.exists()) {
                                                file.delete();
                                            }
                                        }
                                    }
                                    String access$0002 = MXFileStore.LOG_TAG;
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("saveGroups done in ");
                                    sb3.append(System.currentTimeMillis() - currentTimeMillis);
                                    sb3.append(" ms");
                                    Log.m209d(access$0002, sb3.toString());
                                }
                            }
                        });
                    }
                }).start();
            } catch (OutOfMemoryError e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("saveGroups : failed");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean loadGroups() {
        boolean z;
        try {
            List listFiles = listFiles(this.mStoreGroupsFolderFile.list());
            long currentTimeMillis = System.currentTimeMillis();
            Iterator it = listFiles.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = true;
                    break;
                }
                String str = (String) it.next();
                File file = new File(this.mStoreGroupsFolderFile, str);
                if (file.exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("loadGroups ");
                    sb.append(str);
                    Object readObject = readObject(sb.toString(), file);
                    if (readObject == null || !(readObject instanceof Group)) {
                        z = false;
                    } else {
                        Group group = (Group) readObject;
                        this.mGroups.put(group.getGroupId(), group);
                    }
                }
            }
            if (z) {
                long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("loadGroups : ");
                sb2.append(listFiles.size());
                sb2.append(" groups in ");
                sb2.append(currentTimeMillis2);
                sb2.append(" ms");
                Log.m209d(str2, sb2.toString());
                this.mStoreStats.put("loadGroups", Long.valueOf(currentTimeMillis2));
            }
            return z;
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("loadGroups failed : ");
            sb3.append(e.getMessage());
            Log.m211e(str3, sb3.toString());
            return false;
        }
    }

    public void setURLPreviewEnabled(boolean z) {
        super.setURLPreviewEnabled(z);
        this.mMetaDataHasChanged = true;
    }

    public void setRoomsWithoutURLPreview(Set<String> set) {
        super.setRoomsWithoutURLPreview(set);
        this.mMetaDataHasChanged = true;
    }

    public void setUserWidgets(Map<String, Object> map) {
        super.setUserWidgets(map);
        this.mMetaDataHasChanged = true;
    }

    public void setAntivirusServerPublicKey(@Nullable String str) {
        super.setAntivirusServerPublicKey(str);
        this.mMetaDataHasChanged = true;
    }
}
