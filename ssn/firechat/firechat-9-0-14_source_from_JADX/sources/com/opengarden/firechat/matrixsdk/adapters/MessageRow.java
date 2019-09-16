package com.opengarden.firechat.matrixsdk.adapters;

import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class MessageRow {
    private Event mEvent;
    private final RoomState mRoomState;

    public MessageRow(Event event, RoomState roomState) {
        this.mEvent = event;
        this.mRoomState = roomState;
    }

    public Event getEvent() {
        return this.mEvent;
    }

    public void updateEvent(Event event) {
        this.mEvent = event;
    }

    public RoomState getRoomState() {
        return this.mRoomState;
    }
}
