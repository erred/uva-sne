package com.opengarden.firechat.fragments;

import android.preference.Preference;
import com.opengarden.firechat.preference.UserAvatarPreference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Lcom/opengarden/firechat/preference/UserAvatarPreference;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$mUserAvatarPreference$2 extends Lambda implements Function0<UserAvatarPreference> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$mUserAvatarPreference$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
        super(0);
    }

    @NotNull
    public final UserAvatarPreference invoke() {
        Preference findPreference = this.this$0.findPreference(PreferencesManager.SETTINGS_PROFILE_PICTURE_PREFERENCE_KEY);
        if (findPreference != null) {
            return (UserAvatarPreference) findPreference;
        }
        throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.UserAvatarPreference");
    }
}
