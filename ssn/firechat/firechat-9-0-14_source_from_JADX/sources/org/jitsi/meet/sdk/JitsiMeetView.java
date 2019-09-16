package org.jitsi.meet.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import com.RNFetchBlob.RNFetchBlobPackage;
import com.corbt.keepawake.KCKeepAwakePackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.i18n.reactnativei18n.ReactNativeI18n;
import com.oblador.vectoricons.VectorIconsPackage;
import com.ocetnik.timer.BackgroundTimerPackage;
import com.oney.WebRTCModule.WebRTCModulePackage;
import com.rnimmersive.RNImmersivePackage;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class JitsiMeetView extends FrameLayout {
    private static final int BACKGROUND_COLOR = -15658735;
    private static ReactInstanceManager reactInstanceManager;
    private static final Set<JitsiMeetView> views = Collections.newSetFromMap(new WeakHashMap());
    private URL defaultURL;
    private final String externalAPIScope;
    private JitsiMeetViewListener listener;
    private ReactRootView reactRootView;
    private boolean welcomePageEnabled;

    /* access modifiers changed from: private */
    public static List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        return Arrays.asList(new NativeModule[]{new AndroidSettingsModule(reactApplicationContext), new AppInfoModule(reactApplicationContext), new AudioModeModule(reactApplicationContext), new ExternalAPIModule(reactApplicationContext), new ProximityModule(reactApplicationContext), new WiFiStatsModule(reactApplicationContext)});
    }

    public static JitsiMeetView findViewByExternalAPIScope(String str) {
        synchronized (views) {
            for (JitsiMeetView jitsiMeetView : views) {
                if (jitsiMeetView.externalAPIScope.equals(str)) {
                    return jitsiMeetView;
                }
            }
            return null;
        }
    }

    private static void initReactInstanceManager(Application application) {
        reactInstanceManager = ReactInstanceManager.builder().setApplication(application).setBundleAssetName("index.android.bundle").setJSMainModulePath("index.android").addPackage(new KCKeepAwakePackage()).addPackage(new MainReactPackage()).addPackage(new ReactNativeI18n()).addPackage(new VectorIconsPackage()).addPackage(new BackgroundTimerPackage()).addPackage(new WebRTCModulePackage()).addPackage(new RNFetchBlobPackage()).addPackage(new RNImmersivePackage()).addPackage(new ReactPackageAdapter() {
            public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
                return JitsiMeetView.createNativeModules(reactApplicationContext);
            }
        }).setUseDeveloperSupport(false).setInitialLifecycleState(LifecycleState.RESUMED).build();
    }

    private static boolean loadURLStringInViews(String str) {
        synchronized (views) {
            if (views.isEmpty()) {
                return false;
            }
            for (JitsiMeetView loadURLString : views) {
                loadURLString.loadURLString(str);
            }
            return true;
        }
    }

    public static boolean onBackPressed() {
        if (reactInstanceManager == null) {
            return false;
        }
        reactInstanceManager.onBackPressed();
        return true;
    }

    public static void onHostDestroy(Activity activity) {
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostDestroy(activity);
        }
    }

    public static void onHostPause(Activity activity) {
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostPause(activity);
        }
    }

    public static void onHostResume(Activity activity) {
        onHostResume(activity, new DefaultHardwareBackBtnHandlerImpl(activity));
    }

    public static void onHostResume(Activity activity, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostResume(activity, defaultHardwareBackBtnHandler);
        }
    }

    public static void onNewIntent(Intent intent) {
        if ("android.intent.action.VIEW".equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null && loadURLStringInViews(data.toString())) {
                return;
            }
        }
        if (reactInstanceManager != null) {
            reactInstanceManager.onNewIntent(intent);
        }
    }

    public JitsiMeetView(@NonNull Context context) {
        super(context);
        setBackgroundColor(BACKGROUND_COLOR);
        if (reactInstanceManager == null) {
            initReactInstanceManager(((Activity) context).getApplication());
        }
        this.externalAPIScope = UUID.randomUUID().toString();
        synchronized (views) {
            views.add(this);
        }
    }

    public void dispose() {
        if (this.reactRootView != null) {
            removeView(this.reactRootView);
            this.reactRootView.unmountReactApplication();
            this.reactRootView = null;
        }
    }

    public URL getDefaultURL() {
        return this.defaultURL;
    }

    public JitsiMeetViewListener getListener() {
        return this.listener;
    }

    public boolean getWelcomePageEnabled() {
        return this.welcomePageEnabled;
    }

    public void loadURL(@Nullable URL url) {
        loadURLString(url == null ? null : url.toString());
    }

    public void loadURLObject(@Nullable Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (this.defaultURL != null) {
            bundle2.putString("defaultURL", this.defaultURL.toString());
        }
        bundle2.putString("externalAPIScope", this.externalAPIScope);
        if (bundle != null) {
            bundle2.putBundle(ImagesContract.URL, bundle);
        }
        bundle2.putBoolean("welcomePageEnabled", this.welcomePageEnabled);
        bundle2.putLong(Param.TIMESTAMP, System.currentTimeMillis());
        if (this.reactRootView == null) {
            this.reactRootView = new ReactRootView(getContext());
            this.reactRootView.startReactApplication(reactInstanceManager, "App", bundle2);
            this.reactRootView.setBackgroundColor(BACKGROUND_COLOR);
            addView(this.reactRootView);
            return;
        }
        this.reactRootView.setAppProperties(bundle2);
    }

    public void loadURLString(@Nullable String str) {
        Bundle bundle;
        if (str == null) {
            bundle = null;
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putString(ImagesContract.URL, str);
            bundle = bundle2;
        }
        loadURLObject(bundle);
    }

    public void setDefaultURL(URL url) {
        this.defaultURL = url;
    }

    public void setListener(JitsiMeetViewListener jitsiMeetViewListener) {
        this.listener = jitsiMeetViewListener;
    }

    public void setWelcomePageEnabled(boolean z) {
        this.welcomePageEnabled = z;
    }
}
