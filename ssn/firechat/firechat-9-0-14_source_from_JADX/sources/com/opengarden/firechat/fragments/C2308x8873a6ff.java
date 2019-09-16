package com.opengarden.firechat.fragments;

import android.preference.Preference;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference.OnPreferenceLongClickListener;
import com.opengarden.firechat.util.VectorUtils;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceLongClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$refreshCryptographyPreference$6$onSuccess$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2308x8873a6ff implements OnPreferenceLongClickListener {
    final /* synthetic */ MXDeviceInfo $deviceInfo;
    final /* synthetic */ C2307x44133f2 this$0;

    C2308x8873a6ff(C2307x44133f2 vectorSettingsPreferencesFragment$refreshCryptographyPreference$6, MXDeviceInfo mXDeviceInfo) {
        this.this$0 = vectorSettingsPreferencesFragment$refreshCryptographyPreference$6;
        this.$deviceInfo = mXDeviceInfo;
    }

    public final boolean onPreferenceLongClick(Preference preference) {
        VectorUtils.copyToClipboard(this.this$0.this$0.getActivity(), this.$deviceInfo.fingerprint());
        return true;
    }
}
