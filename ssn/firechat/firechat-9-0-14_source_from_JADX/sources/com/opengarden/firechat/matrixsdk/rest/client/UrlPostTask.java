package com.opengarden.firechat.matrixsdk.rest.client;

import android.os.AsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class UrlPostTask extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = "UrlPostTask";
    private IPostTaskListener mListener;

    public interface IPostTaskListener {
        void onError(String str);

        void onSucceed(JsonObject jsonObject);
    }

    /* access modifiers changed from: protected */
    public String doInBackground(String... strArr) {
        String str = "";
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(strArr[0]).openConnection();
            if (RestClient.getUserAgent() != null) {
                httpURLConnection.setRequestProperty("User-Agent", RestClient.getUserAgent());
            }
            httpURLConnection.setRequestMethod("POST");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            if (bufferedInputStream != null) {
                return convertStreamToString(bufferedInputStream);
            }
            return str;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void setListener(IPostTaskListener iPostTaskListener) {
        this.mListener = iPostTaskListener;
    }

    private static String convertStreamToString(InputStream inputStream) {
        String str;
        StringBuilder sb;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb2 = new StringBuilder();
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(readLine);
                    sb3.append(StringUtils.f158LF);
                    sb2.append(sb3.toString());
                } else {
                    try {
                        break;
                    } catch (Exception e) {
                        e = e;
                        str = LOG_TAG;
                        sb = new StringBuilder();
                    }
                }
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("convertStreamToString ");
                sb4.append(e2.getMessage());
                Log.m211e(str2, sb4.toString());
                try {
                } catch (Exception e3) {
                    e = e3;
                    str = LOG_TAG;
                    sb = new StringBuilder();
                }
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e4) {
                    String str3 = LOG_TAG;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append("convertStreamToString finally failed ");
                    sb5.append(e4.getMessage());
                    Log.m211e(str3, sb5.toString());
                }
            }
        }
        return sb2.toString();
        sb.append("convertStreamToString finally failed ");
        sb.append(e.getMessage());
        Log.m211e(str, sb.toString());
        return sb2.toString();
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(String str) {
        JsonObject jsonObject;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onPostExecute ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        try {
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## onPostExecute() failed");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
            jsonObject = null;
        }
        if (this.mListener == null) {
            return;
        }
        if (jsonObject != null) {
            this.mListener.onSucceed(jsonObject);
        } else {
            this.mListener.onError(str);
        }
    }
}
