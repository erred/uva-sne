package com.opengarden.firechat.fragments;

import android.app.AlertDialog;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$exportKeys$1 implements OnClickListener {
    final /* synthetic */ AlertDialog $exportDialog;
    final /* synthetic */ TextInputEditText $passPhrase1EditText;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$exportKeys$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, TextInputEditText textInputEditText, AlertDialog alertDialog) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$passPhrase1EditText = textInputEditText;
        this.$exportDialog = alertDialog;
    }

    public final void onClick(View view) {
        this.this$0.displayLoadingView();
        MXSession access$getMSession$p = VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0);
        TextInputEditText textInputEditText = this.$passPhrase1EditText;
        Intrinsics.checkExpressionValueIsNotNull(textInputEditText, "passPhrase1EditText");
        CommonActivityUtils.exportKeys(access$getMSession$p, textInputEditText.getText().toString(), new ApiCallback<String>(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$exportKeys$1 this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@NotNull String str) {
                Intrinsics.checkParameterIsNotNull(str, "filename");
                VectorApp instance = VectorApp.getInstance();
                Intrinsics.checkExpressionValueIsNotNull(instance, "VectorApp.getInstance()");
                Toast.makeText(instance.getApplicationContext(), str, 0).show();
                this.this$0.this$0.hideLoadingView();
            }

            public void onNetworkError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.hideLoadingView();
            }

            public void onMatrixError(@NotNull MatrixError matrixError) {
                Intrinsics.checkParameterIsNotNull(matrixError, "e");
                this.this$0.this$0.hideLoadingView();
            }

            public void onUnexpectedError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.hideLoadingView();
            }
        });
        this.$exportDialog.dismiss();
    }
}
