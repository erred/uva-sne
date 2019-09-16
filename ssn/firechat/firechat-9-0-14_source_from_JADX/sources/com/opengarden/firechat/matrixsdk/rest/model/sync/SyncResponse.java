package com.opengarden.firechat.matrixsdk.rest.model.sync;

import com.opengarden.firechat.matrixsdk.rest.model.group.GroupsSyncResponse;
import java.io.Serializable;
import java.util.Map;

public class SyncResponse implements Serializable {
    public Map<String, Object> accountData;
    public DeviceListResponse deviceLists;
    public DeviceOneTimeKeysCountSyncResponse deviceOneTimeKeysCount;
    public GroupsSyncResponse groups;
    public String nextBatch;
    public PresenceSyncResponse presence;
    public RoomsSyncResponse rooms;
    public ToDeviceSyncResponse toDevice;
}
