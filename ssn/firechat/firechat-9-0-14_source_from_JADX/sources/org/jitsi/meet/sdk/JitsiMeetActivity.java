package org.jitsi.meet.sdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.p003v7.app.AppCompatActivity;
import android.view.View;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import java.net.URL;

public class JitsiMeetActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = ((int) (Math.random() * 32767.0d));
    private DefaultHardwareBackBtnHandler defaultBackButtonImpl;
    private URL defaultURL;
    private JitsiMeetView view;
    private boolean welcomePageEnabled;

    private boolean canRequestOverlayPermission() {
        return false;
    }

    public URL getDefaultURL() {
        return this.view == null ? this.defaultURL : this.view.getDefaultURL();
    }

    public boolean getWelcomePageEnabled() {
        return this.view == null ? this.welcomePageEnabled : this.view.getWelcomePageEnabled();
    }

    private void initializeContentView() {
        JitsiMeetView initializeView = initializeView();
        if (initializeView != null) {
            this.view = initializeView;
            setContentView((View) this.view);
        }
    }

    /* access modifiers changed from: protected */
    public JitsiMeetView initializeView() {
        JitsiMeetView jitsiMeetView = new JitsiMeetView(this);
        jitsiMeetView.setDefaultURL(this.defaultURL);
        jitsiMeetView.setWelcomePageEnabled(this.welcomePageEnabled);
        jitsiMeetView.loadURL(null);
        return jitsiMeetView;
    }

    public void loadURL(@Nullable URL url) {
        this.view.loadURL(url);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == OVERLAY_PERMISSION_REQUEST_CODE && canRequestOverlayPermission() && Settings.canDrawOverlays(this)) {
            initializeContentView();
        }
    }

    public void onBackPressed() {
        if (JitsiMeetView.onBackPressed()) {
            return;
        }
        if (this.defaultBackButtonImpl == null) {
            super.onBackPressed();
        } else {
            this.defaultBackButtonImpl.invokeDefaultOnBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!canRequestOverlayPermission() || Settings.canDrawOverlays(this)) {
            initializeContentView();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("package:");
        sb.append(getPackageName());
        startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(sb.toString())), OVERLAY_PERMISSION_REQUEST_CODE);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.view != null) {
            this.view.dispose();
            this.view = null;
        }
        JitsiMeetView.onHostDestroy(this);
    }

    public void onNewIntent(Intent intent) {
        JitsiMeetView.onNewIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        JitsiMeetView.onHostPause(this);
        this.defaultBackButtonImpl = null;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.defaultBackButtonImpl = new DefaultHardwareBackBtnHandlerImpl(this);
        JitsiMeetView.onHostResume(this, this.defaultBackButtonImpl);
    }

    public void setDefaultURL(URL url) {
        if (this.view == null) {
            this.defaultURL = url;
        } else {
            this.view.setDefaultURL(url);
        }
    }

    public void setWelcomePageEnabled(boolean z) {
        if (this.view == null) {
            this.welcomePageEnabled = z;
        } else {
            this.view.setWelcomePageEnabled(z);
        }
    }
}
