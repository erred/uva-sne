package com.opengarden.firechat.matrixsdk.crypto.algorithms;

import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.MXDecryptionException;
import com.opengarden.firechat.matrixsdk.crypto.MXEventDecryptionResult;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public interface IMXDecrypting {
    MXEventDecryptionResult decryptEvent(Event event, String str) throws MXDecryptionException;

    boolean hasKeysForKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest);

    void initWithMatrixSession(MXSession mXSession);

    void onNewSession(String str, String str2);

    void onRoomKeyEvent(Event event);

    void shareKeysWithDevice(IncomingRoomKeyRequest incomingRoomKeyRequest);
}
