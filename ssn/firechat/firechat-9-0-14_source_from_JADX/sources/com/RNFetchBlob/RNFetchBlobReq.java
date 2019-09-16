package com.RNFetchBlob;

import android.content.BroadcastReceiver;
import android.os.Build.VERSION;
import android.support.p000v4.app.NotificationCompat;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.network.TLSSocketFactory;
import com.facebook.stetho.server.http.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import okhttp3.C3012Response;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.TlsVersion;

public class RNFetchBlobReq extends BroadcastReceiver implements Runnable {
    static ConnectionPool pool = new ConnectionPool();
    static HashMap<String, RNFetchBlobProgressConfig> progressReport = new HashMap<>();
    public static HashMap<String, Call> taskTable = new HashMap<>();
    static HashMap<String, RNFetchBlobProgressConfig> uploadProgressReport = new HashMap<>();
    Callback callback;
    OkHttpClient client;
    long contentLength;
    ReactApplicationContext ctx;
    String destPath;
    long downloadManagerId;
    ReadableMap headers;
    String method;
    RNFetchBlobConfig options;
    String rawRequestBody;
    ReadableArray rawRequestBodyArray;
    ArrayList<String> redirects = new ArrayList<>();
    RNFetchBlobBody requestBody;
    RequestType requestType;
    WritableMap respInfo;
    ResponseFormat responseFormat = ResponseFormat.Auto;
    ResponseType responseType;
    String taskId;
    boolean timeout = false;
    String url;

    enum RequestType {
        Form,
        SingleFile,
        AsIs,
        WithoutBody,
        Others
    }

    enum ResponseFormat {
        Auto,
        UTF8,
        BASE64
    }

    enum ResponseType {
        KeepInMemory,
        FileStorage
    }

    public RNFetchBlobReq(ReadableMap readableMap, String str, String str2, String str3, ReadableMap readableMap2, String str4, ReadableArray readableArray, OkHttpClient okHttpClient, Callback callback2) {
        this.method = str2.toUpperCase();
        this.options = new RNFetchBlobConfig(readableMap);
        this.taskId = str;
        this.url = str3;
        this.headers = readableMap2;
        this.callback = callback2;
        this.rawRequestBody = str4;
        this.rawRequestBodyArray = readableArray;
        this.client = okHttpClient;
        if (this.options.fileCache.booleanValue() || this.options.path != null) {
            this.responseType = ResponseType.FileStorage;
        } else {
            this.responseType = ResponseType.KeepInMemory;
        }
        if (str4 != null) {
            this.requestType = RequestType.SingleFile;
        } else if (readableArray != null) {
            this.requestType = RequestType.Form;
        } else {
            this.requestType = RequestType.WithoutBody;
        }
    }

