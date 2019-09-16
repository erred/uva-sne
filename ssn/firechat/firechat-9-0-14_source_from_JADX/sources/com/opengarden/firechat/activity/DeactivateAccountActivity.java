package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\r\u0010\u0016\u001a\u00020\u0015H\u0001¢\u0006\u0002\b\u0017J\r\u0010\u0018\u001a\u00020\u0015H\u0001¢\u0006\u0002\b\u0019R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X.¢\u0006\u0002\n\u0000¨\u0006\u001b"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/DeactivateAccountActivity;", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "()V", "eraseCheckBox", "Landroid/widget/CheckBox;", "getEraseCheckBox", "()Landroid/widget/CheckBox;", "setEraseCheckBox", "(Landroid/widget/CheckBox;)V", "passwordEditText", "Landroid/widget/EditText;", "getPasswordEditText", "()Landroid/widget/EditText;", "setPasswordEditText", "(Landroid/widget/EditText;)V", "session", "Lcom/opengarden/firechat/matrixsdk/MXSession;", "getLayoutRes", "", "getTitleRes", "initUiAndData", "", "onCancel", "onCancel$vector_appfirechatRelease", "onSubmit", "onSubmit$vector_appfirechatRelease", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: DeactivateAccountActivity.kt */
public final class DeactivateAccountActivity extends RiotAppCompatActivity {
    public static final Companion Companion = new Companion(null);
    @NotNull
    @BindView(2131296421)
    public CheckBox eraseCheckBox;
    @NotNull
    @BindView(2131296422)
    public EditText passwordEditText;
    private MXSession session;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/DeactivateAccountActivity$Companion;", "", "()V", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: DeactivateAccountActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            return new Intent(context, DeactivateAccountActivity.class);
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_deactivate_account;
    }

    public int getTitleRes() {
        return C1299R.string.deactivate_account_title;
    }

    @NotNull
    public final CheckBox getEraseCheckBox() {
        CheckBox checkBox = this.eraseCheckBox;
        if (checkBox == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eraseCheckBox");
        }
        return checkBox;
    }

    public final void setEraseCheckBox(@NotNull CheckBox checkBox) {
        Intrinsics.checkParameterIsNotNull(checkBox, "<set-?>");
        this.eraseCheckBox = checkBox;
    }

    @NotNull
    public final EditText getPasswordEditText() {
        EditText editText = this.passwordEditText;
        if (editText == null) {
            Intrinsics.throwUninitializedPropertyAccessException("passwordEditText");
        }
        return editText;
    }

    public final void setPasswordEditText(@NotNull EditText editText) {
        Intrinsics.checkParameterIsNotNull(editText, "<set-?>");
        this.passwordEditText = editText;
    }

    public void initUiAndData() {
        super.initUiAndData();
        configureToolbar();
        setWaitingView(findViewById(C1299R.C1301id.waiting_view));
        Matrix instance = Matrix.getInstance(this);
        Intrinsics.checkExpressionValueIsNotNull(instance, "Matrix.getInstance(this)");
        MXSession defaultSession = instance.getDefaultSession();
        Intrinsics.checkExpressionValueIsNotNull(defaultSession, "Matrix.getInstance(this).defaultSession");
        this.session = defaultSession;
    }

    @OnClick({2131296419})
    public final void onSubmit$vector_appfirechatRelease() {
        EditText editText = this.passwordEditText;
        if (editText == null) {
            Intrinsics.throwUninitializedPropertyAccessException("passwordEditText");
        }
        String obj = editText.getText().toString();
        if (obj.length() == 0) {
            EditText editText2 = this.passwordEditText;
            if (editText2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("passwordEditText");
            }
            editText2.setError(getString(C1299R.string.auth_missing_password));
            return;
        }
        showWaitingView();
        Context context = this;
        MXSession mXSession = this.session;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("session");
        }
        CheckBox checkBox = this.eraseCheckBox;
        if (checkBox == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eraseCheckBox");
        }
        CommonActivityUtils.deactivateAccount(context, mXSession, obj, checkBox.isChecked(), new DeactivateAccountActivity$onSubmit$1(this, this));
    }

    @OnClick({2131296418})
    public final void onCancel$vector_appfirechatRelease() {
        finish();
    }
}
