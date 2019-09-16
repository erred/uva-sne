package com.opengarden.firechat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.NonNull;
import android.support.p003v7.widget.helper.ItemTouchHelper.Callback;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.CallSoundsManager;
import com.opengarden.firechat.matrixsdk.call.CallSoundsManager.OnAudioConfigurationUpdateListener;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.call.IMXCallListener;
import com.opengarden.firechat.matrixsdk.call.MXCallListener;
import com.opengarden.firechat.matrixsdk.call.VideoLayoutConfiguration;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.VectorPendingCallView;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.altbeacon.beacon.service.RangedBeacon;

public class VectorCallViewActivity extends RiotAppCompatActivity implements SensorEventListener {
    public static final String EXTRA_CALL_ID = "CallViewActivity.EXTRA_CALL_ID";
    private static final String EXTRA_LOCAL_FRAME_LAYOUT = "EXTRA_LOCAL_FRAME_LAYOUT";
    public static final String EXTRA_MATRIX_ID = "CallViewActivity.EXTRA_MATRIX_ID";
    public static final String EXTRA_UNKNOWN_DEVICES = "CallViewActivity.EXTRA_UNKNOWN_DEVICES";
    private static final short FADE_IN_DURATION = 250;
    private static final short FADE_OUT_DURATION = 2000;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorCallViewActivity";
    private static final int PERCENT_LOCAL_USER_VIDEO_SIZE = 25;
    private static final float PROXIMITY_THRESHOLD = 3.0f;
    private static final short VIDEO_FADING_TIMER = 5000;
    private static final float VIDEO_TO_BUTTONS_VERTICAL_SPACE = 0.03076923f;
    /* access modifiers changed from: private */
    public View mAcceptIncomingCallButton;
    private final OnAudioConfigurationUpdateListener mAudioConfigListener = new OnAudioConfigurationUpdateListener() {
        public void onAudioConfigurationUpdate() {
            VectorCallViewActivity.this.refreshSpeakerButton();
            VectorCallViewActivity.this.refreshMuteMicButton();
        }
    };
    private ImageView mAvatarView;
    /* access modifiers changed from: private */
    public View mButtonsContainerView;
    /* access modifiers changed from: private */
    public IMXCall mCall;
    /* access modifiers changed from: private */
    public View mCallView;
    /* access modifiers changed from: private */
    public CallsManager mCallsManager;
    private int mField = 32;
    private ImageView mHangUpImageView;
    private VectorPendingCallView mHeaderPendingCallView;
    private View mIncomingCallTabbar;
    /* access modifiers changed from: private */
    public boolean mIsCustomLocalVideoLayoutConfig;
    private boolean mIsScreenOff = false;
    private final IMXCallListener mListener = new MXCallListener() {
        public void onStateDidChange(final String str) {
            VectorCallViewActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    String access$000 = VectorCallViewActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onStateDidChange(): new state=");
                    sb.append(str);
                    Log.m209d(access$000, sb.toString());
                    VectorCallViewActivity.this.manageSubViews();
                    if (VectorCallViewActivity.this.mCall != null && VectorCallViewActivity.this.mCall.isVideo() && VectorCallViewActivity.this.mCall.getCallState().equals(IMXCall.CALL_STATE_CONNECTED)) {
                        VectorCallViewActivity.this.mCall.updateLocalVideoRendererPosition(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
                    }
                }
            });
        }

        public void onCallViewCreated(View view) {
            Log.m209d(VectorCallViewActivity.LOG_TAG, "## onViewLoading():");
            VectorCallViewActivity.this.mCallView = view;
            VectorCallViewActivity.this.insertCallView();
        }

        public void onReady() {
            VectorCallViewActivity.this.computeVideoUiLayout();
            if (!VectorCallViewActivity.this.mCall.isIncoming()) {
                Log.m209d(VectorCallViewActivity.LOG_TAG, "## onReady(): placeCall()");
                VectorCallViewActivity.this.mCall.placeCall(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
                return;
            }
            Log.m209d(VectorCallViewActivity.LOG_TAG, "## onReady(): launchIncomingCall()");
            VectorCallViewActivity.this.mCall.launchIncomingCall(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
        }

        public void onPreviewSizeChanged(int i, int i2) {
            String access$000 = VectorCallViewActivity.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onPreviewSizeChanged : ");
            sb.append(i);
            sb.append(" * ");
            sb.append(i2);
            Log.m209d(access$000, sb.toString());
            VectorCallViewActivity.this.mSourceVideoWidth = i;
            VectorCallViewActivity.this.mSourceVideoHeight = i2;
            if (VectorCallViewActivity.this.mCall != null && VectorCallViewActivity.this.mCall.isVideo() && VectorCallViewActivity.this.mCall.getCallState().equals(IMXCall.CALL_STATE_CONNECTED)) {
                VectorCallViewActivity.this.computeVideoUiLayout();
                VectorCallViewActivity.this.mCall.updateLocalVideoRendererPosition(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
            }
        }
    };
    /* access modifiers changed from: private */
    public VideoLayoutConfiguration mLocalVideoLayoutConfig;
    private final OnTouchListener mMainViewTouchListener = new OnTouchListener() {
        private Rect mPreviewRect = null;
        private int mStartX = 0;
        private int mStartY = 0;

        private Rect computePreviewRect() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            VectorCallViewActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = displayMetrics.heightPixels;
            int i2 = displayMetrics.widthPixels;
            return new Rect((VectorCallViewActivity.this.mLocalVideoLayoutConfig.f127mX * i2) / 100, (VectorCallViewActivity.this.mLocalVideoLayoutConfig.f128mY * i) / 100, ((VectorCallViewActivity.this.mLocalVideoLayoutConfig.f127mX + VectorCallViewActivity.this.mLocalVideoLayoutConfig.mWidth) * i2) / 100, ((VectorCallViewActivity.this.mLocalVideoLayoutConfig.f128mY + VectorCallViewActivity.this.mLocalVideoLayoutConfig.mHeight) * i) / 100);
        }

        private void updatePreviewFrame(int i, int i2) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            VectorCallViewActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i3 = displayMetrics.heightPixels;
            int i4 = displayMetrics.widthPixels;
            int width = this.mPreviewRect.width();
            int height = this.mPreviewRect.height();
            this.mPreviewRect.left = Math.max(0, this.mPreviewRect.left + i);
            this.mPreviewRect.right = this.mPreviewRect.left + width;
            this.mPreviewRect.top = Math.max(0, this.mPreviewRect.top + i2);
            this.mPreviewRect.bottom = this.mPreviewRect.top + height;
            if (this.mPreviewRect.right > i4) {
                this.mPreviewRect.right = i4;
                this.mPreviewRect.left = this.mPreviewRect.right - width;
            }
            if (this.mPreviewRect.bottom > i3) {
                this.mPreviewRect.bottom = i3;
                this.mPreviewRect.top = i3 - height;
            }
            VectorCallViewActivity.this.mLocalVideoLayoutConfig.f127mX = (this.mPreviewRect.left * 100) / i4;
            VectorCallViewActivity.this.mLocalVideoLayoutConfig.f128mY = (this.mPreviewRect.top * 100) / i3;
            VectorCallViewActivity.this.mLocalVideoLayoutConfig.mDisplayWidth = i4;
            VectorCallViewActivity.this.mLocalVideoLayoutConfig.mDisplayHeight = i3;
            VectorCallViewActivity.this.mIsCustomLocalVideoLayoutConfig = true;
            VectorCallViewActivity.this.mCall.updateLocalVideoRendererPosition(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (VectorCallViewActivity.this.mCall != null && VectorCallViewActivity.this.mCall.isVideo() && TextUtils.equals(IMXCall.CALL_STATE_CONNECTED, VectorCallViewActivity.this.mCall.getCallState())) {
                int action = motionEvent.getAction();
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (action == 0) {
                    Rect computePreviewRect = computePreviewRect();
                    if (computePreviewRect.contains(x, y)) {
                        this.mPreviewRect = computePreviewRect;
                        this.mStartX = x;
                        this.mStartY = y;
                        return true;
                    }
                } else if (this.mPreviewRect == null || action != 2) {
                    this.mPreviewRect = null;
                } else {
                    updatePreviewFrame(x - this.mStartX, y - this.mStartY);
                    this.mStartX = x;
                    this.mStartY = y;
                    return true;
                }
            }
            return false;
        }
    };
    private String mMatrixId = null;
    private ImageView mMuteLocalCameraView;
    private ImageView mMuteMicImageView;
    private int mPermissionCode;
    private Sensor mProximitySensor;
    private SensorManager mSensorMgr;
    /* access modifiers changed from: private */
    public MXSession mSession = null;
    /* access modifiers changed from: private */
    public int mSourceVideoHeight = 0;
    /* access modifiers changed from: private */
    public int mSourceVideoWidth = 0;
    private ImageView mSpeakerSelectionView;
    private ImageView mSwitchRearFrontCameraImageView;
    private Timer mVideoFadingEdgesTimer;
    private TimerTask mVideoFadingEdgesTimerTask;
    private WakeLock mWakeLock;

    public int getLayoutRes() {
        return C1299R.layout.activity_callview;
    }

    /* access modifiers changed from: private */
    public void insertCallView() {
        if (this.mCallView != null) {
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(C1299R.C1301id.call_layout);
            LayoutParams layoutParams = new LayoutParams(-1, -1);
            layoutParams.addRule(13, -1);
            relativeLayout.removeView(this.mCallView);
            relativeLayout.setVisibility(0);
            if (this.mCall.isVideo()) {
                if (this.mCallView.getParent() != null) {
                    ((ViewGroup) this.mCallView.getParent()).removeView(this.mCallView);
                }
                relativeLayout.addView(this.mCallView, 1, layoutParams);
            }
            this.mCall.setVisibility(8);
        }
    }

    public void initUiAndData() {
        final Intent intent = getIntent();
        if (intent == null) {
            Log.m211e(LOG_TAG, "Need an intent to view.");
            finish();
        } else if (!intent.hasExtra(EXTRA_MATRIX_ID)) {
            Log.m211e(LOG_TAG, "No matrix ID extra.");
            finish();
        } else {
            String stringExtra = intent.getStringExtra(EXTRA_CALL_ID);
            this.mMatrixId = intent.getStringExtra(EXTRA_MATRIX_ID);
            this.mSession = Matrix.getInstance(getApplicationContext()).getSession(this.mMatrixId);
            if (this.mSession == null || !this.mSession.isAlive()) {
                Log.m211e(LOG_TAG, "invalid session");
                finish();
                return;
            }
            this.mCall = CallsManager.getSharedInstance().getActiveCall();
            if (this.mCall == null || !TextUtils.equals(this.mCall.getCallId(), stringExtra)) {
                Log.m211e(LOG_TAG, "invalid call");
                finish();
                return;
            }
            this.mCallsManager = CallsManager.getSharedInstance();
            this.mHangUpImageView = (ImageView) findViewById(C1299R.C1301id.hang_up_button);
            this.mSpeakerSelectionView = (ImageView) findViewById(C1299R.C1301id.call_speaker_view);
            this.mAvatarView = (ImageView) findViewById(C1299R.C1301id.call_other_member);
            this.mMuteMicImageView = (ImageView) findViewById(C1299R.C1301id.mute_audio);
            this.mHeaderPendingCallView = (VectorPendingCallView) findViewById(C1299R.C1301id.header_pending_callview);
            this.mSwitchRearFrontCameraImageView = (ImageView) findViewById(C1299R.C1301id.call_switch_camera_view);
            this.mMuteLocalCameraView = (ImageView) findViewById(C1299R.C1301id.mute_local_camera);
            this.mButtonsContainerView = findViewById(C1299R.C1301id.call_menu_buttons_layout_container);
            this.mIncomingCallTabbar = findViewById(C1299R.C1301id.incoming_call_menu_buttons_layout_container);
            this.mAcceptIncomingCallButton = findViewById(C1299R.C1301id.accept_incoming_call);
            View findViewById = findViewById(C1299R.C1301id.reject_incoming_call);
            View findViewById2 = findViewById(C1299R.C1301id.call_layout);
            findViewById2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.fadeInVideoEdge();
                    VectorCallViewActivity.this.startVideoFadingEdgesScreenTimer();
                }
            });
            findViewById2.setOnTouchListener(this.mMainViewTouchListener);
            ((ImageView) findViewById(C1299R.C1301id.room_chat_link)).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.finish();
                    VectorCallViewActivity.this.startRoomActivity();
                }
            });
            this.mSwitchRearFrontCameraImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.toggleRearFrontCamera();
                    VectorCallViewActivity.this.refreshSwitchRearFrontCameraButton();
                    VectorCallViewActivity.this.startVideoFadingEdgesScreenTimer();
                }
            });
            this.mMuteLocalCameraView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.toggleVideoMute();
                    VectorCallViewActivity.this.refreshMuteVideoButton();
                    VectorCallViewActivity.this.startVideoFadingEdgesScreenTimer();
                }
            });
            this.mMuteMicImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.toggleMicMute();
                    VectorCallViewActivity.this.startVideoFadingEdgesScreenTimer();
                }
            });
            this.mHangUpImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.mCallsManager.onHangUp(CallsManager.HANGUP_MSG_USER_CANCEL);
                }
            });
            this.mSpeakerSelectionView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.toggleSpeaker();
                    VectorCallViewActivity.this.startVideoFadingEdgesScreenTimer();
                }
            });
            if (!isFirstCreation()) {
                this.mLocalVideoLayoutConfig = (VideoLayoutConfiguration) getSavedInstanceState().getSerializable(EXTRA_LOCAL_FRAME_LAYOUT);
                if (this.mLocalVideoLayoutConfig != null) {
                    if (this.mLocalVideoLayoutConfig.mIsPortrait != (2 != getResources().getConfiguration().orientation)) {
                        this.mLocalVideoLayoutConfig = null;
                    }
                }
                this.mIsCustomLocalVideoLayoutConfig = this.mLocalVideoLayoutConfig != null;
            }
            manageSubViews();
            if (this.mCallsManager.getCallView() == null || this.mCallsManager.getCallView().getParent() != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (VectorCallViewActivity.this.mCall.getCallView() != null) {
                            VectorCallViewActivity.this.mCallView = VectorCallViewActivity.this.mCall.getCallView();
                            VectorCallViewActivity.this.insertCallView();
                            VectorCallViewActivity.this.computeVideoUiLayout();
                            VectorCallViewActivity.this.mCall.updateLocalVideoRendererPosition(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
                            if (TextUtils.equals(VectorCallViewActivity.this.mCall.getCallState(), IMXCall.CALL_STATE_READY) && VectorCallViewActivity.this.mCall.isIncoming()) {
                                VectorCallViewActivity.this.mCall.launchIncomingCall(VectorCallViewActivity.this.mLocalVideoLayoutConfig);
                            }
                        } else if (!VectorCallViewActivity.this.mCall.isIncoming() && TextUtils.equals(IMXCall.CALL_STATE_CREATED, VectorCallViewActivity.this.mCall.getCallState())) {
                            VectorCallViewActivity.this.mCall.createCallView();
                        }
                    }
                });
            } else {
                this.mCallView = this.mCallsManager.getCallView();
                insertCallView();
                if (this.mCallsManager.getVideoLayoutConfiguration() != null) {
                    if (this.mCallsManager.getVideoLayoutConfiguration().mIsPortrait == (2 != getResources().getConfiguration().orientation)) {
                        this.mLocalVideoLayoutConfig = this.mCallsManager.getVideoLayoutConfiguration();
                        this.mIsCustomLocalVideoLayoutConfig = true;
                    }
                }
            }
            ImageView imageView = (ImageView) findViewById(C1299R.C1301id.call_other_member);
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            int min = Math.min(point.x, point.y) / 2;
            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
            layoutParams.height = min;
            layoutParams.width = min;
            imageView.setLayoutParams(layoutParams);
            VectorUtils.loadCallAvatar(this, this.mSession, imageView, this.mCall.getRoom());
            int i = 8;
            this.mIncomingCallTabbar.setVisibility((!CallsManager.getSharedInstance().isRinging() || !this.mCall.isIncoming()) ? 8 : 0);
            this.mPermissionCode = this.mCall.isVideo() ? 5 : 4;
            View view = this.mAcceptIncomingCallButton;
            if (CommonActivityUtils.checkPermissions(this.mPermissionCode, (Activity) this)) {
                i = 0;
            }
            view.setVisibility(i);
            this.mAcceptIncomingCallButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Log.m209d(VectorCallViewActivity.LOG_TAG, "Accept the incoming call");
                    VectorCallViewActivity.this.mAcceptIncomingCallButton.setVisibility(8);
                    VectorCallViewActivity.this.mCall.createCallView();
                }
            });
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Log.m209d(VectorCallViewActivity.LOG_TAG, "Reject the incoming call");
                    VectorCallViewActivity.this.mCallsManager.rejectCall();
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    CommonActivityUtils.verifyUnknownDevices(VectorCallViewActivity.this.mSession, CommonActivityUtils.getDevicesList((MXUsersDevicesMap) intent.getSerializableExtra(VectorCallViewActivity.EXTRA_UNKNOWN_DEVICES)));
                }
            });
            setupHeaderPendingCallView();
            Log.m209d(LOG_TAG, "## onCreate(): OUT");
        }
    }

    private void setupHeaderPendingCallView() {
        if (this.mHeaderPendingCallView != null) {
            this.mHeaderPendingCallView.findViewById(C1299R.C1301id.main_view).setBackgroundResource(C1299R.C1300drawable.call_header_transparent_bg);
            this.mHeaderPendingCallView.findViewById(C1299R.C1301id.call_icon_container).setVisibility(8);
            View findViewById = this.mHeaderPendingCallView.findViewById(C1299R.C1301id.back_icon);
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorCallViewActivity.this.onBackPressed();
                }
            });
            LinearLayout linearLayout = (LinearLayout) this.mHeaderPendingCallView.findViewById(C1299R.C1301id.call_info_container);
            linearLayout.setHorizontalGravity(1);
            linearLayout.setPadding(0, 0, 0, 0);
            this.mHeaderPendingCallView.enableCallStatusDisplay(false);
        }
    }

    private void initBackLightManagement() {
        if (this.mCall == null) {
            return;
        }
        if (this.mCall.isVideo()) {
            Log.m209d(LOG_TAG, "## initBackLightManagement(): backlight is ON");
            getWindow().addFlags(128);
        } else if (this.mSensorMgr == null && this.mCall != null && TextUtils.equals(this.mCall.getCallState(), IMXCall.CALL_STATE_CONNECTED)) {
            this.mSensorMgr = (SensorManager) getSystemService("sensor");
            Sensor defaultSensor = this.mSensorMgr.getDefaultSensor(8);
            this.mProximitySensor = defaultSensor;
            if (defaultSensor == null) {
                Log.m217w(LOG_TAG, "## initBackLightManagement(): Warning - proximity sensor not supported");
            } else {
                this.mSensorMgr.registerListener(this, this.mProximitySensor, 3);
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }

    public void finish() {
        super.finish();
        stopProximitySensor();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (!this.mIsScreenOff) {
            stopProximitySensor();
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == this.mPermissionCode) {
            int i2 = 8;
            if (5 == i) {
                View view = this.mAcceptIncomingCallButton;
                if (CommonActivityUtils.onPermissionResultVideoIpCall(this, strArr, iArr)) {
                    i2 = 0;
                }
                view.setVisibility(i2);
                return;
            }
            View view2 = this.mAcceptIncomingCallButton;
            if (CommonActivityUtils.onPermissionResultAudioIpCall(this, strArr, iArr)) {
                i2 = 0;
            }
            view2.setVisibility(i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (!this.mIsScreenOff && this.mCall != null) {
            this.mCall.onPause();
        }
        if (this.mCall != null) {
            this.mCall.removeListener(this.mListener);
        }
        saveCallView();
        CallsManager.getSharedInstance().setCallActivity(null);
        CallSoundsManager.getSharedInstance(this).removeAudioConfigurationListener(this.mAudioConfigListener);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mCallsManager.getActiveCall() == null) {
            Log.m209d(LOG_TAG, "## onResume() : the call does not exist anymore");
            finish();
            return;
        }
        this.mHeaderPendingCallView.checkPendingCall();
        computeVideoUiLayout();
        if (this.mCall != null && this.mCall.isVideo() && this.mCall.getCallState().equals(IMXCall.CALL_STATE_CONNECTED)) {
            this.mCall.updateLocalVideoRendererPosition(this.mLocalVideoLayoutConfig);
        }
        if (this.mCall != null) {
            this.mCall.addListener(this.mListener);
            if (!this.mIsScreenOff) {
                this.mCall.onResume();
            }
            this.mIsScreenOff = false;
            String callState = this.mCall.getCallState();
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onResume(): call state=");
            sb.append(callState);
            Log.m209d(str, sb.toString());
            this.mCallView = this.mCallsManager.getCallView();
            insertCallView();
            manageSubViews();
            initBackLightManagement();
            CallsManager.getSharedInstance().setCallActivity(this);
            CallSoundsManager.getSharedInstance(this).addAudioConfigurationListener(this.mAudioConfigListener);
        } else {
            finish();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        computeVideoUiLayout();
        if (this.mCall != null && this.mCall.isVideo() && this.mCall.getCallState().equals(IMXCall.CALL_STATE_CONNECTED)) {
            this.mCall.updateLocalVideoRendererPosition(this.mLocalVideoLayoutConfig);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mIsCustomLocalVideoLayoutConfig) {
            bundle.putSerializable(EXTRA_LOCAL_FRAME_LAYOUT, this.mLocalVideoLayoutConfig);
        }
    }

    /* access modifiers changed from: private */
    public void toggleMicMute() {
        CallSoundsManager sharedInstance = CallSoundsManager.getSharedInstance(this);
        sharedInstance.setMicrophoneMute(!sharedInstance.isMicrophoneMute());
    }

    /* access modifiers changed from: private */
    public void toggleVideoMute() {
        if (this.mCall == null) {
            Log.m217w(LOG_TAG, "## toggleVideoMute(): Failed");
        } else if (this.mCall.isVideo()) {
            boolean isVideoRecordingMuted = this.mCall.isVideoRecordingMuted();
            this.mCall.muteVideoRecording(!isVideoRecordingMuted);
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toggleVideoMute(): camera record turned to ");
            sb.append(!isVideoRecordingMuted);
            Log.m217w(str, sb.toString());
        }
    }

    /* access modifiers changed from: private */
    public void toggleSpeaker() {
        this.mCallsManager.toggleSpeaker();
    }

    /* access modifiers changed from: private */
    public void toggleRearFrontCamera() {
        boolean z;
        if (this.mCall == null || !this.mCall.isVideo()) {
            Log.m217w(LOG_TAG, "## toggleRearFrontCamera(): Skipped");
            z = false;
        } else {
            z = this.mCall.switchRearFrontCamera();
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## toggleRearFrontCamera(): done? ");
        sb.append(z);
        Log.m217w(str, sb.toString());
    }

    /* access modifiers changed from: private */
    public void startRoomActivity() {
        if (this.mCall != null) {
            String roomId = this.mCall.getRoom().getRoomId();
            if (VectorApp.getCurrentActivity() != null) {
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mMatrixId);
                hashMap.put("EXTRA_ROOM_ID", roomId);
                CommonActivityUtils.goToRoomPage(VectorApp.getCurrentActivity(), this.mSession, hashMap);
                return;
            }
            Intent intent = new Intent(getApplicationContext(), VectorRoomActivity.class);
            intent.putExtra("EXTRA_ROOM_ID", roomId);
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mMatrixId);
            startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void stopVideoFadingEdgesScreenTimer() {
        if (this.mVideoFadingEdgesTimer != null) {
            this.mVideoFadingEdgesTimer.cancel();
            this.mVideoFadingEdgesTimer = null;
            this.mVideoFadingEdgesTimerTask = null;
        }
    }

    /* access modifiers changed from: private */
    public void startVideoFadingEdgesScreenTimer() {
        if (this.mCall != null && this.mCall.isVideo()) {
            stopVideoFadingEdgesScreenTimer();
            try {
                this.mVideoFadingEdgesTimer = new Timer();
                this.mVideoFadingEdgesTimerTask = new TimerTask() {
                    public void run() {
                        VectorCallViewActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VectorCallViewActivity.this.stopVideoFadingEdgesScreenTimer();
                                VectorCallViewActivity.this.fadeOutVideoEdge();
                            }
                        });
                    }
                };
                this.mVideoFadingEdgesTimer.schedule(this.mVideoFadingEdgesTimerTask, RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
            } catch (Throwable th) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## startVideoFadingEdgesScreenTimer() ");
                sb.append(th.getMessage());
                Log.m211e(str, sb.toString());
                stopVideoFadingEdgesScreenTimer();
                fadeOutVideoEdge();
            }
        }
    }

    private void fadeVideoEdge(final float f, int i) {
        if (!(this.mHeaderPendingCallView == null || f == this.mHeaderPendingCallView.getAlpha())) {
            this.mHeaderPendingCallView.animate().alpha(f).setDuration((long) i).setInterpolator(new AccelerateInterpolator());
        }
        if (this.mButtonsContainerView != null && f != this.mButtonsContainerView.getAlpha()) {
            this.mButtonsContainerView.animate().alpha(f).setDuration((long) i).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (0.0f == f) {
                        VectorCallViewActivity.this.mButtonsContainerView.setVisibility(8);
                    } else {
                        VectorCallViewActivity.this.mButtonsContainerView.setVisibility(0);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void fadeOutVideoEdge() {
        fadeVideoEdge(0.0f, 2000);
    }

    /* access modifiers changed from: private */
    public void fadeInVideoEdge() {
        fadeVideoEdge(1.0f, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    /* access modifiers changed from: private */
    public void computeVideoUiLayout() {
        if (this.mLocalVideoLayoutConfig == null) {
            this.mLocalVideoLayoutConfig = new VideoLayoutConfiguration();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        int i2 = displayMetrics.widthPixels;
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(16843499, typedValue, true)) {
            i -= TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }
        ViewGroup.LayoutParams layoutParams = findViewById(C1299R.C1301id.hang_up_button).getLayoutParams();
        if (this.mLocalVideoLayoutConfig.mWidth == 0) {
            this.mLocalVideoLayoutConfig.mWidth = 25;
        }
        if (this.mLocalVideoLayoutConfig.mHeight == 0) {
            this.mLocalVideoLayoutConfig.mHeight = 25;
        }
        if (this.mSourceVideoWidth == 0 || this.mSourceVideoHeight == 0) {
            this.mLocalVideoLayoutConfig.mWidth = 25;
            this.mLocalVideoLayoutConfig.mHeight = 25;
        } else {
            int i3 = (this.mSourceVideoWidth * 100) / this.mSourceVideoHeight;
            if (i3 != (((this.mLocalVideoLayoutConfig.mWidth * i2) / 100) * 100) / ((this.mLocalVideoLayoutConfig.mHeight * i) / 100)) {
                int i4 = (i2 * 25) / 100;
                int i5 = ((i * 25) / 100) * i3;
                if (i5 / 100 > i4) {
                    this.mLocalVideoLayoutConfig.mHeight = (((i4 * 100) * 100) / i3) / i;
                    this.mLocalVideoLayoutConfig.mWidth = 25;
                } else {
                    this.mLocalVideoLayoutConfig.mWidth = i5 / i2;
                    this.mLocalVideoLayoutConfig.mHeight = 25;
                }
            }
        }
        boolean z = false;
        if (!this.mIsCustomLocalVideoLayoutConfig) {
            int i6 = this.mButtonsContainerView.getVisibility() == 0 ? (layoutParams.height * 100) / i : 0;
            float f = (float) i;
            int i7 = (int) (((VIDEO_TO_BUTTONS_VERTICAL_SPACE * f) * 100.0f) / f);
            this.mLocalVideoLayoutConfig.f127mX = (i7 * i) / i2;
            this.mLocalVideoLayoutConfig.f128mY = ((100 - i7) - i6) - this.mLocalVideoLayoutConfig.mHeight;
        }
        VideoLayoutConfiguration videoLayoutConfiguration = this.mLocalVideoLayoutConfig;
        if (getResources().getConfiguration().orientation != 2) {
            z = true;
        }
        videoLayoutConfiguration.mIsPortrait = z;
        this.mLocalVideoLayoutConfig.mDisplayWidth = i2;
        this.mLocalVideoLayoutConfig.mDisplayHeight = i;
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## computeVideoUiLayout() : x ");
        sb.append(this.mLocalVideoLayoutConfig.f127mX);
        sb.append(" y ");
        sb.append(this.mLocalVideoLayoutConfig.f128mY);
        Log.m209d(str, sb.toString());
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## computeVideoUiLayout() : mWidth ");
        sb2.append(this.mLocalVideoLayoutConfig.mWidth);
        sb2.append(" mHeight ");
        sb2.append(this.mLocalVideoLayoutConfig.mHeight);
        Log.m209d(str2, sb2.toString());
    }

    /* access modifiers changed from: private */
    public void refreshMuteMicButton() {
        this.mMuteMicImageView.setImageResource(CallSoundsManager.getSharedInstance(this).isMicrophoneMute() ? C1299R.C1300drawable.ic_material_mic_off_pink_red : C1299R.C1300drawable.ic_material_mic_off_grey);
    }

    public void refreshSpeakerButton() {
        this.mSpeakerSelectionView.setImageResource(CallSoundsManager.getSharedInstance(this).isSpeakerphoneOn() ? C1299R.C1300drawable.ic_material_speaker_phone_pink_red : C1299R.C1300drawable.ic_material_speaker_phone_grey);
    }

    /* access modifiers changed from: private */
    public void refreshMuteVideoButton() {
        if (this.mCall == null || !this.mCall.isVideo()) {
            Log.m209d(LOG_TAG, "## refreshMuteVideoButton(): View.INVISIBLE");
            this.mMuteLocalCameraView.setVisibility(4);
            return;
        }
        this.mMuteLocalCameraView.setVisibility(0);
        boolean isVideoRecordingMuted = this.mCall.isVideoRecordingMuted();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## refreshMuteVideoButton(): isMuted=");
        sb.append(isVideoRecordingMuted);
        Log.m209d(str, sb.toString());
        this.mMuteLocalCameraView.setImageResource(isVideoRecordingMuted ? C1299R.C1300drawable.ic_material_videocam_off_pink_red : C1299R.C1300drawable.ic_material_videocam_off_grey);
    }

    /* access modifiers changed from: private */
    public void refreshSwitchRearFrontCameraButton() {
        if (this.mCall == null || !this.mCall.isVideo() || !this.mCall.isSwitchCameraSupported()) {
            Log.m209d(LOG_TAG, "## refreshSwitchRearFrontCameraButton(): View.INVISIBLE");
            this.mSwitchRearFrontCameraImageView.setVisibility(4);
            return;
        }
        this.mSwitchRearFrontCameraImageView.setVisibility(0);
        boolean isCameraSwitched = this.mCall.isCameraSwitched();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## refreshSwitchRearFrontCameraButton(): isSwitched=");
        sb.append(isCameraSwitched);
        Log.m209d(str, sb.toString());
        this.mSwitchRearFrontCameraImageView.setImageResource(isCameraSwitched ? C1299R.C1300drawable.ic_material_switch_video_pink_red : C1299R.C1300drawable.ic_material_switch_video_grey);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0105 A[FALL_THROUGH] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x010c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void manageSubViews() {
        /*
            r8 = this;
            com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r8.mCall
            if (r0 != 0) goto L_0x000c
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "## manageSubViews(): call instance = null, just return"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            return
        L_0x000c:
            com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r8.mCall
            java.lang.String r0 = r0.getCallState()
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## manageSubViews() IN callState : "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
            android.widget.ImageView r1 = r8.mAvatarView
            java.lang.String r2 = "IMXCall.CALL_STATE_CONNECTED"
            boolean r2 = r0.equals(r2)
            r3 = 8
            r4 = 0
            if (r2 == 0) goto L_0x0040
            com.opengarden.firechat.matrixsdk.call.IMXCall r2 = r8.mCall
            boolean r2 = r2.isVideo()
            if (r2 == 0) goto L_0x0040
            r2 = 8
            goto L_0x0041
        L_0x0040:
            r2 = 0
        L_0x0041:
            r1.setVisibility(r2)
            r8.refreshSpeakerButton()
            r8.refreshMuteMicButton()
            r8.refreshMuteVideoButton()
            r8.refreshSwitchRearFrontCameraButton()
            int r1 = r0.hashCode()
            r2 = 1322015527(0x4ecc5b27, float:1.71426291E9)
            r5 = 1
            r6 = 1831632118(0x6d2c7cf6, float:3.3364056E27)
            r7 = -1
            if (r1 == r2) goto L_0x006b
            if (r1 == r6) goto L_0x0061
            goto L_0x0075
        L_0x0061:
            java.lang.String r1 = "IMXCall.CALL_STATE_CONNECTED"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0075
            r1 = 1
            goto L_0x0076
        L_0x006b:
            java.lang.String r1 = "IMXCall.CALL_STATE_ENDED"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0075
            r1 = 0
            goto L_0x0076
        L_0x0075:
            r1 = -1
        L_0x0076:
            r2 = 4
            switch(r1) {
                case 0: goto L_0x0089;
                case 1: goto L_0x0080;
                default: goto L_0x007a;
            }
        L_0x007a:
            android.widget.ImageView r1 = r8.mHangUpImageView
            r1.setVisibility(r4)
            goto L_0x008e
        L_0x0080:
            r8.initBackLightManagement()
            android.widget.ImageView r1 = r8.mHangUpImageView
            r1.setVisibility(r4)
            goto L_0x008e
        L_0x0089:
            android.widget.ImageView r1 = r8.mHangUpImageView
            r1.setVisibility(r2)
        L_0x008e:
            com.opengarden.firechat.matrixsdk.call.IMXCall r1 = r8.mCall
            boolean r1 = r1.isVideo()
            if (r1 == 0) goto L_0x00b1
            int r1 = r0.hashCode()
            if (r1 == r6) goto L_0x009d
            goto L_0x00a7
        L_0x009d:
            java.lang.String r1 = "IMXCall.CALL_STATE_CONNECTED"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x00a7
            r1 = 0
            goto L_0x00a8
        L_0x00a7:
            r1 = -1
        L_0x00a8:
            if (r1 == 0) goto L_0x00ae
            r8.stopVideoFadingEdgesScreenTimer()
            goto L_0x00b1
        L_0x00ae:
            r8.startVideoFadingEdgesScreenTimer()
        L_0x00b1:
            com.opengarden.firechat.matrixsdk.call.IMXCall r1 = r8.mCall
            boolean r1 = r1.isVideo()
            if (r1 == 0) goto L_0x011e
            java.lang.String r1 = "IMXCall.CALL_STATE_ENDED"
            boolean r1 = r0.equals(r1)
            if (r1 != 0) goto L_0x011e
            int r1 = r0.hashCode()
            switch(r1) {
                case -215535408: goto L_0x00fb;
                case 183694318: goto L_0x00f1;
                case 358221275: goto L_0x00e7;
                case 946025035: goto L_0x00dd;
                case 1831632118: goto L_0x00d3;
                case 1960371423: goto L_0x00c9;
                default: goto L_0x00c8;
            }
        L_0x00c8:
            goto L_0x0105
        L_0x00c9:
            java.lang.String r1 = "IMXCall.CALL_STATE_RINGING"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 2
            goto L_0x0106
        L_0x00d3:
            java.lang.String r1 = "IMXCall.CALL_STATE_CONNECTED"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 5
            goto L_0x0106
        L_0x00dd:
            java.lang.String r1 = "IMXCall.CALL_STATE_CONNECTING"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 4
            goto L_0x0106
        L_0x00e7:
            java.lang.String r1 = "IMXCall.CALL_STATE_INVITE_SENT"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 1
            goto L_0x0106
        L_0x00f1:
            java.lang.String r1 = "IMXCall.CALL_STATE_CREATE_ANSWER"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 3
            goto L_0x0106
        L_0x00fb:
            java.lang.String r1 = "IMXCall.CALL_STATE_WAIT_CREATE_OFFER"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0105
            r1 = 0
            goto L_0x0106
        L_0x0105:
            r1 = -1
        L_0x0106:
            switch(r1) {
                case 0: goto L_0x010c;
                case 1: goto L_0x010c;
                case 2: goto L_0x010c;
                case 3: goto L_0x010c;
                case 4: goto L_0x010c;
                case 5: goto L_0x010c;
                default: goto L_0x0109;
            }
        L_0x0109:
            r1 = 8
            goto L_0x010d
        L_0x010c:
            r1 = 0
        L_0x010d:
            com.opengarden.firechat.matrixsdk.call.IMXCall r2 = r8.mCall
            if (r2 == 0) goto L_0x011e
            com.opengarden.firechat.matrixsdk.call.IMXCall r2 = r8.mCall
            int r2 = r2.getVisibility()
            if (r1 == r2) goto L_0x011e
            com.opengarden.firechat.matrixsdk.call.IMXCall r2 = r8.mCall
            r2.setVisibility(r1)
        L_0x011e:
            int r1 = r0.hashCode()
            r2 = 1960371423(0x74d8e4df, float:1.3747292E32)
            if (r1 == r2) goto L_0x0128
            goto L_0x0131
        L_0x0128:
            java.lang.String r1 = "IMXCall.CALL_STATE_RINGING"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0131
            goto L_0x0132
        L_0x0131:
            r4 = -1
        L_0x0132:
            if (r4 == 0) goto L_0x0135
            goto L_0x0147
        L_0x0135:
            com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r8.mCall
            boolean r0 = r0.isIncoming()
            if (r0 == 0) goto L_0x0147
            com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r8.mCall
            r0.answer()
            android.view.View r0 = r8.mIncomingCallTabbar
            r0.setVisibility(r3)
        L_0x0147:
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "## manageSubViews(): OUT"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorCallViewActivity.manageSubViews():void");
    }

    private void saveCallView() {
        if (this.mCall != null && !this.mCall.getCallState().equals(IMXCall.CALL_STATE_ENDED) && this.mCallView != null && this.mCallView.getParent() != null) {
            this.mCall.onPause();
            ((ViewGroup) this.mCallView.getParent()).removeView(this.mCallView);
            this.mCallsManager.setCallView(this.mCallView);
            this.mCallsManager.setVideoLayoutConfiguration(this.mLocalVideoLayoutConfig);
            ((RelativeLayout) findViewById(C1299R.C1301id.call_layout)).setVisibility(8);
            this.mCallView = null;
        }
    }

    private void initScreenManagement() {
        try {
            this.mField = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable th) {
            try {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initScreenManagement ");
                sb.append(th.getMessage());
                Log.m211e(str, sb.toString());
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## initScreenManagement() : failed ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
                return;
            }
        }
        this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(this.mField, getLocalClassName());
    }

    private void turnScreenOff() {
        if (this.mWakeLock == null) {
            initScreenManagement();
        }
        try {
            if (this.mWakeLock != null && !this.mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
                this.mIsScreenOff = true;
            }
        } catch (Exception unused) {
            Log.m211e(LOG_TAG, "## turnScreenOff() failed");
        }
        if (getWindow() != null && getWindow().getAttributes() != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.screenBrightness = 0.0f;
            getWindow().setAttributes(attributes);
        }
    }

    private void turnScreenOn() {
        try {
            if (this.mWakeLock != null) {
                this.mWakeLock.release();
            }
        } catch (Exception unused) {
            Log.m211e(LOG_TAG, "## turnScreenOn() failed");
        }
        this.mIsScreenOff = false;
        this.mWakeLock = null;
        if (getWindow() != null && getWindow().getAttributes() != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.screenBrightness = -1.0f;
            getWindow().setAttributes(attributes);
        }
    }

    private void stopProximitySensor() {
        if (!(this.mProximitySensor == null || this.mSensorMgr == null)) {
            this.mSensorMgr.unregisterListener(this);
            this.mProximitySensor = null;
            this.mSensorMgr = null;
        }
        turnScreenOn();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent != null) {
            float f = sensorEvent.values[0];
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onSensorChanged(): ");
            sb.append(String.format(VectorApp.getApplicationLocale(), "distance=%.3f", new Object[]{Float.valueOf(f)}));
            Log.m209d(str, sb.toString());
            if (CallsManager.getSharedInstance().isSpeakerphoneOn()) {
                Log.m209d(LOG_TAG, "## onSensorChanged(): Skipped due speaker ON");
            } else if (f <= PROXIMITY_THRESHOLD) {
                turnScreenOff();
                Log.m209d(LOG_TAG, "## onSensorChanged(): force screen OFF");
            } else {
                turnScreenOn();
                Log.m209d(LOG_TAG, "## onSensorChanged(): force screen ON");
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onAccuracyChanged(): accuracy=");
        sb.append(i);
        Log.m209d(str, sb.toString());
    }
}
