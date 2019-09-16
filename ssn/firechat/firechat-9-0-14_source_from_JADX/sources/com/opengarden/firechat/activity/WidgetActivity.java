package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import com.opengarden.firechat.widgets.WidgetsManager.onWidgetUpdateListener;
import java.io.Serializable;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\u0018\u0000 +2\u00020\u0001:\u0001+B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0003J\b\u0010\u001f\u001a\u00020 H\u0016J\b\u0010!\u001a\u00020\u001eH\u0017J\b\u0010\"\u001a\u00020\u001eH\u0002J\r\u0010#\u001a\u00020\u001eH\u0001¢\u0006\u0002\b$J\r\u0010%\u001a\u00020\u001eH\u0001¢\u0006\u0002\b&J\b\u0010'\u001a\u00020\u001eH\u0014J\b\u0010(\u001a\u00020\u001eH\u0014J\b\u0010)\u001a\u00020\u001eH\u0014J\b\u0010*\u001a\u00020\u001eH\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0011\u001a\u00020\u00128\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0017\u001a\u00020\u00188\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c¨\u0006,"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/WidgetActivity;", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "()V", "mCloseWidgetIcon", "Landroid/view/View;", "getMCloseWidgetIcon", "()Landroid/view/View;", "setMCloseWidgetIcon", "(Landroid/view/View;)V", "mRoom", "Lcom/opengarden/firechat/matrixsdk/data/Room;", "mSession", "Lcom/opengarden/firechat/matrixsdk/MXSession;", "mWidget", "Lcom/opengarden/firechat/widgets/Widget;", "mWidgetListener", "Lcom/opengarden/firechat/widgets/WidgetsManager$onWidgetUpdateListener;", "mWidgetTypeTextView", "Landroid/widget/TextView;", "getMWidgetTypeTextView", "()Landroid/widget/TextView;", "setMWidgetTypeTextView", "(Landroid/widget/TextView;)V", "mWidgetWebView", "Landroid/webkit/WebView;", "getMWidgetWebView", "()Landroid/webkit/WebView;", "setMWidgetWebView", "(Landroid/webkit/WebView;)V", "configureWebView", "", "getLayoutRes", "", "initUiAndData", "loadUrl", "onBackClicked", "onBackClicked$vector_appfirechatRelease", "onCloseClick", "onCloseClick$vector_appfirechatRelease", "onDestroy", "onPause", "onResume", "refreshStatusBar", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: WidgetActivity.kt */
public final class WidgetActivity extends RiotAppCompatActivity {
    public static final Companion Companion = new Companion(null);
    private static final String EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "WidgetActivity";
    @NotNull
    @BindView(2131297129)
    public View mCloseWidgetIcon;
    /* access modifiers changed from: private */
    public Room mRoom;
    /* access modifiers changed from: private */
    public MXSession mSession;
    /* access modifiers changed from: private */
    public Widget mWidget;
    private final onWidgetUpdateListener mWidgetListener = new WidgetActivity$mWidgetListener$1(this);
    @NotNull
    @BindView(2131297132)
    public TextView mWidgetTypeTextView;
    @NotNull
    @BindView(2131297134)
    public WebView mWidgetWebView;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\n \u0006*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u000f"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/WidgetActivity$Companion;", "", "()V", "EXTRA_WIDGET_ID", "", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "widget", "Lcom/opengarden/firechat/widgets/Widget;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: WidgetActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return WidgetActivity.LOG_TAG;
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context, @NotNull Widget widget) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(widget, "widget");
            Intent intent = new Intent(context, WidgetActivity.class);
            intent.putExtra("EXTRA_WIDGET_ID", widget);
            return intent;
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_widget;
    }

    @NotNull
    public final View getMCloseWidgetIcon() {
        View view = this.mCloseWidgetIcon;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mCloseWidgetIcon");
        }
        return view;
    }

    public final void setMCloseWidgetIcon(@NotNull View view) {
        Intrinsics.checkParameterIsNotNull(view, "<set-?>");
        this.mCloseWidgetIcon = view;
    }

    @NotNull
    public final WebView getMWidgetWebView() {
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        return webView;
    }

    public final void setMWidgetWebView(@NotNull WebView webView) {
        Intrinsics.checkParameterIsNotNull(webView, "<set-?>");
        this.mWidgetWebView = webView;
    }

    @NotNull
    public final TextView getMWidgetTypeTextView() {
        TextView textView = this.mWidgetTypeTextView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetTypeTextView");
        }
        return textView;
    }

    public final void setMWidgetTypeTextView(@NotNull TextView textView) {
        Intrinsics.checkParameterIsNotNull(textView, "<set-?>");
        this.mWidgetTypeTextView = textView;
    }

    @SuppressLint({"NewApi"})
    public void initUiAndData() {
        setWaitingView(findViewById(C1299R.C1301id.widget_progress_layout));
        Serializable serializableExtra = getIntent().getSerializableExtra("EXTRA_WIDGET_ID");
        if (serializableExtra == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.widgets.Widget");
        }
        this.mWidget = (Widget) serializableExtra;
        if (this.mWidget != null) {
            Widget widget = this.mWidget;
            if (widget == null) {
                Intrinsics.throwNpe();
            }
            if (widget.getUrl() != null) {
                Context context = this;
                Widget widget2 = this.mWidget;
                if (widget2 == null) {
                    Intrinsics.throwNpe();
                }
                this.mSession = Matrix.getMXSession(context, widget2.getSessionId());
                if (this.mSession == null) {
                    Log.m211e(Companion.getLOG_TAG(), "## onCreate() : invalid session");
                    finish();
                    return;
                }
                MXSession mXSession = this.mSession;
                if (mXSession == null) {
                    Intrinsics.throwNpe();
                }
                MXDataHandler dataHandler = mXSession.getDataHandler();
                Widget widget3 = this.mWidget;
                if (widget3 == null) {
                    Intrinsics.throwNpe();
                }
                this.mRoom = dataHandler.getRoom(widget3.getRoomId());
                if (this.mRoom == null) {
                    Log.m211e(Companion.getLOG_TAG(), "## onCreate() : invalid room");
                    finish();
                    return;
                }
                TextView textView = this.mWidgetTypeTextView;
                if (textView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mWidgetTypeTextView");
                }
                Widget widget4 = this.mWidget;
                if (widget4 == null) {
                    Intrinsics.throwNpe();
                }
                textView.setText(widget4.getHumanName());
                configureWebView();
                loadUrl();
                return;
            }
        }
        Log.m211e(Companion.getLOG_TAG(), "## onCreate() : invalid widget");
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        ViewParent parent = webView.getParent();
        if (parent == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
        }
        ((ViewGroup) parent).removeView(webView);
        webView.removeAllViews();
        webView.destroy();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        WidgetsManager.addListener(this.mWidgetListener);
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        webView.resumeTimers();
        webView.onResume();
        refreshStatusBar();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        webView.pauseTimers();
        webView.onPause();
        WidgetsManager.removeListener(this.mWidgetListener);
    }

    @OnClick({2131297129})
    public final void onCloseClick$vector_appfirechatRelease() {
        new Builder(this).setMessage(C1299R.string.widget_delete_message_confirmation).setPositiveButton(C1299R.string.remove, new WidgetActivity$onCloseClick$1(this)).setNegativeButton(C1299R.string.cancel, WidgetActivity$onCloseClick$2.INSTANCE).create().show();
    }

    @OnClick({2131297128})
    public final void onBackClicked$vector_appfirechatRelease() {
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        if (webView.canGoBack()) {
            WebView webView2 = this.mWidgetWebView;
            if (webView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
            }
            webView2.goBack();
            return;
        }
        finish();
    }

    private final void refreshStatusBar() {
        int i = 0;
        boolean z = WidgetsManager.getSharedInstance().checkWidgetPermission(this.mSession, this.mRoom) == null;
        View view = this.mCloseWidgetIcon;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mCloseWidgetIcon");
        }
        if (!z) {
            i = 4;
        }
        view.setVisibility(i);
    }

    @SuppressLint({"NewApi"})
    private final void configureWebView() {
        WebView webView = this.mWidgetWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
        }
        webView.setBackgroundColor(0);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);
        WebSettings settings = webView.getSettings();
        Intrinsics.checkExpressionValueIsNotNull(settings, "it");
        settings.setCacheMode(2);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDisplayZoomControls(false);
        webView.setWebChromeClient(new WidgetActivity$configureWebView$1$2());
        webView.setWebViewClient(new WidgetActivity$configureWebView$$inlined$let$lambda$1(this));
        if (VERSION.SDK_INT >= 21) {
            CookieManager instance = CookieManager.getInstance();
            WebView webView2 = this.mWidgetWebView;
            if (webView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWidgetWebView");
            }
            instance.setAcceptThirdPartyCookies(webView2, true);
        }
    }

    private final void loadUrl() {
        showWaitingView();
        Context context = this;
        Widget widget = this.mWidget;
        if (widget == null) {
            Intrinsics.throwNpe();
        }
        WidgetsManager.getFormattedWidgetUrl(context, widget, new WidgetActivity$loadUrl$1(this));
    }
}
