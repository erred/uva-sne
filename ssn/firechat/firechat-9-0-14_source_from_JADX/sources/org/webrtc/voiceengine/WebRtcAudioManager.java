package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import com.amplitude.api.Constants;
import java.util.Timer;
import java.util.TimerTask;
import org.webrtc.Logging;

public class WebRtcAudioManager {
    private static final String[] AUDIO_MODES = {"MODE_NORMAL", "MODE_RINGTONE", "MODE_IN_CALL", "MODE_IN_COMMUNICATION"};
    private static final int BITS_PER_SAMPLE = 16;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_FRAME_PER_BUFFER = 256;
    private static final String TAG = "WebRtcAudioManager";
    private static boolean blacklistDeviceForOpenSLESUsage = false;
    private static boolean blacklistDeviceForOpenSLESUsageIsOverridden = false;
    private static boolean useStereoInput = false;
    private static boolean useStereoOutput = false;
    private final AudioManager audioManager;
    private final Context context;
    private boolean hardwareAEC;
    private boolean hardwareAGC;
    private boolean hardwareNS;
    private boolean initialized = false;
    private int inputBufferSize;
    private int inputChannels;
    private boolean lowLatencyInput;
    private boolean lowLatencyOutput;
    private final long nativeAudioManager;
    private int nativeChannels;
    private int nativeSampleRate;
    private int outputBufferSize;
    private int outputChannels;
    private boolean proAudio;
    private int sampleRate;
    private final VolumeLogger volumeLogger;

    private static class VolumeLogger {
        private static final String THREAD_NAME = "WebRtcVolumeLevelLoggerThread";
        private static final int TIMER_PERIOD_IN_SECONDS = 30;
        /* access modifiers changed from: private */
        public final AudioManager audioManager;
        private Timer timer;

        private class LogVolumeTask extends TimerTask {
            private final int maxRingVolume;
            private final int maxVoiceCallVolume;

            LogVolumeTask(int i, int i2) {
                this.maxRingVolume = i;
                this.maxVoiceCallVolume = i2;
            }

            public void run() {
                int mode = VolumeLogger.this.audioManager.getMode();
                if (mode == 1) {
                    String str = WebRtcAudioManager.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("STREAM_RING stream volume: ");
                    sb.append(VolumeLogger.this.audioManager.getStreamVolume(2));
                    sb.append(" (max=");
                    sb.append(this.maxRingVolume);
                    sb.append(")");
                    Logging.m314d(str, sb.toString());
                } else if (mode == 3) {
                    String str2 = WebRtcAudioManager.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("VOICE_CALL stream volume: ");
                    sb2.append(VolumeLogger.this.audioManager.getStreamVolume(0));
                    sb2.append(" (max=");
                    sb2.append(this.maxVoiceCallVolume);
                    sb2.append(")");
                    Logging.m314d(str2, sb2.toString());
                }
            }
        }

        public VolumeLogger(AudioManager audioManager2) {
            this.audioManager = audioManager2;
        }

        public void start() {
            this.timer = new Timer(THREAD_NAME);
            this.timer.schedule(new LogVolumeTask(this.audioManager.getStreamMaxVolume(2), this.audioManager.getStreamMaxVolume(0)), 0, Constants.EVENT_UPLOAD_PERIOD_MILLIS);
        }

        /* access modifiers changed from: private */
        public void stop() {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
        }
    }

    private native void nativeCacheAudioParameters(int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i4, int i5, long j);

    public static synchronized void setBlacklistDeviceForOpenSLESUsage(boolean z) {
        synchronized (WebRtcAudioManager.class) {
            blacklistDeviceForOpenSLESUsageIsOverridden = true;
            blacklistDeviceForOpenSLESUsage = z;
        }
    }

    public static synchronized void setStereoOutput(boolean z) {
        synchronized (WebRtcAudioManager.class) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Overriding default output behavior: setStereoOutput(");
            sb.append(z);
            sb.append(')');
            Logging.m318w(str, sb.toString());
            useStereoOutput = z;
        }
    }

    public static synchronized void setStereoInput(boolean z) {
        synchronized (WebRtcAudioManager.class) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Overriding default input behavior: setStereoInput(");
            sb.append(z);
            sb.append(')');
            Logging.m318w(str, sb.toString());
            useStereoInput = z;
        }
    }

    public static synchronized boolean getStereoOutput() {
        boolean z;
        synchronized (WebRtcAudioManager.class) {
            z = useStereoOutput;
        }
        return z;
    }

    public static synchronized boolean getStereoInput() {
        boolean z;
        synchronized (WebRtcAudioManager.class) {
            z = useStereoInput;
        }
        return z;
    }

    WebRtcAudioManager(Context context2, long j) {
        Context context3 = context2;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ctor");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
        this.context = context3;
        long j2 = j;
        this.nativeAudioManager = j2;
        this.audioManager = (AudioManager) context3.getSystemService("audio");
        this.volumeLogger = new VolumeLogger(this.audioManager);
        storeAudioParameters();
        nativeCacheAudioParameters(this.sampleRate, this.outputChannels, this.inputChannels, this.hardwareAEC, this.hardwareAGC, this.hardwareNS, this.lowLatencyOutput, this.lowLatencyInput, this.proAudio, this.outputBufferSize, this.inputBufferSize, j2);
    }

