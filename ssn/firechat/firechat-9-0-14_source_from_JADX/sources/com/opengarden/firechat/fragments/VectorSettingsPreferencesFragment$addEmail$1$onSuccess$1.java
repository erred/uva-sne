package com.opengarden.firechat.fragments;

import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$addEmail$1$onSuccess$1 implements Runnable {
    final /* synthetic */ VectorSettingsPreferencesFragment$addEmail$1 this$0;

    VectorSettingsPreferencesFragment$addEmail$1$onSuccess$1(VectorSettingsPreferencesFragment$addEmail$1 vectorSettingsPreferencesFragment$addEmail$1) {
        this.this$0 = vectorSettingsPreferencesFragment$addEmail$1;
    }

    public final void run() {
        this.this$0.this$0.showEmailValidationDialog(this.this$0.$pid);
    }
}
