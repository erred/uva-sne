package com.facebook.react;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.support.p000v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionListener;
import javax.annotation.Nullable;

public class ReactActivityDelegate {
    private static final String REDBOX_PERMISSION_GRANTED_MESSAGE = "Overlay permissions have been granted.";
    private static final String REDBOX_PERMISSION_MESSAGE = "Overlay permissions needs to be granted in order for react native apps to run in dev mode";
    private final int REQUEST_OVERLAY_PERMISSION_CODE = 1111;
    @Nullable
    private final Activity mActivity;
    @Nullable
    private DoubleTapReloadRecognizer mDoubleTapReloadRecognizer;
    @Nullable
    private final FragmentActivity mFragmentActivity;
    @Nullable
    private final String mMainComponentName;
    /* access modifiers changed from: private */
    @Nullable
    public PermissionListener mPermissionListener;
    @Nullable
    private Callback mPermissionsCallback;
    @Nullable
    private ReactRootView mReactRootView;

    /* access modifiers changed from: protected */
    @Nullable
    public Bundle getLaunchOptions() {
        return null;
    }

    public ReactActivityDelegate(Activity activity, @Nullable String str) {
        this.mActivity = activity;
        this.mMainComponentName = str;
        this.mFragmentActivity = null;
    }

    public ReactActivityDelegate(FragmentActivity fragmentActivity, @Nullable String str) {
        this.mFragmentActivity = fragmentActivity;
        this.mMainComponentName = str;
        this.mActivity = null;
    }

    /* access modifiers changed from: protected */
    public ReactRootView createRootView() {
        return new ReactRootView(getContext());
    }

    /* access modifiers changed from: protected */
    public ReactNativeHost getReactNativeHost() {
        return ((ReactApplication) getPlainActivity().getApplication()).getReactNativeHost();
    }

    public ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        boolean z = true;
        if (!getReactNativeHost().getUseDeveloperSupport() || VERSION.SDK_INT < 23 || Settings.canDrawOverlays(getContext())) {
            z = false;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("package:");
            sb.append(getContext().getPackageName());
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(sb.toString()));
            FLog.m105w(ReactConstants.TAG, REDBOX_PERMISSION_MESSAGE);
            Toast.makeText(getContext(), REDBOX_PERMISSION_MESSAGE, 1).show();
            ((Activity) getContext()).startActivityForResult(intent, 1111);
        }
        if (this.mMainComponentName != null && !z) {
            loadApp(this.mMainComponentName);
        }
        this.mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    }

    /* access modifiers changed from: protected */
    public void loadApp(String str) {
        if (this.mReactRootView != null) {
            throw new IllegalStateException("Cannot loadApp while app is already running.");
        }
        this.mReactRootView = createRootView();
        this.mReactRootView.startReactApplication(getReactNativeHost().getReactInstanceManager(), str, getLaunchOptions());
        getPlainActivity().setContentView(this.mReactRootView);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostPause(getPlainActivity());
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostResume(getPlainActivity(), (DefaultHardwareBackBtnHandler) getPlainActivity());
        }
        if (this.mPermissionsCallback != null) {
            this.mPermissionsCallback.invoke(new Object[0]);
            this.mPermissionsCallback = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.mReactRootView != null) {
            this.mReactRootView.unmountReactApplication();
            this.mReactRootView = null;
        }
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostDestroy(getPlainActivity());
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onActivityResult(getPlainActivity(), i, i2, intent);
        } else if (i == 1111 && VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(getContext())) {
            if (this.mMainComponentName != null) {
                loadApp(this.mMainComponentName);
            }
            Toast.makeText(getContext(), REDBOX_PERMISSION_GRANTED_MESSAGE, 1).show();
        }
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (getReactNativeHost().hasInstance() && getReactNativeHost().getUseDeveloperSupport()) {
            if (i == 82) {
                getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
                return true;
            } else if (((DoubleTapReloadRecognizer) Assertions.assertNotNull(this.mDoubleTapReloadRecognizer)).didDoubleTapR(i, getPlainActivity().getCurrentFocus())) {
                getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
                return true;
            }
        }
        return false;
    }

    public boolean onBackPressed() {
        if (!getReactNativeHost().hasInstance()) {
            return false;
        }
        getReactNativeHost().getReactInstanceManager().onBackPressed();
        return true;
    }

    public boolean onNewIntent(Intent intent) {
        if (!getReactNativeHost().hasInstance()) {
            return false;
        }
        getReactNativeHost().getReactInstanceManager().onNewIntent(intent);
        return true;
    }

    @TargetApi(23)
    public void requestPermissions(String[] strArr, int i, PermissionListener permissionListener) {
        this.mPermissionListener = permissionListener;
        getPlainActivity().requestPermissions(strArr, i);
    }

    public void onRequestPermissionsResult(final int i, final String[] strArr, final int[] iArr) {
        this.mPermissionsCallback = new Callback() {
            public void invoke(Object... objArr) {
                if (ReactActivityDelegate.this.mPermissionListener != null && ReactActivityDelegate.this.mPermissionListener.onRequestPermissionsResult(i, strArr, iArr)) {
                    ReactActivityDelegate.this.mPermissionListener = null;
                }
            }
        };
    }

    private Context getContext() {
        if (this.mActivity != null) {
            return this.mActivity;
        }
        return (Context) Assertions.assertNotNull(this.mFragmentActivity);
    }

    private Activity getPlainActivity() {
        return (Activity) getContext();
    }
}
