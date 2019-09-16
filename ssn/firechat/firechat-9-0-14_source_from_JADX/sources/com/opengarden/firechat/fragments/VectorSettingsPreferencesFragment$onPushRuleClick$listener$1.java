package com.opengarden.firechat.fragments;

import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager.ThirdPartyRegistrationListener;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0015\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0002J\b\u0010\u0005\u001a\u00020\u0004H\u0016J\b\u0010\u0006\u001a\u00020\u0004H\u0016J\b\u0010\u0007\u001a\u00020\u0004H\u0016J\b\u0010\b\u001a\u00020\u0004H\u0016¨\u0006\t"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onPushRuleClick$listener$1", "Lcom/opengarden/firechat/gcm/GcmRegistrationManager$ThirdPartyRegistrationListener;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;Lcom/opengarden/firechat/gcm/GcmRegistrationManager;Z)V", "onDone", "", "onThirdPartyRegistered", "onThirdPartyRegistrationFailed", "onThirdPartyUnregistered", "onThirdPartyUnregistrationFailed", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$onPushRuleClick$listener$1 implements ThirdPartyRegistrationListener {
    final /* synthetic */ GcmRegistrationManager $gcmMgr;
    final /* synthetic */ boolean $isAllowed;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onPushRuleClick$listener$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, GcmRegistrationManager gcmRegistrationManager, boolean z) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$gcmMgr = gcmRegistrationManager;
        this.$isAllowed = z;
    }

    private final void onDone() {
        if (this.this$0.getActivity() != null) {
            this.this$0.getActivity().runOnUiThread(new C2301x85083cbe(this));
        }
    }

    public void onThirdPartyRegistered() {
        onDone();
    }

    public void onThirdPartyRegistrationFailed() {
        this.$gcmMgr.setDeviceNotificationsAllowed(this.$isAllowed);
        onDone();
    }

    public void onThirdPartyUnregistered() {
        onDone();
    }

    public void onThirdPartyUnregistrationFailed() {
        this.$gcmMgr.setDeviceNotificationsAllowed(this.$isAllowed);
        onDone();
    }
}
