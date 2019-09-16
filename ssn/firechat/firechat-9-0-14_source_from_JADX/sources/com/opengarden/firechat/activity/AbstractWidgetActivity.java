package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.annotation.CallSuper;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import butterknife.BindView;
import com.amplitude.api.Constants;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.util.RequestCodesKt;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.JsonUtilKt;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\n\b&\u0018\u0000 52\u00020\u0001:\u0003567B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\u0016H&J&\u0010\u0018\u001a\u00020\u00192\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0017J\b\u0010\u001e\u001a\u00020\u001fH\u0017J\b\u0010 \u001a\u00020\u001fH\u0003J\u0010\u0010!\u001a\u00020\u001f2\u0006\u0010\u0017\u001a\u00020\u0016H\u0002J\b\u0010\"\u001a\u00020\u001fH\u0016J0\u0010#\u001a\u00020\u001f2&\u0010$\u001a\"\u0012\u0004\u0012\u00020\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001b\u0018\u00010\u001bj\u0004\u0018\u0001`%H\u0002J\u001c\u0010&\u001a\u00020\u001f2\b\u0010'\u001a\u0004\u0018\u00010\u00162\b\u0010(\u001a\u0004\u0018\u00010\u0016H\u0004J.\u0010)\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020\u00192\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004J.\u0010+\u001a\u00020\u001f2\u0006\u0010,\u001a\u00020\u00162\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004J.\u0010-\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020.2\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004J.\u0010/\u001a\u00020\u001f2\u0006\u00100\u001a\u00020\u001c2\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004J0\u00101\u001a\u00020\u001f2\b\u0010*\u001a\u0004\u0018\u00010\u001c2\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004J.\u00102\u001a\u00020\u001f2\u0006\u00103\u001a\u00020\u00162\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0002J&\u00104\u001a\u00020\u001f2\u001c\u0010\u001a\u001a\u0018\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bj\b\u0012\u0004\u0012\u00020\u001c`\u001dH\u0004R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u00068"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/AbstractWidgetActivity;", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "()V", "mRoom", "Lcom/opengarden/firechat/matrixsdk/data/Room;", "getMRoom", "()Lcom/opengarden/firechat/matrixsdk/data/Room;", "setMRoom", "(Lcom/opengarden/firechat/matrixsdk/data/Room;)V", "mSession", "Lcom/opengarden/firechat/matrixsdk/MXSession;", "getMSession", "()Lcom/opengarden/firechat/matrixsdk/MXSession;", "setMSession", "(Lcom/opengarden/firechat/matrixsdk/MXSession;)V", "mWebView", "Landroid/webkit/WebView;", "getMWebView", "()Landroid/webkit/WebView;", "setMWebView", "(Landroid/webkit/WebView;)V", "buildInterfaceUrl", "", "scalarToken", "dealsWithWidgetRequest", "", "eventData", "", "", "Lcom/opengarden/firechat/types/JsonDict;", "initUiAndData", "", "initWebView", "launchUrl", "onBackPressed", "onWidgetMessage", "JSData", "Lcom/opengarden/firechat/types/WidgetEventData;", "openIntegrationManager", "widgetId", "screenId", "sendBoolResponse", "response", "sendError", "message", "sendIntegerResponse", "", "sendObjectAsJsonMap", "any", "sendObjectResponse", "sendResponse", "jsString", "sendSuccess", "Companion", "WidgetApiCallback", "WidgetWebAppInterface", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: AbstractWidgetActivity.kt */
public abstract class AbstractWidgetActivity extends RiotAppCompatActivity {
    public static final Companion Companion = new Companion(null);
    @NotNull
    public static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    @NotNull
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "AbstractWidgetActivity";
    @Nullable
    private Room mRoom;
    @Nullable
    private MXSession mSession;
    @NotNull
    @BindView(2131297135)
    public WebView mWebView;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\n \u0007*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0010"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/AbstractWidgetActivity$Companion;", "", "()V", "EXTRA_MATRIX_ID", "", "EXTRA_ROOM_ID", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "matrixId", "roomId", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: AbstractWidgetActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return AbstractWidgetActivity.LOG_TAG;
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context, @NotNull String str, @NotNull String str2) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "matrixId");
            Intrinsics.checkParameterIsNotNull(str2, "roomId");
            Intent intent = new Intent(context, AbstractWidgetActivity.class);
            intent.putExtra("EXTRA_MATRIX_ID", str);
            intent.putExtra("EXTRA_ROOM_ID", str2);
            return intent;
        }
    }

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0004\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B+\u0012\u001c\u0010\u0003\u001a\u0018\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004j\b\u0012\u0004\u0012\u00020\u0006`\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005¢\u0006\u0002\u0010\tJ\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u0002J\u0010\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0014\u0010\u0010\u001a\u00020\u000b2\n\u0010\u000e\u001a\u00060\u0011j\u0002`\u0012H\u0016J\u0017\u0010\u0013\u001a\u00020\u000b2\b\u0010\u0014\u001a\u0004\u0018\u00018\u0000H\u0016¢\u0006\u0002\u0010\u0015J\u0014\u0010\u0016\u001a\u00020\u000b2\n\u0010\u000e\u001a\u00060\u0011j\u0002`\u0012H\u0016R\u000e\u0010\b\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R$\u0010\u0003\u001a\u0018\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004j\b\u0012\u0004\u0012\u00020\u0006`\u0007X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/AbstractWidgetActivity$WidgetApiCallback;", "T", "Lcom/opengarden/firechat/matrixsdk/rest/callback/ApiCallback;", "mEventData", "", "", "", "Lcom/opengarden/firechat/types/JsonDict;", "mDescription", "(Lcom/opengarden/firechat/activity/AbstractWidgetActivity;Ljava/util/Map;Ljava/lang/String;)V", "onError", "", "error", "onMatrixError", "e", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "onNetworkError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onSuccess", "info", "(Ljava/lang/Object;)V", "onUnexpectedError", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: AbstractWidgetActivity.kt */
    protected final class WidgetApiCallback<T> implements ApiCallback<T> {
        private final String mDescription;
        private final Map<String, Object> mEventData;
        final /* synthetic */ AbstractWidgetActivity this$0;

        public WidgetApiCallback(@NotNull AbstractWidgetActivity abstractWidgetActivity, @NotNull Map<String, ? extends Object> map, String str) {
            Intrinsics.checkParameterIsNotNull(map, "mEventData");
            Intrinsics.checkParameterIsNotNull(str, "mDescription");
            this.this$0 = abstractWidgetActivity;
            this.mEventData = map;
            this.mDescription = str;
        }

        public void onSuccess(@Nullable T t) {
            String access$getLOG_TAG$p = AbstractWidgetActivity.Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.mDescription);
            sb.append(" succeeds");
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            this.this$0.sendSuccess(this.mEventData);
        }

        private final void onError(String str) {
            String access$getLOG_TAG$p = AbstractWidgetActivity.Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.mDescription);
            sb.append(" failed with error ");
            sb.append(str);
            Log.m211e(access$getLOG_TAG$p, sb.toString());
            AbstractWidgetActivity abstractWidgetActivity = this.this$0;
            String string = this.this$0.getString(C1299R.string.widget_integration_failed_to_send_request);
            Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
            abstractWidgetActivity.sendError(string, this.mEventData);
        }

        public void onNetworkError(@NotNull Exception exc) {
            Intrinsics.checkParameterIsNotNull(exc, "e");
            String message = exc.getMessage();
            if (message == null) {
                Intrinsics.throwNpe();
            }
            onError(message);
        }

        public void onMatrixError(@NotNull MatrixError matrixError) {
            Intrinsics.checkParameterIsNotNull(matrixError, "e");
            String message = matrixError.getMessage();
            Intrinsics.checkExpressionValueIsNotNull(message, "e.message");
            onError(message);
        }

        public void onUnexpectedError(@NotNull Exception exc) {
            Intrinsics.checkParameterIsNotNull(exc, "e");
            String message = exc.getMessage();
            if (message == null) {
                Intrinsics.throwNpe();
            }
            onError(message);
        }
    }

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0007\b\u0000¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007¨\u0006\u0007"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/AbstractWidgetActivity$WidgetWebAppInterface;", "", "(Lcom/opengarden/firechat/activity/AbstractWidgetActivity;)V", "onWidgetEvent", "", "eventData", "", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: AbstractWidgetActivity.kt */
    private final class WidgetWebAppInterface {
        public WidgetWebAppInterface() {
        }

        @JavascriptInterface
        public final void onWidgetEvent(@NotNull String str) {
            Intrinsics.checkParameterIsNotNull(str, "eventData");
            String access$getLOG_TAG$p = AbstractWidgetActivity.Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("BRIDGE onWidgetEvent : ");
            sb.append(str);
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            try {
                AbstractWidgetActivity.this.runOnUiThread(new AbstractWidgetActivity$WidgetWebAppInterface$onWidgetEvent$1(this, (Map) JsonUtils.getGson(false).fromJson(str, new C1329x3351ca60().getType())));
            } catch (Exception e) {
                String access$getLOG_TAG$p2 = AbstractWidgetActivity.Companion.getLOG_TAG();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onWidgetEvent() failed ");
                sb2.append(e.getMessage());
                Log.m211e(access$getLOG_TAG$p2, sb2.toString());
            }
        }
    }

    @Nullable
    public abstract String buildInterfaceUrl(@NotNull String str);

    @NotNull
    public final WebView getMWebView() {
        WebView webView = this.mWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWebView");
        }
        return webView;
    }

    public final void setMWebView(@NotNull WebView webView) {
        Intrinsics.checkParameterIsNotNull(webView, "<set-?>");
        this.mWebView = webView;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public final MXSession getMSession() {
        return this.mSession;
    }

    /* access modifiers changed from: protected */
    public final void setMSession(@Nullable MXSession mXSession) {
        this.mSession = mXSession;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public final Room getMRoom() {
        return this.mRoom;
    }

    /* access modifiers changed from: protected */
    public final void setMRoom(@Nullable Room room) {
        this.mRoom = room;
    }

    @CallSuper
    public void initUiAndData() {
        Context context = this;
        this.mSession = Matrix.getInstance(context).getSession(getIntent().getStringExtra("EXTRA_MATRIX_ID"));
        if (this.mSession != null) {
            MXSession mXSession = this.mSession;
            if (mXSession == null) {
                Intrinsics.throwNpe();
            }
            if (mXSession.isAlive()) {
                initWebView();
                MXSession mXSession2 = this.mSession;
                if (mXSession2 == null) {
                    Intrinsics.throwNpe();
                }
                this.mRoom = mXSession2.getDataHandler().getRoom(getIntent().getStringExtra("EXTRA_ROOM_ID"));
                MXSession mXSession3 = this.mSession;
                if (mXSession3 == null) {
                    Intrinsics.throwNpe();
                }
                WidgetsManager.getScalarToken(context, mXSession3, new AbstractWidgetActivity$initUiAndData$1(this));
                return;
            }
        }
        Log.m211e(Companion.getLOG_TAG(), "## onCreate() : invalid session");
        finish();
    }

    public void onBackPressed() {
        WebView webView = this.mWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWebView");
        }
        if (webView.canGoBack()) {
            WebView webView2 = this.mWebView;
            if (webView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWebView");
            }
            webView2.goBack();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint({"NewApi"})
    private final void initWebView() {
        WebView webView = this.mWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWebView");
        }
        webView.addJavascriptInterface(new WidgetWebAppInterface(), Constants.PLATFORM);
        webView.setWebChromeClient(new AbstractWidgetActivity$initWebView$$inlined$let$lambda$1(this));
        WebSettings settings = webView.getSettings();
        Intrinsics.checkExpressionValueIsNotNull(settings, "it");
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new AbstractWidgetActivity$initWebView$$inlined$let$lambda$2(this));
        if (VERSION.SDK_INT >= 21) {
            CookieManager instance = CookieManager.getInstance();
            WebView webView2 = this.mWebView;
            if (webView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWebView");
            }
            instance.setAcceptThirdPartyCookies(webView2, true);
        }
    }

    /* access modifiers changed from: private */
    public final void launchUrl(String str) {
        String buildInterfaceUrl = buildInterfaceUrl(str);
        if (buildInterfaceUrl == null) {
            finish();
            return;
        }
        WebView webView = this.mWebView;
        if (webView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mWebView");
        }
        webView.loadUrl(buildInterfaceUrl);
    }

    /* access modifiers changed from: private */
    public final void onWidgetMessage(Map<String, ? extends Map<String, ? extends Object>> map) {
        if (map == null) {
            Log.m211e(Companion.getLOG_TAG(), "## onWidgetMessage() : invalid JSData");
            return;
        }
        Map map2 = (Map) map.get("event.data");
        if (map2 == null) {
            Log.m211e(Companion.getLOG_TAG(), "## onWidgetMessage() : invalid JSData");
            return;
        }
        try {
            if (!dealsWithWidgetRequest(map2)) {
                String string = getString(C1299R.string.widget_integration_failed_to_send_request);
                Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
                sendError(string, map2);
            }
        } catch (Exception e) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("## onWidgetMessage() : failed ");
            sb.append(e.getMessage());
            Log.m211e(access$getLOG_TAG$p, sb.toString());
            String string2 = getString(C1299R.string.widget_integration_failed_to_send_request);
            Intrinsics.checkExpressionValueIsNotNull(string2, "getString(R.string.widge…n_failed_to_send_request)");
            sendError(string2, map2);
        }
    }

    @CallSuper
    public boolean dealsWithWidgetRequest(@NotNull Map<String, ? extends Object> map) {
        String str;
        String str2;
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        String str3 = (String) map.get("action");
        if (str3 == null || str3.hashCode() != 1328652327 || !str3.equals("integration_manager_open")) {
            return false;
        }
        String str4 = null;
        Object obj = map.get("data");
        if ((obj instanceof Map ? obj : null) == null) {
            str = str4;
        } else if (obj == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<*, *>");
        } else {
            Map map2 = (Map) obj;
            Object obj2 = map2.get("integType");
            if (!(obj2 instanceof String)) {
                obj2 = null;
            }
            if (obj2 == null) {
                str2 = str4;
            } else if (obj2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            } else {
                str2 = (String) obj2;
            }
            Object obj3 = map2.get("integId");
            if (!(obj3 instanceof String)) {
                obj3 = null;
            }
            if (obj3 != null) {
                if (obj3 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
                }
                str4 = (String) obj3;
            }
            if (str2 != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("type_");
                sb.append(str2);
                str = sb.toString();
            } else {
                str = str2;
            }
        }
        openIntegrationManager(str4, str);
        return true;
    }

    private final void sendResponse(String str, Map<String, ? extends Object> map) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("sendResponseFromRiotAndroid('");
            sb.append(map.get("_id"));
            sb.append("' , ");
            sb.append(str);
            sb.append(");");
            String sb2 = sb.toString();
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("BRIDGE sendResponse: ");
            sb3.append(sb2);
            Log.m215v(access$getLOG_TAG$p, sb3.toString());
            if (VERSION.SDK_INT < 19) {
                WebView webView = this.mWebView;
                if (webView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mWebView");
                }
                StringBuilder sb4 = new StringBuilder();
                sb4.append("javascript:");
                sb4.append(sb2);
                webView.loadUrl(sb4.toString());
                return;
            }
            WebView webView2 = this.mWebView;
            if (webView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mWebView");
            }
            webView2.evaluateJavascript(sb2, null);
        } catch (Exception e) {
            String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
            StringBuilder sb5 = new StringBuilder();
            sb5.append("## sendResponse() failed ");
            sb5.append(e.getMessage());
            Log.m211e(access$getLOG_TAG$p2, sb5.toString());
        }
    }

    /* access modifiers changed from: protected */
    public final void sendBoolResponse(boolean z, @NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        sendResponse(z ? "true" : "false", map);
    }

    /* access modifiers changed from: protected */
    public final void sendIntegerResponse(int i, @NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(i));
        sb.append("");
        sendResponse(sb.toString(), map);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void sendObjectResponse(@org.jetbrains.annotations.Nullable java.lang.Object r5, @org.jetbrains.annotations.NotNull java.util.Map<java.lang.String, ? extends java.lang.Object> r6) {
        /*
            r4 = this;
            java.lang.String r0 = "eventData"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r0)
            r0 = 0
            java.lang.String r0 = (java.lang.String) r0
            if (r5 == 0) goto L_0x0049
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x002a }
            r1.<init>()     // Catch:{ Exception -> 0x002a }
            java.lang.String r2 = "JSON.parse('"
            r1.append(r2)     // Catch:{ Exception -> 0x002a }
            r2 = 0
            com.google.gson.Gson r2 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getGson(r2)     // Catch:{ Exception -> 0x002a }
            java.lang.String r5 = r2.toJson(r5)     // Catch:{ Exception -> 0x002a }
            r1.append(r5)     // Catch:{ Exception -> 0x002a }
            java.lang.String r5 = "')"
            r1.append(r5)     // Catch:{ Exception -> 0x002a }
            java.lang.String r5 = r1.toString()     // Catch:{ Exception -> 0x002a }
            goto L_0x004a
        L_0x002a:
            r5 = move-exception
            com.opengarden.firechat.activity.AbstractWidgetActivity$Companion r1 = Companion
            java.lang.String r1 = r1.getLOG_TAG()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## sendObjectResponse() : toJson failed "
            r2.append(r3)
            java.lang.String r5 = r5.getMessage()
            r2.append(r5)
            java.lang.String r5 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r5)
        L_0x0049:
            r5 = r0
        L_0x004a:
            if (r5 != 0) goto L_0x004e
            java.lang.String r5 = "null"
        L_0x004e:
            r4.sendResponse(r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.AbstractWidgetActivity.sendObjectResponse(java.lang.Object, java.util.Map):void");
    }

    /* access modifiers changed from: protected */
    public final void sendSuccess(@NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        HashMap hashMap = new HashMap();
        hashMap.put(Param.SUCCESS, Boolean.valueOf(true));
        sendObjectResponse(hashMap, map);
    }

    /* access modifiers changed from: protected */
    public final void sendError(@NotNull String str, @NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(str, "message");
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        String access$getLOG_TAG$p = Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("## sendError() : eventData ");
        sb.append(map);
        sb.append(" failed ");
        sb.append(str);
        Log.m211e(access$getLOG_TAG$p, sb.toString());
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        hashMap2.put("message", str);
        hashMap.put("error", hashMap2);
        sendObjectResponse(hashMap, map);
    }

    /* access modifiers changed from: protected */
    public final void sendObjectAsJsonMap(@NotNull Object obj, @NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(obj, "any");
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        sendObjectResponse(JsonUtilKt.toJsonMap(obj), map);
    }

    /* access modifiers changed from: protected */
    public final void openIntegrationManager(@Nullable String str, @Nullable String str2) {
        com.opengarden.firechat.activity.IntegrationManagerActivity.Companion companion = IntegrationManagerActivity.Companion;
        Context context = this;
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwNpe();
        }
        String myUserId = mXSession.getMyUserId();
        Intrinsics.checkExpressionValueIsNotNull(myUserId, "mSession!!.myUserId");
        Room room = this.mRoom;
        if (room == null) {
            Intrinsics.throwNpe();
        }
        String roomId = room.getRoomId();
        Intrinsics.checkExpressionValueIsNotNull(roomId, "mRoom!!.roomId");
        startActivityForResult(companion.getIntent(context, myUserId, roomId, str, str2), RequestCodesKt.INTEGRATION_MANAGER_ACTIVITY_REQUEST_CODE);
    }
}
