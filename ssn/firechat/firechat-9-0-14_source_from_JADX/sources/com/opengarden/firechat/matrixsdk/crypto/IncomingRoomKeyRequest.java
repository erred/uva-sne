package com.opengarden.firechat.matrixsdk.crypto;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequest;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequestBody;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.io.Serializable;

public class IncomingRoomKeyRequest implements Serializable {
    public String mDeviceId;
    public transient Runnable mIgnore;
    public RoomKeyRequestBody mRequestBody;
    public String mRequestId;
    public transient Runnable mShare;
    public String mUserId;

    public IncomingRoomKeyRequest(Event event) {
        this.mUserId = event.getSender();
        RoomKeyRequest roomKeyRequest = JsonUtils.toRoomKeyRequest(event.getContentAsJsonObject());
        this.mDeviceId = roomKeyRequest.requesting_device_id;
        this.mRequestId = roomKeyRequest.request_id;
        this.mRequestBody = roomKeyRequest.body != null ? roomKeyRequest.body : new RoomKeyRequestBody();
    }
}
