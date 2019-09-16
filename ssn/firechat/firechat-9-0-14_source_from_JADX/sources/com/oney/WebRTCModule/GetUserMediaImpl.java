package com.oney.WebRTCModule;

import android.util.Log;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.ViewProps;
import com.opengarden.firechat.matrixsdk.rest.model.login.PasswordLoginParams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaSource;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

class GetUserMediaImpl {
    private static final int DEFAULT_FPS = 30;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_WIDTH = 1280;
    private static final String PERMISSION_AUDIO = "android.permission.RECORD_AUDIO";
    private static final String PERMISSION_VIDEO = "android.permission.CAMERA";
    /* access modifiers changed from: private */
    public static final String TAG = C1267WebRTCModule.TAG;
    private final CameraEventsHandler cameraEventsHandler = new CameraEventsHandler();
    private final ReactApplicationContext reactContext;
    private final Map<String, TrackPrivate> tracks = new HashMap();
    private final C1267WebRTCModule webRTCModule;

    private static class TrackPrivate {
        public final MediaSource mediaSource;
        public final MediaStreamTrack track;
        public final VideoCapturer videoCapturer;

        public TrackPrivate(MediaStreamTrack mediaStreamTrack, MediaSource mediaSource2, VideoCapturer videoCapturer2) {
            this.track = mediaStreamTrack;
            this.mediaSource = mediaSource2;
            this.videoCapturer = videoCapturer2;
        }
    }

    GetUserMediaImpl(C1267WebRTCModule webRTCModule2, ReactApplicationContext reactApplicationContext) {
        this.webRTCModule = webRTCModule2;
        this.reactContext = reactApplicationContext;
    }

    private void addDefaultAudioConstraints(MediaConstraints mediaConstraints) {
        mediaConstraints.optional.add(new KeyValuePair("googNoiseSuppression", "true"));
        mediaConstraints.optional.add(new KeyValuePair("googEchoCancellation", "true"));
        mediaConstraints.optional.add(new KeyValuePair("echoCancellation", "true"));
        mediaConstraints.optional.add(new KeyValuePair("googEchoCancellation2", "true"));
        mediaConstraints.optional.add(new KeyValuePair("googDAEchoCancellation", "true"));
    }

    private void constraint2permission(ReadableMap readableMap, String str, List<String> list) {
        if (readableMap.hasKey(str)) {
            ReadableType type = readableMap.getType(str);
            if (type == ReadableType.Boolean) {
                if (!readableMap.getBoolean(str)) {
                    return;
                }
            } else if (type != ReadableType.Map) {
                return;
            }
            if ("audio".equals(str)) {
                list.add(PERMISSION_AUDIO);
            } else if ("video".equals(str)) {
                list.add(PERMISSION_VIDEO);
            }
        }
    }

