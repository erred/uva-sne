package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;

public class RoomKeyRequest implements Serializable {
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_REQUEST_CANCELLATION = "request_cancellation";
    public String action;
    public RoomKeyRequestBody body;
    public String request_id;
    public String requesting_device_id;
}
