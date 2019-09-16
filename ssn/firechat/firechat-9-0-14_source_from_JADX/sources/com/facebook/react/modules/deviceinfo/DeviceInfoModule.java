package com.facebook.react.modules.deviceinfo;

import android.content.Context;
import android.util.DisplayMetrics;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.opengarden.firechat.matrixsdk.util.ContentManager;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@ReactModule(name = "DeviceInfo")
public class DeviceInfoModule extends BaseJavaModule implements LifecycleEventListener {
    private float mFontScale;
    @Nullable
    private ReactApplicationContext mReactApplicationContext;

    public String getName() {
        return "DeviceInfo";
    }

    public void onHostDestroy() {
    }

    public void onHostPause() {
    }

    public DeviceInfoModule(ReactApplicationContext reactApplicationContext) {
        this((Context) reactApplicationContext);
        this.mReactApplicationContext = reactApplicationContext;
    }

    public DeviceInfoModule(Context context) {
        this.mReactApplicationContext = null;
        DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(context);
        this.mFontScale = context.getResources().getConfiguration().fontScale;
    }

    @Nullable
    public Map<String, Object> getConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put("Dimensions", getDimensionsConstants());
        return hashMap;
    }

    public void onHostResume() {
        if (this.mReactApplicationContext != null) {
            float f = this.mReactApplicationContext.getResources().getConfiguration().fontScale;
            if (this.mFontScale != f) {
                this.mFontScale = f;
                emitUpdateDimensionsEvent();
            }
        }
    }

    public void emitUpdateDimensionsEvent() {
        if (this.mReactApplicationContext != null) {
            ((RCTDeviceEventEmitter) this.mReactApplicationContext.getJSModule(RCTDeviceEventEmitter.class)).emit("didUpdateDimensions", getDimensionsConstants());
        }
    }

    private WritableMap getDimensionsConstants() {
        DisplayMetrics windowDisplayMetrics = DisplayMetricsHolder.getWindowDisplayMetrics();
        DisplayMetrics screenDisplayMetrics = DisplayMetricsHolder.getScreenDisplayMetrics();
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("width", windowDisplayMetrics.widthPixels);
        createMap.putInt("height", windowDisplayMetrics.heightPixels);
        createMap.putDouble(ContentManager.METHOD_SCALE, (double) windowDisplayMetrics.density);
        createMap.putDouble("fontScale", (double) this.mFontScale);
        createMap.putDouble("densityDpi", (double) windowDisplayMetrics.densityDpi);
        WritableMap createMap2 = Arguments.createMap();
        createMap2.putInt("width", screenDisplayMetrics.widthPixels);
        createMap2.putInt("height", screenDisplayMetrics.heightPixels);
        createMap2.putDouble(ContentManager.METHOD_SCALE, (double) screenDisplayMetrics.density);
        createMap2.putDouble("fontScale", (double) this.mFontScale);
        createMap2.putDouble("densityDpi", (double) screenDisplayMetrics.densityDpi);
        WritableMap createMap3 = Arguments.createMap();
        createMap3.putMap("windowPhysicalPixels", createMap);
        createMap3.putMap("screenPhysicalPixels", createMap2);
        return createMap3;
    }
}
