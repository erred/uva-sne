package com.opengarden.firechat.matrixsdk.crypto.algorithms;

import com.google.gson.JsonElement;
import java.util.List;
import java.util.Map;

public class MXDecryptionResult {
    public List<String> mForwardingCurve25519KeyChain;
    public Map<String, String> mKeysClaimed;
    public JsonElement mPayload;
    public String mSenderKey;
}
