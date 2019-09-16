package com.opengarden.firechat.matrixsdk.call;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.gms.dynamite.ProviderConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oney.WebRTCModule.EglUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class MXWebRtcCall extends MXCall {
    private static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private static final int CAMERA_TYPE_FRONT = 1;
    private static final int CAMERA_TYPE_REAR = 2;
    private static final int CAMERA_TYPE_UNDEFINED = -1;
    private static final int DEFAULT_FPS = 30;
    private static final int DEFAULT_HEIGHT = 360;
    private static final int DEFAULT_WIDTH = 640;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXWebRtcCall";
    private static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private static String mBackCameraName = null;
    private static CameraVideoCapturer mCameraVideoCapturer = null;
    private static String mFrontCameraName = null;
    private static boolean mIsInitialized = false;
    private static Boolean mIsSupported;
    /* access modifiers changed from: private */
    public static PeerConnectionFactory mPeerConnectionFactory;
    private AudioSource mAudioSource = null;
    private JsonObject mCallInviteParams = null;
    private String mCallState = IMXCall.CALL_STATE_CREATED;
    /* access modifiers changed from: private */
    public RelativeLayout mCallView = null;
    private int mCameraInUse = -1;
    /* access modifiers changed from: private */
    public MXWebRtcView mFullScreenRTCView = null;
    private boolean mIsAnswered = false;
    private boolean mIsCameraSwitched;
    private boolean mIsCameraUnplugged = false;
    /* access modifiers changed from: private */
    public boolean mIsIncomingPrepared = false;
    private AudioTrack mLocalAudioTrack = null;
    /* access modifiers changed from: private */
    public MediaStream mLocalMediaStream = null;
    /* access modifiers changed from: private */
    public VideoTrack mLocalVideoTrack = null;
    /* access modifiers changed from: private */
    public PeerConnection mPeerConnection = null;
    private JsonArray mPendingCandidates = new JsonArray();
    /* access modifiers changed from: private */
    public MXWebRtcView mPipRTCView = null;
    /* access modifiers changed from: private */
    public VideoTrack mRemoteVideoTrack = null;
    /* access modifiers changed from: private */
    public boolean mUsingLargeLocalRenderer = true;
    private VideoSource mVideoSource = null;

    public static boolean isSupported(Context context) {
        if (mIsSupported == null) {
            initializeAndroidGlobals(context.getApplicationContext());
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("isSupported ");
            sb.append(mIsSupported);
            Log.m209d(str, sb.toString());
        }
        return mIsSupported.booleanValue();
    }

    private static boolean useCamera2(Context context) {
        return Camera2Enumerator.isSupported(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0025, code lost:
        r6 = r3;
     */
    @android.annotation.SuppressLint({"Deprecation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isCameraInUse(android.content.Context r6, boolean r7) {
        /*
            boolean r6 = useCamera2(r6)
            r0 = 0
            r1 = 1
            if (r6 != 0) goto L_0x0059
            r6 = -1
            int r2 = android.hardware.Camera.getNumberOfCameras()
            r3 = 0
        L_0x000e:
            if (r3 >= r2) goto L_0x002a
            android.hardware.Camera$CameraInfo r4 = new android.hardware.Camera$CameraInfo
            r4.<init>()
            android.hardware.Camera.getCameraInfo(r3, r4)
            int r5 = r4.facing
            if (r5 != r1) goto L_0x001f
            if (r7 == 0) goto L_0x001f
            goto L_0x0025
        L_0x001f:
            int r4 = r4.facing
            if (r4 != 0) goto L_0x0027
            if (r7 != 0) goto L_0x0027
        L_0x0025:
            r6 = r3
            goto L_0x002a
        L_0x0027:
            int r3 = r3 + 1
            goto L_0x000e
        L_0x002a:
            if (r6 < 0) goto L_0x0059
            android.hardware.Camera r6 = android.hardware.Camera.open(r6)     // Catch:{ Exception -> 0x003b }
            if (r6 != 0) goto L_0x0033
            r0 = 1
        L_0x0033:
            if (r6 == 0) goto L_0x0059
            r6.release()
            goto L_0x0059
        L_0x0039:
            r6 = move-exception
            goto L_0x0058
        L_0x003b:
            r6 = move-exception
            java.lang.String r7 = LOG_TAG     // Catch:{ all -> 0x0039 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0039 }
            r0.<init>()     // Catch:{ all -> 0x0039 }
            java.lang.String r2 = "## isCameraInUse() : failed "
            r0.append(r2)     // Catch:{ all -> 0x0039 }
            java.lang.String r6 = r6.getMessage()     // Catch:{ all -> 0x0039 }
            r0.append(r6)     // Catch:{ all -> 0x0039 }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0039 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r6)     // Catch:{ all -> 0x0039 }
            r0 = 1
            goto L_0x0059
        L_0x0058:
            throw r6
        L_0x0059:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.call.MXWebRtcCall.isCameraInUse(android.content.Context, boolean):boolean");
    }

    private static CameraEnumerator getCameraEnumerator(Context context) {
        if (useCamera2(context)) {
            return new Camera2Enumerator(context);
        }
        return new Camera1Enumerator(false);
    }

    public MXWebRtcCall(MXSession mXSession, Context context, JsonElement jsonElement) {
        if (!isSupported(context)) {
            throw new AssertionError("MXWebRtcCall : not supported with the current android version");
        } else if (mXSession == null) {
            throw new AssertionError("MXWebRtcCall : session cannot be null");
        } else if (context == null) {
            throw new AssertionError("MXWebRtcCall : context cannot be null");
        } else {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("MXWebRtcCall constructor ");
            sb.append(jsonElement);
            Log.m209d(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("c");
            sb2.append(System.currentTimeMillis());
            this.mCallId = sb2.toString();
            this.mSession = mXSession;
            this.mContext = context;
            this.mTurnServer = jsonElement;
        }
    }

    private static void initializeAndroidGlobals(Context context) {
        if (!mIsInitialized) {
            try {
                mIsInitialized = PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
                PeerConnectionFactory.initializeFieldTrials(null);
                mIsSupported = Boolean.valueOf(true);
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initializeAndroidGlobals(): mIsInitialized=");
                sb.append(mIsInitialized);
                Log.m209d(str, sb.toString());
            } catch (Throwable th) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## initializeAndroidGlobals(): Exception Msg=");
                sb2.append(th.getMessage());
                Log.m211e(str2, sb2.toString());
                mIsInitialized = true;
                mIsSupported = Boolean.valueOf(false);
            }
        }
    }

    public void createCallView() {
        super.createCallView();
        if (mIsSupported != null && mIsSupported.booleanValue()) {
            Log.m209d(LOG_TAG, "++ createCallView()");
            dispatchOnStateDidChange(IMXCall.CALL_STATE_CREATING_CALL_VIEW);
            this.mUIThreadHandler.postDelayed(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.mCallView = new RelativeLayout(MXWebRtcCall.this.mContext);
                    MXWebRtcCall.this.mCallView.setLayoutParams(new LayoutParams(-1, -1));
                    MXWebRtcCall.this.mCallView.setBackgroundColor(ContextCompat.getColor(MXWebRtcCall.this.mContext, 17170444));
                    MXWebRtcCall.this.mCallView.setVisibility(8);
                    MXWebRtcCall.this.dispatchOnCallViewCreated(MXWebRtcCall.this.mCallView);
                    MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                        public void run() {
                            MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_READY);
                            MXWebRtcCall.this.dispatchOnReady();
                        }
                    });
                }
            }, 10);
        }
    }

    /* access modifiers changed from: private */
    public void terminate(final int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## terminate(): reason= ");
        sb.append(i);
        Log.m209d(str, sb.toString());
        if (!isCallEnded()) {
            dispatchOnStateDidChange(IMXCall.CALL_STATE_ENDED);
            boolean z = false;
            if (this.mPeerConnection != null) {
                this.mPeerConnection.dispose();
                this.mPeerConnection = null;
                z = true;
            }
            if (mCameraVideoCapturer != null) {
                mCameraVideoCapturer.dispose();
                mCameraVideoCapturer = null;
            }
            if (this.mVideoSource != null) {
                this.mVideoSource.dispose();
                this.mVideoSource = null;
            }
            if (this.mAudioSource != null) {
                this.mAudioSource.dispose();
                this.mAudioSource = null;
            }
            if (z && mPeerConnectionFactory != null) {
                mPeerConnectionFactory.dispose();
                mPeerConnectionFactory = null;
            }
            if (this.mCallView != null) {
                final RelativeLayout relativeLayout = this.mCallView;
                relativeLayout.post(new Runnable() {
                    public void run() {
                        relativeLayout.setVisibility(8);
                    }
                });
                this.mCallView = null;
            }
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.dispatchOnCallEnd(i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void sendInvite(SessionDescription sessionDescription) {
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "## sendInvite(): isCallEnded");
            return;
        }
        Log.m209d(LOG_TAG, "## sendInvite()");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ProviderConstants.API_COLNAME_FEATURE_VERSION, (Number) Integer.valueOf(0));
        jsonObject.addProperty("call_id", this.mCallId);
        jsonObject.addProperty("lifetime", (Number) Integer.valueOf(MXCall.CALL_TIMEOUT_MS));
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("sdp", sessionDescription.description);
        jsonObject2.addProperty("type", sessionDescription.type.canonicalForm());
        jsonObject.add("offer", jsonObject2);
        this.mPendingEvents.add(new Event(Event.EVENT_TYPE_CALL_INVITE, jsonObject, this.mSession.getCredentials().userId, this.mCallSignalingRoom.getRoomId()));
        try {
            this.mCallTimeoutTimer = new Timer();
            this.mCallTimeoutTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        if (MXWebRtcCall.this.getCallState().equals(IMXCall.CALL_STATE_RINGING) || MXWebRtcCall.this.getCallState().equals(IMXCall.CALL_STATE_INVITE_SENT)) {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "sendInvite : CALL_ERROR_USER_NOT_RESPONDING");
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_USER_NOT_RESPONDING);
                            MXWebRtcCall.this.hangup(null);
                        }
                        MXWebRtcCall.this.mCallTimeoutTimer.cancel();
                        MXWebRtcCall.this.mCallTimeoutTimer = null;
                    } catch (Exception e) {
                        String access$100 = MXWebRtcCall.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## sendInvite(): Exception Msg= ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }, 120000);
        } catch (Throwable th) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## sendInvite(): failed ");
            sb.append(th.getMessage());
            Log.m211e(str, sb.toString());
            if (this.mCallTimeoutTimer != null) {
                this.mCallTimeoutTimer.cancel();
                this.mCallTimeoutTimer = null;
            }
        }
        sendNextEvent();
    }

    /* access modifiers changed from: private */
    public void sendAnswer(SessionDescription sessionDescription) {
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "sendAnswer isCallEnded");
            return;
        }
        Log.m209d(LOG_TAG, "sendAnswer");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ProviderConstants.API_COLNAME_FEATURE_VERSION, (Number) Integer.valueOf(0));
        jsonObject.addProperty("call_id", this.mCallId);
        jsonObject.addProperty("lifetime", (Number) Integer.valueOf(MXCall.CALL_TIMEOUT_MS));
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("sdp", sessionDescription.description);
        jsonObject2.addProperty("type", sessionDescription.type.canonicalForm());
        jsonObject.add("answer", jsonObject2);
        this.mPendingEvents.add(new Event(Event.EVENT_TYPE_CALL_ANSWER, jsonObject, this.mSession.getCredentials().userId, this.mCallSignalingRoom.getRoomId()));
        sendNextEvent();
        this.mIsAnswered = true;
    }

    public void updateLocalVideoRendererPosition(VideoLayoutConfiguration videoLayoutConfiguration) {
        super.updateLocalVideoRendererPosition(videoLayoutConfiguration);
        try {
            updateWebRtcViewLayout(this.mPipRTCView, videoLayoutConfiguration);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## updateLocalVideoRendererPosition(): Exception Msg=");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public boolean isSwitchCameraSupported() {
        String[] deviceNames = getCameraEnumerator(this.mContext).getDeviceNames();
        return (deviceNames == null || deviceNames.length == 0) ? false : true;
    }

    public boolean switchRearFrontCamera() {
        if (mCameraVideoCapturer == null || !isSwitchCameraSupported()) {
            Log.m217w(LOG_TAG, "## switchRearFrontCamera(): failure - invalid values");
        } else {
            try {
                mCameraVideoCapturer.switchCamera(null);
                if (1 == this.mCameraInUse) {
                    this.mCameraInUse = 2;
                } else {
                    this.mCameraInUse = 1;
                }
                this.mIsCameraSwitched = !this.mIsCameraSwitched;
                return true;
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## switchRearFrontCamera(): failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return false;
    }

    public void muteVideoRecording(boolean z) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## muteVideoRecording(): muteValue=");
        sb.append(z);
        Log.m209d(str, sb.toString());
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "## muteVideoRecording(): the call is ended");
        } else if (this.mLocalVideoTrack != null) {
            this.mLocalVideoTrack.setEnabled(!z);
        } else {
            Log.m209d(LOG_TAG, "## muteVideoRecording(): failure - invalid value");
        }
    }

    public boolean isVideoRecordingMuted() {
        boolean z = false;
        if (!isCallEnded()) {
            if (this.mLocalVideoTrack != null) {
                z = !this.mLocalVideoTrack.enabled();
            } else {
                Log.m217w(LOG_TAG, "## isVideoRecordingMuted(): failure - invalid value");
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## isVideoRecordingMuted() = ");
            sb.append(z);
            Log.m209d(str, sb.toString());
        } else {
            Log.m209d(LOG_TAG, "## isVideoRecordingMuted() : the call is ended");
        }
        return z;
    }

    public boolean isCameraSwitched() {
        return this.mIsCameraSwitched;
    }

    /* access modifiers changed from: private */
    public void createLocalStream() {
        Log.m209d(LOG_TAG, "## createLocalStream(): IN");
        if (this.mLocalVideoTrack == null && this.mLocalAudioTrack == null) {
            Log.m209d(LOG_TAG, "## createLocalStream(): CALL_ERROR_CALL_INIT_FAILED");
            dispatchOnCallError(IMXCall.CALL_ERROR_CALL_INIT_FAILED);
            hangup("no_stream");
            terminate(-1);
            return;
        }
        this.mLocalMediaStream = mPeerConnectionFactory.createLocalMediaStream("ARDAMS");
        if (this.mLocalVideoTrack != null) {
            this.mLocalMediaStream.addTrack(this.mLocalVideoTrack);
        }
        if (this.mLocalAudioTrack != null) {
            this.mLocalMediaStream.addTrack(this.mLocalAudioTrack);
        }
        if (this.mFullScreenRTCView != null) {
            this.mFullScreenRTCView.setStream(this.mLocalMediaStream);
            this.mFullScreenRTCView.setVisibility(0);
        }
        ArrayList arrayList = new ArrayList();
        if (this.mTurnServer != null) {
            try {
                JsonObject asJsonObject = this.mTurnServer.getAsJsonObject();
                String str = null;
                String asString = asJsonObject.has("username") ? asJsonObject.get("username").getAsString() : null;
                if (asJsonObject.has("password")) {
                    str = asJsonObject.get("password").getAsString();
                }
                JsonArray asJsonArray = asJsonObject.get("uris").getAsJsonArray();
                for (int i = 0; i < asJsonArray.size(); i++) {
                    String asString2 = asJsonArray.get(i).getAsString();
                    if (asString == null || str == null) {
                        arrayList.add(new IceServer(asString2));
                    } else {
                        arrayList.add(new IceServer(asString2, asString, str));
                    }
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## createLocalStream(): Exception in ICE servers list Msg=");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        String str3 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## createLocalStream(): ");
        sb2.append(arrayList.size());
        sb2.append(" known ice servers");
        Log.m209d(str3, sb2.toString());
        if (arrayList.isEmpty()) {
            Log.m209d(LOG_TAG, "## createLocalStream(): use the default google server");
            arrayList.add(new IceServer("stun:stun.l.google.com:19302"));
        }
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.optional.add(new KeyValuePair("RtpDataChannels", "true"));
        this.mPeerConnection = mPeerConnectionFactory.createPeerConnection((List<IceServer>) arrayList, mediaConstraints, (Observer) new Observer() {
            public void onSignalingChange(SignalingState signalingState) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onSignalingChange state=");
                sb.append(signalingState);
                Log.m209d(access$100, sb.toString());
            }

            public void onIceConnectionChange(final IceConnectionState iceConnectionState) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onIceConnectionChange ");
                sb.append(iceConnectionState);
                Log.m209d(access$100, sb.toString());
                MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if (iceConnectionState == IceConnectionState.CONNECTED) {
                            if (MXWebRtcCall.this.mLocalVideoTrack != null && MXWebRtcCall.this.mUsingLargeLocalRenderer && MXWebRtcCall.this.isVideo()) {
                                MXWebRtcCall.this.mLocalVideoTrack.setEnabled(false);
                                if (!MXWebRtcCall.this.isConference()) {
                                    MXWebRtcCall.this.mPipRTCView.setStream(MXWebRtcCall.this.mLocalMediaStream);
                                    MXWebRtcCall.this.mPipRTCView.setVisibility(0);
                                    MXWebRtcCall.this.mPipRTCView.setZOrder(1);
                                }
                                MXWebRtcCall.this.mLocalVideoTrack.setEnabled(true);
                                MXWebRtcCall.this.mUsingLargeLocalRenderer = false;
                                MXWebRtcCall.this.mCallView.post(new Runnable() {
                                    public void run() {
                                        if (MXWebRtcCall.this.mCallView != null) {
                                            MXWebRtcCall.this.mCallView.invalidate();
                                        }
                                    }
                                });
                            }
                            MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_CONNECTED);
                        } else if (iceConnectionState == IceConnectionState.FAILED) {
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_ICE_FAILED);
                            MXWebRtcCall.this.hangup("ice_failed");
                        }
                    }
                });
            }

            public void onIceConnectionReceivingChange(boolean z) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onIceConnectionReceivingChange ");
                sb.append(z);
                Log.m209d(access$100, sb.toString());
            }

            public void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onIceCandidatesRemoved ");
                sb.append(iceCandidateArr);
                Log.m209d(access$100, sb.toString());
            }

            public void onIceGatheringChange(IceGatheringState iceGatheringState) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onIceGatheringChange ");
                sb.append(iceGatheringState);
                Log.m209d(access$100, sb.toString());
            }

            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreamArr) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onAddTrack ");
                sb.append(rtpReceiver);
                sb.append(" -- ");
                sb.append(mediaStreamArr);
                Log.m209d(access$100, sb.toString());
            }

            public void onIceCandidate(final IceCandidate iceCandidate) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onIceCandidate ");
                sb.append(iceCandidate);
                Log.m209d(access$100, sb.toString());
                MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if (!MXWebRtcCall.this.isCallEnded()) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(ProviderConstants.API_COLNAME_FEATURE_VERSION, (Number) Integer.valueOf(0));
                            jsonObject.addProperty("call_id", MXWebRtcCall.this.mCallId);
                            JsonArray jsonArray = new JsonArray();
                            JsonObject jsonObject2 = new JsonObject();
                            jsonObject2.addProperty("sdpMLineIndex", (Number) Integer.valueOf(iceCandidate.sdpMLineIndex));
                            jsonObject2.addProperty("sdpMid", iceCandidate.sdpMid);
                            jsonObject2.addProperty("candidate", iceCandidate.sdp);
                            jsonArray.add((JsonElement) jsonObject2);
                            jsonObject.add("candidates", jsonArray);
                            boolean z = true;
                            if (MXWebRtcCall.this.mPendingEvents.size() > 0) {
                                try {
                                    Event event = (Event) MXWebRtcCall.this.mPendingEvents.get(MXWebRtcCall.this.mPendingEvents.size() - 1);
                                    if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_CALL_CANDIDATES)) {
                                        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                                        JsonArray asJsonArray = contentAsJsonObject.get("candidates").getAsJsonArray();
                                        JsonArray asJsonArray2 = jsonObject.get("candidates").getAsJsonArray();
                                        String access$100 = MXWebRtcCall.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Merge candidates from ");
                                        sb.append(asJsonArray.size());
                                        sb.append(" to ");
                                        sb.append(asJsonArray.size() + asJsonArray2.size());
                                        sb.append(" items.");
                                        Log.m209d(access$100, sb.toString());
                                        asJsonArray.addAll(asJsonArray2);
                                        contentAsJsonObject.remove("candidates");
                                        contentAsJsonObject.add("candidates", asJsonArray);
                                        z = false;
                                    }
                                } catch (Exception e) {
                                    String access$1002 = MXWebRtcCall.LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("## createLocalStream(): createPeerConnection - onIceCandidate() Exception Msg=");
                                    sb2.append(e.getMessage());
                                    Log.m211e(access$1002, sb2.toString());
                                }
                            }
                            if (z) {
                                MXWebRtcCall.this.mPendingEvents.add(new Event(Event.EVENT_TYPE_CALL_CANDIDATES, jsonObject, MXWebRtcCall.this.mSession.getCredentials().userId, MXWebRtcCall.this.mCallSignalingRoom.getRoomId()));
                                MXWebRtcCall.this.sendNextEvent();
                            }
                        }
                    }
                });
            }

            public void onAddStream(final MediaStream mediaStream) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onAddStream ");
                sb.append(mediaStream);
                Log.m209d(access$100, sb.toString());
                MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if (mediaStream.videoTracks.size() == 1 && !MXWebRtcCall.this.isCallEnded()) {
                            MXWebRtcCall.this.mRemoteVideoTrack = (VideoTrack) mediaStream.videoTracks.get(0);
                            MXWebRtcCall.this.mRemoteVideoTrack.setEnabled(true);
                            MXWebRtcCall.this.mFullScreenRTCView.setStream(mediaStream);
                            MXWebRtcCall.this.mFullScreenRTCView.setVisibility(0);
                        }
                    }
                });
            }

            public void onRemoveStream(final MediaStream mediaStream) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onRemoveStream ");
                sb.append(mediaStream);
                Log.m209d(access$100, sb.toString());
                MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if (MXWebRtcCall.this.mRemoteVideoTrack != null) {
                            MXWebRtcCall.this.mRemoteVideoTrack.dispose();
                            MXWebRtcCall.this.mRemoteVideoTrack = null;
                            ((VideoTrack) mediaStream.videoTracks.get(0)).dispose();
                        }
                    }
                });
            }

            public void onDataChannel(DataChannel dataChannel) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## mPeerConnection creation: onDataChannel ");
                sb.append(dataChannel);
                Log.m209d(access$100, sb.toString());
            }

            public void onRenegotiationNeeded() {
                Log.m209d(MXWebRtcCall.LOG_TAG, "## mPeerConnection creation: onRenegotiationNeeded");
            }
        });
        if (this.mPeerConnection == null) {
            dispatchOnCallError(IMXCall.CALL_ERROR_ICE_FAILED);
            hangup("cannot create peer connection");
            return;
        }
        this.mPeerConnection.addStream(this.mLocalMediaStream);
        MediaConstraints mediaConstraints2 = new MediaConstraints();
        mediaConstraints2.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints2.mandatory.add(new KeyValuePair("OfferToReceiveVideo", isVideo() ? "true" : "false"));
        if (!isIncoming()) {
            Log.m209d(LOG_TAG, "## createLocalStream(): !isIncoming() -> createOffer");
            this.mPeerConnection.createOffer(new SdpObserver() {
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    Log.m209d(MXWebRtcCall.LOG_TAG, "createOffer onCreateSuccess");
                    final SessionDescription sessionDescription2 = new SessionDescription(sessionDescription.type, sessionDescription.description);
                    MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                        public void run() {
                            if (MXWebRtcCall.this.mPeerConnection != null) {
                                MXWebRtcCall.this.mPeerConnection.setLocalDescription(new SdpObserver() {
                                    public void onCreateSuccess(SessionDescription sessionDescription) {
                                        Log.m209d(MXWebRtcCall.LOG_TAG, "setLocalDescription onCreateSuccess");
                                    }

                                    public void onSetSuccess() {
                                        Log.m209d(MXWebRtcCall.LOG_TAG, "setLocalDescription onSetSuccess");
                                        MXWebRtcCall.this.sendInvite(sessionDescription2);
                                        MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_INVITE_SENT);
                                    }

                                    public void onCreateFailure(String str) {
                                        String access$100 = MXWebRtcCall.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("setLocalDescription onCreateFailure ");
                                        sb.append(str);
                                        Log.m211e(access$100, sb.toString());
                                        MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                                        MXWebRtcCall.this.hangup(null);
                                    }

                                    public void onSetFailure(String str) {
                                        String access$100 = MXWebRtcCall.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("setLocalDescription onSetFailure ");
                                        sb.append(str);
                                        Log.m211e(access$100, sb.toString());
                                        MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                                        MXWebRtcCall.this.hangup(null);
                                    }
                                }, sessionDescription2);
                            }
                        }
                    });
                }

                public void onSetSuccess() {
                    Log.m209d(MXWebRtcCall.LOG_TAG, "createOffer onSetSuccess");
                }

                public void onCreateFailure(String str) {
                    String access$100 = MXWebRtcCall.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("createOffer onCreateFailure ");
                    sb.append(str);
                    Log.m209d(access$100, sb.toString());
                    MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                }

                public void onSetFailure(String str) {
                    String access$100 = MXWebRtcCall.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("createOffer onSetFailure ");
                    sb.append(str);
                    Log.m209d(access$100, sb.toString());
                    MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                }
            }, mediaConstraints2);
            dispatchOnStateDidChange(IMXCall.CALL_STATE_WAIT_CREATE_OFFER);
        }
    }

    private boolean hasCameraDevice() {
        int i;
        CameraEnumerator cameraEnumerator = getCameraEnumerator(this.mContext);
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        mFrontCameraName = null;
        mBackCameraName = null;
        if (deviceNames != null) {
            for (String str : deviceNames) {
                if (cameraEnumerator.isFrontFacing(str) && !isCameraInUse(this.mContext, true)) {
                    mFrontCameraName = str;
                } else if (cameraEnumerator.isBackFacing(str) && !isCameraInUse(this.mContext, false)) {
                    mBackCameraName = str;
                }
            }
            i = deviceNames.length;
        } else {
            i = 0;
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("hasCameraDevice():  camera number= ");
        sb.append(i);
        Log.m209d(str2, sb.toString());
        String str3 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("hasCameraDevice():  frontCameraName=");
        sb2.append(mFrontCameraName);
        sb2.append(" backCameraName=");
        sb2.append(mBackCameraName);
        Log.m209d(str3, sb2.toString());
        if (mFrontCameraName == null && mBackCameraName == null) {
            return false;
        }
        return true;
    }

    private CameraVideoCapturer createVideoCapturer(String str) {
        CameraEnumerator cameraEnumerator = getCameraEnumerator(this.mContext);
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        if (deviceNames == null || deviceNames.length <= 0) {
            return null;
        }
        CameraVideoCapturer cameraVideoCapturer = null;
        for (String str2 : deviceNames) {
            if (str2.equals(str)) {
                cameraVideoCapturer = cameraEnumerator.createCapturer(str2, null);
                if (cameraVideoCapturer != null) {
                    break;
                }
            }
        }
        return cameraVideoCapturer == null ? cameraEnumerator.createCapturer(deviceNames[0], null) : cameraVideoCapturer;
    }

    /* access modifiers changed from: private */
    public void createVideoTrack() {
        Log.m209d(LOG_TAG, "createVideoTrack");
        if (hasCameraDevice()) {
            try {
                if (mCameraVideoCapturer != null) {
                    mCameraVideoCapturer.dispose();
                    mCameraVideoCapturer = null;
                }
                if (mFrontCameraName != null) {
                    mCameraVideoCapturer = createVideoCapturer(mFrontCameraName);
                    if (mCameraVideoCapturer == null) {
                        Log.m211e(LOG_TAG, "Cannot create Video Capturer from front camera");
                    } else {
                        this.mCameraInUse = 1;
                    }
                }
                if (mCameraVideoCapturer == null && mBackCameraName != null) {
                    mCameraVideoCapturer = createVideoCapturer(mBackCameraName);
                    if (mCameraVideoCapturer == null) {
                        Log.m211e(LOG_TAG, "Cannot create Video Capturer from back camera");
                    } else {
                        this.mCameraInUse = 2;
                    }
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("createVideoTrack(): Exception Msg=");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            if (mCameraVideoCapturer != null) {
                Log.m209d(LOG_TAG, "createVideoTrack find a video capturer");
                try {
                    this.mVideoSource = mPeerConnectionFactory.createVideoSource(mCameraVideoCapturer);
                    mCameraVideoCapturer.startCapture(DEFAULT_WIDTH, DEFAULT_HEIGHT, 30);
                    this.mLocalVideoTrack = mPeerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, this.mVideoSource);
                    this.mLocalVideoTrack.setEnabled(true);
                } catch (Exception e2) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("createVideoSource fails with exception ");
                    sb2.append(e2.getMessage());
                    Log.m211e(str2, sb2.toString());
                    this.mLocalVideoTrack = null;
                    if (this.mVideoSource != null) {
                        this.mVideoSource.dispose();
                        this.mVideoSource = null;
                    }
                }
            } else {
                Log.m211e(LOG_TAG, "## createVideoTrack(): Cannot create Video Capturer - no camera available");
            }
        }
    }

    /* access modifiers changed from: private */
    public void createAudioTrack() {
        Log.m209d(LOG_TAG, "createAudioTrack");
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new KeyValuePair("googEchoCancellation", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googEchoCancellation2", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googDAEchoCancellation", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googTypingNoiseDetection", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googAutoGainControl", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googAutoGainControl2", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googNoiseSuppression", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googNoiseSuppression2", "true"));
        mediaConstraints.mandatory.add(new KeyValuePair("googAudioMirroring", "false"));
        mediaConstraints.mandatory.add(new KeyValuePair("googHighpassFilter", "true"));
        this.mAudioSource = mPeerConnectionFactory.createAudioSource(mediaConstraints);
        this.mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, this.mAudioSource);
    }

    private void updateWebRtcViewLayout(MXWebRtcView mXWebRtcView, VideoLayoutConfiguration videoLayoutConfiguration) {
        if (mXWebRtcView != null) {
            DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
            int i = videoLayoutConfiguration.mDisplayWidth > 0 ? videoLayoutConfiguration.mDisplayWidth : displayMetrics.widthPixels;
            int i2 = videoLayoutConfiguration.mDisplayHeight > 0 ? videoLayoutConfiguration.mDisplayHeight : displayMetrics.heightPixels;
            int i3 = (videoLayoutConfiguration.f127mX * i) / 100;
            int i4 = (videoLayoutConfiguration.f128mY * i2) / 100;
            LayoutParams layoutParams = new LayoutParams((i * videoLayoutConfiguration.mWidth) / 100, (i2 * videoLayoutConfiguration.mHeight) / 100);
            layoutParams.leftMargin = i3;
            layoutParams.topMargin = i4;
            mXWebRtcView.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"deprecation"})
    public void initCallUI(final JsonObject jsonObject, VideoLayoutConfiguration videoLayoutConfiguration) {
        Log.m209d(LOG_TAG, "## initCallUI(): IN");
        if (isCallEnded()) {
            Log.m217w(LOG_TAG, "## initCallUI(): skipped due to call is ended");
            return;
        }
        if (isVideo()) {
            Log.m209d(LOG_TAG, "## initCallUI(): building UI video call");
            try {
                this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        if (MXWebRtcCall.mPeerConnectionFactory == null) {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "## initCallUI(): video call and no mPeerConnectionFactory");
                            MXWebRtcCall.mPeerConnectionFactory = new PeerConnectionFactory(null);
                            EglBase.Context rootEglBaseContext = EglUtils.getRootEglBaseContext();
                            if (rootEglBaseContext != null) {
                                MXWebRtcCall.mPeerConnectionFactory.setVideoHwAccelerationOptions(rootEglBaseContext, rootEglBaseContext);
                            }
                            MXWebRtcCall.this.createVideoTrack();
                            MXWebRtcCall.this.createAudioTrack();
                            MXWebRtcCall.this.createLocalStream();
                            if (jsonObject != null) {
                                MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_RINGING);
                                MXWebRtcCall.this.setRemoteDescription(jsonObject);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initCallUI(): VideoRendererGui.setView : Exception Msg =");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            try {
                Log.m209d(LOG_TAG, "## initCallUI() building UI");
                this.mFullScreenRTCView = new MXWebRtcView(this.mContext);
                this.mFullScreenRTCView.setBackgroundColor(ContextCompat.getColor(this.mContext, 17170444));
                this.mCallView.addView(this.mFullScreenRTCView, new LayoutParams(-1, -1));
                this.mFullScreenRTCView.setVisibility(8);
                this.mPipRTCView = new MXWebRtcView(this.mContext);
                this.mCallView.addView(this.mPipRTCView, new LayoutParams(-1, -1));
                this.mPipRTCView.setBackgroundColor(ContextCompat.getColor(this.mContext, 17170445));
                this.mPipRTCView.setVisibility(8);
                if (videoLayoutConfiguration != null) {
                    updateWebRtcViewLayout(this.mPipRTCView, videoLayoutConfiguration);
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## initCallUI(): ");
                    sb2.append(videoLayoutConfiguration);
                    Log.m209d(str2, sb2.toString());
                } else {
                    updateWebRtcViewLayout(this.mPipRTCView, new VideoLayoutConfiguration(5, 5, 25, 25));
                }
            } catch (Exception e2) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## initCallUI(): Exception Msg =");
                sb3.append(e2.getMessage());
                Log.m211e(str3, sb3.toString());
            }
            if (this.mCallView != null) {
                this.mCallView.setVisibility(0);
            }
        } else {
            Log.m209d(LOG_TAG, "## initCallUI(): build audio call");
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    if (MXWebRtcCall.mPeerConnectionFactory == null) {
                        MXWebRtcCall.mPeerConnectionFactory = new PeerConnectionFactory();
                        MXWebRtcCall.this.createAudioTrack();
                        MXWebRtcCall.this.createLocalStream();
                        if (jsonObject != null) {
                            MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_RINGING);
                            MXWebRtcCall.this.setRemoteDescription(jsonObject);
                        }
                    }
                }
            });
        }
    }

    public void onPause() {
        super.onPause();
        Log.m209d(LOG_TAG, "onPause");
        try {
            if (!isCallEnded()) {
                Log.m209d(LOG_TAG, "onPause with active call");
                if (!isVideoRecordingMuted()) {
                    muteVideoRecording(true);
                    this.mIsCameraUnplugged = true;
                }
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onPause failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void onResume() {
        super.onResume();
        Log.m209d(LOG_TAG, "onResume");
        try {
            if (!isCallEnded()) {
                Log.m209d(LOG_TAG, "onResume with active call");
                if (this.mIsCameraUnplugged) {
                    muteVideoRecording(false);
                    this.mIsCameraUnplugged = false;
                }
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onResume failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void placeCall(VideoLayoutConfiguration videoLayoutConfiguration) {
        Log.m209d(LOG_TAG, "placeCall");
        super.placeCall(videoLayoutConfiguration);
        dispatchOnStateDidChange(IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA);
        initCallUI(null, videoLayoutConfiguration);
    }

    /* access modifiers changed from: private */
    public void setRemoteDescription(JsonObject jsonObject) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setRemoteDescription ");
        sb.append(jsonObject);
        Log.m209d(str, sb.toString());
        SessionDescription sessionDescription = null;
        try {
            if (jsonObject.has("offer")) {
                JsonObject asJsonObject = jsonObject.getAsJsonObject("offer");
                String asString = asJsonObject.get("type").getAsString();
                String asString2 = asJsonObject.get("sdp").getAsString();
                if (!TextUtils.isEmpty(asString) && !TextUtils.isEmpty(asString2)) {
                    sessionDescription = new SessionDescription(Type.OFFER, asString2);
                }
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## setRemoteDescription(): Exception Msg=");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
        }
        this.mPeerConnection.setRemoteDescription(new SdpObserver() {
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.m209d(MXWebRtcCall.LOG_TAG, "setRemoteDescription onCreateSuccess");
            }

            public void onSetSuccess() {
                Log.m209d(MXWebRtcCall.LOG_TAG, "setRemoteDescription onSetSuccess");
                MXWebRtcCall.this.mIsIncomingPrepared = true;
                MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                    public void run() {
                        MXWebRtcCall.this.checkPendingCandidates();
                    }
                });
            }

            public void onCreateFailure(String str) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setRemoteDescription onCreateFailure ");
                sb.append(str);
                Log.m211e(access$100, sb.toString());
                MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
            }

            public void onSetFailure(String str) {
                String access$100 = MXWebRtcCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setRemoteDescription onSetFailure ");
                sb.append(str);
                Log.m211e(access$100, sb.toString());
                MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
            }
        }, sessionDescription);
    }

    public void prepareIncomingCall(final JsonObject jsonObject, String str, final VideoLayoutConfiguration videoLayoutConfiguration) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## prepareIncomingCall : call state ");
        sb.append(getCallState());
        Log.m209d(str2, sb.toString());
        super.prepareIncomingCall(jsonObject, str, videoLayoutConfiguration);
        this.mCallId = str;
        if (IMXCall.CALL_STATE_READY.equals(getCallState())) {
            this.mIsIncoming = true;
            dispatchOnStateDidChange(IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA);
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.initCallUI(jsonObject, videoLayoutConfiguration);
                }
            });
        } else if (IMXCall.CALL_STATE_CREATED.equals(getCallState())) {
            this.mCallInviteParams = jsonObject;
            try {
                setIsVideo(this.mCallInviteParams.get("offer").getAsJsonObject().get("sdp").getAsString().contains("m=video"));
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## prepareIncomingCall(): Exception Msg=");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }

    public void launchIncomingCall(VideoLayoutConfiguration videoLayoutConfiguration) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("launchIncomingCall : call state ");
        sb.append(getCallState());
        Log.m209d(str, sb.toString());
        super.launchIncomingCall(videoLayoutConfiguration);
        if (IMXCall.CALL_STATE_READY.equals(getCallState())) {
            prepareIncomingCall(this.mCallInviteParams, this.mCallId, videoLayoutConfiguration);
        }
    }

    private void onCallAnswer(final Event event) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onCallAnswer : call state ");
        sb.append(getCallState());
        Log.m209d(str, sb.toString());
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mPeerConnection != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_CONNECTING);
                    SessionDescription sessionDescription = null;
                    try {
                        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
                        if (contentAsJsonObject.has("answer")) {
                            JsonObject asJsonObject = contentAsJsonObject.getAsJsonObject("answer");
                            String asString = asJsonObject.get("type").getAsString();
                            String asString2 = asJsonObject.get("sdp").getAsString();
                            if (!TextUtils.isEmpty(asString) && !TextUtils.isEmpty(asString2) && asString.equals("answer")) {
                                sessionDescription = new SessionDescription(Type.ANSWER, asString2);
                            }
                        }
                    } catch (Exception e) {
                        String access$100 = MXWebRtcCall.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onCallAnswer : ");
                        sb.append(e.getMessage());
                        Log.m209d(access$100, sb.toString());
                    }
                    MXWebRtcCall.this.mPeerConnection.setRemoteDescription(new SdpObserver() {
                        public void onCreateSuccess(SessionDescription sessionDescription) {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "setRemoteDescription onCreateSuccess");
                        }

                        public void onSetSuccess() {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "setRemoteDescription onSetSuccess");
                        }

                        public void onCreateFailure(String str) {
                            String access$100 = MXWebRtcCall.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("setRemoteDescription onCreateFailure ");
                            sb.append(str);
                            Log.m211e(access$100, sb.toString());
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                        }

                        public void onSetFailure(String str) {
                            String access$100 = MXWebRtcCall.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("setRemoteDescription onSetFailure ");
                            sb.append(str);
                            Log.m211e(access$100, sb.toString());
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                        }
                    }, sessionDescription);
                }
            });
        }
    }

    private void onCallHangup(final int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onCallHangup(): call state=");
        sb.append(getCallState());
        Log.m209d(str, sb.toString());
        String callState = getCallState();
        if (!IMXCall.CALL_STATE_CREATED.equals(callState) && this.mPeerConnection != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.terminate(i);
                }
            });
        } else if (IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA.equals(callState) && isVideo()) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXWebRtcCall.this.terminate(i);
                }
            });
        }
    }

    private void onNewCandidates(JsonArray jsonArray) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onNewCandidates(): call state ");
        sb.append(getCallState());
        sb.append(" with candidates ");
        sb.append(jsonArray);
        Log.m209d(str, sb.toString());
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mPeerConnection != null) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject asJsonObject = jsonArray.get(i).getAsJsonObject();
                try {
                    arrayList.add(new IceCandidate(asJsonObject.get("sdpMid").getAsString(), asJsonObject.get("sdpMLineIndex").getAsInt(), asJsonObject.get("candidate").getAsString()));
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onNewCandidates(): Exception Msg=");
                    sb2.append(e.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                IceCandidate iceCandidate = (IceCandidate) it.next();
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## onNewCandidates(): addIceCandidate ");
                sb3.append(iceCandidate);
                Log.m209d(str3, sb3.toString());
                this.mPeerConnection.addIceCandidate(iceCandidate);
            }
        }
    }

    private void addCandidates(JsonArray jsonArray) {
        if (this.mIsIncomingPrepared || !isIncoming()) {
            Log.m209d(LOG_TAG, "addCandidates : ready");
            onNewCandidates(jsonArray);
            return;
        }
        synchronized (LOG_TAG) {
            Log.m209d(LOG_TAG, "addCandidates : pending");
            this.mPendingCandidates.addAll(jsonArray);
        }
    }

    /* access modifiers changed from: private */
    public void checkPendingCandidates() {
        Log.m209d(LOG_TAG, "checkPendingCandidates");
        synchronized (LOG_TAG) {
            onNewCandidates(this.mPendingCandidates);
            this.mPendingCandidates = new JsonArray();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0083, code lost:
        if (r0.equals(com.opengarden.firechat.matrixsdk.rest.model.Event.EVENT_TYPE_CALL_INVITE) == false) goto L_0x009a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleCallEvent(com.opengarden.firechat.matrixsdk.rest.model.Event r6) {
        /*
            r5 = this;
            super.handleCallEvent(r6)
            boolean r0 = r6.isCallEvent()
            if (r0 == 0) goto L_0x00b8
            java.lang.String r0 = r6.getType()
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "handleCallEvent "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
            java.lang.String r1 = r6.getSender()
            com.opengarden.firechat.matrixsdk.MXSession r2 = r5.mSession
            java.lang.String r2 = r2.getMyUserId()
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            r2 = 0
            if (r1 != 0) goto L_0x0067
            java.lang.String r1 = "m.call.answer"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0045
            boolean r1 = r5.mIsIncoming
            if (r1 != 0) goto L_0x0045
            r5.onCallAnswer(r6)
            goto L_0x00b8
        L_0x0045:
            java.lang.String r1 = "m.call.candidates"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x005b
            com.google.gson.JsonObject r6 = r6.getContentAsJsonObject()
            java.lang.String r0 = "candidates"
            com.google.gson.JsonArray r6 = r6.getAsJsonArray(r0)
            r5.addCandidates(r6)
            goto L_0x00b8
        L_0x005b:
            java.lang.String r6 = "m.call.hangup"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x00b8
            r5.onCallHangup(r2)
            goto L_0x00b8
        L_0x0067:
            r6 = -1
            int r1 = r0.hashCode()
            r3 = -1593761459(0xffffffffa101214d, float:-4.3750973E-19)
            r4 = 1
            if (r1 == r3) goto L_0x0090
            r3 = -1405527012(0xffffffffac395c1c, float:-2.6341212E-12)
            if (r1 == r3) goto L_0x0086
            r3 = -1364651880(0xffffffffaea91098, float:-7.688178E-11)
            if (r1 == r3) goto L_0x007d
            goto L_0x009a
        L_0x007d:
            java.lang.String r1 = "m.call.invite"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x009a
            goto L_0x009b
        L_0x0086:
            java.lang.String r1 = "m.call.hangup"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x009a
            r2 = 2
            goto L_0x009b
        L_0x0090:
            java.lang.String r1 = "m.call.answer"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x009a
            r2 = 1
            goto L_0x009b
        L_0x009a:
            r2 = -1
        L_0x009b:
            switch(r2) {
                case 0: goto L_0x00ae;
                case 1: goto L_0x00a3;
                case 2: goto L_0x009f;
                default: goto L_0x009e;
            }
        L_0x009e:
            goto L_0x00b8
        L_0x009f:
            r5.onCallHangup(r4)
            goto L_0x00b8
        L_0x00a3:
            android.os.Handler r6 = r5.mUIThreadHandler
            com.opengarden.firechat.matrixsdk.call.MXWebRtcCall$15 r0 = new com.opengarden.firechat.matrixsdk.call.MXWebRtcCall$15
            r0.<init>()
            r6.post(r0)
            goto L_0x00b8
        L_0x00ae:
            android.os.Handler r6 = r5.mUIThreadHandler
            com.opengarden.firechat.matrixsdk.call.MXWebRtcCall$14 r0 = new com.opengarden.firechat.matrixsdk.call.MXWebRtcCall$14
            r0.<init>()
            r6.post(r0)
        L_0x00b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.call.MXWebRtcCall.handleCallEvent(com.opengarden.firechat.matrixsdk.rest.model.Event):void");
    }

    public void answer() {
        super.answer();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("answer ");
        sb.append(getCallState());
        Log.m209d(str, sb.toString());
        if (!IMXCall.CALL_STATE_CREATED.equals(getCallState()) && this.mPeerConnection != null) {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    if (MXWebRtcCall.this.mPeerConnection == null) {
                        Log.m209d(MXWebRtcCall.LOG_TAG, "answer the connection has been closed");
                        return;
                    }
                    MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_CREATE_ANSWER);
                    MediaConstraints mediaConstraints = new MediaConstraints();
                    mediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
                    mediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", MXWebRtcCall.this.isVideo() ? "true" : "false"));
                    MXWebRtcCall.this.mPeerConnection.createAnswer(new SdpObserver() {
                        public void onCreateSuccess(SessionDescription sessionDescription) {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "createAnswer onCreateSuccess");
                            final SessionDescription sessionDescription2 = new SessionDescription(sessionDescription.type, sessionDescription.description);
                            MXWebRtcCall.this.mUIThreadHandler.post(new Runnable() {
                                public void run() {
                                    if (MXWebRtcCall.this.mPeerConnection != null) {
                                        MXWebRtcCall.this.mPeerConnection.setLocalDescription(new SdpObserver() {
                                            public void onCreateSuccess(SessionDescription sessionDescription) {
                                                Log.m209d(MXWebRtcCall.LOG_TAG, "setLocalDescription onCreateSuccess");
                                            }

                                            public void onSetSuccess() {
                                                Log.m209d(MXWebRtcCall.LOG_TAG, "setLocalDescription onSetSuccess");
                                                MXWebRtcCall.this.sendAnswer(sessionDescription2);
                                                MXWebRtcCall.this.dispatchOnStateDidChange(IMXCall.CALL_STATE_CONNECTING);
                                            }

                                            public void onCreateFailure(String str) {
                                                String access$100 = MXWebRtcCall.LOG_TAG;
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("setLocalDescription onCreateFailure ");
                                                sb.append(str);
                                                Log.m211e(access$100, sb.toString());
                                                MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                                                MXWebRtcCall.this.hangup(null);
                                            }

                                            public void onSetFailure(String str) {
                                                String access$100 = MXWebRtcCall.LOG_TAG;
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("setLocalDescription onSetFailure ");
                                                sb.append(str);
                                                Log.m211e(access$100, sb.toString());
                                                MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                                                MXWebRtcCall.this.hangup(null);
                                            }
                                        }, sessionDescription2);
                                    }
                                }
                            });
                        }

                        public void onSetSuccess() {
                            Log.m209d(MXWebRtcCall.LOG_TAG, "createAnswer onSetSuccess");
                        }

                        public void onCreateFailure(String str) {
                            String access$100 = MXWebRtcCall.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("createAnswer onCreateFailure ");
                            sb.append(str);
                            Log.m211e(access$100, sb.toString());
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                            MXWebRtcCall.this.hangup(null);
                        }

                        public void onSetFailure(String str) {
                            String access$100 = MXWebRtcCall.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("createAnswer onSetFailure ");
                            sb.append(str);
                            Log.m211e(access$100, sb.toString());
                            MXWebRtcCall.this.dispatchOnCallError(IMXCall.CALL_ERROR_CAMERA_INIT_FAILED);
                            MXWebRtcCall.this.hangup(null);
                        }
                    }, mediaConstraints);
                }
            });
        }
    }

    public void hangup(String str) {
        super.hangup(str);
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## hangup(): reason=");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (!isCallEnded()) {
            sendHangup(str);
            terminate(-1);
        }
    }

    public String getCallState() {
        return this.mCallState;
    }

    public View getCallView() {
        return this.mCallView;
    }

    public int getVisibility() {
        if (this.mCallView != null) {
            return this.mCallView.getVisibility();
        }
        return 8;
    }

    public boolean setVisibility(int i) {
        if (this.mCallView == null) {
            return false;
        }
        this.mCallView.setVisibility(i);
        return true;
    }

    public void onAnsweredElsewhere() {
        super.onAnsweredElsewhere();
        String callState = getCallState();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onAnsweredElsewhere in state ");
        sb.append(callState);
        Log.m209d(str, sb.toString());
        if (!isCallEnded() && !this.mIsAnswered) {
            dispatchAnsweredElsewhere();
            terminate(-1);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnStateDidChange(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("dispatchOnStateDidChange ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        this.mCallState = str;
        if ((IMXCall.CALL_STATE_CONNECTING.equals(this.mCallState) || IMXCall.CALL_STATE_CONNECTED.equals(this.mCallState)) && this.mCallTimeoutTimer != null) {
            this.mCallTimeoutTimer.cancel();
            this.mCallTimeoutTimer = null;
        }
        super.dispatchOnStateDidChange(str);
    }
}
