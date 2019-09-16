package com.opengarden.firechat.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$importKeys$2 implements OnClickListener {
    final /* synthetic */ Context $appContext;
    final /* synthetic */ AlertDialog $importDialog;
    final /* synthetic */ TextInputEditText $passPhraseEditText;
    final /* synthetic */ RoomMediaMessage $sharedDataItem;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$importKeys$2(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, TextInputEditText textInputEditText, Context context, RoomMediaMessage roomMediaMessage, AlertDialog alertDialog) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$passPhraseEditText = textInputEditText;
        this.$appContext = context;
        this.$sharedDataItem = roomMediaMessage;
        this.$importDialog = alertDialog;
    }

    public final void onClick(View view) {
        TextInputEditText textInputEditText = this.$passPhraseEditText;
        Intrinsics.checkExpressionValueIsNotNull(textInputEditText, "passPhraseEditText");
        String obj = textInputEditText.getText().toString();
        Context context = this.$appContext;
        RoomMediaMessage roomMediaMessage = this.$sharedDataItem;
        Intrinsics.checkExpressionValueIsNotNull(roomMediaMessage, "sharedDataItem");
        Resource openResource = ResourceUtils.openResource(context, roomMediaMessage.getUri(), this.$sharedDataItem.getMimeType(this.$appContext));
        try {
            byte[] bArr = new byte[openResource.mContentStream.available()];
            openResource.mContentStream.read(bArr);
            openResource.mContentStream.close();
            this.this$0.displayLoadingView();
            VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getCrypto().importRoomKeys(bArr, obj, new ApiCallback<Void>(this) {
                final /* synthetic */ VectorSettingsPreferencesFragment$importKeys$2 this$0;

                {
                    this.this$0 = r1;
                }

                public void onSuccess(@Nullable Void voidR) {
                    this.this$0.this$0.hideLoadingView();
                }

                public void onNetworkError(@NotNull Exception exc) {
                    Intrinsics.checkParameterIsNotNull(exc, "e");
                    Toast.makeText(this.this$0.$appContext, exc.getLocalizedMessage(), 0).show();
                    this.this$0.this$0.hideLoadingView();
                }

                public void onMatrixError(@NotNull MatrixError matrixError) {
                    Intrinsics.checkParameterIsNotNull(matrixError, "e");
                    Toast.makeText(this.this$0.$appContext, matrixError.getLocalizedMessage(), 0).show();
                    this.this$0.this$0.hideLoadingView();
                }

                public void onUnexpectedError(@NotNull Exception exc) {
                    Intrinsics.checkParameterIsNotNull(exc, "e");
                    Toast.makeText(this.this$0.$appContext, exc.getLocalizedMessage(), 0).show();
                    this.this$0.this$0.hideLoadingView();
                }
            });
            this.$importDialog.dismiss();
        } catch (Exception e) {
            try {
                openResource.mContentStream.close();
            } catch (Exception e2) {
                String access$getLOG_TAG$p = VectorSettingsPreferencesFragment.Companion.getLOG_TAG();
                StringBuilder sb = new StringBuilder();
                sb.append("## importKeys() : ");
                sb.append(e2.getMessage());
                Log.m211e(access$getLOG_TAG$p, sb.toString());
            }
            Toast.makeText(this.$appContext, e.getLocalizedMessage(), 0).show();
        }
    }
}
