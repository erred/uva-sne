package com.opengarden.firechat.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.common.internal.ImagesContract;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\"\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0016J \u0010\u0010\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0017J(\u0010\u0010\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\fH\u0016J\u0010\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0018\u0010\u001a\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0017J\u0018\u0010\u001a\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/VectorWebViewClient;", "Landroid/webkit/WebViewClient;", "eventListener", "Lcom/opengarden/firechat/webview/WebViewEventListener;", "(Lcom/opengarden/firechat/webview/WebViewEventListener;)V", "mInError", "", "onPageFinished", "", "view", "Landroid/webkit/WebView;", "url", "", "onPageStarted", "favicon", "Landroid/graphics/Bitmap;", "onReceivedError", "request", "Landroid/webkit/WebResourceRequest;", "error", "Landroid/webkit/WebResourceError;", "errorCode", "", "description", "failingUrl", "shouldOverrideUrl", "shouldOverrideUrlLoading", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorWebViewClient.kt */
public final class VectorWebViewClient extends WebViewClient {
    private final WebViewEventListener eventListener;
    private boolean mInError;

    public VectorWebViewClient(@NotNull WebViewEventListener webViewEventListener) {
        Intrinsics.checkParameterIsNotNull(webViewEventListener, "eventListener");
        this.eventListener = webViewEventListener;
    }

    @TargetApi(24)
    public boolean shouldOverrideUrlLoading(@NotNull WebView webView, @NotNull WebResourceRequest webResourceRequest) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(webResourceRequest, "request");
        String uri = webResourceRequest.getUrl().toString();
        Intrinsics.checkExpressionValueIsNotNull(uri, "request.url.toString()");
        return shouldOverrideUrl(uri);
    }

    public boolean shouldOverrideUrlLoading(@NotNull WebView webView, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        return shouldOverrideUrl(str);
    }

    public void onPageStarted(@NotNull WebView webView, @NotNull String str, @Nullable Bitmap bitmap) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        super.onPageStarted(webView, str, bitmap);
        this.mInError = false;
        this.eventListener.onPageStarted(str);
    }

    public void onPageFinished(@NotNull WebView webView, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        super.onPageFinished(webView, str);
        if (!this.mInError) {
            this.eventListener.onPageFinished(str);
        }
    }

    public void onReceivedError(@NotNull WebView webView, int i, @NotNull String str, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(str, "description");
        Intrinsics.checkParameterIsNotNull(str2, "failingUrl");
        super.onReceivedError(webView, i, str, str2);
        if (!this.mInError) {
            this.mInError = true;
            this.eventListener.onPageError(str2, i, str);
        }
    }

    @TargetApi(24)
    public void onReceivedError(@NotNull WebView webView, @NotNull WebResourceRequest webResourceRequest, @NotNull WebResourceError webResourceError) {
        Intrinsics.checkParameterIsNotNull(webView, "view");
        Intrinsics.checkParameterIsNotNull(webResourceRequest, "request");
        Intrinsics.checkParameterIsNotNull(webResourceError, "error");
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        if (!this.mInError) {
            this.mInError = true;
            WebViewEventListener webViewEventListener = this.eventListener;
            String uri = webResourceRequest.getUrl().toString();
            Intrinsics.checkExpressionValueIsNotNull(uri, "request.url.toString()");
            webViewEventListener.onPageError(uri, webResourceError.getErrorCode(), webResourceError.getDescription().toString());
        }
    }

    private final boolean shouldOverrideUrl(String str) {
        this.mInError = false;
        boolean shouldOverrideUrlLoading = this.eventListener.shouldOverrideUrlLoading(str);
        if (!shouldOverrideUrlLoading) {
            this.eventListener.pageWillStart(str);
        }
        return shouldOverrideUrlLoading;
    }
}
