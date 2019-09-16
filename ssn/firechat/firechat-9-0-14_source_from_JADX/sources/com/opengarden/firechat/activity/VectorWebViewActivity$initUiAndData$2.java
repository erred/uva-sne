package com.opengarden.firechat.activity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\t"}, mo21251d2 = {"com/opengarden/firechat/activity/VectorWebViewActivity$initUiAndData$2", "Landroid/webkit/WebChromeClient;", "(Lcom/opengarden/firechat/activity/VectorWebViewActivity;I)V", "onReceivedTitle", "", "view", "Landroid/webkit/WebView;", "title", "", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorWebViewActivity.kt */
public final class VectorWebViewActivity$initUiAndData$2 extends WebChromeClient {
    final /* synthetic */ int $titleRes;
    final /* synthetic */ VectorWebViewActivity this$0;

    VectorWebViewActivity$initUiAndData$2(VectorWebViewActivity vectorWebViewActivity, int i) {
        this.this$0 = vectorWebViewActivity;
        this.$titleRes = i;
    }

    public void onReceivedTitle(@NotNull WebView webView, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, "title");
        if (this.$titleRes == -1) {
            this.this$0.setTitle(str);
        }
    }
}
