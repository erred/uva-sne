package org.webrtc;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.webrtc.EglBase.Context;
import org.webrtc.RendererCommon.GlDrawer;
import org.webrtc.RendererCommon.YuvUploader;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRenderer.I420Frame;

public class EglRenderer implements Callbacks {
    private static final long LOG_INTERVAL_SEC = 4;
    private static final int MAX_SURFACE_CLEAR_COUNT = 3;
    private static final String TAG = "EglRenderer";
    /* access modifiers changed from: private */
    public GlTextureFrameBuffer bitmapTextureFramebuffer;
    /* access modifiers changed from: private */
    public GlDrawer drawer;
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final EglSurfaceCreation eglSurfaceCreationRunnable = new EglSurfaceCreation();
    private final Object fpsReductionLock = new Object();
    /* access modifiers changed from: private */
    public final ArrayList<FrameListenerAndParams> frameListeners = new ArrayList<>();
    private final Object frameLock = new Object();
    private int framesDropped;
    private int framesReceived;
    private int framesRendered;
    /* access modifiers changed from: private */
    public final Object handlerLock = new Object();
    private float layoutAspectRatio;
    private final Object layoutLock = new Object();
    /* access modifiers changed from: private */
    public final Runnable logStatisticsRunnable = new Runnable() {
        public void run() {
            EglRenderer.this.logStatistics();
            synchronized (EglRenderer.this.handlerLock) {
                if (EglRenderer.this.renderThreadHandler != null) {
                    EglRenderer.this.renderThreadHandler.removeCallbacks(EglRenderer.this.logStatisticsRunnable);
                    EglRenderer.this.renderThreadHandler.postDelayed(EglRenderer.this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(4));
                }
            }
        }
    };
    private long minRenderPeriodNs;
    private boolean mirror;
    private final String name;
    private long nextFrameTimeNs;
    private I420Frame pendingFrame;
    private final Runnable renderFrameRunnable = new Runnable() {
        public void run() {
            EglRenderer.this.renderFrameOnRenderThread();
        }
    };
    private long renderSwapBufferTimeNs;
    /* access modifiers changed from: private */
    public Handler renderThreadHandler;
    private long renderTimeNs;
    private final Object statisticsLock = new Object();
    private long statisticsStartTimeNs;
    /* access modifiers changed from: private */
    public int[] yuvTextures = null;
    private final YuvUploader yuvUploader = new YuvUploader();

    private class EglSurfaceCreation implements Runnable {
        private Object surface;

        private EglSurfaceCreation() {
        }

        public synchronized void setSurface(Object obj) {
            this.surface = obj;
        }

        public synchronized void run() {
            if (!(this.surface == null || EglRenderer.this.eglBase == null || EglRenderer.this.eglBase.hasSurface())) {
                if (this.surface instanceof Surface) {
                    EglRenderer.this.eglBase.createSurface((Surface) this.surface);
                } else if (this.surface instanceof SurfaceTexture) {
                    EglRenderer.this.eglBase.createSurface((SurfaceTexture) this.surface);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid surface: ");
                    sb.append(this.surface);
                    throw new IllegalStateException(sb.toString());
                }
                EglRenderer.this.eglBase.makeCurrent();
                GLES20.glPixelStorei(3317, 1);
            }
        }
    }

    public interface FrameListener {
        void onFrame(Bitmap bitmap);
    }

    private static class FrameListenerAndParams {
        public final GlDrawer drawer;
        public final FrameListener listener;
        public final float scale;

        public FrameListenerAndParams(FrameListener frameListener, float f, GlDrawer glDrawer) {
            this.listener = frameListener;
            this.scale = f;
            this.drawer = glDrawer;
        }
    }

    public EglRenderer(String str) {
        this.name = str;
    }

