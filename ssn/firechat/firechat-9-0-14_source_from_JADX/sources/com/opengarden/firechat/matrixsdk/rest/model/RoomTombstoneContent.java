package com.opengarden.firechat.matrixsdk.rest.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RoomTombstoneContent implements Serializable {
    public String body;
    @SerializedName("replacement_room")
    public String replacementRoom;

    public RoomTombstoneContent deepCopy() {
        RoomTombstoneContent roomTombstoneContent = new RoomTombstoneContent();
        roomTombstoneContent.body = this.body;
        roomTombstoneContent.replacementRoom = this.replacementRoom;
        return roomTombstoneContent;
    }
}
