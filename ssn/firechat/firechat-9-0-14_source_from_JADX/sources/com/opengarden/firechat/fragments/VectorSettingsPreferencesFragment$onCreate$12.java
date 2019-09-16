package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValueAsVoid", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$12 implements OnPreferenceChangeListener {
    final /* synthetic */ Preference $cryptoIsEnabledPref;
    final /* synthetic */ CheckBoxPreference $useCryptoPref;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$12(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, CheckBoxPreference checkBoxPreference, Preference preference) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$useCryptoPref = checkBoxPreference;
        this.$cryptoIsEnabledPref = preference;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.isEmpty(VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getCredentials().deviceId)) {
            new Builder(this.this$0.getActivity()).setMessage(C1299R.string.room_settings_labs_end_to_end_warnings).setPositiveButton(C1299R.string.logout, new OnClickListener(this) {
                final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$12 this$0;

                {
                    this.this$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    CommonActivityUtils.logout(this.this$0.this$0.getActivity());
                }
            }).setNegativeButton(C1299R.string.cancel, new OnClickListener(this) {
                final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$12 this$0;

                {
                    this.this$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    this.this$0.$useCryptoPref.setChecked(false);
                }
            }).setOnCancelListener(new OnCancelListener(this) {
                final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$12 this$0;

                {
                    this.this$0 = r1;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    this.this$0.$useCryptoPref.setChecked(false);
                }
            }).create().show();
        } else if (obj == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Boolean");
        } else {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).isCryptoEnabled() != booleanValue) {
                this.this$0.displayLoadingView();
                VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).enableCrypto(booleanValue, new ApiCallback<Void>(this) {
                    final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$12 this$0;

                    {
                        this.this$0 = r1;
                    }

                    private final void refresh() {
                        if (this.this$0.this$0.getActivity() != null) {
                            this.this$0.this$0.getActivity().runOnUiThread(new VectorSettingsPreferencesFragment$onCreate$12$4$refresh$1(this));
                        }
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        this.this$0.$useCryptoPref.setEnabled(false);
                        refresh();
                    }

                    public void onNetworkError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        this.this$0.$useCryptoPref.setChecked(false);
                    }

                    public void onMatrixError(@NotNull MatrixError matrixError) {
                        Intrinsics.checkParameterIsNotNull(matrixError, "e");
                        this.this$0.$useCryptoPref.setChecked(false);
                    }

                    public void onUnexpectedError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        this.this$0.$useCryptoPref.setChecked(false);
                    }
                });
            }
        }
        return true;
    }
}
