package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00001\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\nH\u0016J\u0014\u0010\u000b\u001a\u00020\u00052\n\u0010\t\u001a\u00060\fj\u0002`\rH\u0016J\u0012\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\u0010\u001a\u00020\u00052\n\u0010\t\u001a\u00060\fj\u0002`\rH\u0016¨\u0006\u0011"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$deleteDevice$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Ljava/lang/Void;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onError", "", "message", "", "onMatrixError", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$deleteDevice$1 implements ApiCallback<Void> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$deleteDevice$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onSuccess(@Nullable Void voidR) {
        this.this$0.hideLoadingView();
        this.this$0.refreshDevicesList();
    }

    private final void onError(String str) {
        this.this$0.mAccountPassword = null;
        this.this$0.onCommonDone(str);
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String localizedMessage = exc.getLocalizedMessage();
        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
        onError(localizedMessage);
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        String localizedMessage = matrixError.getLocalizedMessage();
        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
        onError(localizedMessage);
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String localizedMessage = exc.getLocalizedMessage();
        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
        onError(localizedMessage);
    }
}
