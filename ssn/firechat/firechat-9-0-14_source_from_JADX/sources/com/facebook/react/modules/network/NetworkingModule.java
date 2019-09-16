package com.facebook.react.modules.network;

import android.util.Base64;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.network.OkHttpCallUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.C3012Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;

@ReactModule(name = "Networking")
public final class NetworkingModule extends ReactContextBaseJavaModule {
    private static final int CHUNK_TIMEOUT_NS = 100000000;
    private static final String CONTENT_ENCODING_HEADER_NAME = "content-encoding";
    private static final String CONTENT_TYPE_HEADER_NAME = "content-type";
    private static final int MAX_CHUNK_SIZE_BETWEEN_FLUSHES = 8192;
    protected static final String NAME = "Networking";
    private static final String REQUEST_BODY_KEY_BASE64 = "base64";
    private static final String REQUEST_BODY_KEY_FORMDATA = "formData";
    private static final String REQUEST_BODY_KEY_STRING = "string";
    private static final String REQUEST_BODY_KEY_URI = "uri";
    private static final String USER_AGENT_HEADER_NAME = "user-agent";
    /* access modifiers changed from: private */
    public final OkHttpClient mClient;
    private final ForwardingCookieHandler mCookieHandler;
    private final CookieJarContainer mCookieJarContainer;
    @Nullable
    private final String mDefaultUserAgent;
    private final Set<Integer> mRequestIds;
    /* access modifiers changed from: private */
    public boolean mShuttingDown;

    /* access modifiers changed from: private */
    public static boolean shouldDispatch(long j, long j2) {
        return j2 + 100000000 < j;
    }

    public String getName() {
        return NAME;
    }

    NetworkingModule(ReactApplicationContext reactApplicationContext, @Nullable String str, OkHttpClient okHttpClient, @Nullable List<NetworkInterceptorCreator> list) {
        super(reactApplicationContext);
        if (list != null) {
            Builder newBuilder = okHttpClient.newBuilder();
            for (NetworkInterceptorCreator create : list) {
                newBuilder.addNetworkInterceptor(create.create());
            }
            okHttpClient = newBuilder.build();
        }
        this.mClient = okHttpClient;
        this.mCookieHandler = new ForwardingCookieHandler(reactApplicationContext);
        this.mCookieJarContainer = (CookieJarContainer) this.mClient.cookieJar();
        this.mShuttingDown = false;
        this.mDefaultUserAgent = str;
        this.mRequestIds = new HashSet();
    }

    NetworkingModule(ReactApplicationContext reactApplicationContext, @Nullable String str, OkHttpClient okHttpClient) {
        this(reactApplicationContext, str, okHttpClient, null);
    }

    public NetworkingModule(ReactApplicationContext reactApplicationContext) {
        this(reactApplicationContext, null, OkHttpClientProvider.createClient(), null);
    }

    public NetworkingModule(ReactApplicationContext reactApplicationContext, List<NetworkInterceptorCreator> list) {
        this(reactApplicationContext, null, OkHttpClientProvider.createClient(), list);
    }

    public NetworkingModule(ReactApplicationContext reactApplicationContext, String str) {
        this(reactApplicationContext, str, OkHttpClientProvider.createClient(), null);
    }

    public void initialize() {
        this.mCookieJarContainer.setCookieJar(new JavaNetCookieJar(this.mCookieHandler));
    }

    public void onCatalystInstanceDestroy() {
        this.mShuttingDown = true;
        cancelAllRequests();
        this.mCookieHandler.destroy();
        this.mCookieJarContainer.removeCookieJar();
    }

