package com.opengarden.firechat.matrixsdk.rest.model.sync;

import java.io.Serializable;
import java.util.Map;

public class RoomsSyncResponse implements Serializable {
    public Map<String, InvitedRoomSync> invite;
    public Map<String, RoomSync> join;
    public Map<String, RoomSync> leave;
}
