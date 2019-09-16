package com.opengarden.firechat.matrixsdk.data;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RoomSummary implements Serializable {
    private static final String LOG_TAG = "RoomSummary";
    private static final long serialVersionUID = -3683013938626566489L;
    public int mHighlightsCount;
    private String mInviterName = null;
    private String mInviterUserId = null;
    private boolean mIsInvited = false;
    private Event mLatestReceivedEvent = null;
    private transient RoomState mLatestRoomState = null;
    private String mMatrixId = null;
    private String mName = null;
    public int mNotificationCount;
    private String mReadMarkerEventId;
    private String mReadReceiptEventId;
    private String mRoomId = null;
    private Set<String> mRoomTags;
    private String mTopic = null;
    public int mUnreadEventsCount;

    public RoomSummary() {
    }

    public RoomSummary(@Nullable RoomSummary roomSummary, Event event, RoomState roomState, String str) {
        setMatrixId(str);
        if (roomState != null) {
            setRoomId(roomState.roomId);
        }
        if (getRoomId() == null && event != null) {
            setRoomId(event.roomId);
        }
        setLatestReceivedEvent(event, roomState);
        if (roomSummary == null) {
            if (event != null) {
                setReadMarkerEventId(event.eventId);
                setReadReceiptEventId(event.eventId);
            }
            if (roomState != null) {
                setHighlightCount(roomState.getHighlightCount());
                setNotificationCount(roomState.getHighlightCount());
            }
            setUnreadEventsCount(Math.max(getHighlightCount(), getNotificationCount()));
            return;
        }
        setReadMarkerEventId(roomSummary.getReadMarkerEventId());
        setReadReceiptEventId(roomSummary.getReadReceiptEventId());
        setUnreadEventsCount(roomSummary.getUnreadEventsCount());
        setHighlightCount(roomSummary.getHighlightCount());
        setNotificationCount(roomSummary.getNotificationCount());
    }

    public static boolean isSupportedEvent(Event event) {
        String type = event.getType();
        boolean z = false;
        if (TextUtils.equals(Event.EVENT_TYPE_MESSAGE, type)) {
            try {
                String str = "";
                JsonElement jsonElement = event.getContentAsJsonObject().get("msgtype");
                if (jsonElement != null) {
                    str = jsonElement.getAsString();
                }
                if (TextUtils.equals(str, Message.MSGTYPE_TEXT) || TextUtils.equals(str, Message.MSGTYPE_EMOTE) || TextUtils.equals(str, Message.MSGTYPE_NOTICE) || TextUtils.equals(str, Message.MSGTYPE_IMAGE) || TextUtils.equals(str, Message.MSGTYPE_AUDIO) || TextUtils.equals(str, Message.MSGTYPE_VIDEO) || TextUtils.equals(str, Message.MSGTYPE_FILE)) {
                    z = true;
                }
                if (z || TextUtils.isEmpty(str)) {
                    return z;
                }
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("isSupportedEvent : Unsupported msg type ");
                sb.append(str);
                Log.m211e(str2, sb.toString());
                return z;
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("isSupportedEvent failed ");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
                return false;
            }
        } else if (TextUtils.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTED, type)) {
            return event.hasContentFields();
        } else {
            if (TextUtils.isEmpty(type)) {
                return false;
            }
            boolean z2 = TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_TOPIC, type) || TextUtils.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTED, type) || TextUtils.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTION, type) || TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_NAME, type) || TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_MEMBER, type) || TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_CREATE, type) || TextUtils.equals(Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY, type) || TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE, type) || TextUtils.equals(Event.EVENT_TYPE_STICKER, type) || (event.isCallEvent() && !Event.EVENT_TYPE_CALL_CANDIDATES.equals(type));
            if (!z2) {
                if (!TextUtils.equals(Event.EVENT_TYPE_TYPING, type) && !TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS, type) && !TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_JOIN_RULES, type) && !TextUtils.equals(Event.EVENT_TYPE_STATE_CANONICAL_ALIAS, type) && !TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_ALIASES, type)) {
                    String str4 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("isSupportedEvent :  Unsupported event type ");
                    sb3.append(type);
                    Log.m211e(str4, sb3.toString());
                }
            } else if (TextUtils.equals(Event.EVENT_TYPE_STATE_ROOM_MEMBER, type)) {
                JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                if (contentAsJsonObject != null) {
                    if (contentAsJsonObject.entrySet().size() == 0) {
                        Log.m209d(LOG_TAG, "isSupportedEvent : room member with no content is not supported");
                        return false;
                    }
                    EventContent prevContent = event.getPrevContent();
                    String str5 = null;
                    CharSequence charSequence = prevContent != null ? event.getEventContent().membership : null;
                    if (prevContent != null) {
                        str5 = prevContent.membership;
                    }
                    if (charSequence == null || !TextUtils.equals(charSequence, str5)) {
                        z = true;
                    }
                    if (z) {
                        return z;
                    }
                    Log.m209d(LOG_TAG, "isSupportedEvent : do not support avatar display name update");
                    return z;
                }
            }
            return z2;
        }
    }

    public String getMatrixId() {
        return this.mMatrixId;
    }

    public String getRoomId() {
        return this.mRoomId;
    }

    public String getRoomName() {
        String str;
        String str2 = this.mName;
        if (!isInvited() || this.mLatestReceivedEvent == null) {
            return str2;
        }
        if (this.mLatestRoomState != null) {
            str = this.mLatestRoomState.getMemberName(this.mLatestReceivedEvent.getSender());
        } else {
            str = this.mInviterName;
        }
        return str != null ? str : str2;
    }

    public String getRoomTopic() {
        return this.mTopic;
    }

    public Event getLatestReceivedEvent() {
        return this.mLatestReceivedEvent;
    }

    public RoomState getLatestRoomState() {
        return this.mLatestRoomState;
    }

    public boolean isInvited() {
        return this.mIsInvited || this.mInviterUserId != null;
    }

    public String getInviterUserId() {
        return this.mInviterUserId;
    }

    public void setMatrixId(String str) {
        this.mMatrixId = str;
    }

    public RoomSummary setTopic(String str) {
        this.mTopic = str;
        return this;
    }

    public RoomSummary setName(String str) {
        this.mName = str;
        return this;
    }

    public RoomSummary setRoomId(String str) {
        this.mRoomId = str;
        return this;
    }

    public RoomSummary setLatestReceivedEvent(Event event, RoomState roomState) {
        setLatestReceivedEvent(event);
        setLatestRoomState(roomState);
        if (roomState != null) {
            setName(roomState.getDisplayName(getMatrixId()));
            setTopic(roomState.topic);
        }
        return this;
    }

    public RoomSummary setLatestReceivedEvent(Event event) {
        this.mLatestReceivedEvent = event;
        return this;
    }

    public RoomSummary setLatestRoomState(RoomState roomState) {
        this.mLatestRoomState = roomState;
        if (this.mLatestRoomState != null) {
            RoomMember member = this.mLatestRoomState.getMember(this.mMatrixId);
            this.mIsInvited = member != null && "invite".equals(member.membership);
        }
        if (this.mIsInvited) {
            this.mInviterName = null;
            if (this.mLatestReceivedEvent != null) {
                String sender = this.mLatestReceivedEvent.getSender();
                this.mInviterUserId = sender;
                this.mInviterName = sender;
                if (this.mLatestRoomState != null) {
                    this.mInviterName = this.mLatestRoomState.getMemberName(this.mLatestReceivedEvent.getSender());
                }
            }
        } else {
            this.mInviterName = null;
            this.mInviterUserId = null;
        }
        return this;
    }

    public void setReadReceiptEventId(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setReadReceiptEventId() : ");
        sb.append(str);
        sb.append(" roomId ");
        sb.append(getRoomId());
        Log.m209d(str2, sb.toString());
        this.mReadReceiptEventId = str;
    }

    public String getReadReceiptEventId() {
        return this.mReadReceiptEventId;
    }

    public void setReadMarkerEventId(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setReadMarkerEventId() : ");
        sb.append(str);
        sb.append(" roomId ");
        sb.append(getRoomId());
        Log.m209d(str2, sb.toString());
        if (TextUtils.isEmpty(str)) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## setReadMarkerEventId') : null mReadMarkerEventId, in ");
            sb2.append(getRoomId());
            Log.m211e(str3, sb2.toString());
        }
        this.mReadMarkerEventId = str;
    }

    public String getReadMarkerEventId() {
        if (TextUtils.isEmpty(this.mReadMarkerEventId)) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getReadMarkerEventId') : null mReadMarkerEventId, in ");
            sb.append(getRoomId());
            Log.m211e(str, sb.toString());
            this.mReadMarkerEventId = getReadReceiptEventId();
        }
        return this.mReadMarkerEventId;
    }

    public void setUnreadEventsCount(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setUnreadEventsCount() : ");
        sb.append(i);
        sb.append(" roomId ");
        sb.append(getRoomId());
        Log.m209d(str, sb.toString());
        this.mUnreadEventsCount = i;
    }

    public int getUnreadEventsCount() {
        return this.mUnreadEventsCount;
    }

    public void setNotificationCount(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setNotificationCount() : ");
        sb.append(i);
        sb.append(" roomId ");
        sb.append(getRoomId());
        Log.m209d(str, sb.toString());
        this.mNotificationCount = i;
    }

    public int getNotificationCount() {
        return this.mNotificationCount;
    }

    public void setHighlightCount(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setHighlightCount() : ");
        sb.append(i);
        sb.append(" roomId ");
        sb.append(getRoomId());
        Log.m209d(str, sb.toString());
        this.mHighlightsCount = i;
    }

    public int getHighlightCount() {
        return this.mHighlightsCount;
    }

    public Set<String> getRoomTags() {
        return this.mRoomTags;
    }

    public void setRoomTags(Set<String> set) {
        if (set != null) {
            this.mRoomTags = new HashSet(set);
        } else {
            this.mRoomTags = new HashSet();
        }
    }
}
