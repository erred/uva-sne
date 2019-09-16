package com.opengarden.firechat.matrixsdk.data;

import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomCreateContent;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.RoomTombstoneContent;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.pid.RoomThirdPartyInvite;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RoomState implements Externalizable {
    public static final String DIRECTORY_VISIBILITY_PRIVATE = "private";
    public static final String DIRECTORY_VISIBILITY_PUBLIC = "public";
    public static final String GUEST_ACCESS_CAN_JOIN = "can_join";
    public static final String GUEST_ACCESS_FORBIDDEN = "forbidden";
    public static final String HISTORY_VISIBILITY_INVITED = "invited";
    public static final String HISTORY_VISIBILITY_JOINED = "joined";
    public static final String HISTORY_VISIBILITY_SHARED = "shared";
    public static final String HISTORY_VISIBILITY_WORLD_READABLE = "world_readable";
    public static final String JOIN_RULE_INVITE = "invite";
    public static final String JOIN_RULE_PUBLIC = "public";
    private static final String LOG_TAG = "RoomState";
    private static final long serialVersionUID = -6019932024524988201L;
    public String algorithm;
    public String alias;
    public List<String> aliases;
    public String avatar_url;
    public String canonical_alias;
    public List<String> groups;
    public String guest_access;
    public String history_visibility;
    public String join_rule;
    private Map<String, List<String>> mAliasesByDomain = new HashMap();
    private transient Object mDataHandler = null;
    private int mHighlightCount;
    private Boolean mIsConferenceUserRoom = null;
    private boolean mIsLive;
    private transient Map<String, String> mMemberDisplayNameByUserId = new HashMap();
    private final Map<String, RoomMember> mMembers = new HashMap();
    private final Map<String, RoomMember> mMembersWithThirdPartyInviteTokenCache = new HashMap();
    private String mMembership;
    private List<String> mMergedAliasesList;
    private int mNotificationCount;
    private Map<String, Event> mRoomAliases = new HashMap();
    private RoomCreateContent mRoomCreateContent;
    private RoomTombstoneContent mRoomTombstoneContent;
    private Map<String, List<Event>> mStateEvents = new HashMap();
    private final Map<String, RoomThirdPartyInvite> mThirdPartyInvites = new HashMap();
    public String name;
    private PowerLevels powerLevels;
    public String roomAliasName;
    public String roomId;
    private String token;
    public String topic;
    public String url;
    public String visibility;

    public String getGuestAccess() {
        return this.guest_access != null ? this.guest_access : GUEST_ACCESS_FORBIDDEN;
    }

    public String getHistoryVisibility() {
        return this.history_visibility != null ? this.history_visibility : HISTORY_VISIBILITY_SHARED;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public String getAvatarUrl() {
        if (this.url != null) {
            return this.url;
        }
        return this.avatar_url;
    }

    public List<String> getRelatedGroups() {
        return this.groups == null ? new ArrayList() : this.groups;
    }

    public Collection<RoomMember> getMembers() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mMembers.values());
        }
        return arrayList;
    }

    public List<Event> getStateEvents(Set<String> set) {
        ArrayList arrayList = new ArrayList();
        ArrayList<Event> arrayList2 = new ArrayList<>();
        for (List addAll : this.mStateEvents.values()) {
            arrayList2.addAll(addAll);
        }
        if (set == null || set.isEmpty()) {
            arrayList.addAll(arrayList2);
        } else {
            for (Event event : arrayList2) {
                if (event.getType() != null && set.contains(event.getType())) {
                    arrayList.add(event);
                }
            }
        }
        return arrayList;
    }

    public void getStateEvents(IMXStore iMXStore, final Set<String> set, final ApiCallback<List<Event>> apiCallback) {
        if (iMXStore != null) {
            final ArrayList arrayList = new ArrayList();
            for (List addAll : this.mStateEvents.values()) {
                arrayList.addAll(addAll);
            }
            iMXStore.getRoomStateEvents(this.roomId, new SimpleApiCallback<List<Event>>() {
                public void onSuccess(List<Event> list) {
                    arrayList.addAll(list);
                    ArrayList arrayList = new ArrayList();
                    if (set == null || set.isEmpty()) {
                        arrayList.addAll(arrayList);
                    } else {
                        for (Event event : arrayList) {
                            if (event.getType() != null && set.contains(event.getType())) {
                                arrayList.add(event);
                            }
                        }
                    }
                    apiCallback.onSuccess(arrayList);
                }
            });
        }
    }

    public Collection<RoomMember> getDisplayableMembers() {
        Collection<RoomMember> members = getMembers();
        RoomMember member = getMember(MXCallsManager.getConferenceUserId(this.roomId));
        if (member == null) {
            return members;
        }
        ArrayList arrayList = new ArrayList(members);
        arrayList.remove(member);
        return arrayList;
    }

    public boolean isConferenceUserRoom() {
        if (this.mIsConferenceUserRoom == null) {
            this.mIsConferenceUserRoom = Boolean.valueOf(false);
            Collection members = getMembers();
            if (2 == members.size()) {
                Iterator it = members.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (MXCallsManager.isConferenceUserId(((RoomMember) it.next()).getUserId())) {
                            this.mIsConferenceUserRoom = Boolean.valueOf(true);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return this.mIsConferenceUserRoom.booleanValue();
    }

    public void setIsConferenceUserRoom(boolean z) {
        this.mIsConferenceUserRoom = Boolean.valueOf(z);
    }

    public void setMember(String str, RoomMember roomMember) {
        if (roomMember.getUserId() == null) {
            roomMember.setUserId(str);
        }
        synchronized (this) {
            if (this.mMemberDisplayNameByUserId != null) {
                this.mMemberDisplayNameByUserId.remove(str);
            }
            this.mMembers.put(str, roomMember);
        }
    }

    public RoomMember getMember(String str) {
        RoomMember roomMember;
        synchronized (this) {
            roomMember = (RoomMember) this.mMembers.get(str);
        }
        return roomMember;
    }

    public RoomMember getMemberByEventId(String str) {
        RoomMember roomMember;
        synchronized (this) {
            Iterator it = this.mMembers.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    roomMember = null;
                    break;
                }
                roomMember = (RoomMember) it.next();
                if (roomMember.getOriginalEventId().equals(str)) {
                    break;
                }
            }
        }
        return roomMember;
    }

    public void removeMember(String str) {
        synchronized (this) {
            this.mMembers.remove(str);
            if (this.mMemberDisplayNameByUserId != null) {
                this.mMemberDisplayNameByUserId.remove(str);
            }
        }
    }

    public RoomMember memberWithThirdPartyInviteToken(String str) {
        return (RoomMember) this.mMembersWithThirdPartyInviteTokenCache.get(str);
    }

    public RoomThirdPartyInvite thirdPartyInviteWithToken(String str) {
        return (RoomThirdPartyInvite) this.mThirdPartyInvites.get(str);
    }

    public Collection<RoomThirdPartyInvite> thirdPartyInvites() {
        return this.mThirdPartyInvites.values();
    }

    public PowerLevels getPowerLevels() {
        if (this.powerLevels != null) {
            return this.powerLevels.deepCopy();
        }
        return null;
    }

    public void setPowerLevels(PowerLevels powerLevels2) {
        this.powerLevels = powerLevels2;
    }

    public void setDataHandler(MXDataHandler mXDataHandler) {
        this.mDataHandler = mXDataHandler;
    }

    public MXDataHandler getDataHandler() {
        return (MXDataHandler) this.mDataHandler;
    }

    public void setNotificationCount(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## setNotificationCount() : ");
        sb.append(i);
        sb.append(" room id ");
        sb.append(this.roomId);
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
        sb.append(" room id ");
        sb.append(this.roomId);
        Log.m209d(str, sb.toString());
        this.mHighlightCount = i;
    }

    public int getHighlightCount() {
        return this.mHighlightCount;
    }

    public boolean canBackPaginated(String str) {
        RoomMember member = getMember(str);
        String str2 = member != null ? member.membership : "";
        String str3 = TextUtils.isEmpty(this.history_visibility) ? HISTORY_VISIBILITY_SHARED : this.history_visibility;
        return str3.equals(HISTORY_VISIBILITY_WORLD_READABLE) || str3.equals(HISTORY_VISIBILITY_SHARED) || RoomMember.MEMBERSHIP_JOIN.equals(str2) || ("invite".equals(str2) && str3.equals(HISTORY_VISIBILITY_INVITED));
    }

    public RoomState deepCopy() {
        RoomState roomState = new RoomState();
        roomState.roomId = this.roomId;
        RoomTombstoneContent roomTombstoneContent = null;
        roomState.setPowerLevels(this.powerLevels == null ? null : this.powerLevels.deepCopy());
        roomState.aliases = this.aliases == null ? null : new ArrayList<>(this.aliases);
        roomState.mAliasesByDomain = new HashMap(this.mAliasesByDomain);
        roomState.alias = this.alias;
        roomState.name = this.name;
        roomState.topic = this.topic;
        roomState.url = this.url;
        roomState.mRoomCreateContent = this.mRoomCreateContent != null ? this.mRoomCreateContent.deepCopy() : null;
        roomState.join_rule = this.join_rule;
        roomState.guest_access = this.guest_access;
        roomState.history_visibility = this.history_visibility;
        roomState.visibility = this.visibility;
        roomState.roomAliasName = this.roomAliasName;
        roomState.token = this.token;
        roomState.groups = this.groups;
        roomState.mDataHandler = this.mDataHandler;
        roomState.mMembership = this.mMembership;
        roomState.mIsLive = this.mIsLive;
        roomState.mIsConferenceUserRoom = this.mIsConferenceUserRoom;
        roomState.algorithm = this.algorithm;
        roomState.mRoomAliases = new HashMap(this.mRoomAliases);
        roomState.mStateEvents = new HashMap(this.mStateEvents);
        if (this.mRoomTombstoneContent != null) {
            roomTombstoneContent = this.mRoomTombstoneContent.deepCopy();
        }
        roomState.mRoomTombstoneContent = roomTombstoneContent;
        synchronized (this) {
            for (Entry entry : this.mMembers.entrySet()) {
                roomState.setMember((String) entry.getKey(), ((RoomMember) entry.getValue()).deepCopy());
            }
            for (String str : this.mThirdPartyInvites.keySet()) {
                roomState.mThirdPartyInvites.put(str, ((RoomThirdPartyInvite) this.mThirdPartyInvites.get(str)).deepCopy());
            }
            for (String str2 : this.mMembersWithThirdPartyInviteTokenCache.keySet()) {
                roomState.mMembersWithThirdPartyInviteTokenCache.put(str2, ((RoomMember) this.mMembersWithThirdPartyInviteTokenCache.get(str2)).deepCopy());
            }
        }
        return roomState;
    }

    public String getAlias() {
        if (!TextUtils.isEmpty(this.alias)) {
            return this.alias;
        }
        if (!TextUtils.isEmpty(getFirstAlias())) {
            return getFirstAlias();
        }
        if (!TextUtils.isEmpty(this.canonical_alias)) {
            return this.canonical_alias;
        }
        return null;
    }

    private String getFirstAlias() {
        List aliases2 = getAliases();
        if (aliases2.size() != 0) {
            return (String) aliases2.get(0);
        }
        return null;
    }

    public List<String> getAliases() {
        if (this.mMergedAliasesList == null) {
            this.mMergedAliasesList = new ArrayList();
            for (String str : this.mAliasesByDomain.keySet()) {
                this.mMergedAliasesList.addAll((Collection) this.mAliasesByDomain.get(str));
            }
            if (this.aliases != null) {
                for (String str2 : this.aliases) {
                    if (this.mMergedAliasesList.indexOf(str2) < 0) {
                        this.mMergedAliasesList.add(str2);
                    }
                }
            }
        }
        return this.mMergedAliasesList;
    }

    public Map<String, List<String>> getAliasesByDomain() {
        return new HashMap(this.mAliasesByDomain);
    }

    public void removeAlias(String str) {
        if (getAliases().indexOf(str) >= 0) {
            if (this.aliases != null) {
                this.aliases.remove(str);
            }
            for (String str2 : this.mAliasesByDomain.keySet()) {
                ((List) this.mAliasesByDomain.get(str2)).remove(str);
            }
            this.mMergedAliasesList = null;
        }
    }

    public void addAlias(String str) {
        if (getAliases().indexOf(str) < 0) {
            this.mMergedAliasesList.add(str);
        }
    }

    public String getDisplayName(String str) {
        String str2;
        String alias2 = getAlias();
        synchronized (this) {
            Entry entry = null;
            if (this.name != null) {
                str2 = this.name;
            } else if (!TextUtils.isEmpty(alias2)) {
                str2 = getAlias();
            } else if (this.mMembers.size() > 0) {
                Iterator it = this.mMembers.entrySet().iterator();
                if (this.mMembers.size() < 3 || str == null) {
                    String memberName = getMemberName(str);
                    if (str != null) {
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Entry entry2 = (Entry) it.next();
                            if (!str.equals(entry2.getKey())) {
                                entry = entry2;
                                break;
                            }
                        }
                    }
                    str2 = entry != null ? ((RoomMember) entry.getValue()).getName() != null ? getMemberName(((RoomMember) entry.getValue()).getUserId()) : getMemberName((String) entry.getKey()) : memberName;
                } else {
                    int i = 0;
                    String str3 = "";
                    while (it.hasNext()) {
                        Entry entry3 = (Entry) it.next();
                        if (!str.equals(entry3.getKey())) {
                            if (i > 0) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(str3);
                                sb.append(", ");
                                str3 = sb.toString();
                            }
                            if (((RoomMember) entry3.getValue()).getName() != null) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(str3);
                                sb2.append(getMemberName(((RoomMember) entry3.getValue()).getUserId()));
                                str3 = sb2.toString();
                            } else {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(str3);
                                sb3.append(getMemberName((String) entry3.getKey()));
                                str3 = sb3.toString();
                            }
                            i++;
                        }
                    }
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("(");
                    sb4.append(i);
                    sb4.append(") ");
                    sb4.append(str3);
                    str2 = sb4.toString();
                }
            } else {
                str2 = null;
            }
        }
        if (str2 == null || alias2 == null || str2.equals(alias2)) {
            alias2 = str2;
        } else if (!TextUtils.isEmpty(str2)) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str2);
            sb5.append(" (");
            sb5.append(alias2);
            sb5.append(")");
            alias2 = sb5.toString();
        }
        if (alias2 == null) {
            return this.roomId;
        }
        return alias2;
    }

    public boolean isEncrypted() {
        return this.algorithm != null;
    }

    public boolean isVersioned() {
        return this.mRoomTombstoneContent != null;
    }

    public RoomTombstoneContent getRoomTombstoneContent() {
        return this.mRoomTombstoneContent;
    }

    public boolean hasPredecessor() {
        return this.mRoomCreateContent != null && this.mRoomCreateContent.hasPredecessor();
    }

    public RoomCreateContent getRoomCreateContent() {
        return this.mRoomCreateContent;
    }

    public String encryptionAlgorithm() {
        if (TextUtils.isEmpty(this.algorithm)) {
            return null;
        }
        return this.algorithm;
    }

    public boolean applyState(IMXStore iMXStore, Event event, Direction direction) {
        if (event.stateKey == null) {
            return false;
        }
        JsonObject contentAsJsonObject = direction == Direction.FORWARDS ? event.getContentAsJsonObject() : event.getPrevContentAsJsonObject();
        String type = event.getType();
        try {
            if (Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type)) {
                this.name = JsonUtils.toRoomState(contentAsJsonObject).name;
            } else if (Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type)) {
                this.topic = JsonUtils.toRoomState(contentAsJsonObject).topic;
            } else if (Event.EVENT_TYPE_STATE_ROOM_CREATE.equals(type)) {
                this.mRoomCreateContent = JsonUtils.toRoomCreateContent(contentAsJsonObject);
            } else if (Event.EVENT_TYPE_STATE_ROOM_JOIN_RULES.equals(type)) {
                this.join_rule = JsonUtils.toRoomState(contentAsJsonObject).join_rule;
            } else if (Event.EVENT_TYPE_STATE_ROOM_GUEST_ACCESS.equals(type)) {
                this.guest_access = JsonUtils.toRoomState(contentAsJsonObject).guest_access;
            } else if (Event.EVENT_TYPE_STATE_ROOM_ALIASES.equals(type)) {
                if (!TextUtils.isEmpty(event.stateKey)) {
                    this.aliases = JsonUtils.toRoomState(contentAsJsonObject).aliases;
                    if (this.aliases != null) {
                        this.mAliasesByDomain.put(event.stateKey, this.aliases);
                        this.mRoomAliases.put(event.stateKey, event);
                    } else {
                        this.mAliasesByDomain.put(event.stateKey, new ArrayList());
                    }
                }
            } else if (Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type)) {
                this.algorithm = JsonUtils.toRoomState(contentAsJsonObject).algorithm;
                if (this.algorithm == null) {
                    this.algorithm = "";
                }
            } else if (Event.EVENT_TYPE_STATE_CANONICAL_ALIAS.equals(type)) {
                this.alias = JsonUtils.toRoomState(contentAsJsonObject).alias;
            } else if (Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type)) {
                this.history_visibility = JsonUtils.toRoomState(contentAsJsonObject).history_visibility;
            } else if (Event.EVENT_TYPE_STATE_ROOM_AVATAR.equals(type)) {
                this.url = JsonUtils.toRoomState(contentAsJsonObject).url;
            } else if (Event.EVENT_TYPE_STATE_RELATED_GROUPS.equals(type)) {
                this.groups = JsonUtils.toRoomState(contentAsJsonObject).groups;
            } else if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type)) {
                RoomMember roomMember = JsonUtils.toRoomMember(contentAsJsonObject);
                String str = event.stateKey;
                if (str == null) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## applyState() : null stateKey in ");
                    sb.append(this.roomId);
                    Log.m211e(str2, sb.toString());
                } else if (roomMember != null) {
                    try {
                        roomMember.setUserId(str);
                        roomMember.setOriginServerTs(event.getOriginServerTs());
                        roomMember.setOriginalEventId(event.eventId);
                        roomMember.mSender = event.getSender();
                        if (iMXStore != null && direction == Direction.FORWARDS) {
                            iMXStore.storeRoomStateEvent(this.roomId, event);
                        }
                        RoomMember member = getMember(str);
                        if (roomMember.equals(member)) {
                            String str3 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## applyState() : seems being a duplicated event for ");
                            sb2.append(str);
                            sb2.append(" in room ");
                            sb2.append(this.roomId);
                            Log.m211e(str3, sb2.toString());
                            return false;
                        }
                        if (member != null && (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_LEAVE) || TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_BAN))) {
                            if (roomMember.getAvatarUrl() == null) {
                                roomMember.setAvatarUrl(member.getAvatarUrl());
                            }
                            if (roomMember.displayname == null) {
                                roomMember.displayname = member.displayname;
                            }
                            if (this.mMemberDisplayNameByUserId != null) {
                                this.mMemberDisplayNameByUserId.remove(str);
                            }
                            if (!TextUtils.equals(event.getSender(), event.stateKey) && TextUtils.equals(member.membership, RoomMember.MEMBERSHIP_JOIN) && TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_LEAVE)) {
                                roomMember.membership = RoomMember.MEMBERSHIP_KICK;
                            }
                        }
                        if (direction == Direction.FORWARDS && iMXStore != null) {
                            iMXStore.updateUserWithRoomMemberEvent(roomMember);
                        }
                        if (!TextUtils.isEmpty(roomMember.getThirdPartyInviteToken())) {
                            this.mMembersWithThirdPartyInviteTokenCache.put(roomMember.getThirdPartyInviteToken(), roomMember);
                        }
                        setMember(str, roomMember);
                    } catch (Exception e) {
                        String str4 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## applyState() - EVENT_TYPE_STATE_ROOM_MEMBER failed ");
                        sb3.append(e.getMessage());
                        Log.m212e(str4, sb3.toString(), e);
                    }
                } else if (getMember(str) == null) {
                    String str5 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## applyState() : the user ");
                    sb4.append(str);
                    sb4.append(" is not anymore a member of ");
                    sb4.append(this.roomId);
                    Log.m211e(str5, sb4.toString());
                    return false;
                } else {
                    removeMember(str);
                }
            } else if (Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS.equals(type)) {
                this.powerLevels = JsonUtils.toPowerLevels(contentAsJsonObject);
            } else if (Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(event.getType())) {
                if (contentAsJsonObject != null) {
                    RoomThirdPartyInvite roomThirdPartyInvite = JsonUtils.toRoomThirdPartyInvite(contentAsJsonObject);
                    roomThirdPartyInvite.token = event.stateKey;
                    if (direction == Direction.FORWARDS && iMXStore != null) {
                        iMXStore.storeRoomStateEvent(this.roomId, event);
                    }
                    if (!TextUtils.isEmpty(roomThirdPartyInvite.token)) {
                        this.mThirdPartyInvites.put(roomThirdPartyInvite.token, roomThirdPartyInvite);
                    }
                }
            } else if (Event.EVENT_TYPE_STATE_ROOM_TOMBSTONE.equals(type)) {
                this.mRoomTombstoneContent = JsonUtils.toRoomTombstoneContent(contentAsJsonObject);
            }
            if (!TextUtils.isEmpty(type) && !Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type)) {
                List list = (List) this.mStateEvents.get(type);
                if (list == null) {
                    list = new ArrayList();
                    this.mStateEvents.put(type, list);
                }
                list.add(event);
            }
        } catch (Exception e2) {
            String str6 = LOG_TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("applyState failed with error ");
            sb5.append(e2.getMessage());
            Log.m212e(str6, sb5.toString(), e2);
        }
        return true;
    }

    public boolean isPublic() {
        return TextUtils.equals(this.visibility != null ? this.visibility : this.join_rule, "public");
    }

    public String getMemberName(String str) {
        String str2;
        if (str == null) {
            return null;
        }
        synchronized (this) {
            if (this.mMemberDisplayNameByUserId == null) {
                this.mMemberDisplayNameByUserId = new HashMap();
            }
            str2 = (String) this.mMemberDisplayNameByUserId.get(str);
        }
        if (str2 != null) {
            return str2;
        }
        RoomMember member = getMember(str);
        if (member != null && !TextUtils.isEmpty(member.displayname)) {
            str2 = member.displayname;
            synchronized (this) {
                ArrayList arrayList = new ArrayList();
                for (RoomMember roomMember : this.mMembers.values()) {
                    if (str2.equals(roomMember.displayname)) {
                        arrayList.add(roomMember.getUserId());
                    }
                }
                if (arrayList.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(" (");
                    sb.append(str);
                    sb.append(")");
                    str2 = sb.toString();
                }
            }
        } else if (member != null && TextUtils.equals(member.membership, "invite")) {
            User user = ((MXDataHandler) this.mDataHandler).getUser(str);
            if (user != null) {
                str2 = user.displayname;
            }
        }
        if (str2 == null) {
            str2 = str;
        }
        this.mMemberDisplayNameByUserId.put(str, str2);
        return str2;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if (objectInput.readBoolean()) {
            this.roomId = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.powerLevels = (PowerLevels) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.aliases = (List) objectInput.readObject();
        }
        for (Event event : (List) objectInput.readObject()) {
            this.mRoomAliases.put(event.stateKey, event);
        }
        this.mAliasesByDomain = (Map) objectInput.readObject();
        if (objectInput.readBoolean()) {
            this.mMergedAliasesList = (List) objectInput.readObject();
        }
        Map map = (Map) objectInput.readObject();
        if (map != null) {
            this.mStateEvents = new HashMap(map);
        }
        if (objectInput.readBoolean()) {
            this.alias = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.name = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.topic = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.url = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.avatar_url = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.mRoomCreateContent = (RoomCreateContent) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.join_rule = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.guest_access = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.history_visibility = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.roomAliasName = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.visibility = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.algorithm = objectInput.readUTF();
        }
        this.mNotificationCount = objectInput.readInt();
        this.mHighlightCount = objectInput.readInt();
        if (objectInput.readBoolean()) {
            this.token = objectInput.readUTF();
        }
        for (RoomMember roomMember : (List) objectInput.readObject()) {
            this.mMembers.put(roomMember.getUserId(), roomMember);
        }
        for (RoomThirdPartyInvite roomThirdPartyInvite : (List) objectInput.readObject()) {
            this.mThirdPartyInvites.put(roomThirdPartyInvite.token, roomThirdPartyInvite);
        }
        for (RoomMember roomMember2 : (List) objectInput.readObject()) {
            this.mMembersWithThirdPartyInviteTokenCache.put(roomMember2.getThirdPartyInviteToken(), roomMember2);
        }
        if (objectInput.readBoolean()) {
            this.mMembership = objectInput.readUTF();
        }
        this.mIsLive = objectInput.readBoolean();
        if (objectInput.readBoolean()) {
            this.mIsConferenceUserRoom = Boolean.valueOf(objectInput.readBoolean());
        }
        if (objectInput.readBoolean()) {
            this.groups = (List) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.mRoomTombstoneContent = (RoomTombstoneContent) objectInput.readObject();
        }
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        boolean z = false;
        objectOutput.writeBoolean(this.roomId != null);
        if (this.roomId != null) {
            objectOutput.writeUTF(this.roomId);
        }
        objectOutput.writeBoolean(this.powerLevels != null);
        if (this.powerLevels != null) {
            objectOutput.writeObject(this.powerLevels);
        }
        objectOutput.writeBoolean(this.aliases != null);
        if (this.aliases != null) {
            objectOutput.writeObject(this.aliases);
        }
        objectOutput.writeObject(new ArrayList(this.mRoomAliases.values()));
        objectOutput.writeObject(this.mAliasesByDomain);
        objectOutput.writeBoolean(this.mMergedAliasesList != null);
        if (this.mMergedAliasesList != null) {
            objectOutput.writeObject(this.mMergedAliasesList);
        }
        objectOutput.writeObject(this.mStateEvents);
        objectOutput.writeBoolean(this.alias != null);
        if (this.alias != null) {
            objectOutput.writeUTF(this.alias);
        }
        objectOutput.writeBoolean(this.name != null);
        if (this.name != null) {
            objectOutput.writeUTF(this.name);
        }
        objectOutput.writeBoolean(this.topic != null);
        if (this.topic != null) {
            objectOutput.writeUTF(this.topic);
        }
        objectOutput.writeBoolean(this.url != null);
        if (this.url != null) {
            objectOutput.writeUTF(this.url);
        }
        objectOutput.writeBoolean(this.avatar_url != null);
        if (this.avatar_url != null) {
            objectOutput.writeUTF(this.avatar_url);
        }
        objectOutput.writeBoolean(this.mRoomCreateContent != null);
        if (this.mRoomCreateContent != null) {
            objectOutput.writeObject(this.mRoomCreateContent);
        }
        objectOutput.writeBoolean(this.join_rule != null);
        if (this.join_rule != null) {
            objectOutput.writeUTF(this.join_rule);
        }
        objectOutput.writeBoolean(this.guest_access != null);
        if (this.guest_access != null) {
            objectOutput.writeUTF(this.guest_access);
        }
        objectOutput.writeBoolean(this.history_visibility != null);
        if (this.history_visibility != null) {
            objectOutput.writeUTF(this.history_visibility);
        }
        objectOutput.writeBoolean(this.roomAliasName != null);
        if (this.roomAliasName != null) {
            objectOutput.writeUTF(this.roomAliasName);
        }
        objectOutput.writeBoolean(this.visibility != null);
        if (this.visibility != null) {
            objectOutput.writeUTF(this.visibility);
        }
        objectOutput.writeBoolean(this.algorithm != null);
        if (this.algorithm != null) {
            objectOutput.writeUTF(this.algorithm);
        }
        objectOutput.writeInt(this.mNotificationCount);
        objectOutput.writeInt(this.mHighlightCount);
        objectOutput.writeBoolean(this.token != null);
        if (this.token != null) {
            objectOutput.writeUTF(this.token);
        }
        objectOutput.writeObject(new ArrayList(this.mMembers.values()));
        objectOutput.writeObject(new ArrayList(this.mThirdPartyInvites.values()));
        objectOutput.writeObject(new ArrayList(this.mMembersWithThirdPartyInviteTokenCache.values()));
        objectOutput.writeBoolean(this.mMembership != null);
        if (this.mMembership != null) {
            objectOutput.writeUTF(this.mMembership);
        }
        objectOutput.writeBoolean(this.mIsLive);
        objectOutput.writeBoolean(this.mIsConferenceUserRoom != null);
        if (this.mIsConferenceUserRoom != null) {
            objectOutput.writeBoolean(this.mIsConferenceUserRoom.booleanValue());
        }
        objectOutput.writeBoolean(this.groups != null);
        if (this.groups != null) {
            objectOutput.writeObject(this.groups);
        }
        if (this.mRoomTombstoneContent != null) {
            z = true;
        }
        objectOutput.writeBoolean(z);
        if (this.mRoomTombstoneContent != null) {
            objectOutput.writeObject(this.mRoomTombstoneContent);
        }
    }
}
