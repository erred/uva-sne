package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;
import com.facebook.imagepipeline.common.RotationOptions;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat.FramerateRange;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.CameraVideoCapturer.CameraStatistics;
import org.webrtc.SurfaceTextureHelper.OnTextureFrameAvailableListener;
import org.webrtc.VideoCapturer.CapturerObserver;

@Deprecated
public class VideoCapturerAndroid implements CameraVideoCapturer, PreviewCallback, OnTextureFrameAvailableListener {
    private static final int CAMERA_STOP_TIMEOUT_MS = 7000;
    private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    private static final int OPEN_CAMERA_DELAY_MS = 500;
    private static final String TAG = "VideoCapturerAndroid";
    private static final Histogram videoCapturerAndroidResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.VideoCapturerAndroid.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    private static final Histogram videoCapturerAndroidStartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.VideoCapturerAndroid.StartTimeMs", 1, 10000, 50);
    private static final Histogram videoCapturerAndroidStopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.VideoCapturerAndroid.StopTimeMs", 1, 10000, 50);
    private Context applicationContext;
    private Camera camera;
    private final ErrorCallback cameraErrorCallback = new ErrorCallback() {
        public void onError(int i, Camera camera) {
            String str;
            boolean z = VideoCapturerAndroid.this.isCameraRunning.get();
            if (i == 100) {
                str = "Camera server died!";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Camera error: ");
                sb.append(i);
                str = sb.toString();
            }
            String str2 = VideoCapturerAndroid.TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(". Camera running: ");
            sb2.append(z);
            Logging.m315e(str2, sb2.toString());
            if (VideoCapturerAndroid.this.eventsHandler == null) {
                return;
            }
            if (i != 2) {
                VideoCapturerAndroid.this.eventsHandler.onCameraError(str);
            } else if (z) {
                VideoCapturerAndroid.this.eventsHandler.onCameraDisconnected();
            } else {
                Logging.m314d(VideoCapturerAndroid.TAG, "Ignore CAMERA_ERROR_EVICTED for closed camera.");
            }
        }
    };
    private final Object cameraIdLock = new Object();
    private CameraStatistics cameraStatistics;
    private volatile Handler cameraThreadHandler;
    private CaptureFormat captureFormat;
    /* access modifiers changed from: private */
    public final CameraEventsHandler eventsHandler;
    private boolean firstFrameReported;
    private CapturerObserver frameObserver = null;

    /* renamed from: id */
    private int f174id;
    /* access modifiers changed from: private */
    public CameraInfo info;
    /* access modifiers changed from: private */
    public final AtomicBoolean isCameraRunning = new AtomicBoolean();
    private final boolean isCapturingToTexture;
    /* access modifiers changed from: private */
    public int openCameraAttempts;
    /* access modifiers changed from: private */
    public volatile boolean pendingCameraSwitch;
    /* access modifiers changed from: private */
    public final Object pendingCameraSwitchLock = new Object();
    private final Set<byte[]> queuedBuffers = new HashSet();
    private int requestedFramerate;
    private int requestedHeight;
    private int requestedWidth;
    private long startStartTimeNs;
    private SurfaceTextureHelper surfaceHelper;

    public boolean isScreencast() {
        return false;
    }

    public static VideoCapturerAndroid create(String str, CameraEventsHandler cameraEventsHandler) {
        return create(str, cameraEventsHandler, false);
    }

    @Deprecated
    public static VideoCapturerAndroid create(String str, CameraEventsHandler cameraEventsHandler, boolean z) {
        try {
            return new VideoCapturerAndroid(str, cameraEventsHandler, z);
        } catch (RuntimeException e) {
            Logging.m316e(TAG, "Couldn't create camera.", e);
            return null;
        }
    }

