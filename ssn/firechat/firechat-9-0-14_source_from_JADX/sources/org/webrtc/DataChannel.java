package org.webrtc;

import java.nio.ByteBuffer;

public class DataChannel {
    private final long nativeDataChannel;
    private long nativeObserver;

    public static class Buffer {
        public final boolean binary;
        public final ByteBuffer data;

        public Buffer(ByteBuffer byteBuffer, boolean z) {
            this.data = byteBuffer;
            this.binary = z;
        }
    }

    public static class Init {

        /* renamed from: id */
        public int f171id = -1;
        public int maxRetransmitTimeMs = -1;
        public int maxRetransmits = -1;
        public boolean negotiated = false;
        public boolean ordered = true;
        public String protocol = "";

        public Init() {
        }

        private Init(boolean z, int i, int i2, String str, boolean z2, int i3) {
            this.ordered = z;
            this.maxRetransmitTimeMs = i;
            this.maxRetransmits = i2;
            this.protocol = str;
            this.negotiated = z2;
            this.f171id = i3;
        }
    }

    public interface Observer {
        void onBufferedAmountChange(long j);

        void onMessage(Buffer buffer);

        void onStateChange();
    }

    public enum State {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED
    }

    private native long registerObserverNative(Observer observer);

    private native boolean sendNative(byte[] bArr, boolean z);

    private native void unregisterObserverNative(long j);

    public native long bufferedAmount();

    public native void close();

    public native void dispose();

    /* renamed from: id */
    public native int mo27779id();

    public native String label();

    public native State state();

    public DataChannel(long j) {
        this.nativeDataChannel = j;
    }

    public void registerObserver(Observer observer) {
        if (this.nativeObserver != 0) {
            unregisterObserverNative(this.nativeObserver);
        }
        this.nativeObserver = registerObserverNative(observer);
    }

    public void unregisterObserver() {
        unregisterObserverNative(this.nativeObserver);
    }

    public boolean send(Buffer buffer) {
        byte[] bArr = new byte[buffer.data.remaining()];
        buffer.data.get(bArr);
        return sendNative(bArr, buffer.binary);
    }
}
