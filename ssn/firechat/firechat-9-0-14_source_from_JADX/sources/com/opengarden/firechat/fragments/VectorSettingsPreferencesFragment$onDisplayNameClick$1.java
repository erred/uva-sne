package com.opengarden.firechat.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0012\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onDisplayNameClick$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Ljava/lang/Void;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;Ljava/lang/String;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$onDisplayNameClick$1 implements ApiCallback<Void> {
    final /* synthetic */ String $value;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onDisplayNameClick$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, String str) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$value = str;
    }

    public void onSuccess(@Nullable Void voidR) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.this$0.getActivity());
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…aredPreferences(activity)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        edit.putString(PreferencesManager.SETTINGS_DISPLAY_NAME_PREFERENCE_KEY, this.$value);
        edit.apply();
        this.this$0.onCommonDone(null);
        this.this$0.refreshDisplay();
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.onCommonDone(exc.getLocalizedMessage());
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        if (!Intrinsics.areEqual((Object) MatrixError.M_CONSENT_NOT_GIVEN, (Object) matrixError.errcode)) {
            this.this$0.onCommonDone(matrixError.getLocalizedMessage());
        } else if (this.this$0.getActivity() != null) {
            this.this$0.getActivity().runOnUiThread(new C2294xa456cefa(this, matrixError));
        }
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        this.this$0.onCommonDone(exc.getLocalizedMessage());
    }
}
