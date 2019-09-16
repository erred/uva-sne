package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$refreshEmailsList$2 implements OnPreferenceClickListener {
    final /* synthetic */ ThirdPartyIdentifier $email3PID;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$refreshEmailsList$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, ThirdPartyIdentifier thirdPartyIdentifier) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$email3PID = thirdPartyIdentifier;
    }

    public final boolean onPreferenceClick(Preference preference) {
        VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this.this$0;
        ThirdPartyIdentifier thirdPartyIdentifier = this.$email3PID;
        Intrinsics.checkExpressionValueIsNotNull(thirdPartyIdentifier, "email3PID");
        Intrinsics.checkExpressionValueIsNotNull(preference, "preference");
        CharSequence summary = preference.getSummary();
        Intrinsics.checkExpressionValueIsNotNull(summary, "preference.summary");
        vectorSettingsPreferencesFragment.displayDelete3PIDConfirmationDialog(thirdPartyIdentifier, summary);
        return true;
    }
}
