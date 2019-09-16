package com.opengarden.firechat.dialogs;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "onCancel"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: ConsentNotGivenHelper.kt */
final class ConsentNotGivenHelper$displayDialog$3 implements OnCancelListener {
    final /* synthetic */ ConsentNotGivenHelper this$0;

    ConsentNotGivenHelper$displayDialog$3(ConsentNotGivenHelper consentNotGivenHelper) {
        this.this$0 = consentNotGivenHelper;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.this$0.isDialogDisplayed = false;
    }
}
