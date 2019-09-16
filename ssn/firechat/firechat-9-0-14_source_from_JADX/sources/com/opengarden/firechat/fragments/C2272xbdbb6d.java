package com.opengarden.firechat.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "dialog", "Landroid/content/DialogInterface;", "kotlin.jvm.PlatformType", "which", "", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$displayDelete3PIDConfirmationDialog$1 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2272xbdbb6d implements OnClickListener {
    final /* synthetic */ ThirdPartyIdentifier $pid;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2272xbdbb6d(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, ThirdPartyIdentifier thirdPartyIdentifier) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$pid = thirdPartyIdentifier;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.this$0.displayLoadingView();
        VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getMyUser().delete3Pid(this.$pid, new ApiCallback<Void>(this) {
            final /* synthetic */ C2272xbdbb6d this$0;

            {
                this.this$0 = r1;
            }

            public void onSuccess(@Nullable Void voidR) {
                String str = this.this$0.$pid.medium;
                if (str != null) {
                    int hashCode = str.hashCode();
                    if (hashCode != -1064943142) {
                        if (hashCode == 96619420 && str.equals("email")) {
                            this.this$0.this$0.refreshEmailsList();
                        }
                    } else if (str.equals(ThreePid.MEDIUM_MSISDN)) {
                        this.this$0.this$0.refreshPhoneNumbersList();
                    }
                }
                this.this$0.this$0.onCommonDone(null);
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
