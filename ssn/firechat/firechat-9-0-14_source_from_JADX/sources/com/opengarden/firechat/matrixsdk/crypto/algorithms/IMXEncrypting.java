package com.opengarden.firechat.matrixsdk.crypto.algorithms;

import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import java.util.List;

public interface IMXEncrypting {
    void encryptEventContent(JsonElement jsonElement, String str, List<String> list, ApiCallback<JsonElement> apiCallback);

    void initWithMatrixSession(MXSession mXSession, String str);
}
