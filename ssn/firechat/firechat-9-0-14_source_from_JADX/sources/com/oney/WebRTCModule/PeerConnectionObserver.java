package com.oney.WebRTCModule;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.DataChannel.Buffer;
import org.webrtc.DataChannel.Init;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.RtpReceiver;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.StatsReport.Value;
import org.webrtc.VideoTrack;

class PeerConnectionObserver implements Observer {
    private static final String TAG = C1267WebRTCModule.TAG;
    private final SparseArray<DataChannel> dataChannels = new SparseArray<>();

    /* renamed from: id */
    private final int f111id;
    final List<MediaStream> localStreams;
    private PeerConnection peerConnection;
    final Map<String, MediaStream> remoteStreams;
    final Map<String, MediaStreamTrack> remoteTracks;
    private SoftReference<StringBuilder> statsToJSONStringBuilder = new SoftReference<>(null);
    private final C1267WebRTCModule webRTCModule;

    public void onIceConnectionReceivingChange(boolean z) {
    }

    PeerConnectionObserver(C1267WebRTCModule webRTCModule2, int i) {
        this.webRTCModule = webRTCModule2;
        this.f111id = i;
        this.localStreams = new ArrayList();
        this.remoteStreams = new HashMap();
        this.remoteTracks = new HashMap();
    }

