package org.webrtc;

import org.webrtc.Logging.Severity;

public class CallSessionFileRotatingLogSink {
    private long nativeSink;

    private static native long nativeAddSink(String str, int i, int i2);

    private static native void nativeDeleteSink(long j);

    private static native byte[] nativeGetLogData(String str);

    static {
        System.loadLibrary("jingle_peerconnection_so");
    }

    public static byte[] getLogData(String str) {
        return nativeGetLogData(str);
    }

    public CallSessionFileRotatingLogSink(String str, int i, Severity severity) {
        this.nativeSink = nativeAddSink(str, i, severity.ordinal());
    }

    public void dispose() {
        if (this.nativeSink != 0) {
            nativeDeleteSink(this.nativeSink);
            this.nativeSink = 0;
        }
    }
}
