package com.i18n.reactnativei18n;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import java.util.HashMap;
import java.util.Map;

public class ReactNativeI18nLocale extends ReactContextBaseJavaModule {
    ReactContext reactContext;

    public String getName() {
        return "RNI18n";
    }

    public ReactNativeI18nLocale(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.reactContext = reactApplicationContext;
    }

    public Map<String, Object> getConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put("locale", this.reactContext.getResources().getConfiguration().locale.toString());
        return hashMap;
    }
}
