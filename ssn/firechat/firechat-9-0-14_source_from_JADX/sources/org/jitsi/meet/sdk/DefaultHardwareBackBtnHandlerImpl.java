package org.jitsi.meet.sdk;

import android.app.Activity;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

public class DefaultHardwareBackBtnHandlerImpl implements DefaultHardwareBackBtnHandler {
    private final Activity activity;

    public DefaultHardwareBackBtnHandlerImpl(Activity activity2) {
        this.activity = activity2;
    }

    public void invokeDefaultOnBackPressed() {
        this.activity.finish();
    }
}
