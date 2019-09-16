package com.opengarden.firechat.activity;

import android.app.Activity;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0012\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/activity/DeactivateAccountActivity$onSubmit$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/SimpleApiCallback;", "Ljava/lang/Void;", "(Lcom/opengarden/firechat/activity/DeactivateAccountActivity;Landroid/app/Activity;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: DeactivateAccountActivity.kt */
public final class DeactivateAccountActivity$onSubmit$1 extends SimpleApiCallback<Void> {
    final /* synthetic */ DeactivateAccountActivity this$0;

    DeactivateAccountActivity$onSubmit$1(DeactivateAccountActivity deactivateAccountActivity, Activity activity) {
        this.this$0 = deactivateAccountActivity;
        super(activity);
    }

    public void onSuccess(@Nullable Void voidR) {
        this.this$0.hideWaitingView();
        CommonActivityUtils.startLoginActivityNewTask(this.this$0);
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        this.this$0.hideWaitingView();
        if (Intrinsics.areEqual((Object) matrixError.errcode, (Object) MatrixError.FORBIDDEN)) {
            this.this$0.getPasswordEditText().setError(this.this$0.getString(C1299R.string.auth_invalid_login_param));
        } else {
            super.onMatrixError(matrixError);
        }
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.hideWaitingView();
        super.onNetworkError(exc);
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.hideWaitingView();
        super.onUnexpectedError(exc);
    }
}
