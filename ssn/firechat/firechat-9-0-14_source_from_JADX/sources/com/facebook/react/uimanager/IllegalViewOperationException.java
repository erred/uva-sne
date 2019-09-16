package com.facebook.react.uimanager;

import com.facebook.react.bridge.JSApplicationCausedNativeException;

public class IllegalViewOperationException extends JSApplicationCausedNativeException {
    public IllegalViewOperationException(String str) {
        super(str);
    }
}
