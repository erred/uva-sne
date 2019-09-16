package com.opengarden.firechat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.VectorCallViewActivity;
import com.opengarden.firechat.activity.VectorHomeActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.CallSoundsManager;
import com.opengarden.firechat.matrixsdk.call.CallSoundsManager.OnMediaListener;
import com.opengarden.firechat.matrixsdk.call.HeadsetConnectionReceiver;
import com.opengarden.firechat.matrixsdk.call.HeadsetConnectionReceiver.OnHeadsetStatusUpdateListener;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.call.IMXCallListener;
import com.opengarden.firechat.matrixsdk.call.IMXCallsManagerListener;
import com.opengarden.firechat.matrixsdk.call.MXCallListener;
import com.opengarden.firechat.matrixsdk.call.MXCallsManagerListener;
import com.opengarden.firechat.matrixsdk.call.VideoLayoutConfiguration;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.services.EventStreamService;

public class CallsManager {
    public static final String HANGUP_MSG_USER_CANCEL = "user hangup";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "CallsManager";
    private static final String RING_TONE_START_RINGING = "ring.ogg";
    private static CallsManager mSharedInstance;
    /* access modifiers changed from: private */
    public IMXCall mActiveCall;
    /* access modifiers changed from: private */
    public Activity mCallActivity;
    /* access modifiers changed from: private */
    public final IMXCallListener mCallListener = new MXCallListener() {
        public void onStateDidChange(final String str) {
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    if (CallsManager.this.mActiveCall == null) {
                        Log.m209d(CallsManager.LOG_TAG, "## onStateDidChange() : no more active call");
                        return;
                    }
                    String access$300 = CallsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("dispatchOnStateDidChange ");
                    sb.append(CallsManager.this.mActiveCall.getCallId());
                    sb.append(" : ");
                    sb.append(str);
                    Log.m209d(access$300, sb.toString());
                    String str = str;
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1444885671:
                            if (str.equals(IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA)) {
                                c = 4;
                                break;
                            }
                            break;
                        case -215535408:
                            if (str.equals(IMXCall.CALL_STATE_WAIT_CREATE_OFFER)) {
                                c = 5;
                                break;
                            }
                            break;
                        case 183694318:
                            if (str.equals(IMXCall.CALL_STATE_CREATE_ANSWER)) {
                                c = 3;
                                break;
                            }
                            break;
                        case 946025035:
                            if (str.equals(IMXCall.CALL_STATE_CONNECTING)) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1322015527:
                            if (str.equals(IMXCall.CALL_STATE_ENDED)) {
                                c = 8;
                                break;
                            }
                            break;
                        case 1700515443:
                            if (str.equals(IMXCall.CALL_STATE_CREATING_CALL_VIEW)) {
                                c = 1;
                                break;
                            }
                            break;
                        case 1781900309:
                            if (str.equals(IMXCall.CALL_STATE_CREATED)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 1831632118:
                            if (str.equals(IMXCall.CALL_STATE_CONNECTED)) {
                                c = 6;
                                break;
                            }
                            break;
                        case 1960371423:
                            if (str.equals(IMXCall.CALL_STATE_RINGING)) {
                                c = 7;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (CallsManager.this.mActiveCall.isIncoming()) {
                                EventStreamService.getInstance().displayIncomingCallNotification(CallsManager.this.mActiveCall.getSession(), CallsManager.this.mActiveCall.getRoom(), null, CallsManager.this.mActiveCall.getCallId(), null);
                                CallsManager.this.startRinging();
                                break;
                            }
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            if (CallsManager.this.mActiveCall.isIncoming()) {
                                CallsManager.this.mCallSoundsManager.stopSounds();
                                break;
                            }
                            break;
                        case 6:
                            EventStreamService.getInstance().displayCallInProgressNotification(CallsManager.this.mActiveCall.getSession(), CallsManager.this.mActiveCall.getRoom(), CallsManager.this.mActiveCall.getCallId());
                            CallsManager.this.mCallSoundsManager.stopSounds();
                            CallsManager.this.requestAudioFocus();
                            CallsManager.this.mUiHandler.post(new Runnable() {
                                public void run() {
                                    if (CallsManager.this.mActiveCall != null) {
                                        CallsManager.this.setCallSpeakerphoneOn(CallsManager.this.mActiveCall.isVideo() && !HeadsetConnectionReceiver.isHeadsetPlugged(CallsManager.this.mContext));
                                        CallsManager.this.mCallSoundsManager.setMicrophoneMute(false);
                                        return;
                                    }
                                    Log.m211e(CallsManager.LOG_TAG, "## onStateDidChange() : no more active call");
                                }
                            });
                            break;
                        case 7:
                            if (!CallsManager.this.mActiveCall.isIncoming()) {
                                CallsManager.this.startRingBackSound();
                                break;
                            }
                            break;
                        case 8:
                            if ((TextUtils.equals(IMXCall.CALL_STATE_RINGING, CallsManager.this.mPrevCallState) && !CallsManager.this.mActiveCall.isIncoming()) || TextUtils.equals(IMXCall.CALL_STATE_INVITE_SENT, CallsManager.this.mPrevCallState)) {
                                if (!CallsManager.this.mIsStoppedByUser) {
                                    CallsManager.this.showToast(CallsManager.this.mContext.getString(C1299R.string.call_error_user_not_responding));
                                }
                                CallsManager.this.endCall(true);
                                break;
                            } else {
                                CallsManager.this.endCall(false);
                                break;
                            }
                            break;
                    }
                    CallsManager.this.mPrevCallState = str;
                }
            });
        }

        public void onCallError(final String str) {
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    if (CallsManager.this.mActiveCall == null) {
                        Log.m209d(CallsManager.LOG_TAG, "## onCallError() : no more active call");
                        return;
                    }
                    String access$300 = CallsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCallError(): error=");
                    sb.append(str);
                    Log.m209d(access$300, sb.toString());
                    if (IMXCall.CALL_ERROR_USER_NOT_RESPONDING.equals(str)) {
                        CallsManager.this.showToast(CallsManager.this.mContext.getString(C1299R.string.call_error_user_not_responding));
                    } else if (IMXCall.CALL_ERROR_ICE_FAILED.equals(str)) {
                        CallsManager.this.showToast(CallsManager.this.mContext.getString(C1299R.string.call_error_ice_failed));
                    } else if (IMXCall.CALL_ERROR_CAMERA_INIT_FAILED.equals(str)) {
                        CallsManager.this.showToast(CallsManager.this.mContext.getString(C1299R.string.call_error_camera_init_failed));
                    } else {
                        CallsManager.this.showToast(str);
                    }
                    CallsManager.this.endCall(IMXCall.CALL_ERROR_USER_NOT_RESPONDING.equals(str));
                }
            });
        }

        public void onCallAnsweredElsewhere() {
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    if (CallsManager.this.mActiveCall == null) {
                        Log.m209d(CallsManager.LOG_TAG, "## onCallError() : no more active call");
                        return;
                    }
                    String access$300 = CallsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onCallAnsweredElsewhere ");
                    sb.append(CallsManager.this.mActiveCall.getCallId());
                    Log.m209d(access$300, sb.toString());
                    CallsManager.this.showToast(CallsManager.this.mContext.getString(C1299R.string.call_error_answered_elsewhere));
                    CallsManager.this.releaseCall();
                }
            });
        }

        public void onCallEnd(int i) {
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    if (CallsManager.this.mActiveCall == null) {
                        Log.m209d(CallsManager.LOG_TAG, "## onCallEnd() : no more active call");
                    } else {
                        CallsManager.this.endCall(false);
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public CallSoundsManager mCallSoundsManager;
    private View mCallView = null;
    private final IMXCallsManagerListener mCallsManagerListener = new MXCallsManagerListener() {
        public void onIncomingCall(final IMXCall iMXCall, final MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    VectorApp instance = VectorApp.getInstance();
                    String access$300 = CallsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onIncomingCall () :");
                    sb.append(iMXCall.getCallId());
                    Log.m209d(access$300, sb.toString());
                    TelephonyManager telephonyManager = (TelephonyManager) instance.getSystemService("phone");
                    int callState = (telephonyManager == null || telephonyManager.getSimState() != 5) ? 0 : telephonyManager.getCallState();
                    String access$3002 = CallsManager.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onIncomingCall () : currentCallState(GSM) = ");
                    sb2.append(callState);
                    Log.m209d(access$3002, sb2.toString());
                    if (callState == 2 || callState == 1) {
                        Log.m209d(CallsManager.LOG_TAG, "## onIncomingCall () : rejected because GSM Call is in progress");
                        iMXCall.hangup("busy");
                    } else if (CallsManager.this.mActiveCall != null) {
                        String access$3003 = CallsManager.LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## onIncomingCall () : rejected because ");
                        sb3.append(CallsManager.this.mActiveCall);
                        sb3.append(" is in progress");
                        Log.m209d(access$3003, sb3.toString());
                        iMXCall.hangup("busy");
                    } else {
                        CallsManager.this.mPrevCallState = null;
                        CallsManager.this.mIsStoppedByUser = false;
                        CallsManager.this.mActiveCall = iMXCall;
                        VectorHomeActivity instance2 = VectorHomeActivity.getInstance();
                        if (instance2 == null) {
                            Log.m209d(CallsManager.LOG_TAG, "onIncomingCall : the home activity does not exist -> launch it");
                            Intent intent = new Intent(instance, VectorHomeActivity.class);
                            intent.setFlags(872415232);
                            intent.putExtra(VectorHomeActivity.EXTRA_CALL_SESSION_ID, CallsManager.this.mActiveCall.getSession().getMyUserId());
                            intent.putExtra(VectorHomeActivity.EXTRA_CALL_ID, CallsManager.this.mActiveCall.getCallId());
                            if (mXUsersDevicesMap != null) {
                                intent.putExtra(VectorHomeActivity.EXTRA_CALL_UNKNOWN_DEVICES, mXUsersDevicesMap);
                            }
                            instance.startActivity(intent);
                        } else {
                            Log.m209d(CallsManager.LOG_TAG, "onIncomingCall : the home activity exists : but permissions have to be checked before");
                            instance2.startCall(CallsManager.this.mActiveCall.getSession().getMyUserId(), CallsManager.this.mActiveCall.getCallId(), mXUsersDevicesMap);
                        }
                        CallsManager.this.startRinging();
                        CallsManager.this.mActiveCall.addListener(CallsManager.this.mCallListener);
                    }
                }
            });
        }

        public void onOutgoingCall(final IMXCall iMXCall) {
            String access$300 = CallsManager.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onOutgoingCall () :");
            sb.append(iMXCall.getCallId());
            Log.m209d(access$300, sb.toString());
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    CallsManager.this.mPrevCallState = null;
                    CallsManager.this.mIsStoppedByUser = false;
                    CallsManager.this.mActiveCall = iMXCall;
                    CallsManager.this.mActiveCall.addListener(CallsManager.this.mCallListener);
                    CallsManager.this.startRingBackSound();
                }
            });
        }

        public void onCallHangUp(IMXCall iMXCall) {
            String access$300 = CallsManager.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onCallHangUp ");
            sb.append(iMXCall.getCallId());
            Log.m209d(access$300, sb.toString());
            CallsManager.this.mUiHandler.post(new Runnable() {
                public void run() {
                    if (CallsManager.this.mActiveCall == null) {
                        Log.m209d(CallsManager.LOG_TAG, "## onCallEnd() : no more active call");
                    } else {
                        CallsManager.this.endCall(false);
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsStoppedByUser;
    private VideoLayoutConfiguration mLocalVideoLayoutConfig = null;
    private final OnHeadsetStatusUpdateListener mOnHeadsetStatusUpdateListener = new OnHeadsetStatusUpdateListener() {
        private void onHeadsetUpdate(boolean z) {
            if (CallsManager.this.mActiveCall != null) {
                boolean isHeadsetPlugged = HeadsetConnectionReceiver.isHeadsetPlugged(CallsManager.this.mContext);
                if (CallsManager.this.mCallSoundsManager.isSpeakerphoneOn() && isHeadsetPlugged) {
                    Log.m209d(CallsManager.LOG_TAG, "toggle the call speaker because the call was on loudspeaker.");
                    CallsManager.this.mCallSoundsManager.toggleSpeaker();
                } else if (!isHeadsetPlugged && CallsManager.this.mActiveCall.isVideo()) {
                    Log.m209d(CallsManager.LOG_TAG, "toggle the call speaker because the headset was unplugged during a video call.");
                    CallsManager.this.mCallSoundsManager.toggleSpeaker();
                } else if (z) {
                    AudioManager audioManager = (AudioManager) CallsManager.this.mContext.getSystemService("audio");
                    if (HeadsetConnectionReceiver.isBTHeadsetPlugged()) {
                        audioManager.startBluetoothSco();
                        audioManager.setBluetoothScoOn(true);
                    } else if (audioManager.isBluetoothScoOn()) {
                        audioManager.stopBluetoothSco();
                        audioManager.setBluetoothScoOn(false);
                    }
                }
                if (CallsManager.this.mCallActivity instanceof VectorCallViewActivity) {
                    ((VectorCallViewActivity) CallsManager.this.mCallActivity).refreshSpeakerButton();
                }
            }
        }

        public void onWiredHeadsetUpdate(boolean z) {
            onHeadsetUpdate(false);
        }

        public void onBluetoothHeadsetUpdate(boolean z) {
            onHeadsetUpdate(true);
        }
    };
    /* access modifiers changed from: private */
    public String mPrevCallState;
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper());

    public CallsManager(Context context) {
        this.mContext = context.getApplicationContext();
        CallSoundsManager callSoundsManager = this.mCallSoundsManager;
        this.mCallSoundsManager = CallSoundsManager.getSharedInstance(this.mContext);
        HeadsetConnectionReceiver.getSharedInstance(this.mContext).addListener(this.mOnHeadsetStatusUpdateListener);
    }

    public static CallsManager getSharedInstance() {
        if (mSharedInstance == null) {
            mSharedInstance = new CallsManager(VectorApp.getInstance());
        }
        return mSharedInstance;
    }

    public IMXCall getActiveCall() {
        if (this.mActiveCall == null || !TextUtils.equals(this.mActiveCall.getCallState(), IMXCall.CALL_STATE_ENDED)) {
            return this.mActiveCall;
        }
        return null;
    }

    public void setCallView(View view) {
        this.mCallView = view;
    }

    public View getCallView() {
        return this.mCallView;
    }

    public void setVideoLayoutConfiguration(VideoLayoutConfiguration videoLayoutConfiguration) {
        this.mLocalVideoLayoutConfig = videoLayoutConfiguration;
    }

    public VideoLayoutConfiguration getVideoLayoutConfiguration() {
        return this.mLocalVideoLayoutConfig;
    }

    public void setCallActivity(Activity activity) {
        this.mCallActivity = activity;
    }

    /* access modifiers changed from: private */
    public void showToast(final String str) {
        this.mUiHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(CallsManager.this.mContext, str, 1).show();
            }
        });
    }

    public void addSession(MXSession mXSession) {
        mXSession.getDataHandler().getCallsManager().addListener(this.mCallsManagerListener);
    }

    public void removeSession(MXSession mXSession) {
        mXSession.getDataHandler().getCallsManager().removeListener(this.mCallsManagerListener);
    }

    public void checkDeadCalls() {
        boolean z = false;
        for (MXSession dataHandler : Matrix.getMXSessions(this.mContext)) {
            z |= dataHandler.getDataHandler().getCallsManager().hasActiveCalls();
        }
        if (this.mActiveCall != null && !z) {
            Log.m211e(LOG_TAG, "## checkDeadCalls() : fix an infinite ringing");
            if (EventStreamService.getInstance() != null) {
                EventStreamService.getInstance().hideCallNotifications();
            }
            releaseCall();
        }
    }

    public void rejectCall() {
        if (this.mActiveCall != null) {
            this.mActiveCall.hangup("Reject");
            releaseCall();
        }
    }

    public void toggleSpeaker() {
        if (this.mActiveCall != null) {
            this.mCallSoundsManager.toggleSpeaker();
        } else {
            Log.m217w(LOG_TAG, "## toggleSpeaker(): no active call");
        }
    }

    /* access modifiers changed from: private */
    public void setCallSpeakerphoneOn(boolean z) {
        if (this.mActiveCall != null) {
            this.mCallSoundsManager.setCallSpeakerphoneOn(z);
        } else {
            Log.m217w(LOG_TAG, "## toggleSpeaker(): no active call");
        }
    }

    public void onHangUp(String str) {
        if (this.mActiveCall != null) {
            this.mIsStoppedByUser = true;
            this.mActiveCall.hangup(str);
            endCall(false);
        }
    }

    /* access modifiers changed from: private */
    public void startRinging() {
        requestAudioFocus();
        this.mCallSoundsManager.startRinging(C1299R.raw.ring, RING_TONE_START_RINGING);
    }

    public boolean isRinging() {
        return this.mCallSoundsManager.isRinging();
    }

    public boolean isSpeakerphoneOn() {
        return this.mCallSoundsManager.isSpeakerphoneOn();
    }

    /* access modifiers changed from: private */
    public void requestAudioFocus() {
        this.mCallSoundsManager.requestAudioFocus();
    }

    /* access modifiers changed from: private */
    public void startRingBackSound() {
        this.mCallSoundsManager.startSound(C1299R.raw.ringback, true, new OnMediaListener() {
            public void onMediaCompleted() {
            }

            public void onMediaPlay() {
            }

            public void onMediaReadyToPlay() {
                if (CallsManager.this.mActiveCall != null) {
                    CallsManager.this.requestAudioFocus();
                    CallsManager.this.mCallSoundsManager.setSpeakerphoneOn(true, CallsManager.this.mActiveCall.isVideo() && !HeadsetConnectionReceiver.isHeadsetPlugged(CallsManager.this.mContext));
                    return;
                }
                Log.m211e(CallsManager.LOG_TAG, "## startSound() : null mActiveCall");
            }
        });
    }

    /* access modifiers changed from: private */
    public void endCall(boolean z) {
        if (this.mActiveCall != null) {
            final IMXCall iMXCall = this.mActiveCall;
            this.mActiveCall = null;
            if (this.mCallSoundsManager.isRinging()) {
                releaseCall(iMXCall);
            } else {
                this.mCallSoundsManager.startSound(z ? C1299R.raw.busy : C1299R.raw.callend, false, new OnMediaListener() {
                    public void onMediaPlay() {
                    }

                    public void onMediaReadyToPlay() {
                        if (CallsManager.this.mCallActivity != null) {
                            CallsManager.this.mCallActivity.finish();
                            CallsManager.this.mCallActivity = null;
                        }
                    }

                    public void onMediaCompleted() {
                        CallsManager.this.releaseCall(iMXCall);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void releaseCall() {
        if (this.mActiveCall != null) {
            releaseCall(this.mActiveCall);
            this.mActiveCall = null;
        }
    }

    /* access modifiers changed from: private */
    public void releaseCall(IMXCall iMXCall) {
        if (iMXCall != null) {
            iMXCall.removeListener(this.mCallListener);
            this.mCallSoundsManager.stopSounds();
            this.mCallSoundsManager.releaseAudioFocus();
            if (this.mCallActivity != null) {
                this.mCallActivity.finish();
                this.mCallActivity = null;
            }
            this.mCallView = null;
            this.mLocalVideoLayoutConfig = null;
            EventStreamService.getInstance().hideCallNotifications();
        }
    }
}
