package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.List;

public abstract class ReactInstancePackage implements ReactPackage {
    public abstract List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext, ReactInstanceManager reactInstanceManager);

    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        throw new RuntimeException("ReactInstancePackage must be passed in the ReactInstanceManager.");
    }
}
