package com.opengarden.firechat.matrixsdk.data;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.Nullable;

public class RoomAccountData implements Serializable {
    private static final long serialVersionUID = -8406116277864521120L;
    private HashMap<String, RoomTag> tags = null;

    public void handleTagEvent(Event event) {
        if (event.getType().equals(Event.EVENT_TYPE_TAGS)) {
            this.tags = RoomTag.roomTagsWithTagEvent(event);
        }
    }

    @Nullable
    public RoomTag roomTag(String str) {
        if (this.tags == null || !this.tags.containsKey(str)) {
            return null;
        }
        return (RoomTag) this.tags.get(str);
    }

    public boolean hasTags() {
        return this.tags != null && this.tags.size() > 0;
    }

    @Nullable
    public Set<String> getKeys() {
        if (hasTags()) {
            return this.tags.keySet();
        }
        return null;
    }
}
