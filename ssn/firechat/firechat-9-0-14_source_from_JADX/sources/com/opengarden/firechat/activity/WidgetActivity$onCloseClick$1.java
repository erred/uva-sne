package com.opengarden.firechat.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: WidgetActivity.kt */
final class WidgetActivity$onCloseClick$1 implements OnClickListener {
    final /* synthetic */ WidgetActivity this$0;

    WidgetActivity$onCloseClick$1(WidgetActivity widgetActivity) {
        this.this$0 = widgetActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.this$0.showWaitingView();
        WidgetsManager sharedInstance = WidgetsManager.getSharedInstance();
        MXSession access$getMSession$p = this.this$0.mSession;
        Room access$getMRoom$p = this.this$0.mRoom;
        Widget access$getMWidget$p = this.this$0.mWidget;
        if (access$getMWidget$p == null) {
            Intrinsics.throwNpe();
        }
        sharedInstance.closeWidget(access$getMSession$p, access$getMRoom$p, access$getMWidget$p.getWidgetId(), new ApiCallback<Void>(this) {
            final /* synthetic */ WidgetActivity$onCloseClick$1 this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@NotNull Void voidR) {
                Intrinsics.checkParameterIsNotNull(voidR, "info");
                this.this$0.this$0.hideWaitingView();
                this.this$0.this$0.finish();
            }

            private final void onError(String str) {
                this.this$0.this$0.hideWaitingView();
                CommonActivityUtils.displayToast(this.this$0.this$0, str);
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
        });
    }
}
