package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class NotificationPrivacyActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private NotificationPrivacyActivity target;

    @UiThread
    public NotificationPrivacyActivity_ViewBinding(NotificationPrivacyActivity notificationPrivacyActivity) {
        this(notificationPrivacyActivity, notificationPrivacyActivity.getWindow().getDecorView());
    }

    @UiThread
    public NotificationPrivacyActivity_ViewBinding(NotificationPrivacyActivity notificationPrivacyActivity, View view) {
        super(notificationPrivacyActivity, view);
        this.target = notificationPrivacyActivity;
        notificationPrivacyActivity.tvNeedPermission = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.tv_apps_needs_permission, "field 'tvNeedPermission'", TextView.class);
        notificationPrivacyActivity.tvNoPermission = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.tv_apps_no_permission, "field 'tvNoPermission'", TextView.class);
        notificationPrivacyActivity.rlyNormalPrivacy = C0487Utils.findRequiredView(view, C1299R.C1301id.rly_normal_notification_privacy, "field 'rlyNormalPrivacy'");
        notificationPrivacyActivity.rlyLowDetailNotifications = C0487Utils.findRequiredView(view, C1299R.C1301id.rly_low_detail_notifications, "field 'rlyLowDetailNotifications'");
        notificationPrivacyActivity.rlyReducedPrivacy = C0487Utils.findRequiredView(view, C1299R.C1301id.rly_reduced_privacy_notifications, "field 'rlyReducedPrivacy'");
        notificationPrivacyActivity.rbPrivacyNormal = (RadioButton) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.rb_normal_notification_privacy, "field 'rbPrivacyNormal'", RadioButton.class);
        notificationPrivacyActivity.rbPrivacyLowDetail = (RadioButton) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.rb_notification_low_detail, "field 'rbPrivacyLowDetail'", RadioButton.class);
        notificationPrivacyActivity.rbPrivacyReduced = (RadioButton) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.rb_notification_reduce_privacy, "field 'rbPrivacyReduced'", RadioButton.class);
        notificationPrivacyActivity.tvPrivacyNormal = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.tv_normal_notification_privacy, "field 'tvPrivacyNormal'", TextView.class);
        notificationPrivacyActivity.tvPrivacyLowDetail = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.tv_notification_low_detail, "field 'tvPrivacyLowDetail'", TextView.class);
        notificationPrivacyActivity.tvPrivacyReduced = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.tv_notification_reduce_privacy, "field 'tvPrivacyReduced'", TextView.class);
    }

    public void unbind() {
        NotificationPrivacyActivity notificationPrivacyActivity = this.target;
        if (notificationPrivacyActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        notificationPrivacyActivity.tvNeedPermission = null;
        notificationPrivacyActivity.tvNoPermission = null;
        notificationPrivacyActivity.rlyNormalPrivacy = null;
        notificationPrivacyActivity.rlyLowDetailNotifications = null;
        notificationPrivacyActivity.rlyReducedPrivacy = null;
        notificationPrivacyActivity.rbPrivacyNormal = null;
        notificationPrivacyActivity.rbPrivacyLowDetail = null;
        notificationPrivacyActivity.rbPrivacyReduced = null;
        notificationPrivacyActivity.tvPrivacyNormal = null;
        notificationPrivacyActivity.tvPrivacyLowDetail = null;
        notificationPrivacyActivity.tvPrivacyReduced = null;
        super.unbind();
    }
}
