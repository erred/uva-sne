package com.opengarden.firechat.matrixsdk.rest.model.sync;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.io.Serializable;
import java.util.List;

public class RoomSyncAccountData implements Serializable {
    public List<Event> events;
}
