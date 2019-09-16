package com.facebook.react.devsupport;

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.facebook.react.bridge.ReactContext;
import javax.annotation.Nullable;

class DebugOverlayController {
    @Nullable
    private FrameLayout mFPSDebugViewContainer;
    private final ReactContext mReactContext;
    private final WindowManager mWindowManager;

    public DebugOverlayController(ReactContext reactContext) {
        this.mReactContext = reactContext;
        this.mWindowManager = (WindowManager) reactContext.getSystemService("window");
    }

    public void setFpsDebugViewVisible(boolean z) {
        if (z && this.mFPSDebugViewContainer == null) {
            this.mFPSDebugViewContainer = new FpsView(this.mReactContext);
            LayoutParams layoutParams = new LayoutParams(-1, -1, WindowOverlayCompat.TYPE_SYSTEM_OVERLAY, 24, -3);
            this.mWindowManager.addView(this.mFPSDebugViewContainer, layoutParams);
        } else if (!z && this.mFPSDebugViewContainer != null) {
            this.mFPSDebugViewContainer.removeAllViews();
            this.mWindowManager.removeView(this.mFPSDebugViewContainer);
            this.mFPSDebugViewContainer = null;
        }
    }
}
