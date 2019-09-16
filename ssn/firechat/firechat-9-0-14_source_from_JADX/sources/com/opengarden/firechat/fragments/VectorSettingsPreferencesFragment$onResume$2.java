package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0002H\u0016¨\u0006\u0007"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onResume$2", "Lcom/opengarden/firechat/matrixsdk/rest/callback/SimpleApiCallback;", "Ljava/lang/Void;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onSuccess", "", "info", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$onResume$2 extends SimpleApiCallback<Void> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onResume$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onSuccess(@Nullable Void voidR) {
        this.this$0.refreshPushersList();
    }
}
