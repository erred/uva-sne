package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import android.util.Patterns;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.pid.Invite3Pid;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateRoomParams {
    public static final String PRESET_PRIVATE_CHAT = "private_chat";
    public static final String PRESET_PUBLIC_CHAT = "public_chat";
    public static final String PRESET_TRUSTED_PRIVATE_CHAT = "trusted_private_chat";
    public Object creation_content;
    public String guest_access;
    public List<Event> initial_state;
    public List<String> invite;
    public List<Invite3Pid> invite_3pid;
    public Boolean is_direct;
    public String name;
    public String preset;
    public String roomAliasName;
    public String topic;
    public String visibility;

    public void addCryptoAlgorithm(String str) {
        if (!TextUtils.isEmpty(str)) {
            Event event = new Event();
            event.type = Event.EVENT_TYPE_MESSAGE_ENCRYPTION;
            HashMap hashMap = new HashMap();
            hashMap.put("algorithm", str);
            event.content = JsonUtils.getGson(false).toJsonTree(hashMap);
            if (this.initial_state == null) {
                this.initial_state = Arrays.asList(new Event[]{event});
                return;
            }
            this.initial_state.add(event);
        }
    }

    public void setHistoryVisibility(String str) {
        if (!TextUtils.isEmpty(str)) {
            Event event = new Event();
            event.type = Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY;
            HashMap hashMap = new HashMap();
            hashMap.put("history_visibility", str);
            event.content = JsonUtils.getGson(false).toJsonTree(hashMap);
            if (this.initial_state == null) {
                this.initial_state = Arrays.asList(new Event[]{event});
                return;
            }
            this.initial_state.add(event);
        } else if (!this.initial_state.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            for (Event event2 : this.initial_state) {
                if (!event2.type.equals(Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY)) {
                    arrayList.add(event2);
                }
            }
            this.initial_state = arrayList;
        }
    }

    public void setDirectMessage() {
        this.preset = PRESET_TRUSTED_PRIVATE_CHAT;
        this.is_direct = Boolean.valueOf(true);
    }

    private int getInviteCount() {
        if (this.invite == null) {
            return 0;
        }
        return this.invite.size();
    }

    private int getInvite3PidCount() {
        if (this.invite_3pid == null) {
            return 0;
        }
        return this.invite_3pid.size();
    }

    public boolean isDirect() {
        if (!TextUtils.equals(this.preset, PRESET_TRUSTED_PRIVATE_CHAT) || this.is_direct == null || !this.is_direct.booleanValue() || (1 != getInviteCount() && 1 != getInvite3PidCount())) {
            return false;
        }
        return true;
    }

    public String getFirstInvitedUserId() {
        if (getInviteCount() != 0) {
            return (String) this.invite.get(0);
        }
        if (getInvite3PidCount() != 0) {
            return ((Invite3Pid) this.invite_3pid.get(0)).address;
        }
        return null;
    }

    public void addParticipantIds(HomeServerConnectionConfig homeServerConnectionConfig, List<String> list) {
        for (String str : list) {
            if (Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
                if (this.invite_3pid == null) {
                    this.invite_3pid = new ArrayList();
                }
                Invite3Pid invite3Pid = new Invite3Pid();
                invite3Pid.id_server = homeServerConnectionConfig.getIdentityServerUri().getHost();
                invite3Pid.medium = "email";
                invite3Pid.address = str;
                this.invite_3pid.add(invite3Pid);
            } else if (MXSession.isUserId(str) && !TextUtils.equals(homeServerConnectionConfig.getCredentials().userId, str)) {
                if (this.invite == null) {
                    this.invite = new ArrayList();
                }
                this.invite.add(str);
            }
        }
    }
}