    public void printStackTrace() {
        Thread thread = this.cameraThreadHandler != null ? this.cameraThreadHandler.getLooper().getThread() : null;
        if (thread != null) {
            StackTraceElement[] stackTrace = thread.getStackTrace();
            if (stackTrace.length > 0) {
                Logging.m314d(TAG, "VideoCapturerAndroid stacks trace:");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.m314d(TAG, stackTraceElement.toString());
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        if (maybePostOnCameraThread(new org.webrtc.VideoCapturerAndroid.C31962(r3)) != false) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0035, code lost:
        r0 = r3.pendingCameraSwitchLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0037, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r3.pendingCameraSwitch = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003b, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003c, code lost:
        if (r4 == null) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003e, code lost:
        r4.onCameraSwitchError("Camera is stopped.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0047, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchCamera(final org.webrtc.CameraVideoCapturer.CameraSwitchHandler r4) {
        /*
            r3 = this;
            int r0 = android.hardware.Camera.getNumberOfCameras()
            r1 = 2
            if (r0 >= r1) goto L_0x000f
            if (r4 == 0) goto L_0x000e
            java.lang.String r0 = "No camera to switch to."
            r4.onCameraSwitchError(r0)
        L_0x000e:
            return
        L_0x000f:
            java.lang.Object r0 = r3.pendingCameraSwitchLock
            monitor-enter(r0)
            boolean r1 = r3.pendingCameraSwitch     // Catch:{ all -> 0x0048 }
            if (r1 == 0) goto L_0x0026
            java.lang.String r1 = "VideoCapturerAndroid"
            java.lang.String r2 = "Ignoring camera switch request."
            org.webrtc.Logging.m318w(r1, r2)     // Catch:{ all -> 0x0048 }
            if (r4 == 0) goto L_0x0024
            java.lang.String r1 = "Pending camera switch already in progress."
            r4.onCameraSwitchError(r1)     // Catch:{ all -> 0x0048 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0026:
            r1 = 1
            r3.pendingCameraSwitch = r1     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            org.webrtc.VideoCapturerAndroid$2 r0 = new org.webrtc.VideoCapturerAndroid$2
            r0.<init>(r4)
            boolean r0 = r3.maybePostOnCameraThread(r0)
            if (r0 != 0) goto L_0x0047
            java.lang.Object r0 = r3.pendingCameraSwitchLock
            monitor-enter(r0)
            r1 = 0
            r3.pendingCameraSwitch = r1     // Catch:{ all -> 0x0044 }
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            if (r4 == 0) goto L_0x0047
            java.lang.String r0 = "Camera is stopped."
            r4.onCameraSwitchError(r0)
            goto L_0x0047
        L_0x0044:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            throw r4
        L_0x0047:
            return
        L_0x0048:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.VideoCapturerAndroid.switchCamera(org.webrtc.CameraVideoCapturer$CameraSwitchHandler):void");
    }

    public void changeCaptureFormat(final int i, final int i2, final int i3) {
        maybePostOnCameraThread(new Runnable() {
            public void run() {
                VideoCapturerAndroid.this.startPreviewOnCameraThread(i, i2, i3);
            }
        });
    }

    private int getCurrentCameraId() {
        int i;
        synchronized (this.cameraIdLock) {
            i = this.f174id;
        }
        return i;
    }

    public boolean isCapturingToTexture() {
        return this.isCapturingToTexture;
    }

    public VideoCapturerAndroid(String str, CameraEventsHandler cameraEventsHandler, boolean z) {
        if (Camera.getNumberOfCameras() == 0) {
            throw new RuntimeException("No cameras available");
        }
        if (str == null || str.equals("")) {
            this.f174id = 0;
        } else {
            this.f174id = Camera1Enumerator.getCameraIndex(str);
        }
        this.eventsHandler = cameraEventsHandler;
        this.isCapturingToTexture = z;
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("VideoCapturerAndroid isCapturingToTexture : ");
        sb.append(this.isCapturingToTexture);
        Logging.m314d(str2, sb.toString());
    }

    private void checkIsOnCameraThread() {
        if (this.cameraThreadHandler == null) {
            Logging.m315e(TAG, "Camera is not initialized - can't check thread.");
        } else if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }

    private boolean maybePostOnCameraThread(Runnable runnable) {
        return maybePostDelayedOnCameraThread(0, runnable);
    }

    private boolean maybePostDelayedOnCameraThread(int i, Runnable runnable) {
        return this.cameraThreadHandler != null && this.isCameraRunning.get() && this.cameraThreadHandler.postAtTime(runnable, this, SystemClock.uptimeMillis() + ((long) i));
    }

    public void dispose() {
        Logging.m314d(TAG, "dispose");
    }

    private boolean isInitialized() {
        return (this.applicationContext == null || this.frameObserver == null) ? false : true;
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        Handler handler;
        Logging.m314d(TAG, "initialize");
        if (context == null) {
            throw new IllegalArgumentException("applicationContext not set.");
        } else if (capturerObserver == null) {
            throw new IllegalArgumentException("frameObserver not set.");
        } else if (isInitialized()) {
            throw new IllegalStateException("Already initialized");
        } else {
            this.applicationContext = context;
            this.frameObserver = capturerObserver;
            this.surfaceHelper = surfaceTextureHelper;
            if (surfaceTextureHelper == null) {
                handler = null;
            } else {
                handler = surfaceTextureHelper.getHandler();
            }
            this.cameraThreadHandler = handler;
        }
    }

    public void startCapture(final int i, final int i2, final int i3) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("startCapture requested: ");
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        sb.append("@");
        sb.append(i3);
        Logging.m314d(str, sb.toString());
        if (!isInitialized()) {
            throw new IllegalStateException("startCapture called in uninitialized state");
        } else if (this.surfaceHelper == null) {
            this.frameObserver.onCapturerStarted(false);
            if (this.eventsHandler != null) {
                this.eventsHandler.onCameraError("No SurfaceTexture created.");
            }
        } else if (this.isCameraRunning.getAndSet(true)) {
            Logging.m315e(TAG, "Camera has already been started.");
        } else {
            if (!maybePostOnCameraThread(new Runnable() {
                public void run() {
                    VideoCapturerAndroid.this.openCameraAttempts = 0;
                    VideoCapturerAndroid.this.startCaptureOnCameraThread(i, i2, i3);
                }
            })) {
                this.frameObserver.onCapturerStarted(false);
                if (this.eventsHandler != null) {
                    this.eventsHandler.onCameraError("Could not post task to camera thread.");
                }
                this.isCameraRunning.set(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00c2, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00e2, code lost:
        org.webrtc.Logging.m316e(TAG, "startCapture failed", r7);
        stopCaptureOnCameraThread(true);
        r6.frameObserver.onCapturerStarted(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00f3, code lost:
        if (r6.eventsHandler != null) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00f5, code lost:
        r6.eventsHandler.onCameraError("Camera can not be started.");
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00c2 A[ExcHandler: IOException (r7v1 'e' java.io.IOException A[CUSTOM_DECLARE]), Splitter:B:19:0x006a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startCaptureOnCameraThread(final int r7, final int r8, final int r9) {
        /*
            r6 = this;
            r6.checkIsOnCameraThread()
            long r0 = java.lang.System.nanoTime()
            r6.startStartTimeNs = r0
            java.util.concurrent.atomic.AtomicBoolean r0 = r6.isCameraRunning
            boolean r0 = r0.get()
            if (r0 != 0) goto L_0x0019
            java.lang.String r7 = "VideoCapturerAndroid"
            java.lang.String r8 = "startCaptureOnCameraThread: Camera is stopped"
            org.webrtc.Logging.m315e(r7, r8)
            return
        L_0x0019:
            android.hardware.Camera r0 = r6.camera
            if (r0 == 0) goto L_0x0025
            java.lang.String r7 = "VideoCapturerAndroid"
            java.lang.String r8 = "startCaptureOnCameraThread: Camera has already been started."
            org.webrtc.Logging.m315e(r7, r8)
            return
        L_0x0025:
            r0 = 0
            r6.firstFrameReported = r0
            r1 = 1
            java.lang.Object r2 = r6.cameraIdLock     // Catch:{ RuntimeException -> 0x00c4 }
            monitor-enter(r2)     // Catch:{ RuntimeException -> 0x00c4 }
            java.lang.String r3 = "VideoCapturerAndroid"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bf }
            r4.<init>()     // Catch:{ all -> 0x00bf }
            java.lang.String r5 = "Opening camera "
            r4.append(r5)     // Catch:{ all -> 0x00bf }
            int r5 = r6.f174id     // Catch:{ all -> 0x00bf }
            r4.append(r5)     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00bf }
            org.webrtc.Logging.m314d(r3, r4)     // Catch:{ all -> 0x00bf }
            org.webrtc.CameraVideoCapturer$CameraEventsHandler r3 = r6.eventsHandler     // Catch:{ all -> 0x00bf }
            if (r3 == 0) goto L_0x0053
            org.webrtc.CameraVideoCapturer$CameraEventsHandler r3 = r6.eventsHandler     // Catch:{ all -> 0x00bf }
            int r4 = r6.f174id     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = org.webrtc.Camera1Enumerator.getDeviceName(r4)     // Catch:{ all -> 0x00bf }
            r3.onCameraOpening(r4)     // Catch:{ all -> 0x00bf }
        L_0x0053:
            int r3 = r6.f174id     // Catch:{ all -> 0x00bf }
            android.hardware.Camera r3 = android.hardware.Camera.open(r3)     // Catch:{ all -> 0x00bf }
            r6.camera = r3     // Catch:{ all -> 0x00bf }
            android.hardware.Camera$CameraInfo r3 = new android.hardware.Camera$CameraInfo     // Catch:{ all -> 0x00bf }
            r3.<init>()     // Catch:{ all -> 0x00bf }
            r6.info = r3     // Catch:{ all -> 0x00bf }
            int r3 = r6.f174id     // Catch:{ all -> 0x00bf }
            android.hardware.Camera$CameraInfo r4 = r6.info     // Catch:{ all -> 0x00bf }
            android.hardware.Camera.getCameraInfo(r3, r4)     // Catch:{ all -> 0x00bf }
            monitor-exit(r2)     // Catch:{ all -> 0x00bf }
            android.hardware.Camera r2 = r6.camera     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            org.webrtc.SurfaceTextureHelper r3 = r6.surfaceHelper     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            android.graphics.SurfaceTexture r3 = r3.getSurfaceTexture()     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r2.setPreviewTexture(r3)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            java.lang.String r2 = "VideoCapturerAndroid"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r3.<init>()     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            java.lang.String r4 = "Camera orientation: "
            r3.append(r4)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            android.hardware.Camera$CameraInfo r4 = r6.info     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            int r4 = r4.orientation     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r3.append(r4)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            java.lang.String r4 = " .Device orientation: "
            r3.append(r4)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            int r4 = r6.getDeviceOrientation()     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r3.append(r4)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            org.webrtc.Logging.m314d(r2, r3)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            android.hardware.Camera r2 = r6.camera     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            android.hardware.Camera$ErrorCallback r3 = r6.cameraErrorCallback     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r2.setErrorCallback(r3)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r6.startPreviewOnCameraThread(r7, r8, r9)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            org.webrtc.VideoCapturer$CapturerObserver r7 = r6.frameObserver     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r7.onCapturerStarted(r1)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            boolean r7 = r6.isCapturingToTexture     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            if (r7 == 0) goto L_0x00b3
            org.webrtc.SurfaceTextureHelper r7 = r6.surfaceHelper     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r7.startListening(r6)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
        L_0x00b3:
            org.webrtc.CameraVideoCapturer$CameraStatistics r7 = new org.webrtc.CameraVideoCapturer$CameraStatistics     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            org.webrtc.SurfaceTextureHelper r8 = r6.surfaceHelper     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            org.webrtc.CameraVideoCapturer$CameraEventsHandler r9 = r6.eventsHandler     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r7.<init>(r8, r9)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r6.cameraStatistics = r7     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            goto L_0x00fc
        L_0x00bf:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00bf }
            throw r3     // Catch:{ RuntimeException -> 0x00c4 }
        L_0x00c2:
            r7 = move-exception
            goto L_0x00e2
        L_0x00c4:
            r2 = move-exception
            int r3 = r6.openCameraAttempts     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            int r3 = r3 + r1
            r6.openCameraAttempts = r3     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            int r3 = r6.openCameraAttempts     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r4 = 3
            if (r3 >= r4) goto L_0x00e1
            java.lang.String r3 = "VideoCapturerAndroid"
            java.lang.String r4 = "Camera.open failed, retrying"
            org.webrtc.Logging.m316e(r3, r4, r2)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r2 = 500(0x1f4, float:7.0E-43)
            org.webrtc.VideoCapturerAndroid$5 r3 = new org.webrtc.VideoCapturerAndroid$5     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r3.<init>(r7, r8, r9)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            r6.maybePostDelayedOnCameraThread(r2, r3)     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
            return
        L_0x00e1:
            throw r2     // Catch:{ IOException -> 0x00c2, IOException -> 0x00c2 }
        L_0x00e2:
            java.lang.String r8 = "VideoCapturerAndroid"
            java.lang.String r9 = "startCapture failed"
            org.webrtc.Logging.m316e(r8, r9, r7)
            r6.stopCaptureOnCameraThread(r1)
            org.webrtc.VideoCapturer$CapturerObserver r7 = r6.frameObserver
            r7.onCapturerStarted(r0)
            org.webrtc.CameraVideoCapturer$CameraEventsHandler r7 = r6.eventsHandler
            if (r7 == 0) goto L_0x00fc
            org.webrtc.CameraVideoCapturer$CameraEventsHandler r7 = r6.eventsHandler
            java.lang.String r8 = "Camera can not be started."
            r7.onCameraError(r8)
        L_0x00fc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.VideoCapturerAndroid.startCaptureOnCameraThread(int, int, int):void");
    }

    /* access modifiers changed from: private */
    public void startPreviewOnCameraThread(int i, int i2, int i3) {
        checkIsOnCameraThread();
        if (!this.isCameraRunning.get() || this.camera == null) {
            Logging.m315e(TAG, "startPreviewOnCameraThread: Camera is stopped");
            return;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("startPreviewOnCameraThread requested: ");
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        sb.append("@");
        sb.append(i3);
        Logging.m314d(str, sb.toString());
        this.requestedWidth = i;
        this.requestedHeight = i2;
        this.requestedFramerate = i3;
        Parameters parameters = this.camera.getParameters();
        List convertFramerates = Camera1Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Available fps ranges: ");
        sb2.append(convertFramerates);
        Logging.m314d(str2, sb2.toString());
        FramerateRange closestSupportedFramerateRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(convertFramerates, i3);
        List convertSizes = Camera1Enumerator.convertSizes(parameters.getSupportedPreviewSizes());
        Size closestSupportedSize = CameraEnumerationAndroid.getClosestSupportedSize(convertSizes, i, i2);
        CameraEnumerationAndroid.reportCameraResolution(videoCapturerAndroidResolutionHistogram, closestSupportedSize);
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Available preview sizes: ");
        sb3.append(convertSizes);
        Logging.m314d(str3, sb3.toString());
        CaptureFormat captureFormat2 = new CaptureFormat(closestSupportedSize.width, closestSupportedSize.height, closestSupportedFramerateRange);
        if (!captureFormat2.equals(this.captureFormat)) {
            String str4 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("isVideoStabilizationSupported: ");
            sb4.append(parameters.isVideoStabilizationSupported());
            Logging.m314d(str4, sb4.toString());
            if (parameters.isVideoStabilizationSupported()) {
                parameters.setVideoStabilization(true);
            }
            if (captureFormat2.framerate.max > 0) {
                parameters.setPreviewFpsRange(captureFormat2.framerate.min, captureFormat2.framerate.max);
            }
            parameters.setPreviewSize(closestSupportedSize.width, closestSupportedSize.height);
            if (!this.isCapturingToTexture) {
                captureFormat2.getClass();
                parameters.setPreviewFormat(17);
            }
            Size closestSupportedSize2 = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPictureSizes()), i, i2);
            parameters.setPictureSize(closestSupportedSize2.width, closestSupportedSize2.height);
            if (this.captureFormat != null) {
                this.camera.stopPreview();
                this.camera.setPreviewCallbackWithBuffer(null);
            }
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                Logging.m314d(TAG, "Enable continuous auto focus mode.");
                parameters.setFocusMode("continuous-video");
            }
            String str5 = TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Start capturing: ");
            sb5.append(captureFormat2);
            Logging.m314d(str5, sb5.toString());
            this.captureFormat = captureFormat2;
            this.camera.setParameters(parameters);
            this.camera.setDisplayOrientation(0);
            if (!this.isCapturingToTexture) {
                this.queuedBuffers.clear();
                int frameSize = captureFormat2.frameSize();
                for (int i4 = 0; i4 < 3; i4++) {
                    ByteBuffer allocateDirect = ByteBuffer.allocateDirect(frameSize);
                    this.queuedBuffers.add(allocateDirect.array());
                    this.camera.addCallbackBuffer(allocateDirect.array());
                }
                this.camera.setPreviewCallbackWithBuffer(this);
            }
            this.camera.startPreview();
        }
    }

    public void stopCapture() throws InterruptedException {
        Logging.m314d(TAG, "stopCapture");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if (!maybePostOnCameraThread(new Runnable() {
            public void run() {
                VideoCapturerAndroid.this.stopCaptureOnCameraThread(true);
                countDownLatch.countDown();
            }
        })) {
            Logging.m315e(TAG, "Calling stopCapture() for already stopped camera.");
            return;
        }
        if (!countDownLatch.await(7000, TimeUnit.MILLISECONDS)) {
            Logging.m315e(TAG, "Camera stop timeout");
            printStackTrace();
            if (this.eventsHandler != null) {
                this.eventsHandler.onCameraError("Camera stop timeout");
            }
        }
        this.frameObserver.onCapturerStopped();
        Logging.m314d(TAG, "stopCapture done");
    }

    /* access modifiers changed from: private */
    public void stopCaptureOnCameraThread(boolean z) {
        checkIsOnCameraThread();
        Logging.m314d(TAG, "stopCaptureOnCameraThread");
        long nanoTime = System.nanoTime();
        if (this.surfaceHelper != null) {
            this.surfaceHelper.stopListening();
        }
        if (z) {
            this.isCameraRunning.set(false);
            this.cameraThreadHandler.removeCallbacksAndMessages(this);
        }
        if (this.cameraStatistics != null) {
            this.cameraStatistics.release();
            this.cameraStatistics = null;
        }
        Logging.m314d(TAG, "Stop preview.");
        if (this.camera != null) {
            this.camera.stopPreview();
            this.camera.setPreviewCallbackWithBuffer(null);
        }
        this.queuedBuffers.clear();
        this.captureFormat = null;
        Logging.m314d(TAG, "Release camera.");
        if (this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
        if (this.eventsHandler != null) {
            this.eventsHandler.onCameraClosed();
        }
        videoCapturerAndroidStopTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime));
        Logging.m314d(TAG, "stopCaptureOnCameraThread done");
    }

    /* access modifiers changed from: private */
    public void switchCameraOnCameraThread() {
        checkIsOnCameraThread();
        if (!this.isCameraRunning.get()) {
            Logging.m315e(TAG, "switchCameraOnCameraThread: Camera is stopped");
            return;
        }
        Logging.m314d(TAG, "switchCameraOnCameraThread");
        stopCaptureOnCameraThread(false);
        synchronized (this.cameraIdLock) {
            this.f174id = (this.f174id + 1) % Camera.getNumberOfCameras();
        }
        startCaptureOnCameraThread(this.requestedWidth, this.requestedHeight, this.requestedFramerate);
        Logging.m314d(TAG, "switchCameraOnCameraThread done");
    }

    private int getDeviceOrientation() {
        switch (((WindowManager) this.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 1:
                return 90;
            case 2:
                return RotationOptions.ROTATE_180;
            case 3:
                return RotationOptions.ROTATE_270;
            default:
                return 0;
        }
    }

    private int getFrameOrientation() {
        int deviceOrientation = getDeviceOrientation();
        if (this.info.facing == 0) {
            deviceOrientation = 360 - deviceOrientation;
        }
        return (this.info.orientation + deviceOrientation) % 360;
    }

    public void onPreviewFrame(byte[] bArr, Camera camera2) {
        checkIsOnCameraThread();
        if (!this.isCameraRunning.get()) {
            Logging.m315e(TAG, "onPreviewFrame: Camera is stopped");
        } else if (this.queuedBuffers.contains(bArr)) {
            if (this.camera != camera2) {
                throw new RuntimeException("Unexpected camera in callback!");
            }
            long nanos = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
            if (!this.firstFrameReported) {
                onFirstFrameAvailable();
            }
            this.cameraStatistics.addFrame();
            this.frameObserver.onByteBufferFrameCaptured(bArr, this.captureFormat.width, this.captureFormat.height, getFrameOrientation(), nanos);
            this.camera.addCallbackBuffer(bArr);
        }
    }

    public void onTextureFrameAvailable(int i, float[] fArr, long j) {
        checkIsOnCameraThread();
        if (!this.isCameraRunning.get()) {
            Logging.m315e(TAG, "onTextureFrameAvailable: Camera is stopped");
            this.surfaceHelper.returnTextureFrame();
            return;
        }
        int frameOrientation = getFrameOrientation();
        if (this.info.facing == 1) {
            fArr = RendererCommon.multiplyMatrices(fArr, RendererCommon.horizontalFlipMatrix());
        }
        float[] fArr2 = fArr;
        if (!this.firstFrameReported) {
            onFirstFrameAvailable();
        }
        this.cameraStatistics.addFrame();
        this.frameObserver.onTextureFrameCaptured(this.captureFormat.width, this.captureFormat.height, i, fArr2, frameOrientation, j);
    }

    private void onFirstFrameAvailable() {
        if (this.eventsHandler != null) {
            this.eventsHandler.onFirstFrameAvailable();
        }
        videoCapturerAndroidStartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.startStartTimeNs));
        this.firstFrameReported = true;
    }
}