    private VideoCapturer createVideoCapturer(CameraEnumerator cameraEnumerator, String str, String str2) {
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        if (str != null) {
            int length = deviceNames.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String str3 = deviceNames[i];
                if (str3.equals(str)) {
                    CameraVideoCapturer createCapturer = cameraEnumerator.createCapturer(str3, this.cameraEventsHandler);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Create user-specified camera ");
                    sb.append(str3);
                    String sb2 = sb.toString();
                    if (createCapturer != null) {
                        String str4 = TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(sb2);
                        sb3.append(" succeeded");
                        Log.d(str4, sb3.toString());
                        return createCapturer;
                    }
                    String str5 = TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(sb2);
                    sb4.append(" failed");
                    Log.d(str5, sb4.toString());
                } else {
                    i++;
                }
            }
        }
        boolean z = true;
        if (str2 == null) {
            str2 = PasswordLoginParams.IDENTIFIER_KEY_USER;
        } else {
            z = true ^ str2.equals("environment");
        }
        for (String str6 : deviceNames) {
            if (cameraEnumerator.isFrontFacing(str6) == z) {
                CameraVideoCapturer createCapturer2 = cameraEnumerator.createCapturer(str6, this.cameraEventsHandler);
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Create ");
                sb5.append(str2);
                sb5.append("-facing camera ");
                sb5.append(str6);
                String sb6 = sb5.toString();
                if (createCapturer2 != null) {
                    String str7 = TAG;
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append(sb6);
                    sb7.append(" succeeded");
                    Log.d(str7, sb7.toString());
                    return createCapturer2;
                }
                String str8 = TAG;
                StringBuilder sb8 = new StringBuilder();
                sb8.append(sb6);
                sb8.append(" failed");
                Log.d(str8, sb8.toString());
            }
        }
        return null;
    }

    private String getFacingMode(ReadableMap readableMap) {
        if (readableMap == null) {
            return null;
        }
        return ReactBridgeUtil.getMapStrValue(readableMap, "facingMode");
    }

    private ReactApplicationContext getReactApplicationContext() {
        return this.reactContext;
    }

    private String getSourceIdConstraint(ReadableMap readableMap) {
        if (readableMap != null && readableMap.hasKey("optional") && readableMap.getType("optional") == ReadableType.Array) {
            ReadableArray array = readableMap.getArray("optional");
            int size = array.size();
            for (int i = 0; i < size; i++) {
                if (array.getType(i) == ReadableType.Map) {
                    ReadableMap map = array.getMap(i);
                    if (map.hasKey("sourceId") && map.getType("sourceId") == ReadableType.String) {
                        return map.getString("sourceId");
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public MediaStreamTrack getTrack(String str) {
        TrackPrivate trackPrivate = (TrackPrivate) this.tracks.get(str);
        if (trackPrivate == null) {
            return null;
        }
        return trackPrivate.track;
    }

    private AudioTrack getUserAudio(ReadableMap readableMap) {
        MediaConstraints mediaConstraints;
        if (readableMap.getType("audio") == ReadableType.Boolean) {
            mediaConstraints = new MediaConstraints();
            addDefaultAudioConstraints(mediaConstraints);
        } else {
            mediaConstraints = this.webRTCModule.parseMediaConstraints(readableMap.getMap("audio"));
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getUserMedia(audio): ");
        sb.append(mediaConstraints);
        Log.i(str, sb.toString());
        String nextTrackUUID = this.webRTCModule.getNextTrackUUID();
        PeerConnectionFactory peerConnectionFactory = this.webRTCModule.mFactory;
        AudioSource createAudioSource = peerConnectionFactory.createAudioSource(mediaConstraints);
        AudioTrack createAudioTrack = peerConnectionFactory.createAudioTrack(nextTrackUUID, createAudioSource);
        this.tracks.put(nextTrackUUID, new TrackPrivate(createAudioTrack, createAudioSource, null));
        return createAudioTrack;
    }

    /* access modifiers changed from: 0000 */
    public void getUserMedia(ReadableMap readableMap, Callback callback, final Callback callback2, MediaStream mediaStream) {
        ArrayList arrayList = new ArrayList();
        constraint2permission(readableMap, "audio", arrayList);
        constraint2permission(readableMap, "video", arrayList);
        if (arrayList.isEmpty()) {
            callback2.invoke("TypeError", "constraints requests no media types");
            return;
        }
        final ReadableMap readableMap2 = readableMap;
        final Callback callback3 = callback;
        final Callback callback4 = callback2;
        final MediaStream mediaStream2 = mediaStream;
        C12491 r1 = new Callback() {
            public void invoke(Object... objArr) {
                GetUserMediaImpl.this.getUserMedia(readableMap2, callback3, callback4, mediaStream2, (List) objArr[0]);
            }
        };
        requestPermissions(arrayList, r1, new Callback() {
            public void invoke(Object... objArr) {
                callback2.invoke("DOMException", "NotAllowedError");
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
        r14 = r1[r12];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
        if (r14 == null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
        removeTrack(r14.mo27904id());
        r14.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        r13.invoke(null, "Failed to create new track");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0013, code lost:
        if (r2 != null) goto L_0x0015;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0023, code lost:
        if (r11 == null) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0025, code lost:
        r11 = r1.length;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0027, code lost:
        if (r12 >= r11) goto L_0x003a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getUserMedia(com.facebook.react.bridge.ReadableMap r11, com.facebook.react.bridge.Callback r12, com.facebook.react.bridge.Callback r13, org.webrtc.MediaStream r14, java.util.List<java.lang.String> r15) {
        /*
            r10 = this;
            r0 = 2
            org.webrtc.MediaStreamTrack[] r1 = new org.webrtc.MediaStreamTrack[r0]
            java.lang.String r2 = "android.permission.RECORD_AUDIO"
            boolean r2 = r15.contains(r2)
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x0015
            org.webrtc.AudioTrack r2 = r10.getUserAudio(r11)
            r1[r4] = r2
            if (r2 == 0) goto L_0x0025
        L_0x0015:
            java.lang.String r2 = "android.permission.CAMERA"
            boolean r15 = r15.contains(r2)
            if (r15 == 0) goto L_0x0047
            org.webrtc.VideoTrack r11 = r10.getUserVideo(r11)
            r1[r3] = r11
            if (r11 != 0) goto L_0x0047
        L_0x0025:
            int r11 = r1.length
            r12 = 0
        L_0x0027:
            if (r12 >= r11) goto L_0x003a
            r14 = r1[r12]
            if (r14 == 0) goto L_0x0037
            java.lang.String r15 = r14.mo27904id()
            r10.removeTrack(r15)
            r14.dispose()
        L_0x0037:
            int r12 = r12 + 1
            goto L_0x0027
        L_0x003a:
            java.lang.Object[] r11 = new java.lang.Object[r0]
            r12 = 0
            r11[r4] = r12
            java.lang.String r12 = "Failed to create new track"
            r11[r3] = r12
            r13.invoke(r11)
            return
        L_0x0047:
            com.facebook.react.bridge.WritableArray r11 = com.facebook.react.bridge.Arguments.createArray()
            int r13 = r1.length
            r15 = 0
        L_0x004d:
            if (r15 >= r13) goto L_0x00a1
            r2 = r1[r15]
            if (r2 != 0) goto L_0x0054
            goto L_0x009e
        L_0x0054:
            java.lang.String r5 = r2.mo27904id()
            boolean r6 = r2 instanceof org.webrtc.AudioTrack
            if (r6 == 0) goto L_0x0063
            r6 = r2
            org.webrtc.AudioTrack r6 = (org.webrtc.AudioTrack) r6
            r14.addTrack(r6)
            goto L_0x0069
        L_0x0063:
            r6 = r2
            org.webrtc.VideoTrack r6 = (org.webrtc.VideoTrack) r6
            r14.addTrack(r6)
        L_0x0069:
            com.facebook.react.bridge.WritableMap r6 = com.facebook.react.bridge.Arguments.createMap()
            java.lang.String r7 = r2.kind()
            java.lang.String r8 = "enabled"
            boolean r9 = r2.enabled()
            r6.putBoolean(r8, r9)
            java.lang.String r8 = "id"
            r6.putString(r8, r5)
            java.lang.String r5 = "kind"
            r6.putString(r5, r7)
            java.lang.String r5 = "label"
            r6.putString(r5, r7)
            java.lang.String r5 = "readyState"
            org.webrtc.MediaStreamTrack$State r2 = r2.state()
            java.lang.String r2 = r2.toString()
            r6.putString(r5, r2)
            java.lang.String r2 = "remote"
            r6.putBoolean(r2, r4)
            r11.pushMap(r6)
        L_0x009e:
            int r15 = r15 + 1
            goto L_0x004d
        L_0x00a1:
            java.lang.String r13 = r14.label()
            java.lang.String r15 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "MediaStream id: "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r15, r1)
            com.oney.WebRTCModule.WebRTCModule r15 = r10.webRTCModule
            java.util.Map<java.lang.String, org.webrtc.MediaStream> r15 = r15.localStreams
            r15.put(r13, r14)
            java.lang.Object[] r14 = new java.lang.Object[r0]
            r14[r4] = r13
            r14[r3] = r11
            r12.invoke(r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oney.WebRTCModule.GetUserMediaImpl.getUserMedia(com.facebook.react.bridge.ReadableMap, com.facebook.react.bridge.Callback, com.facebook.react.bridge.Callback, org.webrtc.MediaStream, java.util.List):void");
    }

    private VideoTrack getUserVideo(ReadableMap readableMap) {
        ReadableMap readableMap2;
        ReadableMap readableMap3;
        CameraEnumerator cameraEnumerator;
        if (readableMap.getType("video") == ReadableType.Map) {
            readableMap2 = readableMap.getMap("video");
            readableMap3 = (!readableMap2.hasKey("mandatory") || readableMap2.getType("mandatory") != ReadableType.Map) ? null : readableMap2.getMap("mandatory");
        } else {
            readableMap2 = null;
            readableMap3 = null;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getUserMedia(video): ");
        sb.append(readableMap2);
        Log.i(str, sb.toString());
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        if (Camera2Enumerator.isSupported(reactApplicationContext)) {
            Log.d(TAG, "Creating video capturer using Camera2 API.");
            cameraEnumerator = new Camera2Enumerator(reactApplicationContext);
        } else {
            Log.d(TAG, "Creating video capturer using Camera1 API.");
            cameraEnumerator = new Camera1Enumerator(false);
        }
        VideoCapturer createVideoCapturer = createVideoCapturer(cameraEnumerator, getSourceIdConstraint(readableMap2), getFacingMode(readableMap2));
        if (createVideoCapturer == null) {
            return null;
        }
        PeerConnectionFactory peerConnectionFactory = this.webRTCModule.mFactory;
        VideoSource createVideoSource = peerConnectionFactory.createVideoSource(createVideoCapturer);
        try {
            createVideoCapturer.startCapture(readableMap3.hasKey(ViewProps.MIN_WIDTH) ? readableMap3.getInt(ViewProps.MIN_WIDTH) : DEFAULT_WIDTH, readableMap3.hasKey(ViewProps.MIN_HEIGHT) ? readableMap3.getInt(ViewProps.MIN_HEIGHT) : DEFAULT_HEIGHT, readableMap3.hasKey("minFrameRate") ? readableMap3.getInt("minFrameRate") : 30);
            String nextTrackUUID = this.webRTCModule.getNextTrackUUID();
            VideoTrack createVideoTrack = peerConnectionFactory.createVideoTrack(nextTrackUUID, createVideoSource);
            this.tracks.put(nextTrackUUID, new TrackPrivate(createVideoTrack, createVideoSource, createVideoCapturer));
            return createVideoTrack;
        } catch (RuntimeException unused) {
            createVideoSource.dispose();
            createVideoCapturer.dispose();
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void mediaStreamTrackStop(String str) {
        MediaStreamTrack track = getTrack(str);
        if (track == null) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("mediaStreamTrackStop() No local MediaStreamTrack with id ");
            sb.append(str);
            Log.d(str2, sb.toString());
            return;
        }
        track.setEnabled(false);
        removeTrack(str);
    }

    private void removeTrack(String str) {
        TrackPrivate trackPrivate = (TrackPrivate) this.tracks.remove(str);
        if (trackPrivate != null) {
            VideoCapturer videoCapturer = trackPrivate.videoCapturer;
            boolean z = true;
            if (videoCapturer != null) {
                try {
                    videoCapturer.stopCapture();
                } catch (InterruptedException unused) {
                    Log.e(TAG, "removeTrack() Failed to stop video capturer");
                    z = false;
                }
            }
            if (z) {
                trackPrivate.mediaSource.dispose();
                if (videoCapturer != null) {
                    videoCapturer.dispose();
                }
            }
        }
    }

    private void requestPermissions(final ArrayList<String> arrayList, final Callback callback, final Callback callback2) {
        PermissionUtils.requestPermissions((ReactContext) getReactApplicationContext(), (String[]) arrayList.toArray(new String[arrayList.size()]), (PermissionUtils.Callback) new PermissionUtils.Callback() {
            private boolean invoked = false;

            public void invoke(String[] strArr, int[] iArr) {
                if (this.invoked) {
                    Log.w(GetUserMediaImpl.TAG, "GetUserMediaImpl.PermissionUtils.Callback invoked more than once!");
                    return;
                }
                this.invoked = true;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < strArr.length; i++) {
                    String str = strArr[i];
                    if (iArr[i] == 0) {
                        arrayList.add(str);
                    } else {
                        arrayList2.add(str);
                    }
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    if (!arrayList.contains((String) it.next())) {
                        callback2.invoke(arrayList2);
                        return;
                    }
                }
                callback.invoke(arrayList);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void switchCamera(String str) {
        TrackPrivate trackPrivate = (TrackPrivate) this.tracks.get(str);
        if (trackPrivate != null && trackPrivate.videoCapturer != null) {
            ((CameraVideoCapturer) trackPrivate.videoCapturer).switchCamera(null);
        }
    }
}
