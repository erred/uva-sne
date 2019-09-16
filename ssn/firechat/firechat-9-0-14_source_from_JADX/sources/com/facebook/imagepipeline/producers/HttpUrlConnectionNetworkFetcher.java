package com.facebook.imagepipeline.producers;

import android.net.Uri;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.NetworkFetcher.Callback;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.matrix.olm.OlmException;

public class HttpUrlConnectionNetworkFetcher extends BaseNetworkFetcher<FetchState> {
    public static final int HTTP_PERMANENT_REDIRECT = 308;
    public static final int HTTP_TEMPORARY_REDIRECT = 307;
    private static final int MAX_REDIRECTS = 5;
    private static final int NUM_NETWORK_THREADS = 3;
    private final ExecutorService mExecutorService;

    private static boolean isHttpRedirect(int i) {
        switch (i) {
            case 300:
            case OlmException.EXCEPTION_CODE_INIT_OUTBOUND_GROUP_SESSION /*301*/:
            case OlmException.EXCEPTION_CODE_OUTBOUND_GROUP_SESSION_IDENTIFIER /*302*/:
            case OlmException.EXCEPTION_CODE_OUTBOUND_GROUP_SESSION_KEY /*303*/:
            case 307:
            case 308:
                return true;
            default:
                return false;
        }
    }

    private static boolean isHttpSuccess(int i) {
        return i >= 200 && i < 300;
    }

    public HttpUrlConnectionNetworkFetcher() {
        this(Executors.newFixedThreadPool(3));
    }

    @VisibleForTesting
    HttpUrlConnectionNetworkFetcher(ExecutorService executorService) {
        this.mExecutorService = executorService;
    }

    public FetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        return new FetchState(consumer, producerContext);
    }

    public void fetch(final FetchState fetchState, final Callback callback) {
        final Future submit = this.mExecutorService.submit(new Runnable() {
            public void run() {
                HttpUrlConnectionNetworkFetcher.this.fetchSync(fetchState, callback);
            }
        });
        fetchState.getContext().addCallbacks(new BaseProducerContextCallbacks() {
            public void onCancellationRequested() {
                if (submit.cancel(false)) {
                    callback.onCancellation();
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x003b A[SYNTHETIC, Splitter:B:30:0x003b] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0048 A[SYNTHETIC, Splitter:B:38:0x0048] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    @com.facebook.common.internal.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fetchSync(com.facebook.imagepipeline.producers.FetchState r4, com.facebook.imagepipeline.producers.NetworkFetcher.Callback r5) {
        /*
            r3 = this;
            r0 = 0
            android.net.Uri r4 = r4.getUri()     // Catch:{ IOException -> 0x0034, all -> 0x0031 }
            r1 = 5
            java.net.HttpURLConnection r4 = r3.downloadFrom(r4, r1)     // Catch:{ IOException -> 0x0034, all -> 0x0031 }
            if (r4 == 0) goto L_0x0026
            java.io.InputStream r1 = r4.getInputStream()     // Catch:{ IOException -> 0x0021, all -> 0x001f }
            r0 = -1
            r5.onResponse(r1, r0)     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r0 = r1
            goto L_0x0026
        L_0x0016:
            r5 = move-exception
            r0 = r1
            goto L_0x0046
        L_0x0019:
            r0 = move-exception
            r2 = r1
            r1 = r4
            r4 = r0
            r0 = r2
            goto L_0x0036
        L_0x001f:
            r5 = move-exception
            goto L_0x0046
        L_0x0021:
            r1 = move-exception
            r2 = r1
            r1 = r4
            r4 = r2
            goto L_0x0036
        L_0x0026:
            if (r0 == 0) goto L_0x002b
            r0.close()     // Catch:{ IOException -> 0x002b }
        L_0x002b:
            if (r4 == 0) goto L_0x0043
            r4.disconnect()
            goto L_0x0043
        L_0x0031:
            r5 = move-exception
            r4 = r0
            goto L_0x0046
        L_0x0034:
            r4 = move-exception
            r1 = r0
        L_0x0036:
            r5.onFailure(r4)     // Catch:{ all -> 0x0044 }
            if (r0 == 0) goto L_0x003e
            r0.close()     // Catch:{ IOException -> 0x003e }
        L_0x003e:
            if (r1 == 0) goto L_0x0043
            r1.disconnect()
        L_0x0043:
            return
        L_0x0044:
            r5 = move-exception
            r4 = r1
        L_0x0046:
            if (r0 == 0) goto L_0x004b
            r0.close()     // Catch:{ IOException -> 0x004b }
        L_0x004b:
            if (r4 == 0) goto L_0x0050
            r4.disconnect()
        L_0x0050:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.HttpUrlConnectionNetworkFetcher.fetchSync(com.facebook.imagepipeline.producers.FetchState, com.facebook.imagepipeline.producers.NetworkFetcher$Callback):void");
    }

    private HttpURLConnection downloadFrom(Uri uri, int i) throws IOException {
        Uri uri2;
        String str;
        HttpURLConnection openConnectionTo = openConnectionTo(uri);
        int responseCode = openConnectionTo.getResponseCode();
        if (isHttpSuccess(responseCode)) {
            return openConnectionTo;
        }
        if (isHttpRedirect(responseCode)) {
            String headerField = openConnectionTo.getHeaderField("Location");
            openConnectionTo.disconnect();
            if (headerField == null) {
                uri2 = null;
            } else {
                uri2 = Uri.parse(headerField);
            }
            String scheme = uri.getScheme();
            if (i > 0 && uri2 != null && !uri2.getScheme().equals(scheme)) {
                return downloadFrom(uri2, i - 1);
            }
            if (i == 0) {
                str = error("URL %s follows too many redirects", uri.toString());
            } else {
                str = error("URL %s returned %d without a valid redirect", uri.toString(), Integer.valueOf(responseCode));
            }
            throw new IOException(str);
        }
        openConnectionTo.disconnect();
        throw new IOException(String.format("Image URL %s returned HTTP code %d", new Object[]{uri.toString(), Integer.valueOf(responseCode)}));
    }

    @VisibleForTesting
    static HttpURLConnection openConnectionTo(Uri uri) throws IOException {
        return (HttpURLConnection) new URL(uri.toString()).openConnection();
    }

    private static String error(String str, Object... objArr) {
        return String.format(Locale.getDefault(), str, objArr);
    }
}
