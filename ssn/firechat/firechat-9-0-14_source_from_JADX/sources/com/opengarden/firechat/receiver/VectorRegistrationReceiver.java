package com.opengarden.firechat.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.opengarden.firechat.activity.LoginActivity;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressLint({"LongLogTag"})
public class VectorRegistrationReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION_REGISTRATION = "im.vector.receiver.BROADCAST_ACTION_REGISTRATION";
    public static final String EXTRA_EMAIL_VALIDATION_PARAMS = "EXTRA_EMAIL_VALIDATION_PARAMS";
    public static final String KEY_MAIL_VALIDATION_CLIENT_SECRET = "client_secret";
    public static final String KEY_MAIL_VALIDATION_HOME_SERVER_URL = "hs_url";
    public static final String KEY_MAIL_VALIDATION_IDENTITY_SERVER_SESSION_ID = "sid";
    public static final String KEY_MAIL_VALIDATION_IDENTITY_SERVER_URL = "is_url";
    public static final String KEY_MAIL_VALIDATION_NEXT_LINK = "nextLink";
    public static final String KEY_MAIL_VALIDATION_SESSION_ID = "session_id";
    public static final String KEY_MAIL_VALIDATION_TOKEN = "token";
    private static final String LOG_TAG = "VectorRegistrationReceiver";
    public static final String SUPPORTED_PATH_ACCOUNT_EMAIL_VALIDATION = "/_matrix/identity/api/v1/validate/email/submitToken";
    private static final List<String> mSupportedHosts = Arrays.asList(new String[]{"vector.im", "riot.im"});

    public void onReceive(Context context, Intent intent) {
        Log.m209d(LOG_TAG, "## onReceive() IN");
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.equals(action, BROADCAST_ACTION_REGISTRATION)) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onReceive() Error - not supported action =");
                sb.append(action);
                Log.m211e(str, sb.toString());
                return;
            }
            Uri data = intent.getData();
            if (data == null) {
                Log.m211e(LOG_TAG, "## onReceive() Error - Uri is null");
            } else if (SUPPORTED_PATH_ACCOUNT_EMAIL_VALIDATION.equals(data.getPath())) {
                HashMap parseMailRegistrationLink = parseMailRegistrationLink(data);
                Intent intent2 = new Intent(context, LoginActivity.class);
                intent2.putExtra(EXTRA_EMAIL_VALIDATION_PARAMS, parseMailRegistrationLink);
                intent2.setFlags(872415232);
                context.startActivity(intent2);
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onReceive() Error - received path not supported: ");
                sb2.append(data.getPath());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    public static HashMap<String, String> parseMailRegistrationLink(Uri uri) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (uri != null) {
            try {
                if (!TextUtils.isEmpty(uri.getPath())) {
                    if (!SUPPORTED_PATH_ACCOUNT_EMAIL_VALIDATION.equals(uri.getPath())) {
                        Log.m211e(LOG_TAG, "## parseMailRegistrationLink(): not supported");
                    } else {
                        String host = uri.getHost();
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## parseMailRegistrationLink(): host=");
                        sb.append(host);
                        Log.m213i(str, sb.toString());
                        if (!mSupportedHosts.contains(host)) {
                            String str2 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## parseMailRegistrationLink(): unsupported host =");
                            sb2.append(host);
                            Log.m211e(str2, sb2.toString());
                            return null;
                        }
                        String fragment = uri.getFragment();
                        String lastPathSegment = uri.getLastPathSegment();
                        String schemeSpecificPart = uri.getSchemeSpecificPart();
                        String str3 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## parseMailRegistrationLink(): uriFragment=");
                        sb3.append(fragment);
                        Log.m213i(str3, sb3.toString());
                        String str4 = LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## parseMailRegistrationLink(): getLastPathSegment()=");
                        sb4.append(lastPathSegment);
                        Log.m213i(str4, sb4.toString());
                        String str5 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## parseMailRegistrationLink(): getSchemeSpecificPart()=");
                        sb5.append(schemeSpecificPart);
                        Log.m213i(str5, sb5.toString());
                        Uri uri2 = null;
                        for (String str6 : uri.getQueryParameterNames()) {
                            String queryParameter = uri.getQueryParameter(str6);
                            if (KEY_MAIL_VALIDATION_NEXT_LINK.equals(str6)) {
                                uri2 = Uri.parse(queryParameter.replace("#/", ""));
                            }
                            try {
                                queryParameter = URLDecoder.decode(queryParameter, "UTF-8");
                            } catch (Exception e) {
                                String str7 = LOG_TAG;
                                StringBuilder sb6 = new StringBuilder();
                                sb6.append("## parseMailRegistrationLink(): Exception - parse query params Msg=");
                                sb6.append(e.getLocalizedMessage());
                                Log.m211e(str7, sb6.toString());
                            }
                            hashMap.put(str6, queryParameter);
                        }
                        if (uri2 != null) {
                            hashMap.put(KEY_MAIL_VALIDATION_HOME_SERVER_URL, uri2.getQueryParameter(KEY_MAIL_VALIDATION_HOME_SERVER_URL));
                            hashMap.put(KEY_MAIL_VALIDATION_IDENTITY_SERVER_URL, uri2.getQueryParameter(KEY_MAIL_VALIDATION_IDENTITY_SERVER_URL));
                            hashMap.put(KEY_MAIL_VALIDATION_SESSION_ID, uri2.getQueryParameter(KEY_MAIL_VALIDATION_SESSION_ID));
                        }
                        String str8 = LOG_TAG;
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("## parseMailRegistrationLink(): map query=");
                        sb7.append(hashMap.toString());
                        Log.m213i(str8, sb7.toString());
                    }
                    return hashMap;
                }
            } catch (Exception e2) {
                String str9 = LOG_TAG;
                StringBuilder sb8 = new StringBuilder();
                sb8.append("## parseMailRegistrationLink(): Exception - Msg=");
                sb8.append(e2.getLocalizedMessage());
                Log.m211e(str9, sb8.toString());
                hashMap = null;
            }
        }
        Log.m211e(LOG_TAG, "## parseMailRegistrationLink : null");
        return hashMap;
    }
}
