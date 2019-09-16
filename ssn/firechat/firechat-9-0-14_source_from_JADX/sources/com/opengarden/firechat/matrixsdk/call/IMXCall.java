package com.opengarden.firechat.matrixsdk.call;

import android.view.View;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public interface IMXCall {
    public static final String CALL_ERROR_CALL_INIT_FAILED = "IMXCall.CALL_ERROR_CALL_INIT_FAILED";
    public static final String CALL_ERROR_CAMERA_INIT_FAILED = "IMXCall.CALL_ERROR_CAMERA_INIT_FAILED";
    public static final String CALL_ERROR_ICE_FAILED = "IMXCall.CALL_ERROR_ICE_FAILED";
    public static final String CALL_ERROR_USER_NOT_RESPONDING = "IMXCall.CALL_ERROR_USER_NOT_RESPONDING";
    public static final String CALL_STATE_CONNECTED = "IMXCall.CALL_STATE_CONNECTED";
    public static final String CALL_STATE_CONNECTING = "IMXCall.CALL_STATE_CONNECTING";
    public static final String CALL_STATE_CREATED = "IMXCall.CALL_STATE_CREATED";
    public static final String CALL_STATE_CREATE_ANSWER = "IMXCall.CALL_STATE_CREATE_ANSWER";
    public static final String CALL_STATE_CREATING_CALL_VIEW = "IMXCall.CALL_STATE_CREATING_CALL_VIEW";
    public static final String CALL_STATE_ENDED = "IMXCall.CALL_STATE_ENDED";
    public static final String CALL_STATE_INVITE_SENT = "IMXCall.CALL_STATE_INVITE_SENT";
    public static final String CALL_STATE_READY = "IMXCall.CALL_STATE_READY";
    public static final String CALL_STATE_RINGING = "IMXCall.CALL_STATE_RINGING";
    public static final String CALL_STATE_WAIT_CREATE_OFFER = "IMXCall.CALL_STATE_WAIT_CREATE_OFFER";
    public static final String CALL_STATE_WAIT_LOCAL_MEDIA = "IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA";
    public static final int END_CALL_REASON_PEER_HANG_UP = 0;
    public static final int END_CALL_REASON_PEER_HANG_UP_ELSEWHERE = 1;
    public static final int END_CALL_REASON_UNDEFINED = -1;
    public static final int END_CALL_REASON_USER_HIMSELF = 2;

    void addListener(IMXCallListener iMXCallListener);

    void answer();

    void createCallView();

    long getCallElapsedTime();

    String getCallId();

    Room getCallSignalingRoom();

    long getCallStartTime();

    String getCallState();

    View getCallView();

    Room getRoom();

    MXSession getSession();

    int getVisibility();

    void handleCallEvent(Event event);

    void hangup(String str);

    boolean isCameraSwitched();

    boolean isConference();

    boolean isIncoming();

    boolean isSwitchCameraSupported();

    boolean isVideo();

    boolean isVideoRecordingMuted();

    void launchIncomingCall(VideoLayoutConfiguration videoLayoutConfiguration);

    void muteVideoRecording(boolean z);

    void onAnsweredElsewhere();

    void onPause();

    void onResume();

    void placeCall(VideoLayoutConfiguration videoLayoutConfiguration);

    void prepareIncomingCall(JsonObject jsonObject, String str, VideoLayoutConfiguration videoLayoutConfiguration);

    void removeListener(IMXCallListener iMXCallListener);

    void setCallId(String str);

    void setIsConference(boolean z);

    void setIsVideo(boolean z);

    void setRooms(Room room, Room room2);

    boolean setVisibility(int i);

    boolean switchRearFrontCamera();

    void updateLocalVideoRendererPosition(VideoLayoutConfiguration videoLayoutConfiguration);
}
