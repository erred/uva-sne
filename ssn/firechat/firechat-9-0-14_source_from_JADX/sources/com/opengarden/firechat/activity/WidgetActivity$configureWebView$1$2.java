package com.opengarden.firechat.activity;

import android.os.Handler;
import android.os.Looper;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, mo21251d2 = {"com/opengarden/firechat/activity/WidgetActivity$configureWebView$1$2", "Landroid/webkit/WebChromeClient;", "()V", "onPermissionRequest", "", "request", "Landroid/webkit/PermissionRequest;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: WidgetActivity.kt */
public final class WidgetActivity$configureWebView$1$2 extends WebChromeClient {
    WidgetActivity$configureWebView$1$2() {
    }

    public void onPermissionRequest(@NotNull PermissionRequest permissionRequest) {
        Intrinsics.checkParameterIsNotNull(permissionRequest, "request");
        new Handler(Looper.getMainLooper()).post(new WidgetActivity$configureWebView$1$2$onPermissionRequest$1(permissionRequest));
    }
}
