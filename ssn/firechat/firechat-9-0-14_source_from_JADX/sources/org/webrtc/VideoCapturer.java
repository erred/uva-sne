package org.webrtc;

import android.content.Context;

public interface VideoCapturer {

    public static class AndroidVideoTrackSourceObserver implements CapturerObserver {
        private final long nativeSource;

        private native void nativeCapturerStarted(long j, boolean z);

        private native void nativeCapturerStopped(long j);

        private native void nativeOnByteBufferFrameCaptured(long j, byte[] bArr, int i, int i2, int i3, int i4, long j2);

        private native void nativeOnTextureFrameCaptured(long j, int i, int i2, int i3, float[] fArr, int i4, long j2);

        public AndroidVideoTrackSourceObserver(long j) {
            this.nativeSource = j;
        }

        public void onCapturerStarted(boolean z) {
            nativeCapturerStarted(this.nativeSource, z);
        }

        public void onCapturerStopped() {
            nativeCapturerStopped(this.nativeSource);
        }

        public void onByteBufferFrameCaptured(byte[] bArr, int i, int i2, int i3, long j) {
            byte[] bArr2 = bArr;
            nativeOnByteBufferFrameCaptured(this.nativeSource, bArr2, bArr2.length, i, i2, i3, j);
        }

        public void onTextureFrameCaptured(int i, int i2, int i3, float[] fArr, int i4, long j) {
            nativeOnTextureFrameCaptured(this.nativeSource, i, i2, i3, fArr, i4, j);
        }
    }

    public interface CapturerObserver {
        void onByteBufferFrameCaptured(byte[] bArr, int i, int i2, int i3, long j);

        void onCapturerStarted(boolean z);

        void onCapturerStopped();

        void onTextureFrameCaptured(int i, int i2, int i3, float[] fArr, int i4, long j);
    }

    void changeCaptureFormat(int i, int i2, int i3);

    void dispose();

    void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver);

    boolean isScreencast();

    void startCapture(int i, int i2, int i3);

    void stopCapture() throws InterruptedException;
}
