package com.opengarden.firechat.fragments;

import android.text.TextUtils;
import android.widget.Toast;
import com.opengarden.firechat.VectorApp;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCommonDone$1 implements Runnable {
    final /* synthetic */ String $errorMessage;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCommonDone$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, String str) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$errorMessage = str;
    }

    public final void run() {
        if (!TextUtils.isEmpty(this.$errorMessage)) {
            Toast.makeText(VectorApp.getInstance(), this.$errorMessage, 0).show();
        }
        this.this$0.hideLoadingView();
    }
}
