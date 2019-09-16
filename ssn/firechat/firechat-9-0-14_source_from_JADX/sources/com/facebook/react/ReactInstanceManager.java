package com.facebook.react;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.util.Log;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.Assertions;
import com.facebook.infer.annotation.ThreadConfined;
import com.facebook.infer.annotation.ThreadSafe;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.CatalystInstanceImpl;
import com.facebook.react.bridge.CatalystInstanceImpl.Builder;
import com.facebook.react.bridge.CatalystInstanceImpl.PendingJSCall;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JavaJSExecutor.Factory;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.NativeArray;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.NativeModuleRegistry;
import com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener;
import com.facebook.react.bridge.ProxyJavaScriptExecutor;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.queue.ReactQueueConfigurationSpec;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.devsupport.DevSupportManagerFactory;
import com.facebook.react.devsupport.ReactInstanceDevCommandsHandler;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.modules.appregistry.AppRegistry;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.soloader.SoLoader;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

@ThreadSafe
public class ReactInstanceManager {
    private static final String TAG = "ReactInstanceManager";
    private final Context mApplicationContext;
    private final List<ReactRootView> mAttachedRootViews = Collections.synchronizedList(new ArrayList());
    private final DefaultHardwareBackBtnHandler mBackBtnHandler = new DefaultHardwareBackBtnHandler() {
        public void invokeDefaultOnBackPressed() {
            ReactInstanceManager.this.invokeDefaultOnBackPressed();
        }
    };
    @Nullable
    private final NotThreadSafeBridgeIdleDebugListener mBridgeIdleDebugListener;
    @Nullable
    private final JSBundleLoader mBundleLoader;
    /* access modifiers changed from: private */
    @Nullable
    public volatile Thread mCreateReactContextThread;
    @Nullable
    private Activity mCurrentActivity;
    @Nullable
    private volatile ReactContext mCurrentReactContext;
    @ThreadConfined("UI")
    @Nullable
    private DefaultHardwareBackBtnHandler mDefaultBackButtonImpl;
    private final ReactInstanceDevCommandsHandler mDevInterface = new ReactInstanceDevCommandsHandler() {
        public void onReloadWithJSDebugger(Factory factory) {
            ReactInstanceManager.this.onReloadWithJSDebugger(factory);
        }

        public void onJSBundleLoadedFromServer() {
            ReactInstanceManager.this.onJSBundleLoadedFromServer();
        }

        public void toggleElementInspector() {
            ReactInstanceManager.this.toggleElementInspector();
        }
    };
    /* access modifiers changed from: private */
    public final DevSupportManager mDevSupportManager;
    /* access modifiers changed from: private */
    public volatile boolean mHasStartedCreatingInitialContext = false;
    /* access modifiers changed from: private */
    public volatile Boolean mHasStartedDestroying = Boolean.valueOf(false);
    private final List<PendingJSCall> mInitFunctions;
    @Nullable
    private final String mJSMainModulePath;
    private final JavaScriptExecutorFactory mJavaScriptExecutorFactory;
    private final boolean mLazyNativeModulesEnabled;
    private final boolean mLazyViewManagersEnabled;
    private volatile LifecycleState mLifecycleState;
    private final MemoryPressureRouter mMemoryPressureRouter;
    private final int mMinNumShakes;
    private final int mMinTimeLeftInFrameForNonBatchedOperationMs;
    @Nullable
    private final NativeModuleCallExceptionHandler mNativeModuleCallExceptionHandler;
    private final List<ReactPackage> mPackages;
    /* access modifiers changed from: private */
    @ThreadConfined("UI")
    @Nullable
    public ReactContextInitParams mPendingReactContextInitParams;
    private final Object mReactContextLock = new Object();
    private final Collection<ReactInstanceEventListener> mReactInstanceEventListeners = Collections.synchronizedSet(new HashSet());
    private final UIImplementationProvider mUIImplementationProvider;
    private final boolean mUseDeveloperSupport;
    private final boolean mUseSeparateUIBackgroundThread;

    private class ReactContextInitParams {
        private final JSBundleLoader mJsBundleLoader;
        private final JavaScriptExecutorFactory mJsExecutorFactory;

        public ReactContextInitParams(JavaScriptExecutorFactory javaScriptExecutorFactory, JSBundleLoader jSBundleLoader) {
            this.mJsExecutorFactory = (JavaScriptExecutorFactory) Assertions.assertNotNull(javaScriptExecutorFactory);
            this.mJsBundleLoader = (JSBundleLoader) Assertions.assertNotNull(jSBundleLoader);
        }

        public JavaScriptExecutorFactory getJsExecutorFactory() {
            return this.mJsExecutorFactory;
        }

        public JSBundleLoader getJsBundleLoader() {
            return this.mJsBundleLoader;
        }
    }

