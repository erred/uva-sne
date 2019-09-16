package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$refreshBackgroundSyncPrefs$$inlined$let$lambda$2 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2268x24e1f185 implements OnPreferenceChangeListener {
    final /* synthetic */ int $delay$inlined;
    final /* synthetic */ GcmRegistrationManager $gcmmgr$inlined;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2268x24e1f185(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, int i, GcmRegistrationManager gcmRegistrationManager) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$delay$inlined = i;
        this.$gcmmgr$inlined = gcmRegistrationManager;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        int i = this.$delay$inlined;
        if (obj == null) {
            try {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            } catch (Exception e) {
                String access$getLOG_TAG$p = VectorSettingsPreferencesFragment.Companion.getLOG_TAG();
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshBackgroundSyncPrefs : parseInt failed ");
                sb.append(e.getMessage());
                Log.m211e(access$getLOG_TAG$p, sb.toString());
            }
        } else {
            i = Integer.parseInt((String) obj);
            if (i != this.$delay$inlined) {
                GcmRegistrationManager gcmRegistrationManager = this.$gcmmgr$inlined;
                Intrinsics.checkExpressionValueIsNotNull(gcmRegistrationManager, "gcmmgr");
                gcmRegistrationManager.setBackgroundSyncDelay(i * 1000);
                this.this$0.getActivity().runOnUiThread(new Runnable(this) {
                    final /* synthetic */ C2268x24e1f185 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public final void run() {
                        this.this$0.this$0.refreshBackgroundSyncPrefs();
                    }
                });
            }
            return false;
        }
    }
}
