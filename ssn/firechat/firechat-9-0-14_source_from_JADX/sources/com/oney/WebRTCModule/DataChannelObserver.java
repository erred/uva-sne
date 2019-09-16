package com.oney.WebRTCModule;

import android.support.annotation.Nullable;
import android.util.Base64;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import java.nio.charset.Charset;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;
import org.webrtc.DataChannel;
import org.webrtc.DataChannel.Buffer;
import org.webrtc.DataChannel.Observer;
import org.webrtc.DataChannel.State;

class DataChannelObserver implements Observer {
    private final DataChannel mDataChannel;
    private final int mId;
    private final int peerConnectionId;
    private final C1267WebRTCModule webRTCModule;

    public void onBufferedAmountChange(long j) {
    }

    DataChannelObserver(C1267WebRTCModule webRTCModule2, int i, int i2, DataChannel dataChannel) {
        this.webRTCModule = webRTCModule2;
        this.peerConnectionId = i;
        this.mId = i2;
        this.mDataChannel = dataChannel;
    }

    @Nullable
    private String dataChannelStateString(State state) {
        switch (state) {
            case CONNECTING:
                return "connecting";
            case OPEN:
                return AbstractCircuitBreaker.PROPERTY_NAME;
            case CLOSING:
                return "closing";
            case CLOSED:
                return "closed";
            default:
                return null;
        }
    }

    public void onMessage(Buffer buffer) {
        byte[] bArr;
        String str;
        String str2;
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.mId);
        createMap.putInt("peerConnectionId", this.peerConnectionId);
        if (buffer.data.hasArray()) {
            bArr = buffer.data.array();
        } else {
            bArr = new byte[buffer.data.remaining()];
            buffer.data.get(bArr);
        }
        if (buffer.binary) {
            str = "binary";
            str2 = Base64.encodeToString(bArr, 2);
        } else {
            str = "text";
            str2 = new String(bArr, Charset.forName("UTF-8"));
        }
        createMap.putString("type", str);
        createMap.putString("data", str2);
        this.webRTCModule.sendEvent("dataChannelReceiveMessage", createMap);
    }

    public void onStateChange() {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.mId);
        createMap.putInt("peerConnectionId", this.peerConnectionId);
        createMap.putString("state", dataChannelStateString(this.mDataChannel.state()));
        this.webRTCModule.sendEvent("dataChannelStateChanged", createMap);
    }
}
