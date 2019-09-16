package com.opengarden.firechat.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$showEmailValidationDialog$1 implements OnClickListener {
    final /* synthetic */ ThreePid $pid;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$showEmailValidationDialog$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, ThreePid threePid) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$pid = threePid;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getMyUser().add3Pid(this.$pid, true, new ApiCallback<Void>(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$showEmailValidationDialog$1 this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@Nullable Void voidR) {
                if (this.this$0.this$0.getActivity() != null) {
                    this.this$0.this$0.getActivity().runOnUiThread(new C2321xe61bfb85(this));
                }
            }

            public void onNetworkError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(@NotNull MatrixError matrixError) {
                Intrinsics.checkParameterIsNotNull(matrixError, "e");
                if (!TextUtils.equals(matrixError.errcode, MatrixError.THREEPID_AUTH_FAILED)) {
                    this.this$0.this$0.onCommonDone(matrixError.getLocalizedMessage());
                } else if (this.this$0.this$0.getActivity() != null) {
                    this.this$0.this$0.getActivity().runOnUiThread(new C2320x9b7e32c9(this));
                }
            }

            public void onUnexpectedError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }
        });
    }
}
