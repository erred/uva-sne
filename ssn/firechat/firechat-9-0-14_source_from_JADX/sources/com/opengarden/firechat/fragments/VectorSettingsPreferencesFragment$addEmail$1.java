package com.opengarden.firechat.fragments;

import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0012\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$addEmail$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Ljava/lang/Void;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;Lcom/opengarden/firechat/matrixsdk/rest/model/pid/ThreePid;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$addEmail$1 implements ApiCallback<Void> {
    final /* synthetic */ ThreePid $pid;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$addEmail$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, ThreePid threePid) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$pid = threePid;
    }

    public void onSuccess(@Nullable Void voidR) {
        if (this.this$0.getActivity() != null) {
            this.this$0.getActivity().runOnUiThread(new VectorSettingsPreferencesFragment$addEmail$1$onSuccess$1(this));
        }
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.onCommonDone(exc.getLocalizedMessage());
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        if (TextUtils.equals(MatrixError.THREEPID_IN_USE, matrixError.errcode)) {
            this.this$0.onCommonDone(this.this$0.getString(C1299R.string.account_email_already_used_error));
        } else {
            this.this$0.onCommonDone(matrixError.getLocalizedMessage());
        }
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.onCommonDone(exc.getLocalizedMessage());
    }
}
