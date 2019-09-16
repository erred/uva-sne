package com.opengarden.firechat.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.facebook.common.util.UriUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.net.URLDecoder;
import java.util.HashMap;

public class AccountCreationActivity extends RiotAppCompatActivity {
    public static final String EXTRA_HOME_SERVER_ID = "AccountCreationActivity.EXTRA_HOME_SERVER_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "AccountCreationActivity";
    /* access modifiers changed from: private */
    public String mHomeServerUrl;

    public int getLayoutRes() {
        return C1299R.layout.activity_account_creation;
    }

    public int getTitleRes() {
        return C1299R.string.create_account;
    }

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }

    public void initUiAndData() {
        WebView webView = (WebView) findViewById(C1299R.C1301id.account_creation_webview);
        webView.getSettings().setJavaScriptEnabled(true);
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
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.mHomeServerUrl);
        sb2.append("_matrix/static/client/register/");
        webView.loadUrl(sb2.toString());
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
                Builder builder = new Builder(AccountCreationActivity.this);
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
                AccountCreationActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AccountCreationActivity.this.finish();
                    }
                });
            }

            public void onPageFinished(WebView webView, String str) {
                if (str.startsWith(UriUtil.HTTP_SCHEME)) {
                    webView.loadUrl("javascript:window.matrixRegistration.sendObjectMessage = function(parameters) { var iframe = document.createElement('iframe');  iframe.setAttribute('src', 'js:' + JSON.stringify(parameters));  document.documentElement.appendChild(iframe); iframe.parentNode.removeChild(iframe); iframe = null; };");
                    webView.loadUrl("javascript:window.matrixRegistration.onRegistered = function(homeserverUrl, userId, accessToken) { matrixRegistration.sendObjectMessage({ 'action': 'onRegistered', 'homeServer': homeserverUrl,'userId': userId,  'accessToken': accessToken  }); };");
                }
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                HashMap hashMap;
                if (str == null || !str.startsWith("js:")) {
                    return true;
                }
                try {
                    hashMap = (HashMap) new Gson().fromJson(URLDecoder.decode(str.substring(3), "UTF-8"), new TypeToken<HashMap<String, String>>() {
                    }.getType());
                } catch (Exception e) {
                    String access$000 = AccountCreationActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shouldOverrideUrlLoading() : fromJson failed ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                    hashMap = null;
                }
                if (hashMap != null && hashMap.containsKey("homeServer") && hashMap.containsKey("userId") && hashMap.containsKey("accessToken") && hashMap.containsKey("action")) {
                    final String str2 = (String) hashMap.get("userId");
                    final String str3 = (String) hashMap.get("accessToken");
                    final String str4 = (String) hashMap.get("homeServer");
                    String str5 = (String) hashMap.get("action");
                    if (AccountCreationActivity.this.mHomeServerUrl.endsWith("/")) {
                        AccountCreationActivity.this.mHomeServerUrl = AccountCreationActivity.this.mHomeServerUrl.substring(0, AccountCreationActivity.this.mHomeServerUrl.length() - 1);
                    }
                    if (str5.equals("onRegistered")) {
                        AccountCreationActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("homeServerUrl", AccountCreationActivity.this.mHomeServerUrl);
                                intent.putExtra("homeServer", str4);
                                intent.putExtra("userId", str2);
                                intent.putExtra("accessToken", str3);
                                AccountCreationActivity.this.setResult(-1, intent);
                                AccountCreationActivity.this.finish();
                            }
                        });
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
}
