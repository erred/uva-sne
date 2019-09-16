package com.opengarden.firechat.fragments;

import android.content.Context;
import android.preference.Preference;
import android.text.format.Formatter;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016¨\u0006\u0007"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onCreate$21$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/SimpleApiCallback;", "", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onCreate$21;Landroid/preference/Preference;)V", "onSuccess", "", "size", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$11 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class C2261x45ea6a7e extends SimpleApiCallback<Long> {
    final /* synthetic */ Context $appContext$inlined;
    final /* synthetic */ Preference $it;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2261x45ea6a7e(Preference preference, VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, Context context) {
        this.$it = preference;
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$appContext$inlined = context;
    }

    public /* bridge */ /* synthetic */ void onSuccess(Object obj) {
        onSuccess(((Number) obj).longValue());
    }

    public void onSuccess(long j) {
        if (this.this$0.getActivity() != null) {
            Preference preference = this.$it;
            Intrinsics.checkExpressionValueIsNotNull(preference, "it");
            preference.setSummary(Formatter.formatFileSize(this.this$0.getActivity(), j));
        }
    }
}
