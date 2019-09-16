package com.opengarden.firechat.fragments;

import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRuleUpdateListener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0004H\u0016J\b\u0010\b\u001a\u00020\u0004H\u0002¨\u0006\t"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onPushRuleClick$1", "Lcom/opengarden/firechat/matrixsdk/util/BingRulesManager$onBingRuleUpdateListener;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onBingRuleUpdateFailure", "", "errorMessage", "", "onBingRuleUpdateSuccess", "onDone", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$onPushRuleClick$1 implements onBingRuleUpdateListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onPushRuleClick$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    private final void onDone() {
        this.this$0.refreshDisplay();
        this.this$0.hideLoadingView();
    }

    public void onBingRuleUpdateSuccess() {
        onDone();
    }

    public void onBingRuleUpdateFailure(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "errorMessage");
        if (this.this$0.getActivity() != null) {
            Toast.makeText(this.this$0.getActivity(), str, 0).show();
        }
        onDone();
    }
}
