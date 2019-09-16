package com.opengarden.firechat.webview;

import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006H\u0016J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u000f"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/DefaultWebViewEventListener;", "Lcom/opengarden/firechat/webview/WebViewEventListener;", "()V", "onPageError", "", "url", "", "errorCode", "", "description", "onPageFinished", "onPageStarted", "pageWillStart", "shouldOverrideUrlLoading", "", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: DefaultWebViewEventListener.kt */
public final class DefaultWebViewEventListener implements WebViewEventListener {
    public void pageWillStart(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        StringBuilder sb = new StringBuilder();
        sb.append("On page will start: ");
        sb.append(str);
        Log.m215v("DefaultWebViewEventListener", sb.toString());
    }

    public void onPageStarted(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        StringBuilder sb = new StringBuilder();
        sb.append("On page started: ");
        sb.append(str);
        Log.m209d("DefaultWebViewEventListener", sb.toString());
    }

    public void onPageFinished(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        StringBuilder sb = new StringBuilder();
        sb.append("On page finished: ");
        sb.append(str);
        Log.m209d("DefaultWebViewEventListener", sb.toString());
    }

    public void onPageError(@NotNull String str, int i, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        Intrinsics.checkParameterIsNotNull(str2, "description");
        StringBuilder sb = new StringBuilder();
        sb.append("On received error: ");
        sb.append(str);
        sb.append(" - errorCode: ");
        sb.append(i);
        sb.append(" - message: ");
        sb.append(str2);
        Log.m211e("DefaultWebViewEventListener", sb.toString());
    }

    public boolean shouldOverrideUrlLoading(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        StringBuilder sb = new StringBuilder();
        sb.append("Should override url: ");
        sb.append(str);
        Log.m215v("DefaultWebViewEventListener", sb.toString());
        return false;
    }
}
