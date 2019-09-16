package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.data.Pusher;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference.OnPreferenceLongClickListener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceLongClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$refreshPushersList$1 implements OnPreferenceLongClickListener {
    final /* synthetic */ GcmRegistrationManager $gcmRegistrationManager;
    final /* synthetic */ Pusher $pusher;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$refreshPushersList$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, GcmRegistrationManager gcmRegistrationManager, Pusher pusher) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$gcmRegistrationManager = gcmRegistrationManager;
        this.$pusher = pusher;
    }

    public final boolean onPreferenceLongClick(Preference preference) {
        new Builder(this.this$0.getActivity()).setTitle(C1299R.string.dialog_title_confirmation).setMessage(this.this$0.getString(C1299R.string.settings_delete_notification_targets_confirmation)).setPositiveButton(C1299R.string.remove, new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$refreshPushersList$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                this.this$0.this$0.displayLoadingView();
                this.this$0.$gcmRegistrationManager.unregister(VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0), this.this$0.$pusher, new ApiCallback<Void>(this) {
                    final /* synthetic */ C23161 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        this.this$0.this$0.this$0.refreshPushersList();
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
        }).setNegativeButton(C1299R.string.cancel, C23182.INSTANCE).create().show();
        return true;
    }
}
