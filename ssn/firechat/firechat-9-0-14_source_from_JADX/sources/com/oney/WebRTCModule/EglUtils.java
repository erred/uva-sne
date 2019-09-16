package com.oney.WebRTCModule;

import android.util.Log;
import org.webrtc.EglBase;
import org.webrtc.EglBase.Context;
import org.webrtc.EglBase10;
import org.webrtc.EglBase14;

public class EglUtils {
    private static EglBase rootEglBase;

    public static synchronized EglBase getRootEglBase() {
        EglBase eglBase;
        Throwable th;
        EglBase eglBase2;
        synchronized (EglUtils.class) {
            if (rootEglBase == null) {
                int[] iArr = EglBase.CONFIG_PLAIN;
                try {
                    eglBase2 = EglBase14.isEGL14Supported() ? new EglBase14(null, iArr) : null;
                    th = null;
                } catch (RuntimeException e) {
                    th = e;
                    eglBase2 = null;
                }
                if (eglBase2 == null) {
                    try {
                        eglBase2 = new EglBase10(null, iArr);
                    } catch (RuntimeException e2) {
                        th = e2;
                    }
                }
                if (th != null) {
                    Log.e(EglUtils.class.getName(), "Failed to create EglBase", th);
                } else {
                    rootEglBase = eglBase2;
                }
            }
            eglBase = rootEglBase;
        }
        return eglBase;
    }

    public static Context getRootEglBaseContext() {
        EglBase rootEglBase2 = getRootEglBase();
        if (rootEglBase2 == null) {
            return null;
        }
        return rootEglBase2.getEglBaseContext();
    }
}
