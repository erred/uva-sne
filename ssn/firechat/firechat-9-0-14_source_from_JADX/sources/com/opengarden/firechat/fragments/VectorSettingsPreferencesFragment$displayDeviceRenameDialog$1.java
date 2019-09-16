package com.opengarden.firechat.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.text.TextUtils;
import android.widget.EditText;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$displayDeviceRenameDialog$1 implements OnClickListener {
    final /* synthetic */ DeviceInfo $aDeviceInfoToRename;
    final /* synthetic */ EditText $input;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$displayDeviceRenameDialog$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, DeviceInfo deviceInfo, EditText editText) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$aDeviceInfoToRename = deviceInfo;
        this.$input = editText;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.this$0.displayLoadingView();
        VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).setDeviceName(this.$aDeviceInfoToRename.device_id, this.$input.getText().toString(), new ApiCallback<Void>(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$displayDeviceRenameDialog$1 this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@Nullable Void voidR) {
                int preferenceCount = this.this$0.this$0.getMDevicesListSettingsCategory().getPreferenceCount();
                for (int i = 0; i < preferenceCount; i++) {
                    Preference preference = this.this$0.this$0.getMDevicesListSettingsCategory().getPreference(i);
                    if (preference == null) {
                        throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.VectorCustomActionEditTextPreference");
                    }
                    VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = (VectorCustomActionEditTextPreference) preference;
                    if (TextUtils.equals(this.this$0.$aDeviceInfoToRename.device_id, vectorCustomActionEditTextPreference.getTitle())) {
                        vectorCustomActionEditTextPreference.setSummary(this.this$0.$input.getText());
                    }
                }
                Preference findPreference = this.this$0.this$0.findPreference(PreferencesManager.SETTINGS_ENCRYPTION_INFORMATION_DEVICE_ID_PREFERENCE_KEY);
                Intrinsics.checkExpressionValueIsNotNull(findPreference, "pref");
                if (TextUtils.equals(findPreference.getSummary(), this.this$0.$aDeviceInfoToRename.device_id)) {
                    Preference findPreference2 = this.this$0.this$0.findPreference(PreferencesManager.SETTINGS_ENCRYPTION_INFORMATION_DEVICE_NAME_PREFERENCE_KEY);
                    Intrinsics.checkExpressionValueIsNotNull(findPreference2, "findPreference(Preferenc…VICE_NAME_PREFERENCE_KEY)");
                    findPreference2.setSummary(this.this$0.$input.getText());
                }
                this.this$0.this$0.hideLoadingView();
            }

            public void onNetworkError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(@NotNull MatrixError matrixError) {
                Intrinsics.checkParameterIsNotNull(matrixError, "e");
                this.this$0.this$0.onCommonDone(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }
        });
    }
}
