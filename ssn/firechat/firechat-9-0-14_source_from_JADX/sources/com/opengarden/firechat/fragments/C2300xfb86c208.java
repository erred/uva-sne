package com.opengarden.firechat.fragments;

import android.widget.Toast;
import com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onPasswordUpdateClick$1.C22951.C22961;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onPasswordUpdateClick$1$1$1$onDone$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2300xfb86c208 implements Runnable {
    final /* synthetic */ int $textId;
    final /* synthetic */ C22961 this$0;

    C2300xfb86c208(C22961 r1, int i) {
        this.this$0 = r1;
        this.$textId = i;
    }

    public final void run() {
        this.this$0.this$0.this$0.this$0.hideLoadingView();
        Toast.makeText(this.this$0.this$0.this$0.this$0.getActivity(), this.this$0.this$0.this$0.this$0.getString(this.$textId), 1).show();
    }
}
