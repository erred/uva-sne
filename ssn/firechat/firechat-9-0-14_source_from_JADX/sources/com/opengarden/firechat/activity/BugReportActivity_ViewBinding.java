package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class BugReportActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private BugReportActivity target;

    @UiThread
    public BugReportActivity_ViewBinding(BugReportActivity bugReportActivity) {
        this(bugReportActivity, bugReportActivity.getWindow().getDecorView());
    }

    @UiThread
    public BugReportActivity_ViewBinding(BugReportActivity bugReportActivity, View view) {
        super(bugReportActivity, view);
        this.target = bugReportActivity;
        bugReportActivity.mBugReportText = (EditText) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_edit_text, "field 'mBugReportText'", EditText.class);
        bugReportActivity.mIncludeLogsButton = (CheckBox) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_button_include_logs, "field 'mIncludeLogsButton'", CheckBox.class);
        bugReportActivity.mIncludeCrashLogsButton = (CheckBox) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_button_include_crash_logs, "field 'mIncludeCrashLogsButton'", CheckBox.class);
        bugReportActivity.mIncludeScreenShotButton = (CheckBox) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_button_include_screenshot, "field 'mIncludeScreenShotButton'", CheckBox.class);
        bugReportActivity.mProgressBar = (ProgressBar) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_progress_view, "field 'mProgressBar'", ProgressBar.class);
        bugReportActivity.mProgressTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bug_report_progress_text_view, "field 'mProgressTextView'", TextView.class);
        bugReportActivity.mScrollView = C0487Utils.findRequiredView(view, C1299R.C1301id.bug_report_scrollview, "field 'mScrollView'");
        bugReportActivity.mMaskView = C0487Utils.findRequiredView(view, C1299R.C1301id.bug_report_mask_view, "field 'mMaskView'");
    }

    public void unbind() {
        BugReportActivity bugReportActivity = this.target;
        if (bugReportActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        bugReportActivity.mBugReportText = null;
        bugReportActivity.mIncludeLogsButton = null;
        bugReportActivity.mIncludeCrashLogsButton = null;
        bugReportActivity.mIncludeScreenShotButton = null;
        bugReportActivity.mProgressBar = null;
        bugReportActivity.mProgressTextView = null;
        bugReportActivity.mScrollView = null;
        bugReportActivity.mMaskView = null;
        super.unbind();
    }
}
