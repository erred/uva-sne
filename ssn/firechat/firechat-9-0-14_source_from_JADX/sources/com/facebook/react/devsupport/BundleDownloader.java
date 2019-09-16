package com.facebook.react.devsupport;

import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.p002os.EnvironmentCompat;
import android.util.Log;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.DebugServerException;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.MultipartStreamReader.ChunkCallback;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.stetho.server.http.HttpHeaders;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.C3012Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import org.json.JSONException;
import org.json.JSONObject;

public class BundleDownloader {
    private static final int FILES_CHANGED_COUNT_NOT_BUILT_BY_BUNDLER = -2;
    private static final String TAG = "BundleDownloader";
    private final OkHttpClient mClient;
    /* access modifiers changed from: private */
    @Nullable
    public Call mDownloadBundleFromURLCall;

    public static class BundleInfo {
        /* access modifiers changed from: private */
        public int mFilesChangedCount;
        /* access modifiers changed from: private */
        @Nullable
        public String mUrl;

        @Nullable
        public static BundleInfo fromJSONString(String str) {
            if (str == null) {
                return null;
            }
            BundleInfo bundleInfo = new BundleInfo();
            try {
                JSONObject jSONObject = new JSONObject(str);
                bundleInfo.mUrl = jSONObject.getString(ImagesContract.URL);
                bundleInfo.mFilesChangedCount = jSONObject.getInt("filesChangedCount");
                return bundleInfo;
            } catch (JSONException e) {
                Log.e(BundleDownloader.TAG, "Invalid bundle info: ", e);
                return null;
            }
        }