    @ReactMethod
    public void sendRequest(String str, String str2, int i, ReadableArray readableArray, ReadableMap readableMap, String str3, boolean z, int i2, boolean z2) {
        final String str4;
        String str5 = str;
        final int i3 = i;
        ReadableMap readableMap2 = readableMap;
        int i4 = i2;
        Request.Builder url = new Request.Builder().url(str2);
        if (i3 != 0) {
            url.tag(Integer.valueOf(i));
        }
        final RCTDeviceEventEmitter eventEmitter = getEventEmitter();
        Builder newBuilder = this.mClient.newBuilder();
        if (!z2) {
            newBuilder.cookieJar(CookieJar.NO_COOKIES);
        }
        if (z) {
            str4 = str3;
            newBuilder.addNetworkInterceptor(new Interceptor() {
                public C3012Response intercept(Chain chain) throws IOException {
                    C3012Response proceed = chain.proceed(chain.request());
                    return proceed.newBuilder().body(new ProgressResponseBody(proceed.body(), new ProgressListener() {
                        long last = System.nanoTime();

                        public void onProgress(long j, long j2, boolean z) {
                            long nanoTime = System.nanoTime();
                            if ((z || NetworkingModule.shouldDispatch(nanoTime, this.last)) && !str4.equals("text")) {
                                ResponseUtil.onDataReceivedProgress(eventEmitter, i3, j, j2);
                                this.last = nanoTime;
                            }
                        }
                    })).build();
                }
            });
        } else {
            str4 = str3;
        }
        if (i4 != this.mClient.connectTimeoutMillis()) {
            newBuilder.readTimeout((long) i4, TimeUnit.MILLISECONDS);
        }
        OkHttpClient build = newBuilder.build();
        Headers extractHeaders = extractHeaders(readableArray, readableMap2);
        if (extractHeaders == null) {
            ResponseUtil.onRequestError(eventEmitter, i3, "Unrecognized headers format", null);
            return;
        }
        String str6 = extractHeaders.get(CONTENT_TYPE_HEADER_NAME);
        String str7 = extractHeaders.get(CONTENT_ENCODING_HEADER_NAME);
        url.headers(extractHeaders);
        if (readableMap2 == null) {
            url.method(str5, RequestBodyUtil.getEmptyBody(str5));
        } else if (readableMap2.hasKey(REQUEST_BODY_KEY_STRING)) {
            if (str6 == null) {
                ResponseUtil.onRequestError(eventEmitter, i3, "Payload is set but no content-type header specified", null);
                return;
            }
            String string = readableMap2.getString(REQUEST_BODY_KEY_STRING);
            MediaType parse = MediaType.parse(str6);
            if (RequestBodyUtil.isGzipEncoding(str7)) {
                RequestBody createGzip = RequestBodyUtil.createGzip(parse, string);
                if (createGzip == null) {
                    ResponseUtil.onRequestError(eventEmitter, i3, "Failed to gzip request body", null);
                    return;
                }
                url.method(str5, createGzip);
            } else {
                url.method(str5, RequestBody.create(parse, string));
            }
        } else if (readableMap2.hasKey("base64")) {
            if (str6 == null) {
                ResponseUtil.onRequestError(eventEmitter, i3, "Payload is set but no content-type header specified", null);
                return;
            } else {
                url.method(str5, RequestBody.create(MediaType.parse(str6), ByteString.decodeBase64(readableMap2.getString("base64"))));
            }
        } else if (readableMap2.hasKey("uri")) {
            if (str6 == null) {
                ResponseUtil.onRequestError(eventEmitter, i3, "Payload is set but no content-type header specified", null);
                return;
            }
            String string2 = readableMap2.getString("uri");
            InputStream fileInputStream = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), string2);
            if (fileInputStream == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not retrieve file for uri ");
                sb.append(string2);
                ResponseUtil.onRequestError(eventEmitter, i3, sb.toString(), null);
                return;
            }
            url.method(str5, RequestBodyUtil.create(MediaType.parse(str6), fileInputStream));
        } else if (readableMap2.hasKey(REQUEST_BODY_KEY_FORMDATA)) {
            if (str6 == null) {
                str6 = "multipart/form-data";
            }
            MultipartBody.Builder constructMultipartBody = constructMultipartBody(readableMap2.getArray(REQUEST_BODY_KEY_FORMDATA), str6, i3);
            if (constructMultipartBody != null) {
                url.method(str5, RequestBodyUtil.createProgressRequest(constructMultipartBody.build(), new ProgressListener() {
                    long last = System.nanoTime();

                    public void onProgress(long j, long j2, boolean z) {
                        long nanoTime = System.nanoTime();
                        if (z || NetworkingModule.shouldDispatch(nanoTime, this.last)) {
                            ResponseUtil.onDataSend(eventEmitter, i3, j, j2);
                            this.last = nanoTime;
                        }
                    }
                }));
            } else {
                return;
            }
        } else {
            url.method(str5, RequestBodyUtil.getEmptyBody(str5));
        }
        addRequest(i3);
        Call newCall = build.newCall(url.build());
        final RCTDeviceEventEmitter rCTDeviceEventEmitter = eventEmitter;
        final boolean z3 = z;
        final String str8 = str4;
        C09173 r0 = new Callback() {
            public void onFailure(Call call, IOException iOException) {
                String str;
                if (!NetworkingModule.this.mShuttingDown) {
                    NetworkingModule.this.removeRequest(i3);
                    if (iOException.getMessage() != null) {
                        str = iOException.getMessage();
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Error while executing request: ");
                        sb.append(iOException.getClass().getSimpleName());
                        str = sb.toString();
                    }
                    ResponseUtil.onRequestError(rCTDeviceEventEmitter, i3, str, iOException);
                }
            }

            public void onResponse(Call call, C3012Response response) throws IOException {
                if (!NetworkingModule.this.mShuttingDown) {
                    NetworkingModule.this.removeRequest(i3);
                    ResponseUtil.onResponseReceived(rCTDeviceEventEmitter, i3, response.code(), NetworkingModule.translateHeaders(response.headers()), response.request().url().toString());
                    ResponseBody body = response.body();
                    try {
                        if (!z3 || !str8.equals("text")) {
                            String str = "";
                            if (str8.equals("text")) {
                                try {
                                    str = body.string();
                                } catch (IOException e) {
                                    if (!response.request().method().equalsIgnoreCase("HEAD")) {
                                        ResponseUtil.onRequestError(rCTDeviceEventEmitter, i3, e.getMessage(), e);
                                    }
                                }
                            } else if (str8.equals("base64")) {
                                str = Base64.encodeToString(body.bytes(), 2);
                            }
                            ResponseUtil.onDataReceived(rCTDeviceEventEmitter, i3, str);
                            ResponseUtil.onRequestSuccess(rCTDeviceEventEmitter, i3);
                            return;
                        }
                        NetworkingModule.this.readWithProgress(rCTDeviceEventEmitter, i3, body);
                        ResponseUtil.onRequestSuccess(rCTDeviceEventEmitter, i3);
                    } catch (IOException e2) {
                        ResponseUtil.onRequestError(rCTDeviceEventEmitter, i3, e2.getMessage(), e2);
                    }
                }
            }
        };
        newCall.enqueue(r0);
    }

    /* access modifiers changed from: private */
    public void readWithProgress(RCTDeviceEventEmitter rCTDeviceEventEmitter, int i, ResponseBody responseBody) throws IOException {
        long j;
        long j2 = -1;
        try {
            ProgressResponseBody progressResponseBody = (ProgressResponseBody) responseBody;
            j = progressResponseBody.totalBytesRead();
            try {
                j2 = progressResponseBody.contentLength();
            } catch (ClassCastException unused) {
            }
        } catch (ClassCastException unused2) {
            j = -1;
        }
        Reader charStream = responseBody.charStream();
        try {
            char[] cArr = new char[8192];
            while (true) {
                int read = charStream.read(cArr);
                if (read != -1) {
                    ResponseUtil.onIncrementalDataReceived(rCTDeviceEventEmitter, i, new String(cArr, 0, read), j, j2);
                } else {
                    return;
                }
            }
        } finally {
            charStream.close();
        }
    }

    private synchronized void addRequest(int i) {
        this.mRequestIds.add(Integer.valueOf(i));
    }

    /* access modifiers changed from: private */
    public synchronized void removeRequest(int i) {
        this.mRequestIds.remove(Integer.valueOf(i));
    }

    private synchronized void cancelAllRequests() {
        for (Integer intValue : this.mRequestIds) {
            cancelRequest(intValue.intValue());
        }
        this.mRequestIds.clear();
    }

    /* access modifiers changed from: private */
    public static WritableMap translateHeaders(Headers headers) {
        WritableMap createMap = Arguments.createMap();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
            if (createMap.hasKey(name)) {
                StringBuilder sb = new StringBuilder();
                sb.append(createMap.getString(name));
                sb.append(", ");
                sb.append(headers.value(i));
                createMap.putString(name, sb.toString());
            } else {
                createMap.putString(name, headers.value(i));
            }
        }
        return createMap;
    }

    @ReactMethod
    public void abortRequest(int i) {
        cancelRequest(i);
        removeRequest(i);
    }

    private void cancelRequest(final int i) {
        new GuardedAsyncTask<Void, Void>(getReactApplicationContext()) {
            /* access modifiers changed from: protected */
            public void doInBackgroundGuarded(Void... voidArr) {
                OkHttpCallUtil.cancelTag(NetworkingModule.this.mClient, Integer.valueOf(i));
            }
        }.execute(new Void[0]);
    }

    @ReactMethod
    public void clearCookies(com.facebook.react.bridge.Callback callback) {
        this.mCookieHandler.clearCookies(callback);
    }

    @Nullable
    private MultipartBody.Builder constructMultipartBody(ReadableArray readableArray, String str, int i) {
        MediaType mediaType;
        RCTDeviceEventEmitter eventEmitter = getEventEmitter();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse(str));
        int size = readableArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            ReadableMap map = readableArray.getMap(i2);
            Headers extractHeaders = extractHeaders(map.getArray("headers"), null);
            if (extractHeaders == null) {
                ResponseUtil.onRequestError(eventEmitter, i, "Missing or invalid header format for FormData part.", null);
                return null;
            }
            String str2 = extractHeaders.get(CONTENT_TYPE_HEADER_NAME);
            if (str2 != null) {
                mediaType = MediaType.parse(str2);
                extractHeaders = extractHeaders.newBuilder().removeAll(CONTENT_TYPE_HEADER_NAME).build();
            } else {
                mediaType = null;
            }
            if (map.hasKey(REQUEST_BODY_KEY_STRING)) {
                builder.addPart(extractHeaders, RequestBody.create(mediaType, map.getString(REQUEST_BODY_KEY_STRING)));
            } else if (!map.hasKey("uri")) {
                ResponseUtil.onRequestError(eventEmitter, i, "Unrecognized FormData part.", null);
            } else if (mediaType == null) {
                ResponseUtil.onRequestError(eventEmitter, i, "Binary FormData part needs a content-type header.", null);
                return null;
            } else {
                String string = map.getString("uri");
                InputStream fileInputStream = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), string);
                if (fileInputStream == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Could not retrieve file for uri ");
                    sb.append(string);
                    ResponseUtil.onRequestError(eventEmitter, i, sb.toString(), null);
                    return null;
                }
                builder.addPart(extractHeaders, RequestBodyUtil.create(mediaType, fileInputStream));
            }
        }
        return builder;
    }

    @Nullable
    private Headers extractHeaders(@Nullable ReadableArray readableArray, @Nullable ReadableMap readableMap) {
        if (readableArray == null) {
            return null;
        }
        Headers.Builder builder = new Headers.Builder();
        int size = readableArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            ReadableArray array = readableArray.getArray(i);
            if (array == null || array.size() != 2) {
                return null;
            }
            String string = array.getString(0);
            String string2 = array.getString(1);
            if (string == null || string2 == null) {
                return null;
            }
            builder.add(string, string2);
        }
        if (builder.get(USER_AGENT_HEADER_NAME) == null && this.mDefaultUserAgent != null) {
            builder.add(USER_AGENT_HEADER_NAME, this.mDefaultUserAgent);
        }
        if (readableMap != null && readableMap.hasKey(REQUEST_BODY_KEY_STRING)) {
            z = true;
        }
        if (!z) {
            builder.removeAll(CONTENT_ENCODING_HEADER_NAME);
        }
        return builder.build();
    }

    private RCTDeviceEventEmitter getEventEmitter() {
        return (RCTDeviceEventEmitter) getReactApplicationContext().getJSModule(RCTDeviceEventEmitter.class);
    }
}
