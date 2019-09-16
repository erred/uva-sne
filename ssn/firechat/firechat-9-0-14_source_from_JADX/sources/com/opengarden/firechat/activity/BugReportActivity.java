package com.opengarden.firechat.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.BugReporter;
import com.opengarden.firechat.util.BugReporter.IMXBugReportListener;
import com.opengarden.firechat.util.ThemeUtils;

public class BugReportActivity extends MXCActionBarActivity {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "BugReportActivity";
    @BindView(2131296341)
    EditText mBugReportText;
    @BindView(2131296338)
    CheckBox mIncludeCrashLogsButton;
    @BindView(2131296339)
    CheckBox mIncludeLogsButton;
    @BindView(2131296340)
    CheckBox mIncludeScreenShotButton;
    @BindView(2131296342)
    View mMaskView;
    @BindView(2131296344)
    ProgressBar mProgressBar;
    @BindView(2131296343)
    TextView mProgressTextView;
    @BindView(2131296345)
    View mScrollView;
    /* access modifiers changed from: private */
    public MenuItem mSendBugReportItem;

    public int getLayoutRes() {
        return C1299R.layout.activity_bug_report;
    }

    public void initUiAndData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mBugReportText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                BugReportActivity.this.refreshSendButton();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.bug_report, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        this.mSendBugReportItem = menu.findItem(C1299R.C1301id.ic_action_send_bug_report);
        refreshSendButton();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        } else if (itemId != C1299R.C1301id.ic_action_send_bug_report) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            sendBugReport();
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        refreshSendButton();
    }

    /* access modifiers changed from: private */
    public void refreshSendButton() {
        if (this.mSendBugReportItem != null && this.mBugReportText != null) {
            boolean z = this.mBugReportText.getText() != null && this.mBugReportText.getText().toString().trim().length() > 10;
            this.mSendBugReportItem.setEnabled(z);
            this.mSendBugReportItem.getIcon().setAlpha(z ? 255 : 100);
        }
    }

    private void sendBugReport() {
        this.mScrollView.setAlpha(0.3f);
        this.mMaskView.setVisibility(0);
        this.mSendBugReportItem.setEnabled(false);
        this.mProgressTextView.setVisibility(0);
        this.mProgressTextView.setText(getString(C1299R.string.send_bug_report_progress, new Object[]{"0"}));
        this.mProgressBar.setVisibility(0);
        this.mProgressBar.setProgress(0);
        BugReporter.sendBugReport(VectorApp.getInstance(), this.mIncludeLogsButton.isChecked(), this.mIncludeCrashLogsButton.isChecked(), this.mIncludeScreenShotButton.isChecked(), this.mBugReportText.getText().toString(), new IMXBugReportListener() {
            public void onUploadFailed(String str) {
                try {
                    if (VectorApp.getInstance() != null && !TextUtils.isEmpty(str)) {
                        Toast.makeText(VectorApp.getInstance(), VectorApp.getInstance().getString(C1299R.string.send_bug_report_failed, new Object[]{str}), 1).show();
                    }
                } catch (Exception e) {
                    String access$100 = BugReportActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onUploadFailed() : failed to display the toast ");
                    sb.append(e.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
                BugReportActivity.this.mMaskView.setVisibility(8);
                BugReportActivity.this.mProgressBar.setVisibility(8);
                BugReportActivity.this.mProgressTextView.setVisibility(8);
                BugReportActivity.this.mScrollView.setAlpha(1.0f);
                BugReportActivity.this.mSendBugReportItem.setEnabled(true);
            }

            public void onUploadCancelled() {
                onUploadFailed(null);
            }

            public void onProgress(int i) {
                if (i > 100) {
                    Log.m211e(BugReportActivity.LOG_TAG, "## onProgress() : progress > 100");
                    i = 100;
                } else if (i < 0) {
                    Log.m211e(BugReportActivity.LOG_TAG, "## onProgress() : progress < 0");
                    i = 0;
                }
                BugReportActivity.this.mProgressBar.setProgress(i);
                TextView textView = BugReportActivity.this.mProgressTextView;
                BugReportActivity bugReportActivity = BugReportActivity.this;
                StringBuilder sb = new StringBuilder();
                sb.append(i);
                sb.append("");
                textView.setText(bugReportActivity.getString(C1299R.string.send_bug_report_progress, new Object[]{sb.toString()}));
            }

            public void onUploadSucceed() {
                try {
                    if (VectorApp.getInstance() != null) {
                        Toast.makeText(VectorApp.getInstance(), VectorApp.getInstance().getString(C1299R.string.send_bug_report_sent), 1).show();
                    }
                } catch (Exception e) {
                    String access$100 = BugReportActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onUploadSucceed() : failed to dismiss the toast ");
                    sb.append(e.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
                try {
                    BugReportActivity.this.finish();
                } catch (Exception e2) {
                    String access$1002 = BugReportActivity.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onUploadSucceed() : failed to dismiss the dialog ");
                    sb2.append(e2.getMessage());
                    Log.m211e(access$1002, sb2.toString());
                }
            }
        });
    }
}
