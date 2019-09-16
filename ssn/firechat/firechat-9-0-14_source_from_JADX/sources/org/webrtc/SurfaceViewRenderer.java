package org.webrtc;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.util.concurrent.CountDownLatch;
import org.webrtc.EglRenderer.FrameListener;
import org.webrtc.RendererCommon.GlDrawer;
import org.webrtc.RendererCommon.RendererEvents;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.RendererCommon.VideoLayoutMeasure;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRenderer.I420Frame;

public class SurfaceViewRenderer extends SurfaceView implements Callback, Callbacks {
    private static final String TAG = "SurfaceViewRenderer";
    private final EglRenderer eglRenderer = new EglRenderer(this.resourceName);
    private boolean enableFixedSize;
    private int frameRotation;
    private boolean isFirstFrameRendered;
    private final Object layoutLock = new Object();
    private RendererEvents rendererEvents;
    private final String resourceName = getResourceName();
    private int rotatedFrameHeight;
    private int rotatedFrameWidth;
    private int surfaceHeight;
    private int surfaceWidth;
    private final VideoLayoutMeasure videoLayoutMeasure = new VideoLayoutMeasure();

    public SurfaceViewRenderer(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public SurfaceViewRenderer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
    }

    public void init(EglBase.Context context, RendererEvents rendererEvents2) {
        init(context, rendererEvents2, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context context, RendererEvents rendererEvents2, int[] iArr, GlDrawer glDrawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        synchronized (this.layoutLock) {
            this.rotatedFrameWidth = 0;
            this.rotatedFrameHeight = 0;
            this.frameRotation = 0;
        }
        this.eglRenderer.init(context, iArr, glDrawer);
    }

    public void release() {
        this.eglRenderer.release();
    }

    public void addFrameListener(FrameListener frameListener, float f, GlDrawer glDrawer) {
        this.eglRenderer.addFrameListener(frameListener, f, glDrawer);
    }

    public void addFrameListener(FrameListener frameListener, float f) {
        this.eglRenderer.addFrameListener(frameListener, f);
    }

    public void removeFrameListener(FrameListener frameListener) {
        this.eglRenderer.removeFrameListener(frameListener);
    }

    public void setEnableHardwareScaler(boolean z) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = z;
        updateSurfaceSize();
    }

    public void setMirror(boolean z) {
        this.eglRenderer.setMirror(z);
    }

    public void setScalingType(ScalingType scalingType) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType);
    }

    public void setScalingType(ScalingType scalingType, ScalingType scalingType2) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType, scalingType2);
    }

    public void setFpsReduction(float f) {
        this.eglRenderer.setFpsReduction(f);
    }

    public void disableFpsReduction() {
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        this.eglRenderer.pauseVideo();
    }

    public void renderFrame(I420Frame i420Frame) {
        updateFrameDimensionsAndReportEvents(i420Frame);
        this.eglRenderer.renderFrame(i420Frame);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        Point measure;
        ThreadUtils.checkIsOnMainThread();
        synchronized (this.layoutLock) {
            measure = this.videoLayoutMeasure.measure(i, i2, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(measure.x, measure.y);
        StringBuilder sb = new StringBuilder();
        sb.append("onMeasure(). New size: ");
        sb.append(measure.x);
        sb.append("x");
        sb.append(measure.y);
        logD(sb.toString());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.setLayoutAspectRatio(((float) (i3 - i)) / ((float) (i4 - i2)));
        updateSurfaceSize();
    }

    /* access modifiers changed from: private */
    public void updateSurfaceSize() {
        int i;
        int i2;
        ThreadUtils.checkIsOnMainThread();
        synchronized (this.layoutLock) {
            if (!this.enableFixedSize || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || getWidth() == 0 || getHeight() == 0) {
                this.surfaceHeight = 0;
                this.surfaceWidth = 0;
                getHolder().setSizeFromLayout();
            } else {
                float width = ((float) getWidth()) / ((float) getHeight());
                if (((float) this.rotatedFrameWidth) / ((float) this.rotatedFrameHeight) > width) {
                    i2 = (int) (((float) this.rotatedFrameHeight) * width);
                    i = this.rotatedFrameHeight;
                } else {
                    int i3 = this.rotatedFrameWidth;
                    i = (int) (((float) this.rotatedFrameWidth) / width);
                    i2 = i3;
                }
                int min = Math.min(getWidth(), i2);
                int min2 = Math.min(getHeight(), i);
                StringBuilder sb = new StringBuilder();
                sb.append("updateSurfaceSize. Layout size: ");
                sb.append(getWidth());
                sb.append("x");
                sb.append(getHeight());
                sb.append(", frame size: ");
                sb.append(this.rotatedFrameWidth);
                sb.append("x");
                sb.append(this.rotatedFrameHeight);
                sb.append(", requested surface size: ");
                sb.append(min);
                sb.append("x");
                sb.append(min2);
                sb.append(", old surface size: ");
                sb.append(this.surfaceWidth);
                sb.append("x");
                sb.append(this.surfaceHeight);
                logD(sb.toString());
                if (!(min == this.surfaceWidth && min2 == this.surfaceHeight)) {
                    this.surfaceWidth = min;
                    this.surfaceHeight = min2;
                    getHolder().setFixedSize(min, min2);
                }
            }
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.createEglSurface(surfaceHolder.getSurface());
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        ThreadUtils.checkIsOnMainThread();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.eglRenderer.releaseEglSurface(new Runnable() {
            public void run() {
                countDownLatch.countDown();
            }
        });
        ThreadUtils.awaitUninterruptibly(countDownLatch);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        ThreadUtils.checkIsOnMainThread();
        StringBuilder sb = new StringBuilder();
        sb.append("surfaceChanged: format: ");
        sb.append(i);
        sb.append(" size: ");
        sb.append(i2);
        sb.append("x");
        sb.append(i3);
        logD(sb.toString());
    }

    private String getResourceName() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getResourceEntryName(getId()));
            sb.append(": ");
            return sb.toString();
        } catch (NotFoundException unused) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
    }

    private void updateFrameDimensionsAndReportEvents(I420Frame i420Frame) {
        synchronized (this.layoutLock) {
            if (!this.isFirstFrameRendered) {
                this.isFirstFrameRendered = true;
                logD("Reporting first rendered frame.");
                if (this.rendererEvents != null) {
                    this.rendererEvents.onFirstFrameRendered();
                }
            }
            if (!(this.rotatedFrameWidth == i420Frame.rotatedWidth() && this.rotatedFrameHeight == i420Frame.rotatedHeight() && this.frameRotation == i420Frame.rotationDegree)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Reporting frame resolution changed to ");
                sb.append(i420Frame.width);
                sb.append("x");
                sb.append(i420Frame.height);
                sb.append(" with rotation ");
                sb.append(i420Frame.rotationDegree);
                logD(sb.toString());
                if (this.rendererEvents != null) {
                    this.rendererEvents.onFrameResolutionChanged(i420Frame.width, i420Frame.height, i420Frame.rotationDegree);
                }
                this.rotatedFrameWidth = i420Frame.rotatedWidth();
                this.rotatedFrameHeight = i420Frame.rotatedHeight();
                this.frameRotation = i420Frame.rotationDegree;
                post(new Runnable() {
                    public void run() {
                        SurfaceViewRenderer.this.updateSurfaceSize();
                        SurfaceViewRenderer.this.requestLayout();
                    }
                });
            }
        }
    }

    private void logD(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(this.resourceName);
        sb.append(str);
        Logging.m314d(str2, sb.toString());
    }
}
