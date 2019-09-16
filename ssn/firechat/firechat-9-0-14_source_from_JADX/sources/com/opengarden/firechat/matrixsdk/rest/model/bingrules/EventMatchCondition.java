package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.util.HashMap;
import java.util.regex.Pattern;

public class EventMatchCondition extends Condition {
    private static HashMap<String, Pattern> mPatternByRule;
    public String key;
    public String pattern;

    public EventMatchCondition() {
        this.kind = Condition.KIND_EVENT_MATCH;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EventMatchCondition{key='");
        sb.append(this.key);
        sb.append(", pattern=");
        sb.append(this.pattern);
        sb.append('}');
        return sb.toString();
    }

    public boolean isSatisfied(Event event) {
        String extractField = (!event.isEncrypted() || event.getClearEvent() == null) ? null : extractField(event.getClearEvent().toJsonObject(), this.key);
        if (TextUtils.isEmpty(extractField)) {
            extractField = extractField(event.toJsonObject(), this.key);
        }
        if (TextUtils.isEmpty(extractField)) {
            return false;
        }
        if (TextUtils.equals(this.pattern, extractField)) {
            return true;
        }
        if (mPatternByRule == null) {
            mPatternByRule = new HashMap<>();
        }
        Pattern pattern2 = (Pattern) mPatternByRule.get(this.pattern);
        if (pattern2 == null) {
            pattern2 = Pattern.compile(globToRegex(this.pattern), 2);
            mPatternByRule.put(this.pattern, pattern2);
        }
        return pattern2.matcher(extractField).matches();
    }

    private String extractField(JsonObject jsonObject, String str) {
        String str2 = null;
        JsonElement jsonElement = null;
        for (String str3 : str.split("\\.")) {
            jsonElement = jsonObject.get(str3);
            if (jsonElement == null) {
                return null;
            }
            if (jsonElement.isJsonObject()) {
                jsonObject = (JsonObject) jsonElement;
            }
        }
        if (jsonElement != null) {
            str2 = jsonElement.getAsString();
        }
        return str2;
    }

    private String globToRegex(String str) {
        String replace = str.replace("*", ".*").replace("?", ".");
        if (!replace.equals(str)) {
            return replace;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(^|.*\\W)");
        sb.append(replace);
        sb.append("($|\\W.*)");
        return sb.toString();
    }
}
