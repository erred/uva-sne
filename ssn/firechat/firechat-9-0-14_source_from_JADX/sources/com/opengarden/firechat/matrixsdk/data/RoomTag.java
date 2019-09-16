package com.opengarden.firechat.matrixsdk.data;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.RoomTags;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Serializable;
import java.util.HashMap;

public class RoomTag implements Serializable {
    private static final String LOG_TAG = "RoomTag";
    public static final String ROOM_TAG_FAVOURITE = "m.favourite";
    public static final String ROOM_TAG_LOW_PRIORITY = "m.lowpriority";
    public static final String ROOM_TAG_NO_TAG = "m.recent";
    public static final String ROOM_TAG_SERVER_NOTICE = "m.server_notice";
    private static final long serialVersionUID = 5172602958896551204L;
    public String mName;
    public Double mOrder;

    public RoomTag(String str, Double d) {
        this.mName = str;
        this.mOrder = d;
    }

    public static HashMap<String, RoomTag> roomTagsWithTagEvent(Event event) {
        HashMap<String, RoomTag> hashMap = new HashMap<>();
        try {
            RoomTags roomTags = JsonUtils.toRoomTags(event.getContent());
            if (!(roomTags.tags == null || roomTags.tags.size() == 0)) {
                for (String str : roomTags.tags.keySet()) {
                    HashMap hashMap2 = (HashMap) roomTags.tags.get(str);
                    if (hashMap2 != null) {
                        hashMap.put(str, new RoomTag(str, (Double) hashMap2.get("order")));
                    } else {
                        hashMap.put(str, new RoomTag(str, null));
                    }
                }
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("roomTagsWithTagEvent fails ");
            sb.append(e.getMessage());
            Log.m209d(str2, sb.toString());
        }
        return hashMap;
    }
}
