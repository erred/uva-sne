package com.opengarden.firechat.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.call.IMXCallListener;
import com.opengarden.firechat.matrixsdk.call.MXCallListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.util.CallUtilities;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.VectorUtils;

public class VectorPendingCallView extends RelativeLayout {
    /* access modifiers changed from: private */
    public IMXCall mCall;
    private TextView mCallDescriptionTextView;
    private final IMXCallListener mCallListener = new MXCallListener() {
        public void onPreviewSizeChanged(int i, int i2) {
        }

        public void onStateDidChange(String str) {
            VectorPendingCallView.this.refresh();
        }

        public void onCallError(String str) {
            VectorPendingCallView.this.refresh();
        }

        public void onCallViewCreated(View view) {
            VectorPendingCallView.this.refresh();
        }

        public void onCallAnsweredElsewhere() {
            VectorPendingCallView.this.onCallTerminated();
        }

        public void onCallEnd(int i) {
            VectorPendingCallView.this.onCallTerminated();
        }
    };
    private TextView mCallStatusTextView;
    private boolean mIsCallStatusHidden;
    private View mMainView;
    /* access modifiers changed from: private */
    public Handler mUIHandler;

    public VectorPendingCallView(Context context) {
        super(context);
        initView();
    }

    public VectorPendingCallView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public VectorPendingCallView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), C1299R.layout.vector_pending_call_view, this);
        this.mMainView = findViewById(C1299R.C1301id.main_view);
        this.mCallDescriptionTextView = (TextView) findViewById(C1299R.C1301id.pending_call_room_name_textview);
        this.mCallDescriptionTextView.setVisibility(8);
        this.mCallStatusTextView = (TextView) findViewById(C1299R.C1301id.pending_call_status_textview);
        this.mCallStatusTextView.setVisibility(8);
        this.mUIHandler = new Handler(Looper.getMainLooper());
    }

    public void checkPendingCall() {
        IMXCall activeCall = CallsManager.getSharedInstance().getActiveCall();
        if (activeCall == null) {
            if (this.mCall != null) {
                this.mCall.removeListener(this.mCallListener);
            }
            this.mCall = null;
            setVisibility(8);
            return;
        }
        if (this.mCall != activeCall) {
            if (this.mCall != null) {
                this.mCall.removeListener(this.mCallListener);
            }
            this.mCall = activeCall;
            activeCall.addListener(this.mCallListener);
            setVisibility(0);
        }
        refresh();
    }

    /* access modifiers changed from: private */
    public void refresh() {
        this.mUIHandler.post(new Runnable() {
            public void run() {
                if (VectorPendingCallView.this.mCall != null) {
                    VectorPendingCallView.this.refreshCallDescription();
                    VectorPendingCallView.this.refreshCallStatus();
                    VectorPendingCallView.this.mUIHandler.postDelayed(new Runnable() {
                        public void run() {
                            VectorPendingCallView.this.refresh();
                        }
                    }, 1000);
                }
            }
        });
    }

    public void onCallTerminated() {
        checkPendingCall();
    }

    /* access modifiers changed from: private */
    public void refreshCallDescription() {
        String str;
        if (this.mCall != null) {
            this.mCallDescriptionTextView.setVisibility(0);
            Room room = this.mCall.getRoom();
            if (room != null) {
                str = VectorUtils.getCallingRoomDisplayName(getContext(), this.mCall.getSession(), room);
            } else {
                str = this.mCall.getCallId();
            }
            if (TextUtils.equals(this.mCall.getCallState(), IMXCall.CALL_STATE_CONNECTED) && !this.mIsCallStatusHidden) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(" - ");
                sb.append(getResources().getString(C1299R.string.active_call));
                str = sb.toString();
            }
            this.mCallDescriptionTextView.setText(str);
            return;
        }
        this.mCallDescriptionTextView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void refreshCallStatus() {
        String callStatus = CallUtilities.getCallStatus(getContext(), this.mCall);
        this.mCallStatusTextView.setText(callStatus);
        this.mCallStatusTextView.setVisibility(TextUtils.isEmpty(callStatus) ? 8 : 0);
    }

    public void enableCallStatusDisplay(boolean z) {
        this.mIsCallStatusHidden = !z;
    }

    public void updateBackgroundColor(int i) {
        this.mMainView.setBackgroundColor(i);
    }
}
