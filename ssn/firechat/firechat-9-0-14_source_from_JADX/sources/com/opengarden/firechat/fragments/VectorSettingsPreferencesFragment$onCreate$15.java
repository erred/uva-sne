package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$15 implements OnPreferenceClickListener {
    final /* synthetic */ Context $appContext;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$15(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, Context context) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$appContext = context;
    }

    public final boolean onPreferenceClick(Preference preference) {
        if (this.this$0.getActivity() != null) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.addFlags(ErrorDialogData.BINDER_CRASH);
            Context context = this.$appContext;
            Intrinsics.checkExpressionValueIsNotNull(context, "appContext");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            Activity activity = this.this$0.getActivity();
            Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
            activity.getApplicationContext().startActivity(intent);
        }
        return true;
    }
}
