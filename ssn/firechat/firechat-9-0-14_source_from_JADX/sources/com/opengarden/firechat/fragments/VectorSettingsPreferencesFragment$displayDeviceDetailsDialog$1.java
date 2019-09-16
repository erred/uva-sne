package com.opengarden.firechat.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$1 implements OnClickListener {
    final /* synthetic */ DeviceInfo $aDeviceInfo;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, DeviceInfo deviceInfo) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$aDeviceInfo = deviceInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.this$0.displayDeviceRenameDialog(this.$aDeviceInfo);
    }
}
