package com.opengarden.firechat.matrixsdk.rest.model.sync;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.io.Serializable;
import java.util.List;

public class RoomSyncState implements Serializable {
    public List<Event> events;
}
