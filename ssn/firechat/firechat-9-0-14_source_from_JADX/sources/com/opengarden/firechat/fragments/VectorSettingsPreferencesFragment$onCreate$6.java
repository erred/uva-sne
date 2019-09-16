package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.opengarden.firechat.VectorApp;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$6 implements OnPreferenceChangeListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$6(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if (!(obj instanceof String)) {
            return false;
        }
        VectorApp.updateApplicationTheme((String) obj);
        Activity activity = this.this$0.getActivity();
        Activity activity2 = this.this$0.getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity2, "activity");
        activity.startActivity(activity2.getIntent());
        this.this$0.getActivity().finish();
        return true;
    }
}
