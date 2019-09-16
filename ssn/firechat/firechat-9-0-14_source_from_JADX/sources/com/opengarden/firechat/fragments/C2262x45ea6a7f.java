package com.opengarden.firechat.fragments;

import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.opengarden.firechat.Matrix;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$12 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2262x45ea6a7f implements OnPreferenceClickListener {
    final /* synthetic */ Context $appContext$inlined;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2262x45ea6a7f(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, Context context) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$appContext$inlined = context;
    }

    public final boolean onPreferenceClick(Preference preference) {
        this.this$0.displayLoadingView();
        Matrix.getInstance(this.$appContext$inlined).reloadSessions(this.$appContext$inlined);
        return false;
    }
}
