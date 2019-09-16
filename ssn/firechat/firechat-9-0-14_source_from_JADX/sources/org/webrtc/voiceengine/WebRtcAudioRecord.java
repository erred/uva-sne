package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioRecord;
import android.os.Process;
import java.nio.ByteBuffer;
import org.webrtc.Logging;
import org.webrtc.ThreadUtils;

public class WebRtcAudioRecord {
    private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioRecord";
    private static WebRtcAudioRecordErrorCallback errorCallback = null;
    /* access modifiers changed from: private */
    public static volatile boolean microphoneMute = false;
    /* access modifiers changed from: private */
    public AudioRecord audioRecord = null;
    private AudioRecordThread audioThread = null;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    private final Context context;
    private WebRtcAudioEffects effects = null;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    /* access modifiers changed from: private */
    public final long nativeAudioRecord;

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String str) {
            super(str);
        }

        public void run() {
            Process.setThreadPriority(-19);
            String str = WebRtcAudioRecord.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("AudioRecordThread");
            sb.append(WebRtcAudioUtils.getThreadInfo());
            Logging.m314d(str, sb.toString());
            WebRtcAudioRecord.assertTrue(WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3);
            System.nanoTime();
            while (this.keepAlive) {
                int read = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (read == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    WebRtcAudioRecord.this.nativeDataIsRecorded(read, WebRtcAudioRecord.this.nativeAudioRecord);
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("AudioRecord.read failed: ");
                    sb2.append(read);
                    String sb3 = sb2.toString();
                    Logging.m315e(WebRtcAudioRecord.TAG, sb3);
                    if (read == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(sb3);
                    }
                }
            }
            try {
                if (WebRtcAudioRecord.this.audioRecord != null) {
                    WebRtcAudioRecord.this.audioRecord.stop();
                }
            } catch (IllegalStateException e) {
                String str2 = WebRtcAudioRecord.TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("AudioRecord.stop failed: ");
                sb4.append(e.getMessage());
                Logging.m315e(str2, sb4.toString());
            }
        }

        public void stopThread() {
            Logging.m314d(WebRtcAudioRecord.TAG, "stopThread");
            this.keepAlive = false;
        }
    }

    public interface WebRtcAudioRecordErrorCallback {
        void onWebRtcAudioRecordError(String str);

        void onWebRtcAudioRecordInitError(String str);

        void onWebRtcAudioRecordStartError(String str);
    }

    private int channelCountToConfiguration(int i) {
        return i == 1 ? 16 : 12;
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(int i, long j);

    public static void setErrorCallback(WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback) {
        Logging.m314d(TAG, "Set error callback");
        errorCallback = webRtcAudioRecordErrorCallback;
    }

    WebRtcAudioRecord(Context context2, long j) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ctor");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
        this.context = context2;
        this.nativeAudioRecord = j;
        this.effects = WebRtcAudioEffects.create();
    }

    private boolean enableBuiltInAEC(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("enableBuiltInAEC(");
        sb.append(z);
        sb.append(')');
        Logging.m314d(str, sb.toString());
        if (this.effects != null) {
            return this.effects.setAEC(z);
        }
        Logging.m315e(TAG, "Built-in AEC is not supported on this platform");
        return false;
    }

    private boolean enableBuiltInNS(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("enableBuiltInNS(");
        sb.append(z);
        sb.append(')');
        Logging.m314d(str, sb.toString());
        if (this.effects != null) {
            return this.effects.setNS(z);
        }
        Logging.m315e(TAG, "Built-in NS is not supported on this platform");
        return false;
    }

    private int initRecording(int i, int i2) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("initRecording(sampleRate=");
        sb.append(i);
        sb.append(", channels=");
        sb.append(i2);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        if (!WebRtcAudioUtils.hasPermission(this.context, "android.permission.RECORD_AUDIO")) {
            reportWebRtcAudioRecordInitError("RECORD_AUDIO permission is missing");
            return -1;
        } else if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        } else {
            int i3 = i / 100;
            this.byteBuffer = ByteBuffer.allocateDirect(i2 * 2 * i3);
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("byteBuffer.capacity: ");
            sb2.append(this.byteBuffer.capacity());
            Logging.m314d(str2, sb2.toString());
            this.emptyBytes = new byte[this.byteBuffer.capacity()];
            nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
            int channelCountToConfiguration = channelCountToConfiguration(i2);
            int minBufferSize = AudioRecord.getMinBufferSize(i, channelCountToConfiguration, 2);
            if (minBufferSize == -1 || minBufferSize == -2) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("AudioRecord.getMinBufferSize failed: ");
                sb3.append(minBufferSize);
                reportWebRtcAudioRecordInitError(sb3.toString());
                return -1;
            }
            String str3 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("AudioRecord.getMinBufferSize: ");
            sb4.append(minBufferSize);
            Logging.m314d(str3, sb4.toString());
            int max = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
            String str4 = TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("bufferSizeInBytes: ");
            sb5.append(max);
            Logging.m314d(str4, sb5.toString());
            try {
                AudioRecord audioRecord2 = new AudioRecord(7, i, channelCountToConfiguration, 2, max);
                this.audioRecord = audioRecord2;
                if (this.audioRecord == null || this.audioRecord.getState() != 1) {
                    reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
                    releaseAudioResources();
                    return -1;
                }
                if (this.effects != null) {
                    this.effects.enable(this.audioRecord.getAudioSessionId());
                }
                logMainParameters();
                logMainParametersExtended();
                return i3;
            } catch (IllegalArgumentException e) {
                StringBuilder sb6 = new StringBuilder();
                sb6.append("AudioRecord ctor error: ");
                sb6.append(e.getMessage());
                reportWebRtcAudioRecordInitError(sb6.toString());
                releaseAudioResources();
                return -1;
            }
        }
    }

    private boolean startRecording() {
        Logging.m314d(TAG, "startRecording");
        assertTrue(this.audioRecord != null);
        assertTrue(this.audioThread == null);
        try {
            this.audioRecord.startRecording();
            if (this.audioRecord.getRecordingState() != 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("AudioRecord.startRecording failed - incorrect state :");
                sb.append(this.audioRecord.getRecordingState());
                reportWebRtcAudioRecordStartError(sb.toString());
                return false;
            }
            this.audioThread = new AudioRecordThread("AudioRecordJavaThread");
            this.audioThread.start();
            return true;
        } catch (IllegalStateException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("AudioRecord.startRecording failed: ");
            sb2.append(e.getMessage());
            reportWebRtcAudioRecordStartError(sb2.toString());
            return false;
        }
    }

    private boolean stopRecording() {
        Logging.m314d(TAG, "stopRecording");
        assertTrue(this.audioThread != null);
        this.audioThread.stopThread();
        if (!ThreadUtils.joinUninterruptibly(this.audioThread, AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS)) {
            Logging.m315e(TAG, "Join of AudioRecordJavaThread timed out");
        }
        this.audioThread = null;
        if (this.effects != null) {
            this.effects.release();
        }
        releaseAudioResources();
        return true;
    }

    private void logMainParameters() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("AudioRecord: session ID: ");
        sb.append(this.audioRecord.getAudioSessionId());
        sb.append(", channels: ");
        sb.append(this.audioRecord.getChannelCount());
        sb.append(", sample rate: ");
        sb.append(this.audioRecord.getSampleRate());
        Logging.m314d(str, sb.toString());
    }

    @TargetApi(23)
    private void logMainParametersExtended() {
        if (WebRtcAudioUtils.runningOnMarshmallowOrHigher()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("AudioRecord: buffer size in frames: ");
            sb.append(this.audioRecord.getBufferSizeInFrames());
            Logging.m314d(str, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    public static void setMicrophoneMute(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setMicrophoneMute(");
        sb.append(z);
        sb.append(")");
        Logging.m318w(str, sb.toString());
        microphoneMute = z;
    }

    private void releaseAudioResources() {
        if (this.audioRecord != null) {
            this.audioRecord.release();
            this.audioRecord = null;
        }
    }

    private void reportWebRtcAudioRecordInitError(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Init recording error: ");
        sb.append(str);
        Logging.m315e(str2, sb.toString());
        if (errorCallback != null) {
            errorCallback.onWebRtcAudioRecordInitError(str);
        }
    }

    private void reportWebRtcAudioRecordStartError(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Start recording error: ");
        sb.append(str);
        Logging.m315e(str2, sb.toString());
        if (errorCallback != null) {
            errorCallback.onWebRtcAudioRecordStartError(str);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Run-time recording error: ");
        sb.append(str);
        Logging.m315e(str2, sb.toString());
        if (errorCallback != null) {
            errorCallback.onWebRtcAudioRecordError(str);
        }
    }
}
