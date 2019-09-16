package com.facebook.react;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.ModuleHolder;
import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.OnBatchCompleteListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.model.ReactModuleInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NativeModuleRegistryBuilder {
    private final boolean mLazyNativeModulesEnabled;
    private final Map<Class<? extends NativeModule>, ModuleHolder> mModules = new HashMap();
    private final ReactApplicationContext mReactApplicationContext;
    private final ReactInstanceManager mReactInstanceManager;
    private final Map<String, Class<? extends NativeModule>> namesToType = new HashMap();

    public NativeModuleRegistryBuilder(ReactApplicationContext reactApplicationContext, ReactInstanceManager reactInstanceManager, boolean z) {
        this.mReactApplicationContext = reactApplicationContext;
        this.mReactInstanceManager = reactInstanceManager;
        this.mLazyNativeModulesEnabled = z;
    }

    /* JADX INFO: finally extract failed */
    public void processPackage(ReactPackage reactPackage) {
        List<NativeModule> list;
        ModuleHolder moduleHolder;
        if (!this.mLazyNativeModulesEnabled) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(reactPackage.getClass().getSimpleName());
            sb.append(" is not a LazyReactPackage, falling back to old version.");
            FLog.m53d(str, sb.toString());
            if (reactPackage instanceof ReactInstancePackage) {
                list = ((ReactInstancePackage) reactPackage).createNativeModules(this.mReactApplicationContext, this.mReactInstanceManager);
            } else {
                list = reactPackage.createNativeModules(this.mReactApplicationContext);
            }
            for (NativeModule addNativeModule : list) {
                addNativeModule(addNativeModule);
            }
        } else if (!(reactPackage instanceof LazyReactPackage)) {
            throw new IllegalStateException("Lazy native modules requires all ReactPackage to inherit from LazyReactPackage");
        } else {
            LazyReactPackage lazyReactPackage = (LazyReactPackage) reactPackage;
            List<ModuleSpec> nativeModules = lazyReactPackage.getNativeModules(this.mReactApplicationContext);
            Map reactModuleInfos = lazyReactPackage.getReactModuleInfoProvider().getReactModuleInfos();
            for (ModuleSpec moduleSpec : nativeModules) {
                Class type = moduleSpec.getType();
                ReactModuleInfo reactModuleInfo = (ReactModuleInfo) reactModuleInfos.get(type);
                if (reactModuleInfo != null) {
                    moduleHolder = new ModuleHolder(reactModuleInfo, moduleSpec.getProvider());
                } else if (BaseJavaModule.class.isAssignableFrom(type)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Native Java module ");
                    sb2.append(type.getSimpleName());
                    sb2.append(" should be annotated with @ReactModule and added to a @ReactModuleList.");
                    throw new IllegalStateException(sb2.toString());
                } else {
                    ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_START, moduleSpec.getType().getName());
                    try {
                        NativeModule nativeModule = (NativeModule) moduleSpec.getProvider().get();
                        ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
                        moduleHolder = new ModuleHolder(nativeModule);
                    } catch (Throwable th) {
                        ReactMarker.logMarker(ReactMarkerConstants.CREATE_MODULE_END);
                        throw th;
                    }
                }
                String name = moduleHolder.getName();
                if (this.namesToType.containsKey(name)) {
                    Class cls = (Class) this.namesToType.get(name);
                    if (!moduleHolder.getCanOverrideExistingModule()) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Native module ");
                        sb3.append(type.getSimpleName());
                        sb3.append(" tried to override ");
                        sb3.append(cls.getSimpleName());
                        sb3.append(" for module name ");
                        sb3.append(name);
                        sb3.append(". If this was your intention, set canOverrideExistingModule=true");
                        throw new IllegalStateException(sb3.toString());
                    }
                    this.mModules.remove(cls);
                }
                this.namesToType.put(name, type);
                this.mModules.put(type, moduleHolder);
            }
        }
    }

    public void addNativeModule(NativeModule nativeModule) {
        String name = nativeModule.getName();
        Class cls = nativeModule.getClass();
        if (this.namesToType.containsKey(name)) {
            Class cls2 = (Class) this.namesToType.get(name);
            if (!nativeModule.canOverrideExistingModule()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Native module ");
                sb.append(cls.getSimpleName());
                sb.append(" tried to override ");
                sb.append(cls2.getSimpleName());
                sb.append(" for module name ");
                sb.append(name);
                sb.append(". If this was your intention, set canOverrideExistingModule=true");
                throw new IllegalStateException(sb.toString());
            }
            this.mModules.remove(cls2);
        }
        this.namesToType.put(name, cls);
        this.mModules.put(cls, new ModuleHolder(nativeModule));
    }

    public NativeModuleRegistry build() {
        ArrayList arrayList = new ArrayList();
        for (Entry entry : this.mModules.entrySet()) {
            if (OnBatchCompleteListener.class.isAssignableFrom((Class) entry.getKey())) {
                arrayList.add(entry.getValue());
            }
        }
        return new NativeModuleRegistry(this.mReactApplicationContext, this.mModules, arrayList);
    }
}
