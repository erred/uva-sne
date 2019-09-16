package com.RNFetchBlob;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import com.RNFetchBlob.RNFetchBlobProgressConfig.ReportType;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.network.CookieJarContainer;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.modules.network.OkHttpClientProvider;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.altbeacon.beacon.service.RangedBeacon;

/* renamed from: com.RNFetchBlob.RNFetchBlob */
public class C0491RNFetchBlob extends ReactContextBaseJavaModule {
    public static boolean ActionViewVisible = false;
    static ReactApplicationContext RCTContext;
    static LinkedBlockingQueue<Runnable> fsTaskQueue = new LinkedBlockingQueue<>();
    static ThreadPoolExecutor fsThreadPool;
    static HashMap<Integer, Promise> promiseTable = new HashMap<>();
    static LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    static ThreadPoolExecutor threadPool;
    private final OkHttpClient mClient = OkHttpClientProvider.getOkHttpClient();
    private final ForwardingCookieHandler mCookieHandler;
    private final CookieJarContainer mCookieJarContainer;

    public String getName() {
        return "RNFetchBlob";
    }

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, RangedBeacon.DEFAULT_MAX_TRACKING_AGE, TimeUnit.MILLISECONDS, taskQueue);
        threadPool = threadPoolExecutor;
        ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(2, 10, RangedBeacon.DEFAULT_MAX_TRACKING_AGE, TimeUnit.MILLISECONDS, taskQueue);
        fsThreadPool = threadPoolExecutor2;
    }

    public C0491RNFetchBlob(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.mCookieHandler = new ForwardingCookieHandler(reactApplicationContext);
        this.mCookieJarContainer = (CookieJarContainer) this.mClient.cookieJar();
        this.mCookieJarContainer.setCookieJar(new JavaNetCookieJar(this.mCookieHandler));
        RCTContext = reactApplicationContext;
        reactApplicationContext.addActivityEventListener(new ActivityEventListener() {
            public void onNewIntent(Intent intent) {
            }

            public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
                if (i == RNFetchBlobConst.GET_CONTENT_INTENT.intValue() && i2 == -1) {
                    ((Promise) C0491RNFetchBlob.promiseTable.get(RNFetchBlobConst.GET_CONTENT_INTENT)).resolve(intent.getData().toString());
                    C0491RNFetchBlob.promiseTable.remove(RNFetchBlobConst.GET_CONTENT_INTENT);
                }
            }
        });
    }

    public Map<String, Object> getConstants() {
        return RNFetchBlobFS.getSystemfolders(getReactApplicationContext());
    }

    @ReactMethod
    public void createFile(String str, String str2, String str3, Callback callback) {
        ThreadPoolExecutor threadPoolExecutor = threadPool;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final Callback callback2 = callback;
        C04952 r1 = new Runnable() {
            public void run() {
                RNFetchBlobFS.createFile(str4, str5, str6, callback2);
            }
        };
        threadPoolExecutor.execute(r1);
    }

    @ReactMethod
    public void actionViewIntent(String str, String str2, final Promise promise) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            StringBuilder sb = new StringBuilder();
            sb.append("file://");
            sb.append(str);
            Intent dataAndType = intent.setDataAndType(Uri.parse(sb.toString()), str2);
            dataAndType.setFlags(ErrorDialogData.BINDER_CRASH);
            getReactApplicationContext().startActivity(dataAndType);
            ActionViewVisible = true;
            RCTContext.addLifecycleEventListener(new LifecycleEventListener() {
                public void onHostDestroy() {
                }

                public void onHostPause() {
                }

                public void onHostResume() {
                    if (C0491RNFetchBlob.ActionViewVisible) {
                        promise.resolve(null);
                    }
                    C0491RNFetchBlob.RCTContext.removeLifecycleEventListener(this);
                }
            });
        } catch (Exception e) {
            promise.reject(e.getLocalizedMessage());
        }
    }

    @ReactMethod
    public void createFileASCII(final String str, final ReadableArray readableArray, final Callback callback) {
        threadPool.execute(new Runnable() {
            public void run() {
                RNFetchBlobFS.createFileASCII(str, readableArray, callback);
            }
        });
    }

    @ReactMethod
    public void writeArrayChunk(String str, ReadableArray readableArray, Callback callback) {
        RNFetchBlobFS.writeArrayChunk(str, readableArray, callback);
    }

    @ReactMethod
    public void unlink(String str, Callback callback) {
        RNFetchBlobFS.unlink(str, callback);
    }

    @ReactMethod
    public void mkdir(String str, Callback callback) {
        RNFetchBlobFS.mkdir(str, callback);
    }

    @ReactMethod
    public void exists(String str, Callback callback) {
        RNFetchBlobFS.exists(str, callback);
    }

    @ReactMethod
    /* renamed from: cp */
    public void mo8896cp(final String str, final String str2, final Callback callback) {
        threadPool.execute(new Runnable() {
            public void run() {
                RNFetchBlobFS.m19cp(str, str2, callback);
            }
        });
    }

    @ReactMethod
    /* renamed from: mv */
    public void mo8911mv(String str, String str2, Callback callback) {
        RNFetchBlobFS.m22mv(str, str2, callback);
    }

    @ReactMethod
    /* renamed from: ls */
    public void mo8908ls(String str, Callback callback) {
        RNFetchBlobFS.m21ls(str, callback);
    }

    @ReactMethod
    public void writeStream(String str, String str2, boolean z, Callback callback) {
        new RNFetchBlobFS(getReactApplicationContext()).writeStream(str, str2, z, callback);
    }

    @ReactMethod
    public void writeChunk(String str, String str2, Callback callback) {
        RNFetchBlobFS.writeChunk(str, str2, callback);
    }

    @ReactMethod
    public void closeStream(String str, Callback callback) {
        RNFetchBlobFS.closeStream(str, callback);
    }

    @ReactMethod
    public void removeSession(ReadableArray readableArray, Callback callback) {
        RNFetchBlobFS.removeSession(readableArray, callback);
    }

    @ReactMethod
    public void readFile(final String str, final String str2, final Promise promise) {
        threadPool.execute(new Runnable() {
            public void run() {
                RNFetchBlobFS.readFile(str, str2, promise);
            }
        });
    }

    @ReactMethod
    public void writeFileArray(String str, ReadableArray readableArray, boolean z, Promise promise) {
        ThreadPoolExecutor threadPoolExecutor = threadPool;
        final String str2 = str;
        final ReadableArray readableArray2 = readableArray;
        final boolean z2 = z;
        final Promise promise2 = promise;
        C05007 r1 = new Runnable() {
            public void run() {
                RNFetchBlobFS.writeFile(str2, readableArray2, z2, promise2);
            }
        };
        threadPoolExecutor.execute(r1);
    }

    @ReactMethod
    public void writeFile(String str, String str2, String str3, boolean z, Promise promise) {
        ThreadPoolExecutor threadPoolExecutor = threadPool;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final boolean z2 = z;
        final Promise promise2 = promise;
        C05018 r1 = new Runnable() {
            public void run() {
                RNFetchBlobFS.writeFile(str4, str5, str6, z2, promise2);
            }
        };
        threadPoolExecutor.execute(r1);
    }

    @ReactMethod
    public void lstat(String str, Callback callback) {
        RNFetchBlobFS.lstat(str, callback);
    }

    @ReactMethod
    public void stat(String str, Callback callback) {
        RNFetchBlobFS.stat(str, callback);
    }

    @ReactMethod
    public void scanFile(final ReadableArray readableArray, final Callback callback) {
        final ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        threadPool.execute(new Runnable() {
            public void run() {
                int size = readableArray.size();
                String[] strArr = new String[size];
                String[] strArr2 = new String[size];
                for (int i = 0; i < size; i++) {
                    ReadableMap map = readableArray.getMap(i);
                    if (map.hasKey(RNFetchBlobConst.RNFB_RESPONSE_PATH)) {
                        strArr[i] = map.getString(RNFetchBlobConst.RNFB_RESPONSE_PATH);
                        if (map.hasKey("mime")) {
                            strArr2[i] = map.getString("mime");
                        } else {
                            strArr2[i] = null;
                        }
                    }
                }
                new RNFetchBlobFS(reactApplicationContext).scanFile(strArr, strArr2, callback);
            }
        });
    }

    @ReactMethod
    public void readStream(String str, String str2, int i, int i2, String str3) {
        final ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        ThreadPoolExecutor threadPoolExecutor = fsThreadPool;
        final String str4 = str;
        final String str5 = str2;
        final int i3 = i;
        final int i4 = i2;
        final String str6 = str3;
        C049310 r0 = new Runnable() {
            public void run() {
                new RNFetchBlobFS(reactApplicationContext).readStream(str4, str5, i3, i4, str6);
            }
        };
        threadPoolExecutor.execute(r0);
    }

    @ReactMethod
    public void cancelRequest(String str, Callback callback) {
        try {
            RNFetchBlobReq.cancelTask(str);
            callback.invoke(null, str);
        } catch (Exception e) {
            callback.invoke(e.getLocalizedMessage(), null);
        }
    }

    @ReactMethod
    public void slice(String str, String str2, int i, int i2, Promise promise) {
        RNFetchBlobFS.slice(str, str2, i, i2, "", promise);
    }

    @ReactMethod
    public void enableProgressReport(String str, int i, int i2) {
        RNFetchBlobReq.progressReport.put(str, new RNFetchBlobProgressConfig(true, i, i2, ReportType.Download));
    }

    @ReactMethod
    /* renamed from: df */
    public void mo8899df(final Callback callback) {
        fsThreadPool.execute(new Runnable() {
            public void run() {
                RNFetchBlobFS.m20df(callback);
            }
        });
    }

    @ReactMethod
    public void enableUploadProgressReport(String str, int i, int i2) {
        RNFetchBlobReq.uploadProgressReport.put(str, new RNFetchBlobProgressConfig(true, i, i2, ReportType.Upload));
    }

    @ReactMethod
    public void fetchBlob(ReadableMap readableMap, String str, String str2, String str3, ReadableMap readableMap2, String str4, Callback callback) {
        RNFetchBlobReq rNFetchBlobReq = new RNFetchBlobReq(readableMap, str, str2, str3, readableMap2, str4, null, this.mClient, callback);
        rNFetchBlobReq.run();
    }

    @ReactMethod
    public void fetchBlobForm(ReadableMap readableMap, String str, String str2, String str3, ReadableMap readableMap2, ReadableArray readableArray, Callback callback) {
        RNFetchBlobReq rNFetchBlobReq = new RNFetchBlobReq(readableMap, str, str2, str3, readableMap2, null, readableArray, this.mClient, callback);
        rNFetchBlobReq.run();
    }

    @ReactMethod
    public void getContentIntent(String str, Promise promise) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        if (str != null) {
            intent.setType(str);
        } else {
            intent.setType(ResourceUtils.MIME_TYPE_ALL_CONTENT);
        }
        promiseTable.put(RNFetchBlobConst.GET_CONTENT_INTENT, promise);
        getReactApplicationContext().startActivityForResult(intent, RNFetchBlobConst.GET_CONTENT_INTENT.intValue(), null);
    }

    @ReactMethod
    public void addCompleteDownload(ReadableMap readableMap, Promise promise) {
        ReactApplicationContext reactApplicationContext = RCTContext;
        ReactApplicationContext reactApplicationContext2 = RCTContext;
        DownloadManager downloadManager = (DownloadManager) reactApplicationContext.getSystemService("download");
        String normalizePath = RNFetchBlobFS.normalizePath(readableMap.getString(RNFetchBlobConst.RNFB_RESPONSE_PATH));
        if (normalizePath == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("RNFetchblob.addCompleteDownload can not resolve URI:");
            sb.append(readableMap.getString(RNFetchBlobConst.RNFB_RESPONSE_PATH));
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("RNFetchblob.addCompleteDownload can not resolve URI:");
            sb3.append(normalizePath);
            promise.reject(sb2, sb3.toString());
            return;
        }
        try {
            downloadManager.addCompletedDownload(readableMap.hasKey("title") ? readableMap.getString("title") : "", readableMap.hasKey("description") ? readableMap.getString("description") : "", true, readableMap.hasKey("mime") ? readableMap.getString("mime") : null, normalizePath, Long.valueOf(RNFetchBlobFS.statFile(normalizePath).getString("size")).longValue(), readableMap.hasKey("showNotification") && readableMap.getBoolean("showNotification"));
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("RNFetchblob.addCompleteDownload failed", e.getStackTrace().toString());
        }
    }
}
