package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRuleUpdateListener;
import com.opengarden.firechat.preference.BingRulePreference;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$10 implements OnPreferenceClickListener {
    final /* synthetic */ Preference $preference;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$10(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, Preference preference) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$preference = preference;
    }

    public final boolean onPreferenceClick(Preference preference) {
        new Builder(this.this$0.getActivity()).setSingleChoiceItems(((BingRulePreference) this.$preference).getBingRuleStatuses(), ((BingRulePreference) this.$preference).getRuleStatusIndex(), new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$10 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BingRule createRule = ((BingRulePreference) this.this$0.$preference).createRule(i);
                dialogInterface.cancel();
                if (createRule != null) {
                    this.this$0.this$0.displayLoadingView();
                    MXDataHandler dataHandler = VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).getDataHandler();
                    Intrinsics.checkExpressionValueIsNotNull(dataHandler, "mSession.dataHandler");
                    dataHandler.getBingRulesManager().updateRule(((BingRulePreference) this.this$0.$preference).getRule(), createRule, new onBingRuleUpdateListener(this) {
                        final /* synthetic */ C22881 this$0;

                        {
                            this.this$0 = r1;
                        }

                        private final void onDone() {
                            this.this$0.this$0.this$0.refreshDisplay();
                            this.this$0.this$0.this$0.hideLoadingView();
                        }

                        public void onBingRuleUpdateSuccess() {
                            onDone();
                        }

                        public void onBingRuleUpdateFailure(@NotNull String str) {
                            Intrinsics.checkParameterIsNotNull(str, "errorMessage");
                            if (this.this$0.this$0.this$0.getActivity() != null) {
                                Toast.makeText(this.this$0.this$0.this$0.getActivity(), str, 0).show();
                            }
                            onDone();
                        }
                    });
                }
            }
        }).show();
        return true;
    }
}
