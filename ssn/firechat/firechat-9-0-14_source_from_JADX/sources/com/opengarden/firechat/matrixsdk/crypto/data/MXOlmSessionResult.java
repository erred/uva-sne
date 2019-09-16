package com.opengarden.firechat.matrixsdk.crypto.data;

import java.io.Serializable;

public class MXOlmSessionResult implements Serializable {
    public final MXDeviceInfo mDevice;
    public String mSessionId;

    public MXOlmSessionResult(MXDeviceInfo mXDeviceInfo, String str) {
        this.mDevice = mXDeviceInfo;
        this.mSessionId = str;
    }
}
