package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$refreshIgnoredUsersList$2 implements OnPreferenceClickListener {
    final /* synthetic */ String $userId;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$refreshIgnoredUsersList$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, String str) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$userId = str;
    }

    public final boolean onPreferenceClick(Preference preference) {
        new Builder(this.this$0.getActivity()).setMessage(this.this$0.getString(C1299R.string.settings_unignore_user, new Object[]{this.$userId})).setPositiveButton(C1299R.string.yes, new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$refreshIgnoredUsersList$2 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                this.this$0.this$0.displayLoadingView();
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.this$0.$userId);
                VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).unIgnoreUsers(arrayList, new ApiCallback<Void>(this) {
                    final /* synthetic */ C23131 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        this.this$0.this$0.this$0.onCommonDone(null);
                    }

                    public void onNetworkError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        this.this$0.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(@NotNull MatrixError matrixError) {
                        Intrinsics.checkParameterIsNotNull(matrixError, "e");
                        this.this$0.this$0.this$0.onCommonDone(matrixError.getLocalizedMessage());
                    }

                    public void onUnexpectedError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        this.this$0.this$0.this$0.onCommonDone(exc.getLocalizedMessage());
                    }
                });
            }
        }).setNegativeButton(C1299R.string.f114no, C23152.INSTANCE).create().show();
        return false;
    }
}
