package org.webrtc;

public class RtpSender {
    private MediaStreamTrack cachedTrack;
    private final DtmfSender dtmfSender;
    final long nativeRtpSender;
    private boolean ownsTrack = true;

    private static native void free(long j);

    private static native long nativeGetDtmfSender(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native long nativeGetTrack(long j);

    private static native String nativeId(long j);

    private static native boolean nativeSetParameters(long j, RtpParameters rtpParameters);

    private static native boolean nativeSetTrack(long j, long j2);

    public RtpSender(long j) {
        this.nativeRtpSender = j;
        long nativeGetTrack = nativeGetTrack(j);
        DtmfSender dtmfSender2 = null;
        this.cachedTrack = nativeGetTrack != 0 ? new MediaStreamTrack(nativeGetTrack) : null;
        long nativeGetDtmfSender = nativeGetDtmfSender(j);
        if (nativeGetDtmfSender != 0) {
            dtmfSender2 = new DtmfSender(nativeGetDtmfSender);
        }
        this.dtmfSender = dtmfSender2;
    }

    public boolean setTrack(MediaStreamTrack mediaStreamTrack, boolean z) {
        if (!nativeSetTrack(this.nativeRtpSender, mediaStreamTrack == null ? 0 : mediaStreamTrack.nativeTrack)) {
            return false;
        }
        if (this.cachedTrack != null && this.ownsTrack) {
            this.cachedTrack.dispose();
        }
        this.cachedTrack = mediaStreamTrack;
        this.ownsTrack = z;
        return true;
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public boolean setParameters(RtpParameters rtpParameters) {
        return nativeSetParameters(this.nativeRtpSender, rtpParameters);
    }

    public RtpParameters getParameters() {
        return nativeGetParameters(this.nativeRtpSender);
    }

    /* renamed from: id */
    public String mo27996id() {
        return nativeId(this.nativeRtpSender);
    }

    public DtmfSender dtmf() {
        return this.dtmfSender;
    }

    public void dispose() {
        if (this.dtmfSender != null) {
            this.dtmfSender.dispose();
        }
        if (this.cachedTrack != null && this.ownsTrack) {
            this.cachedTrack.dispose();
        }
        free(this.nativeRtpSender);
    }
}
