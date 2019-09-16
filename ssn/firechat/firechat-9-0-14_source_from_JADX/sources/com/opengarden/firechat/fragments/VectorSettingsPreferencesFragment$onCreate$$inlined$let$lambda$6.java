package com.opengarden.firechat.fragments;

import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.TypeCastException;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$6 implements OnPreferenceChangeListener {
    final /* synthetic */ Context $appContext$inlined;

    VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$6(Context context) {
        this.$appContext$inlined = context;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        Context context = this.$appContext$inlined;
        if (obj == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Boolean");
        }
        PreferencesManager.setUseEnterKeyToSendMessage(context, ((Boolean) obj).booleanValue());
        return true;
    }
}
