package com.oney.WebRTCModule;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.MeasureSpec;
import com.facebook.imagepipeline.common.RotationOptions;
import org.webrtc.EglBase;
import org.webrtc.GlRectDrawer;
import org.webrtc.Logging;
import org.webrtc.RendererCommon;
import org.webrtc.RendererCommon.GlDrawer;
import org.webrtc.RendererCommon.RendererEvents;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.RendererCommon.YuvUploader;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRenderer.I420Frame;

public class SurfaceViewRenderer extends SurfaceView implements Callback, Callbacks {
    private static final String TAG = "SurfaceViewRenderer";
    private Point desiredLayoutSize = new Point();
    /* access modifiers changed from: private */
    public GlDrawer drawer;
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private long firstFrameTimeNs;
    private int frameHeight;
    private final Object frameLock = new Object();
    private int frameRotation;
    private int frameWidth;
    private int framesDropped;
    private int framesReceived;
    private int framesRendered;
    private final Object handlerLock = new Object();
    /* access modifiers changed from: private */
    public boolean isSurfaceCreated;
    /* access modifiers changed from: private */
    public final Object layoutLock = new Object();
    private final Point layoutSize = new Point();
    private final Runnable makeBlackRunnable = new Runnable() {
        public void run() {
            SurfaceViewRenderer.this.makeBlack();
        }
    };
    private boolean mirror;
    private I420Frame pendingFrame;
    private final Runnable renderFrameRunnable = new Runnable() {
        public void run() {
            SurfaceViewRenderer.this.renderFrameOnRenderThread();
        }
    };
    private HandlerThread renderThread;
    private Handler renderThreadHandler;
    private long renderTimeNs;
    private RendererEvents rendererEvents;
    private ScalingType scalingType = ScalingType.SCALE_ASPECT_BALANCED;
    private final Object statisticsLock = new Object();
    private final Point surfaceSize = new Point();
    /* access modifiers changed from: private */
    public int[] yuvTextures = null;
    private final YuvUploader yuvUploader = new YuvUploader();

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

