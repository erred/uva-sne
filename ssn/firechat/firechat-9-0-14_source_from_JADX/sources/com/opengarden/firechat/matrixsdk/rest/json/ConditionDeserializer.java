package com.opengarden.firechat.matrixsdk.rest.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.Condition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.ContainsDisplayNameCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.DeviceCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.EventMatchCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.RoomMemberCountCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.SenderNotificationPermissionCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.UnknownCondition;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.lang.reflect.Type;

public class ConditionDeserializer implements JsonDeserializer<Condition> {
    private static final String LOG_TAG = "ConditionDeserializer";

    public Condition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonElement jsonElement2 = jsonElement.getAsJsonObject().get("kind");
        if (jsonElement2 != null) {
            String asString = jsonElement2.getAsString();
            if (asString != null) {
                char c = 65535;
                switch (asString.hashCode()) {
                    case -1335157162:
                        if (asString.equals(Condition.KIND_DEVICE)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -895393272:
                        if (asString.equals(Condition.KIND_CONTAINS_DISPLAY_NAME)) {
                            c = 2;
                            break;
                        }
                        break;
                    case -224562791:
                        if (asString.equals(Condition.KIND_SENDER_NOTIFICATION_PERMISSION)) {
                            c = 4;
                            break;
                        }
                        break;
                    case 443732960:
                        if (asString.equals(Condition.KIND_EVENT_MATCH)) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1519110158:
                        if (asString.equals(Condition.KIND_ROOM_MEMBER_COUNT)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, EventMatchCondition.class);
                    case 1:
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, DeviceCondition.class);
                    case 2:
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, ContainsDisplayNameCondition.class);
                    case 3:
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, RoomMemberCountCondition.class);
                    case 4:
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, SenderNotificationPermissionCondition.class);
                    default:
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## deserialize() : unsupported kind ");
                        sb.append(asString);
                        sb.append(" with value ");
                        sb.append(jsonElement);
                        Log.m211e(str, sb.toString());
                        return (Condition) jsonDeserializationContext.deserialize(jsonElement, UnknownCondition.class);
                }
            }
        }
        return null;
    }
}
