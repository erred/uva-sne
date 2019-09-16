package com.opengarden.firechat.matrixsdk.rest.model.filter;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.util.ArrayList;
import java.util.List;

public class FilterBody {
    public static final String LOG_TAG = "FilterBody";
    private static FilterBody dataSaveModeFilterBody;
    @SerializedName("account_data")
    public Filter accountData;
    @SerializedName("event_fields")
    public List<String> eventFields;
    @SerializedName("event_format")
    public String eventFormat;
    public Filter presence;
    public RoomFilter room;

    public static FilterBody getDataSaveModeFilterBody() {
        if (dataSaveModeFilterBody == null) {
            FilterBody filterBody = new FilterBody();
            filterBody.room = new RoomFilter();
            filterBody.room.ephemeral = new RoomEventFilter();
            filterBody.room.ephemeral.types = new ArrayList();
            filterBody.room.ephemeral.types.add(Event.EVENT_TYPE_RECEIPT);
            filterBody.presence = new Filter();
            filterBody.presence.notTypes = new ArrayList();
            filterBody.presence.notTypes.add("*");
            dataSaveModeFilterBody = filterBody;
        }
        return dataSaveModeFilterBody;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LOG_TAG);
        sb.append(toJSONString());
        return sb.toString();
    }

    public String toJSONString() {
        return new Gson().toJson((Object) this);
    }
}
