package com.opengarden.firechat.matrixsdk.call;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import com.google.android.gms.dynamite.ProviderConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class MXCall implements IMXCall {
    public static final int CALL_TIMEOUT_MS = 120000;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXCall";
    protected String mCallId;
    private final Set<IMXCallListener> mCallListeners = new HashSet();
    protected Room mCallSignalingRoom;
    protected Timer mCallTimeoutTimer;
    protected Room mCallingRoom;
    protected Context mContext;
    private boolean mIsConference = false;
    protected boolean mIsIncoming = false;
    protected boolean mIsVideoCall = false;
    /* access modifiers changed from: private */
    public Event mPendingEvent;
    protected final ArrayList<Event> mPendingEvents = new ArrayList<>();
    protected MXSession mSession;
    private long mStartTime = -1;
    protected JsonElement mTurnServer;
    final Handler mUIThreadHandler = new Handler();

    public void answer() {
    }

    public void createCallView() {
    }

    public String getCallState() {
        return null;
    }

    public View getCallView() {
        return null;
    }

    public int getVisibility() {
        return 8;
    }

    public void handleCallEvent(Event event) {
    }

    public void hangup(String str) {
    }

    public void launchIncomingCall(VideoLayoutConfiguration videoLayoutConfiguration) {
    }

    public void onAnsweredElsewhere() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void placeCall(VideoLayoutConfiguration videoLayoutConfiguration) {
    }

    public boolean setVisibility(int i) {
        return false;
    }

    public void prepareIncomingCall(JsonObject jsonObject, String str, VideoLayoutConfiguration videoLayoutConfiguration) {
        setIsIncoming(true);
    }

    public void updateLocalVideoRendererPosition(VideoLayoutConfiguration videoLayoutConfiguration) {
        Log.m217w(LOG_TAG, "## updateLocalVideoRendererPosition(): not implemented");
    }

    public boolean switchRearFrontCamera() {
        Log.m217w(LOG_TAG, "## switchRearFrontCamera(): not implemented");
        return false;
    }

    public boolean isCameraSwitched() {
        Log.m217w(LOG_TAG, "## isCameraSwitched(): not implemented");
        return false;
    }

    public boolean isSwitchCameraSupported() {
        Log.m217w(LOG_TAG, "## isSwitchCameraSupported(): not implemented");
        return false;
    }

    public String getCallId() {
        return this.mCallId;
    }

    public void setCallId(String str) {
        this.mCallId = str;
    }

    public Room getRoom() {
        return this.mCallingRoom;
    }

    public Room getCallSignalingRoom() {
        return this.mCallSignalingRoom;
    }

    public void setRooms(Room room, Room room2) {
        this.mCallingRoom = room;
        this.mCallSignalingRoom = room2;
    }

    public MXSession getSession() {
        return this.mSession;
    }

    public boolean isIncoming() {
        return this.mIsIncoming;
    }

    private void setIsIncoming(boolean z) {
        this.mIsIncoming = z;
    }

    public void setIsVideo(boolean z) {
        this.mIsVideoCall = z;
    }

    public boolean isVideo() {
        return this.mIsVideoCall;
    }

    public void setIsConference(boolean z) {
        this.mIsConference = z;
    }

    public boolean isConference() {
        return this.mIsConference;
    }

    public boolean isCallEnded() {
        return TextUtils.equals(IMXCall.CALL_STATE_ENDED, getCallState());
    }

    public long getCallStartTime() {
        return this.mStartTime;
    }

    public long getCallElapsedTime() {
        if (-1 == this.mStartTime) {
            return -1;
        }
        return (System.currentTimeMillis() - this.mStartTime) / 1000;
    }

    public void addListener(IMXCallListener iMXCallListener) {
        if (iMXCallListener != null) {
            synchronized (LOG_TAG) {
                this.mCallListeners.add(iMXCallListener);
            }
        }
    }

    public void removeListener(IMXCallListener iMXCallListener) {
        if (iMXCallListener != null) {
            synchronized (LOG_TAG) {
                this.mCallListeners.remove(iMXCallListener);
            }
        }
    }

    public void clearListeners() {
        synchronized (LOG_TAG) {
            this.mCallListeners.clear();
        }
    }

    private Collection<IMXCallListener> getCallListeners() {
        HashSet hashSet;
        synchronized (LOG_TAG) {
            hashSet = new HashSet(this.mCallListeners);
        }
        return hashSet;
    }

    /* access modifiers changed from: protected */
    public void dispatchOnCallViewCreated(View view) {
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "## dispatchOnCallViewCreated(): the call is ended");
            return;
        }
        Log.m209d(LOG_TAG, "## dispatchOnCallViewCreated()");
        for (IMXCallListener onCallViewCreated : getCallListeners()) {
            try {
                onCallViewCreated.onCallViewCreated(view);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## dispatchOnCallViewCreated(): Exception Msg=");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnReady() {
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "## dispatchOnReady() : the call is ended");
            return;
        }
        Log.m209d(LOG_TAG, "## dispatchOnReady()");
        for (IMXCallListener onReady : getCallListeners()) {
            try {
                onReady.onReady();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## dispatchOnReady(): Exception Msg=");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnCallError(String str) {
        if (isCallEnded()) {
            Log.m209d(LOG_TAG, "## dispatchOnCallError() : the call is ended");
            return;
        }
        Log.m209d(LOG_TAG, "## dispatchOnCallError()");
        for (IMXCallListener onCallError : getCallListeners()) {
            try {
                onCallError.onCallError(str);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## dispatchOnCallError(): ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnStateDidChange(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## dispatchOnCallErrorOnStateDidChange(): ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (TextUtils.equals(IMXCall.CALL_STATE_CONNECTED, str) && -1 == this.mStartTime) {
            this.mStartTime = System.currentTimeMillis();
        }
        if (TextUtils.equals(IMXCall.CALL_STATE_ENDED, str)) {
            this.mStartTime = -1;
        }
        for (IMXCallListener onStateDidChange : getCallListeners()) {
            try {
                onStateDidChange.onStateDidChange(str);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## dispatchOnStateDidChange(): Exception Msg=");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchAnsweredElsewhere() {
        Log.m209d(LOG_TAG, "## dispatchAnsweredElsewhere()");
        for (IMXCallListener onCallAnsweredElsewhere : getCallListeners()) {
            try {
                onCallAnsweredElsewhere.onCallAnsweredElsewhere();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## dispatchAnsweredElsewhere(): Exception Msg=");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnCallEnd(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## dispatchOnCallEnd(): endReason=");
        sb.append(i);
        Log.m209d(str, sb.toString());
        for (IMXCallListener onCallEnd : getCallListeners()) {
            try {
                onCallEnd.onCallEnd(i);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## dispatchOnCallEnd(): Exception Msg=");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void sendNextEvent() {
        this.mUIThreadHandler.post(new Runnable() {
            public void run() {
                if (MXCall.this.isCallEnded() && MXCall.this.mPendingEvents != null) {
                    MXCall.this.mPendingEvents.clear();
                }
                if (MXCall.this.mPendingEvent == null && MXCall.this.mPendingEvents.size() != 0) {
                    MXCall.this.mPendingEvent = (Event) MXCall.this.mPendingEvents.get(0);
                    MXCall.this.mPendingEvents.remove(MXCall.this.mPendingEvent);
                    String access$100 = MXCall.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## sendNextEvent() : sending event of type ");
                    sb.append(MXCall.this.mPendingEvent.getType());
                    sb.append(" event id ");
                    sb.append(MXCall.this.mPendingEvent.eventId);
                    Log.m209d(access$100, sb.toString());
                    MXCall.this.mCallSignalingRoom.sendEvent(MXCall.this.mPendingEvent, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            MXCall.this.mUIThreadHandler.post(new Runnable() {
                                public void run() {
                                    String access$100 = MXCall.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("## sendNextEvent() : event ");
                                    sb.append(MXCall.this.mPendingEvent.eventId);
                                    sb.append(" is sent");
                                    Log.m209d(access$100, sb.toString());
                                    MXCall.this.mPendingEvent = null;
                                    MXCall.this.sendNextEvent();
                                }
                            });
                        }

                        private void commonFailure(String str) {
                            String access$100 = MXCall.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## sendNextEvent() : event ");
                            sb.append(MXCall.this.mPendingEvent.eventId);
                            sb.append(" failed to be sent ");
                            sb.append(str);
                            Log.m209d(access$100, sb.toString());
                            if (TextUtils.equals(MXCall.this.mPendingEvent.getType(), Event.EVENT_TYPE_CALL_CANDIDATES)) {
                                MXCall.this.mUIThreadHandler.post(new Runnable() {
                                    public void run() {
                                        MXCall.this.mPendingEvent = null;
                                        MXCall.this.sendNextEvent();
                                    }
                                });
                            } else {
                                MXCall.this.hangup(str);
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            commonFailure(exc.getLocalizedMessage());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            commonFailure(matrixError.getLocalizedMessage());
                        }

                        public void onUnexpectedError(Exception exc) {
                            commonFailure(exc.getLocalizedMessage());
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void dispatchOnPreviewSizeChanged(int i, int i2) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## dispatchOnPreviewSizeChanged(): width =");
        sb.append(i);
        sb.append(" - height =");
        sb.append(i2);
        Log.m209d(str, sb.toString());
        for (IMXCallListener onPreviewSizeChanged : getCallListeners()) {
            try {
                onPreviewSizeChanged.onPreviewSizeChanged(i, i2);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## dispatchOnPreviewSizeChanged(): Exception Msg=");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void sendHangup(String str) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(ProviderConstants.API_COLNAME_FEATURE_VERSION, new JsonPrimitive((Number) Integer.valueOf(0)));
        jsonObject.add("call_id", new JsonPrimitive(this.mCallId));
        if (!TextUtils.isEmpty(str)) {
            jsonObject.add("reason", new JsonPrimitive(str));
        }
        Event event = new Event(Event.EVENT_TYPE_CALL_HANGUP, jsonObject, this.mSession.getCredentials().userId, this.mCallSignalingRoom.getRoomId());
        this.mUIThreadHandler.post(new Runnable() {
            public void run() {
                MXCall.this.dispatchOnCallEnd(2);
            }
        });
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## sendHangup(): reason=");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        this.mCallSignalingRoom.sendEvent(event, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(MXCall.LOG_TAG, "## sendHangup(): onSuccess");
            }

            public void onNetworkError(Exception exc) {
                String access$100 = MXCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendHangup(): onNetworkError Msg=");
                sb.append(exc.getMessage());
                Log.m209d(access$100, sb.toString());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$100 = MXCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendHangup(): onMatrixError Msg=");
                sb.append(matrixError.getMessage());
                Log.m209d(access$100, sb.toString());
            }

            public void onUnexpectedError(Exception exc) {
                String access$100 = MXCall.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendHangup(): onUnexpectedError Msg=");
                sb.append(exc.getMessage());
                Log.m209d(access$100, sb.toString());
            }
        });
    }

    public void muteVideoRecording(boolean z) {
        Log.m217w(LOG_TAG, "## muteVideoRecording(): not implemented");
    }

    public boolean isVideoRecordingMuted() {
        Log.m217w(LOG_TAG, "## muteVideoRecording(): not implemented - default value = false");
        return false;
    }
}
