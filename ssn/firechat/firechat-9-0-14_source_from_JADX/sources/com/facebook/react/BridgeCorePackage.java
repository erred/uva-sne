package com.facebook.react;

import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ExceptionsManagerModule;
import com.facebook.react.modules.core.HeadlessJsTaskSupportModule;
import com.facebook.react.modules.core.Timing;
import com.facebook.react.modules.debug.SourceCodeModule;
import com.facebook.react.modules.deviceinfo.DeviceInfoModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;

class BridgeCorePackage extends LazyReactPackage {
    /* access modifiers changed from: private */
    public final DefaultHardwareBackBtnHandler mHardwareBackBtnHandler;
    /* access modifiers changed from: private */
    public final ReactInstanceManager mReactInstanceManager;

    BridgeCorePackage(ReactInstanceManager reactInstanceManager, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
        this.mReactInstanceManager = reactInstanceManager;
        this.mHardwareBackBtnHandler = defaultHardwareBackBtnHandler;
    }

    public List<ModuleSpec> getNativeModules(final ReactApplicationContext reactApplicationContext) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ModuleSpec(AndroidInfoModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new AndroidInfoModule();
            }
        }));
        arrayList.add(new ModuleSpec(DeviceEventManagerModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new DeviceEventManagerModule(reactApplicationContext, BridgeCorePackage.this.mHardwareBackBtnHandler);
            }
        }));
        arrayList.add(new ModuleSpec(ExceptionsManagerModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new ExceptionsManagerModule(BridgeCorePackage.this.mReactInstanceManager.getDevSupportManager());
            }
        }));
        arrayList.add(new ModuleSpec(HeadlessJsTaskSupportModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new HeadlessJsTaskSupportModule(reactApplicationContext);
            }
        }));
        arrayList.add(new ModuleSpec(SourceCodeModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new SourceCodeModule(reactApplicationContext);
            }
        }));
        arrayList.add(new ModuleSpec(Timing.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new Timing(reactApplicationContext, BridgeCorePackage.this.mReactInstanceManager.getDevSupportManager());
            }
        }));
        arrayList.add(new ModuleSpec(DeviceInfoModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new DeviceInfoModule(reactApplicationContext);
            }
        }));
        return arrayList;
    }

    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return LazyReactPackage.getReactModuleInfoProviderViaReflection(this);
    }
}