    public interface ReactInstanceEventListener {
        void onReactContextInitialized(ReactContext reactContext);
    }

    public static ReactInstanceManagerBuilder builder() {
        return new ReactInstanceManagerBuilder();
    }

    ReactInstanceManager(Context context, @Nullable Activity activity, @Nullable DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler, JavaScriptExecutorFactory javaScriptExecutorFactory, @Nullable JSBundleLoader jSBundleLoader, @Nullable String str, List<ReactPackage> list, boolean z, @Nullable NotThreadSafeBridgeIdleDebugListener notThreadSafeBridgeIdleDebugListener, LifecycleState lifecycleState, UIImplementationProvider uIImplementationProvider, NativeModuleCallExceptionHandler nativeModuleCallExceptionHandler, @Nullable RedBoxHandler redBoxHandler, boolean z2, boolean z3, @Nullable DevBundleDownloadListener devBundleDownloadListener, boolean z4, int i, boolean z5, boolean z6, int i2) {
        Context context2 = context;
        Log.d(ReactConstants.TAG, "ReactInstanceManager.ctor()");
        initializeSoLoaderIfNecessary(context);
        DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(context);
        this.mApplicationContext = context2;
        this.mCurrentActivity = activity;
        this.mDefaultBackButtonImpl = defaultHardwareBackBtnHandler;
        this.mJavaScriptExecutorFactory = javaScriptExecutorFactory;
        this.mBundleLoader = jSBundleLoader;
        this.mJSMainModulePath = str;
        this.mPackages = new ArrayList();
        this.mInitFunctions = new ArrayList();
        boolean z7 = z;
        this.mUseDeveloperSupport = z7;
        this.mDevSupportManager = DevSupportManagerFactory.create(context2, this.mDevInterface, this.mJSMainModulePath, z7, redBoxHandler, devBundleDownloadListener, i);
        this.mBridgeIdleDebugListener = notThreadSafeBridgeIdleDebugListener;
        this.mLifecycleState = lifecycleState;
        this.mUIImplementationProvider = uIImplementationProvider;
        this.mMemoryPressureRouter = new MemoryPressureRouter(context2);
        this.mNativeModuleCallExceptionHandler = nativeModuleCallExceptionHandler;
        this.mLazyNativeModulesEnabled = z2;
        this.mLazyViewManagersEnabled = z3;
        this.mMinTimeLeftInFrameForNonBatchedOperationMs = i2;
        this.mUseSeparateUIBackgroundThread = z4;
        this.mMinNumShakes = i;
        synchronized (this.mPackages) {
            if (!z5) {
                try {
                    CoreModulesPackage coreModulesPackage = new CoreModulesPackage(this, this.mBackBtnHandler, this.mUIImplementationProvider, this.mLazyViewManagersEnabled, this.mMinTimeLeftInFrameForNonBatchedOperationMs);
                    this.mPackages.add(coreModulesPackage);
                } catch (Throwable th) {
                    while (true) {
                        throw th;
                    }
                }
            } else {
                PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: Use Split Packages");
                this.mPackages.add(new BridgeCorePackage(this, this.mBackBtnHandler));
                if (this.mUseDeveloperSupport) {
                    this.mPackages.add(new DebugCorePackage());
                }
                if (!z6) {
                    this.mPackages.add(new ReactNativeCorePackage(this, this.mUIImplementationProvider, this.mLazyViewManagersEnabled, this.mMinTimeLeftInFrameForNonBatchedOperationMs));
                }
            }
            this.mPackages.addAll(list);
        }
        ReactChoreographer.initialize();
        if (this.mUseDeveloperSupport) {
            this.mDevSupportManager.startInspector();
        }
    }

    public DevSupportManager getDevSupportManager() {
        return this.mDevSupportManager;
    }

    public MemoryPressureRouter getMemoryPressureRouter() {
        return this.mMemoryPressureRouter;
    }

    private static void initializeSoLoaderIfNecessary(Context context) {
        SoLoader.init(context, false);
    }

    @ThreadConfined("UI")
    public void createReactContextInBackground() {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.createReactContextInBackground()");
        Assertions.assertCondition(!this.mHasStartedCreatingInitialContext, "createReactContextInBackground should only be called when creating the react application for the first time. When reloading JS, e.g. from a new file, explicitlyuse recreateReactContextInBackground");
        this.mHasStartedCreatingInitialContext = true;
        recreateReactContextInBackgroundInner();
    }

