package com.opengarden.firechat.webview;

import com.opengarden.firechat.activity.RiotAppCompatActivity;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000+\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0002H\u0002J\u0010\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0016J\u0014\u0010\n\u001a\u00020\u00052\n\u0010\b\u001a\u00060\u000bj\u0002`\fH\u0016J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\u0014\u0010\u000f\u001a\u00020\u00052\n\u0010\b\u001a\u00060\u000bj\u0002`\fH\u0016¨\u0006\u0010"}, mo21251d2 = {"com/opengarden/firechat/webview/ConsentWebViewEventListener$createRiotBotRoomCallback$1", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "", "(Lcom/opengarden/firechat/webview/ConsentWebViewEventListener;)V", "onError", "", "error", "onMatrixError", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ConsentWebViewEventListener.kt */
public final class ConsentWebViewEventListener$createRiotBotRoomCallback$1 implements ApiCallback<String> {
    final /* synthetic */ ConsentWebViewEventListener this$0;

    ConsentWebViewEventListener$createRiotBotRoomCallback$1(ConsentWebViewEventListener consentWebViewEventListener) {
        this.this$0 = consentWebViewEventListener;
    }

    public void onSuccess(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "info");
        Log.m209d("ConsentWebViewEventListener", "## On success : succeed to invite riot-bot");
        RiotAppCompatActivity access$getSafeActivity$p = this.this$0.getSafeActivity();
        if (access$getSafeActivity$p != null) {
            access$getSafeActivity$p.finish();
        }
    }

    private final void onError(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("## On error : failed  to invite riot-bot ");
        sb.append(str);
        Log.m211e("ConsentWebViewEventListener", sb.toString());
        RiotAppCompatActivity access$getSafeActivity$p = this.this$0.getSafeActivity();
        if (access$getSafeActivity$p != null) {
            access$getSafeActivity$p.finish();
        }
    }

    public void onNetworkError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        onError(exc.getMessage());
    }

    public void onMatrixError(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "e");
        onError(matrixError.getMessage());
    }

    public void onUnexpectedError(@NotNull Exception exc) {
        Intrinsics.checkParameterIsNotNull(exc, "e");
        onError(exc.getMessage());
    }
}
