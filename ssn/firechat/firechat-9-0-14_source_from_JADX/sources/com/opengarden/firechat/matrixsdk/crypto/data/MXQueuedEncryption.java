package com.opengarden.firechat.matrixsdk.crypto.data;

import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;

public class MXQueuedEncryption {
    public ApiCallback<JsonElement> mApiCallback;
    public JsonElement mEventContent;
    public String mEventType;
}
