package com.opengarden.firechat.fragments;

import com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$showEmailValidationDialog$1.C23191;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$showEmailValidationDialog$1$1$onSuccess$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2321xe61bfb85 implements Runnable {
    final /* synthetic */ C23191 this$0;

    C2321xe61bfb85(C23191 r1) {
        this.this$0 = r1;
    }

    public final void run() {
        this.this$0.this$0.this$0.hideLoadingView();
        this.this$0.this$0.this$0.refreshEmailsList();
    }
}
