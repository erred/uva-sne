package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000#\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0016J$\u0010\b\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0006H\u0016¨\u0006\f"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$onActivityResult$1", "Lcom/opengarden/firechat/matrixsdk/listeners/MXMediaUploadListener;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onUploadComplete", "", "uploadId", "", "contentUri", "onUploadError", "serverResponseCode", "", "serverErrorMessage", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$onActivityResult$1 extends MXMediaUploadListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onActivityResult$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onUploadError(@Nullable String str, int i, @Nullable String str2) {
        this.this$0.getActivity().runOnUiThread(new C2287xb075f7ab(this, i, str2));
    }

    public void onUploadComplete(@Nullable String str, @Nullable String str2) {
        this.this$0.getActivity().runOnUiThread(new C2284x719768b0(this, str2));
    }
}
