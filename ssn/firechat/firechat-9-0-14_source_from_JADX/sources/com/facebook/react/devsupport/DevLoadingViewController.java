package com.facebook.react.devsupport;

import android.content.Context;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.facebook.common.logging.FLog;
import com.facebook.react.C0742R;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.annotation.Nullable;

public class DevLoadingViewController {
    private static final int COLOR_DARK_GREEN = Color.parseColor("#035900");
    private static boolean sEnabled = true;
    private final Context mContext;
    /* access modifiers changed from: private */
    public TextView mDevLoadingView;
    private boolean mIsVisible = false;
    private final WindowManager mWindowManager;

    public static void setDevLoadingEnabled(boolean z) {
        sEnabled = z;
    }

    public DevLoadingViewController(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mDevLoadingView = (TextView) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0742R.layout.dev_loading_view, null);
    }

    public void showMessage(final String str, final int i, final int i2) {
        if (sEnabled && isWindowPermissionGranted()) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    DevLoadingViewController.this.mDevLoadingView.setBackgroundColor(i2);
                    DevLoadingViewController.this.mDevLoadingView.setText(str);
                    DevLoadingViewController.this.mDevLoadingView.setTextColor(i);
                    DevLoadingViewController.this.setVisible(true);
                }
            });
        }
    }

    public void showForUrl(String str) {
        try {
            URL url = new URL(str);
            Context context = this.mContext;
            int i = C0742R.string.catalyst_loading_from_url;
            StringBuilder sb = new StringBuilder();
            sb.append(url.getHost());
            sb.append(":");
            sb.append(url.getPort());
            showMessage(context.getString(i, new Object[]{sb.toString()}), -1, COLOR_DARK_GREEN);
        } catch (MalformedURLException e) {
            String str2 = ReactConstants.TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Bundle url format is invalid. \n\n");
            sb2.append(e.toString());
            FLog.m65e(str2, sb2.toString());
        }
    }

    public void showForRemoteJSEnabled() {
        showMessage(this.mContext.getString(C0742R.string.catalyst_remotedbg_message), -1, COLOR_DARK_GREEN);
    }

    public void updateProgress(@Nullable final String str, @Nullable final Integer num, @Nullable final Integer num2) {
        if (sEnabled) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str != null ? str : "Loading");
                    if (!(num == null || num2 == null || num2.intValue() <= 0)) {
                        sb.append(String.format(Locale.getDefault(), " %.1f%% (%d/%d)", new Object[]{Float.valueOf((((float) num.intValue()) / ((float) num2.intValue())) * 100.0f), num, num2}));
                    }
                    sb.append("…");
                    DevLoadingViewController.this.mDevLoadingView.setText(sb);
                }
            });
        }
    }

    public void show() {
        if (sEnabled) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    DevLoadingViewController.this.setVisible(true);
                }
            });
        }
    }

    public void hide() {
        if (sEnabled) {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    DevLoadingViewController.this.setVisible(false);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setVisible(boolean z) {
        if (z && !this.mIsVisible) {
            LayoutParams layoutParams = new LayoutParams(-1, -2, WindowOverlayCompat.TYPE_SYSTEM_OVERLAY, 8, -3);
            layoutParams.gravity = 48;
            this.mWindowManager.addView(this.mDevLoadingView, layoutParams);
        } else if (!z && this.mIsVisible) {
            this.mWindowManager.removeView(this.mDevLoadingView);
        }
        this.mIsVisible = z;
    }

    private boolean isWindowPermissionGranted() {
        return VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.mContext) || this.mContext.checkSelfPermission("android.permission.SYSTEM_ALERT_WINDOW") == 0;
    }
}