    public void init(final EglBase.Context context, RendererEvents rendererEvents2, final int[] iArr, GlDrawer glDrawer) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(getResourceName());
                sb.append("Already initialized");
                throw new IllegalStateException(sb.toString());
            }
            String str = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getResourceName());
            sb2.append("Initializing.");
            Logging.m314d(str, sb2.toString());
            this.rendererEvents = rendererEvents2;
            this.drawer = glDrawer;
            this.renderThread = new HandlerThread(TAG);
            this.renderThread.start();
            this.renderThreadHandler = new Handler(this.renderThread.getLooper());
            ThreadUtils.invokeAtFrontUninterruptibly(this.renderThreadHandler, (Runnable) new Runnable() {
                public void run() {
                    SurfaceViewRenderer.this.eglBase = EglBase.create(context, iArr);
                }
            });
        }
        tryCreateEglSurface();
    }

    public void tryCreateEglSurface() {
        runOnRenderThread(new Runnable() {
            public void run() {
                synchronized (SurfaceViewRenderer.this.layoutLock) {
                    if (SurfaceViewRenderer.this.eglBase != null && SurfaceViewRenderer.this.isSurfaceCreated && !SurfaceViewRenderer.this.eglBase.hasSurface()) {
                        SurfaceViewRenderer.this.eglBase.createSurface(SurfaceViewRenderer.this.getHolder().getSurface());
                        SurfaceViewRenderer.this.eglBase.makeCurrent();
                        GLES20.glPixelStorei(3317, 1);
                    }
                }
                SurfaceViewRenderer.this.makeBlack();
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r4.renderThread.quit();
        r0 = r4.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0041, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0044, code lost:
        if (r4.pendingFrame == null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        org.webrtc.VideoRenderer.renderFrameDone(r4.pendingFrame);
        r4.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004d, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004e, code lost:
        org.webrtc.ThreadUtils.joinUninterruptibly(r4.renderThread);
        r4.renderThread = null;
        r1 = r4.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0057, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r4.frameWidth = 0;
        r4.frameHeight = 0;
        r4.frameRotation = 0;
        r4.rendererEvents = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0061, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0062, code lost:
        resetStatistics();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0065, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void release() {
        /*
            r4 = this;
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch
            r1 = 1
            r0.<init>(r1)
            java.lang.Object r1 = r4.handlerLock
            monitor-enter(r1)
            android.os.Handler r2 = r4.renderThreadHandler     // Catch:{ all -> 0x006c }
            if (r2 != 0) goto L_0x0029
            java.lang.String r0 = "SurfaceViewRenderer"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x006c }
            r2.<init>()     // Catch:{ all -> 0x006c }
            java.lang.String r3 = r4.getResourceName()     // Catch:{ all -> 0x006c }
            r2.append(r3)     // Catch:{ all -> 0x006c }
            java.lang.String r3 = "Already released"
            r2.append(r3)     // Catch:{ all -> 0x006c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x006c }
            org.webrtc.Logging.m314d(r0, r2)     // Catch:{ all -> 0x006c }
            monitor-exit(r1)     // Catch:{ all -> 0x006c }
            return
        L_0x0029:
            android.os.Handler r2 = r4.renderThreadHandler     // Catch:{ all -> 0x006c }
            com.oney.WebRTCModule.SurfaceViewRenderer$5 r3 = new com.oney.WebRTCModule.SurfaceViewRenderer$5     // Catch:{ all -> 0x006c }
            r3.<init>(r0)     // Catch:{ all -> 0x006c }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x006c }
            r2 = 0
            r4.renderThreadHandler = r2     // Catch:{ all -> 0x006c }
            monitor-exit(r1)     // Catch:{ all -> 0x006c }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            android.os.HandlerThread r0 = r4.renderThread
            r0.quit()
            java.lang.Object r0 = r4.frameLock
            monitor-enter(r0)
            org.webrtc.VideoRenderer$I420Frame r1 = r4.pendingFrame     // Catch:{ all -> 0x0069 }
            if (r1 == 0) goto L_0x004d
            org.webrtc.VideoRenderer$I420Frame r1 = r4.pendingFrame     // Catch:{ all -> 0x0069 }
            org.webrtc.VideoRenderer.renderFrameDone(r1)     // Catch:{ all -> 0x0069 }
            r4.pendingFrame = r2     // Catch:{ all -> 0x0069 }
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x0069 }
            android.os.HandlerThread r0 = r4.renderThread
            org.webrtc.ThreadUtils.joinUninterruptibly(r0)
            r4.renderThread = r2
            java.lang.Object r1 = r4.layoutLock
            monitor-enter(r1)
            r0 = 0
            r4.frameWidth = r0     // Catch:{ all -> 0x0066 }
            r4.frameHeight = r0     // Catch:{ all -> 0x0066 }
            r4.frameRotation = r0     // Catch:{ all -> 0x0066 }
            r4.rendererEvents = r2     // Catch:{ all -> 0x0066 }
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            r4.resetStatistics()
            return
        L_0x0066:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            throw r0
        L_0x0069:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0069 }
            throw r1
        L_0x006c:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x006c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.SurfaceViewRenderer.release():void");
    }

    public void resetStatistics() {
        synchronized (this.statisticsLock) {
            this.framesReceived = 0;
            this.framesDropped = 0;
            this.framesRendered = 0;
            this.firstFrameTimeNs = 0;
            this.renderTimeNs = 0;
        }
    }

    public void setMirror(boolean z) {
        synchronized (this.layoutLock) {
            this.mirror = z;
        }
    }

    public void setScalingType(ScalingType scalingType2) {
        synchronized (this.layoutLock) {
            this.scalingType = scalingType2;
        }
    }

    public void renderFrame(I420Frame i420Frame) {
        synchronized (this.statisticsLock) {
            this.framesReceived++;
        }
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(getResourceName());
                sb.append("Dropping frame - Not initialized or already released.");
                Logging.m314d(str, sb.toString());
                VideoRenderer.renderFrameDone(i420Frame);
                return;
            }
            synchronized (this.frameLock) {
                if (this.pendingFrame != null) {
                    synchronized (this.statisticsLock) {
                        this.framesDropped++;
                    }
                    VideoRenderer.renderFrameDone(this.pendingFrame);
                }
                this.pendingFrame = i420Frame;
                this.renderThreadHandler.post(this.renderFrameRunnable);
            }
        }
    }

    private Point getDesiredLayoutSize(int i, int i2) {
        Point displaySize;
        synchronized (this.layoutLock) {
            int defaultSize = getDefaultSize(Integer.MAX_VALUE, i);
            int defaultSize2 = getDefaultSize(Integer.MAX_VALUE, i2);
            displaySize = RendererCommon.getDisplaySize(this.scalingType, frameAspectRatio(), defaultSize, defaultSize2);
            if (MeasureSpec.getMode(i) == 1073741824) {
                displaySize.x = defaultSize;
            }
            if (MeasureSpec.getMode(i2) == 1073741824) {
                displaySize.y = defaultSize2;
            }
        }
        return displaySize;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0036, code lost:
        if (r3 == false) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
        r3 = r2.handlerLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        if (r2.renderThreadHandler == null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003f, code lost:
        r2.renderThreadHandler.postAtFrontOfQueue(r2.makeBlackRunnable);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0046, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r3, int r4) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.layoutLock
            monitor-enter(r0)
            int r1 = r2.frameWidth     // Catch:{ all -> 0x0051 }
            if (r1 == 0) goto L_0x004c
            int r1 = r2.frameHeight     // Catch:{ all -> 0x0051 }
            if (r1 != 0) goto L_0x000c
            goto L_0x004c
        L_0x000c:
            android.graphics.Point r3 = r2.getDesiredLayoutSize(r3, r4)     // Catch:{ all -> 0x0051 }
            r2.desiredLayoutSize = r3     // Catch:{ all -> 0x0051 }
            android.graphics.Point r3 = r2.desiredLayoutSize     // Catch:{ all -> 0x0051 }
            int r3 = r3.x     // Catch:{ all -> 0x0051 }
            int r4 = r2.getMeasuredWidth()     // Catch:{ all -> 0x0051 }
            if (r3 != r4) goto L_0x0029
            android.graphics.Point r3 = r2.desiredLayoutSize     // Catch:{ all -> 0x0051 }
            int r3 = r3.y     // Catch:{ all -> 0x0051 }
            int r4 = r2.getMeasuredHeight()     // Catch:{ all -> 0x0051 }
            if (r3 == r4) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r3 = 0
            goto L_0x002a
        L_0x0029:
            r3 = 1
        L_0x002a:
            android.graphics.Point r4 = r2.desiredLayoutSize     // Catch:{ all -> 0x0051 }
            int r4 = r4.x     // Catch:{ all -> 0x0051 }
            android.graphics.Point r1 = r2.desiredLayoutSize     // Catch:{ all -> 0x0051 }
            int r1 = r1.y     // Catch:{ all -> 0x0051 }
            r2.setMeasuredDimension(r4, r1)     // Catch:{ all -> 0x0051 }
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            if (r3 == 0) goto L_0x004b
            java.lang.Object r3 = r2.handlerLock
            monitor-enter(r3)
            android.os.Handler r4 = r2.renderThreadHandler     // Catch:{ all -> 0x0048 }
            if (r4 == 0) goto L_0x0046
            android.os.Handler r4 = r2.renderThreadHandler     // Catch:{ all -> 0x0048 }
            java.lang.Runnable r0 = r2.makeBlackRunnable     // Catch:{ all -> 0x0048 }
            r4.postAtFrontOfQueue(r0)     // Catch:{ all -> 0x0048 }
        L_0x0046:
            monitor-exit(r3)     // Catch:{ all -> 0x0048 }
            goto L_0x004b
        L_0x0048:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0048 }
            throw r4
        L_0x004b:
            return
        L_0x004c:
            super.onMeasure(r3, r4)     // Catch:{ all -> 0x0051 }
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            return
        L_0x0051:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.SurfaceViewRenderer.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        synchronized (this.layoutLock) {
            this.layoutSize.x = i3 - i;
            this.layoutSize.y = i4 - i2;
        }
        runOnRenderThread(this.renderFrameRunnable);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(getResourceName());
        sb.append("Surface created.");
        Logging.m314d(str, sb.toString());
        synchronized (this.layoutLock) {
            this.isSurfaceCreated = true;
        }
        tryCreateEglSurface();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(getResourceName());
        sb.append("Surface destroyed.");
        Logging.m314d(str, sb.toString());
        synchronized (this.layoutLock) {
            this.isSurfaceCreated = false;
            this.surfaceSize.x = 0;
            this.surfaceSize.y = 0;
        }
        runOnRenderThread(new Runnable() {
            public void run() {
                if (SurfaceViewRenderer.this.eglBase != null) {
                    SurfaceViewRenderer.this.eglBase.detachCurrent();
                    SurfaceViewRenderer.this.eglBase.releaseSurface();
                }
            }
        });
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(getResourceName());
        sb.append("Surface changed: ");
        sb.append(i2);
        sb.append("x");
        sb.append(i3);
        Logging.m314d(str, sb.toString());
        synchronized (this.layoutLock) {
            this.surfaceSize.x = i2;
            this.surfaceSize.y = i3;
        }
        runOnRenderThread(this.renderFrameRunnable);
    }

    private void runOnRenderThread(Runnable runnable) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.post(runnable);
            }
        }
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

    /* access modifiers changed from: private */
    public void makeBlack() {
        if (Thread.currentThread() != this.renderThread) {
            StringBuilder sb = new StringBuilder();
            sb.append(getResourceName());
            sb.append("Wrong thread.");
            throw new IllegalStateException(sb.toString());
        } else if (this.eglBase != null && this.eglBase.hasSurface()) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(16384);
            this.eglBase.swapBuffers();
        }
    }

    private boolean checkConsistentLayout() {
        boolean equals;
        if (Thread.currentThread() != this.renderThread) {
            StringBuilder sb = new StringBuilder();
            sb.append(getResourceName());
            sb.append("Wrong thread.");
            throw new IllegalStateException(sb.toString());
        }
        synchronized (this.layoutLock) {
            equals = this.surfaceSize.equals(this.layoutSize);
        }
        return equals;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0032, code lost:
        updateFrameDimensionsAndReportEvents(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0037, code lost:
        if (r15.eglBase == null) goto L_0x0160;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003f, code lost:
        if (r15.eglBase.hasSurface() != false) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0047, code lost:
        if (checkConsistentLayout() != false) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        makeBlack();
        org.webrtc.VideoRenderer.renderFrameDone(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0050, code lost:
        r0 = r15.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0052, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005d, code lost:
        if (r15.eglBase.surfaceWidth() != r15.surfaceSize.x) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0069, code lost:
        if (r15.eglBase.surfaceHeight() == r15.surfaceSize.y) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006b, code lost:
        makeBlack();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006e, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006f, code lost:
        r2 = java.lang.System.nanoTime();
        r4 = r15.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0075, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r8 = org.webrtc.RendererCommon.multiplyMatrices(org.webrtc.RendererCommon.rotateTextureMatrix(r1.samplingMatrix, (float) r1.rotationDegree), org.webrtc.RendererCommon.getLayoutMatrix(r15.mirror, frameAspectRatio(), ((float) r15.layoutSize.x) / ((float) r15.layoutSize.y)));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0098, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0099, code lost:
        android.opengl.GLES20.glClear(16384);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a0, code lost:
        if (r1.yuvFrame == false) goto L_0x00e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a4, code lost:
        if (r15.yuvTextures != null) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a6, code lost:
        r15.yuvTextures = new int[3];
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ac, code lost:
        if (r4 >= 3) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ae, code lost:
        r15.yuvTextures[r4] = org.webrtc.GlUtil.generateTexture(3553);
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00bb, code lost:
        r15.yuvUploader.uploadYuvData(r15.yuvTextures, r1.width, r1.height, r1.yuvStrides, r1.yuvPlanes);
        r15.drawer.drawYuv(r15.yuvTextures, r8, r1.rotatedWidth(), r1.rotatedHeight(), 0, 0, r15.surfaceSize.x, r15.surfaceSize.y);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00e4, code lost:
        r15.drawer.drawOes(r1.textureId, r8, r1.rotatedWidth(), r1.rotatedHeight(), 0, 0, r15.surfaceSize.x, r15.surfaceSize.y);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00fd, code lost:
        r15.eglBase.swapBuffers();
        org.webrtc.VideoRenderer.renderFrameDone(r1);
        r0 = r15.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0107, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x010a, code lost:
        if (r15.framesRendered != 0) goto L_0x0139;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x010c, code lost:
        r15.firstFrameTimeNs = r2;
        r1 = r15.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0110, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0111, code lost:
        r4 = TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r5 = new java.lang.StringBuilder();
        r5.append(getResourceName());
        r5.append("Reporting first rendered frame.");
        org.webrtc.Logging.m314d(r4, r5.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x012d, code lost:
        if (r15.rendererEvents == null) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x012f, code lost:
        r15.rendererEvents.onFirstFrameRendered();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0134, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0139, code lost:
        r15.framesRendered++;
        r15.renderTimeNs += java.lang.System.nanoTime() - r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0150, code lost:
        if ((r15.framesRendered % 300) != 0) goto L_0x0155;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0152, code lost:
        logStatistics();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0155, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0156, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0160, code lost:
        r0 = TAG;
        r2 = new java.lang.StringBuilder();
        r2.append(getResourceName());
        r2.append("No surface to draw on");
        org.webrtc.Logging.m314d(r0, r2.toString());
        org.webrtc.VideoRenderer.renderFrameDone(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x017d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r15 = this;
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            android.os.HandlerThread r1 = r15.renderThread
            if (r0 == r1) goto L_0x0023
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r15.getResourceName()
            r1.append(r2)
            java.lang.String r2 = "Wrong thread."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0023:
            java.lang.Object r0 = r15.frameLock
            monitor-enter(r0)
            org.webrtc.VideoRenderer$I420Frame r1 = r15.pendingFrame     // Catch:{ all -> 0x017e }
            if (r1 != 0) goto L_0x002c
            monitor-exit(r0)     // Catch:{ all -> 0x017e }
            return
        L_0x002c:
            org.webrtc.VideoRenderer$I420Frame r1 = r15.pendingFrame     // Catch:{ all -> 0x017e }
            r2 = 0
            r15.pendingFrame = r2     // Catch:{ all -> 0x017e }
            monitor-exit(r0)     // Catch:{ all -> 0x017e }
            r15.updateFrameDimensionsAndReportEvents(r1)
            org.webrtc.EglBase r0 = r15.eglBase
            if (r0 == 0) goto L_0x0160
            org.webrtc.EglBase r0 = r15.eglBase
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x0043
            goto L_0x0160
        L_0x0043:
            boolean r0 = r15.checkConsistentLayout()
            if (r0 != 0) goto L_0x0050
            r15.makeBlack()
            org.webrtc.VideoRenderer.renderFrameDone(r1)
            return
        L_0x0050:
            java.lang.Object r0 = r15.layoutLock
            monitor-enter(r0)
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ all -> 0x015d }
            int r2 = r2.surfaceWidth()     // Catch:{ all -> 0x015d }
            android.graphics.Point r3 = r15.surfaceSize     // Catch:{ all -> 0x015d }
            int r3 = r3.x     // Catch:{ all -> 0x015d }
            if (r2 != r3) goto L_0x006b
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ all -> 0x015d }
            int r2 = r2.surfaceHeight()     // Catch:{ all -> 0x015d }
            android.graphics.Point r3 = r15.surfaceSize     // Catch:{ all -> 0x015d }
            int r3 = r3.y     // Catch:{ all -> 0x015d }
            if (r2 == r3) goto L_0x006e
        L_0x006b:
            r15.makeBlack()     // Catch:{ all -> 0x015d }
        L_0x006e:
            monitor-exit(r0)     // Catch:{ all -> 0x015d }
            long r2 = java.lang.System.nanoTime()
            java.lang.Object r4 = r15.layoutLock
            monitor-enter(r4)
            float[] r0 = r1.samplingMatrix     // Catch:{ all -> 0x015a }
            int r5 = r1.rotationDegree     // Catch:{ all -> 0x015a }
            float r5 = (float) r5     // Catch:{ all -> 0x015a }
            float[] r0 = org.webrtc.RendererCommon.rotateTextureMatrix(r0, r5)     // Catch:{ all -> 0x015a }
            boolean r5 = r15.mirror     // Catch:{ all -> 0x015a }
            float r6 = r15.frameAspectRatio()     // Catch:{ all -> 0x015a }
            android.graphics.Point r7 = r15.layoutSize     // Catch:{ all -> 0x015a }
            int r7 = r7.x     // Catch:{ all -> 0x015a }
            float r7 = (float) r7     // Catch:{ all -> 0x015a }
            android.graphics.Point r8 = r15.layoutSize     // Catch:{ all -> 0x015a }
            int r8 = r8.y     // Catch:{ all -> 0x015a }
            float r8 = (float) r8     // Catch:{ all -> 0x015a }
            float r7 = r7 / r8
            float[] r5 = org.webrtc.RendererCommon.getLayoutMatrix(r5, r6, r7)     // Catch:{ all -> 0x015a }
            float[] r8 = org.webrtc.RendererCommon.multiplyMatrices(r0, r5)     // Catch:{ all -> 0x015a }
            monitor-exit(r4)     // Catch:{ all -> 0x015a }
            r0 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r0)
            boolean r0 = r1.yuvFrame
            if (r0 == 0) goto L_0x00e4
            int[] r0 = r15.yuvTextures
            if (r0 != 0) goto L_0x00bb
            r0 = 3
            int[] r4 = new int[r0]
            r15.yuvTextures = r4
            r4 = 0
        L_0x00ac:
            if (r4 >= r0) goto L_0x00bb
            int[] r5 = r15.yuvTextures
            r6 = 3553(0xde1, float:4.979E-42)
            int r6 = org.webrtc.GlUtil.generateTexture(r6)
            r5[r4] = r6
            int r4 = r4 + 1
            goto L_0x00ac
        L_0x00bb:
            org.webrtc.RendererCommon$YuvUploader r9 = r15.yuvUploader
            int[] r10 = r15.yuvTextures
            int r11 = r1.width
            int r12 = r1.height
            int[] r13 = r1.yuvStrides
            java.nio.ByteBuffer[] r14 = r1.yuvPlanes
            r9.uploadYuvData(r10, r11, r12, r13, r14)
            org.webrtc.RendererCommon$GlDrawer r6 = r15.drawer
            int[] r7 = r15.yuvTextures
            int r9 = r1.rotatedWidth()
            int r10 = r1.rotatedHeight()
            r11 = 0
            r12 = 0
            android.graphics.Point r0 = r15.surfaceSize
            int r13 = r0.x
            android.graphics.Point r0 = r15.surfaceSize
            int r14 = r0.y
            r6.drawYuv(r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x00fd
        L_0x00e4:
            org.webrtc.RendererCommon$GlDrawer r6 = r15.drawer
            int r7 = r1.textureId
            int r9 = r1.rotatedWidth()
            int r10 = r1.rotatedHeight()
            r11 = 0
            r12 = 0
            android.graphics.Point r0 = r15.surfaceSize
            int r13 = r0.x
            android.graphics.Point r0 = r15.surfaceSize
            int r14 = r0.y
            r6.drawOes(r7, r8, r9, r10, r11, r12, r13, r14)
        L_0x00fd:
            org.webrtc.EglBase r0 = r15.eglBase
            r0.swapBuffers()
            org.webrtc.VideoRenderer.renderFrameDone(r1)
            java.lang.Object r0 = r15.statisticsLock
            monitor-enter(r0)
            int r1 = r15.framesRendered     // Catch:{ all -> 0x0157 }
            if (r1 != 0) goto L_0x0139
            r15.firstFrameTimeNs = r2     // Catch:{ all -> 0x0157 }
            java.lang.Object r1 = r15.layoutLock     // Catch:{ all -> 0x0157 }
            monitor-enter(r1)     // Catch:{ all -> 0x0157 }
            java.lang.String r4 = "SurfaceViewRenderer"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0136 }
            r5.<init>()     // Catch:{ all -> 0x0136 }
            java.lang.String r6 = r15.getResourceName()     // Catch:{ all -> 0x0136 }
            r5.append(r6)     // Catch:{ all -> 0x0136 }
            java.lang.String r6 = "Reporting first rendered frame."
            r5.append(r6)     // Catch:{ all -> 0x0136 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0136 }
            org.webrtc.Logging.m314d(r4, r5)     // Catch:{ all -> 0x0136 }
            org.webrtc.RendererCommon$RendererEvents r4 = r15.rendererEvents     // Catch:{ all -> 0x0136 }
            if (r4 == 0) goto L_0x0134
            org.webrtc.RendererCommon$RendererEvents r4 = r15.rendererEvents     // Catch:{ all -> 0x0136 }
            r4.onFirstFrameRendered()     // Catch:{ all -> 0x0136 }
        L_0x0134:
            monitor-exit(r1)     // Catch:{ all -> 0x0136 }
            goto L_0x0139
        L_0x0136:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0136 }
            throw r2     // Catch:{ all -> 0x0157 }
        L_0x0139:
            int r1 = r15.framesRendered     // Catch:{ all -> 0x0157 }
            int r1 = r1 + 1
            r15.framesRendered = r1     // Catch:{ all -> 0x0157 }
            long r4 = r15.renderTimeNs     // Catch:{ all -> 0x0157 }
            long r6 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0157 }
            r1 = 0
            long r8 = r6 - r2
            long r1 = r4 + r8
            r15.renderTimeNs = r1     // Catch:{ all -> 0x0157 }
            int r1 = r15.framesRendered     // Catch:{ all -> 0x0157 }
            int r1 = r1 % 300
            if (r1 != 0) goto L_0x0155
            r15.logStatistics()     // Catch:{ all -> 0x0157 }
        L_0x0155:
            monitor-exit(r0)     // Catch:{ all -> 0x0157 }
            return
        L_0x0157:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0157 }
            throw r1
        L_0x015a:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x015a }
            throw r0
        L_0x015d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x015d }
            throw r1
        L_0x0160:
            java.lang.String r0 = "SurfaceViewRenderer"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r15.getResourceName()
            r2.append(r3)
            java.lang.String r3 = "No surface to draw on"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            org.webrtc.Logging.m314d(r0, r2)
            org.webrtc.VideoRenderer.renderFrameDone(r1)
            return
        L_0x017e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x017e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.SurfaceViewRenderer.renderFrameOnRenderThread():void");
    }

    private float frameAspectRatio() {
        float f;
        int i;
        synchronized (this.layoutLock) {
            if (this.frameWidth != 0) {
                if (this.frameHeight != 0) {
                    if (this.frameRotation % RotationOptions.ROTATE_180 == 0) {
                        f = (float) this.frameWidth;
                        i = this.frameHeight;
                    } else {
                        f = (float) this.frameHeight;
                        i = this.frameWidth;
                    }
                    float f2 = f / ((float) i);
                    return f2;
                }
            }
            return 0.0f;
        }
    }

    private void updateFrameDimensionsAndReportEvents(I420Frame i420Frame) {
        synchronized (this.layoutLock) {
            if (!(this.frameWidth == i420Frame.width && this.frameHeight == i420Frame.height && this.frameRotation == i420Frame.rotationDegree)) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(getResourceName());
                sb.append("Reporting frame resolution changed to ");
                sb.append(i420Frame.width);
                sb.append("x");
                sb.append(i420Frame.height);
                sb.append(" with rotation ");
                sb.append(i420Frame.rotationDegree);
                Logging.m314d(str, sb.toString());
                if (this.rendererEvents != null) {
                    this.rendererEvents.onFrameResolutionChanged(i420Frame.width, i420Frame.height, i420Frame.rotationDegree);
                }
                this.frameWidth = i420Frame.width;
                this.frameHeight = i420Frame.height;
                this.frameRotation = i420Frame.rotationDegree;
                post(new Runnable() {
                    public void run() {
                        SurfaceViewRenderer.this.requestLayout();
                    }
                });
            }
        }
    }

    private void logStatistics() {
        synchronized (this.statisticsLock) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(getResourceName());
            sb.append("Frames received: ");
            sb.append(this.framesReceived);
            sb.append(". Dropped: ");
            sb.append(this.framesDropped);
            sb.append(". Rendered: ");
            sb.append(this.framesRendered);
            Logging.m314d(str, sb.toString());
            if (this.framesReceived > 0 && this.framesRendered > 0) {
                long nanoTime = System.nanoTime() - this.firstFrameTimeNs;
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getResourceName());
                sb2.append("Duration: ");
                double d = (double) nanoTime;
                sb2.append((int) (d / 1000000.0d));
                sb2.append(" ms. FPS: ");
                sb2.append((((double) this.framesRendered) * 1.0E9d) / d);
                Logging.m314d(str2, sb2.toString());
                String str3 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(getResourceName());
                sb3.append("Average render time: ");
                sb3.append((int) (this.renderTimeNs / ((long) (this.framesRendered * 1000))));
                sb3.append(" us.");
                Logging.m314d(str3, sb3.toString());
            }
        }
    }
}
