package com.facebook.react.modules.systeminfo;

import android.os.Build.VERSION;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@ReactModule(name = "PlatformConstants")
public class AndroidInfoModule extends BaseJavaModule {
    private static final String IS_TESTING = "IS_TESTING";

    public String getName() {
        return "PlatformConstants";
    }

    @Nullable
    public Map<String, Object> getConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put("Version", Integer.valueOf(VERSION.SDK_INT));
        hashMap.put("ServerHost", AndroidInfoHelpers.getServerHost());
        hashMap.put("isTesting", Boolean.valueOf("true".equals(System.getProperty(IS_TESTING))));
        hashMap.put("reactNativeVersion", ReactNativeVersion.VERSION);
        return hashMap;
    }
}
