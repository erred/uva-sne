package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class JitsiCallActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private JitsiCallActivity target;

    @UiThread
    public JitsiCallActivity_ViewBinding(JitsiCallActivity jitsiCallActivity) {
        this(jitsiCallActivity, jitsiCallActivity.getWindow().getDecorView());
    }

    @UiThread
    public JitsiCallActivity_ViewBinding(JitsiCallActivity jitsiCallActivity, View view) {
        super(jitsiCallActivity, view);
        this.target = jitsiCallActivity;
        jitsiCallActivity.mBackToAppIcon = C0487Utils.findRequiredView(view, C1299R.C1301id.jsti_back_to_app_icon, "field 'mBackToAppIcon'");
        jitsiCallActivity.mCloseWidgetIcon = C0487Utils.findRequiredView(view, C1299R.C1301id.jsti_close_widget_icon, "field 'mCloseWidgetIcon'");
        jitsiCallActivity.mConnectingTextView = C0487Utils.findRequiredView(view, C1299R.C1301id.jsti_connecting_text_view, "field 'mConnectingTextView'");
        jitsiCallActivity.waitingView = C0487Utils.findRequiredView(view, C1299R.C1301id.jitsi_progress_layout, "field 'waitingView'");
    }

    public void unbind() {
        JitsiCallActivity jitsiCallActivity = this.target;
        if (jitsiCallActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        jitsiCallActivity.mBackToAppIcon = null;
        jitsiCallActivity.mCloseWidgetIcon = null;
        jitsiCallActivity.mConnectingTextView = null;
        jitsiCallActivity.waitingView = null;
        super.unbind();
    }
}