    public void init(final Context context, final int[] iArr, GlDrawer glDrawer) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.name);
                sb.append("Already initialized");
                throw new IllegalStateException(sb.toString());
            }
            logD("Initializing EglRenderer");
            this.drawer = glDrawer;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.name);
            sb2.append(TAG);
            HandlerThread handlerThread = new HandlerThread(sb2.toString());
            handlerThread.start();
            this.renderThreadHandler = new Handler(handlerThread.getLooper());
            ThreadUtils.invokeAtFrontUninterruptibly(this.renderThreadHandler, (Runnable) new Runnable() {
                public void run() {
                    if (context == null) {
                        EglRenderer.this.logD("EglBase10.create context");
                        EglRenderer.this.eglBase = new EglBase10(null, iArr);
                        return;
                    }
                    EglRenderer.this.logD("EglBase.create shared context");
                    EglRenderer.this.eglBase = EglBase.create(context, iArr);
                }
            });
            this.renderThreadHandler.post(this.eglSurfaceCreationRunnable);
            resetStatistics(System.nanoTime());
            this.renderThreadHandler.postDelayed(this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(4));
        }
    }

    public void createEglSurface(Surface surface) {
        createEglSurfaceInternal(surface);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture);
    }

    private void createEglSurfaceInternal(Object obj) {
        this.eglSurfaceCreationRunnable.setSurface(obj);
        postToRenderThread(this.eglSurfaceCreationRunnable);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003e, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r0 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0043, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        if (r5.pendingFrame == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        org.webrtc.VideoRenderer.renderFrameDone(r5.pendingFrame);
        r5.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0050, code lost:
        logD("Releasing done.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0055, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void release() {
        /*
            r5 = this;
            java.lang.String r0 = "Releasing."
            r5.logD(r0)
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch
            r1 = 1
            r0.<init>(r1)
            java.lang.Object r1 = r5.handlerLock
            monitor-enter(r1)
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            if (r2 != 0) goto L_0x0019
            java.lang.String r0 = "Already released"
            r5.logD(r0)     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            return
        L_0x0019:
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            java.lang.Runnable r3 = r5.logStatisticsRunnable     // Catch:{ all -> 0x0059 }
            r2.removeCallbacks(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.EglRenderer$4 r3 = new org.webrtc.EglRenderer$4     // Catch:{ all -> 0x0059 }
            r3.<init>(r0)     // Catch:{ all -> 0x0059 }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x0059 }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.EglRenderer$5 r4 = new org.webrtc.EglRenderer$5     // Catch:{ all -> 0x0059 }
            r4.<init>(r2)     // Catch:{ all -> 0x0059 }
            r3.post(r4)     // Catch:{ all -> 0x0059 }
            r2 = 0
            r5.renderThreadHandler = r2     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r0 = r5.frameLock
            monitor-enter(r0)
            org.webrtc.VideoRenderer$I420Frame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            if (r1 == 0) goto L_0x004f
            org.webrtc.VideoRenderer$I420Frame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            org.webrtc.VideoRenderer.renderFrameDone(r1)     // Catch:{ all -> 0x0056 }
            r5.pendingFrame = r2     // Catch:{ all -> 0x0056 }
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            java.lang.String r0 = "Releasing done."
            r5.logD(r0)
            return
        L_0x0056:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            throw r1
        L_0x0059:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.release():void");
    }

    private void resetStatistics(long j) {
        synchronized (this.statisticsLock) {
            this.statisticsStartTimeNs = j;
            this.framesReceived = 0;
            this.framesDropped = 0;
            this.framesRendered = 0;
            this.renderTimeNs = 0;
            this.renderSwapBufferTimeNs = 0;
        }
    }

    public void printStackTrace() {
        Thread thread;
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                thread = null;
            } else {
                thread = this.renderThreadHandler.getLooper().getThread();
            }
            if (thread != null) {
                StackTraceElement[] stackTrace = thread.getStackTrace();
                if (stackTrace.length > 0) {
                    logD("EglRenderer stack trace:");
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        logD(stackTraceElement.toString());
                    }
                }
            }
        }
    }

    public void setMirror(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("setMirror: ");
        sb.append(z);
        logD(sb.toString());
        synchronized (this.layoutLock) {
            this.mirror = z;
        }
    }

    public void setLayoutAspectRatio(float f) {
        StringBuilder sb = new StringBuilder();
        sb.append("setLayoutAspectRatio: ");
        sb.append(f);
        logD(sb.toString());
        synchronized (this.layoutLock) {
            this.layoutAspectRatio = f;
        }
    }

    public void setFpsReduction(float f) {
        StringBuilder sb = new StringBuilder();
        sb.append("setFpsReduction: ");
        sb.append(f);
        logD(sb.toString());
        synchronized (this.fpsReductionLock) {
            long j = this.minRenderPeriodNs;
            if (f <= 0.0f) {
                this.minRenderPeriodNs = Long.MAX_VALUE;
            } else {
                this.minRenderPeriodNs = (long) (((float) TimeUnit.SECONDS.toNanos(1)) / f);
            }
            if (this.minRenderPeriodNs != j) {
                this.nextFrameTimeNs = System.nanoTime();
            }
        }
    }

    public void disableFpsReduction() {
        setFpsReduction(Float.POSITIVE_INFINITY);
    }

    public void pauseVideo() {
        setFpsReduction(0.0f);
    }

    public void addFrameListener(final FrameListener frameListener, final float f) {
        postToRenderThread(new Runnable() {
            public void run() {
                EglRenderer.this.frameListeners.add(new FrameListenerAndParams(frameListener, f, EglRenderer.this.drawer));
            }
        });
    }

    public void addFrameListener(final FrameListener frameListener, final float f, final GlDrawer glDrawer) {
        postToRenderThread(new Runnable() {
            public void run() {
                EglRenderer.this.frameListeners.add(new FrameListenerAndParams(frameListener, f, glDrawer));
            }
        });
    }

    public void removeFrameListener(final FrameListener frameListener) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        postToRenderThread(new Runnable() {
            public void run() {
                countDownLatch.countDown();
                Iterator it = EglRenderer.this.frameListeners.iterator();
                while (it.hasNext()) {
                    if (((FrameListenerAndParams) it.next()).listener == frameListener) {
                        it.remove();
                    }
                }
            }
        });
        ThreadUtils.awaitUninterruptibly(countDownLatch);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        r0 = r11.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004f, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0052, code lost:
        if (r11.pendingFrame == null) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0054, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0056, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0057, code lost:
        if (r3 == false) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0059, code lost:
        org.webrtc.VideoRenderer.renderFrameDone(r11.pendingFrame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x005e, code lost:
        r11.pendingFrame = r12;
        r11.renderThreadHandler.post(r11.renderFrameRunnable);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0067, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0069, code lost:
        if (r3 == false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x006b, code lost:
        r12 = r11.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x006d, code lost:
        monitor-enter(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        r11.framesDropped++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0073, code lost:
        monitor-exit(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0078, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrame(org.webrtc.VideoRenderer.I420Frame r12) {
        /*
            r11 = this;
            java.lang.Object r0 = r11.statisticsLock
            monitor-enter(r0)
            int r1 = r11.framesReceived     // Catch:{ all -> 0x0082 }
            r2 = 1
            int r1 = r1 + r2
            r11.framesReceived = r1     // Catch:{ all -> 0x0082 }
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            java.lang.Object r1 = r11.handlerLock
            monitor-enter(r1)
            android.os.Handler r0 = r11.renderThreadHandler     // Catch:{ all -> 0x007f }
            if (r0 != 0) goto L_0x001b
            java.lang.String r0 = "Dropping frame - Not initialized or already released."
            r11.logD(r0)     // Catch:{ all -> 0x007f }
            org.webrtc.VideoRenderer.renderFrameDone(r12)     // Catch:{ all -> 0x007f }
            monitor-exit(r1)     // Catch:{ all -> 0x007f }
            return
        L_0x001b:
            java.lang.Object r0 = r11.fpsReductionLock     // Catch:{ all -> 0x007f }
            monitor-enter(r0)     // Catch:{ all -> 0x007f }
            long r3 = r11.minRenderPeriodNs     // Catch:{ all -> 0x007c }
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x004c
            long r3 = java.lang.System.nanoTime()     // Catch:{ all -> 0x007c }
            long r5 = r11.nextFrameTimeNs     // Catch:{ all -> 0x007c }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x003b
            java.lang.String r2 = "Dropping frame - fps reduction is active."
            r11.logD(r2)     // Catch:{ all -> 0x007c }
            org.webrtc.VideoRenderer.renderFrameDone(r12)     // Catch:{ all -> 0x007c }
            monitor-exit(r0)     // Catch:{ all -> 0x007c }
            monitor-exit(r1)     // Catch:{ all -> 0x007f }
            return
        L_0x003b:
            long r5 = r11.nextFrameTimeNs     // Catch:{ all -> 0x007c }
            long r7 = r11.minRenderPeriodNs     // Catch:{ all -> 0x007c }
            r9 = 0
            long r9 = r5 + r7
            r11.nextFrameTimeNs = r9     // Catch:{ all -> 0x007c }
            long r5 = r11.nextFrameTimeNs     // Catch:{ all -> 0x007c }
            long r3 = java.lang.Math.max(r5, r3)     // Catch:{ all -> 0x007c }
            r11.nextFrameTimeNs = r3     // Catch:{ all -> 0x007c }
        L_0x004c:
            monitor-exit(r0)     // Catch:{ all -> 0x007c }
            java.lang.Object r0 = r11.frameLock     // Catch:{ all -> 0x007f }
            monitor-enter(r0)     // Catch:{ all -> 0x007f }
            org.webrtc.VideoRenderer$I420Frame r3 = r11.pendingFrame     // Catch:{ all -> 0x0079 }
            if (r3 == 0) goto L_0x0056
            r3 = 1
            goto L_0x0057
        L_0x0056:
            r3 = 0
        L_0x0057:
            if (r3 == 0) goto L_0x005e
            org.webrtc.VideoRenderer$I420Frame r4 = r11.pendingFrame     // Catch:{ all -> 0x0079 }
            org.webrtc.VideoRenderer.renderFrameDone(r4)     // Catch:{ all -> 0x0079 }
        L_0x005e:
            r11.pendingFrame = r12     // Catch:{ all -> 0x0079 }
            android.os.Handler r12 = r11.renderThreadHandler     // Catch:{ all -> 0x0079 }
            java.lang.Runnable r4 = r11.renderFrameRunnable     // Catch:{ all -> 0x0079 }
            r12.post(r4)     // Catch:{ all -> 0x0079 }
            monitor-exit(r0)     // Catch:{ all -> 0x0079 }
            monitor-exit(r1)     // Catch:{ all -> 0x007f }
            if (r3 == 0) goto L_0x0078
            java.lang.Object r12 = r11.statisticsLock
            monitor-enter(r12)
            int r0 = r11.framesDropped     // Catch:{ all -> 0x0075 }
            int r0 = r0 + r2
            r11.framesDropped = r0     // Catch:{ all -> 0x0075 }
            monitor-exit(r12)     // Catch:{ all -> 0x0075 }
            goto L_0x0078
        L_0x0075:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0075 }
            throw r0
        L_0x0078:
            return
        L_0x0079:
            r12 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0079 }
            throw r12     // Catch:{ all -> 0x007f }
        L_0x007c:
            r12 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x007c }
            throw r12     // Catch:{ all -> 0x007f }
        L_0x007f:
            r12 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x007f }
            throw r12
        L_0x0082:
            r12 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.renderFrame(org.webrtc.VideoRenderer$I420Frame):void");
    }

    public void releaseEglSurface(final Runnable runnable) {
        this.eglSurfaceCreationRunnable.setSurface(null);
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.removeCallbacks(this.eglSurfaceCreationRunnable);
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        if (EglRenderer.this.eglBase != null) {
                            EglRenderer.this.eglBase.detachCurrent();
                            EglRenderer.this.eglBase.releaseSurface();
                        }
                        runnable.run();
                    }
                });
                return;
            }
            runnable.run();
        }
    }

    private void postToRenderThread(Runnable runnable) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.post(runnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearSurfaceOnRenderThread() {
        if (this.eglBase != null && this.eglBase.hasSurface()) {
            logD("clearSurface");
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(16384);
            this.eglBase.swapBuffers();
        }
    }

    public void clearImage() {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        EglRenderer.this.clearSurfaceOnRenderThread();
                    }
                });
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0013, code lost:
        if (r1.eglBase == null) goto L_0x012c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        if (r1.eglBase.hasSurface() != false) goto L_0x001f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001f, code lost:
        r4 = java.lang.System.nanoTime();
        r2 = org.webrtc.RendererCommon.rotateTextureMatrix(r3.samplingMatrix, (float) r3.rotationDegree);
        r6 = r1.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        if (r1.layoutAspectRatio <= 0.0f) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        r7 = ((float) r3.rotatedWidth()) / ((float) r3.rotatedHeight());
        r9 = org.webrtc.RendererCommon.getLayoutMatrix(r1.mirror, r7, r1.layoutAspectRatio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004d, code lost:
        if (r7 <= r1.layoutAspectRatio) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        r7 = (int) (((float) r3.rotatedHeight()) * r1.layoutAspectRatio);
        r10 = r3.rotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005e, code lost:
        r7 = r3.rotatedWidth();
        r10 = (int) (((float) r3.rotatedWidth()) / r1.layoutAspectRatio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006e, code lost:
        if (r1.mirror == false) goto L_0x0075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0070, code lost:
        r7 = org.webrtc.RendererCommon.horizontalFlipMatrix();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0075, code lost:
        r7 = org.webrtc.RendererCommon.identityMatrix();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0079, code lost:
        r9 = r7;
        r7 = r3.rotatedWidth();
        r10 = r3.rotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0082, code lost:
        r13 = r7;
        r14 = r10;
        r12 = org.webrtc.RendererCommon.multiplyMatrices(r2, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0088, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0089, code lost:
        android.opengl.GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        android.opengl.GLES20.glClear(16384);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0093, code lost:
        if (r3.yuvFrame == false) goto L_0x00de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0097, code lost:
        if (r1.yuvTextures != null) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0099, code lost:
        r1.yuvTextures = new int[3];
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009f, code lost:
        if (r7 >= 3) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a1, code lost:
        r1.yuvTextures[r7] = org.webrtc.GlUtil.generateTexture(3553);
        r7 = r7 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ae, code lost:
        r1.yuvUploader.uploadYuvData(r1.yuvTextures, r3.width, r3.height, r3.yuvStrides, r3.yuvPlanes);
        r1.drawer.drawYuv(r1.yuvTextures, r12, r13, r14, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00de, code lost:
        r1.drawer.drawOes(r3.textureId, r12, r13, r14, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00f4, code lost:
        r6 = java.lang.System.nanoTime();
        r1.eglBase.swapBuffers();
        r8 = java.lang.System.nanoTime();
        r10 = r1.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0103, code lost:
        monitor-enter(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
        r1.framesRendered++;
        r1.renderTimeNs += r8 - r4;
        r1.renderSwapBufferTimeNs += r8 - r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x011c, code lost:
        monitor-exit(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x011d, code lost:
        notifyCallbacks(r3, r2);
        org.webrtc.VideoRenderer.renderFrameDone(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0123, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0124, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0127, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0128, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x012b, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x012c, code lost:
        logD("Dropping frame - No surface");
        org.webrtc.VideoRenderer.renderFrameDone(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0134, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r21 = this;
            r1 = r21
            java.lang.Object r2 = r1.frameLock
            monitor-enter(r2)
            org.webrtc.VideoRenderer$I420Frame r3 = r1.pendingFrame     // Catch:{ all -> 0x0135 }
            if (r3 != 0) goto L_0x000b
            monitor-exit(r2)     // Catch:{ all -> 0x0135 }
            return
        L_0x000b:
            org.webrtc.VideoRenderer$I420Frame r3 = r1.pendingFrame     // Catch:{ all -> 0x0135 }
            r4 = 0
            r1.pendingFrame = r4     // Catch:{ all -> 0x0135 }
            monitor-exit(r2)     // Catch:{ all -> 0x0135 }
            org.webrtc.EglBase r2 = r1.eglBase
            if (r2 == 0) goto L_0x012c
            org.webrtc.EglBase r2 = r1.eglBase
            boolean r2 = r2.hasSurface()
            if (r2 != 0) goto L_0x001f
            goto L_0x012c
        L_0x001f:
            long r4 = java.lang.System.nanoTime()
            float[] r2 = r3.samplingMatrix
            int r6 = r3.rotationDegree
            float r6 = (float) r6
            float[] r2 = org.webrtc.RendererCommon.rotateTextureMatrix(r2, r6)
            java.lang.Object r6 = r1.layoutLock
            monitor-enter(r6)
            float r7 = r1.layoutAspectRatio     // Catch:{ all -> 0x0128 }
            r8 = 0
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x006c
            int r7 = r3.rotatedWidth()     // Catch:{ all -> 0x0128 }
            float r7 = (float) r7     // Catch:{ all -> 0x0128 }
            int r9 = r3.rotatedHeight()     // Catch:{ all -> 0x0128 }
            float r9 = (float) r9     // Catch:{ all -> 0x0128 }
            float r7 = r7 / r9
            boolean r9 = r1.mirror     // Catch:{ all -> 0x0128 }
            float r10 = r1.layoutAspectRatio     // Catch:{ all -> 0x0128 }
            float[] r9 = org.webrtc.RendererCommon.getLayoutMatrix(r9, r7, r10)     // Catch:{ all -> 0x0128 }
            float r10 = r1.layoutAspectRatio     // Catch:{ all -> 0x0128 }
            int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r7 <= 0) goto L_0x005e
            int r7 = r3.rotatedHeight()     // Catch:{ all -> 0x0128 }
            float r7 = (float) r7     // Catch:{ all -> 0x0128 }
            float r10 = r1.layoutAspectRatio     // Catch:{ all -> 0x0128 }
            float r7 = r7 * r10
            int r7 = (int) r7     // Catch:{ all -> 0x0128 }
            int r10 = r3.rotatedHeight()     // Catch:{ all -> 0x0128 }
            goto L_0x0082
        L_0x005e:
            int r7 = r3.rotatedWidth()     // Catch:{ all -> 0x0128 }
            int r10 = r3.rotatedWidth()     // Catch:{ all -> 0x0128 }
            float r10 = (float) r10     // Catch:{ all -> 0x0128 }
            float r11 = r1.layoutAspectRatio     // Catch:{ all -> 0x0128 }
            float r10 = r10 / r11
            int r10 = (int) r10     // Catch:{ all -> 0x0128 }
            goto L_0x0082
        L_0x006c:
            boolean r7 = r1.mirror     // Catch:{ all -> 0x0128 }
            if (r7 == 0) goto L_0x0075
            float[] r7 = org.webrtc.RendererCommon.horizontalFlipMatrix()     // Catch:{ all -> 0x0128 }
            goto L_0x0079
        L_0x0075:
            float[] r7 = org.webrtc.RendererCommon.identityMatrix()     // Catch:{ all -> 0x0128 }
        L_0x0079:
            r9 = r7
            int r7 = r3.rotatedWidth()     // Catch:{ all -> 0x0128 }
            int r10 = r3.rotatedHeight()     // Catch:{ all -> 0x0128 }
        L_0x0082:
            r13 = r7
            r14 = r10
            float[] r12 = org.webrtc.RendererCommon.multiplyMatrices(r2, r9)     // Catch:{ all -> 0x0128 }
            monitor-exit(r6)     // Catch:{ all -> 0x0128 }
            android.opengl.GLES20.glClearColor(r8, r8, r8, r8)
            r6 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r6)
            boolean r6 = r3.yuvFrame
            if (r6 == 0) goto L_0x00de
            int[] r6 = r1.yuvTextures
            if (r6 != 0) goto L_0x00ae
            r6 = 3
            int[] r7 = new int[r6]
            r1.yuvTextures = r7
            r7 = 0
        L_0x009f:
            if (r7 >= r6) goto L_0x00ae
            int[] r8 = r1.yuvTextures
            r9 = 3553(0xde1, float:4.979E-42)
            int r9 = org.webrtc.GlUtil.generateTexture(r9)
            r8[r7] = r9
            int r7 = r7 + 1
            goto L_0x009f
        L_0x00ae:
            org.webrtc.RendererCommon$YuvUploader r15 = r1.yuvUploader
            int[] r6 = r1.yuvTextures
            int r7 = r3.width
            int r8 = r3.height
            int[] r9 = r3.yuvStrides
            java.nio.ByteBuffer[] r10 = r3.yuvPlanes
            r16 = r6
            r17 = r7
            r18 = r8
            r19 = r9
            r20 = r10
            r15.uploadYuvData(r16, r17, r18, r19, r20)
            org.webrtc.RendererCommon$GlDrawer r10 = r1.drawer
            int[] r11 = r1.yuvTextures
            r15 = 0
            r16 = 0
            org.webrtc.EglBase r6 = r1.eglBase
            int r17 = r6.surfaceWidth()
            org.webrtc.EglBase r6 = r1.eglBase
            int r18 = r6.surfaceHeight()
            r10.drawYuv(r11, r12, r13, r14, r15, r16, r17, r18)
            goto L_0x00f4
        L_0x00de:
            org.webrtc.RendererCommon$GlDrawer r10 = r1.drawer
            int r11 = r3.textureId
            r15 = 0
            r16 = 0
            org.webrtc.EglBase r6 = r1.eglBase
            int r17 = r6.surfaceWidth()
            org.webrtc.EglBase r6 = r1.eglBase
            int r18 = r6.surfaceHeight()
            r10.drawOes(r11, r12, r13, r14, r15, r16, r17, r18)
        L_0x00f4:
            long r6 = java.lang.System.nanoTime()
            org.webrtc.EglBase r8 = r1.eglBase
            r8.swapBuffers()
            long r8 = java.lang.System.nanoTime()
            java.lang.Object r10 = r1.statisticsLock
            monitor-enter(r10)
            int r11 = r1.framesRendered     // Catch:{ all -> 0x0124 }
            int r11 = r11 + 1
            r1.framesRendered = r11     // Catch:{ all -> 0x0124 }
            long r11 = r1.renderTimeNs     // Catch:{ all -> 0x0124 }
            r13 = 0
            long r13 = r8 - r4
            long r4 = r11 + r13
            r1.renderTimeNs = r4     // Catch:{ all -> 0x0124 }
            long r4 = r1.renderSwapBufferTimeNs     // Catch:{ all -> 0x0124 }
            r11 = 0
            long r11 = r8 - r6
            long r6 = r4 + r11
            r1.renderSwapBufferTimeNs = r6     // Catch:{ all -> 0x0124 }
            monitor-exit(r10)     // Catch:{ all -> 0x0124 }
            r1.notifyCallbacks(r3, r2)
            org.webrtc.VideoRenderer.renderFrameDone(r3)
            return
        L_0x0124:
            r0 = move-exception
            r2 = r0
            monitor-exit(r10)     // Catch:{ all -> 0x0124 }
            throw r2
        L_0x0128:
            r0 = move-exception
            r2 = r0
            monitor-exit(r6)     // Catch:{ all -> 0x0128 }
            throw r2
        L_0x012c:
            java.lang.String r2 = "Dropping frame - No surface"
            r1.logD(r2)
            org.webrtc.VideoRenderer.renderFrameDone(r3)
            return
        L_0x0135:
            r0 = move-exception
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x0135 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.renderFrameOnRenderThread():void");
    }

    private void notifyCallbacks(I420Frame i420Frame, float[] fArr) {
        int i;
        EglRenderer eglRenderer = this;
        I420Frame i420Frame2 = i420Frame;
        if (!eglRenderer.frameListeners.isEmpty()) {
            ArrayList arrayList = new ArrayList(eglRenderer.frameListeners);
            eglRenderer.frameListeners.clear();
            float[] multiplyMatrices = RendererCommon.multiplyMatrices(RendererCommon.multiplyMatrices(fArr, eglRenderer.mirror ? RendererCommon.horizontalFlipMatrix() : RendererCommon.identityMatrix()), RendererCommon.verticalFlipMatrix());
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                FrameListenerAndParams frameListenerAndParams = (FrameListenerAndParams) it.next();
                int rotatedWidth = (int) (frameListenerAndParams.scale * ((float) i420Frame.rotatedWidth()));
                int rotatedHeight = (int) (frameListenerAndParams.scale * ((float) i420Frame.rotatedHeight()));
                if (rotatedWidth == 0 || rotatedHeight == 0) {
                    frameListenerAndParams.listener.onFrame(null);
                } else {
                    if (eglRenderer.bitmapTextureFramebuffer == null) {
                        eglRenderer.bitmapTextureFramebuffer = new GlTextureFrameBuffer(6408);
                    }
                    eglRenderer.bitmapTextureFramebuffer.setSize(rotatedWidth, rotatedHeight);
                    GLES20.glBindFramebuffer(36160, eglRenderer.bitmapTextureFramebuffer.getFrameBufferId());
                    GLES20.glFramebufferTexture2D(36160, 36064, 3553, eglRenderer.bitmapTextureFramebuffer.getTextureId(), 0);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    GLES20.glClear(16384);
                    if (i420Frame2.yuvFrame) {
                        i = 36160;
                        frameListenerAndParams.drawer.drawYuv(eglRenderer.yuvTextures, multiplyMatrices, i420Frame.rotatedWidth(), i420Frame.rotatedHeight(), 0, 0, rotatedWidth, rotatedHeight);
                    } else {
                        i = 36160;
                        frameListenerAndParams.drawer.drawOes(i420Frame2.textureId, multiplyMatrices, i420Frame.rotatedWidth(), i420Frame.rotatedHeight(), 0, 0, rotatedWidth, rotatedHeight);
                    }
                    ByteBuffer allocateDirect = ByteBuffer.allocateDirect(rotatedWidth * rotatedHeight * 4);
                    GLES20.glViewport(0, 0, rotatedWidth, rotatedHeight);
                    GLES20.glReadPixels(0, 0, rotatedWidth, rotatedHeight, 6408, 5121, allocateDirect);
                    GLES20.glBindFramebuffer(i, 0);
                    GlUtil.checkNoGLES2Error("EglRenderer.notifyCallbacks");
                    Bitmap createBitmap = Bitmap.createBitmap(rotatedWidth, rotatedHeight, Config.ARGB_8888);
                    createBitmap.copyPixelsFromBuffer(allocateDirect);
                    frameListenerAndParams.listener.onFrame(createBitmap);
                }
                eglRenderer = this;
            }
        }
    }

    private String averageTimeAsString(long j, int i) {
        if (i <= 0) {
            return "NA";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(TimeUnit.NANOSECONDS.toMicros(j / ((long) i)));
        sb.append(" μs");
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public void logStatistics() {
        long nanoTime = System.nanoTime();
        synchronized (this.statisticsLock) {
            long j = nanoTime - this.statisticsStartTimeNs;
            if (j > 0) {
                float nanos = ((float) (((long) this.framesRendered) * TimeUnit.SECONDS.toNanos(1))) / ((float) j);
                StringBuilder sb = new StringBuilder();
                sb.append("Duration: ");
                sb.append(TimeUnit.NANOSECONDS.toMillis(j));
                sb.append(" ms. Frames received: ");
                sb.append(this.framesReceived);
                sb.append(". Dropped: ");
                sb.append(this.framesDropped);
                sb.append(". Rendered: ");
                sb.append(this.framesRendered);
                sb.append(". Render fps: ");
                sb.append(String.format(Locale.US, "%.1f", new Object[]{Float.valueOf(nanos)}));
                sb.append(". Average render time: ");
                sb.append(averageTimeAsString(this.renderTimeNs, this.framesRendered));
                sb.append(". Average swapBuffer time: ");
                sb.append(averageTimeAsString(this.renderSwapBufferTimeNs, this.framesRendered));
                sb.append(".");
                logD(sb.toString());
                resetStatistics(nanoTime);
            }
        }
    }

    /* access modifiers changed from: private */
    public void logD(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append(str);
        Logging.m314d(str2, sb.toString());
    }
}
