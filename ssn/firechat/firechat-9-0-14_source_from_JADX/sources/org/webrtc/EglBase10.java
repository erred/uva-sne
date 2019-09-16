package org.webrtc;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public final class EglBase10 extends EglBase {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final EGL10 egl = ((EGL10) EGLContext.getEGL());
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay = getEglDisplay();
    private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;

    public static class Context extends org.webrtc.EglBase.Context {
        /* access modifiers changed from: private */
        public final EGLContext eglContext;

        public Context(EGLContext eGLContext) {
            this.eglContext = eGLContext;
        }
    }

    public EglBase10(Context context, int[] iArr) {
        this.eglConfig = getEglConfig(this.eglDisplay, iArr);
        this.eglContext = createEglContext(context, this.eglDisplay, this.eglConfig);
    }

    public void createSurface(Surface surface) {
        createSurfaceInternal(new SurfaceHolder(surface) {
            private final Surface surface;

            public void addCallback(Callback callback) {
            }

            public Rect getSurfaceFrame() {
                return null;
            }

            public boolean isCreating() {
                return false;
            }

            public Canvas lockCanvas() {
                return null;
            }

            public Canvas lockCanvas(Rect rect) {
                return null;
            }

            public void removeCallback(Callback callback) {
            }

            public void setFixedSize(int i, int i2) {
            }

            public void setFormat(int i) {
            }

            public void setKeepScreenOn(boolean z) {
            }

            public void setSizeFromLayout() {
            }

            @Deprecated
            public void setType(int i) {
            }

            public void unlockCanvasAndPost(Canvas canvas) {
            }

            {
                this.surface = r2;
            }

            public Surface getSurface() {
                return this.surface;
            }
        });
    }

    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture);
    }

    private void createSurfaceInternal(Object obj) {
        if ((obj instanceof SurfaceHolder) || (obj instanceof SurfaceTexture)) {
            checkIsNotReleased();
            if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("Already has an EGLSurface");
            }
            this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, obj, new int[]{12344});
            if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to create window surface: 0x");
                sb.append(Integer.toHexString(this.egl.eglGetError()));
                throw new RuntimeException(sb.toString());
            }
            return;
        }
        throw new IllegalStateException("Input must be either a SurfaceHolder or SurfaceTexture");
    }

    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    public void createPbufferSurface(int i, int i2) {
        checkIsNotReleased();
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        this.eglSurface = this.egl.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[]{12375, i, 12374, i2, 12344});
        if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to create pixel buffer surface with size ");
            sb.append(i);
            sb.append("x");
            sb.append(i2);
            sb.append(": 0x");
            sb.append(Integer.toHexString(this.egl.eglGetError()));
            throw new RuntimeException(sb.toString());
        }
    }

    public org.webrtc.EglBase.Context getEglBaseContext() {
        return new Context(this.eglContext);
    }

    public boolean hasSurface() {
        return this.eglSurface != EGL10.EGL_NO_SURFACE;
    }

    public int surfaceWidth() {
        int[] iArr = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, iArr);
        return iArr[0];
    }

    public int surfaceHeight() {
        int[] iArr = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, iArr);
        return iArr[0];
    }

    public void releaseSurface() {
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    private void checkIsNotReleased() {
        if (this.eglDisplay == EGL10.EGL_NO_DISPLAY || this.eglContext == EGL10.EGL_NO_CONTEXT || this.eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    public void release() {
        checkIsNotReleased();
        releaseSurface();
        detachCurrent();
        this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.egl.eglTerminate(this.eglDisplay);
        this.eglContext = EGL10.EGL_NO_CONTEXT;
        this.eglDisplay = EGL10.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    public void makeCurrent() {
        checkIsNotReleased();
        if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't make current");
        }
        synchronized (EglBase.lock) {
            if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                StringBuilder sb = new StringBuilder();
                sb.append("eglMakeCurrent failed: 0x");
                sb.append(Integer.toHexString(this.egl.eglGetError()));
                throw new RuntimeException(sb.toString());
            }
        }
    }

    public void detachCurrent() {
        synchronized (EglBase.lock) {
            if (!this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
                StringBuilder sb = new StringBuilder();
                sb.append("eglDetachCurrent failed: 0x");
                sb.append(Integer.toHexString(this.egl.eglGetError()));
                throw new RuntimeException(sb.toString());
            }
        }
    }

    public void swapBuffers() {
        checkIsNotReleased();
        if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't swap buffers");
        }
        synchronized (EglBase.lock) {
            this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
        }
    }

    private EGLDisplay getEglDisplay() {
        EGLDisplay eglGetDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to get EGL10 display: 0x");
            sb.append(Integer.toHexString(this.egl.eglGetError()));
            throw new RuntimeException(sb.toString());
        }
        if (this.egl.eglInitialize(eglGetDisplay, new int[2])) {
            return eglGetDisplay;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Unable to initialize EGL10: 0x");
        sb2.append(Integer.toHexString(this.egl.eglGetError()));
        throw new RuntimeException(sb2.toString());
    }

    private EGLConfig getEglConfig(EGLDisplay eGLDisplay, int[] iArr) {
        EGLConfig[] eGLConfigArr = new EGLConfig[1];
        int[] iArr2 = new int[1];
        if (!this.egl.eglChooseConfig(eGLDisplay, iArr, eGLConfigArr, eGLConfigArr.length, iArr2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("eglChooseConfig failed: 0x");
            sb.append(Integer.toHexString(this.egl.eglGetError()));
            throw new RuntimeException(sb.toString());
        } else if (iArr2[0] <= 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        } else {
            EGLConfig eGLConfig = eGLConfigArr[0];
            if (eGLConfig != null) {
                return eGLConfig;
            }
            throw new RuntimeException("eglChooseConfig returned null");
        }
    }

    private EGLContext createEglContext(Context context, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        EGLContext eGLContext;
        EGLContext eglCreateContext;
        if (context == null || context.eglContext != EGL10.EGL_NO_CONTEXT) {
            int[] iArr = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
            if (context == null) {
                eGLContext = EGL10.EGL_NO_CONTEXT;
            } else {
                eGLContext = context.eglContext;
            }
            synchronized (EglBase.lock) {
                eglCreateContext = this.egl.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
            }
            if (eglCreateContext != EGL10.EGL_NO_CONTEXT) {
                return eglCreateContext;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to create EGL context: 0x");
            sb.append(Integer.toHexString(this.egl.eglGetError()));
            throw new RuntimeException(sb.toString());
        }
        throw new RuntimeException("Invalid sharedContext");
    }
}
