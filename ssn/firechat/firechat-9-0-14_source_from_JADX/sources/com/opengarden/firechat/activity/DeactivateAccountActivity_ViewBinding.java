package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import butterknife.internal.C0487Utils;
import butterknife.internal.DebouncingOnClickListener;
import com.opengarden.firechat.C1299R;

public final class DeactivateAccountActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private DeactivateAccountActivity target;
    private View view2131296418;
    private View view2131296419;

    @UiThread
    public DeactivateAccountActivity_ViewBinding(DeactivateAccountActivity deactivateAccountActivity) {
        this(deactivateAccountActivity, deactivateAccountActivity.getWindow().getDecorView());
    }

    @UiThread
    public DeactivateAccountActivity_ViewBinding(final DeactivateAccountActivity deactivateAccountActivity, View view) {
        super(deactivateAccountActivity, view);
        this.target = deactivateAccountActivity;
        deactivateAccountActivity.eraseCheckBox = (CheckBox) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.deactivate_account_erase_checkbox, "field 'eraseCheckBox'", CheckBox.class);
        deactivateAccountActivity.passwordEditText = (EditText) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.deactivate_account_password, "field 'passwordEditText'", EditText.class);
        View findRequiredView = C0487Utils.findRequiredView(view, C1299R.C1301id.deactivate_account_button_submit, "method 'onSubmit$vector_appfirechatRelease'");
        this.view2131296419 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                deactivateAccountActivity.onSubmit$vector_appfirechatRelease();
            }
        });
        View findRequiredView2 = C0487Utils.findRequiredView(view, C1299R.C1301id.deactivate_account_button_cancel, "method 'onCancel$vector_appfirechatRelease'");
        this.view2131296418 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                deactivateAccountActivity.onCancel$vector_appfirechatRelease();
            }
        });
    }

    public void unbind() {
        DeactivateAccountActivity deactivateAccountActivity = this.target;
        if (deactivateAccountActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        deactivateAccountActivity.eraseCheckBox = null;
        deactivateAccountActivity.passwordEditText = null;
        this.view2131296419.setOnClickListener(null);
        this.view2131296419 = null;
        this.view2131296418.setOnClickListener(null);
        this.view2131296418 = null;
        super.unbind();
    }
}
