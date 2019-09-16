package retrofit2;

import javax.annotation.Nullable;
import okhttp3.C3012Response;
import okhttp3.C3012Response.Builder;
import okhttp3.Headers;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;

/* renamed from: retrofit2.Response */
public final class C3224Response<T> {
    @Nullable
    private final T body;
    @Nullable
    private final ResponseBody errorBody;
    private final C3012Response rawResponse;

    public static <T> C3224Response<T> success(@Nullable T t) {
        return success(t, new Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1).request(new Request.Builder().url("http://localhost/").build()).build());
    }

    public static <T> C3224Response<T> success(@Nullable T t, Headers headers) {
        C3226Utils.checkNotNull(headers, "headers == null");
        return success(t, new Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1).headers(headers).request(new Request.Builder().url("http://localhost/").build()).build());
    }

    public static <T> C3224Response<T> success(@Nullable T t, C3012Response response) {
        C3226Utils.checkNotNull(response, "rawResponse == null");
        if (response.isSuccessful()) {
            return new C3224Response<>(response, t, null);
        }
        throw new IllegalArgumentException("rawResponse must be successful response");
    }

    public static <T> C3224Response<T> error(int i, ResponseBody responseBody) {
        if (i >= 400) {
            return error(responseBody, new Builder().code(i).message("Response.error()").protocol(Protocol.HTTP_1_1).request(new Request.Builder().url("http://localhost/").build()).build());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("code < 400: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public static <T> C3224Response<T> error(ResponseBody responseBody, C3012Response response) {
        C3226Utils.checkNotNull(responseBody, "body == null");
        C3226Utils.checkNotNull(response, "rawResponse == null");
        if (!response.isSuccessful()) {
            return new C3224Response<>(response, null, responseBody);
        }
        throw new IllegalArgumentException("rawResponse should not be successful response");
    }

    private C3224Response(C3012Response response, @Nullable T t, @Nullable ResponseBody responseBody) {
        this.rawResponse = response;
        this.body = t;
        this.errorBody = responseBody;
    }

    public C3012Response raw() {
        return this.rawResponse;
    }

    public int code() {
        return this.rawResponse.code();
    }

    public String message() {
        return this.rawResponse.message();
    }

    public Headers headers() {
        return this.rawResponse.headers();
    }

    public boolean isSuccessful() {
        return this.rawResponse.isSuccessful();
    }

    @Nullable
    public T body() {
        return this.body;
    }

    @Nullable
    public ResponseBody errorBody() {
        return this.errorBody;
    }

    public String toString() {
        return this.rawResponse.toString();
    }
}
