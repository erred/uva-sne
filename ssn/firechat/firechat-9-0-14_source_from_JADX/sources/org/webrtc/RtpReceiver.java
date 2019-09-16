package org.webrtc;

import org.webrtc.MediaStreamTrack.MediaType;

public class RtpReceiver {
    private MediaStreamTrack cachedTrack;
    private long nativeObserver;
    final long nativeRtpReceiver;

    public interface Observer {
        void onFirstPacketReceived(MediaType mediaType);
    }

    private static native void free(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native long nativeGetTrack(long j);

    private static native String nativeId(long j);

    private static native long nativeSetObserver(long j, Observer observer);

    private static native boolean nativeSetParameters(long j, RtpParameters rtpParameters);

    private static native long nativeUnsetObserver(long j, long j2);

    public RtpReceiver(long j) {
        this.nativeRtpReceiver = j;
        this.cachedTrack = new MediaStreamTrack(nativeGetTrack(j));
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public boolean setParameters(RtpParameters rtpParameters) {
        return nativeSetParameters(this.nativeRtpReceiver, rtpParameters);
    }

    public RtpParameters getParameters() {
        return nativeGetParameters(this.nativeRtpReceiver);
    }

    /* renamed from: id */
    public String mo27989id() {
        return nativeId(this.nativeRtpReceiver);
    }

    public void dispose() {
        this.cachedTrack.dispose();
        if (this.nativeObserver != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, this.nativeObserver);
            this.nativeObserver = 0;
        }
        free(this.nativeRtpReceiver);
    }

    public void SetObserver(Observer observer) {
        if (this.nativeObserver != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, this.nativeObserver);
        }
        this.nativeObserver = nativeSetObserver(this.nativeRtpReceiver, observer);
    }
}
