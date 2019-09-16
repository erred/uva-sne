package com.rnimmersive;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.view.View.OnSystemUiVisibilityChangeListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

public class RNImmersiveModule extends ReactContextBaseJavaModule {
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    private static final String ERROR_NO_ACTIVITY_MESSAGE = "Tried to set immersive while not attached to an Activity";
    private static RNImmersiveModule SINGLETON = null;
    private static final int UI_FLAG_IMMERSIVE = 5894;
    /* access modifiers changed from: private */
    public boolean _isImmersiveOn = false;
    private ReactContext _reactContext = null;

    public String getName() {
        return "RNImmersive";
    }

    public static RNImmersiveModule getInstance() {
        return SINGLETON;
    }

    public RNImmersiveModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this._reactContext = reactApplicationContext;
        SINGLETON = this;
    }

    public void onCatalystInstanceDestroy() {
        this._reactContext = null;
        SINGLETON = null;
    }

    @ReactMethod
    public void setImmersive(boolean z, Promise promise) {
        _setImmersive(z, promise);
    }

    @ReactMethod
    public void getImmersive(Promise promise) {
        _getImmersive(promise);
    }

    @ReactMethod
    public void addImmersiveListener() {
        _addImmersiveListener();
    }

    public void emitImmersiveStateChangeEvent() {
        if (this._reactContext != null) {
            ((RCTDeviceEventEmitter) this._reactContext.getJSModule(RCTDeviceEventEmitter.class)).emit("@@IMMERSIVE_STATE_CHANGED", null);
        }
    }

    private void _setImmersive(final boolean z, final Promise promise) {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
            return;
        }
        if (VERSION.SDK_INT >= 19) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                @TargetApi(19)
                public void run() {
                    RNImmersiveModule.this._isImmersiveOn = z;
                    currentActivity.getWindow().getDecorView().setSystemUiVisibility(z ? RNImmersiveModule.UI_FLAG_IMMERSIVE : 0);
                    promise.resolve(null);
                }
            });
        }
    }

    private void _getImmersive(final Promise promise) {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
            return;
        }
        if (VERSION.SDK_INT >= 19) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                @TargetApi(19)
                public void run() {
                    boolean z = (currentActivity.getWindow().getDecorView().getSystemUiVisibility() & RNImmersiveModule.UI_FLAG_IMMERSIVE) != 0;
                    WritableMap createMap = Arguments.createMap();
                    createMap.putBoolean("isImmersiveOn", z);
                    promise.resolve(createMap);
                }
            });
        }
    }

    private void _addImmersiveListener() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity != null && VERSION.SDK_INT >= 19) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                @TargetApi(19)
                public void run() {
                    currentActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
                        public void onSystemUiVisibilityChange(int i) {
                            if (((i & RNImmersiveModule.UI_FLAG_IMMERSIVE) != 0) != RNImmersiveModule.this._isImmersiveOn) {
                                RNImmersiveModule.this.emitImmersiveStateChangeEvent();
                            }
                        }
                    });
                }
            });
        }
    }
}
