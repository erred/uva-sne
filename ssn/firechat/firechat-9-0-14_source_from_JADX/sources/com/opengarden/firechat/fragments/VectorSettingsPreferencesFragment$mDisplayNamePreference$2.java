package com.opengarden.firechat.fragments;

import android.preference.EditTextPreference;
import android.preference.Preference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Landroid/preference/EditTextPreference;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$mDisplayNamePreference$2 extends Lambda implements Function0<EditTextPreference> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$mDisplayNamePreference$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
        super(0);
    }

    @NotNull
    public final EditTextPreference invoke() {
        Preference findPreference = this.this$0.findPreference(PreferencesManager.SETTINGS_DISPLAY_NAME_PREFERENCE_KEY);
        if (findPreference != null) {
            return (EditTextPreference) findPreference;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.preference.EditTextPreference");
    }
}
