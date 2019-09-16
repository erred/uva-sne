package com.opengarden.firechat.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.amplitude.api.AmplitudeClient;
import com.facebook.common.util.UriUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.net.URLDecoder;
import java.util.HashMap;

public class FallbackLoginActivity extends RiotAppCompatActivity {
    public static final String EXTRA_HOME_SERVER_ID = "FallbackLoginActivity.EXTRA_HOME_SERVER_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "FallbackLoginActivity";
    /* access modifiers changed from: private */
    public String mHomeServerUrl = null;
    private WebView mWebView = null;

    public int getLayoutRes() {
        return C1299R.layout.activity_login_fallback;
    }

    public int getTitleRes() {
        return C1299R.string.login;
    }

    public void initUiAndData() {
        this.mWebView = (WebView) findViewById(C1299R.C1301id.account_creation_webview);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        this.mHomeServerUrl = "https://matrix.org/";
        if (intent.hasExtra(EXTRA_HOME_SERVER_ID)) {
            this.mHomeServerUrl = intent.getStringExtra(EXTRA_HOME_SERVER_ID);
        }
        if (!this.mHomeServerUrl.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mHomeServerUrl);
            sb.append("/");
            this.mHomeServerUrl = sb.toString();
        }
        CookieManager instance = CookieManager.getInstance();
        if (instance != null && !instance.hasCookies()) {
            launchWebView();
        } else if (VERSION.SDK_INT < 21) {
            try {
                instance.removeAllCookie();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(" cookieManager.removeAllCookie() fails ");
                sb2.append(e.getLocalizedMessage());
                Log.m211e(str, sb2.toString());
            }
            launchWebView();
        } else {
            try {
                instance.removeAllCookies(new ValueCallback<Boolean>() {
                    public void onReceiveValue(Boolean bool) {
                        FallbackLoginActivity.this.launchWebView();
                    }
                });
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(" cookieManager.removeAllCookie() fails ");
                sb3.append(e2.getLocalizedMessage());
                Log.m211e(str2, sb3.toString());
                launchWebView();
            }
        }
    }

    /* access modifiers changed from: private */
    public void launchWebView() {
        WebView webView = this.mWebView;
        StringBuilder sb = new StringBuilder();
        sb.append(this.mHomeServerUrl);
        sb.append("_matrix/static/client/login/");
        webView.loadUrl(sb.toString());
        this.mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
                Builder builder = new Builder(FallbackLoginActivity.this);
                builder.setMessage(C1299R.string.ssl_could_not_verify);
                builder.setPositiveButton(C1299R.string.ssl_trust, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sslErrorHandler.proceed();
                    }
                });
                builder.setNegativeButton(C1299R.string.ssl_do_not_trust, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sslErrorHandler.cancel();
                    }
                });
                builder.setOnKeyListener(new OnKeyListener() {
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() != 1 || i != 4) {
                            return false;
                        }
                        sslErrorHandler.cancel();
                        dialogInterface.dismiss();
                        return true;
                    }
                });
                builder.create().show();
            }

            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
                FallbackLoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        FallbackLoginActivity.this.finish();
                    }
                });
            }

            public void onPageFinished(WebView webView, String str) {
                if (str.startsWith(UriUtil.HTTP_SCHEME)) {
                    webView.loadUrl("javascript:window.matrixLogin.sendObjectMessage = function(parameters) { var iframe = document.createElement('iframe');  iframe.setAttribute('src', 'js:' + JSON.stringify(parameters));  document.documentElement.appendChild(iframe); iframe.parentNode.removeChild(iframe); iframe = null; };");
                    webView.loadUrl("javascript:window.matrixLogin.onLogin = function(homeserverUrl, userId, accessToken) { matrixLogin.sendObjectMessage({ 'action': 'onLogin', 'homeServer': homeserverUrl,'userId': userId,  'accessToken': accessToken  }); };");
                }
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                HashMap hashMap;
                if (str == null || !str.startsWith("js:")) {
                    return false;
                }
                try {
                    hashMap = (HashMap) new Gson().fromJson(URLDecoder.decode(str.substring(3), "UTF-8"), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                } catch (Exception e) {
                    String access$100 = FallbackLoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shouldOverrideUrlLoading() : fromJson failed ");
                    sb.append(e.getMessage());
                    Log.m211e(access$100, sb.toString());
                    hashMap = null;
                }
                if (hashMap != null) {
                    try {
                        String str2 = (String) hashMap.get("action");
                        LinkedTreeMap linkedTreeMap = (LinkedTreeMap) hashMap.get("homeServer");
                        if (TextUtils.equals("onLogin", str2) && linkedTreeMap != null) {
                            final String str3 = (String) linkedTreeMap.get(AmplitudeClient.USER_ID_KEY);
                            final String str4 = (String) linkedTreeMap.get("access_token");
                            final String str5 = (String) linkedTreeMap.get("home_server");
                            if (FallbackLoginActivity.this.mHomeServerUrl.endsWith("/")) {
                                FallbackLoginActivity.this.mHomeServerUrl = FallbackLoginActivity.this.mHomeServerUrl.substring(0, FallbackLoginActivity.this.mHomeServerUrl.length() - 1);
                            }
                            if (!(str5 == null || str3 == null || str4 == null)) {
                                FallbackLoginActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.putExtra("homeServerUrl", FallbackLoginActivity.this.mHomeServerUrl);
                                        intent.putExtra("homeServer", str5);
                                        intent.putExtra("userId", str3);
                                        intent.putExtra("accessToken", str4);
                                        FallbackLoginActivity.this.setResult(-1, intent);
                                        FallbackLoginActivity.this.finish();
                                    }
                                });
                            }
                        }
                    } catch (Exception e2) {
                        String access$1002 = FallbackLoginActivity.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## shouldOverrideUrlLoading() : failed ");
                        sb2.append(e2.getMessage());
                        Log.m211e(access$1002, sb2.toString());
                    }
                }
                return true;
            }
        });
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 82) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }
}
