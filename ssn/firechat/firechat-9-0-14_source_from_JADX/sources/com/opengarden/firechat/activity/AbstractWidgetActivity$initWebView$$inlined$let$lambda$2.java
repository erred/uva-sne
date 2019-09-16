package com.opengarden.firechat.activity;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.AssetReader;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J&\u0010\t\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016¨\u0006\f"}, mo21251d2 = {"com/opengarden/firechat/activity/AbstractWidgetActivity$initWebView$1$3", "Landroid/webkit/WebViewClient;", "(Lcom/opengarden/firechat/activity/AbstractWidgetActivity$initWebView$1;)V", "onPageFinished", "", "view", "Landroid/webkit/WebView;", "url", "", "onPageStarted", "favicon", "Landroid/graphics/Bitmap;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: AbstractWidgetActivity.kt */
public final class AbstractWidgetActivity$initWebView$$inlined$let$lambda$2 extends WebViewClient {
    final /* synthetic */ AbstractWidgetActivity this$0;

    AbstractWidgetActivity$initWebView$$inlined$let$lambda$2(AbstractWidgetActivity abstractWidgetActivity) {
        this.this$0 = abstractWidgetActivity;
    }

    public void onPageStarted(@Nullable WebView webView, @Nullable String str, @Nullable Bitmap bitmap) {
        String access$getLOG_TAG$p = AbstractWidgetActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## onPageStarted - Url: ");
        sb.append(str);
        Log.m209d(access$getLOG_TAG$p, sb.toString());
        this.this$0.showWaitingView();
    }

    public void onPageFinished(@NotNull WebView webView, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        if (!this.this$0.isDestroyed()) {
            this.this$0.hideWaitingView();
            final String readAssetFile = AssetReader.INSTANCE.readAssetFile(this.this$0, "postMessageAPI.js");
            if (readAssetFile != null) {
                this.this$0.runOnUiThread(new Runnable(this) {
                    final /* synthetic */ AbstractWidgetActivity$initWebView$$inlined$let$lambda$2 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public final void run() {
                        WebView mWebView = this.this$0.this$0.getMWebView();
                        StringBuilder sb = new StringBuilder();
                        sb.append("javascript:");
                        sb.append(readAssetFile);
                        mWebView.loadUrl(sb.toString());
                    }
                });
            }
        }
    }
}
