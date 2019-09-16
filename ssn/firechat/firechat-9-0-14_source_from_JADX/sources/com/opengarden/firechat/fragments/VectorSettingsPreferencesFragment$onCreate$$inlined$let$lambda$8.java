package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$8 implements OnPreferenceClickListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$8(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final boolean onPreferenceClick(final Preference preference) {
        new Builder(this.this$0.getActivity()).setSingleChoiceItems(PreferencesManager.getMediasSavingItemsChoicesList(this.this$0.getActivity()), PreferencesManager.getSelectedMediasSavingPeriod(this.this$0.getActivity()), new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$8 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PreferencesManager.setSelectedMediasSavingPeriod(this.this$0.this$0.getActivity(), i);
                dialogInterface.cancel();
                Preference preference = preference;
                Intrinsics.checkExpressionValueIsNotNull(preference, "it");
                preference.setSummary(PreferencesManager.getSelectedMediasSavingPeriodString(this.this$0.this$0.getActivity()));
            }
        }).show();
        return false;
    }
}
