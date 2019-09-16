package com.opengarden.firechat.matrixsdk.crypto;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class MXEventDecryptionResult {
    public String mClaimedEd25519Key;
    public JsonElement mClearEvent;
    public List<String> mForwardingCurve25519KeyChain = new ArrayList();
    public String mSenderCurve25519Key;
}
