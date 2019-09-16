package com.opengarden.firechat.activity;

import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000+\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0002J\u0010\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0016J\u0014\u0010\n\u001a\u00020\u00052\n\u0010\b\u001a\u00060\u000bj\u0002`\fH\u0016J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\u0014\u0010\u000f\u001a\u00020\u00052\n\u0010\b\u001a\u00060\u000bj\u0002`\fH\u0016¨\u0006\u0010"}, mo21251d2 = {"com/opengarden/firechat/activity/AbstractWidgetActivity$initUiAndData$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "", "(Lcom/opengarden/firechat/activity/AbstractWidgetActivity;)V", "onError", "", "errorMessage", "onMatrixError", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "scalarToken", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: AbstractWidgetActivity.kt */
public final class AbstractWidgetActivity$initUiAndData$1 implements ApiCallback<String> {
    final /* synthetic */ AbstractWidgetActivity this$0;

    AbstractWidgetActivity$initUiAndData$1(AbstractWidgetActivity abstractWidgetActivity) {
        this.this$0 = abstractWidgetActivity;
    }

    public void onSuccess(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "scalarToken");
        this.this$0.hideWaitingView();
        this.this$0.launchUrl(str);
    }

    private final void onError(String str) {
        this.this$0.finish();
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
