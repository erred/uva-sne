package com.opengarden.firechat.dialogs;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "<anonymous parameter 0>", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "<anonymous parameter 1>", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: ConsentNotGivenHelper.kt */
final class ConsentNotGivenHelper$displayDialog$1 implements OnClickListener {
    final /* synthetic */ MatrixError $matrixError;
    final /* synthetic */ ConsentNotGivenHelper this$0;

    ConsentNotGivenHelper$displayDialog$1(ConsentNotGivenHelper consentNotGivenHelper, MatrixError matrixError) {
        this.this$0 = consentNotGivenHelper;
        this.$matrixError = matrixError;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        ConsentNotGivenHelper consentNotGivenHelper = this.this$0;
        String str = this.$matrixError.consentUri;
        Intrinsics.checkExpressionValueIsNotNull(str, "matrixError.consentUri");
        consentNotGivenHelper.openWebViewActivity(str);
        this.this$0.isDialogDisplayed = false;
    }
}
