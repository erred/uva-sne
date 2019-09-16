package com.opengarden.firechat.fragments;

import android.preference.Preference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001H\n¢\u0006\u0002\b\u0003"}, mo21251d2 = {"<anonymous>", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$mNotificationPrivacyPreference$2 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2281xdf454cbb extends Lambda implements Function0<Preference> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2281xdf454cbb(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
        super(0);
    }

    public final Preference invoke() {
        return this.this$0.findPreference(PreferencesManager.SETTINGS_NOTIFICATION_PRIVACY_PREFERENCE_KEY);
    }
}
