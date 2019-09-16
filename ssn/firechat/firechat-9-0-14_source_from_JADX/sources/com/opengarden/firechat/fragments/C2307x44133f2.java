package com.opengarden.firechat.fragments;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.util.MatrixSdkExtensionsKt;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0002H\u0016¨\u0006\u0007"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$refreshCryptographyPreference$6", "Lcom/opengarden/firechat/matrixsdk/rest/callback/SimpleApiCallback;", "Lcom/opengarden/firechat/matrixsdk/crypto/data/MXDeviceInfo;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onSuccess", "", "deviceInfo", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$refreshCryptographyPreference$6 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class C2307x44133f2 extends SimpleApiCallback<MXDeviceInfo> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2307x44133f2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onSuccess(@Nullable MXDeviceInfo mXDeviceInfo) {
        if (mXDeviceInfo != null && !TextUtils.isEmpty(mXDeviceInfo.fingerprint()) && this.this$0.getActivity() != null) {
            this.this$0.getCryptoInfoTextPreference().setSummary(MatrixSdkExtensionsKt.getFingerprintHumanReadable(mXDeviceInfo));
            this.this$0.getCryptoInfoTextPreference().setOnPreferenceLongClickListener(new C2308x8873a6ff(this, mXDeviceInfo));
        }
    }
}
