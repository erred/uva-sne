package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$displayDeviceDeletionDialog$1 implements OnClickListener {
    final /* synthetic */ DeviceInfo $aDeviceInfoToDelete;
    final /* synthetic */ EditText $passwordEditText;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$displayDeviceDeletionDialog$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, EditText editText, DeviceInfo deviceInfo) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$passwordEditText = editText;
        this.$aDeviceInfoToDelete = deviceInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        if (TextUtils.isEmpty(this.$passwordEditText.toString())) {
            Activity activity = this.this$0.getActivity();
            Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
            Context applicationContext = activity.getApplicationContext();
            Intrinsics.checkExpressionValueIsNotNull(applicationContext, "activity.applicationContext");
            Toast makeText = Toast.makeText(applicationContext, "Password missing..", 0);
            makeText.show();
            Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, tex…uration).apply { show() }");
            return;
        }
        VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this.this$0;
        EditText editText = this.$passwordEditText;
        Intrinsics.checkExpressionValueIsNotNull(editText, "passwordEditText");
        vectorSettingsPreferencesFragment.mAccountPassword = editText.getText().toString();
        VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment2 = this.this$0;
        String str = this.$aDeviceInfoToDelete.device_id;
        Intrinsics.checkExpressionValueIsNotNull(str, "aDeviceInfoToDelete.device_id");
        vectorSettingsPreferencesFragment2.deleteDevice(str);
    }
}
