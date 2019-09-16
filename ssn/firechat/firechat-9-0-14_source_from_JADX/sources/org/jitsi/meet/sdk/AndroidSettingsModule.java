package org.jitsi.meet.sdk;

import android.content.Intent;
import android.net.Uri;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;

class AndroidSettingsModule extends ReactContextBaseJavaModule {
    public String getName() {
        return "AndroidSettings";
    }

    public AndroidSettingsModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @ReactMethod
    public void open() {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        Intent intent = new Intent();
        intent.addFlags(ErrorDialogData.BINDER_CRASH);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", reactApplicationContext.getPackageName(), null));
        reactApplicationContext.startActivity(intent);
    }
}
