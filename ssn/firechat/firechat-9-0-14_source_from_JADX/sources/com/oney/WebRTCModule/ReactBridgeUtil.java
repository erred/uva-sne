package com.oney.WebRTCModule;

import com.facebook.react.bridge.ReadableMap;

public class ReactBridgeUtil {
    public static String getMapStrValue(ReadableMap readableMap, String str) {
        if (!readableMap.hasKey(str)) {
            return null;
        }
        switch (readableMap.getType(str)) {
            case Boolean:
                return String.valueOf(readableMap.getBoolean(str));
            case Number:
                return String.valueOf(readableMap.getDouble(str));
            case String:
                return readableMap.getString(str);
            default:
                return null;
        }
    }
}
