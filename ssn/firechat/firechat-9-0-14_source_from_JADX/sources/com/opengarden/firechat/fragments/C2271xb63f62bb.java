package com.opengarden.firechat.fragments;

import android.preference.Preference;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$cryptoInfoDeviceNamePreference$2 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2271xb63f62bb extends Lambda implements Function0<VectorCustomActionEditTextPreference> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2271xb63f62bb(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
        super(0);
    }

    @NotNull
    public final VectorCustomActionEditTextPreference invoke() {
        Preference findPreference = this.this$0.findPreference(PreferencesManager.SETTINGS_ENCRYPTION_INFORMATION_DEVICE_NAME_PREFERENCE_KEY);
        if (findPreference != null) {
            return (VectorCustomActionEditTextPreference) findPreference;
        }
        throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.VectorCustomActionEditTextPreference");
    }
}
