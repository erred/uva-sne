package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PowerLevels implements Serializable {
    public int ban = 50;
    public Map<String, Integer> events = new HashMap();
    public int events_default = 0;
    public int invite = 50;
    public int kick = 50;
    public Map<String, Object> notifications = new HashMap();
    public int redact = 50;
    public int state_default = 50;
    public Map<String, Integer> users = new HashMap();
    public int users_default = 0;

    public PowerLevels deepCopy() {
        PowerLevels powerLevels = new PowerLevels();
        powerLevels.ban = this.ban;
        powerLevels.kick = this.kick;
        powerLevels.invite = this.invite;
        powerLevels.redact = this.redact;
        powerLevels.events_default = this.events_default;
        powerLevels.events = new HashMap();
        powerLevels.events.putAll(this.events);
        powerLevels.users_default = this.users_default;
        powerLevels.users = new HashMap();
        powerLevels.users.putAll(this.users);
        powerLevels.state_default = this.state_default;
        powerLevels.notifications = new HashMap(this.notifications);
        return powerLevels;
    }

    public int getUserPowerLevel(String str) {
        if (TextUtils.isEmpty(str)) {
            return 50;
        }
        Integer num = (Integer) this.users.get(str);
        return num != null ? num.intValue() : this.users_default;
    }

    public void setUserPowerLevel(String str, int i) {
        if (str != null) {
            this.users.put(str, Integer.valueOf(i));
        }
    }

    public boolean maySendEventOfType(String str, String str2) {
        boolean z = false;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        if (getUserPowerLevel(str2) >= minimumPowerLevelForSendingEventAsMessage(str)) {
            z = true;
        }
        return z;
    }

    public boolean maySendMessage(String str) {
        return maySendEventOfType(Event.EVENT_TYPE_MESSAGE, str);
    }

    public int minimumPowerLevelForSendingEventAsMessage(String str) {
        int i = this.events_default;
        return (str == null || !this.events.containsKey(str)) ? i : ((Integer) this.events.get(str)).intValue();
    }

    public int minimumPowerLevelForSendingEventAsStateEvent(String str) {
        int i = this.state_default;
        return (str == null || !this.events.containsKey(str)) ? i : ((Integer) this.events.get(str)).intValue();
    }

    public int notificationLevel(String str) {
        if (str == null || !this.notifications.containsKey(str)) {
            return this.users_default;
        }
        Object obj = this.notifications.get(str);
        if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        }
        return ((Integer) obj).intValue();
    }
}
