package com.ocetnik.timer;

import android.os.Handler;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

public class BackgroundTimerModule extends ReactContextBaseJavaModule {
    private Handler handler;
    /* access modifiers changed from: private */
    public ReactContext reactContext;
    private Runnable runnable;

    public String getName() {
        return "RNBackgroundTimer";
    }

    public BackgroundTimerModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.reactContext = reactApplicationContext;
    }

    @ReactMethod
    public void start(int i) {
        this.handler = new Handler();
        this.runnable = new Runnable() {
            public void run() {
                BackgroundTimerModule.this.sendEvent(BackgroundTimerModule.this.reactContext, "backgroundTimer");
            }
        };
        this.handler.post(this.runnable);
    }

    @ReactMethod
    public void stop() {
        if (this.handler != null) {
            this.handler.removeCallbacks(this.runnable);
        }
    }

    /* access modifiers changed from: private */
    public void sendEvent(ReactContext reactContext2, String str) {
        ((RCTDeviceEventEmitter) reactContext2.getJSModule(RCTDeviceEventEmitter.class)).emit(str, null);
    }

    @ReactMethod
    public void setTimeout(final int i, int i2) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (BackgroundTimerModule.this.getReactApplicationContext().hasActiveCatalystInstance()) {
                    ((RCTDeviceEventEmitter) BackgroundTimerModule.this.getReactApplicationContext().getJSModule(RCTDeviceEventEmitter.class)).emit("backgroundTimer.timeout", Integer.valueOf(i));
                }
            }
        }, (long) i2);
    }
}
