package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingRule {
    public static final String ACTION_COALESCE = "coalesce";
    public static final String ACTION_DONT_NOTIFY = "dont_notify";
    public static final String ACTION_NOTIFY = "notify";
    public static final String ACTION_PARAMETER_SET_TWEAK = "set_tweak";
    public static final String ACTION_PARAMETER_VALUE = "value";
    public static final String ACTION_SET_TWEAK_HIGHLIGHT_VALUE = "highlight";
    public static final String ACTION_SET_TWEAK_SOUND_VALUE = "sound";
    public static final String ACTION_VALUE_DEFAULT = "default";
    public static final String ACTION_VALUE_RING = "ring";
    public static final String KIND_CONTENT = "content";
    public static final String KIND_OVERRIDE = "override";
    public static final String KIND_ROOM = "room";
    public static final String KIND_SENDER = "sender";
    public static final String KIND_UNDERRIDE = "underride";
    private static final String LOG_TAG = "BingRule";
    public static final String RULE_ID_ALL_OTHER_MESSAGES_ROOMS = ".m.rule.message";
    public static final String RULE_ID_CALL = ".m.rule.call";
    public static final String RULE_ID_CONTAIN_DISPLAY_NAME = ".m.rule.contains_display_name";
    public static final String RULE_ID_CONTAIN_USER_NAME = ".m.rule.contains_user_name";
    public static final String RULE_ID_DISABLE_ALL = ".m.rule.master";
    public static final String RULE_ID_FALLBACK = ".m.rule.fallback";
    public static final String RULE_ID_INVITE_ME = ".m.rule.invite_for_me";
    public static final String RULE_ID_ONE_TO_ONE_ROOM = ".m.rule.room_one_to_one";
    public static final String RULE_ID_PEOPLE_JOIN_LEAVE = ".m.rule.member_event";
    public static final String RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS = ".m.rule.suppress_notices";
    public List<Object> actions;
    public List<Condition> conditions;
    @SerializedName("default")
    public boolean isDefault;
    @SerializedName("enabled")
    public boolean isEnabled;
    public String kind;
    public String ruleId;

    public BingRule(boolean z) {
        this.ruleId = null;
        this.conditions = null;
        this.actions = null;
        this.isDefault = false;
        this.isEnabled = true;
        this.kind = null;
        this.isDefault = z;
    }

    public BingRule() {
        this.ruleId = null;
        this.conditions = null;
        this.actions = null;
        this.isDefault = false;
        this.isEnabled = true;
        this.kind = null;
        this.isDefault = false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BingRule{ruleId='");
        sb.append(this.ruleId);
        sb.append('\'');
        sb.append(", conditions=");
        sb.append(this.conditions);
        sb.append(", actions=");
        sb.append(this.actions);
        sb.append(", isDefault=");
        sb.append(this.isDefault);
        sb.append(", isEnabled=");
        sb.append(this.isEnabled);
        sb.append(", kind='");
        sb.append(this.kind);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }

    public JsonElement toJsonElement() {
        JsonObject asJsonObject = JsonUtils.getGson(false).toJsonTree(this).getAsJsonObject();
        if (this.conditions != null) {
            asJsonObject.add("conditions", JsonUtils.getGson(false).toJsonTree(this.conditions));
        }
        return asJsonObject;
    }

    public BingRule(String str, String str2, Boolean bool, Boolean bool2, boolean z) {
        this.ruleId = null;
        this.conditions = null;
        this.actions = null;
        this.isDefault = false;
        this.isEnabled = true;
        this.kind = null;
        this.ruleId = str2;
        this.isEnabled = true;
        this.isDefault = false;
        this.kind = str;
        this.conditions = null;
        this.actions = new ArrayList();
        if (bool != null) {
            setNotify(bool.booleanValue());
        }
        if (bool2 != null) {
            setHighlight(bool2.booleanValue());
        }
        if (z) {
            setNotificationSound();
        }
    }

    public BingRule(BingRule bingRule) {
        this.ruleId = null;
        this.conditions = null;
        this.actions = null;
        this.isDefault = false;
        this.isEnabled = true;
        this.kind = null;
        this.ruleId = bingRule.ruleId;
        if (bingRule.conditions != null) {
            this.conditions = new ArrayList(bingRule.conditions);
        }
        if (bingRule.actions != null) {
            this.actions = new ArrayList(bingRule.actions);
        }
        this.isDefault = bingRule.isDefault;
        this.isEnabled = bingRule.isEnabled;
        this.kind = bingRule.kind;
    }

    public void addCondition(Condition condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList();
        }
        this.conditions.add(condition);
    }

    public Map<String, Object> getActionMap(String str) {
        if (this.actions != null && !TextUtils.isEmpty(str)) {
            for (Object next : this.actions) {
                if (next instanceof Map) {
                    try {
                        Map<String, Object> map = (Map) next;
                        if (TextUtils.equals((String) map.get(ACTION_PARAMETER_SET_TWEAK), str)) {
                            return map;
                        }
                    } catch (Exception e) {
                        String str2 = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## getActionMap() : ");
                        sb.append(e.getMessage());
                        Log.m211e(str2, sb.toString());
                    }
                }
            }
        }
        return null;
    }

    public static boolean isDefaultNotificationSound(String str) {
        return ACTION_VALUE_DEFAULT.equals(str);
    }

    public static boolean isCallRingNotificationSound(String str) {
        return ACTION_VALUE_RING.equals(str);
    }

    public String getNotificationSound() {
        Map actionMap = getActionMap(ACTION_SET_TWEAK_SOUND_VALUE);
        if (actionMap == null || !actionMap.containsKey("value")) {
            return null;
        }
        return (String) actionMap.get("value");
    }

    public void setNotificationSound() {
        setNotificationSound(ACTION_VALUE_DEFAULT);
    }

    public void setNotificationSound(String str) {
        removeNotificationSound();
        if (!TextUtils.isEmpty(str)) {
            HashMap hashMap = new HashMap();
            hashMap.put(ACTION_PARAMETER_SET_TWEAK, ACTION_SET_TWEAK_SOUND_VALUE);
            hashMap.put("value", str);
            this.actions.add(hashMap);
        }
    }

    public void removeNotificationSound() {
        Map actionMap = getActionMap(ACTION_SET_TWEAK_SOUND_VALUE);
        if (actionMap != null) {
            this.actions.remove(actionMap);
        }
    }

    public void setHighlight(boolean z) {
        Map actionMap = getActionMap(ACTION_SET_TWEAK_HIGHLIGHT_VALUE);
        if (actionMap == null) {
            actionMap = new HashMap();
            actionMap.put(ACTION_PARAMETER_SET_TWEAK, ACTION_SET_TWEAK_HIGHLIGHT_VALUE);
            this.actions.add(actionMap);
        }
        if (z) {
            actionMap.remove("value");
        } else {
            actionMap.put("value", Boolean.valueOf(false));
        }
    }

    public boolean shouldHighlight() {
        Map actionMap = getActionMap(ACTION_SET_TWEAK_HIGHLIGHT_VALUE);
        if (actionMap == null) {
            return false;
        }
        if (!actionMap.containsKey("value")) {
            return true;
        }
        Object obj = actionMap.get("value");
        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        }
        if (obj instanceof String) {
            return TextUtils.equals((String) obj, "true");
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## shouldHighlight() : unexpected type ");
        sb.append(obj);
        Log.m211e(str, sb.toString());
        return true;
    }

    public void setNotify(boolean z) {
        if (z) {
            this.actions.remove(ACTION_DONT_NOTIFY);
            if (!this.actions.contains(ACTION_NOTIFY)) {
                this.actions.add(ACTION_NOTIFY);
                return;
            }
            return;
        }
        this.actions.remove(ACTION_NOTIFY);
        if (!this.actions.contains(ACTION_DONT_NOTIFY)) {
            this.actions.add(ACTION_DONT_NOTIFY);
        }
    }

    public boolean shouldNotify() {
        return this.actions.contains(ACTION_NOTIFY);
    }

    public boolean shouldNotNotify() {
        return this.actions.contains(ACTION_DONT_NOTIFY);
    }
}
