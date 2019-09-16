package com.opengarden.firechat.activity;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J&\u0010\t\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016¨\u0006\f"}, mo21251d2 = {"com/opengarden/firechat/activity/WidgetActivity$configureWebView$1$3", "Landroid/webkit/WebViewClient;", "(Lcom/opengarden/firechat/activity/WidgetActivity$configureWebView$1;)V", "onPageFinished", "", "view", "Landroid/webkit/WebView;", "url", "", "onPageStarted", "favicon", "Landroid/graphics/Bitmap;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: WidgetActivity.kt */
public final class WidgetActivity$configureWebView$$inlined$let$lambda$1 extends WebViewClient {
    final /* synthetic */ WidgetActivity this$0;

    WidgetActivity$configureWebView$$inlined$let$lambda$1(WidgetActivity widgetActivity) {
        this.this$0 = widgetActivity;
    }

    public void onPageStarted(@Nullable WebView webView, @Nullable String str, @Nullable Bitmap bitmap) {
        this.this$0.showWaitingView();
    }

    public void onPageFinished(@Nullable WebView webView, @Nullable String str) {
        this.this$0.hideWaitingView();
    }
}
