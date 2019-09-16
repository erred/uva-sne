package com.opengarden.firechat.fragments;

import android.app.Activity;
import com.opengarden.firechat.activity.RiotAppCompatActivity;
import com.opengarden.firechat.fragments.C2284x719768b0.C22851;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.TypeCastException;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onActivityResult$1$onUploadComplete$1$1$onMatrixError$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2286xbc68688e implements Runnable {

    /* renamed from: $e */
    final /* synthetic */ MatrixError f124$e;
    final /* synthetic */ C22851 this$0;

    C2286xbc68688e(C22851 r1, MatrixError matrixError) {
        this.this$0 = r1;
        this.f124$e = matrixError;
    }

    public final void run() {
        this.this$0.this$0.this$0.this$0.hideLoadingView();
        Activity activity = this.this$0.this$0.this$0.this$0.getActivity();
        if (activity == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.activity.RiotAppCompatActivity");
        }
        ((RiotAppCompatActivity) activity).getConsentNotGivenHelper().displayDialog(this.f124$e);
    }
}