    public static void cancelTask(String str) {
        if (taskTable.containsKey(str)) {
            ((Call) taskTable.get(str)).cancel();
            taskTable.remove(str);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:0x02d2 A[Catch:{ Exception -> 0x041e }] */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02d4 A[Catch:{ Exception -> 0x041e }] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0307 A[Catch:{ Exception -> 0x041e }] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0354 A[Catch:{ Exception -> 0x041e }] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0383 A[Catch:{ Exception -> 0x041e }] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03cf A[Catch:{ Exception -> 0x041e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r10 = this;
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            r1 = 1
            if (r0 == 0) goto L_0x00fa
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r2 = "useDownloadManager"
            boolean r0 = r0.hasKey(r2)
            if (r0 == 0) goto L_0x00fa
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r2 = "useDownloadManager"
            boolean r0 = r0.getBoolean(r2)
            if (r0 == 0) goto L_0x00fa
            java.lang.String r0 = r10.url
            android.net.Uri r0 = android.net.Uri.parse(r0)
            android.app.DownloadManager$Request r2 = new android.app.DownloadManager$Request
            r2.<init>(r0)
            r2.setNotificationVisibility(r1)
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "title"
            boolean r0 = r0.hasKey(r3)
            if (r0 == 0) goto L_0x0046
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "title"
            java.lang.String r0 = r0.getString(r3)
            r2.setTitle(r0)
        L_0x0046:
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "description"
            boolean r0 = r0.hasKey(r3)
            if (r0 == 0) goto L_0x005f
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "description"
            java.lang.String r0 = r0.getString(r3)
            r2.setDescription(r0)
        L_0x005f:
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "path"
            boolean r0 = r0.hasKey(r3)
            if (r0 == 0) goto L_0x008d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "file://"
            r0.append(r3)
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            com.facebook.react.bridge.ReadableMap r3 = r3.addAndroidDownloads
            java.lang.String r4 = "path"
            java.lang.String r3 = r3.getString(r4)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            r2.setDestinationUri(r0)
        L_0x008d:
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "mime"
            boolean r0 = r0.hasKey(r3)
            if (r0 == 0) goto L_0x00a6
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            com.facebook.react.bridge.ReadableMap r0 = r0.addAndroidDownloads
            java.lang.String r3 = "mime"
            java.lang.String r0 = r0.getString(r3)
            r2.setMimeType(r0)
        L_0x00a6:
            com.facebook.react.bridge.ReadableMap r0 = r10.headers
            com.facebook.react.bridge.ReadableMapKeySetIterator r0 = r0.keySetIterator()
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            com.facebook.react.bridge.ReadableMap r3 = r3.addAndroidDownloads
            java.lang.String r4 = "mediaScannable"
            boolean r3 = r3.hasKey(r4)
            if (r3 == 0) goto L_0x00c7
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            com.facebook.react.bridge.ReadableMap r3 = r3.addAndroidDownloads
            java.lang.String r4 = "mediaScannable"
            boolean r3 = r3.hasKey(r4)
            if (r3 != r1) goto L_0x00c7
            r2.allowScanningByMediaScanner()
        L_0x00c7:
            boolean r1 = r0.hasNextKey()
            if (r1 == 0) goto L_0x00db
            java.lang.String r1 = r0.nextKey()
            com.facebook.react.bridge.ReadableMap r3 = r10.headers
            java.lang.String r3 = r3.getString(r1)
            r2.addRequestHeader(r1, r3)
            goto L_0x00c7
        L_0x00db:
            com.facebook.react.bridge.ReactApplicationContext r0 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext
            android.content.Context r0 = r0.getApplicationContext()
            java.lang.String r1 = "download"
            java.lang.Object r1 = r0.getSystemService(r1)
            android.app.DownloadManager r1 = (android.app.DownloadManager) r1
            long r1 = r1.enqueue(r2)
            r10.downloadManagerId = r1
            android.content.IntentFilter r1 = new android.content.IntentFilter
            java.lang.String r2 = "android.intent.action.DOWNLOAD_COMPLETE"
            r1.<init>(r2)
            r0.registerReceiver(r10, r1)
            return
        L_0x00fa:
            java.lang.String r0 = r10.taskId
            com.RNFetchBlob.RNFetchBlobConfig r2 = r10.options
            java.lang.String r2 = r2.appendExt
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0109
            java.lang.String r2 = ""
            goto L_0x011e
        L_0x0109:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "."
            r2.append(r3)
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            java.lang.String r3 = r3.appendExt
            r2.append(r3)
            java.lang.String r2 = r2.toString()
        L_0x011e:
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            java.lang.String r3 = r3.key
            r4 = 0
            r5 = 0
            if (r3 == 0) goto L_0x0168
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            java.lang.String r0 = r0.key
            java.lang.String r0 = com.RNFetchBlob.RNFetchBlobUtils.getMD5(r0)
            if (r0 != 0) goto L_0x0132
            java.lang.String r0 = r10.taskId
        L_0x0132:
            java.io.File r3 = new java.io.File
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            com.facebook.react.bridge.ReactApplicationContext r7 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext
            java.lang.String r7 = com.RNFetchBlob.RNFetchBlobFS.getTmpPath(r7, r0)
            r6.append(r7)
            r6.append(r2)
            java.lang.String r6 = r6.toString()
            r3.<init>(r6)
            boolean r6 = r3.exists()
            if (r6 == 0) goto L_0x0168
            com.facebook.react.bridge.Callback r0 = r10.callback
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r4
            java.lang.String r4 = "path"
            r2[r1] = r4
            r1 = 2
            java.lang.String r3 = r3.getAbsolutePath()
            r2[r1] = r3
            r0.invoke(r2)
            return
        L_0x0168:
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            java.lang.String r3 = r3.path
            if (r3 == 0) goto L_0x0175
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options
            java.lang.String r0 = r0.path
            r10.destPath = r0
            goto L_0x0196
        L_0x0175:
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options
            java.lang.Boolean r3 = r3.fileCache
            boolean r3 = r3.booleanValue()
            if (r3 == 0) goto L_0x0196
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            com.facebook.react.bridge.ReactApplicationContext r6 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext
            java.lang.String r0 = com.RNFetchBlob.RNFetchBlobFS.getTmpPath(r6, r0)
            r3.append(r0)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
            r10.destPath = r0
        L_0x0196:
            com.RNFetchBlob.RNFetchBlobConfig r0 = r10.options     // Catch:{ Exception -> 0x041e }
            java.lang.Boolean r0 = r0.trusty     // Catch:{ Exception -> 0x041e }
            boolean r0 = r0.booleanValue()     // Catch:{ Exception -> 0x041e }
            if (r0 == 0) goto L_0x01a7
            okhttp3.OkHttpClient r0 = r10.client     // Catch:{ Exception -> 0x041e }
            okhttp3.OkHttpClient$Builder r0 = com.RNFetchBlob.RNFetchBlobUtils.getUnsafeOkHttpClient(r0)     // Catch:{ Exception -> 0x041e }
            goto L_0x01ad
        L_0x01a7:
            okhttp3.OkHttpClient r0 = r10.client     // Catch:{ Exception -> 0x041e }
            okhttp3.OkHttpClient$Builder r0 = r0.newBuilder()     // Catch:{ Exception -> 0x041e }
        L_0x01ad:
            okhttp3.Request$Builder r2 = new okhttp3.Request$Builder     // Catch:{ Exception -> 0x041e }
            r2.<init>()     // Catch:{ Exception -> 0x041e }
            java.net.URL r3 = new java.net.URL     // Catch:{ MalformedURLException -> 0x01bd }
            java.lang.String r6 = r10.url     // Catch:{ MalformedURLException -> 0x01bd }
            r3.<init>(r6)     // Catch:{ MalformedURLException -> 0x01bd }
            r2.url(r3)     // Catch:{ MalformedURLException -> 0x01bd }
            goto L_0x01c1
        L_0x01bd:
            r3 = move-exception
            r3.printStackTrace()     // Catch:{ Exception -> 0x041e }
        L_0x01c1:
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x041e }
            r3.<init>()     // Catch:{ Exception -> 0x041e }
            com.facebook.react.bridge.ReadableMap r6 = r10.headers     // Catch:{ Exception -> 0x041e }
            if (r6 == 0) goto L_0x0211
            com.facebook.react.bridge.ReadableMap r6 = r10.headers     // Catch:{ Exception -> 0x041e }
            com.facebook.react.bridge.ReadableMapKeySetIterator r6 = r6.keySetIterator()     // Catch:{ Exception -> 0x041e }
        L_0x01d0:
            boolean r7 = r6.hasNextKey()     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x0211
            java.lang.String r7 = r6.nextKey()     // Catch:{ Exception -> 0x041e }
            com.facebook.react.bridge.ReadableMap r8 = r10.headers     // Catch:{ Exception -> 0x041e }
            java.lang.String r8 = r8.getString(r7)     // Catch:{ Exception -> 0x041e }
            java.lang.String r9 = "RNFB-Response"
            boolean r9 = r7.equalsIgnoreCase(r9)     // Catch:{ Exception -> 0x041e }
            if (r9 == 0) goto L_0x0202
            java.lang.String r7 = "base64"
            boolean r7 = r8.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x01f5
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r7 = com.RNFetchBlob.RNFetchBlobReq.ResponseFormat.BASE64     // Catch:{ Exception -> 0x041e }
            r10.responseFormat = r7     // Catch:{ Exception -> 0x041e }
            goto L_0x01d0
        L_0x01f5:
            java.lang.String r7 = "utf8"
            boolean r7 = r8.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x01d0
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r7 = com.RNFetchBlob.RNFetchBlobReq.ResponseFormat.UTF8     // Catch:{ Exception -> 0x041e }
            r10.responseFormat = r7     // Catch:{ Exception -> 0x041e }
            goto L_0x01d0
        L_0x0202:
            java.lang.String r9 = r7.toLowerCase()     // Catch:{ Exception -> 0x041e }
            r2.header(r9, r8)     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = r7.toLowerCase()     // Catch:{ Exception -> 0x041e }
            r3.put(r7, r8)     // Catch:{ Exception -> 0x041e }
            goto L_0x01d0
        L_0x0211:
            java.lang.String r6 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "post"
            boolean r6 = r6.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            if (r6 != 0) goto L_0x0236
            java.lang.String r6 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "put"
            boolean r6 = r6.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            if (r6 != 0) goto L_0x0236
            java.lang.String r6 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "patch"
            boolean r6 = r6.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            if (r6 == 0) goto L_0x0230
            goto L_0x0236
        L_0x0230:
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = com.RNFetchBlob.RNFetchBlobReq.RequestType.WithoutBody     // Catch:{ Exception -> 0x041e }
            r10.requestType = r6     // Catch:{ Exception -> 0x041e }
            goto L_0x02b9
        L_0x0236:
            java.lang.String r6 = "Content-Type"
            java.lang.String r6 = r10.getHeaderIgnoreCases(r3, r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x041e }
            com.facebook.react.bridge.ReadableArray r7 = r10.rawRequestBodyArray     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x0249
            com.RNFetchBlob.RNFetchBlobReq$RequestType r7 = com.RNFetchBlob.RNFetchBlobReq.RequestType.Form     // Catch:{ Exception -> 0x041e }
            r10.requestType = r7     // Catch:{ Exception -> 0x041e }
            goto L_0x025a
        L_0x0249:
            boolean r7 = r6.isEmpty()     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x025a
            java.lang.String r7 = "Content-Type"
            java.lang.String r8 = "application/octet-stream"
            r2.header(r7, r8)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$RequestType r7 = com.RNFetchBlob.RNFetchBlobReq.RequestType.SingleFile     // Catch:{ Exception -> 0x041e }
            r10.requestType = r7     // Catch:{ Exception -> 0x041e }
        L_0x025a:
            java.lang.String r7 = r10.rawRequestBody     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x02b9
            java.lang.String r7 = r10.rawRequestBody     // Catch:{ Exception -> 0x041e }
            java.lang.String r8 = "RNFetchBlob-file://"
            boolean r7 = r7.startsWith(r8)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x026d
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = com.RNFetchBlob.RNFetchBlobReq.RequestType.SingleFile     // Catch:{ Exception -> 0x041e }
            r10.requestType = r6     // Catch:{ Exception -> 0x041e }
            goto L_0x02b9
        L_0x026d:
            java.lang.String r7 = r6.toLowerCase()     // Catch:{ Exception -> 0x041e }
            java.lang.String r8 = ";base64"
            boolean r7 = r7.contains(r8)     // Catch:{ Exception -> 0x041e }
            if (r7 != 0) goto L_0x028b
            java.lang.String r7 = r6.toLowerCase()     // Catch:{ Exception -> 0x041e }
            java.lang.String r8 = "application/octet"
            boolean r7 = r7.startsWith(r8)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x0286
            goto L_0x028b
        L_0x0286:
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = com.RNFetchBlob.RNFetchBlobReq.RequestType.AsIs     // Catch:{ Exception -> 0x041e }
            r10.requestType = r6     // Catch:{ Exception -> 0x041e }
            goto L_0x02b9
        L_0x028b:
            java.lang.String r7 = ";base64"
            java.lang.String r8 = ""
            java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = ";BASE64"
            java.lang.String r8 = ""
            java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "content-type"
            boolean r7 = r3.containsKey(r7)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x02a8
            java.lang.String r7 = "content-type"
            r3.put(r7, r6)     // Catch:{ Exception -> 0x041e }
        L_0x02a8:
            java.lang.String r7 = "Content-Type"
            boolean r7 = r3.containsKey(r7)     // Catch:{ Exception -> 0x041e }
            if (r7 == 0) goto L_0x02b5
            java.lang.String r7 = "Content-Type"
            r3.put(r7, r6)     // Catch:{ Exception -> 0x041e }
        L_0x02b5:
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = com.RNFetchBlob.RNFetchBlobReq.RequestType.SingleFile     // Catch:{ Exception -> 0x041e }
            r10.requestType = r6     // Catch:{ Exception -> 0x041e }
        L_0x02b9:
            java.lang.String r6 = "Transfer-Encoding"
            java.lang.String r6 = r10.getHeaderIgnoreCases(r3, r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "chunked"
            boolean r6 = r6.equalsIgnoreCase(r7)     // Catch:{ Exception -> 0x041e }
            int[] r7 = com.RNFetchBlob.RNFetchBlobReq.C05104.$SwitchMap$com$RNFetchBlob$RNFetchBlobReq$RequestType     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$RequestType r8 = r10.requestType     // Catch:{ Exception -> 0x041e }
            int r8 = r8.ordinal()     // Catch:{ Exception -> 0x041e }
            r7 = r7[r8]     // Catch:{ Exception -> 0x041e }
            switch(r7) {
                case 1: goto L_0x0383;
                case 2: goto L_0x0354;
                case 3: goto L_0x0307;
                case 4: goto L_0x02d4;
                default: goto L_0x02d2;
            }     // Catch:{ Exception -> 0x041e }
        L_0x02d2:
            goto L_0x03b1
        L_0x02d4:
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = "post"
            boolean r3 = r3.equalsIgnoreCase(r6)     // Catch:{ Exception -> 0x041e }
            if (r3 != 0) goto L_0x02fa
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = "put"
            boolean r3 = r3.equalsIgnoreCase(r6)     // Catch:{ Exception -> 0x041e }
            if (r3 != 0) goto L_0x02fa
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = "patch"
            boolean r3 = r3.equalsIgnoreCase(r6)     // Catch:{ Exception -> 0x041e }
            if (r3 == 0) goto L_0x02f3
            goto L_0x02fa
        L_0x02f3:
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            r2.method(r3, r4)     // Catch:{ Exception -> 0x041e }
            goto L_0x03b1
        L_0x02fa:
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x041e }
            okhttp3.RequestBody r4 = okhttp3.RequestBody.create(r4, r6)     // Catch:{ Exception -> 0x041e }
            r2.method(r3, r4)     // Catch:{ Exception -> 0x041e }
            goto L_0x03b1
        L_0x0307:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x041e }
            r3.<init>()     // Catch:{ Exception -> 0x041e }
            java.lang.String r4 = "RNFetchBlob-"
            r3.append(r4)     // Catch:{ Exception -> 0x041e }
            java.lang.String r4 = r10.taskId     // Catch:{ Exception -> 0x041e }
            r3.append(r4)     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = new com.RNFetchBlob.RNFetchBlobBody     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = r10.taskId     // Catch:{ Exception -> 0x041e }
            r4.<init>(r7)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.chunkedEncoding(r6)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = r10.requestType     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setRequestType(r6)     // Catch:{ Exception -> 0x041e }
            com.facebook.react.bridge.ReadableArray r6 = r10.rawRequestBodyArray     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setBody(r6)     // Catch:{ Exception -> 0x041e }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x041e }
            r6.<init>()     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = "multipart/form-data; boundary="
            r6.append(r7)     // Catch:{ Exception -> 0x041e }
            r6.append(r3)     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r6.toString()     // Catch:{ Exception -> 0x041e }
            okhttp3.MediaType r3 = okhttp3.MediaType.parse(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r3 = r4.setMIME(r3)     // Catch:{ Exception -> 0x041e }
            r10.requestBody = r3     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r10.requestBody     // Catch:{ Exception -> 0x041e }
            r2.method(r3, r4)     // Catch:{ Exception -> 0x041e }
            goto L_0x03b1
        L_0x0354:
            com.RNFetchBlob.RNFetchBlobBody r4 = new com.RNFetchBlob.RNFetchBlobBody     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = r10.taskId     // Catch:{ Exception -> 0x041e }
            r4.<init>(r7)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.chunkedEncoding(r6)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = r10.requestType     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setRequestType(r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = r10.rawRequestBody     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setBody(r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = "content-type"
            java.lang.String r3 = r10.getHeaderIgnoreCases(r3, r6)     // Catch:{ Exception -> 0x041e }
            okhttp3.MediaType r3 = okhttp3.MediaType.parse(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r3 = r4.setMIME(r3)     // Catch:{ Exception -> 0x041e }
            r10.requestBody = r3     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r10.requestBody     // Catch:{ Exception -> 0x041e }
            r2.method(r3, r4)     // Catch:{ Exception -> 0x041e }
            goto L_0x03b1
        L_0x0383:
            com.RNFetchBlob.RNFetchBlobBody r4 = new com.RNFetchBlob.RNFetchBlobBody     // Catch:{ Exception -> 0x041e }
            java.lang.String r7 = r10.taskId     // Catch:{ Exception -> 0x041e }
            r4.<init>(r7)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.chunkedEncoding(r6)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$RequestType r6 = r10.requestType     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setRequestType(r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = r10.rawRequestBody     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r4.setBody(r6)     // Catch:{ Exception -> 0x041e }
            java.lang.String r6 = "content-type"
            java.lang.String r3 = r10.getHeaderIgnoreCases(r3, r6)     // Catch:{ Exception -> 0x041e }
            okhttp3.MediaType r3 = okhttp3.MediaType.parse(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r3 = r4.setMIME(r3)     // Catch:{ Exception -> 0x041e }
            r10.requestBody = r3     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r10.method     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobBody r4 = r10.requestBody     // Catch:{ Exception -> 0x041e }
            r2.method(r3, r4)     // Catch:{ Exception -> 0x041e }
        L_0x03b1:
            okhttp3.Request r2 = r2.build()     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$1 r3 = new com.RNFetchBlob.RNFetchBlobReq$1     // Catch:{ Exception -> 0x041e }
            r3.<init>()     // Catch:{ Exception -> 0x041e }
            r0.addNetworkInterceptor(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$2 r3 = new com.RNFetchBlob.RNFetchBlobReq$2     // Catch:{ Exception -> 0x041e }
            r3.<init>(r2)     // Catch:{ Exception -> 0x041e }
            r0.addInterceptor(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options     // Catch:{ Exception -> 0x041e }
            long r3 = r3.timeout     // Catch:{ Exception -> 0x041e }
            r6 = 0
            int r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r8 < 0) goto L_0x03e1
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options     // Catch:{ Exception -> 0x041e }
            long r3 = r3.timeout     // Catch:{ Exception -> 0x041e }
            java.util.concurrent.TimeUnit r6 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x041e }
            r0.connectTimeout(r3, r6)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options     // Catch:{ Exception -> 0x041e }
            long r3 = r3.timeout     // Catch:{ Exception -> 0x041e }
            java.util.concurrent.TimeUnit r6 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x041e }
            r0.readTimeout(r3, r6)     // Catch:{ Exception -> 0x041e }
        L_0x03e1:
            okhttp3.ConnectionPool r3 = pool     // Catch:{ Exception -> 0x041e }
            r0.connectionPool(r3)     // Catch:{ Exception -> 0x041e }
            r0.retryOnConnectionFailure(r5)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options     // Catch:{ Exception -> 0x041e }
            java.lang.Boolean r3 = r3.followRedirect     // Catch:{ Exception -> 0x041e }
            boolean r3 = r3.booleanValue()     // Catch:{ Exception -> 0x041e }
            r0.followRedirects(r3)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobConfig r3 = r10.options     // Catch:{ Exception -> 0x041e }
            java.lang.Boolean r3 = r3.followRedirect     // Catch:{ Exception -> 0x041e }
            boolean r3 = r3.booleanValue()     // Catch:{ Exception -> 0x041e }
            r0.followSslRedirects(r3)     // Catch:{ Exception -> 0x041e }
            r0.retryOnConnectionFailure(r1)     // Catch:{ Exception -> 0x041e }
            okhttp3.OkHttpClient$Builder r0 = enableTls12OnPreLollipop(r0)     // Catch:{ Exception -> 0x041e }
            okhttp3.OkHttpClient r0 = r0.build()     // Catch:{ Exception -> 0x041e }
            okhttp3.Call r0 = r0.newCall(r2)     // Catch:{ Exception -> 0x041e }
            java.util.HashMap<java.lang.String, okhttp3.Call> r2 = taskTable     // Catch:{ Exception -> 0x041e }
            java.lang.String r3 = r10.taskId     // Catch:{ Exception -> 0x041e }
            r2.put(r3, r0)     // Catch:{ Exception -> 0x041e }
            com.RNFetchBlob.RNFetchBlobReq$3 r2 = new com.RNFetchBlob.RNFetchBlobReq$3     // Catch:{ Exception -> 0x041e }
            r2.<init>()     // Catch:{ Exception -> 0x041e }
            r0.enqueue(r2)     // Catch:{ Exception -> 0x041e }
            goto L_0x044a
        L_0x041e:
            r0 = move-exception
            r0.printStackTrace()
            r10.releaseTaskResource()
            com.facebook.react.bridge.Callback r2 = r10.callback
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "RNFetchBlob request error: "
            r3.append(r4)
            java.lang.String r4 = r0.getMessage()
            r3.append(r4)
            java.lang.Throwable r0 = r0.getCause()
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r5] = r0
            r2.invoke(r1)
        L_0x044a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.RNFetchBlob.RNFetchBlobReq.run():void");
    }

    /* access modifiers changed from: private */
    public void releaseTaskResource() {
        if (taskTable.containsKey(this.taskId)) {
            taskTable.remove(this.taskId);
        }
        if (uploadProgressReport.containsKey(this.taskId)) {
            uploadProgressReport.remove(this.taskId);
        }
        if (progressReport.containsKey(this.taskId)) {
            progressReport.remove(this.taskId);
        }
        if (this.requestBody != null) {
            this.requestBody.clearRequestBody();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:21|22|23|24|(1:26)(1:27)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x00e2 */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00e8 A[Catch:{ IOException -> 0x010e }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00fa A[Catch:{ IOException -> 0x010e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void done(okhttp3.C3012Response r12) {
        /*
            r11 = this;
            boolean r0 = r11.isBlobResponse(r12)
            com.facebook.react.bridge.WritableMap r1 = r11.getResponseInfo(r12, r0)
            r11.emitStateEvent(r1)
            int[] r1 = com.RNFetchBlob.RNFetchBlobReq.C05104.$SwitchMap$com$RNFetchBlob$RNFetchBlobReq$ResponseType
            com.RNFetchBlob.RNFetchBlobReq$ResponseType r2 = r11.responseType
            int r2 = r2.ordinal()
            r1 = r1[r2]
            r2 = 3
            r3 = 1
            r4 = 0
            r5 = 0
            r6 = 2
            switch(r1) {
                case 1: goto L_0x0047;
                case 2: goto L_0x0021;
                default: goto L_0x001d;
            }
        L_0x001d:
            com.facebook.react.bridge.Callback r0 = r11.callback     // Catch:{ IOException -> 0x0139 }
            goto L_0x011c
        L_0x0021:
            okhttp3.ResponseBody r0 = r12.body()     // Catch:{ Exception -> 0x0028 }
            r0.bytes()     // Catch:{ Exception -> 0x0028 }
        L_0x0028:
            java.lang.String r0 = r11.destPath
            java.lang.String r1 = "?append=true"
            java.lang.String r7 = ""
            java.lang.String r0 = r0.replace(r1, r7)
            r11.destPath = r0
            com.facebook.react.bridge.Callback r0 = r11.callback
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r5] = r4
            java.lang.String r2 = "path"
            r1[r3] = r2
            java.lang.String r2 = r11.destPath
            r1[r6] = r2
            r0.invoke(r1)
            goto L_0x0146
        L_0x0047:
            if (r0 == 0) goto L_0x0096
            com.RNFetchBlob.RNFetchBlobConfig r0 = r11.options     // Catch:{ IOException -> 0x010e }
            java.lang.Boolean r0 = r0.auto     // Catch:{ IOException -> 0x010e }
            boolean r0 = r0.booleanValue()     // Catch:{ IOException -> 0x010e }
            if (r0 == 0) goto L_0x0096
            com.facebook.react.bridge.ReactApplicationContext r0 = r11.ctx     // Catch:{ IOException -> 0x010e }
            java.lang.String r1 = r11.taskId     // Catch:{ IOException -> 0x010e }
            java.lang.String r0 = com.RNFetchBlob.RNFetchBlobFS.getTmpPath(r0, r1)     // Catch:{ IOException -> 0x010e }
            okhttp3.ResponseBody r1 = r12.body()     // Catch:{ IOException -> 0x010e }
            java.io.InputStream r1 = r1.byteStream()     // Catch:{ IOException -> 0x010e }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x010e }
            java.io.File r8 = new java.io.File     // Catch:{ IOException -> 0x010e }
            r8.<init>(r0)     // Catch:{ IOException -> 0x010e }
            r7.<init>(r8)     // Catch:{ IOException -> 0x010e }
            r8 = 10240(0x2800, float:1.4349E-41)
            byte[] r8 = new byte[r8]     // Catch:{ IOException -> 0x010e }
        L_0x0071:
            int r9 = r1.read(r8)     // Catch:{ IOException -> 0x010e }
            r10 = -1
            if (r9 == r10) goto L_0x007c
            r7.write(r8, r5, r9)     // Catch:{ IOException -> 0x010e }
            goto L_0x0071
        L_0x007c:
            r1.close()     // Catch:{ IOException -> 0x010e }
            r7.flush()     // Catch:{ IOException -> 0x010e }
            r7.close()     // Catch:{ IOException -> 0x010e }
            com.facebook.react.bridge.Callback r1 = r11.callback     // Catch:{ IOException -> 0x010e }
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x010e }
            r2[r5] = r4     // Catch:{ IOException -> 0x010e }
            java.lang.String r7 = "path"
            r2[r3] = r7     // Catch:{ IOException -> 0x010e }
            r2[r6] = r0     // Catch:{ IOException -> 0x010e }
            r1.invoke(r2)     // Catch:{ IOException -> 0x010e }
            goto L_0x0146
        L_0x0096:
            okhttp3.ResponseBody r0 = r12.body()     // Catch:{ IOException -> 0x010e }
            byte[] r0 = r0.bytes()     // Catch:{ IOException -> 0x010e }
            java.lang.String r1 = "UTF-8"
            java.nio.charset.Charset r1 = java.nio.charset.Charset.forName(r1)     // Catch:{ IOException -> 0x010e }
            java.nio.charset.CharsetEncoder r1 = r1.newEncoder()     // Catch:{ IOException -> 0x010e }
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r7 = r11.responseFormat     // Catch:{ IOException -> 0x010e }
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r8 = com.RNFetchBlob.RNFetchBlobReq.ResponseFormat.BASE64     // Catch:{ IOException -> 0x010e }
            if (r7 != r8) goto L_0x00c2
            com.facebook.react.bridge.Callback r1 = r11.callback     // Catch:{ IOException -> 0x010e }
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x010e }
            r2[r5] = r4     // Catch:{ IOException -> 0x010e }
            java.lang.String r7 = "base64"
            r2[r3] = r7     // Catch:{ IOException -> 0x010e }
            java.lang.String r0 = android.util.Base64.encodeToString(r0, r6)     // Catch:{ IOException -> 0x010e }
            r2[r6] = r0     // Catch:{ IOException -> 0x010e }
            r1.invoke(r2)     // Catch:{ IOException -> 0x010e }
            return
        L_0x00c2:
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.wrap(r0)     // Catch:{ CharacterCodingException -> 0x00e2 }
            java.nio.CharBuffer r7 = r7.asCharBuffer()     // Catch:{ CharacterCodingException -> 0x00e2 }
            r1.encode(r7)     // Catch:{ CharacterCodingException -> 0x00e2 }
            java.lang.String r1 = new java.lang.String     // Catch:{ CharacterCodingException -> 0x00e2 }
            r1.<init>(r0)     // Catch:{ CharacterCodingException -> 0x00e2 }
            com.facebook.react.bridge.Callback r7 = r11.callback     // Catch:{ CharacterCodingException -> 0x00e2 }
            java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ CharacterCodingException -> 0x00e2 }
            r8[r5] = r4     // Catch:{ CharacterCodingException -> 0x00e2 }
            java.lang.String r9 = "utf8"
            r8[r3] = r9     // Catch:{ CharacterCodingException -> 0x00e2 }
            r8[r6] = r1     // Catch:{ CharacterCodingException -> 0x00e2 }
            r7.invoke(r8)     // Catch:{ CharacterCodingException -> 0x00e2 }
            goto L_0x0146
        L_0x00e2:
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r1 = r11.responseFormat     // Catch:{ IOException -> 0x010e }
            com.RNFetchBlob.RNFetchBlobReq$ResponseFormat r7 = com.RNFetchBlob.RNFetchBlobReq.ResponseFormat.UTF8     // Catch:{ IOException -> 0x010e }
            if (r1 != r7) goto L_0x00fa
            com.facebook.react.bridge.Callback r0 = r11.callback     // Catch:{ IOException -> 0x010e }
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x010e }
            r1[r5] = r4     // Catch:{ IOException -> 0x010e }
            java.lang.String r2 = "utf8"
            r1[r3] = r2     // Catch:{ IOException -> 0x010e }
            java.lang.String r2 = ""
            r1[r6] = r2     // Catch:{ IOException -> 0x010e }
            r0.invoke(r1)     // Catch:{ IOException -> 0x010e }
            goto L_0x0146
        L_0x00fa:
            com.facebook.react.bridge.Callback r1 = r11.callback     // Catch:{ IOException -> 0x010e }
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x010e }
            r2[r5] = r4     // Catch:{ IOException -> 0x010e }
            java.lang.String r7 = "base64"
            r2[r3] = r7     // Catch:{ IOException -> 0x010e }
            java.lang.String r0 = android.util.Base64.encodeToString(r0, r6)     // Catch:{ IOException -> 0x010e }
            r2[r6] = r0     // Catch:{ IOException -> 0x010e }
            r1.invoke(r2)     // Catch:{ IOException -> 0x010e }
            goto L_0x0146
        L_0x010e:
            com.facebook.react.bridge.Callback r0 = r11.callback
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = "RNFetchBlob failed to encode response data to BASE64 string."
            r1[r5] = r2
            r1[r3] = r4
            r0.invoke(r1)
            goto L_0x0146
        L_0x011c:
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0139 }
            r1[r5] = r4     // Catch:{ IOException -> 0x0139 }
            java.lang.String r2 = "utf8"
            r1[r3] = r2     // Catch:{ IOException -> 0x0139 }
            java.lang.String r2 = new java.lang.String     // Catch:{ IOException -> 0x0139 }
            okhttp3.ResponseBody r7 = r12.body()     // Catch:{ IOException -> 0x0139 }
            byte[] r7 = r7.bytes()     // Catch:{ IOException -> 0x0139 }
            java.lang.String r8 = "UTF-8"
            r2.<init>(r7, r8)     // Catch:{ IOException -> 0x0139 }
            r1[r6] = r2     // Catch:{ IOException -> 0x0139 }
            r0.invoke(r1)     // Catch:{ IOException -> 0x0139 }
            goto L_0x0146
        L_0x0139:
            com.facebook.react.bridge.Callback r0 = r11.callback
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = "RNFetchBlob failed to encode response data to UTF8 string."
            r1[r5] = r2
            r1[r3] = r4
            r0.invoke(r1)
        L_0x0146:
            okhttp3.ResponseBody r12 = r12.body()
            r12.close()
            r11.releaseTaskResource()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.RNFetchBlob.RNFetchBlobReq.done(okhttp3.Response):void");
    }

    public static RNFetchBlobProgressConfig getReportProgress(String str) {
        if (!progressReport.containsKey(str)) {
            return null;
        }
        return (RNFetchBlobProgressConfig) progressReport.get(str);
    }

    public static RNFetchBlobProgressConfig getReportUploadProgress(String str) {
        if (!uploadProgressReport.containsKey(str)) {
            return null;
        }
        return (RNFetchBlobProgressConfig) uploadProgressReport.get(str);
    }

    private WritableMap getResponseInfo(C3012Response response, boolean z) {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt(NotificationCompat.CATEGORY_STATUS, response.code());
        createMap.putString("state", "2");
        createMap.putString("taskId", this.taskId);
        createMap.putBoolean("timeout", this.timeout);
        WritableMap createMap2 = Arguments.createMap();
        for (int i = 0; i < response.headers().size(); i++) {
            createMap2.putString(response.headers().name(i), response.headers().value(i));
        }
        WritableArray createArray = Arguments.createArray();
        Iterator it = this.redirects.iterator();
        while (it.hasNext()) {
            createArray.pushString((String) it.next());
        }
        createMap.putArray("redirects", createArray);
        createMap.putMap("headers", createMap2);
        Headers headers2 = response.headers();
        if (z) {
            createMap.putString("respType", "blob");
        } else if (getHeaderIgnoreCases(headers2, "content-type").equalsIgnoreCase("text/")) {
            createMap.putString("respType", "text");
        } else if (getHeaderIgnoreCases(headers2, "content-type").contains("application/json")) {
            createMap.putString("respType", "json");
        } else {
            createMap.putString("respType", "");
        }
        return createMap;
    }

    private boolean isBlobResponse(C3012Response response) {
        boolean z;
        String headerIgnoreCases = getHeaderIgnoreCases(response.headers(), HttpHeaders.CONTENT_TYPE);
        boolean z2 = !headerIgnoreCases.equalsIgnoreCase("text/");
        boolean z3 = !headerIgnoreCases.equalsIgnoreCase("application/json");
        if (this.options.binaryContentTypes != null) {
            int i = 0;
            while (true) {
                if (i >= this.options.binaryContentTypes.size()) {
                    break;
                } else if (headerIgnoreCases.toLowerCase().contains(this.options.binaryContentTypes.getString(i).toLowerCase())) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if ((!z3 || z2) && !z) {
                return false;
            }
            return true;
        }
        z = false;
        if (!z3) {
        }
        return false;
    }

    private String getHeaderIgnoreCases(Headers headers2, String str) {
        String str2 = headers2.get(str);
        if (str2 != null) {
            return str2;
        }
        return headers2.get(str.toLowerCase()) == null ? "" : headers2.get(str.toLowerCase());
    }

    private String getHeaderIgnoreCases(HashMap<String, String> hashMap, String str) {
        String str2 = (String) hashMap.get(str);
        if (str2 != null) {
            return str2;
        }
        String str3 = (String) hashMap.get(str.toLowerCase());
        if (str3 == null) {
            str3 = "";
        }
        return str3;
    }

    private void emitStateEvent(WritableMap writableMap) {
        ((RCTDeviceEventEmitter) C0491RNFetchBlob.RCTContext.getJSModule(RCTDeviceEventEmitter.class)).emit(RNFetchBlobConst.EVENT_HTTP_STATE, writableMap);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x00d7 A[SYNTHETIC, Splitter:B:22:0x00d7] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0118  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r13, android.content.Intent r14) {
        /*
            r12 = this;
            java.lang.String r13 = r14.getAction()
            java.lang.String r0 = "android.intent.action.DOWNLOAD_COMPLETE"
            boolean r13 = r0.equals(r13)
            if (r13 == 0) goto L_0x013b
            com.facebook.react.bridge.ReactApplicationContext r13 = com.RNFetchBlob.C0491RNFetchBlob.RCTContext
            android.content.Context r13 = r13.getApplicationContext()
            android.os.Bundle r14 = r14.getExtras()
            java.lang.String r0 = "extra_download_id"
            long r0 = r14.getLong(r0)
            long r2 = r12.downloadManagerId
            int r14 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r14 != 0) goto L_0x013b
            android.app.DownloadManager$Query r14 = new android.app.DownloadManager$Query
            r14.<init>()
            r0 = 1
            long[] r1 = new long[r0]
            long r2 = r12.downloadManagerId
            r4 = 0
            r1[r4] = r2
            r14.setFilterById(r1)
            java.lang.String r1 = "download"
            java.lang.Object r1 = r13.getSystemService(r1)
            android.app.DownloadManager r1 = (android.app.DownloadManager) r1
            r1.query(r14)
            android.database.Cursor r14 = r1.query(r14)
            boolean r1 = r14.moveToFirst()
            r2 = 3
            r3 = 2
            r5 = 0
            if (r1 == 0) goto L_0x00ca
            java.lang.String r1 = "status"
            int r1 = r14.getColumnIndex(r1)
            int r1 = r14.getInt(r1)
            r6 = 16
            if (r1 != r6) goto L_0x0081
            com.facebook.react.bridge.Callback r13 = r12.callback
            java.lang.Object[] r14 = new java.lang.Object[r2]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "Download manager failed to download from  "
            r2.append(r6)
            java.lang.String r6 = r12.url
            r2.append(r6)
            java.lang.String r6 = ". Statu Code = "
            r2.append(r6)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            r14[r4] = r1
            r14[r0] = r5
            r14[r3] = r5
            r13.invoke(r14)
            return
        L_0x0081:
            java.lang.String r1 = "local_uri"
            int r1 = r14.getColumnIndex(r1)
            java.lang.String r14 = r14.getString(r1)
            if (r14 == 0) goto L_0x00ca
            com.RNFetchBlob.RNFetchBlobConfig r1 = r12.options
            com.facebook.react.bridge.ReadableMap r1 = r1.addAndroidDownloads
            java.lang.String r6 = "mime"
            boolean r1 = r1.hasKey(r6)
            if (r1 == 0) goto L_0x00ca
            com.RNFetchBlob.RNFetchBlobConfig r1 = r12.options
            com.facebook.react.bridge.ReadableMap r1 = r1.addAndroidDownloads
            java.lang.String r6 = "mime"
            java.lang.String r1 = r1.getString(r6)
            java.lang.String r6 = "image"
            boolean r1 = r1.contains(r6)
            if (r1 == 0) goto L_0x00ca
            android.net.Uri r7 = android.net.Uri.parse(r14)
            android.content.ContentResolver r6 = r13.getContentResolver()
            java.lang.String[] r8 = new java.lang.String[r0]
            java.lang.String r13 = "_data"
            r8[r4] = r13
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r13 = r6.query(r7, r8, r9, r10, r11)
            if (r13 == 0) goto L_0x00ca
            r13.moveToFirst()
            java.lang.String r13 = r13.getString(r4)
            goto L_0x00cb
        L_0x00ca:
            r13 = r5
        L_0x00cb:
            com.RNFetchBlob.RNFetchBlobConfig r14 = r12.options
            com.facebook.react.bridge.ReadableMap r14 = r14.addAndroidDownloads
            java.lang.String r1 = "path"
            boolean r14 = r14.hasKey(r1)
            if (r14 == 0) goto L_0x0118
            com.RNFetchBlob.RNFetchBlobConfig r13 = r12.options     // Catch:{ Exception -> 0x0104 }
            com.facebook.react.bridge.ReadableMap r13 = r13.addAndroidDownloads     // Catch:{ Exception -> 0x0104 }
            java.lang.String r14 = "path"
            java.lang.String r13 = r13.getString(r14)     // Catch:{ Exception -> 0x0104 }
            java.io.File r14 = new java.io.File     // Catch:{ Exception -> 0x0104 }
            r14.<init>(r13)     // Catch:{ Exception -> 0x0104 }
            boolean r14 = r14.exists()     // Catch:{ Exception -> 0x0104 }
            if (r14 != 0) goto L_0x00f4
            java.lang.Exception r13 = new java.lang.Exception     // Catch:{ Exception -> 0x0104 }
            java.lang.String r14 = "Download manager download failed, the file does not downloaded to destination."
            r13.<init>(r14)     // Catch:{ Exception -> 0x0104 }
            throw r13     // Catch:{ Exception -> 0x0104 }
        L_0x00f4:
            com.facebook.react.bridge.Callback r14 = r12.callback     // Catch:{ Exception -> 0x0104 }
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0104 }
            r1[r4] = r5     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = "path"
            r1[r0] = r2     // Catch:{ Exception -> 0x0104 }
            r1[r3] = r13     // Catch:{ Exception -> 0x0104 }
            r14.invoke(r1)     // Catch:{ Exception -> 0x0104 }
            goto L_0x013b
        L_0x0104:
            r13 = move-exception
            r13.printStackTrace()
            com.facebook.react.bridge.Callback r14 = r12.callback
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r13 = r13.getLocalizedMessage()
            r1[r4] = r13
            r1[r0] = r5
            r14.invoke(r1)
            goto L_0x013b
        L_0x0118:
            if (r13 != 0) goto L_0x012c
            com.facebook.react.bridge.Callback r13 = r12.callback
            java.lang.Object[] r14 = new java.lang.Object[r2]
            java.lang.String r1 = "Download manager could not resolve downloaded file path."
            r14[r4] = r1
            java.lang.String r1 = "path"
            r14[r0] = r1
            r14[r3] = r5
            r13.invoke(r14)
            goto L_0x013b
        L_0x012c:
            com.facebook.react.bridge.Callback r14 = r12.callback
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r4] = r5
            java.lang.String r2 = "path"
            r1[r0] = r2
            r1[r3] = r13
            r14.invoke(r1)
        L_0x013b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.RNFetchBlob.RNFetchBlobReq.onReceive(android.content.Context, android.content.Intent):void");
    }

    public static Builder enableTls12OnPreLollipop(Builder builder) {
        if (VERSION.SDK_INT >= 16 && VERSION.SDK_INT <= 19) {
            try {
                builder.sslSocketFactory(new TLSSocketFactory());
                ConnectionSpec build = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2).build();
                ArrayList arrayList = new ArrayList();
                arrayList.add(build);
                arrayList.add(ConnectionSpec.COMPATIBLE_TLS);
                arrayList.add(ConnectionSpec.CLEARTEXT);
                builder.connectionSpecs(arrayList);
            } catch (Exception e) {
                FLog.m66e("OkHttpClientProvider", "Error while enabling TLS 1.2", (Throwable) e);
            }
        }
        return builder;
    }
}
