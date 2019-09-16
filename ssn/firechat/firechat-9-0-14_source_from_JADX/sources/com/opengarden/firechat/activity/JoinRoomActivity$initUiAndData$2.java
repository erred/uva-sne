package com.opengarden.firechat.activity;

import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0014\u0010\b\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016J\u0012\u0010\u000b\u001a\u00020\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016J\u0014\u0010\r\u001a\u00020\u00052\n\u0010\u0006\u001a\u00060\tj\u0002`\nH\u0016¨\u0006\u000e"}, mo21251d2 = {"com/opengarden/firechat/activity/JoinRoomActivity$initUiAndData$2", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "Ljava/lang/Void;", "()V", "onMatrixError", "", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: JoinRoomActivity.kt */
public final class JoinRoomActivity$initUiAndData$2 implements ApiCallback<Void> {
    JoinRoomActivity$initUiAndData$2() {
    }

    public void onSuccess(@Nullable Void voidR) {
        Log.m209d(JoinRoomActivity.Companion.getLOG_TAG(), "## onCreate() : reject succeeds");
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String access$getLOG_TAG$p = JoinRoomActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## onCreate() : reject fails ");
        sb.append(exc.getMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        String access$getLOG_TAG$p = JoinRoomActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## onCreate() : reject fails ");
        sb.append(matrixError.getLocalizedMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        String access$getLOG_TAG$p = JoinRoomActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## onCreate() : reject fails ");
        sb.append(exc.getMessage());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
    }
}
