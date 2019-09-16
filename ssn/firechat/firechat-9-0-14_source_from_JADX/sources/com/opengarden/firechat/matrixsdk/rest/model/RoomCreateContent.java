package com.opengarden.firechat.matrixsdk.rest.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RoomCreateContent implements Serializable {
    public String creator;
    public Predecessor predecessor;

    public static class Predecessor implements Serializable {
        @SerializedName("event_id")
        public String eventId;
        @SerializedName("room_id")
        public String roomId;

        public Predecessor deepCopy() {
            Predecessor predecessor = new Predecessor();
            predecessor.roomId = this.roomId;
            predecessor.eventId = this.eventId;
            return predecessor;
        }
    }

    public RoomCreateContent deepCopy() {
        RoomCreateContent roomCreateContent = new RoomCreateContent();
        roomCreateContent.creator = this.creator;
        roomCreateContent.predecessor = this.predecessor != null ? this.predecessor.deepCopy() : null;
        return roomCreateContent;
    }

    public boolean hasPredecessor() {
        return this.predecessor != null;
    }
}
