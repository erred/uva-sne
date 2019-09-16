package com.facebook.fbui.textlayoutbuilder.glyphwarmer;

import android.annotation.SuppressLint;
import android.graphics.Picture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.text.Layout;
import com.facebook.fbui.textlayoutbuilder.GlyphWarmer;
import com.facebook.fbui.textlayoutbuilder.util.LayoutMeasureUtil;

public class GlyphWarmerImpl implements GlyphWarmer {
    private static WarmHandler sWarmHandler;

    private static class WarmHandler extends Handler {
        private static final int NO_OP = 1;
        private final Picture mPicture = new Picture();

        public WarmHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            Layout layout = (Layout) message.obj;
            try {
                layout.draw(this.mPicture.beginRecording(LayoutMeasureUtil.getWidth(layout), LayoutMeasureUtil.getHeight(layout)));
                this.mPicture.endRecording();
            } catch (Exception unused) {
            }
        }
    }

    public void warmLayout(Layout layout) {
        WarmHandler warmHandler = getWarmHandler();
        warmHandler.sendMessage(warmHandler.obtainMessage(1, layout));
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public Looper getWarmHandlerLooper() {
        return getWarmHandler().getLooper();
    }

    @SuppressLint({"BadMethodUse-android.os.HandlerThread._Constructor", "BadMethodUse-java.lang.Thread.start"})
    private WarmHandler getWarmHandler() {
        if (sWarmHandler == null) {
            HandlerThread handlerThread = new HandlerThread("GlyphWarmer");
            handlerThread.start();
            sWarmHandler = new WarmHandler(handlerThread.getLooper());
        }
        return sWarmHandler;
    }
}
