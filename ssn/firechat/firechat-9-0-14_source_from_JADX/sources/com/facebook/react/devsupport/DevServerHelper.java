package com.facebook.react.devsupport;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.network.OkHttpCallUtil;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
import com.facebook.react.packagerconnection.FileIoHandler;
import com.facebook.react.packagerconnection.JSPackagerClient;
import com.facebook.react.packagerconnection.NotificationOnlyHandler;
import com.facebook.react.packagerconnection.RequestOnlyHandler;
import com.facebook.react.packagerconnection.Responder;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.C3012Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.altbeacon.beacon.service.RangedBeacon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DevServerHelper {
    private static final String BUNDLE_URL_FORMAT = "http://%s/%s.bundle?platform=android&dev=%s&minify=%s";
    private static final String DEBUGGER_MSG_DISABLE = "{ \"id\":1,\"method\":\"Debugger.disable\" }";
    private static final String HEAP_CAPTURE_UPLOAD_URL_FORMAT = "http://%s/jscheapcaptureupload";
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final String INSPECTOR_DEVICE_URL_FORMAT = "http://%s/inspector/device?name=%s&app=%s";
    private static final String LAUNCH_JS_DEVTOOLS_COMMAND_URL_FORMAT = "http://%s/launch-js-devtools";
    private static final int LONG_POLL_FAILURE_DELAY_MS = 5000;
    private static final int LONG_POLL_KEEP_ALIVE_DURATION_MS = 120000;
    private static final String ONCHANGE_ENDPOINT_URL_FORMAT = "http://%s/onchange";
    private static final String OPEN_STACK_FRAME_URL_FORMAT = "http://%s/open-stack-frame";
    private static final String PACKAGER_OK_STATUS = "packager-status:running";
    private static final String PACKAGER_STATUS_URL_FORMAT = "http://%s/status";
    private static final String RELOAD_APP_ACTION_SUFFIX = ".RELOAD_APP_ACTION";
    public static final String RELOAD_APP_EXTRA_JS_PROXY = "jsproxy";
    private static final String RESOURCE_URL_FORMAT = "http://%s/%s";
    private static final String SOURCE_MAP_URL_FORMAT = BUNDLE_URL_FORMAT.replaceFirst("\\.bundle", ".map");
    private static final String SYMBOLICATE_URL_FORMAT = "http://%s/symbolicate";
    private static final String WEBSOCKET_PROXY_URL_FORMAT = "ws://%s/debugger-proxy?role=client";
    private final BundleDownloader mBundleDownloader = new BundleDownloader(this.mClient);
    private final OkHttpClient mClient = new Builder().connectTimeout(RangedBeacon.DEFAULT_MAX_TRACKING_AGE, TimeUnit.MILLISECONDS).readTimeout(0, TimeUnit.MILLISECONDS).writeTimeout(0, TimeUnit.MILLISECONDS).build();
    /* access modifiers changed from: private */
    @Nullable
    public InspectorPackagerConnection mInspectorPackagerConnection;
    @Nullable
    private OkHttpClient mOnChangePollingClient;
    /* access modifiers changed from: private */
    public boolean mOnChangePollingEnabled;
    /* access modifiers changed from: private */
    @Nullable
    public OnServerContentChangeListener mOnServerContentChangeListener;
    /* access modifiers changed from: private */
    public final String mPackageName;
    /* access modifiers changed from: private */
    @Nullable
    public JSPackagerClient mPackagerClient;
    /* access modifiers changed from: private */
    public final Handler mRestartOnChangePollingHandler = new Handler();
    /* access modifiers changed from: private */
    public final DevInternalSettings mSettings;

    public interface OnServerContentChangeListener {
        void onServerContentChanged();
    }

    public interface PackagerCommandListener {
        void onCaptureHeapCommand(Responder responder);

        void onPackagerDevMenuCommand();

        void onPackagerReloadCommand();

        void onPokeSamplingProfilerCommand(Responder responder);
    }

    public interface SymbolicationListener {
        void onSymbolicationComplete(@Nullable Iterable<StackFrame> iterable);
    }

    public DevServerHelper(DevInternalSettings devInternalSettings, String str) {
        this.mSettings = devInternalSettings;
        this.mPackageName = str;
    }

    public void openPackagerConnection(final String str, final PackagerCommandListener packagerCommandListener) {
        if (this.mPackagerClient != null) {
            FLog.m105w(ReactConstants.TAG, "Packager connection already open, nooping.");
        } else {
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... voidArr) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("reload", new NotificationOnlyHandler() {
                        public void onNotification(@Nullable Object obj) {
                            packagerCommandListener.onPackagerReloadCommand();
                        }
                    });
                    hashMap.put("devMenu", new NotificationOnlyHandler() {
                        public void onNotification(@Nullable Object obj) {
                            packagerCommandListener.onPackagerDevMenuCommand();
                        }
                    });
                    hashMap.put("captureHeap", new RequestOnlyHandler() {
                        public void onRequest(@Nullable Object obj, Responder responder) {
                            packagerCommandListener.onCaptureHeapCommand(responder);
                        }
                    });
                    hashMap.put("pokeSamplingProfiler", new RequestOnlyHandler() {
                        public void onRequest(@Nullable Object obj, Responder responder) {
                            packagerCommandListener.onPokeSamplingProfilerCommand(responder);
                        }
                    });
                    hashMap.putAll(new FileIoHandler().handlers());
                    DevServerHelper.this.mPackagerClient = new JSPackagerClient(str, DevServerHelper.this.mSettings.getPackagerConnectionSettings(), hashMap);
                    DevServerHelper.this.mPackagerClient.init();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public void closePackagerConnection() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                if (DevServerHelper.this.mPackagerClient != null) {
                    DevServerHelper.this.mPackagerClient.close();
                    DevServerHelper.this.mPackagerClient = null;
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void openInspectorConnection() {
        if (this.mInspectorPackagerConnection != null) {
            FLog.m105w(ReactConstants.TAG, "Inspector connection already open, nooping.");
        } else {
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... voidArr) {
                    DevServerHelper.this.mInspectorPackagerConnection = new InspectorPackagerConnection(DevServerHelper.this.getInspectorDeviceUrl(), DevServerHelper.this.mPackageName);
                    DevServerHelper.this.mInspectorPackagerConnection.connect();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public void sendEventToAllConnections(String str) {
        if (this.mInspectorPackagerConnection != null) {
            this.mInspectorPackagerConnection.sendEventToAllConnections(str);
        }
    }

    public void disableDebugger() {
        if (this.mInspectorPackagerConnection != null) {
            this.mInspectorPackagerConnection.sendEventToAllConnections(DEBUGGER_MSG_DISABLE);
        }
    }

    public void closeInspectorConnection() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                if (DevServerHelper.this.mInspectorPackagerConnection != null) {
                    DevServerHelper.this.mInspectorPackagerConnection.closeQuietly();
                    DevServerHelper.this.mInspectorPackagerConnection = null;
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void symbolicateStackTrace(Iterable<StackFrame> iterable, final SymbolicationListener symbolicationListener) {
        try {
            String createSymbolicateURL = createSymbolicateURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
            JSONArray jSONArray = new JSONArray();
            for (StackFrame json : iterable) {
                jSONArray.put(json.toJSON());
            }
            ((Call) Assertions.assertNotNull(this.mClient.newCall(new Request.Builder().url(createSymbolicateURL).post(RequestBody.create(MediaType.parse("application/json"), new JSONObject().put("stack", jSONArray).toString())).build()))).enqueue(new Callback() {
                public void onFailure(Call call, IOException iOException) {
                    String str = ReactConstants.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Got IOException when attempting symbolicate stack trace: ");
                    sb.append(iOException.getMessage());
                    FLog.m105w(str, sb.toString());
                    symbolicationListener.onSymbolicationComplete(null);
                }

                public void onResponse(Call call, C3012Response response) throws IOException {
                    try {
                        symbolicationListener.onSymbolicationComplete(Arrays.asList(StackTraceHelper.convertJsStackTrace(new JSONObject(response.body().string()).getJSONArray("stack"))));
                    } catch (JSONException unused) {
                        symbolicationListener.onSymbolicationComplete(null);
                    }
                }
            });
        } catch (JSONException e) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Got JSONException when attempting symbolicate stack trace: ");
            sb.append(e.getMessage());
            FLog.m105w(str, sb.toString());
        }
    }

    public void openStackFrameCall(StackFrame stackFrame) {
        ((Call) Assertions.assertNotNull(this.mClient.newCall(new Request.Builder().url(createOpenStackFrameURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost())).post(RequestBody.create(MediaType.parse("application/json"), stackFrame.toJSON().toString())).build()))).enqueue(new Callback() {
            public void onResponse(Call call, C3012Response response) throws IOException {
            }

            public void onFailure(Call call, IOException iOException) {
                String str = ReactConstants.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Got IOException when attempting to open stack frame: ");
                sb.append(iOException.getMessage());
                FLog.m105w(str, sb.toString());
            }
        });
    }

    public static String getReloadAppAction(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getPackageName());
        sb.append(RELOAD_APP_ACTION_SUFFIX);
        return sb.toString();
    }

    public String getWebsocketProxyURL() {
        return String.format(Locale.US, WEBSOCKET_PROXY_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost()});
    }

    public String getHeapCaptureUploadUrl() {
        return String.format(Locale.US, HEAP_CAPTURE_UPLOAD_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost()});
    }

    public String getInspectorDeviceUrl() {
        return String.format(Locale.US, INSPECTOR_DEVICE_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getInspectorServerHost(), AndroidInfoHelpers.getFriendlyDeviceName(), this.mPackageName});
    }

    public BundleDownloader getBundleDownloader() {
        return this.mBundleDownloader;
    }

    private String getHostForJSProxy() {
        String str = (String) Assertions.assertNotNull(this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
        int lastIndexOf = str.lastIndexOf(58);
        if (lastIndexOf <= -1) {
            return AndroidInfoHelpers.DEVICE_LOCALHOST;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(AndroidInfoHelpers.DEVICE_LOCALHOST);
        sb.append(str.substring(lastIndexOf));
        return sb.toString();
    }

    private boolean getDevMode() {
        return this.mSettings.isJSDevModeEnabled();
    }

    private boolean getJSMinifyMode() {
        return this.mSettings.isJSMinifyEnabled();
    }

    private static String createBundleURL(String str, String str2, boolean z, boolean z2) {
        return String.format(Locale.US, BUNDLE_URL_FORMAT, new Object[]{str, str2, Boolean.valueOf(z), Boolean.valueOf(z2)});
    }

    private static String createResourceURL(String str, String str2) {
        return String.format(Locale.US, RESOURCE_URL_FORMAT, new Object[]{str, str2});
    }

    private static String createSymbolicateURL(String str) {
        return String.format(Locale.US, SYMBOLICATE_URL_FORMAT, new Object[]{str});
    }

    private static String createOpenStackFrameURL(String str) {
        return String.format(Locale.US, OPEN_STACK_FRAME_URL_FORMAT, new Object[]{str});
    }

    public String getDevServerBundleURL(String str) {
        return createBundleURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost(), str, getDevMode(), getJSMinifyMode());
    }

    public void isPackagerRunning(final PackagerStatusCallback packagerStatusCallback) {
        this.mClient.newCall(new Request.Builder().url(createPackagerStatusURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                String str = ReactConstants.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("The packager does not seem to be running as we got an IOException requesting its status: ");
                sb.append(iOException.getMessage());
                FLog.m105w(str, sb.toString());
                packagerStatusCallback.onPackagerStatusFetched(false);
            }

            public void onResponse(Call call, C3012Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String str = ReactConstants.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Got non-success http code from packager when requesting status: ");
                    sb.append(response.code());
                    FLog.m65e(str, sb.toString());
                    packagerStatusCallback.onPackagerStatusFetched(false);
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    FLog.m65e(ReactConstants.TAG, "Got null body response from packager when requesting status");
                    packagerStatusCallback.onPackagerStatusFetched(false);
                } else if (!DevServerHelper.PACKAGER_OK_STATUS.equals(body.string())) {
                    String str2 = ReactConstants.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Got unexpected response from packager when requesting status: ");
                    sb2.append(body.string());
                    FLog.m65e(str2, sb2.toString());
                    packagerStatusCallback.onPackagerStatusFetched(false);
                } else {
                    packagerStatusCallback.onPackagerStatusFetched(true);
                }
            }
        });
    }

    private static String createPackagerStatusURL(String str) {
        return String.format(Locale.US, PACKAGER_STATUS_URL_FORMAT, new Object[]{str});
    }

    public void stopPollingOnChangeEndpoint() {
        this.mOnChangePollingEnabled = false;
        this.mRestartOnChangePollingHandler.removeCallbacksAndMessages(null);
        if (this.mOnChangePollingClient != null) {
            OkHttpCallUtil.cancelTag(this.mOnChangePollingClient, this);
            this.mOnChangePollingClient = null;
        }
        this.mOnServerContentChangeListener = null;
    }

    public void startPollingOnChangeEndpoint(OnServerContentChangeListener onServerContentChangeListener) {
        if (!this.mOnChangePollingEnabled) {
            this.mOnChangePollingEnabled = true;
            this.mOnServerContentChangeListener = onServerContentChangeListener;
            this.mOnChangePollingClient = new Builder().connectionPool(new ConnectionPool(1, 120000, TimeUnit.MINUTES)).connectTimeout(RangedBeacon.DEFAULT_MAX_TRACKING_AGE, TimeUnit.MILLISECONDS).build();
            enqueueOnChangeEndpointLongPolling();
        }
    }

    /* access modifiers changed from: private */
    public void handleOnChangePollingResponse(boolean z) {
        if (this.mOnChangePollingEnabled) {
            if (z) {
                UiThreadUtil.runOnUiThread(new Runnable() {
                    public void run() {
                        if (DevServerHelper.this.mOnServerContentChangeListener != null) {
                            DevServerHelper.this.mOnServerContentChangeListener.onServerContentChanged();
                        }
                    }
                });
            }
            enqueueOnChangeEndpointLongPolling();
        }
    }

    private void enqueueOnChangeEndpointLongPolling() {
        ((OkHttpClient) Assertions.assertNotNull(this.mOnChangePollingClient)).newCall(new Request.Builder().url(createOnChangeEndpointUrl()).tag(this).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                if (DevServerHelper.this.mOnChangePollingEnabled) {
                    FLog.m58d(ReactConstants.TAG, "Error while requesting /onchange endpoint", (Throwable) iOException);
                    DevServerHelper.this.mRestartOnChangePollingHandler.postDelayed(new Runnable() {
                        public void run() {
                            DevServerHelper.this.handleOnChangePollingResponse(false);
                        }
                    }, RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
                }
            }

            public void onResponse(Call call, C3012Response response) throws IOException {
                DevServerHelper.this.handleOnChangePollingResponse(response.code() == 205);
            }
        });
    }

    private String createOnChangeEndpointUrl() {
        return String.format(Locale.US, ONCHANGE_ENDPOINT_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost()});
    }

    private String createLaunchJSDevtoolsCommandUrl() {
        return String.format(Locale.US, LAUNCH_JS_DEVTOOLS_COMMAND_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost()});
    }

    public void launchJSDevtools() {
        this.mClient.newCall(new Request.Builder().url(createLaunchJSDevtoolsCommandUrl()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
            }

            public void onResponse(Call call, C3012Response response) throws IOException {
            }
        });
    }

    public String getSourceMapUrl(String str) {
        return String.format(Locale.US, SOURCE_MAP_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost(), str, Boolean.valueOf(getDevMode()), Boolean.valueOf(getJSMinifyMode())});
    }

    public String getSourceUrl(String str) {
        return String.format(Locale.US, BUNDLE_URL_FORMAT, new Object[]{this.mSettings.getPackagerConnectionSettings().getDebugServerHost(), str, Boolean.valueOf(getDevMode()), Boolean.valueOf(getJSMinifyMode())});
    }

    public String getJSBundleURLForRemoteDebugging(String str) {
        return createBundleURL(getHostForJSProxy(), str, getDevMode(), getJSMinifyMode());
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c A[Catch:{ Exception -> 0x0050 }] */
    @javax.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.io.File downloadBundleResourceFromUrlSync(java.lang.String r7, java.io.File r8) {
        /*
            r6 = this;
            com.facebook.react.devsupport.DevInternalSettings r0 = r6.mSettings
            com.facebook.react.packagerconnection.PackagerConnectionSettings r0 = r0.getPackagerConnectionSettings()
            java.lang.String r0 = r0.getDebugServerHost()
            java.lang.String r0 = createResourceURL(r0, r7)
            okhttp3.Request$Builder r1 = new okhttp3.Request$Builder
            r1.<init>()
            okhttp3.Request$Builder r0 = r1.url(r0)
            okhttp3.Request r0 = r0.build()
            r1 = 0
            okhttp3.OkHttpClient r2 = r6.mClient     // Catch:{ Exception -> 0x0050 }
            okhttp3.Call r0 = r2.newCall(r0)     // Catch:{ Exception -> 0x0050 }
            okhttp3.Response r0 = r0.execute()     // Catch:{ Exception -> 0x0050 }
            boolean r2 = r0.isSuccessful()     // Catch:{ Exception -> 0x0050 }
            if (r2 != 0) goto L_0x002d
            return r1
        L_0x002d:
            okio.Sink r2 = okio.Okio.sink(r8)     // Catch:{ all -> 0x0048 }
            okhttp3.ResponseBody r0 = r0.body()     // Catch:{ all -> 0x0046 }
            okio.BufferedSource r0 = r0.source()     // Catch:{ all -> 0x0046 }
            okio.BufferedSource r0 = okio.Okio.buffer(r0)     // Catch:{ all -> 0x0046 }
            r0.readAll(r2)     // Catch:{ all -> 0x0046 }
            if (r2 == 0) goto L_0x0045
            r2.close()     // Catch:{ Exception -> 0x0050 }
        L_0x0045:
            return r8
        L_0x0046:
            r0 = move-exception
            goto L_0x004a
        L_0x0048:
            r0 = move-exception
            r2 = r1
        L_0x004a:
            if (r2 == 0) goto L_0x004f
            r2.close()     // Catch:{ Exception -> 0x0050 }
        L_0x004f:
            throw r0     // Catch:{ Exception -> 0x0050 }
        L_0x0050:
            r0 = move-exception
            java.lang.String r2 = "ReactNative"
            java.lang.String r3 = "Failed to fetch resource synchronously - resourcePath: \"%s\", outputFile: \"%s\""
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r7
            r7 = 1
            java.lang.String r8 = r8.getAbsolutePath()
            r4[r7] = r8
            r7 = 2
            r4[r7] = r0
            com.facebook.common.logging.FLog.m67e(r2, r3, r4)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.devsupport.DevServerHelper.downloadBundleResourceFromUrlSync(java.lang.String, java.io.File):java.io.File");
    }
}
