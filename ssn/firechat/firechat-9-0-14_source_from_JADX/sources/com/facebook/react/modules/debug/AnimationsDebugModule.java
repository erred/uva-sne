package com.facebook.react.modules.debug;

import android.widget.Toast;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.ChoreographerCompat;
import com.facebook.react.modules.debug.FpsDebugFrameCallback.FpsInfo;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

@ReactModule(name = "AnimationsDebugModule")
public class AnimationsDebugModule extends ReactContextBaseJavaModule {
    protected static final String NAME = "AnimationsDebugModule";
    @Nullable
    private final DeveloperSettings mCatalystSettings;
    @Nullable
    private FpsDebugFrameCallback mFrameCallback;

    public String getName() {
        return NAME;
    }

    public AnimationsDebugModule(ReactApplicationContext reactApplicationContext, DeveloperSettings developerSettings) {
        super(reactApplicationContext);
        this.mCatalystSettings = developerSettings;
    }

    @ReactMethod
    public void startRecordingFps() {
        if (this.mCatalystSettings != null && this.mCatalystSettings.isAnimationFpsDebugEnabled()) {
            if (this.mFrameCallback != null) {
                throw new JSApplicationCausedNativeException("Already recording FPS!");
            }
            this.mFrameCallback = new FpsDebugFrameCallback(ChoreographerCompat.getInstance(), getReactApplicationContext());
            this.mFrameCallback.startAndRecordFpsAtEachFrame();
        }
    }

    @ReactMethod
    public void stopRecordingFps(double d) {
        if (this.mFrameCallback != null) {
            this.mFrameCallback.stop();
            FpsInfo fpsInfo = this.mFrameCallback.getFpsInfo((long) d);
            if (fpsInfo == null) {
                Toast.makeText(getReactApplicationContext(), "Unable to get FPS info", 1);
            } else {
                String format = String.format(Locale.US, "FPS: %.2f, %d frames (%d expected)", new Object[]{Double.valueOf(fpsInfo.fps), Integer.valueOf(fpsInfo.totalFrames), Integer.valueOf(fpsInfo.totalExpectedFrames)});
                String format2 = String.format(Locale.US, "JS FPS: %.2f, %d frames (%d expected)", new Object[]{Double.valueOf(fpsInfo.jsFps), Integer.valueOf(fpsInfo.totalJsFrames), Integer.valueOf(fpsInfo.totalExpectedFrames)});
                StringBuilder sb = new StringBuilder();
                sb.append(format);
                sb.append(StringUtils.f158LF);
                sb.append(format2);
                sb.append(StringUtils.f158LF);
                sb.append("Total Time MS: ");
                sb.append(String.format(Locale.US, "%d", new Object[]{Integer.valueOf(fpsInfo.totalTimeMs)}));
                String sb2 = sb.toString();
                FLog.m53d(ReactConstants.TAG, sb2);
                Toast.makeText(getReactApplicationContext(), sb2, 1).show();
            }
            this.mFrameCallback = null;
        }
    }

    public void onCatalystInstanceDestroy() {
        if (this.mFrameCallback != null) {
            this.mFrameCallback.stop();
            this.mFrameCallback = null;
        }
    }
}
