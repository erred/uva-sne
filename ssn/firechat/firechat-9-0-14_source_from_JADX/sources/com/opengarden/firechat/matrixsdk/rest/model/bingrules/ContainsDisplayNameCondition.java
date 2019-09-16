package com.opengarden.firechat.matrixsdk.rest.model.bingrules;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.EventUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;

public class ContainsDisplayNameCondition extends Condition {
    public ContainsDisplayNameCondition() {
        this.kind = Condition.KIND_CONTAINS_DISPLAY_NAME;
    }

    public boolean isSatisfied(Event event, String str) {
        if (Event.EVENT_TYPE_MESSAGE.equals(event.getType())) {
            Message message = JsonUtils.toMessage(event.getContent());
            if (message != null) {
                return EventUtils.caseInsensitiveFind(str, message.body);
            }
        }
        return false;
    }
}