        @Nullable
        public String toJSONString() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(ImagesContract.URL, this.mUrl);
                jSONObject.put("filesChangedCount", this.mFilesChangedCount);
                return jSONObject.toString();
            } catch (JSONException e) {
                Log.e(BundleDownloader.TAG, "Can't serialize bundle info: ", e);
                return null;
            }
        }

        public String getUrl() {
            return this.mUrl != null ? this.mUrl : EnvironmentCompat.MEDIA_UNKNOWN;
        }

        public int getFilesChangedCount() {
            return this.mFilesChangedCount;
        }
    }

    public BundleDownloader(OkHttpClient okHttpClient) {
        this.mClient = okHttpClient;
    }

    public void downloadBundleFromURL(final DevBundleDownloadListener devBundleDownloadListener, final File file, String str, @Nullable final BundleInfo bundleInfo) {
        this.mDownloadBundleFromURLCall = (Call) Assertions.assertNotNull(this.mClient.newCall(new Builder().url(str).build()));
        this.mDownloadBundleFromURLCall.enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (BundleDownloader.this.mDownloadBundleFromURLCall == null || BundleDownloader.this.mDownloadBundleFromURLCall.isCanceled()) {
                    BundleDownloader.this.mDownloadBundleFromURLCall = null;
                    return;
                }
                BundleDownloader.this.mDownloadBundleFromURLCall = null;
                DevBundleDownloadListener devBundleDownloadListener = devBundleDownloadListener;
                StringBuilder sb = new StringBuilder();
                sb.append("URL: ");
                sb.append(call.request().url().toString());
                devBundleDownloadListener.onFailure(DebugServerException.makeGeneric("Could not connect to development server.", sb.toString(), iOException));
            }

            public void onResponse(Call call, final C3012Response response) throws IOException {
                if (BundleDownloader.this.mDownloadBundleFromURLCall == null || BundleDownloader.this.mDownloadBundleFromURLCall.isCanceled()) {
                    BundleDownloader.this.mDownloadBundleFromURLCall = null;
                    return;
                }
                BundleDownloader.this.mDownloadBundleFromURLCall = null;
                final String httpUrl = response.request().url().toString();
                Matcher matcher = Pattern.compile("multipart/mixed;.*boundary=\"([^\"]+)\"").matcher(response.header("content-type"));
                if (matcher.find()) {
                    if (!new MultipartStreamReader(response.body().source(), matcher.group(1)).readAllParts(new ChunkCallback() {
                        public void execute(Map<String, String> map, Buffer buffer, boolean z) throws IOException {
                            if (z) {
                                int code = response.code();
                                if (map.containsKey("X-Http-Status")) {
                                    code = Integer.parseInt((String) map.get("X-Http-Status"));
                                }
                                BundleDownloader.processBundleResult(httpUrl, code, Headers.m234of(map), buffer, file, bundleInfo, devBundleDownloadListener);
                            } else if (map.containsKey(HttpHeaders.CONTENT_TYPE) && ((String) map.get(HttpHeaders.CONTENT_TYPE)).equals("application/json")) {
                                try {
                                    JSONObject jSONObject = new JSONObject(buffer.readUtf8());
                                    Integer num = null;
                                    String string = jSONObject.has(NotificationCompat.CATEGORY_STATUS) ? jSONObject.getString(NotificationCompat.CATEGORY_STATUS) : null;
                                    Integer valueOf = jSONObject.has("done") ? Integer.valueOf(jSONObject.getInt("done")) : null;
                                    if (jSONObject.has("total")) {
                                        num = Integer.valueOf(jSONObject.getInt("total"));
                                    }
                                    devBundleDownloadListener.onProgress(string, valueOf, num);
                                } catch (JSONException e) {
                                    String str = ReactConstants.TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Error parsing progress JSON. ");
                                    sb.append(e.toString());
                                    FLog.m65e(str, sb.toString());
                                }
                            }
                        }
                    })) {
                        DevBundleDownloadListener devBundleDownloadListener = devBundleDownloadListener;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Error while reading multipart response.\n\nResponse code: ");
                        sb.append(response.code());
                        sb.append("\n\n");
                        sb.append("URL: ");
                        sb.append(call.request().url().toString());
                        sb.append("\n\n");
                        devBundleDownloadListener.onFailure(new DebugServerException(sb.toString()));
                    }
                } else {
                    BundleDownloader.processBundleResult(httpUrl, response.code(), response.headers(), Okio.buffer((Source) response.body().source()), file, bundleInfo, devBundleDownloadListener);
                }
            }
        });
    }

    public void cancelDownloadBundleFromURL() {
        if (this.mDownloadBundleFromURLCall != null) {
            this.mDownloadBundleFromURLCall.cancel();
            this.mDownloadBundleFromURLCall = null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void processBundleResult(java.lang.String r1, int r2, okhttp3.Headers r3, okio.BufferedSource r4, java.io.File r5, com.facebook.react.devsupport.BundleDownloader.BundleInfo r6, com.facebook.react.devsupport.interfaces.DevBundleDownloadListener r7) throws java.io.IOException {
        /*
            r0 = 200(0xc8, float:2.8E-43)
            if (r2 == r0) goto L_0x0046
            java.lang.String r3 = r4.readUtf8()
            com.facebook.react.common.DebugServerException r4 = com.facebook.react.common.DebugServerException.parse(r3)
            if (r4 == 0) goto L_0x0012
            r7.onFailure(r4)
            goto L_0x0045
        L_0x0012:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "The development server returned response error code: "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r2 = "\n\n"
            r4.append(r2)
            java.lang.String r2 = "URL: "
            r4.append(r2)
            r4.append(r1)
            java.lang.String r1 = "\n\n"
            r4.append(r1)
            java.lang.String r1 = "Body:\n"
            r4.append(r1)
            r4.append(r3)
            com.facebook.react.common.DebugServerException r1 = new com.facebook.react.common.DebugServerException
            java.lang.String r2 = r4.toString()
            r1.<init>(r2)
            r7.onFailure(r1)
        L_0x0045:
            return
        L_0x0046:
            if (r6 == 0) goto L_0x004b
            populateBundleInfo(r1, r3, r6)
        L_0x004b:
            java.io.File r1 = new java.io.File
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r5.getPath()
            r2.append(r3)
            java.lang.String r3 = ".tmp"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            r2 = 0
            okio.Sink r3 = okio.Okio.sink(r1)     // Catch:{ all -> 0x009e }
            r4.readAll(r3)     // Catch:{ all -> 0x009b }
            if (r3 == 0) goto L_0x0072
            r3.close()
        L_0x0072:
            boolean r2 = r1.renameTo(r5)
            if (r2 == 0) goto L_0x007c
            r7.onSuccess()
            return
        L_0x007c:
            java.io.IOException r2 = new java.io.IOException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Couldn't rename "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " to "
            r3.append(r1)
            r3.append(r5)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x009b:
            r1 = move-exception
            r2 = r3
            goto L_0x009f
        L_0x009e:
            r1 = move-exception
        L_0x009f:
            if (r2 == 0) goto L_0x00a4
            r2.close()
        L_0x00a4:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.devsupport.BundleDownloader.processBundleResult(java.lang.String, int, okhttp3.Headers, okio.BufferedSource, java.io.File, com.facebook.react.devsupport.BundleDownloader$BundleInfo, com.facebook.react.devsupport.interfaces.DevBundleDownloadListener):void");
    }

    private static void populateBundleInfo(String str, Headers headers, BundleInfo bundleInfo) {
        bundleInfo.mUrl = str;
        String str2 = headers.get("X-Metro-Files-Changed-Count");
        if (str2 != null) {
            try {
                bundleInfo.mFilesChangedCount = Integer.parseInt(str2);
            } catch (NumberFormatException unused) {
                bundleInfo.mFilesChangedCount = -2;
            }
        }
    }
}
