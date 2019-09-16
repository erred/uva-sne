package com.opengarden.firechat.matrixsdk.crypto;

import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class IncomingRoomKeyRequestCancellation extends IncomingRoomKeyRequest {
    public IncomingRoomKeyRequestCancellation(Event event) {
        super(event);
        this.mRequestBody = null;
    }
}
