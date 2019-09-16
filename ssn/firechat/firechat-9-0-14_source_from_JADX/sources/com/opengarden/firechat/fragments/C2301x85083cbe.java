package com.opengarden.firechat.fragments;

import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onPushRuleClick$listener$1$onDone$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2301x85083cbe implements Runnable {
    final /* synthetic */ VectorSettingsPreferencesFragment$onPushRuleClick$listener$1 this$0;

    C2301x85083cbe(VectorSettingsPreferencesFragment$onPushRuleClick$listener$1 vectorSettingsPreferencesFragment$onPushRuleClick$listener$1) {
        this.this$0 = vectorSettingsPreferencesFragment$onPushRuleClick$listener$1;
    }

    public final void run() {
        this.this$0.this$0.hideLoadingView(true);
        this.this$0.this$0.refreshPushersList();
    }
}
