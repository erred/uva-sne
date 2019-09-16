package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import java.nio.ByteBuffer;
import org.webrtc.Logging;

public class WebRtcAudioTrack {
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioTrack";
    /* access modifiers changed from: private */
    public static volatile boolean speakerMute = false;
    private final AudioManager audioManager;
    private AudioTrackThread audioThread = null;
    /* access modifiers changed from: private */
    public AudioTrack audioTrack = null;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    private final Context context;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    /* access modifiers changed from: private */
    public final long nativeAudioTrack;

    private class AudioTrackThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioTrackThread(String str) {
            super(str);
        }

        public void run() {
            int i;
            Process.setThreadPriority(-19);
            String str = WebRtcAudioTrack.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("AudioTrackThread");
            sb.append(WebRtcAudioUtils.getThreadInfo());
            Logging.m314d(str, sb.toString());
            try {
                WebRtcAudioTrack.this.audioTrack.play();
                boolean z = true;
                WebRtcAudioTrack.assertTrue(WebRtcAudioTrack.this.audioTrack.getPlayState() == 3);
                int capacity = WebRtcAudioTrack.this.byteBuffer.capacity();
                while (this.keepAlive) {
                    WebRtcAudioTrack.this.nativeGetPlayoutData(capacity, WebRtcAudioTrack.this.nativeAudioTrack);
                    WebRtcAudioTrack.assertTrue(capacity <= WebRtcAudioTrack.this.byteBuffer.remaining());
                    if (WebRtcAudioTrack.speakerMute) {
                        WebRtcAudioTrack.this.byteBuffer.clear();
                        WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
                        WebRtcAudioTrack.this.byteBuffer.position(0);
                    }
                    if (WebRtcAudioUtils.runningOnLollipopOrHigher()) {
                        i = writeOnLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, capacity);
                    } else {
                        i = writePreLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, capacity);
                    }
                    if (i != capacity) {
                        String str2 = WebRtcAudioTrack.TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("AudioTrack.write failed: ");
                        sb2.append(i);
                        Logging.m315e(str2, sb2.toString());
                        if (i == -3) {
                            this.keepAlive = false;
                        }
                    }
                    WebRtcAudioTrack.this.byteBuffer.rewind();
                }
                try {
                    WebRtcAudioTrack.this.audioTrack.stop();
                } catch (IllegalStateException e) {
                    String str3 = WebRtcAudioTrack.TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("AudioTrack.stop failed: ");
                    sb3.append(e.getMessage());
                    Logging.m315e(str3, sb3.toString());
                }
                if (WebRtcAudioTrack.this.audioTrack.getPlayState() != 1) {
                    z = false;
                }
                WebRtcAudioTrack.assertTrue(z);
                WebRtcAudioTrack.this.audioTrack.flush();
            } catch (IllegalStateException e2) {
                String str4 = WebRtcAudioTrack.TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("AudioTrack.play failed: ");
                sb4.append(e2.getMessage());
                Logging.m315e(str4, sb4.toString());
                WebRtcAudioTrack.this.releaseAudioResources();
            }
        }

        @TargetApi(21)
        private int writeOnLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int i) {
            return audioTrack.write(byteBuffer, i, 0);
        }

        private int writePreLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int i) {
            return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), i);
        }

        public void joinThread() {
            this.keepAlive = false;
            while (isAlive()) {
                try {
                    join();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    private int channelCountToConfiguration(int i) {
        return i == 1 ? 4 : 12;
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeGetPlayoutData(int i, long j);

    WebRtcAudioTrack(Context context2, long j) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ctor");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
        this.context = context2;
        this.nativeAudioTrack = j;
        this.audioManager = (AudioManager) context2.getSystemService("audio");
    }

    private boolean initPlayout(int i, int i2) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("initPlayout(sampleRate=");
        sb.append(i);
        sb.append(", channels=");
        sb.append(i2);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        int i3 = i2 * 2;
        ByteBuffer byteBuffer2 = this.byteBuffer;
        this.byteBuffer = ByteBuffer.allocateDirect(i3 * (i / 100));
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("byteBuffer.capacity: ");
        sb2.append(this.byteBuffer.capacity());
        Logging.m314d(str2, sb2.toString());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioTrack);
        int channelCountToConfiguration = channelCountToConfiguration(i2);
        int minBufferSize = AudioTrack.getMinBufferSize(i, channelCountToConfiguration, 2);
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("AudioTrack.getMinBufferSize: ");
        sb3.append(minBufferSize);
        Logging.m314d(str3, sb3.toString());
        if (minBufferSize < this.byteBuffer.capacity()) {
            Logging.m315e(TAG, "AudioTrack.getMinBufferSize returns an invalid value.");
            return false;
        } else if (this.audioTrack != null) {
            Logging.m315e(TAG, "Conflict with existing AudioTrack.");
            return false;
        } else {
            try {
                AudioTrack audioTrack2 = new AudioTrack(0, i, channelCountToConfiguration, 2, minBufferSize, 1);
                this.audioTrack = audioTrack2;
                if (this.audioTrack == null || this.audioTrack.getState() != 1) {
                    Logging.m315e(TAG, "Initialization of audio track failed.");
                    releaseAudioResources();
                    return false;
                }
                logMainParameters();
                logMainParametersExtended();
                return true;
            } catch (IllegalArgumentException e) {
                Logging.m314d(TAG, e.getMessage());
                releaseAudioResources();
                return false;
            }
        }
    }

    private boolean startPlayout() {
        Logging.m314d(TAG, "startPlayout");
        assertTrue(this.audioTrack != null);
        assertTrue(this.audioThread == null);
        if (this.audioTrack.getState() != 1) {
            Logging.m315e(TAG, "AudioTrack instance is not successfully initialized.");
            return false;
        }
        this.audioThread = new AudioTrackThread("AudioTrackJavaThread");
        this.audioThread.start();
        return true;
    }

    private boolean stopPlayout() {
        Logging.m314d(TAG, "stopPlayout");
        assertTrue(this.audioThread != null);
        logUnderrunCount();
        this.audioThread.joinThread();
        this.audioThread = null;
        releaseAudioResources();
        return true;
    }

    private int getStreamMaxVolume() {
        Logging.m314d(TAG, "getStreamMaxVolume");
        assertTrue(this.audioManager != null);
        return this.audioManager.getStreamMaxVolume(0);
    }

    private boolean setStreamVolume(int i) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setStreamVolume(");
        sb.append(i);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        assertTrue(this.audioManager != null);
        if (isVolumeFixed()) {
            Logging.m315e(TAG, "The device implements a fixed volume policy.");
            return false;
        }
        this.audioManager.setStreamVolume(0, i, 0);
        return true;
    }

    private boolean isVolumeFixed() {
        if (!WebRtcAudioUtils.runningOnLollipopOrHigher()) {
            return false;
        }
        return this.audioManager.isVolumeFixed();
    }

    private int getStreamVolume() {
        Logging.m314d(TAG, "getStreamVolume");
        assertTrue(this.audioManager != null);
        return this.audioManager.getStreamVolume(0);
    }

    private void logMainParameters() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("AudioTrack: session ID: ");
        sb.append(this.audioTrack.getAudioSessionId());
        sb.append(", channels: ");
        sb.append(this.audioTrack.getChannelCount());
        sb.append(", sample rate: ");
        sb.append(this.audioTrack.getSampleRate());
        sb.append(", max gain: ");
        AudioTrack audioTrack2 = this.audioTrack;
        sb.append(AudioTrack.getMaxVolume());
        Logging.m314d(str, sb.toString());
    }

    @TargetApi(24)
    private void logMainParametersExtended() {
        if (WebRtcAudioUtils.runningOnMarshmallowOrHigher()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("AudioTrack: buffer size in frames: ");
            sb.append(this.audioTrack.getBufferSizeInFrames());
            Logging.m314d(str, sb.toString());
        }
        if (WebRtcAudioUtils.runningOnNougatOrHigher()) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("AudioTrack: buffer capacity in frames: ");
            sb2.append(this.audioTrack.getBufferCapacityInFrames());
            Logging.m314d(str2, sb2.toString());
        }
    }

    @TargetApi(24)
    private void logUnderrunCount() {
        if (WebRtcAudioUtils.runningOnNougatOrHigher()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("underrun count: ");
            sb.append(this.audioTrack.getUnderrunCount());
            Logging.m314d(str, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public static void setSpeakerMute(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setSpeakerMute(");
        sb.append(z);
        sb.append(")");
        Logging.m318w(str, sb.toString());
        speakerMute = z;
    }

    /* access modifiers changed from: private */
    public void releaseAudioResources() {
        if (this.audioTrack != null) {
            this.audioTrack.release();
            this.audioTrack = null;
        }
    }
}
