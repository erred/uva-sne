package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import java.util.Map;

public class KeysQueryResponse {
    public Map<String, Map<String, MXDeviceInfo>> deviceKeys;
    public Map<String, Map<String, Object>> failures;
}
