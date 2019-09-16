package org.webrtc;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.altbeacon.beacon.service.RangedBeacon;

@TargetApi(19)
public class MediaCodecVideoEncoder {
    private static final int BITRATE_ADJUSTMENT_FPS = 30;
    private static final double BITRATE_CORRECTION_MAX_SCALE = 4.0d;
    private static final double BITRATE_CORRECTION_SEC = 3.0d;
    private static final int BITRATE_CORRECTION_STEPS = 20;
    private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
    private static final int DEQUEUE_TIMEOUT = 0;
    private static final String[] H264_HW_EXCEPTION_MODELS = {"SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4"};
    private static final String H264_MIME_TYPE = "video/avc";
    private static final int MAXIMUM_INITIAL_FPS = 30;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final long QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 25000;
    private static final long QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "MediaCodecVideoEncoder";
    private static final int VIDEO_ControlRateConstant = 2;
    private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
    private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
    private static int codecErrors;
    private static MediaCodecVideoEncoderErrorCallback errorCallback;
    private static final MediaCodecProperties exynosH264HwProperties = new MediaCodecProperties("OMX.Exynos.", 21, BitrateAdjustmentType.FRAMERATE_ADJUSTMENT);
    private static final MediaCodecProperties exynosVp8HwProperties = new MediaCodecProperties("OMX.Exynos.", 23, BitrateAdjustmentType.DYNAMIC_ADJUSTMENT);
    private static final MediaCodecProperties exynosVp9HwProperties = new MediaCodecProperties("OMX.Exynos.", 23, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties[] h264HwList = {qcomH264HwProperties, exynosH264HwProperties};
    private static Set<String> hwEncoderDisabledTypes = new HashSet();
    private static final MediaCodecProperties intelVp8HwProperties = new MediaCodecProperties("OMX.Intel.", 21, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomH264HwProperties = new MediaCodecProperties("OMX.qcom.", 19, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomVp8HwProperties = new MediaCodecProperties("OMX.qcom.", 19, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomVp9HwProperties = new MediaCodecProperties("OMX.qcom.", 23, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static MediaCodecVideoEncoder runningInstance;
    private static final int[] supportedColorList = {19, 21, 2141391872, COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m};
    private static final int[] supportedSurfaceColorList = {2130708361};
    private static final MediaCodecProperties[] vp9HwList = {qcomVp9HwProperties, exynosVp9HwProperties};
    private double bitrateAccumulator;
    private double bitrateAccumulatorMax;
    private int bitrateAdjustmentScaleExp;
    private BitrateAdjustmentType bitrateAdjustmentType = BitrateAdjustmentType.NO_ADJUSTMENT;
    private double bitrateObservationTimeMs;
    private int colorFormat;
    private ByteBuffer configData = null;
    private GlRectDrawer drawer;
    private EglBase14 eglBase;
    private long forcedKeyFrameMs;
    private int height;
    private Surface inputSurface;
    private long lastKeyFrameMs;
    /* access modifiers changed from: private */
    public MediaCodec mediaCodec;
    private Thread mediaCodecThread;
    private ByteBuffer[] outputBuffers;
    private int targetBitrateBps;
    private int targetFps;
    private VideoCodecType type;
    private int width;

    public enum BitrateAdjustmentType {
        NO_ADJUSTMENT,
        FRAMERATE_ADJUSTMENT,
        DYNAMIC_ADJUSTMENT
    }

    public static class EncoderProperties {
        public final BitrateAdjustmentType bitrateAdjustmentType;
        public final String codecName;
        public final int colorFormat;

        public EncoderProperties(String str, int i, BitrateAdjustmentType bitrateAdjustmentType2) {
            this.codecName = str;
            this.colorFormat = i;
            this.bitrateAdjustmentType = bitrateAdjustmentType2;
        }
    }

    private static class MediaCodecProperties {
        public final BitrateAdjustmentType bitrateAdjustmentType;
        public final String codecPrefix;
        public final int minSdk;

        MediaCodecProperties(String str, int i, BitrateAdjustmentType bitrateAdjustmentType2) {
            this.codecPrefix = str;
            this.minSdk = i;
            this.bitrateAdjustmentType = bitrateAdjustmentType2;
        }
    }

    public interface MediaCodecVideoEncoderErrorCallback {
        void onMediaCodecVideoEncoderCriticalError(int i);
    }

    static class OutputBufferInfo {
        public final ByteBuffer buffer;
        public final int index;
        public final boolean isKeyFrame;
        public final long presentationTimestampUs;

        public OutputBufferInfo(int i, ByteBuffer byteBuffer, boolean z, long j) {
            this.index = i;
            this.buffer = byteBuffer;
            this.isKeyFrame = z;
            this.presentationTimestampUs = j;
        }
    }

    public enum VideoCodecType {
        VIDEO_CODEC_VP8,
        VIDEO_CODEC_VP9,
        VIDEO_CODEC_H264
    }

    private static MediaCodecProperties[] vp8HwList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(qcomVp8HwProperties);
        arrayList.add(exynosVp8HwProperties);
        if (PeerConnectionFactory.fieldTrialsFindFullName("WebRTC-IntelVP8").equals("Enabled")) {
            arrayList.add(intelVp8HwProperties);
        }
        return (MediaCodecProperties[]) arrayList.toArray(new MediaCodecProperties[arrayList.size()]);
    }

    public static void setErrorCallback(MediaCodecVideoEncoderErrorCallback mediaCodecVideoEncoderErrorCallback) {
        Logging.m314d(TAG, "Set error callback");
        errorCallback = mediaCodecVideoEncoderErrorCallback;
    }

    public static void disableVp8HwCodec() {
        Logging.m318w(TAG, "VP8 encoding is disabled by application.");
        hwEncoderDisabledTypes.add(VP8_MIME_TYPE);
    }

    public static void disableVp9HwCodec() {
        Logging.m318w(TAG, "VP9 encoding is disabled by application.");
        hwEncoderDisabledTypes.add(VP9_MIME_TYPE);
    }

    public static void disableH264HwCodec() {
        Logging.m318w(TAG, "H.264 encoding is disabled by application.");
        hwEncoderDisabledTypes.add(H264_MIME_TYPE);
    }

    public static boolean isVp8HwSupported() {
        return !hwEncoderDisabledTypes.contains(VP8_MIME_TYPE) && findHwEncoder(VP8_MIME_TYPE, vp8HwList(), supportedColorList) != null;
    }

    public static EncoderProperties vp8HwEncoderProperties() {
        if (hwEncoderDisabledTypes.contains(VP8_MIME_TYPE)) {
            return null;
        }
        return findHwEncoder(VP8_MIME_TYPE, vp8HwList(), supportedColorList);
    }

    public static boolean isVp9HwSupported() {
        return !hwEncoderDisabledTypes.contains(VP9_MIME_TYPE) && findHwEncoder(VP9_MIME_TYPE, vp9HwList, supportedColorList) != null;
    }

    public static boolean isH264HwSupported() {
        return !hwEncoderDisabledTypes.contains(H264_MIME_TYPE) && findHwEncoder(H264_MIME_TYPE, h264HwList, supportedColorList) != null;
    }

    public static boolean isVp8HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains(VP8_MIME_TYPE) && findHwEncoder(VP8_MIME_TYPE, vp8HwList(), supportedSurfaceColorList) != null;
    }

    public static boolean isVp9HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains(VP9_MIME_TYPE) && findHwEncoder(VP9_MIME_TYPE, vp9HwList, supportedSurfaceColorList) != null;
    }

    public static boolean isH264HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains(H264_MIME_TYPE) && findHwEncoder(H264_MIME_TYPE, h264HwList, supportedSurfaceColorList) != null;
    }

    private static EncoderProperties findHwEncoder(String str, MediaCodecProperties[] mediaCodecPropertiesArr, int[] iArr) {
        MediaCodecInfo mediaCodecInfo;
        String str2;
        boolean z;
        int[] iArr2;
        int[] iArr3;
        if (VERSION.SDK_INT < 19) {
            return null;
        }
        if (!str.equals(H264_MIME_TYPE) || !Arrays.asList(H264_HW_EXCEPTION_MODELS).contains(Build.MODEL)) {
            for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
                try {
                    mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                } catch (IllegalArgumentException e) {
                    Logging.m316e(TAG, "Cannot retrieve encoder codec info", e);
                    mediaCodecInfo = null;
                }
                if (mediaCodecInfo != null && mediaCodecInfo.isEncoder()) {
                    String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
                    int length = supportedTypes.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            str2 = null;
                            break;
                        } else if (supportedTypes[i2].equals(str)) {
                            str2 = mediaCodecInfo.getName();
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (str2 == null) {
                        continue;
                    } else {
                        String str3 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Found candidate encoder ");
                        sb.append(str2);
                        Logging.m317v(str3, sb.toString());
                        BitrateAdjustmentType bitrateAdjustmentType2 = BitrateAdjustmentType.NO_ADJUSTMENT;
                        int length2 = mediaCodecPropertiesArr.length;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= length2) {
                                z = false;
                                break;
                            }
                            MediaCodecProperties mediaCodecProperties = mediaCodecPropertiesArr[i3];
                            if (str2.startsWith(mediaCodecProperties.codecPrefix)) {
                                if (VERSION.SDK_INT < mediaCodecProperties.minSdk) {
                                    String str4 = TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("Codec ");
                                    sb2.append(str2);
                                    sb2.append(" is disabled due to SDK version ");
                                    sb2.append(VERSION.SDK_INT);
                                    Logging.m318w(str4, sb2.toString());
                                } else {
                                    if (mediaCodecProperties.bitrateAdjustmentType != BitrateAdjustmentType.NO_ADJUSTMENT) {
                                        bitrateAdjustmentType2 = mediaCodecProperties.bitrateAdjustmentType;
                                        String str5 = TAG;
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append("Codec ");
                                        sb3.append(str2);
                                        sb3.append(" requires bitrate adjustment: ");
                                        sb3.append(bitrateAdjustmentType2);
                                        Logging.m318w(str5, sb3.toString());
                                    }
                                    z = true;
                                }
                            }
                            i3++;
                        }
                        if (!z) {
                            continue;
                        } else {
                            try {
                                CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
                                for (int i4 : capabilitiesForType.colorFormats) {
                                    String str6 = TAG;
                                    StringBuilder sb4 = new StringBuilder();
                                    sb4.append("   Color: 0x");
                                    sb4.append(Integer.toHexString(i4));
                                    Logging.m317v(str6, sb4.toString());
                                }
                                for (int i5 : iArr) {
                                    for (int i6 : capabilitiesForType.colorFormats) {
                                        if (i6 == i5) {
                                            String str7 = TAG;
                                            StringBuilder sb5 = new StringBuilder();
                                            sb5.append("Found target encoder for mime ");
                                            sb5.append(str);
                                            sb5.append(" : ");
                                            sb5.append(str2);
                                            sb5.append(". Color: 0x");
                                            sb5.append(Integer.toHexString(i6));
                                            sb5.append(". Bitrate adjustment: ");
                                            sb5.append(bitrateAdjustmentType2);
                                            Logging.m314d(str7, sb5.toString());
                                            return new EncoderProperties(str2, i6, bitrateAdjustmentType2);
                                        }
                                    }
                                }
                                continue;
                            } catch (IllegalArgumentException e2) {
                                Logging.m316e(TAG, "Cannot retrieve encoder capabilities", e2);
                            }
                        }
                    }
                }
            }
            return null;
        }
        String str8 = TAG;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("Model: ");
        sb6.append(Build.MODEL);
        sb6.append(" has black listed H.264 encoder.");
        Logging.m318w(str8, sb6.toString());
        return null;
    }

    private void checkOnMediaCodecThread() {
        if (this.mediaCodecThread.getId() != Thread.currentThread().getId()) {
            StringBuilder sb = new StringBuilder();
            sb.append("MediaCodecVideoEncoder previously operated on ");
            sb.append(this.mediaCodecThread);
            sb.append(" but is now called on ");
            sb.append(Thread.currentThread());
            throw new RuntimeException(sb.toString());
        }
    }

    public static void printStackTrace() {
        if (runningInstance != null && runningInstance.mediaCodecThread != null) {
            StackTraceElement[] stackTrace = runningInstance.mediaCodecThread.getStackTrace();
            if (stackTrace.length > 0) {
                Logging.m314d(TAG, "MediaCodecVideoEncoder stacks trace:");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.m314d(TAG, stackTraceElement.toString());
                }
            }
        }
    }

    static MediaCodec createByCodecName(String str) {
        try {
            return MediaCodec.createByCodecName(str);
        } catch (Exception unused) {
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean initEncode(org.webrtc.MediaCodecVideoEncoder.VideoCodecType r19, int r20, int r21, int r22, int r23, org.webrtc.EglBase14.Context r24) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r22
            r6 = r23
            r7 = r24
            if (r7 == 0) goto L_0x0012
            r10 = 1
            goto L_0x0013
        L_0x0012:
            r10 = 0
        L_0x0013:
            java.lang.String r11 = "MediaCodecVideoEncoder"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Java initEncode: "
            r12.append(r13)
            r12.append(r2)
            java.lang.String r13 = " : "
            r12.append(r13)
            r12.append(r3)
            java.lang.String r13 = " x "
            r12.append(r13)
            r12.append(r4)
            java.lang.String r13 = ". @ "
            r12.append(r13)
            r12.append(r5)
            java.lang.String r13 = " kbps. Fps: "
            r12.append(r13)
            r12.append(r6)
            java.lang.String r13 = ". Encode from texture : "
            r12.append(r13)
            r12.append(r10)
            java.lang.String r12 = r12.toString()
            org.webrtc.Logging.m314d(r11, r12)
            r1.width = r3
            r1.height = r4
            java.lang.Thread r11 = r1.mediaCodecThread
            if (r11 == 0) goto L_0x0061
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            java.lang.String r3 = "Forgot to release()?"
            r2.<init>(r3)
            throw r2
        L_0x0061:
            org.webrtc.MediaCodecVideoEncoder$VideoCodecType r11 = org.webrtc.MediaCodecVideoEncoder.VideoCodecType.VIDEO_CODEC_VP8
            if (r2 != r11) goto L_0x007e
            java.lang.String r11 = "video/x-vnd.on2.vp8"
            java.lang.String r14 = "video/x-vnd.on2.vp8"
            org.webrtc.MediaCodecVideoEncoder$MediaCodecProperties[] r15 = vp8HwList()
            if (r10 == 0) goto L_0x0074
            int[] r16 = supportedSurfaceColorList
        L_0x0071:
            r12 = r16
            goto L_0x0077
        L_0x0074:
            int[] r16 = supportedColorList
            goto L_0x0071
        L_0x0077:
            org.webrtc.MediaCodecVideoEncoder$EncoderProperties r12 = findHwEncoder(r14, r15, r12)
        L_0x007b:
            r14 = 100
            goto L_0x00af
        L_0x007e:
            org.webrtc.MediaCodecVideoEncoder$VideoCodecType r11 = org.webrtc.MediaCodecVideoEncoder.VideoCodecType.VIDEO_CODEC_VP9
            if (r2 != r11) goto L_0x0094
            java.lang.String r11 = "video/x-vnd.on2.vp9"
            java.lang.String r12 = "video/x-vnd.on2.vp9"
            org.webrtc.MediaCodecVideoEncoder$MediaCodecProperties[] r14 = vp9HwList
            if (r10 == 0) goto L_0x008d
            int[] r15 = supportedSurfaceColorList
            goto L_0x008f
        L_0x008d:
            int[] r15 = supportedColorList
        L_0x008f:
            org.webrtc.MediaCodecVideoEncoder$EncoderProperties r12 = findHwEncoder(r12, r14, r15)
            goto L_0x007b
        L_0x0094:
            org.webrtc.MediaCodecVideoEncoder$VideoCodecType r11 = org.webrtc.MediaCodecVideoEncoder.VideoCodecType.VIDEO_CODEC_H264
            if (r2 != r11) goto L_0x00ac
            java.lang.String r11 = "video/avc"
            java.lang.String r12 = "video/avc"
            org.webrtc.MediaCodecVideoEncoder$MediaCodecProperties[] r14 = h264HwList
            if (r10 == 0) goto L_0x00a3
            int[] r15 = supportedSurfaceColorList
            goto L_0x00a5
        L_0x00a3:
            int[] r15 = supportedColorList
        L_0x00a5:
            org.webrtc.MediaCodecVideoEncoder$EncoderProperties r12 = findHwEncoder(r12, r14, r15)
            r14 = 20
            goto L_0x00af
        L_0x00ac:
            r11 = 0
            r12 = 0
            r14 = 0
        L_0x00af:
            if (r12 != 0) goto L_0x00c8
            java.lang.RuntimeException r3 = new java.lang.RuntimeException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Can not find HW encoder for "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            r3.<init>(r2)
            throw r3
        L_0x00c8:
            runningInstance = r1
            int r15 = r12.colorFormat
            r1.colorFormat = r15
            org.webrtc.MediaCodecVideoEncoder$BitrateAdjustmentType r15 = r12.bitrateAdjustmentType
            r1.bitrateAdjustmentType = r15
            org.webrtc.MediaCodecVideoEncoder$BitrateAdjustmentType r15 = r1.bitrateAdjustmentType
            org.webrtc.MediaCodecVideoEncoder$BitrateAdjustmentType r8 = org.webrtc.MediaCodecVideoEncoder.BitrateAdjustmentType.FRAMERATE_ADJUSTMENT
            r13 = 30
            if (r15 != r8) goto L_0x00dd
        L_0x00da:
            r17 = r10
            goto L_0x00e2
        L_0x00dd:
            int r13 = java.lang.Math.min(r6, r13)
            goto L_0x00da
        L_0x00e2:
            r9 = 0
            r1.forcedKeyFrameMs = r9
            r8 = -1
            r1.lastKeyFrameMs = r8
            org.webrtc.MediaCodecVideoEncoder$VideoCodecType r6 = org.webrtc.MediaCodecVideoEncoder.VideoCodecType.VIDEO_CODEC_VP8
            if (r2 != r6) goto L_0x010d
            java.lang.String r6 = r12.codecName
            org.webrtc.MediaCodecVideoEncoder$MediaCodecProperties r8 = qcomVp8HwProperties
            java.lang.String r8 = r8.codecPrefix
            boolean r6 = r6.startsWith(r8)
            if (r6 == 0) goto L_0x010d
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 23
            if (r6 != r8) goto L_0x0105
            r8 = 25000(0x61a8, double:1.23516E-319)
            r1.forcedKeyFrameMs = r8
            goto L_0x010d
        L_0x0105:
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 <= r8) goto L_0x010d
            r8 = 15000(0x3a98, double:7.411E-320)
            r1.forcedKeyFrameMs = r8
        L_0x010d:
            java.lang.String r6 = "MediaCodecVideoEncoder"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Color format: "
            r8.append(r9)
            int r9 = r1.colorFormat
            r8.append(r9)
            java.lang.String r9 = ". Bitrate adjustment: "
            r8.append(r9)
            org.webrtc.MediaCodecVideoEncoder$BitrateAdjustmentType r9 = r1.bitrateAdjustmentType
            r8.append(r9)
            java.lang.String r9 = ". Key frame interval: "
            r8.append(r9)
            long r9 = r1.forcedKeyFrameMs
            r8.append(r9)
            java.lang.String r9 = " . Initial fps: "
            r8.append(r9)
            r8.append(r13)
            java.lang.String r8 = r8.toString()
            org.webrtc.Logging.m314d(r6, r8)
            int r5 = r5 * 1000
            r1.targetBitrateBps = r5
            r1.targetFps = r13
            int r5 = r1.targetBitrateBps
            double r5 = (double) r5
            r8 = 4620693217682128896(0x4020000000000000, double:8.0)
            double r5 = r5 / r8
            r1.bitrateAccumulatorMax = r5
            r5 = 0
            r1.bitrateAccumulator = r5
            r1.bitrateObservationTimeMs = r5
            r5 = 0
            r1.bitrateAdjustmentScaleExp = r5
            java.lang.Thread r5 = java.lang.Thread.currentThread()
            r1.mediaCodecThread = r5
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r11, r3, r4)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "bitrate"
            int r5 = r1.targetBitrateBps     // Catch:{ IllegalStateException -> 0x01ff }
            r3.setInteger(r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "bitrate-mode"
            r5 = 2
            r3.setInteger(r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "color-format"
            int r5 = r12.colorFormat     // Catch:{ IllegalStateException -> 0x01ff }
            r3.setInteger(r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "frame-rate"
            int r5 = r1.targetFps     // Catch:{ IllegalStateException -> 0x01ff }
            r3.setInteger(r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "i-frame-interval"
            r3.setInteger(r4, r14)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "MediaCodecVideoEncoder"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x01ff }
            r5.<init>()     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r6 = "  Format: "
            r5.append(r6)     // Catch:{ IllegalStateException -> 0x01ff }
            r5.append(r3)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r5 = r5.toString()     // Catch:{ IllegalStateException -> 0x01ff }
            org.webrtc.Logging.m314d(r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = r12.codecName     // Catch:{ IllegalStateException -> 0x01ff }
            android.media.MediaCodec r4 = createByCodecName(r4)     // Catch:{ IllegalStateException -> 0x01ff }
            r1.mediaCodec = r4     // Catch:{ IllegalStateException -> 0x01ff }
            r1.type = r2     // Catch:{ IllegalStateException -> 0x01ff }
            android.media.MediaCodec r2 = r1.mediaCodec     // Catch:{ IllegalStateException -> 0x01ff }
            if (r2 != 0) goto L_0x01af
            java.lang.String r2 = "MediaCodecVideoEncoder"
            java.lang.String r3 = "Can not create media encoder"
            org.webrtc.Logging.m315e(r2, r3)     // Catch:{ IllegalStateException -> 0x01ff }
            r2 = 0
            return r2
        L_0x01af:
            android.media.MediaCodec r2 = r1.mediaCodec     // Catch:{ IllegalStateException -> 0x01ff }
            r4 = 0
            r5 = 1
            r2.configure(r3, r4, r4, r5)     // Catch:{ IllegalStateException -> 0x01ff }
            if (r17 == 0) goto L_0x01d7
            org.webrtc.EglBase14 r2 = new org.webrtc.EglBase14     // Catch:{ IllegalStateException -> 0x01ff }
            int[] r3 = org.webrtc.EglBase.CONFIG_RECORDABLE     // Catch:{ IllegalStateException -> 0x01ff }
            r2.<init>(r7, r3)     // Catch:{ IllegalStateException -> 0x01ff }
            r1.eglBase = r2     // Catch:{ IllegalStateException -> 0x01ff }
            android.media.MediaCodec r2 = r1.mediaCodec     // Catch:{ IllegalStateException -> 0x01ff }
            android.view.Surface r2 = r2.createInputSurface()     // Catch:{ IllegalStateException -> 0x01ff }
            r1.inputSurface = r2     // Catch:{ IllegalStateException -> 0x01ff }
            org.webrtc.EglBase14 r2 = r1.eglBase     // Catch:{ IllegalStateException -> 0x01ff }
            android.view.Surface r3 = r1.inputSurface     // Catch:{ IllegalStateException -> 0x01ff }
            r2.createSurface(r3)     // Catch:{ IllegalStateException -> 0x01ff }
            org.webrtc.GlRectDrawer r2 = new org.webrtc.GlRectDrawer     // Catch:{ IllegalStateException -> 0x01ff }
            r2.<init>()     // Catch:{ IllegalStateException -> 0x01ff }
            r1.drawer = r2     // Catch:{ IllegalStateException -> 0x01ff }
        L_0x01d7:
            android.media.MediaCodec r2 = r1.mediaCodec     // Catch:{ IllegalStateException -> 0x01ff }
            r2.start()     // Catch:{ IllegalStateException -> 0x01ff }
            android.media.MediaCodec r2 = r1.mediaCodec     // Catch:{ IllegalStateException -> 0x01ff }
            java.nio.ByteBuffer[] r2 = r2.getOutputBuffers()     // Catch:{ IllegalStateException -> 0x01ff }
            r1.outputBuffers = r2     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r2 = "MediaCodecVideoEncoder"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x01ff }
            r3.<init>()     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r4 = "Output buffers: "
            r3.append(r4)     // Catch:{ IllegalStateException -> 0x01ff }
            java.nio.ByteBuffer[] r4 = r1.outputBuffers     // Catch:{ IllegalStateException -> 0x01ff }
            int r4 = r4.length     // Catch:{ IllegalStateException -> 0x01ff }
            r3.append(r4)     // Catch:{ IllegalStateException -> 0x01ff }
            java.lang.String r3 = r3.toString()     // Catch:{ IllegalStateException -> 0x01ff }
            org.webrtc.Logging.m314d(r2, r3)     // Catch:{ IllegalStateException -> 0x01ff }
            r2 = 1
            return r2
        L_0x01ff:
            r0 = move-exception
            r2 = r0
            java.lang.String r3 = "MediaCodecVideoEncoder"
            java.lang.String r4 = "initEncode failed"
            org.webrtc.Logging.m316e(r3, r4, r2)
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.MediaCodecVideoEncoder.initEncode(org.webrtc.MediaCodecVideoEncoder$VideoCodecType, int, int, int, int, org.webrtc.EglBase14$Context):boolean");
    }

    /* access modifiers changed from: 0000 */
    public ByteBuffer[] getInputBuffers() {
        ByteBuffer[] inputBuffers = this.mediaCodec.getInputBuffers();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Input buffers: ");
        sb.append(inputBuffers.length);
        Logging.m314d(str, sb.toString());
        return inputBuffers;
    }

    /* access modifiers changed from: 0000 */
    public void checkKeyFrameRequired(boolean z, long j) {
        long j2 = (j + 500) / 1000;
        if (this.lastKeyFrameMs < 0) {
            this.lastKeyFrameMs = j2;
        }
        boolean z2 = !z && this.forcedKeyFrameMs > 0 && j2 > this.lastKeyFrameMs + this.forcedKeyFrameMs;
        if (z || z2) {
            if (z) {
                Logging.m314d(TAG, "Sync frame request");
            } else {
                Logging.m314d(TAG, "Sync frame forced");
            }
            Bundle bundle = new Bundle();
            bundle.putInt("request-sync", 0);
            this.mediaCodec.setParameters(bundle);
            this.lastKeyFrameMs = j2;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean encodeBuffer(boolean z, int i, int i2, long j) {
        checkOnMediaCodecThread();
        try {
            checkKeyFrameRequired(z, j);
            this.mediaCodec.queueInputBuffer(i, 0, i2, j, 0);
            return true;
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "encodeBuffer failed", e);
            return false;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean encodeTexture(boolean z, int i, float[] fArr, long j) {
        checkOnMediaCodecThread();
        try {
            checkKeyFrameRequired(z, j);
            this.eglBase.makeCurrent();
            GLES20.glClear(16384);
            this.drawer.drawOes(i, fArr, this.width, this.height, 0, 0, this.width, this.height);
            this.eglBase.swapBuffers(TimeUnit.MICROSECONDS.toNanos(j));
            return true;
        } catch (RuntimeException e) {
            Logging.m316e(TAG, "encodeTexture failed", e);
            return false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void release() {
        Logging.m314d(TAG, "Java releaseEncoder");
        checkOnMediaCodecThread();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Logging.m314d(MediaCodecVideoEncoder.TAG, "Java releaseEncoder on release thread");
                    MediaCodecVideoEncoder.this.mediaCodec.stop();
                    MediaCodecVideoEncoder.this.mediaCodec.release();
                    Logging.m314d(MediaCodecVideoEncoder.TAG, "Java releaseEncoder on release thread done");
                } catch (Exception e) {
                    Logging.m316e(MediaCodecVideoEncoder.TAG, "Media encoder release failed", e);
                }
                countDownLatch.countDown();
            }
        }).start();
        if (!ThreadUtils.awaitUninterruptibly(countDownLatch, RangedBeacon.DEFAULT_MAX_TRACKING_AGE)) {
            Logging.m315e(TAG, "Media encoder release timeout");
            codecErrors++;
            if (errorCallback != null) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Invoke codec error callback. Errors: ");
                sb.append(codecErrors);
                Logging.m315e(str, sb.toString());
                errorCallback.onMediaCodecVideoEncoderCriticalError(codecErrors);
            }
        }
        this.mediaCodec = null;
        this.mediaCodecThread = null;
        if (this.drawer != null) {
            this.drawer.release();
            this.drawer = null;
        }
        if (this.eglBase != null) {
            this.eglBase.release();
            this.eglBase = null;
        }
        if (this.inputSurface != null) {
            this.inputSurface.release();
            this.inputSurface = null;
        }
        runningInstance = null;
        Logging.m314d(TAG, "Java releaseEncoder done");
    }

    private boolean setRates(int i, int i2) {
        checkOnMediaCodecThread();
        int i3 = i * 1000;
        if (this.bitrateAdjustmentType == BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            double d = (double) i3;
            this.bitrateAccumulatorMax = d / 8.0d;
            if (this.targetBitrateBps > 0 && i3 < this.targetBitrateBps) {
                this.bitrateAccumulator = (this.bitrateAccumulator * d) / ((double) this.targetBitrateBps);
            }
        }
        this.targetBitrateBps = i3;
        this.targetFps = i2;
        if (this.bitrateAdjustmentType == BitrateAdjustmentType.FRAMERATE_ADJUSTMENT && this.targetFps > 0) {
            i3 = (this.targetBitrateBps * 30) / this.targetFps;
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("setRates: ");
            sb.append(i);
            sb.append(" -> ");
            sb.append(i3 / 1000);
            sb.append(" kbps. Fps: ");
            sb.append(this.targetFps);
            Logging.m317v(str, sb.toString());
        } else if (this.bitrateAdjustmentType == BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("setRates: ");
            sb2.append(i);
            sb2.append(" kbps. Fps: ");
            sb2.append(this.targetFps);
            sb2.append(". ExpScale: ");
            sb2.append(this.bitrateAdjustmentScaleExp);
            Logging.m317v(str2, sb2.toString());
            if (this.bitrateAdjustmentScaleExp != 0) {
                i3 = (int) (((double) i3) * getBitrateScale(this.bitrateAdjustmentScaleExp));
            }
        } else {
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("setRates: ");
            sb3.append(i);
            sb3.append(" kbps. Fps: ");
            sb3.append(this.targetFps);
            Logging.m317v(str3, sb3.toString());
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("video-bitrate", i3);
            this.mediaCodec.setParameters(bundle);
            return true;
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "setRates failed", e);
            return false;
        }
    }

    /* access modifiers changed from: 0000 */
    public int dequeueInputBuffer() {
        checkOnMediaCodecThread();
        try {
            return this.mediaCodec.dequeueInputBuffer(0);
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "dequeueIntputBuffer failed", e);
            return -2;
        }
    }

    /* access modifiers changed from: 0000 */
    public OutputBufferInfo dequeueOutputBuffer() {
        checkOnMediaCodecThread();
        try {
            BufferInfo bufferInfo = new BufferInfo();
            int dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            boolean z = true;
            if (dequeueOutputBuffer >= 0) {
                if ((bufferInfo.flags & 2) != 0) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Config frame generated. Offset: ");
                    sb.append(bufferInfo.offset);
                    sb.append(". Size: ");
                    sb.append(bufferInfo.size);
                    Logging.m314d(str, sb.toString());
                    this.configData = ByteBuffer.allocateDirect(bufferInfo.size);
                    this.outputBuffers[dequeueOutputBuffer].position(bufferInfo.offset);
                    this.outputBuffers[dequeueOutputBuffer].limit(bufferInfo.offset + bufferInfo.size);
                    this.configData.put(this.outputBuffers[dequeueOutputBuffer]);
                    this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                    dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                }
            }
            int i = dequeueOutputBuffer;
            if (i >= 0) {
                ByteBuffer duplicate = this.outputBuffers[i].duplicate();
                duplicate.position(bufferInfo.offset);
                duplicate.limit(bufferInfo.offset + bufferInfo.size);
                reportEncodedFrame(bufferInfo.size);
                if ((bufferInfo.flags & 1) == 0) {
                    z = false;
                }
                if (z) {
                    Logging.m314d(TAG, "Sync frame generated");
                }
                if (!z || this.type != VideoCodecType.VIDEO_CODEC_H264) {
                    OutputBufferInfo outputBufferInfo = new OutputBufferInfo(i, duplicate.slice(), z, bufferInfo.presentationTimeUs);
                    return outputBufferInfo;
                }
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Appending config frame of size ");
                sb2.append(this.configData.capacity());
                sb2.append(" to output buffer with offset ");
                sb2.append(bufferInfo.offset);
                sb2.append(", size ");
                sb2.append(bufferInfo.size);
                Logging.m314d(str2, sb2.toString());
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(this.configData.capacity() + bufferInfo.size);
                this.configData.rewind();
                allocateDirect.put(this.configData);
                allocateDirect.put(duplicate);
                allocateDirect.position(0);
                OutputBufferInfo outputBufferInfo2 = new OutputBufferInfo(i, allocateDirect, z, bufferInfo.presentationTimeUs);
                return outputBufferInfo2;
            } else if (i == -3) {
                this.outputBuffers = this.mediaCodec.getOutputBuffers();
                return dequeueOutputBuffer();
            } else if (i == -2) {
                return dequeueOutputBuffer();
            } else {
                if (i == -1) {
                    return null;
                }
                StringBuilder sb3 = new StringBuilder();
                sb3.append("dequeueOutputBuffer: ");
                sb3.append(i);
                throw new RuntimeException(sb3.toString());
            }
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "dequeueOutputBuffer failed", e);
            OutputBufferInfo outputBufferInfo3 = new OutputBufferInfo(-1, null, false, -1);
            return outputBufferInfo3;
        }
    }

    private double getBitrateScale(int i) {
        return Math.pow(BITRATE_CORRECTION_MAX_SCALE, ((double) i) / 20.0d);
    }

    private void reportEncodedFrame(int i) {
        if (this.targetFps != 0 && this.bitrateAdjustmentType == BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            this.bitrateAccumulator += ((double) i) - (((double) this.targetBitrateBps) / (((double) this.targetFps) * 8.0d));
            this.bitrateObservationTimeMs += 1000.0d / ((double) this.targetFps);
            double d = this.bitrateAccumulatorMax * BITRATE_CORRECTION_SEC;
            this.bitrateAccumulator = Math.min(this.bitrateAccumulator, d);
            this.bitrateAccumulator = Math.max(this.bitrateAccumulator, -d);
            if (this.bitrateObservationTimeMs > 3000.0d) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Acc: ");
                sb.append((int) this.bitrateAccumulator);
                sb.append(". Max: ");
                sb.append((int) this.bitrateAccumulatorMax);
                sb.append(". ExpScale: ");
                sb.append(this.bitrateAdjustmentScaleExp);
                Logging.m314d(str, sb.toString());
                boolean z = true;
                if (this.bitrateAccumulator > this.bitrateAccumulatorMax) {
                    this.bitrateAdjustmentScaleExp -= (int) ((this.bitrateAccumulator / this.bitrateAccumulatorMax) + 0.5d);
                    this.bitrateAccumulator = this.bitrateAccumulatorMax;
                } else if (this.bitrateAccumulator < (-this.bitrateAccumulatorMax)) {
                    this.bitrateAdjustmentScaleExp += (int) (((-this.bitrateAccumulator) / this.bitrateAccumulatorMax) + 0.5d);
                    this.bitrateAccumulator = -this.bitrateAccumulatorMax;
                } else {
                    z = false;
                }
                if (z) {
                    this.bitrateAdjustmentScaleExp = Math.min(this.bitrateAdjustmentScaleExp, 20);
                    this.bitrateAdjustmentScaleExp = Math.max(this.bitrateAdjustmentScaleExp, -20);
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Adjusting bitrate scale to ");
                    sb2.append(this.bitrateAdjustmentScaleExp);
                    sb2.append(". Value: ");
                    sb2.append(getBitrateScale(this.bitrateAdjustmentScaleExp));
                    Logging.m314d(str2, sb2.toString());
                    setRates(this.targetBitrateBps / 1000, this.targetFps);
                }
                this.bitrateObservationTimeMs = 0.0d;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean releaseOutputBuffer(int i) {
        checkOnMediaCodecThread();
        try {
            this.mediaCodec.releaseOutputBuffer(i, false);
            return true;
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "releaseOutputBuffer failed", e);
            return false;
        }
    }
}
