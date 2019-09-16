package com.opengarden.firechat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.p003v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager.NotificationPrivacy;

public class NotificationPrivacyActivity extends RiotAppCompatActivity {
    private static final String LOG_TAG = "NotificationPrivacyActivity";
    @BindView(2131296876)
    RadioButton rbPrivacyLowDetail;
    @BindView(2131296875)
    RadioButton rbPrivacyNormal;
    @BindView(2131296877)
    RadioButton rbPrivacyReduced;
    @BindView(2131296898)
    View rlyLowDetailNotifications;
    @BindView(2131296899)
    View rlyNormalPrivacy;
    @BindView(2131296901)
    View rlyReducedPrivacy;
    @BindView(2131297089)
    TextView tvNeedPermission;
    @BindView(2131297090)
    TextView tvNoPermission;
    @BindView(2131297093)
    TextView tvPrivacyLowDetail;
    @BindView(2131297092)
    TextView tvPrivacyNormal;
    @BindView(2131297094)
    TextView tvPrivacyReduced;

    public int getLayoutRes() {
        return C1299R.layout.activity_notification_privacy;
    }

    public int getTitleRes() {
        return C1299R.string.settings_notification_privacy;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, NotificationPrivacyActivity.class);
    }

    public void initUiAndData() {
        Toolbar toolbar = (Toolbar) findViewById(C1299R.C1301id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        if (VERSION.SDK_INT >= 23) {
            this.tvNeedPermission.setVisibility(0);
            this.tvNoPermission.setVisibility(0);
        } else {
            this.tvNeedPermission.setVisibility(8);
            this.tvNoPermission.setVisibility(8);
        }
        refreshNotificationPrivacy();
        this.rlyNormalPrivacy.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NotificationPrivacyActivity.setNotificationPrivacy(NotificationPrivacyActivity.this, NotificationPrivacy.NORMAL);
                NotificationPrivacyActivity.this.refreshNotificationPrivacy();
            }
        });
        this.rlyLowDetailNotifications.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NotificationPrivacyActivity.setNotificationPrivacy(NotificationPrivacyActivity.this, NotificationPrivacy.LOW_DETAIL);
                NotificationPrivacyActivity.this.refreshNotificationPrivacy();
            }
        });
        this.rlyReducedPrivacy.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                NotificationPrivacyActivity.setNotificationPrivacy(NotificationPrivacyActivity.this, NotificationPrivacy.REDUCED);
                NotificationPrivacyActivity.this.refreshNotificationPrivacy();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        refreshNotificationPrivacy();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        setResult(0);
        finish();
        return true;
    }

    /* access modifiers changed from: private */
    public void refreshNotificationPrivacy() {
        GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(this).getSharedGCMRegistrationManager();
        switch (sharedGCMRegistrationManager.getNotificationPrivacy()) {
            case REDUCED:
                this.rbPrivacyNormal.setChecked(false);
                this.rbPrivacyLowDetail.setChecked(false);
                this.rbPrivacyReduced.setChecked(true);
                return;
            case LOW_DETAIL:
                this.rbPrivacyNormal.setChecked(false);
                this.rbPrivacyLowDetail.setChecked(true);
                this.rbPrivacyReduced.setChecked(false);
                return;
            case NORMAL:
                this.rbPrivacyNormal.setChecked(true);
                this.rbPrivacyLowDetail.setChecked(false);
                this.rbPrivacyReduced.setChecked(false);
                return;
            default:
                return;
        }
    }

    public static void setNotificationPrivacy(Activity activity, NotificationPrivacy notificationPrivacy) {
        Matrix.getInstance(activity).getSharedGCMRegistrationManager().setNotificationPrivacy(notificationPrivacy);
        if (VERSION.SDK_INT >= 23 && notificationPrivacy == NotificationPrivacy.NORMAL) {
            Intent intent = new Intent();
            intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
            StringBuilder sb = new StringBuilder();
            sb.append("package:");
            sb.append(activity.getPackageName());
            intent.setData(Uri.parse(sb.toString()));
            activity.startActivity(intent);
        }
    }

    public static String getNotificationPrivacyString(Context context, NotificationPrivacy notificationPrivacy) {
        switch (notificationPrivacy) {
            case REDUCED:
                return context.getString(C1299R.string.settings_notification_privacy_reduced);
            case LOW_DETAIL:
                return context.getString(C1299R.string.settings_notification_privacy_low_detail);
            case NORMAL:
                return context.getString(C1299R.string.settings_notification_privacy_normal);
            default:
                return null;
        }
    }
}
