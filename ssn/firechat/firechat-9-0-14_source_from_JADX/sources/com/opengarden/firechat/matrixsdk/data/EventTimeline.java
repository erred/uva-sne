package com.opengarden.firechat.matrixsdk.data;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.data.store.MXMemoryStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.EventContext;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.sync.InvitedRoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSync;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.EventDisplay;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;

public class EventTimeline {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "EventTimeline";
    private static final int MAX_EVENT_COUNT_PER_PAGINATION = 30;
    private ArrayList<JsonObject> contents;
    /* access modifiers changed from: private */
    public RoomState mBackState;
    /* access modifiers changed from: private */
    public String mBackwardTopToken;
    /* access modifiers changed from: private */
    public boolean mCanBackPaginate;
    public MXDataHandler mDataHandler;
    private final ArrayList<EventTimelineListener> mEventTimelineListeners;
    /* access modifiers changed from: private */
    public String mForwardsPaginationToken;
    /* access modifiers changed from: private */
    public boolean mHasReachedHomeServerForwardsPaginationEnd;
    private String mInitialEventId;
    /* access modifiers changed from: private */
    public boolean mIsBackPaginating;
    /* access modifiers changed from: private */
    public boolean mIsForwardPaginating;
    private boolean mIsHistorical;
    /* access modifiers changed from: private */
    public boolean mIsLastBackChunk;
    private boolean mIsLiveTimeline;
    private final Room mRoom;
    /* access modifiers changed from: private */
    public String mRoomId;
    /* access modifiers changed from: private */
    public final ArrayList<SnapshotEvent> mSnapshotEvents;
    /* access modifiers changed from: private */
    public RoomState mState;
    /* access modifiers changed from: private */
    public IMXStore mStore;
    private final String mTimelineId;

    public enum Direction {
        FORWARDS,
        BACKWARDS
    }

    public interface EventTimelineListener {
        void onEvent(Event event, Direction direction, RoomState roomState);
    }

    public class SnapshotEvent {
        public final Event mEvent;
        public final RoomState mState;

        public SnapshotEvent(Event event, RoomState roomState) {
            this.mEvent = event;
            this.mState = roomState;
        }
    }

    public EventTimeline(Room room, boolean z) {
        this.mState = new RoomState();
        this.mBackState = new RoomState();
        this.mIsBackPaginating = false;
        this.mIsForwardPaginating = false;
        this.mCanBackPaginate = true;
        this.contents = new ArrayList<>();
        this.mBackwardTopToken = "not yet found";
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        this.mTimelineId = sb.toString();
        this.mSnapshotEvents = new ArrayList<>();
        this.mEventTimelineListeners = new ArrayList<>();
        this.mRoom = room;
        this.mIsLiveTimeline = z;
    }

    public EventTimeline(MXDataHandler mXDataHandler, String str) {
        this(mXDataHandler, str, null);
    }

    public EventTimeline(MXDataHandler mXDataHandler, String str, String str2) {
        this.mState = new RoomState();
        this.mBackState = new RoomState();
        this.mIsBackPaginating = false;
        this.mIsForwardPaginating = false;
        this.mCanBackPaginate = true;
        this.contents = new ArrayList<>();
        this.mBackwardTopToken = "not yet found";
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        this.mTimelineId = sb.toString();
        this.mSnapshotEvents = new ArrayList<>();
        this.mEventTimelineListeners = new ArrayList<>();
        this.mInitialEventId = str2;
        this.mDataHandler = mXDataHandler;
        this.mStore = new MXMemoryStore(mXDataHandler.getCredentials(), null);
        this.mRoom = this.mDataHandler.getRoom(this.mStore, str, true);
        this.mRoom.setLiveTimeline(this);
        this.mRoom.setReadyState(true);
        setRoomId(str);
        this.mState.setDataHandler(mXDataHandler);
        this.mBackState.setDataHandler(mXDataHandler);
    }

    public void setIsHistorical(boolean z) {
        this.mIsHistorical = z;
    }

    public String getTimelineId() {
        return this.mTimelineId;
    }

    public Room getRoom() {
        return this.mRoom;
    }

    public IMXStore getStore() {
        return this.mStore;
    }

    public String getInitialEventId() {
        return this.mInitialEventId;
    }

    public boolean isLiveTimeline() {
        return this.mIsLiveTimeline;
    }

    public boolean hasReachedHomeServerForwardsPaginationEnd() {
        return this.mHasReachedHomeServerForwardsPaginationEnd;
    }

    public void setRoomId(String str) {
        this.mRoomId = str;
        this.mState.roomId = str;
        this.mBackState.roomId = str;
    }

    public void setDataHandler(IMXStore iMXStore, MXDataHandler mXDataHandler) {
        this.mStore = iMXStore;
        this.mDataHandler = mXDataHandler;
        this.mState.setDataHandler(mXDataHandler);
        this.mBackState.setDataHandler(mXDataHandler);
    }

    public void initHistory() {
        this.mBackState = this.mState.deepCopy();
        this.mCanBackPaginate = true;
        this.mIsBackPaginating = false;
        this.mIsForwardPaginating = false;
        if (this.mDataHandler != null && this.mDataHandler.getDataRetriever() != null) {
            this.mDataHandler.resetReplayAttackCheckInTimeline(getTimelineId());
            this.mDataHandler.getDataRetriever().cancelHistoryRequest(this.mRoomId);
        }
    }

    public RoomState getState() {
        return this.mState;
    }

