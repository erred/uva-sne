package com.facebook.react.uimanager.events;

public enum TouchEventType {
    START("topTouchStart"),
    END("topTouchEnd"),
    MOVE("topTouchMove"),
    CANCEL("topTouchCancel");
    
    private final String mJSEventName;

    private TouchEventType(String str) {
        this.mJSEventName = str;
    }

    public String getJSEventName() {
        return this.mJSEventName;
    }
}
