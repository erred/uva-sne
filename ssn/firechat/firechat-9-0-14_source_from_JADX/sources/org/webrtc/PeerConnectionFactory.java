package org.webrtc;

import java.util.List;
import org.webrtc.EglBase.Context;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.RTCConfiguration;
import org.webrtc.VideoCapturer.AndroidVideoTrackSourceObserver;
import org.webrtc.VideoCapturer.CapturerObserver;

public class PeerConnectionFactory {
    private static final String TAG = "PeerConnectionFactory";
    private static volatile boolean nativeLibLoaded;
    private static Thread networkThread;
    private static Thread signalingThread;
    private static Thread workerThread;
    private EglBase localEglbase;
    private final long nativeFactory;
    private EglBase remoteEglbase;

    public static class Options {
        static final int ADAPTER_TYPE_CELLULAR = 4;
        static final int ADAPTER_TYPE_ETHERNET = 1;
        static final int ADAPTER_TYPE_LOOPBACK = 16;
        static final int ADAPTER_TYPE_UNKNOWN = 0;
        static final int ADAPTER_TYPE_VPN = 8;
        static final int ADAPTER_TYPE_WIFI = 2;
        public boolean disableEncryption;
        public boolean disableNetworkMonitor;
        public int networkIgnoreMask;
    }

    public static native boolean initializeAndroidGlobals(Object obj, boolean z, boolean z2, boolean z3);

    public static native void initializeFieldTrials(String str);

    public static native void initializeInternalTracer();

    private static native long nativeCreateAudioSource(long j, MediaConstraints mediaConstraints);

    private static native long nativeCreateAudioTrack(long j, String str, long j2);

    private static native long nativeCreateLocalMediaStream(long j, String str);

    private static native long nativeCreateObserver(Observer observer);

    private static native long nativeCreatePeerConnection(long j, RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, long j2);

    private static native long nativeCreatePeerConnectionFactory(Options options);

    private static native long nativeCreateVideoSource(long j, Context context, boolean z);

    private static native long nativeCreateVideoTrack(long j, String str, long j2);

    private static native String nativeFieldTrialsFindFullName(String str);

    private static native void nativeFreeFactory(long j);

    private static native void nativeInitializeVideoCapturer(long j, VideoCapturer videoCapturer, long j2, CapturerObserver capturerObserver);

    private static native void nativeSetVideoHwAccelerationOptions(long j, Object obj, Object obj2);

    private static native boolean nativeStartAecDump(long j, int i, int i2);

    private static native void nativeStopAecDump(long j);

    private static native void nativeThreadsCallbacks(long j);

    public static native void shutdownInternalTracer();

    public static native boolean startInternalTracingCapture(String str);

    public static native void stopInternalTracingCapture();

    @Deprecated
    public native void nativeSetOptions(long j, Options options);

    static {
        try {
            System.loadLibrary("jingle_peerconnection_so");
            nativeLibLoaded = true;
        } catch (UnsatisfiedLinkError unused) {
            nativeLibLoaded = false;
        }
    }

    public static String fieldTrialsFindFullName(String str) {
        return nativeLibLoaded ? nativeFieldTrialsFindFullName(str) : "";
    }

    @Deprecated
    public PeerConnectionFactory() {
        this(null);
    }

    public PeerConnectionFactory(Options options) {
        this.nativeFactory = nativeCreatePeerConnectionFactory(options);
        if (this.nativeFactory == 0) {
            throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
        }
    }

    public PeerConnection createPeerConnection(RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, Observer observer) {
        long nativeCreateObserver = nativeCreateObserver(observer);
        if (nativeCreateObserver == 0) {
            return null;
        }
        long nativeCreatePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rTCConfiguration, mediaConstraints, nativeCreateObserver);
        if (nativeCreatePeerConnection == 0) {
            return null;
        }
        return new PeerConnection(nativeCreatePeerConnection, nativeCreateObserver);
    }

    public PeerConnection createPeerConnection(List<IceServer> list, MediaConstraints mediaConstraints, Observer observer) {
        return createPeerConnection(new RTCConfiguration(list), mediaConstraints, observer);
    }

    public MediaStream createLocalMediaStream(String str) {
        return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, str));
    }

    public VideoSource createVideoSource(VideoCapturer videoCapturer) {
        Context context;
        if (this.localEglbase == null) {
            context = null;
        } else {
            context = this.localEglbase.getEglBaseContext();
        }
        long nativeCreateVideoSource = nativeCreateVideoSource(this.nativeFactory, context, videoCapturer.isScreencast());
        nativeInitializeVideoCapturer(this.nativeFactory, videoCapturer, nativeCreateVideoSource, new AndroidVideoTrackSourceObserver(nativeCreateVideoSource));
        return new VideoSource(nativeCreateVideoSource);
    }

    public VideoTrack createVideoTrack(String str, VideoSource videoSource) {
        return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, str, videoSource.nativeSource));
    }

    public AudioSource createAudioSource(MediaConstraints mediaConstraints) {
        return new AudioSource(nativeCreateAudioSource(this.nativeFactory, mediaConstraints));
    }

    public AudioTrack createAudioTrack(String str, AudioSource audioSource) {
        return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, str, audioSource.nativeSource));
    }

    public boolean startAecDump(int i, int i2) {
        return nativeStartAecDump(this.nativeFactory, i, i2);
    }

    public void stopAecDump() {
        nativeStopAecDump(this.nativeFactory);
    }

    @Deprecated
    public void setOptions(Options options) {
        nativeSetOptions(this.nativeFactory, options);
    }

    public void setVideoHwAccelerationOptions(Context context, Context context2) {
        if (this.localEglbase != null) {
            Logging.m318w(TAG, "Egl context already set.");
            this.localEglbase.release();
        }
        if (this.remoteEglbase != null) {
            Logging.m318w(TAG, "Egl context already set.");
            this.remoteEglbase.release();
        }
        this.localEglbase = EglBase.create(context);
        this.remoteEglbase = EglBase.create(context2);
        nativeSetVideoHwAccelerationOptions(this.nativeFactory, this.localEglbase.getEglBaseContext(), this.remoteEglbase.getEglBaseContext());
    }

    public void dispose() {
        nativeFreeFactory(this.nativeFactory);
        networkThread = null;
        workerThread = null;
        signalingThread = null;
        if (this.localEglbase != null) {
            this.localEglbase.release();
        }
        if (this.remoteEglbase != null) {
            this.remoteEglbase.release();
        }
    }

    public void threadsCallbacks() {
        nativeThreadsCallbacks(this.nativeFactory);
    }

    private static void printStackTrace(Thread thread, String str) {
        if (thread != null) {
            StackTraceElement[] stackTrace = thread.getStackTrace();
            if (stackTrace.length > 0) {
                String str2 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(" stacks trace:");
                Logging.m314d(str2, sb.toString());
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.m314d(TAG, stackTraceElement.toString());
                }
            }
        }
    }

    public static void printStackTraces() {
        printStackTrace(networkThread, "Network thread");
        printStackTrace(workerThread, "Worker thread");
        printStackTrace(signalingThread, "Signaling thread");
    }

    private static void onNetworkThreadReady() {
        networkThread = Thread.currentThread();
        Logging.m314d(TAG, "onNetworkThreadReady");
    }

    private static void onWorkerThreadReady() {
        workerThread = Thread.currentThread();
        Logging.m314d(TAG, "onWorkerThreadReady");
    }

    private static void onSignalingThreadReady() {
        signalingThread = Thread.currentThread();
        Logging.m314d(TAG, "onSignalingThreadReady");
    }
}
