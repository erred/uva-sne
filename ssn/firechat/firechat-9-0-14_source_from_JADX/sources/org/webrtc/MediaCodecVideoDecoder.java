package org.webrtc;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.p000v4.provider.FontsContractCompat.FontRequestCallback;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.altbeacon.beacon.service.RangedBeacon;
import org.webrtc.SurfaceTextureHelper.OnTextureFrameAvailableListener;

public class MediaCodecVideoDecoder {
    private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka = 2141391874;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka = 2141391873;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka = 2141391875;
    private static final int DEQUEUE_INPUT_TIMEOUT = 500000;
    private static final String H264_MIME_TYPE = "video/avc";
    private static final long MAX_DECODE_TIME_MS = 200;
    private static final int MAX_QUEUED_OUTPUTBUFFERS = 3;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final String TAG = "MediaCodecVideoDecoder";
    private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
    private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
    private static int codecErrors;
    private static MediaCodecVideoDecoderErrorCallback errorCallback;
    private static Set<String> hwDecoderDisabledTypes = new HashSet();
    private static MediaCodecVideoDecoder runningInstance;
    private static final List<Integer> supportedColorList = Arrays.asList(new Integer[]{Integer.valueOf(19), Integer.valueOf(21), Integer.valueOf(2141391872), Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka), Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka), Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka), Integer.valueOf(COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m)});
    private static final String[] supportedH264HighProfileHwCodecPrefixes = {"OMX.qcom."};
    private static final String[] supportedH264HwCodecPrefixes = {"OMX.qcom.", "OMX.Intel.", "OMX.Exynos."};
    private static final String[] supportedVp8HwCodecPrefixes = {"OMX.qcom.", "OMX.Nvidia.", "OMX.Exynos.", "OMX.Intel."};
    private static final String[] supportedVp9HwCodecPrefixes = {"OMX.qcom.", "OMX.Exynos."};
    private int colorFormat;
    private final Queue<TimeStamps> decodeStartTimeMs = new LinkedList();
    private final Queue<DecodedOutputBuffer> dequeuedSurfaceOutputBuffers = new LinkedList();
    private int droppedFrames;
    private boolean hasDecodedFirstFrame;
    private int height;
    private ByteBuffer[] inputBuffers;
    /* access modifiers changed from: private */
    public MediaCodec mediaCodec;
    private Thread mediaCodecThread;
    private ByteBuffer[] outputBuffers;
    private int sliceHeight;
    private int stride;
    private Surface surface = null;
    private TextureListener textureListener;
    private boolean useSurface;
    private int width;

    private static class DecodedOutputBuffer {
        /* access modifiers changed from: private */
        public final long decodeTimeMs;
        /* access modifiers changed from: private */
        public final long endDecodeTimeMs;
        /* access modifiers changed from: private */
        public final int index;
        /* access modifiers changed from: private */
        public final long ntpTimeStampMs;
        private final int offset;
        /* access modifiers changed from: private */
        public final long presentationTimeStampMs;
        private final int size;
        /* access modifiers changed from: private */
        public final long timeStampMs;

        public DecodedOutputBuffer(int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
            this.index = i;
            this.offset = i2;
            this.size = i3;
            this.presentationTimeStampMs = j;
            this.timeStampMs = j2;
            this.ntpTimeStampMs = j3;
            this.decodeTimeMs = j4;
            this.endDecodeTimeMs = j5;
        }
    }

    private static class DecodedTextureBuffer {
        private final long decodeTimeMs;
        private final long frameDelayMs;
        private final long ntpTimeStampMs;
        private final long presentationTimeStampMs;
        private final int textureID;
        private final long timeStampMs;
        private final float[] transformMatrix;

        public DecodedTextureBuffer(int i, float[] fArr, long j, long j2, long j3, long j4, long j5) {
            this.textureID = i;
            this.transformMatrix = fArr;
            this.presentationTimeStampMs = j;
            this.timeStampMs = j2;
            this.ntpTimeStampMs = j3;
            this.decodeTimeMs = j4;
            this.frameDelayMs = j5;
        }
    }

    private static class DecoderProperties {
        public final String codecName;
        public final int colorFormat;

        public DecoderProperties(String str, int i) {
            this.codecName = str;
            this.colorFormat = i;
        }
    }

    public interface MediaCodecVideoDecoderErrorCallback {
        void onMediaCodecVideoDecoderCriticalError(int i);
    }

    private static class TextureListener implements OnTextureFrameAvailableListener {
        private DecodedOutputBuffer bufferToRender;
        private final Object newFrameLock = new Object();
        private DecodedTextureBuffer renderedBuffer;
        private final SurfaceTextureHelper surfaceTextureHelper;

        public TextureListener(SurfaceTextureHelper surfaceTextureHelper2) {
            this.surfaceTextureHelper = surfaceTextureHelper2;
            surfaceTextureHelper2.startListening(this);
        }

        public void addBufferToRender(DecodedOutputBuffer decodedOutputBuffer) {
            if (this.bufferToRender != null) {
                Logging.m315e(MediaCodecVideoDecoder.TAG, "Unexpected addBufferToRender() called while waiting for a texture.");
                throw new IllegalStateException("Waiting for a texture.");
            } else {
                this.bufferToRender = decodedOutputBuffer;
            }
        }

        public boolean isWaitingForTexture() {
            boolean z;
            synchronized (this.newFrameLock) {
                z = this.bufferToRender != null;
            }
            return z;
        }

        /* JADX INFO: finally extract failed */
        public void onTextureFrameAvailable(int i, float[] fArr, long j) {
            synchronized (this.newFrameLock) {
                try {
                    if (this.renderedBuffer != null) {
                        Logging.m315e(MediaCodecVideoDecoder.TAG, "Unexpected onTextureFrameAvailable() called while already holding a texture.");
                        throw new IllegalStateException("Already holding a texture.");
                    }
                    DecodedTextureBuffer decodedTextureBuffer = new DecodedTextureBuffer(i, fArr, this.bufferToRender.presentationTimeStampMs, this.bufferToRender.timeStampMs, this.bufferToRender.ntpTimeStampMs, this.bufferToRender.decodeTimeMs, SystemClock.elapsedRealtime() - this.bufferToRender.endDecodeTimeMs);
                    this.renderedBuffer = decodedTextureBuffer;
                    this.bufferToRender = null;
                    this.newFrameLock.notifyAll();
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(4:8|9|10|11) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0016 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.webrtc.MediaCodecVideoDecoder.DecodedTextureBuffer dequeueTextureBuffer(int r5) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.newFrameLock
                monitor-enter(r0)
                org.webrtc.MediaCodecVideoDecoder$DecodedTextureBuffer r1 = r4.renderedBuffer     // Catch:{ all -> 0x0024 }
                if (r1 != 0) goto L_0x001d
                if (r5 <= 0) goto L_0x001d
                boolean r1 = r4.isWaitingForTexture()     // Catch:{ all -> 0x0024 }
                if (r1 == 0) goto L_0x001d
                java.lang.Object r1 = r4.newFrameLock     // Catch:{ InterruptedException -> 0x0016 }
                long r2 = (long) r5     // Catch:{ InterruptedException -> 0x0016 }
                r1.wait(r2)     // Catch:{ InterruptedException -> 0x0016 }
                goto L_0x001d
            L_0x0016:
                java.lang.Thread r5 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0024 }
                r5.interrupt()     // Catch:{ all -> 0x0024 }
            L_0x001d:
                org.webrtc.MediaCodecVideoDecoder$DecodedTextureBuffer r5 = r4.renderedBuffer     // Catch:{ all -> 0x0024 }
                r1 = 0
                r4.renderedBuffer = r1     // Catch:{ all -> 0x0024 }
                monitor-exit(r0)     // Catch:{ all -> 0x0024 }
                return r5
            L_0x0024:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0024 }
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.MediaCodecVideoDecoder.TextureListener.dequeueTextureBuffer(int):org.webrtc.MediaCodecVideoDecoder$DecodedTextureBuffer");
        }

        public void release() {
            this.surfaceTextureHelper.stopListening();
            synchronized (this.newFrameLock) {
                if (this.renderedBuffer != null) {
                    this.surfaceTextureHelper.returnTextureFrame();
                    this.renderedBuffer = null;
                }
            }
        }
    }

    private static class TimeStamps {
        /* access modifiers changed from: private */
        public final long decodeStartTimeMs;
        /* access modifiers changed from: private */
        public final long ntpTimeStampMs;
        /* access modifiers changed from: private */
        public final long timeStampMs;

        public TimeStamps(long j, long j2, long j3) {
            this.decodeStartTimeMs = j;
            this.timeStampMs = j2;
            this.ntpTimeStampMs = j3;
        }
    }

    public enum VideoCodecType {
        VIDEO_CODEC_VP8,
        VIDEO_CODEC_VP9,
        VIDEO_CODEC_H264
    }

    public static void setErrorCallback(MediaCodecVideoDecoderErrorCallback mediaCodecVideoDecoderErrorCallback) {
        Logging.m314d(TAG, "Set error callback");
        errorCallback = mediaCodecVideoDecoderErrorCallback;
    }

    public static void disableVp8HwCodec() {
        Logging.m318w(TAG, "VP8 decoding is disabled by application.");
        hwDecoderDisabledTypes.add(VP8_MIME_TYPE);
    }

    public static void disableVp9HwCodec() {
        Logging.m318w(TAG, "VP9 decoding is disabled by application.");
        hwDecoderDisabledTypes.add(VP9_MIME_TYPE);
    }

    public static void disableH264HwCodec() {
        Logging.m318w(TAG, "H.264 decoding is disabled by application.");
        hwDecoderDisabledTypes.add(H264_MIME_TYPE);
    }

    public static boolean isVp8HwSupported() {
        return !hwDecoderDisabledTypes.contains(VP8_MIME_TYPE) && findDecoder(VP8_MIME_TYPE, supportedVp8HwCodecPrefixes) != null;
    }

    public static boolean isVp9HwSupported() {
        return !hwDecoderDisabledTypes.contains(VP9_MIME_TYPE) && findDecoder(VP9_MIME_TYPE, supportedVp9HwCodecPrefixes) != null;
    }

    public static boolean isH264HwSupported() {
        return !hwDecoderDisabledTypes.contains(H264_MIME_TYPE) && findDecoder(H264_MIME_TYPE, supportedH264HwCodecPrefixes) != null;
    }

    public static boolean isH264HighProfileHwSupported() {
        return VERSION.SDK_INT >= 21 && !hwDecoderDisabledTypes.contains(H264_MIME_TYPE) && findDecoder(H264_MIME_TYPE, supportedH264HighProfileHwCodecPrefixes) != null;
    }

    public static void printStackTrace() {
        if (runningInstance != null && runningInstance.mediaCodecThread != null) {
            StackTraceElement[] stackTrace = runningInstance.mediaCodecThread.getStackTrace();
            if (stackTrace.length > 0) {
                Logging.m314d(TAG, "MediaCodecVideoDecoder stacks trace:");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.m314d(TAG, stackTraceElement.toString());
                }
            }
        }
    }

    private static DecoderProperties findDecoder(String str, String[] strArr) {
        MediaCodecInfo mediaCodecInfo;
        String str2;
        boolean z;
        int[] iArr;
        if (VERSION.SDK_INT < 19) {
            return null;
        }
        String str3 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Trying to find HW decoder for mime ");
        sb.append(str);
        Logging.m314d(str3, sb.toString());
        for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
            try {
                mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.m316e(TAG, "Cannot retrieve decoder codec info", e);
                mediaCodecInfo = null;
            }
            if (mediaCodecInfo != null && !mediaCodecInfo.isEncoder()) {
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
                    String str4 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Found candidate decoder ");
                    sb2.append(str2);
                    Logging.m314d(str4, sb2.toString());
                    int length2 = strArr.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length2) {
                            z = false;
                            break;
                        } else if (str2.startsWith(strArr[i3])) {
                            z = true;
                            break;
                        } else {
                            i3++;
                        }
                    }
                    if (!z) {
                        continue;
                    } else {
                        try {
                            CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
                            for (int i4 : capabilitiesForType.colorFormats) {
                                String str5 = TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("   Color: 0x");
                                sb3.append(Integer.toHexString(i4));
                                Logging.m317v(str5, sb3.toString());
                            }
                            for (Integer intValue : supportedColorList) {
                                int intValue2 = intValue.intValue();
                                int[] iArr2 = capabilitiesForType.colorFormats;
                                int length3 = iArr2.length;
                                int i5 = 0;
                                while (true) {
                                    if (i5 < length3) {
                                        int i6 = iArr2[i5];
                                        if (i6 == intValue2) {
                                            String str6 = TAG;
                                            StringBuilder sb4 = new StringBuilder();
                                            sb4.append("Found target decoder ");
                                            sb4.append(str2);
                                            sb4.append(". Color: 0x");
                                            sb4.append(Integer.toHexString(i6));
                                            Logging.m314d(str6, sb4.toString());
                                            return new DecoderProperties(str2, i6);
                                        }
                                        i5++;
                                    }
                                }
                            }
                            continue;
                        } catch (IllegalArgumentException e2) {
                            Logging.m316e(TAG, "Cannot retrieve decoder capabilities", e2);
                        }
                    }
                }
            }
        }
        String str7 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("No HW decoder found for mime ");
        sb5.append(str);
        Logging.m314d(str7, sb5.toString());
        return null;
    }

    private void checkOnMediaCodecThread() throws IllegalStateException {
        if (this.mediaCodecThread.getId() != Thread.currentThread().getId()) {
            StringBuilder sb = new StringBuilder();
            sb.append("MediaCodecVideoDecoder previously operated on ");
            sb.append(this.mediaCodecThread);
            sb.append(" but is now called on ");
            sb.append(Thread.currentThread());
            throw new IllegalStateException(sb.toString());
        }
    }

    private boolean initDecode(VideoCodecType videoCodecType, int i, int i2, SurfaceTextureHelper surfaceTextureHelper) {
        String[] strArr;
        String str;
        if (this.mediaCodecThread != null) {
            throw new RuntimeException("initDecode: Forgot to release()?");
        }
        this.useSurface = surfaceTextureHelper != null;
        if (videoCodecType == VideoCodecType.VIDEO_CODEC_VP8) {
            str = VP8_MIME_TYPE;
            strArr = supportedVp8HwCodecPrefixes;
        } else if (videoCodecType == VideoCodecType.VIDEO_CODEC_VP9) {
            str = VP9_MIME_TYPE;
            strArr = supportedVp9HwCodecPrefixes;
        } else if (videoCodecType == VideoCodecType.VIDEO_CODEC_H264) {
            str = H264_MIME_TYPE;
            strArr = supportedH264HwCodecPrefixes;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("initDecode: Non-supported codec ");
            sb.append(videoCodecType);
            throw new RuntimeException(sb.toString());
        }
        DecoderProperties findDecoder = findDecoder(str, strArr);
        if (findDecoder == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot find HW decoder for ");
            sb2.append(videoCodecType);
            throw new RuntimeException(sb2.toString());
        }
        String str2 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Java initDecode: ");
        sb3.append(videoCodecType);
        sb3.append(" : ");
        sb3.append(i);
        sb3.append(" x ");
        sb3.append(i2);
        sb3.append(". Color: 0x");
        sb3.append(Integer.toHexString(findDecoder.colorFormat));
        sb3.append(". Use Surface: ");
        sb3.append(this.useSurface);
        Logging.m314d(str2, sb3.toString());
        runningInstance = this;
        this.mediaCodecThread = Thread.currentThread();
        try {
            this.width = i;
            this.height = i2;
            this.stride = i;
            this.sliceHeight = i2;
            if (this.useSurface) {
                this.textureListener = new TextureListener(surfaceTextureHelper);
                this.surface = new Surface(surfaceTextureHelper.getSurfaceTexture());
            }
            MediaFormat createVideoFormat = MediaFormat.createVideoFormat(str, i, i2);
            if (!this.useSurface) {
                createVideoFormat.setInteger("color-format", findDecoder.colorFormat);
            }
            String str3 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  Format: ");
            sb4.append(createVideoFormat);
            Logging.m314d(str3, sb4.toString());
            this.mediaCodec = MediaCodecVideoEncoder.createByCodecName(findDecoder.codecName);
            if (this.mediaCodec == null) {
                Logging.m315e(TAG, "Can not create media decoder");
                return false;
            }
            this.mediaCodec.configure(createVideoFormat, this.surface, null, 0);
            this.mediaCodec.start();
            this.colorFormat = findDecoder.colorFormat;
            this.outputBuffers = this.mediaCodec.getOutputBuffers();
            this.inputBuffers = this.mediaCodec.getInputBuffers();
            this.decodeStartTimeMs.clear();
            this.hasDecodedFirstFrame = false;
            this.dequeuedSurfaceOutputBuffers.clear();
            this.droppedFrames = 0;
            String str4 = TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Input buffers: ");
            sb5.append(this.inputBuffers.length);
            sb5.append(". Output buffers: ");
            sb5.append(this.outputBuffers.length);
            Logging.m314d(str4, sb5.toString());
            return true;
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "initDecode failed", e);
            return false;
        }
    }

    private void reset(int i, int i2) {
        if (this.mediaCodecThread == null || this.mediaCodec == null) {
            throw new RuntimeException("Incorrect reset call for non-initialized decoder.");
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Java reset: ");
        sb.append(i);
        sb.append(" x ");
        sb.append(i2);
        Logging.m314d(str, sb.toString());
        this.mediaCodec.flush();
        this.width = i;
        this.height = i2;
        this.decodeStartTimeMs.clear();
        this.dequeuedSurfaceOutputBuffers.clear();
        this.hasDecodedFirstFrame = false;
        this.droppedFrames = 0;
    }

    private void release() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Java releaseDecoder. Total number of dropped frames: ");
        sb.append(this.droppedFrames);
        Logging.m314d(str, sb.toString());
        checkOnMediaCodecThread();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Logging.m314d(MediaCodecVideoDecoder.TAG, "Java releaseDecoder on release thread");
                    MediaCodecVideoDecoder.this.mediaCodec.stop();
                    MediaCodecVideoDecoder.this.mediaCodec.release();
                    Logging.m314d(MediaCodecVideoDecoder.TAG, "Java releaseDecoder on release thread done");
                } catch (Exception e) {
                    Logging.m316e(MediaCodecVideoDecoder.TAG, "Media decoder release failed", e);
                }
                countDownLatch.countDown();
            }
        }).start();
        if (!ThreadUtils.awaitUninterruptibly(countDownLatch, RangedBeacon.DEFAULT_MAX_TRACKING_AGE)) {
            Logging.m315e(TAG, "Media decoder release timeout");
            codecErrors++;
            if (errorCallback != null) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Invoke codec error callback. Errors: ");
                sb2.append(codecErrors);
                Logging.m315e(str2, sb2.toString());
                errorCallback.onMediaCodecVideoDecoderCriticalError(codecErrors);
            }
        }
        this.mediaCodec = null;
        this.mediaCodecThread = null;
        runningInstance = null;
        if (this.useSurface) {
            this.surface.release();
            this.surface = null;
            this.textureListener.release();
        }
        Logging.m314d(TAG, "Java releaseDecoder done");
    }

    private int dequeueInputBuffer() {
        checkOnMediaCodecThread();
        try {
            return this.mediaCodec.dequeueInputBuffer(500000);
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "dequeueIntputBuffer failed", e);
            return -2;
        }
    }

    private boolean queueInputBuffer(int i, int i2, long j, long j2, long j3) {
        checkOnMediaCodecThread();
        try {
            this.inputBuffers[i].position(0);
            int i3 = i2;
            this.inputBuffers[i].limit(i3);
            Queue<TimeStamps> queue = this.decodeStartTimeMs;
            TimeStamps timeStamps = new TimeStamps(SystemClock.elapsedRealtime(), j2, j3);
            queue.add(timeStamps);
            this.mediaCodec.queueInputBuffer(i, 0, i3, j, 0);
            return true;
        } catch (IllegalStateException e) {
            Logging.m316e(TAG, "decode failed", e);
            return false;
        }
    }

    private DecodedOutputBuffer dequeueOutputBuffer(int i) {
        long j;
        int integer;
        int integer2;
        checkOnMediaCodecThread();
        if (this.decodeStartTimeMs.isEmpty()) {
            return null;
        }
        BufferInfo bufferInfo = new BufferInfo();
        while (true) {
            int dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(bufferInfo, TimeUnit.MILLISECONDS.toMicros((long) i));
            switch (dequeueOutputBuffer) {
                case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                    this.outputBuffers = this.mediaCodec.getOutputBuffers();
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Decoder output buffers changed: ");
                    sb.append(this.outputBuffers.length);
                    Logging.m314d(str, sb.toString());
                    if (!this.hasDecodedFirstFrame) {
                        break;
                    } else {
                        throw new RuntimeException("Unexpected output buffer change event.");
                    }
                case -2:
                    MediaFormat outputFormat = this.mediaCodec.getOutputFormat();
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Decoder format changed: ");
                    sb2.append(outputFormat.toString());
                    Logging.m314d(str2, sb2.toString());
                    integer = outputFormat.getInteger("width");
                    integer2 = outputFormat.getInteger("height");
                    if (!this.hasDecodedFirstFrame || (integer == this.width && integer2 == this.height)) {
                        this.width = outputFormat.getInteger("width");
                        this.height = outputFormat.getInteger("height");
                        if (!this.useSurface && outputFormat.containsKey("color-format")) {
                            this.colorFormat = outputFormat.getInteger("color-format");
                            String str3 = TAG;
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Color: 0x");
                            sb3.append(Integer.toHexString(this.colorFormat));
                            Logging.m314d(str3, sb3.toString());
                            if (!supportedColorList.contains(Integer.valueOf(this.colorFormat))) {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("Non supported color format: ");
                                sb4.append(this.colorFormat);
                                throw new IllegalStateException(sb4.toString());
                            }
                        }
                        if (outputFormat.containsKey("stride")) {
                            this.stride = outputFormat.getInteger("stride");
                        }
                        if (outputFormat.containsKey("slice-height")) {
                            this.sliceHeight = outputFormat.getInteger("slice-height");
                        }
                        String str4 = TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("Frame stride and slice height: ");
                        sb5.append(this.stride);
                        sb5.append(" x ");
                        sb5.append(this.sliceHeight);
                        Logging.m314d(str4, sb5.toString());
                        this.stride = Math.max(this.width, this.stride);
                        this.sliceHeight = Math.max(this.height, this.sliceHeight);
                        break;
                    }
                    break;
                case -1:
                    return null;
                default:
                    this.hasDecodedFirstFrame = true;
                    TimeStamps timeStamps = (TimeStamps) this.decodeStartTimeMs.remove();
                    long elapsedRealtime = SystemClock.elapsedRealtime() - timeStamps.decodeStartTimeMs;
                    if (elapsedRealtime > MAX_DECODE_TIME_MS) {
                        String str5 = TAG;
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("Very high decode time: ");
                        sb6.append(elapsedRealtime);
                        sb6.append("ms. Q size: ");
                        sb6.append(this.decodeStartTimeMs.size());
                        sb6.append(". Might be caused by resuming H264 decoding after a pause.");
                        Logging.m315e(str5, sb6.toString());
                        j = 200;
                    } else {
                        j = elapsedRealtime;
                    }
                    DecodedOutputBuffer decodedOutputBuffer = new DecodedOutputBuffer(dequeueOutputBuffer, bufferInfo.offset, bufferInfo.size, TimeUnit.MICROSECONDS.toMillis(bufferInfo.presentationTimeUs), timeStamps.timeStampMs, timeStamps.ntpTimeStampMs, j, SystemClock.elapsedRealtime());
                    return decodedOutputBuffer;
            }
        }
        StringBuilder sb7 = new StringBuilder();
        sb7.append("Unexpected size change. Configured ");
        sb7.append(this.width);
        sb7.append("*");
        sb7.append(this.height);
        sb7.append(". New ");
        sb7.append(integer);
        sb7.append("*");
        sb7.append(integer2);
        throw new RuntimeException(sb7.toString());
    }

    private DecodedTextureBuffer dequeueTextureBuffer(int i) {
        int i2 = i;
        checkOnMediaCodecThread();
        if (!this.useSurface) {
            throw new IllegalStateException("dequeueTexture() called for byte buffer decoding.");
        }
        DecodedOutputBuffer dequeueOutputBuffer = dequeueOutputBuffer(i);
        if (dequeueOutputBuffer != null) {
            this.dequeuedSurfaceOutputBuffers.add(dequeueOutputBuffer);
        }
        MaybeRenderDecodedTextureBuffer();
        DecodedTextureBuffer dequeueTextureBuffer = this.textureListener.dequeueTextureBuffer(i2);
        if (dequeueTextureBuffer != null) {
            MaybeRenderDecodedTextureBuffer();
            return dequeueTextureBuffer;
        } else if (this.dequeuedSurfaceOutputBuffers.size() < Math.min(3, this.outputBuffers.length) && (i2 <= 0 || this.dequeuedSurfaceOutputBuffers.isEmpty())) {
            return null;
        } else {
            this.droppedFrames++;
            DecodedOutputBuffer decodedOutputBuffer = (DecodedOutputBuffer) this.dequeuedSurfaceOutputBuffers.remove();
            if (i2 > 0) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Draining decoder. Dropping frame with TS: ");
                sb.append(decodedOutputBuffer.presentationTimeStampMs);
                sb.append(". Total number of dropped frames: ");
                sb.append(this.droppedFrames);
                Logging.m318w(str, sb.toString());
            } else {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Too many output buffers ");
                sb2.append(this.dequeuedSurfaceOutputBuffers.size());
                sb2.append(". Dropping frame with TS: ");
                sb2.append(decodedOutputBuffer.presentationTimeStampMs);
                sb2.append(". Total number of dropped frames: ");
                sb2.append(this.droppedFrames);
                Logging.m318w(str2, sb2.toString());
            }
            this.mediaCodec.releaseOutputBuffer(decodedOutputBuffer.index, false);
            DecodedTextureBuffer decodedTextureBuffer = new DecodedTextureBuffer(0, null, decodedOutputBuffer.presentationTimeStampMs, decodedOutputBuffer.timeStampMs, decodedOutputBuffer.ntpTimeStampMs, decodedOutputBuffer.decodeTimeMs, SystemClock.elapsedRealtime() - decodedOutputBuffer.endDecodeTimeMs);
            return decodedTextureBuffer;
        }
    }

    private void MaybeRenderDecodedTextureBuffer() {
        if (!this.dequeuedSurfaceOutputBuffers.isEmpty() && !this.textureListener.isWaitingForTexture()) {
            DecodedOutputBuffer decodedOutputBuffer = (DecodedOutputBuffer) this.dequeuedSurfaceOutputBuffers.remove();
            this.textureListener.addBufferToRender(decodedOutputBuffer);
            this.mediaCodec.releaseOutputBuffer(decodedOutputBuffer.index, true);
        }
    }

    private void returnDecodedOutputBuffer(int i) throws IllegalStateException, CodecException {
        checkOnMediaCodecThread();
        if (this.useSurface) {
            throw new IllegalStateException("returnDecodedOutputBuffer() called for surface decoding.");
        }
        this.mediaCodec.releaseOutputBuffer(i, false);
    }
}
