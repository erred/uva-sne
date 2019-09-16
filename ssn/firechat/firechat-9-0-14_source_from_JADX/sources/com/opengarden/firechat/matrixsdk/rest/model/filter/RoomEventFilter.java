package com.opengarden.firechat.matrixsdk.rest.model.filter;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoomEventFilter {
    @SerializedName("contains_url")
    public Boolean containsUrl;
    public Integer limit;
    @SerializedName("not_rooms")
    public List<String> notRooms;
    @SerializedName("not_senders")
    public List<String> notSenders;
    @SerializedName("not_types")
    public List<String> notTypes;
    public List<String> rooms;
    public List<String> senders;
    public List<String> types;
}
