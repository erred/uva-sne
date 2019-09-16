package com.opengarden.firechat.fragments;

import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onActivityResult$1$onUploadComplete$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2284x719768b0 implements Runnable {
    final /* synthetic */ String $contentUri;
    final /* synthetic */ VectorSettingsPreferencesFragment$onActivityResult$1 this$0;

    C2284x719768b0(VectorSettingsPreferencesFragment$onActivityResult$1 vectorSettingsPreferencesFragment$onActivityResult$1, String str) {
        this.this$0 = vectorSettingsPreferencesFragment$onActivityResult$1;
        this.$contentUri = str;
    }

    public final void run() {
        VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).getMyUser().updateAvatarUrl(this.$contentUri, new ApiCallback<Void>(this) {
            final /* synthetic */ C2284x719768b0 this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@Nullable Void voidR) {
                this.this$0.this$0.this$0.onCommonDone(null);
                this.this$0.this$0.this$0.refreshDisplay();
            }

            public void onNetworkError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(@NotNull MatrixError matrixError) {
                Intrinsics.checkParameterIsNotNull(matrixError, "e");
                if (!Intrinsics.areEqual((Object) MatrixError.M_CONSENT_NOT_GIVEN, (Object) matrixError.errcode)) {
                    this.this$0.this$0.this$0.onCommonDone(matrixError.getLocalizedMessage());
                } else if (this.this$0.this$0.this$0.getActivity() != null) {
                    this.this$0.this$0.this$0.getActivity().runOnUiThread(new C2286xbc68688e(this, matrixError));
                }
            }

            public void onUnexpectedError(@NotNull Exception exc) {
                Intrinsics.checkParameterIsNotNull(exc, "e");
                this.this$0.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
            }
        });
    }
}
