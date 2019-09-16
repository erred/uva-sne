package com.opengarden.firechat.fragments;

import android.preference.EditTextPreference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u0004\u0018\u00010\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Landroid/preference/EditTextPreference;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$mSyncRequestTimeoutPreference$2 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2282xac335ea1 extends Lambda implements Function0<EditTextPreference> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2282xac335ea1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
        super(0);
    }

    @Nullable
    public final EditTextPreference invoke() {
        return (EditTextPreference) this.this$0.findPreference(PreferencesManager.SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY);
    }
}
