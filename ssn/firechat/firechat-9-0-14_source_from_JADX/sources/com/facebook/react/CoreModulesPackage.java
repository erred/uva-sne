package com.facebook.react;

import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ExceptionsManagerModule;
import com.facebook.react.modules.core.HeadlessJsTaskSupportModule;
import com.facebook.react.modules.core.Timing;
import com.facebook.react.modules.debug.AnimationsDebugModule;
import com.facebook.react.modules.debug.SourceCodeModule;
import com.facebook.react.modules.deviceinfo.DeviceInfoModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIManagerModule.ViewManagerResolver;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.systrace.Systrace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Provider;

class CoreModulesPackage extends LazyReactPackage implements ReactPackageLogger {
    /* access modifiers changed from: private */
    public final DefaultHardwareBackBtnHandler mHardwareBackBtnHandler;
    private final boolean mLazyViewManagersEnabled;
    private final int mMinTimeLeftInFrameForNonBatchedOperationMs;
    /* access modifiers changed from: private */
    public final ReactInstanceManager mReactInstanceManager;
    private final UIImplementationProvider mUIImplementationProvider;

    CoreModulesPackage(ReactInstanceManager reactInstanceManager, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler, UIImplementationProvider uIImplementationProvider, boolean z, int i) {
        this.mReactInstanceManager = reactInstanceManager;
        this.mHardwareBackBtnHandler = defaultHardwareBackBtnHandler;
        this.mUIImplementationProvider = uIImplementationProvider;
        this.mLazyViewManagersEnabled = z;
        this.mMinTimeLeftInFrameForNonBatchedOperationMs = i;
    }

    public List<ModuleSpec> getNativeModules(final ReactApplicationContext reactApplicationContext) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ModuleSpec(AndroidInfoModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new AndroidInfoModule();
            }
        }));
        arrayList.add(new ModuleSpec(AnimationsDebugModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new AnimationsDebugModule(reactApplicationContext, CoreModulesPackage.this.mReactInstanceManager.getDevSupportManager().getDevSettings());
            }
        }));
        arrayList.add(new ModuleSpec(DeviceEventManagerModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new DeviceEventManagerModule(reactApplicationContext, CoreModulesPackage.this.mHardwareBackBtnHandler);
            }
        }));
        arrayList.add(new ModuleSpec(ExceptionsManagerModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return new ExceptionsManagerModule(CoreModulesPackage.this.mReactInstanceManager.getDevSupportManager());
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
                return new Timing(reactApplicationContext, CoreModulesPackage.this.mReactInstanceManager.getDevSupportManager());
            }
        }));
        arrayList.add(new ModuleSpec(UIManagerModule.class, new Provider<NativeModule>() {
            public NativeModule get() {
                return CoreModulesPackage.this.createUIManager(reactApplicationContext);
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

    /* access modifiers changed from: private */
    public UIManagerModule createUIManager(ReactApplicationContext reactApplicationContext) {
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_START);
        Systrace.beginSection(0, "createUIManagerModule");
        try {
            if (this.mLazyViewManagersEnabled) {
                UIManagerModule uIManagerModule = new UIManagerModule(reactApplicationContext, (ViewManagerResolver) new ViewManagerResolver() {
                    @Nullable
                    public ViewManager getViewManager(String str) {
                        return CoreModulesPackage.this.mReactInstanceManager.createViewManager(str);
                    }

                    public List<String> getViewManagerNames() {
                        return CoreModulesPackage.this.mReactInstanceManager.getViewManagerNames();
                    }
                }, this.mUIImplementationProvider, this.mMinTimeLeftInFrameForNonBatchedOperationMs);
                return uIManagerModule;
            }
            UIManagerModule uIManagerModule2 = new UIManagerModule(reactApplicationContext, this.mReactInstanceManager.createAllViewManagers(reactApplicationContext), this.mUIImplementationProvider, this.mMinTimeLeftInFrameForNonBatchedOperationMs);
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
            return uIManagerModule2;
        } finally {
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
        }
    }

    public void startProcessPackage() {
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_START);
    }

    public void endProcessPackage() {
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_END);
    }
}
