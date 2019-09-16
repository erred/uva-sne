package com.opengarden.firechat.fragments;

import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onActivityResult$1$onUploadError$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2287xb075f7ab implements Runnable {
    final /* synthetic */ String $serverErrorMessage;
    final /* synthetic */ int $serverResponseCode;
    final /* synthetic */ VectorSettingsPreferencesFragment$onActivityResult$1 this$0;

    C2287xb075f7ab(VectorSettingsPreferencesFragment$onActivityResult$1 vectorSettingsPreferencesFragment$onActivityResult$1, int i, String str) {
        this.this$0 = vectorSettingsPreferencesFragment$onActivityResult$1;
        this.$serverResponseCode = i;
        this.$serverErrorMessage = str;
    }

    public final void run() {
        VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this.this$0.this$0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(this.$serverResponseCode));
        sb.append(" : ");
        sb.append(this.$serverErrorMessage);
        vectorSettingsPreferencesFragment.onCommonDone(sb.toString());
    }
}
