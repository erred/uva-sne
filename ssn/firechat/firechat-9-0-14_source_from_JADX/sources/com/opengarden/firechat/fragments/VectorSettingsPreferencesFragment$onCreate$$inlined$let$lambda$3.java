package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.preference.VectorSwitchPreference;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "preference", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$3 implements OnPreferenceChangeListener {
    final /* synthetic */ VectorSwitchPreference $it;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$3(VectorSwitchPreference vectorSwitchPreference, VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.$it = vectorSwitchPreference;
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if (obj != null) {
            Boolean bool = (Boolean) obj;
            if (bool.booleanValue() != VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).isURLPreviewEnabled()) {
                this.this$0.displayLoadingView();
                VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).setURLPreviewStatus(bool.booleanValue(), new ApiCallback<Void>(this) {
                    final /* synthetic */ VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$3 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        this.this$0.$it.setChecked(VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).isURLPreviewEnabled());
                        this.this$0.this$0.hideLoadingView();
                    }

                    private final void onError(String str) {
                        if (this.this$0.this$0.getActivity() != null) {
                            Toast.makeText(this.this$0.this$0.getActivity(), str, 0).show();
                        }
                        onSuccess((Void) null);
                    }

                    public void onNetworkError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        String localizedMessage = exc.getLocalizedMessage();
                        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
                        onError(localizedMessage);
                    }

                    public void onMatrixError(@NotNull MatrixError matrixError) {
                        Intrinsics.checkParameterIsNotNull(matrixError, "e");
                        String localizedMessage = matrixError.getLocalizedMessage();
                        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
                        onError(localizedMessage);
                    }

                    public void onUnexpectedError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        String localizedMessage = exc.getLocalizedMessage();
                        Intrinsics.checkExpressionValueIsNotNull(localizedMessage, "e.localizedMessage");
                        onError(localizedMessage);
                    }
                });
            }
        }
        return false;
    }
}
