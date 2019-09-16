package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.annotation.StringRes;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import butterknife.BindView;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.webview.VectorWebViewClient;
import com.opengarden.firechat.webview.WebViewEventListener;
import com.opengarden.firechat.webview.WebViewMode;
import java.io.Serializable;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\fH\u0016R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006\u000f"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/VectorWebViewActivity;", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "()V", "webView", "Landroid/webkit/WebView;", "getWebView", "()Landroid/webkit/WebView;", "setWebView", "(Landroid/webkit/WebView;)V", "getLayoutRes", "", "initUiAndData", "", "onBackPressed", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorWebViewActivity.kt */
public final class VectorWebViewActivity extends RiotAppCompatActivity {
    public static final Companion Companion = new Companion(null);
    private static final String EXTRA_MODE = "EXTRA_MODE";
    private static final String EXTRA_TITLE_RES_ID = "EXTRA_TITLE_RES_ID";
    private static final String EXTRA_URL = "EXTRA_URL";
    private static final int INVALID_RES_ID = -1;
    @NotNull
    @BindView(2131297026)
    public WebView webView;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J*\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00042\b\b\u0003\u0010\u000e\u001a\u00020\b2\b\b\u0002\u0010\u000f\u001a\u00020\u0010R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bXT¢\u0006\u0002\n\u0000¨\u0006\u0011"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/VectorWebViewActivity$Companion;", "", "()V", "EXTRA_MODE", "", "EXTRA_TITLE_RES_ID", "EXTRA_URL", "INVALID_RES_ID", "", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "url", "titleRes", "mode", "Lcom/opengarden/firechat/webview/WebViewMode;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: VectorWebViewActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @NotNull
        public static /* bridge */ /* synthetic */ Intent getIntent$default(Companion companion, Context context, String str, int i, WebViewMode webViewMode, int i2, Object obj) {
            if ((i2 & 4) != 0) {
                i = -1;
            }
            if ((i2 & 8) != 0) {
                webViewMode = WebViewMode.DEFAULT;
            }
            return companion.getIntent(context, str, i, webViewMode);
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context, @NotNull String str, @StringRes int i, @NotNull WebViewMode webViewMode) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
            Intrinsics.checkParameterIsNotNull(webViewMode, "mode");
            Intent intent = new Intent(context, VectorWebViewActivity.class);
            intent.putExtra(VectorWebViewActivity.EXTRA_URL, str);
            intent.putExtra(VectorWebViewActivity.EXTRA_TITLE_RES_ID, i);
            intent.putExtra(VectorWebViewActivity.EXTRA_MODE, webViewMode);
            return intent;
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_web_view;
    }

    @NotNull
    public final WebView getWebView() {
        WebView webView2 = this.webView;
        if (webView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        return webView2;
    }

    public final void setWebView(@NotNull WebView webView2) {
        Intrinsics.checkParameterIsNotNull(webView2, "<set-?>");
        this.webView = webView2;
    }

    public void initUiAndData() {
        configureToolbar();
        setWaitingView(findViewById(C1299R.C1301id.simple_webview_loader));
        WebView webView2 = this.webView;
        if (webView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        WebSettings settings = webView2.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDisplayZoomControls(false);
        if (VERSION.SDK_INT >= 21) {
            CookieManager instance = CookieManager.getInstance();
            WebView webView3 = this.webView;
            if (webView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("webView");
            }
            instance.setAcceptThirdPartyCookies(webView3, true);
        }
        Intent intent = getIntent();
        Intrinsics.checkExpressionValueIsNotNull(intent, "intent");
        String string = intent.getExtras().getString(EXTRA_URL);
        Intent intent2 = getIntent();
        Intrinsics.checkExpressionValueIsNotNull(intent2, "intent");
        int i = intent2.getExtras().getInt(EXTRA_TITLE_RES_ID, -1);
        if (i != -1) {
            setTitle(i);
        }
        Intent intent3 = getIntent();
        Intrinsics.checkExpressionValueIsNotNull(intent3, "intent");
        Serializable serializable = intent3.getExtras().getSerializable(EXTRA_MODE);
        if (serializable == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.webview.WebViewMode");
        }
        WebViewEventListener eventListener = ((WebViewMode) serializable).eventListener(this);
        WebView webView4 = this.webView;
        if (webView4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        webView4.setWebViewClient(new VectorWebViewClient(eventListener));
        WebView webView5 = this.webView;
        if (webView5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        webView5.setWebChromeClient(new VectorWebViewActivity$initUiAndData$2(this, i));
        WebView webView6 = this.webView;
        if (webView6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        webView6.loadUrl(string);
    }

    public void onBackPressed() {
        WebView webView2 = this.webView;
        if (webView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("webView");
        }
        if (webView2.canGoBack()) {
            WebView webView3 = this.webView;
            if (webView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("webView");
            }
            webView3.goBack();
            return;
        }
        super.onBackPressed();
    }
}
