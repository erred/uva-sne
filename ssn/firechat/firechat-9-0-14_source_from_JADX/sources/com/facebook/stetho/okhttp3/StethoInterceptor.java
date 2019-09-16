package com.facebook.stetho.okhttp3;

import com.facebook.stetho.inspector.network.DefaultResponseHandler;
import com.facebook.stetho.inspector.network.NetworkEventReporter;
import com.facebook.stetho.inspector.network.NetworkEventReporter.InspectorRequest;
import com.facebook.stetho.inspector.network.NetworkEventReporter.InspectorResponse;
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl;
import com.facebook.stetho.inspector.network.RequestBodyHelper;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import okhttp3.C3012Response;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class StethoInterceptor implements Interceptor {
    private final NetworkEventReporter mEventReporter = NetworkEventReporterImpl.get();

    private static class ForwardingResponseBody extends ResponseBody {
        private final ResponseBody mBody;
        private final BufferedSource mInterceptedSource;

        public ForwardingResponseBody(ResponseBody responseBody, InputStream inputStream) {
            this.mBody = responseBody;
            this.mInterceptedSource = Okio.buffer(Okio.source(inputStream));
        }

        public MediaType contentType() {
            return this.mBody.contentType();
        }

        public long contentLength() {
            return this.mBody.contentLength();
        }

        public BufferedSource source() {
            return this.mInterceptedSource;
        }
    }

    private static class OkHttpInspectorRequest implements InspectorRequest {
        private final Request mRequest;
        private RequestBodyHelper mRequestBodyHelper;
        private final String mRequestId;

        public String friendlyName() {
            return null;
        }

        @Nullable
        public Integer friendlyNameExtra() {
            return null;
        }

        public OkHttpInspectorRequest(String str, Request request, RequestBodyHelper requestBodyHelper) {
            this.mRequestId = str;
            this.mRequest = request;
            this.mRequestBodyHelper = requestBodyHelper;
        }

        /* renamed from: id */
        public String mo14871id() {
            return this.mRequestId;
        }

        public String url() {
            return this.mRequest.url().toString();
        }

        public String method() {
            return this.mRequest.method();
        }

        /* JADX INFO: finally extract failed */
        @Nullable
        public byte[] body() throws IOException {
            RequestBody body = this.mRequest.body();
            if (body == null) {
                return null;
            }
            BufferedSink buffer = Okio.buffer(Okio.sink(this.mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"))));
            try {
                body.writeTo(buffer);
                buffer.close();
                return this.mRequestBodyHelper.getDisplayBody();
            } catch (Throwable th) {
                buffer.close();
                throw th;
            }
        }

        public int headerCount() {
            return this.mRequest.headers().size();
        }

        public String headerName(int i) {
            return this.mRequest.headers().name(i);
        }

        public String headerValue(int i) {
            return this.mRequest.headers().value(i);
        }

        @Nullable
        public String firstHeaderValue(String str) {
            return this.mRequest.header(str);
        }
    }

    private static class OkHttpInspectorResponse implements InspectorResponse {
        private final Connection mConnection;
        private final Request mRequest;
        private final String mRequestId;
        private final C3012Response mResponse;

        public boolean connectionReused() {
            return false;
        }

        public OkHttpInspectorResponse(String str, Request request, C3012Response response, Connection connection) {
            this.mRequestId = str;
            this.mRequest = request;
            this.mResponse = response;
            this.mConnection = connection;
        }

        public String requestId() {
            return this.mRequestId;
        }

        public String url() {
            return this.mRequest.url().toString();
        }

        public int statusCode() {
            return this.mResponse.code();
        }

        public String reasonPhrase() {
            return this.mResponse.message();
        }

        public int connectionId() {
            return this.mConnection.hashCode();
        }

        public boolean fromDiskCache() {
            return this.mResponse.cacheResponse() != null;
        }

        public int headerCount() {
            return this.mResponse.headers().size();
        }

        public String headerName(int i) {
            return this.mResponse.headers().name(i);
        }

        public String headerValue(int i) {
            return this.mResponse.headers().value(i);
        }

        @Nullable
        public String firstHeaderValue(String str) {
            return this.mResponse.header(str);
        }
    }

    public C3012Response intercept(Chain chain) throws IOException {
        RequestBodyHelper requestBodyHelper;
        InputStream inputStream;
        MediaType mediaType;
        String nextRequestId = this.mEventReporter.nextRequestId();
        Request request = chain.request();
        String str = null;
        if (this.mEventReporter.isEnabled()) {
            requestBodyHelper = new RequestBodyHelper(this.mEventReporter, nextRequestId);
            this.mEventReporter.requestWillBeSent(new OkHttpInspectorRequest(nextRequestId, request, requestBodyHelper));
        } else {
            requestBodyHelper = null;
        }
        try {
            C3012Response proceed = chain.proceed(request);
            if (!this.mEventReporter.isEnabled()) {
                return proceed;
            }
            if (requestBodyHelper != null && requestBodyHelper.hasBody()) {
                requestBodyHelper.reportDataSent();
            }
            this.mEventReporter.responseHeadersReceived(new OkHttpInspectorResponse(nextRequestId, request, proceed, chain.connection()));
            ResponseBody body = proceed.body();
            if (body != null) {
                mediaType = body.contentType();
                inputStream = body.byteStream();
            } else {
                mediaType = null;
                inputStream = null;
            }
            NetworkEventReporter networkEventReporter = this.mEventReporter;
            if (mediaType != null) {
                str = mediaType.toString();
            }
            InputStream interpretResponseStream = networkEventReporter.interpretResponseStream(nextRequestId, str, proceed.header("Content-Encoding"), inputStream, new DefaultResponseHandler(this.mEventReporter, nextRequestId));
            if (interpretResponseStream != null) {
                return proceed.newBuilder().body(new ForwardingResponseBody(body, interpretResponseStream)).build();
            }
            return proceed;
        } catch (IOException e) {
            if (this.mEventReporter.isEnabled()) {
                this.mEventReporter.httpExchangeFailed(nextRequestId, e.toString());
            }
            throw e;
        }
    }
}
