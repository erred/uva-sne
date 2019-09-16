package com.opengarden.firechat.matrixsdk.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.data.MXEncryptEventContentResult;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage.EventCreationListener;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.AccountDataRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.RoomsRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.UrlPostTask;
import com.opengarden.firechat.matrixsdk.rest.client.UrlPostTask.IPostTaskListener;
import com.opengarden.firechat.matrixsdk.rest.model.BannedUser;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.LocationMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.rest.model.sync.InvitedRoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSync;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.offlineMessaging.LocalConnectionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Room {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "Room";
    private final Gson gson = new GsonBuilder().create();
    private RoomAccountData mAccountData = new RoomAccountData();
    private String mCallConferenceUserId;
    /* access modifiers changed from: private */
    public MXDataHandler mDataHandler;
    /* access modifiers changed from: private */
    public final MXEventListener mEncryptionListener = new MXEventListener() {
        public void onLiveEvent(Event event, RoomState roomState) {
            if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE_ENCRYPTION) && Room.this.mRoomEncryptionCallback != null) {
                Room.this.mRoomEncryptionCallback.onSuccess(null);
                Room.this.mRoomEncryptionCallback = null;
            }
        }
    };
    private final Map<IMXEventListener, IMXEventListener> mEventListeners = new HashMap();
    /* access modifiers changed from: private */
    public boolean mIsLeaving = false;
    private boolean mIsLeft;
    /* access modifiers changed from: private */
    public boolean mIsReady = false;
    private boolean mIsSyncing;
    private EventTimeline mLiveTimeline = new EventTimeline(this, true);
    /* access modifiers changed from: private */
    public final Map<String, Event> mMemberEventByEventId = new HashMap();
    private String mMyUserId = null;
    private ApiCallback<Void> mOnInitialSyncCallback;
    private boolean mRefreshUnreadAfterSync = false;
    /* access modifiers changed from: private */
    public ApiCallback<Void> mRoomEncryptionCallback;
    private RoomMediaMessagesSender mRoomMediaMessagesSender;
    private IMXStore mStore;
    private List<String> mTypingUsers = new ArrayList();

    private class RoomInfoUpdateCallback<T> implements ApiCallback<T> {
        private final ApiCallback<T> mCallback;

        public RoomInfoUpdateCallback(ApiCallback<T> apiCallback) {
            this.mCallback = apiCallback;
        }

        public void onSuccess(T t) {
            Room.this.getStore().storeLiveStateForRoom(Room.this.getRoomId());
            if (this.mCallback != null) {
                this.mCallback.onSuccess(t);
            }
        }

        public void onNetworkError(Exception exc) {
            if (this.mCallback != null) {
                this.mCallback.onNetworkError(exc);
            }
        }

        public void onMatrixError(MatrixError matrixError) {
            if (this.mCallback != null) {
                this.mCallback.onMatrixError(matrixError);
            }
        }

        public void onUnexpectedError(Exception exc) {
            if (this.mCallback != null) {
                this.mCallback.onUnexpectedError(exc);
            }
        }
    }

    public void init(IMXStore iMXStore, String str, MXDataHandler mXDataHandler) {
        this.mLiveTimeline.setRoomId(str);
        this.mDataHandler = mXDataHandler;
        this.mStore = iMXStore;
        if (this.mDataHandler != null) {
            this.mMyUserId = this.mDataHandler.getUserId();
            this.mLiveTimeline.setDataHandler(this.mStore, mXDataHandler);
        }
    }

    public MXDataHandler getDataHandler() {
        return this.mDataHandler;
    }

    public IMXStore getStore() {
        if (this.mStore == null) {
            if (this.mDataHandler != null) {
                this.mStore = this.mDataHandler.getStore(getRoomId());
            }
            if (this.mStore == null) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getStore() : cannot retrieve the store of ");
                sb.append(getRoomId());
                Log.m211e(str, sb.toString());
            }
        }
        return this.mStore;
    }

    public boolean shouldEncryptForInvitedMembers() {
        return !TextUtils.equals(getState().history_visibility, RoomState.HISTORY_VISIBILITY_JOINED);
    }

    public boolean isConferenceUserRoom() {
        return getState().isConferenceUserRoom();
    }

    public void setIsConferenceUserRoom(boolean z) {
        getState().setIsConferenceUserRoom(z);
    }

    public boolean isOngoingConferenceCall() {
        RoomMember member = getState().getMember(MXCallsManager.getConferenceUserId(getRoomId()));
        return member != null && TextUtils.equals(member.membership, RoomMember.MEMBERSHIP_JOIN);
    }

    public void setIsLeft(boolean z) {
        this.mIsLeft = z;
        this.mLiveTimeline.setIsHistorical(z);
    }

    public boolean isLeft() {
        return this.mIsLeft;
    }

    private void handleEphemeralEvents(List<Event> list) {
        for (Event event : list) {
            event.roomId = getRoomId();
            try {
                if (Event.EVENT_TYPE_RECEIPT.equals(event.getType())) {
                    if (event.roomId != null) {
                        List handleReceiptEvent = handleReceiptEvent(event);
                        if (handleReceiptEvent != null && handleReceiptEvent.size() > 0) {
                            this.mDataHandler.onReceiptEvent(event.roomId, handleReceiptEvent);
                        }
                    }
                } else if (Event.EVENT_TYPE_TYPING.equals(event.getType())) {
                    JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                    if (contentAsJsonObject.has("user_ids")) {
                        synchronized (this) {
                            this.mTypingUsers = null;
                            try {
                                this.mTypingUsers = (List) new Gson().fromJson(contentAsJsonObject.get("user_ids"), new TypeToken<List<String>>() {
                                }.getType());
                            } catch (Exception e) {
                                String str = LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## handleEphemeralEvents() : exception ");
                                sb.append(e.getMessage());
                                Log.m211e(str, sb.toString());
                            }
                            if (this.mTypingUsers == null) {
                                this.mTypingUsers = new ArrayList();
                            }
                        }
                    }
                    this.mDataHandler.onLiveEvent(event, getState());
                } else {
                    continue;
                }
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("ephemeral event failed ");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    public void handleJoinedRoomSync(RoomSync roomSync, boolean z) {
        if (this.mOnInitialSyncCallback != null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("initial sync handleJoinedRoomSync ");
            sb.append(getRoomId());
            Log.m209d(str, sb.toString());
        } else {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("handleJoinedRoomSync ");
            sb2.append(getRoomId());
            Log.m209d(str2, sb2.toString());
        }
        this.mIsSyncing = true;
        synchronized (this) {
            this.mLiveTimeline.handleJoinedRoomSync(roomSync, z);
            if (!(roomSync.ephemeral == null || roomSync.ephemeral.events == null)) {
                handleEphemeralEvents(roomSync.ephemeral.events);
            }
            if (!(roomSync.accountData == null || roomSync.accountData.events == null || roomSync.accountData.events.size() <= 0)) {
                if (z) {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## handleJoinedRoomSync : received ");
                    sb3.append(roomSync.accountData.events.size());
                    sb3.append(" account data events");
                    Log.m209d(str3, sb3.toString());
                }
                handleAccountDataEvents(roomSync.accountData.events);
            }
        }
        if (this.mOnInitialSyncCallback != null && !isWaitingInitialSync()) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("handleJoinedRoomSync ");
            sb4.append(getRoomId());
            sb4.append(" :  the initial sync is done");
            Log.m209d(str4, sb4.toString());
            final ApiCallback<Void> apiCallback = this.mOnInitialSyncCallback;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    Room.this.markAllAsRead(null);
                    try {
                        apiCallback.onSuccess(null);
                    } catch (Exception e) {
                        String access$000 = Room.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("handleJoinedRoomSync : onSuccess failed");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                }
            });
            this.mOnInitialSyncCallback = null;
        }
        this.mIsSyncing = false;
        if (this.mRefreshUnreadAfterSync) {
            if (!z) {
                refreshUnreadCounter();
            }
            this.mRefreshUnreadAfterSync = false;
        }
    }

    public void handleInvitedRoomSync(InvitedRoomSync invitedRoomSync) {
        this.mLiveTimeline.handleInvitedRoomSync(invitedRoomSync);
    }

    public void storeOutgoingEvent(Event event) {
        this.mLiveTimeline.storeOutgoingEvent(event);
    }

    public void requestServerRoomHistory(String str, int i, final ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        this.mDataHandler.getDataRetriever().requestServerRoomHistory(getRoomId(), str, i, new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback) {
            public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                apiCallback.onSuccess(tokensChunkResponse);
            }
        });
    }

    public void cancelRemoteHistoryRequest() {
        this.mDataHandler.getDataRetriever().cancelRemoteHistoryRequest(getRoomId());
    }

    public String getRoomId() {
        return this.mLiveTimeline.getState().roomId;
    }

    public void setAccountData(RoomAccountData roomAccountData) {
        this.mAccountData = roomAccountData;
    }

    public RoomAccountData getAccountData() {
        return this.mAccountData;
    }

    public RoomState getState() {
        return this.mLiveTimeline.getState();
    }

    public RoomState getLiveState() {
        return getState();
    }

    public boolean isLeaving() {
        return this.mIsLeaving;
    }

    public Collection<RoomMember> getMembers() {
        return getState().getMembers();
    }

    public EventTimeline getLiveTimeLine() {
        return this.mLiveTimeline;
    }

    public void setLiveTimeline(EventTimeline eventTimeline) {
        this.mLiveTimeline = eventTimeline;
    }

    public void setReadyState(boolean z) {
        this.mIsReady = z;
    }

    public boolean isReady() {
        return this.mIsReady;
    }

    public Collection<RoomMember> getActiveMembers() {
        Collection<RoomMember> members = getState().getMembers();
        ArrayList arrayList = new ArrayList();
        String conferenceUserId = MXCallsManager.getConferenceUserId(getRoomId());
        for (RoomMember roomMember : members) {
            if (!TextUtils.equals(roomMember.getUserId(), conferenceUserId) && (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN) || TextUtils.equals(roomMember.membership, "invite"))) {
                arrayList.add(roomMember);
            }
        }
        return arrayList;
    }

    public Collection<RoomMember> getJoinedMembers() {
        Collection<RoomMember> members = getState().getMembers();
        ArrayList arrayList = new ArrayList();
        for (RoomMember roomMember : members) {
            if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN)) {
                arrayList.add(roomMember);
            }
        }
        return arrayList;
    }

    public RoomMember getMember(String str) {
        return getState().getMember(str);
    }

    public void getMemberEvent(String str, final ApiCallback<Event> apiCallback) {
        final Event event;
        RoomMember member = getMember(str);
        if (member == null || member.getOriginalEventId() == null) {
            event = null;
        } else {
            event = (Event) this.mMemberEventByEventId.get(member.getOriginalEventId());
            if (event == null) {
                this.mDataHandler.getDataRetriever().getRoomsRestClient().getEvent(getRoomId(), member.getOriginalEventId(), new SimpleApiCallback<Event>(apiCallback) {
                    public void onSuccess(Event event) {
                        if (event != null) {
                            Room.this.mMemberEventByEventId.put(event.eventId, event);
                        }
                        apiCallback.onSuccess(event);
                    }
                });
                return;
            }
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                apiCallback.onSuccess(event);
            }
        });
    }

    public String getTopic() {
        return getState().topic;
    }

    public String getName(String str) {
        return getState().getDisplayName(str);
    }

    public String getVisibility() {
        return getState().visibility;
    }

    public boolean isInvited() {
        return hasMembership("invite");
    }

    public boolean hasMembership(@NonNull String str) {
        RoomMember member = getState().getMember(this.mMyUserId);
        if (member == null) {
            return false;
        }
        return TextUtils.equals(member.membership, str);
    }

    public boolean isDirectChatInvitation() {
        if (isInvited()) {
            RoomMember member = getState().getMember(this.mMyUserId);
            if (!(member == null || member.is_direct == null)) {
                return member.is_direct.booleanValue();
            }
        }
        return false;
    }

    public void setOnInitialSyncCallback(ApiCallback<Void> apiCallback) {
        this.mOnInitialSyncCallback = apiCallback;
    }

    public void joinWithThirdPartySigned(final String str, String str2, final ApiCallback<Void> apiCallback) {
        if (str2 == null) {
            join(str, apiCallback);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("&mxid=");
        sb.append(this.mMyUserId);
        String sb2 = sb.toString();
        UrlPostTask urlPostTask = new UrlPostTask();
        urlPostTask.setListener(new IPostTaskListener() {
            public void onSucceed(JsonObject jsonObject) {
                HashMap hashMap;
                try {
                    hashMap = (HashMap) new Gson().fromJson((JsonElement) jsonObject, new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("joinWithThirdPartySigned :  Gson().fromJson failed");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                    hashMap = null;
                }
                if (hashMap != null) {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("third_party_signed", hashMap);
                    Room.this.join(str, hashMap2, apiCallback);
                    return;
                }
                Room.this.join(apiCallback);
            }

            public void onError(String str) {
                String access$000 = Room.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("joinWithThirdPartySigned failed ");
                sb.append(str);
                Log.m209d(access$000, sb.toString());
                Room.this.join(apiCallback);
            }
        });
        try {
            urlPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{sb2});
        } catch (Exception e) {
            urlPostTask.cancel(true);
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("joinWithThirdPartySigned : task.executeOnExecutor failed");
            sb3.append(e.getMessage());
            Log.m211e(str3, sb3.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(e);
                    }
                }
            });
        }
    }

    public void join(ApiCallback<Void> apiCallback) {
        join(null, null, apiCallback);
    }

    private void join(String str, ApiCallback<Void> apiCallback) {
        join(str, null, apiCallback);
    }

    /* access modifiers changed from: private */
    public void join(String str, HashMap<String, Object> hashMap, ApiCallback<Void> apiCallback) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Join the room ");
        sb.append(getRoomId());
        sb.append(" with alias ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        RoomsRestClient roomsRestClient = this.mDataHandler.getDataRetriever().getRoomsRestClient();
        String roomId = str != null ? str : getRoomId();
        final ApiCallback<Void> apiCallback2 = apiCallback;
        final String str3 = str;
        final HashMap<String, Object> hashMap2 = hashMap;
        C25918 r2 = new SimpleApiCallback<RoomResponse>(apiCallback) {
            public void onSuccess(RoomResponse roomResponse) {
                try {
                    if (Room.this.isWaitingInitialSync()) {
                        String access$000 = Room.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("the room ");
                        sb.append(Room.this.getRoomId());
                        sb.append(" is joined but wait after initial sync");
                        Log.m209d(access$000, sb.toString());
                        Room.this.setOnInitialSyncCallback(apiCallback2);
                        return;
                    }
                    String access$0002 = Room.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("the room ");
                    sb2.append(Room.this.getRoomId());
                    sb2.append(" is joined : the initial sync has been done");
                    Log.m209d(access$0002, sb2.toString());
                    Room.this.markAllAsRead(null);
                    apiCallback2.onSuccess(null);
                } catch (Exception e) {
                    String access$0003 = Room.LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("join exception ");
                    sb3.append(e.getMessage());
                    Log.m211e(access$0003, sb3.toString());
                }
            }

            public void onNetworkError(Exception exc) {
                String access$000 = Room.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("join onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback2.onNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = Room.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("join onMatrixError ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                if (MatrixError.UNKNOWN.equals(matrixError.errcode) && TextUtils.equals("No known servers", matrixError.error)) {
                    matrixError.error = Room.this.getStore().getContext().getString(C1299R.string.room_error_join_failed_empty_room);
                }
                if (matrixError.mStatus.intValue() != 404 || TextUtils.isEmpty(str3)) {
                    apiCallback2.onMatrixError(matrixError);
                    return;
                }
                Log.m211e(Room.LOG_TAG, "Retry without the room alias");
                Room.this.join(null, hashMap2, apiCallback2);
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = Room.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("join onUnexpectedError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                apiCallback2.onUnexpectedError(exc);
            }
        };
        roomsRestClient.joinRoom(roomId, hashMap, r2);
    }

    private boolean selfJoined() {
        RoomMember member = getMember(this.mMyUserId);
        return member != null && RoomMember.MEMBERSHIP_JOIN.equals(member.membership);
    }

    public boolean isWaitingInitialSync() {
        RoomMember member = getMember(this.mMyUserId);
        return member == null || "invite".equals(member.membership);
    }

    public void updateUserPowerLevels(String str, int i, ApiCallback<Void> apiCallback) {
        PowerLevels deepCopy = getState().getPowerLevels().deepCopy();
        deepCopy.setUserPowerLevel(str, i);
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updatePowerLevels(getRoomId(), deepCopy, apiCallback);
    }

    public void updateName(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateRoomName(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().name = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void updateTopic(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateTopic(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().topic = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void updateCanonicalAlias(final String str, ApiCallback<Void> apiCallback) {
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateCanonicalAlias(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().roomAliasName = str;
                super.onSuccess(voidR);
            }
        });
    }

    public List<String> getAliases() {
        return getState().getAliases();
    }

    public void removeAlias(final String str, ApiCallback<Void> apiCallback) {
        ArrayList arrayList = new ArrayList(getAliases());
        if (TextUtils.isEmpty(str) || arrayList.indexOf(str) < 0) {
            if (apiCallback != null) {
                apiCallback.onSuccess(null);
            }
            return;
        }
        this.mDataHandler.getDataRetriever().getRoomsRestClient().removeRoomAlias(str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().removeAlias(str);
                super.onSuccess(voidR);
            }
        });
    }

    public void addAlias(final String str, ApiCallback<Void> apiCallback) {
        ArrayList arrayList = new ArrayList(getAliases());
        if (TextUtils.isEmpty(str) || arrayList.indexOf(str) >= 0) {
            if (apiCallback != null) {
                apiCallback.onSuccess(null);
            }
            return;
        }
        this.mDataHandler.getDataRetriever().getRoomsRestClient().setRoomIdByAlias(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().addAlias(str);
                super.onSuccess(voidR);
            }
        });
    }

    public void addRelatedGroup(String str, ApiCallback<Void> apiCallback) {
        ArrayList arrayList = new ArrayList(getState().getRelatedGroups());
        if (!arrayList.contains(str)) {
            arrayList.add(str);
        }
        updateRelatedGroups(arrayList, apiCallback);
    }

    public void removeRelatedGroup(String str, ApiCallback<Void> apiCallback) {
        ArrayList arrayList = new ArrayList(getState().getRelatedGroups());
        arrayList.remove(str);
        updateRelatedGroups(arrayList, apiCallback);
    }

    public void updateRelatedGroups(final List<String> list, final ApiCallback<Void> apiCallback) {
        HashMap hashMap = new HashMap();
        hashMap.put("groups", list);
        this.mDataHandler.getDataRetriever().getRoomsRestClient().sendStateEvent(getRoomId(), Event.EVENT_TYPE_STATE_RELATED_GROUPS, null, hashMap, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().groups = list;
                Room.this.getDataHandler().getStore().storeLiveStateForRoom(Room.this.getRoomId());
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }
        });
    }

    public String getAvatarUrl() {
        String avatarUrl = getState().getAvatarUrl();
        if (avatarUrl != null) {
            return avatarUrl;
        }
        ArrayList arrayList = new ArrayList(getState().getMembers());
        if (arrayList.size() == 1) {
            return ((RoomMember) arrayList.get(0)).getAvatarUrl();
        }
        if (arrayList.size() != 2) {
            return avatarUrl;
        }
        RoomMember roomMember = (RoomMember) arrayList.get(0);
        return TextUtils.equals(roomMember.getUserId(), this.mMyUserId) ? ((RoomMember) arrayList.get(1)).getAvatarUrl() : roomMember.getAvatarUrl();
    }

    public String getCallAvatarUrl() {
        ArrayList arrayList = new ArrayList(getJoinedMembers());
        if (2 != arrayList.size()) {
            return getAvatarUrl();
        }
        if (TextUtils.equals(this.mMyUserId, ((RoomMember) arrayList.get(0)).getUserId())) {
            return ((RoomMember) arrayList.get(1)).getAvatarUrl();
        }
        return ((RoomMember) arrayList.get(0)).getAvatarUrl();
    }

    public void updateAvatarUrl(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateAvatarUrl(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().url = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void updateHistoryVisibility(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateHistoryVisibility(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().history_visibility = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void updateDirectoryVisibility(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateDirectoryVisibility(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().visibility = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void getDirectoryVisibility(String str, final ApiCallback<String> apiCallback) {
        RoomsRestClient roomsRestClient = this.mDataHandler.getDataRetriever().getRoomsRestClient();
        if (roomsRestClient != null) {
            roomsRestClient.getDirectoryVisibility(str, new SimpleApiCallback<RoomState>(apiCallback) {
                public void onSuccess(RoomState roomState) {
                    RoomState state = Room.this.getState();
                    if (state != null) {
                        state.visibility = roomState.visibility;
                    }
                    if (apiCallback != null) {
                        apiCallback.onSuccess(roomState.visibility);
                    }
                }
            });
        }
    }

    public void updateJoinRules(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateJoinRules(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().join_rule = str;
                super.onSuccess(voidR);
            }
        });
    }

    public void updateGuestAccess(final String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateGuestAccess(getRoomId(), str, new RoomInfoUpdateCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.getState().guest_access = str;
                super.onSuccess(voidR);
            }
        });
    }

    private String getCallConferenceUserId() {
        if (this.mCallConferenceUserId == null) {
            this.mCallConferenceUserId = MXCallsManager.getConferenceUserId(getRoomId());
        }
        return this.mCallConferenceUserId;
    }

    public boolean handleReceiptData(ReceiptData receiptData) {
        if (TextUtils.equals(receiptData.userId, getCallConferenceUserId()) || getStore() == null) {
            return false;
        }
        boolean storeReceipt = getStore().storeReceipt(receiptData, getRoomId());
        if (storeReceipt && TextUtils.equals(this.mMyUserId, receiptData.userId)) {
            RoomSummary summary = getStore().getSummary(getRoomId());
            if (summary != null) {
                summary.setReadReceiptEventId(receiptData.eventId);
                getStore().flushSummary(summary);
            }
            refreshUnreadCounter();
        }
        return storeReceipt;
    }

    private List<String> handleReceiptEvent(Event event) {
        ArrayList arrayList = new ArrayList();
        try {
            HashMap hashMap = (HashMap) this.gson.fromJson(event.getContent(), new TypeToken<HashMap<String, HashMap<String, HashMap<String, HashMap<String, Object>>>>>() {
            }.getType());
            for (String str : hashMap.keySet()) {
                HashMap hashMap2 = (HashMap) hashMap.get(str);
                for (String str2 : hashMap2.keySet()) {
                    if (TextUtils.equals(str2, "m.read")) {
                        HashMap hashMap3 = (HashMap) hashMap2.get(str2);
                        for (String str3 : hashMap3.keySet()) {
                            HashMap hashMap4 = (HashMap) hashMap3.get(str3);
                            for (String str4 : hashMap4.keySet()) {
                                if (TextUtils.equals("ts", str4) && handleReceiptData(new ReceiptData(str3, str, ((Double) hashMap4.get(str4)).longValue()))) {
                                    arrayList.add(str3);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String str5 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("handleReceiptEvent : failed");
            sb.append(e.getMessage());
            Log.m211e(str5, sb.toString());
        }
        return arrayList;
    }

    private void clearUnreadCounters(RoomSummary roomSummary) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## clearUnreadCounters ");
        sb.append(getRoomId());
        Log.m209d(str, sb.toString());
        getState().setHighlightCount(0);
        getState().setNotificationCount(0);
        if (getStore() != null) {
            getStore().storeLiveStateForRoom(getRoomId());
            if (roomSummary != null) {
                roomSummary.setUnreadEventsCount(0);
                roomSummary.setHighlightCount(0);
                roomSummary.setNotificationCount(0);
                getStore().flushSummary(roomSummary);
            }
            getStore().commit();
        }
    }

    public String getReadMarkerEventId() {
        if (getStore() == null) {
            return null;
        }
        RoomSummary summary = getStore().getSummary(getRoomId());
        if (summary == null) {
            return null;
        }
        return summary.getReadMarkerEventId() != null ? summary.getReadMarkerEventId() : summary.getReadReceiptEventId();
    }

    public boolean markAllAsRead(ApiCallback<Void> apiCallback) {
        return markAllAsRead(true, apiCallback);
    }

    /* access modifiers changed from: private */
    public boolean markAllAsRead(boolean z, ApiCallback<Void> apiCallback) {
        Event latestEvent = getStore() != null ? getStore().getLatestEvent(getRoomId()) : null;
        String str = z ? latestEvent != null ? latestEvent.eventId : null : getReadMarkerEventId();
        boolean sendReadMarkers = sendReadMarkers(str, null, apiCallback);
        if (!sendReadMarkers) {
            RoomSummary summary = getStore() != null ? getStore().getSummary(getRoomId()) : null;
            if (summary == null) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendReadReceipt() : no summary for ");
                sb.append(getRoomId());
                Log.m211e(str2, sb.toString());
            } else if (!(summary.getUnreadEventsCount() == 0 && summary.getHighlightCount() == 0 && summary.getNotificationCount() == 0)) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## markAllAsRead() : the summary events counters should be cleared for ");
                sb2.append(getRoomId());
                Log.m211e(str3, sb2.toString());
                Event latestEvent2 = getStore().getLatestEvent(getRoomId());
                summary.setLatestReceivedEvent(latestEvent2);
                if (latestEvent2 != null) {
                    summary.setReadReceiptEventId(latestEvent2.eventId);
                } else {
                    summary.setReadReceiptEventId(null);
                }
                summary.setUnreadEventsCount(0);
                summary.setHighlightCount(0);
                summary.setNotificationCount(0);
                getStore().flushSummary(summary);
            }
            if (!(getState().getNotificationCount() == 0 && getState().getHighlightCount() == 0)) {
                String str4 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## markAllAsRead() : the notification messages count for ");
                sb3.append(getRoomId());
                sb3.append(" should have been cleared");
                Log.m211e(str4, sb3.toString());
                getState().setNotificationCount(0);
                getState().setHighlightCount(0);
                if (getStore() != null) {
                    getStore().storeLiveStateForRoom(getRoomId());
                }
            }
        }
        return sendReadMarkers;
    }

    public void setReadMakerEventId(String str) {
        RoomSummary summary = getStore() != null ? getStore().getSummary(getRoomId()) : null;
        if (summary != null && !str.equals(summary.getReadMarkerEventId())) {
            sendReadMarkers(str, summary.getReadReceiptEventId(), null);
        }
    }

    public void sendReadReceipt() {
        markAllAsRead(false, null);
    }

    public boolean sendReadReceipt(Event event, ApiCallback<Void> apiCallback) {
        String str = event != null ? event.eventId : null;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## sendReadReceipt() : eventId ");
        sb.append(str);
        sb.append(" in room ");
        sb.append(getRoomId());
        Log.m209d(str2, sb.toString());
        return sendReadMarkers(null, str, apiCallback);
    }

    public void forgetReadMarker(ApiCallback<Void> apiCallback) {
        String str = null;
        RoomSummary summary = getStore() != null ? getStore().getSummary(getRoomId()) : null;
        if (summary != null) {
            str = summary.getReadReceiptEventId();
        }
        if (summary != null) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## forgetReadMarker() : update the read marker to ");
            sb.append(str);
            sb.append(" in room ");
            sb.append(getRoomId());
            Log.m209d(str2, sb.toString());
            summary.setReadMarkerEventId(str);
            getStore().flushSummary(summary);
        }
        setReadMarkers(str, str, apiCallback);
    }

    public boolean sendReadMarkers(String str, String str2, ApiCallback<Void> apiCallback) {
        Event latestEvent = getStore() != null ? getStore().getLatestEvent(getRoomId()) : null;
        boolean z = false;
        if (latestEvent == null) {
            Log.m211e(LOG_TAG, "## sendReadMarkers(): no last event");
            return false;
        }
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## sendReadMarkers(): readMarkerEventId ");
        sb.append(str);
        sb.append(" readReceiptEventId ");
        sb.append(str2);
        sb.append(" in room ");
        sb.append(getRoomId());
        Log.m209d(str3, sb.toString());
        if (!TextUtils.isEmpty(str)) {
            if (!MXSession.isMessageId(str)) {
                String str4 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## sendReadMarkers() : invalid event id ");
                sb2.append(str);
                Log.m211e(str4, sb2.toString());
                str = null;
            } else {
                RoomSummary summary = getStore().getSummary(getRoomId());
                if (summary != null && !TextUtils.equals(str, summary.getReadMarkerEventId())) {
                    Event event = getStore().getEvent(str, getRoomId());
                    Event event2 = getStore().getEvent(summary.getReadMarkerEventId(), getRoomId());
                    if (event == null || event2 == null || event.getOriginServerTs() > event2.getOriginServerTs()) {
                        String str5 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## sendReadMarkers(): set new read marker event id ");
                        sb3.append(str);
                        sb3.append(" in room ");
                        sb3.append(getRoomId());
                        Log.m209d(str5, sb3.toString());
                        summary.setReadMarkerEventId(str);
                        getStore().flushSummary(summary);
                        z = true;
                    }
                }
            }
        }
        if (str2 == null) {
            str2 = latestEvent.eventId;
        }
        if (getStore() != null && !getStore().isEventRead(getRoomId(), getDataHandler().getUserId(), str2) && handleReceiptData(new ReceiptData(this.mMyUserId, str2, System.currentTimeMillis()))) {
            if (TextUtils.equals(latestEvent.eventId, str2)) {
                clearUnreadCounters(getStore().getSummary(getRoomId()));
            }
            z = true;
        }
        if (z) {
            setReadMarkers(str, str2, apiCallback);
        }
        return z;
    }

    private void setReadMarkers(String str, String str2, final ApiCallback<Void> apiCallback) {
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setReadMarkers(): readMarkerEventId ");
        sb.append(str);
        sb.append(" readReceiptEventId ");
        sb.append(str);
        Log.m209d(str3, sb.toString());
        if (!MXSession.isMessageId(str)) {
            str = null;
        }
        if (!MXSession.isMessageId(str2)) {
            str2 = null;
        }
        if (!TextUtils.isEmpty(str) || !TextUtils.isEmpty(str2)) {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().sendReadMarker(getRoomId(), str, str2, new SimpleApiCallback<Void>(apiCallback) {
                public void onSuccess(Void voidR) {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(voidR);
                    }
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                }
            });
        }
    }

    public boolean isEventRead(String str) {
        if (getStore() != null) {
            return getStore().isEventRead(getRoomId(), this.mMyUserId, str);
        }
        return false;
    }

    public int getNotificationCount() {
        return getState().getNotificationCount();
    }

    public int getHighlightCount() {
        return getState().getHighlightCount();
    }

    public void refreshUnreadCounter() {
        if (!this.mIsSyncing) {
            RoomSummary summary = getStore() != null ? getStore().getSummary(getRoomId()) : null;
            if (summary != null) {
                int unreadEventsCount = summary.getUnreadEventsCount();
                int eventsCountAfter = getStore().eventsCountAfter(getRoomId(), summary.getReadReceiptEventId());
                if (unreadEventsCount != eventsCountAfter) {
                    summary.setUnreadEventsCount(eventsCountAfter);
                    getStore().flushSummary(summary);
                    return;
                }
                return;
            }
            return;
        }
        this.mRefreshUnreadAfterSync = true;
    }

    public List<String> getTypingUsers() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = this.mTypingUsers == null ? new ArrayList() : new ArrayList(this.mTypingUsers);
        }
        return arrayList;
    }

    public void sendTypingNotification(boolean z, int i, ApiCallback<Void> apiCallback) {
        if (selfJoined()) {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().sendTypingNotification(getRoomId(), this.mMyUserId, z, i, apiCallback);
        }
    }

    public static void fillLocationInfo(Context context, LocationMessage locationMessage, Uri uri, String str) {
        if (uri != null) {
            try {
                locationMessage.thumbnail_url = uri.toString();
                ThumbnailInfo thumbnailInfo = new ThumbnailInfo();
                File file = new File(uri.getPath());
                ExifInterface exifInterface = new ExifInterface(uri.getPath());
                String attribute = exifInterface.getAttribute("ImageWidth");
                String attribute2 = exifInterface.getAttribute("ImageLength");
                if (attribute != null) {
                    thumbnailInfo.f138w = Integer.valueOf(Integer.parseInt(attribute));
                }
                if (attribute2 != null) {
                    thumbnailInfo.f137h = Integer.valueOf(Integer.parseInt(attribute2));
                }
                thumbnailInfo.size = Long.valueOf(file.length());
                thumbnailInfo.mimetype = str;
                locationMessage.thumbnail_info = thumbnailInfo;
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("fillLocationInfo : failed");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    public static void fillVideoInfo(Context context, VideoMessage videoMessage, Uri uri, String str, Uri uri2, String str2) {
        try {
            VideoInfo videoInfo = new VideoInfo();
            File file = new File(uri.getPath());
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime();
            videoInfo.f139h = Integer.valueOf(frameAtTime.getHeight());
            videoInfo.f140w = Integer.valueOf(frameAtTime.getWidth());
            videoInfo.mimetype = str;
            try {
                MediaPlayer create = MediaPlayer.create(context, uri);
                if (create != null) {
                    videoInfo.duration = Long.valueOf((long) create.getDuration());
                    create.release();
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("fillVideoInfo : MediaPlayer.create failed");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
            }
            videoInfo.size = Long.valueOf(file.length());
            if (uri2 != null) {
                videoInfo.thumbnail_url = uri2.toString();
                ThumbnailInfo thumbnailInfo = new ThumbnailInfo();
                File file2 = new File(uri2.getPath());
                ExifInterface exifInterface = new ExifInterface(uri2.getPath());
                String attribute = exifInterface.getAttribute("ImageWidth");
                String attribute2 = exifInterface.getAttribute("ImageLength");
                if (attribute != null) {
                    thumbnailInfo.f138w = Integer.valueOf(Integer.parseInt(attribute));
                }
                if (attribute2 != null) {
                    thumbnailInfo.f137h = Integer.valueOf(Integer.parseInt(attribute2));
                }
                thumbnailInfo.size = Long.valueOf(file2.length());
                thumbnailInfo.mimetype = str2;
                videoInfo.thumbnail_info = thumbnailInfo;
            }
            videoMessage.info = videoInfo;
        } catch (Exception e2) {
            String str4 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("fillVideoInfo : failed");
            sb2.append(e2.getMessage());
            Log.m211e(str4, sb2.toString());
        }
    }

    public static void fillFileInfo(Context context, FileMessage fileMessage, Uri uri, String str) {
        try {
            FileInfo fileInfo = new FileInfo();
            File file = new File(uri.getPath());
            fileInfo.mimetype = str;
            fileInfo.size = Long.valueOf(file.length());
            fileMessage.info = fileInfo;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("fillFileInfo : failed");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0095 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo getImageInfo(android.content.Context r6, com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r7, android.net.Uri r8, java.lang.String r9) {
        /*
            if (r7 != 0) goto L_0x0007
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r7 = new com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo
            r7.<init>()
        L_0x0007:
            java.lang.String r0 = r8.getPath()     // Catch:{ Exception -> 0x00d5 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x00d5 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x00d5 }
            android.media.ExifInterface r2 = new android.media.ExifInterface     // Catch:{ Exception -> 0x00d5 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r0 = "ImageWidth"
            java.lang.String r0 = r2.getAttribute(r0)     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r3 = "ImageLength"
            java.lang.String r2 = r2.getAttribute(r3)     // Catch:{ Exception -> 0x00d5 }
            int r6 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getOrientationForBitmap(r6, r8)     // Catch:{ Exception -> 0x00d5 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x00d5 }
            r7.orientation = r6     // Catch:{ Exception -> 0x00d5 }
            r6 = 0
            if (r0 == 0) goto L_0x006b
            if (r2 == 0) goto L_0x006b
            java.lang.Integer r6 = r7.orientation     // Catch:{ Exception -> 0x00d5 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x00d5 }
            r3 = 5
            if (r6 == r3) goto L_0x005f
            java.lang.Integer r6 = r7.orientation     // Catch:{ Exception -> 0x00d5 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x00d5 }
            r3 = 6
            if (r6 == r3) goto L_0x005f
            java.lang.Integer r6 = r7.orientation     // Catch:{ Exception -> 0x00d5 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x00d5 }
            r3 = 7
            if (r6 == r3) goto L_0x005f
            java.lang.Integer r6 = r7.orientation     // Catch:{ Exception -> 0x00d5 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x00d5 }
            r3 = 8
            if (r6 != r3) goto L_0x0056
            goto L_0x005f
        L_0x0056:
            int r6 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x00d5 }
            int r0 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x00d5 }
            goto L_0x006c
        L_0x005f:
            int r6 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x00d5 }
            int r0 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x00d5 }
            r5 = r0
            r0 = r6
            r6 = r5
            goto L_0x006c
        L_0x006b:
            r0 = 0
        L_0x006c:
            if (r6 == 0) goto L_0x0070
            if (r0 != 0) goto L_0x00b8
        L_0x0070:
            android.graphics.BitmapFactory$Options r2 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            r2.<init>()     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            r3 = 1
            r2.inJustDecodeBounds = r3     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            java.lang.String r8 = r8.getPath()     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            android.graphics.BitmapFactory.decodeFile(r8, r2)     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            int r8 = r2.outHeight     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            if (r8 <= 0) goto L_0x00b8
            int r8 = r2.outWidth     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            if (r8 <= 0) goto L_0x00b8
            int r8 = r2.outWidth     // Catch:{ Exception -> 0x009d, OutOfMemoryError -> 0x0095 }
            int r6 = r2.outHeight     // Catch:{ Exception -> 0x0090, OutOfMemoryError -> 0x008e }
            r0 = r6
            r6 = r8
            goto L_0x00b8
        L_0x008e:
            r6 = r8
            goto L_0x0095
        L_0x0090:
            r6 = move-exception
            r5 = r8
            r8 = r6
            r6 = r5
            goto L_0x009e
        L_0x0095:
            java.lang.String r8 = LOG_TAG     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r2 = "fillImageInfo : oom"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r2)     // Catch:{ Exception -> 0x00d5 }
            goto L_0x00b8
        L_0x009d:
            r8 = move-exception
        L_0x009e:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x00d5 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d5 }
            r3.<init>()     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r4 = "fillImageInfo : failed"
            r3.append(r4)     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r8 = r8.getMessage()     // Catch:{ Exception -> 0x00d5 }
            r3.append(r8)     // Catch:{ Exception -> 0x00d5 }
            java.lang.String r8 = r3.toString()     // Catch:{ Exception -> 0x00d5 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r8)     // Catch:{ Exception -> 0x00d5 }
        L_0x00b8:
            if (r6 != 0) goto L_0x00bc
            if (r0 == 0) goto L_0x00c8
        L_0x00bc:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x00d5 }
            r7.f136w = r6     // Catch:{ Exception -> 0x00d5 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00d5 }
            r7.f135h = r6     // Catch:{ Exception -> 0x00d5 }
        L_0x00c8:
            r7.mimetype = r9     // Catch:{ Exception -> 0x00d5 }
            long r8 = r1.length()     // Catch:{ Exception -> 0x00d5 }
            java.lang.Long r6 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x00d5 }
            r7.size = r6     // Catch:{ Exception -> 0x00d5 }
            goto L_0x00f1
        L_0x00d5:
            r6 = move-exception
            java.lang.String r7 = LOG_TAG
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "fillImageInfo : failed"
            r8.append(r9)
            java.lang.String r6 = r6.getMessage()
            r8.append(r6)
            java.lang.String r6 = r8.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r6)
            r7 = 0
        L_0x00f1:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.Room.getImageInfo(android.content.Context, com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo, android.net.Uri, java.lang.String):com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo");
    }

    public static void fillImageInfo(Context context, ImageMessage imageMessage, Uri uri, String str) {
        imageMessage.info = getImageInfo(context, imageMessage.info, uri, str);
    }

    public static void fillThumbnailInfo(Context context, ImageMessage imageMessage, Uri uri, String str) {
        ImageInfo imageInfo = getImageInfo(context, null, uri, str);
        if (imageInfo != null) {
            if (imageMessage.info == null) {
                imageMessage.info = new ImageInfo();
            }
            imageMessage.info.thumbnailInfo = new ThumbnailInfo();
            imageMessage.info.thumbnailInfo.f138w = imageInfo.f136w;
            imageMessage.info.thumbnailInfo.f137h = imageInfo.f135h;
            imageMessage.info.thumbnailInfo.size = imageInfo.size;
            imageMessage.info.thumbnailInfo.mimetype = imageInfo.mimetype;
        }
    }

    public boolean canPerformCall() {
        return getActiveMembers().size() > 1;
    }

    public List<RoomMember> callees() {
        ArrayList arrayList = new ArrayList();
        for (RoomMember roomMember : getMembers()) {
            if (RoomMember.MEMBERSHIP_JOIN.equals(roomMember.membership) && !this.mMyUserId.equals(roomMember.getUserId())) {
                arrayList.add(roomMember);
            }
        }
        return arrayList;
    }

    private void handleAccountDataEvents(List<Event> list) {
        if (list != null && list.size() > 0) {
            for (Event event : list) {
                String type = event.getType();
                RoomSummary summary = getStore() != null ? getStore().getSummary(getRoomId()) : null;
                if (!type.equals(Event.EVENT_TYPE_READ_MARKER)) {
                    this.mAccountData.handleTagEvent(event);
                    if (Event.EVENT_TYPE_TAGS.equals(event.getType())) {
                        summary.setRoomTags(this.mAccountData.getKeys());
                        getStore().flushSummary(summary);
                        this.mDataHandler.onRoomTagEvent(getRoomId());
                    } else if (Event.EVENT_TYPE_URL_PREVIEW.equals(event.getType())) {
                        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                        if (contentAsJsonObject.has(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE)) {
                            boolean asBoolean = contentAsJsonObject.get(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE).getAsBoolean();
                            Set roomsWithoutURLPreviews = this.mDataHandler.getStore().getRoomsWithoutURLPreviews();
                            if (asBoolean) {
                                roomsWithoutURLPreviews.add(getRoomId());
                            } else {
                                roomsWithoutURLPreviews.remove(getRoomId());
                            }
                            this.mDataHandler.getStore().setRoomsWithoutURLPreview(roomsWithoutURLPreviews);
                        }
                    }
                } else if (summary != null) {
                    Event event2 = JsonUtils.toEvent(event.getContent());
                    if (event2 != null && !TextUtils.equals(event2.eventId, summary.getReadMarkerEventId())) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## handleAccountDataEvents() : update the read marker to ");
                        sb.append(event2.eventId);
                        sb.append(" in room ");
                        sb.append(getRoomId());
                        Log.m209d(str, sb.toString());
                        if (TextUtils.isEmpty(event2.eventId)) {
                            String str2 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## handleAccountDataEvents() : null event id ");
                            sb2.append(event.getContent());
                            Log.m211e(str2, sb2.toString());
                        }
                        summary.setReadMarkerEventId(event2.eventId);
                        getStore().flushSummary(summary);
                        this.mDataHandler.onReadMarkerEvent(getRoomId());
                    }
                }
            }
            if (getStore() != null) {
                getStore().storeAccountData(getRoomId(), this.mAccountData);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addTag(String str, Double d, ApiCallback<Void> apiCallback) {
        if (str != null && d != null) {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().addTag(getRoomId(), str, d, apiCallback);
        } else if (apiCallback != null) {
            apiCallback.onSuccess(null);
        }
    }

    private void removeTag(String str, ApiCallback<Void> apiCallback) {
        if (str != null) {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().removeTag(getRoomId(), str, apiCallback);
        } else if (apiCallback != null) {
            apiCallback.onSuccess(null);
        }
    }

    public void replaceTag(String str, String str2, Double d, ApiCallback<Void> apiCallback) {
        if (str != null && str2 == null) {
            removeTag(str, apiCallback);
        } else if ((str != null || str2 == null) && !TextUtils.equals(str, str2)) {
            final String str3 = str2;
            final Double d2 = d;
            final ApiCallback<Void> apiCallback2 = apiCallback;
            C257524 r1 = new SimpleApiCallback<Void>(apiCallback) {
                public void onSuccess(Void voidR) {
                    Room.this.addTag(str3, d2, apiCallback2);
                }
            };
            removeTag(str, r1);
        } else {
            addTag(str2, d, apiCallback);
        }
    }

    public boolean isURLPreviewAllowedByUser() {
        return !getDataHandler().getStore().getRoomsWithoutURLPreviews().contains(getRoomId());
    }

    public void setIsURLPreviewAllowedByUser(boolean z, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().updateURLPreviewStatus(getRoomId(), z, apiCallback);
    }

    public void addEventListener(final IMXEventListener iMXEventListener) {
        if (iMXEventListener == null) {
            Log.m211e(LOG_TAG, "addEventListener : eventListener is null");
        } else if (this.mDataHandler == null) {
            Log.m211e(LOG_TAG, "addEventListener : mDataHandler is null");
        } else {
            C257625 r0 = new MXEventListener() {
                public void onPresenceUpdate(Event event, User user) {
                    if (Room.this.getMember(user.user_id) != null) {
                        try {
                            iMXEventListener.onPresenceUpdate(event, user);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onPresenceUpdate exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onLiveEvent(Event event, RoomState roomState) {
                    if (TextUtils.equals(Room.this.getRoomId(), event.roomId) && Room.this.mIsReady) {
                        try {
                            iMXEventListener.onLiveEvent(event, roomState);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onLiveEvent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onLiveEventsChunkProcessed(String str, String str2) {
                    try {
                        iMXEventListener.onLiveEventsChunkProcessed(str, str2);
                    } catch (Exception e) {
                        String access$000 = Room.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onLiveEventsChunkProcessed exception ");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                }

                public void onEventSentStateUpdated(Event event) {
                    if (TextUtils.equals(Room.this.getRoomId(), event.roomId)) {
                        try {
                            iMXEventListener.onEventSentStateUpdated(event);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onEventSentStateUpdated exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onEventDecrypted(Event event) {
                    if (TextUtils.equals(Room.this.getRoomId(), event.roomId)) {
                        try {
                            iMXEventListener.onEventDecrypted(event);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onDecryptedEvent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onEventSent(Event event, String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), event.roomId)) {
                        try {
                            iMXEventListener.onEventSent(event, str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onEventSent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onRoomInitialSyncComplete(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onRoomInitialSyncComplete(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomInitialSyncComplete exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onRoomInternalUpdate(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onRoomInternalUpdate(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomInternalUpdate exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onNotificationCountUpdate(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onNotificationCountUpdate(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onNotificationCountUpdate exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onNewRoom(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onNewRoom(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onNewRoom exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onJoinRoom(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onJoinRoom(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onJoinRoom exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onReceiptEvent(String str, List<String> list) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onReceiptEvent(str, list);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onReceiptEvent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onRoomTagEvent(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onRoomTagEvent(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomTagEvent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onReadMarkerEvent(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onReadMarkerEvent(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onReadMarkerEvent exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onRoomFlush(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onRoomFlush(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomFlush exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onLeaveRoom(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onLeaveRoom(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onLeaveRoom exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }

                public void onRoomKick(String str) {
                    if (TextUtils.equals(Room.this.getRoomId(), str)) {
                        try {
                            iMXEventListener.onRoomKick(str);
                        } catch (Exception e) {
                            String access$000 = Room.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onRoomKick exception ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }
                }
            };
            this.mEventListeners.put(iMXEventListener, r0);
            if (this.mDataHandler != null) {
                this.mDataHandler.addListener(r0);
            }
        }
    }

    public void removeEventListener(IMXEventListener iMXEventListener) {
        if (iMXEventListener != null && this.mDataHandler != null) {
            this.mDataHandler.removeListener((IMXEventListener) this.mEventListeners.get(iMXEventListener));
            this.mEventListeners.remove(iMXEventListener);
        }
    }

    public void sendEvent(final Event event, final ApiCallback<Void> apiCallback) {
        JsonElement jsonElement = null;
        if (!this.mIsReady || !selfJoined()) {
            this.mDataHandler.updateEventState(event, SentState.WAITING_RETRY);
            try {
                apiCallback.onNetworkError(null);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("sendEvent exception ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            return;
        }
        final String str2 = event.eventId;
        C257726 r7 = new ApiCallback<Event>() {
            public void onSuccess(Event event) {
                if (Room.this.getStore() != null) {
                    Room.this.getStore().deleteEvent(event);
                }
                boolean equals = TextUtils.equals(Room.this.getReadMarkerEventId(), event.eventId);
                event.eventId = event.eventId;
                if (!Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
                    Event event2 = event;
                    StringBuilder sb = new StringBuilder();
                    sb.append("$");
                    sb.append(System.currentTimeMillis());
                    sb.append(":serve2.firech.at");
                    event2.eventId = sb.toString();
                }
                event.originServerTs = System.currentTimeMillis();
                Room.this.mDataHandler.updateEventState(event, SentState.SENT);
                if (Room.this.getStore() != null && !Room.this.getStore().doesEventExist(event.eventId, Room.this.getRoomId())) {
                    Room.this.getStore().storeLiveRoomEvent(event);
                }
                Room.this.markAllAsRead(equals, null);
                if (Room.this.getStore() != null) {
                    Room.this.getStore().commit();
                }
                Room.this.mDataHandler.onEventSent(event, str2);
                try {
                    apiCallback.onSuccess(null);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("sendEvent exception ");
                    sb2.append(e.getMessage());
                    Log.m211e(access$000, sb2.toString());
                }
            }

            public void onNetworkError(Exception exc) {
                event.unsentException = exc;
                try {
                    apiCallback.onNetworkError(exc);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("sendEvent exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                event.unsentMatrixError = matrixError;
                Room.this.mDataHandler.updateEventState(event, SentState.UNDELIVERABLE);
                if (MatrixError.isConfigurationErrorCode(matrixError.errcode)) {
                    Room.this.mDataHandler.onConfigurationError(matrixError.errcode);
                    return;
                }
                try {
                    apiCallback.onMatrixError(matrixError);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("sendEvent exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
            }

            public void onUnexpectedError(Exception exc) {
                event.unsentException = exc;
                Room.this.mDataHandler.updateEventState(event, SentState.UNDELIVERABLE);
                try {
                    apiCallback.onUnexpectedError(exc);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("sendEvent exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
            }
        };
        if (!isEncrypted() || this.mDataHandler.getCrypto() == null) {
            this.mDataHandler.updateEventState(event, SentState.SENDING);
            if (Event.EVENT_TYPE_MESSAGE.equals(event.getType())) {
                RoomsRestClient roomsRestClient = this.mDataHandler.getDataRetriever().getRoomsRestClient();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(event.originServerTs);
                sb2.append("");
                roomsRestClient.sendMessage(sb2.toString(), getRoomId(), JsonUtils.toMessage(event.getContent()), r7);
            } else {
                RoomsRestClient roomsRestClient2 = this.mDataHandler.getDataRetriever().getRoomsRestClient();
                StringBuilder sb3 = new StringBuilder();
                sb3.append(event.originServerTs);
                sb3.append("");
                roomsRestClient2.sendEventToRoom(sb3.toString(), getRoomId(), event.getType(), event.getContent().getAsJsonObject(), r7);
            }
        } else {
            this.mDataHandler.updateEventState(event, SentState.ENCRYPTING);
            JsonObject contentAsJsonObject = event.getContentAsJsonObject();
            if (contentAsJsonObject != null && contentAsJsonObject.has("m.relates_to")) {
                jsonElement = contentAsJsonObject.get("m.relates_to");
                contentAsJsonObject.remove("m.relates_to");
            }
            final JsonElement jsonElement2 = jsonElement;
            MXCrypto crypto = this.mDataHandler.getCrypto();
            String type = event.getType();
            final Event event2 = event;
            final C257726 r6 = r7;
            final ApiCallback<Void> apiCallback2 = apiCallback;
            C257827 r2 = new ApiCallback<MXEncryptEventContentResult>() {
                public void onSuccess(MXEncryptEventContentResult mXEncryptEventContentResult) {
                    event2.type = mXEncryptEventContentResult.mEventType;
                    JsonObject asJsonObject = mXEncryptEventContentResult.mEventContent.getAsJsonObject();
                    if (jsonElement2 != null) {
                        asJsonObject.add("m.relates_to", jsonElement2);
                    }
                    event2.updateContent(asJsonObject);
                    event2.updateContent(mXEncryptEventContentResult.mEventContent.getAsJsonObject());
                    Room.this.mDataHandler.decryptEvent(event2, null);
                    Room.this.mDataHandler.updateEventState(event2, SentState.SENDING);
                    if (VectorApp.getInstance().offLineMessagePreference) {
                        Room.this.mDataHandler.updateEventState(event2, SentState.SENT);
                        LocalConnectionManager.sendToPeers(event2.eventId, event2, Room.this.mDataHandler.getCredentials().accessToken, 1);
                        if (!Matrix.getInstance(VectorApp.getInstance().getApplicationContext()).isConnected()) {
                            r6.onSuccess(event2);
                        }
                    }
                    RoomsRestClient roomsRestClient = Room.this.mDataHandler.getDataRetriever().getRoomsRestClient();
                    StringBuilder sb = new StringBuilder();
                    sb.append(event2.originServerTs);
                    sb.append("");
                    roomsRestClient.sendEventToRoom(sb.toString(), Room.this.getRoomId(), event2.type, asJsonObject, r6);
                }

                public void onNetworkError(Exception exc) {
                    event2.unsentException = exc;
                    Room.this.mDataHandler.updateEventState(event2, SentState.UNDELIVERABLE);
                    if (apiCallback2 != null) {
                        apiCallback2.onNetworkError(exc);
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (!(matrixError instanceof MXCryptoError) || !TextUtils.equals(((MXCryptoError) matrixError).errcode, MXCryptoError.UNKNOWN_DEVICES_CODE)) {
                        event2.mSentState = SentState.UNDELIVERABLE;
                    } else {
                        event2.mSentState = SentState.FAILED_UNKNOWN_DEVICES;
                    }
                    event2.unsentMatrixError = matrixError;
                    Room.this.mDataHandler.onEventSentStateUpdated(event2);
                    if (apiCallback2 != null) {
                        apiCallback2.onMatrixError(matrixError);
                    }
                }

                public void onUnexpectedError(Exception exc) {
                    event2.unsentException = exc;
                    Room.this.mDataHandler.updateEventState(event2, SentState.UNDELIVERABLE);
                    if (apiCallback2 != null) {
                        apiCallback2.onUnexpectedError(exc);
                    }
                }
            };
            crypto.encryptEventContent(contentAsJsonObject, type, this, r2);
        }
    }

    public void cancelEventSending(Event event) {
        if (event != null) {
            if (SentState.UNSENT == event.mSentState || SentState.SENDING == event.mSentState || SentState.WAITING_RETRY == event.mSentState || SentState.ENCRYPTING == event.mSentState) {
                this.mDataHandler.updateEventState(event, SentState.UNDELIVERABLE);
            }
            List<String> mediaUrls = event.getMediaUrls();
            MXMediasCache mediasCache = this.mDataHandler.getMediasCache();
            for (String str : mediaUrls) {
                mediasCache.cancelUpload(str);
                mediasCache.cancelDownload(mediasCache.downloadIdFromUrl(str));
            }
        }
    }

    public void redact(final String str, final ApiCallback<Event> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().redactEvent(getRoomId(), str, new SimpleApiCallback<Event>(apiCallback) {
            public void onSuccess(Event event) {
                Event event2 = Room.this.getStore() != null ? Room.this.getStore().getEvent(str, Room.this.getRoomId()) : null;
                if (event2 != null && (event2.unsigned == null || event2.unsigned.redacted_because == null)) {
                    event2.prune(null);
                    Room.this.getStore().storeLiveRoomEvent(event2);
                    Room.this.getStore().commit();
                }
                if (apiCallback != null) {
                    apiCallback.onSuccess(event2);
                }
            }
        });
    }

    public void report(String str, int i, String str2, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().reportEvent(getRoomId(), str, i, str2, apiCallback);
    }

    public void invite(String str, ApiCallback<Void> apiCallback) {
        if (str != null) {
            invite(Collections.singletonList(str), apiCallback);
        }
    }

    public void inviteByEmail(String str, ApiCallback<Void> apiCallback) {
        if (str != null) {
            invite(Collections.singletonList(str), apiCallback);
        }
    }

    public void invite(List<String> list, ApiCallback<Void> apiCallback) {
        if (list != null) {
            invite(list.iterator(), apiCallback);
        }
    }

    /* access modifiers changed from: private */
    public void invite(final Iterator<String> it, final ApiCallback<Void> apiCallback) {
        if (!it.hasNext()) {
            apiCallback.onSuccess(null);
            return;
        }
        C258029 r0 = new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Room.this.invite(it, apiCallback);
            }
        };
        String str = (String) it.next();
        if (Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().inviteByEmailToRoom(getRoomId(), str, r0);
        } else {
            this.mDataHandler.getDataRetriever().getRoomsRestClient().inviteUserToRoom(getRoomId(), str, r0);
        }
    }

    public void leave(final ApiCallback<Void> apiCallback) {
        this.mIsLeaving = true;
        this.mDataHandler.onRoomInternalUpdate(getRoomId());
        this.mDataHandler.getDataRetriever().getRoomsRestClient().leaveRoom(getRoomId(), new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (Room.this.mDataHandler.isAlive()) {
                    Room.this.mIsLeaving = false;
                    Room.this.mDataHandler.deleteRoom(Room.this.getRoomId());
                    if (Room.this.getStore() != null) {
                        Log.m209d(Room.LOG_TAG, "leave : commit");
                        Room.this.getStore().commit();
                    }
                    try {
                        apiCallback.onSuccess(voidR);
                    } catch (Exception e) {
                        String access$000 = Room.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("leave exception ");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                    Room.this.mDataHandler.onLeaveRoom(Room.this.getRoomId());
                }
            }

            public void onNetworkError(Exception exc) {
                Room.this.mIsLeaving = false;
                try {
                    apiCallback.onNetworkError(exc);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("leave exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
                Room.this.mDataHandler.onRoomInternalUpdate(Room.this.getRoomId());
            }

            public void onMatrixError(MatrixError matrixError) {
                if (matrixError.mStatus.intValue() == 404) {
                    onSuccess((Void) null);
                    return;
                }
                Room.this.mIsLeaving = false;
                try {
                    apiCallback.onMatrixError(matrixError);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("leave exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
                Room.this.mDataHandler.onRoomInternalUpdate(Room.this.getRoomId());
            }

            public void onUnexpectedError(Exception exc) {
                Room.this.mIsLeaving = false;
                try {
                    apiCallback.onUnexpectedError(exc);
                } catch (Exception e) {
                    String access$000 = Room.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("leave exception ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
                Room.this.mDataHandler.onRoomInternalUpdate(Room.this.getRoomId());
            }
        });
    }

    public void forget(final ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().forgetRoom(getRoomId(), new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                if (Room.this.mDataHandler.isAlive()) {
                    IMXStore store = Room.this.mDataHandler.getStore(Room.this.getRoomId());
                    if (store != null) {
                        store.deleteRoom(Room.this.getRoomId());
                        store.commit();
                    }
                    try {
                        apiCallback.onSuccess(voidR);
                    } catch (Exception e) {
                        String access$000 = Room.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("forget exception ");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                }
            }
        });
    }

    public void kick(String str, ApiCallback<Void> apiCallback) {
        this.mDataHandler.getDataRetriever().getRoomsRestClient().kickFromRoom(getRoomId(), str, apiCallback);
    }

    public void ban(String str, String str2, ApiCallback<Void> apiCallback) {
        BannedUser bannedUser = new BannedUser();
        bannedUser.userId = str;
        if (!TextUtils.isEmpty(str2)) {
            bannedUser.reason = str2;
        }
        this.mDataHandler.getDataRetriever().getRoomsRestClient().banFromRoom(getRoomId(), bannedUser, apiCallback);
    }

    public void unban(String str, ApiCallback<Void> apiCallback) {
        BannedUser bannedUser = new BannedUser();
        bannedUser.userId = str;
        this.mDataHandler.getDataRetriever().getRoomsRestClient().unbanFromRoom(getRoomId(), bannedUser, apiCallback);
    }

    public boolean isEncrypted() {
        return getState().isEncrypted();
    }

    public void enableEncryptionWithAlgorithm(String str, final ApiCallback<Void> apiCallback) {
        if (this.mDataHandler.getCrypto() != null && !TextUtils.isEmpty(str)) {
            HashMap hashMap = new HashMap();
            hashMap.put("algorithm", str);
            if (apiCallback != null) {
                this.mRoomEncryptionCallback = apiCallback;
                addEventListener(this.mEncryptionListener);
            }
            this.mDataHandler.getDataRetriever().getRoomsRestClient().sendStateEvent(getRoomId(), Event.EVENT_TYPE_MESSAGE_ENCRYPTION, null, hashMap, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                }

                public void onNetworkError(Exception exc) {
                    if (apiCallback != null) {
                        apiCallback.onNetworkError(exc);
                        Room.this.removeEventListener(Room.this.mEncryptionListener);
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (apiCallback != null) {
                        apiCallback.onMatrixError(matrixError);
                        Room.this.removeEventListener(Room.this.mEncryptionListener);
                    }
                }

                public void onUnexpectedError(Exception exc) {
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(exc);
                        Room.this.removeEventListener(Room.this.mEncryptionListener);
                    }
                }
            });
        } else if (apiCallback == null) {
        } else {
            if (this.mDataHandler.getCrypto() == null) {
                apiCallback.onMatrixError(new MXCryptoError(MXCryptoError.ENCRYPTING_NOT_ENABLED_ERROR_CODE, MXCryptoError.ENCRYPTING_NOT_ENABLED_REASON, MXCryptoError.ENCRYPTING_NOT_ENABLED_REASON));
            } else {
                apiCallback.onMatrixError(new MXCryptoError(MXCryptoError.MISSING_FIELDS_ERROR_CODE, MXCryptoError.UNABLE_TO_ENCRYPT, MXCryptoError.MISSING_FIELDS_REASON));
            }
        }
    }

    private void initRoomMediaMessagesSender() {
        if (this.mRoomMediaMessagesSender == null) {
            this.mRoomMediaMessagesSender = new RoomMediaMessagesSender(getStore().getContext(), this.mDataHandler, this);
        }
    }

    public void sendTextMessage(String str, String str2, String str3, EventCreationListener eventCreationListener) {
        sendTextMessage(str, str2, str3, null, Message.MSGTYPE_TEXT, eventCreationListener);
    }

    public void sendTextMessage(String str, String str2, String str3, @Nullable Event event, EventCreationListener eventCreationListener) {
        sendTextMessage(str, str2, str3, event, Message.MSGTYPE_TEXT, eventCreationListener);
    }

    public void sendEmoteMessage(String str, String str2, String str3, EventCreationListener eventCreationListener) {
        sendTextMessage(str, str2, str3, null, Message.MSGTYPE_EMOTE, eventCreationListener);
    }

    private void sendTextMessage(String str, String str2, String str3, @Nullable Event event, String str4, EventCreationListener eventCreationListener) {
        initRoomMediaMessagesSender();
        RoomMediaMessage roomMediaMessage = new RoomMediaMessage(str, str2, str3);
        roomMediaMessage.setMessageType(str4);
        roomMediaMessage.setEventCreationListener(eventCreationListener);
        if (canReplyTo(event)) {
            roomMediaMessage.setReplyToEvent(event);
        }
        this.mRoomMediaMessagesSender.send(roomMediaMessage);
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canReplyTo(@android.support.annotation.Nullable com.opengarden.firechat.matrixsdk.rest.model.Event r5) {
        /*
            r4 = this;
            r0 = 0
            if (r5 == 0) goto L_0x006f
            java.lang.String r1 = "m.room.message"
            java.lang.String r2 = r5.getType()
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x006f
            com.google.gson.JsonObject r5 = r5.getContentAsJsonObject()
            java.lang.String r5 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getMessageMsgType(r5)
            if (r5 == 0) goto L_0x006f
            r1 = -1
            int r2 = r5.hashCode()
            r3 = 1
            switch(r2) {
                case -1128764835: goto L_0x005f;
                case -1128351218: goto L_0x0055;
                case -636239083: goto L_0x004b;
                case -632772425: goto L_0x0041;
                case -629092198: goto L_0x0037;
                case -617202758: goto L_0x002d;
                case 2118539129: goto L_0x0023;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x0069
        L_0x0023:
            java.lang.String r2 = "m.notice"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 1
            goto L_0x006a
        L_0x002d:
            java.lang.String r2 = "m.video"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 4
            goto L_0x006a
        L_0x0037:
            java.lang.String r2 = "m.image"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 3
            goto L_0x006a
        L_0x0041:
            java.lang.String r2 = "m.emote"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 2
            goto L_0x006a
        L_0x004b:
            java.lang.String r2 = "m.audio"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 5
            goto L_0x006a
        L_0x0055:
            java.lang.String r2 = "m.text"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 0
            goto L_0x006a
        L_0x005f:
            java.lang.String r2 = "m.file"
            boolean r5 = r5.equals(r2)
            if (r5 == 0) goto L_0x0069
            r5 = 6
            goto L_0x006a
        L_0x0069:
            r5 = -1
        L_0x006a:
            switch(r5) {
                case 0: goto L_0x006e;
                case 1: goto L_0x006e;
                case 2: goto L_0x006e;
                case 3: goto L_0x006e;
                case 4: goto L_0x006e;
                case 5: goto L_0x006e;
                case 6: goto L_0x006e;
                default: goto L_0x006d;
            }
        L_0x006d:
            goto L_0x006f
        L_0x006e:
            return r3
        L_0x006f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.Room.canReplyTo(com.opengarden.firechat.matrixsdk.rest.model.Event):boolean");
    }

    public void sendMediaMessage(RoomMediaMessage roomMediaMessage, int i, int i2, EventCreationListener eventCreationListener) {
        initRoomMediaMessagesSender();
        roomMediaMessage.setThumbnailSize(new Pair(Integer.valueOf(i), Integer.valueOf(i2)));
        roomMediaMessage.setEventCreationListener(eventCreationListener);
        this.mRoomMediaMessagesSender.send(roomMediaMessage);
    }

    public void sendStickerMessage(Event event, EventCreationListener eventCreationListener) {
        initRoomMediaMessagesSender();
        RoomMediaMessage roomMediaMessage = new RoomMediaMessage(event);
        roomMediaMessage.setMessageType(Event.EVENT_TYPE_STICKER);
        roomMediaMessage.setEventCreationListener(eventCreationListener);
        this.mRoomMediaMessagesSender.send(roomMediaMessage);
    }

    public List<Event> getUnsentEvents() {
        ArrayList arrayList = new ArrayList();
        if (getStore() != null) {
            List undeliverableEvents = getStore().getUndeliverableEvents(getRoomId());
            List unknownDeviceEvents = getStore().getUnknownDeviceEvents(getRoomId());
            if (undeliverableEvents != null) {
                arrayList.addAll(undeliverableEvents);
            }
            if (unknownDeviceEvents != null) {
                arrayList.addAll(unknownDeviceEvents);
            }
        }
        return arrayList;
    }

    public void deleteEvents(List<Event> list) {
        if (getStore() != null && list != null && list.size() > 0) {
            for (Event deleteEvent : list) {
                getStore().deleteEvent(deleteEvent);
            }
            Event latestEvent = getStore().getLatestEvent(getRoomId());
            if (latestEvent != null && RoomSummary.isSupportedEvent(latestEvent)) {
                RoomSummary summary = getStore().getSummary(getRoomId());
                if (summary != null) {
                    summary.setLatestReceivedEvent(latestEvent, getState());
                } else {
                    summary = new RoomSummary(null, latestEvent, getState(), this.mDataHandler.getUserId());
                }
                getStore().storeSummary(summary);
            }
            getStore().commit();
        }
    }

    public boolean isDirect() {
        return this.mDataHandler.getDirectChatRoomIdsList().contains(getRoomId());
    }
}
