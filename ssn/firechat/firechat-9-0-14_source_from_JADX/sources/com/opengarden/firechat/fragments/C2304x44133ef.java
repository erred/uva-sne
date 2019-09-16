package com.opengarden.firechat.fragments;

import android.preference.Preference;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference.OnPreferenceLongClickListener;
import com.opengarden.firechat.util.VectorUtils;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceLongClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$refreshCryptographyPreference$3 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2304x44133ef implements OnPreferenceLongClickListener {
    final /* synthetic */ String $deviceId;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2304x44133ef(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, String str) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$deviceId = str;
    }

    public final boolean onPreferenceLongClick(Preference preference) {
        VectorUtils.copyToClipboard(this.this$0.getActivity(), this.$deviceId);
        return true;
    }
}
