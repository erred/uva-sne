package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n¢\u0006\u0002\b\u0004"}, mo21251d2 = {"<anonymous>", "", "it", "", "onNetworkConnectionUpdate"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$mNetworkListener$1 implements IMXNetworkEventListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$mNetworkListener$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final void onNetworkConnectionUpdate(boolean z) {
        this.this$0.refreshDisplay();
    }
}
