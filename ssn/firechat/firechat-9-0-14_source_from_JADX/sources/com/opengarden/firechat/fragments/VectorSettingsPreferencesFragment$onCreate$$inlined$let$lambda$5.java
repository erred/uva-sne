package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager.ThirdPartyRegistrationListener;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "aNewValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$5 implements OnPreferenceChangeListener {
    final /* synthetic */ GcmRegistrationManager $gcmMgr$inlined;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$5(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, GcmRegistrationManager gcmRegistrationManager) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$gcmMgr$inlined = gcmRegistrationManager;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if (obj == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Boolean");
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        GcmRegistrationManager gcmRegistrationManager = this.$gcmMgr$inlined;
        Intrinsics.checkExpressionValueIsNotNull(gcmRegistrationManager, "gcmMgr");
        if (booleanValue != gcmRegistrationManager.isBackgroundSyncAllowed()) {
            GcmRegistrationManager gcmRegistrationManager2 = this.$gcmMgr$inlined;
            Intrinsics.checkExpressionValueIsNotNull(gcmRegistrationManager2, "gcmMgr");
            gcmRegistrationManager2.setBackgroundSyncAllowed(booleanValue);
        }
        this.this$0.displayLoadingView();
        Matrix instance = Matrix.getInstance(this.this$0.getActivity());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        instance.getSharedGCMRegistrationManager().forceSessionsRegistration(new ThirdPartyRegistrationListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$5 this$0;

            {
                this.this$0 = r1;
            }

            public void onThirdPartyRegistered() {
                this.this$0.this$0.hideLoadingView();
            }

            public void onThirdPartyRegistrationFailed() {
                this.this$0.this$0.hideLoadingView();
            }

            public void onThirdPartyUnregistered() {
                this.this$0.this$0.hideLoadingView();
            }

            public void onThirdPartyUnregistrationFailed() {
                this.this$0.this$0.hideLoadingView();
            }
        });
        return true;
    }
}
