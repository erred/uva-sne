package com.corbt.keepawake;

import android.app.Activity;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class KCKeepAwake extends ReactContextBaseJavaModule {
    public String getName() {
        return "KCKeepAwake";
    }

    public KCKeepAwake(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @ReactMethod
    public void activate() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    currentActivity.getWindow().addFlags(128);
                }
            });
        }
    }

    @ReactMethod
    public void deactivate() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    currentActivity.getWindow().clearFlags(128);
                }
            });
        }
    }
}