    private boolean init() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("init");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
        if (this.initialized) {
            return true;
        }
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("audio mode is: ");
        sb2.append(AUDIO_MODES[this.audioManager.getMode()]);
        Logging.m314d(str2, sb2.toString());
        this.initialized = true;
        this.volumeLogger.start();
        return true;
    }

    private void dispose() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("dispose");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
        if (this.initialized) {
            this.volumeLogger.stop();
        }
    }

    private boolean isCommunicationModeEnabled() {
        return this.audioManager.getMode() == 3;
    }

    private boolean isDeviceBlacklistedForOpenSLESUsage() {
        boolean z;
        if (blacklistDeviceForOpenSLESUsageIsOverridden) {
            z = blacklistDeviceForOpenSLESUsage;
        } else {
            z = WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage();
        }
        if (z) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(Build.MODEL);
            sb.append(" is blacklisted for OpenSL ES usage!");
            Logging.m315e(str, sb.toString());
        }
        return z;
    }

    private void storeAudioParameters() {
        int i;
        int i2;
        int i3 = 1;
        this.outputChannels = getStereoOutput() ? 2 : 1;
        if (getStereoInput()) {
            i3 = 2;
        }
        this.inputChannels = i3;
        this.sampleRate = getNativeOutputSampleRate();
        this.hardwareAEC = isAcousticEchoCancelerSupported();
        this.hardwareAGC = false;
        this.hardwareNS = isNoiseSuppressorSupported();
        this.lowLatencyOutput = isLowLatencyOutputSupported();
        this.lowLatencyInput = isLowLatencyInputSupported();
        this.proAudio = isProAudioSupported();
        if (this.lowLatencyOutput) {
            i = getLowLatencyOutputFramesPerBuffer();
        } else {
            i = getMinOutputFrameSize(this.sampleRate, this.outputChannels);
        }
        this.outputBufferSize = i;
        if (this.lowLatencyInput) {
            i2 = getLowLatencyInputFramesPerBuffer();
        } else {
            i2 = getMinInputFrameSize(this.sampleRate, this.inputChannels);
        }
        this.inputBufferSize = i2;
    }

    private boolean hasEarpiece() {
        return this.context.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    private boolean isLowLatencyOutputSupported() {
        return isOpenSLESSupported() && this.context.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    public boolean isLowLatencyInputSupported() {
        return WebRtcAudioUtils.runningOnLollipopOrHigher() && isLowLatencyOutputSupported();
    }

    @TargetApi(23)
    private boolean isProAudioSupported() {
        return WebRtcAudioUtils.runningOnMarshmallowOrHigher() && this.context.getPackageManager().hasSystemFeature("android.hardware.audio.pro");
    }

    private int getNativeOutputSampleRate() {
        int i;
        if (WebRtcAudioUtils.runningOnEmulator()) {
            Logging.m314d(TAG, "Running emulator, overriding sample rate to 8 kHz.");
            return 8000;
        } else if (WebRtcAudioUtils.isDefaultSampleRateOverridden()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Default sample rate is overriden to ");
            sb.append(WebRtcAudioUtils.getDefaultSampleRateHz());
            sb.append(" Hz");
            Logging.m314d(str, sb.toString());
            return WebRtcAudioUtils.getDefaultSampleRateHz();
        } else {
            if (WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher()) {
                i = getSampleRateOnJellyBeanMR10OrHigher();
            } else {
                i = WebRtcAudioUtils.getDefaultSampleRateHz();
            }
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Sample rate is set to ");
            sb2.append(i);
            sb2.append(" Hz");
            Logging.m314d(str2, sb2.toString());
            return i;
        }
    }

    @TargetApi(17)
    private int getSampleRateOnJellyBeanMR10OrHigher() {
        String property = this.audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
        if (property == null) {
            return WebRtcAudioUtils.getDefaultSampleRateHz();
        }
        return Integer.parseInt(property);
    }

    @TargetApi(17)
    private int getLowLatencyOutputFramesPerBuffer() {
        assertTrue(isLowLatencyOutputSupported());
        int i = 256;
        if (!WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher()) {
            return 256;
        }
        String property = this.audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
        if (property != null) {
            i = Integer.parseInt(property);
        }
        return i;
    }

    private static boolean isAcousticEchoCancelerSupported() {
        return WebRtcAudioEffects.canUseAcousticEchoCanceler();
    }

    private static boolean isNoiseSuppressorSupported() {
        return WebRtcAudioEffects.canUseNoiseSuppressor();
    }

    private static int getMinOutputFrameSize(int i, int i2) {
        return AudioTrack.getMinBufferSize(i, i2 == 1 ? 4 : 12, 2) / (i2 * 2);
    }

    private int getLowLatencyInputFramesPerBuffer() {
        assertTrue(isLowLatencyInputSupported());
        return getLowLatencyOutputFramesPerBuffer();
    }

    private static int getMinInputFrameSize(int i, int i2) {
        return AudioRecord.getMinBufferSize(i, i2 == 1 ? 16 : 12, 2) / (i2 * 2);
    }

    private static boolean isOpenSLESSupported() {
        return WebRtcAudioUtils.runningOnGingerBreadOrHigher();
    }

    private static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }
}
