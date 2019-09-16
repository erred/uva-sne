package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.Descriptor;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import com.facebook.react.uimanager.ViewProps;
import java.util.UUID;
import org.webrtc.Logging;

class WebRtcAudioEffects {
    private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5c51b");
    private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("c06c8400-8e06-11e0-9cb6-0002a5d5c51b");
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioEffects";
    private static Descriptor[] cachedEffects;
    private AcousticEchoCanceler aec = null;

    /* renamed from: ns */
    private NoiseSuppressor f175ns = null;
    private boolean shouldEnableAec = false;
    private boolean shouldEnableNs = false;

    public static boolean isAcousticEchoCancelerSupported() {
        return WebRtcAudioUtils.runningOnJellyBeanOrHigher() && isAcousticEchoCancelerEffectAvailable();
    }

    public static boolean isNoiseSuppressorSupported() {
        return WebRtcAudioUtils.runningOnJellyBeanOrHigher() && isNoiseSuppressorEffectAvailable();
    }

    public static boolean isAcousticEchoCancelerBlacklisted() {
        boolean contains = WebRtcAudioUtils.getBlackListedModelsForAecUsage().contains(Build.MODEL);
        if (contains) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(Build.MODEL);
            sb.append(" is blacklisted for HW AEC usage!");
            Logging.m318w(str, sb.toString());
        }
        return contains;
    }

    public static boolean isNoiseSuppressorBlacklisted() {
        boolean contains = WebRtcAudioUtils.getBlackListedModelsForNsUsage().contains(Build.MODEL);
        if (contains) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(Build.MODEL);
            sb.append(" is blacklisted for HW NS usage!");
            Logging.m318w(str, sb.toString());
        }
        return contains;
    }

    @TargetApi(18)
    private static boolean isAcousticEchoCancelerExcludedByUUID() {
        Descriptor[] availableEffects;
        for (Descriptor descriptor : getAvailableEffects()) {
            if (descriptor.type.equals(AudioEffect.EFFECT_TYPE_AEC) && descriptor.uuid.equals(AOSP_ACOUSTIC_ECHO_CANCELER)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(18)
    private static boolean isNoiseSuppressorExcludedByUUID() {
        Descriptor[] availableEffects;
        for (Descriptor descriptor : getAvailableEffects()) {
            if (descriptor.type.equals(AudioEffect.EFFECT_TYPE_NS) && descriptor.uuid.equals(AOSP_NOISE_SUPPRESSOR)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(18)
    private static boolean isAcousticEchoCancelerEffectAvailable() {
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
    }

    @TargetApi(18)
    private static boolean isNoiseSuppressorEffectAvailable() {
        return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
    }

    public static boolean canUseAcousticEchoCanceler() {
        boolean z = isAcousticEchoCancelerSupported() && !WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler() && !isAcousticEchoCancelerBlacklisted() && !isAcousticEchoCancelerExcludedByUUID();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("canUseAcousticEchoCanceler: ");
        sb.append(z);
        Logging.m314d(str, sb.toString());
        return z;
    }

    public static boolean canUseNoiseSuppressor() {
        boolean z = isNoiseSuppressorSupported() && !WebRtcAudioUtils.useWebRtcBasedNoiseSuppressor() && !isNoiseSuppressorBlacklisted() && !isNoiseSuppressorExcludedByUUID();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("canUseNoiseSuppressor: ");
        sb.append(z);
        Logging.m314d(str, sb.toString());
        return z;
    }

    static WebRtcAudioEffects create() {
        if (WebRtcAudioUtils.runningOnJellyBeanOrHigher()) {
            return new WebRtcAudioEffects();
        }
        Logging.m318w(TAG, "API level 16 or higher is required!");
        return null;
    }

    private WebRtcAudioEffects() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ctor");
        sb.append(WebRtcAudioUtils.getThreadInfo());
        Logging.m314d(str, sb.toString());
    }

    public boolean setAEC(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setAEC(");
        sb.append(z);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        if (!canUseAcousticEchoCanceler()) {
            Logging.m318w(TAG, "Platform AEC is not supported");
            this.shouldEnableAec = false;
            return false;
        } else if (this.aec == null || z == this.shouldEnableAec) {
            this.shouldEnableAec = z;
            return true;
        } else {
            Logging.m315e(TAG, "Platform AEC state can't be modified while recording");
            return false;
        }
    }

    public boolean setNS(boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setNS(");
        sb.append(z);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        if (!canUseNoiseSuppressor()) {
            Logging.m318w(TAG, "Platform NS is not supported");
            this.shouldEnableNs = false;
            return false;
        } else if (this.f175ns == null || z == this.shouldEnableNs) {
            this.shouldEnableNs = z;
            return true;
        } else {
            Logging.m315e(TAG, "Platform NS state can't be modified while recording");
            return false;
        }
    }

    public void enable(int i) {
        Descriptor[] queryEffects;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("enable(audioSession=");
        sb.append(i);
        sb.append(")");
        Logging.m314d(str, sb.toString());
        boolean z = false;
        assertTrue(this.aec == null);
        assertTrue(this.f175ns == null);
        for (Descriptor descriptor : AudioEffect.queryEffects()) {
            if (effectTypeIsVoIP(descriptor.type)) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("name: ");
                sb2.append(descriptor.name);
                sb2.append(", mode: ");
                sb2.append(descriptor.connectMode);
                sb2.append(", implementor: ");
                sb2.append(descriptor.implementor);
                sb2.append(", UUID: ");
                sb2.append(descriptor.uuid);
                Logging.m314d(str2, sb2.toString());
            }
        }
        if (isAcousticEchoCancelerSupported()) {
            this.aec = AcousticEchoCanceler.create(i);
            if (this.aec != null) {
                boolean enabled = this.aec.getEnabled();
                boolean z2 = this.shouldEnableAec && canUseAcousticEchoCanceler();
                if (this.aec.setEnabled(z2) != 0) {
                    Logging.m315e(TAG, "Failed to set the AcousticEchoCanceler state");
                }
                String str3 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("AcousticEchoCanceler: was ");
                sb3.append(enabled ? ViewProps.ENABLED : "disabled");
                sb3.append(", enable: ");
                sb3.append(z2);
                sb3.append(", is now: ");
                sb3.append(this.aec.getEnabled() ? ViewProps.ENABLED : "disabled");
                Logging.m314d(str3, sb3.toString());
            } else {
                Logging.m315e(TAG, "Failed to create the AcousticEchoCanceler instance");
            }
        }
        if (isNoiseSuppressorSupported()) {
            this.f175ns = NoiseSuppressor.create(i);
            if (this.f175ns != null) {
                boolean enabled2 = this.f175ns.getEnabled();
                if (this.shouldEnableNs && canUseNoiseSuppressor()) {
                    z = true;
                }
                if (this.f175ns.setEnabled(z) != 0) {
                    Logging.m315e(TAG, "Failed to set the NoiseSuppressor state");
                }
                String str4 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("NoiseSuppressor: was ");
                sb4.append(enabled2 ? ViewProps.ENABLED : "disabled");
                sb4.append(", enable: ");
                sb4.append(z);
                sb4.append(", is now: ");
                sb4.append(this.f175ns.getEnabled() ? ViewProps.ENABLED : "disabled");
                Logging.m314d(str4, sb4.toString());
                return;
            }
            Logging.m315e(TAG, "Failed to create the NoiseSuppressor instance");
        }
    }

    public void release() {
        Logging.m314d(TAG, "release");
        if (this.aec != null) {
            this.aec.release();
            this.aec = null;
        }
        if (this.f175ns != null) {
            this.f175ns.release();
            this.f175ns = null;
        }
    }

    @TargetApi(18)
    private boolean effectTypeIsVoIP(UUID uuid) {
        boolean z = false;
        if (!WebRtcAudioUtils.runningOnJellyBeanMR2OrHigher()) {
            return false;
        }
        if ((AudioEffect.EFFECT_TYPE_AEC.equals(uuid) && isAcousticEchoCancelerSupported()) || (AudioEffect.EFFECT_TYPE_NS.equals(uuid) && isNoiseSuppressorSupported())) {
            z = true;
        }
        return z;
    }

    private static void assertTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private static Descriptor[] getAvailableEffects() {
        if (cachedEffects != null) {
            return cachedEffects;
        }
        cachedEffects = AudioEffect.queryEffects();
        return cachedEffects;
    }

    private static boolean isEffectTypeAvailable(UUID uuid) {
        Descriptor[] availableEffects = getAvailableEffects();
        if (availableEffects == null) {
            return false;
        }
        for (Descriptor descriptor : availableEffects) {
            if (descriptor.type.equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
