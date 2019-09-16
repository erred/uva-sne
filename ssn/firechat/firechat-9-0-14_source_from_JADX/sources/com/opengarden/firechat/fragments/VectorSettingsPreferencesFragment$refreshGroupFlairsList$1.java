package com.opengarden.firechat.fragments;

import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000-\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0014\u0010\t\u001a\u00020\u00062\n\u0010\u0007\u001a\u00060\nj\u0002`\u000bH\u0016J\u0016\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u0016J\u0014\u0010\u000e\u001a\u00020\u00062\n\u0010\u0007\u001a\u00060\nj\u0002`\u000bH\u0016¨\u0006\u000f"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$refreshGroupFlairsList$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "", "", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "publicisedGroups", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$refreshGroupFlairsList$1 implements ApiCallback<Set<? extends String>> {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
    }

    VectorSettingsPreferencesFragment$refreshGroupFlairsList$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onSuccess(@NotNull Set<String> set) {
        Intrinsics.checkParameterIsNotNull(set, "publicisedGroups");
        if (set.isEmpty()) {
            VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(this.this$0.getActivity());
            vectorCustomActionEditTextPreference.setTitle(this.this$0.getResources().getString(C1299R.string.settings_without_flair));
            this.this$0.getMGroupsFlairCategory().addPreference(vectorCustomActionEditTextPreference);
            return;
        }
        this.this$0.buildGroupsList(set);
    }
}
