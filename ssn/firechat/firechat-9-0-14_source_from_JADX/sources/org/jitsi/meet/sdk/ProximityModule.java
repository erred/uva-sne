package org.jitsi.meet.sdk;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;

class ProximityModule extends ReactContextBaseJavaModule {
    private static final String MODULE_NAME = "Proximity";
    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    /* access modifiers changed from: private */
    public final WakeLock wakeLock;

    public String getName() {
        return MODULE_NAME;
    }

    public ProximityModule(ReactApplicationContext reactApplicationContext) {
        WakeLock wakeLock2;
        super(reactApplicationContext);
        try {
            wakeLock2 = ((PowerManager) reactApplicationContext.getSystemService("power")).newWakeLock(32, MODULE_NAME);
        } catch (Throwable unused) {
            wakeLock2 = null;
        }
        this.wakeLock = wakeLock2;
    }

    @ReactMethod
    public void setEnabled(final boolean z) {
        if (this.wakeLock != null) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    if (z) {
                        if (!ProximityModule.this.wakeLock.isHeld()) {
                            ProximityModule.this.wakeLock.acquire();
                        }
                    } else if (ProximityModule.this.wakeLock.isHeld()) {
                        ProximityModule.this.wakeLock.release();
                    }
                }
            });
        }
    }
}
