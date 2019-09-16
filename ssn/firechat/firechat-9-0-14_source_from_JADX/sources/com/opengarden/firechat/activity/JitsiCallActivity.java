package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import butterknife.BindView;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import com.opengarden.firechat.widgets.WidgetsManager.onWidgetUpdateListener;
import java.util.Map;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

public class JitsiCallActivity extends RiotAppCompatActivity {
    private static final int CAN_DRAW_OVERLAY_REQUEST_CODE = 1234;
    public static final String EXTRA_ENABLE_VIDEO = "EXTRA_ENABLE_VIDEO";
    public static final String EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID";
    private static final String JITSI_SERVER_URL = "https://jitsi.riot.im/";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "JitsiCallActivity";
    @BindView(2131296647)
    View mBackToAppIcon;
    private String mCallUrl;
    @BindView(2131296648)
    View mCloseWidgetIcon;
    @BindView(2131296649)
    View mConnectingTextView;
    private boolean mIsVideoCall;
    private JitsiMeetView mJitsiView = null;
    /* access modifiers changed from: private */
    public Room mRoom;
    /* access modifiers changed from: private */
    public MXSession mSession;
    /* access modifiers changed from: private */
    public Widget mWidget = null;
    private final onWidgetUpdateListener mWidgetListener = new onWidgetUpdateListener() {
        public void onWidgetUpdate(Widget widget) {
            if (TextUtils.equals(widget.getWidgetId(), JitsiCallActivity.this.mWidget.getWidgetId()) && !widget.isActive()) {
                JitsiCallActivity.this.finish();
            }
        }
    };
    @BindView(2131296645)
    View waitingView;

    public boolean displayInFullscreen() {
        return true;
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_jitsi_call;
    }

    @SuppressLint({"NewApi"})
    public void initUiAndData() {
        this.mWidget = (Widget) getIntent().getSerializableExtra("EXTRA_WIDGET_ID");
        this.mIsVideoCall = getIntent().getBooleanExtra(EXTRA_ENABLE_VIDEO, true);
        try {
            String queryParameter = Uri.parse(this.mWidget.getUrl()).getQueryParameter("confId");
            StringBuilder sb = new StringBuilder();
            sb.append(JITSI_SERVER_URL);
            sb.append(queryParameter);
            this.mCallUrl = sb.toString();
            this.mSession = Matrix.getMXSession(this, this.mWidget.getSessionId());
            if (this.mSession == null) {
                Log.m211e(LOG_TAG, "## onCreate() : undefined session ");
                finish();
                return;
            }
            this.mRoom = this.mSession.getDataHandler().getRoom(this.mWidget.getRoomId());
            if (this.mRoom == null) {
                String str = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onCreate() : undefined room ");
                sb2.append(this.mWidget.getRoomId());
                Log.m211e(str, sb2.toString());
                finish();
                return;
            }
            this.mJitsiView = new JitsiMeetView(this);
            refreshStatusBar();
            if (VERSION.SDK_INT < 23) {
                loadURL();
            } else if (!Settings.canDrawOverlays(this)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("package:");
                sb3.append(getPackageName());
                startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(sb3.toString())), CAN_DRAW_OVERLAY_REQUEST_CODE);
            } else {
                loadURL();
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## onCreate() failed : ");
            sb4.append(e.getMessage());
            Log.m211e(str2, sb4.toString());
            finish();
        }
    }

    private void refreshStatusBar() {
        int i = 0;
        boolean z = WidgetsManager.getSharedInstance().checkWidgetPermission(this.mSession, this.mRoom) == null;
        View view = this.mCloseWidgetIcon;
        if (!z) {
            i = 8;
        }
        view.setVisibility(i);
        this.mCloseWidgetIcon.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                JitsiCallActivity.this.showWaitingView();
                WidgetsManager.getSharedInstance().closeWidget(JitsiCallActivity.this.mSession, JitsiCallActivity.this.mRoom, JitsiCallActivity.this.mWidget.getWidgetId(), new ApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        JitsiCallActivity.this.finish();
                    }

                    private void onError(String str) {
                        JitsiCallActivity.this.hideWaitingView();
                        CommonActivityUtils.displayToast(JitsiCallActivity.this, str);
                    }

                    public void onNetworkError(Exception exc) {
                        onError(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        onError(matrixError.getLocalizedMessage());
                    }

                    public void onUnexpectedError(Exception exc) {
                        onError(exc.getLocalizedMessage());
                    }
                });
            }
        });
        this.mBackToAppIcon.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                JitsiCallActivity.this.finish();
            }
        });
    }

    private void loadURL() {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("startWithVideoMuted", !this.mIsVideoCall);
            Bundle bundle2 = new Bundle();
            bundle2.putBundle("config", bundle);
            bundle2.putString(ImagesContract.URL, this.mCallUrl);
            this.mJitsiView.loadURLObject(bundle2);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## loadURL() failed : ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            finish();
        }
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(C1299R.C1301id.call_layout);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.addRule(13, -1);
        relativeLayout.setVisibility(0);
        relativeLayout.addView(this.mJitsiView, 0, layoutParams);
        this.mJitsiView.setListener(new JitsiMeetViewListener() {
            public void onConferenceFailed(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onConferenceFailed() : ");
                sb.append(map);
                Log.m211e(access$300, sb.toString());
                JitsiCallActivity.this.finish();
            }

            public void onConferenceJoined(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onConferenceJoined() : ");
                sb.append(map);
                Log.m209d(access$300, sb.toString());
                JitsiCallActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        JitsiCallActivity.this.mConnectingTextView.setVisibility(8);
                    }
                });
            }

            public void onConferenceLeft(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onConferenceLeft() : ");
                sb.append(map);
                Log.m209d(access$300, sb.toString());
                JitsiCallActivity.this.finish();
            }

            public void onConferenceWillJoin(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onConferenceWillJoin() : ");
                sb.append(map);
                Log.m209d(access$300, sb.toString());
                JitsiCallActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        JitsiCallActivity.this.hideWaitingView();
                    }
                });
            }

            public void onConferenceWillLeave(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onConferenceWillLeave() : ");
                sb.append(map);
                Log.m209d(access$300, sb.toString());
            }

            public void onLoadConfigError(Map<String, Object> map) {
                String access$300 = JitsiCallActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onLoadConfigError() : ");
                sb.append(map);
                Log.m209d(access$300, sb.toString());
            }
        });
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != CAN_DRAW_OVERLAY_REQUEST_CODE) {
            return;
        }
        if (Settings.canDrawOverlays(this)) {
            loadURL();
            return;
        }
        Log.m211e(LOG_TAG, "## onActivityResult() : cannot draw overlay");
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mJitsiView != null) {
            ViewGroup viewGroup = (ViewGroup) this.mJitsiView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(this.mJitsiView);
            }
            this.mJitsiView.dispose();
            this.mJitsiView = null;
        }
        JitsiMeetView.onHostDestroy(this);
    }

    public void onNewIntent(Intent intent) {
        JitsiMeetView.onNewIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        JitsiMeetView.onHostPause(this);
        WidgetsManager.removeListener(this.mWidgetListener);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        JitsiMeetView.onHostResume(this);
        WidgetsManager.addListener(this.mWidgetListener);
        refreshStatusBar();
    }

    public void onBackPressed() {
        if (!JitsiMeetView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