    @ThreadConfined("UI")
    public void registerAdditionalPackages(List<ReactPackage> list) {
        if (list != null && !list.isEmpty()) {
            if (!hasStartedCreatingInitialContext()) {
                synchronized (this.mPackages) {
                    for (ReactPackage reactPackage : list) {
                        if (!this.mPackages.contains(reactPackage)) {
                            this.mPackages.add(reactPackage);
                        }
                    }
                }
                return;
            }
            ReactContext currentReactContext = getCurrentReactContext();
            CatalystInstance catalystInstance = currentReactContext != null ? currentReactContext.getCatalystInstance() : null;
            Assertions.assertNotNull(catalystInstance, "CatalystInstance null after hasStartedCreatingInitialContext true.");
            catalystInstance.extendNativeModules(processPackages(new ReactApplicationContext(this.mApplicationContext), list, true));
        }
    }

    public void registerInitFunction(String str, String str2, @Nullable NativeArray nativeArray) {
        CatalystInstance catalystInstance;
        PendingJSCall pendingJSCall = new PendingJSCall(str, str2, nativeArray);
        synchronized (this) {
            this.mInitFunctions.add(pendingJSCall);
        }
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext == null) {
            catalystInstance = null;
        } else {
            catalystInstance = currentReactContext.getCatalystInstance();
        }
        if (catalystInstance != null) {
            ((CatalystInstanceImpl) catalystInstance).callFunction(pendingJSCall);
        }
    }

    @ThreadConfined("UI")
    public void recreateReactContextInBackground() {
        Assertions.assertCondition(this.mHasStartedCreatingInitialContext, "recreateReactContextInBackground should only be called after the initial createReactContextInBackground call.");
        recreateReactContextInBackgroundInner();
    }

    @ThreadConfined("UI")
    private void recreateReactContextInBackgroundInner() {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.recreateReactContextInBackgroundInner()");
        PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: recreateReactContextInBackground");
        UiThreadUtil.assertOnUiThread();
        if (!this.mUseDeveloperSupport || this.mJSMainModulePath == null || Systrace.isTracing(0)) {
            recreateReactContextInBackgroundFromBundleLoader();
            return;
        }
        final DeveloperSettings devSettings = this.mDevSupportManager.getDevSettings();
        if (this.mDevSupportManager.hasUpToDateJSBundleInCache() && !devSettings.isRemoteJSDebugEnabled()) {
            onJSBundleLoadedFromServer();
        } else if (this.mBundleLoader == null) {
            this.mDevSupportManager.handleReloadJS();
        } else {
            this.mDevSupportManager.isPackagerRunning(new PackagerStatusCallback() {
                public void onPackagerStatusFetched(final boolean z) {
                    UiThreadUtil.runOnUiThread(new Runnable() {
                        public void run() {
                            if (z) {
                                ReactInstanceManager.this.mDevSupportManager.handleReloadJS();
                                return;
                            }
                            devSettings.setRemoteJSDebugEnabled(false);
                            ReactInstanceManager.this.recreateReactContextInBackgroundFromBundleLoader();
                        }
                    });
                }
            });
        }
    }

    /* access modifiers changed from: private */
    @ThreadConfined("UI")
    public void recreateReactContextInBackgroundFromBundleLoader() {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.recreateReactContextInBackgroundFromBundleLoader()");
        PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: load from BundleLoader");
        recreateReactContextInBackground(this.mJavaScriptExecutorFactory, this.mBundleLoader);
    }

    public boolean hasStartedCreatingInitialContext() {
        return this.mHasStartedCreatingInitialContext;
    }

    public void onBackPressed() {
        UiThreadUtil.assertOnUiThread();
        ReactContext reactContext = this.mCurrentReactContext;
        if (reactContext == null) {
            FLog.m105w(ReactConstants.TAG, "Instance detached from instance manager");
            invokeDefaultOnBackPressed();
            return;
        }
        ((DeviceEventManagerModule) reactContext.getNativeModule(DeviceEventManagerModule.class)).emitHardwareBackPressed();
    }

    /* access modifiers changed from: private */
    public void invokeDefaultOnBackPressed() {
        UiThreadUtil.assertOnUiThread();
        if (this.mDefaultBackButtonImpl != null) {
            this.mDefaultBackButtonImpl.invokeDefaultOnBackPressed();
        }
    }

    @ThreadConfined("UI")
    public void onNewIntent(Intent intent) {
        UiThreadUtil.assertOnUiThread();
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext == null) {
            FLog.m105w(ReactConstants.TAG, "Instance detached from instance manager");
            return;
        }
        String action = intent.getAction();
        Uri data = intent.getData();
        if ("android.intent.action.VIEW".equals(action) && data != null) {
            ((DeviceEventManagerModule) currentReactContext.getNativeModule(DeviceEventManagerModule.class)).emitNewIntentReceived(data);
        }
        currentReactContext.onNewIntent(this.mCurrentActivity, intent);
    }

    /* access modifiers changed from: private */
    public void toggleElementInspector() {
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            ((RCTDeviceEventEmitter) currentReactContext.getJSModule(RCTDeviceEventEmitter.class)).emit("toggleElementInspector", null);
        }
    }

    @ThreadConfined("UI")
    public void onHostPause() {
        UiThreadUtil.assertOnUiThread();
        this.mDefaultBackButtonImpl = null;
        if (this.mUseDeveloperSupport) {
            this.mDevSupportManager.setDevSupportEnabled(false);
        }
        moveToBeforeResumeLifecycleState();
    }

    @ThreadConfined("UI")
    public void onHostPause(Activity activity) {
        Assertions.assertNotNull(this.mCurrentActivity);
        boolean z = activity == this.mCurrentActivity;
        StringBuilder sb = new StringBuilder();
        sb.append("Pausing an activity that is not the current activity, this is incorrect! Current activity: ");
        sb.append(this.mCurrentActivity.getClass().getSimpleName());
        sb.append(StringUtils.SPACE);
        sb.append("Paused activity: ");
        sb.append(activity.getClass().getSimpleName());
        Assertions.assertCondition(z, sb.toString());
        onHostPause();
    }

    @ThreadConfined("UI")
    public void onHostResume(Activity activity, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
        UiThreadUtil.assertOnUiThread();
        this.mDefaultBackButtonImpl = defaultHardwareBackBtnHandler;
        if (this.mUseDeveloperSupport) {
            this.mDevSupportManager.setDevSupportEnabled(true);
        }
        this.mCurrentActivity = activity;
        moveToResumedLifecycleState(false);
    }

    @ThreadConfined("UI")
    public void onHostDestroy() {
        UiThreadUtil.assertOnUiThread();
        if (this.mUseDeveloperSupport) {
            this.mDevSupportManager.setDevSupportEnabled(false);
        }
        moveToBeforeCreateLifecycleState();
        this.mCurrentActivity = null;
    }

    @ThreadConfined("UI")
    public void onHostDestroy(Activity activity) {
        if (activity == this.mCurrentActivity) {
            onHostDestroy();
        }
    }

    @ThreadConfined("UI")
    public void destroy() {
        UiThreadUtil.assertOnUiThread();
        PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: Destroy");
        this.mHasStartedDestroying = Boolean.valueOf(true);
        if (this.mUseDeveloperSupport) {
            this.mDevSupportManager.setDevSupportEnabled(false);
            this.mDevSupportManager.stopInspector();
        }
        moveToBeforeCreateLifecycleState();
        if (this.mCreateReactContextThread != null) {
            this.mCreateReactContextThread = null;
        }
        this.mMemoryPressureRouter.destroy(this.mApplicationContext);
        synchronized (this.mReactContextLock) {
            if (this.mCurrentReactContext != null) {
                this.mCurrentReactContext.destroy();
                this.mCurrentReactContext = null;
            }
        }
        this.mHasStartedCreatingInitialContext = false;
        this.mCurrentActivity = null;
        ResourceDrawableIdHelper.getInstance().clear();
        this.mHasStartedDestroying = Boolean.valueOf(false);
        synchronized (this.mHasStartedDestroying) {
            this.mHasStartedDestroying.notifyAll();
        }
    }

    private synchronized void moveToResumedLifecycleState(boolean z) {
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null && (z || this.mLifecycleState == LifecycleState.BEFORE_RESUME || this.mLifecycleState == LifecycleState.BEFORE_CREATE)) {
            currentReactContext.onHostResume(this.mCurrentActivity);
        }
        this.mLifecycleState = LifecycleState.RESUMED;
    }

    private synchronized void moveToBeforeResumeLifecycleState() {
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            if (this.mLifecycleState == LifecycleState.BEFORE_CREATE) {
                currentReactContext.onHostResume(this.mCurrentActivity);
                currentReactContext.onHostPause();
            } else if (this.mLifecycleState == LifecycleState.RESUMED) {
                currentReactContext.onHostPause();
            }
        }
        this.mLifecycleState = LifecycleState.BEFORE_RESUME;
    }

    private synchronized void moveToBeforeCreateLifecycleState() {
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            if (this.mLifecycleState == LifecycleState.RESUMED) {
                currentReactContext.onHostPause();
                this.mLifecycleState = LifecycleState.BEFORE_RESUME;
            }
            if (this.mLifecycleState == LifecycleState.BEFORE_RESUME) {
                currentReactContext.onHostDestroy();
            }
        }
        this.mLifecycleState = LifecycleState.BEFORE_CREATE;
    }

    private synchronized void moveReactContextToCurrentLifecycleState() {
        if (this.mLifecycleState == LifecycleState.RESUMED) {
            moveToResumedLifecycleState(true);
        }
    }

    @ThreadConfined("UI")
    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        ReactContext currentReactContext = getCurrentReactContext();
        if (currentReactContext != null) {
            currentReactContext.onActivityResult(activity, i, i2, intent);
        }
    }

    @ThreadConfined("UI")
    public void showDevOptionsDialog() {
        UiThreadUtil.assertOnUiThread();
        this.mDevSupportManager.showDevOptionsDialog();
    }

    @ThreadConfined("UI")
    public void attachRootView(ReactRootView reactRootView) {
        UiThreadUtil.assertOnUiThread();
        this.mAttachedRootViews.add(reactRootView);
        reactRootView.removeAllViews();
        reactRootView.setId(-1);
        ReactContext currentReactContext = getCurrentReactContext();
        if (this.mCreateReactContextThread == null && currentReactContext != null) {
            attachRootViewToInstance(reactRootView, currentReactContext.getCatalystInstance());
        }
    }

    @ThreadConfined("UI")
    public void detachRootView(ReactRootView reactRootView) {
        UiThreadUtil.assertOnUiThread();
        if (this.mAttachedRootViews.remove(reactRootView)) {
            ReactContext currentReactContext = getCurrentReactContext();
            if (currentReactContext != null && currentReactContext.hasActiveCatalystInstance()) {
                detachViewFromInstance(reactRootView, currentReactContext.getCatalystInstance());
            }
        }
    }

    public List<ViewManager> createAllViewManagers(ReactApplicationContext reactApplicationContext) {
        ArrayList arrayList;
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_START);
        Systrace.beginSection(0, "createAllViewManagers");
        try {
            synchronized (this.mPackages) {
                arrayList = new ArrayList();
                for (ReactPackage createViewManagers : this.mPackages) {
                    arrayList.addAll(createViewManagers.createViewManagers(reactApplicationContext));
                }
            }
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_END);
            return arrayList;
        } catch (Throwable th) {
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_VIEW_MANAGERS_END);
            throw th;
        }
    }

    @Nullable
    public ViewManager createViewManager(String str) {
        ReactApplicationContext reactApplicationContext = (ReactApplicationContext) Assertions.assertNotNull((ReactApplicationContext) getCurrentReactContext());
        synchronized (this.mPackages) {
            for (ReactPackage reactPackage : this.mPackages) {
                if (reactPackage instanceof ViewManagerOnDemandReactPackage) {
                    ViewManager createViewManager = ((ViewManagerOnDemandReactPackage) reactPackage).createViewManager(reactApplicationContext, str);
                    if (createViewManager != null) {
                        return createViewManager;
                    }
                }
            }
            return null;
        }
    }

    public List<String> getViewManagerNames() {
        ArrayList arrayList;
        ReactApplicationContext reactApplicationContext = (ReactApplicationContext) Assertions.assertNotNull((ReactApplicationContext) getCurrentReactContext());
        synchronized (this.mPackages) {
            HashSet hashSet = new HashSet();
            for (ReactPackage reactPackage : this.mPackages) {
                if (reactPackage instanceof ViewManagerOnDemandReactPackage) {
                    List viewManagerNames = ((ViewManagerOnDemandReactPackage) reactPackage).getViewManagerNames(reactApplicationContext);
                    if (viewManagerNames != null) {
                        hashSet.addAll(viewManagerNames);
                    }
                }
            }
            arrayList = new ArrayList(hashSet);
        }
        return arrayList;
    }

    public void addReactInstanceEventListener(ReactInstanceEventListener reactInstanceEventListener) {
        this.mReactInstanceEventListeners.add(reactInstanceEventListener);
    }

    public void removeReactInstanceEventListener(ReactInstanceEventListener reactInstanceEventListener) {
        this.mReactInstanceEventListeners.remove(reactInstanceEventListener);
    }

    @VisibleForTesting
    @Nullable
    public ReactContext getCurrentReactContext() {
        ReactContext reactContext;
        synchronized (this.mReactContextLock) {
            reactContext = this.mCurrentReactContext;
        }
        return reactContext;
    }

    public LifecycleState getLifecycleState() {
        return this.mLifecycleState;
    }

    public int getMinNumShakes() {
        return this.mMinNumShakes;
    }

    /* access modifiers changed from: private */
    @ThreadConfined("UI")
    public void onReloadWithJSDebugger(Factory factory) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.onReloadWithJSDebugger()");
        recreateReactContextInBackground(new ProxyJavaScriptExecutor.Factory(factory), JSBundleLoader.createRemoteDebuggerBundleLoader(this.mDevSupportManager.getJSBundleURLForRemoteDebugging(), this.mDevSupportManager.getSourceUrl()));
    }

    /* access modifiers changed from: private */
    @ThreadConfined("UI")
    public void onJSBundleLoadedFromServer() {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.onJSBundleLoadedFromServer()");
        recreateReactContextInBackground(this.mJavaScriptExecutorFactory, JSBundleLoader.createCachedBundleFromNetworkLoader(this.mDevSupportManager.getSourceUrl(), this.mDevSupportManager.getDownloadedJSBundleFile()));
    }

    @ThreadConfined("UI")
    private void recreateReactContextInBackground(JavaScriptExecutorFactory javaScriptExecutorFactory, JSBundleLoader jSBundleLoader) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.recreateReactContextInBackground()");
        UiThreadUtil.assertOnUiThread();
        ReactContextInitParams reactContextInitParams = new ReactContextInitParams(javaScriptExecutorFactory, jSBundleLoader);
        if (this.mCreateReactContextThread == null) {
            runCreateReactContextOnNewThread(reactContextInitParams);
        } else {
            this.mPendingReactContextInitParams = reactContextInitParams;
        }
    }

    /* access modifiers changed from: private */
    @ThreadConfined("UI")
    public void runCreateReactContextOnNewThread(final ReactContextInitParams reactContextInitParams) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.runCreateReactContextOnNewThread()");
        UiThreadUtil.assertOnUiThread();
        synchronized (this.mReactContextLock) {
            if (this.mCurrentReactContext != null) {
                tearDownReactContext(this.mCurrentReactContext);
                this.mCurrentReactContext = null;
            }
        }
        this.mCreateReactContextThread = new Thread(new Runnable() {
            /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
            /* JADX WARNING: Missing exception handler attribute for start block: B:2:0x000c */
            /* JADX WARNING: Removed duplicated region for block: B:2:0x000c A[LOOP:0: B:2:0x000c->B:18:0x000c, LOOP_START, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r3 = this;
                    com.facebook.react.bridge.ReactMarkerConstants r0 = com.facebook.react.bridge.ReactMarkerConstants.REACT_CONTEXT_THREAD_END
                    com.facebook.react.bridge.ReactMarker.logMarker(r0)
                    com.facebook.react.ReactInstanceManager r0 = com.facebook.react.ReactInstanceManager.this
                    java.lang.Boolean r0 = r0.mHasStartedDestroying
                    monitor-enter(r0)
                L_0x000c:
                    com.facebook.react.ReactInstanceManager r1 = com.facebook.react.ReactInstanceManager.this     // Catch:{ all -> 0x006a }
                    java.lang.Boolean r1 = r1.mHasStartedDestroying     // Catch:{ all -> 0x006a }
                    boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x006a }
                    if (r1 == 0) goto L_0x0022
                    com.facebook.react.ReactInstanceManager r1 = com.facebook.react.ReactInstanceManager.this     // Catch:{ InterruptedException -> 0x000c }
                    java.lang.Boolean r1 = r1.mHasStartedDestroying     // Catch:{ InterruptedException -> 0x000c }
                    r1.wait()     // Catch:{ InterruptedException -> 0x000c }
                    goto L_0x000c
                L_0x0022:
                    monitor-exit(r0)     // Catch:{ all -> 0x006a }
                    com.facebook.react.ReactInstanceManager r0 = com.facebook.react.ReactInstanceManager.this
                    r1 = 1
                    r0.mHasStartedCreatingInitialContext = r1
                    r0 = -4
                    android.os.Process.setThreadPriority(r0)     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager r0 = com.facebook.react.ReactInstanceManager.this     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager$ReactContextInitParams r1 = r3     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.JavaScriptExecutorFactory r1 = r1.getJsExecutorFactory()     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.JavaScriptExecutor r1 = r1.create()     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager$ReactContextInitParams r2 = r3     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.JSBundleLoader r2 = r2.getJsBundleLoader()     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.ReactApplicationContext r0 = r0.createReactContext(r1, r2)     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager r1 = com.facebook.react.ReactInstanceManager.this     // Catch:{ Exception -> 0x005f }
                    r2 = 0
                    r1.mCreateReactContextThread = r2     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.ReactMarkerConstants r1 = com.facebook.react.bridge.ReactMarkerConstants.PRE_SETUP_REACT_CONTEXT_START     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.ReactMarker.logMarker(r1)     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager$4$1 r1 = new com.facebook.react.ReactInstanceManager$4$1     // Catch:{ Exception -> 0x005f }
                    r1.<init>()     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.ReactInstanceManager$4$2 r2 = new com.facebook.react.ReactInstanceManager$4$2     // Catch:{ Exception -> 0x005f }
                    r2.<init>(r0)     // Catch:{ Exception -> 0x005f }
                    r0.runOnNativeModulesQueueThread(r2)     // Catch:{ Exception -> 0x005f }
                    com.facebook.react.bridge.UiThreadUtil.runOnUiThread(r1)     // Catch:{ Exception -> 0x005f }
                    goto L_0x0069
                L_0x005f:
                    r0 = move-exception
                    com.facebook.react.ReactInstanceManager r1 = com.facebook.react.ReactInstanceManager.this
                    com.facebook.react.devsupport.interfaces.DevSupportManager r1 = r1.mDevSupportManager
                    r1.handleException(r0)
                L_0x0069:
                    return
                L_0x006a:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x006a }
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.ReactInstanceManager.C07504.run():void");
            }
        });
        ReactMarker.logMarker(ReactMarkerConstants.REACT_CONTEXT_THREAD_START);
        this.mCreateReactContextThread.start();
    }

    /* access modifiers changed from: private */
    public void setupReactContext(final ReactApplicationContext reactApplicationContext) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.setupReactContext()");
        ReactMarker.logMarker(ReactMarkerConstants.PRE_SETUP_REACT_CONTEXT_END);
        ReactMarker.logMarker(ReactMarkerConstants.SETUP_REACT_CONTEXT_START);
        Systrace.beginSection(0, "setupReactContext");
        synchronized (this.mReactContextLock) {
            this.mCurrentReactContext = (ReactContext) Assertions.assertNotNull(reactApplicationContext);
        }
        CatalystInstance catalystInstance = (CatalystInstance) Assertions.assertNotNull(reactApplicationContext.getCatalystInstance());
        catalystInstance.initialize();
        this.mDevSupportManager.onNewReactContextCreated(reactApplicationContext);
        this.mMemoryPressureRouter.addMemoryPressureListener(catalystInstance);
        moveReactContextToCurrentLifecycleState();
        ReactMarker.logMarker(ReactMarkerConstants.ATTACH_MEASURED_ROOT_VIEWS_START);
        synchronized (this.mAttachedRootViews) {
            for (ReactRootView attachRootViewToInstance : this.mAttachedRootViews) {
                attachRootViewToInstance(attachRootViewToInstance, catalystInstance);
            }
        }
        ReactMarker.logMarker(ReactMarkerConstants.ATTACH_MEASURED_ROOT_VIEWS_END);
        final ReactInstanceEventListener[] reactInstanceEventListenerArr = (ReactInstanceEventListener[]) this.mReactInstanceEventListeners.toArray(new ReactInstanceEventListener[this.mReactInstanceEventListeners.size()]);
        UiThreadUtil.runOnUiThread(new Runnable() {
            public void run() {
                for (ReactInstanceEventListener onReactContextInitialized : reactInstanceEventListenerArr) {
                    onReactContextInitialized.onReactContextInitialized(reactApplicationContext);
                }
            }
        });
        Systrace.endSection(0);
        ReactMarker.logMarker(ReactMarkerConstants.SETUP_REACT_CONTEXT_END);
        reactApplicationContext.runOnJSQueueThread(new Runnable() {
            public void run() {
                Process.setThreadPriority(0);
            }
        });
        reactApplicationContext.runOnNativeModulesQueueThread(new Runnable() {
            public void run() {
                Process.setThreadPriority(0);
            }
        });
        if (this.mUseSeparateUIBackgroundThread) {
            reactApplicationContext.runOnUiBackgroundQueueThread(new Runnable() {
                public void run() {
                    Process.setThreadPriority(0);
                }
            });
        }
    }

    private void attachRootViewToInstance(final ReactRootView reactRootView, CatalystInstance catalystInstance) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.attachRootViewToInstance()");
        Systrace.beginSection(0, "attachRootViewToInstance");
        final int addRootView = ((UIManagerModule) catalystInstance.getNativeModule(UIManagerModule.class)).addRootView(reactRootView);
        reactRootView.setRootViewTag(addRootView);
        reactRootView.runApplication();
        Systrace.beginAsyncSection(0, "pre_rootView.onAttachedToReactInstance", addRootView);
        UiThreadUtil.runOnUiThread(new Runnable() {
            public void run() {
                Systrace.endAsyncSection(0, "pre_rootView.onAttachedToReactInstance", addRootView);
                reactRootView.onAttachedToReactInstance();
            }
        });
        Systrace.endSection(0);
    }

    private void detachViewFromInstance(ReactRootView reactRootView, CatalystInstance catalystInstance) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.detachViewFromInstance()");
        UiThreadUtil.assertOnUiThread();
        ((AppRegistry) catalystInstance.getJSModule(AppRegistry.class)).unmountApplicationComponentAtRootTag(reactRootView.getId());
    }

    private void tearDownReactContext(ReactContext reactContext) {
        Log.d(ReactConstants.TAG, "ReactInstanceManager.tearDownReactContext()");
        UiThreadUtil.assertOnUiThread();
        if (this.mLifecycleState == LifecycleState.RESUMED) {
            reactContext.onHostPause();
        }
        synchronized (this.mAttachedRootViews) {
            for (ReactRootView reactRootView : this.mAttachedRootViews) {
                reactRootView.removeAllViews();
                reactRootView.setId(-1);
            }
        }
        reactContext.destroy();
        this.mDevSupportManager.onReactInstanceDestroyed(reactContext);
        this.mMemoryPressureRouter.removeMemoryPressureListener(reactContext.getCatalystInstance());
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public ReactApplicationContext createReactContext(JavaScriptExecutor javaScriptExecutor, JSBundleLoader jSBundleLoader) {
        ReactQueueConfigurationSpec reactQueueConfigurationSpec;
        Log.d(ReactConstants.TAG, "ReactInstanceManager.createReactContext()");
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_REACT_CONTEXT_START);
        ReactApplicationContext reactApplicationContext = new ReactApplicationContext(this.mApplicationContext);
        if (this.mUseDeveloperSupport) {
            reactApplicationContext.setNativeModuleCallExceptionHandler(this.mDevSupportManager);
        }
        NativeModuleRegistry processPackages = processPackages(reactApplicationContext, this.mPackages, false);
        NativeModuleCallExceptionHandler nativeModuleCallExceptionHandler = this.mNativeModuleCallExceptionHandler != null ? this.mNativeModuleCallExceptionHandler : this.mDevSupportManager;
        Builder builder = new Builder();
        if (this.mUseSeparateUIBackgroundThread) {
            reactQueueConfigurationSpec = ReactQueueConfigurationSpec.createWithSeparateUIBackgroundThread();
        } else {
            reactQueueConfigurationSpec = ReactQueueConfigurationSpec.createDefault();
        }
        Builder nativeModuleCallExceptionHandler2 = builder.setReactQueueConfigurationSpec(reactQueueConfigurationSpec).setJSExecutor(javaScriptExecutor).setRegistry(processPackages).setJSBundleLoader(jSBundleLoader).setNativeModuleCallExceptionHandler(nativeModuleCallExceptionHandler);
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_START);
        Systrace.beginSection(0, "createCatalystInstance");
        try {
            CatalystInstanceImpl build = nativeModuleCallExceptionHandler2.build();
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_END);
            if (this.mBridgeIdleDebugListener != null) {
                build.addBridgeIdleDebugListener(this.mBridgeIdleDebugListener);
            }
            if (Systrace.isTracing(0)) {
                build.setGlobalVariable("__RCTProfileIsProfiling", "true");
            }
            ReactMarker.logMarker(ReactMarkerConstants.PRE_RUN_JS_BUNDLE_START);
            build.runJSBundle();
            if (!this.mInitFunctions.isEmpty()) {
                for (PendingJSCall callFunction : this.mInitFunctions) {
                    build.callFunction(callFunction);
                }
            }
            reactApplicationContext.initializeWithInstance(build);
            return reactApplicationContext;
        } catch (Throwable th) {
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_CATALYST_INSTANCE_END);
            throw th;
        }
    }

    private NativeModuleRegistry processPackages(ReactApplicationContext reactApplicationContext, List<ReactPackage> list, boolean z) {
        NativeModuleRegistryBuilder nativeModuleRegistryBuilder = new NativeModuleRegistryBuilder(reactApplicationContext, this, this.mLazyNativeModulesEnabled);
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_PACKAGES_START);
        synchronized (this.mPackages) {
            for (ReactPackage reactPackage : list) {
                if (!z || !this.mPackages.contains(reactPackage)) {
                    Systrace.beginSection(0, "createAndProcessCustomReactPackage");
                    if (z) {
                        try {
                            this.mPackages.add(reactPackage);
                        } catch (Throwable th) {
                            Systrace.endSection(0);
                            throw th;
                        }
                    }
                    processPackage(reactPackage, nativeModuleRegistryBuilder);
                    Systrace.endSection(0);
                }
            }
        }
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_PACKAGES_END);
        ReactMarker.logMarker(ReactMarkerConstants.BUILD_NATIVE_MODULE_REGISTRY_START);
        Systrace.beginSection(0, "buildNativeModuleRegistry");
        try {
            return nativeModuleRegistryBuilder.build();
        } finally {
            Systrace.endSection(0);
            ReactMarker.logMarker(ReactMarkerConstants.BUILD_NATIVE_MODULE_REGISTRY_END);
        }
    }

    private void processPackage(ReactPackage reactPackage, NativeModuleRegistryBuilder nativeModuleRegistryBuilder) {
        SystraceMessage.beginSection(0, "processPackage").arg("className", (Object) reactPackage.getClass().getSimpleName()).flush();
        boolean z = reactPackage instanceof ReactPackageLogger;
        if (z) {
            ((ReactPackageLogger) reactPackage).startProcessPackage();
        }
        nativeModuleRegistryBuilder.processPackage(reactPackage);
        if (z) {
            ((ReactPackageLogger) reactPackage).endProcessPackage();
        }
        SystraceMessage.endSection(0).flush();
    }
}
