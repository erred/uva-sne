package com.oney.WebRTCModule;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class RTCVideoViewManager extends SimpleViewManager<WebRTCView> {
    private static final String REACT_CLASS = "RTCVideoView";

    public String getName() {
        return REACT_CLASS;
    }

    public WebRTCView createViewInstance(ThemedReactContext themedReactContext) {
        return new WebRTCView(themedReactContext);
    }

    @ReactProp(name = "mirror")
    public void setMirror(WebRTCView webRTCView, boolean z) {
        webRTCView.setMirror(z);
    }

    @ReactProp(name = "objectFit")
    public void setObjectFit(WebRTCView webRTCView, String str) {
        webRTCView.setObjectFit(str);
    }

    @ReactProp(name = "streamURL")
    public void setStreamURL(WebRTCView webRTCView, String str) {
        webRTCView.setStreamURL(str);
    }

    @ReactProp(name = "zOrder")
    public void setZOrder(WebRTCView webRTCView, int i) {
        webRTCView.setZOrder(i);
    }
}
