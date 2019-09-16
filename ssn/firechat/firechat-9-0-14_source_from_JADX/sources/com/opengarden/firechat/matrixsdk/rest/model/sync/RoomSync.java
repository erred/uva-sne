package com.opengarden.firechat.matrixsdk.rest.model.sync;

import java.io.Serializable;

public class RoomSync implements Serializable {
    public RoomSyncAccountData accountData;
    public RoomSyncEphemeral ephemeral;
    public RoomSyncState state;
    public RoomSyncTimeline timeline;
    public RoomSyncUnreadNotifications unreadNotifications;
}
