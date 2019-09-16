package com.opengarden.firechat.webview;

import com.opengarden.firechat.activity.RiotAppCompatActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u0007\b\u0002¢\u0006\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/WebViewMode;", "", "Lcom/opengarden/firechat/webview/WebViewEventListenerFactory;", "(Ljava/lang/String;I)V", "DEFAULT", "CONSENT", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: WebViewMode.kt */
public enum WebViewMode implements WebViewEventListenerFactory {
    ;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0001\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/WebViewMode$CONSENT;", "Lcom/opengarden/firechat/webview/WebViewMode;", "(Ljava/lang/String;I)V", "eventListener", "Lcom/opengarden/firechat/webview/WebViewEventListener;", "activity", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: WebViewMode.kt */
    static final class CONSENT extends WebViewMode {
        CONSENT(String str, int i) {
            super(str, i);
        }

        @NotNull
        public WebViewEventListener eventListener(@NotNull RiotAppCompatActivity riotAppCompatActivity) {
            Intrinsics.checkParameterIsNotNull(riotAppCompatActivity, "activity");
            return new ConsentWebViewEventListener(riotAppCompatActivity, new DefaultWebViewEventListener());
        }
    }

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0001\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, mo21251d2 = {"Lcom/opengarden/firechat/webview/WebViewMode$DEFAULT;", "Lcom/opengarden/firechat/webview/WebViewMode;", "(Ljava/lang/String;I)V", "eventListener", "Lcom/opengarden/firechat/webview/WebViewEventListener;", "activity", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: WebViewMode.kt */
    static final class DEFAULT extends WebViewMode {
        DEFAULT(String str, int i) {
            super(str, i);
        }

        @NotNull
        public WebViewEventListener eventListener(@NotNull RiotAppCompatActivity riotAppCompatActivity) {
            Intrinsics.checkParameterIsNotNull(riotAppCompatActivity, "activity");
            return new DefaultWebViewEventListener();
        }
    }
}
