package org.webrtc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import org.webrtc.CameraSession.CreateSessionCallback;
import org.webrtc.CameraSession.Events;
import org.webrtc.CameraSession.FailureType;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.CameraVideoCapturer.CameraStatistics;
import org.webrtc.CameraVideoCapturer.CameraSwitchHandler;
import org.webrtc.VideoCapturer.CapturerObserver;

abstract class CameraCapturer implements CameraVideoCapturer {
    private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
    private static final int OPEN_CAMERA_DELAY_MS = 500;
    private static final int OPEN_CAMERA_TIMEOUT = 10000;
    private static final String TAG = "CameraCapturer";
    /* access modifiers changed from: private */
    public Context applicationContext;
    /* access modifiers changed from: private */
    public final CameraEnumerator cameraEnumerator;
    /* access modifiers changed from: private */
    public String cameraName;
    /* access modifiers changed from: private */
    public final Events cameraSessionEventsHandler = new Events() {
        public void onCameraOpening() {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (CameraCapturer.this.currentSession != null) {
                    Logging.m318w(CameraCapturer.TAG, "onCameraOpening while session was open.");
                } else {
                    CameraCapturer.this.eventsHandler.onCameraOpening(CameraCapturer.this.cameraName);
                }
            }
        }

        public void onCameraError(CameraSession cameraSession, String str) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (cameraSession != CameraCapturer.this.currentSession) {
                    String str2 = CameraCapturer.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onCameraError from another session: ");
                    sb.append(str);
                    Logging.m318w(str2, sb.toString());
                    return;
                }
                CameraCapturer.this.eventsHandler.onCameraError(str);
                CameraCapturer.this.stopCapture();
            }
        }

        public void onCameraDisconnected(CameraSession cameraSession) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (cameraSession != CameraCapturer.this.currentSession) {
                    Logging.m318w(CameraCapturer.TAG, "onCameraDisconnected from another session.");
                    return;
                }
                CameraCapturer.this.eventsHandler.onCameraDisconnected();
                CameraCapturer.this.stopCapture();
            }
        }

        public void onCameraClosed(CameraSession cameraSession) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (cameraSession == CameraCapturer.this.currentSession || CameraCapturer.this.currentSession == null) {
                    CameraCapturer.this.eventsHandler.onCameraClosed();
                } else {
                    Logging.m314d(CameraCapturer.TAG, "onCameraClosed from another session.");
                }
            }
        }

        public void onByteBufferFrameCaptured(CameraSession cameraSession, byte[] bArr, int i, int i2, int i3, long j) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                if (cameraSession != CameraCapturer.this.currentSession) {
                    Logging.m318w(CameraCapturer.TAG, "onByteBufferFrameCaptured from another session.");
                    return;
                }
                if (!CameraCapturer.this.firstFrameObserved) {
                    CameraCapturer.this.eventsHandler.onFirstFrameAvailable();
                    CameraCapturer.this.firstFrameObserved = true;
                }
                CameraCapturer.this.cameraStatistics.addFrame();
                CameraCapturer.this.capturerObserver.onByteBufferFrameCaptured(bArr, i, i2, i3, j);
            }
        }

        /* JADX INFO: finally extract failed */
        public void onTextureFrameCaptured(CameraSession cameraSession, int i, int i2, int i3, float[] fArr, int i4, long j) {
            CameraCapturer.this.checkIsOnCameraThread();
            synchronized (CameraCapturer.this.stateLock) {
                try {
                    if (cameraSession != CameraCapturer.this.currentSession) {
                        Logging.m318w(CameraCapturer.TAG, "onTextureFrameCaptured from another session.");
                        CameraCapturer.this.surfaceHelper.returnTextureFrame();
                        return;
                    }
                    if (!CameraCapturer.this.firstFrameObserved) {
                        CameraCapturer.this.eventsHandler.onFirstFrameAvailable();
                        CameraCapturer.this.firstFrameObserved = true;
                    }
                    CameraCapturer.this.cameraStatistics.addFrame();
                    CameraCapturer.this.capturerObserver.onTextureFrameCaptured(i, i2, i3, fArr, i4, j);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public CameraStatistics cameraStatistics;
    private Handler cameraThreadHandler;
    /* access modifiers changed from: private */
    public CapturerObserver capturerObserver;
    /* access modifiers changed from: private */
    public final CreateSessionCallback createSessionCallback = new CreateSessionCallback() {
        public void onDone(CameraSession cameraSession) {
            CameraCapturer.this.checkIsOnCameraThread();
            Logging.m314d(CameraCapturer.TAG, "Create session done");
            CameraCapturer.this.uiThreadHandler.removeCallbacks(CameraCapturer.this.openCameraTimeoutRunnable);
            synchronized (CameraCapturer.this.stateLock) {
                CameraCapturer.this.capturerObserver.onCapturerStarted(true);
                CameraCapturer.this.sessionOpening = false;
                CameraCapturer.this.currentSession = cameraSession;
                CameraCapturer.this.cameraStatistics = new CameraStatistics(CameraCapturer.this.surfaceHelper, CameraCapturer.this.eventsHandler);
                CameraCapturer.this.firstFrameObserved = false;
                CameraCapturer.this.stateLock.notifyAll();
                if (CameraCapturer.this.switchState == SwitchState.IN_PROGRESS) {
                    if (CameraCapturer.this.switchEventsHandler != null) {
                        CameraCapturer.this.switchEventsHandler.onCameraSwitchDone(CameraCapturer.this.cameraEnumerator.isFrontFacing(CameraCapturer.this.cameraName));
                        CameraCapturer.this.switchEventsHandler = null;
                    }
                    CameraCapturer.this.switchState = SwitchState.IDLE;
                } else if (CameraCapturer.this.switchState == SwitchState.PENDING) {
                    CameraCapturer.this.switchState = SwitchState.IDLE;
                    CameraCapturer.this.switchCameraInternal(CameraCapturer.this.switchEventsHandler);
                }
            }
        }

        public void onFailure(FailureType failureType, String str) {
            CameraCapturer.this.checkIsOnCameraThread();
            CameraCapturer.this.uiThreadHandler.removeCallbacks(CameraCapturer.this.openCameraTimeoutRunnable);
            synchronized (CameraCapturer.this.stateLock) {
                CameraCapturer.this.capturerObserver.onCapturerStarted(false);
                CameraCapturer.this.openAttemptsRemaining = CameraCapturer.this.openAttemptsRemaining - 1;
                if (CameraCapturer.this.openAttemptsRemaining <= 0) {
                    String str2 = CameraCapturer.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Opening camera failed, passing: ");
                    sb.append(str);
                    Logging.m318w(str2, sb.toString());
                    CameraCapturer.this.sessionOpening = false;
                    CameraCapturer.this.stateLock.notifyAll();
                    if (CameraCapturer.this.switchState != SwitchState.IDLE) {
                        if (CameraCapturer.this.switchEventsHandler != null) {
                            CameraCapturer.this.switchEventsHandler.onCameraSwitchError(str);
                            CameraCapturer.this.switchEventsHandler = null;
                        }
                        CameraCapturer.this.switchState = SwitchState.IDLE;
                    }
                    if (failureType == FailureType.DISCONNECTED) {
                        CameraCapturer.this.eventsHandler.onCameraDisconnected();
                    } else {
                        CameraCapturer.this.eventsHandler.onCameraError(str);
                    }
                } else {
                    String str3 = CameraCapturer.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Opening camera failed, retry: ");
                    sb2.append(str);
                    Logging.m318w(str3, sb2.toString());
                    CameraCapturer.this.createSessionInternal(500);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public CameraSession currentSession;
    /* access modifiers changed from: private */
    public final CameraEventsHandler eventsHandler;
    /* access modifiers changed from: private */
    public boolean firstFrameObserved;
    /* access modifiers changed from: private */
    public int framerate;
    /* access modifiers changed from: private */
    public int height;
    /* access modifiers changed from: private */
    public int openAttemptsRemaining;
    /* access modifiers changed from: private */
    public final Runnable openCameraTimeoutRunnable = new Runnable() {
        public void run() {
            CameraCapturer.this.eventsHandler.onCameraError("Camera failed to start within timeout.");
        }
    };
    /* access modifiers changed from: private */
    public boolean sessionOpening;
    /* access modifiers changed from: private */
    public final Object stateLock = new Object();
    /* access modifiers changed from: private */
    public SurfaceTextureHelper surfaceHelper;
    /* access modifiers changed from: private */
    public CameraSwitchHandler switchEventsHandler;
    /* access modifiers changed from: private */
    public SwitchState switchState = SwitchState.IDLE;
    /* access modifiers changed from: private */
    public final Handler uiThreadHandler;
    /* access modifiers changed from: private */
    public int width;

    enum SwitchState {
        IDLE,
        PENDING,
        IN_PROGRESS
    }

    /* access modifiers changed from: protected */
    public abstract void createCameraSession(CreateSessionCallback createSessionCallback2, Events events, Context context, SurfaceTextureHelper surfaceTextureHelper, String str, int i, int i2, int i3);

    public boolean isScreencast() {
        return false;
    }

    public CameraCapturer(String str, CameraEventsHandler cameraEventsHandler, CameraEnumerator cameraEnumerator2) {
        if (cameraEventsHandler == null) {
            cameraEventsHandler = new CameraEventsHandler() {
                public void onCameraClosed() {
                }

                public void onCameraDisconnected() {
                }

                public void onCameraError(String str) {
                }

                public void onCameraFreezed(String str) {
                }

                public void onCameraOpening(String str) {
                }

                public void onFirstFrameAvailable() {
                }
            };
        }
        this.eventsHandler = cameraEventsHandler;
        this.cameraEnumerator = cameraEnumerator2;
        this.cameraName = str;
        this.uiThreadHandler = new Handler(Looper.getMainLooper());
        String[] deviceNames = cameraEnumerator2.getDeviceNames();
        if (deviceNames.length == 0) {
            throw new RuntimeException("No cameras attached.");
        } else if (!Arrays.asList(deviceNames).contains(this.cameraName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Camera name ");
            sb.append(this.cameraName);
            sb.append(" does not match any known camera device.");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver2) {
        Handler handler;
        this.applicationContext = context;
        this.capturerObserver = capturerObserver2;
        this.surfaceHelper = surfaceTextureHelper;
        if (surfaceTextureHelper == null) {
            handler = null;
        } else {
            handler = surfaceTextureHelper.getHandler();
        }
        this.cameraThreadHandler = handler;
    }

    public void startCapture(int i, int i2, int i3) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("startCapture: ");
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        sb.append("@");
        sb.append(i3);
        Logging.m314d(str, sb.toString());
        if (this.applicationContext == null || this.surfaceHelper == null) {
            throw new RuntimeException("CameraCapturer must be initialized before calling startCapture.");
        }
        synchronized (this.stateLock) {
            if (!this.sessionOpening) {
                if (this.currentSession == null) {
                    this.width = i;
                    this.height = i2;
                    this.framerate = i3;
                    this.sessionOpening = true;
                    this.openAttemptsRemaining = 3;
                    createSessionInternal(0);
                    return;
                }
            }
            Logging.m318w(TAG, "Session already open");
        }
    }

    /* access modifiers changed from: private */
    public void createSessionInternal(int i) {
        this.uiThreadHandler.postDelayed(this.openCameraTimeoutRunnable, (long) (i + OPEN_CAMERA_TIMEOUT));
        this.cameraThreadHandler.postDelayed(new Runnable() {
            public void run() {
                CameraCapturer.this.createCameraSession(CameraCapturer.this.createSessionCallback, CameraCapturer.this.cameraSessionEventsHandler, CameraCapturer.this.applicationContext, CameraCapturer.this.surfaceHelper, CameraCapturer.this.cameraName, CameraCapturer.this.width, CameraCapturer.this.height, CameraCapturer.this.framerate);
            }
        }, (long) i);
    }

    public void stopCapture() {
        Logging.m314d(TAG, "Stop capture");
        synchronized (this.stateLock) {
            while (this.sessionOpening) {
                Logging.m314d(TAG, "Stop capture: Waiting for session to open");
                ThreadUtils.waitUninterruptibly(this.stateLock);
            }
            if (this.currentSession != null) {
                Logging.m314d(TAG, "Stop capture: Nulling session");
                this.cameraStatistics.release();
                this.cameraStatistics = null;
                final CameraSession cameraSession = this.currentSession;
                this.cameraThreadHandler.post(new Runnable() {
                    public void run() {
                        cameraSession.stop();
                    }
                });
                this.currentSession = null;
                this.capturerObserver.onCapturerStopped();
            } else {
                Logging.m314d(TAG, "Stop capture: No session open");
            }
        }
        Logging.m314d(TAG, "Stop capture done");
    }

    public void changeCaptureFormat(int i, int i2, int i3) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("changeCaptureFormat: ");
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        sb.append("@");
        sb.append(i3);
        Logging.m314d(str, sb.toString());
        synchronized (this.stateLock) {
            stopCapture();
            startCapture(i, i2, i3);
        }
    }

    public void dispose() {
        Logging.m314d(TAG, "dispose");
        stopCapture();
    }

    public void switchCamera(final CameraSwitchHandler cameraSwitchHandler) {
        Logging.m314d(TAG, "switchCamera");
        this.cameraThreadHandler.post(new Runnable() {
            public void run() {
                CameraCapturer.this.switchCameraInternal(cameraSwitchHandler);
            }
        });
    }

    public void printStackTrace() {
        Thread thread = this.cameraThreadHandler != null ? this.cameraThreadHandler.getLooper().getThread() : null;
        if (thread != null) {
            StackTraceElement[] stackTrace = thread.getStackTrace();
            if (stackTrace.length > 0) {
                Logging.m314d(TAG, "CameraCapturer stack trace:");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.m314d(TAG, stackTraceElement.toString());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0031, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0049, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchCameraInternal(org.webrtc.CameraVideoCapturer.CameraSwitchHandler r6) {
        /*
            r5 = this;
            java.lang.String r0 = "CameraCapturer"
            java.lang.String r1 = "switchCamera internal"
            org.webrtc.Logging.m314d(r0, r1)
            org.webrtc.CameraEnumerator r0 = r5.cameraEnumerator
            java.lang.String[] r0 = r0.getDeviceNames()
            int r1 = r0.length
            r2 = 2
            if (r1 >= r2) goto L_0x0019
            if (r6 == 0) goto L_0x0018
            java.lang.String r0 = "No camera to switch to."
            r6.onCameraSwitchError(r0)
        L_0x0018:
            return
        L_0x0019:
            java.lang.Object r1 = r5.stateLock
            monitor-enter(r1)
            org.webrtc.CameraCapturer$SwitchState r2 = r5.switchState     // Catch:{ all -> 0x009a }
            org.webrtc.CameraCapturer$SwitchState r3 = org.webrtc.CameraCapturer.SwitchState.IDLE     // Catch:{ all -> 0x009a }
            if (r2 == r3) goto L_0x0032
            java.lang.String r0 = "CameraCapturer"
            java.lang.String r2 = "switchCamera switchInProgress"
            org.webrtc.Logging.m314d(r0, r2)     // Catch:{ all -> 0x009a }
            if (r6 == 0) goto L_0x0030
            java.lang.String r0 = "Camera switch already in progress."
            r6.onCameraSwitchError(r0)     // Catch:{ all -> 0x009a }
        L_0x0030:
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            return
        L_0x0032:
            boolean r2 = r5.sessionOpening     // Catch:{ all -> 0x009a }
            if (r2 != 0) goto L_0x004a
            org.webrtc.CameraSession r2 = r5.currentSession     // Catch:{ all -> 0x009a }
            if (r2 != 0) goto L_0x004a
            java.lang.String r0 = "CameraCapturer"
            java.lang.String r2 = "switchCamera: No session open"
            org.webrtc.Logging.m314d(r0, r2)     // Catch:{ all -> 0x009a }
            if (r6 == 0) goto L_0x0048
            java.lang.String r0 = "Camera is not running."
            r6.onCameraSwitchError(r0)     // Catch:{ all -> 0x009a }
        L_0x0048:
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            return
        L_0x004a:
            r5.switchEventsHandler = r6     // Catch:{ all -> 0x009a }
            boolean r6 = r5.sessionOpening     // Catch:{ all -> 0x009a }
            if (r6 == 0) goto L_0x0056
            org.webrtc.CameraCapturer$SwitchState r6 = org.webrtc.CameraCapturer.SwitchState.PENDING     // Catch:{ all -> 0x009a }
            r5.switchState = r6     // Catch:{ all -> 0x009a }
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            return
        L_0x0056:
            org.webrtc.CameraCapturer$SwitchState r6 = org.webrtc.CameraCapturer.SwitchState.IN_PROGRESS     // Catch:{ all -> 0x009a }
            r5.switchState = r6     // Catch:{ all -> 0x009a }
            java.lang.String r6 = "CameraCapturer"
            java.lang.String r2 = "switchCamera: Stopping session"
            org.webrtc.Logging.m314d(r6, r2)     // Catch:{ all -> 0x009a }
            org.webrtc.CameraVideoCapturer$CameraStatistics r6 = r5.cameraStatistics     // Catch:{ all -> 0x009a }
            r6.release()     // Catch:{ all -> 0x009a }
            r6 = 0
            r5.cameraStatistics = r6     // Catch:{ all -> 0x009a }
            org.webrtc.CameraSession r2 = r5.currentSession     // Catch:{ all -> 0x009a }
            android.os.Handler r3 = r5.cameraThreadHandler     // Catch:{ all -> 0x009a }
            org.webrtc.CameraCapturer$8 r4 = new org.webrtc.CameraCapturer$8     // Catch:{ all -> 0x009a }
            r4.<init>(r2)     // Catch:{ all -> 0x009a }
            r3.post(r4)     // Catch:{ all -> 0x009a }
            r5.currentSession = r6     // Catch:{ all -> 0x009a }
            java.util.List r6 = java.util.Arrays.asList(r0)     // Catch:{ all -> 0x009a }
            java.lang.String r2 = r5.cameraName     // Catch:{ all -> 0x009a }
            int r6 = r6.indexOf(r2)     // Catch:{ all -> 0x009a }
            r2 = 1
            int r6 = r6 + r2
            int r3 = r0.length     // Catch:{ all -> 0x009a }
            int r6 = r6 % r3
            r6 = r0[r6]     // Catch:{ all -> 0x009a }
            r5.cameraName = r6     // Catch:{ all -> 0x009a }
            r5.sessionOpening = r2     // Catch:{ all -> 0x009a }
            r5.openAttemptsRemaining = r2     // Catch:{ all -> 0x009a }
            r6 = 0
            r5.createSessionInternal(r6)     // Catch:{ all -> 0x009a }
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            java.lang.String r6 = "CameraCapturer"
            java.lang.String r0 = "switchCamera done"
            org.webrtc.Logging.m314d(r6, r0)
            return
        L_0x009a:
            r6 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x009a }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.CameraCapturer.switchCameraInternal(org.webrtc.CameraVideoCapturer$CameraSwitchHandler):void");
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            Logging.m315e(TAG, "Check is on camera thread failed.");
            throw new RuntimeException("Not on camera thread.");
        }
    }

    /* access modifiers changed from: protected */
    public String getCameraName() {
        String str;
        synchronized (this.stateLock) {
            str = this.cameraName;
        }
        return str;
    }
}
