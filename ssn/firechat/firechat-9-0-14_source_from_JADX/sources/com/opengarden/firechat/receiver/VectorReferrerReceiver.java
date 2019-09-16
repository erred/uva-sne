package com.opengarden.firechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.LoginActivity;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.repositories.ServerUrlsRepository;
import java.net.URLDecoder;

public class VectorReferrerReceiver extends BroadcastReceiver {
    private static final String INSTALL_REFERRER_ACTION = "com.android.vending.INSTALL_REFERRER";
    private static final String KEY_HS = "hs";
    private static final String KEY_IS = "is";
    private static final String KEY_REFERRER = "referrer";
    private static final String LOG_TAG = "VectorReferrerReceiver";
    private static final String UTM_CONTENT = "utm_content";
    private static final String UTM_SOURCE = "utm_source";

    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.m211e(LOG_TAG, "No intent");
            return;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onReceive() : ");
        sb.append(intent.getAction());
        Log.m209d(str, sb.toString());
        if (TextUtils.equals(intent.getAction(), INSTALL_REFERRER_ACTION)) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.m211e(LOG_TAG, "No extra");
                return;
            }
            String str2 = "";
            String str3 = "";
            try {
                String str4 = (String) extras.get(KEY_REFERRER);
                String str5 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onReceive() : referrer ");
                sb2.append(str4);
                Log.m209d(str5, sb2.toString());
                if (!TextUtils.isEmpty(str4)) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("https://dummy?");
                    sb3.append(URLDecoder.decode(str4, "utf-8"));
                    Uri parse = Uri.parse(sb3.toString());
                    String queryParameter = parse.getQueryParameter(UTM_SOURCE);
                    String queryParameter2 = parse.getQueryParameter(UTM_CONTENT);
                    String str6 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## onReceive() : utm_source ");
                    sb4.append(queryParameter);
                    sb4.append(" -- utm_content ");
                    sb4.append(queryParameter2);
                    Log.m209d(str6, sb4.toString());
                    if (queryParameter2 != null) {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("https://dummy?");
                        sb5.append(URLDecoder.decode(queryParameter2, "utf-8"));
                        Uri parse2 = Uri.parse(sb5.toString());
                        String queryParameter3 = parse2.getQueryParameter(KEY_HS);
                        try {
                            str3 = parse2.getQueryParameter(KEY_IS);
                            str2 = queryParameter3;
                        } catch (Throwable th) {
                            th = th;
                            str2 = queryParameter3;
                            String str7 = LOG_TAG;
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("## onReceive() : failed ");
                            sb6.append(th.getMessage());
                            Log.m211e(str7, sb6.toString());
                            String str8 = LOG_TAG;
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("## onReceive() : HS ");
                            sb7.append(str2);
                            Log.m209d(str8, sb7.toString());
                            String str9 = LOG_TAG;
                            StringBuilder sb8 = new StringBuilder();
                            sb8.append("## onReceive() : IS ");
                            sb8.append(str3);
                            Log.m209d(str9, sb8.toString());
                            ServerUrlsRepository.INSTANCE.setDefaultUrlsFromReferrer(context, str2, str3);
                            Log.m209d(LOG_TAG, "## onReceive() : warn loginactivity");
                            ((LoginActivity) VectorApp.getCurrentActivity()).onServerUrlsUpdateFromReferrer();
                        }
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                String str72 = LOG_TAG;
                StringBuilder sb62 = new StringBuilder();
                sb62.append("## onReceive() : failed ");
                sb62.append(th.getMessage());
                Log.m211e(str72, sb62.toString());
                String str82 = LOG_TAG;
                StringBuilder sb72 = new StringBuilder();
                sb72.append("## onReceive() : HS ");
                sb72.append(str2);
                Log.m209d(str82, sb72.toString());
                String str92 = LOG_TAG;
                StringBuilder sb82 = new StringBuilder();
                sb82.append("## onReceive() : IS ");
                sb82.append(str3);
                Log.m209d(str92, sb82.toString());
                ServerUrlsRepository.INSTANCE.setDefaultUrlsFromReferrer(context, str2, str3);
                Log.m209d(LOG_TAG, "## onReceive() : warn loginactivity");
                ((LoginActivity) VectorApp.getCurrentActivity()).onServerUrlsUpdateFromReferrer();
            }
            String str822 = LOG_TAG;
            StringBuilder sb722 = new StringBuilder();
            sb722.append("## onReceive() : HS ");
            sb722.append(str2);
            Log.m209d(str822, sb722.toString());
            String str922 = LOG_TAG;
            StringBuilder sb822 = new StringBuilder();
            sb822.append("## onReceive() : IS ");
            sb822.append(str3);
            Log.m209d(str922, sb822.toString());
            if (!TextUtils.isEmpty(str2) || !TextUtils.isEmpty(str3)) {
                ServerUrlsRepository.INSTANCE.setDefaultUrlsFromReferrer(context, str2, str3);
                if (VectorApp.getCurrentActivity() != null && (VectorApp.getCurrentActivity() instanceof LoginActivity)) {
                    Log.m209d(LOG_TAG, "## onReceive() : warn loginactivity");
                    ((LoginActivity) VectorApp.getCurrentActivity()).onServerUrlsUpdateFromReferrer();
                }
            }
        }
    }
}
