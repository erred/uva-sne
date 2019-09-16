package com.opengarden.firechat.matrixsdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.amplitude.api.Constants;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.client.MXRestExecutorService;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.ssl.CertUtil;
import com.opengarden.firechat.matrixsdk.util.FormattedJsonHttpLogger;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.PolymorphicRequestBodyConverter;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.C3012Response;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.apache.commons.lang3.time.DateUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient<T> {
    protected static final int CONNECTION_TIMEOUT_MS = 30000;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RestClient";
    private static final int READ_TIMEOUT_MS = 60000;
    public static final String URI_API_PREFIX_IDENTITY = "_matrix/identity/api/v1/";
    public static final String URI_API_PREFIX_PATH_MEDIA_PROXY_UNSTABLE = "_matrix/media_proxy/unstable/";
    public static final String URI_API_PREFIX_PATH_MEDIA_R0 = "_matrix/media/r0/";
    public static final String URI_API_PREFIX_PATH_R0 = "_matrix/client/r0/";
    public static final String URI_API_PREFIX_PATH_UNSTABLE = "_matrix/client/unstable/";
    private static final int WRITE_TIMEOUT_MS = 60000;
    public static boolean mUseMXExecutor = false;
    /* access modifiers changed from: private */
    public static String sUserAgent;
    /* access modifiers changed from: protected */
    public Gson gson;
    /* access modifiers changed from: protected */
    public T mApi;
    /* access modifiers changed from: protected */
    public Credentials mCredentials;
    protected HomeServerConnectionConfig mHsConfig;
    private OkHttpClient mOkHttpClient;
    protected UnsentEventsManager mUnsentEventsManager;

    public enum EndPointServer {
        HOME_SERVER,
        IDENTITY_SERVER,
        ANTIVIRUS_SERVER
    }

    public RestClient(HomeServerConnectionConfig homeServerConnectionConfig, Class<T> cls, String str, boolean z) {
        this(homeServerConnectionConfig, cls, str, z, EndPointServer.HOME_SERVER);
    }

    public RestClient(HomeServerConnectionConfig homeServerConnectionConfig, Class<T> cls, String str, boolean z, boolean z2) {
        this(homeServerConnectionConfig, cls, str, z, z2 ? EndPointServer.IDENTITY_SERVER : EndPointServer.HOME_SERVER);
    }

    public RestClient(HomeServerConnectionConfig homeServerConnectionConfig, Class<T> cls, String str, boolean z, EndPointServer endPointServer) {
        this.mOkHttpClient = new OkHttpClient();
        this.gson = JsonUtils.getGson(z);
        this.mHsConfig = homeServerConnectionConfig;
        this.mCredentials = homeServerConnectionConfig.getCredentials();
        C23301 r10 = new Interceptor() {
            public C3012Response intercept(Chain chain) throws IOException {
                Builder newBuilder = chain.request().newBuilder();
                if (RestClient.sUserAgent != null) {
                    newBuilder.addHeader("User-Agent", RestClient.sUserAgent);
                }
                if (!(RestClient.this.mCredentials == null || RestClient.this.mCredentials.accessToken == null)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bearer ");
                    sb.append(RestClient.this.mCredentials.accessToken);
                    newBuilder.addHeader("Authorization", sb.toString());
                }
                return chain.proceed(newBuilder.build());
            }
        };
        C23312 r0 = new Interceptor() {
            public C3012Response intercept(Chain chain) throws IOException {
                if (RestClient.this.mUnsentEventsManager == null || RestClient.this.mUnsentEventsManager.getNetworkConnectivityReceiver() == null || RestClient.this.mUnsentEventsManager.getNetworkConnectivityReceiver().isConnected()) {
                    return chain.proceed(chain.request());
                }
                throw new IOException("Not connected");
            }
        };
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new FormattedJsonHttpLogger());
        httpLoggingInterceptor.setLevel(Level.BODY);
        OkHttpClient.Builder addNetworkInterceptor = new OkHttpClient().newBuilder().connectTimeout(Constants.EVENT_UPLOAD_PERIOD_MILLIS, TimeUnit.MILLISECONDS).readTimeout(DateUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS).writeTimeout(DateUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS).addInterceptor(r10).addInterceptor(r0).addInterceptor(httpLoggingInterceptor).addNetworkInterceptor(new StethoInterceptor());
        if (mUseMXExecutor) {
            addNetworkInterceptor.dispatcher(new Dispatcher(new MXRestExecutorService()));
        }
        try {
            Pair newPinnedSSLSocketFactory = CertUtil.newPinnedSSLSocketFactory(homeServerConnectionConfig);
            addNetworkInterceptor.sslSocketFactory((SSLSocketFactory) newPinnedSSLSocketFactory.first, (X509TrustManager) newPinnedSSLSocketFactory.second);
            addNetworkInterceptor.hostnameVerifier(CertUtil.newHostnameVerifier(homeServerConnectionConfig));
            addNetworkInterceptor.connectionSpecs(CertUtil.newConnectionSpecs(homeServerConnectionConfig));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## RestClient() setSslSocketFactory failed");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
        this.mOkHttpClient = addNetworkInterceptor.build();
        this.mApi = new Retrofit.Builder().baseUrl(makeEndpoint(homeServerConnectionConfig, str, endPointServer)).addConverterFactory(PolymorphicRequestBodyConverter.FACTORY).addConverterFactory(GsonConverterFactory.create(this.gson)).client(this.mOkHttpClient).build().create(cls);
    }

    public RestClient(HomeServerConnectionConfig homeServerConnectionConfig, Class<T> cls, String str, boolean z, EndPointServer endPointServer, boolean z2) {
        this.mOkHttpClient = new OkHttpClient();
        this.gson = JsonUtils.getGson(z);
        this.mHsConfig = homeServerConnectionConfig;
        this.mCredentials = homeServerConnectionConfig.getCredentials();
        C23323 r8 = new Interceptor() {
            public C3012Response intercept(Chain chain) throws IOException {
                if (RestClient.this.mUnsentEventsManager == null || RestClient.this.mUnsentEventsManager.getNetworkConnectivityReceiver() == null || RestClient.this.mUnsentEventsManager.getNetworkConnectivityReceiver().isConnected()) {
                    return chain.proceed(chain.request());
                }
                throw new IOException("Not connected");
            }
        };
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new FormattedJsonHttpLogger());
        httpLoggingInterceptor.setLevel(Level.BODY);
        OkHttpClient.Builder addNetworkInterceptor = new OkHttpClient().newBuilder().connectTimeout(Constants.EVENT_UPLOAD_PERIOD_MILLIS, TimeUnit.MILLISECONDS).readTimeout(DateUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS).writeTimeout(DateUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS).addInterceptor(r8).addInterceptor(httpLoggingInterceptor).addNetworkInterceptor(new StethoInterceptor());
        if (mUseMXExecutor) {
            addNetworkInterceptor.dispatcher(new Dispatcher(new MXRestExecutorService()));
        }
        try {
            Pair newPinnedSSLSocketFactory = CertUtil.newPinnedSSLSocketFactory(homeServerConnectionConfig);
            addNetworkInterceptor.sslSocketFactory((SSLSocketFactory) newPinnedSSLSocketFactory.first, (X509TrustManager) newPinnedSSLSocketFactory.second);
            addNetworkInterceptor.hostnameVerifier(CertUtil.newHostnameVerifier(homeServerConnectionConfig));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## RestClient() setSslSocketFactory failed");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
        this.mOkHttpClient = addNetworkInterceptor.build();
        this.mApi = new Retrofit.Builder().baseUrl(makeEndpoint(homeServerConnectionConfig, str, endPointServer)).addConverterFactory(PolymorphicRequestBodyConverter.FACTORY).addConverterFactory(GsonConverterFactory.create(this.gson)).client(this.mOkHttpClient).build().create(cls);
    }

    @NonNull
    private String makeEndpoint(HomeServerConnectionConfig homeServerConnectionConfig, String str, EndPointServer endPointServer) {
        String str2;
        switch (endPointServer) {
            case IDENTITY_SERVER:
                str2 = homeServerConnectionConfig.getIdentityServerUri().toString();
                break;
            case ANTIVIRUS_SERVER:
                str2 = homeServerConnectionConfig.getAntiVirusServerUri().toString();
                break;
            default:
                str2 = homeServerConnectionConfig.getHomeserverUri().toString();
                break;
        }
        String sanitizeBaseUrl = sanitizeBaseUrl(str2);
        String sanitizeDynamicPath = sanitizeDynamicPath(str);
        StringBuilder sb = new StringBuilder();
        sb.append(sanitizeBaseUrl);
        sb.append(sanitizeDynamicPath);
        return sb.toString();
    }

    private String sanitizeBaseUrl(String str) {
        if (str.endsWith("/")) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("/");
        return sb.toString();
    }

    private String sanitizeDynamicPath(String str) {
        if (str.startsWith("http://")) {
            return str.substring("http://".length());
        }
        return str.startsWith("https://") ? str.substring("https://".length()) : str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x010a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void initUserAgent(android.content.Context r6) {
        /*
            java.lang.String r0 = ""
            java.lang.String r1 = ""
            if (r6 == 0) goto L_0x004f
            android.content.pm.PackageManager r2 = r6.getPackageManager()     // Catch:{ Exception -> 0x0034 }
            android.content.Context r3 = r6.getApplicationContext()     // Catch:{ Exception -> 0x0034 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ Exception -> 0x0034 }
            r4 = 0
            android.content.pm.ApplicationInfo r3 = r2.getApplicationInfo(r3, r4)     // Catch:{ Exception -> 0x0034 }
            java.lang.CharSequence r3 = r2.getApplicationLabel(r3)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0034 }
            android.content.Context r0 = r6.getApplicationContext()     // Catch:{ Exception -> 0x0030 }
            java.lang.String r0 = r0.getPackageName()     // Catch:{ Exception -> 0x0030 }
            android.content.pm.PackageInfo r0 = r2.getPackageInfo(r0, r4)     // Catch:{ Exception -> 0x0030 }
            java.lang.String r0 = r0.versionName     // Catch:{ Exception -> 0x0030 }
            r1 = r0
            r0 = r3
            goto L_0x004f
        L_0x0030:
            r0 = move-exception
            r2 = r0
            r0 = r3
            goto L_0x0035
        L_0x0034:
            r2 = move-exception
        L_0x0035:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## initUserAgent() : failed "
            r4.append(r5)
            java.lang.String r2 = r2.getMessage()
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)
        L_0x004f:
            java.lang.String r2 = "http.agent"
            java.lang.String r2 = java.lang.System.getProperty(r2)
            sUserAgent = r2
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0106
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 == 0) goto L_0x0065
            goto L_0x0106
        L_0x0065:
            java.lang.String r2 = sUserAgent
            r3 = 2131689762(0x7f0f0122, float:1.9008549E38)
            if (r2 == 0) goto L_0x00d4
            java.lang.String r2 = sUserAgent
            java.lang.String r4 = ")"
            int r2 = r2.lastIndexOf(r4)
            r4 = -1
            if (r2 == r4) goto L_0x00d4
            java.lang.String r2 = sUserAgent
            java.lang.String r5 = "("
            int r2 = r2.indexOf(r5)
            if (r2 != r4) goto L_0x0082
            goto L_0x00d4
        L_0x0082:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r0 = "/"
            r2.append(r0)
            r2.append(r1)
            java.lang.String r0 = " "
            r2.append(r0)
            java.lang.String r0 = sUserAgent
            java.lang.String r1 = sUserAgent
            java.lang.String r4 = "("
            int r1 = r1.indexOf(r4)
            java.lang.String r4 = sUserAgent
            java.lang.String r5 = ")"
            int r4 = r4.lastIndexOf(r5)
            int r4 = r4 + -1
            java.lang.String r0 = r0.substring(r1, r4)
            r2.append(r0)
            java.lang.String r0 = "; Flavour "
            r2.append(r0)
            java.lang.String r6 = r6.getString(r3)
            r2.append(r6)
            java.lang.String r6 = "; MatrixAndroidSDK "
            r2.append(r6)
            java.lang.String r6 = "9.0.14"
            r2.append(r6)
            java.lang.String r6 = ")"
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            sUserAgent = r6
            goto L_0x0105
        L_0x00d4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r0 = "/"
            r2.append(r0)
            r2.append(r1)
            java.lang.String r0 = " ( Flavour "
            r2.append(r0)
            java.lang.String r6 = r6.getString(r3)
            r2.append(r6)
            java.lang.String r6 = "; MatrixAndroidSDK "
            r2.append(r6)
            java.lang.String r6 = "9.0.14"
            r2.append(r6)
            java.lang.String r6 = ")"
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            sUserAgent = r6
        L_0x0105:
            return
        L_0x0106:
            java.lang.String r6 = sUserAgent
            if (r6 != 0) goto L_0x0123
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r0 = "Java"
            r6.append(r0)
            java.lang.String r0 = "java.version"
            java.lang.String r0 = java.lang.System.getProperty(r0)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            sUserAgent = r6
        L_0x0123:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.RestClient.initUserAgent(android.content.Context):void");
    }

    @Nullable
    public static String getUserAgent() {
        return sUserAgent;
    }

    /* access modifiers changed from: private */
    public void refreshConnectionTimeout(NetworkConnectivityReceiver networkConnectivityReceiver) {
        OkHttpClient.Builder newBuilder = this.mOkHttpClient.newBuilder();
        if (networkConnectivityReceiver.isConnected()) {
            float timeoutScale = networkConnectivityReceiver.getTimeoutScale();
            float f = 30000.0f * timeoutScale;
            float f2 = timeoutScale * 60000.0f;
            long j = (long) ((int) f2);
            newBuilder.connectTimeout((long) ((int) f), TimeUnit.MILLISECONDS).readTimeout(j, TimeUnit.MILLISECONDS).writeTimeout(j, TimeUnit.MILLISECONDS);
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## refreshConnectionTimeout()  : update setConnectTimeout to ");
            sb.append(f);
            sb.append(" ms");
            Log.m211e(str, sb.toString());
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## refreshConnectionTimeout()  : update setReadTimeout to ");
            sb2.append(f2);
            sb2.append(" ms");
            Log.m211e(str2, sb2.toString());
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## refreshConnectionTimeout()  : update setWriteTimeout to ");
            sb3.append(f2);
            sb3.append(" ms");
            Log.m211e(str3, sb3.toString());
        } else {
            newBuilder.connectTimeout(1, TimeUnit.MILLISECONDS);
            Log.m211e(LOG_TAG, "## refreshConnectionTimeout()  : update the requests timeout to 1 ms");
        }
        this.mOkHttpClient = newBuilder.build();
    }

    /* access modifiers changed from: protected */
    public void setConnectionTimeout(int i) {
        if (this.mUnsentEventsManager != null) {
            NetworkConnectivityReceiver networkConnectivityReceiver = this.mUnsentEventsManager.getNetworkConnectivityReceiver();
            if (networkConnectivityReceiver != null) {
                i = networkConnectivityReceiver.isConnected() ? (int) (((float) i) * networkConnectivityReceiver.getTimeoutScale()) : 1000;
            }
        }
        if (i != this.mOkHttpClient.connectTimeoutMillis()) {
            this.mOkHttpClient = this.mOkHttpClient.newBuilder().connectTimeout((long) i, TimeUnit.MILLISECONDS).build();
        }
    }

    public void setUnsentEventsManager(UnsentEventsManager unsentEventsManager) {
        this.mUnsentEventsManager = unsentEventsManager;
        final NetworkConnectivityReceiver networkConnectivityReceiver = this.mUnsentEventsManager.getNetworkConnectivityReceiver();
        refreshConnectionTimeout(networkConnectivityReceiver);
        networkConnectivityReceiver.addEventListener(new IMXNetworkEventListener() {
            public void onNetworkConnectionUpdate(boolean z) {
                String access$100 = RestClient.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setUnsentEventsManager()  : update the requests timeout to ");
                sb.append(z ? RestClient.CONNECTION_TIMEOUT_MS : 1);
                sb.append(" ms");
                Log.m211e(access$100, sb.toString());
                RestClient.this.refreshConnectionTimeout(networkConnectivityReceiver);
            }
        });
    }

    public Credentials getCredentials() {
        return this.mCredentials;
    }

    public void setCredentials(Credentials credentials) {
        this.mCredentials = credentials;
    }

    protected RestClient() {
        this.mOkHttpClient = new OkHttpClient();
    }

    /* access modifiers changed from: protected */
    public void setApi(T t) {
        this.mApi = t;
    }
}