    /* access modifiers changed from: 0000 */
    public boolean addStream(MediaStream mediaStream) {
        if (this.peerConnection == null || !this.peerConnection.addStream(mediaStream)) {
            return false;
        }
        this.localStreams.add(mediaStream);
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean removeStream(MediaStream mediaStream) {
        if (this.peerConnection != null) {
            this.peerConnection.removeStream(mediaStream);
        }
        return this.localStreams.remove(mediaStream);
    }

    /* access modifiers changed from: 0000 */
    public PeerConnection getPeerConnection() {
        return this.peerConnection;
    }

    /* access modifiers changed from: 0000 */
    public void setPeerConnection(PeerConnection peerConnection2) {
        this.peerConnection = peerConnection2;
    }

    /* access modifiers changed from: 0000 */
    public void close() {
        this.peerConnection.close();
        Iterator it = new ArrayList(this.localStreams).iterator();
        while (it.hasNext()) {
            removeStream((MediaStream) it.next());
        }
        this.peerConnection.dispose();
        this.remoteStreams.clear();
        this.remoteTracks.clear();
        this.dataChannels.clear();
    }

    /* access modifiers changed from: 0000 */
    public void createDataChannel(String str, ReadableMap readableMap) {
        Init init = new Init();
        if (readableMap != null) {
            if (readableMap.hasKey("id")) {
                init.f171id = readableMap.getInt("id");
            }
            if (readableMap.hasKey("ordered")) {
                init.ordered = readableMap.getBoolean("ordered");
            }
            if (readableMap.hasKey("maxRetransmitTime")) {
                init.maxRetransmitTimeMs = readableMap.getInt("maxRetransmitTime");
            }
            if (readableMap.hasKey("maxRetransmits")) {
                init.maxRetransmits = readableMap.getInt("maxRetransmits");
            }
            if (readableMap.hasKey("protocol")) {
                init.protocol = readableMap.getString("protocol");
            }
            if (readableMap.hasKey("negotiated")) {
                init.negotiated = readableMap.getBoolean("negotiated");
            }
        }
        DataChannel createDataChannel = this.peerConnection.createDataChannel(str, init);
        int i = init.f171id;
        if (-1 != i) {
            this.dataChannels.put(i, createDataChannel);
            registerDataChannelObserver(i, createDataChannel);
        }
    }

    /* access modifiers changed from: 0000 */
    public void dataChannelClose(int i) {
        DataChannel dataChannel = (DataChannel) this.dataChannels.get(i);
        if (dataChannel != null) {
            dataChannel.close();
            this.dataChannels.remove(i);
            return;
        }
        Log.d(TAG, "dataChannelClose() dataChannel is null");
    }

    /* access modifiers changed from: 0000 */
    public void dataChannelSend(int i, String str, String str2) {
        byte[] bArr;
        DataChannel dataChannel = (DataChannel) this.dataChannels.get(i);
        if (dataChannel != null) {
            if (str2.equals("text")) {
                try {
                    bArr = str.getBytes("UTF-8");
                } catch (UnsupportedEncodingException unused) {
                    Log.d(TAG, "Could not encode text string as UTF-8.");
                    return;
                }
            } else if (str2.equals("binary")) {
                bArr = Base64.decode(str, 2);
            } else {
                String str3 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Unsupported data type: ");
                sb.append(str2);
                Log.e(str3, sb.toString());
                return;
            }
            dataChannel.send(new Buffer(ByteBuffer.wrap(bArr), str2.equals("binary")));
        } else {
            Log.d(TAG, "dataChannelSend() dataChannel is null");
        }
    }

    /* access modifiers changed from: 0000 */
    public void getStats(String str, final Callback callback) {
        MediaStreamTrack mediaStreamTrack;
        if (str == null || str.isEmpty()) {
            mediaStreamTrack = null;
        } else {
            mediaStreamTrack = this.webRTCModule.getLocalTrack(str);
            if (mediaStreamTrack == null) {
                mediaStreamTrack = (MediaStreamTrack) this.remoteTracks.get(str);
                if (mediaStreamTrack == null) {
                    String str2 = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("peerConnectionGetStats() MediaStreamTrack not found for id: ");
                    sb.append(str);
                    Log.e(str2, sb.toString());
                    return;
                }
            }
        }
        this.peerConnection.getStats(new StatsObserver() {
            public void onComplete(StatsReport[] statsReportArr) {
                callback.invoke(PeerConnectionObserver.this.statsToJSON(statsReportArr));
            }
        }, mediaStreamTrack);
    }

    /* access modifiers changed from: private */
    public String statsToJSON(StatsReport[] statsReportArr) {
        StringBuilder sb = (StringBuilder) this.statsToJSONStringBuilder.get();
        if (sb == null) {
            sb = new StringBuilder();
            this.statsToJSONStringBuilder = new SoftReference<>(sb);
        }
        sb.append('[');
        int length = statsReportArr.length;
        for (int i = 0; i < length; i++) {
            StatsReport statsReport = statsReportArr[i];
            if (i != 0) {
                sb.append(',');
            }
            sb.append("{\"id\":\"");
            sb.append(statsReport.f172id);
            sb.append("\",\"type\":\"");
            sb.append(statsReport.type);
            sb.append("\",\"timestamp\":");
            sb.append(statsReport.timestamp);
            sb.append(",\"values\":[");
            Value[] valueArr = statsReport.values;
            int length2 = valueArr.length;
            for (int i2 = 0; i2 < length2; i2++) {
                Value value = valueArr[i2];
                if (i2 != 0) {
                    sb.append(',');
                }
                sb.append("{\"");
                sb.append(value.name);
                sb.append("\":\"");
                sb.append(value.value);
                sb.append("\"}");
            }
            sb.append("]}");
        }
        sb.append("]");
        String sb2 = sb.toString();
        sb.setLength(0);
        return sb2;
    }

    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "onIceCandidate");
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        WritableMap createMap2 = Arguments.createMap();
        createMap2.putInt("sdpMLineIndex", iceCandidate.sdpMLineIndex);
        createMap2.putString("sdpMid", iceCandidate.sdpMid);
        createMap2.putString("candidate", iceCandidate.sdp);
        createMap.putMap("candidate", createMap2);
        this.webRTCModule.sendEvent("peerConnectionGotICECandidate", createMap);
    }

    public void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr) {
        Log.d(TAG, "onIceCandidatesRemoved");
    }

    public void onIceConnectionChange(IceConnectionState iceConnectionState) {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        createMap.putString("iceConnectionState", iceConnectionStateString(iceConnectionState));
        this.webRTCModule.sendEvent("peerConnectionIceConnectionChanged", createMap);
    }

    public void onIceGatheringChange(IceGatheringState iceGatheringState) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onIceGatheringChange");
        sb.append(iceGatheringState.name());
        Log.d(str, sb.toString());
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        createMap.putString("iceGatheringState", iceGatheringStateString(iceGatheringState));
        this.webRTCModule.sendEvent("peerConnectionIceGatheringChanged", createMap);
    }

    private String getReactTagForStream(MediaStream mediaStream) {
        for (Entry entry : this.remoteStreams.entrySet()) {
            if (((MediaStream) entry.getValue()).equals(mediaStream)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0067 A[LOOP:1: B:12:0x005e->B:14:0x0067, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00ba A[LOOP:2: B:15:0x00b2->B:17:0x00ba, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onAddStream(org.webrtc.MediaStream r10) {
        /*
            r9 = this;
            java.lang.String r0 = r10.label()
            java.lang.String r1 = "default"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0035
            java.util.Map<java.lang.String, org.webrtc.MediaStream> r1 = r9.remoteStreams
            java.util.Set r1 = r1.entrySet()
            java.util.Iterator r1 = r1.iterator()
        L_0x0016:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0035
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getValue()
            org.webrtc.MediaStream r3 = (org.webrtc.MediaStream) r3
            boolean r3 = r3.equals(r10)
            if (r3 == 0) goto L_0x0016
            java.lang.Object r1 = r2.getKey()
            java.lang.String r1 = (java.lang.String) r1
            goto L_0x0036
        L_0x0035:
            r1 = 0
        L_0x0036:
            if (r1 != 0) goto L_0x0043
            com.oney.WebRTCModule.WebRTCModule r1 = r9.webRTCModule
            java.lang.String r1 = r1.getNextStreamUUID()
            java.util.Map<java.lang.String, org.webrtc.MediaStream> r2 = r9.remoteStreams
            r2.put(r1, r10)
        L_0x0043:
            com.facebook.react.bridge.WritableMap r2 = com.facebook.react.bridge.Arguments.createMap()
            java.lang.String r3 = "id"
            int r4 = r9.f111id
            r2.putInt(r3, r4)
            java.lang.String r3 = "streamId"
            r2.putString(r3, r0)
            java.lang.String r0 = "streamReactTag"
            r2.putString(r0, r1)
            com.facebook.react.bridge.WritableArray r0 = com.facebook.react.bridge.Arguments.createArray()
            r1 = 0
            r3 = 0
        L_0x005e:
            java.util.LinkedList<org.webrtc.VideoTrack> r4 = r10.videoTracks
            int r4 = r4.size()
            r5 = 1
            if (r3 >= r4) goto L_0x00b2
            java.util.LinkedList<org.webrtc.VideoTrack> r4 = r10.videoTracks
            java.lang.Object r4 = r4.get(r3)
            org.webrtc.VideoTrack r4 = (org.webrtc.VideoTrack) r4
            java.lang.String r6 = r4.mo27904id()
            java.util.Map<java.lang.String, org.webrtc.MediaStreamTrack> r7 = r9.remoteTracks
            r7.put(r6, r4)
            com.facebook.react.bridge.WritableMap r7 = com.facebook.react.bridge.Arguments.createMap()
            java.lang.String r8 = "id"
            r7.putString(r8, r6)
            java.lang.String r6 = "label"
            java.lang.String r8 = "Video"
            r7.putString(r6, r8)
            java.lang.String r6 = "kind"
            java.lang.String r8 = r4.kind()
            r7.putString(r6, r8)
            java.lang.String r6 = "enabled"
            boolean r8 = r4.enabled()
            r7.putBoolean(r6, r8)
            java.lang.String r6 = "readyState"
            org.webrtc.MediaStreamTrack$State r4 = r4.state()
            java.lang.String r4 = r4.toString()
            r7.putString(r6, r4)
            java.lang.String r4 = "remote"
            r7.putBoolean(r4, r5)
            r0.pushMap(r7)
            int r3 = r3 + 1
            goto L_0x005e
        L_0x00b2:
            java.util.LinkedList<org.webrtc.AudioTrack> r3 = r10.audioTracks
            int r3 = r3.size()
            if (r1 >= r3) goto L_0x0105
            java.util.LinkedList<org.webrtc.AudioTrack> r3 = r10.audioTracks
            java.lang.Object r3 = r3.get(r1)
            org.webrtc.AudioTrack r3 = (org.webrtc.AudioTrack) r3
            java.lang.String r4 = r3.mo27904id()
            java.util.Map<java.lang.String, org.webrtc.MediaStreamTrack> r6 = r9.remoteTracks
            r6.put(r4, r3)
            com.facebook.react.bridge.WritableMap r6 = com.facebook.react.bridge.Arguments.createMap()
            java.lang.String r7 = "id"
            r6.putString(r7, r4)
            java.lang.String r4 = "label"
            java.lang.String r7 = "Audio"
            r6.putString(r4, r7)
            java.lang.String r4 = "kind"
            java.lang.String r7 = r3.kind()
            r6.putString(r4, r7)
            java.lang.String r4 = "enabled"
            boolean r7 = r3.enabled()
            r6.putBoolean(r4, r7)
            java.lang.String r4 = "readyState"
            org.webrtc.MediaStreamTrack$State r3 = r3.state()
            java.lang.String r3 = r3.toString()
            r6.putString(r4, r3)
            java.lang.String r3 = "remote"
            r6.putBoolean(r3, r5)
            r0.pushMap(r6)
            int r1 = r1 + 1
            goto L_0x00b2
        L_0x0105:
            java.lang.String r10 = "tracks"
            r2.putArray(r10, r0)
            com.oney.WebRTCModule.WebRTCModule r10 = r9.webRTCModule
            java.lang.String r0 = "peerConnectionAddedStream"
            r10.sendEvent(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.PeerConnectionObserver.onAddStream(org.webrtc.MediaStream):void");
    }

    public void onRemoveStream(MediaStream mediaStream) {
        String reactTagForStream = getReactTagForStream(mediaStream);
        if (reactTagForStream == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRemoveStream - no remote stream for id: ");
            sb.append(mediaStream.label());
            Log.w(str, sb.toString());
            return;
        }
        Iterator it = mediaStream.videoTracks.iterator();
        while (it.hasNext()) {
            this.remoteTracks.remove(((VideoTrack) it.next()).mo27904id());
        }
        Iterator it2 = mediaStream.audioTracks.iterator();
        while (it2.hasNext()) {
            this.remoteTracks.remove(((AudioTrack) it2.next()).mo27904id());
        }
        this.remoteStreams.remove(reactTagForStream);
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        createMap.putString("streamId", reactTagForStream);
        this.webRTCModule.sendEvent("peerConnectionRemovedStream", createMap);
    }

    public void onDataChannel(DataChannel dataChannel) {
        int i = 65536;
        while (true) {
            if (i > Integer.MAX_VALUE) {
                i = -1;
                break;
            } else if (this.dataChannels.get(i, null) == null) {
                break;
            } else {
                i++;
            }
        }
        if (-1 != i) {
            WritableMap createMap = Arguments.createMap();
            createMap.putInt("id", i);
            createMap.putString("label", dataChannel.label());
            WritableMap createMap2 = Arguments.createMap();
            createMap2.putInt("id", this.f111id);
            createMap2.putMap("dataChannel", createMap);
            this.dataChannels.put(i, dataChannel);
            registerDataChannelObserver(i, dataChannel);
            this.webRTCModule.sendEvent("peerConnectionDidOpenDataChannel", createMap2);
        }
    }

    private void registerDataChannelObserver(int i, DataChannel dataChannel) {
        dataChannel.registerObserver(new DataChannelObserver(this.webRTCModule, this.f111id, i, dataChannel));
    }

    public void onRenegotiationNeeded() {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        this.webRTCModule.sendEvent("peerConnectionOnRenegotiationNeeded", createMap);
    }

    public void onSignalingChange(SignalingState signalingState) {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", this.f111id);
        createMap.putString("signalingState", signalingStateString(signalingState));
        this.webRTCModule.sendEvent("peerConnectionSignalingStateChanged", createMap);
    }

    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreamArr) {
        Log.d(TAG, "onAddTrack");
    }

    @Nullable
    private String iceConnectionStateString(IceConnectionState iceConnectionState) {
        switch (iceConnectionState) {
            case NEW:
                return "new";
            case CHECKING:
                return "checking";
            case CONNECTED:
                return "connected";
            case COMPLETED:
                return "completed";
            case FAILED:
                return "failed";
            case DISCONNECTED:
                return "disconnected";
            case CLOSED:
                return "closed";
            default:
                return null;
        }
    }

    @Nullable
    private String iceGatheringStateString(IceGatheringState iceGatheringState) {
        switch (iceGatheringState) {
            case NEW:
                return "new";
            case GATHERING:
                return "gathering";
            case COMPLETE:
                return "complete";
            default:
                return null;
        }
    }

    @Nullable
    private String signalingStateString(SignalingState signalingState) {
        switch (signalingState) {
            case STABLE:
                return "stable";
            case HAVE_LOCAL_OFFER:
                return "have-local-offer";
            case HAVE_LOCAL_PRANSWER:
                return "have-local-pranswer";
            case HAVE_REMOTE_OFFER:
                return "have-remote-offer";
            case HAVE_REMOTE_PRANSWER:
                return "have-remote-pranswer";
            case CLOSED:
                return "closed";
            default:
                return null;
        }
    }
}
