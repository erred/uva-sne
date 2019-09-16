package com.opengarden.firechat.activity;

import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000%\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b"}, mo21251d2 = {"com/opengarden/firechat/activity/AbstractWidgetActivity$initWebView$1$1", "Landroid/webkit/WebChromeClient;", "(Lcom/opengarden/firechat/activity/AbstractWidgetActivity$initWebView$1;)V", "onConsoleMessage", "", "consoleMessage", "Landroid/webkit/ConsoleMessage;", "onPermissionRequest", "", "request", "Landroid/webkit/PermissionRequest;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: AbstractWidgetActivity.kt */
public final class AbstractWidgetActivity$initWebView$$inlined$let$lambda$1 extends WebChromeClient {
    final /* synthetic */ AbstractWidgetActivity this$0;

    AbstractWidgetActivity$initWebView$$inlined$let$lambda$1(AbstractWidgetActivity abstractWidgetActivity) {
        this.this$0 = abstractWidgetActivity;
    }

    public void onPermissionRequest(@NotNull final PermissionRequest permissionRequest) {
        Intrinsics.checkParameterIsNotNull(permissionRequest, "request");
        this.this$0.runOnUiThread(new Runnable() {
            public final void run() {
                permissionRequest.grant(permissionRequest.getResources());
            }
        });
    }

    public boolean onConsoleMessage(@NotNull ConsoleMessage consoleMessage) {
        Intrinsics.checkParameterIsNotNull(consoleMessage, "consoleMessage");
        String access$getLOG_TAG$p = AbstractWidgetActivity.Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## onConsoleMessage() : ");
        sb.append(consoleMessage.message());
        sb.append(" line ");
        sb.append(consoleMessage.lineNumber());
        sb.append(" source Id ");
        sb.append(consoleMessage.sourceId());
        Log.m211e(access$getLOG_TAG$p, sb.toString());
        return super.onConsoleMessage(consoleMessage);
    }
}
