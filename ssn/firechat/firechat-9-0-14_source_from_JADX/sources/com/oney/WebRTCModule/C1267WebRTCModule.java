package com.oney.WebRTCModule;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase.Context;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;
import org.webrtc.VideoTrack;

/* renamed from: com.oney.WebRTCModule.WebRTCModule */
public class C1267WebRTCModule extends ReactContextBaseJavaModule {
    private static final String LANGUAGE = "language";
    static final String TAG = C1267WebRTCModule.class.getCanonicalName();
    private final GetUserMediaImpl getUserMediaImpl;
    final Map<String, MediaStream> localStreams = new HashMap();
    final PeerConnectionFactory mFactory;
    private final SparseArray<PeerConnectionObserver> mPeerConnectionObservers = new SparseArray<>();

    public String getName() {
        return "WebRTCModule";
    }

    public C1267WebRTCModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        PeerConnectionFactory.initializeAndroidGlobals(reactApplicationContext, true, true, true);
        this.mFactory = new PeerConnectionFactory(null);
        Context rootEglBaseContext = EglUtils.getRootEglBaseContext();
        if (rootEglBaseContext != null) {
            this.mFactory.setVideoHwAccelerationOptions(rootEglBaseContext, rootEglBaseContext);
        }
        this.getUserMediaImpl = new GetUserMediaImpl(this, reactApplicationContext);
    }

    private String getCurrentLanguage() {
        return getReactApplicationContext().getResources().getConfiguration().locale.getLanguage();
    }

    public Map<String, Object> getConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put(LANGUAGE, getCurrentLanguage());
        return hashMap;
    }

    @ReactMethod
    public void getLanguage(Callback callback) {
        String currentLanguage = getCurrentLanguage();
        PrintStream printStream = System.out;
        StringBuilder sb = new StringBuilder();
        sb.append("The current language is ");
        sb.append(currentLanguage);
        printStream.println(sb.toString());
        callback.invoke(null, currentLanguage);
    }

    private PeerConnection getPeerConnection(int i) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null) {
            return null;
        }
        return peerConnectionObserver.getPeerConnection();
    }

    /* access modifiers changed from: 0000 */
    public void sendEvent(String str, @Nullable WritableMap writableMap) {
        ((RCTDeviceEventEmitter) getReactApplicationContext().getJSModule(RCTDeviceEventEmitter.class)).emit(str, writableMap);
    }

    private List<IceServer> createIceServers(ReadableArray readableArray) {
        int size = readableArray == null ? 0 : readableArray.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            ReadableMap map = readableArray.getMap(i);
            boolean z = map.hasKey("username") && map.hasKey("credential");
            if (map.hasKey(ImagesContract.URL)) {
                if (z) {
                    arrayList.add(new IceServer(map.getString(ImagesContract.URL), map.getString("username"), map.getString("credential")));
                } else {
                    arrayList.add(new IceServer(map.getString(ImagesContract.URL)));
                }
            } else if (map.hasKey("urls")) {
                switch (map.getType("urls")) {
                    case String:
                        if (!z) {
                            arrayList.add(new IceServer(map.getString("urls")));
                            break;
                        } else {
                            arrayList.add(new IceServer(map.getString("urls"), map.getString("username"), map.getString("credential")));
                            break;
                        }
                    case Array:
                        ReadableArray array = map.getArray("urls");
                        for (int i2 = 0; i2 < array.size(); i2++) {
                            String string = array.getString(i2);
                            if (z) {
                                arrayList.add(new IceServer(string, map.getString("username"), map.getString("credential")));
                            } else {
                                arrayList.add(new IceServer(string));
                            }
                        }
                        break;
                }
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0269, code lost:
        if (r0.equals("gather_once") != false) goto L_0x026d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00cb, code lost:
        if (r0.equals("max-bundle") != false) goto L_0x00d9;
     */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0199  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0227  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0271  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0276  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0132  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.webrtc.PeerConnection.RTCConfiguration parseRTCConfiguration(com.facebook.react.bridge.ReadableMap r10) {
        /*
            r9 = this;
            if (r10 == 0) goto L_0x0009
            java.lang.String r0 = "iceServers"
            com.facebook.react.bridge.ReadableArray r0 = r10.getArray(r0)
            goto L_0x000a
        L_0x0009:
            r0 = 0
        L_0x000a:
            java.util.List r0 = r9.createIceServers(r0)
            org.webrtc.PeerConnection$RTCConfiguration r1 = new org.webrtc.PeerConnection$RTCConfiguration
            r1.<init>(r0)
            if (r10 != 0) goto L_0x0016
            return r1
        L_0x0016:
            java.lang.String r0 = "iceTransportPolicy"
            boolean r0 = r10.hasKey(r0)
            r2 = 2
            r3 = 96673(0x179a1, float:1.35468E-40)
            r4 = 0
            r5 = 1
            r6 = -1
            if (r0 == 0) goto L_0x008d
            java.lang.String r0 = "iceTransportPolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r7 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r7) goto L_0x008d
            java.lang.String r0 = "iceTransportPolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x008d
            int r7 = r0.hashCode()
            r8 = -1040041239(0xffffffffc2023ae9, float:-32.55753)
            if (r7 == r8) goto L_0x006b
            if (r7 == r3) goto L_0x0061
            r8 = 3387192(0x33af38, float:4.746467E-39)
            if (r7 == r8) goto L_0x0057
            r8 = 108397201(0x6760291, float:4.6269343E-35)
            if (r7 == r8) goto L_0x004d
            goto L_0x0075
        L_0x004d:
            java.lang.String r7 = "relay"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0075
            r0 = 1
            goto L_0x0076
        L_0x0057:
            java.lang.String r7 = "none"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0075
            r0 = 3
            goto L_0x0076
        L_0x0061:
            java.lang.String r7 = "all"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0075
            r0 = 0
            goto L_0x0076
        L_0x006b:
            java.lang.String r7 = "nohost"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0075
            r0 = 2
            goto L_0x0076
        L_0x0075:
            r0 = -1
        L_0x0076:
            switch(r0) {
                case 0: goto L_0x0089;
                case 1: goto L_0x0084;
                case 2: goto L_0x007f;
                case 3: goto L_0x007a;
                default: goto L_0x0079;
            }
        L_0x0079:
            goto L_0x008d
        L_0x007a:
            org.webrtc.PeerConnection$IceTransportsType r0 = org.webrtc.PeerConnection.IceTransportsType.NONE
            r1.iceTransportsType = r0
            goto L_0x008d
        L_0x007f:
            org.webrtc.PeerConnection$IceTransportsType r0 = org.webrtc.PeerConnection.IceTransportsType.NOHOST
            r1.iceTransportsType = r0
            goto L_0x008d
        L_0x0084:
            org.webrtc.PeerConnection$IceTransportsType r0 = org.webrtc.PeerConnection.IceTransportsType.RELAY
            r1.iceTransportsType = r0
            goto L_0x008d
        L_0x0089:
            org.webrtc.PeerConnection$IceTransportsType r0 = org.webrtc.PeerConnection.IceTransportsType.ALL
            r1.iceTransportsType = r0
        L_0x008d:
            java.lang.String r0 = "bundlePolicy"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x00eb
            java.lang.String r0 = "bundlePolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r7 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r7) goto L_0x00eb
            java.lang.String r0 = "bundlePolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x00eb
            int r7 = r0.hashCode()
            r8 = -1924829944(0xffffffff8d456d08, float:-6.0836553E-31)
            if (r7 == r8) goto L_0x00ce
            r8 = -585638645(0xffffffffdd17dd0b, float:-6.8393217E17)
            if (r7 == r8) goto L_0x00c5
            r2 = -562569205(0xffffffffde77e00b, float:-4.46532205E18)
            if (r7 == r2) goto L_0x00bb
            goto L_0x00d8
        L_0x00bb:
            java.lang.String r2 = "max-compat"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x00d8
            r2 = 1
            goto L_0x00d9
        L_0x00c5:
            java.lang.String r7 = "max-bundle"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00d8
            goto L_0x00d9
        L_0x00ce:
            java.lang.String r2 = "balanced"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x00d8
            r2 = 0
            goto L_0x00d9
        L_0x00d8:
            r2 = -1
        L_0x00d9:
            switch(r2) {
                case 0: goto L_0x00e7;
                case 1: goto L_0x00e2;
                case 2: goto L_0x00dd;
                default: goto L_0x00dc;
            }
        L_0x00dc:
            goto L_0x00eb
        L_0x00dd:
            org.webrtc.PeerConnection$BundlePolicy r0 = org.webrtc.PeerConnection.BundlePolicy.MAXBUNDLE
            r1.bundlePolicy = r0
            goto L_0x00eb
        L_0x00e2:
            org.webrtc.PeerConnection$BundlePolicy r0 = org.webrtc.PeerConnection.BundlePolicy.MAXCOMPAT
            r1.bundlePolicy = r0
            goto L_0x00eb
        L_0x00e7:
            org.webrtc.PeerConnection$BundlePolicy r0 = org.webrtc.PeerConnection.BundlePolicy.BALANCED
            r1.bundlePolicy = r0
        L_0x00eb:
            java.lang.String r0 = "rtcpMuxPolicy"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x0136
            java.lang.String r0 = "rtcpMuxPolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r2) goto L_0x0136
            java.lang.String r0 = "rtcpMuxPolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x0136
            int r2 = r0.hashCode()
            r7 = -1109522818(0xffffffffbdde067e, float:-0.10841082)
            if (r2 == r7) goto L_0x011e
            r7 = 1095696741(0x414f0165, float:12.93784)
            if (r2 == r7) goto L_0x0114
            goto L_0x0128
        L_0x0114:
            java.lang.String r2 = "require"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0128
            r0 = 1
            goto L_0x0129
        L_0x011e:
            java.lang.String r2 = "negotiate"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0128
            r0 = 0
            goto L_0x0129
        L_0x0128:
            r0 = -1
        L_0x0129:
            switch(r0) {
                case 0: goto L_0x0132;
                case 1: goto L_0x012d;
                default: goto L_0x012c;
            }
        L_0x012c:
            goto L_0x0136
        L_0x012d:
            org.webrtc.PeerConnection$RtcpMuxPolicy r0 = org.webrtc.PeerConnection.RtcpMuxPolicy.REQUIRE
            r1.rtcpMuxPolicy = r0
            goto L_0x0136
        L_0x0132:
            org.webrtc.PeerConnection$RtcpMuxPolicy r0 = org.webrtc.PeerConnection.RtcpMuxPolicy.NEGOTIATE
            r1.rtcpMuxPolicy = r0
        L_0x0136:
            java.lang.String r0 = "iceCandidatePoolSize"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x0152
            java.lang.String r0 = "iceCandidatePoolSize"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Number
            if (r0 != r2) goto L_0x0152
            java.lang.String r0 = "iceCandidatePoolSize"
            int r0 = r10.getInt(r0)
            if (r0 <= 0) goto L_0x0152
            r1.iceCandidatePoolSize = r0
        L_0x0152:
            java.lang.String r0 = "tcpCandidatePolicy"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x019d
            java.lang.String r0 = "tcpCandidatePolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r2) goto L_0x019d
            java.lang.String r0 = "tcpCandidatePolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x019d
            int r2 = r0.hashCode()
            r7 = -1609594047(0xffffffffa00f8b41, float:-1.2158646E-19)
            if (r2 == r7) goto L_0x0185
            r7 = 270940796(0x10263a7c, float:3.2782782E-29)
            if (r2 == r7) goto L_0x017b
            goto L_0x018f
        L_0x017b:
            java.lang.String r2 = "disabled"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x018f
            r0 = 1
            goto L_0x0190
        L_0x0185:
            java.lang.String r2 = "enabled"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x018f
            r0 = 0
            goto L_0x0190
        L_0x018f:
            r0 = -1
        L_0x0190:
            switch(r0) {
                case 0: goto L_0x0199;
                case 1: goto L_0x0194;
                default: goto L_0x0193;
            }
        L_0x0193:
            goto L_0x019d
        L_0x0194:
            org.webrtc.PeerConnection$TcpCandidatePolicy r0 = org.webrtc.PeerConnection.TcpCandidatePolicy.DISABLED
            r1.tcpCandidatePolicy = r0
            goto L_0x019d
        L_0x0199:
            org.webrtc.PeerConnection$TcpCandidatePolicy r0 = org.webrtc.PeerConnection.TcpCandidatePolicy.ENABLED
            r1.tcpCandidatePolicy = r0
        L_0x019d:
            java.lang.String r0 = "candidateNetworkPolicy"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x01e5
            java.lang.String r0 = "candidateNetworkPolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r2) goto L_0x01e5
            java.lang.String r0 = "candidateNetworkPolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x01e5
            int r2 = r0.hashCode()
            r7 = -1823688232(0xffffffff934cb9d8, float:-2.5840048E-27)
            if (r2 == r7) goto L_0x01cd
            if (r2 == r3) goto L_0x01c3
            goto L_0x01d7
        L_0x01c3:
            java.lang.String r2 = "all"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01d7
            r0 = 0
            goto L_0x01d8
        L_0x01cd:
            java.lang.String r2 = "low_cost"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01d7
            r0 = 1
            goto L_0x01d8
        L_0x01d7:
            r0 = -1
        L_0x01d8:
            switch(r0) {
                case 0: goto L_0x01e1;
                case 1: goto L_0x01dc;
                default: goto L_0x01db;
            }
        L_0x01db:
            goto L_0x01e5
        L_0x01dc:
            org.webrtc.PeerConnection$CandidateNetworkPolicy r0 = org.webrtc.PeerConnection.CandidateNetworkPolicy.LOW_COST
            r1.candidateNetworkPolicy = r0
            goto L_0x01e5
        L_0x01e1:
            org.webrtc.PeerConnection$CandidateNetworkPolicy r0 = org.webrtc.PeerConnection.CandidateNetworkPolicy.ALL
            r1.candidateNetworkPolicy = r0
        L_0x01e5:
            java.lang.String r0 = "keyType"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x0230
            java.lang.String r0 = "keyType"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r2) goto L_0x0230
            java.lang.String r0 = "keyType"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x0230
            int r2 = r0.hashCode()
            r3 = 81440(0x13e20, float:1.14122E-40)
            if (r2 == r3) goto L_0x0218
            r3 = 65786932(0x3ebd434, float:1.3860778E-36)
            if (r2 == r3) goto L_0x020e
            goto L_0x0222
        L_0x020e:
            java.lang.String r2 = "ECDSA"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0222
            r0 = 1
            goto L_0x0223
        L_0x0218:
            java.lang.String r2 = "RSA"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0222
            r0 = 0
            goto L_0x0223
        L_0x0222:
            r0 = -1
        L_0x0223:
            switch(r0) {
                case 0: goto L_0x022c;
                case 1: goto L_0x0227;
                default: goto L_0x0226;
            }
        L_0x0226:
            goto L_0x0230
        L_0x0227:
            org.webrtc.PeerConnection$KeyType r0 = org.webrtc.PeerConnection.KeyType.ECDSA
            r1.keyType = r0
            goto L_0x0230
        L_0x022c:
            org.webrtc.PeerConnection$KeyType r0 = org.webrtc.PeerConnection.KeyType.RSA
            r1.keyType = r0
        L_0x0230:
            java.lang.String r0 = "continualGatheringPolicy"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x027a
            java.lang.String r0 = "continualGatheringPolicy"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.String
            if (r0 != r2) goto L_0x027a
            java.lang.String r0 = "continualGatheringPolicy"
            java.lang.String r0 = r10.getString(r0)
            if (r0 == 0) goto L_0x027a
            int r2 = r0.hashCode()
            r3 = -2128544187(0xffffffff8120fe45, float:-2.9569788E-38)
            if (r2 == r3) goto L_0x0263
            r3 = 1217112882(0x488bab32, float:286041.56)
            if (r2 == r3) goto L_0x0259
            goto L_0x026c
        L_0x0259:
            java.lang.String r2 = "gather_continually"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x026c
            r4 = 1
            goto L_0x026d
        L_0x0263:
            java.lang.String r2 = "gather_once"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x026c
            goto L_0x026d
        L_0x026c:
            r4 = -1
        L_0x026d:
            switch(r4) {
                case 0: goto L_0x0276;
                case 1: goto L_0x0271;
                default: goto L_0x0270;
            }
        L_0x0270:
            goto L_0x027a
        L_0x0271:
            org.webrtc.PeerConnection$ContinualGatheringPolicy r0 = org.webrtc.PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            r1.continualGatheringPolicy = r0
            goto L_0x027a
        L_0x0276:
            org.webrtc.PeerConnection$ContinualGatheringPolicy r0 = org.webrtc.PeerConnection.ContinualGatheringPolicy.GATHER_ONCE
            r1.continualGatheringPolicy = r0
        L_0x027a:
            java.lang.String r0 = "audioJitterBufferMaxPackets"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x0296
            java.lang.String r0 = "audioJitterBufferMaxPackets"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Number
            if (r0 != r2) goto L_0x0296
            java.lang.String r0 = "audioJitterBufferMaxPackets"
            int r0 = r10.getInt(r0)
            if (r0 <= 0) goto L_0x0296
            r1.audioJitterBufferMaxPackets = r0
        L_0x0296:
            java.lang.String r0 = "iceConnectionReceivingTimeout"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x02b0
            java.lang.String r0 = "iceConnectionReceivingTimeout"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Number
            if (r0 != r2) goto L_0x02b0
            java.lang.String r0 = "iceConnectionReceivingTimeout"
            int r0 = r10.getInt(r0)
            r1.iceConnectionReceivingTimeout = r0
        L_0x02b0:
            java.lang.String r0 = "iceBackupCandidatePairPingInterval"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x02ca
            java.lang.String r0 = "iceBackupCandidatePairPingInterval"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Number
            if (r0 != r2) goto L_0x02ca
            java.lang.String r0 = "iceBackupCandidatePairPingInterval"
            int r0 = r10.getInt(r0)
            r1.iceBackupCandidatePairPingInterval = r0
        L_0x02ca:
            java.lang.String r0 = "audioJitterBufferFastAccelerate"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x02e4
            java.lang.String r0 = "audioJitterBufferFastAccelerate"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Boolean
            if (r0 != r2) goto L_0x02e4
            java.lang.String r0 = "audioJitterBufferFastAccelerate"
            boolean r0 = r10.getBoolean(r0)
            r1.audioJitterBufferFastAccelerate = r0
        L_0x02e4:
            java.lang.String r0 = "pruneTurnPorts"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x02fe
            java.lang.String r0 = "pruneTurnPorts"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Boolean
            if (r0 != r2) goto L_0x02fe
            java.lang.String r0 = "pruneTurnPorts"
            boolean r0 = r10.getBoolean(r0)
            r1.pruneTurnPorts = r0
        L_0x02fe:
            java.lang.String r0 = "presumeWritableWhenFullyRelayed"
            boolean r0 = r10.hasKey(r0)
            if (r0 == 0) goto L_0x0318
            java.lang.String r0 = "presumeWritableWhenFullyRelayed"
            com.facebook.react.bridge.ReadableType r0 = r10.getType(r0)
            com.facebook.react.bridge.ReadableType r2 = com.facebook.react.bridge.ReadableType.Boolean
            if (r0 != r2) goto L_0x0318
            java.lang.String r0 = "presumeWritableWhenFullyRelayed"
            boolean r10 = r10.getBoolean(r0)
            r1.presumeWritableWhenFullyRelayed = r10
        L_0x0318:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.C1267WebRTCModule.parseRTCConfiguration(com.facebook.react.bridge.ReadableMap):org.webrtc.PeerConnection$RTCConfiguration");
    }

    @ReactMethod
    public void peerConnectionInit(ReadableMap readableMap, ReadableMap readableMap2, int i) {
        PeerConnectionObserver peerConnectionObserver = new PeerConnectionObserver(this, i);
        peerConnectionObserver.setPeerConnection(this.mFactory.createPeerConnection(parseRTCConfiguration(readableMap), parseMediaConstraints(readableMap2), (Observer) peerConnectionObserver));
        this.mPeerConnectionObservers.put(i, peerConnectionObserver);
    }

    /* access modifiers changed from: 0000 */
    public String getNextStreamUUID() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (getStreamForReactTag(uuid) != null);
        return uuid;
    }

    /* access modifiers changed from: 0000 */
    public String getNextTrackUUID() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (getTrack(uuid) != null);
        return uuid;
    }

    /* access modifiers changed from: 0000 */
    public MediaStream getStreamForReactTag(String str) {
        MediaStream mediaStream = (MediaStream) this.localStreams.get(str);
        if (mediaStream == null) {
            int size = this.mPeerConnectionObservers.size();
            for (int i = 0; i < size; i++) {
                mediaStream = (MediaStream) ((PeerConnectionObserver) this.mPeerConnectionObservers.valueAt(i)).remoteStreams.get(str);
                if (mediaStream != null) {
                    break;
                }
            }
        }
        return mediaStream;
    }

    private MediaStreamTrack getTrack(String str) {
        MediaStreamTrack localTrack = getLocalTrack(str);
        if (localTrack == null) {
            int size = this.mPeerConnectionObservers.size();
            for (int i = 0; i < size; i++) {
                localTrack = (MediaStreamTrack) ((PeerConnectionObserver) this.mPeerConnectionObservers.valueAt(i)).remoteTracks.get(str);
                if (localTrack != null) {
                    break;
                }
            }
        }
        return localTrack;
    }

    /* access modifiers changed from: 0000 */
    public MediaStreamTrack getLocalTrack(String str) {
        return this.getUserMediaImpl.getTrack(str);
    }

    private static MediaStreamTrack getLocalTrack(MediaStream mediaStream, String str) {
        Iterator it = mediaStream.audioTracks.iterator();
        while (it.hasNext()) {
            AudioTrack audioTrack = (AudioTrack) it.next();
            if (audioTrack.mo27904id().equals(str)) {
                return audioTrack;
            }
        }
        Iterator it2 = mediaStream.videoTracks.iterator();
        while (it2.hasNext()) {
            VideoTrack videoTrack = (VideoTrack) it2.next();
            if (videoTrack.mo27904id().equals(str)) {
                return videoTrack;
            }
        }
        return null;
    }

    private void parseConstraints(ReadableMap readableMap, List<KeyValuePair> list) {
        ReadableMapKeySetIterator keySetIterator = readableMap.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            list.add(new KeyValuePair(nextKey, ReactBridgeUtil.getMapStrValue(readableMap, nextKey)));
        }
    }

    /* access modifiers changed from: 0000 */
    public MediaConstraints parseMediaConstraints(ReadableMap readableMap) {
        MediaConstraints mediaConstraints = new MediaConstraints();
        if (!readableMap.hasKey("mandatory") || readableMap.getType("mandatory") != ReadableType.Map) {
            Log.d(TAG, "mandatory constraints are not a map");
        } else {
            parseConstraints(readableMap.getMap("mandatory"), mediaConstraints.mandatory);
        }
        if (!readableMap.hasKey("optional") || readableMap.getType("optional") != ReadableType.Array) {
            Log.d(TAG, "optional constraints are not an array");
        } else {
            ReadableArray array = readableMap.getArray("optional");
            int size = array.size();
            for (int i = 0; i < size; i++) {
                if (array.getType(i) == ReadableType.Map) {
                    parseConstraints(array.getMap(i), mediaConstraints.optional);
                }
            }
        }
        return mediaConstraints;
    }

    @ReactMethod
    public void getUserMedia(ReadableMap readableMap, Callback callback, Callback callback2) {
        MediaStream createLocalMediaStream = this.mFactory.createLocalMediaStream(getNextStreamUUID());
        if (createLocalMediaStream == null) {
            callback2.invoke(null, "Failed to create new media stream");
            return;
        }
        this.getUserMediaImpl.getUserMedia(readableMap, callback, callback2, createLocalMediaStream);
    }

    @ReactMethod
    public void mediaStreamRelease(String str) {
        MediaStream mediaStream = (MediaStream) this.localStreams.get(str);
        if (mediaStream == null) {
            Log.d(TAG, "mediaStreamRelease() stream is null");
            return;
        }
        ArrayList<MediaStreamTrack> arrayList = new ArrayList<>(mediaStream.audioTracks.size() + mediaStream.videoTracks.size());
        arrayList.addAll(mediaStream.audioTracks);
        arrayList.addAll(mediaStream.videoTracks);
        for (MediaStreamTrack id : arrayList) {
            mediaStreamTrackRelease(str, id.mo27904id());
        }
        this.localStreams.remove(str);
        int size = this.mPeerConnectionObservers.size();
        for (int i = 0; i < size; i++) {
            ((PeerConnectionObserver) this.mPeerConnectionObservers.valueAt(i)).removeStream(mediaStream);
        }
        mediaStream.dispose();
    }

    @ReactMethod
    public void mediaStreamTrackGetSources(Callback callback) {
        WritableArray createArray = Arguments.createArray();
        String[] strArr = new String[Camera.getNumberOfCameras()];
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            WritableMap cameraInfo = getCameraInfo(i);
            if (cameraInfo != null) {
                createArray.pushMap(cameraInfo);
            }
        }
        WritableMap createMap = Arguments.createMap();
        createMap.putString("label", "Audio");
        createMap.putString("id", "audio-1");
        createMap.putString("facing", "");
        createMap.putString("kind", "audio");
        createArray.pushMap(createMap);
        callback.invoke(createArray);
    }

    @ReactMethod
    public void mediaStreamTrackRelease(String str, String str2) {
        MediaStream mediaStream = (MediaStream) this.localStreams.get(str);
        if (mediaStream == null) {
            Log.d(TAG, "mediaStreamTrackRelease() stream is null");
            return;
        }
        MediaStreamTrack localTrack = getLocalTrack(str2);
        if (localTrack == null) {
            localTrack = getLocalTrack(mediaStream, str2);
            if (localTrack == null) {
                String str3 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("mediaStreamTrackRelease() No local MediaStreamTrack with id ");
                sb.append(str2);
                Log.d(str3, sb.toString());
                return;
            }
        } else {
            mediaStreamTrackStop(str2);
        }
        String kind = localTrack.kind();
        if ("audio".equals(kind)) {
            mediaStream.removeTrack((AudioTrack) localTrack);
        } else if ("video".equals(kind)) {
            mediaStream.removeTrack((VideoTrack) localTrack);
        }
        localTrack.dispose();
    }

    @ReactMethod
    public void mediaStreamTrackSetEnabled(String str, boolean z) {
        MediaStreamTrack localTrack = getLocalTrack(str);
        if (localTrack == null) {
            Log.d(TAG, "mediaStreamTrackSetEnabled() track is null");
        } else if (localTrack.enabled() != z) {
            localTrack.setEnabled(z);
        }
    }

    @ReactMethod
    public void mediaStreamTrackStop(String str) {
        this.getUserMediaImpl.mediaStreamTrackStop(str);
    }

    @ReactMethod
    public void mediaStreamTrackSwitchCamera(String str) {
        if (getLocalTrack(str) != null) {
            this.getUserMediaImpl.switchCamera(str);
        }
    }

    public WritableMap getCameraInfo(int i) {
        CameraInfo cameraInfo = new CameraInfo();
        try {
            Camera.getCameraInfo(i, cameraInfo);
            WritableMap createMap = Arguments.createMap();
            String str = cameraInfo.facing == 1 ? "front" : "back";
            StringBuilder sb = new StringBuilder();
            sb.append("Camera ");
            sb.append(i);
            sb.append(", Facing ");
            sb.append(str);
            sb.append(", Orientation ");
            sb.append(cameraInfo.orientation);
            createMap.putString("label", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(i);
            createMap.putString("id", sb2.toString());
            createMap.putString("facing", str);
            createMap.putString("kind", "video");
            return createMap;
        } catch (Exception e) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("getCameraInfo failed on index ");
            sb3.append(i);
            Logging.m316e("CameraEnumerationAndroid", sb3.toString(), e);
            return null;
        }
    }

    private MediaConstraints defaultConstraints() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        return mediaConstraints;
    }

    @ReactMethod
    public void peerConnectionSetConfiguration(ReadableMap readableMap, int i) {
        PeerConnection peerConnection = getPeerConnection(i);
        if (peerConnection == null) {
            Log.d(TAG, "peerConnectionSetConfiguration() peerConnection is null");
        } else {
            peerConnection.setConfiguration(parseRTCConfiguration(readableMap));
        }
    }

    @ReactMethod
    public void peerConnectionAddStream(String str, int i) {
        MediaStream mediaStream = (MediaStream) this.localStreams.get(str);
        if (mediaStream == null) {
            Log.d(TAG, "peerConnectionAddStream() mediaStream is null");
            return;
        }
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || !peerConnectionObserver.addStream(mediaStream)) {
            Log.e(TAG, "peerConnectionAddStream() failed");
        }
    }

    @ReactMethod
    public void peerConnectionRemoveStream(String str, int i) {
        MediaStream mediaStream = (MediaStream) this.localStreams.get(str);
        if (mediaStream == null) {
            Log.d(TAG, "peerConnectionRemoveStream() mediaStream is null");
            return;
        }
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || !peerConnectionObserver.removeStream(mediaStream)) {
            Log.e(TAG, "peerConnectionRemoveStream() failed");
        }
    }

    @ReactMethod
    public void peerConnectionCreateOffer(int i, ReadableMap readableMap, final Callback callback) {
        PeerConnection peerConnection = getPeerConnection(i);
        if (peerConnection != null) {
            peerConnection.createOffer(new SdpObserver() {
                public void onSetFailure(String str) {
                }

                public void onSetSuccess() {
                }

                public void onCreateFailure(String str) {
                    callback.invoke(Boolean.valueOf(false), str);
                }

                public void onCreateSuccess(SessionDescription sessionDescription) {
                    WritableMap createMap = Arguments.createMap();
                    createMap.putString("sdp", sessionDescription.description);
                    createMap.putString("type", sessionDescription.type.canonicalForm());
                    callback.invoke(Boolean.valueOf(true), createMap);
                }
            }, parseMediaConstraints(readableMap));
            return;
        }
        Log.d(TAG, "peerConnectionCreateOffer() peerConnection is null");
        callback.invoke(Boolean.valueOf(false), "peerConnection is null");
    }

    @ReactMethod
    public void peerConnectionCreateAnswer(int i, ReadableMap readableMap, final Callback callback) {
        PeerConnection peerConnection = getPeerConnection(i);
        if (peerConnection != null) {
            peerConnection.createAnswer(new SdpObserver() {
                public void onSetFailure(String str) {
                }

                public void onSetSuccess() {
                }

                public void onCreateFailure(String str) {
                    callback.invoke(Boolean.valueOf(false), str);
                }

                public void onCreateSuccess(SessionDescription sessionDescription) {
                    WritableMap createMap = Arguments.createMap();
                    createMap.putString("sdp", sessionDescription.description);
                    createMap.putString("type", sessionDescription.type.canonicalForm());
                    callback.invoke(Boolean.valueOf(true), createMap);
                }
            }, parseMediaConstraints(readableMap));
            return;
        }
        Log.d(TAG, "peerConnectionCreateAnswer() peerConnection is null");
        callback.invoke(Boolean.valueOf(false), "peerConnection is null");
    }

    @ReactMethod
    public void peerConnectionSetLocalDescription(ReadableMap readableMap, int i, final Callback callback) {
        PeerConnection peerConnection = getPeerConnection(i);
        Log.d(TAG, "peerConnectionSetLocalDescription() start");
        if (peerConnection != null) {
            peerConnection.setLocalDescription(new SdpObserver() {
                public void onCreateFailure(String str) {
                }

                public void onCreateSuccess(SessionDescription sessionDescription) {
                }

                public void onSetSuccess() {
                    callback.invoke(Boolean.valueOf(true));
                }

                public void onSetFailure(String str) {
                    callback.invoke(Boolean.valueOf(false), str);
                }
            }, new SessionDescription(Type.fromCanonicalForm(readableMap.getString("type")), readableMap.getString("sdp")));
        } else {
            Log.d(TAG, "peerConnectionSetLocalDescription() peerConnection is null");
            callback.invoke(Boolean.valueOf(false), "peerConnection is null");
        }
        Log.d(TAG, "peerConnectionSetLocalDescription() end");
    }

    @ReactMethod
    public void peerConnectionSetRemoteDescription(ReadableMap readableMap, int i, final Callback callback) {
        PeerConnection peerConnection = getPeerConnection(i);
        Log.d(TAG, "peerConnectionSetRemoteDescription() start");
        if (peerConnection != null) {
            peerConnection.setRemoteDescription(new SdpObserver() {
                public void onCreateFailure(String str) {
                }

                public void onCreateSuccess(SessionDescription sessionDescription) {
                }

                public void onSetSuccess() {
                    callback.invoke(Boolean.valueOf(true));
                }

                public void onSetFailure(String str) {
                    callback.invoke(Boolean.valueOf(false), str);
                }
            }, new SessionDescription(Type.fromCanonicalForm(readableMap.getString("type")), readableMap.getString("sdp")));
        } else {
            Log.d(TAG, "peerConnectionSetRemoteDescription() peerConnection is null");
            callback.invoke(Boolean.valueOf(false), "peerConnection is null");
        }
        Log.d(TAG, "peerConnectionSetRemoteDescription() end");
    }

    @ReactMethod
    public void peerConnectionAddICECandidate(ReadableMap readableMap, int i, Callback callback) {
        boolean z;
        PeerConnection peerConnection = getPeerConnection(i);
        Log.d(TAG, "peerConnectionAddICECandidate() start");
        if (peerConnection != null) {
            z = peerConnection.addIceCandidate(new IceCandidate(readableMap.getString("sdpMid"), readableMap.getInt("sdpMLineIndex"), readableMap.getString("candidate")));
        } else {
            Log.d(TAG, "peerConnectionAddICECandidate() peerConnection is null");
            z = false;
        }
        callback.invoke(Boolean.valueOf(z));
        Log.d(TAG, "peerConnectionAddICECandidate() end");
    }

    @ReactMethod
    public void peerConnectionGetStats(String str, int i, Callback callback) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || peerConnectionObserver.getPeerConnection() == null) {
            Log.d(TAG, "peerConnectionGetStats() peerConnection is null");
        } else {
            peerConnectionObserver.getStats(str, callback);
        }
    }

    @ReactMethod
    public void peerConnectionClose(int i) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || peerConnectionObserver.getPeerConnection() == null) {
            Log.d(TAG, "peerConnectionClose() peerConnection is null");
            return;
        }
        peerConnectionObserver.close();
        this.mPeerConnectionObservers.remove(i);
    }

    @ReactMethod
    public void createDataChannel(int i, String str, ReadableMap readableMap) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || peerConnectionObserver.getPeerConnection() == null) {
            Log.d(TAG, "createDataChannel() peerConnection is null");
        } else {
            peerConnectionObserver.createDataChannel(str, readableMap);
        }
    }

    @ReactMethod
    public void dataChannelClose(int i, int i2) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || peerConnectionObserver.getPeerConnection() == null) {
            Log.d(TAG, "dataChannelClose() peerConnection is null");
        } else {
            peerConnectionObserver.dataChannelClose(i2);
        }
    }

    @ReactMethod
    public void dataChannelSend(int i, int i2, String str, String str2) {
        PeerConnectionObserver peerConnectionObserver = (PeerConnectionObserver) this.mPeerConnectionObservers.get(i);
        if (peerConnectionObserver == null || peerConnectionObserver.getPeerConnection() == null) {
            Log.d(TAG, "dataChannelSend() peerConnection is null");
        } else {
            peerConnectionObserver.dataChannelSend(i2, str, str2);
        }
    }
}
