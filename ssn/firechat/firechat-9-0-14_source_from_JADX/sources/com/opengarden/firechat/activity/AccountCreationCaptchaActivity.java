package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.net.URLDecoder;
import java.util.Formatter;
import java.util.HashMap;

public class AccountCreationCaptchaActivity extends RiotAppCompatActivity {
    public static final String EXTRA_HOME_SERVER_URL = "AccountCreationCaptchaActivity.EXTRA_HOME_SERVER_URL";
    public static final String EXTRA_SITE_KEY = "AccountCreationCaptchaActivity.EXTRA_SITE_KEY";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "AccountCreationCaptchaActivity";
    private static final String mRecaptchaHTMLString = "<html>  <head>  <script type=\"text/javascript\">  var verifyCallback = function(response) {  var iframe = document.createElement('iframe');  iframe.setAttribute('src', 'js:' + JSON.stringify({'action': 'verifyCallback', 'response': response}));  document.documentElement.appendChild(iframe);  iframe.parentNode.removeChild(iframe);  iframe = null;  };  var onloadCallback = function() {  grecaptcha.render('recaptcha_widget', {  'sitekey' : '%s',  'callback': verifyCallback  });  };  </script>  </head>  <body>  <div id=\"recaptcha_widget\"></div>  <script src=\"https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit\" async defer>  </script>  </body>  </html> ";

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_registration_captcha;
    }

    public int getTitleRes() {
        return C1299R.string.create_account;
    }

    public void initUiAndData() {
        WebView webView = (WebView) findViewById(C1299R.C1301id.account_creation_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        final View findViewById = findViewById(C1299R.C1301id.account_creation_webview_loading);
        Intent intent = getIntent();
        String str = "https://matrix.org/";
        if (intent.hasExtra(EXTRA_HOME_SERVER_URL)) {
            str = intent.getStringExtra(EXTRA_HOME_SERVER_URL);
        }
        if (!str.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            str = sb.toString();
        }
        String stringExtra = intent.getStringExtra(EXTRA_SITE_KEY);
        webView.loadDataWithBaseURL(str, new Formatter().format(mRecaptchaHTMLString, new Object[]{stringExtra}).toString(), "text/html", "utf-8", null);
        webView.requestLayout();
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                findViewById.setVisibility(8);
            }

            public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
                String access$000 = AccountCreationCaptchaActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onReceivedSslError() : ");
                sb.append(sslError.getCertificate());
                Log.m211e(access$000, sb.toString());
                Builder builder = new Builder(AccountCreationCaptchaActivity.this);
                builder.setMessage(C1299R.string.ssl_could_not_verify);
                builder.setPositiveButton(C1299R.string.ssl_trust, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.m209d(AccountCreationCaptchaActivity.LOG_TAG, "## onReceivedSslError() : the user trusted");
                        sslErrorHandler.proceed();
                    }
                });
                builder.setNegativeButton(C1299R.string.ssl_do_not_trust, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.m209d(AccountCreationCaptchaActivity.LOG_TAG, "## onReceivedSslError() : the user did not trust");
                        sslErrorHandler.cancel();
                    }
                });
                builder.setOnKeyListener(new OnKeyListener() {
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() != 1 || i != 4) {
                            return false;
                        }
                        sslErrorHandler.cancel();
                        Log.m209d(AccountCreationCaptchaActivity.LOG_TAG, "## onReceivedSslError() : the user dismisses the trust dialog.");
                        dialogInterface.dismiss();
                        return true;
                    }
                });
                builder.create().show();
            }

            private void onError(String str) {
                Log.m211e(AccountCreationCaptchaActivity.LOG_TAG, "## onError() : errorMessage");
                Toast.makeText(AccountCreationCaptchaActivity.this, str, 1).show();
                AccountCreationCaptchaActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AccountCreationCaptchaActivity.this.finish();
                    }
                });
            }

            @SuppressLint({"NewApi"})
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                if (VERSION.SDK_INT >= 23) {
                    onError(webResourceResponse.getReasonPhrase());
                } else {
                    onError(webResourceResponse.toString());
                }
            }

            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
                onError(str);
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
                    String access$000 = AccountCreationCaptchaActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shouldOverrideUrlLoading() : fromJson failed ");
                    sb.append(e.getMessage());
                    Log.m211e(access$000, sb.toString());
                    hashMap = null;
                }
                if (hashMap != null && hashMap.containsKey("action") && hashMap.containsKey("response") && TextUtils.equals((String) hashMap.get("action"), "verifyCallback")) {
                    Intent intent = new Intent();
                    intent.putExtra("response", (String) hashMap.get("response"));
                    AccountCreationCaptchaActivity.this.setResult(-1, intent);
                    AccountCreationCaptchaActivity.this.finish();
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
