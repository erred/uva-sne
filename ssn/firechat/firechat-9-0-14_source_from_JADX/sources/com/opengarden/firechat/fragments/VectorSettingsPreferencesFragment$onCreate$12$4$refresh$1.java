package com.opengarden.firechat.fragments;

import com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onCreate$12.C22934;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$12$4$refresh$1 implements Runnable {
    final /* synthetic */ C22934 this$0;

    VectorSettingsPreferencesFragment$onCreate$12$4$refresh$1(C22934 r1) {
        this.this$0 = r1;
    }

    public final void run() {
        this.this$0.this$0.this$0.hideLoadingView();
        this.this$0.this$0.$useCryptoPref.setChecked(VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0.this$0).isCryptoEnabled());
        if (VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0.this$0).isCryptoEnabled()) {
            this.this$0.this$0.this$0.getMLabsCategory().removePreference(this.this$0.this$0.$useCryptoPref);
            this.this$0.this$0.this$0.getMLabsCategory().addPreference(this.this$0.this$0.$cryptoIsEnabledPref);
        }
    }
}