    public void setState(RoomState roomState) {
        this.mState = roomState;
    }

    /* access modifiers changed from: private */
    public RoomState getBackState() {
        return this.mBackState;
    }

    private void deepCopyState(Direction direction) {
        if (direction == Direction.FORWARDS) {
            this.mState = this.mState.deepCopy();
        } else {
            this.mBackState = this.mBackState.deepCopy();
        }
    }

    /* access modifiers changed from: private */
    public boolean processStateEvent(Event event, Direction direction) {
        boolean applyState = (direction == Direction.FORWARDS ? this.mState : this.mBackState).applyState(getStore(), event, direction);
        if (applyState && direction == Direction.FORWARDS) {
            this.mStore.storeLiveStateForRoom(this.mRoomId);
        }
        return applyState;
    }

    public void handleInvitedRoomSync(InvitedRoomSync invitedRoomSync) {
        if (invitedRoomSync != null && invitedRoomSync.inviteState != null && invitedRoomSync.inviteState.events != null) {
            for (Event event : invitedRoomSync.inviteState.events) {
                if (event.eventId == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mRoomId);
                    sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                    sb.append(System.currentTimeMillis());
                    sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                    sb.append(event.hashCode());
                    event.eventId = sb.toString();
                }
                event.roomId = this.mRoomId;
                handleLiveEvent(event, false, true);
            }
            this.mRoom.setReadyState(true);
        }
    }

    public void handleJoinedRoomSync(RoomSync roomSync, boolean z) {
        RoomSummary roomSummary;
        String str = this.mDataHandler.getMyUser().user_id;
        RoomMember member = this.mState.getMember(this.mDataHandler.getMyUser().user_id);
        CharSequence charSequence = member != null ? member.membership : null;
        int i = 0;
        boolean z2 = charSequence == null || TextUtils.equals(charSequence, "invite");
        if (TextUtils.equals(charSequence, "invite")) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("handleJoinedRoomSync: clean invited room from the store ");
            sb.append(this.mRoomId);
            Log.m209d(str2, sb.toString());
            this.mStore.deleteRoomData(this.mRoomId);
            RoomState roomState = new RoomState();
            roomState.roomId = this.mRoomId;
            roomState.setDataHandler(this.mDataHandler);
            this.mState = roomState;
            this.mBackState = roomState;
        }
        if (!(roomSync.state == null || roomSync.state.events == null || roomSync.state.events.size() <= 0)) {
            if (z2) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## handleJoinedRoomSync() : ");
                sb2.append(roomSync.state.events.size());
                sb2.append(" events for room ");
                sb2.append(this.mRoomId);
                sb2.append(" in store ");
                sb2.append(getStore());
                Log.m209d(str3, sb2.toString());
            }
            if (this.mDataHandler.isAlive()) {
                for (Event processStateEvent : roomSync.state.events) {
                    try {
                        processStateEvent(processStateEvent, Direction.FORWARDS);
                    } catch (Exception e) {
                        String str4 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("processStateEvent failed ");
                        sb3.append(e.getMessage());
                        Log.m211e(str4, sb3.toString());
                    }
                }
                this.mRoom.setReadyState(true);
            } else {
                Log.m211e(LOG_TAG, "## handleJoinedRoomSync() : mDataHandler.isAlive() is false");
            }
            if (z2) {
                String str5 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## handleJoinedRoomSync() : retrieve ");
                sb4.append(this.mState.getMembers().size());
                sb4.append(" members for room ");
                sb4.append(this.mRoomId);
                Log.m209d(str5, sb4.toString());
                this.mBackState = this.mState.deepCopy();
            }
        }
        if (roomSync.timeline != null) {
            if (roomSync.timeline.limited) {
                if (!z2) {
                    roomSummary = this.mStore.getSummary(this.mRoomId);
                    Event oldestEvent = this.mStore.getOldestEvent(this.mRoomId);
                    this.mStore.deleteAllRoomMessages(this.mRoomId, true);
                    if (oldestEvent != null && RoomSummary.isSupportedEvent(oldestEvent)) {
                        if (roomSummary != null) {
                            roomSummary.setLatestReceivedEvent(oldestEvent, this.mState);
                            this.mStore.storeSummary(roomSummary);
                        } else {
                            this.mStore.storeSummary(new RoomSummary(null, oldestEvent, this.mState, str));
                        }
                    }
                } else {
                    roomSummary = null;
                }
                if (roomSync.timeline.prevBatch == null) {
                    roomSync.timeline.prevBatch = Event.PAGINATE_BACK_TOKEN_END;
                }
                this.mStore.storeBackToken(this.mRoomId, roomSync.timeline.prevBatch);
                this.mBackState.setToken(null);
                this.mCanBackPaginate = true;
            } else {
                roomSummary = null;
            }
            if (roomSync.timeline.events != null && roomSync.timeline.events.size() > 0) {
                List<Event> list = roomSync.timeline.events;
                ((Event) list.get(0)).mToken = roomSync.timeline.prevBatch;
                for (Event event : list) {
                    event.roomId = this.mRoomId;
                    try {
                        handleLiveEvent(event, !(roomSync.timeline != null && roomSync.timeline.limited) && !z, !z && !z2);
                    } catch (Exception e2) {
                        String str6 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("timeline event failed ");
                        sb5.append(e2.getMessage());
                        Log.m211e(str6, sb5.toString());
                    }
                }
            }
        } else {
            roomSummary = null;
        }
        if (z2) {
            this.mRoom.setReadyState(true);
        } else if (roomSync.timeline != null && roomSync.timeline.limited) {
            this.mDataHandler.onRoomFlush(this.mRoomId);
        }
        if (this.mIsLiveTimeline) {
            if (this.mStore.getRoom(this.mRoomId) != null) {
                RoomSummary summary = this.mStore.getSummary(this.mRoomId);
                if (summary == null) {
                    Event oldestEvent2 = this.mStore.getOldestEvent(this.mRoomId);
                    if (oldestEvent2 != null) {
                        this.mStore.storeSummary(new RoomSummary(null, oldestEvent2, this.mState, str));
                        this.mStore.commit();
                        if (!RoomSummary.isSupportedEvent(oldestEvent2)) {
                            String str7 = LOG_TAG;
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("the room ");
                            sb6.append(this.mRoomId);
                            sb6.append(" has no valid summary, back paginate once to find a valid one");
                            Log.m211e(str7, sb6.toString());
                        }
                    } else if (roomSummary != null) {
                        roomSummary.setLatestReceivedEvent(roomSummary.getLatestReceivedEvent(), this.mState);
                        this.mStore.storeSummary(roomSummary);
                        this.mStore.commit();
                    } else if (roomSync.state != null && roomSync.state.events != null && roomSync.state.events.size() > 0) {
                        ArrayList arrayList = new ArrayList(roomSync.state.events);
                        Collections.reverse(arrayList);
                        Iterator it = arrayList.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Event event2 = (Event) it.next();
                            event2.roomId = this.mRoomId;
                            if (RoomSummary.isSupportedEvent(event2)) {
                                if (summary == null) {
                                    summary = new RoomSummary(this.mStore.getSummary(this.mRoomId), event2, this.mState, str);
                                } else {
                                    summary.setLatestReceivedEvent(event2, this.mState);
                                }
                                this.mStore.storeSummary(summary);
                                String type = event2.getType();
                                if ((Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) || Event.EVENT_TYPE_STATE_ROOM_ALIASES.equals(type) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type)) && summary != null) {
                                    summary.setName(this.mRoom.getName(str));
                                }
                                this.mStore.commit();
                            }
                        }
                    }
                }
            }
            if (roomSync.unreadNotifications != null) {
                int intValue = roomSync.unreadNotifications.highlightCount != null ? roomSync.unreadNotifications.highlightCount.intValue() : 0;
                if (roomSync.unreadNotifications.notificationCount != null) {
                    i = roomSync.unreadNotifications.notificationCount.intValue();
                }
                if (!(i == this.mState.getNotificationCount() && this.mState.getHighlightCount() == intValue)) {
                    String str8 = LOG_TAG;
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("## handleJoinedRoomSync() : update room state notifs count for room id ");
                    sb7.append(getRoom().getRoomId());
                    sb7.append(": highlightCount ");
                    sb7.append(intValue);
                    sb7.append(" - notifCount ");
                    sb7.append(i);
                    Log.m209d(str8, sb7.toString());
                    this.mState.setNotificationCount(i);
                    this.mState.setHighlightCount(intValue);
                    this.mStore.storeLiveStateForRoom(this.mRoomId);
                    this.mDataHandler.onNotificationCountUpdate(this.mRoomId);
                }
                RoomSummary summary2 = this.mStore.getSummary(this.mRoomId);
                if (summary2 == null) {
                    return;
                }
                if (i != summary2.getNotificationCount() || summary2.getHighlightCount() != intValue) {
                    String str9 = LOG_TAG;
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append("## handleJoinedRoomSync() : update room summary notifs count for room id ");
                    sb8.append(getRoom().getRoomId());
                    sb8.append(": highlightCount ");
                    sb8.append(intValue);
                    sb8.append(" - notifCount ");
                    sb8.append(i);
                    Log.m209d(str9, sb8.toString());
                    summary2.setNotificationCount(i);
                    summary2.setHighlightCount(intValue);
                    this.mStore.flushSummary(summary2);
                    this.mDataHandler.onNotificationCountUpdate(this.mRoomId);
                }
            }
        }
    }

    public void storeOutgoingEvent(Event event) {
        if (this.mIsLiveTimeline) {
            storeEvent(event);
        }
    }

    private void storeEvent(Event event) {
        String str = this.mDataHandler.getCredentials().userId;
        if (!(event.getSender() == null || event.eventId == null)) {
            this.mRoom.handleReceiptData(new ReceiptData(event.getSender(), event.eventId, event.originServerTs));
        }
        this.mStore.storeLiveRoomEvent(event);
        if (RoomSummary.isSupportedEvent(event)) {
            RoomSummary summary = this.mStore.getSummary(event.roomId);
            if (summary == null) {
                summary = new RoomSummary(summary, event, this.mState, str);
            } else {
                summary.setLatestReceivedEvent(event, this.mState);
            }
            this.mStore.storeSummary(summary);
            String type = event.getType();
            if ((Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) || Event.EVENT_TYPE_STATE_ROOM_ALIASES.equals(type) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type)) && summary != null) {
                summary.setName(this.mRoom.getName(str));
            }
        }
    }

    private void storeLiveRoomEvent(Event event, boolean z) {
        String str = this.mDataHandler.getCredentials().userId;
        boolean z2 = false;
        if (!Event.EVENT_TYPE_REDACTION.equals(event.getType())) {
            if (!event.isCallEvent() || !Event.EVENT_TYPE_CALL_CANDIDATES.equals(event.getType())) {
                z2 = true;
            }
            if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(event.getType()) && str.equals(event.stateKey)) {
                String asString = event.getContentAsJsonObject().getAsJsonPrimitive("membership").getAsString();
                if (RoomMember.MEMBERSHIP_LEAVE.equals(asString) || RoomMember.MEMBERSHIP_BAN.equals(asString)) {
                    z2 = this.mIsHistorical;
                }
            }
        } else if (event.getRedacts() != null) {
            Event event2 = this.mStore.getEvent(event.getRedacts(), event.roomId);
            if (event2 != null) {
                event2.prune(event);
                storeEvent(event2);
                storeEvent(event);
                if (z && event2.stateKey != null) {
                    checkStateEventRedaction(event);
                }
                ArrayList arrayList = new ArrayList(this.mStore.getRoomMessages(event.roomId));
                int size = arrayList.size() - 1;
                while (true) {
                    if (size < 0) {
                        break;
                    }
                    Event event3 = (Event) arrayList.get(size);
                    if (RoomSummary.isSupportedEvent(event3)) {
                        if (TextUtils.equals(event3.getType(), Event.EVENT_TYPE_MESSAGE_ENCRYPTED) && this.mDataHandler.getCrypto() != null) {
                            this.mDataHandler.decryptEvent(event3, getTimelineId());
                        }
                        if (!TextUtils.isEmpty(new EventDisplay(this.mStore.getContext(), event3, this.mState).getTextualDisplay())) {
                            event = event3;
                            break;
                        }
                    }
                    size--;
                }
                z2 = true;
            } else if (z) {
                checkStateEventRedaction(event);
            }
        }
        if (z2) {
            storeEvent(event);
        }
        if (Event.EVENT_TYPE_STATE_ROOM_CREATE.equals(event.getType())) {
            this.mDataHandler.onNewRoom(event.roomId);
        }
        if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(event.getType()) && str.equals(event.stateKey)) {
            String asString2 = event.getContentAsJsonObject().getAsJsonPrimitive("membership").getAsString();
            if (RoomMember.MEMBERSHIP_JOIN.equals(asString2)) {
                this.mDataHandler.onJoinRoom(event.roomId);
            } else if ("invite".equals(asString2)) {
                this.mDataHandler.onNewRoom(event.roomId);
            }
        }
    }

    private void triggerPush(Event event) {
        long j;
        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
        long j2 = 0;
        boolean z = false;
        if (contentAsJsonObject.has("lifetime")) {
            j2 = contentAsJsonObject.get("lifetime").getAsLong();
            j = System.currentTimeMillis() - event.getOriginServerTs();
            if (j > j2) {
                z = true;
            }
        } else {
            j = 0;
        }
        BingRulesManager bingRulesManager = this.mDataHandler.getBingRulesManager();
        if (!z && bingRulesManager != null) {
            BingRule fulfilledBingRule = bingRulesManager.fulfilledBingRule(event);
            if (fulfilledBingRule != null) {
                if (fulfilledBingRule.shouldNotify()) {
                    if (Event.EVENT_TYPE_CALL_INVITE.equals(event.getType())) {
                        long age = event.getAge();
                        if (Long.MAX_VALUE == age) {
                            age = System.currentTimeMillis() - event.getOriginServerTs();
                        }
                        if (age > 120000) {
                            String str = LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("handleLiveEvent : IGNORED onBingEvent rule id ");
                            sb.append(fulfilledBingRule.ruleId);
                            sb.append(" event id ");
                            sb.append(event.eventId);
                            sb.append(" in ");
                            sb.append(event.roomId);
                            Log.m209d(str, sb.toString());
                            return;
                        }
                    }
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("handleLiveEvent : onBingEvent rule id ");
                    sb2.append(fulfilledBingRule.ruleId);
                    sb2.append(" event id ");
                    sb2.append(event.eventId);
                    sb2.append(" in ");
                    sb2.append(event.roomId);
                    Log.m209d(str2, sb2.toString());
                    this.mDataHandler.onBingEvent(event, this.mState, fulfilledBingRule);
                } else {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("handleLiveEvent :rule id ");
                    sb3.append(fulfilledBingRule.ruleId);
                    sb3.append(" event id ");
                    sb3.append(event.eventId);
                    sb3.append(" in ");
                    sb3.append(event.roomId);
                    sb3.append(" has a mute notify rule");
                    Log.m209d(str3, sb3.toString());
                }
            }
        }
        if (z) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("handleLiveEvent : outOfTimeEvent for ");
            sb4.append(event.eventId);
            sb4.append(" in ");
            sb4.append(event.roomId);
            Log.m211e(str4, sb4.toString());
            String str5 = LOG_TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("handleLiveEvent : outOfTimeEvent maxlifetime ");
            sb5.append(j2);
            sb5.append(" eventLifeTime ");
            sb5.append(j);
            Log.m211e(str5, sb5.toString());
        }
    }

    public void handleLiveEvent(Event event, boolean z, boolean z2) {
        if (event.hasContentFields() && (this.contents.size() == 0 || !event.type.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTED) || !this.contents.contains(event.getContentAsJsonObject()))) {
            if (event.type.equals(Event.EVENT_TYPE_MESSAGE_ENCRYPTED)) {
                this.contents.add(event.getContentAsJsonObject());
            }
            MyUser myUser = this.mDataHandler.getMyUser();
            this.mDataHandler.decryptEvent(event, getTimelineId());
            boolean z3 = false;
            if (event.isCallEvent()) {
                this.mDataHandler.getCallsManager().handleCallEvent(this.mStore, event);
                storeLiveRoomEvent(event, false);
                if (!TextUtils.equals(event.getType(), Event.EVENT_TYPE_CALL_CANDIDATES)) {
                    this.mDataHandler.onLiveEvent(event, this.mState);
                    onEvent(event, Direction.FORWARDS, this.mState);
                }
                if (z2) {
                    triggerPush(event);
                }
            } else {
                Event event2 = this.mStore.getEvent(event.eventId, event.roomId);
                if (event2 != null) {
                    if (event2.getAge() == Event.DUMMY_EVENT_AGE) {
                        this.mStore.deleteEvent(event2);
                        this.mStore.storeLiveRoomEvent(event);
                        this.mStore.commit();
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("handleLiveEvent : the event ");
                        sb.append(event.eventId);
                        sb.append(" in ");
                        sb.append(event.roomId);
                        sb.append(" has been echoed");
                        Log.m209d(str, sb.toString());
                    } else {
                        String str2 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("handleLiveEvent : the event ");
                        sb2.append(event.eventId);
                        sb2.append(" in ");
                        sb2.append(event.roomId);
                        sb2.append(" already exist.");
                        Log.m209d(str2, sb2.toString());
                        return;
                    }
                }
                if (event.roomId != null) {
                    if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(event.getType()) && TextUtils.equals(event.getSender(), this.mDataHandler.getUserId())) {
                        EventContent eventContent = JsonUtils.toEventContent(event.getContentAsJsonObject());
                        EventContent prevContent = event.getPrevContent();
                        String str3 = null;
                        if (prevContent != null) {
                            str3 = prevContent.membership;
                        }
                        if (!event.isRedacted() && TextUtils.equals(str3, eventContent.membership) && TextUtils.equals(RoomMember.MEMBERSHIP_JOIN, eventContent.membership)) {
                            if (!TextUtils.equals(eventContent.displayname, myUser.displayname)) {
                                myUser.displayname = eventContent.displayname;
                                this.mStore.setDisplayName(myUser.displayname, event.getOriginServerTs());
                                z3 = true;
                            }
                            if (!TextUtils.equals(eventContent.avatar_url, myUser.getAvatarUrl())) {
                                myUser.setAvatarUrl(eventContent.avatar_url);
                                this.mStore.setAvatarURL(myUser.avatar_url, event.getOriginServerTs());
                                z3 = true;
                            }
                            if (z3) {
                                this.mDataHandler.onAccountInfoUpdate(myUser);
                            }
                        }
                    }
                    RoomState roomState = this.mState;
                    if (event.stateKey != null) {
                        deepCopyState(Direction.FORWARDS);
                        if (!processStateEvent(event, Direction.FORWARDS)) {
                            return;
                        }
                    }
                    storeLiveRoomEvent(event, z);
                    this.mDataHandler.onLiveEvent(event, roomState);
                    onEvent(event, Direction.FORWARDS, roomState);
                    if (z2) {
                        triggerPush(event);
                    }
                } else {
                    String str4 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Unknown live event type: ");
                    sb3.append(event.getType());
                    Log.m211e(str4, sb3.toString());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void manageBackEvents(int i, ApiCallback<Integer> apiCallback) {
        if (!this.mDataHandler.isAlive()) {
            Log.m209d(LOG_TAG, "manageEvents : mDataHandler is not anymore active.");
            return;
        }
        int min = Math.min(this.mSnapshotEvents.size(), i);
        Event event = null;
        for (int i2 = 0; i2 < min; i2++) {
            SnapshotEvent snapshotEvent = (SnapshotEvent) this.mSnapshotEvents.get(0);
            if (event == null && RoomSummary.isSupportedEvent(snapshotEvent.mEvent)) {
                event = snapshotEvent.mEvent;
            }
            this.mSnapshotEvents.remove(0);
            onEvent(snapshotEvent.mEvent, Direction.BACKWARDS, snapshotEvent.mState);
        }
        RoomSummary summary = this.mStore.getSummary(this.mRoomId);
        if (event != null && (summary == null || !RoomSummary.isSupportedEvent(summary.getLatestReceivedEvent()))) {
            this.mStore.storeSummary(new RoomSummary(null, event, this.mState, this.mDataHandler.getUserId()));
        }
        Log.m209d(LOG_TAG, "manageEvents : commit");
        this.mStore.commit();
        if (this.mSnapshotEvents.size() < 30 && this.mIsLastBackChunk) {
            this.mCanBackPaginate = false;
        }
        if (apiCallback != null) {
            try {
                apiCallback.onSuccess(Integer.valueOf(min));
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("requestHistory exception ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        this.mIsBackPaginating = false;
    }

    /* access modifiers changed from: private */
    public void addPaginationEvents(List<Event> list, Direction direction) {
        boolean z;
        RoomSummary summary = this.mStore.getSummary(this.mRoomId);
        boolean z2 = false;
        for (Event event : list) {
            if (event.stateKey != null) {
                deepCopyState(direction);
                z = processStateEvent(event, direction);
            } else {
                z = true;
            }
            this.mDataHandler.decryptEvent(event, getTimelineId());
            if (z && direction == Direction.BACKWARDS) {
                if (this.mIsLiveTimeline && summary != null && (summary.getLatestReceivedEvent() == null || (event.isValidOriginServerTs() && summary.getLatestReceivedEvent().originServerTs < event.originServerTs && RoomSummary.isSupportedEvent(event)))) {
                    summary.setLatestReceivedEvent(event, getState());
                    this.mStore.storeSummary(summary);
                    z2 = true;
                }
                this.mSnapshotEvents.add(new SnapshotEvent(event, getBackState()));
            }
        }
        if (z2) {
            this.mStore.commit();
        }
    }

    /* access modifiers changed from: private */
    public void addPaginationEvents(final List<Event> list, final Direction direction, final ApiCallback<Integer> apiCallback) {
        C25461 r0 = new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                EventTimeline.this.addPaginationEvents(list, direction);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                if (direction == Direction.BACKWARDS) {
                    EventTimeline.this.manageBackEvents(30, apiCallback);
                    return;
                }
                for (Event access$200 : list) {
                    EventTimeline.this.onEvent(access$200, Direction.FORWARDS, EventTimeline.this.getState());
                }
                if (apiCallback != null) {
                    apiCallback.onSuccess(Integer.valueOf(list.size()));
                }
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## addPaginationEvents() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
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

    public boolean canBackPaginate() {
        return !this.mIsBackPaginating && this.mState.canBackPaginated(this.mDataHandler.getUserId()) && this.mCanBackPaginate && this.mRoom.isReady();
    }

    public boolean backPaginate(ApiCallback<Integer> apiCallback) {
        return backPaginate(30, apiCallback);
    }

    public boolean backPaginate(int i, ApiCallback<Integer> apiCallback) {
        return backPaginate(i, false, apiCallback);
    }

    public boolean backPaginate(final int i, boolean z, final ApiCallback<Integer> apiCallback) {
        String userId = this.mDataHandler.getUserId();
        boolean z2 = false;
        if (!canBackPaginate()) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("cannot requestHistory ");
            sb.append(this.mIsBackPaginating);
            sb.append(StringUtils.SPACE);
            sb.append(!getState().canBackPaginated(userId));
            sb.append(StringUtils.SPACE);
            sb.append(!this.mCanBackPaginate);
            sb.append(StringUtils.SPACE);
            sb.append(!this.mRoom.isReady());
            Log.m209d(str, sb.toString());
            return false;
        }
        Log.m209d(LOG_TAG, "backPaginate starts");
        if (getBackState().getToken() == null) {
            this.mSnapshotEvents.clear();
        }
        final String token = getBackState().getToken();
        this.mIsBackPaginating = true;
        if (z || this.mSnapshotEvents.size() >= i || TextUtils.equals(token, this.mBackwardTopToken) || TextUtils.equals(token, Event.PAGINATE_BACK_TOKEN_END)) {
            if (TextUtils.equals(token, this.mBackwardTopToken) || TextUtils.equals(token, Event.PAGINATE_BACK_TOKEN_END)) {
                z2 = true;
            }
            this.mIsLastBackChunk = z2;
            final Handler handler = new Handler(Looper.getMainLooper());
            if (z) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("backPaginate : load ");
                sb2.append(this.mSnapshotEvents.size());
                sb2.append("cached events list");
                Log.m209d(str2, sb2.toString());
                i = Math.min(this.mSnapshotEvents.size(), i);
            } else if (this.mSnapshotEvents.size() >= i) {
                Log.m209d(LOG_TAG, "backPaginate : the events are already loaded.");
            } else {
                Log.m209d(LOG_TAG, "backPaginate : reach the history top");
            }
            new Thread(new Runnable() {
                public void run() {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            EventTimeline.this.manageBackEvents(i, apiCallback);
                        }
                    }, 0);
                }
            }).start();
            return true;
        }
        this.mDataHandler.getDataRetriever().backPaginate(this.mStore, this.mRoomId, getBackState().getToken(), i, new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback) {
            public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                if (EventTimeline.this.mDataHandler.isAlive()) {
                    if (tokensChunkResponse.chunk != null) {
                        String access$300 = EventTimeline.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("backPaginate : ");
                        sb.append(tokensChunkResponse.chunk.size());
                        sb.append(" events are retrieved.");
                        Log.m209d(access$300, sb.toString());
                    } else {
                        Log.m209d(EventTimeline.LOG_TAG, "backPaginate : there is no event");
                    }
                    EventTimeline.this.mIsLastBackChunk = (tokensChunkResponse.chunk != null && tokensChunkResponse.chunk.size() == 0 && TextUtils.equals(tokensChunkResponse.end, tokensChunkResponse.start)) || tokensChunkResponse.end == null;
                    if (EventTimeline.this.mIsLastBackChunk && tokensChunkResponse.end != null) {
                        EventTimeline.this.mBackwardTopToken = token;
                    } else if (tokensChunkResponse.end == null) {
                        EventTimeline.this.getBackState().setToken(Event.PAGINATE_BACK_TOKEN_END);
                    } else {
                        EventTimeline.this.getBackState().setToken(tokensChunkResponse.end);
                    }
                    EventTimeline.this.addPaginationEvents(tokensChunkResponse.chunk == null ? new ArrayList() : tokensChunkResponse.chunk, Direction.BACKWARDS, apiCallback);
                    return;
                }
                Log.m209d(EventTimeline.LOG_TAG, "mDataHandler is not active.");
            }

            public void onMatrixError(MatrixError matrixError) {
                Log.m209d(EventTimeline.LOG_TAG, "backPaginate onMatrixError");
                if (MatrixError.UNKNOWN.equals(matrixError.errcode)) {
                    EventTimeline.this.mCanBackPaginate = false;
                }
                EventTimeline.this.mIsBackPaginating = false;
                super.onMatrixError(matrixError);
            }

            public void onNetworkError(Exception exc) {
                Log.m209d(EventTimeline.LOG_TAG, "backPaginate onNetworkError");
                EventTimeline.this.mIsBackPaginating = false;
                super.onNetworkError(exc);
            }

            public void onUnexpectedError(Exception exc) {
                Log.m209d(EventTimeline.LOG_TAG, "backPaginate onUnexpectedError");
                EventTimeline.this.mIsBackPaginating = false;
                super.onUnexpectedError(exc);
            }
        });
        return true;
    }

    public boolean forwardPaginate(final ApiCallback<Integer> apiCallback) {
        if (this.mIsLiveTimeline) {
            Log.m209d(LOG_TAG, "Cannot forward paginate on Live timeline");
            return false;
        } else if (this.mIsForwardPaginating || this.mHasReachedHomeServerForwardsPaginationEnd) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("forwardPaginate ");
            sb.append(this.mIsForwardPaginating);
            sb.append(" mHasReachedHomeServerForwardsPaginationEnd ");
            sb.append(this.mHasReachedHomeServerForwardsPaginationEnd);
            Log.m209d(str, sb.toString());
            return false;
        } else {
            this.mIsForwardPaginating = true;
            this.mDataHandler.getDataRetriever().paginate(this.mStore, this.mRoomId, this.mForwardsPaginationToken, Direction.FORWARDS, new SimpleApiCallback<TokensChunkResponse<Event>>(apiCallback) {
                public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                    if (EventTimeline.this.mDataHandler.isAlive()) {
                        String access$300 = EventTimeline.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("forwardPaginate : ");
                        sb.append(tokensChunkResponse.chunk.size());
                        sb.append(" are retrieved.");
                        Log.m209d(access$300, sb.toString());
                        EventTimeline.this.mHasReachedHomeServerForwardsPaginationEnd = tokensChunkResponse.chunk.size() == 0 && TextUtils.equals(tokensChunkResponse.end, tokensChunkResponse.start);
                        EventTimeline.this.mForwardsPaginationToken = tokensChunkResponse.end;
                        EventTimeline.this.addPaginationEvents(tokensChunkResponse.chunk, Direction.FORWARDS, apiCallback);
                        EventTimeline.this.mIsForwardPaginating = false;
                        return;
                    }
                    Log.m209d(EventTimeline.LOG_TAG, "mDataHandler is not active.");
                }

                public void onMatrixError(MatrixError matrixError) {
                    EventTimeline.this.mIsForwardPaginating = false;
                    super.onMatrixError(matrixError);
                }

                public void onNetworkError(Exception exc) {
                    EventTimeline.this.mIsForwardPaginating = false;
                    super.onNetworkError(exc);
                }

                public void onUnexpectedError(Exception exc) {
                    EventTimeline.this.mIsForwardPaginating = false;
                    super.onUnexpectedError(exc);
                }
            });
            return true;
        }
    }

    public boolean paginate(Direction direction, ApiCallback<Integer> apiCallback) {
        if (Direction.BACKWARDS == direction) {
            return backPaginate(apiCallback);
        }
        return forwardPaginate(apiCallback);
    }

    public void cancelPaginationRequest() {
        this.mDataHandler.getDataRetriever().cancelHistoryRequest(this.mRoomId);
        this.mIsBackPaginating = false;
        this.mIsForwardPaginating = false;
    }

    public void resetPaginationAroundInitialEvent(int i, final ApiCallback<Void> apiCallback) {
        this.mStore.deleteRoomData(this.mRoomId);
        this.mDataHandler.resetReplayAttackCheckInTimeline(getTimelineId());
        this.mForwardsPaginationToken = null;
        this.mHasReachedHomeServerForwardsPaginationEnd = false;
        this.mDataHandler.getDataRetriever().getRoomsRestClient().getContextOfEvent(this.mRoomId, this.mInitialEventId, i, new SimpleApiCallback<EventContext>(apiCallback) {
            public void onSuccess(final EventContext eventContext) {
                C25531 r0 = new AsyncTask<Void, Void, Void>() {
                    /* access modifiers changed from: protected */
                    public Void doInBackground(Void... voidArr) {
                        for (Event access$1300 : eventContext.state) {
                            EventTimeline.this.processStateEvent(access$1300, Direction.FORWARDS);
                        }
                        EventTimeline.this.initHistory();
                        ArrayList arrayList = new ArrayList();
                        Collections.reverse(eventContext.eventsAfter);
                        arrayList.addAll(eventContext.eventsAfter);
                        arrayList.add(eventContext.event);
                        arrayList.addAll(eventContext.eventsBefore);
                        EventTimeline.this.addPaginationEvents(arrayList, Direction.BACKWARDS);
                        return null;
                    }

                    /* access modifiers changed from: protected */
                    public void onPostExecute(Void voidR) {
                        ArrayList arrayList = new ArrayList(EventTimeline.this.mSnapshotEvents.subList(0, (EventTimeline.this.mSnapshotEvents.size() + 1) / 2));
                        Collections.reverse(arrayList);
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            SnapshotEvent snapshotEvent = (SnapshotEvent) it.next();
                            EventTimeline.this.mSnapshotEvents.remove(snapshotEvent);
                            EventTimeline.this.onEvent(snapshotEvent.mEvent, Direction.FORWARDS, snapshotEvent.mState);
                        }
                        EventTimeline.this.mBackState.setToken(eventContext.start);
                        EventTimeline.this.mForwardsPaginationToken = eventContext.end;
                        EventTimeline.this.manageBackEvents(30, new ApiCallback<Integer>() {
                            public void onSuccess(Integer num) {
                                Log.m209d(EventTimeline.LOG_TAG, "addPaginationEvents succeeds");
                            }

                            public void onNetworkError(Exception exc) {
                                String access$300 = EventTimeline.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("addPaginationEvents failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$300, sb.toString());
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$300 = EventTimeline.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("addPaginationEvents failed ");
                                sb.append(matrixError.getMessage());
                                Log.m211e(access$300, sb.toString());
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$300 = EventTimeline.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("addPaginationEvents failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$300, sb.toString());
                            }
                        });
                        apiCallback.onSuccess(null);
                    }
                };
                try {
                    r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                } catch (Exception e) {
                    String access$300 = EventTimeline.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## resetPaginationAroundInitialEvent() failed ");
                    sb.append(e.getMessage());
                    Log.m211e(access$300, sb.toString());
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
        });
    }

    private void checkStateEventRedaction(final Event event) {
        final String redacts = event.getRedacts();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("checkStateEventRedaction of event ");
        sb.append(redacts);
        Log.m209d(str, sb.toString());
        this.mState.getStateEvents(getStore(), null, new SimpleApiCallback<List<Event>>() {
            public void onSuccess(List<Event> list) {
                boolean z = false;
                int i = 0;
                while (true) {
                    if (i >= list.size()) {
                        break;
                    }
                    Event event = (Event) list.get(i);
                    if (TextUtils.equals(event.eventId, redacts)) {
                        Log.m209d(EventTimeline.LOG_TAG, "checkStateEventRedaction: the current room state has been modified by the event redaction");
                        event.prune(event);
                        list.set(i, event);
                        EventTimeline.this.processStateEvent(event, Direction.FORWARDS);
                        z = true;
                        break;
                    }
                    i++;
                }
                if (!z) {
                    RoomMember memberByEventId = EventTimeline.this.mState.getMemberByEventId(redacts);
                    if (memberByEventId != null) {
                        Log.m209d(EventTimeline.LOG_TAG, "checkStateEventRedaction: the current room members list has been modified by the event redaction");
                        memberByEventId.prune();
                        z = true;
                    }
                }
                if (z) {
                    EventTimeline.this.mStore.storeLiveStateForRoom(EventTimeline.this.mRoomId);
                    EventTimeline.this.initHistory();
                    EventTimeline.this.mDataHandler.onRoomFlush(EventTimeline.this.mRoomId);
                    return;
                }
                Log.m209d(EventTimeline.LOG_TAG, "checkStateEventRedaction: the redacted event is unknown. Fetch it from the homeserver");
                EventTimeline.this.checkStateEventRedactionWithHomeserver(redacts);
            }
        });
    }

    /* access modifiers changed from: private */
    public void checkStateEventRedactionWithHomeserver(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("checkStateEventRedactionWithHomeserver on event Id ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (!TextUtils.isEmpty(str)) {
            Log.m209d(LOG_TAG, "checkStateEventRedactionWithHomeserver : retrieving the event");
            this.mDataHandler.getDataRetriever().getRoomsRestClient().getEvent(this.mRoomId, str, new ApiCallback<Event>() {
                public void onSuccess(Event event) {
                    if (event == null || event.stateKey == null) {
                        Log.m209d(EventTimeline.LOG_TAG, "checkStateEventRedactionWithHomeserver : the redacted event is a not state event -> job is done");
                    } else {
                        Log.m209d(EventTimeline.LOG_TAG, "checkStateEventRedactionWithHomeserver : the redacted event is a state event in the past. TODO: prune prev_content of the new state event");
                    }
                }

                public void onNetworkError(Exception exc) {
                    String access$300 = EventTimeline.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("checkStateEventRedactionWithHomeserver : failed to retrieved the redacted event: onNetworkError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$300 = EventTimeline.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("checkStateEventRedactionWithHomeserver : failed to retrieved the redacted event: onNetworkError ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$300, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$300 = EventTimeline.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("checkStateEventRedactionWithHomeserver : failed to retrieved the redacted event: onNetworkError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                }
            });
        }
    }

    public void addEventTimelineListener(EventTimelineListener eventTimelineListener) {
        if (eventTimelineListener != null) {
            synchronized (this) {
                if (-1 == this.mEventTimelineListeners.indexOf(eventTimelineListener)) {
                    this.mEventTimelineListeners.add(eventTimelineListener);
                }
            }
        }
    }

    public void removeEventTimelineListener(EventTimelineListener eventTimelineListener) {
        if (eventTimelineListener != null) {
            synchronized (this) {
                this.mEventTimelineListeners.remove(eventTimelineListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onEvent(final Event event, final Direction direction, final RoomState roomState) {
        ArrayList arrayList;
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    EventTimeline.this.onEvent(event, direction, roomState);
                }
            });
            return;
        }
        synchronized (this) {
            arrayList = new ArrayList(this.mEventTimelineListeners);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            EventTimelineListener eventTimelineListener = (EventTimelineListener) it.next();
            try {
                eventTimelineListener.onEvent(event, direction, roomState);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("EventTimeline.onEvent ");
                sb.append(eventTimelineListener);
                sb.append(" crashes ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void handleReceivedOfflineEvent(Event event) {
        handleLiveEvent(event, false, true);
    }
}
