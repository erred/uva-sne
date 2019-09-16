package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$refreshCryptographyPreference$8 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2310x44133f4 implements OnPreferenceClickListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2310x44133f4(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final boolean onPreferenceClick(Preference preference) {
        VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getCrypto().getGlobalBlacklistUnverifiedDevices(new SimpleApiCallback<Boolean>(this) {
            final /* synthetic */ C2310x44133f4 this$0;

            {
                this.this$0 = r1;
            }

            public /* bridge */ /* synthetic */ void onSuccess(Object obj) {
                onSuccess(((Boolean) obj).booleanValue());
            }

            public void onSuccess(boolean z) {
                if (this.this$0.this$0.getSendToUnverifiedDevicesPref().isChecked() != z) {
                    VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).getCrypto().setGlobalBlacklistUnverifiedDevices(this.this$0.this$0.getSendToUnverifiedDevicesPref().isChecked(), new C2312xf9ca00e());
                }
            }
        });
        return true;
    }
}
